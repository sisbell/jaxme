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

import org.apache.ws.jaxme.xs.XSAtomicType;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSUnionType;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XSSimpleTypeImpl implements XSSimpleType {
  public boolean isAtomic() { return false; }
  public XSAtomicType getAtomicType() { throw new IllegalStateException("This type is not atomic."); }
  public boolean isList() { return false; }
  public XSListType getListType() { throw new IllegalStateException("This type is no list."); }
  public boolean isUnion() { return false; }
  public XSUnionType getUnionType() { throw new IllegalStateException("This type is no union."); }
}
