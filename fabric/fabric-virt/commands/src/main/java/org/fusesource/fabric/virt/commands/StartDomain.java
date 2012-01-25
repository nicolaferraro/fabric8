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

package org.fusesource.fabric.virt.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.libvirt.Connect;
import org.libvirt.Domain;

@Command(scope = "virt", name = "domain-start")
public class StartDomain extends LibvirtCommandSupport {

    @Argument(name = "name", description = "The id of the domain", multiValued = false, required = true)
    private String name;


    @Override
    protected Object doExecute() throws Exception {
        Connect connect = getConnection();
        Domain domain = connect.domainLookupByName(name);
        domain.create();
        return null;
    }
}