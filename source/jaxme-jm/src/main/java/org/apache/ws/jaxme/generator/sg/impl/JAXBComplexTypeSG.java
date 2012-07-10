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
import java.util.List;

import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.AttributeSGChain;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexContentSGChain;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.AllHandlerSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.BeanGeneratingVisitor;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.ChoiceHandlerSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.DriverGeneratingVisitor;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.EmptyElementHandlerSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.HandlerSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.ParticleWalker;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.SequenceHandlerSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.SimpleContentHandlerSG;
import org.apache.ws.jaxme.impl.JMSAXDriver;
import org.apache.ws.jaxme.impl.JMSAXElementParser;
import org.apache.ws.jaxme.impl.JMSAXMixedElementParser;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class JAXBComplexTypeSG implements ComplexTypeSGChain {
    private final static Logger log = LoggerAccess.getLogger(JAXBComplexTypeSG.class);
	private final TypeSG typeSG;
	private final XSType xsType;
	private final boolean hasSimpleContent;
	private final Context classContext;
	private SimpleContentSG simpleContentSG;
	private ComplexContentSG complexContentSG;
	private AttributeSG[] attributes;
	
	protected JAXBComplexTypeSG(TypeSG pTypeSG, XSType pType) throws SAXException {
		final String mName = "<init>(XSType)";
		log.finest(mName, "->", new Object[]{pTypeSG, pType});
		typeSG = pTypeSG;
		xsType = pType;
		XSComplexType complexType= pType.getComplexType();
		hasSimpleContent = complexType.hasSimpleContent();
		String suffix = pTypeSG.isGlobalType() ? null : "Type"; 
		classContext = new GlobalContext(pTypeSG.getName(), pType, null, suffix, pTypeSG.getSchema());
		log.finest(mName, "<-", classContext);
	}
	
	/** <p>Constructor for a local type, which is embedded into the enclosing
	 * <code>pContext</code>.</p>
	 */
	protected JAXBComplexTypeSG(TypeSG pTypeSG, XSType pType, Context pContext) throws SAXException {
		final String mName = "<init>(XSType)";
		log.finest(mName, "->", new Object[]{pTypeSG, pType, pContext});
		typeSG = pTypeSG;
		xsType = pType;
		XSComplexType complexType= pType.getComplexType();
		hasSimpleContent = complexType.hasSimpleContent();
		classContext = new LocalContext(pContext, pTypeSG.getName().getLocalName(), pType, null, "Type",
				pTypeSG.getSchema());
		log.finest(mName, "<-", classContext);
	}
	
	public Object newAttributeSG(ComplexTypeSG pController, XSAttribute pAttribute) throws SAXException {
		return new JAXBAttributeSG(typeSG.getSchema(), pAttribute, classContext);
	}

	public Object newAttributeSG(ComplexTypeSG pController, XSWildcard pWildcard) throws SAXException {
		return new JAXBAttributeSG(typeSG.getSchema(), pWildcard, classContext);
	}

	private AttributeSG[] initAttributes(ComplexTypeSG pController) throws SAXException {
		XSAttributable[] xsAttributes = xsType.getComplexType().getAttributes();
		List attributeList = new ArrayList();
		for (int i = 0;  i < xsAttributes.length;  i++) {
			AttributeSGChain attrChain;
			if (xsAttributes[i] instanceof XSAttribute) {
				XSAttribute attr = (XSAttribute) xsAttributes[i];
				attrChain = (AttributeSGChain) pController.newAttributeSG(attr);
			} else if (xsAttributes[i] instanceof XSWildcard) {
				boolean isSupportingExtensions = false;
				SchemaReader schemaReader = pController.getTypeSG().getFactory().getGenerator().getSchemaReader();
				if (schemaReader instanceof JAXBSchemaReader) {
					isSupportingExtensions = ((JAXBSchemaReader) schemaReader).isSupportingExtensions();
				}
				if (isSupportingExtensions) {
					XSWildcard wildcard = (XSWildcard) xsAttributes[i];
					attrChain = (AttributeSGChain) pController.newAttributeSG(wildcard);
				} else {
					throw new SAXParseException("Extensions must be enabled to support wildcard attributes (JAXB 1.0, App. E.2.1.1)",
							((XSWildcard) xsAttributes[i]).getLocator());
				}
			} else {
				throw new IllegalStateException("Unknown attribute type: " + xsAttributes[i].getClass().getName());
			}
			AttributeSG attrSG = new AttributeSGImpl(attrChain);
			attrSG.init();
			attributeList.add(attrSG);
		}
		return (AttributeSG[]) attributeList.toArray(new AttributeSG[attributeList.size()]);
	}
	
	public void addAttributeSG(ComplexTypeSG pController, AttributeSG pAttribute) throws SAXException {
		AttributeSG[] result = new AttributeSG[attributes.length+1];
		System.arraycopy(attributes, 0, result, 0, attributes.length);
		result[attributes.length] = pAttribute;
		attributes = result;
		pAttribute.init();
	}
	
	public void init(ComplexTypeSG pController) throws SAXException {
		attributes = initAttributes(pController);
		if (pController.hasSimpleContent()) {
			SimpleContentSGChain chain = (SimpleContentSGChain) pController.newSimpleContentTypeSG();
			simpleContentSG = new SimpleContentSGImpl(chain);
			simpleContentSG.init();
		} else {
			ComplexContentSGChain chain = (ComplexContentSGChain) pController.newComplexContentTypeSG();
			complexContentSG = new ComplexContentSGImpl(chain);
			complexContentSG.init();
		}
	}

	public boolean hasSimpleContent(ComplexTypeSG pController) { return hasSimpleContent; }
	public TypeSG getTypeSG(ComplexTypeSG pController) { return typeSG; }
	
	public boolean hasAttributes(ComplexTypeSG pController) {
		return attributes.length != 0;
	}
	
	public AttributeSG[] getAttributes(ComplexTypeSG pController) {
		return attributes;
	}
	
	public Context getClassContext(ComplexTypeSG pController) { return classContext; }
	public Locator getLocator(ComplexTypeSG pController) { return xsType.getLocator(); }
	
	public JavaSource getXMLInterface(ComplexTypeSG pController) throws SAXException {
		final String mName = "getXMLInterface";
		log.finest(mName, "->");
		JavaQName qName = pController.getClassContext().getXMLInterfaceName();
		JavaSourceFactory jsf = pController.getTypeSG().getSchema().getJavaSourceFactory();
		JavaSource js = jsf.newJavaSource(qName, JavaSource.PUBLIC);
		js.setType(JavaSource.INTERFACE);
		createXMLBean(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}

	private void createXMLBean(ComplexTypeSG pController, JavaSource pJs)
			throws SAXException {
		if (pJs.isInterface()) {
			if (pController.getTypeSG().isExtension()) {
				TypeSG extType = pController.getTypeSG().getExtendedType();
				if (extType.isComplex()) {
					pJs.addExtends(extType.getComplexTypeSG().getClassContext().getXMLInterfaceName());
				}
			}
		} else {
			SerializableSG.makeSerializable(pController.getTypeSG().getSchema(), pJs);
			if (pController.getTypeSG().isExtension()) {
				TypeSG extType = pController.getTypeSG().getExtendedType();
				if (extType.isComplex()) {
					pJs.addExtends(extType.getComplexTypeSG().getClassContext().getXMLImplementationName());
				}
			}
		}
		BeanGeneratingVisitor visitor = new BeanGeneratingVisitor(pJs);
		new ParticleWalker(visitor).walk(pController);
	}

	public JavaSource getXMLInterface(ComplexTypeSG pController, JavaSource pSource) throws SAXException {
		final String mName = "getXMLInterface(JavaSource)";
		log.finest(mName, "->", pSource.getQName());
		JavaQName qName = pController.getClassContext().getXMLInterfaceName();
		JavaSource js = pSource.newJavaInnerClass(qName.getClassName(), JavaSource.PUBLIC);
		js.setType(JavaSource.INTERFACE);
		createXMLBean(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}
	
	public JavaSource getXMLImplementation(ComplexTypeSG pController) throws SAXException {
		final String mName = "getXMLImplementation(JavaQName)";
		log.finest(mName, "->", typeSG.getName());
		JavaSourceFactory jsf = pController.getTypeSG().getSchema().getJavaSourceFactory();
		JavaSource js = jsf.newJavaSource(pController.getClassContext().getXMLImplementationName(), JavaSource.PUBLIC);
		js.addImplements(pController.getClassContext().getXMLInterfaceName());
		createXMLBean(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}
	
	public JavaSource getXMLImplementation(ComplexTypeSG pController, JavaSource pSource) throws SAXException {
		final String mName = "getXMLImplementation(JavaQName,JavaSource)";
		log.finest(mName, "->", pSource.getQName());
		JavaSource js = pSource.newJavaInnerClass(pController.getClassContext().getXMLImplementationName().getInnerClassName(), JavaSource.PUBLIC);
		js.setStatic(true);
		js.addImplements(pController.getClassContext().getXMLInterfaceName());
		createXMLBean(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}

	private void createXMLSerializer(ComplexTypeSG pController, JavaSource pSource)
			throws SAXException {
		pSource.addImplements(JMSAXDriver.class);
		DriverGeneratingVisitor visitor = new DriverGeneratingVisitor(pSource);
		new ParticleWalker(visitor).walk(pController);
	}

	public JavaSource getXMLSerializer(ComplexTypeSG pController) throws SAXException {
		final String mName = "getXMLSerializer";
		log.finest(mName, "->", typeSG.getName());
		JavaQName xmlSerializerName = pController.getClassContext().getXMLSerializerName();
		JavaSourceFactory jsf = typeSG.getSchema().getJavaSourceFactory();
		JavaSource js = jsf.newJavaSource(xmlSerializerName, JavaSource.PUBLIC);
		createXMLSerializer(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}
	
	public JavaSource getXMLSerializer(ComplexTypeSG pController, JavaSource pSource) throws SAXException {
		final String mName = "getXMLSerializer(JavaSource)";
		log.finest(mName, "->", pSource.getQName());
		JavaSource js = pSource.newJavaInnerClass(pController.getClassContext().getXMLSerializerName().getInnerClassName(), JavaSource.PUBLIC);
		js.setStatic(true);
		createXMLSerializer(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}

	private HandlerSG newHandlerSG(ComplexTypeSG pController,
								   JavaSource pJs)
			throws SAXException {
		if (pController.hasSimpleContent()) {
			return new SimpleContentHandlerSG(pController, pJs);
		} else {
			ComplexContentSG ccSG = pController.getComplexContentSG();
			if (ccSG.isEmpty()) {
				return new EmptyElementHandlerSG(pController, pJs);
			} else {
				GroupSG group = ccSG.getRootParticle().getGroupSG();
				if (group.isSequence()) {
					return new SequenceHandlerSG(pController, pJs);
				} else if (group.isChoice()) {
					return new ChoiceHandlerSG(pController, pJs);
				} else if (group.isAll()) {
					return new AllHandlerSG(pController, pJs);
				} else {
					throw new IllegalStateException("Invalid group type");
				}
			}
		}
	}

	private void createXMLHandler(ComplexTypeSG pController, JavaSource pJs) throws SAXException {
	    final Class parserClass;
        if (!pController.hasSimpleContent()  &&  pController.getComplexContentSG().isMixed()) {
            parserClass = JMSAXMixedElementParser.class;
        } else {
            parserClass = JMSAXElementParser.class;
        }
        pJs.addExtends(parserClass);
		HandlerSG handlerSG = newHandlerSG(pController, pJs);
		handlerSG.generate();
	}

	public JavaSource getXMLHandler(ComplexTypeSG pController, JavaQName pQName) throws SAXException {
		final String mName = "getXMLHandler";
		log.finest(mName, "->", typeSG.getName());
		JavaSourceFactory jsf = typeSG.getSchema().getJavaSourceFactory();
		JavaSource js = jsf.newJavaSource(pQName, JavaSource.PUBLIC);
		createXMLHandler(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}

    public JavaSource getXMLHandler(ComplexTypeSG pController, JavaSource pSource) throws SAXException {
		final String mName = "getXMLHandler(JavaSource)";
		log.finest(mName, "->", pSource.getQName());
		JavaSource js = pSource.newJavaInnerClass(pController.getClassContext().getXMLHandlerName().getInnerClassName(), JavaSource.PUBLIC);
		js.setStatic(true);
		createXMLHandler(pController, js);
		log.finest(mName, "<-", js.getQName());
		return js;
	}
	
	public SimpleContentSG getSimpleContentSG(ComplexTypeSG pController) {
		if (simpleContentSG == null) {
			throw new IllegalStateException("This complex type doesn't have simple content.");
		}
		return simpleContentSG;
	}
	
	public ComplexContentSG getComplexContentSG(ComplexTypeSG pController) {
		if (complexContentSG == null) {
			throw new IllegalStateException("This complex type doesn't have complex content.");
		}
		return complexContentSG;
	}

	public Object newComplexContentTypeSG(ComplexTypeSG pController) throws SAXException {
		return new JAXBComplexContentTypeSG(pController, xsType);
	}
	
	public Object newSimpleContentTypeSG(ComplexTypeSG pController) throws SAXException {
		return new JAXBSimpleContentTypeSG(pController, xsType);
	}
}
