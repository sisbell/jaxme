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

import java.util.HashMap;
import java.util.Map;

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSGChain;
import org.apache.ws.jaxme.generator.types.Base64BinarySG;
import org.apache.ws.jaxme.generator.types.BooleanSG;
import org.apache.ws.jaxme.generator.types.ByteSG;
import org.apache.ws.jaxme.generator.types.DateSG;
import org.apache.ws.jaxme.generator.types.DateTimeSG;
import org.apache.ws.jaxme.generator.types.DecimalSG;
import org.apache.ws.jaxme.generator.types.DoubleSG;
import org.apache.ws.jaxme.generator.types.DurationSG;
import org.apache.ws.jaxme.generator.types.FloatSG;
import org.apache.ws.jaxme.generator.types.HexBinarySG;
import org.apache.ws.jaxme.generator.types.IDREFSG;
import org.apache.ws.jaxme.generator.types.IDSG;
import org.apache.ws.jaxme.generator.types.IntSG;
import org.apache.ws.jaxme.generator.types.IntegerSG;
import org.apache.ws.jaxme.generator.types.ListTypeSGImpl;
import org.apache.ws.jaxme.generator.types.LongSG;
import org.apache.ws.jaxme.generator.types.QNameSG;
import org.apache.ws.jaxme.generator.types.ShortSG;
import org.apache.ws.jaxme.generator.types.StringSG;
import org.apache.ws.jaxme.generator.types.TimeSG;
import org.apache.ws.jaxme.generator.types.UnionTypeSGImpl;
import org.apache.ws.jaxme.generator.types.UnsignedIntSG;
import org.apache.ws.jaxme.generator.types.UnsignedShortSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBEnumeration;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBSimpleType;
import org.apache.ws.jaxme.xs.jaxb.JAXBType;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumClass;
import org.apache.ws.jaxme.xs.types.XSAnySimpleType;
import org.apache.ws.jaxme.xs.types.XSAnyURI;
import org.apache.ws.jaxme.xs.types.XSBase64Binary;
import org.apache.ws.jaxme.xs.types.XSBoolean;
import org.apache.ws.jaxme.xs.types.XSByte;
import org.apache.ws.jaxme.xs.types.XSDate;
import org.apache.ws.jaxme.xs.types.XSDateTime;
import org.apache.ws.jaxme.xs.types.XSDecimal;
import org.apache.ws.jaxme.xs.types.XSDouble;
import org.apache.ws.jaxme.xs.types.XSDuration;
import org.apache.ws.jaxme.xs.types.XSEntity;
import org.apache.ws.jaxme.xs.types.XSFloat;
import org.apache.ws.jaxme.xs.types.XSGDay;
import org.apache.ws.jaxme.xs.types.XSGMonth;
import org.apache.ws.jaxme.xs.types.XSGMonthDay;
import org.apache.ws.jaxme.xs.types.XSGYear;
import org.apache.ws.jaxme.xs.types.XSGYearMonth;
import org.apache.ws.jaxme.xs.types.XSHexBinary;
import org.apache.ws.jaxme.xs.types.XSID;
import org.apache.ws.jaxme.xs.types.XSIDREF;
import org.apache.ws.jaxme.xs.types.XSInt;
import org.apache.ws.jaxme.xs.types.XSInteger;
import org.apache.ws.jaxme.xs.types.XSLanguage;
import org.apache.ws.jaxme.xs.types.XSLong;
import org.apache.ws.jaxme.xs.types.XSNCName;
import org.apache.ws.jaxme.xs.types.XSNMToken;
import org.apache.ws.jaxme.xs.types.XSName;
import org.apache.ws.jaxme.xs.types.XSNegativeInteger;
import org.apache.ws.jaxme.xs.types.XSNonNegativeInteger;
import org.apache.ws.jaxme.xs.types.XSNonPositiveInteger;
import org.apache.ws.jaxme.xs.types.XSNormalizedString;
import org.apache.ws.jaxme.xs.types.XSNotation;
import org.apache.ws.jaxme.xs.types.XSPositiveInteger;
import org.apache.ws.jaxme.xs.types.XSQName;
import org.apache.ws.jaxme.xs.types.XSShort;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.types.XSTime;
import org.apache.ws.jaxme.xs.types.XSToken;
import org.apache.ws.jaxme.xs.types.XSUnsignedByte;
import org.apache.ws.jaxme.xs.types.XSUnsignedInt;
import org.apache.ws.jaxme.xs.types.XSUnsignedLong;
import org.apache.ws.jaxme.xs.types.XSUnsignedShort;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class JAXBTypeSG extends JAXBSGItem implements TypeSGChain {
  private final static Logger log = LoggerAccess.getLogger(JAXBTypeSG.class);
  private SimpleTypeSG simpleTypeSG;
  private ComplexTypeSG complexTypeSG;
  private final TypeSG restrictedType, extendedType;
  private final Context classContext;
  private final XSType xsType;
  private boolean isGlobalClass;
  private final XsQName name;
  private Map properties;
  private final XsSchemaHeader schemaHeader;
  private final JAXBProperty.BaseType baseType;

  /** <p>Creates a new, global instance of JAXBTypeSG.</p>
   */
  protected JAXBTypeSG(SGFactory pFactory, SchemaSG pSchema, XSType pType, JAXBProperty.BaseType pBaseType)
      throws SAXException {
    this(pFactory, pSchema, pType, null, pType.getName(), pBaseType);
    isGlobalClass = true;
  }

  protected JAXBTypeSG(SGFactory pFactory, SchemaSG pSchema, XSType pType, XsQName pName,
		  JAXBProperty.BaseType pBaseType) throws SAXException {
    this(pFactory, pSchema, pType, null, pName, pBaseType);
    isGlobalClass = true;
  }

  private TypeSG getReferencedType(SGFactory pFactory, Context pClassContext,
  		                           XSType pReferencingType, XSType pReferencedType)
          throws SAXException {
      XsQName name = pReferencedType.getName();
      TypeSG result;
      if (pReferencedType.isGlobal()) {
      	  result = pFactory.getTypeSG(pReferencedType, null);
      	  if (result == null) {
      	  	  throw new SAXParseException("Unknown global type " + name,
      	  	  		                      pReferencingType.getLocator());
      	  }
      } else {
      	  throw new IllegalStateException("A referenced type must be global.");
      }
      return result;
  }

  /** <p>Creates a new, local instance of JAXBTypeSG. Classes are generated
   * into the given context.</p>
   */
  protected JAXBTypeSG(SGFactory pFactory, SchemaSG pSchema, XSType pType, Context pClassContext, XsQName pName,
		  JAXBProperty.BaseType pBaseType)
      throws SAXException {
    super(pFactory, pSchema, pType);
    baseType = pBaseType;
    name = pName;
    xsType = pType;
    if (pType.isSimple()) {
      if (pType.getSimpleType().isRestriction()) {
        XSType restrictedXsType = pType.getSimpleType().getRestrictedType();
        restrictedType = getReferencedType(pFactory, pClassContext, pType, restrictedXsType);
        extendedType = null;
      } else {
        restrictedType = extendedType = null;
      }
    } else {
      if (pType.getComplexType().isRestriction()) {
        restrictedType = getReferencedType(pFactory, pClassContext, pType, pType.getComplexType().getRestrictedType());
        extendedType = null;
      } else if (pType.getComplexType().isExtension()) {
        extendedType = getReferencedType(pFactory, pClassContext, pType, pType.getComplexType().getExtendedType());
        restrictedType = null;
      } else {
        restrictedType = extendedType = null;
      }
    }
    classContext = pClassContext;
    schemaHeader = pType.getSchemaHeader();
  }

  public Object newComplexTypeSG(TypeSG pController) throws SAXException {
    return classContext == null ? new JAXBComplexTypeSG(pController, xsType) :
                                   new JAXBComplexTypeSG(pController, xsType, classContext);
  }


  protected XSType getXSType() { return (XSType) getXSObject(); }
  public XsQName getName(TypeSG pController) { return name; }
  public boolean isGlobalType(TypeSG pController) { return getXSType().isGlobal(); }
  public boolean isGlobalClass(TypeSG pController) { return isGlobalClass; }
  public boolean isComplex(TypeSG pController) { return !getXSType().isSimple(); }
  public Locator getLocator(TypeSG pController) { return getLocator(); }
  public SGFactory getFactory(TypeSG pController) { return getFactory(); }
  public SchemaSG getSchema(TypeSG pController) { return getSchema(); }
  public boolean isExtension(TypeSG pController) { return extendedType != null; }
  public TypeSG getExtendedType(TypeSG pController) {
    if (extendedType == null) {
      throw new IllegalStateException("This type is no extension.");
    }
    return extendedType;
  }
  public boolean isRestriction(TypeSG pController) { return restrictedType != null; }
  public TypeSG getRestrictedType(TypeSG pController) {
    if (restrictedType == null) {
      throw new IllegalStateException("This type is no restriction.");
    }
    return restrictedType;
  }

  public void init(TypeSG pController) throws SAXException {
    final String mName = "init";
    log.finest(mName, "->");
    if (pController.isComplex()) {
      ComplexTypeSGChain chain = (ComplexTypeSGChain) pController.newComplexTypeSG();
      complexTypeSG = new ComplexTypeSGImpl(chain);
      complexTypeSG.init();
    } else {
      SimpleTypeSGChain chain = (SimpleTypeSGChain) pController.newSimpleTypeSG();
      simpleTypeSG = new SimpleTypeSGImpl(chain);
      simpleTypeSG.init();
    }
    log.finest(mName, "<-");
  }

  public ComplexTypeSG getComplexTypeSG(TypeSG pController) { return complexTypeSG; }
  public SimpleTypeSG getSimpleTypeSG(TypeSG pController) { return simpleTypeSG; }

  public void generate(TypeSG pController) throws SAXException {
    final String mName = "generate";
    log.finest(mName, "->", pController.getName());
    if (pController.isComplex()) {
      ComplexTypeSG myComplexTypeSG = pController.getComplexTypeSG();
      myComplexTypeSG.getXMLInterface();
      myComplexTypeSG.getXMLImplementation();
      myComplexTypeSG.getXMLSerializer();
      myComplexTypeSG.getXMLHandler(myComplexTypeSG.getClassContext().getXMLHandlerName());
    } else {
      pController.getSimpleTypeSG().generate();
    }
    log.finest(mName, "<-");      
  }

  public void generate(TypeSG pController, JavaSource pSource) throws SAXException {
    final String mName = "generate(JavaSource)";
    log.finest(mName, "->", new Object[]{pController.getName(), pSource.getQName()});
    if (pController.isComplex()) {
    } else {
      pController.getSimpleTypeSG().generate(pSource);
    }

    log.finest(mName, "<-");
  }

  public JavaField getXMLField(TypeSG pController, JavaSource pSource, 
                                 String pFieldName, String pDefaultValue) throws SAXException {
    if (pSource.isInterface()) {
      return null;
    } else {
      JavaQName runtimeType;
      if (pController.isComplex() && pFieldName.equals("value") && pController.getComplexTypeSG().hasSimpleContent()) {
        SimpleContentSG obj = pController.getComplexTypeSG().getSimpleContentSG();
        runtimeType = obj.getContentTypeSG().getRuntimeType();
        if (pController.isRestriction()) {
          if (pController.getRestrictedType().isExtension()) {
            runtimeType = pController.getRestrictedType().getExtendedType().getRuntimeType();
          }
        }
      }
      else {
        runtimeType = pController.getRuntimeType();
      } 
      JavaField jf = pSource.newJavaField(pFieldName, runtimeType, JavaSource.PRIVATE);
      if (!pController.isComplex()) {
        Object o = pController.getSimpleTypeSG().getInitialValue(pSource);
        if (o == null && pDefaultValue != null) {
            o = pController.getSimpleTypeSG().getCastFromString(pDefaultValue);
        }
        if (o != null) {
            jf.addLine(o);
        }
      }
      return jf;
    }
  }

  public JavaMethod getXMLGetMethod(TypeSG pController, JavaSource pSource,
		  							String pFieldName, String pMethodName) throws SAXException {
    JavaQName runtimeType;
    if (pController.isComplex() && pFieldName.equals("value") && pController.getComplexTypeSG().hasSimpleContent()) {
      SimpleContentSG obj = pController.getComplexTypeSG().getSimpleContentSG();
      runtimeType = obj.getContentTypeSG().getRuntimeType();
      if (pController.isRestriction()) {
        if (pController.getRestrictedType().isExtension()) {
          runtimeType = pController.getRestrictedType().getExtendedType().getRuntimeType();
        }
      }
    }
    else {
      runtimeType = pController.getRuntimeType();
    } 
    JavaMethod jm = pSource.newJavaMethod(pMethodName, runtimeType, JavaSource.PUBLIC);
    if (!pSource.isInterface()) {
      jm.addLine("return " + pFieldName + ";");
    }
    return jm;
  }

  public JavaMethod getXMLSetMethod(TypeSG pController, JavaSource pSource,
  		                            String pFieldName, String pParamName,
  		                            String pMethodName,
                                    boolean pSetIsSet) throws SAXException {
  	if (pController.isComplex()) {
  		JavaMethod jm = pSource.newJavaMethod(pMethodName, JavaQNameImpl.VOID, JavaSource.PUBLIC);
  		JavaQName runtimeType;
  		if (pFieldName.equals("value") && pController.getComplexTypeSG().hasSimpleContent()) {
  			SimpleContentSG obj = pController.getComplexTypeSG().getSimpleContentSG();
  			runtimeType = obj.getContentTypeSG().getRuntimeType();
  			if (pController.isRestriction()) {
  				if (pController.getRestrictedType().isExtension()) {
  					runtimeType = pController.getRestrictedType().getExtendedType().getRuntimeType();
  				}
  			}
  		}
  		else {
  			runtimeType = pController.getRuntimeType();
  		}
  		DirectAccessible param = jm.addParam(runtimeType, pParamName);
  		if (!pSource.isInterface()) {
  			jm.addLine(pFieldName, " = ", param, ";");
  		}
  		return jm;
  	} else {
  		JavaMethod jm = pController.getSimpleTypeSG().getXMLSetMethod(pSource, pFieldName, pParamName, pMethodName);
  		if (pSetIsSet  &&  pController.getRuntimeType().isPrimitive()) {
  			jm.addLine(getIsSetCheckFieldName(pFieldName), " = true;");
        }
        return jm;
  	}
  }

  private String getIsSetCheckFieldName(String pFieldName) {
  	return "has" + Character.toUpperCase(pFieldName.charAt(0)) + pFieldName.substring(1);
  }

  /** 
   * The implementation of this method is temporarily experimental.
   * isSet and unset methods will be implemented by a flag and corresponding default value if it exist. 
   */
  public JavaMethod getXMLIsSetMethod(TypeSG pController, JavaSource pSource,
  		                              String pFieldName, String pMethodName)
      throws SAXException {
    JavaMethod jm = pSource.newJavaMethod(pMethodName, BooleanSG.BOOLEAN_TYPE, JavaSource.PUBLIC);
    if (!pSource.isInterface()) {
      if (pController.getRuntimeType().isPrimitive()) {
      	JavaField jf = pSource.newJavaField(getIsSetCheckFieldName(pFieldName), BooleanSG.BOOLEAN_TYPE);
        jm.addLine("return ", jf, ";");
      } else {
        jm.addLine("return (" + pFieldName + " != null);");
      }
    }
    return jm;
  }

  public JavaQName getRuntimeType(TypeSG pController) throws SAXException {
    if (pController.isComplex()) {
      return pController.getComplexTypeSG().getClassContext().getXMLInterfaceName();
    } else {
      return pController.getSimpleTypeSG().getRuntimeType();
    }
  }

  private SimpleTypeSGChain newSimpleTypeSGByJavaType(JAXBJavaType pJavaType,
  		                                              SGFactory pFactory,
                                                      SchemaSG pSchema,
                                                      XSType pType)
      throws SAXException {
    JavaQName qName = JavaQNameImpl.getInstance(pJavaType.getName());
    if (BooleanSG.BOOLEAN_TYPE.equals(qName)) {
    	return new BooleanSG(pFactory, pSchema, pType);
    } else if (BooleanSG.BOOLEAN_OBJECT_TYPE.equals(qName)) {
    	BooleanSG result = new BooleanSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (ByteSG.BYTE_TYPE.equals(qName)) {
    	return new ByteSG(pFactory, pSchema, pType);
    } else if (ByteSG.BYTE_OBJECT_TYPE.equals(qName)) {
    	ByteSG result = new ByteSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (ShortSG.SHORT_TYPE.equals(qName)) {
    	return new ShortSG(pFactory, pSchema, pType);
    } else if (ShortSG.SHORT_OBJECT_TYPE.equals(qName)) {
        ShortSG result = new ShortSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (IntSG.INT_TYPE.equals(qName)) {
    	return new IntSG(pFactory, pSchema, pType);
    } else if (IntSG.INT_OBJECT_TYPE.equals(qName)) {
    	IntSG result = new IntSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (LongSG.LONG_TYPE.equals(qName)) {
    	return new LongSG(pFactory, pSchema, pType);
    } else if (LongSG.LONG_OBJECT_TYPE.equals(qName)) {
    	LongSG result = new LongSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (FloatSG.FLOAT_TYPE.equals(qName)) {
    	return new FloatSG(pFactory, pSchema, pType);
    } else if (FloatSG.FLOAT_OBJECT_TYPE.equals(qName)) {
    	FloatSG result = new FloatSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (DoubleSG.DOUBLE_TYPE.equals(qName)) {
    	return new DoubleSG(pFactory, pSchema, pType);
    } else if (DoubleSG.DOUBLE_OBJECT_TYPE.equals(qName)) {
        DoubleSG result = new DoubleSG(pFactory, pSchema, pType);
        result.setNullable(true);
        return result;
    } else if (StringSG.STRING_TYPE.equals(qName)) {
    	return new StringSG(pFactory, pSchema, pType);
    } else if (IntegerSG.INTEGER_TYPE.equals(qName)) {
    	return new IntegerSG(pFactory, pSchema, pType);
    } else if (DecimalSG.DECIMAL_TYPE.equals(qName)) {
    	return new DecimalSG(pFactory, pSchema, pType);
    } else if (QNameSG.QNAME_TYPE.equals(qName)) {
    	return new QNameSG(pFactory, pSchema, pType);
    } else if (DateTimeSG.CALENDAR_TYPE.equals(qName)) {
    	return new DateTimeSG(pFactory, pSchema, pType);
    } else {
    	return null;
    }
  }

  private SimpleTypeSGChain newGlobalAtomicTypeSG(SGFactory pFactory,
  		                                          SchemaSG pSchema,
  		                                          XSType pType)
      throws SAXException {
  	if (!pType.isGlobal()) {
  		return null;
    }
    XsQName myName = pType.getName();
    if (myName.equals(XSNotation.getInstance().getName())    ||
        myName.equals(XSGYearMonth.getInstance().getName())  ||
        myName.equals(XSGYear.getInstance().getName())       ||
        myName.equals(XSGMonthDay.getInstance().getName())   ||
        myName.equals(XSGMonth.getInstance().getName())      ||
        myName.equals(XSGDay.getInstance().getName())) {
      throw new SAXException("The type " + myName + " is not supported.");
    } else if (myName.equals(XSIDREF.getInstance().getName())) {
      return new IDREFSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSID.getInstance().getName())) {
      return new IDSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSByte.getInstance().getName())) {
      return new ByteSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSShort.getInstance().getName())) {
      return new ShortSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSInt.getInstance().getName())) {
      return new IntSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSLong.getInstance().getName())) {
      return new LongSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSUnsignedByte.getInstance().getName())  ||
                myName.equals(XSUnsignedShort.getInstance().getName())) {
      return new UnsignedShortSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSUnsignedInt.getInstance().getName())) {
      return new UnsignedIntSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSInteger.getInstance().getName())             ||
                myName.equals(XSNonPositiveInteger.getInstance().getName())  ||
                myName.equals(XSNegativeInteger.getInstance().getName())     ||
                myName.equals(XSUnsignedLong.getInstance().getName())        ||
                myName.equals(XSPositiveInteger.getInstance().getName())     ||
                myName.equals(XSNonNegativeInteger.getInstance().getName())) {
      return new IntegerSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSDecimal.getInstance().getName())) {
      return new DecimalSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSQName.getInstance().getName())) {
      return new QNameSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSDouble.getInstance().getName())) {
      return new DoubleSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSFloat.getInstance().getName())) {
      return new FloatSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSHexBinary.getInstance().getName())) {
      return new HexBinarySG(pFactory, pSchema, pType);
    } else if (myName.equals(XSBase64Binary.getInstance().getName())) {
      return new Base64BinarySG(pFactory, pSchema, pType);
    } else if (myName.equals(XSBoolean.getInstance().getName())) {
      return new BooleanSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSDate.getInstance().getName())) {
      return new DateSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSDateTime.getInstance().getName())) {
      return new DateTimeSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSTime.getInstance().getName())) {
      return new TimeSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSDuration.getInstance().getName())) {
      return new DurationSG(pFactory, pSchema, pType);
    } else if (myName.equals(XSAnySimpleType.getInstance().getName())     ||
                myName.equals(XSString.getInstance().getName())            ||
                myName.equals(XSAnyURI.getInstance().getName())            ||
                myName.equals(XSNormalizedString.getInstance().getName())  ||
                myName.equals(XSToken.getInstance().getName())             ||
                myName.equals(XSLanguage.getInstance().getName())          ||
                myName.equals(XSName.getInstance().getName())              ||
                myName.equals(XSNMToken.getInstance().getName())           ||
                myName.equals(XSNCName.getInstance().getName())            ||
                myName.equals(XSEntity.getInstance().getName())) {
      return new StringSG(pFactory, pSchema, pType);
    } else {
      return null;
    }
  }

  private SimpleTypeSGChain newAtomicTypeRestriction(TypeSG pController,
                                                     SGFactory pFactory,
                                                     SchemaSG pSchema,
                                                     XSType pType)
      throws SAXException {
  	XSSimpleType simpleType = pType.getSimpleType();
    SimpleTypeSGChain result = newSimpleTypeSG(pController, pFactory, pSchema, simpleType.getRestrictedType());
    result = new SimpleTypeRestrictionSG(result, pType, simpleType);
    
    if (simpleType.getEnumerations().length > 0) {
        boolean generateTypesafeEnumClass = false;
        String className = null;
        
        if (simpleType instanceof JAXBSimpleType) {
            JAXBSimpleType jaxbSimpleType = (JAXBSimpleType) simpleType;
            JAXBTypesafeEnumClass jaxbTypesafeEnumClass = jaxbSimpleType.getJAXBTypesafeEnumClass();
            if (jaxbTypesafeEnumClass != null) {
                generateTypesafeEnumClass = true;
                className = jaxbTypesafeEnumClass.getName();
            }
        }
        
        if (!generateTypesafeEnumClass) {
            XSEnumeration[] enumerations = simpleType.getEnumerations();
            for (int i = 0;  i < enumerations.length;  i++) {
                XSEnumeration enumeration = enumerations[i];
                if (enumeration instanceof JAXBEnumeration) {
                    JAXBEnumeration jaxbEnumeration = (JAXBEnumeration) enumeration;
                    if (jaxbEnumeration.getJAXBTypesafeEnumMember() != null) {
                        generateTypesafeEnumClass = true;
                        break;
                    }
                }
            }
        }
        
        if (!generateTypesafeEnumClass  &&  pType.isGlobal()) {
            XsQName[] qNames = new XsQName[]{XSNMToken.getInstance().getName()};
            XSSchema xsSchema = xsType.getXSSchema();
            if (xsSchema instanceof JAXBSchema) {
                JAXBSchema jaxbSchema = (JAXBSchema) xsSchema;
                JAXBGlobalBindings globalBindings = jaxbSchema.getJAXBGlobalBindings();
                if (globalBindings != null) {
                    qNames = globalBindings.getTypesafeEnumBase();
                }
            }
            
            for (XSType restrType = xsType;  !generateTypesafeEnumClass;  ) {
                restrType = restrType.getSimpleType().getRestrictedType();
                if (restrType.isGlobal()) {
                    for (int i = 0;  i < qNames.length;  i++) {
                        if (qNames[i].equals(restrType.getName())) {
                            generateTypesafeEnumClass = true;
                            break;
                        }
                    }
                    if (XSAnySimpleType.getInstance().getName().equals(restrType.getName())) {
                        break;
                    }
                }
            }
        }
        
        JAXBSchemaBindings jaxbSchemaBindings;
        if (xsType instanceof JAXBType) {
            jaxbSchemaBindings = ((JAXBType) xsType).getJAXBSchemaBindings();
        } else {
            jaxbSchemaBindings = null;
        }
        
        if (generateTypesafeEnumClass) {
            if (className == null) {
                XsQName qName = pType.isGlobal() ? pType.getName() : pController.getName();
                className = AbstractContext.getClassNameFromLocalName(pController.getLocator(),
                        qName.getLocalName(),
                        pSchema) + "Class";
            }
            JavaQName qName;
            if (classContext == null) {
                String packageName = AbstractContext.getPackageName(pSchema,
                        jaxbSchemaBindings,
                        pController.getLocator(),
                        pController.getName());
                qName = JavaQNameImpl.getInstance(packageName, className);
            } else {
                JavaQName elementClassName = classContext.getXMLInterfaceName();
                if (elementClassName == null) {
                    elementClassName = classContext.getXMLImplementationName();
                }
                qName = JavaQNameImpl.getInstance(elementClassName.getPackageName(), className);
            }
            result = new EnumerationSG(result, qName, pType);
        }
    }
    
    return result;
  }

  private JAXBJavaType findJavaType(SchemaSG pSchema, XSType pType)
      throws SAXException {
	if (baseType != null  &&  baseType.getJavaType() != null) {
		return baseType.getJavaType();
	}
    JAXBJavaType[] globalJavaTypes = pSchema.getJAXBJavaTypes();
    
    for (XSType currentType = pType;  currentType != null;  ) {
    	if (currentType instanceof JAXBType) {
    		JAXBType jaxbType = (JAXBType) currentType;
    		JAXBJavaType jaxbJavaType = jaxbType.getJAXBJavaType();
    		if (jaxbJavaType == null) {
    			return jaxbJavaType;
    		}
    	}
        for (int i = 0;  i < globalJavaTypes.length;  i++) {
            if (globalJavaTypes[i].getXmlType().equals(currentType.getName())) {
        		return globalJavaTypes[i];
            }
        }
        XSSimpleType simpleType = currentType.getSimpleType();
        if (simpleType.isRestriction()) {
        	currentType = simpleType.getRestrictedType();
        } else {
        	currentType = null;
        }
    }
    return null;
  }

  private SimpleTypeSGChain newSimpleTypeSG(TypeSG pController, SGFactory pFactory,
  		                                    SchemaSG pSchema, XSType pType) throws SAXException {
    if (!pType.isSimple()) {
      throw new IllegalStateException("Expected a simple type");
    }

    JAXBJavaType javaType = findJavaType(pSchema, pType);
    SimpleTypeSGChain result;
    if (javaType == null  ||  javaType.getName() == null) {
        XSSimpleType simpleType = pType.getSimpleType();
        JavaQName runtimeType;
        if (simpleType.isAtomic()) {
        	result = newGlobalAtomicTypeSG(pFactory, pSchema, pType);
            if (result == null) {
                if (!simpleType.isRestriction()) {
                    throw new SAXParseException("Atomic type must be either a builtin or a restriction of another atomic type",
                                                pType.getLocator());
                }
                TypeSG type = pSchema.getType(simpleType.getRestrictedType().getName());
                runtimeType = type.getSimpleTypeSG().getRuntimeType();
                result = newAtomicTypeRestriction(pController, pFactory, pSchema, pType);
            } else {
                SimpleTypeSG simpleTypeSG = new SimpleTypeSGImpl(result);
                simpleTypeSG.init();
            	runtimeType = simpleTypeSG.getRuntimeType();
            }
            if (javaType == null) {
                JAXBJavaType[] globalJavaTypes = pSchema.getJAXBJavaTypes();
                for (int i = 0;  i < globalJavaTypes.length;  i++) {
                	if (runtimeType.equals(globalJavaTypes[i].getName())) {
                        javaType = globalJavaTypes[i];
                        break;
                    }
                }
            }
        } else if (simpleType.isList()) {
        	result = new ListTypeSGImpl(pFactory, pSchema, pType, classContext, pController.getName());
        } else if (simpleType.isUnion()) {
            result = new UnionTypeSGImpl(pFactory, pSchema, pType, classContext, pController.getName());
        } else {
            throw new IllegalStateException("Simple type " + pType + " is neither of atomic, list, or union");
        }
    } else {
        result = newSimpleTypeSGByJavaType(javaType, pFactory, pSchema, pType);
    }

    if (javaType != null  &&
        (javaType.getParseMethod() != null  ||  javaType.getPrintMethod() != null)) {
    	result = new ParsePrintSG(result, javaType);
    }
    return result;
  }

  public Object newSimpleTypeSG(TypeSG pController) throws SAXException {
    final String mName = "newSimpleTypeSG";
    log.finest(mName, "->");
    SchemaSG schema = pController.getSchema();
    SimpleTypeSGChain result = newSimpleTypeSG(pController, schema.getFactory(), schema, xsType);
    log.finest(mName, "<-", result);
    return result;
  }

  public Object getProperty(TypeSG pController, String pName) {
    if (properties == null) {
      return null;
    }
    return properties.get(pName);
  }

  public void setProperty(TypeSG pController, String pName, Object pValue) {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(pName, pValue);
  }

  public XsSchemaHeader getSchemaHeader(TypeSG pController) {
  	return schemaHeader;
  }
}
