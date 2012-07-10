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
package javax.xml.bind.annotation;



/**
 * Used by XmlAccessorType to control serialization of fields or
 * properties. 
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since JAXB2.0
 * @version $Revision: 1.1 $
 * @see XmlAccessorType
 */

public enum XmlAccessType {
    /**
     * Properties are mapped to XML, unless annotated by {@link XmlTransient}.
     * Fields are only mapped to XML, if specifically requested
     * by JAXB annotations.
     */
    PROPERTY,
    /**
     * All fields (with the exception of static and transient fields)
     * will be mapped to XML, unless annotated by {@link XmlTransient}.
     * Properties are only mapped to XML, if specifically requested
     * by JAXB annotations.
     */
    FIELD,
    /**
     * Public fields or properties are mapped to XML, unless
     * annotated by {@link XmlTransient}.
     * Other fields or properties are only mapped to XML, if
     * specifically requested by JAXB annotations.
     */
    PUBLIC_MEMBER,
    /**
     * A field or property is only mapped to XML, if specifically
     * requested by JAXB annotations.
     */
    NONE
}

