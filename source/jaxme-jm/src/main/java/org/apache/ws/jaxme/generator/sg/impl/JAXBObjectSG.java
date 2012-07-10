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

import javax.xml.bind.Element;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.JMElement;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ObjectSGChain;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBPropertyOwner;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBObjectSG extends JAXBSGItem implements ObjectSGChain {
  private final static Logger log = LoggerAccess.getLogger(JAXBObjectSG.class);
  private final XSType type;
  private final TypeSG typeSG;
  private final XsQName name;
  private final Context classContext;
  private final boolean global;

  /** <p>Creates a new, local instance of JAXBObjectSG, generating
   * the given attribute within the given {@link Context}.</p>
   */
  public JAXBObjectSG(SGFactory pFactory, SchemaSG pSchema, XSAttribute pAttribute,
                       Context pClassContext) throws SAXException {
    this(pFactory, pSchema, (XSObject) pAttribute, pClassContext);
  }

  /** <p>Creates a new, global instance of JAXBObjectSG, generating
   * the given element.</p>
   */
  public JAXBObjectSG(SGFactory pFactory, SchemaSG pSchema, XSElement pElement) throws SAXException {
    this(pFactory, pSchema, (XSObject) pElement, null);
  }

  /** <p>Creates a new, local instance of JAXBObjectSG, generating
   * the given element within the given {@link Context}.</p>
   */
  public JAXBObjectSG(SGFactory pFactory, SchemaSG pSchema, XSElement pElement,
                       Context pClassContext) throws SAXException {
    this(pFactory, pSchema, (XSObject) pElement, pClassContext);
  }

  /** <p>Creates a new, local instance of JAXBObjectSG, which
   * is generated within the given {@link Context}.</p>
   */
  private JAXBObjectSG(SGFactory pFactory, SchemaSG pSchema, XSObject pObject,
  		               Context pClassContext) throws SAXException {
  	super(pFactory, pSchema, pObject);
    final String mName = "<init>(XSObject,Context)";
    boolean isClassGlobal;
    if (pObject instanceof XSElement) {
      XSElement element = (XSElement) pObject;
      log.finest(mName, "->", new Object[]{element.getName(), pClassContext});
      type = element.getType();
      name = element.getName();
      global = element.isGlobal();
      isClassGlobal = !type.isSimple()  &&  (type.isGlobal()  ||  global);
    } else {
      throw new IllegalStateException("Unknown object type: " + pObject.getClass().getName());
    }

    Context myClassContext;
    final boolean useTypesContext = pClassContext != null;
    if (useTypesContext) {
      myClassContext = pClassContext;
    } else {
      XSObject contextObject = pObject;
      if (pObject instanceof XSElement) {
    	XSElement element = (XSElement) pObject;
    	if (element.isReference()) {
    	  contextObject = pObject.getXSSchema().getElement(name);
    	}
      }
      myClassContext = new GlobalContext(name, contextObject, null, null, pSchema);
    }

    final JAXBProperty.BaseType baseType;
    if (pObject instanceof JAXBPropertyOwner) {
    	JAXBProperty property = ((JAXBPropertyOwner) pObject).getJAXBProperty();
    	baseType = property == null ? null : property.getBaseType();
    } else {
    	baseType = null;
    }

    if (isClassGlobal) {
      if (type.isGlobal()) {
        typeSG = pFactory.getTypeSG(type, baseType);
      } else {
        typeSG = pFactory.getTypeSG(type, myClassContext.getName(), baseType);
      }
    } else {
      typeSG = pFactory.getTypeSG(type, myClassContext, name, baseType);
    }

    if (useTypesContext) {
      if (typeSG.isComplex()) {
        classContext = typeSG.getComplexTypeSG().getClassContext();
      } else {
        classContext = pClassContext;
      }
    } else if (typeSG.isComplex()) {
      classContext = myClassContext;
      Context tctx = typeSG.getComplexTypeSG().getClassContext();
      AbstractContext ctx = (AbstractContext) classContext;
      ctx.setPMName(tctx.getPMName());
      ctx.setXMLSerializerName(tctx.getXMLSerializerName());
      ctx.setXMLValidatorName(tctx.getXMLValidatorName());
    } else {
      classContext = null;
    }
    log.finest(mName, "<-", new Object[]{typeSG, classContext});
  }

  /** <p>Creates a new instance of JAXBObjectSG generating the given
   * wildcard object.</p>
   */
  public JAXBObjectSG(SGFactory pFactory, SchemaSG pSchema, XSAny pAny) {
      super(pFactory, pSchema, pAny);
      type = null;
      name = null;
      classContext = null;
      typeSG = null;
      global = false;
  }

  public void init(ObjectSG pController) throws SAXException {
  }

  public TypeSG getTypeSG(ObjectSG pController) {
    if (typeSG == null) {
      throw new NullPointerException("ObjectSG not initialized");
    }
    return typeSG;
  }

  public Locator getLocator(ObjectSG pController) { return getLocator(); }
  public SGFactory getFactory(ObjectSG pController) { return getFactory(); }
  public SchemaSG getSchema(ObjectSG pController) { return getSchema(); }
  public Context getClassContext(ObjectSG pController) { return classContext; }
  public XsQName getName(ObjectSG pController) {
    if (name == null) {
      throw new IllegalStateException("The content object of a complex type with simple content doesn't have an XML Schema name.");
    }
    return name;
  }

  public JavaSource getXMLInterface(ObjectSG pController) throws SAXException {
    final String mName = "getXMLInterface";
    log.finest(mName, "->", pController.getName());
    if (!pController.getTypeSG().isComplex()) {
      log.finest(mName, "<-", "null");
      return null;
    }

    JavaQName xmlInterfaceName = pController.getClassContext().getXMLInterfaceName();
    JavaSourceFactory jsf = getSchema().getJavaSourceFactory();
    JavaSource js = jsf.newJavaSource(xmlInterfaceName, JavaSource.PUBLIC);
    js.setType(JavaSource.INTERFACE);
    js.addExtends(Element.class);

    TypeSG myTypeSG = pController.getTypeSG();
    ComplexTypeSG complexTypeSG = myTypeSG.getComplexTypeSG();
    if (myTypeSG.isGlobalClass()) {
      js.addExtends(complexTypeSG.getClassContext().getXMLInterfaceName());
    }
    log.finest(mName, "<-", xmlInterfaceName);
    return js;
  }

  public JavaSource getXMLImplementation(ObjectSG pController) throws SAXException {
    final String mName = "getXMLImplementation";
    log.finest(mName, "->", pController.getName());
    if (!pController.getTypeSG().isComplex()) {
      log.finest(mName, "<-", "null");
      return null;
    }

    JavaQName xmlImplementationName = pController.getClassContext().getXMLImplementationName();
    JavaSourceFactory jsf = getSchema().getJavaSourceFactory();
    JavaSource js = jsf.newJavaSource(xmlImplementationName, JavaSource.PUBLIC);
    SerializableSG.makeSerializable(pController.getSchema(), js);
    js.addImplements(pController.getClassContext().getXMLInterfaceName());
    js.addImplements(JMElement.class);

    TypeSG myTypeSG = pController.getTypeSG();
    ComplexTypeSG complexTypeSG = myTypeSG.getComplexTypeSG();
    if (myTypeSG.isGlobalClass()) {
      js.addExtends(complexTypeSG.getClassContext().getXMLImplementationName());
    }

    JavaField myName = js.newJavaField("__qName", QName.class, JavaSource.PRIVATE);
    myName.setStatic(true);
    myName.setFinal(true);
    XsQName qName = pController.getName();
    String prefix = qName.getPrefix();
    if (prefix == null) {
	    myName.addLine("new ", QName.class, "(", JavaSource.getQuoted(qName.getNamespaceURI()),
	                   ", ", JavaSource.getQuoted(qName.getLocalName()), ")");
    } else {
        myName.addLine("new ", QName.class, "(", JavaSource.getQuoted(qName.getNamespaceURI()),
                	   ", ", JavaSource.getQuoted(qName.getLocalName()), ", ",
					   JavaSource.getQuoted(prefix), ")");
    }

    JavaMethod getQName = js.newJavaMethod("getQName", QName.class, JavaSource.PUBLIC);
    getQName.addLine("return ", myName, ";");

    return js;
  }

  public JavaSource getXMLSerializer(ObjectSG pController) throws SAXException {
    final String mName = "getXMLSerializer";
    log.finest(mName, "->", pController.getName());
    TypeSG myTypeSG = pController.getTypeSG();
	JavaSource result = myTypeSG.getComplexTypeSG().getXMLSerializer();
	log.finest(mName, "<-", result.getQName());
	return result;
  }

    public JavaSource getXMLHandler(ObjectSG pController) throws SAXException {
        final String mName = "getXMLHandler";
        log.finest(mName, "->", pController.getName());
        TypeSG myTypeSG = pController.getTypeSG();
        if (!myTypeSG.isComplex()) {
            log.finest(mName, "<-", null);
            return null;
        } else if (myTypeSG.isGlobalClass()) {
			return null;
        } else {
            JavaQName xmlHandlerName = pController.getClassContext().getXMLHandlerName();
			return myTypeSG.getComplexTypeSG().getXMLHandler(xmlHandlerName);
        }
    }

  public void generate(ObjectSG pController) throws SAXException {
    final String mName = "generate";
    log.finest(mName, "->", pController.getName());
    pController.getXMLInterface();
    pController.getXMLImplementation();

    TypeSG myTypeSG = pController.getTypeSG();
    if (myTypeSG.isGlobalClass()  &&  !myTypeSG.isGlobalType()) {
      myTypeSG.generate();
    }
    log.finest(mName, "<-");
  }

  public boolean isGlobal(ObjectSG pController) throws SAXException {
   return global;
  }
}
