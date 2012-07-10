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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.XMLWriter;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.SchemaVisitorImpl;
import org.apache.ws.jaxme.generator.sg.impl.ccsg.SchemaWalker;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.impl.JMSAXDriverController;
import org.apache.ws.jaxme.impl.XMLWriterImpl;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.TextFile;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.util.DOMSerializer;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchema;
import org.apache.ws.jaxme.xs.types.XSNCName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSchemaSG implements SchemaSGChain {
  private final static Logger log = LoggerAccess.getLogger(JAXBSchemaSG.class);
  private final Map elementsByName = new HashMap();
  private final Map groupsByName = new HashMap();
  private final Map typesByName = new HashMap();
  private final SGFactory factory;
  private final XSSchema xsSchema;
  private final JavaSourceFactory javaSourceFactory = new JavaSourceFactory();
  private TypeSG[] typesByOrder;
  private ObjectSG[] elementsByOrder;
  private ObjectSG[] objectsByOrder;
  private GroupSG[] groupsByOrder;

  /** <p>Creates a new instance of JAXBSchemaSG.</p>
   */
  public JAXBSchemaSG(SGFactory pFactory, XSSchema pSchema) {
    factory = pFactory;
    xsSchema = pSchema;
    javaSourceFactory.setOverwriteForced(pFactory.getGenerator().isForcingOverwrite());
    javaSourceFactory.setSettingReadOnly(pFactory.getGenerator().isSettingReadOnly());
  }

  public SchemaSG getSchema(SchemaSG pController) { return pController; }
  public Locator getLocator(SchemaSG pController) { return xsSchema.getLocator(); }
  public SGFactory getFactory(SchemaSG pController) { return factory; }
  protected XSSchema getXSSchema() { return xsSchema; }

  public void init(SchemaSG pController) throws SAXException {
    final String mName = "init";
    log.finest(mName, "->");

    Object[] childs = getXSSchema().getChilds();

    List elements = new ArrayList();
    List types = new ArrayList();
    List groups = new ArrayList();
    List objects = new ArrayList();
    for (int i = 0;  i < childs.length;  i++) {
      Object o = childs[i];
      log.finest(mName, "Child" + o);
      if (o instanceof XSType) {
        TypeSG typeSG = pController.getType(((XSType) o).getName());
        if (typeSG == null) {
          throw new IllegalStateException("TypeSG not created");
        }
        types.add(typeSG);
      } else if (o instanceof XSGroup) {
        GroupSG groupSG = pController.getGroup(((XSGroup) o).getName());
        if (groupSG == null) {
          throw new IllegalStateException("GroupSG not created");
        }
        groups.add(groupSG);
      } else if (o instanceof XSElement) {
        ObjectSG objectSG = pController.getElement(((XSElement) o).getName());
        if (objectSG == null) {
          throw new IllegalStateException("ObjectSG not created");
        }
        objects.add(objectSG);
        elements.add(objectSG);
      }
    }

    elementsByOrder = (ObjectSG[]) elements.toArray(new ObjectSG[elements.size()]);
    typesByOrder = (TypeSG[]) types.toArray(new TypeSG[types.size()]);
    groupsByOrder = (GroupSG[]) groups.toArray(new GroupSG[groups.size()]);
    objectsByOrder = (ObjectSG[]) objects.toArray(new ObjectSG[objects.size()]);
    log.finest(mName, "<-");
  }

  public TypeSG getType(SchemaSG pController, XsQName pName) throws SAXException {
    TypeSG typeSG = (TypeSG) typesByName.get(pName);
    if (typeSG != null) {
      return typeSG;
    }
    XSType type = getXSSchema().getType(pName);
    if (type == null) {
      return null;
    }
    typeSG = pController.getFactory().getTypeSG(type, null);
    typesByName.put(pName, typeSG);
    return typeSG;
  }

  public GroupSG[] getGroups(SchemaSG pController) throws SAXException {
    return groupsByOrder;
  }

  public GroupSG getGroup(SchemaSG pController, XsQName pName) throws SAXException {
    GroupSG groupSG = (GroupSG) groupsByName.get(pName);
    if (groupSG != null) {
      return groupSG;
    }
    XSGroup group = getXSSchema().getGroup(pName);
    if (group == null) {
      return null;
    }
    groupSG = pController.getFactory().getGroupSG(group);
    groupsByName.put(pName, groupSG);
    return groupSG;
  }

  public TypeSG[] getTypes(SchemaSG pController) throws SAXException {
    return typesByOrder;
  }

  public ObjectSG[] getObjects(SchemaSG pController) throws SAXException {
    return objectsByOrder;
  }

  public ObjectSG getElement(SchemaSG pController, XsQName pName) throws SAXException {
    ObjectSG objectSG = (ObjectSG) elementsByName.get(pName);
    if (objectSG != null) {
      return objectSG;
    }
    XSElement element = getXSSchema().getElement(pName);
    if (element == null) {
      return null;
    }
    objectSG = pController.getFactory().getObjectSG(element);
    elementsByName.put(pName, objectSG);
    return objectSG;
  }

  public String getCollectionType(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        String result = globalBindings.getCollectionType();
        if (result != null) {
          return result;
        }
      }
    }
    return ArrayList.class.getName();
  }

  public JavaSourceFactory getJavaSourceFactory(SchemaSG pController) {
    return javaSourceFactory;
  }

  public void generate(SchemaSG pController) throws SAXException {
    TypeSG[] types = pController.getTypes();
    for (int i = 0;  i < types.length;  i++) {
      types[i].generate();
    }

    ObjectSG[] objects = pController.getObjects();
    for (int i = 0;  i < objects.length;  i++) {
      objects[i].generate();
    }

    pController.generateConfigFiles();
    pController.generateJaxbProperties();
  }

  public ObjectSG[] getElements(SchemaSG pController) throws SAXException {
    return elementsByOrder;
  }

  public boolean isBindingStyleModelGroup(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isBindingStyleModelGroup();
      }
    }
    return false;
  }

  public boolean isChoiceContentProperty(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isChoiceContentProperty();
      }
    }
    return false;
  }

  public boolean isFailFastCheckEnabled(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isEnableFailFastCheck();
      }
    }
    return false;
  }

  public boolean isJavaNamingConventionsEnabled(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isEnableJavaNamingConventions();
      }
    }
    return true;
  }

  public boolean isFixedAttributeConstantProperty(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isFixedAttributeAsConstantProperty();
      }
    }
    return false;
  }

  public boolean isGeneratingIsSetMethod(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.isGenerateIsSetMethod();
      }
    }
    return false;
  }

  public boolean isUnderscoreWordSeparator(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return JAXBGlobalBindings.UnderscoreBinding.AS_WORD_SEPARATOR.equals(globalBindings.getUnderscoreBinding());
      }
    }
    return true;
  }

  public JAXBJavaType[] getJAXBJavaTypes(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.getJavaType();
      }
    }
    return new JAXBJavaType[0];
  }

  public XsQName[] getTypesafeEnumBase(SchemaSG pController) {
    XSSchema schema = getXSSchema();
    if (schema instanceof JAXBSchema) {
      JAXBSchema jaxbSchema = (JAXBSchema) schema;
      JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
      if (globalBindings != null) {
        return globalBindings.getTypesafeEnumBase();
      }
    }
    return new XsQName[]{XSNCName.getInstance().getName()};
  }

  /** Used for generating the method
   * {@link JMSAXDriverController#getXsIdAttribute(Object)}.
   */
  private class AttributeIdGenerator extends SchemaVisitorImpl {
	  private final List types = new ArrayList();
	  private final Map packages = new HashMap();
	  private final JavaSourceFactory factory;

	  AttributeIdGenerator(JavaSourceFactory pFactory) {
		  factory = pFactory;
	  }

	  private AttributeSG getIdAttribute(TypeSG pType) {
		  if (pType.isComplex()) {
			  /* Check, if the super class has an ID attribute.
			   * If so, do nothing and leave the ID handling to
			   * super class.
			   */
			  if (!pType.isExtension()  ||
			      getIdAttribute(pType.getExtendedType()) == null) {
				  AttributeSG[] attributes = pType.getComplexTypeSG().getAttributes();
				  for (int i = 0;  i < attributes.length;  i++) {
					  AttributeSG attr = attributes[i];
					  TypeSG type = attr.getTypeSG();
					  if (type != null  &&  type.getSimpleTypeSG().isXsId()) {
						  return attr;
					  }
				  }
			  }
		  }
		  return null;
	  }

	  public void visit(TypeSG pType) {
			AttributeSG attr = getIdAttribute(pType);
			if (attr != null) {
				types.add(pType);
			}
	  }

	  protected JavaQName getJMSAXDriverControllerClass(String pPackage) throws SAXException {
		  if (types.size() == 0) {
			  return JavaQNameImpl.getInstance(JMSAXDriverController.class);
		  }
		  JavaQName qName = (JavaQName) packages.get(pPackage);
		  if (qName == null) {
			  qName = newDriverControllerClass(pPackage);
			  packages.put(pPackage, qName);
		  }
		  return qName;
	  }

	  private JavaQName newDriverControllerClass(String pPackage) throws SAXException {
		  JavaQName qName;
		  qName = JavaQNameImpl.getInstance(pPackage + ".runtime", "JMSAXDriverController");
		  JavaSource js = factory.newJavaSource(qName, JavaSource.PUBLIC);
		  js.addExtends(JMSAXDriverController.class);
		  JavaMethod jm = js.newJavaMethod("getXsIdAttribute", String.class, JavaSource.PUBLIC);
		  Parameter param = jm.addParam(Object.class, "pObject");
		  for (int i = 0;  i < types.size();  i++) {
			  TypeSG tSG = (TypeSG) types.get(i);
			  jm.addIf(i == 0, param, " instanceof ", tSG.getRuntimeType());
			  LocalJavaField f = jm.newJavaField(tSG.getRuntimeType());
			  f.addLine("(", tSG.getRuntimeType(), ") ", param);
			  AttributeSG attr = getIdAttribute(tSG);
			  jm.addLine("return ", attr.getPropertySG().getValue(f), ";");
		  }
		  jm.addElse();
		  jm.addLine("return null;");
		  jm.addEndIf();
		  return qName;
	  }
  }

  public void generateJaxbProperties(SchemaSG pController) throws SAXException {
    List contextList = new ArrayList();
    ObjectSG[] elements = pController.getElements();
    for (int i = 0;  i < elements.length;  i++) {
    	if (elements[i].getTypeSG().isComplex()) {
            contextList.add(elements[i]);
    	}
    }
    TypeSG[] types = pController.getTypes();
    for (int i = 0;  i < types.length;  i++) {
      if (types[i].isComplex()) {
        contextList.add(types[i]);
      }
    }


    Set packages = new HashSet();
    for (Iterator iter = contextList.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      Context ctx;
      if (o instanceof ObjectSG) {
        ctx = ((ObjectSG) o).getClassContext();
      } else {
        ctx = ((TypeSG) o).getComplexTypeSG().getClassContext();
      }
      String packageName = ctx.getXMLInterfaceName().getPackageName();
      if (packages.contains(packageName)) {
        continue;
      }

      TextFile textFile = pController.getJavaSourceFactory().newTextFile(packageName, "jaxb.properties");
      textFile.addLine(JAXBContext.JAXB_CONTEXT_FACTORY + "=" + JAXBContextImpl.class.getName());
      packages.add(packageName);

      getObjectFactory(pController, packageName, contextList);
    }
  }

  protected JavaSource getObjectFactory(SchemaSG pController, String pPackageName,
		  								List pContextList)
  		throws SAXException {
    JavaQName qName = JavaQNameImpl.getInstance(pPackageName, "ObjectFactory");
    JavaSource js = pController.getJavaSourceFactory().newJavaSource(qName, "public");
    JavaField jf = js.newJavaField("jaxbContext", JAXBContextImpl.class, "private");
    JavaField properties = js.newJavaField("properties", Map.class, "private");

    JavaMethod newInstanceMethod = js.newJavaMethod("newInstance", Object.class, "public");
    newInstanceMethod.addThrows(JAXBException.class);
    Parameter pElementInterface = newInstanceMethod.addParam(Class.class, "pElementInterface");
    newInstanceMethod.addLine("return ", jf, ".getManager(", pElementInterface, ").getElementJ();");

    {
      JavaMethod getPropertyMethod = js.newJavaMethod("getProperty", Object.class, "public");
      Parameter pName = getPropertyMethod.addParam(String.class, "pName");
      getPropertyMethod.addIf(properties, " == null");
      getPropertyMethod.addLine("return null;");
      getPropertyMethod.addEndIf();
      getPropertyMethod.addLine("return ", properties, ".get(", pName, ");");
    }

    {
      JavaMethod setPropertyMethod = js.newJavaMethod("setProperty", void.class, "public");
      Parameter pName = setPropertyMethod.addParam(String.class, "pName");
      Parameter pValue = setPropertyMethod.addParam(Object.class, "pValue");
      setPropertyMethod.addIf(properties, " == null");
      setPropertyMethod.addLine(properties, " = new ", HashMap.class, "();");
      setPropertyMethod.addEndIf();
      setPropertyMethod.addLine(properties, ".put(", pName, ", ", pValue, ");");
    }

    Set contextSet = new HashSet();
    final Set packageNames = new HashSet();
    packageNames.add(pPackageName);
    for (Iterator iter = pContextList.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      TypeSG typeSG;
      if (o instanceof ObjectSG) {
        ObjectSG objectSG = ((ObjectSG) o);
        typeSG = objectSG.getTypeSG();
        generateCreateMethod(js, null, objectSG.getClassContext());
        packageNames.add(objectSG.getClassContext().getXMLInterfaceName().getPackageName());
        //NB: we don't have to check for duplicate element names since that would violate the XSD spec
      } else if (o instanceof TypeSG) {
        typeSG = (TypeSG) o;
      } else {
        continue;
      }

	  generateCreateMethod(js, contextSet, typeSG, null);
    }

    StringBuffer sb = new StringBuffer();
    for (Iterator iter = packageNames.iterator();  iter.hasNext();  ) {
        if (sb.length() > 0) {
            sb.append(':');
        }
        sb.append(iter.next());
    }
    JavaConstructor jcon = js.newJavaConstructor("public");
    jcon.addThrows(JAXBException.class);
    jcon.addLine(jf, " = (", JAXBContextImpl.class, ") ",
                 JAXBContext.class, ".newInstance(",
                 JavaSource.getQuoted(pPackageName), ");");
    return js;
  }

  private void generateCreateMethod(JavaSource pJs, Set pContextSet,
		  							TypeSG pType, String pPrefix) throws SAXException {
	  if (!pType.isComplex()  ||  pContextSet.contains(pType)) {
		  return;
	  }
	  // many global elements may have the same global complex type so must check first
	  String prefix = generateCreateMethod(pJs, pPrefix, pType.getComplexTypeSG().getClassContext());
	  pContextSet.add(pType);
	  generateCreateMethods(pJs, pType, prefix, pContextSet);
  }

  /** Generate create methods for the given particle.
   */
  private void generateCreateMethods(JavaSource pJs, ParticleSG pParticle,
		  							String pName, Set pContextSet)
  		throws SAXException {
	  if (pParticle.isGroup()) {
		  GroupSG group = pParticle.getGroupSG();
		  generateCreateMethods(pJs, group.getParticles(), pName, pContextSet);
	  } else if (pParticle.isElement()) {
		  ObjectSG oSG = pParticle.getObjectSG();
		  if (oSG.isGlobal()) {
			  return;  // Will be generated elsewhere
		  }
		  TypeSG tSG = oSG.getTypeSG();
		  if (tSG.isGlobalType()) {
			  return;  // Will be generated elsewhere
		  }
		  generateCreateMethod(pJs, pContextSet, tSG, pName);
	  } else if (pParticle.isWildcard()) {
		  throw new IllegalStateException("TODO: Add support for wildcards here.");
	  } else {
		  throw new IllegalStateException("Invalid class type");
	  }
  }

  /** Generate create methods for the given particles.
   */
  private void generateCreateMethods(JavaSource pJs, ParticleSG[] pParticles,
		  							 String pName, Set pContextSet)
  		throws SAXException {
	  for (int i = 0;  i < pParticles.length;  i++) {
		  generateCreateMethods(pJs, pParticles[i], pName, pContextSet);
	  }
  }

  /** Generate create methods for the content.
   */
  private void generateCreateMethods(JavaSource pJs, TypeSG pType,
		  							 String pName, Set pContextSet)
  		throws SAXException {
	  ComplexTypeSG ctSG = pType.getComplexTypeSG();
	  if (ctSG.hasSimpleContent()) {
		  return; // No elements contained
	  }
	  ComplexContentSG ccSG = ctSG.getComplexContentSG();
	  if (ccSG.isEmpty()) {
		  return;
	  }
	  ParticleSG particle = ccSG.getRootParticle();
	  generateCreateMethods(pJs, particle, pName, pContextSet);
  }

  /**
   * Generic util method for generating the create<NAME> methods for the object factory.
   * @param pSource the java source object to add the method
   * @param pContext the Class Context from either an ObjectSG or a TypeSG
   */
  private String generateCreateMethod(JavaSource pSource, String pPrefix,
		  							  Context pContext) {
    JavaQName resultName = pContext.getXMLInterfaceName();
    String className = resultName.isInnerClass() ? resultName.getInnerClassName() : resultName.getClassName();
	String result = Character.toUpperCase(className.charAt(0)) + className.substring(1);
	boolean anonymous = pPrefix != null;
	if (anonymous) {
		result = pPrefix + result;
	}
    String methodName = "create" + result;
    JavaMethod createMethod = pSource.newJavaMethod(methodName, resultName, "public");
    createMethod.addThrows(JAXBException.class);
	if (anonymous) {
		createMethod.addLine("return new ", pContext.getXMLImplementationName(), "();");
	} else {
		createMethod.addLine("return (", resultName, ") newInstance(", resultName, ".class);");
	}
	return result;
  }

  private class ConfigFileGenerator extends SchemaVisitorImpl {
	private final AttributeIdGenerator attrIdGen;
	private final Map packages = new HashMap();
	private final SchemaSG controller;
	ConfigFileGenerator(SchemaSG pController) throws SAXException {
		attrIdGen = new AttributeIdGenerator(pController.getJavaSourceFactory());
		new SchemaWalker(attrIdGen).walk(pController);
		controller = pController;
	}
	public void visit(ObjectSG pElement) throws SAXException {
		if (pElement.isGlobal()  &&  pElement.getTypeSG().isComplex()) {
			Context ctx = pElement.getClassContext();
			Document doc = getDocument(ctx);
			Context typeCtx = pElement.getTypeSG().getComplexTypeSG().getClassContext();
			Element manager = generate(doc, ctx, typeCtx);
			XsQName name = pElement.getName();
			manager.setAttributeNS(null, "qName", name.toString());
			if (name.getPrefix() != null) {
				manager.setAttributeNS(null, "prefix", name.getPrefix());
			}
			controller.createConfigFile(pElement, manager);
		}
	}

	public void visit(TypeSG pType) throws SAXException {
		if (pType.isGlobalType()  &&  pType.isComplex()) {
			Context ctx = pType.getComplexTypeSG().getClassContext();
			Document doc = getDocument(ctx);
			Element manager = generate(doc, ctx, ctx);
			controller.createConfigFile(pType, manager);
		}
	}

	private Element generate(Document pDoc, Context pContext, Context pTypeContext) throws SAXException {
		JavaQName implQName = pContext.getXMLImplementationName();
		JavaQName jmSaxDriverControllerClass = attrIdGen.getJMSAXDriverControllerClass(implQName.getPackageName());
		Element manager = pDoc.createElementNS(JAXBContextImpl.CONFIGURATION_URI, "Manager");
		pDoc.getDocumentElement().appendChild(manager);
		manager.setAttributeNS(null, "elementInterface", pContext.getXMLInterfaceName().toString());
		manager.setAttributeNS(null, "elementClass", implQName.toString());
		manager.setAttributeNS(null, "handlerClass", pTypeContext.getXMLHandlerName().toString());
		manager.setAttributeNS(null, "driverClass", pTypeContext.getXMLSerializerName().toString());
		manager.setAttributeNS(null, "driverControllerClass", jmSaxDriverControllerClass.toString());
		manager.setAttributeNS(null, "validatorClass", pContext.getXMLValidatorName().toString());
		return manager;
	}

	private Document getDocument(Context ctx) throws SAXException {
		String packageName = ctx.getXMLInterfaceName().getPackageName();
		if (packageName == null) {
			packageName = "";
		}
		Document doc = (Document) packages.get(packageName);
		if (doc == null) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setValidating(false);
				doc = dbf.newDocumentBuilder().newDocument();
			} catch (ParserConfigurationException e) {
				throw new SAXException(e);
			}
			packages.put(packageName, doc);
		    final Element root = doc.createElementNS(JAXBContextImpl.CONFIGURATION_URI, "Configuration");
		    root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", JAXBContextImpl.CONFIGURATION_URI);
		    doc.appendChild(root);
		}
		return doc;
	}
	Map getPackages() { return packages; }
  }

  public void generateConfigFiles(SchemaSG pController) throws SAXException {
	  Map map = pController.getConfigFiles();
	  for (Iterator iter = map.entrySet().iterator();  iter.hasNext();  ) {
		  Map.Entry entry = (Map.Entry) iter.next();
		  String packageName = (String) entry.getKey();
		  Document doc = (Document) entry.getValue();
		  pController.generateConfigFile(packageName, doc);
	  }
  }
  
  public Map getConfigFiles(SchemaSG pController) throws SAXException {
	  ConfigFileGenerator gen = new ConfigFileGenerator(pController);
	  new SchemaWalker(gen).walk(pController);
	  return gen.getPackages();
  }
  
  public void generateConfigFile(SchemaSG pController, String pPackageName, Document pDoc) throws SAXException {
	  StringWriter sw = new StringWriter();
	  XMLWriter xw = new XMLWriterImpl();
	  try {
	      xw.setWriter(sw);
	  } catch (JAXBException e) {
		  throw new SAXException(e);
	  }
	  DOMSerializer ds = new DOMSerializer();
	  ds.serialize(pDoc, xw);
      TextFile confFile = pController.getJavaSourceFactory().newTextFile(pPackageName, "Configuration.xml");
      confFile.setContents(sw.toString());
  }

  public void createConfigFile(SchemaSG pController, ObjectSG pElement, Element pManager) {
	// Does nothing, for overwriting by subclasses only
  }

  public void createConfigFile(SchemaSG pController, TypeSG pType, Element pManager) {
	// Does nothing, for overwriting by subclasses only
  }
}
