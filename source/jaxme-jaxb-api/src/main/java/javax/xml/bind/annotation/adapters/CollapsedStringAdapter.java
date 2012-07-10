/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.annotation.adapters;



/**
 * Predefined {@link XmlAdapter} for implementing {@code xs:token}.
 * Removes leading and trailing whitespace and converts sequences
 * of whitespace characters into a single blank.
 * @since JAXB 2.0
 */
public class CollapsedStringAdapter extends XmlAdapter<String,String> {
    /**
     * Removes leading and trailing whitespace and converts sequences
     * of whitespace characters into a single blank.
     */
    public String unmarshal(String text) {
        if (text==null) {
            return null;
        }

        final int len = text.length();
        int start = len;
        for (int i = 0;  i < len;  i++) {
            if (!isWhiteSpace(text.charAt(i))) {
                start = i;
                break;
            }
        }
        if (start == len) {
            return "";
        }
        int end = start;
        for (int i = len-1;  i > start;  i--) {
            if (!isWhiteSpace(text.charAt(i))) {
                end = i;
                break;
            }
        }

        StringBuffer sb = new StringBuffer(end-start+1);
        boolean inWhitespace = false;
        for (int i = start;  i <= end;  i++) {
            char c = text.charAt(i);
            if (isWhiteSpace(c)) {
                if (inWhitespace) {
                    continue;
                }
                inWhitespace = true;
                sb.append(' ');
            } else {
                sb.append(c);
                inWhitespace = false;
            }
        }
        return sb.toString();
    }

    /**
     * Returns the unmodified string.
     */
    public String marshal(String s) {
        return s;
    }


    /**
     * Returns, whether the given character is a whitespace character.
     */
    protected static boolean isWhiteSpace(char ch) {
        if (ch>0x20) {
            // Return fast, if possible
            return false;
        }
        return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
    }
}
