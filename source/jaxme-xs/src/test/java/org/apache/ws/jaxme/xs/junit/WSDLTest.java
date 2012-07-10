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
 
 package org.apache.ws.jaxme.xs.junit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;
import org.apache.ws.jaxme.xs.XSContentHandler;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** <p>A variety of WSDL schemas being tested.</p>
 *
 * @author Srinath Perera (hemapani@opensource.lk)
 * @author Jeyakumaran.C (jkumaran@opensource.lk)
 */
public class WSDLTest extends TestCase {
  private static final boolean parseBySax = false;
  private static final boolean parseByDom = true;

  public WSDLTest(String pArg0) {
    super(pArg0);
  }

  /** <p>This is the actual parsing code.</p>
   */
  public void addTypes(org.w3c.dom.Element ele, Document pSoapDataTypes) throws SAXException {
    Element element = pSoapDataTypes.getDocumentElement();
    String namespace = element.getAttributeNS(null, "targetNamespace");
    if (namespace == null  ||  namespace.length() == 0) {
      throw new IllegalStateException("Namespace is not set");
    }
    XSParser parser = new XSParser();
    parser.addImport(namespace, null, pSoapDataTypes);
    XSSchema schema = parser.parse(ele);
    assertNotNull(schema);
    assertTrue(schema.getElements().length > 0);
  }

  private class FilterHandler implements ContentHandler {
    private XSContentHandler xsContentHandler;
    private List prefixes = new ArrayList();
    private Locator locator;
    private List schemas = new ArrayList();
    private final Document myDataTypes;

    public FilterHandler(Document pSoapDataTypes) {
      myDataTypes = pSoapDataTypes;
    }

    public XSSchema[] getSchemas() {
      return (XSSchema[]) schemas.toArray(new XSSchema[schemas.size()]);
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void characters(char[] pChars, int pOffset, int pLen) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.characters(pChars, pOffset, pLen);
      }
    }

    public void ignorableWhitespace(char[] pChars, int pOffset, int pLen) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.ignorableWhitespace(pChars, pOffset, pLen);
      }
    }

    public void endPrefixMapping(String pPrefix) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.endPrefixMapping(pPrefix);
      }
      int size = prefixes.size();
      prefixes.remove(size-1);
      prefixes.remove(size-2);
    }

    public void skippedEntity(String pEntity) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.skippedEntity(pEntity);
      }
    }

    public void setDocumentLocator(Locator pLocator) {
      if (xsContentHandler != null) {
        xsContentHandler.setDocumentLocator(pLocator);
      }
      locator = pLocator;
    }

    public void processingInstruction(String pTarget, String pData) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.processingInstruction(pTarget, pData);
      }
    }

    public void startPrefixMapping(String pPrefix, String pUri) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.startPrefixMapping(pPrefix, pUri);
      }
      prefixes.add(pPrefix);
      prefixes.add(pUri);
    }

    public void endElement(String pNamespaceURI, String pLocalName, String pQName) throws SAXException {
      if (xsContentHandler != null) {
        xsContentHandler.endElement(pNamespaceURI, pLocalName, pQName);
        if (XSParser.XML_SCHEMA_URI.equals(pNamespaceURI)  &&  "schema".equals(pLocalName)) {
          for (int i = prefixes.size();  i > 0;  i -= 2) {
            xsContentHandler.endPrefixMapping((String) prefixes.get(i-2));
          }
          xsContentHandler.endDocument();
          schemas.add(xsContentHandler.getXSSchema());
          xsContentHandler = null;
        }
      }
    }

    public void startElement(String pNamespaceURI, String pLocalName, String pQName,
                              Attributes pAttr) throws SAXException {
      if (xsContentHandler == null) {
        if (XSParser.XML_SCHEMA_URI.equals(pNamespaceURI)  &&  "schema".equals(pLocalName)) {
          XSParser parser = new XSParser();
          Element element = myDataTypes.getDocumentElement();
          String namespace = element.getAttributeNS(null, "targetNamespace");
          if (namespace == null  ||  namespace.length() == 0) {
            throw new IllegalStateException("Namespace is not set");
          }
          parser.addImport(namespace, soapDataTypes);
          xsContentHandler = new XSParser().getXSContentHandler(null);
          if (locator != null) {
            xsContentHandler.setDocumentLocator(locator);
          }
          xsContentHandler.startDocument();
          for (int i = 0;  i < prefixes.size();  i += 2) {
            xsContentHandler.startPrefixMapping((String) prefixes.get(i), (String) prefixes.get(i+1));
          }
          xsContentHandler.startElement(pNamespaceURI, pLocalName, pQName, pAttr);
        }
      } else {
        xsContentHandler.startElement(pNamespaceURI, pLocalName, pQName, pAttr);
      }
    }
  }

  public void parseBySax(String file, String name, Document pSoapDataTypes)
      throws SAXException, ParserConfigurationException, IOException {
    StringReader sr = new StringReader(file);
    InputSource isource = new InputSource(sr);
    isource.setSystemId(name);
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setValidating(false);
    spf.setNamespaceAware(true);
    SAXParser sp = spf.newSAXParser();
    XMLReader xr = sp.getXMLReader();
    FilterHandler fh = new FilterHandler(pSoapDataTypes);
    xr.setContentHandler(fh);
    xr.parse(isource);
    assertTrue(fh.getSchemas().length > 0);
  }

  public void parseByDom(String file, String name, Document pSoapDataTypes)
      throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(true);
    StringReader sr = new StringReader(file);
    InputSource isource = new InputSource(sr);
    isource.setSystemId(name);
    Document doc = dbf.newDocumentBuilder().parse(isource);
  
    org.w3c.dom.Element root = doc.getDocumentElement();
    NodeList nodes = null;
            
    if (root.getTagName() != null
        && root.getTagName().endsWith("schema")) {
      addTypes(root, pSoapDataTypes);
    } else if(root.getTagName()!=null && root.getTagName().endsWith("definitions")) {
      // search for 'types' element        
      Node node = null;     
      boolean found = false;
      nodes = root.getChildNodes();
      for (int i=0;  i < nodes.getLength();  i++) {
        node = nodes.item(i);
        if (node.getNodeName() != null &&
            node.getNodeName().endsWith("types")) {
          found=true;
          break;       
        }         
      }
      if (found) {
        nodes = node.getChildNodes();
        for(int i=0; i< nodes.getLength() ; i++ ){
          node = nodes.item(i);
          if (node.getNodeName()!=null && node.getNodeName().endsWith("schema")) {
            addTypes((org.w3c.dom.Element) node, pSoapDataTypes);
          }
        }//found      
      } else{
        throw new IllegalStateException("I can not handle this. The parameter passed in -doc's root is not the schema or definition");
      }
    }
  }

  public void parseString(String file, String name) throws Exception{
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(true);
    StringReader sr = new StringReader(soapDataTypes);
    Document doc = dbf.newDocumentBuilder().parse(new InputSource(sr));
    
    // To get better error messages, parse with a special ContentHandler first
    if (parseBySax) {
      parseBySax(file, name, doc);
    }

    if (parseByDom) {
      parseByDom(file, name, doc);
    }
  }

  private static final String soapDataTypes =
  "<?xml version='1.0' encoding='UTF-8' ?>\n" +
  "\n" +
  "<schema xmlns:tns='http://schemas.xmlsoap.org/soap/encoding/'\n" +
  "        targetNamespace='http://schemas.xmlsoap.org/soap/encoding/' \n" +
  " xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
  "        \n" +
  " <attribute name='root' >\n" +
  "   <annotation>\n" +
  "     <documentation>\n" +
  "    'root' can be used to distinguish serialization roots from other\n" +
  "       elements that are present in a serialization but are not roots of\n" +
  "       a serialized value graph \n" +
  "  </documentation>\n" +
  "   </annotation>\n" +
  "   <simpleType>\n" +
  "     <restriction base='boolean'>\n" +
  "    <pattern value='0|1' />\n" +
  "  </restriction>\n" +
  "   </simpleType>\n" +
  " </attribute>\n" +
  "\n" +
  "  <attributeGroup name='commonAttributes' >\n" +
  "    <annotation>\n" +
  "   <documentation>\n" +
  "     Attributes common to all elements that function as accessors or \n" +
  "        represent independent (multi-ref) values.  The href attribute is\n" +
  "        intended to be used in a manner like CONREF.  That is, the element\n" +
  "        content should be empty iff the href attribute appears\n" +
  "   </documentation>\n" +
  " </annotation>\n" +
  "    <attribute name='id' type='ID' />\n" +
  "    <attribute name='href' type='anyURI' />\n" +
  "    <anyAttribute namespace='##other' processContents='lax' />\n" +
  "  </attributeGroup>\n" +
  "\n" +
  " <simpleType name='arrayCoordinate' >\n" +
  "    <restriction base='string' />\n" +
  "  </simpleType>\n" +
  "          \n" +
  "  <attribute name='arrayType' type='string' />\n" +
  "  <attribute name='offset' type='tns:arrayCoordinate' />\n" +
  "  \n" +
  "  <attributeGroup name='arrayAttributes' >\n" +
  "    <attribute ref='tns:arrayType' />\n" +
  "    <attribute ref='tns:offset' />\n" +
  "  </attributeGroup>    \n" +
  "  \n" +
  "  <attribute name='position' type='tns:arrayCoordinate' /> \n" +
  "  \n" +
  "  <attributeGroup name='arrayMemberAttributes' >\n" +
  "    <attribute ref='tns:position' />\n" +
  "  </attributeGroup>    \n" +
  "\n" +
  "  <group name='Array' >\n" +
  "    <sequence>\n" +
  "      <any namespace='##any' minOccurs='0' maxOccurs='unbounded' processContents='lax' />\n" +
  " </sequence>\n" +
  "  </group>\n" +
  "\n" +
  "  <element name='Array' type='tns:Array' />\n" +
  "  <complexType name='Array' >\n" +
  "    <annotation>\n" +
  "   <documentation>\n" +
  "    'Array' is a complex type for accessors identified by position \n" +
  "   </documentation>\n" +
  " </annotation>\n" +
  "    <group ref='tns:Array' minOccurs='0' />\n" +
  "    <attributeGroup ref='tns:arrayAttributes' />\n" +
  "    <attributeGroup ref='tns:commonAttributes' />\n" +
  "  </complexType> \n" +
  "\n" +
  "  \n" +
  "  <element name='Struct' type='tns:Struct' />\n" +
  "\n" +
  "  <group name='Struct' >\n" +
  "    <sequence>\n" +
  "      <any namespace='##any' minOccurs='0' maxOccurs='unbounded' processContents='lax' />\n" +
  " </sequence>\n" +
  "  </group>\n" +
  "\n" +
  "  <complexType name='Struct' >\n" +
  "    <group ref='tns:Struct' minOccurs='0' />\n" +
  "    <attributeGroup ref='tns:commonAttributes'/>\n" +
  "  </complexType> \n" +
  "\n" +
  "\n" +
  "  <simpleType name='base64' >\n" +
  "    <restriction base='base64Binary' />\n" +
  "  </simpleType>\n" +
  "\n" +
  "\n" +
  "  <element name='duration' type='tns:duration' />\n" +
  "  <complexType name='duration' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='duration' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='dateTime' type='tns:dateTime' />\n" +
  "  <complexType name='dateTime' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='dateTime' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "\n" +
  "\n" +
  "  <element name='NOTATION' type='tns:NOTATION' />\n" +
  "  <complexType name='NOTATION' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='QName' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "  \n" +
  "\n" +
  "  <element name='time' type='tns:time' />\n" +
  "  <complexType name='time' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='time' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='date' type='tns:date' />\n" +
  "  <complexType name='date' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='date' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='gYearMonth' type='tns:gYearMonth' />\n" +
  "  <complexType name='gYearMonth' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='gYearMonth' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='gYear' type='tns:gYear' />\n" +
  "  <complexType name='gYear' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='gYear' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='gMonthDay' type='tns:gMonthDay' />\n" +
  "  <complexType name='gMonthDay' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='gMonthDay' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='gDay' type='tns:gDay' />\n" +
  "  <complexType name='gDay' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='gDay' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='gMonth' type='tns:gMonth' />\n" +
  "  <complexType name='gMonth' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='gMonth' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "  \n" +
  "  <element name='boolean' type='tns:boolean' />\n" +
  "  <complexType name='boolean' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='boolean' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='base64Binary' type='tns:base64Binary' />\n" +
  "  <complexType name='base64Binary' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='base64Binary' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='hexBinary' type='tns:hexBinary' />\n" +
  "  <complexType name='hexBinary' >\n" +
  "    <simpleContent>\n" +
  "     <extension base='hexBinary' >\n" +
  "       <attributeGroup ref='tns:commonAttributes' />\n" +
  "     </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='float' type='tns:float' />\n" +
  "  <complexType name='float' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='float' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='double' type='tns:double' />\n" +
  "  <complexType name='double' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='double' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='anyURI' type='tns:anyURI' />\n" +
  "  <complexType name='anyURI' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='anyURI' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='QName' type='tns:QName' />\n" +
  "  <complexType name='QName' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='QName' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  \n" +
  "  <element name='string' type='tns:string' />\n" +
  "  <complexType name='string' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='string' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='normalizedString' type='tns:normalizedString' />\n" +
  "  <complexType name='normalizedString' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='normalizedString' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='token' type='tns:token' />\n" +
  "  <complexType name='token' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='token' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='language' type='tns:language' />\n" +
  "  <complexType name='language' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='language' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='Name' type='tns:Name' />\n" +
  "  <complexType name='Name' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='Name' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='NMTOKEN' type='tns:NMTOKEN' />\n" +
  "  <complexType name='NMTOKEN' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='NMTOKEN' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='NCName' type='tns:NCName' />\n" +
  "  <complexType name='NCName' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='NCName' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='NMTOKENS' type='tns:NMTOKENS' />\n" +
  "  <complexType name='NMTOKENS' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='NMTOKENS' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='ID' type='tns:ID' />\n" +
  "  <complexType name='ID' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='ID' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='IDREF' type='tns:IDREF' />\n" +
  "  <complexType name='IDREF' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='IDREF' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='ENTITY' type='tns:ENTITY' />\n" +
  "  <complexType name='ENTITY' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='ENTITY' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='IDREFS' type='tns:IDREFS' />\n" +
  "  <complexType name='IDREFS' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='IDREFS' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='ENTITIES' type='tns:ENTITIES' />\n" +
  "  <complexType name='ENTITIES' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='ENTITIES' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='decimal' type='tns:decimal' />\n" +
  "  <complexType name='decimal' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='decimal' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='integer' type='tns:integer' />\n" +
  "  <complexType name='integer' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='integer' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='nonPositiveInteger' type='tns:nonPositiveInteger' />\n" +
  "  <complexType name='nonPositiveInteger' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='nonPositiveInteger' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='negativeInteger' type='tns:negativeInteger' />\n" +
  "  <complexType name='negativeInteger' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='negativeInteger' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='long' type='tns:long' />\n" +
  "  <complexType name='long' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='long' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='int' type='tns:int' />\n" +
  "  <complexType name='int' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='int' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='short' type='tns:short' />\n" +
  "  <complexType name='short' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='short' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='byte' type='tns:byte' />\n" +
  "  <complexType name='byte' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='byte' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='nonNegativeInteger' type='tns:nonNegativeInteger' />\n" +
  "  <complexType name='nonNegativeInteger' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='nonNegativeInteger' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='unsignedLong' type='tns:unsignedLong' />\n" +
  "  <complexType name='unsignedLong' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='unsignedLong' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='unsignedInt' type='tns:unsignedInt' />\n" +
  "  <complexType name='unsignedInt' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='unsignedInt' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='unsignedShort' type='tns:unsignedShort' />\n" +
  "  <complexType name='unsignedShort' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='unsignedShort' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='unsignedByte' type='tns:unsignedByte' />\n" +
  "  <complexType name='unsignedByte' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='unsignedByte' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  "  <element name='positiveInteger' type='tns:positiveInteger' />\n" +
  "  <complexType name='positiveInteger' >\n" +
  "    <simpleContent>\n" +
  "      <extension base='positiveInteger' >\n" +
  "        <attributeGroup ref='tns:commonAttributes' />\n" +
  "      </extension>\n" +
  "    </simpleContent>\n" +
  "  </complexType>\n" +
  "\n" +
  " </schema>\n";
  
  private static final String addressBook =
  "<?xml version='1.0' ?>\n" +
  "\n" +
  "<definitions name='urn:AddrNoImplSEI'\n" +
  "             targetNamespace='urn:AddrNoImplSEI'\n" +
  "             xmlns:tns='urn:AddrNoImplSEI'\n" +
  "             xmlns:typens='urn:AddrNoImplSEI'\n" +
  "             xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n" +
  "             xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'\n" +
  "             xmlns:soap-enc='http://schemas.xmlsoap.org/soap/encoding/'\n" +
  "             xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'\n" +
  "             xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "\n" +
  "  <!-- type defs -->\n" +
  "  <types>\n" +
  "    <xsd:schema targetNamespace='urn:AddrNoImplSEI'\n" +
  "                xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
  "\n" +
  "      <xsd:simpleType name='StateType'>\n" +
  "        <xsd:restriction base='xsd:string'>\n" +
  "            <xsd:enumeration value='TX'/>                      \n" +
  "            <xsd:enumeration value='IN'/>                    \n" +
  "            <xsd:enumeration value='OH'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:element name='Phone'>\n" +
  "        <xsd:complexType>\n" +
  "          <xsd:all>\n" +
  "              <xsd:element name='areaCode' type='xsd:int'/>\n" +
  "              <xsd:element name='exchange' type='xsd:string'/>\n" +
  "              <xsd:element name='number' type='xsd:string'/>\n" +
  "          </xsd:all>\n" +
  "        </xsd:complexType>\n" +
  "      </xsd:element>\n" +
  "\n" +
  "      <xsd:complexType name='Address'>\n" +
  "        <xsd:all>\n" +
  "            <xsd:element name='streetNum' type='xsd:int'/>\n" +
  "            <xsd:element name='streetName' type='xsd:string'/>\n" +
  "            <xsd:element name='city' type='xsd:string'/>\n" +
  "            <xsd:element name='state' type='typens:StateType'/>\n" +
  "            <xsd:element name='zip' type='xsd:int'/>\n" +
  "            <xsd:element ref='typens:Phone'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='ArrayOfaddress'>\n" +
  "         <xsd:complexContent>\n" +
  "            <xsd:restriction base='soap-enc:Array'>\n" +
  "                <xsd:attribute ref='soap-enc:arrayType' wsdl:arrayType='typens:Address[]'/>\n" +
  "            </xsd:restriction>\n" +
  "         </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "    </xsd:schema>\n" +
  "  </types>\n" +
  "\n" +
  "  <!-- message declns -->\n" +
  "  <message name='AddEntryRequest'>\n" +
  "    <part name='name' type='xsd:string'/>\n" +
  "    <part name='address' type='typens:Address'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='GetAddressFromNameRequest'>\n" +
  "    <part name='name' type='xsd:string'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='GetAddressFromNameResponse'>\n" +
  "    <part name='address' type='typens:Address'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='getAddressesRequest'>\n" +
  "  </message>  \n" +
  "\n" +
  "  <message name='getAddressesResponse'>\n" +
  "    <part name='addresses' type='typens:ArrayOfaddress'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <!-- port type declns -->\n" +
  "  <portType name='AddressBookNoImplSEI'>\n" +
  "    <operation name='addEntry'>\n" +
  "      <input message='tns:AddEntryRequest'/>\n" +
  "    </operation>\n" +
  "    <operation name='getAddressFromName'>\n" +
  "      <input message='tns:GetAddressFromNameRequest'/>\n" +
  "      <output message='tns:GetAddressFromNameResponse'/>\n" +
  "    </operation>\n" +
  "    <operation name='getAddresses'>\n" +
  "      <input message='tns:getAddressesRequest'/>\n" +
  "      <output message='tns:getAddressesResponse'/>\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "\n" +
  "  <!-- binding declns -->\n" +
  "  <binding name='AddressBookNoImplSEISoapBinding' type='tns:AddressBookNoImplSEI'>\n" +
  "    <soap:binding style='rpc'\n" +
  "                  transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='addEntry'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddrNoImplSEI'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddrNoImplSEI'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='getAddressFromName'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddrNoImplSEI'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddrNoImplSEI'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='getAddresses'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddressFetcher2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:AddressFetcher2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "\n" +
  "  <!-- service decln -->\n" +
  "  <service name='AddressBookNoImplSEIService'>\n" +
  "    <port name='AddressBookNoImplSEI' binding='tns:AddressBookNoImplSEISoapBinding'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/AddressBookNoImplSEI'/>\n" +
  "    </port>\n" +
  "  </service>\n" +
  "\n" +
  "</definitions>\n";

  public void testAddressBook() throws Exception {
    parseString(addressBook, "addressBook.xsd");
  }

  private final String clash =
  "<?xml version='1.0' ?>\n" +
  "\n" +
  "<definitions \n" +
  "    name='name clash test'\n" +
  "    targetNamespace='urn:clash.wsdl.test'\n" +
  "    xmlns:tns='urn:clash.wsdl.test'\n" +
  "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n" +
  "    xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'\n" +
  "    xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/'\n" +
  "    xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'\n" +
  "    xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "\n" +
  "  <!-- type defs -->\n" +
  "  <types>\n" +
  "    <xsd:schema targetNamespace='urn:clash.wsdl.test'\n" +
  "                xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
  "      <xsd:complexType name='sharedName'>\n" +
  "        <xsd:all>\n" +
  "          <xsd:element name='sharedName' type='xsd:int'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:element name='sharedName'>\n" +
  "        <xsd:complexType>\n" +
  "          <xsd:all>\n" +
  "            <xsd:element name='sharedName' type='xsd:int'/>\n" +
  "          </xsd:all>\n" +
  "        </xsd:complexType>\n" +
  "      </xsd:element>\n" +
  "      <xsd:element name='another'>\n" +
  "        <xsd:complexType>\n" +
  "          <xsd:all>\n" +
  "            <xsd:element name='sharedName' type='xsd:boolean'/>\n" +
  "          </xsd:all>\n" +
  "        </xsd:complexType>\n" +
  "      </xsd:element>\n" +
  "    </xsd:schema>\n" +
  "  </types>\n" +
  "\n" +
  "  <!-- message declns -->\n" +
  "  <message name='empty'/>\n" +
  "\n" +
  "  <message name='sharedName'>\n" +
  "    <part name='sharedName' type='tns:sharedName'/>\n" +
  "  </message>\n" +
  "  <message name='anotherMessage'>\n" +
  "    <part name='sharedName' type='xsd:int'/>\n" +
  "  </message>\n" +
  "  <message name='literalMessage'>\n" +
  "    <part name='literalPart' element='tns:sharedName'/>\n" +
  "  </message>\n" +
  "  <message name='anotherLitMessage'>\n" +
  "    <part name='sharedName' element='tns:another'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <!-- port type declns -->\n" +
  "  <portType name='sharedName'>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn' message='tns:empty'/>\n" +
  "      <output name='sharedNameOut' message='tns:sharedName'/>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn2' message='tns:sharedName'/>\n" +
  "      <output name='emptyOut2' message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='anotherIn3' message='tns:anotherMessage'/>\n" +
  "      <output name='emptyOut3' message='tns:empty'/>\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "\n" +
  "  <portType name='literalPort'>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn' message='tns:empty'/>\n" +
  "      <output name='sharedNameOut' message='tns:literalMessage'/>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn2' message='tns:literalMessage'/>\n" +
  "      <output name='emptyOut2' message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='anotherIn3' message='tns:anotherLitMessage'/>\n" +
  "      <output name='emptyOut3' message='tns:empty'/>\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "\n" +
  "  <!-- binding declns -->\n" +
  "  <binding name='nonSharedName' type='tns:sharedName'>\n" +
  "    <soap:binding\n" +
  "        style='rpc'\n" +
  "        transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='sharedNameOut'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn2'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut2'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='anotherIn3'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut3'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "\n" +
  "<!-- don't do this one just yet...\n" +
  "  <binding name='sharedName' type='tns:literalPort'>\n" +
  "    <soap:binding\n" +
  "        style='rpc'\n" +
  "        transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </input>\n" +
  "      <output name='sharedNameOut'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn2'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut2'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='anotherIn3'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut3'>\n" +
  "        <soap:body use='literal'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "-->\n" +
  "\n" +
  "  <binding name='anotherNonSharedName' type='tns:sharedName'>\n" +
  "    <soap:binding\n" +
  "        style='rpc'\n" +
  "        transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='sharedNameOut'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='sharedNameIn2'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut2'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='sharedName'>\n" +
  "      <input name='anotherIn3'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </input>\n" +
  "      <output name='emptyOut3'>\n" +
  "        <soap:body use='encoded'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "\n" +
  "  <!-- service decln -->\n" +
  "  <service name='sharedName'>\n" +
  "    <port name='nonSharedName' binding='tns:nonSharedName'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/nonSharedName'/>\n" +
  "    </port>\n" +
  "    <port name='anotherNonSharedName' binding='tns:anotherNonSharedName'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/anotherNonSharedName'/>\n" +
  "    </port>\n" +
  "<!-- don't do this just yet...\n" +
  "    <port name='sharedName' binding='tns:sharedName'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/sharedName'/>\n" +
  "    </port>\n" +
  "-->\n" +
  "  </service>\n" +
  "\n" +
  "</definitions>\n";

  public void testClash() throws Exception {
    parseString(clash, "clash.xsd");
  }

  private static final String getPlan =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<definitions xmlns:http='http://schemas.xmlsoap.org/wsdl/http/'\n" +
  "    xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'\n" +
  "    xmlns:s='http://www.w3.org/2001/XMLSchema'\n" +
  "    xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/'\n" +
  "    xmlns:tm='http://microsoft.com/wsdl/mime/textMatching/'\n" +
  "    xmlns:s0='http://tempuri.org/'\n" +
  "    xmlns:mime='http://schemas.xmlsoap.org/wsdl/mime/'\n" +
  "    targetNamespace='http://tempuri.org/' xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "  <types>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://tempuri.org/'>\n" +
  "      <s:element name='GetPlan'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "      <s:element name='GetPlanResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='GetPlanResult'\n" +
  "                type='s0:Plan'/>\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "\n" +
  "      <s:complexType name='Plan'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='Disposition'\n" +
  "              type='s0:Disposition'/>\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "\n" +
  "      <s:complexType name='Disposition'>\n" +
  "        <s:complexContent mixed='false'>\n" +
  "          <s:extension base='s0:Status' />\n" +
  "        </s:complexContent>\n" +
  "      </s:complexType>\n" +
  "\n" +
  "      <s:complexType name='Status'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='Code' type='s:string' />\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='Description' \n" +
  "              type='s:string'/>\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "\n" +
  "      <s:element name='Plan' nillable='true' type='s0:Plan' />\n" +
  "    </s:schema>\n" +
  "  </types>\n" +
  "  <message name='GetPlanSoapIn'>\n" +
  "    <part name='parameters' element='s0:GetPlan' />\n" +
  "  </message>\n" +
  "  <message name='GetPlanSoapOut'>\n" +
  "    <part name='parameters' element='s0:GetPlanResponse' />\n" +
  "  </message>\n" +
  "  <message name='GetPlanHttpGetIn' />\n" +
  "  <message name='GetPlanHttpGetOut'>\n" +
  "    <part name='Body' element='s0:Plan' />\n" +
  "  </message>\n" +
  "  <message name='GetPlanHttpPostIn' />\n" +
  "  <message name='GetPlanHttpPostOut'>\n" +
  "    <part name='Body' element='s0:Plan' />\n" +
  "  </message>\n" +
  "  <portType name='PlanWSSoap'>\n" +
  "    <operation name='GetPlan'>\n" +
  "      <input message='s0:GetPlanSoapIn' />\n" +
  "      <output message='s0:GetPlanSoapOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <portType name='PlanWSHttpGet'>\n" +
  "    <operation name='GetPlan'>\n" +
  "      <input message='s0:GetPlanHttpGetIn' />\n" +
  "      <output message='s0:GetPlanHttpGetOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <portType name='PlanWSHttpPost'>\n" +
  "    <operation name='GetPlan'>\n" +
  "      <input message='s0:GetPlanHttpPostIn' />\n" +
  "      <output message='s0:GetPlanHttpPostOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <binding name='PlanWSSoap' type='s0:PlanWSSoap'>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' \n" +
  "        style='document'/>\n" +
  "    <operation name='GetPlan'>\n" +
  "      <soap:operation soapAction='http://tempuri.org/GetPlan' \n" +
  "          style='document'/>\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <binding name='PlanWSHttpGet' type='s0:PlanWSHttpGet'>\n" +
  "    <http:binding verb='GET' />\n" +
  "    <operation name='GetPlan'>\n" +
  "      <http:operation location='/GetPlan' />\n" +
  "      <input>\n" +
  "        <http:urlEncoded />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <mime:mimeXml part='Body' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <binding name='PlanWSHttpPost' type='s0:PlanWSHttpPost'>\n" +
  "    <http:binding verb='POST' />\n" +
  "    <operation name='GetPlan'>\n" +
  "      <http:operation location='/GetPlan' />\n" +
  "      <input>\n" +
  "        <mime:content type='application/x-www-form-urlencoded' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <mime:mimeXml part='Body' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <service name='PlanWS'>\n" +
  "    <port name='PlanWSSoap' binding='s0:PlanWSSoap'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/PlanWSSoap' />\n" +
  "    </port>\n" +
  "    <port name='PlanWSHttpGet' binding='s0:PlanWSHttpGet'>\n" +
  "      <http:address location='http://localhost:8080/axis/services/PlanWSSoap' />\n" +
  "    </port>\n" +
  "    <port name='PlanWSHttpPost' binding='s0:PlanWSHttpPost'>\n" +
  "      <http:address location='http://localhost:8080/axis/services/PlanWSSoap' />\n" +
  "    </port>\n" +
  "  </service>\n" +
  "</definitions>\n" +
  "\n";

  public void testGetPlan() throws Exception {
    parseString(getPlan, "getPlan.xsd");
  }

  private String adaptive =
  "<?xml version='1.0' encoding='UTF-8'?>\n" +
  "\n" +
  "<definitions name='Adaptive' targetNamespace='http://com.test/wsdl/Adaptive' xmlns:tns='http://com.test/wsdl/Adaptive' xmlns='http://schemas.xmlsoap.org/wsdl/' xmlns:ns2='http://com.test/types/Adaptive' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'>\n" +
  "  <types>\n" +
  "    <schema targetNamespace='http://com.test/types/Adaptive' xmlns:tns='http://com.test/types/Adaptive' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:ns2='http://java.sun.com/jax-rpc-ri/internal' xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/' xmlns:soap-enc='http://schemas.xmlsoap.org/soap/encoding/' xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
  "      <import namespace='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      <import namespace='http://java.sun.com/jax-rpc-ri/internal'/>\n" +
  "      <complexType name='ArrayOfResourceInfo'>\n" +
  "        <complexContent>\n" +
  "          <restriction base='soap-enc:Array'>\n" +
  "            <attribute ref='soap-enc:arrayType' wsdl:arrayType='tns:ResourceInfo[]'/></restriction></complexContent></complexType>\n" +
  "      <complexType name='ResourceInfo'>\n" +
  "        <sequence>\n" +
  "          <element name='properties' type='ns2:vector'/>\n" +
  "          <element name='id' type='string'/></sequence></complexType>\n" +
  "      <complexType name='ApplicationInfo'>\n" +
  "        <sequence>\n" +
  "          <element name='dummy' type='tns:KeyValue'/>\n" +
  "          <element name='properties' type='ns2:vector'/>\n" +
  "          <element name='id' type='string'/></sequence></complexType>\n" +
  "      <complexType name='KeyValue'>\n" +
  "        <sequence>\n" +
  "          <element name='key' type='string'/>\n" +
  "          <element name='value' type='string'/></sequence></complexType>\n" +
  "      <complexType name='ArrayOfint'>\n" +
  "        <complexContent>\n" +
  "          <restriction base='soap-enc:Array'>\n" +
  "            <attribute ref='soap-enc:arrayType' wsdl:arrayType='int[]'/></restriction></complexContent></complexType>\n" +
  "      <complexType name='ArrayOfstring'>\n" +
  "        <complexContent>\n" +
  "          <restriction base='soap-enc:Array'>\n" +
  "            <attribute ref='soap-enc:arrayType' wsdl:arrayType='string[]'/></restriction></complexContent></complexType>\n" +
  "      <complexType name='ArrayOfArrayOfstring'>\n" +
  "        <complexContent>\n" +
  "          <restriction base='soap-enc:Array'>\n" +
  "            <attribute ref='soap-enc:arrayType' wsdl:arrayType='tns:ArrayOfstring[]'/></restriction></complexContent></complexType></schema>\n" +
  "    <schema targetNamespace='http://java.sun.com/jax-rpc-ri/internal' xmlns:tns='http://java.sun.com/jax-rpc-ri/internal' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/' xmlns:soap-enc='http://schemas.xmlsoap.org/soap/encoding/' xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
  "      <import namespace='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      <import namespace='http://com.test/types/Adaptive'/>\n" +
  "      <complexType name='vector'>\n" +
  "        <complexContent>\n" +
  "          <extension base='tns:list'>\n" +
  "            <sequence/></extension></complexContent></complexType>\n" +
  "      <complexType name='list'>\n" +
  "        <complexContent>\n" +
  "          <extension base='tns:collection'>\n" +
  "            <sequence/></extension></complexContent></complexType>\n" +
  "      <complexType name='collection'>\n" +
  "        <complexContent>\n" +
  "          <restriction base='soap-enc:Array'>\n" +
  "            <attribute ref='soap-enc:arrayType' wsdl:arrayType='anyType[]'/></restriction></complexContent></complexType></schema></types>\n" +
  "  <message name='AdaptiveInterface_getServiceDescription'/>\n" +
  "  <message name='AdaptiveInterface_getServiceDescriptionResponse'>\n" +
  "    <part name='result' type='xsd:string'/></message>\n" +
  "  <message name='AdaptiveInterface_rankResources'>\n" +
  "    <part name='arrayOfResourceInfo_1' type='ns2:ArrayOfResourceInfo'/>\n" +
  "    <part name='ApplicationInfo_2' type='ns2:ApplicationInfo'/></message>\n" +
  "  <message name='AdaptiveInterface_rankResourcesResponse'>\n" +
  "    <part name='result' type='ns2:ArrayOfResourceInfo'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateTransferTime'>\n" +
  "    <part name='boolean_1' type='xsd:boolean'/>\n" +
  "    <part name='ResourceInfo_2' type='ns2:ResourceInfo'/>\n" +
  "    <part name='arrayOfResourceInfo_3' type='ns2:ArrayOfResourceInfo'/>\n" +
  "    <part name='long_4' type='xsd:long'/>\n" +
  "    <part name='Calendar_5' type='xsd:dateTime'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateTransferTimeResponse'>\n" +
  "    <part name='result' type='ns2:ArrayOfint'/></message>\n" +
  "  <message name='AdaptiveInterface_logDataTransfer'>\n" +
  "    <part name='ResourceInfo_1' type='ns2:ResourceInfo'/>\n" +
  "    <part name='ResourceInfo_2' type='ns2:ResourceInfo'/>\n" +
  "    <part name='long_3' type='xsd:long'/>\n" +
  "    <part name='Calendar_4' type='xsd:dateTime'/>\n" +
  "    <part name='Calendar_5' type='xsd:dateTime'/></message>\n" +
  "  <message name='AdaptiveInterface_logDataTransferResponse'/>\n" +
  "  <message name='AdaptiveInterface_estimateUsage'>\n" +
  "    <part name='boolean_1' type='xsd:boolean'/>\n" +
  "    <part name='ResourceInfo_2' type='ns2:ResourceInfo'/>\n" +
  "    <part name='String_3' type='xsd:string'/>\n" +
  "    <part name='int_4' type='xsd:int'/>\n" +
  "    <part name='Calendar_5' type='xsd:dateTime'/>\n" +
  "    <part name='Calendar_6' type='xsd:dateTime'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateUsageResponse'>\n" +
  "    <part name='result' type='xsd:string'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateMultipleUsage'>\n" +
  "    <part name='boolean_1' type='xsd:boolean'/>\n" +
  "    <part name='arrayOfResourceInfo_2' type='ns2:ArrayOfResourceInfo'/>\n" +
  "    <part name='arrayOfString_3' type='ns2:ArrayOfstring'/>\n" +
  "    <part name='int_4' type='xsd:int'/>\n" +
  "    <part name='Calendar_5' type='xsd:dateTime'/>\n" +
  "    <part name='Calendar_6' type='xsd:dateTime'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateMultipleUsageResponse'>\n" +
  "    <part name='result' type='ns2:ArrayOfArrayOfstring'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateNetworkGraph'>\n" +
  "    <part name='boolean_1' type='xsd:boolean'/>\n" +
  "    <part name='arrayOfResourceInfo_2' type='ns2:ArrayOfResourceInfo'/>\n" +
  "    <part name='int_3' type='xsd:int'/>\n" +
  "    <part name='Calendar_4' type='xsd:dateTime'/>\n" +
  "    <part name='Calendar_5' type='xsd:dateTime'/></message>\n" +
  "  <message name='AdaptiveInterface_estimateNetworkGraphResponse'>\n" +
  "    <part name='result' type='ns2:ArrayOfArrayOfstring'/></message>\n" +
  "  <portType name='AdaptiveInterface'>\n" +
  "    <operation name='getServiceDescription' parameterOrder=''>\n" +
  "      <input message='tns:AdaptiveInterface_getServiceDescription'/>\n" +
  "      <output message='tns:AdaptiveInterface_getServiceDescriptionResponse'/></operation>\n" +
  "    <operation name='rankResources' parameterOrder='arrayOfResourceInfo_1 ApplicationInfo_2'>\n" +
  "      <input message='tns:AdaptiveInterface_rankResources'/>\n" +
  "      <output message='tns:AdaptiveInterface_rankResourcesResponse'/></operation>\n" +
  "    <operation name='estimateTransferTime' parameterOrder='boolean_1 ResourceInfo_2 arrayOfResourceInfo_3 long_4 Calendar_5'>\n" +
  "      <input message='tns:AdaptiveInterface_estimateTransferTime'/>\n" +
  "      <output message='tns:AdaptiveInterface_estimateTransferTimeResponse'/></operation>\n" +
  "    <operation name='logDataTransfer' parameterOrder='ResourceInfo_1 ResourceInfo_2 long_3 Calendar_4 Calendar_5'>\n" +
  "      <input message='tns:AdaptiveInterface_logDataTransfer'/>\n" +
  "      <output message='tns:AdaptiveInterface_logDataTransferResponse'/></operation>\n" +
  "    <operation name='estimateUsage' parameterOrder='boolean_1 ResourceInfo_2 String_3 int_4 Calendar_5 Calendar_6'>\n" +
  "      <input message='tns:AdaptiveInterface_estimateUsage'/>\n" +
  "      <output message='tns:AdaptiveInterface_estimateUsageResponse'/></operation>\n" +
  "    <operation name='estimateMultipleUsage' parameterOrder='boolean_1 arrayOfResourceInfo_2 arrayOfString_3 int_4 Calendar_5 Calendar_6'>\n" +
  "      <input message='tns:AdaptiveInterface_estimateMultipleUsage'/>\n" +
  "      <output message='tns:AdaptiveInterface_estimateMultipleUsageResponse'/></operation>\n" +
  "    <operation name='estimateNetworkGraph' parameterOrder='boolean_1 arrayOfResourceInfo_2 int_3 Calendar_4 Calendar_5'>\n" +
  "      <input message='tns:AdaptiveInterface_estimateNetworkGraph'/>\n" +
  "      <output message='tns:AdaptiveInterface_estimateNetworkGraphResponse'/></operation></portType>\n" +
  "  <binding name='AdaptiveInterfaceBinding' type='tns:AdaptiveInterface'>\n" +
  "    <operation name='getServiceDescription'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='rankResources'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='estimateTransferTime'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='logDataTransfer'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='estimateUsage'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='estimateMultipleUsage'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <operation name='estimateNetworkGraph'>\n" +
  "      <input>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></input>\n" +
  "      <output>\n" +
  "        <soap:body encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' use='encoded' namespace='http://com.test/wsdl/Adaptive'/></output>\n" +
  "      <soap:operation soapAction=''/></operation>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' style='rpc'/></binding>\n" +
  "  <service name='AdaptiveService'>\n" +
  "    <port name='Adaptive' binding='tns:AdaptiveInterfaceBinding'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/Adaptive'/></port></service></definitions>\n";

  public void testAdaptive() throws Exception {
    parseString(adaptive, "adaptive.xsd");
  }

  private String dataService =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<definitions xmlns:http='http://schemas.xmlsoap.org/wsdl/http/' xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:s0='http://tempuri.org/' xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/' xmlns:tm='http://microsoft.com/wsdl/mime/textMatching/' xmlns:mime='http://schemas.xmlsoap.org/wsdl/mime/' targetNamespace='http://tempuri.org/' xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "  <types>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://tempuri.org/'>\n" +
  "      <s:import namespace='http://www.w3.org/2001/XMLSchema' />\n" +
  "      <s:element name='GetTitleAuthors'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "      <s:element name='GetTitleAuthorsResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='GetTitleAuthorsResult'>\n" +
  "              <s:complexType>\n" +
  "                <s:sequence>\n" +
  "                  <s:element ref='s:schema' />\n" +
  "                  <s:any />\n" +
  "                </s:sequence>\n" +
  "              </s:complexType>\n" +
  "            </s:element>\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='PutTitleAuthors'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='DS'>\n" +
  "              <s:complexType>\n" +
  "                <s:sequence>\n" +
  "                  <s:element ref='s:schema' />\n" +
  "                  <s:any />\n" +
  "                </s:sequence>\n" +
  "              </s:complexType>\n" +
  "            </s:element>\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='PutTitleAuthorsResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='PutTitleAuthorsResult' type='s:int' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='DataSet' nillable='true'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element ref='s:schema' />\n" +
  "            <s:any />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "    </s:schema>\n" +
  "  </types>\n" +
  "  <message name='GetTitleAuthorsSoapIn'>\n" +
  "    <part name='parameters' element='s0:GetTitleAuthors' />\n" +
  "  </message>\n" +
  "  <message name='GetTitleAuthorsSoapOut'>\n" +
  "    <part name='parameters' element='s0:GetTitleAuthorsResponse' />\n" +
  "  </message>\n" +
  "  <message name='PutTitleAuthorsSoapIn'>\n" +
  "    <part name='parameters' element='s0:PutTitleAuthors' />\n" +
  "  </message>\n" +
  "  <message name='PutTitleAuthorsSoapOut'>\n" +
  "    <part name='parameters' element='s0:PutTitleAuthorsResponse' />\n" +
  "  </message>\n" +
  "  <message name='GetTitleAuthorsHttpGetIn' />\n" +
  "  <message name='GetTitleAuthorsHttpGetOut'>\n" +
  "    <part name='Body' element='s0:DataSet' />\n" +
  "  </message>\n" +
  "  <message name='GetTitleAuthorsHttpPostIn' />\n" +
  "  <message name='GetTitleAuthorsHttpPostOut'>\n" +
  "    <part name='Body' element='s0:DataSet' />\n" +
  "  </message>\n" +
  "  <portType name='DataServiceSoap'>\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <input message='s0:GetTitleAuthorsSoapIn' />\n" +
  "      <output message='s0:GetTitleAuthorsSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='PutTitleAuthors'>\n" +
  "      <input message='s0:PutTitleAuthorsSoapIn' />\n" +
  "      <output message='s0:PutTitleAuthorsSoapOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <portType name='DataServiceHttpGet'>\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <input message='s0:GetTitleAuthorsHttpGetIn' />\n" +
  "      <output message='s0:GetTitleAuthorsHttpGetOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <portType name='DataServiceHttpPost'>\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <input message='s0:GetTitleAuthorsHttpPostIn' />\n" +
  "      <output message='s0:GetTitleAuthorsHttpPostOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <binding name='DataServiceSoap' type='s0:DataServiceSoap'>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' style='document' />\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <soap:operation soapAction='http://tempuri.org/GetTitleAuthors' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='PutTitleAuthors'>\n" +
  "      <soap:operation soapAction='http://tempuri.org/PutTitleAuthors' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <binding name='DataServiceHttpGet' type='s0:DataServiceHttpGet'>\n" +
  "    <http:binding verb='GET' />\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <http:operation location='/GetTitleAuthors' />\n" +
  "      <input>\n" +
  "        <http:urlEncoded />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <mime:mimeXml part='Body' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <binding name='DataServiceHttpPost' type='s0:DataServiceHttpPost'>\n" +
  "    <http:binding verb='POST' />\n" +
  "    <operation name='GetTitleAuthors'>\n" +
  "      <http:operation location='/GetTitleAuthors' />\n" +
  "      <input>\n" +
  "        <mime:content type='application/x-www-form-urlencoded' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <mime:mimeXml part='Body' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <service name='DataService'>\n" +
  "    <port name='DataServiceSoap' binding='s0:DataServiceSoap'>\n" +
  "      <soap:address location='http://samples.gotdotnet.com/quickstart/aspplus/samples/services/DataService/VB/DataService.asmx' />\n" +
  "    </port>\n" +
  "    <port name='DataServiceHttpGet' binding='s0:DataServiceHttpGet'>\n" +
  "      <http:address location='http://samples.gotdotnet.com/quickstart/aspplus/samples/services/DataService/VB/DataService.asmx' />\n" +
  "    </port>\n" +
  "    <port name='DataServiceHttpPost' binding='s0:DataServiceHttpPost'>\n" +
  "      <http:address location='http://samples.gotdotnet.com/quickstart/aspplus/samples/services/DataService/VB/DataService.asmx' />\n" +
  "    </port>\n" +
  "  </service>\n" +
  "</definitions>\n";

  public void testDataService() throws Exception {
    parseString(dataService, "dataService.xsd");
  }

  private static final String thing =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<definitions xmlns:http='http://schemas.xmlsoap.org/wsdl/http/' \n" +
  "xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' \n" +
  "xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:s0='urn:Thing' \n" +
  "xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/' \n" +
  "xmlns:tm='http://microsoft.com/wsdl/mime/textMatching/' \n" +
  "xmlns:mime='http://schemas.xmlsoap.org/wsdl/mime/' \n" +
  "targetNamespace='urn:Thing' xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "<!--\n" +
  "\n" +
  "This test checks out the emitting of holders for container elements\n" +
  "(arrays) that are inside elements.\n" +
  "\n" +
  "-->\n" +
  "\n" +
  "\n" +
  "  <types>\n" +
  "      <s:schema elementFormDefault='qualified' targetNamespace='urn:Thing'>\n" +
  "          <s:element name='aThing' type='s0:aThing' final='restriction'/>\n" +
  "          <s:simpleType name='aThing'>\n" +
  "              <s:restriction base='s:string'>\n" +
  "                  <s:maxLength value='255'/>\n" +
  "              </s:restriction>\n" +
  "          </s:simpleType>\n" +
  "          <s:element name='otherData' type='s0:otherData' final='restriction'/>\n" +
  "          <s:simpleType name='otherData'>\n" +
  "              <s:restriction base='s:string'>\n" +
  "                  <s:maxLength value='1024'/>\n" +
  "              </s:restriction>\n" +
  "          </s:simpleType>\n" +
  "          <s:element name='find_aThing' type='s0:find_aThing' final='restriction'/>\n" +
  "          <s:complexType name='find_aThing' final='restriction'>\n" +
  "              <s:sequence>\n" +
  "                  <s:element ref='s0:aThing' maxOccurs='1'/>\n" +
  "              </s:sequence>\n" +
  "          </s:complexType>\n" +
  "          <s:element name='AThingResponse' type='s0:AThingResponse' final='restriction'/>\n" +
  "          <s:complexType name='AThingResponse' final='restriction'>\n" +
  "              <s:sequence>\n" +
  "                  <s:element ref='s0:aThing' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "                  <s:element ref='s0:otherData' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "              </s:sequence>\n" +
  "          </s:complexType>\n" +
  "      </s:schema>\n" +
  "  </types>\n" +
  "  <message name='ThingIn'>\n" +
  "    <part name='parameters' element='s0:find_aThing' />\n" +
  "  </message>\n" +
  "  <message name='ThingOut'>\n" +
  "    <part name='parameters' element='s0:AThingResponse' />\n" +
  "  </message>\n" +
  "  <portType name='Thing'>\n" +
  "    <operation name='find_aThing'>\n" +
  "      <input message='s0:ThingIn' />\n" +
  "      <output message='s0:ThingOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <binding name='Thing' type='s0:Thing'>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' style='document' />\n" +
  "    <operation name='find_aThing'>\n" +
  "      <soap:operation soapAction='http://tempuri.org/find_aThing' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <service name='wrapped_holders'>\n" +
  "    <port name='Thing' binding='s0:Thing'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/Thing' />\n" +
  "    </port>\n" +
  "  </service>\n" +
  "</definitions>\n" +
  "\n";

  public void testThing() throws Exception {
    parseString(thing, "thing.xsd");
  }

  private final String soap12Test =
  "<?xml version='1.0'?>\n" +
  "\n" +
  "<definitions name='SOAP-12-TestDefinitions'\n" +
  " targetNamespace='http://whitemesa.net/wsdl/soap12-test'\n" +
  " xmlns='http://schemas.xmlsoap.org/wsdl/'\n" +
  " xmlns:soap12='http://schemas.xmlsoap.org/wsdl/soap12/'\n" +
  " xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n" +
  " xmlns:enc='http://www.w3.org/2003/05/soap-encoding'\n" +
  " xmlns:tns='http://whitemesa.net/wsdl/soap12-test'\n" +
  " xmlns:types='http://example.org/ts-tests/xsd'\n" +
  " xmlns:test='http://example.org/ts-tests'\n" +
  " xmlns:xlink='http://www.w3.org/1999/xlink'\n" +
  " xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'>\n" +
  " <types>\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' targetNamespace='http://example.org/ts-tests/xsd'>\n" +
  "\n" +
  "     <import namespace='http://www.w3.org/2003/05/soap-encoding' />\n" +
  "\n" +
  "     <complexType name='ArrayOfstring'>\n" +
  "       <complexContent>\n" +
  "         <restriction base='enc:Array'>\n" +
  "           <sequence>\n" +
  "             <element name='item' type='xsd:string' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "           </sequence>\n" +
  "             <attributeGroup ref='enc:arrayAttributes' /> \n" +
  "             <attributeGroup ref='enc:commonAttributes' /> \n" +
  "         </restriction>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <complexType name='ArrayOfint'>\n" +
  "       <complexContent>\n" +
  "         <restriction base='enc:Array'>\n" +
  "           <sequence>\n" +
  "             <element name='item' type='xsd:int' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "           </sequence>\n" +
  "             <attributeGroup ref='enc:arrayAttributes' /> \n" +
  "             <attributeGroup ref='enc:commonAttributes' /> \n" +
  "         </restriction>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <complexType name='ArrayOffloat'>\n" +
  "       <complexContent>\n" +
  "         <restriction base='enc:Array'>\n" +
  "           <sequence>\n" +
  "             <element name='item' type='xsd:float' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "           </sequence>\n" +
  "             <attributeGroup ref='enc:arrayAttributes' /> \n" +
  "             <attributeGroup ref='enc:commonAttributes' /> \n" +
  "         </restriction>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <complexType name='ArrayOfSOAPStruct'>\n" +
  "       <complexContent>\n" +
  "         <restriction base='enc:Array'>\n" +
  "           <sequence>\n" +
  "             <element name='item' type='types:SOAPStruct' minOccurs='0' maxOccurs='unbounded'/>\n" +
  "           </sequence>\n" +
  "             <attributeGroup ref='enc:arrayAttributes' /> \n" +
  "             <attributeGroup ref='enc:commonAttributes' /> \n" +
  "         </restriction>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <complexType name='SOAPStruct'>\n" +
  "       <all>\n" +
  "         <element name='varString' type='xsd:string'/>\n" +
  "         <element name='varInt' type='xsd:int'/>\n" +
  "         <element name='varFloat' type='xsd:float'/>\n" +
  "       </all>\n" +
  "     </complexType>\n" +
  "     <complexType name='SOAPStructStruct'>\n" +
  "       <all>\n" +
  "         <element name='varString' type='xsd:string'/>\n" +
  "         <element name='varInt' type='xsd:int'/>\n" +
  "         <element name='varFloat' type='xsd:float'/>\n" +
  "         <element name='varStruct' type='types:SOAPStruct'/>\n" +
  "       </all>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <complexType name='SOAPArrayStruct'>\n" +
  "       <all>\n" +
  "         <element name='varString' type='xsd:string'/>\n" +
  "         <element name='varInt' type='xsd:int'/>\n" +
  "         <element name='varFloat' type='xsd:float'/>\n" +
  "         <element name='varArray' type='types:ArrayOfstring'/>\n" +
  "       </all>\n" +
  "     </complexType>\n" +
  "\n" +
  "   </schema>\n" +
  "\n" +
  "       <schema xmlns='http://www.w3.org/2001/XMLSchema'\n" +
  "     elementFormDefault='qualified'\n" +
  "     targetNamespace='http://example.org/ts-tests'>\n" +
  "\n" +
  "     <import namespace='http://www.w3.org/1999/xlink' />\n" +
  "\n" +
  "     <!-- 3.2.1 echoOk -->\n" +
  "     <element name='echoOk' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.2 responseOk -->\n" +
  "     <element name='responseOk' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.3 Ignore -->\n" +
  "     <element name='Ignore' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.4 requiredHeader -->\n" +
  "     <element name='requiredHeader' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.5 DataHolder -->\n" +
  "     <element name='DataHolder' type='test:DataHolder_t'/>\n" +
  "     <complexType name='DataHolder_t'>\n" +
  "       <sequence>\n" +
  "         <element name='Data' type='xsd:string' minOccurs='1' maxOccurs='unbounded'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <!-- 3.2.6 concatAndForwardEchoOk -->\n" +
  "     <element name='concatAndForwardEchoOk'/>\n" +
  "\n" +
  "     <!-- 3.2.7 concatAndForwardEchoOkArg1 -->\n" +
  "     <element name='concatAndForwardEchoOkArg1' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.8 concatAndForwardEchoOkArg2 -->\n" +
  "     <element name='concatAndForwardEchoOkArg2' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.9 validateCountryCode -->\n" +
  "     <element name='validateCountryCode' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.10 validateCountryCodeFault -->\n" +
  "     <element name='validateCountryCodeFault' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- 3.2.11 echoResolvedRef -->\n" +
  "     <element name='RelativeReference' type='test:RelativeReference_t'/>\n" +
  "     <complexType name='RelativeReference_t'>\n" +
  "       <attribute ref='xml:base'/>\n" +
  "         <attribute ref='xlink:href'/>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <element name='echoResolvedRef' type='test:echoResolvedRef_t'/>\n" +
  "     <complexType name='echoResolvedRef_t'>\n" +
  "       <complexContent>\n" +
  "         <sequence>\n" +
  "           <element ref='test:RelativeReference' minOccurs='1' maxOccurs='1'/>\n" +
  "         </sequence>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "\n" +
  "     <!-- 3.2.12 responseResolvedRef -->\n" +
  "     <element name='responseResolvedRef' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- echoOkUltimateReceiver added to support testing of 'relay' -->\n" +
  "     <element name='echoOkUltimateReceiver' type='xsd:string'/>\n" +
  "\n" +
  "     <!-- responseOkUltimateReceiver added to support testing of 'relay' -->\n" +
  "     <element name='responseOkUltimateReceiver' type='xsd:string'/>\n" +
  "\n" +
  "   </schema>\n" +
  "\n" +
  " </types>\n" +
  "\n" +
  " <!-- 3.4.1 returnVoid rpc operation -->\n" +
  " <message name='returnVoidRequest'/>\n" +
  " <message name='returnVoidResponse'/>\n" +
  "\n" +
  " <!-- 3.4.2 echoStruct rpc operation -->\n" +
  " <message name='echoStructRequest'>\n" +
  "   <part name='inputStruct' type='types:SOAPStruct'/>\n" +
  " </message>\n" +
  " <message name='echoStructResponse'>\n" +
  "   <part name='return' type='types:SOAPStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.3 echoStructArray rpc operation -->\n" +
  " <message name='echoStructArrayRequest'>\n" +
  "   <part name='inputStructArray' type='types:ArrayOfSOAPStruct'/>\n" +
  " </message>\n" +
  " <message name='echoStructArrayResponse'>\n" +
  "   <part name='return' type='types:ArrayOfSOAPStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.4 echoStructAsSimpleTypes rpc operation -->\n" +
  " <message name='echoStructAsSimpleTypesRequest'>\n" +
  "   <part name='inputStruct' type='types:SOAPStruct'/>\n" +
  " </message>\n" +
  " <message name='echoStructAsSimpleTypesResponse'>\n" +
  "   <part name='outputString' type='xsd:string'/>\n" +
  "   <part name='outputInteger' type='xsd:int'/>\n" +
  "   <part name='outputFloat' type='xsd:float'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.5 echoSimpleTypesAsStruct rpc operation -->\n" +
  " <message name='echoSimpleTypesAsStructRequest'>\n" +
  "   <part name='inputString' type='xsd:string'/>\n" +
  "   <part name='inputInteger' type='xsd:int'/>\n" +
  "   <part name='inputFloat' type='xsd:float'/>\n" +
  " </message>\n" +
  " <message name='echoSimpleTypesAsStructResponse'>\n" +
  "   <part name='return' type='types:SOAPStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.6 echoNestedStruct rpc operation -->\n" +
  " <message name='echoNestedStructRequest'>\n" +
  "   <part name='inputStruct' type='types:SOAPStructStruct'/>\n" +
  " </message>\n" +
  " <message name='echoNestedStructResponse'>\n" +
  "   <part name='return' type='types:SOAPStructStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.7 echoNestedArray rpc operation -->\n" +
  " <message name='echoNestedArrayRequest'>\n" +
  "   <part name='inputStruct' type='types:SOAPArrayStruct'/>\n" +
  " </message>\n" +
  " <message name='echoNestedArrayResponse'>\n" +
  "   <part name='return' type='types:SOAPArrayStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.8 echoFloatArray rpc operation -->\n" +
  " <message name='echoFloatArrayRequest'>\n" +
  "   <part name='inputFloatArray' type='types:ArrayOffloat'/>\n" +
  " </message>\n" +
  " <message name='echoFloatArrayResponse'>\n" +
  "   <part name='return' type='types:ArrayOffloat'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.9 echoStringArray rpc operation -->\n" +
  " <message name='echoStringArrayRequest'>\n" +
  "   <part name='inputStringArray' type='types:ArrayOfstring'/>\n" +
  " </message>\n" +
  " <message name='echoStringArrayResponse'>\n" +
  "   <part name='return' type='types:ArrayOfstring'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.10 echoIntegerArray rpc operation -->\n" +
  " <message name='echoIntegerArrayRequest'>\n" +
  "   <part name='inputIntegerArray' type='types:ArrayOfint'/>\n" +
  " </message>\n" +
  " <message name='echoIntegerArrayResponse'>\n" +
  "   <part name='return' type='types:ArrayOfint'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.11 echoBase64 rpc operation -->\n" +
  " <message name='echoBase64Request'>\n" +
  "   <part name='inputBase64' type='xsd:base64Binary'/>\n" +
  " </message>\n" +
  " <message name='echoBase64Response'>\n" +
  "   <part name='return' type='xsd:base64Binary'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.12 echoBoolean rpc operation -->\n" +
  " <message name='echoBooleanRequest'>\n" +
  "   <part name='inputBoolean' type='xsd:boolean'/>\n" +
  " </message>\n" +
  " <message name='echoBooleanResponse'>\n" +
  "   <part name='return' type='xsd:boolean'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.13 echoDate rpc operation -->\n" +
  " <message name='echoDateRequest'>\n" +
  "   <part name='inputDate' type='xsd:dateTime'/>\n" +
  " </message>\n" +
  " <message name='echoDateResponse'>\n" +
  "   <part name='return' type='xsd:dateTime'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.14 echoDecimal rpc operation -->\n" +
  " <message name='echoDecimalRequest'>\n" +
  "   <part name='inputDecimal' type='xsd:decimal'/>\n" +
  " </message>\n" +
  " <message name='echoDecimalResponse'>\n" +
  "   <part name='return' type='xsd:decimal'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.15 echoFloat rpc operation -->\n" +
  " <message name='echoFloatRequest'>\n" +
  "   <part name='inputFloat' type='xsd:float'/>\n" +
  " </message>\n" +
  " <message name='echoFloatResponse'>\n" +
  "   <part name='return' type='xsd:float'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.16 echoString rpc operation -->\n" +
  " <message name='echoStringRequest'>\n" +
  "   <part name='inputString' type='xsd:string'/>\n" +
  " </message>\n" +
  " <message name='echoStringResponse'>\n" +
  "   <part name='return' type='xsd:string'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.17 countItems rpc operation -->\n" +
  " <message name='countItemsRequest'>\n" +
  "   <part name='inputStringArray' type='types:ArrayOfstring'/>\n" +
  " </message>\n" +
  " <message name='countItemsResponse'>\n" +
  "   <part name='return' type='xsd:int'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- 3.4.18 isNil rpc operation -->\n" +
  " <message name='isNilRequest'>\n" +
  "   <part name='inputString' type='xsd:string'/>\n" +
  " </message>\n" +
  " <message name='isNilResponse'>\n" +
  "   <part name='return' type='xsd:boolean'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- DataHolder header block -->\n" +
  " <message name='DataHolderRequest'>\n" +
  "   <part name='DataHolder' type='test:DataHolder_t'/>\n" +
  " </message>\n" +
  " <message name='DataHolderResponse'>\n" +
  "   <part name='DataHolder' type='test:DataHolder_t'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- empty message for doc/literal testing -->\n" +
  " <message name='emptyBodyRequest'/>\n" +
  " <message name='emptyBodyResponse'/>\n" +
  "\n" +
  " <!-- echoOk body/header block -->\n" +
  " <message name='echoOkRequest'>\n" +
  "   <part name='echoOk' element='test:echoOk'/>\n" +
  " </message>\n" +
  " <message name='echoOkResponse'>\n" +
  "   <part name='responseOk' element='test:responseOk'/>\n" +
  " </message>\n" +
  "\n" +
  "\n" +
  "   <portType name='Soap12TestPortTypeDoc'>\n" +
  "       <operation name='emptyBody'>\n" +
  "           <input message='tns:emptyBodyRequest' />\n" +
  "           <output message='tns:emptyBodyResponse' />\n" +
  "       </operation>\n" +
  "       <operation name='echoOk'>\n" +
  "           <input message='tns:echoOkRequest' />\n" +
  "           <output message='tns:echoOkResponse' />\n" +
  "       </operation>\n" +
  " </portType>\n" +
  "\n" +
  " <portType name='Soap12TestPortTypeRpc'>\n" +
  "\n" +
  "   <!-- 3.4.1 returnVoid rpc operation -->\n" +
  "   <operation name='returnVoid'>\n" +
  "     <input message='tns:returnVoidRequest' name='returnVoid'/>\n" +
  "     <output message='tns:returnVoidResponse' name='returnVoidResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.2 echoStruct rpc operation -->\n" +
  "   <operation name='echoStruct' parameterOrder='inputStruct'>\n" +
  "     <input message='tns:echoStructRequest' name='echoStruct'/>\n" +
  "     <output message='tns:echoStructResponse' name='echoStructResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.3 echoStructArray rpc operation -->\n" +
  "   <operation name='echoStructArray' parameterOrder='inputStructArray'>\n" +
  "     <input message='tns:echoStructArrayRequest' name='echoStructArray'/>\n" +
  "     <output message='tns:echoStructArrayResponse' name='echoStructArrayResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.4 echoStructAsSimpleTypes rpc operation -->\n" +
  "   <operation name='echoStructAsSimpleTypes' parameterOrder='inputStruct outputString outputInteger outputFloat'>\n" +
  "     <input message='tns:echoStructAsSimpleTypesRequest'/>\n" +
  "     <output message='tns:echoStructAsSimpleTypesResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.5 echoSimpleTypesAsStruct rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStruct' parameterOrder='inputString inputInteger inputFloat'>\n" +
  "     <input message='tns:echoSimpleTypesAsStructRequest'/>\n" +
  "     <output message='tns:echoSimpleTypesAsStructResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.6 echoNestedStruct rpc operation -->\n" +
  "   <operation name='echoNestedStruct' parameterOrder='inputStruct'>\n" +
  "     <input message='tns:echoNestedStructRequest'/>\n" +
  "     <output message='tns:echoNestedStructResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.7 echoNestedArray rpc operation -->\n" +
  "   <operation name='echoNestedArray' parameterOrder='inputStruct'>\n" +
  "     <input message='tns:echoNestedArrayRequest'/>\n" +
  "     <output message='tns:echoNestedArrayResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.8 echoFloatArray rpc operation -->\n" +
  "   <operation name='echoFloatArray' parameterOrder='inputFloatArray'>\n" +
  "     <input message='tns:echoFloatArrayRequest' name='echoFloatArray'/>\n" +
  "     <output message='tns:echoFloatArrayResponse' name='echoFloatArrayResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.9 echoStringArray rpc operation -->\n" +
  "   <operation name='echoStringArray' parameterOrder='inputStringArray'>\n" +
  "     <input message='tns:echoStringArrayRequest' name='echoStringArray'/>\n" +
  "     <output message='tns:echoStringArrayResponse' name='echoStringArrayResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.10 echoIntegerArray rpc operation -->\n" +
  "   <operation name='echoIntegerArray' parameterOrder='inputIntegerArray'>\n" +
  "     <input message='tns:echoIntegerArrayRequest' name='echoIntegerArray'/>\n" +
  "     <output message='tns:echoIntegerArrayResponse' name='echoIntegerArrayResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.11 echoBase64 rpc operation -->\n" +
  "   <operation name='echoBase64' parameterOrder='inputBase64'>\n" +
  "     <input message='tns:echoBase64Request' name='echoBase64'/>\n" +
  "     <output message='tns:echoBase64Response' name='echoBase64Response'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.12 echoBoolean rpc operation -->\n" +
  "   <operation name='echoBoolean' parameterOrder='inputBoolean'>\n" +
  "     <input message='tns:echoBooleanRequest' name='echoBoolean'/>\n" +
  "     <output message='tns:echoBooleanResponse' name='echoBooleanResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.13 echoDate rpc operation -->\n" +
  "   <operation name='echoDate' parameterOrder='inputDate'>\n" +
  "     <input message='tns:echoDateRequest' name='echoDate'/>\n" +
  "     <output message='tns:echoDateResponse' name='echoDateResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.14 echoDecimal rpc operation -->\n" +
  "   <operation name='echoDecimal' parameterOrder='inputDecimal'>\n" +
  "     <input message='tns:echoDecimalRequest' name='echoDecimal'/>\n" +
  "     <output message='tns:echoDecimalResponse' name='echoDecimalResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.15 echoFloat rpc operation -->\n" +
  "   <operation name='echoFloat' parameterOrder='inputFloat'>\n" +
  "     <input message='tns:echoFloatRequest' name='echoFloat'/>\n" +
  "     <output message='tns:echoFloatResponse' name='echoFloatResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.16 echoString rpc operation -->\n" +
  "   <operation name='echoString' parameterOrder='inputString'>\n" +
  "     <input message='tns:echoStringRequest' name='echoString'/>\n" +
  "     <output message='tns:echoStringResponse' name='echoStringResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.17 countItems rpc operation -->\n" +
  "   <operation name='countItems' parameterOrder='inputStringArray'>\n" +
  "     <input message='tns:countItemsRequest' name='countItems'/>\n" +
  "     <output message='tns:countItemsResponse' name='countItemsResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.18 isNil rpc operation -->\n" +
  "   <operation name='isNil' parameterOrder='inputString'>\n" +
  "     <input message='tns:isNilRequest' name='isNil'/>\n" +
  "     <output message='tns:isNilResponse' name='isNilResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  " </portType>\n" +
  "\n" +
  "   <binding name='Soap12TestDocBinding' type='tns:Soap12TestPortTypeDoc'>\n" +
  "       <soap12:binding style='document' transport='http://schemas.xmlsoap.org/soap/http' />\n" +
  "       <operation name='emptyBody'>\n" +
  "           <soap12:operation/>\n" +
  "           <input>\n" +
  "             <soap12:body use='literal' />\n" +
  "             <soap12:header message='tns:echoOkRequest' part='echoOk' use='literal' />\n" +
  "           </input>\n" +
  "           <output>\n" +
  "             <soap12:body use='literal' />\n" +
  "             <soap12:header message='tns:echoOkResponse' part='responseOk' use='literal' />\n" +
  "           </output>\n" +
  "       </operation>\n" +
  "       <operation name='echoOk'>\n" +
  "           <soap12:operation/>\n" +
  "           <input>\n" +
  "             <soap12:body use='literal' />\n" +
  "             <soap12:header message='tns:echoOkRequest' part='echoOk' use='literal' />\n" +
  "           </input>\n" +
  "           <output>\n" +
  "             <soap12:body use='literal' />\n" +
  "             <soap12:header message='tns:echoOkResponse' part='responseOk' use='literal' />\n" +
  "           </output>\n" +
  "       </operation>\n" +
  " </binding>\n" +
  "\n" +
  " <binding name='Soap12TestRpcBinding' type='tns:Soap12TestPortTypeRpc'>\n" +
  "   <soap12:binding style='rpc' transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "\n" +
  "   <!-- 3.4.1 returnVoid rpc operation -->\n" +
  "   <operation name='returnVoid'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.2 echoStruct rpc operation -->\n" +
  "   <operation name='echoStruct'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.3 echoStructArray rpc operation -->\n" +
  "   <operation name='echoStructArray'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.4 echoStructAsSimpleTypes rpc operation -->\n" +
  "   <operation name='echoStructAsSimpleTypes'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.5 echoSimpleTypesAsStruct rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStruct'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.6 echoNestedStruct rpc operation -->\n" +
  "   <operation name='echoNestedStruct'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.7 echoNestedArray rpc operation -->\n" +
  "   <operation name='echoNestedArray'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.8 echoFloatArray rpc operation -->\n" +
  "   <operation name='echoFloatArray'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.9 echoStringArray rpc operation -->\n" +
  "   <operation name='echoStringArray'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.10 echoIntegerArray rpc operation -->\n" +
  "   <operation name='echoIntegerArray'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.11 echoBase64 rpc operation -->\n" +
  "   <operation name='echoBase64'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.12 echoBoolean rpc operation -->\n" +
  "   <operation name='echoBoolean'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.13 echoDate rpc operation -->\n" +
  "   <operation name='echoDate'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.14 echoDecimal rpc operation -->\n" +
  "   <operation name='echoDecimal'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.15 echoFloat rpc operation -->\n" +
  "   <operation name='echoFloat'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.16 echoString rpc operation -->\n" +
  "   <operation name='echoString'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "       <soap12:header use='encoded' message='tns:DataHolder_Request' part='DataHolder' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "       <soap12:header use='encoded' message='tns:DataHolder_Response' part='DataHolder' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.17 countItems rpc operation -->\n" +
  "   <operation name='countItems'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- 3.4.18 isNil rpc operation -->\n" +
  "   <operation name='isNil'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  " </binding>\n" +
  "\n" +
  "\n" +
  " <service name='WhiteMesaSoap12TestSvc'>\n" +
  "        <!--\n" +
  "     <port name='Soap12TestDocPort' binding='tns:Soap12TestDocBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/test-doc'/>\n" +
  "     </port>\n" +
  "     <port name='Soap12TestRpcPort' binding='tns:Soap12TestRpcBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/test-rpc'/>\n" +
  "     </port>\n" +
  "        -->\n" +
  "        <port name='Soap12TestDocPort' binding='tns:Soap12TestDocBinding'>\n" +
  "              <soap12:address location='http://localhost:8080/axis/services/Soap12TestDocPort'/>\n" +
  "        </port>\n" +
  "        <port name='Soap12TestRpcPort' binding='tns:Soap12TestRpcBinding'>\n" +
  "              <soap12:address location='http://localhost:8080/axis/services/Soap12TestRpcPort'/>\n" +
  "        </port>\n" +
  "\n" +
  " </service>\n" +
  "\n" +
  "</definitions>\n" +
  "\n";

  public void testSoap12Test() throws Exception {
    parseString(soap12Test, "soap12-test.wsdl");
  }

  private static final String soap12AddTest =
  "<?xml version='1.0'?>\n" +
  "\n" +
  "<definitions name='SOAP-12-TestDefinitions'\n" +
  " targetNamespace='http://whitemesa.net/wsdl/soap12-test'\n" +
  " xmlns='http://schemas.xmlsoap.org/wsdl/'\n" +
  " xmlns:soap12='http://schemas.xmlsoap.org/wsdl/soap12/'\n" +
  " xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n" +
  " xmlns:enc='http://www.w3.org/2003/05/soap-encoding'\n" +
  " xmlns:tns='http://whitemesa.net/wsdl/soap12-test'\n" +
  " xmlns:types='http://example.org/ts-tests/xsd'\n" +
  " xmlns:test='http://example.org/ts-tests'\n" +
  " xmlns:iop='http://soapinterop.org/'\n" +
  " xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'>\n" +
  " <types>\n" +
  "\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' targetNamespace='http://example.org/ts-tests/xsd'>\n" +
  "\n" +
  "     <complexType name='SOAPStruct'>\n" +
  "       <all>\n" +
  "         <element name='varString' type='xsd:string'/>\n" +
  "         <element name='varInt' type='xsd:int'/>\n" +
  "         <element name='varFloat' type='xsd:float'/>\n" +
  "       </all>\n" +
  "     </complexType>\n" +
  "\n" +
  "   </schema>\n" +
  "\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' targetNamespace='http://example.org/ts-tests/xsd'>\n" +
  "\n" +
  "     <!-- added to support test xmlp-10 -->\n" +
  "     <complexType name='SOAPStructInputs'>\n" +
  "       <sequence>\n" +
  "         <element name='input1' type='anyType' />\n" +
  "         <element name='input2' type='anyType' />\n" +
  "         <element name='input3' type='anyType' />\n" +
  "         <element name='input4' type='anyType' />\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <complexType name='SOAPStructTypes'>\n" +
  "       <sequence>\n" +
  "         <element name='type1' type='QName' />\n" +
  "         <element name='type2' type='QName' />\n" +
  "         <element name='type3' type='QName' />\n" +
  "         <element name='type4' type='QName' />\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "\n" +
  "   </schema>\n" +
  "\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' elementFormDefault='qualified' targetNamespace='http://soapinterop.org/'>\n" +
  "\n" +
  "     <!-- xmlp-2 getTime -->\n" +
  "     <element name='time' type='time'/>\n" +
  "\n" +
  "     <!-- xmlp-7 echoSenderFault -->\n" +
  "     <element name='echoSenderFault' />\n" +
  "\n" +
  "     <!-- xmlp-8 echoReceiverFault -->\n" +
  "     <element name='echoReceiverFault' />\n" +
  "\n" +
  "     <!-- xmlp-13 thru 19 echoString doc/lit operation -->\n" +
  "     <element name='echoString'>\n" +
  "       <complexType>\n" +
  "         <sequence>\n" +
  "           <element minOccurs='0' maxOccurs='1' name='inputString' type='string' />\n" +
  "         </sequence>\n" +
  "       </complexType>\n" +
  "     </element>\n" +
  "     <element name='echoStringResponse'>\n" +
  "       <complexType>\n" +
  "         <sequence>\n" +
  "             <element minOccurs='0' maxOccurs='1' name='return' type='string' />\n" +
  "         </sequence>\n" +
  "       </complexType>\n" +
  "     </element>\n" +
  "\n" +
  "   </schema>\n" +
  "\n" +
  " </types>\n" +
  "\n" +
  " <!-- xmlp-5, xmlp-6 echoVoid rpc operation -->\n" +
  " <message name='echoVoidRequest' />\n" +
  " <message name='echoVoidResponse' />\n" +
  "\n" +
  " <!-- xmlp-4 echoSimpleTypesAsStruct rpc operation -->\n" +
  " <message name='echoSimpleTypesAsStructRequest'>\n" +
  "   <part name='inputString' type='xsd:string'/>\n" +
  "   <part name='inputInteger' type='xsd:int'/>\n" +
  "   <part name='inputFloat' type='xsd:float'/>\n" +
  " </message>\n" +
  " <message name='echoSimpleTypesAsStructResponse'>\n" +
  "   <part name='return' type='types:SOAPStruct'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-1, xmlp-9 echoString rpc operation -->\n" +
  " <message name='echoStringRequest'>\n" +
  "   <part name='inputString' type='xsd:string'/>\n" +
  " </message>\n" +
  " <message name='echoStringResponse'>\n" +
  "   <part name='return' type='xsd:string'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-10 echoSimpleTypesAsStructOfSchemaTypesRequest rpc operation -->\n" +
  " <message name='echoSimpleTypesAsStructOfSchemaTypesRequest'>\n" +
  "   <part name='input1' type='xsd:anyType'/>\n" +
  "   <part name='input2' type='xsd:anyType'/>\n" +
  "   <part name='input3' type='xsd:anyType'/>\n" +
  "   <part name='input4' type='xsd:anyType'/>\n" +
  " </message>\n" +
  " <message name='echoSimpleTypesAsStructOfSchemaTypesResponse'>\n" +
  "   <part name='return' type='types:SOAPStructTypes'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-11 echoInteger rpc operation -->\n" +
  " <message name='echoIntegerRequest'>\n" +
  "   <part name='inputInteger' type='xsd:int'/>\n" +
  " </message>\n" +
  " <message name='echoIntegerResponse'>\n" +
  "   <part name='return' type='xsd:int'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-3 getTime rpc operation -->\n" +
  " <message name='getTimeRpcResponse'>\n" +
  "   <part name='return' type='xsd:time'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-2 getTime doc/lit operation -->\n" +
  " <message name='getTimeDocResponse'>\n" +
  "   <part name='time' element='iop:time'/>\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-13 thru 19 echoString doc/lit operation -->\n" +
  " <message name='echoStringDocRequest'>\n" +
  "   <part name='inElement' element='iop:echoString' />\n" +
  " </message>\n" +
  " <message name='echoStringDocResponse'>\n" +
  "   <part name='outElement' element='iop:echoStringResponse' />\n" +
  " </message>\n" +
  "\n" +
  " <!-- xmlp-7 echoSenderFault operation -->\n" +
  " <message name='echoSenderFaultRequest'>\n" +
  "   <part name='inElement' element='iop:echoSenderFault' />\n" +
  " </message>\n" +
  " <message name='echoSenderFaultResponse' />\n" +
  "\n" +
  " <!-- xmlp-8 echoReceiverFault operation -->\n" +
  " <message name='echoReceiverFaultRequest'>\n" +
  "   <part name='inElement' element='iop:echoReceiverFault' />\n" +
  " </message>\n" +
  " <message name='echoReceiverFaultResponse' />\n" +
  "\n" +
  "   <portType name='Soap12AddTestPortTypeDoc'>\n" +
  "   <!-- xmlp-2 getTime operation -->\n" +
  "       <operation name='getTime'>\n" +
  "             <output message='tns:getTimeDocResponse' />\n" +
  "       </operation>\n" +
  "   <!-- xmlp-13 thru xmlp-19 echoString doc/literal operation -->\n" +
  "   <operation name='echoString'>\n" +
  "     <input message='tns:echoStringDocRequest' />\n" +
  "     <output message='tns:echoStringDocResponse' />\n" +
  "   </operation>\n" +
  "   <!-- xmlp-7 echoSenderFault doc/literal operation -->\n" +
  "   <operation name='echoSenderFault'>\n" +
  "     <input message='tns:echoSenderFaultRequest' />\n" +
  "     <output message='tns:echoSenderFaultResponse' />\n" +
  "   </operation>\n" +
  "   <!-- xmlp-8 echoReceiverFault doc/literal operation -->\n" +
  "   <operation name='echoReceiverFault'>\n" +
  "     <input message='tns:echoReceiverFaultRequest' />\n" +
  "     <output message='tns:echoReceiverFaultResponse' />\n" +
  "   </operation>\n" +
  " </portType>\n" +
  "\n" +
  " <portType name='Soap12AddTestPortTypeRpc'>\n" +
  "\n" +
  "   <!-- xmlp-5, xmlp-6 echoVoid rpc operation -->\n" +
  "   <operation name='echoVoid'>\n" +
  "     <input message='tns:echoVoidRequest' name='echoVoid'/>\n" +
  "     <output message='tns:echoVoidResponse' name='echoVoidResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "\n" +
  "   <!-- xmlp-4 echoSimpleTypesAsStruct rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStruct' parameterOrder='inputString inputInteger inputFloat'>\n" +
  "     <input message='tns:echoSimpleTypesAsStructRequest'/>\n" +
  "     <output message='tns:echoSimpleTypesAsStructResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "\n" +
  "   <!-- xmlp-1, xmlp-9 echoString rpc operation -->\n" +
  "   <operation name='echoString' parameterOrder='inputString'>\n" +
  "     <input message='tns:echoStringRequest' name='echoString'/>\n" +
  "     <output message='tns:echoStringResponse' name='echoStringResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-10 echoSimpleTypesAsStructOfSchemaTypes rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStructOfSchemaTypes' parameterOrder='input1 input2 input3 input4'>\n" +
  "     <input message='tns:echoSimpleTypesAsStructOfSchemaTypesRequest'/>\n" +
  "     <output message='tns:echoSimpleTypesAsStructOfSchemaTypesResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-11 echoInteger rpc operation -->\n" +
  "   <operation name='echoInteger' parameterOrder='inputInteger'>\n" +
  "     <input message='tns:echoIntegerRequest' name='echoInteger'/>\n" +
  "     <output message='tns:echoIntegerResponse' name='echoIntegerResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-3 getTime rpc operation -->\n" +
  "   <operation name='getTime'>\n" +
  "     <output message='tns:getTimeRpcResponse' name='getTimeRpcResponse'/>\n" +
  "   </operation>\n" +
  "\n" +
  " </portType>\n" +
  "\n" +
  "   <binding name='Soap12AddTestDocBinding' type='tns:Soap12AddTestPortTypeDoc'>\n" +
  "       <soap12:binding style='document' transport='http://schemas.xmlsoap.org/soap/http' />\n" +
  "       <operation name='getTime'>\n" +
  "           <soap12:operation style='document' />\n" +
  "           <output>\n" +
  "             <soap12:body use='literal' />\n" +
  "           </output>\n" +
  "       </operation>\n" +
  "   <operation name='echoString'>\n" +
  "     <soap12:operation style='document' />\n" +
  "     <input>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "   <operation name='echoSenderFault'>\n" +
  "     <soap12:operation style='document' />\n" +
  "     <input>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "   <operation name='echoReceiverFault'>\n" +
  "     <soap12:operation style='document' />\n" +
  "     <input>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='literal' />\n" +
  "     </output>\n" +
  "   </operation>\n" +
  " </binding>\n" +
  "\n" +
  " <binding name='Soap12AddTestRpcBinding' type='tns:Soap12AddTestPortTypeRpc'>\n" +
  "   <soap12:binding style='rpc' transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "\n" +
  "   <!-- xmlp-5, xmlp-6 echoVoid rpc operation -->\n" +
  "   <operation name='echoVoid'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-4 echoSimpleTypesAsStruct rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStruct'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-3 getTime rpc operation -->\n" +
  "   <operation name='getTime'>\n" +
  "     <soap12:operation/>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-1, xmlp-9 echoString rpc operation -->\n" +
  "   <operation name='echoString'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-10 echoSimpleTypesAsStructOfSchemaTypes rpc operation -->\n" +
  "   <operation name='echoSimpleTypesAsStructOfSchemaTypes'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://example.org/ts-tests' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  "   <!-- xmlp-11 echoInteger rpc operation -->\n" +
  "   <operation name='echoInteger'>\n" +
  "     <soap12:operation/>\n" +
  "     <input>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </input>\n" +
  "     <output>\n" +
  "       <soap12:body use='encoded' namespace='http://soapinterop.org/' encodingStyle='http://www.w3.org/2003/05/soap-encoding'/>\n" +
  "     </output>\n" +
  "   </operation>\n" +
  "\n" +
  " </binding>\n" +
  "\n" +
  "\n" +
  " <service name='WhiteMesaSoap12AddTestSvc'>\n" +
  "\n" +
  "     <port name='Soap12AddTestDocPort' binding='tns:Soap12AddTestDocBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/add-test-doc'/>\n" +
  "     </port>\n" +
  "     <port name='Soap12AddTestDocIntermediaryPort' binding='tns:Soap12AddTestDocBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/add-test-doc-int'/>\n" +
  "     </port>\n" +
  "     <port name='Soap12AddTestDocUpperPort' binding='tns:Soap12AddTestDocBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/add-test-doc-int-uc'/>\n" +
  "     </port>\n" +
  "     <port name='Soap12AddTestRpcPort' binding='tns:Soap12AddTestRpcBinding'>\n" +
  "         <soap12:address location='http://www.whitemesa.net/soap12/add-test-rpc'/>\n" +
  "     </port>\n" +
  "\n" +
  " </service>\n" +
  "\n" +
  "</definitions>\n" +
  "\n";

  public void testSoap12AddTest() throws Exception {
    parseString(soap12AddTest, "soap12-add-test.wsdl");
  }

  private static final String round4XSD =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<definitions xmlns:s1='http://soapinterop.org/xsd' xmlns:http='http://schemas.xmlsoap.org/wsdl/http/' xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:s0='http://soapinterop.org/' xmlns:s3='http://soapinterop.org/echoheader/' xmlns:s2='http://soapinterop.org' xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/' xmlns:tm='http://microsoft.com/wsdl/mime/textMatching/' xmlns:mime='http://schemas.xmlsoap.org/wsdl/mime/' targetNamespace='http://soapinterop.org/' xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "  <types>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://soapinterop.org/'>\n" +
  "      <s:import namespace='http://soapinterop.org/xsd' />\n" +
  "      <s:import namespace='http://soapinterop.org/echoheader/' />\n" +
  "      <s:element name='echoVoid'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "      <s:element name='echoVoidResponse'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "      <s:element name='echoInteger'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputInteger' type='s:int' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoIntegerResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s:int' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoFloat'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputFloat' type='s:float' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoFloatResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s:float' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoString'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputString' type='s:string' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoStringResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s:string' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoBase64'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputBase64' type='s:base64Binary' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoBase64Response'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s:base64Binary' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoDate'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputDate' type='s:dateTime' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoDateResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s:dateTime' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoComplexType'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputComplexType' type='s1:SOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoComplexTypeResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s1:SOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoIntegerMultiOccurs'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputIntegerMultiOccurs' type='s0:ArrayOfInt' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:complexType name='ArrayOfInt'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='unbounded' name='int' type='s:int' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:element name='echoIntegerMultiOccursResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='unbounded' name='return' type='s:int' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoFloatMultiOccurs'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputFloatMultiOccurs' type='s0:ArrayOfFloat' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:complexType name='ArrayOfFloat'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='unbounded' name='float' type='s:float' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:element name='echoFloatMultiOccursResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='unbounded' name='return' type='s:float' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoStringMultiOccurs'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputStringMultiOccurs' type='s0:ArrayOfString' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:complexType name='ArrayOfString'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='unbounded' name='string' nillable='true' type='s:string' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:element name='echoStringMultiOccursResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='unbounded' name='return' type='s:string' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoComplexTypeMultiOccurs'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputComplexTypeMultiOccurs' type='s1:ArrayOfSOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='SOAPComplexType' nillable='true' type='s1:SOAPComplexType' />\n" +
  "      <s:element name='echoComplexTypeMultiOccursResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='unbounded' name='return' type='s1:SOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoDecimal'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputDecimal' type='s:decimal' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoDecimalResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s:decimal' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoBoolean'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputBoolean' type='s:boolean' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoBooleanResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s:boolean' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoHexBinary'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputHexBinary' type='s:hexBinary' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoHexBinaryResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s:hexBinary' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoComplexTypeAsSimpleTypes'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputComplexType' type='s1:SOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoComplexTypeAsSimpleTypesResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='outputString' type='s:string' />\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='outputInteger' type='s:int' />\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='outputFloat' type='s:float' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoSimpleTypesAsComplexType'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputString' type='s:string' />\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputInteger' type='s:int' />\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputFloat' type='s:float' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoSimpleTypesAsComplexTypeResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s1:SOAPComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoNestedComplexType'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputComplexType' type='s1:SOAPComplexTypeComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoNestedComplexTypeResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s1:SOAPComplexTypeComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoNestedMultiOccurs'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputComplexType' type='s1:SOAPMultiOccursComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoNestedMultiOccursResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s1:SOAPMultiOccursComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoChoice'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputChoice' type='s1:ChoiceComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoChoiceResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' type='s1:ChoiceComplexType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoEnum'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='inputEnum' type='s1:Enum' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoEnumResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='1' maxOccurs='1' name='return' type='s1:Enum' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoAnyType'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputAnyType' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoAnyTypeResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return' />\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoAnyElement'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='inputAny'>\n" +
  "              <s:complexType>\n" +
  "                <s:sequence>\n" +
  "                  <s:any />\n" +
  "                </s:sequence>\n" +
  "              </s:complexType>\n" +
  "            </s:element>\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoAnyElementResponse'>\n" +
  "        <s:complexType>\n" +
  "          <s:sequence>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='return'>\n" +
  "              <s:complexType>\n" +
  "                <s:sequence>\n" +
  "                  <s:any />\n" +
  "                </s:sequence>\n" +
  "              </s:complexType>\n" +
  "            </s:element>\n" +
  "          </s:sequence>\n" +
  "        </s:complexType>\n" +
  "      </s:element>\n" +
  "      <s:element name='echoVoidSoapHeaderResponse'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "      <s:element name='echoMeComplexTypeRequest' type='s3:echoMeComplexTypeRequest' />\n" +
  "      <s:element name='echoMeStringRequest' type='s3:echoMeStringRequest' />\n" +
  "      <s:element name='echoMeComplexTypeResponse' type='s3:echoMeComplexTypeResponse' />\n" +
  "      <s:element name='echoMeStringResponse' type='s3:echoMeStringResponse' />\n" +
  "    </s:schema>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://soapinterop.org/xsd'>\n" +
  "      <s:import namespace='http://soapinterop.org/' />\n" +
  "      <s:complexType name='SOAPComplexType'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varInt' type='s:int' />\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varFloat' type='s:float' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='ArrayOfSOAPComplexType'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='unbounded' ref='s0:SOAPComplexType' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='SOAPComplexTypeComplexType'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varInt' type='s:int' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varFloat' type='s:float' />\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varComplexType' type='s1:SOAPComplexType' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='SOAPMultiOccursComplexType'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varInt' type='s:int' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varFloat' type='s:float' />\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varMultiOccurs' type='s1:ArrayOfString' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='ArrayOfString'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='unbounded' name='string' nillable='true' type='s:string' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='ChoiceComplexType'>\n" +
  "        <s:sequence>\n" +
  "          <s:choice minOccurs='1' maxOccurs='1'>\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='name0' type='s:string' />\n" +
  "            <s:element minOccurs='0' maxOccurs='1' name='name1' type='s:string' />\n" +
  "          </s:choice>\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:simpleType name='Enum'>\n" +
  "        <s:restriction base='s:string'>\n" +
  "          <s:enumeration value='BitOne' />\n" +
  "          <s:enumeration value='BitTwo' />\n" +
  "          <s:enumeration value='BitThree' />\n" +
  "          <s:enumeration value='BitFour' />\n" +
  "          <s:enumeration value='BitFive' />\n" +
  "        </s:restriction>\n" +
  "      </s:simpleType>\n" +
  "    </s:schema>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://soapinterop.org'>\n" +
  "      <s:element name='echoVoidSoapHeader'>\n" +
  "        <s:complexType />\n" +
  "      </s:element>\n" +
  "    </s:schema>\n" +
  "    <s:schema elementFormDefault='qualified' targetNamespace='http://soapinterop.org/echoheader/'>\n" +
  "      <s:complexType name='echoMeComplexTypeRequest'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varInt' type='s:int' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varFloat' type='s:float' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='echoMeStringRequest'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='echoMeComplexTypeResponse'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varInt' type='s:int' />\n" +
  "          <s:element minOccurs='1' maxOccurs='1' name='varFloat' type='s:float' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "      <s:complexType name='echoMeStringResponse'>\n" +
  "        <s:sequence>\n" +
  "          <s:element minOccurs='0' maxOccurs='1' name='varString' type='s:string' />\n" +
  "        </s:sequence>\n" +
  "      </s:complexType>\n" +
  "    </s:schema>\n" +
  "  </types>\n" +
  "  <message name='echoVoidSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoVoid' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoVoidResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoIntegerSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoInteger' />\n" +
  "  </message>\n" +
  "  <message name='echoIntegerSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoIntegerResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoFloatSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoFloat' />\n" +
  "  </message>\n" +
  "  <message name='echoFloatSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoFloatResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoStringSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoString' />\n" +
  "  </message>\n" +
  "  <message name='echoStringSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoStringResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoBase64SoapIn'>\n" +
  "    <part name='parameters' element='s0:echoBase64' />\n" +
  "  </message>\n" +
  "  <message name='echoBase64SoapOut'>\n" +
  "    <part name='parameters' element='s0:echoBase64Response' />\n" +
  "  </message>\n" +
  "  <message name='echoDateSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoDate' />\n" +
  "  </message>\n" +
  "  <message name='echoDateSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoDateResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoComplexType' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoComplexTypeResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoIntegerMultiOccursSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoIntegerMultiOccurs' />\n" +
  "  </message>\n" +
  "  <message name='echoIntegerMultiOccursSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoIntegerMultiOccursResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoFloatMultiOccursSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoFloatMultiOccurs' />\n" +
  "  </message>\n" +
  "  <message name='echoFloatMultiOccursSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoFloatMultiOccursResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoStringMultiOccursSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoStringMultiOccurs' />\n" +
  "  </message>\n" +
  "  <message name='echoStringMultiOccursSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoStringMultiOccursResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeMultiOccursSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoComplexTypeMultiOccurs' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeMultiOccursSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoComplexTypeMultiOccursResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoDecimalSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoDecimal' />\n" +
  "  </message>\n" +
  "  <message name='echoDecimalSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoDecimalResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoBooleanSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoBoolean' />\n" +
  "  </message>\n" +
  "  <message name='echoBooleanSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoBooleanResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoHexBinarySoapIn'>\n" +
  "    <part name='parameters' element='s0:echoHexBinary' />\n" +
  "  </message>\n" +
  "  <message name='echoHexBinarySoapOut'>\n" +
  "    <part name='parameters' element='s0:echoHexBinaryResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeAsSimpleTypesSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoComplexTypeAsSimpleTypes' />\n" +
  "  </message>\n" +
  "  <message name='echoComplexTypeAsSimpleTypesSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoComplexTypeAsSimpleTypesResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoSimpleTypesAsComplexTypeSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoSimpleTypesAsComplexType' />\n" +
  "  </message>\n" +
  "  <message name='echoSimpleTypesAsComplexTypeSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoSimpleTypesAsComplexTypeResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoNestedComplexTypeSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoNestedComplexType' />\n" +
  "  </message>\n" +
  "  <message name='echoNestedComplexTypeSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoNestedComplexTypeResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoNestedMultiOccursSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoNestedMultiOccurs' />\n" +
  "  </message>\n" +
  "  <message name='echoNestedMultiOccursSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoNestedMultiOccursResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoChoiceSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoChoice' />\n" +
  "  </message>\n" +
  "  <message name='echoChoiceSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoChoiceResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoEnumSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoEnum' />\n" +
  "  </message>\n" +
  "  <message name='echoEnumSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoEnumResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoAnyTypeSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoAnyType' />\n" +
  "  </message>\n" +
  "  <message name='echoAnyTypeSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoAnyTypeResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoAnyElementSoapIn'>\n" +
  "    <part name='parameters' element='s0:echoAnyElement' />\n" +
  "  </message>\n" +
  "  <message name='echoAnyElementSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoAnyElementResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderSoapIn'>\n" +
  "    <part name='parameters' element='s2:echoVoidSoapHeader' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderSoapOut'>\n" +
  "    <part name='parameters' element='s0:echoVoidSoapHeaderResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderechoMeComplexTypeRequest'>\n" +
  "    <part name='echoMeComplexTypeRequest' element='s0:echoMeComplexTypeRequest' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderechoMeComplexTypeResponse'>\n" +
  "    <part name='echoMeComplexTypeResponse' element='s0:echoMeComplexTypeResponse' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderechoMeStringRequest'>\n" +
  "    <part name='echoMeStringRequest' element='s0:echoMeStringRequest' />\n" +
  "  </message>\n" +
  "  <message name='echoVoidSoapHeaderechoMeStringResponse'>\n" +
  "    <part name='echoMeStringResponse' element='s0:echoMeStringResponse' />\n" +
  "  </message>\n" +
  "  <portType name='Round4XSDTestSoap'>\n" +
  "    <operation name='echoVoid'>\n" +
  "      <input message='s0:echoVoidSoapIn' />\n" +
  "      <output message='s0:echoVoidSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoInteger'>\n" +
  "      <input message='s0:echoIntegerSoapIn' />\n" +
  "      <output message='s0:echoIntegerSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoFloat'>\n" +
  "      <input message='s0:echoFloatSoapIn' />\n" +
  "      <output message='s0:echoFloatSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoString'>\n" +
  "      <input message='s0:echoStringSoapIn' />\n" +
  "      <output message='s0:echoStringSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoBase64'>\n" +
  "      <input message='s0:echoBase64SoapIn' />\n" +
  "      <output message='s0:echoBase64SoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoDate'>\n" +
  "      <input message='s0:echoDateSoapIn' />\n" +
  "      <output message='s0:echoDateSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexType'>\n" +
  "      <input message='s0:echoComplexTypeSoapIn' />\n" +
  "      <output message='s0:echoComplexTypeSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoIntegerMultiOccurs'>\n" +
  "      <input message='s0:echoIntegerMultiOccursSoapIn' />\n" +
  "      <output message='s0:echoIntegerMultiOccursSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoFloatMultiOccurs'>\n" +
  "      <input message='s0:echoFloatMultiOccursSoapIn' />\n" +
  "      <output message='s0:echoFloatMultiOccursSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoStringMultiOccurs'>\n" +
  "      <input message='s0:echoStringMultiOccursSoapIn' />\n" +
  "      <output message='s0:echoStringMultiOccursSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexTypeMultiOccurs'>\n" +
  "      <input message='s0:echoComplexTypeMultiOccursSoapIn' />\n" +
  "      <output message='s0:echoComplexTypeMultiOccursSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoDecimal'>\n" +
  "      <input message='s0:echoDecimalSoapIn' />\n" +
  "      <output message='s0:echoDecimalSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoBoolean'>\n" +
  "      <input message='s0:echoBooleanSoapIn' />\n" +
  "      <output message='s0:echoBooleanSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoHexBinary'>\n" +
  "      <input message='s0:echoHexBinarySoapIn' />\n" +
  "      <output message='s0:echoHexBinarySoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexTypeAsSimpleTypes'>\n" +
  "      <input message='s0:echoComplexTypeAsSimpleTypesSoapIn' />\n" +
  "      <output message='s0:echoComplexTypeAsSimpleTypesSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoSimpleTypesAsComplexType'>\n" +
  "      <input message='s0:echoSimpleTypesAsComplexTypeSoapIn' />\n" +
  "      <output message='s0:echoSimpleTypesAsComplexTypeSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoNestedComplexType'>\n" +
  "      <input message='s0:echoNestedComplexTypeSoapIn' />\n" +
  "      <output message='s0:echoNestedComplexTypeSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoNestedMultiOccurs'>\n" +
  "      <input message='s0:echoNestedMultiOccursSoapIn' />\n" +
  "      <output message='s0:echoNestedMultiOccursSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoChoice'>\n" +
  "      <input message='s0:echoChoiceSoapIn' />\n" +
  "      <output message='s0:echoChoiceSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoEnum'>\n" +
  "      <input message='s0:echoEnumSoapIn' />\n" +
  "      <output message='s0:echoEnumSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoAnyType'>\n" +
  "      <input message='s0:echoAnyTypeSoapIn' />\n" +
  "      <output message='s0:echoAnyTypeSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoAnyElement'>\n" +
  "      <input message='s0:echoAnyElementSoapIn' />\n" +
  "      <output message='s0:echoAnyElementSoapOut' />\n" +
  "    </operation>\n" +
  "    <operation name='echoVoidSoapHeader'>\n" +
  "      <input message='s0:echoVoidSoapHeaderSoapIn' />\n" +
  "      <output message='s0:echoVoidSoapHeaderSoapOut' />\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "  <binding name='Round4XSDTestSoap' type='s0:Round4XSDTestSoap'>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' style='document' />\n" +
  "    <operation name='echoVoid'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoVoid' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoInteger'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoInteger' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoFloat'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoFloat' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoString'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoString' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoBase64'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoBase64' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoDate'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoDate' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexType'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoComplexType' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoIntegerMultiOccurs'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoIntegerMultiOccurs' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoFloatMultiOccurs'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoFloatMultiOccurs' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoStringMultiOccurs'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoStringMultiOccurs' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexTypeMultiOccurs'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoComplexTypeMultiOccurs' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoDecimal'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoDecimal' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoBoolean'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoBoolean' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoHexBinary'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoHexBinary' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoComplexTypeAsSimpleTypes'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoComplexTypeAsSimpleTypes' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoSimpleTypesAsComplexType'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoSimpleTypesAsComplexType' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoNestedComplexType'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoNestedComplexType' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoNestedMultiOccurs'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoNestedMultiOccurs' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoChoice'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoChoice' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoEnum'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoEnum' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoAnyType'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoAnyType' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoAnyElement'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org/echoAnyElement' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='echoVoidSoapHeader'>\n" +
  "      <soap:operation soapAction='http://soapinterop.org' style='document' />\n" +
  "      <input>\n" +
  "        <soap:body use='literal' />\n" +
  "        <soap:header message='s0:echoVoidSoapHeaderechoMeComplexTypeRequest' part='echoMeComplexTypeRequest' use='literal' />\n" +
  "        <soap:header message='s0:echoVoidSoapHeaderechoMeStringRequest' part='echoMeStringRequest' use='literal' />\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='literal' />\n" +
  "        <soap:header message='s0:echoVoidSoapHeaderechoMeComplexTypeResponse' part='echoMeComplexTypeResponse' use='literal' />\n" +
  "        <soap:header message='s0:echoVoidSoapHeaderechoMeStringResponse' part='echoMeStringResponse' use='literal' />\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "  <service name='Round4XSDTest'>\n" +
  "    <documentation>These operations implement DOC/LIT SOAP operations, for interop testing. Please email johnko@microsoft.com with any questions/coments.</documentation>\n" +
  "    <port name='Round4XSDTestSoap' binding='s0:Round4XSDTestSoap'>\n" +
  "      <!--\n" +
  "      <soap:address location='http://mssoapinterop.org/asmx/xsd/round4xsd.asmx' />\n" +
  "      -->\n" +
  "      <soap:address location='http://localhost:8080/axis/services/Round4XSDTestSoap' />\n" +
  "    </port>\n" +
  "  </service>\n" +
  "</definitions>\n";

  public void testRound4XSD() throws Exception {
    parseString(round4XSD, "round4XSD.wsdl");
  }

  private static final String ram =
  "<?xml version='1.0' encoding='UTF-8'?>\n" +
  "<wsdl:definitions targetNamespace='urn:ram' \n" +
  "xmlns='http://schemas.xmlsoap.org/wsdl/' xmlns:SOAP-ENC='http://schemas.xmlsoap.org/soap/encoding/' xmlns:impl='urn:Ram-impl' \n" +
  "xmlns:intf='urn:ram' xmlns:tns1='http://ram.uspto.gov'\n" +
  "xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/' \n" +
  "xmlns:wsdlsoap='http://schemas.xmlsoap.org/wsdl/soap/' \n" +
  "xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
  " <types>\n" +
  "  <schema targetNamespace='http://ram.uspto.gov' \n" +
  "xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
  "   <complexType name='RamData'>\n" +
  "    <sequence>\n" +
  "     <element name='mailRoomDate' nillable='true' type='xsd:date'/>\n" +
  "     <element name='callingClientUserIdentifier' nillable='true' \n" +
  "type='xsd:string'/>\n" +
  "     <element name='bankInfoRequestType' nillable='true' type='xsd:string'/>\n" +
  "     <element name='callingClientPortNumber' nillable='true' type='xsd:string'/>\n" +
  "     <element name='salePostingReferenceText' nillable='true' \n" +
  "type='xsd:string'/>\n" +
  "     <element name='bankInfoPaymentType' nillable='true' type='xsd:string'/>\n" +
  "     <element name='saleOtherPaymentTotalAmount' type='xsd:double'/>\n" +
  "     <element name='fees' nillable='true' type='intf:ArrayOf_tns1_Fee'/>\n" +
  "     <element name='creditCard' nillable='true' type='tns1:CreditCard'/>\n" +
  "    </sequence>\n" +
  "   </complexType>\n" +
  "   <complexType name='Fee'>\n" +
  "    <sequence>\n" +
  "     <element name='code' nillable='true' type='xsd:string'/>\n" +
  "     <element name='amount' type='xsd:double'/>\n" +
  "     <element name='quantity' type='xsd:int'/>\n" +
  "    </sequence>\n" +
  "   </complexType>\n" +
  "   <complexType name='CreditCard'>\n" +
  "    <sequence>\n" +
  "     <element name='name' nillable='true' type='xsd:string'/>\n" +
  "     <element name='expires' nillable='true' type='xsd:date'/>\n" +
  "     <element name='number' nillable='true' type='xsd:string'/>\n" +
  "     <element name='postalCode' nillable='true' type='xsd:string'/>\n" +
  "    </sequence>\n" +
  "   </complexType>\n" +
  "   <complexType name='Response'>\n" +
  "    <sequence>\n" +
  "     <element name='salePostingReferenceText' nillable='true' \n" +
  "type='xsd:string'/>\n" +
  "     <element name='authorization' nillable='true' type='xsd:string'/>\n" +
  "     <element name='hostResponseCode' nillable='true' type='xsd:string'/>\n" +
  "     <element name='hostResponseMessage' nillable='true' type='xsd:string'/>\n" +
  "     <element name='protoBaseResponseCode' nillable='true' type='xsd:string'/>\n" +
  "     <element name='protoBaseResponseMessage' nillable='true' \n" +
  "type='xsd:string'/>\n" +
  "     <element name='transactionReferenceNumber' nillable='true' \n" +
  "type='xsd:string'/>\n" +
  "     <element name='reasonCode' nillable='true' type='xsd:string'/>\n" +
  "     <element name='returnCode' nillable='true' type='xsd:string'/>\n" +
  "     <element name='accountingDate' nillable='true' type='xsd:date'/>\n" +
  "    </sequence>\n" +
  "   </complexType>\n" +
  "  </schema>\n" +
  "  <schema targetNamespace='urn:ram' xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
  "   <complexType name='ArrayOf_tns1_Fee'>\n" +
  "    <complexContent>\n" +
  "     <restriction base='SOAP-ENC:Array'>\n" +
  "      <attribute ref='SOAP-ENC:arrayType' wsdl:arrayType='tns1:Fee[]'/>\n" +
  "     </restriction>\n" +
  "    </complexContent>\n" +
  "   </complexType>\n" +
  "   <complexType name='ArrayOf_tns1_RamData'>\n" +
  "    <complexContent>\n" +
  "     <restriction base='SOAP-ENC:Array'>\n" +
  "      <attribute ref='SOAP-ENC:arrayType' wsdl:arrayType='tns1:RamData[]'/>\n" +
  "     </restriction>\n" +
  "    </complexContent>\n" +
  "   </complexType>\n" +
  "   <element name='ArrayOf_tns1_RamData' nillable='true' \n" +
  "type='intf:ArrayOf_tns1_RamData'/>\n" +
  "   <complexType name='ArrayOf_tns1_Response'>\n" +
  "    <complexContent>\n" +
  "     <restriction base='SOAP-ENC:Array'>\n" +
  "      <attribute ref='SOAP-ENC:arrayType' wsdl:arrayType='tns1:Response[]'/>\n" +
  "     </restriction>\n" +
  "    </complexContent>\n" +
  "   </complexType>\n" +
  "   <element name='ArrayOf_tns1_Response' nillable='true' \n" +
  "type='intf:ArrayOf_tns1_Response'/>\n" +
  "  </schema>\n" +
  " </types>\n" +
  "\n" +
  "   <wsdl:message name='validateResponse'>\n" +
  "\n" +
  "      <wsdl:part name='return' type='intf:ArrayOf_tns1_Response'/>\n" +
  "\n" +
  "   </wsdl:message>\n" +
  "\n" +
  "   <wsdl:message name='validateRequest'>\n" +
  "\n" +
  "      <wsdl:part name='in0' type='intf:ArrayOf_tns1_RamData'/>\n" +
  "\n" +
  "   </wsdl:message>\n" +
  "\n" +
  "   <wsdl:portType name='Ram'>\n" +
  "\n" +
  "      <wsdl:operation name='validate' parameterOrder='in0'>\n" +
  "\n" +
  "         <wsdl:input message='intf:validateRequest'/>\n" +
  "\n" +
  "         <wsdl:output message='intf:validateResponse'/>\n" +
  "\n" +
  "      </wsdl:operation>\n" +
  "\n" +
  "   </wsdl:portType>\n" +
  "\n" +
  "   <wsdl:binding name='RamSoapBinding' type='intf:Ram'>\n" +
  "\n" +
  "      <wsdlsoap:binding style='rpc' \n" +
  "transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "\n" +
  "      <wsdl:operation name='validate'>\n" +
  "\n" +
  "         <wsdlsoap:operation soapAction=''/>\n" +
  "\n" +
  "         <wsdl:input>\n" +
  "\n" +
  "            <wsdlsoap:body \n" +
  "encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' namespace='urn:ram' \n" +
  "use='encoded'/>\n" +
  "\n" +
  "         </wsdl:input>\n" +
  "\n" +
  "         <wsdl:output>\n" +
  "\n" +
  "            <wsdlsoap:body \n" +
  "encodingStyle='http://schemas.xmlsoap.org/soap/encoding/' namespace='urn:ram' \n" +
  "use='encoded'/>\n" +
  "\n" +
  "         </wsdl:output>\n" +
  "\n" +
  "      </wsdl:operation>\n" +
  "\n" +
  "   </wsdl:binding>\n" +
  "\n" +
  "   <wsdl:service name='RamService'>\n" +
  "\n" +
  "      <wsdl:port binding='intf:RamSoapBinding' name='Ram'>\n" +
  "\n" +
  "         <wsdlsoap:address location='http://localhost:8080/axis/services/Ram'/>\n" +
  "\n" +
  "      </wsdl:port>\n" +
  "\n" +
  "   </wsdl:service>\n" +
  "\n" +
  "</wsdl:definitions>\n";

  public void testRam() throws Exception {
    parseString(ram, "ram.wsdl");
  }

  private static final String multiRefTest =
  "<?xml version='1.0' ?>\n" +
  "\n" +
  "<definitions name='urn:MultiRefTest'\n" +
  "             targetNamespace='urn:MultiRefTest2'\n" +
  "             xmlns:tns='urn:MultiRefTest2'\n" +
  "             xmlns:typens='urn:MultiRefTest2'\n" +
  "             xmlns:xsd='http://www.w3.org/1999/XMLSchema'\n" +
  "             xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'\n" +
  "             xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "\n" +
  "  <!-- type defs -->\n" +
  "  <types>\n" +
  "    <xsd:schema targetNamespace='urn:MultiRefTest2'\n" +
  "                xmlns:xsd='http://www.w3.org/1999/XMLSchema'>\n" +
  "                \n" +
  "              \n" +
  "      <xsd:complexType name='nodebase'>\n" +
  "        <xsd:all>\n" +
  "            <xsd:element name='left' type='typens:node' xsd:nillable='true'/>\n" +
  "            <xsd:element name='right' type='typens:node' xsd:nillable='true'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='node'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:extension base='typens:nodebase'>\n" +
  "            <xsd:all>\n" +
  "              <xsd:element name='data' type='xsd:int'/>\n" +
  "            </xsd:all>\n" +
  "          </xsd:extension>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "    </xsd:schema>\n" +
  "  </types>\n" +
  "\n" +
  "  <!-- message declns -->\n" +
  "  <message name='InNodes'>\n" +
  "    <part name='root' type='typens:node'/>\n" +
  "  </message>\n" +
  "  <message name='OutNodes'>\n" +
  "    <part name='root' type='typens:node'/>\n" +
  "    <part name='rc' type='xsd:int'/>\n" +
  "  </message>\n" +
  "  <message name='InNodes2'>\n" +
  "    <part name='root1' type='typens:node'/>\n" +
  "    <part name='root2' type='typens:node'/>\n" +
  "  </message>\n" +
  "  <message name='OutNodes2'>\n" +
  "    <part name='root1' type='typens:node'/>\n" +
  "    <part name='root2' type='typens:node'/>\n" +
  "    <part name='rc' type='xsd:int'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <!-- port type declns -->\n" +
  "  <portType name='MultiRefTest'>\n" +
  "    <operation name='testSimpleTree'>\n" +
  "      <input  message='tns:InNodes'/>\n" +
  "      <output message='tns:OutNodes'/>\n" +
  "    </operation>\n" +
  "    <operation name='testDiamond'>\n" +
  "      <input  message='tns:InNodes'/>\n" +
  "      <output message='tns:OutNodes'/>\n" +
  "    </operation>\n" +
  "    <operation name='testLoop'>\n" +
  "      <input  message='tns:InNodes'/>\n" +
  "      <output message='tns:OutNodes'/>\n" +
  "    </operation>\n" +
  "    <operation name='testSelfRef'>\n" +
  "      <input  message='tns:InNodes'/>\n" +
  "      <output message='tns:OutNodes'/>\n" +
  "    </operation>\n" +
  "    <operation name='testSameArgs'>\n" +
  "      <input  message='tns:InNodes2'/>\n" +
  "      <output message='tns:OutNodes2'/>\n" +
  "    </operation>\n" +
  "    <operation name='testArgsRefSameNode'>\n" +
  "      <input  message='tns:InNodes2'/>\n" +
  "      <output message='tns:OutNodes2'/>\n" +
  "    </operation>\n" +
  "    <operation name='testArgsRefEachOther'>\n" +
  "      <input  message='tns:InNodes2'/>\n" +
  "      <output message='tns:OutNodes2'/>\n" +
  "    </operation>\n" +
  "  </portType>\n" +
  "\n" +
  "  <!-- binding declns -->\n" +
  "  <binding name='MultiRefTestSOAPBinding' type='tns:MultiRefTest'>\n" +
  "    <soap:binding style='rpc'\n" +
  "                  transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='testSimpleTree'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testDiamond'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testLoop'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "         <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testSelfRef'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "         <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testSameArgs'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "         <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testArgsRefSameNode'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "         <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='testArgsRefEachOther'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "         <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body use='encoded'\n" +
  "                   namespace='urn:MultiRefTest2'\n" +
  "                   encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "  </binding>\n" +
  "\n" +
  "  <!-- service decln -->\n" +
  "  <service name='MultiRefTestService'>\n" +
  "    <port name='MultiRefTest' binding='tns:MultiRefTestSOAPBinding'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/MultiRefTest'/>\n" +
  "    </port>\n" +
  "  </service>\n" +
  "\n" +
  "</definitions>\n";

  public void testMultiRefTest() throws Exception {
    parseString(multiRefTest, "MultiRefTest.wsdl");
  }

  private static final String header =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "\n" +
  "<wsdl:definitions \n" +
  " xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' \n" +
  " xmlns:xsd='http://www.w3.org/2001/XMLSchema' \n" +
  " xmlns:tns='urn:header.wsdl.test' \n" +
  " targetNamespace='urn:header.wsdl.test' \n" +
  " xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "\n" +
  "  <wsdl:types>\n" +
  "    <xsd:schema>\n" +
  "\n" +
  "      <xsd:element name='Header' type='tns:HeaderType'/>\n" +
  "      <xsd:complexType name='HeaderType'>\n" +
  "        <xsd:all>\n" +
  "          <element name='e1' type='xsd:string'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "    </xsd:schema>\n" +
  "  </wsdl:types>\n" +
  "\n" +
  "  <wsdl:message name='op1Request'>\n" +
  "    <wsdl:part name='parm1' type='xsd:int'/>\n" +
  "    <wsdl:part name='parm2' type='xsd:string'/>\n" +
  "    <wsdl:part name='header' element='tns:Header'/>\n" +
  "  </wsdl:message>\n" +
  "\n" +
  "  <wsdl:message name='op1Response'>\n" +
  "<!--\n" +
  "    <wsdl:part name='header' element='tns:Header'/>\n" +
  "-->\n" +
  "    <wsdl:part name='return' type='xsd:float'/>\n" +
  "  </wsdl:message>\n" +
  "\n" +
  "  <wsdl:message name='op1Fault'>\n" +
  "    <wsdl:part name='message' type='xsd:string'/>\n" +
  "  </wsdl:message>\n" +
  "\n" +
  "  <wsdl:message name='op2Request'>\n" +
  "    <wsdl:part name='parm' type='xsd:int'/>\n" +
  "    <wsdl:part name='header' element='tns:Header'/>\n" +
  "  </wsdl:message>\n" +
  "\n" +
  "  <wsdl:message name='op2Response'/>\n" +
  "\n" +
  "  <wsdl:message name='implicitFault'>\n" +
  "    <wsdl:part name='message' type='xsd:string'/>\n" +
  "  </wsdl:message>\n" +
  "\n" +
  "  <wsdl:portType name='PortType'>\n" +
  "    <wsdl:operation name='op1'>\n" +
  "      <wsdl:input message='tns:op1Request'/>\n" +
  "      <wsdl:output message='tns:op1Response'/>\n" +
  "      <wsdl:fault name='op1Fault' message='tns:op1Fault'/>\n" +
  "    </wsdl:operation>\n" +
  "    <wsdl:operation name='op2'>\n" +
  "      <wsdl:input message='tns:op2Request'/>\n" +
  "      <wsdl:output message='tns:op2Response'/>\n" +
  "    </wsdl:operation>\n" +
  "  </wsdl:portType>\n" +
  "\n" +
  "  <wsdl:binding name='Binding' type='tns:PortType'>\n" +
  "    <soap:binding transport='http://schemas.xmlsoap.org/soap/http' style='rpc'/>\n" +
  "    <wsdl:operation name='op1'>\n" +
  "      <soap:operation/>\n" +
  "      <wsdl:input>\n" +
  "        <soap:body use='literal'\n" +
  "                   namespace='urn:header.wsdl.test'\n" +
  "                   parts='parm2'/>\n" +
  "        <soap:header message='tns:op1Request'\n" +
  "                     part='parm1'\n" +
  "                     use='literal'\n" +
  "                     namespace='urn:header.wsdl.test'>\n" +
  "          <soap:headerfault message='tns:op1Fault'\n" +
  "                            part='message'\n" +
  "                            use='literal' \n" +
  "                            namespace='urn:header.wsdl.test'/>\n" +
  "        </soap:header>\n" +
  "        <soap:header message='tns:op1Request'\n" +
  "                     part='header'\n" +
  "                     use='literal'\n" +
  "                     namespace='urn:header.wsdl.test'>\n" +
  "          <soap:headerfault message='tns:op1Fault'\n" +
  "                            part='message'\n" +
  "                            use='literal' \n" +
  "                            namespace='urn:header.wsdl.test'/>\n" +
  "        </soap:header>\n" +
  "      </wsdl:input>\n" +
  "      <wsdl:output>\n" +
  "<!--\n" +
  "        <soap:body use='literal'\n" +
  "                   parts='header'\n" +
  "                   namespace='urn:header.wsdl.test'/>\n" +
  "-->\n" +
  "        <soap:header message='tns:op1Response'\n" +
  "                     part='return'\n" +
  "                     use='literal'\n" +
  "                     namespace='urn:header.wsdl.test'>\n" +
  "        </soap:header>\n" +
  "      </wsdl:output>\n" +
  "    </wsdl:operation>\n" +
  "    <wsdl:operation name='op2'>\n" +
  "      <soap:operation/>\n" +
  "      <wsdl:input>\n" +
  "        <soap:body use='literal'\n" +
  "                   namespace='urn:header.wsdl.test'\n" +
  "                   parts='parm'/>\n" +
  "        <soap:header message='tns:op2Request'\n" +
  "                     part='header'\n" +
  "                     use='literal'\n" +
  "                     namespace='urn:header.wsdl.test'>\n" +
  "          <soap:headerfault message='tns:implicitFault'\n" +
  "                            part='message'\n" +
  "                            use='literal' \n" +
  "                            namespace='urn:header.wsdl.test'/>\n" +
  "        </soap:header>\n" +
  "      </wsdl:input>\n" +
  "      <wsdl:output>\n" +
  "        <soap:body use='literal'\n" +
  "                   namespace='urn:header.wsdl.test'/>\n" +
  "      </wsdl:output>\n" +
  "    </wsdl:operation>\n" +
  "  </wsdl:binding>\n" +
  "\n" +
  "  <wsdl:service name='HeaderService'>\n" +
  "    <wsdl:port name='header' binding='tns:Binding'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/header'/>\n" +
  "    </wsdl:port>\n" +
  "  </wsdl:service>\n" +
  "\n" +
  "</wsdl:definitions>\n";

  public void testHeader() throws Exception {
    parseString(header, "header.wsdl");
  }

  private static final String dimeRpc =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<wsdl:definitions name='SOAPBuilders' xmlns='http://soapinterop.org/attachments/wsdl' xmlns:types='http://soapinterop.org/attachments/xsd' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/' xmlns:soap-enc='http://schemas.xmlsoap.org/soap/encoding/' xmlns:dime='http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/' xmlns:content='http://schemas.xmlsoap.org/ws/2002/04/content-type/' targetNamespace='http://soapinterop.org/attachments/wsdl'>\n" +
  " <wsdl:types>\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' targetNamespace='http://soapinterop.org/attachments/xsd'>\n" +
  "     <!-- <import namespace='http://schemas.xmlsoap.org/soap/encoding/' schemaLocation='http://schemas.xmlsoap.org/soap/encoding/' /> -->\n" +
  "     <import namespace='http://schemas.xmlsoap.org/ws/2002/04/content-type/'/>\n" +
  "     <import namespace='http://schemas.xmlsoap.org/ws/2002/04/reference/'/>\n" +
  "     <complexType name='ReferencedBinary'>\n" +
  "       <simpleContent>\n" +
  "         <restriction base='soap-enc:base64Binary'>\n" +
  "           <annotation>\n" +
  "             <appinfo>\n" +
  "               <content:mediaType value='application/octetstream'/>\n" +
  "             </appinfo>\n" +
  "           </annotation>\n" +
  "           <attributeGroup ref='soap-enc:commonAttributes'/>\n" +
  "         </restriction>\n" +
  "       </simpleContent>\n" +
  "     </complexType>\n" +
  "     <complexType name='ArrayOfBinary'>\n" +
  "       <complexContent>\n" +
  "         <restriction base='soap-enc:Array'>\n" +
  "           <attribute ref='soap-enc:arrayType' wsdl:arrayType='types:ReferencedBinary[]'/>\n" +
  "         </restriction>\n" +
  "       </complexContent>\n" +
  "     </complexType>\n" +
  "     <complexType name='ReferencedText'>\n" +
  "       <simpleContent>\n" +
  "         <restriction base='soap-enc:base64Binary'>\n" +
  "           <annotation>\n" +
  "             <appinfo>\n" +
  "               <content:mediaType value='text/plain'/>\n" +
  "             </appinfo>\n" +
  "           </annotation>\n" +
  "           <attributeGroup ref='soap-enc:commonAttributes'/>\n" +
  "         </restriction>\n" +
  "       </simpleContent>\n" +
  "     </complexType>\n" +
  "   </schema>\n" +
  " </wsdl:types>\n" +
  " <wsdl:message name='EchoAttachmentIn'>\n" +
  "   <wsdl:part name='In' type='types:ReferencedBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentOut'>\n" +
  "   <wsdl:part name='Out' type='types:ReferencedBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentsIn'>\n" +
  "   <wsdl:part name='In' type='types:ArrayOfBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentsOut'>\n" +
  "   <wsdl:part name='Out' type='types:ArrayOfBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsBase64In'>\n" +
  "   <wsdl:part name='In' type='types:ReferencedBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsBase64Out'>\n" +
  "   <wsdl:part name='Out' type='xsd:base64Binary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoBase64AsAttachmentIn'>\n" +
  "   <wsdl:part name='In' type='xsd:base64Binary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoBase64AsAttachmentOut'>\n" +
  "   <wsdl:part name='Out' type='types:ReferencedBinary'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoUnrefAttachmentsIn' />\n" +
  " <wsdl:message name='EchoUnrefAttachmentsOut' />\n" +
  " <wsdl:message name='EchoAttachmentAsStringIn'>\n" +
  "   <wsdl:part name='In' type='types:ReferencedText'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsStringOut'>\n" +
  "   <wsdl:part name='Out' type='xsd:string'/>\n" +
  " </wsdl:message>\n" +
  " \n" +
  " <wsdl:portType name='AttachmentsPortType'>\n" +
  "   <wsdl:operation name='EchoAttachment'>\n" +
  "     <wsdl:input name='EchoAttachmentInput' message='EchoAttachmentIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentOutput' message='EchoAttachmentOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachments'>\n" +
  "     <wsdl:input name='EchoAttachmentsInput' message='EchoAttachmentsIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentsOutput' message='EchoAttachmentsOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsBase64'>\n" +
  "     <wsdl:input name='EchoAttachmentAsBase64Input' message='EchoAttachmentAsBase64In'/>\n" +
  "     <wsdl:output name='EchoAttachmentAsBase64Output' message='EchoAttachmentAsBase64Out'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoBase64AsAttachment'>\n" +
  "     <wsdl:input name='EchoBase64AsAttachmentInput' message='EchoBase64AsAttachmentIn'/>\n" +
  "     <wsdl:output name='EchoBase64AsAttachmentOutput' message='EchoBase64AsAttachmentOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoUnrefAttachments'>\n" +
  "     <wsdl:input name='EchoUnrefAttachmentsInput' message='EchoUnrefAttachmentsIn'/>\n" +
  "     <wsdl:output name='EchoUnrefAttachmentsOutput' message='EchoUnrefAttachmentsOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsString'>\n" +
  "     <wsdl:input  name='EchoAttachmentAsStringInput'  message='EchoAttachmentAsStringIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentAsStringOutput' message='EchoAttachmentAsStringOut'/>\n" +
  "   </wsdl:operation>\n" +
  " </wsdl:portType>\n" +
  " <wsdl:binding name='AttachmentsBinding' type='AttachmentsPortType'>\n" +
  "   <soap:binding style='rpc' transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "   <wsdl:operation name='EchoAttachment'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachments'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentsInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentsOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsBase64'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentAsBase64Input'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentAsBase64Output'>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoBase64AsAttachment'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoBase64AsAttachmentInput'>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoBase64AsAttachmentOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoUnrefAttachments'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoUnrefAttachmentsInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoUnrefAttachmentsOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsString'>\n" +
  "     <soap:operation style='rpc' soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentAsStringInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentAsStringOutput'>\n" +
  "       <soap:body use='encoded' namespace='http://soapinterop.org/attachments/' encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  " </wsdl:binding>\n" +
  "    <wsdl:service name='DimeRPCInterop'>\n" +
  "     <wsdl:port name='DimeRPCSoapPort' binding='AttachmentsBinding'>\n" +
  "            <soap:address location='http://localhost:8080/axis/services/DimeRPCSoapPort'/>\n" +
  "        </wsdl:port>\n" +
  "    </wsdl:service>\n" +
  "</wsdl:definitions>\n";

  public void testDimeRpc() throws Exception {
    parseString(dimeRpc, "dime-rpc.wsdl");
  }

  private static final String dimeDoc =
  "<?xml version='1.0' encoding='utf-8'?>\n" +
  "<wsdl:definitions name='SOAPBuilders' xmlns='http://soapinterop.org/attachments/wsdl' xmlns:types='http://soapinterop.org/attachments/xsd' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/' xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/' xmlns:dime='http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/' xmlns:content='http://schemas.xmlsoap.org/ws/2002/04/content-type/' xmlns:ref='http://schemas.xmlsoap.org/ws/2002/04/reference/' targetNamespace='http://soapinterop.org/attachments/wsdl'>\n" +
  " <wsdl:types>\n" +
  "   <schema xmlns='http://www.w3.org/2001/XMLSchema' targetNamespace='http://soapinterop.org/attachments/xsd' elementFormDefault='qualified' attributeFormDefault='qualified'>\n" +
  "     <import namespace='http://schemas.xmlsoap.org/ws/2002/04/reference/'/>\n" +
  "     <import namespace='http://schemas.xmlsoap.org/ws/2002/04/content-type/'/>\n" +
  "     <complexType name='ReferencedBinary'>\n" +
  "       <simpleContent>\n" +
  "         <restriction base='xsd:base64Binary'>\n" +
  "           <annotation>\n" +
  "             <appinfo>\n" +
  "               <content:mediaType value='application/octetstream'/>\n" +
  "             </appinfo>\n" +
  "           </annotation>\n" +
  "           <attribute ref='ref:location' use='optional'/>\n" +
  "         </restriction>\n" +
  "       </simpleContent>\n" +
  "     </complexType>\n" +
  "     <complexType name='ReferencedText'>\n" +
  "       <simpleContent>\n" +
  "         <restriction base='xsd:base64Binary'>\n" +
  "           <annotation>\n" +
  "             <appinfo>\n" +
  "               <content:mediaType value='text/plain'/>\n" +
  "             </appinfo>\n" +
  "           </annotation>\n" +
  "           <attribute ref='ref:location' use='optional'/>\n" +
  "         </restriction>\n" +
  "       </simpleContent>\n" +
  "     </complexType>\n" +
  "     <element name='EchoAttachment' type='types:EchoAttachment'/>\n" +
  "     <element name='EchoAttachmentResponse' type='types:EchoAttachmentResponse'/>\n" +
  "     <complexType name='EchoAttachment'>\n" +
  "       <sequence>\n" +
  "         <element name='In' type='types:ReferencedBinary'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <complexType name='EchoAttachmentResponse'>\n" +
  "       <sequence>\n" +
  "         <element name='Out' type='types:ReferencedBinary'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <element name='EchoAttachments' type='types:Attachments'/>\n" +
  "     <element name='EchoAttachmentsResponse' type='types:Attachments'/>\n" +
  "     <complexType name='Attachments'>\n" +
  "       <sequence>\n" +
  "         <element name='Item' minOccurs='0' maxOccurs='unbounded' type='types:ReferencedBinary'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <element name='EchoAttachmentAsBase64' type='types:EchoAttachment'/>\n" +
  "     <element name='EchoAttachmentAsBase64Response' type='types:base64Out'/>\n" +
  "     <element name='EchoBase64AsAttachment' type='types:base64In'/>\n" +
  "     <element name='EchoBase64AsAttachmentResponse' type='types:EchoAttachmentResponse'/>\n" +
  "     <complexType name='base64In'>\n" +
  "       <sequence>\n" +
  "         <element name='In' type='xsd:base64Binary'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <complexType name='base64Out'>\n" +
  "       <sequence>\n" +
  "         <element name='Out' type='xsd:base64Binary'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <element name='EchoUnrefAttachments' type='types:emptyType'/>\n" +
  "     <element name='EchoUnrefAttachmentsResponse' type='types:emptyType'/>\n" +
  "     <complexType name='emptyType'>\n" +
  "       <sequence/>\n" +
  "     </complexType>\n" +
  "     <element name='EchoAttachmentAsString' type='types:EchoAttachmentAsString'/>\n" +
  "     <element name='EchoAttachmentAsStringResponse' type='types:EchoAttachmentAsStringResponse'/>\n" +
  "     <complexType name='EchoAttachmentAsString'>\n" +
  "       <sequence>\n" +
  "         <element name='In' type='types:ReferencedText'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "     <complexType name='EchoAttachmentAsStringResponse'>\n" +
  "       <sequence>\n" +
  "         <element name='Out' type='xsd:string'/>\n" +
  "       </sequence>\n" +
  "     </complexType>\n" +
  "   </schema>\n" +
  " </wsdl:types>\n" +
  " <wsdl:message name='EchoAttachmentIn'>\n" +
  "   <wsdl:part name='In' element='types:EchoAttachment'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentOut'>\n" +
  "   <wsdl:part name='Out' element='types:EchoAttachmentResponse'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentsIn'>\n" +
  "   <wsdl:part name='In' element='types:EchoAttachments'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentsOut'>\n" +
  "   <wsdl:part name='Out' element='types:EchoAttachmentsResponse'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsBase64In'>\n" +
  "   <wsdl:part name='In' element='types:EchoAttachmentAsBase64'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsBase64Out'>\n" +
  "   <wsdl:part name='Out' element='types:EchoAttachmentAsBase64Response'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoBase64AsAttachmentIn'>\n" +
  "   <wsdl:part name='In' element='types:EchoBase64AsAttachment'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoBase64AsAttachmentOut'>\n" +
  "   <wsdl:part name='Out' element='types:EchoBase64AsAttachmentResponse'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoUnrefAttachmentsIn'>\n" +
  "   <wsdl:part name='In' element='types:EchoUnrefAttachments'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoUnrefAttachmentsOut'>\n" +
  "   <wsdl:part name='Out' element='types:EchoUnrefAttachmentsResponse'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsStringIn'>\n" +
  "   <wsdl:part name='In' element='types:EchoAttachmentAsString'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:message name='EchoAttachmentAsStringOut'>\n" +
  "   <wsdl:part name='Out' element='types:EchoAttachmentAsStringResponse'/>\n" +
  " </wsdl:message>\n" +
  " <wsdl:portType name='AttachmentsPortType'>\n" +
  "   <wsdl:operation name='EchoAttachment'>\n" +
  "     <wsdl:input name='EchoAttachmentInput' message='EchoAttachmentIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentOutput' message='EchoAttachmentOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachments'>\n" +
  "     <wsdl:input name='EchoAttachmentsInput' message='EchoAttachmentsIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentsOutput' message='EchoAttachmentsOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsBase64'>\n" +
  "     <wsdl:input name='EchoAttachmentAsBase64Input' message='EchoAttachmentAsBase64In'/>\n" +
  "     <wsdl:output name='EchoAttachmentAsBase64Output' message='EchoAttachmentAsBase64Out'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoBase64AsAttachment'>\n" +
  "     <wsdl:input name='EchoBase64AsAttachmentInput' message='EchoBase64AsAttachmentIn'/>\n" +
  "     <wsdl:output name='EchoBase64AsAttachmentOutput' message='EchoBase64AsAttachmentOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoUnrefAttachments'>\n" +
  "     <wsdl:input name='EchoUnrefAttachmentsInput' message='EchoUnrefAttachmentsIn'/>\n" +
  "     <wsdl:output name='EchoUnrefAttachmentsOutput' message='EchoUnrefAttachmentsOut'/>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsString'>\n" +
  "     <wsdl:input name='EchoAttachmentAsStringInput' message='EchoAttachmentAsStringIn'/>\n" +
  "     <wsdl:output name='EchoAttachmentAsStringOutput' message='EchoAttachmentAsStringOut'/>\n" +
  "   </wsdl:operation>\n" +
  " </wsdl:portType>\n" +
  " <wsdl:binding name='AttachmentsBinding' type='AttachmentsPortType'>\n" +
  "   <soap:binding style='document' transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "   <wsdl:operation name='EchoAttachment'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachments'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentsInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentsOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsBase64'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentAsBase64Input'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentAsBase64Output'>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoBase64AsAttachment'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoBase64AsAttachmentInput'>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoBase64AsAttachmentOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoUnrefAttachments'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoUnrefAttachmentsInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoUnrefAttachmentsOutput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  "   <wsdl:operation name='EchoAttachmentAsString'>\n" +
  "     <soap:operation soapAction='http://soapinterop.org/attachments/'/>\n" +
  "     <wsdl:input name='EchoAttachmentAsStringInput'>\n" +
  "       <dime:message layout='http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout' wsdl:required='true'/>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:input>\n" +
  "     <wsdl:output name='EchoAttachmentAsStringOutput'>\n" +
  "       <soap:body use='literal'/>\n" +
  "     </wsdl:output>\n" +
  "   </wsdl:operation>\n" +
  " </wsdl:binding>\n" +
  "    <wsdl:service name='DimeDOCInterop'>\n" +
  "     <wsdl:port name='DimeDOCSoapPort' binding='AttachmentsBinding'>\n" +
  "            <soap:address location='http://localhost:8080/axis/services/DimeDOCSoapPort'/>\n" +
  "        </wsdl:port>\n" +
  "    </wsdl:service>\n" +
  "</wsdl:definitions>\n";

  public void testDimeDoc() throws Exception {
    parseString(dimeDoc, "dime-doc.wsdl");
  }

  private final static String comprehensiveTypes1 =
  "<?xml version='1.0' ?>\n" +
  "\n" +
  "<definitions \n" +
  "    name='comprehensive types test'\n" +
  "    targetNamespace='urn:comprehensive-service.types.wsdl.test'\n" +
  "    xmlns:tns='urn:comprehensive-service.types.wsdl.test'\n" +
  "    xmlns:typens='urn:comprehensive-types.types.wsdl.test'\n" +
  "    xmlns:typens2='urn:comprehensive-types2.types.wsdl.test'\n" +
  "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n" +
  "    xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'\n" +
  "    xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/'\n" +
  "    xmlns:wsdl='http://schemas.xmlsoap.org/wsdl/'\n" +
  "    xmlns='http://schemas.xmlsoap.org/wsdl/'>\n" +
  "\n" +
  "  <!-- type defs -->\n" +
  "  <types>\n" +
  "    <xsd:schema \n" +
  "        targetNamespace='urn:comprehensive-types.types.wsdl.test'\n" +
  "        xmlns:xsd='http://www.w3.org/1999/XMLSchema'\n" +
  "        xmlns:xsd2='http://www.w3.org/2001/XMLSchema'>\n" +
  "\n" +
  "      <xsd:simpleType name='simple'>\n" +
  "        <xsd:restriction base='xsd:string' />\n" +
  "      </xsd:simpleType>\n" +
  "      \n" +
  "      <xsd:simpleType name='simpleDate'>\n" +
  "        <xsd:restriction base='xsd:date' />\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:simpleType name='simpleDateTime'>\n" +
  "        <xsd:restriction base='xsd2:dateTime' />\n" +
  "      </xsd:simpleType>\n" +
  "      \n" +
  "      <xsd:simpleType name='enum'>\n" +
  "        <xsd:restriction base='xsd:string'>\n" +
  "          <xsd:enumeration value='one'/>                      \n" +
  "          <xsd:enumeration value='two'/>                    \n" +
  "          <xsd:enumeration value='three'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:complexType name='array'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:restriction base='soapenc:Array'>\n" +
  "            <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='xsd:string[]'/>\n" +
  "          </xsd:restriction>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='array_of_base64'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:restriction base='soapenc:Array'>\n" +
  "            <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='xsd:base64Binary[]'/>\n" +
  "          </xsd:restriction>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='complexAll'>\n" +
  "        <xsd:all>\n" +
  "          <xsd:element name='areaCode' type='xsd:int'/>\n" +
  "          <xsd:element name='exchange' type='xsd:string'/>\n" +
  "          <xsd:element name='number' type='xsd:string'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='complexSequence'>\n" +
  "        <xsd:sequence>\n" +
  "          <xsd:element name='areaCode' type='xsd:int'/>\n" +
  "          <xsd:element name='exchange' type='xsd:string'/>\n" +
  "          <xsd:element name='number' type='xsd:string'/>\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='complexChoice'>\n" +
  "        <xsd:choice>\n" +
  "          <xsd:element name='choiceA' type='xsd:int'/>\n" +
  "          <xsd:element name='choiceB' type='xsd:string'/>\n" +
  "          <xsd:element name='choiceC' type='xsd:string'/>\n" +
  "        </xsd:choice>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <!-- Test proper construction of types with names that match common java.lang classes -->\n" +
  "      <xsd:complexType name='Object'>\n" +
  "        <xsd:sequence>\n" +
  "           <xsd:element name='test' type='xsd:string' />\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:complexType name='String'>\n" +
  "        <xsd:sequence>\n" +
  "           <xsd:element name='test' type='xsd:string' />\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:complexType name='Class'>\n" +
  "        <xsd:sequence>\n" +
  "           <xsd:element name='test' type='xsd:string' />\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:element name='elemWComplex'>\n" +
  "        <xsd:complexType>\n" +
  "          <xsd:all>\n" +
  "            <xsd:element name='one' type='typens:simple'/> <!-- Ref to a simple type -->\n" +
  "            <xsd:element name='dateone' type='typens:simpleDate'/>\n" +
  "            <xsd:element name='datetwo' type='typens:simpleDateTime'/>\n" +
  "            <xsd:element name='two' type='typens2:fwd'/> <!-- Forward type use to dif namespace -->\n" +
  "            <xsd:element ref='typens2:three'/>           <!-- Forward ref use to a dif namespace -->\n" +
  "            <xsd:element ref='typens:enumValue' maxOccurs='unbounded' /> <!-- ref & maxOccurs -->\n" +
  "            <xsd:element name='enum1' type='typens:enumString'/>\n" +
  "            <xsd:element name='enum2' type='typens:enumInt'/>\n" +
  "            <xsd:element name='enum3' type='typens:enumLong'/>\n" +
  "            <xsd:element name='enum4' type='typens:enumFloat'/>\n" +
  "            <xsd:element name='enum5' type='typens:enumDouble'/>\n" +
  "            <xsd:element name='enum6' type='typens:enumShort'/>\n" +
  "            <xsd:element name='enum7' type='typens:enumByte'/>\n" +
  "            <xsd:element name='enum8' type='typens:enumInteger'/>\n" +
  "            <xsd:element name='enum9' type='typens:enumNMTOKEN'/>\n" +
  "            <xsd:element name='soapint' type='soapenc:int'/>\n" +
  "            <xsd:element name='nested' type='typens2:b'/>\n" +
  "            <xsd:element name='D_TSENT' type='xsd:string'/> <!-- test strange name -->\n" +
  "            <xsd:element name='optArray' minOccurs='0' maxOccurs='1' type='typens:array' />\n" +
  "            <xsd:element name='byteArray' type='typens:array_of_base64' />\n" +
  "            <xsd:element name='parm' type='typens:StringParameter' />\n" +
  "\n" +
  "            <!-- Test for types that are similarly named to common java lang classes -->\n" +
  "            <xsd:element name='myObject' type='typens:Object' />\n" +
  "            <xsd:element name='myString' type='typens:String' />\n" +
  "            <xsd:element name='myClass' type='typens:Class' />\n" +
  "\n" +
  "            <!-- Test anonymous type with an element that has maxOccurs -->\n" +
  "            <xsd:element name='logEntry' minOccurs='0' maxOccurs='unbounded'>\n" +
  "              <xsd:complexType>\n" +
  "                <xsd:sequence>\n" +
  "                   <xsd:element name='name' type='xsd:string'/>\n" +
  "                   <xsd:element name='value' type='xsd:string'/>\n" +
  "                </xsd:sequence>\n" +
  "              </xsd:complexType>\n" +
  "            </xsd:element>\n" +
  "          </xsd:all>\n" +
  "          <xsd:attribute name='attr' type='typens:enum' />\n" +
  "          <xsd:attribute name='parmAttr' type='typens:StringParameter' />\n" +
  "          <xsd:attribute name='enumAttr'>\n" +
  "            <xsd:simpleType>\n" +
  "              <xsd:restriction base='xsd:string'>\n" +
  "                <xsd:enumeration value='one'/>                      \n" +
  "                <xsd:enumeration value='two'/>                    \n" +
  "                <xsd:enumeration value='three'/>                    \n" +
  "              </xsd:restriction>\n" +
  "            </xsd:simpleType>\n" +
  "          </xsd:attribute>\n" +
  "        </xsd:complexType>\n" +
  "      </xsd:element>\n" +
  "\n" +
  "      <xsd:element name='enumValue' type='typens:enum'/>\n" +
  "\n" +
  "      <xsd:complexType name='time'>\n" +
  "        <xsd:simpleContent>\n" +
  "          <xsd:extension base='xsd:string'>\n" +
  "            <xsd:attribute name='DST' type='xsd:boolean' />\n" +
  "          </xsd:extension>\n" +
  "        </xsd:simpleContent>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='complexWComplex'>\n" +
  "        <xsd:sequence>\n" +
  "          <xsd:element name='stock_quote'>\n" +
  "            <xsd:complexType>\n" +
  "              <xsd:attribute name='symbol' type='xsd:string'/> \n" +
  "              <xsd:sequence>\n" +
  "                <!-- forward simple type ref -->\n" +
  "                <xsd:element name='time' type='typens:time'/>\n" +
  "                <xsd:element name='change' type='typens:simpleFwd'/>  \n" +
  "                <xsd:element name='pctchange' type='xsd:string'/>\n" +
  "                <xsd:element name='bid' type='xsd:string'/>\n" +
  "                <xsd:element name='ask' type='xsd:string'/>\n" +
  "                <xsd:element name='choice' type='typens:complexChoice'/>\n" +
  "              </xsd:sequence>\n" +
  "              <xsd:attribute name='last' type='xsd:string'/>\n" +
  "            </xsd:complexType>\n" +
  "          </xsd:element>\n" +
  "          <xsd:element name='outside' type='xsd:int'/>\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='emptyFault'>\n" +
  "        <xsd:sequence />\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:element name='faultElement' type='typens:faultType' />\n" +
  "      <xsd:complexType name='faultType'>\n" +
  "        <xsd:sequence>\n" +
  "           <xsd:element name='userData' type='xsd:string' />\n" +
  "        </xsd:sequence>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:complexType name='emptyComplexType'>\n" +
  "        <xsd:sequence />\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <xsd:simpleType name='simpleFwd'>\n" +
  "        <xsd:restriction base='typens:simple' />\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <!-- The following definitions validate forward refs -->\n" +
  "      <xsd:complexType name='arrayM'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:restriction base='soapenc:Array'>\n" +
  "            <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='typens:arrayM2[]'/>\n" +
  "          </xsd:restriction>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:complexType name='arrayM2'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:restriction base='soapenc:Array'>\n" +
  "            <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='typens:arrayM3[]'/>\n" +
  "          </xsd:restriction>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:complexType name='arrayM3'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:restriction base='soapenc:Array'>\n" +
  "            <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='xsd:int[]'/>\n" +
  "          </xsd:restriction>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "\n" +
  "      <!-- The following is a simple test of inheritance with types -->\n" +
  "      <xsd:complexType name='Animal'>\n" +
  "        <xsd:all>\n" +
  "          <xsd:element name='Name' nillable='true' type='xsd:string'/>\n" +
  "        </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:element name='Animal' nillable='true' type='typens:Animal'/>\n" +
  "      <xsd:complexType name='Cat'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:extension base='typens:Animal'>\n" +
  "            <xsd:all>\n" +
  "              <xsd:element name='Purr' nillable='true' type='xsd:string'/>\n" +
  "            </xsd:all>\n" +
  "          </xsd:extension>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "      <xsd:element name='Cat' nillable='true' type='typens:Cat'/>\n" +
  "\n" +
  "      <!-- Even though PersionCat is not directly referenced, it should\n" +
  "      be generated and registered because its base class (Cat) is referenced -->\n" +
  "      <xsd:complexType name='PersionCat'>\n" +
  "        <xsd:complexContent>\n" +
  "          <xsd:extension base='typens:Cat'>\n" +
  "            <xsd:all>\n" +
  "              <xsd:element name='Color' type='xsd:string'/>\n" +
  "              <xsd:element name='Toy' /> <!-- Defaults to xsd:anyType -->\n" +
  "            </xsd:all>\n" +
  "          </xsd:extension>\n" +
  "        </xsd:complexContent>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <!-- Even though Yarn is not directly referenced, it should\n" +
  "      be generated and registered because the Toy above is an anyType -->\n" +
  "      <xsd:complexType name='Yarn'>\n" +
  "         <xsd:all>\n" +
  "           <xsd:element name='Color' type='xsd:string'/>\n" +
  "         </xsd:all>\n" +
  "      </xsd:complexType>\n" +
  "\n" +
  "      <!-- The following test all of the valid enum basic types -->\n" +
  "      <xsd:simpleType name='enumString'>\n" +
  "        <xsd:restriction base='xsd:string'>\n" +
  "          <xsd:enumeration value='Ho Ho Ho'/> <!-- Blanks should force value<1..n> names -->        \n" +
  "          <xsd:enumeration value='He He He'/>                    \n" +
  "          <xsd:enumeration value='Ha Ha Ha'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumInt'>\n" +
  "        <xsd:restriction base='xsd:int'>\n" +
  "          <xsd:enumeration value='1'/>\n" +
  "          <xsd:enumeration value='2'/>                    \n" +
  "          <xsd:enumeration value='3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumLong'>\n" +
  "        <xsd:restriction base='xsd:long'>\n" +
  "          <xsd:enumeration value='1'/>\n" +
  "          <xsd:enumeration value='2'/>                    \n" +
  "          <xsd:enumeration value='3'/>                    \n" +
  "          <xsd:enumeration value='-9223372036854775808'/>                    \n" +
  "          <xsd:enumeration value='9223372036854775807'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumShort'>\n" +
  "        <xsd:restriction base='xsd:short'>\n" +
  "          <xsd:enumeration value='1'/>\n" +
  "          <xsd:enumeration value='2'/>                    \n" +
  "          <xsd:enumeration value='3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumFloat'>\n" +
  "        <xsd:restriction base='xsd:float'>\n" +
  "          <xsd:enumeration value='1.1'/>\n" +
  "          <xsd:enumeration value='2.2'/>                    \n" +
  "          <xsd:enumeration value='3.3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumDouble'>\n" +
  "        <xsd:restriction base='xsd:double'>\n" +
  "          <xsd:enumeration value='1.1'/>\n" +
  "          <xsd:enumeration value='2.2'/>                    \n" +
  "          <xsd:enumeration value='3.3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "      <xsd:simpleType name='enumByte'>\n" +
  "        <xsd:restriction base='xsd:byte'>\n" +
  "          <xsd:enumeration value='1'/>\n" +
  "          <xsd:enumeration value='2'/>                    \n" +
  "          <xsd:enumeration value='3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:simpleType name='enumInteger'>\n" +
  "        <xsd:restriction base='xsd:integer'>\n" +
  "          <xsd:enumeration value='1'/>\n" +
  "          <xsd:enumeration value='2'/>                    \n" +
  "          <xsd:enumeration value='3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:simpleType name='flowDirectionType'>\n" +
  "        <xsd:restriction base='xsd:string'>\n" +
  "          <xsd:enumeration value='in'/>\n" +
  "          <xsd:enumeration value='inOut'/>\n" +
  "          <xsd:enumeration value='out'/>\n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:simpleType name='enumNMTOKEN'>\n" +
  "        <xsd:restriction base='xsd:NMTOKEN'> <!-- axis provided simple type -->\n" +
  "          <xsd:enumeration value='NameToken1'/>\n" +
  "          <xsd:enumeration value='NameToken2'/>                    \n" +
  "          <xsd:enumeration value='NameToken3'/>                    \n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "      <xsd:simpleType name='AIDType'>\n" +
  "        <xsd:restriction base='xsd:hexBinary'>\n" +
  "          <xsd:maxLength value='16'/>\n" +
  "          <xsd:minLength value='5'/>\n" +
  "        </xsd:restriction>\n" +
  "      </xsd:simpleType>\n" +
  "\n" +
  "<xsd:simpleType name='passModeType'>\n" +
  "    <xsd:restriction base='xsd:string'>\n" +
  "        <xsd:enumeration value='passByValue'/>\n" +
  "        <xsd:enumeration value='passByReference'/>\n" +
  "     </xsd:restriction>\n" +
  "</xsd:simpleType>\n" +
  "\n" +
  "\n" +
  "<xsd:element name='StringParameter' type='typens:StringParameter' />\n" +
  " <xsd:complexType name='StringParameter'>\n" +
  "   <xsd:simpleContent>\n" +
  "      <xsd:extension base='xsd:string'>\n" +
  "        <xsd:attribute name='numBytes' type='soapenc:int'/>\n" +
  "        <xsd:attribute name='storageEncoding' type='xsd:string'/>\n" +
  "        <xsd:attribute name='direction' type='typens:flowDirectionType'/>  <!-- in, out, or inOut -->\n" +
  "        <xsd:attribute name='passMode' type='typens:passModeType'/>  <!-- passByValue or passByReference -->\n" +
  "        <xsd:attribute name='description' type='xsd:string'/>\n" +
  "      </xsd:extension>\n" +
  "   </xsd:simpleContent>\n" +
  "</xsd:complexType>\n" +
  "\n" +
  "    </xsd:schema>\n" +
  "\n" +
  "\n" +
  "    <xsd:schema \n" +
  "        targetNamespace='urn:comprehensive-types2.types.wsdl.test'\n" +
  "        xmlns:xsd='http://www.w3.org/1999/XMLSchema'>\n" +
  "        <xsd:complexType name='fwd'>\n" +
  "          <xsd:complexContent>\n" +
  "            <xsd:restriction base='soapenc:Array'>\n" +
  "              <xsd:attribute ref='soapenc:arrayType' wsdl:arrayType='xsd:QName[]'/>\n" +
  "            </xsd:restriction>\n" +
  "          </xsd:complexContent>\n" +
  "        </xsd:complexType>\n" +
  "\n" +
  "        <!-- Collection of enums -->\n" +
  "        <xsd:element name='three' type='typens:enum' maxOccurs='unbounded' />\n" +
  "\n" +
  "\n" +
  "        <!-- Test for nested defined complexType -->\n" +
  "        <xsd:element name='a' type='xsd:short' />\n" +
  "        <xsd:complexType name='b'>\n" +
  "          <xsd:sequence>\n" +
  "            <xsd:complexType name='a'>\n" +
  "              <xsd:sequence>\n" +
  "                <xsd:element name='c' type='xsd:int' />\n" +
  "              </xsd:sequence>\n" +
  "            </xsd:complexType>\n" +
  "            <xsd:element name='d' type='typens2:a' />\n" +
  "          </xsd:sequence>\n" +
  "        </xsd:complexType>\n" +
  "\n" +
  "  <!-- Test for a WS-I-like type that we used to fail on. -->\n" +
  "        <xsd:complexType name='SimpleAnyURIType'>\n" +
  "          <xsd:simpleContent>\n" +
  "            <xsd:extension base='xsd:anyURI'>\n" +
  "            </xsd:extension>\n" +
  "          </xsd:simpleContent>\n" +
  "        </xsd:complexType>\n" +
  "\n" +
  "    </xsd:schema>\n" +
  "\n" +
  "  </types>\n" +
  "\n" +
  "  <!-- message declns -->\n" +
  "  <message name='empty'/>\n" +
  "\n" +
  "  <message name='allPrimitives'>\n" +
  "    <part name='string' type='xsd:string'/>\n" +
  "    <part name='integer' type='xsd:integer'/>\n" +
  "    <part name='int' type='xsd:int'/>\n" +
  "    <part name='long' type='xsd:long'/>\n" +
  "    <part name='short' type='xsd:short'/>\n" +
  "    <part name='decimal' type='xsd:decimal'/>\n" +
  "    <part name='float' type='xsd:float'/>\n" +
  "    <part name='double' type='xsd:double'/>\n" +
  "    <part name='boolean' type='xsd:boolean'/>\n" +
  "    <part name='byte' type='xsd:byte'/>\n" +
  "    <part name='QName' type='xsd:QName'/>\n" +
  "    <part name='dateTime' type='xsd:dateTime'/>\n" +
  "    <part name='base64Binary' type='xsd:base64Binary'/>\n" +
  "    <part name='hexBinary' type='xsd:hexBinary'/>\n" +
  "    <part name='soapString' type='soapenc:string'/>\n" +
  "    <part name='soapBoolean' type='soapenc:boolean'/>\n" +
  "    <part name='soapFloat' type='soapenc:float'/>\n" +
  "    <part name='soapDouble' type='soapenc:double'/>\n" +
  "    <part name='soapDecimal' type='soapenc:decimal'/>\n" +
  "    <part name='soapInt' type='soapenc:int'/>\n" +
  "    <part name='soapShort' type='soapenc:short'/>\n" +
  "    <part name='soapBase64' type='soapenc:base64'/>\n" +
  "    <part name='time' type='xsd:time'/>\n" +
  "    <part name='unsignedLong' type='xsd:unsignedLong'/>\n" +
  "    <part name='unsignedInt' type='xsd:unsignedInt'/>\n" +
  "    <part name='unsignedShort' type='xsd:unsignedShort'/>\n" +
  "    <part name='unsignedByte' type='xsd:unsignedByte'/>\n" +
  "    <part name='nonNegativeInteger' type='xsd:nonNegativeInteger'/>\n" +
  "    <part name='positiveInteger' type='xsd:positiveInteger'/>\n" +
  "    <part name='nonPositiveInteger' type='xsd:nonPositiveInteger'/>\n" +
  "    <part name='negativeInteger' type='xsd:negativeInteger'/>\n" +
  "    <part name='anyURI' type='xsd:anyURI'/>\n" +
  "    <part name='gYear' type='xsd:gYear'/>\n" +
  "    <part name='gMonth' type='xsd:gMonth'/>\n" +
  "    <part name='gDay' type='xsd:gDay'/>\n" +
  "    <part name='gYearMonth' type='xsd:gYearMonth'/>\n" +
  "    <part name='gMonthDay' type='xsd:gMonthDay'/>\n" +
  "  </message>\n" +
  "  <message name='inputBoolean'>\n" +
  "    <part name='inBoolean' type='xsd:boolean'/>\n" +
  "    <part name='boolean' type='xsd:boolean'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputBoolean'>\n" +
  "    <part name='outBoolean' type='xsd:boolean'/>\n" +
  "    <part name='boolean' type='xsd:boolean'/>\n" +
  "  </message> \n" +
  "\n" +
  "  <message name='inputByte'>\n" +
  "    <part name='inByte' type='xsd:byte'/>\n" +
  "    <part name='byte' type='xsd:byte'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputByte'>\n" +
  "    <part name='outByte' type='xsd:byte'/>\n" +
  "    <part name='byte' type='xsd:byte'/>\n" +
  "  </message> \n" +
  "\n" +
  "  <message name='inputShort'>\n" +
  "    <part name='inShort' type='xsd:short'/>\n" +
  "    <part name='short' type='xsd:short'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputShort'>\n" +
  "    <part name='outShort' type='xsd:short'/>\n" +
  "    <part name='short' type='xsd:short'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputInt'>\n" +
  "    <part name='inInt' type='xsd:int'/>\n" +
  "    <part name='int' type='xsd:int'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputInt'>\n" +
  "    <part name='outInt' type='xsd:int'/>\n" +
  "    <part name='int' type='xsd:int'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputLong'>\n" +
  "    <part name='inLong' type='xsd:long'/>\n" +
  "    <part name='long' type='xsd:long'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputLong'>\n" +
  "    <part name='outLong' type='xsd:long'/>\n" +
  "    <part name='long' type='xsd:long'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputFloat'>\n" +
  "    <part name='inFloat' type='xsd:float'/>\n" +
  "    <part name='float' type='xsd:float'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputFloat'>\n" +
  "    <part name='outFloat' type='xsd:float'/>\n" +
  "    <part name='float' type='xsd:float'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputDouble'>\n" +
  "    <part name='inDouble' type='xsd:double'/>\n" +
  "    <part name='double' type='xsd:double'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputDouble'>\n" +
  "    <part name='outDouble' type='xsd:double'/>\n" +
  "    <part name='double' type='xsd:double'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputString'>\n" +
  "    <part name='inString' type='xsd:string'/>\n" +
  "    <part name='string' type='xsd:string'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputString'>\n" +
  "    <part name='outString' type='xsd:string'/>\n" +
  "    <part name='string' type='xsd:string'/>\n" +
  "  </message> \n" +
  "\n" +
  "  <message name='inputInteger'>\n" +
  "    <part name='inInteger' type='xsd:integer'/>\n" +
  "    <part name='integer' type='xsd:integer'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputInteger'>\n" +
  "    <part name='outInteger' type='xsd:integer'/>\n" +
  "    <part name='integer' type='xsd:integer'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputDecimal'>\n" +
  "    <part name='inDecimal' type='xsd:decimal'/>\n" +
  "    <part name='decimal' type='xsd:decimal'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputDecimal'>\n" +
  "    <part name='outDecimal' type='xsd:decimal'/>\n" +
  "    <part name='decimal' type='xsd:decimal'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputDateTime'>\n" +
  "    <part name='inDateTime' type='xsd:dateTime'/>\n" +
  "    <part name='dateTime' type='xsd:dateTime'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputDateTime'>\n" +
  "    <part name='outDateTime' type='xsd:dateTime'/>\n" +
  "    <part name='dateTime' type='xsd:dateTime'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputBase64Binary'>\n" +
  "    <part name='inBase64Binary' type='xsd:base64Binary'/>\n" +
  "    <part name='base64Binary' type='xsd:base64Binary'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputBase64Binary'>\n" +
  "    <part name='outBase64Binary' type='xsd:base64Binary'/>\n" +
  "    <part name='base64Binary' type='xsd:base64Binary'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputQName'>\n" +
  "    <part name='inQName' type='xsd:QName'/>\n" +
  "    <part name='qName' type='xsd:QName'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputQName'>\n" +
  "    <part name='outQName' type='xsd:QName'/>\n" +
  "    <part name='qName' type='xsd:QName'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputHexBinary'>\n" +
  "    <part name='inHexBinary' type='xsd:hexBinary'/>\n" +
  "    <part name='hexBinary' type='xsd:hexBinary'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputHexBinary'>\n" +
  "    <part name='outHexBinary' type='xsd:hexBinary'/>\n" +
  "    <part name='hexBinary' type='xsd:hexBinary'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputTime'>\n" +
  "    <part name='inTime' type='xsd:time'/>\n" +
  "    <part name='time' type='xsd:time'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputTime'>\n" +
  "    <part name='outTime' type='xsd:time'/>\n" +
  "    <part name='time' type='xsd:time'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputUnsignedLong'>\n" +
  "    <part name='inUnsignedLong' type='xsd:unsignedLong'/>\n" +
  "    <part name='unsignedLong' type='xsd:unsignedLong'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputUnsignedLong'>\n" +
  "    <part name='outUnsignedLong' type='xsd:unsignedLong'/>\n" +
  "    <part name='unsignedLong' type='xsd:unsignedLong'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputUnsignedInt'>\n" +
  "    <part name='inUnsignedInt' type='xsd:unsignedInt'/>\n" +
  "    <part name='unsignedInt' type='xsd:unsignedInt'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputUnsignedInt'>\n" +
  "    <part name='outUnsignedInt' type='xsd:unsignedInt'/>\n" +
  "    <part name='unsignedInt' type='xsd:unsignedInt'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputUnsignedShort'>\n" +
  "    <part name='inUnsignedShort' type='xsd:unsignedShort'/>\n" +
  "    <part name='unsignedShort' type='xsd:unsignedShort'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputUnsignedShort'>\n" +
  "    <part name='outUnsignedShort' type='xsd:unsignedShort'/>\n" +
  "    <part name='unsignedShort' type='xsd:unsignedShort'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputUnsignedByte'>\n" +
  "    <part name='inUnsignedByte' type='xsd:unsignedByte'/>\n" +
  "    <part name='unsignedByte' type='xsd:unsignedByte'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputUnsignedByte'>\n" +
  "    <part name='outUnsignedByte' type='xsd:unsignedByte'/>\n" +
  "    <part name='unsignedByte' type='xsd:unsignedByte'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputNonNegativeInteger'>\n" +
  "    <part name='inNonNegativeInteger' type='xsd:nonNegativeInteger'/>\n" +
  "    <part name='NonNegativeInteger' type='xsd:nonNegativeInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputNonNegativeInteger'>\n" +
  "    <part name='outNonNegativeInteger' type='xsd:nonNegativeInteger'/>\n" +
  "    <part name='NonNegativeInteger' type='xsd:nonNegativeInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputPositiveInteger'>\n" +
  "    <part name='inPositiveInteger' type='xsd:positiveInteger'/>\n" +
  "    <part name='PositiveInteger' type='xsd:positiveInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputPositiveInteger'>\n" +
  "    <part name='outPositiveInteger' type='xsd:positiveInteger'/>\n" +
  "    <part name='PositiveInteger' type='xsd:positiveInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputNonPositiveInteger'>\n" +
  "    <part name='inNonPositiveInteger' type='xsd:nonPositiveInteger'/>\n" +
  "    <part name='NonPositiveInteger' type='xsd:nonPositiveInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputNonPositiveInteger'>\n" +
  "    <part name='outNonPositiveInteger' type='xsd:nonPositiveInteger'/>\n" +
  "    <part name='NonPositiveInteger' type='xsd:nonPositiveInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputNegativeInteger'>\n" +
  "    <part name='inNegativeInteger' type='xsd:negativeInteger'/>\n" +
  "    <part name='NegativeInteger' type='xsd:negativeInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputNegativeInteger'>\n" +
  "    <part name='outNegativeInteger' type='xsd:negativeInteger'/>\n" +
  "    <part name='NegativeInteger' type='xsd:negativeInteger'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputAnyURI'>\n" +
  "    <part name='inAnyURI' type='xsd:anyURI'/>\n" +
  "    <part name='anyURI' type='xsd:anyURI'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputAnyURI'>\n" +
  "    <part name='outAnyURI' type='xsd:anyURI'/>\n" +
  "    <part name='anyURI' type='xsd:anyURI'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSimpleAnyURI'>\n" +
  "    <part name='inAnyURI' type='typens2:SimpleAnyURIType'/>\n" +
  "    <part name='anyURI' type='typens2:SimpleAnyURIType'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSimpleAnyURI'>\n" +
  "    <part name='outAnyURI' type='typens2:SimpleAnyURIType'/>\n" +
  "    <part name='anyURI' type='typens2:SimpleAnyURIType'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputYear'>\n" +
  "    <part name='inYear' type='xsd:gYear'/>\n" +
  "    <part name='Year' type='xsd:gYear'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputYear'>\n" +
  "    <part name='outYear' type='xsd:gYear'/>\n" +
  "    <part name='Year' type='xsd:gYear'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputMonth'>\n" +
  "    <part name='inMonth' type='xsd:gMonth'/>\n" +
  "    <part name='Month' type='xsd:gMonth'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputMonth'>\n" +
  "    <part name='outMonth' type='xsd:gMonth'/>\n" +
  "    <part name='Month' type='xsd:gMonth'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputDay'>\n" +
  "    <part name='inDay' type='xsd:gDay'/>\n" +
  "    <part name='Day' type='xsd:gDay'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputDay'>\n" +
  "    <part name='outDay' type='xsd:gDay'/>\n" +
  "    <part name='Day' type='xsd:gDay'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputYearMonth'>\n" +
  "    <part name='inYearMonth' type='xsd:gYearMonth'/>\n" +
  "    <part name='YearMonth' type='xsd:gYearMonth'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputYearMonth'>\n" +
  "    <part name='outYearMonth' type='xsd:gYearMonth'/>\n" +
  "    <part name='YearMonth' type='xsd:gYearMonth'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputMonthDay'>\n" +
  "    <part name='inMonthDay' type='xsd:gMonthDay'/>\n" +
  "    <part name='MonthDay' type='xsd:gMonthDay'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputMonthDay'>\n" +
  "    <part name='outMonthDay' type='xsd:gMonthDay'/>\n" +
  "    <part name='MonthDay' type='xsd:gMonthDay'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapString'>\n" +
  "    <part name='inSoapString' type='soapenc:string'/>\n" +
  "    <part name='soapencString' type='soapenc:string'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapString'>\n" +
  "    <part name='outSoapString' type='soapenc:string'/>\n" +
  "    <part name='soapencString' type='soapenc:string'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapBoolean'>\n" +
  "    <part name='inSoapBoolean' type='soapenc:boolean'/>\n" +
  "    <part name='soapencBoolean' type='soapenc:boolean'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapBoolean'>\n" +
  "    <part name='outSoapBoolean' type='soapenc:boolean'/>\n" +
  "    <part name='soapencBoolean' type='soapenc:boolean'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapFloat'>\n" +
  "    <part name='inSoapFloat' type='soapenc:float'/>\n" +
  "    <part name='soapencFloat' type='soapenc:float'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapFloat'>\n" +
  "    <part name='outSoapFloat' type='soapenc:float'/>\n" +
  "    <part name='soapencFloat' type='soapenc:float'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapDouble'>\n" +
  "    <part name='inSoapDouble' type='soapenc:double'/>\n" +
  "    <part name='soapencDouble' type='soapenc:double'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapDouble'>\n" +
  "    <part name='outSoapDouble' type='soapenc:double'/>\n" +
  "    <part name='soapencDouble' type='soapenc:double'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapDecimal'>\n" +
  "    <part name='inSoapDecimal' type='soapenc:decimal'/>\n" +
  "    <part name='soapencDecimal' type='soapenc:decimal'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapDecimal'>\n" +
  "    <part name='outSoapDecimal' type='soapenc:decimal'/>\n" +
  "    <part name='soapencDecimal' type='soapenc:decimal'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapInt'>\n" +
  "    <part name='inSoapInt' type='soapenc:int'/>\n" +
  "    <part name='soapencInt' type='soapenc:int'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapInt'>\n" +
  "    <part name='outSoapInt' type='soapenc:int'/>\n" +
  "    <part name='soapencInt' type='soapenc:int'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapShort'>\n" +
  "    <part name='inSoapShort' type='soapenc:short'/>\n" +
  "    <part name='soapencShort' type='soapenc:short'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapShort'>\n" +
  "    <part name='outSoapShort' type='soapenc:short'/>\n" +
  "    <part name='soapencShort' type='soapenc:short'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='inputSoapBase64'>\n" +
  "    <part name='inSoapBase64' type='soapenc:base64'/>\n" +
  "    <part name='soapencBase64' type='soapenc:base64'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='outputSoapBase64'>\n" +
  "    <part name='outSoapBase64' type='soapenc:base64'/>\n" +
  "    <part name='soapencBase64' type='soapenc:base64'/>\n" +
  "  </message>\n" +
  "                     \n" +
  "  <message name='enum'>\n" +
  "    <part name='enum' type='typens:enum'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='enumInt'>\n" +
  "    <part name='enumInt' type='typens:enumInt'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='array'>\n" +
  "    <part name='array' type='typens:array'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='arrayM'>\n" +
  "    <part name='arrayM' type='typens:arrayM'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='complexAll'>\n" +
  "    <part name='complexAll' type='typens:complexAll'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='complexSequence'>\n" +
  "    <part name='complexSequence' type='typens:complexSequence'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='elemWComplex'>\n" +
  "    <part name='elemWComplex' element='typens:elemWComplex'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='complexWComplex'>\n" +
  "    <part name='complexWComplex' type='typens:complexWComplex'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='any'>\n" +
  "    <part name='any' type='xsd:anyType'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='animal'>\n" +
  "    <part name='animal' type='typens:Animal'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='cat'>\n" +
  "    <part name='cat' type='typens:Cat'/>\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='emptyFault'>\n" +
  "    <part name='theFault' type='typens:emptyFault' />\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='faultWithElement'>\n" +
  "    <part name='fault2' element='typens:faultElement' />\n" +
  "  </message>\n" +
  "\n" +
  "  <message name='emptyComplexType'>\n" +
  "    <part name='emptyComplexType' type='typens:emptyComplexType' />\n" +
  "  </message>\n" +
  "\n" +
  "  <!-- port type declns -->\n" +
  "  <portType name='TypeTest'>\n" +
  "    <operation name='allPrimitivesIn'>\n" +
  "      <input message='tns:allPrimitives'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='allPrimitivesInout'>\n" +
  "      <input message='tns:allPrimitives'/>\n" +
  "      <output message='tns:allPrimitives'/>\n" +
  "    </operation>\n" +
  "    <operation name='allPrimitivesOut'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:allPrimitives'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumIn'>\n" +
  "      <input message='tns:enum'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumInout'>\n" +
  "      <input message='tns:enum'/>\n" +
  "      <output message='tns:enum'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumOut' parameterOrder='enum'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:enum'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:enum'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntIn'>\n" +
  "      <input message='tns:enumInt'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntInout'>\n" +
  "      <input message='tns:enumInt'/>\n" +
  "      <output message='tns:enumInt'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntOut' parameterOrder='enumInt'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:enumInt'/>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:enumInt'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayIn'>\n" +
  "      <input message='tns:array'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayInout'>\n" +
  "      <input message='tns:array'/>\n" +
  "      <output message='tns:array'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayOut' parameterOrder='array'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:array'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:array'/>\n" +
  "    </operation>\n" +
  "\n" +
  "    <operation name='arrayMIn'>\n" +
  "      <input message='tns:arrayM'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMInout'>\n" +
  "      <input message='tns:arrayM'/>\n" +
  "      <output message='tns:arrayM'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMOut' parameterOrder='arrayM'>\n" +
  "      <!-- BUG: type emptyM not defined ! Replacing by type empty\n" +
  "       for the time being.\n" +
  "      -->\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:arrayM'/>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMReturn'>\n" +
  "      <!-- BUG: type emptyM not defined ! Replacing by type empty\n" +
  "       for the time being.\n" +
  "      -->\n" +
  "      <input message='tns:empty'/>      \n" +
  "      <output message='tns:arrayM'/>\n" +
  "    </operation>\n" +
  "\n" +
  "    <operation name='complexAllIn'>\n" +
  "      <input message='tns:complexAll'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllInout'>\n" +
  "      <input message='tns:complexAll'/>\n" +
  "      <output message='tns:complexAll'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllOut' parameterOrder='complexAll'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexAll'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexAll'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceIn'>\n" +
  "      <input message='tns:complexSequence'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceInout'>\n" +
  "      <input message='tns:complexSequence'/>\n" +
  "      <output message='tns:complexSequence'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceOut' parameterOrder='complexSequence'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexSequence'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexSequence'/>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexIn'>\n" +
  "      <input message='tns:elemWComplex'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexInout'>\n" +
  "      <input message='tns:elemWComplex'/>\n" +
  "      <output message='tns:elemWComplex'/>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexOut' parameterOrder='elemWComplex'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:elemWComplex'/>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:elemWComplex'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexIn'>\n" +
  "      <input message='tns:complexWComplex'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexInout'>\n" +
  "      <input message='tns:complexWComplex'/>\n" +
  "      <output message='tns:complexWComplex'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexOut' parameterOrder='complexWComplex'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexWComplex'/>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:complexWComplex'/>\n" +
  "    </operation>\n";

  private static final String comprehensiveTypes2 =
  "    <operation name='emptyComplexTypeIn' parameterOrder='emptyComplexType'>\n" +
  "      <input message='tns:emptyComplexType'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "      <fault name='emptyFault' message='tns:emptyFault'/>\n" +
  "        <!-- Can't have faults with <part element=''> in an RPC service...\n" +
  "             Don't know what the intent was here, but since no one is actually\n" +
  "             throwing this fault, commenting it out because it's now triggering\n" +
  "             our more-draconian error checking.\n" +
  "             \n" +
  "      <fault name='faultWithElement' message='tns:faultWithElement'/>\n" +
  "      -->\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeInout' parameterOrder='emptyComplexType'>\n" +
  "      <input message='tns:emptyComplexType'/>\n" +
  "      <output message='tns:emptyComplexType'/>\n" +
  "      <fault name='emptyFault' message='tns:emptyFault'/>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeOut' parameterOrder='emptyComplexType'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:emptyComplexType'/>\n" +
  "      <fault name='emptyFault' message='tns:emptyFault'/>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:emptyComplexType'/>\n" +
  "      <fault name='emptyFault' message='tns:emptyFault'/>\n" +
  "    </operation>\n" +
  "    <operation name='anyIn'>\n" +
  "      <input message='tns:any'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='anyInout'>\n" +
  "      <input message='tns:any'/>\n" +
  "      <output message='tns:any'/>\n" +
  "    </operation>\n" +
  "    <operation name='anyOut' parameterOrder='any'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:any'/>\n" +
  "    </operation>\n" +
  "    <operation name='anyReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:any'/>\n" +
  "    </operation>\n" +
  "    <operation name='animalIn'>\n" +
  "      <input message='tns:animal'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='animalInout'>\n" +
  "      <input message='tns:animal'/>\n" +
  "      <output message='tns:animal'/>\n" +
  "    </operation>\n" +
  "    <operation name='animalOut' parameterOrder='animal'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:animal'/>\n" +
  "    </operation>\n" +
  "    <operation name='animalReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:animal'/>\n" +
  "    </operation>\n" +
  "    <operation name='catIn'>\n" +
  "      <input message='tns:cat'/>\n" +
  "      <output message='tns:empty'/>\n" +
  "    </operation>\n" +
  "    <operation name='catInout'>\n" +
  "      <input message='tns:cat'/>\n" +
  "      <output message='tns:cat'/>\n" +
  "    </operation>\n" +
  "    <operation name='catOut' parameterOrder='cat'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:cat'/>\n" +
  "    </operation>\n" +
  "    <operation name='catReturn'>\n" +
  "      <input message='tns:empty'/>\n" +
  "      <output message='tns:cat'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodBoolean'>\n" +
  "      <input message='tns:inputBoolean'/>\n" +
  "      <output message='tns:outputBoolean'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodByte'>\n" +
  "      <input message='tns:inputByte'/>\n" +
  "      <output message='tns:outputByte'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodShort'>\n" +
  "      <input message='tns:inputShort'/>\n" +
  "      <output message='tns:outputShort'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodInt'>\n" +
  "      <input message='tns:inputInt'/>\n" +
  "      <output message='tns:outputInt'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodLong'>\n" +
  "      <input message='tns:inputLong'/>\n" +
  "      <output message='tns:outputLong'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodFloat'>\n" +
  "      <input message='tns:inputFloat'/>\n" +
  "      <output message='tns:outputFloat'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodDouble'>\n" +
  "      <input message='tns:inputDouble'/>\n" +
  "      <output message='tns:outputDouble'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodString'>\n" +
  "      <input message='tns:inputString'/>\n" +
  "      <output message='tns:outputString'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodInteger'>\n" +
  "      <input message='tns:inputInteger'/>\n" +
  "      <output message='tns:outputInteger'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodDecimal'>\n" +
  "      <input message='tns:inputDecimal'/>\n" +
  "      <output message='tns:outputDecimal'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodDateTime'>\n" +
  "      <input message='tns:inputDateTime'/>\n" +
  "      <output message='tns:outputDateTime'/>\n" +
  "    </operation>    \n" +
  "    <!-- Comment out for now because causes compile errors\n" +
  "    <operation name='methodBase64Binary'>\n" +
  "      <input message='tns:inputBase64Binary'/>\n" +
  "      <output message='tns:outputBase64Binary'/>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "    <operation name='methodQName'>\n" +
  "      <input message='tns:inputQName'/>\n" +
  "      <output message='tns:outputQName'/>\n" +
  "    </operation>    \n" +
  "    <!-- Comment out for now because causes compile errors\n" +
  "    <operation name='methodHexBinary'>\n" +
  "      <input message='tns:inputHexBinary'/>\n" +
  "      <output message='tns:outputHexBinary'/>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "    <operation name='methodTime'>\n" +
  "      <input message='tns:inputTime'/>\n" +
  "      <output message='tns:outputTime'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedLong'>\n" +
  "      <input message='tns:inputUnsignedLong'/>\n" +
  "      <output message='tns:outputUnsignedLong'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedInt'>\n" +
  "      <input message='tns:inputUnsignedInt'/>\n" +
  "      <output message='tns:outputUnsignedInt'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedShort'>\n" +
  "      <input message='tns:inputUnsignedShort'/>\n" +
  "      <output message='tns:outputUnsignedShort'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedByte'>\n" +
  "      <input message='tns:inputUnsignedByte'/>\n" +
  "      <output message='tns:outputUnsignedByte'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodNonNegativeInteger'>\n" +
  "      <input message='tns:inputNonNegativeInteger'/>\n" +
  "      <output message='tns:outputNonNegativeInteger'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodPositiveInteger'>\n" +
  "      <input message='tns:inputPositiveInteger'/>\n" +
  "      <output message='tns:outputPositiveInteger'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodNonPositiveInteger'>\n" +
  "      <input message='tns:inputNonPositiveInteger'/>\n" +
  "      <output message='tns:outputNonPositiveInteger'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodNegativeInteger'>\n" +
  "      <input message='tns:inputNegativeInteger'/>\n" +
  "      <output message='tns:outputNegativeInteger'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodAnyURI'>\n" +
  "      <input message='tns:inputAnyURI'/>\n" +
  "      <output message='tns:outputAnyURI'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSimpleAnyURI'>\n" +
  "      <input message='tns:inputSimpleAnyURI'/>\n" +
  "      <output message='tns:outputSimpleAnyURI'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodYear'>\n" +
  "      <input message='tns:inputYear'/>\n" +
  "      <output message='tns:outputYear'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodMonth'>\n" +
  "      <input message='tns:inputMonth'/>\n" +
  "      <output message='tns:outputMonth'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodDay'>\n" +
  "      <input message='tns:inputDay'/>\n" +
  "      <output message='tns:outputDay'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodYearMonth'>\n" +
  "      <input message='tns:inputYearMonth'/>\n" +
  "      <output message='tns:outputYearMonth'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodMonthDay'>\n" +
  "      <input message='tns:inputMonthDay'/>\n" +
  "      <output message='tns:outputMonthDay'/>\n" +
  "    </operation>    \n" +
  "    <operation name='methodSoapString'>\n" +
  "      <input message='tns:inputSoapString'/>\n" +
  "      <output message='tns:outputSoapString'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapBoolean'>\n" +
  "      <input message='tns:inputSoapBoolean'/>\n" +
  "      <output message='tns:outputSoapBoolean'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapFloat'>\n" +
  "      <input message='tns:inputSoapFloat'/>\n" +
  "      <output message='tns:outputSoapFloat'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapDouble'>\n" +
  "      <input message='tns:inputSoapDouble'/>\n" +
  "      <output message='tns:outputSoapDouble'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapDecimal'>\n" +
  "      <input message='tns:inputSoapDecimal'/>\n" +
  "      <output message='tns:outputSoapDecimal'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapInt'>\n" +
  "      <input message='tns:inputSoapInt'/>\n" +
  "      <output message='tns:outputSoapInt'/>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapShort'>\n" +
  "      <input message='tns:inputSoapShort'/>\n" +
  "      <output message='tns:outputSoapShort'/>\n" +
  "    </operation>    \n" +
  "    <!-- Comment out for now because causes compile errors \n" +
  "    <operation name='methodSoapBase64'>\n" +
  "      <input message='tns:inputSoapBase64'/>\n" +
  "      <output message='tns:outputSoapBase64'/>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "  </portType>\n" +
  "\n" +
  "  <!-- binding declns -->\n" +
  "  <binding name='TypeTestBinding' type='tns:TypeTest'>\n" +
  "    <soap:binding\n" +
  "        style='rpc'\n" +
  "        transport='http://schemas.xmlsoap.org/soap/http'/>\n" +
  "    <operation name='allPrimitivesIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='allPrimitivesInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='allPrimitivesOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='enumIntReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "\n" +
  "    <operation name='arrayMIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='arrayMReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "\n" +
  "    <operation name='complexAllIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexAllReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexSequenceReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='elemWComplexReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='complexWComplexReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "      <fault name='emptyFault'>\n" +
  "          <soap:fault name='emptyFault' use='encoded'/>\n" +
  "      </fault>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "      <fault name='emptyFault'>\n" +
  "        <soap:fault name='emptyFault' use='encoded'/>\n" +
  "      </fault>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "      <fault name='emptyFault'>\n" +
  "        <soap:fault name='emptyFault' use='encoded'/>\n" +
  "      </fault>\n" +
  "    </operation>\n" +
  "    <operation name='emptyComplexTypeReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "      <fault name='emptyFault'>\n" +
  "        <soap:fault name='emptyFault' use='encoded'/>\n" +
  "      </fault>\n" +
  "    </operation>\n" +
  "    <operation name='anyIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='anyInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='anyOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='anyReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='animalIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='animalInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='animalOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='animalReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='catIn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='catInout'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='catOut'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='catReturn'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodBoolean'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodByte'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodShort'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodInt'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodLong'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodFloat'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodDouble'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodString'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodInteger'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodDecimal'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodDateTime'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <!-- Comment out for now because causes compile errors\n" +
  "    <operation name='methodBase64Binary'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "    <operation name='methodQName'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <!-- Comment out for now because causes compile errors\n" +
  "    <operation name='methodHexBinary'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "    <operation name='methodTime'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedLong'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedInt'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedShort'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodUnsignedByte'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodNonNegativeInteger'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodPositiveInteger'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodNonPositiveInteger'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodNegativeInteger'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    \n" +
  "    <operation name='methodAnyURI'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodSimpleAnyURI'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodYear'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodMonth'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodDay'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodYearMonth'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodMonthDay'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>    \n" +
  "    <operation name='methodSoapString'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapBoolean'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapFloat'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapDouble'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapDecimal'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapInt'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    <operation name='methodSoapShort'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>  \n" +
  "    <!-- Comment out for now because causes compile errors\n" +
  "    <operation name='methodSoapBase64'>\n" +
  "      <soap:operation soapAction=''/>\n" +
  "      <input>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </input>\n" +
  "      <output>\n" +
  "        <soap:body\n" +
  "            use='encoded'\n" +
  "            namespace=''\n" +
  "            encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'/>\n" +
  "      </output>\n" +
  "    </operation>\n" +
  "    -->\n" +
  "  </binding>\n" +
  "\n" +
  "  <!-- service decln -->\n" +
  "  <service name='TypeTestService'>\n" +
  "    <port name='TypeTest' binding='tns:TypeTestBinding'>\n" +
  "      <soap:address location='http://localhost:8080/axis/services/TypeTest'/>\n" +
  "    </port>\n" +
  "  </service>\n" +
  "\n" +
  "</definitions>\n";

  public void testComprehensiveTypes() throws Exception {
      StringBuffer comprehensiveTypesText = new StringBuffer(comprehensiveTypes1);
      comprehensiveTypesText.append(comprehensiveTypes2);
      parseString(comprehensiveTypesText.toString(), "ComprehensiveTypes.wsdl");
  }
}
