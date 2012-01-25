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

package org.fusesource.fabric.fab.osgi.itests;

import org.apache.karaf.testing.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

@RunWith(JUnit4TestRunner.class)
public class AgentIntegrationTest extends IntegrationTestSupport {

    @Test
    public void testRun() throws Exception {
        Thread.sleep(10000);

        assertStartBundle("org.fusesource.fabric.fabric-agent");
        Thread.sleep(10000);
        stopBundles();
    }

    @Configuration
    public static Option[] configuration() throws Exception {
        Option[] options = combine(
                // Default karaf environment
                Helper.getDefaultOptions(
                        // this is how you set the default log level when using pax logging (logProfile)
                        //Helper.setLogLevel("TRACE")
                        Helper.setLogLevel("INFO")
                ),

                // add fab features
                scanFeatures(
                        maven().groupId("org.fusesource.fabric").artifactId("fuse-fabric").type("xml").classifier("features").versionAsInProject(),
                        "fabric-agent"
                ),

                workingDirectory("target/paxrunner/core/"),

                waitForFrameworkStartup(),

                // TODO Test on both equinox and felix
                // TODO: pax-exam does not support the latest felix version :-(
                // TODO: so we use the higher supported which should be the same
                // TODO: as the one specified in itests/dependencies/pom.xml
                //equinox(), felix().version("3.0.2")
                felix().version("3.0.2")
        );
        // Stop the shell log bundle
        //Helper.findMaven(options, "org.apache.karaf.shell", "org.apache.karaf.shell.log").noStart();
        return options;
    }


}
