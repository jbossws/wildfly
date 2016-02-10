/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.metadata.JAXRSDeploymentMetadata;

import java.util.Map;

/**
 * Jaxrs attachments
 *
 * @author Stuart Douglas
 *
 */
public class JaxrsAttachments {

    public static final AttachmentKey<JAXRSDeploymentMetadata> JAXRS_DEPLOYMENT_DATA = AttachmentKey.create(JAXRSDeploymentMetadata.class);
    public static final AttachmentKey<Map<ModuleIdentifier, JAXRSDeploymentMetadata>> ADDITIONAL_JAXRS_DEPLOYMENT_DATA = AttachmentKey.create(Map.class);
    public static final AttachmentKey<Deployment> JAXRS_DEPLOYMENT_KEY = AttachmentKey.create(Deployment.class);
    public static final AttachmentKey<ClassLoader> CLASSLOADER_KEY = AttachmentKey.create(ClassLoader.class);
}
