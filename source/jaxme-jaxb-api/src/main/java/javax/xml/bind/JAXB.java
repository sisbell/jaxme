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
package javax.xml.bind;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * <p>A utility class, which provides convenience methods for
 * simplyfying the creation of JAXB applications.</p>
 * <p><em>Note</em>: These methods aren't optimized for
 * performance. In particular, you should consider additional
 * steps, if the same operation is repeated frequently.</p>
 * @since JAXB 2.1
 */
public final class JAXB {
    /**
     * Prevent instance creation.
     */
    private JAXB() {
        // Does nothing.
    }

    private static final Map<Class<?>, WeakReference<JAXBContext>> map
        = new WeakHashMap<Class<?>, WeakReference<JAXBContext>>();

    private static <T> JAXBContext getContext(Class<T> pType) throws JAXBException {
        WeakReference<JAXBContext> ref;
        synchronized (map) {
            ref = map.get(pType);
        }
        if (ref != null) {
            JAXBContext ctx = ref.get();
            if (ctx != null) {
                return ctx;
            }
        }
        JAXBContext ctx = JAXBContext.newInstance(pType);
        ref = new WeakReference<JAXBContext>(ctx);
        synchronized (map) {
            map.put(pType, ref);
        }
        return ctx;
    }

    private static final URI asURI(String pURI) {
        try {
            return new URI(pURI);
        } catch (URISyntaxException e) {
            return new File(pURI).toURI();
        }
    }
    /**
     * Reads the given files contents and converts it into
     * a JAXB object.
     * @param pFile The file to read.
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(File pFile, Class<T> pType) {
        return unmarshal(new StreamSource(pFile), pType);
    }

    /**
     * Reads the given URL's contents and converts it into
     * a JAXB object.
     * @param pURL The URL to read.
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(URL pURL, Class<T> pType) {
        InputStream stream = null;
        try {
            stream = pURL.openStream();
            InputSource isource = new InputSource(stream);
            isource.setSystemId(pURL.toExternalForm());
            T result = unmarshal(new SAXSource(isource), pType);
            stream.close();
            stream = null;
            return result;
        } catch (IOException e) {
            throw new DataBindingException(e);
        } finally {
            if (stream != null) { try { stream.close(); } catch (Throwable t) { /* Ignore me */ } }
        }
    }

    /**
     * Reads the given URI's contents and converts it into
     * a JAXB object.
     * @param pURI The URI to read.
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(URI pURI, Class<T> pType) {
        try {
            return unmarshal(pURI.toURL(), pType);
        } catch (MalformedURLException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Reads the given URI's contents and converts it into
     * a JAXB object.
     * @param pURI The URI to read.
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(String pURI, Class<T> type) {
        return unmarshal(asURI(pURI), type);
    }

    /**
     * Reads the given byte streams contents and converts it into
     * a JAXB object.
     * @param pStream The stream to read; will be closed by this
     *   method upon successful completion
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(InputStream pStream, Class<T> pType) {
        T result = unmarshal(new StreamSource(pStream), pType);
        try {
            pStream.close();
        } catch (IOException e) {
            throw new DataBindingException(e);
        }
        return result;
    }

    /**
     * Reads the given character streams contents and converts it into
     * a JAXB object.
     * @param pStream The stream to read; will be closed by this
     *   method upon successful completion
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(Reader pStream, Class<T> pType) {
        T result = unmarshal(new StreamSource(pStream), pType);
        try {
            pStream.close();
        } catch (IOException e) {
            throw new DataBindingException(e);
        }
        return result;
    }

    /**
     * Reads the given source and converts it into
     * a JAXB object.
     * @param pSource The XML document to read.
     * @param pType Type of the JAXB object, which is being returned.
     * @throws DataBindingException A {@link JAXBException}
     *   was raised and may be read as the {@link DataBindingException
     *   DataBindingException's} cause.
     */
    public static <T> T unmarshal(Source pSource, Class<T> pType) {
        try {
            JAXBElement<T> item = getContext(pType).createUnmarshaller().unmarshal(pSource, pType);
            T result = item.getValue();
            return result;
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Writes the given JAXB object to the given file.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, File pFile) {
        marshal(pObject, new StreamResult(pFile));
    }

    /**
     * Writes the given JAXB object to the given URL.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, URL pURL) {
        URLConnection con = null;
        HttpURLConnection httpCon = null;
        OutputStream ostream = null;
        try {
            con = pURL.openConnection();
            httpCon = (con instanceof HttpURLConnection) ? ((HttpURLConnection) con) : null;
            con.setDoOutput(true);
            con.setDoInput(false);
            con.connect();
            ostream = con.getOutputStream();
            marshal(pObject, new StreamResult(ostream));
            if (ostream != null) {
                ostream.close();
                ostream = null;
            }
            if (httpCon != null) {
                httpCon.disconnect();
                httpCon = null;
            }
        } catch (IOException e) {
            throw new DataBindingException(e);
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (Throwable t) {
                    // Ignore me
                }
            }
            if (httpCon != null) {
                try {
                    httpCon.disconnect();
                } catch (Throwable t) {
                    // Ignore me
                }
            }
        }
    }

    /**
     * Writes the given JAXB object to the given URI.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, URI pURI) {
        try {
            marshal(pObject, pURI.toURL());
        } catch (MalformedURLException e) {
            throw new DataBindingException(e);
        }
    }

    /**
     * Writes a Java object tree to XML and store it to the specified location.
     *
     * @param jaxbObject
     *      The Java object to be marshalled into XML. If this object is
     *      a {@link JAXBElement}, it will provide the root tag name and
     *      the body. If this object has {@link XmlRootElement}
     *      on its class definition, that will be used as the root tag name
     *      and the given object will provide the body. Otherwise,
     *      the root tag name is {@link Introspector#decapitalize(String) infered} from
     *      {@link Class#getSimpleName() the short class name}.
     *      This parameter must not be null.
     *
     * @param xml
     *      The string is first interpreted as an absolute <tt>URI</tt>.
     *      If it's not {@link URI#isAbsolute() a valid absolute URI},
     *      then it's interpreted as a <tt>File</tt>
     *
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, String pURI) {
        marshal(pObject, asURI(pURI));
    }

    /**
     * Writes the given JAXB object to the given {@link OutputStream}.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, OutputStream pStream) {
        marshal(pObject, pStream);
    }

    /**
     * Writes the given JAXB object to the given {@link Writer}.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    public static void marshal(Object pObject, Writer pWriter) {
        marshal(pObject, new StreamResult(pWriter));
    }

    /**
     * Writes the given JAXB object to the given location.
     *
     * @param pObject The object being written.
     * @param pFile The target file.
     * @throws DataBindingException
     *      If the operation fails, such as due to I/O error, unbindable classes.
     */
    @SuppressWarnings("unchecked")
    public static void marshal(Object pObject, Result pTarget) {
        Object obj = pObject;
        try {
            JAXBContext ctx;
            if (obj instanceof JAXBElement) {
                ctx = getContext(((JAXBElement<?>) obj).getDeclaredType());
            } else {
                Class<?> clazz = obj.getClass();
                XmlRootElement r = clazz.getAnnotation(XmlRootElement.class);
                ctx = getContext(clazz);
                if (r == null) {
                    // we need to infer the name
                    obj = new JAXBElement(new QName(Introspector.decapitalize(clazz.getSimpleName())), clazz, obj);
                }
            }
    
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(obj, pTarget);
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }
}
