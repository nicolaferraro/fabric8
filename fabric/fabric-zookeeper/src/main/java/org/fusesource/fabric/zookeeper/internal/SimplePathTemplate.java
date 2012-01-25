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
package org.fusesource.fabric.zookeeper.internal;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic template for paths.
 *
 * @author ldywicki
 */
public class SimplePathTemplate {

    private static Pattern PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private List<String> parameters = new ArrayList<String>();
    private String path;

    public SimplePathTemplate(String path) {
        this.path = path;
        Matcher matcher = PATTERN.matcher(path);

        while (matcher.find()) {
            parameters.add(matcher.group(1));
        }
    }

    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameters);
    }

    public String bindByPosition(String ... params) {
        if (params.length != parameters.size()) {
            throw new IllegalArgumentException("Parameters mismatch. Path template contains " + parameters.size()
                + " parameters, " + params.length + " was given");
        }

        Map<String, String> paramsMap = new HashMap<String, String>();

        for (int i = 0, j = params.length; i < j; i++) {
            String param = params[i];
            if (param != null) {
                // lets remove trailing whitespace
                param = param.trim();
            }
            paramsMap.put(parameters.get(i), param);
        }

        return bindByName(paramsMap);
    }

    public String bindByName(String ... params) {
        Map<String, String> paramsMap = new HashMap<String, String>();

        for (int i = 0, j = params.length; i < j; i += 2) {
            paramsMap.put(params[i], (i + 1 < j) ? params[i+1] : "");
        }

        return bindByName(paramsMap);
    }

    public String bindByName(Map<String, String> params) {
        if (params.size() != parameters.size()) {
            throw new IllegalArgumentException("Parameters mismatch. Path template contains " + parameters.size()
                + " parameters, " + params.size() + " was given");
        }

        String localPath = path;

        for (String key : params.keySet()) {
            if (!parameters.contains(key)) {
                throw new IllegalArgumentException("Unknown parameter " + key);
            }
            localPath = replace(localPath, key, params.get(key));
        }
        return localPath;
    }

    private String replace(String text, String key, String value) {
        if (value == null) {
            throw new NullPointerException("Parameter " + key + " is null.");
        }
        return text.replace("{" + key + "}", value);
    }
}
