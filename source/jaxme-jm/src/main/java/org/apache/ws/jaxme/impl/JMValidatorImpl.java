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
package org.apache.ws.jaxme.impl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.ws.jaxme.JMValidator;


/** Simple validator implementation. Works by creating a
 * {@link javax.xml.bind.Marshaller}, which generates SAX events.
 * The SAX events are piped into an instance of
 * {@link javax.xml.bind.UnmarshallerHandler}.
 */
public class JMValidatorImpl extends JMControllerImpl implements JMValidator {
    private class MyEventHandler implements ValidationEventHandler {
        private boolean invalid;
        public boolean handleEvent(ValidationEvent pEvent) {
            invalid = true;
            ValidationEventHandler eh = getEventHandler();
            if (eh == null) {
                return true;
            } else {
                return eh.handleEvent(pEvent);
            }
        }
    }

    public boolean validate(Object pObject) throws JAXBException {
        JAXBContext jc = getJAXBContextImpl();
        Marshaller m = jc.createMarshaller();
        Unmarshaller u = jc.createUnmarshaller();
        MyEventHandler eh = new MyEventHandler();
        u.setEventHandler(eh);
        UnmarshallerHandler uh = u.getUnmarshallerHandler();
        m.marshal(pObject, uh);
        return !eh.invalid;
    }

    public boolean validateRoot(Object pObject) throws JAXBException {
        return validate(pObject);
    }
}
