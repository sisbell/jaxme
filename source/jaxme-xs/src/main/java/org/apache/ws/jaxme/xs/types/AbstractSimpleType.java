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
package org.apache.ws.jaxme.xs.types;

import org.apache.ws.jaxme.xs.XSAtomicType;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSUnionType;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractSimpleType extends AbstractBuiltinType implements XSSimpleType {
  public boolean isSimple() { return true; }
  public boolean isAtomic() { return false; }
  public boolean isList() { return false; }
  public boolean isUnion() { return false; }
  public XSSimpleType getSimpleType() { return this; }
  public String[][] getPattern() { return null; }
  public XSEnumeration[] getEnumerations() { return new XSEnumeration[0]; }
  public XSObject getParentObject() {
    throw new IllegalStateException("The " + getName() +
                                     " type is declared outside of any schema and doesn't have a parent.");
  }
  public boolean isTopLevelObject() { return true; }

  public XSComplexType getComplexType() {
    throw new IllegalStateException("This is a complex type.");
  }

  public XSAtomicType getAtomicType() {
    throw new IllegalStateException("The type " + getName() + " is not atomic.");
  }

  public XSListType getListType() {
    throw new IllegalStateException("The global type " + getName() + " is no list type.");
  }

  public XSUnionType getUnionType() {
    throw new IllegalStateException("The global type " + getName() + " is no union type.");
  }

  public XSObjectFactory getXSObjectFactory() {
    throw new IllegalStateException("The global type " + getName() + " does not have an object factory.");
  }
}
