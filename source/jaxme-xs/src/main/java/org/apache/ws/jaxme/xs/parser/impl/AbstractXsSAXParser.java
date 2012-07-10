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
package org.apache.ws.jaxme.xs.parser.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.ws.jaxme.xs.parser.AttributeSetter;
import org.apache.ws.jaxme.xs.parser.ChildSetter;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractXsSAXParser implements ContentHandler, XsSAXParser {
    private final static Class[] ZERO_CLASSES = new Class[0];
    private final static Object[] ZERO_OBJECTS = new Object[0];

    private final Object bean;
    private int level;
    private String qName, namespaceURI, localName;
    private ContentHandler childHandler;

    protected abstract XSContext getData();

    public AbstractXsSAXParser(Object pBean) {
        if (pBean == null) {
            throw new NullPointerException("The target bean is null.");
        }
        bean = pBean;
    }

    public Object getBean() {
        return bean;
    }

    public void setQName(String pQName) {
        qName = pQName;
    }

    public void setNamespaceURI(String pNamespaceURI) {
        namespaceURI = pNamespaceURI;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setLocalName(String pLocalName) {
        localName = pLocalName;
    }

    public String getLocalName() {
        return localName;
    }

    public String getQName() {
        return qName;
    }

    public ContentHandler getChildHandler() {
        return childHandler;
    }

    public void startPrefixMapping(String pPrefix, String pURI)
    throws SAXException {
        if (childHandler == null) {
            getData().getNamespaceSupport().declarePrefix(pPrefix, pURI);
        } else {
            childHandler.startPrefixMapping(pPrefix, pURI);
        }
    }

    public void endPrefixMapping(String pPrefix) throws SAXException {
        if (childHandler != null) {
            childHandler.endPrefixMapping(pPrefix);
        }
    }

    public void startDocument() throws SAXException {
        getData().getNamespaceSupport().pushContext();
    }

    public void endDocument() throws SAXException {
        getData().getNamespaceSupport().popContext();
    }

    public void characters(char[] pBuffer, int pOffset, int pLen) throws SAXException {
        if (childHandler == null) {
            try {
                getData().getTextSetter().addText(new String(pBuffer, pOffset, pLen));
            } catch (SAXException e) {
                throw e;
            } catch (RuntimeException e) {
                Exception ex = e;
                for (;;) {
                    UndeclaredThrowableException te = null;
                    Throwable t;
                    if (ex instanceof UndeclaredThrowableException) {
                        te = ((UndeclaredThrowableException) ex);
                        t = te.getUndeclaredThrowable();
                    } else if (ex instanceof InvocationTargetException) {
                        t = ((InvocationTargetException) ex).getTargetException();
                    } else {
                        break;
                    }
                    if (t instanceof Exception) {
                        ex = (Exception) t;
                    } else {
                        if (te == null) {
                            te = new UndeclaredThrowableException(t);
                        }
                        t.printStackTrace();
                        throw te;
                    }
                }
                throw new LocSAXException(ex.getClass().getName() + ": " + ex.getMessage(),
                        getData().getLocator(), ex);
            }
        } else {
            childHandler.characters(pBuffer, pOffset, pLen);
        }
    }

    public void ignorableWhitespace(char[] pBuffer, int pOffset, int pLen)
    throws SAXException {
        if (childHandler == null) {
            characters(pBuffer, pOffset, pLen);
        } else {
            childHandler.ignorableWhitespace(pBuffer, pOffset, pLen);
        }
    }

    public void skippedEntity(String pEntity) throws SAXException {
        if (childHandler == null) {
            throw new LocSAXException("Unable to skip entities: " + pEntity,
                    getData().getLocator());
        } else {
            skippedEntity(pEntity);
        }
    }

    public void processingInstruction(String pTarget, String pData)
    throws SAXException {
        if (childHandler != null) {
            childHandler.processingInstruction(pTarget, pData);
        }
    }

    public void startElement(String pNamespaceURI, String pLocalName, String pQName, Attributes pAttr)
    throws SAXException {
        switch (++level) {
        case 1:
            setQName(pQName);
            setNamespaceURI(pNamespaceURI);
            setLocalName(pLocalName);
            if (pAttr != null) {
                for (int i = 0;  i < pAttr.getLength();  i++) {
                    try {
                        AttributeSetter attrSetter = getData().getAttributeSetter();
                        attrSetter.setAttribute(pAttr.getQName(i), pAttr.getURI(i),
                                pAttr.getLocalName(i), pAttr.getValue(i));
                    } catch (SAXException e) {
                        throw e;
                    } catch (RuntimeException e) {
                        Exception ex = e;
                        for (;;) {
                            UndeclaredThrowableException te = null;
                            Throwable t;
                            if (ex instanceof UndeclaredThrowableException) {
                                te = ((UndeclaredThrowableException) ex);
                                t = te.getUndeclaredThrowable();
                            } else if (ex instanceof InvocationTargetException) {
                                t = ((InvocationTargetException) ex).getTargetException();
                            } else {
                                break;
                            }
                            if (t instanceof Exception) {
                                ex = (Exception) t;
                            } else {
                                if (te == null) {
                                    te = new UndeclaredThrowableException(t);
                                }
                                t.printStackTrace();
                                throw te;
                            }
                        }
                        throw new LocSAXException(ex.getClass().getName() + ": " + ex.getMessage(),
                                getData().getLocator(), ex);
                    }
                }
            }
            break;
        case 2:
            try {
                ChildSetter childSetter = getData().getChildSetter();
                childHandler = childSetter.getChildHandler(pQName, pNamespaceURI, pLocalName);
            } catch (SAXException e) {
                throw e;
            } catch (RuntimeException e) {
                Exception ex = e;
                for (;;) {
                    UndeclaredThrowableException te = null;
                    Throwable t;
                    if (ex instanceof UndeclaredThrowableException) {
                        te = ((UndeclaredThrowableException) ex);
                        t = te.getUndeclaredThrowable();
                    } else if (ex instanceof InvocationTargetException) {
                        t = ((InvocationTargetException) ex).getTargetException();
                    } else {
                        break;
                    }
                    if (t instanceof Exception) {
                        ex = (Exception) t;
                    } else {
                        if (te == null) {
                            te = new UndeclaredThrowableException(t);
                        }
                        t.printStackTrace();
                        throw te;
                    }
                }
                throw new LocSAXException(ex.getClass().getName() + ": " + ex.getMessage(),
                        getData().getLocator(), ex);
            }
            getData().setCurrentContentHandler(childHandler);
            childHandler.startDocument();
            childHandler.startElement(pNamespaceURI, pLocalName, pQName, pAttr);
            break;
        default:
            childHandler.startElement(pNamespaceURI, pLocalName, pQName, pAttr);
        break;
        }
    }

    public void endElement(String pNamespaceURI, String pLocalName, String pQName)
    throws SAXException {
        switch (level--) {
        case 1:
            Object o = getBean();
            if (o != null) {
                Method m = null;
                try {
                    m = o.getClass().getMethod("validate", ZERO_CLASSES);
                } catch (NoSuchMethodException e) {
                }
                if (m != null) {
                    try {
                        m.invoke(o, ZERO_OBJECTS);
                    } catch (RuntimeException e) {
                        throw new LocSAXException(e.getClass().getName() + ": " + e.getMessage(),
                                getData().getLocator(), e);
                    } catch (InvocationTargetException e) {
                        Throwable t = e.getTargetException();
                        if (t instanceof SAXException) {
                            throw (SAXException) t;
                        } else if (t instanceof RuntimeException) {
                            throw new LocSAXException(t.getClass().getName() + ": " + t.getMessage(),
                                    getData().getLocator(),
                                    (RuntimeException) t);
                        } else if (t instanceof Exception) {
                            throw new LocSAXException("Failed to invoke method validate() " +
                                    " of class " + m.getDeclaringClass() +
                                    " with argument " + o.getClass().getName() + ": " +
                                    t.getClass().getName() + ", " + t.getMessage(),
                                    getData().getLocator(),
                                    (Exception) t);
                        } else {
                            throw new LocSAXException("Failed to invoke method validate() " +
                                    " of class " + m.getDeclaringClass() +
                                    " with argument " + o.getClass().getName() + ": " +
                                    t.getClass().getName() + ", " + t.getMessage(),
                                    getData().getLocator(), e);
                        }
                    } catch (IllegalAccessException e) {
                        throw new LocSAXException("Failed to invoke method validate() " +
                                " of class " + m.getDeclaringClass() +
                                " with argument " + o.getClass().getName() + ": IllegalAccessException, " +
                                e.getMessage(),
                                getData().getLocator(), e); 

                    }
                }
            }
            break;
        case 2:
            childHandler.endElement(pNamespaceURI, pLocalName, pQName);
            childHandler.endDocument();
            getData().setCurrentContentHandler(this);
            childHandler = null;
            break;
        default:
            childHandler.endElement(pNamespaceURI, pLocalName, pQName);
        break;
        }
    }

    public void setDocumentLocator(Locator pLocator) {
        getData().setLocator(pLocator);
        if (childHandler != null) {
            childHandler.setDocumentLocator(pLocator);
        }
    }
}
