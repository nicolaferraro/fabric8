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

package org.fusesource.fabric.fab.osgi.commands.fab;

import org.fusesource.fabric.fab.osgi.internal.FabClassPathResolver;
import org.fusesource.fabric.fab.osgi.commands.BundleCommandSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public abstract class FabCommandSupport extends BundleCommandSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(FabCommandSupport.class);

    protected void doExecute(Bundle bundle) throws Exception {
        FabClassPathResolver resolver = createFabResolver(bundle);
        doExecute(bundle, resolver);
    }

    protected abstract void doExecute(Bundle bundle, FabClassPathResolver resolver) throws Exception;

    protected void stopBundle(Bundle bundle) {
        if (bundle.getState() == Bundle.ACTIVE) {
            LOG.debug("Stopping bundle %s version %s", bundle.getSymbolicName(), bundle.getVersion());
            try {
                bundle.stop();
            } catch (BundleException e) {
                System.out.println("Failed to start " + bundle.getSymbolicName() + " " + bundle.getVersion() + ". " + e);
                e.printStackTrace();
            }
        }
    }
}
