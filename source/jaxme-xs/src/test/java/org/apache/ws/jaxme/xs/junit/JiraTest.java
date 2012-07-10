package org.apache.ws.jaxme.xs.junit;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.types.XSID;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.util.XsDateTimeFormat;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/** Collection of schemas from Jira.
 */
public class JiraTest extends ParserTestBase {
    private XSSchema parse(String pSchema, String pName)
            throws ParserConfigurationException, SAXException, IOException {
        InputSource isource = new InputSource(new StringReader(pSchema));
        isource.setSystemId(pName);
        XSParser parser = new XSParser();
        parser.setValidating(false);
        return parser.parse(isource);
    }

    /** Test for JAXME-34 in Jira.
     */
    public void testJira34() throws Exception {
    	final String schemaSpec =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n"
          + "  <xs:complexType name='mixedType' mixed='true'>\n"
          + "    <xs:attribute name='foo' type='xs:string'/>\n"
          + "  </xs:complexType>\n"
          + "</xs:schema>";
        XSSchema schema = parse(schemaSpec, "jira34.xsd");
        XSType[] types = schema.getTypes();
        assertEquals(1, types.length);
        XSComplexType ct = assertComplexType(types[0]);
        assertTrue(ct.isMixed());
    }

    /** Test for JAXME-42 in Jira.
     */
    public void testJira42() throws Exception {
    	XsDateTimeFormat f = new XsDateTimeFormat();
        Calendar cal = (Calendar) f.parseObject("2004-10-15T13:00:00Z");
        assertEquals(2004, cal.get(Calendar.YEAR));
        assertEquals(9, cal.get(Calendar.MONTH));
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        assertEquals(TimeZone.getTimeZone("GMT"), cal.getTimeZone());

        String s = f.format(cal);
        assertEquals("2004-10-15T13:00:00Z", s);
    }

    /** Test for JAXME-44 in Jira.
     */
    public void testJira44() throws Exception {
        final String schemaSpec =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\n"
            + "  <xs:simpleType name='threeOrFour'>\n"
            + "    <xs:restriction base='xs:string'>\n"
            + "      <xs:enumeration value='3'/>\n"
            + "      <xs:enumeration value='4'/>\n"
            + "    </xs:restriction>\n"
            + "  </xs:simpleType>\n"
            + "\n"
            + "  <xs:complexType name='outerType1'>\n"
            + "    <xs:simpleContent>\n"
            + "      <xs:extension base='threeOrFour'>\n"
            + "        <xs:attribute name='isOctal' type='xs:boolean'/>\n"
            + "      </xs:extension>"
            + "    </xs:simpleContent>\n"
            + "  </xs:complexType>\n"
            + "\n"
            + "  <xs:complexType name='outerType2'>\n"
            + "    <xs:simpleContent>\n"
            + "      <xs:restriction base='outerType1'>\n"
            + "        <xs:enumeration value='4'/>\n"
            + "      </xs:restriction>\n"
            + "    </xs:simpleContent>\n"
            + "  </xs:complexType>\n"
            + "</xs:schema>";

        XSSchema schema = parse(schemaSpec, "jira44.xsd");
        XSType[] types = schema.getTypes();
        assertEquals(3, types.length);
        XSType threeOrFourType = types[0];
        XSSimpleType threeOrFourSimpleType = assertSimpleType(threeOrFourType);
        XSType restrictedType = assertRestriction(threeOrFourSimpleType);
        XSEnumeration[] threeOrFourTypeEnums = threeOrFourSimpleType.getEnumerations();
        assertEquals(2, threeOrFourTypeEnums.length);
        assertEquals("3", threeOrFourTypeEnums[0].getValue());
        assertEquals("4", threeOrFourTypeEnums[1].getValue());
        assertEquals(XSString.getInstance(), restrictedType);
        XSType outerType1 = types[1];
        assertEquals(new XsQName((XsAnyURI) null, "outerType1"), outerType1.getName());
        XSComplexType outerType1complexType = assertComplexType(outerType1);
        XSSimpleContentType outerType1simpleContentType = assertSimpleContent(outerType1complexType);
        XSType outerType1contentType = outerType1simpleContentType.getType();
        assertEquals(threeOrFourType, outerType1contentType);
        XSType outerType2 = types[2];
        assertEquals(new XsQName((XsAnyURI) null, "outerType2"), outerType2.getName());
        XSComplexType outerType2complexType = assertComplexType(outerType2);
        XSSimpleContentType outerType2simpleContentType = assertSimpleContent(outerType2complexType);
        XSType outerType2contentType = outerType2simpleContentType.getType();
        assertEquals(threeOrFourType, assertRestriction(outerType2contentType.getSimpleType()));
        XSEnumeration[] outerType2Enums = outerType2contentType.getSimpleType().getEnumerations();
        assertEquals(1, outerType2Enums.length);
        assertEquals("4", outerType2Enums[0].getValue());
    }

    /** Test for JAXME-46 in Jira.
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
        XSSchema schema = parse(schemaSpec, "jira46.xsd");
        XSAttribute[] attrs = schema.getAttributes();
        assertEquals(1, attrs.length);
        XSAttribute idAttr = attrs[0];
        assertEquals(new XsQName(uri, "id"), idAttr.getName());
        assertEquals(XSID.getInstance(), idAttr.getType());
        assertTrue(idAttr.isOptional());
        XSElement[] elements = schema.getElements();
        assertEquals(1, elements.length);
        XSElement rifElem = elements[0];
        XSComplexType ct = assertComplexType(rifElem.getType());
        XSAttributable[] rifAttrs = ct.getAttributes();
        assertEquals(1, rifAttrs.length);
        XSAttribute idRef = (XSAttribute) rifAttrs[0];
        assertFalse(idRef.equals(idAttr));
        assertEquals(new XsQName(uri, "id"), idAttr.getName());
        assertEquals(XSID.getInstance(), idAttr.getType());
        assertFalse(idRef.isOptional());
    }

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-63">JAXME-63</a>.
	 */
	public void testJAXME63() throws Exception {
		final String xml =
			"<xs:schema\n"
			+ "    xmlns:xs='http://www.w3.org/2001/XMLSchema'\n"
			+ "    elementFormDefault='qualified'>\n"
			+ "  <xs:group name='params'>\n"
			+ "    <xs:choice>\n"
			+ "      <xs:element name='string' type='xs:string'/>\n"
			+ "      <xs:element name='int' type='xs:int'/>\n"
			+ "      <xs:element name='boolean' type='xs:boolean'/>\n"
			+ "    </xs:choice>\n"
			+ "  </xs:group>\n"
			+ "  <xs:element name='call'>\n"
			+ "    <xs:complexType>\n"
			+ "      <xs:group ref='params' maxOccurs='unbounded'/>\n"
			+ "    </xs:complexType>\n"
			+ "  </xs:element>\n"
			+ "</xs:schema>";
        XSSchema schema = parse(xml, "jaxme63.xsd");
        XSElement[] elements = schema.getElements();
        assertEquals(1, elements.length);
        XSElement call = elements[0];
        assertEquals(new XsQName((String) null, "call"), call.getName());
        XSComplexType type = assertComplexType(call.getType());
        XSParticle particle = assertComplexContent(type);
        assertTrue(particle.isGroup());
        assertChoice(particle.getGroup());
        assertEquals(1, particle.getMinOccurs());
        assertEquals(-1, particle.getMaxOccurs());
	}

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-84">JAXME-84</a>.
     */
    public void testJAXME84() throws Exception {
        final String xml =
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' targetNamespace='http://namespaces.softwareag.com/de/s/xDWS/soap-api'\n"
            + "    xmlns:xdws='http://namespaces.softwareag.com/de/s/xDWS/soap-api' elementFormDefault='qualified'\n"
            + "    attributeFormDefault='unqualified'>\n"
            + "  <xs:complexType name='xdwsResultInfo'>\n"
            + "    <xs:attribute name='rc' type='xs:int' use='required'/>\n"
            + "    <xs:attribute name='message' type='xs:string' use='optional'/>\n"
            + "    <xs:attribute name='details' type='xs:string' use='optional'/>\n"
            + "  </xs:complexType>\n"
            + "  <xs:element name='xdwsResponse'>\n"
            + "    <xs:complexType>\n"
            + "      <xs:sequence>\n"
            + "        <xs:element name='response' minOccurs='0' maxOccurs='unbounded'>\n"
            + "          <xs:complexType>\n"
            + "            <xs:complexContent>\n"
            + "              <xs:extension base='xdws:xdwsResultInfo'>\n"
            + "                <xs:choice minOccurs='0'>\n"
            + "                  <xs:element name='a' type='xs:string'/>\n"
            + "                  <xs:element name='b' type='xs:string'/>\n"
            + "                </xs:choice>\n"
            + "              </xs:extension>\n"
            + "            </xs:complexContent>\n"
            + "          </xs:complexType>\n"
            + "        </xs:element>\n"
            + "        <xs:element name='result' type='xdws:xdwsResultInfo'/>\n"
            + "      </xs:sequence>\n"
            + "      <xs:attribute name='version' type='xs:int' use='required' />\n"
            + "    </xs:complexType>\n"
            + "  </xs:element>\n"
            + "</xs:schema>\n";
        XSSchema schema = parse(xml, "jaxme84.xsd");
        XSElement[] elements = schema.getElements();
        assertEquals(1, elements.length);
        XSElement xdwsResponseElement = elements[0];
        XSParticle[] xdwsResponseParticles = assertGroup(assertComplexContent(assertComplexType(xdwsResponseElement.getType()))).getParticles();
        assertEquals(2, xdwsResponseParticles.length);
        XSElement responseElement = assertElement(xdwsResponseParticles[0]);
        XSComplexType responseType = assertComplexType(responseElement.getType());
        XSParticle particle = responseType.getParticle();
        assertEquals(0, particle.getMinOccurs());
        assertEquals(1, particle.getMaxOccurs());
        XSParticle[] particles = assertGroup(particle).getParticles();
        assertEquals(2, particles.length);
        XSElement a = assertElement(particles[0]);
        assertEquals(new XsQName("http://namespaces.softwareag.com/de/s/xDWS/soap-api", "a"), a.getName());
        XSElement b = assertElement(particles[1]);
        assertEquals(new XsQName("http://namespaces.softwareag.com/de/s/xDWS/soap-api", "b"), b.getName());
    }

    public void testMinExclusiveNPE() throws Exception {
        final String xml =
            "<xsd:schema\n"
            + "    xmlns:xsd='http://www.w3.org/2001/XMLSchema'\n"
            + "    xmlns:xsdi='http://www.w3.org/2001/XMLSchema-instance'\n"
            + "    xmlns:dummy='target:ns:dummy'\n"
            + "    targetNamespace='target:ns:dummy'\n"
            + "    elementFormDefault='qualified'\n"
            + "    xsdi:schemaLocation='http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd'>\n"
            + "  <xsd:simpleType name='ExclusiveInclusiveSimple'>\n"
            + "    <xsd:restriction base='xsd:decimal'>\n"
            + "      <xsd:maxInclusive value='90'/>\n"
            + "      <xsd:minExclusive value='-90'/>\n"
            + "    </xsd:restriction>\n"
            + "  </xsd:simpleType>\n"
            + "  <xsd:complexType name='ExclusiveInclusive'>\n"
            + "    <xsd:simpleContent>\n"
            + "      <xsd:extension base='dummy:ExclusiveInclusiveSimple'/>\n"
            + "    </xsd:simpleContent>\n"
            + "  </xsd:complexType>\n"
            + "</xsd:schema>";
        parse(xml, "testMinExclusiveNPE.xsd");
    }
}
