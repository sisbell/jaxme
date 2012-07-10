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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.PACKAGE;


/**
 * An annotation, which allows to specify an {@link XmlAdapter}
 * for custom marshalling and unmarshalling.
 * @since JAXB 2.0
 */

@Retention(RUNTIME)
@Target({PACKAGE,FIELD,METHOD,TYPE,PARAMETER})        
public @interface XmlJavaTypeAdapter {
    /**
     * Specifies the {@link XmlAdapter} class, which converts
     * bound types to values and vice versa.
     */
    Class<? extends XmlAdapter<?,?>> value();

    /**
     * Specifies the annotated objects type. This is usually
     * not required, unless you use this annotation at the
     * package level.
     */
    Class<?> type() default DEFAULT.class;

    /**
     * Used as the default in {@link #type()} to indicate,
     * that the type should be derived from the annotated
     * object.
     */
    static final class DEFAULT { /* Do nothing */ }
}
