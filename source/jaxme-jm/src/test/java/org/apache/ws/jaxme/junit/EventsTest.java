package org.apache.ws.jaxme.junit;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ParseConversionEvent;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.ws.jaxme.test.misc.types.EAllSimpleTypeAttrs;
import org.xml.sax.InputSource;


/** Tests, whether various events are generated.
 */
public class EventsTest extends BaseTestCase {
	private class Handler implements ValidationEventHandler {
		private ValidationEvent event;
		public boolean handleEvent(ValidationEvent pEvent) {
			event = pEvent;
			return false;
		}
	};

	private ValidationEvent unmarshal(String pXML) throws JAXBException {
		Handler h = new Handler();
		JAXBContext context = JAXBContext.newInstance(getPackageName(EAllSimpleTypeAttrs.class));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setEventHandler(h);
		try {
			unmarshaller.unmarshal(new InputSource(new StringReader(pXML)));
		} catch (JAXBException e) {
			return h.event;
		}
		return null;
	}

	/** Tests, whether a ConversionEvent is generated.
	 */
	public void testConversionEvents() throws JAXBException {
		final String xml = "<ex:EAllSimpleTypeAttrs xmlns:ex='http://ws.apache.org/jaxme/test/misc/types' IntAttr=''/>";
		ValidationEvent e = unmarshal(xml);
		assertNotNull(e);
		assertTrue(e instanceof ParseConversionEvent);
	}
}
