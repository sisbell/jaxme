/*
 * Copyright 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.xs.xml;

/** <p>This interface contains the data contained in the schemas
 * root element.</p>
 */
public interface XsSchemaHeader {
	/** <p>Returns the "form" attributes default value for attributes.</p>
	 */
	public XsFormChoice getAttributeFormDefault();

	/** <p>Returns the "block" attributes default value.</p>
	 */
	public XsBlockSet getBlockDefault();

	/** <p>Returns the "form" attributes default value for elements.</p>
	 */
	public XsFormChoice getElementFormDefault();

	/** <p>Returns the "final" attributes default value.</p>
	 */
	public XsDerivationSet getFinalDefault();

	/** <p>Returns the schemas ID.</p>
	 */
	public XsID getId();

	/** <p>Returns the target namespace.</p>
	 */
	public XsAnyURI getTargetNamespace();

	/** <p>Returns the schema version.</p>
	 */
	public XsToken getVersion();

	/** <p>Returns a prefix associated with the target namespace or null, if
	 * no such prefix is available.</p>
	 */
	String getTargetNamespacePrefix();
}
