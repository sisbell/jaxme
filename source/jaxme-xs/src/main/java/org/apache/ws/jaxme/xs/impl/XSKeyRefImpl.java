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

import org.apache.ws.jaxme.xs.XPathMatcher;
import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSElementOrAttrRef;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEKeyref;
import org.xml.sax.SAXException;

/** 
 * Default implementation of the XSKeyRef.
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public class XSKeyRefImpl extends XSOpenAttrsImpl 
  implements XSKeyRef
{
  private XSAnnotation[] annotations;
  private String name;
  private XSIdentityConstraint identityConstraint;
  private XsEKeyref keyRef;
  private XSElementOrAttrRef[][] matchCriteria;

  protected XSKeyRefImpl( XSElement pParent, XsEKeyref keyRef ) 
    throws SAXException
  {
    super( pParent, keyRef );

    this.annotations = getXSSchema().getXSObjectFactory().newXSAnnotations(
      this, 
      keyRef.getAnnotation()
    );

    this.name = keyRef.getName().getValue();
    this.keyRef = keyRef;
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
  }

  /** 
   * @see XSKeyRef#getName
   */
  public String getName() {
    return name;
  }

  /**
   * @see XSKeyRef#getIdentityConstraint
   */
  public XSIdentityConstraint getIdentityConstraint() {
    return identityConstraint;
  }

  /**
   * @see XSKeyRef#getMatchCriteria
   */
  public XSElementOrAttrRef[][] getMatchCriteria() {
    return matchCriteria;
  }

  public void validate() throws SAXException {
    validateAllIn( annotations );

    String referredIdentity = keyRef.getRefer().getLocalName();
    identityConstraint = (XSIdentityConstraint) getXSSchema().getIdentityConstraints().get(
      referredIdentity 
    );

    if ( identityConstraint == null ) {
      throw new LocSAXException( 
        "Unknown identity constraint: " + referredIdentity, 
        keyRef.getLocator()
      );
    }

    matchCriteria = XPathMatcher.match( 
      keyRef, 
      (XSElement) getParentObject() 
    );
  }
}
