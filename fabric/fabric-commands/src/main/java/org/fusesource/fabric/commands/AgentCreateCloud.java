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
package org.fusesource.fabric.commands;

import java.net.URI;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.fusesource.fabric.api.CreateJCloudsAgentArguments;
import org.fusesource.fabric.api.JCloudsInstanceType;
import org.fusesource.fabric.commands.support.AgentCreateSupport;

@Command(name = "agent-create-cloud", scope = "fabric", description = "Creates one or more new agents on the cloud")
public class AgentCreateCloud extends AgentCreateSupport {

    @Option(name = "--provider", required = true, description = "JClouds provider name")
    private String providerName;
    @Option(name = "--identity", required = true, description = "The cloud identity to use")
    private String identity;
    @Option(name = "--credential", required = true, description = "Credential to login to the cloud")
    private String credential;
    @Option(name = "--hardware", required = true, description = "Which hardware kind to use")
    private String hardwareId;
    @Option(name = "--instanceType", required = true, description = "Which kind of instance is required")
    private JCloudsInstanceType instanceType;
    @Option(name = "--image", required = true, description = "The image ID to use for the new node(s)")
    private String imageId;
    @Option(name = "--location", required = true, description = "The location to use to create the new node(s)")
    private String locationId;
    @Option(name = "--user", required = true, description = "User account to run on the new node(s)")
    private String user;
    @Option(name = "--owner", description = "Optional owner of images; only really used for EC2 and deprecated going forward")
    private String owner;
    @Option(name = "--group", description = "Group tag to use on the new node(s)")
    private String group;
    @Option(name = "--proxy-uri", description = "Maven proxy URL to use")
    private URI proxyUri;
    @Argument(index = 0, required = true, description = "The name of the agent to be created. When creating multiple agents it serves as a prefix")
    protected String name;
    @Argument(index = 1, required = false, description = "The number of agents that should be created")
    protected int number = 1;

    @Override
    protected Object doExecute() throws Exception {
        CreateJCloudsAgentArguments args = new CreateJCloudsAgentArguments();
        args.setClusterServer(isClusterServer);
        args.setCredential(credential);
        args.setDebugAgent(debugAgent);
        args.setGroup(group);
        args.setHardwareId(hardwareId);
        args.setIdentity(identity);
        args.setImageId(imageId);
        args.setInstanceType(instanceType);
        args.setLocationId(locationId);
        args.setNumber(number);
        args.setOwner(owner);
        args.setProviderName(providerName);
        args.setUser(user);
        fabricService.createAgents(args, name, number);
        return null;
    }


}
