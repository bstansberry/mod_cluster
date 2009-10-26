/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.modcluster.mcmp;

import java.net.URI;
import java.util.Set;

import org.jboss.modcluster.Context;
import org.jboss.modcluster.Engine;
import org.jboss.modcluster.config.BalancerConfiguration;
import org.jboss.modcluster.config.NodeConfiguration;

/**
 * @author Paul Ferraro
 *
 */
public interface MCMPRequestFactory
{
   MCMPRequest createConfigRequest(Engine engine, NodeConfiguration nodeConfig, BalancerConfiguration balancerConfig);

   MCMPRequest createEnableRequest(Context context);

   MCMPRequest createDisableRequest(Context context);

   MCMPRequest createStopRequest(Context context);

   MCMPRequest createRemoveRequest(Context context);
   
   MCMPRequest createStatusRequest(String jvmRoute, int lbf);

   MCMPRequest createEnableRequest(Engine engine);

   MCMPRequest createDisableRequest(Engine engine);

   MCMPRequest createStopRequest(Engine engine);

   MCMPRequest createRemoveRequest(Engine engine);

   MCMPRequest createInfoRequest();

   MCMPRequest createDumpRequest();

   MCMPRequest createPingRequest(String jvmRoute);

   MCMPRequest createPingRequest(URI uri);
   
   MCMPRequest createRemoveContextRequest(String jvmRoute, Set<String> aliases, String path);
   
   MCMPRequest createRemoveEngineRequest(String jvmRoute);
}
