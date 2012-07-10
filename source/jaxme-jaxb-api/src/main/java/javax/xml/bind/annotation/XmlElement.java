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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates, that a field or property is being mapped to
 * an XML element.
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlElement {
    /**
     * Name of the child element. The default value is
     * "##default" and indicates, that the element name
     * is derived from the field or property name.
     */
    String name() default "##default";
 
    /**
     * Indicates, that the element is nillable.
     */
    boolean nillable() default false;

    /**
     * Indicates, that the element is required. This is used
     * to derive the {@code minOccurs} value, which is either
     * "0" or "1". The {@code maxOccurs} value depends on the
     * field or property type: It is "1" for a single valued
     * field or property, otherwise "unbounded".
     */
    boolean required() default false;

    /**
     * Indicates the elements target namespace. The default value
     * is "##default" and indicates, that the schemas target namespace
     * is used, depending on the schema settings. The value
     * "" indicates the default namespace.
     */
    String namespace() default "##default";

    /**
     * Indicates the elements default value. The special value
     * '\u0000' is used for null or "no default value".
     */
    String defaultValue() default "\u0000";

    /**
     * Indicates the mapped elements type. By default, the
     * element type is derived from the field or property
     * type.
     */
    Class<?> type() default DEFAULT.class;

    /**
     * Default value for use in {@link #type()}, which
     * indicates that the element type is derived from the
     * field or property type.
     */
    static final class DEFAULT {
        // No fields or methods.
    }
}


