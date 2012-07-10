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
package org.apache.ws.jaxme.xs.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSAttributeGroup;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.XSNotation;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.*;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>Implementation of an XML Schema, as defined by the
 * {@link XSSchema} interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSSchemaImpl implements XSSchema {
  private static final XSType[] BUILTIN_TYPES = new XSType[]{
    XSAnySimpleType.getInstance(),
    XSAnyURI.getInstance(),
    XSBase64Binary.getInstance(),
    XSBoolean.getInstance(),
    XSByte.getInstance(),
    XSDate.getInstance(),
    XSDateTime.getInstance(),
    XSDecimal.getInstance(),
    XSDouble.getInstance(),
    XSDuration.getInstance(),
    XSEntities.getInstance(),
    XSEntity.getInstance(),
    XSFloat.getInstance(),
    XSGDay.getInstance(),
    XSGMonth.getInstance(),
    XSGMonthDay.getInstance(),
    XSGYear.getInstance(),
    XSGYearMonth.getInstance(),
    XSHexBinary.getInstance(),
    XSID.getInstance(),
    XSIDREF.getInstance(),
    XSIDREFs.getInstance(),
    XSInt.getInstance(),
    XSInteger.getInstance(),
    XSLanguage.getInstance(),
    XSLong.getInstance(),
    XSName.getInstance(),
    XSNCName.getInstance(),
    XSNegativeInteger.getInstance(),
    XSNMToken.getInstance(),
    XSNMTokens.getInstance(),
    XSNonNegativeInteger.getInstance(),
    XSNonPositiveInteger.getInstance(),
    XSNormalizedString.getInstance(),
    org.apache.ws.jaxme.xs.types.XSNotation.getInstance(),
    XSPositiveInteger.getInstance(),
    XSQName.getInstance(),
    XSShort.getInstance(),
    XSString.getInstance(),
    XSTime.getInstance(),
    XSToken.getInstance(),
    XSUnsignedByte.getInstance(),
    XSUnsignedInt.getInstance(),
    XSUnsignedLong.getInstance(),
    XSUnsignedShort.getInstance(),
    XSAnyType.getInstance()
  };

  private final XSContext context;
  private final XsESchema syntaxSchema;
  private final List childs = new ArrayList(1);
  private final Map types = new HashMap(1);
  private final Map builtinTypes = new HashMap(1);
  private final Map groups = new HashMap(1);
  private final Map attributeGroups = new HashMap(1);
  private final Map attributes = new HashMap(1);
  private final Map elements = new HashMap(1);
  private final Attributes openAttrs;

  private final Map identityConstraintsMap = new HashMap(1);
  private final Map keyRefsMap = new HashMap(1);

  private final Map immutableIdentityConstraintsMap 
    = Collections.unmodifiableMap( identityConstraintsMap );
  private final Map immutableKeyRefsMap = Collections.unmodifiableMap( 
    keyRefsMap 
  );

  private boolean isValidated;

  /** <p>Creates a new logical schema by loading data,
   * which represents the given syntactical schema
   * <code>pSchema</code> and uses the given context
   * <code>pContext</code> for acquiring additional
   * information.</p>
   */
  public XSSchemaImpl(XSContext pContext, XsESchema pSchema) {
    context = pContext;
    syntaxSchema = pSchema;
    for (int i = 0;  i < BUILTIN_TYPES.length;  i++) {
      builtinTypes.put(BUILTIN_TYPES[i].getName(), BUILTIN_TYPES[i]);
    }
    openAttrs = pSchema.getOpenAttributes();
  }

  public XSContext getContext() { return context; }
  public boolean isTopLevelObject() { return true; }
  public XSObject getParentObject() { return null; }
  public XSObjectFactory getXSObjectFactory() { return context.getXSObjectFactory(); }
  public Locator getLocator() { return syntaxSchema.getLocator(); }
  public XSSchema getXSSchema() { return this; }
  protected XsESchema getXsESchema() { return syntaxSchema; }

  /** <p>Adds a new child to the array returned by {@link #getChilds()}.</p>
   */
  protected void addChild(Object pChild) {
    childs.add(pChild);
  }

  /** <p>Replaces the existing child <code>pOldChild</code> with
   * the replacement object <code>pNewChild</code>. This method
   * is used from within the various <code>redefine()</code> methods.</p>
   */
  protected void replace(Object pOldChild, Object pNewChild) {
    for (ListIterator iter = childs.listIterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o.equals(pOldChild)) {
        iter.set(pNewChild);
        return;
      }
    }
    throw new NoSuchElementException();
  }

  public XSAnnotation[] getAnnotations() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSAnnotation) {
        result.add(o);
      }
    }
    return (XSAnnotation[]) result.toArray(new XSAnnotation[result.size()]);
  }

  public XSType[] getTypes() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSType) {
        result.add(o);
      }
    }
    return (XSType[]) result.toArray(new XSType[result.size()]);
  }

  public XSType[] getBuiltinTypes() {
    return BUILTIN_TYPES;
  }

  public XSType getType(XsQName pName) {
    XSType result = (XSType) types.get(pName);
    if (result == null) {
      result = (XSType) builtinTypes.get(pName);
    }
    return result;
  }

  public XSGroup[] getGroups() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSGroup) {
        result.add(o);
      }
    }
    return (XSGroup[]) result.toArray(new XSGroup[result.size()]);
  }

  public XSGroup getGroup(XsQName pName) {
    return (XSGroup) groups.get(pName);
  }

  public XSAttributeGroup[] getAttributeGroups() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSAttributeGroup) {
        result.add(o);
      }
    }
    return (XSAttributeGroup[]) result.toArray(new XSAttributeGroup[result.size()]);
  }

  public XSAttributeGroup getAttributeGroup(XsQName pName) {
    return (XSAttributeGroup) attributeGroups.get(pName);
  }

  public XSElement[] getElements() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSElement) {
        result.add(o);
      }
    }
    return (XSElement[]) result.toArray(new XSElement[result.size()]);
  }

  public XSElement getElement(XsQName pName) {
    return (XSElement) elements.get(pName);
  }

  public Map getIdentityConstraints() {
    return immutableIdentityConstraintsMap;
  }

  public Map getKeyRefs() {
    return immutableKeyRefsMap;
  }

  public void add( XSIdentityConstraint ic ) throws SAXException {
    String name = ic.getName();

    if ( name == null ) {
      throw new LocSAXException(
        "An identity constraint must have a 'name' attribute.", 
        ic.getLocator()
      );
    } else if ( identityConstraintsMap.put( name, ic ) != null ) {
      throw new LocSAXException(
        "No two identity constraints may share the same name.", 
        ic.getLocator()
      );
    }

    identityConstraintsMap.put( name, ic );
  }

  public void add( XSKeyRef rf ) throws SAXException {
    String name = rf.getName();

    if ( name == null ) {
      throw new LocSAXException(
        "A key ref must have a 'name' attribute.", 
        rf.getLocator()
      );
    } else if ( keyRefsMap.put( name, rf ) != null ) {
      throw new LocSAXException(
        "No two key refs may share the same name.", 
        rf.getLocator()
      );
    }

    keyRefsMap.put( name, rf );
  }



  public XSAttribute[] getAttributes() {
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XSAttribute) {
        result.add(o);
      }
    }
    return (XSAttribute[]) result.toArray(new XSAttribute[result.size()]);
  }

  public XSAttribute getAttribute(XsQName pQName) {
    return (XSAttribute) attributes.get(pQName);
  }

  public void add(XSAnnotation pAnnotation) {
    addChild(pAnnotation);
  }

  public void add(XSType pType) throws SAXException {
    XsQName name = pType.getName();
    if (name == null) {
      throw new LocSAXException("A global type must have a 'name' attribute.", pType.getLocator());
    }
    if (types.containsKey(name)) {
      throw new LocSAXException("A global type " + name + " is already defined.", pType.getLocator());
    }
    types.put(name, pType);
    pType.setGlobal(true);
    addChild(pType);
  }

  public void redefine(XSType pType) throws SAXException {
    XsQName name = pType.getName();
    if (name == null) {
      throw new LocSAXException("A global type must have a 'name' attribute.", pType.getLocator());
    }
    Object oldType = types.get(name);
    if (oldType == null) {
      throw new LocSAXException("The global type " + name + " cannot be redefined, as it doesn't yet exist.",
                                   pType.getLocator());
    }
    types.put(name, pType);
    pType.setGlobal(true);
    replace(oldType, pType);
  }

  public void add(XSGroup pGroup) throws SAXException {
    XsQName name = pGroup.getName();
    if (name == null) {
      throw new LocSAXException("A global group must have a 'name' attribute.", pGroup.getLocator());
    }
    if (groups.containsKey(name)) {
      throw new LocSAXException("A global group " + name + " is already defined.", pGroup.getLocator());
    }
    groups.put(name, pGroup);
    addChild(pGroup);
    pGroup.setGlobal(true);
  }

  public void redefine(XSGroup pGroup) throws SAXException {
    XsQName name = pGroup.getName();
    if (name == null) {
      throw new LocSAXException("A global group must have a 'name' attribute.", pGroup.getLocator());
    }
    Object oldGroup = groups.get(name);
    if (oldGroup == null) {
      throw new LocSAXException("The global group " + name + " cannot be redefined, as it doesn't yet exist.",
                                   pGroup.getLocator());
    }
    groups.put(name, pGroup);
    replace(oldGroup, pGroup);
  }

  public void add(XSAttributeGroup pGroup) throws SAXException {
    XsQName name = pGroup.getName();
    if (name == null) {
      throw new LocSAXException("A global attribute group must have a 'name' attribute.", pGroup.getLocator());
    }
    if (attributeGroups.containsKey(name)) {
      throw new LocSAXException("A global attribute group " + name + " is already defined.", pGroup.getLocator());
    }
    attributeGroups.put(name, pGroup);
    addChild(pGroup);
  }

  public void redefine(XSAttributeGroup pGroup) throws SAXException {
    XsQName name = pGroup.getName();
    if (name == null) {
      throw new LocSAXException("A global attribute group must have a 'name' attribute.", pGroup.getLocator());
    }
    Object oldGroup = attributeGroups.get(name);
    if (!attributeGroups.containsKey(name)) {
      throw new LocSAXException("The global attribute group " + name + " cannot be redefined, as it doesn't yet exist.",
                                   pGroup.getLocator());
    }
    attributeGroups.put(name, pGroup);
    replace(oldGroup, pGroup);
  }

  public void add(XSAttribute pAttribute) throws SAXException {
    XsQName name = pAttribute.getName();
    if (name == null) {
      throw new LocSAXException("A global attribute must have a 'name' attribute.", pAttribute.getLocator());
    }
    if (attributes.containsKey(name)) {
      throw new LocSAXException("A global attribute " + name + " is already defined.", pAttribute.getLocator());
    }
    attributes.put(name, pAttribute);
    addChild(pAttribute);
  }

  public void add(XSElement pElement) throws SAXException {
    XsQName name = pElement.getName();
    if (name == null) {
      throw new LocSAXException("A global element must have a 'name' attribute.", pElement.getLocator());
    }
    if (elements.containsKey(name)) {
      throw new LocSAXException("A global element " + name + " is already defined.", pElement.getLocator());
    }
    elements.put(name, pElement);
    addChild(pElement);
  }

  public void add(XSNotation pNotation) {
    addChild(pNotation);
  }

  public Object[] getChilds() {
    return childs.toArray();
  }

  protected void validate(Object pChild) throws SAXException {
    if (pChild instanceof XSObject) {
      ((XSObject) pChild).validate();
    } else {
      throw new IllegalStateException("Unable to validate the child " + pChild +
                                       ", perhaps you should overwrite the method " +
                                       getClass().getName() + ".validate(Object).");
    }
  }

  protected boolean isValidated() {
    return isValidated;
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    } else {
      isValidated = true;
    }

    Object[] myChilds = getChilds();
    for (int i = 0;  i < myChilds.length;  i++) {
      validate(myChilds[i]);
    }
  }

    public Attributes getOpenAttributes() {
    	return openAttrs;
    }

    public XsAnyURI getTargetNamespace() {
		return syntaxSchema.getTargetNamespace();
	}
}
