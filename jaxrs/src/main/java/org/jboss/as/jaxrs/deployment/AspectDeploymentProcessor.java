/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jaxrs.deployment;

import org.jboss.as.ee.structure.DeploymentType;
import org.jboss.as.ee.structure.DeploymentTypeMarker;
import org.jboss.as.jaxrs.logging.JaxrsLogger;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.wsf.spi.classloading.JAXRSClassLoaderProvider;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.DeploymentAspect;
import org.wildfly.security.manager.WildFlySecurityManager;

/**
 * Adaptor of DeploymentAspect to DeploymentUnitProcessor
 *
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
public final class AspectDeploymentProcessor implements DeploymentUnitProcessor {

    private Class<? extends DeploymentAspect> clazz;
    private String aspectClass;
    private DeploymentAspect aspect;

    public AspectDeploymentProcessor(final Class<? extends DeploymentAspect> aspectClass) {
        this.clazz = aspectClass;
    }

    public AspectDeploymentProcessor(final String aspectClass) {
        this.aspectClass = aspectClass;
    }

    public AspectDeploymentProcessor(final DeploymentAspect aspect) {
        this.aspect = aspect;
        this.clazz = aspect.getClass();
    }

    @Override
    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit unit = phaseContext.getDeploymentUnit();
        if (JaxrsDeploymentMarker.isJaxrsDeployment(unit) && DeploymentTypeMarker.isType(DeploymentType.WAR, unit)) {
            ensureAspectInitialized();
            final Deployment dep = getRequiredAttachment(unit, JaxrsAttachments.JAXRS_DEPLOYMENT_KEY);
            JaxrsLogger.JAXRS_LOGGER.tracef("%s start: %s", aspect, unit.getName());
            ClassLoader origClassLoader = WildFlySecurityManager.getCurrentContextClassLoaderPrivileged();
            try {
                WildFlySecurityManager.setCurrentContextClassLoaderPrivileged(aspect.getLoader());
                dep.addAttachment(ServiceTarget.class, phaseContext.getServiceTarget());
                aspect.start(dep);
                dep.removeAttachment(ServiceTarget.class);
            } finally {
                WildFlySecurityManager.setCurrentContextClassLoaderPrivileged(origClassLoader);
            }
        }
    }

    @Override
    public void undeploy(final DeploymentUnit unit) {
        if (JaxrsDeploymentMarker.isJaxrsDeployment(unit) && DeploymentTypeMarker.isType(DeploymentType.WAR, unit)) {
            final Deployment dep = getRequiredAttachment(unit, JaxrsAttachments.JAXRS_DEPLOYMENT_KEY);
            JaxrsLogger.JAXRS_LOGGER.tracef("%s stop: %s", aspect, unit.getName());
            ClassLoader origClassLoader = WildFlySecurityManager.getCurrentContextClassLoaderPrivileged();
            try {
                WildFlySecurityManager.setCurrentContextClassLoaderPrivileged(aspect.getLoader());
                aspect.stop(dep);
            } finally {
                WildFlySecurityManager.setCurrentContextClassLoaderPrivileged(origClassLoader);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void ensureAspectInitialized() throws DeploymentUnitProcessingException {
        if (aspect == null) {
            try {
                if (clazz == null) {
                    clazz = (Class<? extends DeploymentAspect>) JAXRSClassLoaderProvider.getDefaultProvider()
                            .getServerIntegrationClassLoader().loadClass(aspectClass);
                }
                aspect = clazz.newInstance();
            } catch (Exception e) {
                throw new DeploymentUnitProcessingException(e);
            }
        }
    }

    private static <A> A getRequiredAttachment(final DeploymentUnit unit, final AttachmentKey<A> key) {
       final A value = unit.getAttachment(key);
       if (value == null) {
           throw new IllegalStateException();
       }

       return value;
   }
}

