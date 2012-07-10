/*
 * Copyright 2004  The Apache Software Foundation
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

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSElementOrAttrRef;
import org.apache.ws.jaxme.xs.XPathMatcher;
import org.apache.ws.jaxme.xs.xml.XsEKey;
import org.apache.ws.jaxme.xs.xml.XsTKeybase;
import org.apache.ws.jaxme.xs.xml.XsEUnique;
import org.xml.sax.SAXException;

/** 
 * Default implementation of the XSIdentityConstraint.
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public class XSIdentityConstraintImpl extends XSOpenAttrsImpl 
  implements XSIdentityConstraint 
{
  private XSAnnotation[] annotations;
  private String name;
  private boolean isUnique;
  private XsTKeybase keyBase;
  private XSElementOrAttrRef[][] matchCriteria;

  protected XSIdentityConstraintImpl( XSElement pParent, XsEKey key ) 
    throws SAXException
  {
    super( pParent, key );

    initSelf( pParent, key, false );
  }

  protected XSIdentityConstraintImpl( XSElement pParent, XsEUnique unique ) 
    throws SAXException
  {
    super( pParent, unique );

    initSelf( pParent, unique, true );
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
  }

  /** 
   * @see XSIdentityConstraintImpl#getName
   */
  public String getName() {
    return name;
  }

  /**
   * @see XSIdentityConstraintImpl#isUnique
   */
  public boolean isUnique() {
    return isUnique;
  }

  /**
   * @see XSIdentityConstraintImpl#getMatchCriteria
   */
  public XSElementOrAttrRef[][] getMatchCriteria() {
    return matchCriteria;
  }

  public void validate() throws SAXException {
    matchCriteria = XPathMatcher.match( 
      keyBase, 
      (XSElement) getParentObject() 
    );

    validateAllIn( annotations );
  }

  private void initSelf( 
    XSElement pParent, 
    XsTKeybase keyBase, 
    boolean isUnique 
  ) throws SAXException {
    this.isUnique = isUnique;
    this.name = keyBase.getName().getValue();
    this.keyBase = keyBase;

    this.annotations = getXSSchema().getXSObjectFactory().newXSAnnotations(
      this, 
      keyBase.getAnnotation()
    );
  }
}
