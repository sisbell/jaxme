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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Indicates, that the annotated method is being used as a
 * factory method for an XML element. This is valid only,
 * if the containing class is annotated with the {@link
 * XmlRegistry &#64;XmlRegistry} annotation.
 * @see XmlRegistry
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface XmlElementDecl {
    /**
     * Indicates, that the factory method is restricted for
     * use within the given parent element class. Defaults
     * to {@link XmlElementDecl.GLOBAL}.
     */
    Class<?> scope() default GLOBAL.class;

    /**
     * Namespace of the XML element, which is being created.
     * The default value is "##default" and indicates, that the
     * namespace is the packages namespace.
     * @see #name()
     */
    String namespace() default "##default";

    /**
     * local name of the XML element.
     * @see #namespace()
     */
    String name();

    /**
     * Namespace name of a substitution group's head XML element.
     * @see #substitutionHeadName()
     */
    String substitutionHeadNamespace() default "##default";

    /**
     * Local name of a substitution group's head XML element.
     * @see #substitutionHeadNamespace()
     */
    String substitutionHeadName() default "";

    /**
     * Default value of this element. The default value '\u0000'
     * indicates the value null or "no default value".
     */
    String defaultValue() default "\u0000";
    
    /**
     * Used in {@link XmlElementDecl#scope()} to
     * signal that the declaration is in the global scope.
     */
    public final class GLOBAL {
        // No fields or methods.
    }
}
