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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Maps a fields or properties type to one of the simple types,
 * which are builtin to XML schema.
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PACKAGE})        
public @interface XmlSchemaType {
    /**
     * The XML schema types local name.
     */
    String name();

    /**
     * The XML schema types namespace; defaults to
     * "http://www.w3.org/2001/XMLSchema".
     */
    String namespace() default "http://www.w3.org/2001/XMLSchema";

    /**
     * If the annotated object is a package, then the mapped
     * Java type must be specified as well. By default, the
     * annotated fields or properties type is mapped.
     */
    Class<?> type() default DEFAULT.class;

    /**
     * Used by {@link XmlSchemaType#type()} to indicate
     * that the field or properties Java type is being
     * used.
     */
    static final class DEFAULT {
        // Does nothing.
    }
}
