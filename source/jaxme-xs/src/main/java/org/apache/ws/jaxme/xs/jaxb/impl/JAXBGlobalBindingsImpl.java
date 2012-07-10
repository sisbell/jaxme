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
package org.apache.ws.jaxme.xs.jaxb.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.impl.XSLogicalParser;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsSchema;
import org.apache.ws.jaxme.xs.parser.XsObjectCreator;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.XSBase64Binary;
import org.apache.ws.jaxme.xs.types.XSDate;
import org.apache.ws.jaxme.xs.types.XSDateTime;
import org.apache.ws.jaxme.xs.types.XSDuration;
import org.apache.ws.jaxme.xs.types.XSGDay;
import org.apache.ws.jaxme.xs.types.XSGMonth;
import org.apache.ws.jaxme.xs.types.XSGMonthDay;
import org.apache.ws.jaxme.xs.types.XSGYear;
import org.apache.ws.jaxme.xs.types.XSGYearMonth;
import org.apache.ws.jaxme.xs.types.XSHexBinary;
import org.apache.ws.jaxme.xs.types.XSNCName;
import org.apache.ws.jaxme.xs.types.XSQName;
import org.apache.ws.jaxme.xs.types.XSTime;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBGlobalBindingsImpl extends XsObjectImpl implements JAXBGlobalBindings {
  private static final XsQName[] forbiddenEnumQNames =
    new XsQName[]{
      XSQName.getInstance().getName(),
      XSBase64Binary.getInstance().getName(),
      XSHexBinary.getInstance().getName(),
      XSDate.getInstance().getName(),
      XSDateTime.getInstance().getName(),
      XSTime.getInstance().getName(),
      XSDuration.getInstance().getName(),
      XSGDay.getInstance().getName(),
      XSGMonth.getInstance().getName(),
      XSGYear.getInstance().getName(),
      XSGMonthDay.getInstance().getName(),
      XSGYearMonth.getInstance().getName()
    };

  private String collectionType;
  private boolean fixedAttributeAsConstantProperty, generateIsSetMethod,
    enableFailFastCheck, choiceContentProperty,
    javaNamingConventionsDisabled, typesafeEnumMemberNameGeneratesName,
    isBindingStyleModelGroup;
  private List javaTypes;
  private JAXBGlobalBindings.UnderscoreBinding underscoreBinding =
    JAXBGlobalBindings.UnderscoreBinding.AS_WORD_SEPARATOR;
  private XsQName[] typesafeEnumBase = new XsQName[]{XSNCName.getInstance().getName()};

  /** <p>Creates a new instance of JAXBGlobalBindingsImpl.</p>
   */
  protected JAXBGlobalBindingsImpl(XsObject pParent) {
    super(pParent);
  }

  public void setCollectionType(String pType) {
    collectionType = pType;
  }

  public String getCollectionType() {
    return collectionType;
  }

  public void setFixedAttributeAsConstantProperty(boolean pFixedAttributeAsConstantProperty) {
    fixedAttributeAsConstantProperty = pFixedAttributeAsConstantProperty;
  }

  public boolean isFixedAttributeAsConstantProperty() {
    return fixedAttributeAsConstantProperty;
  }

  public void setGenerateIsSetMethod(boolean pGenerateIsSetMethod) {
    generateIsSetMethod = pGenerateIsSetMethod;
  }

  public boolean isGenerateIsSetMethod() {
    return generateIsSetMethod;
  }

  public void setEnableFailFastCheck(boolean pEnableFailFastCheck) {
    enableFailFastCheck = pEnableFailFastCheck;
  }

  public boolean isEnableFailFastCheck() {
    return enableFailFastCheck;
  }

  public void setChoiceContentProperty(boolean pChoiceContentProperty) {
    choiceContentProperty = pChoiceContentProperty;
  }

  public boolean isChoiceContentProperty() {
    return choiceContentProperty;
  }

  public void setUnderscoreBinding(JAXBGlobalBindings.UnderscoreBinding pUnderscoreBinding) {
    underscoreBinding = pUnderscoreBinding;
  }

  public JAXBGlobalBindings.UnderscoreBinding getUnderscoreBinding() {
    return underscoreBinding;
  }

  public void setEnableJavaNamingConventions(boolean pEnableJavaNamingConventions) {
    javaNamingConventionsDisabled = !pEnableJavaNamingConventions;
  }

  public boolean isEnableJavaNamingConventions() {
    return !javaNamingConventionsDisabled;
  }

  public void setTypesafeEnumBase(XsQName[] pTypes) {
    typesafeEnumBase = pTypes;
  }

  public void setTypesafeEnumBase(String pTypesafeEnumBase) throws SAXException {
    List list = new ArrayList();
    String[] parts = new String[3];
    for (StringTokenizer st = new StringTokenizer(pTypesafeEnumBase, " ,");
         st.hasMoreTokens();  ) {
      String token = st.nextToken();
      getNamespaceSupport().processName(token, parts, false);
      XsQName qName = new XsQName(parts[0], parts[1], XsQName.prefixOf(token));
      for (int i = 0;  i < forbiddenEnumQNames.length;  i++) {
        if (forbiddenEnumQNames[i].equals(qName)) {
          throw new LocSAXException("The type " + qName +
                                     " must not be specified in the typesafeEnumBase (JAXB 6.5.1)",
                                     getLocator());
        }
      }
      list.add(qName);
    }
    setTypesafeEnumBase((XsQName[]) list.toArray(new XsQName[list.size()]));
  }

  public XsQName[] getTypesafeEnumBase() {
    return typesafeEnumBase;
  }

  public void setTypesafeEnumMemberName(boolean pTypesafeEnumMemberName) {
    typesafeEnumMemberNameGeneratesName = pTypesafeEnumMemberName;
  }

  public boolean isTypesafeEnumMemberName() {
    return typesafeEnumMemberNameGeneratesName;
  }

  public void setBindingStyle(String pBindingStyle) throws SAXException {
    if ("elementBinding".equals(pBindingStyle)) {
      isBindingStyleModelGroup = false;
    } else if ("modelGroupBinding".equals(pBindingStyle)) {
      isBindingStyleModelGroup = true;
    } else {
      throw new LocSAXException("Illegal value for 'bindingStyle': " + pBindingStyle + ", expected either of 'elementBinding' or 'modelGroupBinding'.",
                                 getLocator());
    }
  }

  public boolean isBindingStyleModelGroup() {
    return isBindingStyleModelGroup;
  }

  public JAXBJavaType createJavaType() {
    JAXBJavaType.JAXBGlobalJavaType javaTypeImpl = ((JAXBXsObjectFactory) getObjectFactory()).newJAXBGlobalJavaType(this);
    if (javaTypes == null) {
    	javaTypes = new ArrayList();
    }
    javaTypes.add(javaTypeImpl);
    return javaTypeImpl;
  }

  private static final JAXBJavaType.JAXBGlobalJavaType[] DEFAULT_JAXB_JAVA_TYPES =
      new JAXBJavaType.JAXBGlobalJavaType[0];
  public JAXBJavaType.JAXBGlobalJavaType[] getJavaType() {
    if (javaTypes == null) {
      return DEFAULT_JAXB_JAVA_TYPES;
    } else {
      return (JAXBJavaType.JAXBGlobalJavaType[]) javaTypes.toArray(new JAXBJavaType.JAXBGlobalJavaType[javaTypes.size()]);
    }
  }

  protected XsObjectCreator[] getXsObjectCreators() {
      return null;
  }

  protected boolean isPrefixEnabled(String pPrefix) {
      // The list of extension prefixes is in the outermost schema.
      String[] prefixes;
      XSLogicalParser xsParser = getContext().getXSLogicalParser();
      if (xsParser == null) {
          // Just syntax parsing, take this schema as outermost instance.
          JAXBXsSchema jaxbXsSchema = (JAXBXsSchema) getXsESchema();
          prefixes = jaxbXsSchema.getJaxbExtensionBindingPrefixes();
      } else {
          prefixes = ((JAXBXsSchema) xsParser.getSyntaxSchemas()[0]).getJaxbExtensionBindingPrefixes();
      }

      for (int i = 0;  i < prefixes.length;  i++) {
          if (prefixes[i].equals(pPrefix)) {
              return true;
          }
      }
      return false;
  }

  protected XsObject getBeanByParent(XsObject pParent, Locator pLocator, XsQName pQName) throws SAXException {
      XsObjectCreator[] creators = getXsObjectCreators();
      if (creators != null) {
          for (int i = 0;  i < creators.length;  i++) {
              XsObject result = creators[i].newBean(pParent, pLocator, pQName);
              if (result != null) {
                  return result;
              }
          }
      }
      return null;
  }

  public ContentHandler getChildHandler(String pQName, String pNamespaceURI,
                                        String pLocalName) throws SAXException {
    if (JAXBParser.JAXB_SCHEMA_URI.equals(pNamespaceURI)  ||
        XSParser.XML_SCHEMA_URI.equals(pNamespaceURI)  ||
        XMLConstants.XML_NS_URI.equals(pNamespaceURI)) {
      return null;
    }

    int offset = pQName.indexOf(':');
    if (offset > 0) {
      String prefix = pQName.substring(0, offset);
      if (isPrefixEnabled(prefix)) {
          XsQName qName = new XsQName(pNamespaceURI, pLocalName, XsQName.prefixOf(pQName));
          XsObject result = getBeanByParent(this, getObjectFactory().getLocator(), qName);
          if (result != null) {
              return getObjectFactory().newXsSAXParser(result);
          } else {
              return new DefaultHandler();
          }
      }
    }
    throw new LocSAXException("Unknown child element " + pQName + ", use the schemas jaxb:extensionBindingPrefixes attribute to ignore it.",
                               getLocator());
  }
}
