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

import java.util.Set;

import javax.xml.bind.ValidationEvent;

/**
 * An adapter allows to implement a mapping between Java types
 * and a particular XML representations. For example, you would
 * want to use this for mapping a {@link Set}: The XML
 * representation might be a list of keys.
 * @param <BoundType> The actual object type. In the above example,
 *   this would be a {@link Set}.
 * @param <ValueType> The mapped type. In the above example, this
 *   would be a list.
 * @since JAXB 2.0
 */
public abstract class XmlAdapter<ValueType,BoundType> {
    /**
     * Protected constructor for access by derived classes
     * only.
     */
    protected XmlAdapter() {
        // Does nothing
    }

    /**
     * Called for converting an instance of the value type
     * to an instance of the bound type.
     *
     * @param pValue The value being converted, may be null.
     * @throws Exception An error occurred while converting
     *   the value. The caller must raise an
     *   {@link ValidationEvent}.
     */
    public abstract BoundType unmarshal(ValueType pValue) throws Exception;

    /**
     * Called for converting an instance of the bound type
     * into an instance of the value type.
     *
     * @param pValue The value being converted, may be null.
     * @throws Exception An error occurred while converting
     *   the value. The caller must raise an
     *   {@link ValidationEvent}.
     */
    public abstract ValueType marshal(BoundType pValue) throws Exception;
}
