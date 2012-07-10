/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.jaxme.junit;


public class PrintParse {
    /** Converts a boolean value into 0 (false) or 1 (true).
     */
    public static String printBoolean(boolean pValue) {
        return pValue ? "1" : "0";
    }

    /** Converts the values 0 (false) or 1 (true) into a
     * boolean value.
     */
    public static boolean parseBoolean(String pValue) {
        if ("1".equals(pValue)) {
            return true;
        } else if ("0".equals(pValue)) {
            return false;
        } else {
            throw new IllegalArgumentException("Illegal argument: " + pValue);
        }
    }
}
