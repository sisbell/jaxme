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
 
package org.apache.ws.jaxme.generator.ino.api4j;

import java.util.HashSet;
import java.util.Set;

import org.apache.ws.jaxme.JMElement;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.AttributeSGChain;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ObjectSGChain;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.AttributeSGImpl;
import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.ObjectSGChainImpl;
import org.apache.ws.jaxme.generator.sg.impl.SGFactoryChainImpl;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.pm.ino.InoObject;
import org.apache.ws.jaxme.pm.ino.InoResponseHandler;
import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBAttribute;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBType;
import org.apache.ws.jaxme.xs.parser.XsObjectCreator;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TaminoAPI4JSG extends SGFactoryChainImpl {
    public static final String TAMINOAPI4J_SCHEMA_URI = "http://ws.apache.org/jaxme/namespaces/jaxme2/TaminoAPI4J";
    private static final JavaQName INO_OBJECT_TYPE = JavaQNameImpl.getInstance(InoObject.class);
    private static final JavaQName JMELEMENT_TYPE = JavaQNameImpl.getInstance(JMElement.class);

    public abstract class InoDetails extends XsObjectImpl {
        private String collection;
        InoDetails(XsObject pParent) {
            super(pParent);
        }
        public void setCollection(String pCollection) {
            collection = pCollection;
        }
        public String getCollection() {
            String p = "taminoapi4j.collection";
            return sgFactory.getGenerator().getProperty(p, collection);
        }
    }

    public class DbDetails extends InoDetails {
    	private String url, user, password;
    	DbDetails(XsObject pParent) {
    		super(pParent);
    	}
        public void setUrl(String pUrl) {
            url = pUrl;
        }
        public String getUrl() {
            String p = "taminoapi4j.url";
            return sgFactory.getGenerator().getProperty(p, url);
        }
        public void setUser(String pUser) {
            user = pUser;
        }
        public String getUser() {
            String p = "taminoapi4j.user";
            return sgFactory.getGenerator().getProperty(p, user);
        }
        public void setPassword(String pPassword) {
            password = pPassword;
        }
        public String getPassword() {
            String p = "taminoapi4j.password";
            return sgFactory.getGenerator().getProperty(p, password);
        }
    }

    public class RaDetails extends InoDetails {
        private String jndiReference;
        RaDetails(XsObject pParent) {
            super(pParent);
        }
        public void setJndiReference(String pReference) {
            jndiReference = pReference;
        }
        public String getJndiReference() {
            String p = "taminoapi4j.jndiReference";
            return sgFactory.getGenerator().getProperty(p, jndiReference);
        }
    }

    private RaDetails raDetails;
    private DbDetails dbDetails;
    private SGFactory sgFactory;

    public TaminoAPI4JSG(SGFactoryChain o) {
        super(o);
    }

    public void setRaDetails(RaDetails pDetails) throws SAXException {
        raDetails = pDetails;
    }
    public RaDetails getRaDetails() {
        return raDetails;
    }

    public void setDbDetails(DbDetails pDetails) {
        dbDetails = pDetails;
    }
    public DbDetails getDbDetails() {
        return dbDetails;
    }

    public void init(SGFactory pFactory) {
        super.init(pFactory);
        sgFactory = pFactory;
        SchemaReader schemaReader = pFactory.getGenerator().getSchemaReader();
        if (schemaReader instanceof JaxMeSchemaReader) {
            JaxMeSchemaReader jaxmeSchemaReader = (JaxMeSchemaReader) schemaReader;
            jaxmeSchemaReader.addXsObjectCreator(new XsObjectCreator(){
                public XsObject newBean(XsObject pParent, Locator pLocator, XsQName pQName) throws SAXException {
                	if (pParent instanceof JAXBGlobalBindings) {
                        if (TAMINOAPI4J_SCHEMA_URI.equals(pQName.getNamespaceURI())) {
                            if ("raDetails".equals(pQName.getLocalName())) {
                                if (getRaDetails() != null) {
                                    throw new LocSAXException("An element named " + pQName + " has already been specified.",
                                            				  pLocator);
                                }
                                if (getDbDetails() != null) {
                                    throw new LocSAXException("The elements dbDetails and raDetails are mutually exclusive.",
                                            				  pLocator);
                                }
                                RaDetails details = new RaDetails(pParent);
                                setRaDetails(details);
                                return details;
                            } else if ("dbDetails".equals(pQName.getLocalName())) {
                            	if (getDbDetails() != null) {
                            		throw new LocSAXException("An element named " + pQName + " has already been specified.",
                            				pLocator);
                            	}
                            	if (getRaDetails() != null) {
                                    throw new LocSAXException("The elements dbDetails and raDetails are mutually exclusive.",
                                            				  pLocator);
                                }
                            	DbDetails details = new DbDetails(pParent);
                            	setDbDetails(details);
                            	return details;
                            } else {
                                throw new LocSAXException("Invalid element name: " + pQName, pLocator);
                            }
                        }
                    }
                    return null;
                }
            });
        } else {
            throw new IllegalStateException("The schema reader " + schemaReader.getClass().getName() +
                    " is not an instance of " + JaxMeSchemaReader.class.getName());
        }
    }

    public Object newSchemaSG(SGFactory pController, XSSchema pSchema) throws SAXException {
        SchemaSGChain chain = (SchemaSGChain) super.newSchemaSG(pController, pSchema);
        chain = new TaminoAPI4JSchemaSG(chain, this);
        return chain;
    }

    private class InoJAXBProperty implements JAXBProperty {
        private final String name;
        public InoJAXBProperty(String pName) {
            name = pName;
        }
        public String getName() { return name; }
        public String getCollectionType() { return null; }
        public Boolean isFixedAttributeAsConstantProperty() { return null; }
        public Boolean isGenerateIsSetMethod() { return null; }
        public Boolean isEnableFailFastCheck() { return null; }
        public JAXBJavadoc getJavadoc() { return null; }
        public BaseType getBaseType() { return null; }
        public XsESchema getXsESchema() { return null; }
        public boolean isTopLevelObject() { return false; }
        public XsObject getParentObject() { return null; }
        public XsObjectFactory getObjectFactory() { return null; }
        public Locator getLocator() { return null; }
        public void validate() throws SAXException {
        }
    }

    private class InoAttribute implements JAXBAttribute {
        private final XsQName qName;
        private final XSType type;
        private final XSType parent;
        private final JAXBProperty jaxbProperty;

        public InoAttribute(XSType pParent, XsQName pName, XSType pType, String pPropertyName) {
            qName = pName;
            type = pType;
            parent = pParent;
            jaxbProperty = new InoJAXBProperty(pPropertyName);
        }
        public boolean isGlobal() { return true; }
        public XsQName getName() { return qName; }
        public XSType getType() { return type; }
        public boolean isOptional() { return true; }
        public XSAnnotation[] getAnnotations() { return new XSAnnotation[0]; }
        public String getDefault() { return null; }
        public String getFixed() { return null; }
        public Attributes getOpenAttributes() { return new AttributesImpl(); }
        public XSSchema getXSSchema() { return parent.getXSSchema(); }
        public boolean isTopLevelObject() { return true; }
        public XSObject getParentObject() { return null; }
        public Locator getLocator() { return parent.getLocator(); }
        public void validate() throws SAXException {
        }
        public JAXBSchemaBindings getJAXBSchemaBindings() {
            return ((JAXBType) parent).getJAXBSchemaBindings();
        }
        public JAXBProperty getJAXBProperty() {
            return jaxbProperty;
        }
    }

    protected void addAttribute(TypeSG pComplexTypeSG, XSType pParent,
								XsQName pName, XSType pType, String pPropertyName)
    		throws SAXException {
        AttributeSG[] attrs = pComplexTypeSG.getComplexTypeSG().getAttributes();
        for (int i = 0;  i < attrs.length;  i++) {
            AttributeSG attr = attrs[i];
            if (attr.isWildcard()) {
                continue;
            }
			XsQName qName = attr.getName();
			if (qName.equals(pName)) {
			    return;
            }
        }

        XSAttribute attribute = new InoAttribute(pParent, pName, pType, pPropertyName);
        AttributeSGChain chain = (AttributeSGChain) pComplexTypeSG.getComplexTypeSG().newAttributeSG(attribute);
        AttributeSG attributeSG = new AttributeSGImpl(chain);
        pComplexTypeSG.getComplexTypeSG().addAttributeSG(attributeSG);
    }

    public Object newObjectSG(SGFactory pFactory, XSElement pElement) throws SAXException {
        ObjectSGChain chain = (ObjectSGChain) super.newObjectSG(pFactory, pElement);
        chain = new ObjectSGChainImpl(chain){
            public JavaSource getXMLImplementation(ObjectSG pController) throws SAXException {
                JavaSource result = super.getXMLImplementation(pController);
                if (result == null) {
                	return null;
                }
                JavaQName[] interfaces = result.getImplements();
                boolean done = false;
                result.clearImplements();
                for (int i = 0;  i < interfaces.length;  i++) {
                    JavaQName interfaceName = interfaces[i];
                    if (interfaceName.equals(JMELEMENT_TYPE)  ||
                        interfaceName.equals(INO_OBJECT_TYPE)) {
                        if (!done) {
                            result.addImplements(INO_OBJECT_TYPE);
                            done = true;
                        } 
                    } else {
                        result.addImplements(interfaceName);
                    }
                }
                if (!done) {
                    result.addImplements(INO_OBJECT_TYPE);
                } 
                return result;
            }
        };
        return chain;
    }

    private Set elementNames = new HashSet();
    public ObjectSG getObjectSG(SGFactory pFactory, XSElement pElement) throws SAXException {
        ObjectSG result = super.getObjectSG(pFactory, pElement);
        if (result.getTypeSG().isComplex()) {
	        XsQName elementName = pElement.getName();
	        if (!elementNames.contains(elementName)) {
	            elementNames.add(elementName);
		        // Does the element have an ino:id attribute? If not, create it
		        XsQName qName = new XsQName(InoResponseHandler.INO_RESPONSE2_URI, "id", "ino");
		        XSType stringType = XSString.getInstance();
		        addAttribute(result.getTypeSG(), pElement.getType(), qName, stringType, "inoId");
		        // Does the element have an ino:docname attribute? If not, create it
		        qName = new XsQName(InoResponseHandler.INO_RESPONSE2_URI, "docname", "ino");
		        addAttribute(result.getTypeSG(), pElement.getType(), qName, stringType, "inoDocname");
	        }
        }
        return result;
    }
}
