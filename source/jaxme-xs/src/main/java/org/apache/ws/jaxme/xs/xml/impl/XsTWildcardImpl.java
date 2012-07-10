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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.xml.*;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList.Basic;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList.Other;


/** <p>Implementation of the <code>xs:wildcard</code> type, with the
 * following specification:
 * <pre>
 *  &lt;xs:complexType name="wildcard"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:attribute name="namespace" type="xs:namespaceList" use="optional" default="##any"/&gt;
 *        &lt;xs:attribute name="processContents" use="optional" default="strict"
 *          &lt;xs:simpleType&gt;
 *            &lt;xs:restriction base="xs:NMTOKEN"&gt;
 *              &lt;xs:enumeration value="skip"/&gt;
 *              &lt;xs:enumeration value="lax"/&gt;
 *              &lt;xs:enumeration value="strict"/&gt;
 *            &lt;/xs:restriction&gt;
 *          &lt;/xs:simpleType&gt;
 *        &lt;/xs:attribute&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTWildcardImpl extends XsTAnnotatedImpl implements XsTWildcard {
	private XsNamespaceList namespaceList = XsNamespaceList.ANY;
	private ProcessContents processContents = STRICT;
	
	protected XsTWildcardImpl(XsObject pParent) {
		super(pParent);
	}
	
	public void setNamespace(final String pNamespaceList) {
        if (getXsESchema().getTargetNamespace() == null) {
            // The target can be changed, when importing this schema,
            // thus we have to return a mutable object.
            XsAnyURI pTargetNamespace = getXsESchema().getTargetNamespace();
            if ("##any".equals(pNamespaceList)) {
                namespaceList = XsNamespaceList.ANY;
            } else if ("##other".equals(pNamespaceList)) {
                namespaceList = new Other(pTargetNamespace){
                    public XsAnyURI[] getUris() {
                        XsAnyURI targetNamespace = getXsESchema().getTargetNamespace();
                        if (targetNamespace == null) {
                            return super.getUris();
                        } else {
                            return new XsAnyURI[]{targetNamespace};
                        }
                    }
                };
            } else {
                namespaceList = new Basic(pNamespaceList, pTargetNamespace){
                    public XsAnyURI[] getUris() {
                        XsAnyURI targetNamespace = getXsESchema().getTargetNamespace();
                        if (targetNamespace == null) {
                            return super.getUris();
                        } else {
                            return XsNamespaceList.valueOf(pNamespaceList, targetNamespace).getUris();
                        }
                    }
                };
            };
        } else {
            // The target cannot be changed, so we return an immutable object.
            namespaceList = XsNamespaceList.valueOf(pNamespaceList, getXsESchema().getTargetNamespace());
        }
	}
	
	public XsNamespaceList getNamespace() {
		return namespaceList;
	}
	
	public void setProcessContents(ProcessContents pProcessContents) {
		processContents = pProcessContents;
	}
	
	public ProcessContents getProcessContents() {
		return processContents;
	}
}
