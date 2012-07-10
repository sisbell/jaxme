package org.apache.ws.jaxme.xs.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;

import junit.framework.TestCase;


/**
 * Base class for deriving jaxmexs tests.
 */
public abstract class XSTestCase extends TestCase {
	private final String xsdDirectory = System.getProperty("jaxme.xs.xsdDirectory", "src/test/xsd");

	protected XsObjectFactory getXsObjectFactoryProxy(final XsObjectFactory pFactory,
			final EntityResolver pResolver) {
		InvocationHandler h = new InvocationHandler() {
			public Object invoke(Object pProxy, Method pMethod, Object[] pArgs)
			throws Throwable {
				if (Object.class.equals(pMethod.getDeclaringClass())) {
					return pMethod.invoke(pProxy, pArgs);
				}
				Object result = pMethod.invoke(pFactory, pArgs);
				if ("newXMLReader".equals(pMethod.getName())  &&  result instanceof XMLReader) {
					XMLReader xr = (XMLReader) result;
					xr.setEntityResolver(pResolver);
				}
				return result;
			}
		};
		Class[] classes;
		if (pFactory instanceof JAXBXsObjectFactory) {
			classes = new Class[]{JAXBXsObjectFactory.class};
		} else {
			classes = new Class[]{XsObjectFactory.class};
		}
		return (XsObjectFactory) Proxy.newProxyInstance(getClass().getClassLoader(), classes, h);
	}

	protected void parseSyntax(InputSource pSource) throws IOException, SAXException, ParserConfigurationException {
		newXSParser().parseSyntax(pSource);
	}

	protected XSSchema parseLogical(InputSource pSource) throws IOException, SAXException, ParserConfigurationException {
		return newXSParser().parse(pSource);
	}

	protected XSParser newXSParser() {
		XSParser parser = new XSParser();
		parser.setValidating(false);
		return parser;
	}

	protected JAXBParser newJAXBParser() {
		JAXBParser parser = new JAXBParser();
		parser.setValidating(false);
		return parser;
	}

	protected URL asURL(String s) throws MalformedURLException {
		File f = new File(xsdDirectory, s);
		if (!f.exists()) {
			throw new IllegalStateException("No such file: " + f.getPath());
		}
		return f.toURI().toURL();
	}

	protected InputSource asInputSource(String s) throws IOException {
		URL url = asURL(s);
		InputSource isource = new InputSource(url.openStream());
		isource.setSystemId(url.toExternalForm());
		return isource;
	}

	protected void parseSyntax(String s) throws IOException, SAXException, ParserConfigurationException {
		parseSyntax(asInputSource(s));
	}

	protected XSSchema parseLogical(String s) throws IOException, SAXException, ParserConfigurationException {
		return parseLogical(asInputSource(s));
	}
}
