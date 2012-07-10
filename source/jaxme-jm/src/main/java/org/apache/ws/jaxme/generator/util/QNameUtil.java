/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.jaxme.generator.util;

import javax.xml.namespace.QName;

import org.apache.ws.jaxme.xs.xml.XsQName;


/** Utility class for working with instances of
 * {@link javax.xml.namespace.QName}.
 */
public class QNameUtil {
	/** Creates a new instance of {@link QName}, matching
     * the given instance of {@link XsQName}.
	 */
    public static QName newQName(XsQName pXsQName) {
        String prefix = pXsQName.getPrefix();
        if (prefix == null) {
        	return new QName(pXsQName.getNamespaceURI(), pXsQName.getLocalName());
        } else {
        	return new QName(pXsQName.getNamespaceURI(), pXsQName.getLocalName(),
        			         pXsQName.getPrefix());
        }
	}
}
