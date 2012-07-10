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

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.DOMBuilder;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:documentation</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="documentation" id="documentation"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-documentation"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType mixed="true"&gt;
 *      &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:any processContents="lax"/&gt;
 *      &lt;/xs:sequence&gt;
 *      &lt;xs:attribute name="source" type="xs:anyURI"/&gt;
 *      &lt;xs:attribute ref="xml:lang"/&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEDocumentationImpl extends XsObjectImpl implements XsEDocumentation {
	private XmlLang lang;
	private XsAnyURI source;
	private List childs = new ArrayList();
    private boolean isSimple;

    protected XsEDocumentationImpl(XsObject pParent) {
		super(pParent);
	}

    protected void addChild(Object pObject) {
        childs.add(pObject);
    }

	public void setLang(XmlLang pLang) {
		lang = pLang;
	}
	
	public XmlLang getLang() {
		return lang;
	}
	
	public void setSource(XsAnyURI pSource) {
		source = pSource;
	}
	
	public XsAnyURI getSource() {
		return source;
	}
	
	public void addText(String pText) {
		childs.add(pText);
	}
	
	public String getText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0;  i < childs.size();  i++) {
        	Object o = childs.get(i);
            if (o instanceof String) {
            	sb.append(o);
            }
        }
		return sb.toString();
	}

    public boolean isTextOnly() {
    	for (int i = 0;  i < childs.size();  i++) {
    		if (!(childs.get(i) instanceof String)) {
    			return false;
            }
        }
        return true;
    }

    public Object[] getChilds() {
    	return childs.toArray();
    }

	public boolean setAttribute(String pQName, String pNamespaceURI, String pLocalName, String pValue) {
		if (XMLConstants.XML_NS_URI.equals(pNamespaceURI)  &&  "lang".equals(pLocalName)) {
			setLang(new XmlLang(pValue));
			return true;
		} else {
			return false;
		}
	}
	
	public ContentHandler getChildHandler(String pQName, String pNamespaceURI, String pLocalName)
            throws SAXException {
		isSimple = false;
        try {
			DOMBuilder db = new DOMBuilder();
			addChild(db.getDocument());
			return db;
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
	}
}
