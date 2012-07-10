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

import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;


/** <p>Implementation of xs:schema. Follows this specification:
 * <pre>
 *  &lt;xs:element name="schema" id="schema"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-schema"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:sequence&gt;
 *            &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *              &lt;xs:element ref="xs:include"/&gt;
 *              &lt;xs:element ref="xs:import"/&gt;
 *              &lt;xs:element ref="xs:redefine"/&gt;
 *              &lt;xs:element ref="xs:annotation"/&gt;
 *            &lt;/xs:choice&gt;
 *            &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *              &lt;xs:group ref="xs:schemaTop"/&gt;
 *              &lt;xs:element ref="xs:annotation" minOccurs="0" maxOccurs="unbounded"/&gt;
 *            &lt;/xs:sequence&gt;
 *          &lt;/xs:sequence&gt;
 *          &lt;xs:attribute name="targetNamespace" type="xs:anyURI"/&gt;
 *          &lt;xs:attribute name="version" type="xs:token"/&gt;
 *          &lt;xs:attribute name="finalDefault" type="xs:derivationSet" use="optional" default=""/&gt;
 *          &lt;xs:attribute name="blockDefault" type="xs:blockSet" use="optional" default=""/&gt;
 *          &lt;xs:attribute name="attributeFormDefault" type="xs:formChoice" use="optional" default="unqualified"/&gt;
 *          &lt;xs:attribute name="elementFormDefault" type="xs:formChoice" use="optional" default="unqualified"/&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *          &lt;xs:attribute ref="xml:lang"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *
 *    &lt;xs:key name="element"&gt;
 *      &lt;xs:selector xpath="xs:element"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="attribute"&gt;
 *      &lt;xs:selector xpath="xs:attribute"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="type"&gt;
 *      &lt;xs:selector xpath="xs:complexType|xs:simpleType"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="group"&gt;
 *      &lt;xs:selector xpath="xs:group"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="attributeGroup"&gt;
 *      &lt;xs:selector xpath="xs:attributeGroup"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="notation"&gt;
 *      &lt;xs:selector xpath="xs:notation"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="identityConstraint"&gt;
 *      &lt;xs:selector xpath=".//xs:key|.//xs:unique|.//xs:keyref"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *  &lt;/xs:element&gt;
 *
 *  &lt;xs:group name="schemaTop"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        This group is for the
 *        elements which occur freely at the top level of schemas.
 *        All of their types are based on the "annotated" type by extension.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:group ref="xs:redefinable"/&gt;
 *      &lt;xs:element ref="xs:element"/&gt;
 *      &lt;xs:element ref="xs:attribute"/&gt;
 *      &lt;xs:element ref="xs:notation"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsESchemaImpl extends XsTOpenAttrsImpl implements XsESchema {
  private final XSContext context;
  private XsAnyURI targetNamespace;
  private String targetNamespacePrefix;
  private XsToken version;
  private XsDerivationSet finalDefault = new XsDerivationSet("");
  private XsBlockSet blockDefault = new XsBlockSet("");
  private XsFormChoice attributeFormDefault = XsFormChoice.UNQUALIFIED;
  private XsFormChoice elementFormDefault = XsFormChoice.UNQUALIFIED;
  private XsID id;
  private XmlLang language;
  private List childs = new ArrayList();
  boolean schemaTopSeen = false;

  protected XsESchemaImpl(XSContext pContext) {
    super(null);
    context = pContext;
  }

  public XSContext getContext() { return context; }

  public XsEInclude createInclude() {
    if (schemaTopSeen) {
      throw new IllegalStateException("xs:include is not allowed after xs:simpleType, xs:complexType, xs:group, xs:attributeGroup, xs:element. xs:attribute, or xs:notation have been seen.");
    }
    XsEInclude xsInclude = getObjectFactory().newXsEInclude(this);
    childs.add(xsInclude);
    return xsInclude;
  }

  public XsEImport createImport() {
    if (schemaTopSeen) {
      throw new IllegalStateException("xs:include is not allowed after xs:simpleType, xs:complexType, xs:group, xs:attributeGroup, xs:element. xs:attribute, or xs:notation have been seen.");
    }
    XsEImport xsImport = getObjectFactory().newXsEImport(this);
    childs.add(xsImport);
    return xsImport;
  }

  public XsERedefine createRedefine() {
    if (schemaTopSeen) {
      throw new IllegalStateException("xs:include is not allowed after xs:simpleType, xs:complexType, xs:group, xs:attributeGroup, xs:element. xs:attribute, or xs:notation have been seen.");
    }
    XsERedefine xsRedefine = getObjectFactory().newXsERedefine(this);
    childs.add(xsRedefine);
    return xsRedefine;
  }

  public XsEAnnotation createAnnotation() {
    XsEAnnotation xsAnnotation = getObjectFactory().newXsEAnnotation(this);
    childs.add(xsAnnotation);
    return xsAnnotation;
  }

  public XsETopLevelSimpleType createSimpleType() {
    schemaTopSeen = true;
    XsETopLevelSimpleType xsSimpleType = getObjectFactory().newXsETopLevelSimpleType(this);
    childs.add(xsSimpleType);
    return xsSimpleType;
  }

  public XsTComplexType createComplexType() {
    schemaTopSeen = true;
    XsTComplexType xsComplexType = getObjectFactory().newXsTComplexType(this);
    childs.add(xsComplexType);
    return xsComplexType;
  }

  public XsTNamedGroup createGroup() {
    schemaTopSeen = true;
    XsTNamedGroup xsGroup = getObjectFactory().newXsTNamedGroup(this);
    childs.add(xsGroup);
    return xsGroup;
  }

  public XsTAttributeGroup createAttributeGroup() {
    schemaTopSeen = true;
    XsTAttributeGroup xsAttributeGroup = getObjectFactory().newXsTAttributeGroup(this);
    childs.add(xsAttributeGroup);
    return xsAttributeGroup;
  }

  public XsTTopLevelElement createElement() {
    schemaTopSeen = true;
    XsTTopLevelElement xsElement = getObjectFactory().newXsTTopLevelElement(this);
    childs.add(xsElement);
    return xsElement;
  }

  public XsTAttribute createAttribute() {
    schemaTopSeen = true;
    XsTAttribute xsAttribute = getObjectFactory().newXsTAttribute(this);
    childs.add(xsAttribute);
    return xsAttribute;
  }

  public XsENotation createNotation() {
    schemaTopSeen = true;
    XsENotation xsNotation = getObjectFactory().newXsENotation(this);
    childs.add(xsNotation);
    return xsNotation;
  }

  public XsFormChoice getAttributeFormDefault() { return attributeFormDefault; }
  public void setAttributeFormDefault(XsFormChoice pChoice) { attributeFormDefault = pChoice; }
  public XsBlockSet getBlockDefault() { return blockDefault; }
  public void setBlockDefault(XsBlockSet pSet) { blockDefault = pSet; }
  public XsFormChoice getElementFormDefault() { return elementFormDefault; }
  public void setElementFormDefault(XsFormChoice pChoice) { elementFormDefault = pChoice; }
  public XsDerivationSet getFinalDefault() { return finalDefault; }
  public void setFinalDefault(XsDerivationSet pSet) { finalDefault = pSet; }
  public XsID getId() { return id; }
  public void setId(XsID pId) { id = pId; }
  public XsAnyURI getTargetNamespace() { return targetNamespace; }
  public String getTargetNamespacePrefix() { return targetNamespacePrefix; }
  public void setTargetNamespace(XsAnyURI pAnyURI) {
    targetNamespace = pAnyURI;
    if (targetNamespace == null) {
      targetNamespacePrefix = null;
    } else {
      NamespaceSupport nss = getNamespaceSupport();
      targetNamespacePrefix = nss.getPrefix(targetNamespace.toString());
    }
  }
  public XsToken getVersion() { return version; }
  public void setVersion(XsToken pToken) { version = pToken; }
  public XmlLang getLang() { return language; }
  public void setLang(XmlLang pLanguage) { language = pLanguage; }

  public boolean setAttribute(String pQName, String pNamespaceURI,
                                String pLocalName, String pValue) throws SAXException {
    if (XMLConstants.XML_NS_URI.equals(pNamespaceURI)  &&  "lang".equals(pLocalName)) {
      setLang(new XmlLang(pValue));
      return true;
    }
    return super.setAttribute(pQName, pNamespaceURI, pLocalName, pValue);
  }

  public Object[] getChilds() {
    return childs.toArray();
  }

  	/** <p>Creates a new {@link XsQName}, which is mutable and
  	 * has the schemas target namespace as a namespace URI:
  	 * If the target namespace changes, then the names URI
  	 * does as well.</p>
  	 */
  	public XsQName newXsQName(String pLocalName, String pPrefix) {
  	    return new XsQName(getTargetNamespace(), pLocalName, pPrefix){  	        
  	        public String getNamespaceURI() {
  	            XsAnyURI uri = getTargetNamespace();
  	            if (uri != null) {
  	                return uri.toString();
  	            } else {
  	                return "";
  	            }
  	        }
  	        public String toString() {
  	            XsAnyURI uri = getTargetNamespace();
  	            if (uri == null) {
  	                return super.toString();
  	            } else {
  	                return "{" + uri + "}" + getLocalName() + " (<= " + super.toString() + ")";
  	            }
  	        }
  	    };
  	}
}
