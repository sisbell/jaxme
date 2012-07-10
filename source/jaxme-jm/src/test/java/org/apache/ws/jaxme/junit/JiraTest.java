package org.apache.ws.jaxme.junit;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import magoffin.matt.ieat.domain.impl.IngredientImpl;
import net.dspc.commons.activitymodel.TransmissionData;
import net.dspc.commons.activitymodel2.Body1;
import net.dspc.commons.activitymodel2.Body2;
import net.dspc.commons.activitymodel2.ObjectFactory;

import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.util.JavaNamer;
import org.apache.ws.jaxme.test.jira.jaxme58.AuthorType;
import org.apache.ws.jaxme.test.jira.jaxme58.BookType;
import org.apache.ws.jaxme.test.jira.jaxme58.Booklist;
import org.apache.ws.jaxme.test.jira.jaxme65.Jaxme65;
import org.apache.ws.jaxme.test.jira.jaxme65.Jaxme65Type;
import org.apache.ws.jaxme.test.jira72.Jaxme72;
import org.apache.ws.jaxme.test.misc.types.Jira62;
import org.apache.ws.jaxme.test.misc.types.Row;
import org.apache.ws.jaxme.test.misc.types.impl.RowImpl;
import org.apache.ws.jaxme.test.misc.xsimport.a.Outer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.softwareag.namespaces.de.s.xdws.soap_api.XdwsResponse;


/** Some excerpts from Jira bug reports.
 */
public class JiraTest extends BaseTestCase {
	/** Creates a new instance with the given name.
	 */
    public JiraTest(String pName) {
    	super(pName);
    }

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-10">JAXME-10</a>
     */
    public void testJaxMe10() throws Exception {
		final String input1 =
            "<TransmissionData mail-id=\"mail456.test-account@dspc.net\" xmlns=\"http://commons.dspc.net/activitymodel\">\n" +
            "  <sender account-id=\"test-account@dspc.net\" name=\"Joe Tester\">\n" +
            "    <e-mail>test-account@bluprints.com</e-mail>\n" +
            "    <phone>0793041414141</phone>\n" +
            "    <crypt-signature/>\n" +
            "  </sender>\n" +
            "  <recipient account-id=\"hvendelbo@bluprints.com\" name=\"Henrik Vendelbo\"/>\n" +
            "</TransmissionData>";
        unmarshalMarshalUnmarshal(TransmissionData.class, input1);
        final String input2 =
            "<TransmissionData mail-id=\"mail456.test-account@dspc.net\" xmlns=\"http://commons.dspc.net/activitymodel\">\n" +
            "  <sender account-id=\"test-account@dspc.net\" name=\"Joe Tester\">\n" +
            "    <e-mail>test-account@bluprints.com</e-mail>\n" +
            "    <phone>0793041414141</phone>\n" +
            "  </sender>\n" +
            "  <recipient account-id=\"hvendelbo@bluprints.com\" name=\"Henrik Vendelbo\"/>\n" +
            "</TransmissionData>";
        unmarshalMarshalUnmarshal(TransmissionData.class, input2);
        final String input3 =
            "<TransmissionData mail-id=\"mail456.test-account@dspc.net\" xmlns=\"http://commons.dspc.net/activitymodel\">\n" +
            "  <sender account-id=\"test-account@dspc.net\" name=\"Joe Tester\">\n" +
            "    <phone>0793041414141</phone>\n" +
            "  </sender>\n" +
            "  <recipient account-id=\"hvendelbo@bluprints.com\" name=\"Henrik Vendelbo\"/>\n" +
            "</TransmissionData>";
        unmarshalMarshalUnmarshal(TransmissionData.class, input3);
    }

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-12">JAXME-12</a>
     */
    public void testJaxMe12() throws Exception {
        final String[] inputs = new String[]{
            "<Body1 text-template=\"y\" html-template=\"x\" xmlns=\"http://commons.dspc.net/activitymodel2\"/>",
            "<Body1 text-template=\"y\" xmlns=\"http://commons.dspc.net/activitymodel2\"/>",
            "<Body1 html-template=\"x\" xmlns=\"http://commons.dspc.net/activitymodel2\"/>",
            "<Body1 xmlns=\"http://commons.dspc.net/activitymodel2\"/>",
            "<Body2 xmlns=\"http://commons.dspc.net/activitymodel2\"/>"
        };
        for (int i = 0;  i < inputs.length;  i++) {
        	unmarshalMarshalUnmarshal(Body1.class, inputs[i]);
        }
        Body1 body1 = new ObjectFactory().createBody1();
        body1.setValue("ok");
        Body2 body2 = new ObjectFactory().createBody2();
        body2.setValue("ok");
    }

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-38">JAXME-38</a>
     */
    public void testJaxMe38() throws Exception {
    	RowImpl emptyRow = new RowImpl();
        final String xml = "<ex:row xmlns:ex=\"" + getNamespaceURI(emptyRow) + "\">\n" +
        "  <ex:cell id=\"1\">a</ex:cell>\n" +
        "</ex:row>";
        unmarshalMarshalUnmarshal(Row.class, xml);
    }

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-39">JAXME-39</a>
     */
    public void testJaxMe39() throws Exception {
    	IngredientImpl ingredientImpl = new IngredientImpl();
        assertTrue(!ingredientImpl.isSetIngredientId());
        ingredientImpl.setIngredientId(new Integer(0));
        assertTrue(ingredientImpl.isSetIngredientId());
    }

    /** Test for <a href="http://issues.apache.org/jira/browse/JAXME-45">JAXME-45</a>
     */
    public void testJaxMe45() throws Exception {
    	Outer outer = new org.apache.ws.jaxme.test.misc.xsimport.a.ObjectFactory().createOuter();
        outer.setInner1("ok");
        outer.setInner2(0);
        JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.xsimport.a");
        StringWriter sw = new StringWriter();
        context.createMarshaller().marshal(outer, sw);
        assertEquals("<?xml version='1.0' encoding='UTF-8'?>\n" +
                     "<a:outer xmlns:a=\"http://ws.apache.org/jaxme/test/misc/xsimport/a\">\n" +
                     "  <b:inner1 xmlns:b=\"http://ws.apache.org/jaxme/test/misc/xsimport/b\">ok</b:inner1>\n" +
                     "  <b:inner2 xmlns:b=\"http://ws.apache.org/jaxme/test/misc/xsimport/b\">0</b:inner2>\n" +
                     "</a:outer>", sw.toString());
    }

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-51">JAXME-51</a>.
	 */
	public void testJAXME51() throws Exception {
		final String xsSchema =
			"<?xml version='1.0' encoding='utf-8'?>\n"
			+ "<xs:schema\n"
			+ "    xmlns:xs='http://www.w3.org/2001/XMLSchema'\n"
			+ "    elementFormDefault='qualified'\n"
			+ "    xmlns='http://ws.apache.org/jaxme/test/jira/jaxme51'\n"
			+ "    xmlns:jaxb='http://java.sun.com/xml/ns/jaxb'\n"
			+ "    targetNamespace='http://ws.apache.org/jaxme/test/jira/jaxme51'"
			+ "    jaxb:version='1.0'>\n"
			+ "  <xs:annotation><xs:appinfo>\n"
			+ "    <jaxb:globalBindings underscoreBinding='asCharInWord'/>"
			+ "  </xs:appinfo></xs:annotation>\n"
			+ "  <xs:element name='bug'>\n"
			+ "    <xs:complexType>\n"
			+ "      <xs:all>\n"
			+ "        <xs:element name='element1' type='xs:string'/>\n"
			+ "        <xs:element name='element_1' type='xs:string'/>\n"
			+ "      </xs:all>\n"
			+ "    </xs:complexType>\n"
			+ "  </xs:element>\n"
			+ "</xs:schema>\n";
		Generator g = new GeneratorImpl();
		SchemaReader sr = new JAXBSchemaReader();
		g.setSchemaReader(sr);
		sr.setGenerator(g);
		SchemaSG schema = g.getSchemaReader().parse(new InputSource(new StringReader(xsSchema)));
		assertEquals("Element1", JavaNamer.convert("element1", schema));
		assertEquals("Element_1", JavaNamer.convert("element_1", schema));
	}

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-62">JAXME-62</a>.
	 */
	public void testJAXME62() throws Exception {
		for (int i = 0;  i < 6;  i++) {
			runFacetTest(i, "a", i < 5);
		}
		for (int i = 0;  i < 6;  i++) {
			runFacetTest(i, "b", i > 3);
		}
		for (int i = 0;  i < 6;  i++) {
			runFacetTest(i, "c", i == 4);
		}
	}

	private void runFacetTest(int i, String pAttrName, boolean pSuccess) throws JAXBException {
		JAXBContext ctx = super.getJAXBContext(Jira62.class);
		String xml =
			"<ex:jira62"
			+ "  xmlns:ex='http://ws.apache.org/jaxme/test/misc/types'"
			+ "  " + pAttrName + "='";
		for (int j = 0;  j < i;  j++) {
			xml += (char) ('0' + j);
		}
		xml += "'/>";
		InputSource isource = new InputSource(new StringReader(xml));
		isource.setSystemId("testJAXME62-a-" + i + ".xsd");
		EventDetector ed = new EventDetector();
		Unmarshaller u = ctx.createUnmarshaller();
		u.setEventHandler(ed);
		u.unmarshal(isource);
		assertEquals(pSuccess, ed.isSuccess());
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
		Generator g = new GeneratorImpl();
		g.setProperty("jaxme.package.name", "org.apache.ws.jaxme.test.jira.jaxme63");
		SchemaReader sr = new JAXBSchemaReader();
		g.setSchemaReader(sr);
		sr.setGenerator(g);
		SchemaSG schema = g.getSchemaReader().parse(new InputSource(new StringReader(xml)));
		try {
			schema.generate();
			fail("Expected exception");
		} catch (SAXException e) {
			assertTrue(e.getMessage().indexOf("Model groups with maxOccurs > 1 are not yet supported.") != -1);
		}
	}

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-65">JAXME-65</a>.
	 */
	public void testJAXME65() throws Exception {
		final String xml1 =
			"<jaxme65 xmlns='http://ws.apache.org/jaxme/test/jira/jaxme65'>some text<problem>here it is</problem>more text</jaxme65>";
		Jaxme65 text1 = (Jaxme65) getJAXBContext(Jaxme65.class).createUnmarshaller().unmarshal(new InputSource(new StringReader(xml1)));
		List list1 = text1.getContent();
		assertEquals(3, list1.size());
		assertEquals("some text", list1.get(0));
		Object o = list1.get(1);
		assertTrue(o instanceof Jaxme65Type.Problem);
		Jaxme65Type.Problem problem = (Jaxme65Type.Problem) o;
		assertEquals("here it is", problem.getValue());
		assertEquals("more text", list1.get(2));
		final String xml2 =
			"<jaxme65 xmlns='http://ws.apache.org/jaxme/test/jira/jaxme65'>some text</jaxme65>";
		Jaxme65 text2 = (Jaxme65) getJAXBContext(Jaxme65.class).createUnmarshaller().unmarshal(new InputSource(new StringReader(xml2)));
		List list2 = text2.getContent();
		assertEquals(1, list2.size());
		assertEquals("some text", list2.get(0));
	}

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-58">JAXME-58</a>.
	 */
	public void testJAXME58() throws Exception {
		final String xml1 = getBooklist(false, false);
		final JAXBContext ctx = getJAXBContext(Booklist.class);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		final Booklist booklist = (Booklist) unmarshaller.unmarshal(new InputSource(new StringReader(xml1)));
		final List books = booklist.getBook();
		assertEquals(2, books.size());
		final BookType hi = (BookType) books.get(0);
		final BookType helloAgain = (BookType) books.get(1);
		final List authors = booklist.getAuthor();
		assertEquals(2, authors.size());
		final AuthorType bill = (AuthorType) authors.get(0);
		final AuthorType bob = (AuthorType) authors.get(1);
		assertSame(hi.getAuthor(), bill);
		assertSame(helloAgain.getAuthor(), bob);

		final String xml2 = getBooklist(true, false);
		EventDetector detector2 = new EventDetector();
		unmarshaller.setEventHandler(detector2);
		unmarshaller.unmarshal(new InputSource(new StringReader(xml2)));
		assertTrue(!detector2.isSuccess());
		assertEquals(ValidationEvents.EVENT_IDREF_UNDECLARED, detector2.getEvent().getErrorCode());

		final String xml3 = getBooklist(false, true);
		EventDetector detector3 = new EventDetector();
		unmarshaller.setEventHandler(detector3);
		unmarshaller.unmarshal(new InputSource(new StringReader(xml3)));
		assertTrue(!detector3.isSuccess());
		assertEquals(ValidationEvents.EVENT_DUPLICATE_ID, detector3.getEvent().getErrorCode());
	}

	private String getBooklist(boolean pUndeclaredId, boolean pDuplicateId) {
		String result =
			"<booklist xmlns='http://ws.apache.org/jaxme/test/jira/jaxme58'>\n"
			+ "  <book title='hi' author='a1'/>\n"
			+ "  <book title='hello again' author='a2'/>\n";
		if (pUndeclaredId) {
			result += "  <book title='hello there!' author='a3'/>\n";
		}
		result +=
			"  <author id='a1' name='Bill'/>\n"
			+ "  <author id='a2' name='Bob'/>\n";
		if (pDuplicateId) {
			result += "  <author id='a2' name='Ben'/>\n";
		}
		result += "</booklist>\n";
		return result;
	}

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-72">JAXME-72</a>.
	 */
	public void testJAXME72() throws JAXBException {
		// create test structure
		org.apache.ws.jaxme.test.jira72.ObjectFactory oFact = new org.apache.ws.jaxme.test.jira72.ObjectFactory();
		Jaxme72 jaxme72  = oFact.createJaxme72();
		QName qName = new QName("http://this.namespace/must/be/declared", "jaxme72");
		jaxme72.setTest(qName);
		// try to marshal it...Jira issue 72 address the problem that this will fail.
		JAXBContext ctx = JAXBContext.newInstance("org.apache.ws.jaxme.test.jira72");
		StringWriter sw = new StringWriter();
		ctx.createMarshaller().marshal(jaxme72, sw);
		// if we get this far issue 72 shoulb be fixed. just to be sure - unmarshal and compare QNames.
		Jaxme72 jaxme72Clone = (Jaxme72) ctx.createUnmarshaller().unmarshal(new InputSource(new StringReader(sw.toString())));
		assertEquals(jaxme72.getTest(),jaxme72Clone.getTest());
	}

	/** Test for <a href="http://issues.apache.org/jira/browse/JAXME-84">JAXME-84</a>.
	 */
	public void testJAXME84() throws Exception {
	    final String xml =
	        "<xdws:xdwsResponse version=\"1\" xmlns:xdws=\"http://namespaces.softwareag.com/de/s/xDWS/soap-api\">\n"
	        + "  <xdws:response rc=\"4\"/>\n"
	        + "  <xdws:result rc=\"4\"/>\n"
	        + "</xdws:xdwsResponse>";
	    unmarshalMarshalUnmarshal(XdwsResponse.class, xml);
	}
}
