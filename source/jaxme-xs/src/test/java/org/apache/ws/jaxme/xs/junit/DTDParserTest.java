package org.apache.ws.jaxme.xs.junit;

import org.apache.ws.jaxme.xs.util.DTDParser;


/** A unit test for the
 *  {@link org.apache.ws.jaxme.xs.util.DTDParser}.
 */
public class DTDParserTest extends XSTestCase {
    /** Parses the file XMLSchema.dtd.
     */
	public void testXMLSchemaDtd() throws Exception {
        new DTDParser().parse(asInputSource("XMLSchema.dtd"));
    }
}
