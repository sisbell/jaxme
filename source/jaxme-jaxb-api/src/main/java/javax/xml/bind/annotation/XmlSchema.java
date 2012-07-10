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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Used to control schema settings.
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target(PACKAGE)
public @interface XmlSchema {
    /**
     * Creates a mapping of namespace URI's to prefixes.
     */
    XmlNs[]  xmlns() default {};

    /**
     * The schemas target namespace. Defaults to "" (default namespace).
     */
    String namespace() default "";

    /**
     * Whether local elements are inheriting the target namespace.
     * Defaults to {@link XmlNsForm#UNSET}.
     */
    XmlNsForm elementFormDefault() default XmlNsForm.UNSET;

    /**
     * Whether attributes are inheriting the target namespace.
     * Defaults to {@link XmlNsForm#UNSET}.
     */
    XmlNsForm attributeFormDefault() default XmlNsForm.UNSET;

    /**
     * Indicates that this namespace (specified by {@link #namespace()})
     * has a schema already available exeternally, available at this location.
     * @since JAXB 2.1
     */
    String location() default NO_LOCATION;

    /**
     * The default value of the {@link #location()} attribute,
     * which indicates that the schema generator will generate
     * components in this namespace.
     */
    static final String NO_LOCATION = "##generate";
}
