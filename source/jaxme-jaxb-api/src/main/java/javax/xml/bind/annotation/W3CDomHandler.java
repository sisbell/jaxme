/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.annotation;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Default implementation of {@link DomHandler} for {@link Node W3C DOM}.
 * @since JAXB 2.0
 */
public class W3CDomHandler implements DomHandler<Element,DOMResult> {
    private DocumentBuilder builder;

    /**
     * Default constructor; uses the {@link DocumentBuilder}
     * choosen by the JAXB provider.
     */
    public W3CDomHandler() {
        // Do nothing
    }

    /**
     * Constructor using the {@link DocumentBuilder}
     * choosen by the caller.
     * @param builder Non-null {@link DocumentBuilder}
     * @throws IllegalArgumentException The parameter was null.
     */
    public W3CDomHandler(DocumentBuilder pBuilder) {
        if (pBuilder==null)
            throw new IllegalArgumentException();
        builder = pBuilder;
    }

    /**
     * Returns the {@link DocumentBuilder}, which is being
     * used for parsing or creating documents.
     */
    public DocumentBuilder getBuilder() {
        return builder;
    }

    /**
     * Sets the {@link DocumentBuilder}, which is being
     * used for parsing or creating documents.
     */
    public void setBuilder(DocumentBuilder pBuilder) {
        builder = pBuilder;
    }

    public DOMResult createUnmarshaller(ValidationEventHandler errorHandler) {
        if (builder==null) {
            return new DOMResult();
        }
        return new DOMResult(builder.newDocument());
    }

    public Element getElement(DOMResult r) {
        Node n = r.getNode();
        switch (n.getNodeType()) {
            case Node.DOCUMENT_NODE:
                return ((Document) n).getDocumentElement();
            case Node.ELEMENT_NODE:
                return (Element) n;
            case Node.DOCUMENT_FRAGMENT_NODE:
                return (Element) ((DocumentFragment) n).getFirstChild();
            default:
                throw new IllegalStateException("Unexpected node type: " + n);
        }
    }

    public Source marshal(Element element, ValidationEventHandler errorHandler) {
        return new DOMSource(element);
    }
}
