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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Used to create a wrapper element for multivalued fields
 * or properties.
 * @see XmlElement 
 * @see XmlElements
 * @see XmlElementRef
 * @see XmlElementRefs
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlElementWrapper {
    /**
     * Name of the wrapper element. The default value is
     * "##default" and indicates, that the element name is
     * being derived from the field or property name.
     */
    String name() default "##default";

    /**
     * Namespace of the wrapper element. The default value
     * is "##default" and indicates, that the namespace
     * is the schemas target namespace (if
     * {@link XmlSchema#elementFormDefault() elementFormDefault}
     * is true) or "" (default namespace).
     */
    String namespace() default "##default";

    /**
     * If this property is true, then the wrapper element is
     * always present {@code xsi:nil} is used to indicate the
     * null value. Otherwise the wrapper element is omitted
     * to indicate the null value. Defaults to false.
     */
    boolean nillable() default false;

    /**
     * Indicates whether the wrapper element is required.
     * This is used to derive the wrapper elements {@code minOccurs}
     * value.
     * @since JAXB 2.1
     */
    boolean required() default false;
}
