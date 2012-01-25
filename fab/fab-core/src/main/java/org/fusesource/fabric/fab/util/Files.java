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
package org.fusesource.fabric.fab.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 */
public class Files {

    public static File urlToFile(String url, String tempFilePrefix, String tempFilePostfix) throws IOException {
        File file = new File(url);
        if (file.exists()) {
            return file;
        } else {
            return urlToFile(new URL(url), tempFilePrefix, tempFilePostfix);
        }
    }

    /**
     * Attempts to convert a URL to a file or copies the URL to a temporary file if it can't be easily converted
     */
    public static File urlToFile(URL url, String tempFilePrefix, String tempFilePostfix) throws IOException {
        String fileName = url.getFile();
        File file = new File(fileName);
        if (!file.exists()) {
            // we need to copy the URL to a new temp file for now...
            file = File.createTempFile(tempFilePrefix, tempFilePostfix);
            InputStream in = url.openStream();
            IOHelpers.writeTo(file, in);
        }
        return file;
    }
}
