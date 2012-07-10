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
package javax.xml.bind;

import  javax.xml.namespace.QName;


/**
 * An instance of this class is created by the {@link JAXBContext}.
 * It's purpose is to provide metadata for JAXB objects.
 * @see JAXBContext#createJAXBIntrospector()
 * @since JAXB 2.0
 */
public abstract class JAXBIntrospector {
    /**
     * Returns, whether the given object is a JAXB element.
     * In other words, it returns whether the object is an
     * instance of {@link JAXBElement} or its class is
     * annotated with {@code &#64XmlRootElement}.
     */
    public abstract boolean isElement(Object object);

    /**
     * Returns the given JAXB elements tag name.
     * @param jaxbElement A JAXB element, as indicated by
     *   {@link #isElement(Object)}.
     * @return The JAXB elements tag name or null, if the object
     *   is no JAXB element.
     */
    public abstract QName getElementName(Object jaxbElement);

    /**
     * Returns the JAXB elements actual value. This is a
     * convenience method, which allows to handle
     * instances of {@link JAXBElement} in the same way
     * than instances of a Java class, which is annotated
     * with {@code &#64XmlRootElement}.
     * @param jaxbElement A JAXB element, as indicated by
     *   {@link #isElement(Object)}.
     * @return The element value of the <code>jaxbElement</code>.
     */
    public static Object getValue(Object jaxbElement) {
        if (jaxbElement instanceof JAXBElement) {
            return ((JAXBElement)jaxbElement).getValue();
        }
        return jaxbElement;
    }
}
