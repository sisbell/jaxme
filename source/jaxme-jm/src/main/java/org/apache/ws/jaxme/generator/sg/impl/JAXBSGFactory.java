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
package org.apache.ws.jaxme.generator.sg.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.impl.Inliner;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ObjectSGChain;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSGChain;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.SchemaTransformer;
import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBObjectFactoryImpl;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBXsObjectFactoryImpl;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSGFactory implements SGFactoryChain {
	private final static Logger log = LoggerAccess.getLogger(JAXBSGFactory.class);
	private final Generator generator;
	private SchemaSG schemaSG;
	private Map groups = new HashMap();
	private Map objects = new HashMap();
	private Map types = new HashMap();
	private List groupsByOrder = new ArrayList();
	private List objectsByOrder = new ArrayList();
	private List typesByOrder = new ArrayList();
	
	/** Creates a new instance for the given {@link Generator}.
	 */
    public JAXBSGFactory(Generator pGenerator) {
		generator = pGenerator;
	}
	
	public void init(SGFactory pController) {
	}
	
	public Generator getGenerator(SGFactory pController) {
		return generator;
	}
	
	public GroupSG getGroupSG(SGFactory pController, XSGroup pGroup) throws SAXException {
		GroupSG result = (GroupSG) groups.get(pGroup);
		if (result == null) {
			result = pController.newGroupSG(pGroup);
			groups.put(pGroup, result);
			groupsByOrder.add(result);
			result.init();
		}
		return result;
	}
	
	public GroupSG newGroupSG(SGFactory pController, XSGroup pGroup) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created");
		}
		return new GroupSGImpl(new JAXBGroupSG(pController, schemaSG, pGroup));
	}
	
	public GroupSG getGroupSG(SGFactory pController, XSGroup pGroup, Context pClassContext) throws SAXException {
		GroupSG result = pController.newGroupSG(pGroup, pClassContext);
		result.init();
		groupsByOrder.add(result);
		return result;
	}
	
	public GroupSG newGroupSG(SGFactory pController, XSGroup pGroup, Context pClassContext) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created");
		}
		return new GroupSGImpl(new JAXBGroupSG(pController, schemaSG, pGroup, pClassContext));
	}
	
	
	public Object newObjectSG(SGFactory pController, XSElement pElement) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created.");
		}
		return new JAXBObjectSG(pController, schemaSG, pElement);
	}
	
	public ObjectSG getObjectSG(SGFactory pController, XSElement pElement) throws SAXException {
		ObjectSG result = (ObjectSG) objects.get(pElement);
		if (result == null) {
			ObjectSGChain chain = (ObjectSGChain) pController.newObjectSG(pElement);
			result = new ObjectSGImpl(chain);
			objects.put(pElement, result);
			objectsByOrder.add(result);
			result.init();
		}
		return result;
	}

	public Object newObjectSG(SGFactory pController, XSAny pAny) {
	    if (schemaSG == null) {
	        throw new IllegalStateException("A schema has not yet been created.");
	    }
	    return new JAXBObjectSG(pController, schemaSG, pAny);
	}

	public ObjectSG getObjectSG(SGFactory pController, XSAny pAny, Context pContext) throws SAXException {
	    ObjectSGChain chain = (ObjectSGChain) pController.newObjectSG(pAny);
	    ObjectSG result = new ObjectSGImpl(chain);
	    result.init();
	    return result;
	}

	public Object newObjectSG(SGFactory pController, XSElement pElement, Context pContext) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created.");
		}
		return new JAXBObjectSG(pController, schemaSG, pElement, pContext);
	}
	
	public ObjectSG getObjectSG(SGFactory pController, XSElement pElement, Context pContext) throws SAXException {
		ObjectSGChain chain = (ObjectSGChain) pController.newObjectSG(pElement, pContext);
		ObjectSG result = new ObjectSGImpl(chain);
		result.init();
		return result;
	}
	
	public SchemaSG getSchemaSG(SGFactory pController, XSSchema pSchema) throws SAXException {
		if (schemaSG == null) {
			SchemaSGChain chain = (SchemaSGChain) pController.newSchemaSG(pSchema);
			schemaSG = new SchemaSGImpl(chain);
			schemaSG.init();
		}
		return schemaSG;
	}
	
	public SchemaSG getSchemaSG(SGFactory pController) {
		if (schemaSG == null) {
			throw new IllegalStateException("A factory has not yet been created.");
		}
		return schemaSG;
	}
	
	public Object newSchemaSG(SGFactory pController, XSSchema pSchema) throws SAXException {
		return new JAXBSchemaSG(pController, pSchema);
	}
	
	public TypeSG getTypeSG(SGFactory pController, XSType pType, JAXBProperty.BaseType pBaseType) throws SAXException {
		final String mName = "getTypeSG(XSType)";
		TypeSG result = (TypeSG) types.get(pType);
		if (result == null  ||  pBaseType != null) {
			log.finest(mName, "->", pType.getName());
			TypeSGChain chain = (TypeSGChain) pController.newTypeSG(pType, pBaseType);
			result = new TypeSGImpl(chain);
			if (pBaseType == null) {
			    types.put(pType, result);
			    typesByOrder.add(result);
            }
			result.init();
			log.finest(mName, "<-", new Object[]{chain, result});
		}
		return result;
	}
	
	public Object newTypeSG(SGFactory pController, XSType pType, JAXBProperty.BaseType pBaseType) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created");
		}
		return new JAXBTypeSG(pController, schemaSG, pType, pBaseType);
	}
	
	public TypeSG getTypeSG(SGFactory pController, XSType pType, Context pClassContext, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException {
		final String mName = "getTypeSG(XSType,ClassContext)";
		log.finest(mName, "->", new Object[]{pType, pClassContext});
        TypeSGChain chain = (TypeSGChain) pController.newTypeSG(pType, pClassContext, pName, pBaseType);
		TypeSG result = new TypeSGImpl(chain);
		typesByOrder.add(result);
		result.init();
		log.finest(mName, "<-", new Object[]{chain, result});
		return result;
	}
	
	public Object newTypeSG(SGFactory pController, XSType pType, Context pClassContext, XsQName pName,
			JAXBProperty.BaseType pBaseType) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created");
		}
		return new JAXBTypeSG(pController, schemaSG, pType, pClassContext, pName, pBaseType);
	}
	
	public Object newTypeSG(SGFactory pController, XSType pType, XsQName pName,
			JAXBProperty.BaseType pBaseType) throws SAXException {
		if (schemaSG == null) {
			throw new IllegalStateException("A schema has not yet been created");
		}
		return new JAXBTypeSG(pController, schemaSG, pType, pName, pBaseType);
	}
	
	public TypeSG getTypeSG(SGFactory pController, XSType pType, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException {
		TypeSGChain chain = (TypeSGChain) pController.newTypeSG(pType, pName, pBaseType);
		TypeSG result = new TypeSGImpl(chain);
		typesByOrder.add(result);
		result.init();
		return result;
	}
	
	public XSParser newXSParser(SGFactory pController) throws SAXException {
		XSParser parser = new JAXBParser();
		XSContext context = parser.getContext();
		context.setXsObjectFactory(pController.newXsObjectFactory());
		context.setXSObjectFactory(pController.newXSObjectFactory());
		return parser;
	}
	
	public Facet newFacet(SGFactory pController, XSType pType, XSEnumeration[] pEnumerations) throws SAXException {
		return new FacetImpl(pType, pEnumerations);
	}
	
	public TypeSG[] getTypes(SGFactory pController) {
		return (TypeSG[]) typesByOrder.toArray(new TypeSG[typesByOrder.size()]);
	}
	
	public GroupSG[] getGroups(SGFactory pController) {
		return (GroupSG[]) groupsByOrder.toArray(new GroupSG[groupsByOrder.size()]);
	}
	
	public ObjectSG[] getObjects(SGFactory pController) {
		return (ObjectSG[]) objectsByOrder.toArray(new ObjectSG[objectsByOrder.size()]);
	}
	
	public XsObjectFactory newXsObjectFactory(SGFactory pController) throws SAXException {
		return new JAXBXsObjectFactoryImpl(){
			public XMLReader newXMLReader(boolean pValidating) throws ParserConfigurationException, SAXException{
				XMLReader xr = super.newXMLReader(pValidating);
				EntityResolver entityResolver = generator.getEntityResolver();
				if (entityResolver != null) {
					xr.setEntityResolver(entityResolver);
				}
				return xr;
			}
		};
	}
	
	public XSObjectFactory newXSObjectFactory(SGFactory pController) throws SAXException {
		return new JAXBObjectFactoryImpl(){
			public SchemaTransformer getSchemaTransformer() {
				Document[] bindings = generator.getBindings();
				if (bindings == null  ||  bindings.length == 0) {
					return null;
				} else {
					return new Inliner(bindings);
				}
			}
		};
	}
}
