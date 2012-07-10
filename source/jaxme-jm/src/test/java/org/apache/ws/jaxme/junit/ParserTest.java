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
package org.apache.ws.jaxme.junit;

import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ListTypeSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.UnionTypeSG;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSGFactory;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.SGFactoryImpl;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBAttribute;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.types.XSID;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/** <p>Implements some basic tests for the Schema generator.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: ParserTest.java 232139 2005-07-13 19:58:16Z jochen $
 */
public class ParserTest extends BaseTestCase {
  public ParserTest(String arg) { super(arg); }

  protected SchemaSG parse(String pSchema, String pSystemId) throws Exception {
    JAXBSchemaReader r = getSchemaReader();
    InputSource isource = new InputSource(new StringReader(pSchema));
    isource.setSystemId(pSystemId);
    return r.parse(isource);
  }

  protected JAXBSchemaReader getSchemaReader() {
      Generator generator = new GeneratorImpl();
      JAXBSchemaReader r = new JAXBSchemaReader();
      generator.setSchemaReader(r);
      r.setGenerator(generator);
      return r;
  }

  public void testSimpleTypes() throws Exception {
    final String schema =
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

    SchemaSG schemaSG = parse(schema, "testSimpleTypes.xsd");

    TypeSG[] st = schemaSG.getTypes();
    assertEquals(3, st.length);

    TypeSG st1 = st[0];
    assertTrue(!st1.isComplex());
    SimpleTypeSG sst1 = st1.getSimpleTypeSG();
    assertTrue(sst1.isAtomic());
    assertTrue(!sst1.isList());
    assertTrue(!sst1.isUnion());
    assertEquals(sst1.getRuntimeType(), JavaQNameImpl.getInstance(String.class));

    TypeSG st2 = st[1];
    assertTrue(!st2.isComplex());
    SimpleTypeSG sst2 = st2.getSimpleTypeSG();
    assertTrue(!sst2.isAtomic());
    assertTrue(sst2.isList());
    assertTrue(!sst2.isUnion());
    ListTypeSG lt = sst2.getListType();
    assertNotNull(lt.getItemType());
    TypeSG sst = lt.getItemType();
    assertTrue(!sst.isComplex());
    SimpleTypeSG it = sst.getSimpleTypeSG();
    assertEquals(JavaQNameImpl.INT, it.getRuntimeType());

    TypeSG st3 = st[2];
    assertEquals(false, st3.isComplex());
    SimpleTypeSG sst3 = st3.getSimpleTypeSG();
    assertEquals(false, sst3.isAtomic());
    assertEquals(false, sst3.isList());
    assertEquals(true, sst3.isUnion());
    UnionTypeSG ut = sst3.getUnionType();
    TypeSG[] uTypes = ut.getMemberTypes();
    assertEquals(2, uTypes.length);
  }

  public void testAttributes() throws Exception {
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation><xs:appinfo>\n" +
      "    <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "      <jaxb:package name='parsertest.testattributes'/>\n" +
      "    </jaxb:schemaBindings>\n" +
      "  </xs:appinfo></xs:annotation>\n" +
      "  <xs:attribute name='a' type='xs:string'/>\n" +
      "\n" +
      "  <xs:attribute name='b'>\n" +
      "    <xs:simpleType>\n" +
      "      <xs:restriction base='xs:int'/>\n" +
      "    </xs:simpleType>\n" +
      "  </xs:attribute>\n" +
      "\n" +
      "  <xs:complexType name='attributesOwner'>\n" +
      "    <xs:attribute ref='a'/>\n" +
      "    <xs:attribute ref='b'/>\n" +
      "    <xs:attribute name='c' type='xs:int'/>\n" +
      "  </xs:complexType>\n" +
      "</xs:schema>\n";

    SchemaSG jschema = parse(schema, "testAttributes.xsd");

    TypeSG[] types = jschema.getTypes();
    assertEquals(1, types.length);
    assertTrue(types[0].isComplex());

    AttributeSG[] attr = types[0].getComplexTypeSG().getAttributes();
    assertEquals(3, attr.length);

    AttributeSG attr1 = attr[0];
    assertTrue(!attr1.getTypeSG().isComplex());
    SimpleTypeSG sst1 = attr1.getTypeSG().getSimpleTypeSG();
    assertTrue(sst1.isAtomic());
    assertEquals(JavaQNameImpl.getInstance(String.class), sst1.getRuntimeType());

    AttributeSG attr2 = attr[1];
    assertTrue(!attr2.getTypeSG().isComplex());
    SimpleTypeSG sst2 = attr2.getTypeSG().getSimpleTypeSG();
    assertTrue(sst2.isAtomic());
    assertEquals(JavaQNameImpl.INT, sst2.getRuntimeType());

    AttributeSG attr3 = attr[2];
    assertTrue(!attr3.getTypeSG().isComplex());
    SimpleTypeSG sst3 = attr3.getTypeSG().getSimpleTypeSG();
    assertTrue(sst3.isAtomic());
    assertEquals(JavaQNameImpl.INT, sst3.getRuntimeType());
  }

  public void testAttributeGroups() throws Exception {
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation><xs:appinfo>\n" +
      "    <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "      <jaxb:package name='parsertest.testattributegroups'/>\n" +
      "    </jaxb:schemaBindings>\n" +
      "  </xs:appinfo></xs:annotation>\n" +
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
      "\n" +
      "  <xs:element name='ea'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:attributeGroup ref='c'/>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "\n" +
      "  <xs:element name='eb'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:attributeGroup ref='f'/>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    SchemaSG jschema = parse(schema, "testAttributeGroups.xsd");

    ObjectSG[] elements = jschema.getElements();
    assertNotNull(elements);
    assertEquals(2, elements.length);

    ObjectSG ea = elements[0];
    assertTrue(ea.getTypeSG().isComplex());
    ComplexTypeSG eact = ea.getTypeSG().getComplexTypeSG();
    AttributeSG[] eactAttr = eact.getAttributes();
    assertEquals(3, eactAttr.length);
    assertEquals(new XsQName((String) null, "d"), eactAttr[0].getName());
    assertEquals(eactAttr[0].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(Calendar.class));
    assertEquals(new XsQName((String) null, "e"), eactAttr[1].getName());
    assertEquals(eactAttr[1].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(float.class));
    assertEquals(new XsQName((String) null, "a"), eactAttr[2].getName());
    assertEquals(eactAttr[2].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(String.class));

    ObjectSG eb = elements[1];
    assertTrue(eb.getTypeSG().isComplex());
    ComplexTypeSG ebct = eb.getTypeSG().getComplexTypeSG();
    AttributeSG[] ebctAttr = ebct.getAttributes();
    assertEquals(5, ebctAttr.length);
    assertEquals(new XsQName((String) null, "g"), ebctAttr[0].getName());
    assertEquals(ebctAttr[0].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(double.class));
    assertEquals(new XsQName((String) null, "d"), ebctAttr[1].getName());
    assertEquals(ebctAttr[1].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(Calendar.class));
    assertEquals(ebctAttr[2].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(float.class));
    assertEquals(ebctAttr[3].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(String.class));
    assertEquals(ebctAttr[4].getTypeSG().getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(int.class));
  }

  public void testElements() throws Exception {
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation><xs:appinfo>\n" +
      "    <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "      <jaxb:package name='parsertest.testelements'/>\n" +
      "    </jaxb:schemaBindings>\n" +
      "  </xs:appinfo></xs:annotation>\n" +
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
      "        <xs:element name='d' type='xs:double'>\n" +
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
      "        </xs:extension>\n" +
      "      </xs:simpleContent>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    System.err.println("testElements: ->");
    SchemaSG jschema = parse(schema, "testElements.xsd");

    ObjectSG[] elements = jschema.getElements();
    assertEquals(4, elements.length);

    ObjectSG e1 = elements[0];
    assertEquals(new XsQName((String) null, "a"), e1.getName());
    TypeSG t1 = e1.getTypeSG();
    assertTrue(!t1.isComplex());
    assertTrue(t1.getSimpleTypeSG().isAtomic());
    assertEquals(t1.getSimpleTypeSG().getRuntimeType(), JavaQNameImpl.getInstance(String.class));

    ObjectSG e2 = elements[1];
    assertEquals(new XsQName((String) null, "b"), e2.getName());
    TypeSG t2 = e2.getTypeSG();
    assertTrue(!t2.isComplex());
    SimpleTypeSG st2 = t2.getSimpleTypeSG();
    assertTrue(st2.isAtomic());
    assertEquals(JavaQNameImpl.getInstance(float.class), st2.getRuntimeType());

    ObjectSG e3 = elements[2];
    assertEquals(new XsQName((String) null, "c"), e3.getName());
    TypeSG t3 = e3.getTypeSG();
    assertTrue(t3.isComplex());
    ComplexTypeSG ct3 = t3.getComplexTypeSG();
    assertTrue(!ct3.hasSimpleContent());
    ComplexContentSG cct3 = ct3.getComplexContentSG();
    ParticleSG[] childs = cct3.getRootParticle().getGroupSG().getParticles();
    assertEquals(2, childs.length);
    assertTrue(childs[0].isElement());
    ObjectSG child1 = childs[0].getObjectSG();
    assertEquals(new XsQName((String) null, "a"), child1.getName());
    assertTrue(childs[1].isElement());
    ObjectSG child2 = childs[1].getObjectSG();
    assertEquals(new XsQName((String) null, "d"), child2.getName());
    assertTrue(!child2.getTypeSG().isComplex());
    SimpleTypeSG st4 = child2.getTypeSG().getSimpleTypeSG();
    assertEquals(JavaQNameImpl.getInstance(double.class), st4.getRuntimeType());
    AttributeSG[] attributes = ct3.getAttributes();
    assertEquals(1, attributes.length);
    assertEquals(new XsQName((String) null, "e"), attributes[0].getName());
    assertTrue(!attributes[0].getTypeSG().isComplex());

    ObjectSG e4 = elements[3];
    assertTrue(e4.getTypeSG().isComplex());
    ComplexTypeSG ct4 = e4.getTypeSG().getComplexTypeSG();
    assertTrue(ct4.hasSimpleContent());
    SimpleContentSG sct4 = ct4.getSimpleContentSG();
    assertTrue(!sct4.getContentTypeSG().isComplex());
    assertEquals(JavaQNameImpl.getInstance(int.class), sct4.getContentTypeSG().getRuntimeType());
    assertEquals(1, ct4.getAttributes().length);
    assertEquals(new XsQName((String) null, "g"), ct4.getAttributes()[0].getName());
    assertEquals(JavaQNameImpl.getInstance(boolean.class), ct4.getAttributes()[0].getTypeSG().getRuntimeType());
  }

  public void testFacets() throws Exception {
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation><xs:appinfo>\n" +
      "    <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "      <jaxb:package name='parsertest.testfacets'/>\n" +
      "    </jaxb:schemaBindings>\n" +
      "  </xs:appinfo></xs:annotation>\n" +
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
    
    SchemaSG jschema = parse(schema, "testFacets.xsd");

    ObjectSG[] elements = jschema.getElements();
    assertEquals(1, elements.length);
    ObjectSG se1 = elements[0];
    assertEquals(new XsQName((String) null, "a"), se1.getName());
    assertTrue(!se1.getTypeSG().isComplex());
    SimpleTypeSG sst1 = se1.getTypeSG().getSimpleTypeSG();
    assertEquals(JavaQNameImpl.getInstance(String.class), sst1.getRuntimeType());
    Facet facet = sst1.getFacet(Facet.ENUMERATION);
    assertNotNull(facet);
    String[] values = facet.getValues();
    assertNotNull(values);
    assertEquals(3, values.length);
    assertEquals("AK", values[0]);
    assertEquals("AL", values[1]);
    assertEquals("AR", values[2]);
  }

  public void testGlobalBindingsDefaults() throws Exception {
    // Parse a schema without globalBindings; it should have the default
    // settings.
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'/>\n";
    
    SchemaSG jschema = parse(schema, "testGlobalBindingsDefaults.xsd");

    String collectionType = jschema.getCollectionType();
    assertEquals(ArrayList.class.getName(), collectionType);
    JAXBJavaType[] javaTypes = jschema.getJAXBJavaTypes();
    assertNotNull(javaTypes);
    assertEquals(0, javaTypes.length);
    XsQName[] typesafeEnumBase = jschema.getTypesafeEnumBase();
    assertNotNull(typesafeEnumBase);
    assertEquals(1, typesafeEnumBase.length);
    assertEquals(new XsQName(XSParser.XML_SCHEMA_URI, "NCName"), typesafeEnumBase[0]);
    boolean isBindingStyleModelGroup = jschema.isBindingStyleModelGroup();
    assertTrue(!isBindingStyleModelGroup);
    boolean choiceContentProperty = jschema.isChoiceContentProperty();
    assertTrue(!choiceContentProperty);
    boolean enableFailFastCheck = jschema.isFailFastCheckEnabled();
    assertTrue(!enableFailFastCheck);
    boolean isJavaNamingConventionsEnabled = jschema.isJavaNamingConventionsEnabled();
    assertTrue(isJavaNamingConventionsEnabled);
    boolean isFixedAttributeConstantProperty = jschema.isFixedAttributeConstantProperty();
    assertTrue(!isFixedAttributeConstantProperty);
    boolean generatingIsSetMethod = jschema.isGeneratingIsSetMethod();
    assertTrue(!generatingIsSetMethod);
    boolean underscoreWordSeparator = jschema.isUnderscoreWordSeparator();
    assertTrue(underscoreWordSeparator);
  }


  public void testGlobalBindings() throws Exception {
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation>\n" +
      "    <xs:appinfo>\n" +
      "      <jaxb:globalBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'\n" +
      "          collectionType='indexed'\n" +
      "          typesafeEnumBase='xs:String xs:NCName'\n" +
      "          bindingStyle='modelGroupBinding'\n" +
      "          choiceContentProperty='true'\n" +
      "          enableFailFastCheck='1'\n" +
      "          enableJavaNamingConventions='false'\n" +
      "          fixedAttributeAsConstantProperty='true'\n" +
      "          generateIsSetMethod='1'\n" +
      "          underscoreBinding='asCharInWord'/>\n" +
      "    </xs:appinfo\n>" +
      "  </xs:annotation>\n" +
      "</xs:schema>";
    
    SchemaSG jschema = parse(schema, "testGlobalBindingsDefaults.xsd");

    String collectionType = jschema.getCollectionType();
    assertEquals("indexed", collectionType);
    XsQName[] typesafeEnumBase = jschema.getTypesafeEnumBase();
    assertNotNull(typesafeEnumBase);
    assertEquals(2, typesafeEnumBase.length);
    assertEquals(new XsQName(XSParser.XML_SCHEMA_URI, "String"), typesafeEnumBase[0]);
    assertEquals(new XsQName(XSParser.XML_SCHEMA_URI, "NCName"), typesafeEnumBase[1]);

    boolean isBindingStyleModelGroup = jschema.isBindingStyleModelGroup();
    assertTrue(isBindingStyleModelGroup);

    boolean isChoiceContentProperty = jschema.isChoiceContentProperty();
    assertTrue(isChoiceContentProperty);

    boolean enableFailFastCheck = jschema.isFailFastCheckEnabled();
    assertTrue(enableFailFastCheck);

    boolean isJavaNamingConventionsEnabled = jschema.isJavaNamingConventionsEnabled();
    assertTrue(!isJavaNamingConventionsEnabled);

    boolean fixedAttributeConstantProperty = jschema.isFixedAttributeConstantProperty();
    assertTrue(fixedAttributeConstantProperty);

    boolean generatingIsSetMethod = jschema.isGeneratingIsSetMethod();
    assertTrue(generatingIsSetMethod);

    boolean isUnderscoreWordSeparator = jschema.isUnderscoreWordSeparator();
    assertTrue(!isUnderscoreWordSeparator);

    JAXBJavaType[] javaTypes = jschema.getJAXBJavaTypes();
    assertNotNull(javaTypes);
    assertEquals(0, javaTypes.length);
  }

/*
  public void testSchemaBindingsDefaults() throws Exception {
    // Parse a schema without schemaBindings; it should have the default
    // settings.
    final String mName = "testSchemaBindingsDefaults";
    log.entering(mName);
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'/>\n";
    
    JAXBSchemaReader r = new JAXBSchemaReader();
    InputSource isource = new InputSource(new StringReader(schema));
    isource.setSystemId("testSchemaBindingsDefaults.xsd");
    JAXBSchema jschema = (JAXBSchema) r.parse(isource);
    SchemaBindings schemaBindings = jschema.getSchemaBindings();
    List nameXmlTransforms = schemaBindings.getNameXmlTransform();
    assertNotNull(nameXmlTransforms);
    assertEquals(0, nameXmlTransforms.size());
    assertNull(schemaBindings.getPackage());
  }

  public void testSchemaBindings() throws Exception {
    final String mName = "testSchemaBindings";
    final String myPackageName = "org.apache.ws.jaxme.somepackage";

    log.entering(mName);
    final String schema =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation>\n" +
      "    <xs:appinfo>\n" +
      "      <jaxb:schemaBindings xmlns:jaxb='" + JAXBSchemaReader.JAXB_SCHEMA_URI + "'>\n" +
      "        <jaxb:package name='" + myPackageName + "'>\n" +
      "          <jaxb:javadoc>Test documentation</jaxb:javadoc>\n" +
      "        </jaxb:package>\n" +
      "        <jaxb:nameXmlTransform>\n" +
      "          <jaxb:typeName prefix='b' suffix='a'/>\n" +
      "        </jaxb:nameXmlTransform>\n" +
      "      </jaxb:schemaBindings>\n" +
      "    </xs:appinfo>\n" +
      "  </xs:annotation>\n" +
      "  <xs:complexType name='a'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element name='x' type='xs:int'/>\n" +
      "    </xs:sequence>\n" +
      "    <xs:attribute name='y' type='xs:string'/>\n" +
      "  </xs:complexType>\n" +
      "  <xs:element name='b' type='a'/>\n" +
      "</xs:schema>";
    
    JAXBSchemaReader r = new JAXBSchemaReader();
    InputSource isource = new InputSource(new StringReader(schema));
    isource.setSystemId("testSchemaBindings.xsd");
    JAXBSchema jschema = (JAXBSchema) r.parse(isource);
    SchemaBindings schemaBindings = jschema.getSchemaBindings();

    SchemaBindings.Package schemaPackage = schemaBindings.getPackage();
    assertNotNull(schemaPackage);
    assertEquals(myPackageName, schemaPackage.getName());
    Javadoc javadoc = schemaPackage.getJavadoc();
    assertNotNull(javadoc);
    assertEquals("Test documentation", javadoc.getText());

    List nameXmlTransforms = schemaBindings.getNameXmlTransform();
    assertNotNull(nameXmlTransforms);
    assertEquals(1, nameXmlTransforms.size());
    SchemaBindings.NameXmlTransform nameXmlTransform =
      (SchemaBindings.NameXmlTransform) nameXmlTransform.get(0);
    assertNotNull(nameXmlTransform);
    SchemaBindings.NameTransformation transformation =
      (SchemaBindings.NameTransformation) nameXmlTransform.getTypeName();
    assertEquals("a", transformation.getSuffix());
    assertEquals("b", transformation.getPrefix());

    JAXBSchemaWriter writer = new JAXBSchemaWriter();
    SGFactory factory = writer.getSGFactory();

    SchemaType[] types = jschema.getSchemaTypes();
    assertNotNull(types);
    assertEquals(1, types.length);
    SchemaType st = types[0];
    TypeSG typeSG = factory.getTypeSG(st);
    JavaQName qName = typeSG.getClassName(st, SchemaClass.CLASS_TYPE_XML_INTERFACE);
    assertEquals(myPackageName, qName.getPackageName());
  }
*/

  public void testPurchaseOrder() throws Exception {
    /*
     A modified version of the first example from XML Schema Part 0:
     http:www.w3.org/TR/xmlschema-0/#po.xml 
     Modifications:
     1) Removed forward references by re-ordering the document 
     2) Changed some types to remove types not currently supported
     xsd:NMTOKEN         -> xsd:string 
     xsd:positiveInteger -> xsd:integer
    */

    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " \n" +
      " <xsd:annotation> \n" +
      "  <xsd:documentation xml:lang='en'> \n" +
      "   Purchase order schema for Example.com. \n" +
      "   Copyright 2000 Example.com. All rights reserved. \n" +
      "  </xsd:documentation> \n" +
      "  <xsd:appinfo>\n" +
      "   <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "    <jaxb:package name='parsertest.testpurchaseorder'/>\n" +
      "   </jaxb:schemaBindings>\n" +
      "  </xsd:appinfo>\n" +
      " </xsd:annotation> \n" +
      " \n" +
      " <xsd:element name='comment' type='xsd:string'/> \n" +
      " \n" +
      " <xsd:simpleType name='SKU'> \n" +
      "  <xsd:restriction base='xsd:string'> \n" +
      "   <xsd:pattern value='\\d{3}-[A-Z]{2}'/> \n" +
      "  </xsd:restriction> \n" +
      " </xsd:simpleType> \n" +
      " \n" +
      " <xsd:complexType name='USAddress'> \n" +
      "  <xsd:sequence> \n" +
      "   <xsd:element name='name'   type='xsd:string'/> \n" +
      "   <xsd:element name='street' type='xsd:string'/> \n" +
      "   <xsd:element name='city'   type='xsd:string'/> \n" +
      "   <xsd:element name='state'  type='xsd:string'/> \n" +
      "   <xsd:element name='zip'    type='xsd:integer'/> \n" +
      "  </xsd:sequence> \n" +
      "  <xsd:attribute name='country' type='xsd:string' fixed='US'/> \n" +
      " </xsd:complexType> \n" +
      " \n" +
      " <xsd:complexType name='Items'> \n" +
      "  <xsd:sequence> \n" +
      "   <xsd:element name='item' minOccurs='0' maxOccurs='unbounded'> \n" +
      "    <xsd:complexType> \n" +
      "     <xsd:sequence> \n" +
      "      <xsd:element name='productName' type='xsd:string'/> \n" +
      "      <xsd:element name='quantity'> \n" +
      "       <xsd:simpleType> \n" +
      "        <xsd:restriction base='xsd:positiveInteger'> \n" +
      "         <xsd:maxExclusive value='100'/> \n" +
      "        </xsd:restriction> \n" +
      "       </xsd:simpleType> \n" +
      "      </xsd:element> \n" +
      "      <xsd:element name='USPrice'  type='xsd:decimal'/> \n" +
      "      <xsd:element ref='comment'   minOccurs='0'/> \n" +
      "      <xsd:element name='shipDate' type='xsd:date' minOccurs='0'/> \n" +
      "     </xsd:sequence> \n" +
      "     <xsd:attribute name='partNum' type='SKU' use='required'/> \n" +
      "    </xsd:complexType> \n" +
      "   </xsd:element> \n" +
      "  </xsd:sequence> \n" +
      " </xsd:complexType> \n" +
      " \n" +
      " <xsd:complexType name='PurchaseOrderType'> \n" +
      "  <xsd:sequence> \n" +
      "   <xsd:element name='shipTo' type='USAddress'/> \n" +
      "   <xsd:element name='billTo' type='USAddress'/> \n" +
      "   <xsd:element ref='comment' minOccurs='0'/> \n" +
      "   <xsd:element name='items'  type='Items'/> \n" +
      "  </xsd:sequence> \n" +
      "  <xsd:attribute name='orderDate' type='xsd:date'/> \n" +
      " </xsd:complexType> \n" +
      " \n" +
      " <xsd:element name='purchaseOrder' type='PurchaseOrderType'/> \n" +
      " \n" +
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testPurchaseOrder.xsd");

    TypeSG[] schemaTypes = jschema.getTypes();
    assertNotNull(schemaTypes);
    assertEquals(4, schemaTypes.length);

    // SKU
    TypeSG sku = schemaTypes[0];
    assertEquals(new XsQName((String) null, "SKU"), sku.getName());
    assertTrue(sku.isGlobalType());
    assertTrue(!sku.isComplex());
    assertTrue(sku.isRestriction());
    assertEquals(new XsQName(XSParser.XML_SCHEMA_URI, "string"), sku.getRestrictedType().getName());

    // USAddress
    // <xsd:complexType name='USAddress'> 
    // <xsd:sequence>
    TypeSG usAddress = schemaTypes[1];
    assertEquals(new XsQName((String) null, "USAddress"), usAddress.getName());
    assertTrue(usAddress.isGlobalType());
    assertTrue(usAddress.isComplex());
    assertTrue(usAddress.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().isSequence());
    // USAddress.country
    //  <xsd:attribute name='country' type='xsd:string' fixed='US'/>
    // ToDo: test attribute fixed='US'
    AttributeSG [] usAddressAttributes = usAddress.getComplexTypeSG().getAttributes();
    assertEquals(1, usAddressAttributes.length);
    AttributeSG country = usAddressAttributes[0];
    assertEquals(new XsQName((String) null, "country"), country.getName());
    assertEquals(JavaQNameImpl.getInstance(String.class), country.getTypeSG().getRuntimeType());
    // USAddress children
    String [] nameShouldBe = {"name", "street", "city",  "state", "zip"};
    ParticleSG [] usAddressChildren = usAddress.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().getParticles();
    assertEquals(5, usAddressChildren.length);
    for (int i = 0; i < usAddressChildren.length; i++) {
      ParticleSG child = usAddressChildren[i];
      assertTrue(child.isElement());
      assertEquals(new XsQName((String) null, nameShouldBe[i]), child.getObjectSG().getName());
      assertTrue(!child.getObjectSG().getTypeSG().isComplex());
      Class childRuntimeClass = "zip".equals(nameShouldBe[i]) ? BigInteger.class : String.class;
      assertEquals(JavaQNameImpl.getInstance(childRuntimeClass), child.getObjectSG().getTypeSG().getRuntimeType());
    }

    // Items
    TypeSG items = schemaTypes[2];
    assertEquals(new XsQName((String) null, "Items"), items.getName());
    assertTrue(items.isGlobalType());
    assertTrue(items.isComplex());
    ComplexTypeSG itemsComplex = items.getComplexTypeSG();
    assertTrue(!itemsComplex.hasSimpleContent());
    ComplexContentSG itemsComplexContent = itemsComplex.getComplexContentSG();
    GroupSG group = itemsComplexContent.getRootParticle().getGroupSG();
    assertTrue(group.isSequence());
    // Items.item
    ParticleSG[] itemsChildren = group.getParticles();
    assertEquals(1, itemsChildren.length);
    ParticleSG item = itemsChildren[0];
    assertTrue(item.isElement());
    assertEquals(new XsQName((String) null, "item"), item.getObjectSG().getName());
    assertEquals(0, item.getMinOccurs());
    assertEquals(-1, item.getMaxOccurs());
    TypeSG itemST = item.getObjectSG().getTypeSG();
    assertTrue(itemST.isComplex());
    assertTrue(!itemST.getComplexTypeSG().hasSimpleContent());
    assertTrue(itemST.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().isSequence());
    // Items.item.partNum
    // <xsd:attribute name='partNum' type='SKU' use='required'/>
    AttributeSG[] itemAttributes = itemST.getComplexTypeSG().getAttributes();
    assertEquals(1, itemAttributes.length);
    AttributeSG partNum = itemAttributes[0];
    assertEquals(new XsQName((String) null, "partNum"), partNum.getName());
    assertTrue(partNum.isRequired());
    // Items.item.USPrice
    // <xsd:element name='USPrice'  type='xsd:decimal'/> 
    ParticleSG[] itemChildren = itemST.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().getParticles();
    assertEquals(5, itemChildren.length);
    ParticleSG usPrice = itemChildren[2];
    assertTrue(usPrice.isElement());
    assertEquals(new XsQName((String) null,"USPrice"), usPrice.getObjectSG().getName());
    TypeSG usPriceSST = usPrice.getObjectSG().getTypeSG();
    assertTrue(!usPrice.getObjectSG().getTypeSG().isComplex());
    assertTrue(usPriceSST.getSimpleTypeSG().isAtomic());
    assertEquals(java.math.BigDecimal.class.getName(), usPriceSST.getRuntimeType().toString());   
    // Items.item.comment
    // <xsd:element ref='comment'   minOccurs='0'/> 
    ParticleSG comment = itemChildren[3];
    assertTrue(comment.isElement());
    assertEquals(new XsQName((String) null, "comment"), comment.getObjectSG().getName());
    assertEquals(0, comment.getMinOccurs());
    TypeSG commentSST = comment.getObjectSG().getTypeSG();
    assertTrue(!commentSST.isComplex());
    assertTrue(commentSST.getSimpleTypeSG().isAtomic());
    assertEquals(JavaQNameImpl.getInstance(String.class), commentSST.getRuntimeType());   
    // Items.item.productName
    // <xsd:element name='productName' type='xsd:string'/>
    ParticleSG productName = itemChildren[0];
    assertTrue(productName.isElement());
    assertEquals(new XsQName((String) null, "productName"), productName.getObjectSG().getName());
    TypeSG productNameSST = productName.getObjectSG().getTypeSG();
    assertTrue(!productNameSST.isComplex());
    assertTrue(productNameSST.getSimpleTypeSG().isAtomic());
    assertEquals(JavaQNameImpl.getInstance(String.class), productNameSST.getRuntimeType());   
    // Items.item.quantity
    ParticleSG quantity = itemChildren[1];
    assertTrue(quantity.isElement());
    assertEquals(new XsQName((String) null, "quantity"), quantity.getObjectSG().getName());
    TypeSG quantitySST = quantity.getObjectSG().getTypeSG();
    assertTrue(!quantitySST.isComplex());
    assertTrue(quantitySST.getSimpleTypeSG().isAtomic());
    assertEquals(JavaQNameImpl.getInstance(java.math.BigInteger.class), quantitySST.getRuntimeType());    
    // Items.item.shipDate
    // <xsd:element name='shipDate' type='xsd:date' minOccurs='0'/> 
    ParticleSG shipDate = itemChildren[4];
    assertTrue(shipDate.isElement());
    assertEquals(new XsQName((String) null, "shipDate"), shipDate.getObjectSG().getName());
    TypeSG shipDateSST = shipDate.getObjectSG().getTypeSG();
    assertTrue(!shipDateSST.isComplex());
    assertEquals(0, shipDate.getMinOccurs());
    assertTrue(shipDateSST.getSimpleTypeSG().isAtomic());
    assertEquals(Calendar.class.getName(), shipDateSST.getRuntimeType().toString());    

    // PurchaseOrderType
    TypeSG purchaseOrderType = schemaTypes[3];
    assertEquals(new XsQName((String) null, "PurchaseOrderType"), purchaseOrderType.getName());
    assertTrue(purchaseOrderType.isGlobalType());
    assertTrue(purchaseOrderType.isComplex());
    assertTrue(purchaseOrderType.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().isSequence());
    // PurchaseOrderType.orderDate
    // <xsd:attribute name='orderDate' type='xsd:date'/>
    AttributeSG [] potAttributes = purchaseOrderType.getComplexTypeSG().getAttributes();
    assertEquals(1, potAttributes.length);
    AttributeSG orderDate = potAttributes[0];
    assertEquals(new XsQName((String) null, "orderDate"), orderDate.getName());
    assertEquals(JavaQNameImpl.getInstance(Calendar.class), 
                 orderDate.getTypeSG().getSimpleTypeSG().getRuntimeType());
    ParticleSG [] potChildren = purchaseOrderType.getComplexTypeSG().getComplexContentSG().getRootParticle().getGroupSG().getParticles();
    assertEquals(4, potChildren.length);
    // PurchaseOrderType.shipTo
    // <xsd:element name='shipTo' type='USAddress'/> 
    ParticleSG shipTo = potChildren[0];
    assertTrue(shipTo.isElement());
    assertEquals(new XsQName((String) null, "shipTo"), shipTo.getObjectSG().getName());
    TypeSG shipToST = shipTo.getObjectSG().getTypeSG();
    assertTrue(shipToST.isComplex());
    assertEquals(new XsQName((String) null, "USAddress"), shipToST.getName());
    assertTrue(shipToST.isComplex());
    // PurchaseOrderType.billTo
    // <xsd:element name='billTo' type='USAddress'/> 
    ParticleSG billTo = potChildren[1];
    assertTrue(billTo.isElement());
    assertEquals(new XsQName((String) null, "billTo"), billTo.getObjectSG().getName());
    TypeSG billToST = billTo.getObjectSG().getTypeSG();
    assertTrue(billToST.isComplex());
    assertEquals(new XsQName((String) null, "USAddress"), billToST.getName());
    assertTrue(billToST.isComplex());
    // PurchaseOrderType.comment
    // <xsd:element ref='comment' minOccurs='0'/> 
    ParticleSG potComment = potChildren[2];
    assertTrue(potComment.isElement());
    assertEquals(new XsQName((String) null, "comment"), comment.getObjectSG().getName());
    assertEquals(0, comment.getMinOccurs());
    TypeSG potCommentST = potComment.getObjectSG().getTypeSG();
    assertTrue(!potCommentST.isComplex());
    assertEquals(JavaQNameImpl.getInstance(String.class), potCommentST.getRuntimeType()); 
    // PurchaseOrderType.items
    // <xsd:element name='items'  type='Items'/> 
    ParticleSG potItems = potChildren[3];
    assertTrue(potItems.isElement());
    assertEquals(new XsQName((String) null, "items"), potItems.getObjectSG().getName());
    assertTrue(potItems.getObjectSG().getTypeSG().isComplex());

    // purchaseOrder
    // <xsd:element name='purchaseOrder' type='PurchaseOrderType'/>
    ObjectSG[] elements = jschema.getElements();
    assertNotNull(elements);
    assertEquals(2, elements.length);
    ObjectSG purchaseOrder = elements[1];
    assertEquals(new XsQName((String) null, "purchaseOrder"), purchaseOrder.getName());
    assertTrue(purchaseOrder.getTypeSG().isComplex());
    assertTrue(purchaseOrder.getTypeSG().isGlobalType());
    assertEquals(new XsQName((String) null, "PurchaseOrderType"), purchaseOrder.getTypeSG().getName());
  }

  /** A test case to trigger a previous parser bug.  */
  public void testRestrictionMaxExclusive() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testrestrictionmaxexclusive'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='quantity'> \n" +
      "  <xsd:simpleType> \n" +
      "   <xsd:restriction base='xsd:decimal'> \n" +
      "    <xsd:maxExclusive value='100'/> \n" +
      "   </xsd:restriction> \n" +
      "  </xsd:simpleType> \n" +
      " </xsd:element> \n" +
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testRestrictionMaxExclusive.xsd");
    ObjectSG[] elements = jschema.getElements();
    assertEquals(1, elements.length);
    ObjectSG quantity = elements[0];
    String maxExclusive = quantity.getTypeSG().getSimpleTypeSG().getAtomicType().getMaxExclusive();
    assertNotNull(maxExclusive);
    assertEquals("100", maxExclusive);
  }

  /** Test some basic functionality of the builtin datatype nonPositiveInteger. */
  public void testNonPositiveInteger() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testnonpositiveinteger'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='non-positive-integer' type='xsd:nonPositiveInteger'/> \n" + 
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testNonPositiveInteger.xsd");

    // simple, atomic, with restriction and maxExclusive and maxInclusive
    TypeSG npi = jschema.getElements()[0].getTypeSG();
    assertTrue(!npi.isComplex());
    SimpleTypeSG npis = npi.getSimpleTypeSG();
    assertTrue(npis.isAtomic());
    assertTrue(!npis.isList());
    assertTrue(!npis.isUnion());
    assertEquals(new Long(0), npis.getAtomicType().getFractionDigits());
    assertEquals("0", npis.getAtomicType().getMaxInclusive());
    assertEquals("1", npis.getAtomicType().getMaxExclusive());
  }

  /** Test some basic functionality of the builtin datatype negativeInteger. */
  public void testNegativeInteger() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testnegativeinteger'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='negative-integer' type='xsd:negativeInteger'/> \n" + 
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testNegativeInteger.xsd");

    // simple, atomic, with restrictions on maxInclusive and MaxExclusive
    TypeSG ni = jschema.getElements()[0].getTypeSG();
    assertTrue(!ni.isComplex());
    SimpleTypeSG nis = ni.getSimpleTypeSG();
    assertTrue(nis.isAtomic());
    assertTrue(!nis.isList());
    assertTrue(!nis.isUnion());
    assertEquals(new Long(0), nis.getAtomicType().getFractionDigits());
    assertEquals("-1", nis.getAtomicType().getMaxInclusive());
    assertEquals("0", nis.getAtomicType().getMaxExclusive());
  }

  public void testNonNegativeIntegerType() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testnonnegativeinteger'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='non-negative-integer' type='xsd:nonNegativeInteger'/> \n" + 
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testNonNegativeIntegerType.xsd");

    // simple, atomic, with restriction on minInclusive and minExclusive
    TypeSG nni = jschema.getElements()[0].getTypeSG();
    assertTrue(!nni.isComplex());
    SimpleTypeSG nnis = nni.getSimpleTypeSG();
    assertTrue(nnis.isAtomic());
    assertTrue(!nnis.isList());
    assertTrue(!nnis.isUnion());
    assertEquals(new Long(0), nnis.getAtomicType().getFractionDigits());
    assertEquals("-1", nnis.getAtomicType().getMinExclusive());
    assertEquals("0", nnis.getAtomicType().getMinInclusive());
  }

  public void testPositiveIntegerType() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testpositiveinteger'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='positive-integer' type='xsd:positiveInteger'/> \n" + 
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testPositiveIntegerType.xsd");

    // simple, atomic, with restriction on minInclusive and minExclusive
    TypeSG pi = jschema.getElements()[0].getTypeSG();
    assertTrue(!pi.isComplex());
    SimpleTypeSG pis = pi.getSimpleTypeSG();
    assertTrue(pis.isAtomic());
    assertTrue(!pis.isList());
    assertTrue(!pis.isUnion());
    assertEquals(new Long(0), pis.getAtomicType().getFractionDigits());
    assertEquals("0", pis.getAtomicType().getMinExclusive());
    assertEquals("1", pis.getAtomicType().getMinInclusive());
  }

  /** Test some basic functionality of the builtin datatype integer. */
  public void testIntegerType() throws Exception {
    final String schema = 
      "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xsd:annotation><xsd:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testintegertype'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xsd:appinfo></xsd:annotation>\n" +
      " <xsd:element name='integer' type='xsd:integer'/> \n" + 
      "</xsd:schema> \n";

    SchemaSG jschema = parse(schema, "testIntegerType.xsd");

    // simple, atomic, with restriction on fractionDigits
    ObjectSG[] elements = jschema.getElements();
    TypeSG i = elements[0].getTypeSG();
    assertTrue(!i.isComplex());
    SimpleTypeSG is = i.getSimpleTypeSG();
    assertTrue(is.isAtomic());
    assertTrue(!is.isList());
    assertTrue(!is.isUnion());
    assertEquals(JavaQNameImpl.getInstance(BigInteger.class), is.getRuntimeType());
    assertEquals(new Long(0), is.getAtomicType().getFractionDigits());
  }

  /** Test built-in type NMTOKENS. */
  public void testNmTokensType() throws Exception {
    final String schema = 
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'> \n" +
      " <xs:annotation><xs:appinfo>\n" +
      "  <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "   <jaxb:package name='parsertest.testnmtokenstype'/>\n" +
      "  </jaxb:schemaBindings>\n" +
      " </xs:appinfo></xs:annotation>\n" +
      " <xs:element name='some-nmtokens' type='xs:NMTOKENS'/> \n" + 
      "</xs:schema> \n";

    SchemaSG jschema = parse(schema, "testNmTokensType.xsd");

    // list of one or more nmtoken's
    ObjectSG[] elements = jschema.getElements();
    assertEquals(1, elements.length);
    TypeSG nmts = elements[0].getTypeSG();
    assertTrue(!nmts.isComplex());
    SimpleTypeSG nmtss = nmts.getSimpleTypeSG();
    assertTrue(!nmtss.isAtomic());
    assertTrue(nmtss.isList());
    assertTrue(!nmtss.isUnion());
    ListTypeSG nmtsl = nmtss.getListType();
    assertEquals(new Long(1), nmtsl.getMinLength());
  }  

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-46">JAXME-46</a>
     */
    public void testJira46() throws Exception {
    	final String uri = "http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/";
    	final String schemaSpec =
    		"<xs:schema targetNamespace='" + uri + "'\n" +
    		"    xmlns:eGov_IT='" + uri + "'\n" +
    		"    xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
    		"  <xs:element name='Riferimento'>\n" +
    		"    <xs:complexType>\n" +
    		"      <xs:sequence/>\n" +
    		"      <xs:attribute ref='eGov_IT:id' use='required'/>\n" +
    		"    </xs:complexType>\n" +
    		"  </xs:element>\n" +
    		"  <xs:attribute name='id' type='xs:ID'/>\n" +
    		"</xs:schema>\n";
    	
    	InputSource isource = new InputSource(new StringReader(schemaSpec));
    	isource.setSystemId("jira46.xsd");
    	JAXBSchemaReader r = getSchemaReader();
    	XSParser parser = r.getSGFactory().newXSParser();
    	parser.setValidating(false);
    	XSSchema schema = parser.parse(isource);
    	XSAttribute[] attrs = schema.getAttributes();
    	assertEquals(1, attrs.length);
    	XSAttribute idAttr = attrs[0];
    	assertTrue(idAttr instanceof JAXBAttribute);
    	assertEquals(new XsQName(uri, "id"), idAttr.getName());
    	assertEquals(XSID.getInstance(), idAttr.getType());
    	assertTrue(idAttr.isOptional());
    	XSElement[] elements = schema.getElements();
    	assertEquals(1, elements.length);
    	XSElement rifElem = elements[0];
    	assertFalse(rifElem.getType().isSimple());
    	XSComplexType ct = rifElem.getType().getComplexType();
    	XSAttributable[] rifAttrs = ct.getAttributes();
    	assertEquals(1, rifAttrs.length);
    	XSAttribute idRef = (XSAttribute) rifAttrs[0];
    	assertTrue(idRef instanceof JAXBAttribute);
    	assertFalse(idRef.equals(idAttr));
    	assertEquals(new XsQName(uri, "id"), idAttr.getName());
    	assertEquals(XSID.getInstance(), idAttr.getType());
    	assertFalse(idRef.isOptional());
    }

    /** Test, whether a choice group with multiplicity > 1 is
     * rejected.
     */
    public void testMultipleGroupRejected() throws Exception {
    	final String schemaSpec =
    		"<xs:schema\n" +
    		"    xmlns:xs='http://www.w3.org/2001/XMLSchema'\n" +
    		"    elementFormDefault='qualified'\n" +
    		"    xmlns:jaxb='http://java.sun.com/xml/ns/jaxb'\n" +
    		"    jaxb:version='1.0'>\n" +
            "  <xs:annotation><xs:appinfo>\n" +
            "    <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
            "      <jaxb:package name='parsertest.testmultiplegroupsrejected'/>\n" +
            "    </jaxb:schemaBindings>\n" +
            "  </xs:appinfo></xs:annotation>\n" +
            "  <xs:element name='DIAGJOBS'>\n" +
    		"    <xs:complexType>\n" +
    		"      <xs:choice maxOccurs='unbounded'>\n" +
    		"        <xs:element name='DIAGJOB' type='xs:string'/>\n" +
    		"        <xs:sequence>\n" +
    		"          <xs:element name='DJREF' type='xs:long'/>\n" +
    		"          <xs:element name='DESCRIPTIONS' type='xs:string' minOccurs='0'/>\n" +
    		"        </xs:sequence>\n" +
    		"      </xs:choice>\n" +
    		"    </xs:complexType>\n" +
    		"  </xs:element>\n" +
    		"</xs:schema>\n";
      	SchemaSG schema = parse(schemaSpec, "testMultipleGroupRejected.xsd");
      	try {
      		schema.generate();
      		fail("Expected an exception");
      	} catch (SAXException e) {
      		assertTrue(e.getMessage().indexOf("Model groups with maxOccurs > 1 are not yet supported.") != -1);
      	}
    }
}
