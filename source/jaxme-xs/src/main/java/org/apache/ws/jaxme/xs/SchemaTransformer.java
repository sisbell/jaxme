package org.apache.ws.jaxme.xs;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/** A schema transformer is able to modify a schema, which
 * is being read. This is used, for example, to implement
 * the external JAXB binding files.
 */
public interface SchemaTransformer {
	/** Reads the given input source.
	 */
	public void parse(InputSource pSource, XMLReader pReader)
			throws ParserConfigurationException, SAXException, IOException;

	/** Returns the new input source. Called after
	 * {@link #parse(InputSource, XMLReader)}.
	 */
	public InputSource getTransformedInputSource();

	/** Returns the new XML reader. Called after
	 * {@link #parse(InputSource, XMLReader)}.
	 */
	public XMLReader getTransformedXMLReader();
}
