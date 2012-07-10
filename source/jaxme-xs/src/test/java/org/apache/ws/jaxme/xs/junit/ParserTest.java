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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAppinfo;
import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSAttributeGroup;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSElementOrAttrRef;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSModelGroup;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.types.XSBoolean;
import org.apache.ws.jaxme.xs.types.XSDate;
import org.apache.ws.jaxme.xs.types.XSDateTime;
import org.apache.ws.jaxme.xs.types.XSDecimal;
import org.apache.ws.jaxme.xs.types.XSDouble;
import org.apache.ws.jaxme.xs.types.XSFloat;
import org.apache.ws.jaxme.xs.types.XSID;
import org.apache.ws.jaxme.xs.types.XSInt;
import org.apache.ws.jaxme.xs.types.XSNMToken;
import org.apache.ws.jaxme.xs.types.XSPositiveInteger;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/** <p>Implements some basic tests for the Schema generator.</p>
 */
public class ParserTest extends ParserTestBase {
	protected void testSimpleTypes(XSParser pParser) throws SAXException, IOException, ParserConfigurationException {
		final String schemaSource =
			"<?xml version='1.0'?>\n" +
			"<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
			"  <xs:simpleType name='a'>\n" +
			"    <xs:restriction base='xs:string'>\n" +
			"      <xs:minLength value='3'/>\n" +
			"    </xs:restriction>\n" +
			"  </xs:simpleType>\n" +
			"\n" +
			"  <xs:simpleType name='b'>\n" +
			"    <xs:list itemType='xs:int'/>\n" +
			"  </xs:simpleType>\n" +
			"\n" +
			"  <xs:simpleType name='c'>\n" +
			"    <xs:union memberTypes='a b'/>\n" +
			"  </xs:simpleType>\n" +
			"</xs:schema>\n";
		
		InputSource isource = new InputSource(new StringReader(schemaSource));
		isource.setSystemId("testSimpleTypes.xsd");
		XSSchema schema = pParser.parse(isource);
		
		XSType[] types = schema.getTypes();
		assertEquals(3, types.length);
		
		XSType t1 = types[0];
		assertEquals(new XsQName((String) null, "a"), t1.getName());
		XSSimpleType st1 = assertSimpleType(t1);
		assertAtomicType(st1);
		XSType t1_1 = assertRestriction(st1);
		assertEquals(XSString.getInstance(), t1_1);
		
		XSType t2 = types[1];
		assertEquals(new XsQName((String) null, "b"), t2.getName());
		XSSimpleType st2 = assertSimpleType(t2);
		XSListType lt = assertListType(st2);
		XSType it = lt.getItemType();
		assertNotNull(it);
		assertEquals(XSInt.getInstance(), it);
		XSSimpleType it2 = assertSimpleType(it);
		assertAtomicType(it2);
		
		XSType t3 = types[2];
		assertEquals(new XsQName((String) null, "c"), t3.getName());
		XSSimpleType st3 = assertSimpleType(t3);
		XSUnionType ut3 = assertUnionType(st3);
		XSType[] memberTypes = ut3.getMemberTypes();
		assertEquals(2, memberTypes.length);
		XSType mt3_1 = memberTypes[0];
		assertEquals(mt3_1, t1);
		XSType mt3_2 = memberTypes[1];
		assertEquals(mt3_2, t2);
	}
	
	/** <p>Tests whether the basic simple types can be parsed:
	 * Atomic, List, and Union.</p>
	 * @throws Exception
	 */
	public void testSimpleTypes() throws Exception {
		XSParser xsParser = newXSParser();
		testSimpleTypes(xsParser);
		JAXBParser jaxbParser = newJAXBParser();
		testSimpleTypes(jaxbParser);
	}

  protected void testAttributes(XSParser pParser) throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:attribute name='a' type='xs:string'/>\n" +
      "\n" +
      "  <xs:attribute name='b'>\n" +
      "    <xs:simpleType>\n" +
      "      <xs:restriction base='xs:int'/>\n" +
      "    </xs:simpleType>\n" +
      "  </xs:attribute>\n" +
      "\n" +
      "  <xs:attribute name='c' type='xs:int'/>\n" +
      "</xs:schema>\n";

    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testAttributes.xsd");
    XSSchema schema = pParser.parse(isource);

    XSAttributable[] attr = schema.getAttributes();
    assertEquals(3, attr.length);

    XSAttribute attr1 = (XSAttribute) attr[0];
    assertTrue(attr1.isGlobal());
    assertEquals(new XsQName((String) null, "a"), attr1.getName());
    XSType t1 = attr1.getType();
    assertEquals(XSString.getInstance(), t1);
    XSSimpleType st1 = assertSimpleType(attr1.getType());
    assertAtomicType(st1);

    XSAttribute attr2 = (XSAttribute) attr[1];
    assertTrue(attr2.isGlobal());
    assertEquals(new XsQName((String) null, "b"), attr2.getName());
    XSType t2 = attr2.getType();
    assertTrue(!XSInt.getInstance().equals(t2));
    XSType t2_1 = assertRestriction(assertSimpleType(t2));
    assertEquals(XSInt.getInstance(), t2_1);
    XSSimpleType st2 = assertSimpleType(t2);
    assertAtomicType(st2);

    XSAttribute attr3 = (XSAttribute) attr[2];
    assertTrue(attr3.isGlobal());
    assertEquals(new XsQName((String) null, "c"), attr3.getName());
    XSType t3 = attr3.getType();
    assertEquals(XSInt.getInstance(), t3);
    XSSimpleType st3 = assertSimpleType(t3);
    assertAtomicType(st3);
  }

  /** Tests typical attribute uses.
   */
  public void testAttributes() throws Exception {
    XSParser xsParser = newXSParser();
    testAttributes(xsParser);
    JAXBParser jaxbParser = newJAXBParser();
    testAttributes(jaxbParser);
  }

  protected void testAttributeGroups(XSParser pParser) throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:attribute name='a' type='xs:string'/>\n" +
      "  <xs:attribute name='b' type='xs:int'/>\n" +
      "\n" +
      "  <xs:attributeGroup name='c'>\n" +
      "    <xs:attribute name='d' type='xs:dateTime'/>\n" +
      "    <xs:attribute name='e' type='xs:float'/>\n" +
      "    <xs:attribute ref='a'/>\n" +
      "  </xs:attributeGroup>\n" +
      "\n" +
      "  <xs:attributeGroup name='f'>\n" +
      "    <xs:attribute name='g' type='xs:double'/>\n" +
      "    <xs:attributeGroup ref='c'/>\n" +
      "    <xs:attribute ref='b'/>\n" +
      "  </xs:attributeGroup>\n" +
      "</xs:schema>\n";

    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testAttributeGroups.xsd");
    XSSchema schema = pParser.parse(isource);

    XSAttributeGroup[] groups = schema.getAttributeGroups();
    assertNotNull(groups);
    assertEquals(2, groups.length);

    XSAttributeGroup ag1 = groups[0];
    assertEquals(new XsQName((String) null, "c"), ag1.getName());
    XSAttributable[] attr1 = ag1.getAttributes();
    assertEquals(3, attr1.length);
    XSAttribute attr1_1 = (XSAttribute) attr1[0];
    assertEquals(new XsQName((String) null, "d"), attr1_1.getName());
    assertEquals(XSDateTime.getInstance(), attr1_1.getType());
    XSAttribute attr1_2 = (XSAttribute) attr1[1];
    assertEquals(new XsQName((String) null, "e"), attr1_2.getName());
    assertEquals(XSFloat.getInstance(), attr1_2.getType());
    XSAttribute attr1_3 = (XSAttribute) attr1[2];
    assertEquals(new XsQName((String) null, "a"), attr1_3.getName());
    assertEquals(XSString.getInstance(), attr1_3.getType());

    XSAttributeGroup ag2 = groups[1];
    assertEquals(new XsQName((String) null, "f"), ag2.getName());
    XSAttributable[] attr2 = ag2.getAttributes();
    assertEquals(5, attr2.length);
    XSAttribute attr2_1 = (XSAttribute) attr2[0];
    assertEquals(new XsQName((String) null, "g"), attr2_1.getName());
    assertEquals(XSDouble.getInstance(), attr2_1.getType());
    XSAttribute attr2_2 = (XSAttribute) attr2[1];
    assertEquals(new XsQName((String) null, "d"), attr2_2.getName());
    assertEquals(XSDateTime.getInstance(), attr2_2.getType());
    XSAttribute attr2_3 = (XSAttribute) attr2[2];
    assertEquals(new XsQName((String) null, "e"), attr2_3.getName());
    assertEquals(XSFloat.getInstance(), attr2_3.getType());
    XSAttribute attr2_4 = (XSAttribute) attr2[3];
    assertEquals(new XsQName((String) null, "a"), attr2_4.getName());
    assertEquals(XSString.getInstance(), attr2_4.getType());
    XSAttribute attr2_5 = (XSAttribute) attr2[4];
    assertEquals(new XsQName((String) null, "b"), attr2_5.getName());
    assertEquals(XSInt.getInstance(), attr2_5.getType());
  }

  /** Tests typical attribute group uses.
   */
  public void testAttributeGroups() throws Exception {
    XSParser xsParser = newXSParser();
    testAttributeGroups(xsParser);
    JAXBParser jaxbParser = newJAXBParser();
    testAttributeGroups(jaxbParser);
  }

  protected void testElements(XSParser pParser) throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:attribute name='id' type='xs:ID'/>\n" +
      "\n" +
      "  <xs:element name='a' type='xs:string'/>\n" +
      "\n" +
      "  <xs:element name='b'>\n" +
      "    <xs:simpleType>\n" +
      "      <xs:restriction base='xs:float'/>\n" +
      "    </xs:simpleType>\n" +
      "  </xs:element>\n" +
      "\n" +
      "  <xs:element name='c'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:annotation>\n" +
      "        <xs:documentation>\n" +
      "          The type of 'c'.\n" +
      "        </xs:documentation>\n" +
      "      </xs:annotation>\n" +
      "      <xs:sequence>\n" +
      "        <xs:element ref='a'/>\n" +
      "        <xs:element name='d' type='xs:double' minOccurs='0'>\n" +
      "          <xs:annotation>\n" +
      "            <xs:documentation>\n" +
      "              The element 'd'.\n" +
      "            </xs:documentation>\n" +
      "          </xs:annotation>\n" +
      "        </xs:element>\n" +
      "      </xs:sequence>\n" +
      "      <xs:attribute name='e' type='xs:int'>\n" +
      "        <xs:annotation>\n" +
      "          <xs:documentation>\n" +
      "            The attribute 'e'.\n" +
      "          </xs:documentation>\n" +
      "        </xs:annotation>\n" +
      "      </xs:attribute>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "\n" +
      "  <xs:element name='f'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:simpleContent>\n" +
      "        <xs:extension base='xs:int'>\n" +
      "          <xs:attribute name='g' type='xs:boolean'/>\n" +
      "          <xs:attribute ref='id'/>\n" +
      "        </xs:extension>\n" +
      "      </xs:simpleContent>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testElements.xsd");
    XSSchema schema = pParser.parse(isource);

    XSObject[] elements = schema.getElements();
    assertEquals(4, elements.length);

    XSElement e1 = (XSElement) elements[0];
    assertEquals(new XsQName((String) null, "a"), e1.getName());
    XSType t1 = e1.getType();
    assertEquals(XSString.getInstance(), t1);

    XSElement e2 = (XSElement) elements[1];
    assertEquals(new XsQName((String) null, "b"), e2.getName());
    XSType t2 = e2.getType();
    assertEquals(XSFloat.getInstance(), assertRestriction(assertSimpleType(t2)));

    XSElement e3 = (XSElement) elements[2];
    assertEquals(new XsQName((String) null, "c"), e3.getName());
    XSComplexType ct3 = assertComplexType(e3.getType());
    XSParticle p3 = assertComplexContent(ct3);
    XSGroup g3 = assertGroup(p3);
    XSParticle[] particles3 = g3.getParticles();
    assertEquals(2, particles3.length);
    XSElement e3_1 = assertElement(particles3[0]);
    assertEquals(new XsQName((String) null, "a"), e3_1.getName());
    assertEquals(e1.getType(), e3_1.getType());
    XSParticle p3_2 = particles3[1];
    assertEquals(0, p3_2.getMinOccurs());
    XSElement e3_2 = assertElement(p3_2);
    assertEquals(new XsQName((String) null, "d"), e3_2.getName());
    assertEquals(XSDouble.getInstance(), e3_2.getType());
    XSAttributable[] a3 = ct3.getAttributes();
    assertEquals(1, a3.length);
    XSAttribute a3_1 = (XSAttribute) a3[0];
    assertEquals(new XsQName((String) null, "e"), a3_1.getName());
    assertEquals(XSInt.getInstance(), a3_1.getType());

    XSElement e4 = (XSElement) elements[3];
    assertEquals(new XsQName((String) null, "f"), e4.getName());
    XSComplexType ct4 = assertComplexType(e4.getType());
    XSType t4 = assertSimpleContent(ct4).getType();
    assertSimpleType(t4);
    assertEquals(XSInt.getInstance(), t4);
    XSAttributable[] a4 = ct4.getAttributes();
    assertEquals(2, a4.length);
    XSAttribute a4_1 = (XSAttribute) a4[0];
    assertEquals(new XsQName((String) null, "g"), a4_1.getName());
    assertEquals(XSBoolean.getInstance(), a4_1.getType());
    XSAttribute a4_2 = (XSAttribute) a4[1];
    assertEquals(new XsQName((String) null, "id"), a4_2.getName());
    assertEquals(XSID.getInstance(), a4_2.getType());
  }

  /** Tests typical element uses.
   */
  public void testElements() throws Exception {
    XSParser xsParser = newXSParser();
    testElements(xsParser);
    JAXBParser jaxbParser = newJAXBParser();
    testElements(jaxbParser);
  }

  protected void testFacets(XSParser pParser) throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:simpleType name='USState'>\n" +
      "    <xs:restriction base='xs:string'>\n" +
      "      <xs:enumeration value='AK'/>\n" +
      "      <xs:enumeration value='AL'/>\n" +
      "      <xs:enumeration value='AR'/>\n" +
      "      <!-- and so on ... -->\n" +   
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "  <xs:element name='a' type='USState'/>\n" +
      "</xs:schema>\n";
    
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testFacets.xsd");
    XSSchema schema = pParser.parse(isource);

    XSElement[] elements = schema.getElements();
    assertEquals(1, elements.length);
    XSElement e1 = elements[0];
    assertEquals(new XsQName((String) null, "a"), e1.getName());
    XSSimpleType st1 = assertSimpleType(e1.getType());
    assertEquals(XSString.getInstance(), assertRestriction(st1));
    XSEnumeration[] enumerations = st1.getEnumerations();
    assertNotNull(enumerations);
    assertEquals(3, enumerations.length);
    assertEquals(3, enumerations.length);
    assertEquals("AK", enumerations[0].getValue());
    assertEquals("AL", enumerations[1].getValue());
    assertEquals("AR", enumerations[2].getValue());
  }

  /** Tests various facets.
   */
  public void testFacets() throws Exception {
    XSParser xsParser = newXSParser();
    testFacets(xsParser);
    JAXBParser jaxbParser = newJAXBParser();
    testFacets(jaxbParser);
  }

  /** Test of the w3c sample schema PO.
   */
  public void testPurchaseOrder() throws Exception {
    final String schemaSource = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
      "\n" +
      "  <xsd:annotation>\n" +
      "    <xsd:documentation xml:lang='en'>\n" +
      "      Purchase order schema for Example.com.\n" +
      "      Copyright 2000 Example.com. All rights reserved.\n" +
      "    </xsd:documentation>\n" +
      "  </xsd:annotation>\n" +
      "\n" +
      "  <xsd:element name='purchaseOrder' type='PurchaseOrderType'/>\n" +
      "\n" +
      "  <xsd:element name='comment' type='xsd:string'/>\n" +
      "\n" +
      "  <xsd:complexType name='PurchaseOrderType'>\n" +
      "    <xsd:sequence>\n" +
      "      <xsd:element name='shipTo' type='USAddress'/>\n" +
      "      <xsd:element name='billTo' type='USAddress'/>\n" +
      "      <xsd:element ref='comment' minOccurs='0'/>\n" +
      "      <xsd:element name='items'  type='Items'/>\n" +
      "    </xsd:sequence>\n" +
      "    <xsd:attribute name='orderDate' type='xsd:date'/>\n" +
      "  </xsd:complexType>\n" +
      "\n" +
      "  <xsd:complexType name='USAddress'>\n" +
      "    <xsd:sequence>\n" +
      "      <xsd:element name='name'   type='xsd:string'/>\n" +
      "      <xsd:element name='street' type='xsd:string'/>\n" +
      "      <xsd:element name='city'   type='xsd:string'/>\n" +
      "      <xsd:element name='state'  type='xsd:string'/>\n" +
      "      <xsd:element name='zip'    type='xsd:decimal'/>\n" +
      "    </xsd:sequence>\n" +
      "    <xsd:attribute name='country' type='xsd:NMTOKEN'\n" +
      "        fixed='US'/>\n" +
      "  </xsd:complexType>\n" +
      "\n" +
      "  <xsd:complexType name='Items'>\n" +
      "    <xsd:sequence>\n" +
      "      <xsd:element name='item' minOccurs='0' maxOccurs='unbounded'>\n" +
      "        <xsd:complexType>\n" +
      "          <xsd:sequence>\n" +
      "            <xsd:element name='productName' type='xsd:string'/>\n" +
      "            <xsd:element name='quantity'>\n" +
      "              <xsd:simpleType>\n" +
      "                <xsd:restriction base='xsd:positiveInteger'>\n" +
      "                  <xsd:maxExclusive value='100'/>\n" +
      "                </xsd:restriction>\n" +
      "              </xsd:simpleType>\n" +
      "            </xsd:element>\n" +
      "            <xsd:element name='USPrice'  type='xsd:decimal'/>\n" +
      "            <xsd:element ref='comment'   minOccurs='0'/>\n" +
      "            <xsd:element name='shipDate' type='xsd:date' minOccurs='0'/>\n" +
      "          </xsd:sequence>\n" +
      "          <xsd:attribute name='partNum' type='SKU' use='required'/>\n" +
      "        </xsd:complexType>\n" +
      "      </xsd:element>\n" +
      "    </xsd:sequence>\n" +
      "  </xsd:complexType>\n" +
      "\n" +
      "  <!-- Stock Keeping Unit, a code for identifying products -->\n" +
      "  <xsd:simpleType name='SKU'>\n" +
      "    <xsd:restriction base='xsd:string'>\n" +
      "      <xsd:pattern value='\\d{3}-[A-Z]{2}'/>\n" +
      "    </xsd:restriction>\n" +
      "  </xsd:simpleType>\n" +
      "</xsd:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testPurchaseOrder.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    JAXBGlobalBindings globalBindings = schema.getJAXBGlobalBindings();
    assertNotNull(globalBindings);

    XSType[] schemaTypes = schema.getTypes();
    assertNotNull(schemaTypes);
    assertEquals(4, schemaTypes.length);

    // Items
    XSType items = schemaTypes[2];
    assertEquals(new XsQName((String) null, "Items"), items.getName());
    assertTrue(items.isGlobal());
    XSComplexType itemsComplexType = assertComplexType(items);
    XSParticle itemsParticle = assertComplexContent(itemsComplexType);
    XSGroup itemsGroup = assertGroup(itemsParticle);
    assertSequence(itemsGroup);
    // Items.item
    XSParticle[] itemsChildren = itemsGroup.getParticles();
    assertEquals(1, itemsChildren.length);
    XSParticle item = itemsChildren[0];
    XSElement itemElement = assertElement(item);
    assertEquals(new XsQName((String) null, "item"), itemElement.getName());
    assertTrue(!itemElement.isGlobal());
    XSComplexType itemComplexType = assertComplexType(itemElement.getType());
    assertEquals(0, item.getMinOccurs());
    assertEquals(-1, item.getMaxOccurs());
    XSParticle itemParticle = assertComplexContent(itemComplexType);
    XSGroup itemGroup = assertGroup(itemParticle);
    assertSequence(itemGroup);
    // Items.item.partNum
    // <xsd:attribute name='partNum' type='SKU' use='required'/>
    XSAttributable[] itemAttributes = itemComplexType.getAttributes();
    assertEquals(1, itemAttributes.length);
    XSAttribute partNum = (XSAttribute) itemAttributes[0];
    assertEquals(new XsQName((String) null, "partNum"), partNum.getName());
    assertTrue(!partNum.isOptional());
    XSType partNumType = partNum.getType();
    assertEquals(new XsQName((String) null, "SKU"), partNumType.getName());
    assertTrue(partNumType.isGlobal());

    XSParticle[] itemGroupParticles = itemGroup.getParticles();
    assertEquals(5, itemGroupParticles.length);

    // Items.item.productName
    // <xsd:element name='productName' type='xsd:string'/>
    XSParticle productName = itemGroupParticles[0];
    assertEquals(1, productName.getMinOccurs());
    assertEquals(1, productName.getMaxOccurs());
    XSElement productNameElement = assertElement(productName);
    assertEquals(new XsQName((String) null, "productName"), productNameElement.getName());
    assertSimpleType(productNameElement.getType());
    assertEquals(XSString.getInstance(), productNameElement.getType());

    // Items.item.quantity
    XSParticle quantity = itemGroupParticles[1];
    assertEquals(1, quantity.getMinOccurs());
    assertEquals(1, quantity.getMaxOccurs());
    XSElement quantityElement = assertElement(quantity);
    assertEquals(new XsQName((String) null, "quantity"), quantityElement.getName());
    XSSimpleType quantitySimpleType = assertSimpleType(quantityElement.getType());
    assertEquals(XSPositiveInteger.getInstance(), assertRestriction(quantitySimpleType));

    // Items.item.USPrice
    // <xsd:element name='USPrice'  type='xsd:decimal'/>
    XSParticle usPrice = itemGroupParticles[2];
    assertEquals(1, usPrice.getMinOccurs());
    assertEquals(1, usPrice.getMaxOccurs());
    XSElement usPriceElement = assertElement(usPrice);
    assertEquals(new XsQName((String) null, "USPrice"), usPriceElement.getName());
    assertEquals(XSDecimal.getInstance(), usPriceElement.getType());

    // Items.item.comment
    // <xsd:element ref='comment'   minOccurs='0'/>
    XSParticle comment = itemGroupParticles[3];
    assertEquals(comment.getMinOccurs(), 0);
    assertEquals(comment.getMaxOccurs(), 1);
    XSElement commentElement = assertElement(comment);
    assertEquals(new XsQName((String) null, "comment"), commentElement.getName());
    assertEquals(XSString.getInstance(), commentElement.getType());

    // Items.item.shipDate
    // <xsd:element name='shipDate' type='xsd:date' minOccurs='0'/> 
    XSParticle shipDate = itemGroupParticles[4];
    assertEquals(shipDate.getMinOccurs(), 0);
    assertEquals(shipDate.getMaxOccurs(), 1);
    XSElement shipDateElement = assertElement(shipDate);
    assertEquals(XSDate.getInstance(), shipDateElement.getType());

    // PurchaseOrderType
    XSType purchaseOrderType = schemaTypes[0];
    assertTrue(purchaseOrderType.isGlobal());
    assertEquals(new XsQName((String) null, "PurchaseOrderType"), purchaseOrderType.getName());
    XSComplexType purchaseOrderTypeComplexType = assertComplexType(purchaseOrderType);
    XSParticle purchaseOrderTypeParticle = assertComplexContent(purchaseOrderTypeComplexType);
    XSGroup purchaseOrderTypeGroup = assertGroup(purchaseOrderTypeParticle);
    assertSequence(purchaseOrderTypeGroup);

    XSAttributable[] potAttributes = purchaseOrderTypeComplexType.getAttributes();
    assertEquals(1, potAttributes.length);
    // PurchaseOrderType.orderDate
    // <xsd:attribute name='orderDate' type='xsd:date'/>
    XSAttribute orderDate = (XSAttribute) potAttributes[0];
    assertEquals(new XsQName((String) null, "orderDate"), orderDate.getName());
    assertEquals(XSDate.getInstance(), orderDate.getType());

    XSParticle[] potChildren = purchaseOrderTypeGroup.getParticles();
    assertEquals(4, potChildren.length);
    // PurchaseOrderType.shipTo
    // <xsd:element name='shipTo' type='USAddress'/>
    XSParticle shipTo = potChildren[0];
    assertEquals(1, shipTo.getMinOccurs());
    assertEquals(1, shipTo.getMaxOccurs());
    XSElement shipToElement = assertElement(shipTo);
    assertEquals(new XsQName((String) null, "shipTo"), shipToElement.getName());
    XSType shipToType = shipToElement.getType();
    assertTrue(shipToType.isGlobal());
    assertEquals(new XsQName((String) null, "USAddress"), shipToType.getName());
    // PurchaseOrderType.billTo
    // <xsd:element name='billTo' type='USAddress'/> 
    XSParticle billTo = potChildren[1];
    assertEquals(1, billTo.getMinOccurs());
    assertEquals(1, billTo.getMaxOccurs());
    XSElement billToElement = assertElement(billTo);
    assertEquals(new XsQName((String) null, "billTo"), billToElement.getName());
    XSType billToType = billToElement.getType();
    assertEquals(shipToType, billToType);
    // PurchaseOrderType.comment
    // <xsd:element ref='comment' minOccurs='0'/> 
    XSParticle potComment = potChildren[2];
    assertEquals(0, potComment.getMinOccurs());
    assertEquals(1, potComment.getMaxOccurs());
    XSElement potCommentElement = assertElement(potComment);
    assertEquals(new XsQName((String) null, "comment"), potCommentElement.getName());
    assertEquals(potCommentElement.getType(), commentElement.getType());
    // PurchaseOrderType.items
    // <xsd:element name='items'  type='Items'/>
    XSParticle potItems = potChildren[3];
    assertEquals(1, potItems.getMinOccurs());
    assertEquals(1, potItems.getMaxOccurs()); 
    XSElement potItemsElement = assertElement(potItems);
    assertEquals(new XsQName((String) null, "items"), potItemsElement.getName());
    assertEquals(items, potItemsElement.getType());

    // SKU
    XSType sku = schemaTypes[3];
    assertTrue(sku.isGlobal());
    assertEquals(new XsQName((String) null, "SKU"), sku.getName());
    XSSimpleType skuSimpleType = assertSimpleType(sku);
    assertEquals(XSString.getInstance(), assertRestriction(skuSimpleType));
    assertEquals(sku, partNumType);

    // USAddress
    // <xsd:complexType name='USAddress'> 
    // <xsd:sequence>
    XSType usAddress = schemaTypes[1];
    assertTrue(usAddress.isGlobal());
    assertEquals(new XsQName((String) null, "USAddress"), usAddress.getName());
    assertTrue(usAddress.isGlobal());
    XSComplexType usAddressComplexType = assertComplexType(usAddress);
    XSParticle usAddressParticle = assertComplexContent(usAddressComplexType);
    XSGroup usAddressGroup = assertGroup(usAddressParticle);
    assertSequence(usAddressGroup);
    // USAddress.country
    //  <xsd:attribute name='country' type='xsd:string' fixed='US'/>
    // ToDo: test attribute fixed='US'
    XSAttributable[] usAddressAttributes = usAddressComplexType.getAttributes();
    assertEquals(1, usAddressAttributes.length);
    XSAttribute country = (XSAttribute) usAddressAttributes[0];
    assertEquals(new XsQName((String) null, "country"), country.getName());
    assertEquals(XSNMToken.getInstance(), country.getType());
    // USAddress children
    String [] nameShouldBe = {"name", "street", "city", "state", "zip"};
    XSParticle[] usAddressChildren = usAddressGroup.getParticles();
    assertEquals(5, usAddressChildren.length);
    for (int i = 0; i < usAddressChildren.length; i++) {
      XSParticle child = usAddressChildren[i];
      assertEquals(1, child.getMinOccurs());
      assertEquals(1, child.getMaxOccurs());
      XSElement element = assertElement(child);
      assertEquals(new XsQName((String) null, nameShouldBe[i]), element.getName());
      assertEquals(i == 4 ? XSDecimal.getInstance() : XSString.getInstance(), element.getType());
    }

    // purchaseOrder
    // <xsd:element name='purchaseOrder' type='PurchaseOrderType'/>
    XSElement[] schemaElements = schema.getElements();
    assertEquals(2, schemaElements.length);
    XSElement purchaseOrder = schemaElements[0];
    assertEquals(new XsQName((String) null, "purchaseOrder"), purchaseOrder.getName());
    assertEquals(purchaseOrderType, purchaseOrder.getType());
    assertEquals(new XsQName((String) null, "comment"), schemaElements[1].getName());
  }

  /** A test case to trigger a previous parser bug.  */
  public void testRestrictionMaxExclusive() throws Exception {
    final String schemaSource = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'>\n" +
      " <xsd:element name='quantity'> \n" +
      "  <xsd:simpleType> \n" +
      "   <xsd:restriction base='xsd:decimal'> \n" +
      "    <xsd:maxExclusive value='100'/> \n" +
      "   </xsd:restriction> \n" +
      "  </xsd:simpleType> \n" +
      " </xsd:element> \n" +
      "</xsd:schema> \n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testRestrictionMaxExclusive.xsd");
    parser.parse(isource);
  }

  private void verifyLocalNamespaces(String pSchema, boolean pQualified) throws Exception {
    XsQName fooQualified = new XsQName("http://test.com/namespaces", "foo");
    XsQName fooUnQualified = new XsQName((String) null, "foo");
    XsQName barQualified = new XsQName("http://test.com/namespaces", "bar");
    XsQName barUnQualified = new XsQName((String) null, "bar");
    
    XSParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(pSchema));
    XSSchema schema = parser.parse(isource);
    XSElement[] schemaElements = schema.getElements();
    assertEquals(1, schemaElements.length);
    assertEquals(fooQualified, schemaElements[0].getName());
    XSAttribute[] schemaAttributes = schema.getAttributes();
    assertEquals(1, schemaAttributes.length);
    assertEquals(barQualified, schemaAttributes[0].getName());
    XSType[] types = schema.getTypes();
    assertEquals(1, types.length);
    XSComplexType complexType = assertComplexType(types[0]);
    XSAttributable[] typeAttributes = complexType.getAttributes();
    assertEquals(1, typeAttributes.length);
    assertEquals(pQualified ? barQualified : barUnQualified, ((XSAttribute) typeAttributes[0]).getName());
    XSGroup group = assertGroup(assertComplexContent(complexType));
    XSParticle[] particles = group.getParticles();
    assertEquals(1, particles.length);
    XSElement typeElement = assertElement(particles[0]);
    assertEquals(pQualified ? fooQualified : fooUnQualified, typeElement.getName());
  }

  /** <p>Verify the correct handling of target namespace, and the like.</p>
   */
  public void testTargetNamespace() throws Exception {
    final String schemaSource1 =
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' targetNamespace='http://test.com/namespaces'>\n" +
      "  <xs:element name='foo' type='xs:string'/>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource1));
    isource.setSystemId("testTargetNamespace1.xsd");
    XSSchema schema = parser.parse(isource);
    XSElement[] schemaElements = schema.getElements();
    assertEquals(1, schemaElements.length);
    assertEquals(new XsQName("http://test.com/namespaces", "foo"), schemaElements[0].getName());
    assertNull(schemaElements[0].getName().getPrefix());

    final String schemaSource2 =
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:test='http://test.com/namespaces'" +
      " targetNamespace='http://test.com/namespaces'>\n" +
      "  <xs:element name='foo' type='xs:string'/>\n" +
      "</xs:schema>\n";

    parser = newJAXBParser();
    isource = new InputSource(new StringReader(schemaSource2));
    isource.setSystemId("testTargetNamespace1.xsd");
    schema = parser.parse(isource);
    schemaElements = schema.getElements();
    assertEquals(1, schemaElements.length);
    assertEquals(new XsQName("http://test.com/namespaces", "foo"), schemaElements[0].getName());
    assertEquals("test", schemaElements[0].getName().getPrefix());

    final String namespaceSchemaHeader =
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:test='http://test.com/namespaces'" +
      "    targetNamespace='http://test.com/namespaces'";
    final String namespaceSchemaFooter =
      "  <xs:complexType name='cType'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element name='foo' type='xs:string'/>\n" +
      "    </xs:sequence>\n" +
      "    <xs:attribute name='bar'/>\n" +
      "   </xs:complexType>\n" +
      "   <xs:element name='foo' type='xs:string'/>\n" +
      "   <xs:attribute name='bar'/>\n" +
      "</xs:schema>\n";

    verifyLocalNamespaces(namespaceSchemaHeader + "  >\n" + namespaceSchemaFooter, false);
    verifyLocalNamespaces(namespaceSchemaHeader +
                          "  elementFormDefault='unqualified'\n" +
                          "  attributeFormDefault='unqualified'>\n" +
                          namespaceSchemaFooter, false);
    verifyLocalNamespaces(namespaceSchemaHeader +
                          "  elementFormDefault='qualified'\n" +
                          "  attributeFormDefault='qualified'>\n" +
                          namespaceSchemaFooter, true);
  }

  /** <p>Verify substitution groups.</p>
   */
  public void testSubstitutionGroups() throws Exception {
    final String schemaSource1 =
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'\n" +
      "    targetNamespace='http://test.com/namespaces'\n" +
      "    xmlns:ns='http://test.com/namespaces'>\n" +
      "  <xs:element name='head' type='xs:string'/>\n" +
      "  <xs:element name='subst1' type='xs:int' substitutionGroup='ns:head'/>\n" +
      "  <xs:element name='subst2' type='xs:float' substitutionGroup='ns:head'/>\n" +
      "  <xs:complexType name='test'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element ref='ns:head'/>\n" +
      "    </xs:sequence>\n" +
      "  </xs:complexType>\n" +
      "</xs:schema>\n";
    
    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource1));
    isource.setSystemId("testTargetNamespace1.xsd");
    XSSchema schema = parser.parse(isource);
    XSElement[] elements = schema.getElements();
    assertEquals(3, elements.length);
    XsQName headName = new XsQName("http://test.com/namespaces", "head");
    XsQName subst1Name = new XsQName("http://test.com/namespaces", "subst1");
    XsQName subst2Name = new XsQName("http://test.com/namespaces", "subst2");
    assertEquals(headName, elements[0].getName());
    assertNotNull(elements[0].getSubstitutionGroup());
    assertNull(elements[0].getSubstitutionGroupName());
    assertEquals(subst1Name, elements[1].getName());
    assertNull(elements[1].getSubstitutionGroup());
    assertEquals(headName, elements[1].getSubstitutionGroupName());
    assertEquals(subst2Name, elements[2].getName());
    assertNull(elements[2].getSubstitutionGroup());
    assertEquals(headName, elements[2].getSubstitutionGroupName());

    XSType[] types = schema.getTypes();
    assertEquals(1, types.length);
    XSGroup group = assertGroup(assertComplexContent(assertComplexType(types[0])));
    XSParticle[] particles = group.getParticles();
    assertEquals(1, particles.length);
    XSGroup substitutedGroup = assertGroup(particles[0]);
    assertEquals(XSModelGroup.CHOICE, substitutedGroup.getCompositor());
    XSParticle[] substParticles = substitutedGroup.getParticles();
    assertEquals(3, substParticles.length);
    assertEquals(headName, assertElement(substParticles[0]).getName());
    assertEquals(subst1Name, assertElement(substParticles[1]).getName());
    assertEquals(subst2Name, assertElement(substParticles[2]).getName());
  }

  /** <p>Verify extension handling.</p>
   */
  public void testExtensions() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:complexType name='a'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element name='ae1' type='xs:int'/>\n" +
      "      <xs:element name='ae2' type='xs:dateTime'/>\n" +
      "    </xs:sequence>\n" +
      "    <xs:attribute name='aa1' type='xs:boolean'/>\n" +
      "  </xs:complexType>\n" +
      "\n" +
      "  <xs:complexType name='b'>\n" +
      "    <xs:complexContent>\n" +
      "      <xs:extension base='a'>\n" +
      "        <xs:choice>\n" +
      "          <xs:element name='be1' type='xs:anyURI'/>\n" +
      "          <xs:element name='be2' type='xs:double'/>\n" +
      "        </xs:choice>\n" +
      "        <xs:attribute name='ba1'/>\n" +
      "      </xs:extension>\n" +
      "    </xs:complexContent>\n" +
      "  </xs:complexType>\n" +
      "</xs:schema>\n";

    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testElements.xsd");
    XSParser xsParser = newXSParser();
    XSSchema schema = xsParser.parse(isource);

    XSType[] types = schema.getTypes();
    assertEquals(2, types.length);
    XSType a = types[0];
    assertEquals(new XsQName((String) null, "a"), a.getName());
    XSComplexType aComplexType = assertComplexType(a);
    XSAttributable[] aAttributes = aComplexType.getAttributes();
    assertEquals(1, aAttributes.length);
    XSAttribute aa1 = (XSAttribute) aAttributes[0];
    assertEquals(new XsQName((String) null, "aa1"), aa1.getName());

    XSType b = types[1];
    assertEquals(new XsQName((String) null, "b"), b.getName());
    XSComplexType bComplexType = assertComplexType(b);
    assertTrue(bComplexType.isExtension());
    assertEquals(bComplexType.getExtendedType(), a);
    XSAttributable[] bAttributes = bComplexType.getAttributes();
    assertEquals(2, bAttributes.length);
    assertEquals(aa1, bAttributes[0]);
    XSAttribute ba1 = (XSAttribute) bAttributes[1];
    assertEquals(new XsQName((String) null, "ba1"), ba1.getName());
  }

  /** Tests setting attributes, which aren't specified in the
   * schema.
   */
  public void testAdditionalAttributes() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:foo='x' foo:a='y'>\n" +
      "  <xs:element name='test' type='xs:string' foo:b='z'/>\n" +
      "</xs:schema>\n";

    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testElements.xsd");
    XSParser xsParser = newXSParser();
    XSSchema schema = xsParser.parse(isource);

    Attributes schemaOpenAttrs = schema.getOpenAttributes();
    assertNotNull(schemaOpenAttrs);
    assertEquals(1, schemaOpenAttrs.getLength());
    assertEquals("x", schemaOpenAttrs.getURI(0));
    assertEquals("a", schemaOpenAttrs.getLocalName(0));
    assertEquals("y", schemaOpenAttrs.getValue(0));

    XSElement[] elements = schema.getElements();
    assertEquals(1, elements.length);
    Attributes elementOpenAttrs = elements[0].getOpenAttributes();
    assertNotNull(elementOpenAttrs);
    assertEquals(1, elementOpenAttrs.getLength());
    assertEquals("x", elementOpenAttrs.getURI(0));
    assertEquals("b", elementOpenAttrs.getLocalName(0));
    assertEquals("z", elementOpenAttrs.getValue(0));
  }

  /** Tests restriction of a simple type.
   */
  public void testSimpleTypeRestriction() throws Exception {
      final String schemaSource =
        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'\n" +
        "    xmlns='http://teamconnect.com'\n" +
        "    targetNamespace='http://teamconnect.com'>\n" +
        "  <xs:simpleType name='ZNSecurityTypeIID'>\n" +
        "    <xs:annotation>\n" +
        "      <xs:documentation/>\n" +
        "    </xs:annotation>\n" +
        "    <xs:restriction base='xs:int'>\n" +
        "      <xs:enumeration value='0'/>\n" +
        "      <xs:enumeration value='2'/>\n" +
        "    </xs:restriction>\n" +
        "  </xs:simpleType>\n" +
        "</xs:schema>\n";
      InputSource isource = new InputSource(new StringReader(schemaSource));
      isource.setSystemId("testSimpleTypeRestriction.xsd");
      XSParser xsParser = newXSParser();
      XSSchema schema = xsParser.parse(isource);
      XSType[] types = schema.getTypes();
      assertEquals(1, types.length);
      XSSimpleType simpleType = assertSimpleType(types[0]);
      assertAtomicType(simpleType);
      XSEnumeration[] enumerations = simpleType.getEnumerations();
      assertEquals(2, enumerations.length);
      assertEquals("0", enumerations[0].getValue());
      assertEquals("2", enumerations[1].getValue());
  }

  private void testSimpleKey(XSParser parser) throws Exception {
    final String schemaSource =
      "<?xml version='1.0' encoding='UTF-8'?>" +
      "<xs:schema targetNamespace='http://www.teamconnect.com' xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns='http://www.teamconnect.com'>" +
      "  <xs:element name='library'>" +
      "          <xs:complexType>" +
      "            <xs:sequence>" +
      "              <xs:element name='book' minOccurs='0' maxOccurs='unbounded'>" +
      "                <xs:complexType>" +
      "                  <xs:attribute name='id' type='xs:int' use='optional'/>" +
      "                  <xs:attribute name='title' type='xs:string' use='required'/>" +
      "                  <xs:attribute name='author' type='xs:string' use='optional'/>" +
      "                </xs:complexType>" +
      "              </xs:element>" +
      "            </xs:sequence>" +
      "          </xs:complexType>" +
      "          <xs:key name='book-key'>" +
      "            <xs:selector xpath='./book'/>" +
      "            <xs:field xpath='@id'/>" +
      "            <xs:field xpath='.'/>" +
      "          </xs:key>" +
      "          <xs:keyref name='book-key-ref' refer='book-key'>" +
      "            <xs:selector xpath='./book'/>" +
      "            <xs:field xpath='@title'/>" +
      "            <xs:field xpath='.'/>" +
      "          </xs:keyref>" +
      "  </xs:element>" +
      "</xs:schema>";

      InputSource isource = new InputSource(new StringReader(schemaSource));
      isource.setSystemId("testSimpleKey.xsd");
      XSSchema schema = parser.parse(isource);

      XSElement[] elements = schema.getElements();
      assertEquals( 1, elements.length );

      XSElement libraryElement = elements[0];
      XSIdentityConstraint[] ics = libraryElement.getIdentityConstraints();

      assertEquals( 1, ics.length );

      XSIdentityConstraint ic = ics[0];
      assertEquals( "book-key", ic.getName() );

      XSElementOrAttrRef[][] icMatchCriteria = ic.getMatchCriteria();

      assertEquals( 2, icMatchCriteria.length );
      assertEquals( 1, icMatchCriteria[0].length );
      assertEquals( 1, icMatchCriteria[1].length );

      assertEquals(
        "id", 
        icMatchCriteria[0][0].getAttribute().getName().getLocalName()
      );
      assertEquals(
        "book", 
        icMatchCriteria[1][0].getElement().getName().getLocalName()
      );

      XSKeyRef[] rfs = libraryElement.getKeyRefs();
      assertEquals( 1, rfs.length );

      XSKeyRef rf = rfs[0];
      assertEquals( "book-key-ref", rf.getName() );

      XSElementOrAttrRef[][] rfMatchCriteria = rf.getMatchCriteria();
      assertEquals( 2, rfMatchCriteria.length );
      assertEquals( 1, rfMatchCriteria[0].length );
      assertEquals( 1, rfMatchCriteria[1].length );

      assertEquals(
        "title", 
        rfMatchCriteria[0][0].getAttribute().getName().getLocalName()
      );
      assertEquals(
        "book", 
        rfMatchCriteria[1][0].getElement().getName().getLocalName()
      );

  }

    /** Tests definition of a key.
     */
    public void testSimpleKey() throws Exception {
    	XSParser xsParser = newXSParser();
    	testSimpleKey(xsParser);
    	JAXBParser jaxbParser = newJAXBParser();
    	testSimpleKey(jaxbParser);
    }

    private void testDocumentationChilds(XSParser pParser) throws Exception {
        final String schemaSource =
            "<?xml version='1.0' encoding='UTF-8'?>" +
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' >\n" +
            "  <xs:annotation>\n" +
            "    <xs:documentation xmlns:cc='http://www.dummy-namespace.org/'>\n" +
            "      <cc:foo/>\n" +
            "    </xs:documentation>\n" +
            "  </xs:annotation>\n" +
            "</xs:schema>";

            InputSource isource = new InputSource(new StringReader(schemaSource));
            isource.setSystemId("testSimpleKey.xsd");
			pParser.parse(isource);
    }

    /** <p>Tests whether xs:documentation may have arbitrary childs.</p>
     */
    public void testDocumentationChilds() throws Exception {
    	XSParser xsParser = newXSParser();
        testDocumentationChilds(xsParser);
        JAXBParser jaxbParser = newJAXBParser();
        testDocumentationChilds(jaxbParser);
    }

    private void testNamespaceLists(XSParser pParser) throws Exception {
    	final String schemaSource =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
            "  <xs:complexType name='foo'>\n" +
			"    <xs:sequence>\n" +
            "      <xs:any namespace='http://purl.org/dc/elements/1.1/' processContents='strict' minOccurs='0' maxOccurs='unbounded'/>\n" +
            "      <xs:any namespace='http://www.jeckle.de/rss' processContents='strict' minOccurs='0'/>\n" +
            "    </xs:sequence>\n" +
            "    <xs:anyAttribute namespace='http://www.w3.org/1999/02/22-rdf-syntax-ns#' processContents='strict'/>\n" +
            "  </xs:complexType>\n" +
            "</xs:schema>\n";
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testNamespaceLists.xsd");
    	XSSchema schema = pParser.parse(isource);
    	XSParticle[] particles = assertGroup(assertComplexContent(assertComplexType(schema.getTypes()[0]))).getParticles();
        assertEquals(2, particles.length);
        assertTrue(particles[0].isWildcard());
        XSWildcard wildcard = particles[0].getWildcard();
        XsNamespaceList namespaceList = wildcard.getNamespaceList();
        assertTrue(!namespaceList.isAny());
        assertTrue(!namespaceList.isAny());
        assertEquals(1, namespaceList.getUris().length);
        assertEquals("http://purl.org/dc/elements/1.1/", namespaceList.getUris()[0].getURI());
        assertTrue(particles[1].isWildcard());
        wildcard = particles[1].getWildcard();
        namespaceList = wildcard.getNamespaceList();
        assertTrue(!namespaceList.isAny());
        assertTrue(!namespaceList.isAny());
        assertEquals(1, namespaceList.getUris().length);
        assertEquals("http://www.jeckle.de/rss", namespaceList.getUris()[0].getURI());
        XSAttributable[] attributes = assertComplexType(schema.getTypes()[0]).getAttributes();
        assertEquals(1, attributes.length);
        assertTrue(attributes[0] instanceof XSWildcard);
        wildcard = (XSWildcard) attributes[0];
        namespaceList = wildcard.getNamespaceList();
        assertTrue(!namespaceList.isAny());
        assertTrue(!namespaceList.isAny());
        assertEquals(1, namespaceList.getUris().length);
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", namespaceList.getUris()[0].getURI());
    }
        
    /** <p>Test the handling of namespace lists.</p>
     */
    public void testNamespaceLists() throws Exception {
        XSParser xsParser = newXSParser();
        testNamespaceLists(xsParser);
        JAXBParser jaxbParser = newJAXBParser();
        testNamespaceLists(jaxbParser);
    }

    private void testSimpleTypeRestriction2(XSParser pParser) throws Exception {
        final String schemaSource =
            "<schema xmlns='http://www.w3.org/2001/XMLSchema'\n" +
            "   targetNamespace='http://asi.sbc.com/cpsosasos/trouble/data'\n" +
            "   xmlns:s='http://asi.sbc.com/cpsosasos/trouble/data'>\n" +
            "  <element name='foo'>\n" +
            "    <complexType>\n" +
            "      <attribute name='bar' type='s:ServiceIDType'/>\n" +
            "    </complexType>\n" +
            "  </element>\n" +
            "  <simpleType name='ServiceIDType'>\n" +
            "    <restriction base='s:NameTypeType'/>\n" +
            "  </simpleType>\n" +
            "  <simpleType name='NameTypeType'>\n" +
            "    <union memberTypes='integer string'/>\n" +
            "  </simpleType>\n" +
            "</schema>\n";

        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testSimpleTypeRestriction2.xsd");
        XSSchema schema = pParser.parse(isource);
        XSType[] types = schema.getTypes();
        assertEquals(2, types.length);
        assertEquals(new XsQName("http://asi.sbc.com/cpsosasos/trouble/data", "NameTypeType"),
                     types[1].getName());
        XSSimpleType nameTypeType = assertSimpleType(types[1]);
        assertUnionType(nameTypeType);
        assertEquals(new XsQName("http://asi.sbc.com/cpsosasos/trouble/data", "ServiceIDType"),
                     types[0].getName());
        XSSimpleType serviceIDType = assertSimpleType(types[0]);
        assertUnionType(serviceIDType);
        assertTrue(serviceIDType.isRestriction());
    }

    /** <p>Tests restrictions of simple types.</p>
     */
    public void testSimpleTypeRestriction2() throws Exception {
        testSimpleTypeRestriction2(newXSParser());
        testSimpleTypeRestriction2(newJAXBParser());
    }

    private void testAppInfoEmbeddedText(XSParser pParser) throws Exception {
        final String schemaSource =
            "<schema xmlns='http://www.w3.org/2001/XMLSchema'>\n" +
            "  <annotation><appinfo>foo</appinfo></annotation>\n" +
            "</schema>\n";
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testAppInfoEmbeddedText.xsd");
        XSSchema schema = pParser.parse(isource);
        XSAnnotation[] annotations = schema.getAnnotations();
        assertEquals(1, annotations.length);
        XSAppinfo[] appinfos = annotations[0].getAppinfos();
        assertEquals(1, appinfos.length);
        Object[] childs = appinfos[0].getChilds();
        assertEquals(1, childs.length);
        assertEquals("foo", (String) childs[0]);
    }

    /** <p>Does the <code>xs:appinfo</code> element support embedded text?</p>
     */
    public void testAppInfoEmbeddedText() throws Exception {
        testAppInfoEmbeddedText(newXSParser());
        testAppInfoEmbeddedText(newJAXBParser());
    }

    private void testImportSchemaWithoutNamespace(XSParser pParser) throws Exception {
        final String schemaSource =
            "<schema xmlns='http://www.w3.org/2001/XMLSchema'" +
            "	 targetNamespace='xyz' xmlns:p='xyz'>\n" +
            "  <include schemaLocation='abc.xsd'/>\n" +
            "  <element name='a' type='string'/>\n" +
            "  <element name='b'>\n" +
            "    <complexType>\n" +
            "      <sequence>\n" +
            "        <element ref='p:a'/>\n" +
            "      </sequence>\n" +
            "    </complexType>\n" +
            "  </element>\n" +
            "</schema>\n";
        final String importedSchema =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
            "  <xs:element name='c' type='xs:string'/>\n" +
            "  <xs:element name='d'>\n" +
            "    <xs:complexType>\n" +
            "      <xs:sequence>\n" +
            "        <xs:element ref='c'/>\n" +
            "      </xs:sequence>\n" +
            "    </xs:complexType>\n" +
            "  </xs:element>\n" +
            "\n" +
            "  <xs:element name='AnyAttribute'>\n" +
            "    <xs:complexType>\n" +
            "      <xs:anyAttribute namespace='##any'/>\n" +
            "    </xs:complexType>\n" +
            "  </xs:element>\n" +
            "\n" +
            "  <xs:element name='OtherAttribute'>\n" +
            "    <xs:complexType>\n" +
            "      <xs:anyAttribute namespace='##other'/>\n" +
            "    </xs:complexType>\n" +
            "  </xs:element>\n" +
            "\n" +
            "  <xs:element name='ListAttribute'>\n" +
            "    <xs:complexType>\n" +
            "      <xs:anyAttribute namespace='##targetNamespace http://ws.apache.org/jaxme/test/misc/wildcards/2'/>\n" +
            "    </xs:complexType>\n" +
            "  </xs:element>\n" +
            "</xs:schema>\n";

		EntityResolver resolver = new EntityResolver(){
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if ("abc.xsd".equals(systemId)) {
                    return new InputSource(new StringReader(importedSchema));
                } else {
                    throw new SAXException("Invalid systemId: " + systemId);
                }
            }
        };
        pParser.getContext().setXsObjectFactory(getXsObjectFactoryProxy(pParser.getContext().getXsObjectFactory(), resolver));
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testImportSchemaWithoutNamespace.xsd");
        XSSchema schema = pParser.parse(isource);
        XSElement[] elements = schema.getElements();
        assertEquals(7, elements.length);
        assertEquals(new XsQName("xyz", "c"), elements[0].getName());
        assertEquals(new XsQName("xyz", "d"), elements[1].getName());
        XSComplexType anyAttrElem = assertComplexType(elements[2].getType());
        XSAttributable[] attrs = anyAttrElem.getAttributes();
        assertEquals(1, attrs.length);
        assertTrue(attrs[0] instanceof XSWildcard);
        XSWildcard wc = (XSWildcard) attrs[0];
        XsNamespaceList nsl = wc.getNamespaceList();
        assertTrue(nsl.isAny());
        assertNull(nsl.getUris());
        XSComplexType otherAttrElem = assertComplexType(elements[3].getType());
        attrs = otherAttrElem.getAttributes();
        assertEquals(1, attrs.length);
        assertTrue(attrs[0] instanceof XSWildcard);
        nsl = ((XSWildcard) attrs[0]).getNamespaceList();
        assertTrue(nsl.isOther());
        assertEquals(1, nsl.getUris().length);
        assertEquals("xyz", nsl.getUris()[0].toString());
        XSComplexType listAttrElem = assertComplexType(elements[4].getType());
        attrs = listAttrElem.getAttributes();
        assertEquals(1, attrs.length);
        assertTrue(attrs[0] instanceof XSWildcard);
        nsl = ((XSWildcard) attrs[0]).getNamespaceList();
        assertTrue(!nsl.isOther());
        assertTrue(!nsl.isAny());
        assertEquals(2, nsl.getUris().length);
        assertEquals("xyz", nsl.getUris()[0].toString());
        assertEquals("http://ws.apache.org/jaxme/test/misc/wildcards/2", nsl.getUris()[1].toString());
        assertEquals(new XsQName("xyz", "a"), elements[5].getName());
        assertEquals(new XsQName("xyz", "b"), elements[6].getName());
    }

    /** <p>Tests, whether importing a schema without namespace
     * changes the imported schemas namespace to the local
     * namespace.</p>
     */
    public void testImportSchemaWithoutNamespace() throws Exception {
        testImportSchemaWithoutNamespace(newXSParser());
        testImportSchemaWithoutNamespace(newJAXBParser());
    }

    private void testElementReferenceGlobal(XSParser pParser) throws Exception {
        final String schemaSource =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<schema xmlns='http://www.w3.org/2001/XMLSchema'" +
            "    xmlns:jaxb='http://java.sun.com/xml/ns/jaxb'" +
            "    targetNamespace='http://ws.apache.org/jaxme/test/recursion'" +
            "    xmlns:rec='http://ws.apache.org/jaxme/test/recursion'" +
            "    elementFormDefault='qualified' attributeFormDefault='unqualified'>\n" +
            "  <element name='Attribute'>\n" +
            "    <complexType>\n" +
            "      <attribute name='id' type='string' use='required'/>\n" +
            "      <attribute name='value' type='string' use='optional'/>\n" +
            "    </complexType>\n" +
            "  </element>\n" +
            "\n" +
            "  <element name='AttributeList'>\n" +
            "    <complexType>\n" +
            "      <sequence>\n" +
            "        <element ref='rec:Attribute'/>\n" +
            "        <element ref='rec:Attribute' minOccurs='0' maxOccurs='unbounded'/>\n" +
            "      </sequence>\n" +
            "    </complexType>\n" +
            "  </element>\n" +
            "</schema>\n";

        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testElementReferenceGlobal.xsd");
        XSSchema schema = pParser.parse(isource);
        XSElement[] elements = schema.getElements();
        assertEquals(2, elements.length);
        assertEquals(new XsQName("http://ws.apache.org/jaxme/test/recursion", "Attribute"), elements[0].getName());
        assertTrue(elements[0].isGlobal());
        assertEquals(new XsQName("http://ws.apache.org/jaxme/test/recursion", "AttributeList"), elements[1].getName());
        assertTrue(elements[1].isGlobal());
        XSComplexType complexType = assertComplexType(elements[1].getType());
        XSParticle particle = assertComplexContent(complexType);
        XSGroup group = assertGroup(particle);
        XSParticle[] particles = group.getParticles();
        assertEquals(2, particles.length);
        assertEquals(1, particles[0].getMinOccurs());
        assertEquals(1, particles[0].getMaxOccurs());
        XSElement refElement = assertElement(particles[0]);
        assertTrue(refElement.isGlobal());
        assertEquals(refElement.getName(), elements[0].getName());
        assertEquals(0, particles[1].getMinOccurs());
        assertEquals(-1, particles[1].getMaxOccurs());
        refElement = assertElement(particles[1]);
        assertTrue(refElement.isGlobal());
        assertEquals(refElement.getName(), elements[0].getName());
    }

    /** Tests whether an element reference is flagged as a global
     * element.
     */
    public void testElementReferenceGlobal() throws Exception {
    	testElementReferenceGlobal(newXSParser());
        testElementReferenceGlobal(newJAXBParser());
    }

	private void checkMailTemplateGroup(String pURI, XSType pType) throws SAXException {
		XSComplexType ct = assertComplexType(pType);
		assertFalse(ct.isEmpty());
		XSParticle particle = assertComplexContent(ct);
		XSGroup group = assertGroup(particle);
		assertSequence(group);
		XSParticle[] particles = group.getParticles();
		assertEquals(2, particles.length);
		assertElement(particles[0]);
		assertEquals(new XsQName(pURI, "subject"), particles[0].getElement().getName());
		XSGroup choice = assertGroup(particles[1]);
		assertChoice(choice);
		XSParticle[] choiceParticles = choice.getParticles();
		assertEquals(2, choiceParticles.length);
		XSGroup sequence = assertGroup(choiceParticles[0]);
		assertSequence(sequence);
		XSParticle[] sequenceParticles = sequence.getParticles();
		assertEquals(2, sequenceParticles.length);
		assertEquals(new XsQName(pURI, "prepend"), sequenceParticles[0].getElement().getName());
		assertEquals(new XsQName(pURI, "append"), sequenceParticles[1].getElement().getName());
		assertEquals(new XsQName(pURI, "body"), choiceParticles[1].getElement().getName());
	}

	private void testMailTemplateMixed(XSParser pParser) throws Exception {
		final String uri = "http://ws.apache.org/jaxme/test/nestedGroups";
		final String schemaSource =
			"<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'\n" +
			"    xmlns:ng='" + uri + "'\n" +
			"    targetNamespace='http://ws.apache.org/jaxme/test/nestedGroups'\n" +
			"    elementFormDefault='qualified'\n" +
			"    attributeFormDefault='unqualified'>\n" +
			"  <xs:group name='MailTemplateGroup'>\n" +
			"    <xs:sequence>\n" +
			"      <xs:element name='subject' type='xs:string'/>\n" +
			"      <xs:choice>\n" +
			"        <xs:sequence>\n" +
			"          <xs:element name='prepend' type='xs:string'/>\n" +
			"          <xs:element name='append' type='xs:string'/>\n" +
			"        </xs:sequence>\n" +
			"        <xs:element name='body' maxOccurs='unbounded'>\n" +
			"          <xs:complexType>\n" +
			"            <xs:simpleContent>\n" +
			"              <xs:extension base='xs:string'>\n" +
			"                <xs:attribute name='delivery' use='required'>\n" +
			"                  <xs:simpleType>\n" +
			"                    <xs:restriction base='xs:string'>\n" +
			"                      <xs:enumeration value='dailyDigest'/>\n" +
			"                      <xs:enumeration value='immediate'/>\n" +
			"                    </xs:restriction>\n" +
			"                  </xs:simpleType>\n" +
			"                </xs:attribute>\n" +
			"              </xs:extension>\n" +
			"            </xs:simpleContent>\n" +
			"          </xs:complexType>\n" +
			"        </xs:element>\n" +
			"      </xs:choice>\n" +
			"    </xs:sequence>\n" +
			"  </xs:group>\n" +
			"\n" +
			"  <xs:element name='MailTemplate'>\n" +
			"    <xs:complexType>\n" +
			"      <xs:group ref='ng:MailTemplateGroup'/>\n" +
			"      <xs:attribute name='language' type='xs:string' use='optional' default='EN'/>\n" +
			"      <xs:attribute name='name' type='xs:string' use='required'/>\n" +
			"    </xs:complexType>\n" +
			"  </xs:element>\n" +
			"\n" +
			"  <xs:element name='MailTemplateMixed'>\n" +
			"    <xs:complexType mixed='true'>\n" +
			"      <xs:group ref='ng:MailTemplateGroup'/>\n" +
			"      <xs:attribute name='language' type='xs:string' use='optional' default='EN'/>\n" +
			"      <xs:attribute name='name' type='xs:string' use='required'/>\n" +
			"    </xs:complexType>\n" +
			"  </xs:element>\n" +
			"</xs:schema>\n";
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testMailTemplateMixed.xsd");
        XSSchema schema = pParser.parse(isource);
		XSElement[] elements = schema.getElements();
		assertEquals(2, elements.length);
		assertEquals(new XsQName(uri, "MailTemplate"), elements[0].getName());
		assertFalse(assertComplexType(elements[0].getType()).isMixed());
		checkMailTemplateGroup(uri, elements[0].getType());
		assertEquals(new XsQName(uri, "MailTemplateMixed"), elements[1].getName());
		assertTrue(assertComplexType(elements[1].getType()).isMixed());
		checkMailTemplateGroup(uri, elements[1].getType());
	}

    /** Tests handling of nested groups with and/or without mixed
     * content.
     */
    public void testMailTemplateMixed() throws Exception {
    	testMailTemplateMixed(newXSParser());
        testMailTemplateMixed(newJAXBParser());
    }


	private void testRecursiveXsInclude(XSParser pParser) throws Exception {
		final String a = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n"
			+ "  <xs:include schemaLocation='b.xsd'/>\n"
			+ "  <xs:element name='a' type='xs:int'/>\n"
			+ "</xs:schema>\n";
		final String b = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n"
			+ "  <xs:include schemaLocation='a.xsd'/>\n"
			+ "  <xs:element name='b' type='xs:int'/>\n"
			+ "</xs:schema>\n";
		final EntityResolver resolver = new EntityResolver(){
			public InputSource resolveEntity(String pPublicId, String pSystemId) throws SAXException, IOException {
				final String xml;
				if ("a.xsd".equals(pSystemId)) {
					xml = a;
				} else if ("b.xsd".equals(pSystemId)) {
					xml = b;
				} else {
					return null;
				}
				InputSource isource = new InputSource(new StringReader(xml));
				isource.setSystemId(pSystemId);
				return isource;
			}
		};
		pParser.getContext().setXsObjectFactory(getXsObjectFactoryProxy(pParser.getContext().getXsObjectFactory(), resolver));
		InputSource isource = new InputSource(new StringReader(a));
		isource.setSystemId("a.xsd");
		XSSchema schema = pParser.parse(isource);
		assertEquals(2, schema.getElements().length);
		assertEquals(0, schema.getTypes().length);
	}

	/** Tests, whether schemas can include each other recursively.
	 */
	public void testRecursiveXsInclude() throws Exception {
		testRecursiveXsInclude(newXSParser());
		testRecursiveXsInclude(newJAXBParser());
	}


	private void testGroupMultiplicity(XSParser pParser) throws Exception {
		final String schemaSource =
			"<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n"
			+ "<xs:element name='PARAMETERS'>\n"
			+ "  <xs:complexType>\n"
			+ "    <xs:choice maxOccurs='unbounded'>\n"
			+ "      <xs:element name='PARAMETER'/>\n"
			+ "      <xs:element name='SYSTEMPARAMETER'/>\n"
			+ "      <xs:element name='PREPREF'/>\n"
			+ "    </xs:choice>\n"
			+ "  </xs:complexType>\n"
			+ "</xs:element></xs:schema>\n";
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testElementReferenceGlobal.xsd");
        XSSchema schema = pParser.parse(isource);
        XSElement[] elements = schema.getElements();
        assertEquals(1, elements.length);
        XSElement parameters = elements[0];
        XSParticle particle = assertComplexContent(assertComplexType(parameters.getType()));
        assertEquals(1, particle.getMinOccurs());
        assertEquals(-1, particle.getMaxOccurs());
	}

	/** Test the representation of groups with multiplicity > 1.
	 */
	public void testGroupMultiplicity() throws Exception {
		testGroupMultiplicity(newXSParser());
		testGroupMultiplicity(newJAXBParser());
	}

	private void testComplexTypeConsideredSimple(XSParser pParser) throws Exception {
	    final String schemaSource = "<xs:schema xmlns='www.fgm.com/services/fgm/1.1'\n"
            + "    xmlns:xs='http://www.w3.org/2001/XMLSchema'\n"
            + "    targetNamespace='www.fgm.com/services/fgm/1.1'\n"
            + "    elementFormDefault='qualified'>\n"
            + "  <xs:complexType name='BaseObject'/>\n"
            + "  <xs:complexType name='ExtendedBrokenObject'>\n"
            + "    <xs:complexContent>\n"
            + "      <xs:extension base='BrokenObject'/>\n"
            + "    </xs:complexContent>\n"
            + "  </xs:complexType>\n"
            + "  <xs:complexType name='BrokenObject'>\n"
            + "    <xs:complexContent>\n"
            + "      <xs:extension base='BaseObject'>\n"
            + "        <xs:sequence>\n"
            + "          <xs:choice minOccurs='0' maxOccurs='unbounded'>\n"
            + "            <xs:element name='Choice' type='ExtendedBrokenObject'\n"
            + "                minOccurs='0' maxOccurs='unbounded'/>\n"
            + "          </xs:choice>\n"
            + "        </xs:sequence>\n"
            + "      </xs:extension>\n"
            + "    </xs:complexContent>\n"
            + "  </xs:complexType>\n"
            + "</xs:schema>\n";
        InputSource isource = new InputSource(new StringReader(schemaSource));
        isource.setSystemId("testComplexTypeConsideredSimple.xsd");
        pParser.parse(isource);
    }

    /** Test for http://marc.theaimsgroup.com/?l=jaxme-dev&m=115945460223201&w=2
     */
    public void testComplexTypeConsideredSimple() throws Exception {
        testComplexTypeConsideredSimple(newXSParser());
        testComplexTypeConsideredSimple(newJAXBParser());
    }
}
