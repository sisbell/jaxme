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
package javax.xml.bind;

import javax.xml.transform.Result;
import java.io.IOException;


/**
 * An object, which controls the target of a set of
 * schema documents.
 */
public abstract class SchemaOutputResolver {
    /**
     * Returns an instance of {@link Result}, to which the
     * given schema document shall be written.
     * @param pNamespaceURI The schema documents namespace URI.
     * @param pFileName The suggested schema documents file name.
     * @return A valid instance of {@link Result} or null, if the
     *   schema document shall be skipped.
     */
    public abstract Result createOutput(String pNamespaceURI, String pFileName) throws IOException;
}
