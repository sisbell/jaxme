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
import static java.lang.annotation.ElementType.PACKAGE;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This container annotations solves the problem, that a package
 * can carry a single {@link XmlSchemaType &#64;XmlSchemaType}
 * annotation only.
 * @see XmlSchemaType
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({PACKAGE})
public @interface XmlSchemaTypes {
    /**
     * Contains the packages set of
     * {@link XmlSchemaType &#64;XmlSchemaType} annotations.
     */
    XmlSchemaType[] value();
}
