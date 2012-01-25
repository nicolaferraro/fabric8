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
package org.fusesource.fabric.zookeeper.commands;

import java.util.List;

import org.apache.karaf.shell.console.Completer;
import org.apache.zookeeper.KeeperException;
import org.linkedin.zookeeper.client.IZKClient;

public class ZNodeCompleter implements Completer {
    private IZKClient zk;

    public ZNodeCompleter() {
        this.zk = zk;
    }

    public void setZooKeeper(IZKClient zk) {
        this.zk = zk;
    }

    @SuppressWarnings("unchecked")
    public int complete(String buffer, int cursor, List candidates) {
        // Guarantee that the final token is the one we're expanding
        if (buffer == null) {
            candidates.add("/");
            return 1;
        }
        buffer = buffer.substring(0, cursor);
        String path = buffer;
        int idx = path.lastIndexOf("/") + 1;
        String prefix = path.substring(idx);
        try {
            // Only the root path can end in a /, so strip it off every other prefix
            String dir = idx == 1 ? "/" : path.substring(0, idx - 1);
            List<String> children = zk.getChildren(dir, false);
            for (String child : children) {
                if (child.startsWith(prefix)) {
                    candidates.add(child);
                }
            }
        } catch (InterruptedException e) {
            return 0;
        } catch (KeeperException e) {
            return 0;
        }
        return candidates.size() == 0 ? buffer.length() : buffer.lastIndexOf("/") + 1;
    }
}
