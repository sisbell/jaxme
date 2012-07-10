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
import static java.lang.annotation.ElementType.TYPE;


/**
 * This annotation indicates, that a bean class is being mapped
 * as an XML element.
 * @since JAXB 2.0
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRootElement {
    /**
     * Namespace of the XML element; if the default value
     * ("##default") is used, then the namespace will be
     * derived from the package name.
     */
    String namespace() default "##default";

    /**
     * Local name of the XML element; if the default value
     * ("##default") is used, then the name will be derived
     * from the class name.
     */
    String name() default "##default";
}
