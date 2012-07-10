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

import junit.framework.TestCase;

public class AdapterTest extends TestCase {
    public void testCollapsedStringAdapter() {
        CollapsedStringAdapter adapter = new CollapsedStringAdapter();
        assertNull(adapter.unmarshal(null));
        assertEquals("", adapter.unmarshal(""));
        assertEquals("", adapter.unmarshal(" "));
        assertEquals("", adapter.unmarshal("\r"));
        assertEquals("", adapter.unmarshal("\t"));
        assertEquals("", adapter.unmarshal("\n"));
        assertEquals("a", adapter.unmarshal(" a"));
        assertEquals("a", adapter.unmarshal("\ra"));
        assertEquals("a", adapter.unmarshal("\na"));
        assertEquals("a", adapter.unmarshal("\ta"));
        assertEquals("a", adapter.unmarshal("a "));
        assertEquals("a", adapter.unmarshal("a\r"));
        assertEquals("a", adapter.unmarshal("a\n"));
        assertEquals("a", adapter.unmarshal("a\t"));
        assertEquals("a", adapter.unmarshal(" a "));
        assertEquals("a", adapter.unmarshal("\ra\r"));
        assertEquals("a", adapter.unmarshal("\na\n"));
        assertEquals("a", adapter.unmarshal("\ta\t"));
        assertEquals("a b", adapter.unmarshal(" a  b "));
        assertEquals("a b", adapter.unmarshal("\ra\r\rb\r"));
        assertEquals("a b", adapter.unmarshal("\na\n\nb\n"));
        assertEquals("a b", adapter.unmarshal("\ta\t\tb\t"));
        
    }

    public void testNormalizedStringAdapter() {
        NormalizedStringAdapter adapter = new NormalizedStringAdapter();
        assertNull(adapter.unmarshal(null));
        assertEquals("", adapter.unmarshal(""));
        assertEquals(" abcdefg ", adapter.unmarshal(" abcdefg "));
        assertEquals(" a b c defg ", adapter.unmarshal(" a\rb\nc\tdefg "));
    }
}
