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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Strings {

    /**
     * Returns true if the given text is not null and not empty
     */
    public static boolean notEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public static String nullIfEmpty(String value) {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    public static String emptyIfNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    /**
     * splits a string into a list of strings, ignoring the empty string
     */
    public static List<String> splitAsList(String text, String delimiter) {
        List<String> answer = new ArrayList<String>();
        if (text != null && text.length() > 0) {
            answer.addAll(Arrays.asList(text.split(delimiter)));
        }
        return answer;
    }

    /**
     * splits a string into a list of strings.  Trims the results and ignores empty strings
     */
    public static List<String> splitAndTrimAsList(String text, String sep) {
        ArrayList<String> answer = new ArrayList<String>();
        if (text != null && text.length() > 0) {
            for (String v : text.split(sep)) {
                String trim = v.trim();
                if (trim.length() > 0) {
                    answer.add(trim);
                }
            }
        }
        return answer;
    }

    /**
     * joins a collection of objects together as a String using a separator
     */
    public static String join(final Collection<?> collection, final String separator) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        Iterator<?> iter = collection.iterator();
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                buffer.append(separator);
            }
            buffer.append(iter.next());
        }
        return buffer.toString();
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        } else if (object instanceof Object[]) {
            return Arrays.asList((Object[]) object).toString();
        } else {
            return object.toString();
        }
    }

}
