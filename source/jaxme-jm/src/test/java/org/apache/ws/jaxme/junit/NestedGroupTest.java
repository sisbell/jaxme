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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.impl.ValidationEventImpl;
import org.apache.ws.jaxme.test.misc.group.impl.PersonsImpl;
import org.apache.ws.jaxme.test.misc.types.FsDirectory;
import org.apache.ws.jaxme.test.misc.types.FsDirectoryType;
import org.apache.ws.jaxme.test.misc.types.FsFile;
import org.apache.ws.jaxme.test.misc.types.FsNode;
import org.apache.ws.jaxme.test.nestedgroups.MailTemplate;
import org.apache.ws.jaxme.test.nestedgroups.MailTemplateMixed;
import org.apache.ws.jaxme.test.nestedgroups.impl.MailTemplateImpl;
import org.xml.sax.InputSource;


/**
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class NestedGroupTest extends BaseTestCase {
	/** Creates a new instance with the given name.
	 */
	public NestedGroupTest(String pName) {
        super(pName);
    }

    private String getPersons() throws Exception {
        return
        "<Persons xmlns=\"" + new PersonsImpl().getQName().getNamespaceURI() + "\">\n"
        + "  <Person Alias=\"Ias\">\n"
        + "    <FirstName>Changshin</FirstName>\n"
        + "    <LastName>Lee</LastName>\n"
        + "    <Age>30</Age>\n"
        + "  </Person>\n"
        + "</Persons>";
    }
    
	/** Tests unmarshalling and marshalling of the
	 * document returned by {@link #getPersons()}.
	 */
	public void testNestedGroup() throws Exception {
		//unmarshalMarshalUnmarshal(Persons.class, getPersons());
    }

    private String getMailTemplate1(boolean pMixed) {
		final String a, b, c, d, name;
		if (pMixed) {
			name = "Mixed";
			a = "abc";
			b = "bcd";
			c = "cde";
			d = "def";
		} else {
			name = a = b = c = d = "";
		}
		return
        "<ng:MailTemplate" + name + " name=\"foo\" language=\"bar\""
        + " xmlns:ng=\"" + new MailTemplateImpl().getQName().getNamespaceURI() + "\">\n"
        + "  " + a + "<ng:subject>A test subject</ng:subject>" + b + "\n"
        + "  " + c + "<ng:body>A test body</ng:body>" + d + "\n"
        + "</ng:MailTemplate" + name + ">";
    }

    private String getMailTemplate2(boolean pMixed) {
		final String a, b, c, d, name;
		if (pMixed) {
			name = "Mixed";
			a = "abc";
			b = "bcd";
			c = "cde";
			d = "def";
		} else {
			name = a = b = c = d = "";
		}
        return
        "<ng:MailTemplate" + name + " name=\"foo\" language=\"bar\""
        + " xmlns:ng=\"" + new MailTemplateImpl().getQName().getNamespaceURI() + "\">\n"
        + "  " + a + "<ng:subject>A test subject</ng:subject>" + d + "\n"
        + "  " + b + "<ng:prepend>A prefix</ng:prepend>\n"
        + "  " + c + "<ng:append>A suffix</ng:append>\n"
        + "</ng:MailTemplate" + name + ">";
    }

    private String getMailTemplate3(boolean pMixed) {
		final String a, b, c, d, name;
		if (pMixed) {
			name = "Mixed";
			a = "abc";
			b = "bcd";
			c = "cde";
			d = "def";
		} else {
			name = a = b = c = d = "";
		}
        return
        "<ng:MailTemplate" + name + " name=\"foo\" language=\"bar\""
        + " xmlns:ng=\"" + new MailTemplateImpl().getQName().getNamespaceURI() + "\">\n"
        + "  " + a + "<ng:subject>A test subject</ng:subject>" + d + "\n"
        + "  " + b + "<ng:prepend>A prefix</ng:prepend>\n"
        + "  " + c + "<ng:append>A suffix</ng:append>\n"
        + "  <ng:body>A test body</ng:body>\n"
        + "</ng:MailTemplate" + name + ">";
    }

    /** Tests unmarshalling and marshalling of an instance of
     * {@link MailTemplate}.
     */
    public void testMailTemplate() throws Exception {
        unmarshalMarshalUnmarshal(MailTemplate.class, getMailTemplate1(false));
        unmarshalMarshalUnmarshal(MailTemplate.class, getMailTemplate2(false));
    }

	private class MyEventHandler implements ValidationEventHandler {
		private boolean ok;
		public boolean handleEvent(ValidationEvent pEvent) {
			if (pEvent instanceof ValidationEventImpl) {
				ValidationEventImpl ev = (ValidationEventImpl) pEvent;
				if (ValidationEvents.EVENT_CHOICE_GROUP_REUSE.equals(ev.getErrorCode())) {
					ok = true;
				}
			}
			return false;
		}
	}

	/** Tests proper handling of the choice group.
	 */
	public void testMailTemplateError() throws Exception {
        JAXBContext context = JAXBContext.newInstance(getPackageName(MailTemplate.class));
        Unmarshaller unmarshaller = context.createUnmarshaller();
		MyEventHandler h = new MyEventHandler();
		unmarshaller.setEventHandler(h);
		try {
	        unmarshaller.unmarshal(new InputSource(new StringReader(getMailTemplate3(false))));
		} catch (Throwable t) {
		}
		assertTrue(h.ok);
	}

    /** Tests unmarshalling and marshalling of an instance of
     * {@link MailTemplateMixed}.
     */
    public void testMailTemplateMixed() throws Exception {
        unmarshalMarshalUnmarshal(MailTemplateMixed.class, getMailTemplate1(true), false);
        unmarshalMarshalUnmarshal(MailTemplateMixed.class, getMailTemplate2(true), false);
    }

	/** Tests proper handling of the choice group.
	 */
	public void testMailTemplateMixedError() throws Exception {
        JAXBContext context = JAXBContext.newInstance(getPackageName(MailTemplateMixed.class));
        Unmarshaller unmarshaller = context.createUnmarshaller();
		MyEventHandler h = new MyEventHandler();
		unmarshaller.setEventHandler(h);
		try {
	        unmarshaller.unmarshal(new InputSource(new StringReader(getMailTemplate3(true))));
		} catch (Throwable t) {
		}
		assertTrue(h.ok);
	}

	/** Tests proper inheritance of xs:extension.
	 */
	public void testInheritance() throws Exception {
		assertTrue(FsNode.class.isAssignableFrom(FsFile.class));
		assertTrue(FsNode.class.isAssignableFrom(FsDirectory.class));
		assertTrue(FsNode.class.isAssignableFrom(FsDirectoryType.class));
	}
}
