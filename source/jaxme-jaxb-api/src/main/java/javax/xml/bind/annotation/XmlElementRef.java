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
 * Indicates, that a field or property is mapped to an XML element
 * reference.
 * @see XmlElementRefs
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlElementRef {
    /**
     * The referenced Java type. The default value indicates,
     * that the referenced Java type is derived from the fields
     * or properties type.
     */
    Class<?> type() default DEFAULT.class;

    /**
     * Used together with {@link #name()} to indicate the
     * referenced XML element. The default value is "".
     */
    String namespace() default "";
    /**
     * Used together with {@link #namespace()} to indicate the
     * referenced XML element. The default value is "##default".
     */
    String name() default "##default";

    /**
     * Used in {@link #type()} as a default value.
     */
    static final class DEFAULT {
        // No additional fields or methods.
    }
}