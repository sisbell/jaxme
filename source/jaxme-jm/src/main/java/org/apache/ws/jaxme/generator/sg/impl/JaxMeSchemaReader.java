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
package org.apache.ws.jaxme.generator.sg.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBAppinfoImpl;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBGlobalBindingsImpl;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBXsObjectFactoryImpl;
import org.apache.ws.jaxme.xs.parser.XsObjectCreator;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JaxMeSchemaReader extends JAXBSchemaReader {
	/** The namespace URI of JaxMe vendor extensions for JAXB.
	 */
	public static final String JAXME_SCHEMA_URI = "http://ws.apache.org/jaxme/namespaces/jaxme2";
	/** The namespace URI of the JAXB RI's vendor extensions for JAXB.
	 */
    public static final String XJC_SCHEMA_URI = "http://java.sun.com/xml/ns/jaxb/xjc";
    private XjcSerializable xjcSerializable;
    private List xsObjectCreators = new ArrayList();

    /** Creates a new instance.
     */
    public JaxMeSchemaReader() {
        setSupportingExtensions(true);
    }

    protected void reset() {
    	super.reset();
        xjcSerializable = null;
    }

    private XsObjectCreator[] append(XsObjectCreator[] pBase) {
        XsObjectCreator[] baseResult = pBase;
        List myXsObjectCreators = getXsObjectCreators();
        List result;
        if (baseResult == null  ||  baseResult.length == 0) {
            result = myXsObjectCreators;
        } else {
            result = new ArrayList(baseResult.length + myXsObjectCreators.size());
            result.addAll(Arrays.asList(baseResult));
            result.addAll(myXsObjectCreators);
        }
        return (XsObjectCreator[]) myXsObjectCreators.toArray(new XsObjectCreator[myXsObjectCreators.size()]);
    }

    /** An extension of JAXBAppInfoImpl with support for
     * <code>xjc:serializable</code>.
     */
    public class JaxMeAppinfoImpl extends JAXBAppinfoImpl {
        JaxMeAppinfoImpl(XsObject pParent) {
            super(pParent);
        }

        protected XsObjectCreator[] getXsObjectCreators() {
            return append(super.getXsObjectCreators());
        }
    }

    /** An extension of
     * {@link org.apache.ws.jaxme.xs.jaxb.impl.JAXBGlobalBindingsImpl}
     * with support for <code>xjc:serializable</code>.
     */
    public class JaxMeGlobalBindingsImpl extends JAXBGlobalBindingsImpl {
        protected JaxMeGlobalBindingsImpl(XsObject pParent) {
            super(pParent);
        }

        protected XsObjectCreator[] getXsObjectCreators() {
            return append(super.getXsObjectCreators());
        }

        protected XsObject getBeanByParent(XsObject pParent, Locator pLocator, XsQName pQName)
        		throws SAXException {
            if (XJC_SCHEMA_URI.equals(pQName.getNamespaceURI())) {
                if ("serializable".equals(pQName.getLocalName())) {
                    if (getXjcSerializable() != null) {
                        throw new LocSAXException("The element xjc:serializable was already specified.", pLocator);
                    }
                    XjcSerializable s = new XjcSerializable(pParent);
                    setXjcSerializable(s);
                    return s;
                }
            }
            return super.getBeanByParent(pParent, pLocator, pQName);
        }
    }

    /** An extension of {@link JAXBXsObjectFactoryImpl} with support
     * for {@link JaxMeGlobalBindingsImpl} and {@link JaxMeAppinfoImpl}.
     */
    public class JaxMeXsObjectFactory extends JAXBXsObjectFactoryImpl {
        public XsEAppinfo newXsEAppinfo(XsObject pParent) {
            return new JaxMeAppinfoImpl(pParent);
        }
        public JAXBGlobalBindings newJAXBGlobalBindings(XsObject pParent) {
            return new JaxMeGlobalBindingsImpl(pParent);
        }
    }

    protected List getXsObjectCreators() {
        return xsObjectCreators;
    }

    public void addXsObjectCreator(XsObjectCreator pCreator) {
        if (pCreator == null) {
            throw new NullPointerException("The object creator must not be null.");
        }
        xsObjectCreators.add(pCreator);
    }

    public SGFactoryChain newSGFactoryChain(Generator pGenerator) {
        SGFactoryChain result = super.newSGFactoryChain(pGenerator);
        result = new SGFactoryChainImpl(result){
            public XsObjectFactory newXsObjectFactory(SGFactory pFactory) {
                return new JaxMeXsObjectFactory();
            }
        };
        
        return result;
    }

    public XjcSerializable getXjcSerializable() {
        return xjcSerializable;
    }

    public void setXjcSerializable(XjcSerializable pXjcSerializable) {
        xjcSerializable = pXjcSerializable;
    }
}
