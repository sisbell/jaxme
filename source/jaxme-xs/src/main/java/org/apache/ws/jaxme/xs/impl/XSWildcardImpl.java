/*
 * Copyright 2003,2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.xs.impl;

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList;
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.apache.ws.jaxme.xs.xml.XsTWildcard;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSWildcardImpl extends XSOpenAttrsImpl implements XSWildcard {
    private boolean isValidated;
    private XSAnnotation[] annotations;
    private final XsEAnnotation xsAnnotation;
    
    protected XSWildcardImpl(XSObject pParent, XsTWildcard pBaseObject) {
		super(pParent, pBaseObject);
		xsAnnotation = pBaseObject.getAnnotation();
	}
	
	public XsNamespaceList getNamespaceList() {
		return ((XsTWildcard) getXsObject()).getNamespace();
	}

	public XsTWildcard.ProcessContents getProcessContents() {
		return ((XsTWildcard) getXsObject()).getProcessContents();
	}

	protected boolean isValidated() {
	    return isValidated;
	}

	public void validate() throws SAXException {
	    if (isValidated()) {
	        return;
	    } else {
	        isValidated = true;
	    }

	    if (xsAnnotation == null) {
	        annotations = new XSAnnotation[0];
	    } else {
	        XSAnnotation ann = getXSSchema().getXSObjectFactory().newXSAnnotation(this, xsAnnotation);
	        annotations = new XSAnnotation[]{ ann };
	        ann.validate();
	    }
	}

	public XsSchemaHeader getSchemaHeader() {
	    return getXsObject().getXsESchema();
	}

	public XSAnnotation[] getAnnotations() {
	    return annotations;
	}
}
