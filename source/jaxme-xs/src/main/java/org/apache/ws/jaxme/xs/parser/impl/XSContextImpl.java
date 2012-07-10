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
package org.apache.ws.jaxme.xs.parser.impl;

import org.apache.ws.jaxme.xs.*;
import org.apache.ws.jaxme.xs.impl.*;
import org.apache.ws.jaxme.xs.parser.*;
import org.apache.ws.jaxme.xs.parser.AttributeSetter;
import org.apache.ws.jaxme.xs.parser.ChildSetter;
import org.apache.ws.jaxme.xs.parser.TextSetter;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectFactoryImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.NamespaceSupport;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
/** <p>This class provides access to the parsers internal data. The
 * <code>Context</code> instance is stored as a thread local element, thus
 * accessible via a static method.</p>
 */
public class XSContextImpl implements XSContext {
  public static final XsObjectFactory DEFAULT_OBJECT_FACTORY = new XsObjectFactoryImpl();
  public static final XSObjectFactory DEFAULT_XS_OBJECT_FACTORY = new XSObjectFactoryImpl();
  public static final AttributeSetter DEFAULT_ATTRIBUTE_SETTER = new AttributeSetterImpl();
  public static final ChildSetter DEFAULT_CHILD_SETTER = new ChildSetterImpl();
  public static final TextSetter DEFAULT_TEXT_SETTER = new TextSetterImpl();

  private XsObjectFactory objectFactory = DEFAULT_OBJECT_FACTORY;
  private XSObjectFactory xSObjectFactory = DEFAULT_XS_OBJECT_FACTORY;
  private AttributeSetter attributeSetter = DEFAULT_ATTRIBUTE_SETTER;
  private ChildSetter childSetter = DEFAULT_CHILD_SETTER;
  private TextSetter textSetter = DEFAULT_TEXT_SETTER;
  private XSLogicalParser xsParser;
  private Locator locator;
  private NamespaceSupport namespaceSupport = new NamespaceSupport();
  private ContentHandler currentContentHandler;

  public XsObjectFactory getXsObjectFactory() { return objectFactory; }
  public void setXsObjectFactory(XsObjectFactory pFactory) { objectFactory = pFactory; }
  public XSObjectFactory getXSObjectFactory() { return xSObjectFactory; }
  public void setXSObjectFactory(XSObjectFactory pFactory) { xSObjectFactory = pFactory; }
  public AttributeSetter getAttributeSetter() { return attributeSetter; }
  public void setAttributeSetter(AttributeSetter pAttributeSetter) { attributeSetter = pAttributeSetter; }
  public ChildSetter getChildSetter() { return childSetter; }
  public void setChildSetter(ChildSetter pChildSetter) { childSetter = pChildSetter; }
  public TextSetter getTextSetter() { return textSetter; }
  public void setTextSetter(TextSetter pTextSetter) { textSetter = pTextSetter; }
  public XSLogicalParser getXSLogicalParser() { return xsParser; }
  public void setXSLogicalParser(XSLogicalParser pParser) { xsParser = pParser; }
  public XSSchema getXSSchema() { return getXSLogicalParser().getSchema(); }
  public Locator getLocator() { return locator; }
  public void setLocator(Locator pLocator) { locator = pLocator; }
  public void setNamespaceSupport(NamespaceSupport pNamespaceSupport) { namespaceSupport = pNamespaceSupport; }
  public NamespaceSupport getNamespaceSupport() { return namespaceSupport; }

  public void setCurrentContentHandler(ContentHandler pHandler) { currentContentHandler = pHandler; }
  public ContentHandler getCurrentContentHandler() { return currentContentHandler; }
}