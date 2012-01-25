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
package org.fusesource.fabric.service;

import org.apache.karaf.admin.management.AdminServiceMBean;
import org.fusesource.fabric.api.Agent;
import org.fusesource.fabric.api.FabricException;
import org.fusesource.insight.log.service.LogQueryCallback;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A utitily class for interacting with a remote Agent via JMX
 */
public class AgentTemplate {
    private final Agent agent;
    private final JmxTemplateSupport jmxTemplate;
    protected String login = "admin";
    protected String password = "admin";

    public AgentTemplate(Agent agent, boolean cacheJmx) {
        this.agent = agent;
        if (cacheJmx) {
            this.jmxTemplate = new AgentCachingJmxTemplate(this);
        } else {
            this.jmxTemplate = new NonCachingJmxTemplate() {
                @Override
                protected JMXConnector createConnector() {
                    return AgentTemplate.this.createConnector();
                }
            };
        }
    }

    public AgentTemplate(Agent agent, boolean cacheJmx, String login, String password) {
        this(agent, cacheJmx);
        this.login = login;
        this.password = password;
    }

    public AgentTemplate(Agent agent, JmxTemplateSupport jmxTemplate) {
        this.jmxTemplate = jmxTemplate;
        this.agent = agent;
    }

    public interface AdminServiceCallback<T> {

        T doWithAdminService(AdminServiceMBean adminService) throws Exception;

    }

    public interface FabricServiceCallback<T> {

        T doWithFabricService(FabricServiceImplMBean fabricService) throws Exception;

    }

    public interface BundleStateCallback<T> {

        T doWithBundleState(BundleStateMBean bundleState) throws Exception;
    }

    public interface ServiceStateCallback<T> {

        T doWithServiceState(ServiceStateMBean serviceState) throws Exception;
    }

    public <T> T execute(JmxTemplateSupport.JmxConnectorCallback<T> callback) {
        return jmxTemplate.execute(callback);
    }

    public <T> T execute(final LogQueryCallback<T> callback) {
        return jmxTemplate.execute(callback);
    }

    // TODO we could refactor all these execute() methods to work at the JmxTemplate level and just delegate to them
    // then folks could use these APIs using a JmxTemplate only

    public <T> T execute(final AdminServiceCallback<T> callback) {
        return jmxTemplate.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                String[] bean = new String[]{"type", "admin", "name", agent.getId()};
                return callback.doWithAdminService(jmxTemplate.getMBean(connector, AdminServiceMBean.class, "org.apache.karaf", bean));
            }
        });
    }

    public <T> T execute(final FabricServiceCallback<T> callback) {
        return jmxTemplate.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                String[] bean = new String[]{"type", "FabricService"};
                return callback.doWithFabricService(jmxTemplate.getMBean(connector, FabricServiceImplMBean.class, "org.fusesource.fabric", bean));
            }
        });
    }

    public <T> T execute(final BundleStateCallback<T> callback) {
        return jmxTemplate.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                String[] bean = new String[] {"type", "bundleState", "version", "1.5"};
                return callback.doWithBundleState(jmxTemplate.getMBean(connector, BundleStateMBean.class, "osgi.core", bean));
            }
        });
    }

    public <T> T execute(final ServiceStateCallback<T> callback) {
        return jmxTemplate.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                String[] bean = new String[]{"type", "serviceState", "version", "1.5"};
                return callback.doWithServiceState(jmxTemplate.getMBean(connector, ServiceStateMBean.class, "osgi.core", bean));
            }
        });
    }


    public static Map getEnvCred(String login, String password) {
        Map env = new HashMap<String, Object>();
        env.put(JMXConnector.CREDENTIALS, new String[] {login, password});
        return env;
    }

    public Map getEnvironmentCredentials() {
        return getEnvCred(login, password);
    }

    public Agent getAgent() {
        return agent;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JmxTemplateSupport getJmxTemplate() {
        return jmxTemplate;
    }

    public JMXConnector createConnector() {
        String rootUrl = agent.getJmxUrl();
        JMXConnector connector;
        try {
            connector = JMXConnectorFactory.connect(
                    new JMXServiceURL(rootUrl),
                    getEnvironmentCredentials());
        } catch (IOException e) {
            throw new FabricException(e);
        }
        return connector;
    }
}
