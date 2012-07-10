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
package javax.xml.bind.helpers;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.ValidationEventLocator;

import org.w3c.dom.Node;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;


/** <p>Default implementation of a {@link javax.xml.bind.ValidationEventLocator}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class ValidationEventLocatorImpl implements ValidationEventLocator {
    private URL url;
    private int offset, lineNumber, columnNumber;
    private Object object;
    private Node node;

    /** <p>Creates a new instance of <code>ValidationEventLocatorImpl</code>.</p>
     */
    public ValidationEventLocatorImpl() {
        offset = lineNumber = columnNumber = -1;
    }

    /** <p>Creates a new instance of <code>ValidationEventLocatorImpl</code>
     * by copying data from the given {@link org.xml.sax.Locator}.</p>
     * @param pLocator The SAX locator where to copy from.
     */
    public ValidationEventLocatorImpl(Locator pLocator) {
        if (pLocator == null) {
            lineNumber = columnNumber = -1;
        } else {
            columnNumber = pLocator.getColumnNumber();
            lineNumber = pLocator.getLineNumber();
            String u = pLocator.getSystemId();
            if (u != null) {
                try {
                    url = new URL(pLocator.getSystemId());
                } catch (MalformedURLException e) {
                    // Ignore me
                }
            }
        }
        offset = -1;
    }

    /** <p>Creates a new instance of <code>ValidationEventLocatorImpl</code>
     * by setting the node property.</p>
     * @param pNode The node being referenced.
     */
    public ValidationEventLocatorImpl(Node pNode) {
        node = pNode;
        offset = lineNumber = columnNumber = -1;
    }

    /** <p>Creates a new instance of <code>ValidationEventLocatorImpl</code>
     * by setting the object property.</p>
     * @param pObject The object being referenced.
     */
    public ValidationEventLocatorImpl(Object pObject) {
        object = pObject;
        offset = lineNumber = columnNumber = -1;
    }

    /** <p>Creates a new instance of <code>ValidationEventLocatorImpl</code>
     * by copying data from the given {@link org.xml.sax.SAXParseException}.</p>
     * @param pException The SAX exception where to copy from.
     */
    public ValidationEventLocatorImpl(SAXParseException pException) {
        columnNumber = pException.getColumnNumber();
        lineNumber = pException.getLineNumber();
        String u = pException.getSystemId();
        if (u != null) {
            try {
                url = new URL(pException.getSystemId());
            } catch (MalformedURLException e) {
                // Ignore me
            }
        }
        offset = -1;
    }

    /** <p>Sets the URL.</p>
     */
    public void setURL(URL pURL) {
        url = pURL;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getURL()
     */
    public URL getURL() {
        return url;
    }

    /** <p>Sets the offset.</p>
     */
    public void setOffset(int pOffset) {
        offset = pOffset;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getOffset()
     */
    public int getOffset() {
        return offset;
    }

    /** <p>Sets the line number.</p>
     */
    public void setLineNumber(int pLineNumber) {
        lineNumber = pLineNumber;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getLineNumber()
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /** <p>Sets the column number.</p>
     */
    public void setColumnNumber(int pColumnNumber) {
        columnNumber = pColumnNumber;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getColumnNumber()
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /** <p>Sets the object.</p>
     */
    public void setObject(Object pObject) {
        object = pObject;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getObject()
     */
    public Object getObject() {
        return object;
    }

    /** <p>Sets the node.</p>
     */
    public void setNode(Node pNode) {
        node = pNode;
    }

    /* @see javax.xml.bind.ValidationEventLocator#getNode()
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns a string representation of this object in a format
     * helpful to debugging.
     * 
     * @see Object#equals(Object)
     */
    @Override
    public String toString() {
        return "[node=" + getNode() + ",object=" + getObject()
            + ",url=" + getURL() + ",line=" + getLineNumber()
            + ",col=" + getColumnNumber() + ",offset={5}]"
            + getOffset() + "]";
    }
}
