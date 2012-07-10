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

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Indicates, that the annotated class or enumeration is being
 * mapped as an XML Schema type.
 * @see XmlElement
 * @see XmlAttribute
 * @see XmlValue
 * @see XmlSchema
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlType {
    /**
     * Local name of the XML Schema type. The default value
     * of "##default" indicates, that the name is being
     * derived from the class name.
     */
    String name() default "##default" ;
 
    /**
     * Specifies the order of the types fields or properies
     * in the XML Schema type. Contains a list of field or
     * property names.
     */
    String[] propOrder() default {""};

    /**
     * Namespace of the XML Schema type. The default value
     * of "##default" indicates, that the packages target
     * namespace is being used.
     */
    String namespace() default "##default" ;
   
    /**
     * Indicates the factory class for creating an instance.
     */
    Class<?> factoryClass() default DEFAULT.class;

    /**
     * Used by {@link XmlType#factoryClass()} as a default
     * value. 
     */
    static final class DEFAULT {
        // No fields or methods.
    }

    /**
     * Name of the factory method in the class specified by
     * {@link #factoryClass()}. The default value of ""
     * indicates, that no factory method is being used.
     * 
     */
    String factoryMethod() default "";
}


