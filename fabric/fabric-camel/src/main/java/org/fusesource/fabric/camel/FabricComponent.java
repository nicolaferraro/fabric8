/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.fabric.camel;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.ProducerCache;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.ServiceHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.fabric.groups.Group;
import org.fusesource.fabric.groups.ZooKeeperGroupFactory;

/**
 * The FABRIC camel component for providing endpoint discovery, clustering and load balancing.
 */
public class FabricComponent extends ZKComponentSupport {
    private static final transient Log LOG = LogFactory.getLog(FabricComponent.class);

    private String zkRoot = "/fabric/registry/camel/endpoints";
    private LoadBalancerFactory loadBalancerFactory = new DefaultLoadBalancerFactory();
    private ProducerCache producerCache;
    private int cacheSize = 1000;


    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public ProducerCache getProducerCache() {
        return producerCache;
    }

    public void setProducerCache(ProducerCache producerCache) {
        this.producerCache = producerCache;
    }

    public LoadBalancerFactory getLoadBalancerFactory() {
        return loadBalancerFactory;
    }

    public void setLoadBalancerFactory(LoadBalancerFactory loadBalancerFactory) {
        this.loadBalancerFactory = loadBalancerFactory;
    }

    //  Implementation methods
    //-------------------------------------------------------------------------


    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (producerCache == null) {
            producerCache = new ProducerCache(this, getCamelContext(), cacheSize);
        }
        ServiceHelper.startService(producerCache);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(producerCache);

        super.doStop();
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
        int idx = remaining.indexOf(':');
        if (idx > 0) {
            // we are registering a regular endpoint
            String name = remaining.substring(0, idx);
            String fabricPath = getFabricPath(name);
            String childUri = remaining.substring(idx + 1);

            Group group = ZooKeeperGroupFactory.create(getZkClient(), fabricPath, accessControlList);
            return new FabricPublisherEndpoint(uri, this, group, childUri);

        } else {
            String fabricPath = getFabricPath(remaining);
            Group group = ZooKeeperGroupFactory.create(getZkClient(), fabricPath, accessControlList);
            return new FabricLocatorEndpoint(uri, this, group);
        }
    }

    protected String getFabricPath(String name) {
        String path = name;
        if (ObjectHelper.isNotEmpty(zkRoot)) {
            path = zkRoot + "/" + name;
        }
        return path;
    }

}
