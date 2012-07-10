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
 * Replaces any tab, CR, and LF with a blank character.
 * @since JAXB 2.0
 */
public final class NormalizedStringAdapter extends XmlAdapter<String,String> {
    /**
     * Replaces any tab, CR, and LF with a blank character.
     */
    public String unmarshal(String text) {
        if (text==null) {
            return null;
        }

        final int len = text.length();
        int start = len;
        for (int i = 0;  i < len;  i++) {
            if (isWhiteSpaceExceptSpace(text.charAt(i))) {
                start = i;
                break;
            }
        }
        if (start == len) {
            return text;
        }
        final char[] result = new char[len];
        for (int i = 0;  i < start;  i++) {
            result[i] = text.charAt(i);
        }
        for (int i = start;  i < len;  i++) {
            final char c = text.charAt(i);
            if (isWhiteSpaceExceptSpace(c)) {
                result[i] = ' ';
            } else {
                result[i] = c;
            }
        }
        return new String(result);
    }

    /**
     * Returns the unmodified input string.
     */
    public String marshal(String s) {
        return s;
    }


    /**
     * Returns, whether the given character is a tab,
     * CR or LF.
     */
    protected static boolean isWhiteSpaceExceptSpace(char ch) {
        return ch == 0x9 || ch == 0xA || ch == 0xD;
    }
}
