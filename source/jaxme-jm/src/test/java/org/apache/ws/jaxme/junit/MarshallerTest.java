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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.jaxme.XMLConstants;
import org.apache.ws.jaxme.XMLWriter;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.impl.JMControllerImpl;
import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.impl.REFactory;
import org.apache.ws.jaxme.test.misc.address.Address;
import org.apache.ws.jaxme.test.misc.address.AddressType;
import org.apache.ws.jaxme.test.misc.types.AllElement;
import org.apache.ws.jaxme.test.misc.types.AllSimpleTypes;
import org.apache.ws.jaxme.test.misc.types.AllTypesElement;
import org.apache.ws.jaxme.test.misc.types.Author;
import org.apache.ws.jaxme.test.misc.types.Html;
import org.apache.ws.jaxme.test.misc.types.ObjectFactory;
import org.apache.ws.jaxme.test.misc.types.Patterns;
import org.apache.ws.jaxme.test.misc.types.impl.AllElementImpl;
import org.apache.ws.jaxme.test.misc.types.impl.AllSimpleTypesImpl;
import org.apache.ws.jaxme.test.misc.types.impl.AllTypesElementImpl;
import org.apache.ws.jaxme.util.Duration;
import org.apache.ws.jaxme.xs.util.XsDateFormat;
import org.apache.ws.jaxme.xs.util.XsDateTimeFormat;
import org.apache.ws.jaxme.xs.util.XsTimeFormat;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: MarshallerTest.java 782085 2009-06-05 18:17:12Z jochen $
 */
public class MarshallerTest extends BaseTestCase {
	private JAXBContextImpl factory;

  /** <p>Creates a new instance of MarshallerTest.</p>
   */
  public MarshallerTest(String arg) { super(arg); }

  public void setUp() throws JAXBException {
    factory = (JAXBContextImpl) JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.types");
  }

  protected JAXBContextImpl getFactory() {
    return factory;
  }

  protected Calendar getDateTime() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.set(Calendar.DAY_OF_MONTH, 22);
    cal.set(Calendar.MONTH, 11-1);
    cal.set(Calendar.YEAR, 2002);
    cal.set(Calendar.HOUR_OF_DAY, 16);
    cal.set(Calendar.MINUTE, 43);
    cal.set(Calendar.SECOND, 37);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }

  protected Calendar getTime() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.set(Calendar.HOUR_OF_DAY, 16);
    cal.set(Calendar.MINUTE, 43);
    cal.set(Calendar.SECOND, 37);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.YEAR, 0);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 0);
    return cal;
  }

  protected Calendar getDate() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.set(Calendar.DAY_OF_MONTH, 22);
    cal.set(Calendar.MONTH, 11-1);
    cal.set(Calendar.YEAR, 2002);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }

  protected Duration getDuration() {
    Duration d = new Duration(false, 1, 2, 3, 4, 5, 6, 7);
    return d;
  }

  protected byte[] getHexBytes() {
    return new byte[]{1, 17, 35, 78, 115, -99, -69, -1};
  }

  protected BigDecimal newBigDecimal() {
      return new BigDecimal("6.023e23");
  }
  
  protected AllSimpleTypes getAllSimpleTypesElement() {
    AllSimpleTypes element = new AllSimpleTypesImpl();
    element.setStringElem("This is a string with german Umlauts: \u00e4\u00f6\u00fc\u00df\u00c4\u00d6\u00dc");
    element.setDoubleElem(23979782937923.2398798);
    element.setFloatElem(-34768.3486787f);
    element.setIntElem(-2139879);
    element.setLongElem(1290380128309182303l);
    element.setShortElem((short) 23878);
    element.setDateElem(getDate());
    element.setDateTimeElem(getDateTime());
    element.setTimeElem(getTime());
    element.setDurationElem(getDuration());
    element.setHexBinaryElem(getHexBytes());
    element.setBase64BinaryElem(getHexBytes());
    element.setNmTokenElem("a-name-token"); 
    List nmTokens = element.getNmTokensElem();
    nmTokens.add("a-name-token");
    nmTokens.add("another:name-token");
    element.setDecimalElem(newBigDecimal());
    element.setIntegerElem(new BigDecimal("-6023e20").toBigInteger());
    element.setNonNegativeIntegerElem(new BigInteger("101"));
    element.setPositiveIntegerElem(new BigDecimal("+6023e20").toBigInteger());
    element.setNonPositiveIntegerElem(new BigInteger("-21714"));
    element.setNegativeIntegerElem(new BigInteger("-21714"));
    element.setQNameElem(new QName("http://ws.apache.org/jaxme/test/misc/types", "ias"));
    element.setBooleanElem(true);
    return element;
  }

  protected AllTypesElement getAllTypesElement() throws JAXBException {
    AllTypesElement element = (AllTypesElement) getFactory().getManager(AllTypesElement.class).getElementJ();
    element.setAllSimpleTypesElement(getAllSimpleTypesElement());
    List list = element.getListTypeElement();
    list.add(new BigInteger("7"));
    list.add(new BigInteger("-3"));
    list.add(new BigInteger("0"));
    element.setUnionTypeElement(getDateTime());
    return element;
  }

  protected String getAllSimpleTypesElementString() {
    AllTypesElementImpl elem = new AllTypesElementImpl();
    String uri = elem.getQName().getNamespaceURI();
    return
      "<ex:AllSimpleTypesElement xmlns:ex=\"" + uri + "\">" +
      "<ex:StringElem>This is a string with german Umlauts: &#228;&#246;&#252;&#223;&#196;&#214;&#220;</ex:StringElem>" +
      "<ex:IntElem>-2139879</ex:IntElem>" +
      "<ex:LongElem>1290380128309182303</ex:LongElem>" +
      "<ex:ShortElem>23878</ex:ShortElem>" +
      "<ex:DoubleElem>2.397978293792324E13</ex:DoubleElem>" +
      "<ex:FloatElem>-34768.348</ex:FloatElem>" +
      "<ex:DateTimeElem>2002-11-22T16:43:37Z</ex:DateTimeElem>" +
      "<ex:DateElem>2002-11-22Z</ex:DateElem>" +
      "<ex:TimeElem>16:43:37Z</ex:TimeElem>" +
      "<ex:DurationElem>P1Y2M3DT4H5M6.7S</ex:DurationElem>" +
      "<ex:HexBinaryElem>0111234E739DBBFF</ex:HexBinaryElem>" +
      "<ex:Base64BinaryElem>AREjTnOdu/8=</ex:Base64BinaryElem>" +
      "<ex:NmTokenElem>a-name-token</ex:NmTokenElem>" +
      "<ex:NmTokensElem>a-name-token another:name-token</ex:NmTokensElem>" +
      "<ex:DecimalElem>" + new DatatypeConverterImpl().printDecimal(newBigDecimal()) + "</ex:DecimalElem>" +
      "<ex:IntegerElem>-602300000000000000000000</ex:IntegerElem>" +
      "<ex:NonNegativeIntegerElem>101</ex:NonNegativeIntegerElem>" +
      "<ex:PositiveIntegerElem>602300000000000000000000</ex:PositiveIntegerElem>" +
      "<ex:NonPositiveIntegerElem>-21714</ex:NonPositiveIntegerElem>" +
      "<ex:NegativeIntegerElem>-21714</ex:NegativeIntegerElem>" +
      "<ex:QNameElem>ex:ias</ex:QNameElem>" +
      "<ex:BooleanElem>true</ex:BooleanElem>" +
      "</ex:AllSimpleTypesElement>";
  }

  private String getAllTypesElementString(boolean pSchemaDeclaration, boolean pFormatted) {
    AllTypesElementImpl elem = new AllTypesElementImpl();
    String uri = elem.getQName().getNamespaceURI();
    String xsi = pSchemaDeclaration ? " xmlns:xsi=\""
    	+ XMLConstants.XML_SCHEMA_URI + "\" xsi:"
        + XMLConstants.XML_SCHEMA_NS_ATTR + "=\"foo.xsd\"" : "";
    String lf = pFormatted ? "\n" : "";
    String sep = pFormatted ? "  " : "";
    String sep2 = sep + sep;
    return
      "<ex:AllTypesElement xmlns:ex=\"" + uri + "\"" + xsi + ">" + lf +
      sep + "<ex:AllSimpleTypesElement>" + lf +
      sep2 + "<ex:StringElem>This is a string with german Umlauts: &#228;&#246;&#252;&#223;&#196;&#214;&#220;</ex:StringElem>" + lf +
	  sep2 + "<ex:IntElem>-2139879</ex:IntElem>" + lf +
      sep2 + "<ex:LongElem>1290380128309182303</ex:LongElem>" + lf +
      sep2 + "<ex:ShortElem>23878</ex:ShortElem>" + lf +
      sep2 + "<ex:DoubleElem>2.397978293792324E13</ex:DoubleElem>" + lf +
      sep2 + "<ex:FloatElem>-34768.348</ex:FloatElem>" + lf +
      sep2 + "<ex:DateTimeElem>2002-11-22T16:43:37Z</ex:DateTimeElem>" + lf +
      sep2 + "<ex:DateElem>2002-11-22Z</ex:DateElem>" + lf +
      sep2 + "<ex:TimeElem>16:43:37Z</ex:TimeElem>" + lf +
      sep2 + "<ex:DurationElem>P1Y2M3DT4H5M6.7S</ex:DurationElem>" + lf +
      sep2 + "<ex:HexBinaryElem>0111234E739DBBFF</ex:HexBinaryElem>" + lf +
      sep2 + "<ex:Base64BinaryElem>AREjTnOdu/8=</ex:Base64BinaryElem>" + lf +
      sep2 + "<ex:NmTokenElem>a-name-token</ex:NmTokenElem>" + lf +
      sep2 + "<ex:NmTokensElem>a-name-token another:name-token</ex:NmTokensElem>" + lf +
      sep2 + "<ex:DecimalElem>" + new DatatypeConverterImpl().printDecimal(newBigDecimal()) + "</ex:DecimalElem>" + lf +
      sep2 + "<ex:IntegerElem>-602300000000000000000000</ex:IntegerElem>" + lf +
      sep2 + "<ex:NonNegativeIntegerElem>101</ex:NonNegativeIntegerElem>" + lf +
      sep2 + "<ex:PositiveIntegerElem>602300000000000000000000</ex:PositiveIntegerElem>" + lf +
      sep2 + "<ex:NonPositiveIntegerElem>-21714</ex:NonPositiveIntegerElem>" + lf +
      sep2 + "<ex:NegativeIntegerElem>-21714</ex:NegativeIntegerElem>" + lf +
      sep2 + "<ex:QNameElem>ex:ias</ex:QNameElem>" + lf +
      sep2 + "<ex:BooleanElem>true</ex:BooleanElem>" + lf +
      sep + "</ex:AllSimpleTypesElement>" + lf +
      sep + "<ex:ListTypeElement>7 -3 0</ex:ListTypeElement>" + lf +
      sep + "<ex:UnionTypeElement>2002-11-22T16:43:37Z</ex:UnionTypeElement>" + lf +
      "</ex:AllTypesElement>";
  }

  protected XMLReader getXMLReader() throws ParserConfigurationException, SAXException {
    // Verify whether we can create a SAX Parser; it's better to detect this here
    // than within JAXB
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    spf.setValidating(false);
    SAXParser sp = spf.newSAXParser();
    return sp.getXMLReader();
  }

  protected void verifyAllSimpleTypesElement(AllSimpleTypes pElement) {
    assertEquals("This is a string with german Umlauts: \u00e4\u00f6\u00fc\u00df\u00c4\u00d6\u00dc",
                       pElement.getStringElem());
    assertEquals(new Double("2.397978293792324E13").doubleValue(),
                 pElement.getDoubleElem(), 0);
    assertEquals(new Float(-34768.348).floatValue(), pElement.getFloatElem(), 0);
    assertEquals(new Integer(-2139879).intValue(), pElement.getIntElem());
    assertEquals(new Long(1290380128309182303L).longValue(), pElement.getLongElem());
    assertEquals(new Short((short) 23878).shortValue(), pElement.getShortElem());
    assertEquals(getDateTime(), pElement.getDateTimeElem());
    assertEquals(getDate(), pElement.getDateElem());
    assertEquals(getTime(), pElement.getTimeElem());
    assertEquals(getDuration(), pElement.getDurationElem());
    assertEquals(getHexBytes(), pElement.getHexBinaryElem());
    assertEquals(getHexBytes(), pElement.getBase64BinaryElem());
    assertEquals(new BigInteger("101"), pElement.getNonNegativeIntegerElem());
    assertTrue(pElement.isBooleanElem());
  }

  protected void verifyAllTypesElement(AllTypesElement pElement) {
    verifyAllSimpleTypesElement(pElement.getAllSimpleTypesElement());
    List list = pElement.getListTypeElement();
    assertEquals(3, list.size());
    assertEquals(new BigInteger("7"), list.get(0));
    assertEquals(new BigInteger("-3"), list.get(1));
    assertEquals(new BigInteger("0"), list.get(2));
    Object object = pElement.getUnionTypeElement();
    String dateTime1 = DateFormat.getDateTimeInstance().format(getDateTime().getTime());
    String dateTime2 = DateFormat.getDateTimeInstance().format(((Calendar) object).getTime());
    assertEquals(dateTime1, dateTime2);
  }

    /** <p>Tests unmarshalling of a simple element.</p>
     */
  	public void testUnmarshalSimpleElements() throws Exception {
		String s = getAllTypesElementString(false, false);
	    StringReader sr = new StringReader(s);
		AllTypesElement e = (AllTypesElement) getFactory().createUnmarshaller().unmarshal(new InputSource(sr));
		verifyAllSimpleTypesElement(e.getAllSimpleTypesElement());
	}

  /** <p>Tests, whether complex elements can be marshalled.</p>
   */
  public void testUnmarshalComplexElements() throws Exception {
    testUnmarshalComplexElements(false);
    testUnmarshalComplexElements(true);
  }

  private void testUnmarshalComplexElements(boolean pSchemaLocation)
      throws Exception {
    JAXBContext myFactory = getFactory();
    Unmarshaller unmarshaller = myFactory.createUnmarshaller();
    String s = getAllTypesElementString(pSchemaLocation, true);
    StringReader sr = new StringReader(s);
    AllTypesElement result = (AllTypesElement) unmarshaller.unmarshal(new InputSource(sr));
    verifyAllTypesElement(result);
    result = (AllTypesElement) myFactory.createUnmarshaller().unmarshal(new StreamSource(new StringReader(s)));
    verifyAllTypesElement(result);
  }

  /** <p>Tests marshalling of a simple element.</p>
   */
  public void testMarshalSimpleElements() throws Exception {
	  JMMarshallerImpl m = (JMMarshallerImpl) getFactory().createMarshaller();
	  StringWriter sw = new StringWriter();
	  AllTypesElement element = getAllTypesElement();
	  m.setXmlDeclaration(false);
	  m.setEncoding("US-ASCII");
	  m.marshal(element, sw);
	  String expected = getAllTypesElementString(false, true);
	  assertEquals(expected, sw.toString());
  }

  /** <p>Tests marshalling of a complex element.</p>
   */
  public void testMarshalComplexElements() throws Exception {
    JAXBContext myFactory = getFactory();
    JMMarshallerImpl marshaller = (JMMarshallerImpl) myFactory.createMarshaller();
    marshaller.setXmlDeclaration(false);
    StringWriter sw = new StringWriter();
    Class c = marshaller.getXMLWriterClass();
    XMLWriter w = (XMLWriter) c.newInstance();
    w.init(marshaller);
    String rawInput = getAllTypesElementString(false, true);
    StringBuffer input = new StringBuffer();
    for (int i = 0;  i < rawInput.length();  i++) {
      char ch = rawInput.charAt(i);
      boolean done = false;
      if (ch == '&') {
        if (i+1 < rawInput.length()  &&  rawInput.charAt(i+1) == '#') {
          int j = 2;
          StringBuffer digits = new StringBuffer();
          while (i+j < rawInput.length()  &&  Character.isDigit(rawInput.charAt(i+j))) {
            digits.append(rawInput.charAt(i+j));
            ++j;
          }
          if (digits.length() > 0  &&
              i+j < rawInput.length()  &&  rawInput.charAt(i+j) == ';') {
            char chr = (char) Integer.parseInt(digits.toString());
            if (w.canEncode(chr)) {
              done = true;
              i += j;
              input.append(chr);
            }
          }
        }
      }
      if (!done) {
        input.append(ch);
      }
    }
    marshaller.marshal(getAllTypesElement(), sw);
    assertEquals(input.toString(), sw.toString());
  }

  protected String getMarshalledAuthor() throws JAXBException {
      Author author = new ObjectFactory().createAuthor();
      author.setRating(2);
      author.setValue("This is a test.");
      StringWriter sw = new StringWriter();
      Marshaller m = getFactory().createMarshaller();
      m.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
      m.marshal(author, sw);
      return sw.toString();
  }

  /** <p>Tests, whether elements with simple content can be marshalled.</p>
   */
  public void testMarshalSimpleContent() throws Exception {
      String expect = "<ex:Author rating=\"2\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/types\">This is a test.</ex:Author>";
      String got = getMarshalledAuthor();
      assertEquals(expect, got);
  }

  private void verifyUnmarshalledAuthor(Author pAuthor) {
    assertEquals(2, pAuthor.getRating());
    assertEquals("This is a test.", pAuthor.getValue());
  }

  /** <p>Tests, whether elements with simple content can be unmarshalled.</p>
   */
  public void testUnmarshalSimpleContent() throws Exception {
  	  String s = getMarshalledAuthor();
      Author author = (Author) getFactory().createUnmarshaller().unmarshal(new InputSource(new StringReader(s)));
      verifyUnmarshalledAuthor(author);
      author = (Author) getFactory().createUnmarshaller().unmarshal(new StreamSource(new StringReader(s)));
      verifyUnmarshalledAuthor(author);
  }

  /**
   * Tests that toString returns an appropriate xsd:duration value
   */
  public void testExplicitToString() {
      String dur1 = "P0Y0M0DT0H2M60S";
      Duration duration1 = Duration.valueOf(dur1);
      String actualReturn = duration1.toString();
      assertEquals("return value not as expected", dur1, actualReturn);
  }

  /**
   * Tests that toString returns an appropriate xsd:duration value
   */
  public void testImplicitToString() {
      String dur2 = "PT2M60S";
      Duration duration2 = Duration.valueOf(dur2);
      String actualReturn = duration2.toString();
      String expect = "P0Y0M0DT0H2M60S";
      assertEquals("return value not as expected ", expect, actualReturn);
  }
  
  /**
   * Test that getMillis returns the total time of duration in
   milliseconds
   */
  public void testMillis() {
      String dur2 = "PT2M60S";
      Duration duration2 = Duration.valueOf(dur2);
      assertEquals(2, duration2.getMinutes());
      assertEquals(60, duration2.getSeconds());
  }

  protected String getAllElementString(boolean pRandom, Format pDateTimeFormat, Format pDateFormat, Format pTimeFormat) {
    AllElementImpl elem = new AllElementImpl();
    String uri = elem.getQName().getNamespaceURI();
    String[] elements = new String[]{
      "<ex:StringElem>This is a string with german Umlauts: &#228;&#246;&#252;&#223;&#196;&#214;&#220;</ex:StringElem>",
      "<ex:IntElem>-2139879</ex:IntElem>",
      "<ex:LongElem>1290380128309182303</ex:LongElem>",
      "<ex:ShortElem>23878</ex:ShortElem>",
      "<ex:DoubleElem>2.397978293792324E13</ex:DoubleElem>",
      "<ex:FloatElem>-34768.348</ex:FloatElem>",
      "<ex:DateTimeElem>" + pDateTimeFormat.format(getDateTime()) + "</ex:DateTimeElem>",
      "<ex:DateElem>" + pDateFormat.format(getDate()) + "</ex:DateElem>",
      "<ex:TimeElem>" + pTimeFormat.format(getTime()) + "</ex:TimeElem>",
      "<ex:DurationElem>P1Y2M3DT4H5M6.7S</ex:DurationElem>",
      "<ex:HexBinaryElem>0111234E739DBBFF</ex:HexBinaryElem>",
      "<ex:Base64BinaryElem>AREjTnOdu/8=</ex:Base64BinaryElem>",
      "<ex:NmTokenElem>a-name-token</ex:NmTokenElem>",
      "<ex:NmTokensElem>a-name-token another:name-token</ex:NmTokensElem>",
      "<ex:DecimalElem>" + new DatatypeConverterImpl().printDecimal(newBigDecimal()) + "</ex:DecimalElem>",
      "<ex:IntegerElem>-602300000000000000000000</ex:IntegerElem>",
      "<ex:NonNegativeIntegerElem>101</ex:NonNegativeIntegerElem>",
      "<ex:PositiveIntegerElem>602300000000000000000000</ex:PositiveIntegerElem>",
      "<ex:NonPositiveIntegerElem>-21714</ex:NonPositiveIntegerElem>",
      "<ex:NegativeIntegerElem>-21714</ex:NegativeIntegerElem>",
      "<ex:QNameElem>ex:ias</ex:QNameElem>",
      "<ex:BooleanElem>true</ex:BooleanElem>"
    };

    if (pRandom) {
    	List list = new ArrayList(Arrays.asList(elements));
        for (int i = 0;  i < elements.length;  i++) {
            int num = (int) (Math.random() * list.size());
            String element = (String) list.remove(num);
        	elements[i] = element;
        }
    }

    StringBuffer result = new StringBuffer();
    result.append("<ex:AllElement xmlns:ex=\"" + uri + "\">");
    for (int i = 0;  i < elements.length;  i++) {
    	result.append(elements[i]);
    }
    result.append("</ex:AllElement>");
    return result.toString();
  }

  /** <p>Tests marshalling and unmarshalling of an element with "xs:all"
   * contents.</p>
   */
  public void testAllElement() throws Exception {
	  final Format defaultDateTimeFormat = new XsDateTimeFormat();
	  final Format defaultDateFormat = new XsDateFormat();
	  final Format defaultTimeFormat = new XsTimeFormat();
	  testAllElement(defaultDateTimeFormat, defaultDateFormat, defaultTimeFormat);

	  final Format otherDateTimeFormat = new Format(){
		private static final long serialVersionUID = -6173879133371739286L;
		private final DateFormat df = new SimpleDateFormat("yyyy.MMM.dd 'at' HH:mm:ss.SSS z");
		public StringBuffer format(Object pObj, StringBuffer pAppendTo, FieldPosition pPos) {
			return df.format(((Calendar) pObj).getTime(), pAppendTo, pPos);
		}
		public Object parseObject(String pSource, ParsePosition pPos) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) df.parseObject(pSource, pPos));
			return cal;
		}
	  };
	  final Format otherDateFormat = new Format(){
		  private final DateFormat df = new SimpleDateFormat("yyyy.MMM.dd z");
		  public StringBuffer format(Object pObj, StringBuffer pAppendTo, FieldPosition pPos) {
			  return df.format(((Calendar) pObj).getTime(), pAppendTo, pPos);
		  }
		  public Object parseObject(String pSource, ParsePosition pPos) {
			  Calendar cal = Calendar.getInstance();
			  cal.setTime((Date) df.parseObject(pSource, pPos));
			  return cal;
		  }
	  };
	  final Format otherTimeFormat = new Format(){
		  private static final long serialVersionUID = -6173879133371739286L;
		  private final DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS z");
		  public StringBuffer format(Object pObj, StringBuffer pAppendTo, FieldPosition pPos) {
			  return df.format(((Calendar) pObj).getTime(), pAppendTo, pPos);
		  }
		  public Object parseObject(String pSource, ParsePosition pPos) {
			  Calendar cal = Calendar.getInstance();
			  cal.setTime((Date) df.parseObject(pSource, pPos));
			  return cal;
		  }
	  };
	  testAllElement(otherDateTimeFormat, otherDateFormat, otherTimeFormat);
  }

  private void testAllElement(final Format pDateTimeFormat, final Format pDateFormat, final Format pTimeFormat) throws JAXBException, PropertyException {
	  String s = getAllElementString(true, pDateTimeFormat, pDateFormat, pTimeFormat);
	  JAXBContext context = getFactory();
	  Unmarshaller unmarshaller = context.createUnmarshaller();
	  unmarshaller.setProperty(JMControllerImpl.JAXME_FORMAT_DATETIME, pDateTimeFormat);
	  unmarshaller.setProperty(JMControllerImpl.JAXME_FORMAT_DATE, pDateFormat);
	  unmarshaller.setProperty(JMControllerImpl.JAXME_FORMAT_TIME, pTimeFormat);
	  AllElement e = (AllElement) unmarshaller.unmarshal(new InputSource(new StringReader(s)));
	  StringWriter sw = new StringWriter();
	  Marshaller marshaller = context.createMarshaller();
	  marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
	  marshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");
	  marshaller.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
	  marshaller.setProperty(JMControllerImpl.JAXME_FORMAT_DATETIME, pDateTimeFormat);
	  marshaller.setProperty(JMControllerImpl.JAXME_FORMAT_DATE, pDateFormat);
	  marshaller.setProperty(JMControllerImpl.JAXME_FORMAT_TIME, pTimeFormat);
	  marshaller.marshal(e, sw);
	  String got = sw.toString();
	  String expect = getAllElementString(false, pDateTimeFormat, pDateFormat, pTimeFormat);
	  assertEquals(expect, got);
}

  private RootElementHandler getRootElementInfo(String pElement) throws ParserConfigurationException, IOException, SAXException {
    RootElementHandler reh = new RootElementHandler();
    XMLReader xr = getXMLReader();
    try {
      xr.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    } catch (SAXNotSupportedException e) {
    } catch (SAXNotRecognizedException e) {
    }
    try {
      xr.setFeature("http://xml.org/sax/features/xmlns-uris", true);
    } catch (SAXNotSupportedException e) {
    } catch (SAXNotRecognizedException e) {
    }
    xr.setContentHandler(reh);
    xr.parse(new InputSource(new StringReader(pElement)));
    return reh;
  }

  private class RootElementHandler extends DefaultHandler {
    Attributes rootElementAttributes;
    String rootElementURI, rootElementLocalName, rootElementQName;
    private boolean isRootElement = true;
    public void startElement(String pNamespaceURI, String pLocalName, String pQName,
    		                 Attributes pAttr) {
      if (isRootElement) {
      	isRootElement = false;
      	rootElementAttributes = pAttr;
      	rootElementURI = pNamespaceURI;
      	rootElementLocalName = pLocalName;
      	rootElementQName = pQName;
      }
    }
  }

  private String getContents(String pElement) {
    int offset1 = pElement.indexOf('>');
    int offset2 = pElement.lastIndexOf('<');
    assertTrue(offset2 > offset1);
    return pElement.substring(offset1+1, offset2);
  }

  protected void assertEquals(Attributes pAttr1, Attributes pAttr2) {
    assertEquals(pAttr1.getLength(), pAttr2.getLength());
    for (int i = 0;  i < pAttr1.getLength();  i++) {
       String uri = pAttr1.getURI(i);
        String localName = pAttr1.getLocalName(i);
        String value = pAttr1.getValue(i);
        assertNotNull(value);
        assertEquals(value, pAttr2.getValue(uri, localName));
    }
  }

  /** <p>Tests the {@link Marshaller} property
   * {@link Marshaller#JAXB_SCHEMA_LOCATION}.</p>
   */
  public void testSchemaLocation() throws Exception {
    String s = getAllTypesElementString(true, false);
    JAXBContext context = getFactory();
    AllTypesElement e = (AllTypesElement) context.createUnmarshaller().unmarshal(new InputSource(new StringReader(s)));
    StringWriter sw = new StringWriter();
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "foo.xsd");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");
    marshaller.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
    marshaller.marshal(e, sw);
    String got = sw.toString();
    String s1 = "xsi:" + XMLConstants.XML_SCHEMA_NS_ATTR + "='foo.xsd'";
    String s2 = "xsi:" + XMLConstants.XML_SCHEMA_NS_ATTR + "=\"foo.xsd\"";
    int offset1 = s.indexOf(s1);
    int offset2 = s.indexOf(s2);
    int offset3 = s.indexOf('>');
    assertTrue((offset1 > 0  &&  offset1 < offset3)
               ||  (offset2 > 0  &&  offset2 < offset3));
 
    // When validating the result, we are unsure about the
    // order of the attributes in the outermost element.
    RootElementHandler reh1 = getRootElementInfo(s);
    RootElementHandler reh2 = getRootElementInfo(got);
    assertEquals(reh1.rootElementURI, reh2.rootElementURI);
    assertEquals(reh1.rootElementLocalName, reh2.rootElementLocalName);
    assertEquals(reh1.rootElementAttributes, reh2.rootElementAttributes);
    assertEquals(getContents(s), getContents(got));
  }


	/** Tests unmarshalling of an invalid string into a JaxMe object.
	 */
	public void testValidator() throws Exception {
		String invalidAddress = // Note the missing "Last"
			"<Address xmlns=\"http://ws.apache.org/jaxme/test/misc/address\">\n" +
			"  <Name>\n" +
			"    <First>Jane</First>\n" +
			"  </Name>\n" +
			"</Address>";
		InputSource isource = new InputSource(new StringReader(invalidAddress));
		isource.setSystemId("testValidator.xml");
		JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.address");
		try {
			context.createUnmarshaller().unmarshal(isource);
			fail("No error reported.");
		} catch (UnmarshalException e) {
			// Ok
		} catch (Throwable t) {
			fail("Unexpected throwable " + t);
		}

		org.apache.ws.jaxme.test.misc.address.ObjectFactory of = new org.apache.ws.jaxme.test.misc.address.ObjectFactory();
		Address address = of.createAddress();
		address.setName(of.createAddressTypeNameType());
		AddressType.NameType name = address.getName();
		name.setFirst("Jane");
		try {
			assertFalse(context.createValidator().validate(address));
		} catch (Throwable t) {
			t.printStackTrace();
			fail("Unexpected throwable " + t);
		}
	}

	/** Tests marshalling and unmarshalling of a mixed content element.
	 */
	public void testMixedContent() throws Exception {
		final String html =
			"<ex:html xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/types\">\n" +
			"  xyz<ex:dummy>012</ex:dummy>\n" +
			"  <ex:head><ex:title>foo bar</ex:title></ex:head>\n" +
			"  <ex:body><ex:p/></ex:body>\n" +
			"</ex:html>";
		InputSource isource = new InputSource(new StringReader(html));
		isource.setSystemId("mixedContent.xml");
		JAXBContext ctx = getJAXBContext(Html.class);
		Html htmlElem = (Html) ctx.createUnmarshaller().unmarshal(isource);
		StringWriter sw = new StringWriter();
		Marshaller m = ctx.createMarshaller();
		m.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		m.marshal(htmlElem, sw);
		String got = sw.toString();
		assertEquals(html, got);
	}

	private void testPattern(boolean pSuccess, String pValue) throws Exception {
		boolean patternTestingAvailable = !(REFactory.getREHandler() instanceof REFactory.DummyREHandler);
		final String prefix = "<ex:patterns xmlns:ex='http://ws.apache.org/jaxme/test/misc/types' foo='";
		final String suffix = "'/>";
		final String xml1 = prefix + pValue + suffix;
		JAXBContext ctx = getJAXBContext(Patterns.class);
		Unmarshaller u = ctx.createUnmarshaller();
		EventDetector ed = new EventDetector();
		u.setEventHandler(ed);
		Patterns patterns = (Patterns) u.unmarshal(new InputSource(new StringReader(xml1)));
		if (pSuccess || !patternTestingAvailable) {
			assertTrue(ed.isSuccess());
			assertEquals(pValue, patterns.getFoo());
		} else {
			assertTrue(!ed.isSuccess());
			assertNull(patterns.getFoo());
		}
	}

	/** Tests, whether patterns are evaluated while unmarshalling.
	 */
	public void testPatterns() throws Exception {
		testPattern(true, "xY01");
		testPattern(true, "XY01");
		testPattern(true, "xY");
		testPattern(true, "xY0123");
		testPattern(true, "x012");
		testPattern(false, "012345");
	}
}
