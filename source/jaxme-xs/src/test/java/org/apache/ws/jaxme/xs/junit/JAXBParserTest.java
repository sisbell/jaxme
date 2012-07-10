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

import java.io.StringReader;
import java.util.Iterator;

import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBAttribute;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBElement;
import org.apache.ws.jaxme.xs.jaxb.JAXBEnumeration;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBGroup;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBSimpleType;
import org.apache.ws.jaxme.xs.jaxb.JAXBType;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumMember;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.types.XSDateTime;
import org.apache.ws.jaxme.xs.types.XSNCName;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBParserTest extends ParserTest {
  public void testJAXBGlobalBindingsDefaults() throws Exception {
    // Parse a schema without globalBindings; it should have the default
    // settings.
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'/>\n";
    
    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testGlobalBindingsDefaults.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    JAXBGlobalBindings globalBindings = schema.getJAXBGlobalBindings();
    String collectionType = globalBindings.getCollectionType();
    assertNull(collectionType);
    JAXBJavaType[] javaTypes = globalBindings.getJavaType();
    assertNotNull(javaTypes);
    assertEquals(0, javaTypes.length);
    XsQName[] typesafeEnumBase = globalBindings.getTypesafeEnumBase();
    assertNotNull(typesafeEnumBase);
    assertEquals(1, typesafeEnumBase.length);
    XsQName qName = typesafeEnumBase[0];
    assertEquals(XSNCName.getInstance().getName(), qName);
    boolean bindingStyleModelGroup = globalBindings.isBindingStyleModelGroup();
    assertTrue(!bindingStyleModelGroup);
    boolean choiceContentProperty = globalBindings.isChoiceContentProperty();
    assertTrue(!choiceContentProperty);
    boolean enableFailFastCheck = globalBindings.isEnableFailFastCheck();
    assertTrue(!enableFailFastCheck);
    boolean enableJavaNamingConventions = globalBindings.isEnableJavaNamingConventions();
    assertTrue(enableJavaNamingConventions);
    boolean fixedAttributeAsConstantProperty = globalBindings.isFixedAttributeAsConstantProperty();
    assertTrue(!fixedAttributeAsConstantProperty);
    boolean generateIsSetMethod = globalBindings.isGenerateIsSetMethod();
    assertTrue(!generateIsSetMethod);
    JAXBGlobalBindings.UnderscoreBinding underscoreBinding = globalBindings.getUnderscoreBinding();
    assertEquals(JAXBGlobalBindings.UnderscoreBinding.AS_WORD_SEPARATOR, underscoreBinding);
  }

  public void testJAXBGlobalBindings() throws Exception {
    // Parse a schema with globalBindings
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation>\n" +
      "    <xs:appinfo>\n" +
      "      <jaxb:globalBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'\n" +
      "          collectionType='java.util.ArrayList'\n" +
      "          typesafeEnumBase='xs:string xs:NCName'\n" +
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
    
    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testGlobalBindingsDefaults.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    JAXBGlobalBindings globalBindings = schema.getJAXBGlobalBindings();
    String collectionType = globalBindings.getCollectionType();
    assertEquals("java.util.ArrayList", collectionType);
  
    XsQName[] typesafeEnumBase = globalBindings.getTypesafeEnumBase();
    assertNotNull(typesafeEnumBase);
    assertEquals(2, typesafeEnumBase.length);
    XsQName qName = typesafeEnumBase[0];
    assertEquals(qName, XSString.getInstance().getName());
    qName = typesafeEnumBase[1];
    assertEquals(qName, XSNCName.getInstance().getName());
  
    boolean bindingStyleModelGroup = globalBindings.isBindingStyleModelGroup();
    assertTrue(bindingStyleModelGroup);
  
    boolean choiceContentProperty = globalBindings.isChoiceContentProperty();
    assertTrue(choiceContentProperty);
  
    boolean enableFailFastCheck = globalBindings.isEnableFailFastCheck();
    assertTrue(enableFailFastCheck);
  
    boolean enableJavaNamingConventions = globalBindings.isEnableJavaNamingConventions();
    assertTrue(!enableJavaNamingConventions);
  
    boolean fixedAttributeAsConstantProperty = globalBindings.isFixedAttributeAsConstantProperty();
    assertTrue(fixedAttributeAsConstantProperty);
  
    boolean generateIsSetMethod = globalBindings.isGenerateIsSetMethod();
    assertTrue(generateIsSetMethod);
  
    JAXBGlobalBindings.UnderscoreBinding underscoreBinding = globalBindings.getUnderscoreBinding();
    assertEquals(JAXBGlobalBindings.UnderscoreBinding.AS_CHAR_IN_WORD, underscoreBinding);
  
    JAXBJavaType[] javaTypes = globalBindings.getJavaType();
    assertNotNull(javaTypes);
    assertEquals(0, javaTypes.length);
  }

  public void testJAXBSchemaBindingsDefaults() throws Exception {
    // Parse a schema without schemaBindings; it should have the default
    // settings.
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:complexType name='a'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element name='b' type='xs:string'/>\n" +
      "    </xs:sequence>\n" +
      "  </xs:complexType>\n" +
      "</xs:schema>";
    
    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testSchemaBindingsDefaults.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    JAXBType a = (JAXBType) schema.getType(new XsQName((String) null, "a"));
    assertNotNull(a);
    assertComplexType(a);
  
    JAXBSchemaBindings schemaBindings = a.getJAXBSchemaBindings();
    JAXBSchemaBindings.NameXmlTransform[] nameXmlTransforms = schemaBindings.getNameXmlTransform();
    assertNotNull(nameXmlTransforms);
    assertEquals(0, nameXmlTransforms.length);
    assertNull(schemaBindings.getPackage());
  }

  public void testJAXBSchemaBindings() throws Exception {
    final String myPackageName = "org.apache.ws.jaxme.somepackage";
  
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n" +
      "  <xs:annotation>\n" +
      "    <xs:appinfo>\n" +
      "      <jaxb:schemaBindings xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
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
    
    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testSchemaBindings.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    JAXBType a = (JAXBType) schema.getType(new XsQName((String) null, "a"));
    JAXBSchemaBindings schemaBindings = a.getJAXBSchemaBindings();
  
    JAXBSchemaBindings.Package schemaPackage = schemaBindings.getPackage();
    assertNotNull(schemaPackage);
    assertEquals(myPackageName, schemaPackage.getName());
    JAXBJavadoc javadoc = schemaPackage.getJavadoc();
    assertNotNull(javadoc);
    assertEquals("Test documentation", javadoc.getText());
  
    JAXBSchemaBindings.NameXmlTransform[] nameXmlTransforms = schemaBindings.getNameXmlTransform();
    assertNotNull(nameXmlTransforms);
    assertEquals(1, nameXmlTransforms.length);
    JAXBSchemaBindings.NameXmlTransform nameXmlTransform = nameXmlTransforms[0];
    assertNotNull(nameXmlTransform);
    JAXBSchemaBindings.NameTransformation transformation = nameXmlTransform.getTypeName();
    assertEquals("a", transformation.getSuffix());
    assertEquals("b", transformation.getPrefix());
  }

  public void testJAXBClass() throws Exception {
    {
      final String schemaSource =
        "<?xml version='1.0'?>\n" +
        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
        "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
        "  <xs:complexType name='a'>\n" +
        "    <xs:annotation>\n" +
        "      <xs:appinfo>\n" +
        "        <jaxb:class name='b' implClass='com.b.c'/>\n" +
        "      </xs:appinfo>\n" +
        "    </xs:annotation>\n" +
        "    <xs:sequence>\n" +
        "      <xs:element name='x' type='xs:int'/>\n" +
        "      <xs:element name='f' type='xs:string'>\n" +
        "        <xs:annotation><xs:appinfo>\n" +
        "            <jaxb:class name='x'/>\n" +
        "        </xs:appinfo></xs:annotation>\n" +
        "      </xs:element>\n" +
        "    </xs:sequence>\n" +
        "    <xs:attribute name='y' type='xs:string'/>\n" +
        "  </xs:complexType>\n" +
        "  <xs:element name='g' type='xs:string'/>\n" +
        "  <xs:group name='m'>\n" +
        "    <xs:annotation>\n" +
        "      <xs:appinfo>\n" +
        "        <jaxb:class name='k' implClass='com.b.i'/>\n" +
        "      </xs:appinfo>\n" +
        "    </xs:annotation>\n" +
        "    <xs:sequence>\n" +
        "      <xs:element name='n' type='xs:float'/>\n" +
        "    </xs:sequence>\n" +
        "  </xs:group>\n" +
        "</xs:schema>";
  
      JAXBParser parser = newJAXBParser();
      InputSource isource = new InputSource(new StringReader(schemaSource));
      isource.setSystemId("testClass1.xsd");
      JAXBSchema schema = (JAXBSchema) parser.parse(isource);
      JAXBType a = (JAXBType) schema.getType(new XsQName((String) null, "a"));
      assertNotNull(a);
      JAXBClass jaxbClass = a.getJAXBClass();
      assertNotNull(jaxbClass);
      assertEquals("b", jaxbClass.getName());
      assertEquals("com.b.c", jaxbClass.getImplClass());
      XSParticle p1 = assertComplexContent(assertComplexType(a));
      XSGroup group = assertGroup(p1);
      XSParticle[] particles = group.getParticles();
      assertEquals(2, particles.length);
      JAXBElement f = (JAXBElement) assertElement(particles[1]);
      jaxbClass = f.getJAXBClass();
      assertNotNull(jaxbClass);
      assertEquals("x", jaxbClass.getName());
      assertNull(jaxbClass.getImplClass());


      JAXBElement g = (JAXBElement) schema.getElement(new XsQName((String) null, "g"));
      assertNotNull(g);
      assertNull(g.getJAXBClass());

      JAXBGroup m = (JAXBGroup) schema.getGroup(new XsQName((String) null, "m"));
      jaxbClass = m.getJAXBClass();
      assertNotNull(jaxbClass);
      assertEquals("k", jaxbClass.getName());
      assertEquals("com.b.i", jaxbClass.getImplClass());
    }

    {
      final String schemaSource =
        "<?xml version='1.0'?>\n" +
        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
        "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
        "  <xs:complexType name='d'>\n" +
        "    <xs:sequence>\n" +
        "      <xs:element name='e'>\n" +
        "        <xs:complexType name='a'>\n" +
        "          <xs:annotation>\n" +
        "            <xs:appinfo>\n" +
        "              <jaxb:class name='b'/>\n" +
        "            </xs:appinfo>\n" +
        "          </xs:annotation>\n" +
        "          <xs:sequence>\n" +
        "            <xs:element name='x' type='xs:int'/>\n" +
        "          </xs:sequence>\n" +
        "          <xs:attribute name='y' type='xs:string'/>\n" +
        "        </xs:complexType>\n" +
        "      </xs:element>\n" +
        "      <xs:element name='f' type='xs:string'>\n" +
        "        <xs:annotation>\n" +
        "          <xs:appinfo>\n" +
        "            <jaxb:class name='x'/>\n" +
        "          </xs:appinfo>\n" +
        "        </xs:annotation>\n" +
        "      </xs:element>\n" +
        "      <xs:element name='g' type='xs:string'/>\n" +
        "    </xs:sequence>\n" +
        "  </xs:complexType>\n" +
        "</xs:schema>";
  
      JAXBParser parser = newJAXBParser();
      InputSource isource = new InputSource(new StringReader(schemaSource));
      isource.setSystemId("testJAXBClass2.xsd");
      JAXBSchema schema = (JAXBSchema) parser.parse(isource);
      JAXBType d = (JAXBType) schema.getType(new XsQName((String) null, "d"));
      assertNotNull(d);
      XSGroup dGroup = assertGroup(assertComplexContent(assertComplexType(d)));
      assertSequence(dGroup);
      XSParticle[] particles = dGroup.getParticles();
      assertEquals(3, particles.length);
      JAXBElement e = (JAXBElement) assertElement(particles[0]);
      JAXBType a = (JAXBType) e.getType();
      JAXBClass jaxbClass = a.getJAXBClass();
      assertNotNull(jaxbClass);
      assertEquals("b", jaxbClass.getName());
      assertNull(jaxbClass.getImplClass());

      JAXBElement f = (JAXBElement) assertElement(particles[1]);
      assertEquals(new XsQName((String) null, "f"), f.getName());
      assertNotNull(f);
      jaxbClass = f.getJAXBClass();
      assertNotNull(jaxbClass);
      assertEquals("x", jaxbClass.getName());
      assertNull(jaxbClass.getImplClass());

      JAXBElement g = (JAXBElement) assertElement(particles[2]);
      assertEquals(new XsQName((String) null, "g"), g.getName());
      assertNotNull(g);
      assertNull(g.getJAXBClass());
    }

    {
      final String schemaSource =
        "<?xml version='1.0'?>\n" +
        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
        "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
        "  <xs:element name='x'>\n" +
        "    <xs:annotation><xs:appinfo>\n" +
        "      <jaxb:class name='b' implClass='com.b.c'/>\n" +
        "    </xs:appinfo></xs:annotation>\n" +
        "    <xs:complexType>\n" +
        "      <xs:sequence>\n" +
        "        <xs:element name='e'>\n" +
        "          <xs:complexType>\n" +
        "            <xs:sequence>\n" +
        "              <xs:element name='x' type='xs:int'/>\n" +
        "            </xs:sequence>\n" +
        "            <xs:attribute name='y' type='xs:string'/>\n" +
        "          </xs:complexType>\n" +
        "        </xs:element>\n" +
        "      </xs:sequence>\n" +
        "    </xs:complexType>\n" +
        "  </xs:element>\n" +
        "</xs:schema>";
  
      JAXBParser parser = newJAXBParser();
      InputSource isource = new InputSource(new StringReader(schemaSource));
      isource.setSystemId("testJAXBClass3.xsd");
      boolean haveException = false;
      try {
        parser.parse(isource);
      } catch (SAXException e) {
        haveException = true;
        assertTrue(e.getMessage().indexOf("[JAXB 6.7.3.4]") >= 0);
      }
      assertTrue(haveException);
    }
  }

  public void testJAXBProperty1() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
      "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "  <xs:complexType name='ct'>\n" +
      "    <xs:sequence>\n" +
      "    </xs:sequence>\n" +
      "    <xs:attribute name='a' type='xs:dateTime'>\n" +
      "      <xs:annotation><xs:appinfo>\n" +
      "        <jaxb:property name='ap' collectionType='indexed'\n" +
      "            fixedAttributeAsConstantProperty='true'\n" +
      "            generateIsSetMethod='false'\n" +
      "            enableFailFastCheck='1'>\n" +
      "          <jaxb:baseType>\n" +
      "            <jaxb:javaType name='java.math.BigDecimal'\n" +
      "                parseMethod='javax.xml.bind.DatatypeConverter.parseInteger'\n" +
      "                printMethod='javax.xml.bind.DatatypeConverter.printInteger'/>\n" +
      "          </jaxb:baseType>\n" +
      "        </jaxb:property>\n" +
      "      </xs:appinfo></xs:annotation>\n" +
      "    </xs:attribute>" +
      "  </xs:complexType>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBProperty1.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    XSType type = schema.getType(new XsQName((String) null, "ct"));
    XSComplexType complexType = assertComplexType(type);
    XSAttributable[] attributes = complexType.getAttributes();
    assertNotNull(attributes);
    assertEquals(1, attributes.length);
    JAXBAttribute a1 = (JAXBAttribute) attributes[0];
    assertEquals(XSDateTime.getInstance(), a1.getType());
    JAXBProperty ap1 = a1.getJAXBProperty();
    assertNotNull(ap1);
    assertEquals("ap", ap1.getName());
    assertEquals("indexed", ap1.getCollectionType());
    Boolean b = ap1.isFixedAttributeAsConstantProperty();
    assertTrue(b != null  &&  b.booleanValue());
    b = ap1.isGenerateIsSetMethod();
    assertTrue(b != null  &&  !b.booleanValue());
    b = ap1.isEnableFailFastCheck();
    assertTrue(b != null  &&  b.booleanValue());
    JAXBProperty.BaseType apbt1 = ap1.getBaseType();
    assertNotNull(apbt1);
    JAXBJavaType apjt1 = apbt1.getJavaType();
    assertNotNull(apjt1);
    assertEquals("java.math.BigDecimal", apjt1.getName());
    assertEquals("javax.xml.bind.DatatypeConverter.parseInteger", apjt1.getParseMethod());
    assertEquals("javax.xml.bind.DatatypeConverter.printInteger", apjt1.getPrintMethod());
  }

  public void testJAXBProperty2() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
      "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "  <xs:attribute name='a' type='xs:dateTime'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:property name='ap' collectionType='indexed'\n" +
      "          fixedAttributeAsConstantProperty='true'\n" +
      "          generateIsSetMethod='false'\n" +
      "          enableFailFastCheck='1'>\n" +
      "        <jaxb:baseType>\n" +
      "          <jaxb:javaType name='java.math.BigDecimal'\n" +
      "              parseMethod='javax.xml.bind.DatatypeConverter.parseInteger'\n" +
      "              printMethod='javax.xml.bind.DatatypeConverter.printInteger'/>\n" +
      "        </jaxb:baseType>\n" +
      "      </jaxb:property>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "  </xs:attribute>" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBProperty2.xsd");
    boolean haveException = false;
    try {
      parser.parse(isource);
    } catch (SAXException e) {
      haveException = true;
      assertTrue(e.getMessage().indexOf("[JAXB 6.8.1.2.1]") >= 0);
    }
    assertTrue(haveException);
  }

  public void testJAXBProperty3() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
      "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "  <xs:attribute name='a' type='xs:dateTime'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:property name='ap' collectionType='indexed'\n" +
      "          fixedAttributeAsConstantProperty='true'\n" +
      "          generateIsSetMethod='false'\n" +
      "          enableFailFastCheck='1'>\n" +
      "      </jaxb:property>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "  </xs:attribute>" +
      "  <xs:complexType name='ct'>\n" +
      "    <xs:sequence>\n" +
      "    </xs:sequence>\n" +
      "    <xs:attribute ref='a'/>\n" +
      "  </xs:complexType>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBProperty1.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    XSAttributable[] outerAttr = schema.getAttributes();
    assertEquals(1, outerAttr.length);
    JAXBAttribute outerA = (JAXBAttribute) outerAttr[0];
    
    XSType ct = schema.getType(new XsQName((String) null, "ct"));
    XSAttributable[] attributes = assertComplexType(ct).getAttributes();
    assertNotNull(attributes);
    assertEquals(1, attributes.length);
    JAXBAttribute a1 = (JAXBAttribute) attributes[0];
    assertEquals(XSDateTime.getInstance(), a1.getType());
    JAXBProperty ap1 = a1.getJAXBProperty();
    assertEquals(ap1, outerA.getJAXBProperty());
    assertNotNull(ap1);
    assertEquals("ap", ap1.getName());
    assertEquals("indexed", ap1.getCollectionType());
    Boolean b = ap1.isFixedAttributeAsConstantProperty();
    assertTrue(b != null  &&  b.booleanValue());
    b = ap1.isGenerateIsSetMethod();
    assertTrue(b != null  &&  !b.booleanValue());
    b = ap1.isEnableFailFastCheck();
    assertTrue(b != null  &&  b.booleanValue());
    assertNull(ap1.getBaseType());
  }

  public void testJAXBJavaType1() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
      "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "  <xs:simpleType name='a'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:javaType name='java.math.BigDecimal'\n" +
      "          hasNsContext='0'\n" +
      "          parseMethod='javax.xml.bind.DatatypeConverter.parseInteger'\n" +
      "          printMethod='javax.xml.bind.DatatypeConverter.printInteger'/>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:string'/>\n" +
      "  </xs:simpleType>\n" +
      "  <xs:element name='b'>\n" +
      "    <xs:simpleType>\n" +
      "      <xs:restriction base='a'/>\n" +
      "    </xs:simpleType>\n" +
      "  </xs:element>\n" +
      "  <xs:element name='c'>\n" +
      "    <xs:simpleType>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:javaType name='java.math.BigInteger'\n" +
      "          hasNsContext='true'\n" +
      "          parseMethod='javax.xml.bind.DatatypeConverter.parseInt'\n" +
      "          printMethod='javax.xml.bind.DatatypeConverter.printInt'/>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "      <xs:restriction base='a'/>\n" +
      "    </xs:simpleType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBJavaType1.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    XSType a = schema.getType(new XsQName((String) null, "a"));
    JAXBSimpleType ast = (JAXBSimpleType) assertSimpleType(a);
    JAXBJavaType ajjt = ast.getJAXBJavaType();
    assertEquals("java.math.BigDecimal", ajjt.getName());
    assertEquals("javax.xml.bind.DatatypeConverter.parseInteger", ajjt.getParseMethod());
    assertEquals("javax.xml.bind.DatatypeConverter.printInteger", ajjt.getPrintMethod());
    assertTrue(!ajjt.hasNsContext());

    XSElement b = schema.getElement(new XsQName((String) null, "b"));
    XSType bt = b.getType();
    JAXBSimpleType bst = (JAXBSimpleType) assertSimpleType(bt);
    assertEquals(a, assertRestriction(bst));
    assertNull(bst.getJAXBJavaType());

    XSElement c = schema.getElement(new XsQName((String) null, "c"));
    XSType ct = c.getType();
    JAXBSimpleType cst = (JAXBSimpleType) assertSimpleType(ct);
    assertEquals(a, assertRestriction(cst));
    JAXBJavaType cjjt = cst.getJAXBJavaType();
    assertEquals("java.math.BigInteger", cjjt.getName());
    assertEquals("javax.xml.bind.DatatypeConverter.parseInt", cjjt.getParseMethod());
    assertEquals("javax.xml.bind.DatatypeConverter.printInt", cjjt.getPrintMethod());
    assertTrue(cjjt.hasNsContext());
  }

  private void checkJAXBTypesafeEnumClass1AType(XSType pType) throws SAXException {
    JAXBSimpleType ast = (JAXBSimpleType) assertSimpleType(pType);
    JAXBTypesafeEnumClass aec = ast.getJAXBTypesafeEnumClass();
    assertEquals("USStateAbbr", aec.getName());
    assertTrue(!aec.getTypesafeEnumMember().hasNext());
    XSEnumeration[] enumerations = ast.getEnumerations();
    assertEquals(2, enumerations.length);
    JAXBEnumeration e1 = (JAXBEnumeration) enumerations[0];
    assertEquals("AK", e1.getValue());
    JAXBTypesafeEnumMember m1 = e1.getJAXBTypesafeEnumMember();
    assertEquals("STATE_AK", m1.getName());
    JAXBEnumeration e2 = (JAXBEnumeration) enumerations[1];
    JAXBTypesafeEnumMember m2 = e2.getJAXBTypesafeEnumMember();
    assertEquals("STATE_AL", m2.getName());
    assertEquals("AL", e2.getValue());
  }

  private void checkJAXBTypesafeEnumClass1BType(XSType pType) throws SAXException {
    JAXBSimpleType bst = (JAXBSimpleType) assertSimpleType(pType);
    JAXBTypesafeEnumClass bec = bst.getJAXBTypesafeEnumClass();
    assertEquals("USStateAbbr2", bec.getName());
    Iterator iter = bec.getTypesafeEnumMember();
    assertTrue(iter.hasNext());
    JAXBTypesafeEnumMember iem = (JAXBTypesafeEnumMember) iter.next();
    assertEquals("STATE2_AK", iem.getName());
    assertEquals("AK", iem.getValue());
    assertTrue(iter.hasNext());
    iem = (JAXBTypesafeEnumMember) iter.next();
    assertEquals("STATE2_AL", iem.getName());
    assertEquals("AL", iem.getValue());
    assertTrue(!iter.hasNext());
    XSEnumeration[] enumerations = bst.getEnumerations();
    assertEquals(2, enumerations.length);
    JAXBEnumeration be1 = (JAXBEnumeration) enumerations[0];
    assertEquals("AK", be1.getValue());
    assertNull(be1.getJAXBTypesafeEnumMember());
    JAXBEnumeration be2 = (JAXBEnumeration) enumerations[1];
    assertEquals("AL", be2.getValue());
    assertNull(be2.getJAXBTypesafeEnumMember());
  }

  public void testJAXBTypesafeEnumClass1() throws Exception {
    final String schemaSource =
      "<?xml version='1.0'?>\n" +
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'" +
      "           xmlns:jaxb='" + JAXBParser.JAXB_SCHEMA_URI + "'>\n" +
      "  <xs:simpleType name='a'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='USStateAbbr'/>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:NCName'>\n" +
      "      <xs:enumeration value='AK'>\n" +
      "        <xs:annotation><xs:appinfo>\n" +
      "          <jaxb:typesafeEnumMember name='STATE_AK'/>\n" +
      "        </xs:appinfo></xs:annotation>\n" +
      "      </xs:enumeration>\n" +
      "      <xs:enumeration value='AL'>\n" +
      "        <xs:annotation><xs:appinfo>\n" +
      "          <jaxb:typesafeEnumMember name='STATE_AL'/>\n" +
      "        </xs:appinfo></xs:annotation>\n" +
      "      </xs:enumeration>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "  <xs:simpleType name='b'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='USStateAbbr2'>\n" +
      "        <jaxb:typesafeEnumMember name='STATE2_AK' value='AK'/>\n" +
      "        <jaxb:typesafeEnumMember name='STATE2_AL' value='AL'/>\n" +
      "      </jaxb:typesafeEnumClass>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:NCName'>\n" +
      "      <xs:enumeration value='AK'/>\n" +
      "      <xs:enumeration value='AL'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <xs:element name='allTypes'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:sequence>\n" +
      "        <xs:element name='aElement' type='a'/>\n" +
      "        <xs:element name='bElement' type='b'/>\n" +
      "      </xs:sequence>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBJavaTypesafeEnumClass1.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);
    XSType a = schema.getType(new XsQName((String) null, "a"));
    checkJAXBTypesafeEnumClass1AType(a);

    XSType b = schema.getType(new XsQName((String) null, "b"));
    checkJAXBTypesafeEnumClass1BType(b);

    XSElement[] elements = schema.getElements();
    assertEquals(1, elements.length);
    XSElement allTypesElement = elements[0];
    XSGroup allTypesGroup = assertGroup(assertComplexContent(assertComplexType(allTypesElement.getType())));
    assertSequence(allTypesGroup);
    XSParticle[] allTypesChilds = allTypesGroup.getParticles();
    assertEquals(2, allTypesChilds.length);
    checkJAXBTypesafeEnumClass1AType(assertElement(allTypesChilds[0]).getType());
    checkJAXBTypesafeEnumClass1BType(assertElement(allTypesChilds[1]).getType());    
  }

  public void testJAXBTypesafeEnumClass2() throws Exception {
    final String schemaSource =
      "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xml:lang='EN'\n" +
      "    targetNamespace='http://ws.apache.org/jaxme/examples/misc/enumeration'\n" +
      "    xmlns:ex='http://ws.apache.org/jaxme/examples/misc/enumeration'\n" +
      "    xmlns:jaxb='http://java.sun.com/xml/ns/jaxb'\n" +
      "    elementFormDefault='qualified'\n" +
      "    attributeFormDefault='unqualified'>\n" +
      "  <xs:annotation>\n" +
      "    <xs:documentation>\n" +
      "      Demonstration of a complex type with all sorts of enumeration values\n" +
      "    </xs:documentation>\n" +
      "    <xs:appinfo>\n" +
      "      <jaxb:globalBindings typesafeEnumBase='xs:string xs:int xs:long xs:short xs:double xs:float'/>\n" +
      "    </xs:appinfo>\n" +
      "  </xs:annotation>\n" +
      "\n" +
      "  <!-- In this example the typesafeEnumMember instances are part of the\n" +
      "       typesafeEnumClass. -->\n" +
      "  <xs:simpleType name='StringType'>\n" +
      "    <xs:annotation>\n" +
      "      <xs:appinfo>\n" +
      "        <jaxb:typesafeEnumClass name='MyStringTypeClass'>\n" +
      "          <jaxb:typesafeEnumMember name='FOO' value='FOO'/>\n" +
      "          <jaxb:typesafeEnumMember name='BAR' value='BAR'/>\n" +
      "        </jaxb:typesafeEnumClass>\n" +
      "      </xs:appinfo>\n" +
      "    </xs:annotation>\n" +
      "    <xs:restriction base='xs:string'>\n" +
      "      <xs:enumeration value='FOO'/>\n" +
      "      <xs:enumeration value='BAR'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <!-- Here's another case: The typesafeEnumMember instances are part\n" +
      "       of their respective values. Choose your own style. -->\n" +
      "  <xs:simpleType name='IntType'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='MyIntTypeClass'/>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:int'>\n" +
      "      <xs:enumeration value='3'>\n" +
      "        <xs:annotation><xs:appinfo>\n" +
      "          <jaxb:typesafeEnumMember name='INT3'/>\n" +
      "        </xs:appinfo></xs:annotation>\n" +
      "      </xs:enumeration>\n" +
      "      <xs:enumeration value='0'>\n" +
      "        <xs:annotation><xs:appinfo>\n" +
      "          <jaxb:typesafeEnumMember name='INT0'/>\n" +
      "        </xs:appinfo></xs:annotation>\n" +
      "      </xs:enumeration>\n" +
      "      <xs:enumeration value='-7'>\n" +
      "        <xs:annotation><xs:appinfo>\n" +
      "          <jaxb:typesafeEnumMember name='INT_7'/>\n" +
      "        </xs:appinfo></xs:annotation>\n" +
      "      </xs:enumeration>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <!-- I personally prefer the former style, for shortness. So we\n" +
      "       revert to this style. -->\n" +
      "  <xs:simpleType name='LongType'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='MyLongTypeClass'>\n" +
      "        <jaxb:typesafeEnumMember name='LONG_POSITIVE' value='987298379879887'/>\n" +
      "        <jaxb:typesafeEnumMember name='LONG_ZERO' value='0'/>\n" +
      "        <jaxb:typesafeEnumMember name='LONG_NEGATIVE' value='-23987982739273989'/>\n" +
      "      </jaxb:typesafeEnumClass>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:long'>\n" +
      "      <xs:enumeration value='987298379879887'/>\n" +
      "      <xs:enumeration value='0'/>\n" +
      "      <xs:enumeration value='-23987982739273989'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <xs:simpleType name='ShortType'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='MyShortTypeClass'>\n" +
      "        <jaxb:typesafeEnumMember name='SHORT_POSITIVE' value='3468'/>\n" +
      "        <jaxb:typesafeEnumMember name='SHORT_ZERO' value='0'/>\n" +
      "        <jaxb:typesafeEnumMember name='SHORT_NEGATIVE' value='-23'/>\n" +
      "      </jaxb:typesafeEnumClass>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:short'>\n" +
      "      <xs:enumeration value='3468'/>\n" +
      "      <xs:enumeration value='0'/>\n" +
      "      <xs:enumeration value='-23'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <xs:simpleType name='DoubleType'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='MyDoubleTypeClass'>\n" +
      "        <jaxb:typesafeEnumMember name='DOUBLE_POSITIVE' value='3249239847982.234'/>\n" +
      "        <jaxb:typesafeEnumMember name='DOUBLE_ZERO' value='0'/>\n" +
      "        <jaxb:typesafeEnumMember name='DOUBLE_NEGATIVE' value='-324234.234'/>\n" +
      "      </jaxb:typesafeEnumClass>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:double'>\n" +
      "      <xs:enumeration value='3249239847982.234'/>\n" +
      "      <xs:enumeration value='0'/>\n" +
      "      <xs:enumeration value='-324234.234'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <xs:simpleType name='FloatType'>\n" +
      "    <xs:annotation><xs:appinfo>\n" +
      "      <jaxb:typesafeEnumClass name='MyFloatTypeClass'>\n" +
      "        <jaxb:typesafeEnumMember name='FLOAT_POSITIVE' value='47982.234'/>\n" +
      "        <jaxb:typesafeEnumMember name='FLOAT_ZERO' value='0'/>\n" +
      "        <jaxb:typesafeEnumMember name='FLOAT_NEGATIVE' value='-24234.234'/>\n" +
      "      </jaxb:typesafeEnumClass>\n" +
      "    </xs:appinfo></xs:annotation>\n" +
      "    <xs:restriction base='xs:float'>\n" +
      "      <xs:enumeration value='47982.234'/>\n" +
      "      <xs:enumeration value='0'/>\n" +
      "      <xs:enumeration value='-24234.234'/>\n" +
      "    </xs:restriction>\n" +
      "  </xs:simpleType>\n" +
      "\n" +
      "  <xs:complexType name='AllSimpleTypes'>\n" +
      "    <xs:sequence>\n" +
      "      <xs:element name='StringElem' type='ex:StringType'/>\n" +
      "      <xs:element name='IntElem' type='ex:IntType'/>\n" +
      "      <xs:element name='LongElem' type='ex:LongType'/>\n" +
      "      <xs:element name='ShortElem' type='ex:ShortType'/>\n" +
      "      <xs:element name='DoubleElem' type='ex:DoubleType'/>\n" +
      "      <xs:element name='FloatElem' type='ex:FloatType'/>\n" +
      "      <xs:element name='DateTimeElem' minOccurs='0'>\n" +
      "        <xs:simpleType>\n" +
      "          <xs:restriction base='xs:dateTime'>\n" +
      "            <xs:enumeration value='2002-12-17T12:23:11'/>\n" +
      "            <xs:enumeration value='2002-12-16T12:00:11'/>\n" +
      "          </xs:restriction>\n" +
      "        </xs:simpleType>\n" +
      "      </xs:element>\n" +
      "      <xs:element name='DateElem' minOccurs='0'>\n" +
      "        <xs:simpleType>\n" +
      "          <xs:restriction base='xs:date'>\n" +
      "            <xs:enumeration value='2002-12-17'/>\n" +
      "            <xs:enumeration value='2002-12-16'/>\n" +
      "          </xs:restriction>\n" +
      "        </xs:simpleType>\n" +
      "      </xs:element>\n" +
      "      <xs:element name='TimeElem' minOccurs='0'>\n" +
      "        <xs:simpleType>\n" +
      "          <xs:restriction base='xs:time'>\n" +
      "            <xs:enumeration value='12:23:11'/>\n" +
      "            <xs:enumeration value='12:00:11'/>\n" +
      "          </xs:restriction>\n" +
      "        </xs:simpleType>\n" +
      "      </xs:element>\n" +
      "    </xs:sequence>\n" +
      "  </xs:complexType>\n" +
      "\n" +
      "  <xs:element name='AllTypesElement'>\n" +
      "    <xs:complexType>\n" +
      "      <xs:sequence>\n" +
      "        <xs:element type='ex:AllSimpleTypes' name='AllSimpleTypesElement'/>\n" +
      "      </xs:sequence>\n" +
      "    </xs:complexType>\n" +
      "  </xs:element>\n" +
      "</xs:schema>\n";

    JAXBParser parser = newJAXBParser();
    InputSource isource = new InputSource(new StringReader(schemaSource));
    isource.setSystemId("testJAXBJavaTypesafeEnumClass2.xsd");
    JAXBSchema schema = (JAXBSchema) parser.parse(isource);

    XSType[] types = schema.getTypes();
    assertEquals(7, types.length);


    XSType stringType = types[0];
    checkJAXBTypesafeEnumClass2StringType(stringType);

    XSElement[] elements = schema.getElements();
    assertEquals(1, elements.length);

    XSElement allTypesElement = elements[0];
    XSParticle allTypesElementParticle = assertComplexContent(assertComplexType(allTypesElement.getType()));
    XSGroup group = allTypesElementParticle.getGroup();
    assertSequence(group);
    XSParticle[] allTypesElementsChilds = group.getParticles();
    assertEquals(1, allTypesElementsChilds.length);
    XSElement allSimpleTypesElement = assertElement(allTypesElementsChilds[0]);
    XSParticle allSimpleTypesElementParticle = assertComplexContent(assertComplexType(allSimpleTypesElement.getType()));
    XSGroup allSimpleTypesElementGroup = allSimpleTypesElementParticle.getGroup();
    assertSequence(allSimpleTypesElementGroup);
    XSParticle[] allSimpleTypesElementChilds = allSimpleTypesElementGroup.getParticles();
    assertEquals(9, allSimpleTypesElementChilds.length);

    XSElement stringTypeElement = assertElement(allSimpleTypesElementChilds[0]);
    checkJAXBTypesafeEnumClass2StringType(stringTypeElement.getType());
  }

  private void checkJAXBTypesafeEnumClass2StringType(XSType pType) throws SAXException {
    XSSimpleType stStringType = assertSimpleType(pType);
    assertEquals(XSString.getInstance(), assertRestriction(stStringType));
    assertTrue(stStringType instanceof JAXBSimpleType);
    JAXBSimpleType jaxbStringType = (JAXBSimpleType) stStringType;
    JAXBTypesafeEnumClass typesafeEnumClass = jaxbStringType.getJAXBTypesafeEnumClass();
    assertNotNull(typesafeEnumClass);
    assertEquals("MyStringTypeClass", typesafeEnumClass.getName());
  }
}
