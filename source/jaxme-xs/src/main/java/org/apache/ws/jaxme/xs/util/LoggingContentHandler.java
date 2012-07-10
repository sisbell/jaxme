package org.apache.ws.jaxme.xs.util;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;



/**
 * A content handler, which may be used to log a stream of SAX events.
 */
public class LoggingContentHandler extends XMLFilterImpl implements LexicalHandler {
	private final TransformerHandler th;

	/**
	 * Creates a new instance, which is logging to the given
	 * output stream.
	 */
	public LoggingContentHandler(OutputStream pStream) throws SAXException {
		this(new StreamResult(pStream));
	}

	/**
	 * Creates a new instance, which is logging to the given writer.
	 * @param pResult
	 * @throws SAXException
	 */
	public LoggingContentHandler(Writer pWriter) throws SAXException {
		this(new StreamResult(pWriter));
	}

	private LoggingContentHandler(Result pResult) throws SAXException {
		try {
			th = ((SAXTransformerFactory) TransformerFactory.newInstance()).newTransformerHandler();
		} catch (TransformerException e) {
			throw new SAXException(e);
		}
		th.setResult(pResult);
	}

	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		th.characters(arg0, arg1, arg2);
		super.characters(arg0, arg1, arg2);
	}

	public void endDocument() throws SAXException {
		th.endDocument();
		super.endDocument();
	}

	public void endElement(String arg0, String arg1, String arg2) throws SAXException {
		th.endElement(arg0, arg1, arg2);
		super.endElement(arg0, arg1, arg2);
	}

	public void endPrefixMapping(String arg0) throws SAXException {
		th.endPrefixMapping(arg0);
		super.endPrefixMapping(arg0);
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
		th.ignorableWhitespace(arg0, arg1, arg2);
		super.ignorableWhitespace(arg0, arg1, arg2);
	}

	public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {
		th.notationDecl(arg0, arg1, arg2);
		super.notationDecl(arg0, arg1, arg2);
	}

	public void processingInstruction(String arg0, String arg1) throws SAXException {
		th.processingInstruction(arg0, arg1);
		super.processingInstruction(arg0, arg1);
	}

	public void skippedEntity(String arg0) throws SAXException {
		th.skippedEntity(arg0);
		super.skippedEntity(arg0);
	}

	public void startDocument() throws SAXException {
		th.startDocument();
		super.startDocument();
	}

	public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
		th.startElement(arg0, arg1, arg2, arg3);
		super.startElement(arg0, arg1, arg2, arg3);
	}

	public void startPrefixMapping(String arg0, String arg1) throws SAXException {
		th.startPrefixMapping(arg0, arg1);
		super.startPrefixMapping(arg0, arg1);
	}

	public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {
		th.unparsedEntityDecl(arg0, arg1, arg2, arg3);
		super.unparsedEntityDecl(arg0, arg1, arg2, arg3);
	}

	public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
		th.comment(arg0, arg1, arg2);
	}

	public void endCDATA() throws SAXException {
		th.endCDATA();
	}

	public void endDTD() throws SAXException {
		th.endDTD();
	}

	public void endEntity(String arg0) throws SAXException {
		th.endEntity(arg0);
	}

	public void startCDATA() throws SAXException {
		th.startCDATA();
	}

	public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
		th.startDTD(arg0, arg1, arg2);
	}

	public void startEntity(String arg0) throws SAXException {
		th.startEntity(arg0);
	}
}
