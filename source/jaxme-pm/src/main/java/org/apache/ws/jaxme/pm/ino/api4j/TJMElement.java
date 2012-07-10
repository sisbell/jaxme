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
 
package org.apache.ws.jaxme.pm.ino.api4j;

import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.pm.ino.InoObject;

import com.softwareag.tamino.db.api.objectModel.sax.TSAXDocument;
import com.softwareag.tamino.db.api.objectModel.sax.TSAXElement;


/** JaxMe specific implementation of
 * {@link com.softwareag.tamino.db.api.objectModel.sax.TSAXElement},
 * and {@link com.softwareag.tamino.db.api.objectModel.sax.TSAXDocument}.
 */
public class TJMElement implements TSAXDocument, TSAXElement {
    private final InoObject element;

    /** Creates a new instance, which reads or writes
     * the given {@link InoObject}.
     */
    public TJMElement(InoObject pElement) {
        element = pElement;
    }

    /** Returns the {@link InoObject}, which is being
     * read, or written.
     */
    public InoObject getJMElement() {
        return element;
    }

    public TSAXElement getRootElement() {
        return this;
    }

    public void writeTo(Writer pWriter) {
        try {
            TaminoAPI4JRaPm.getJAXBContext().createMarshaller().marshal(getJMElement(), pWriter);
        } catch (JAXBException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    public void setDocname(String pDocname) {
        element.setInoDocname(pDocname);
    }

    public void setId(String pId) {
        element.setInoId(pId);
    }

    public String getDoctype() {
        QName qName = element.getQName();
        String prefix = qName.getPrefix();
        if (prefix == null  ||  prefix.length() == 0) {
            return qName.getLocalPart();
        } else {
            return prefix + ':' + qName.getLocalPart();
        }
    }

    public String getDocname() {
        String result = element.getInoDocname();
        return result == null ? "" : result;
    }

    public String getId() {
        String result = element.getInoId();
        return result == null ? "" : result;
    }
}
