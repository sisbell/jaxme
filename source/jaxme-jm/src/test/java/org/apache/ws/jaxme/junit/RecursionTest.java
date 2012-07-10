package org.apache.ws.jaxme.junit;

import org.apache.ws.jaxme.test.recursion.Attribute;
import org.apache.ws.jaxme.test.recursion.DirectEmployee;
import org.apache.ws.jaxme.test.recursion.IndirectEmployee;
import org.apache.ws.jaxme.test.recursion.PotEmployee;


/**
 * Tests for unmarshalling from a string, them marshalling to another
 * string and unmarshalling back to another object for comparison.
 * Three different recursive schemas are used:
 * <ol>
 *   <li>Direct recursion: A node is recurring directly under itself.</li>
 *   <li>Indirect recursion: A node is recurring as a child of another
 *     node within itself.</li>
 *   <li>Potential direct/indirect recursion: A node is directly
 *     recurring/indirectly recurring under as one of the choices under
 *     itself.</li>
 * </ol>
 */
public class RecursionTest extends BaseTestCase {
    /** Creates a new instance with the given name.
     */
    public RecursionTest(String pName) { super(pName); }

    /** Tests indirect recursion: The recursing element is a child
     * elements child.
     */
    public void testIndirectRecursion() throws Exception {
	    final String xml =
	        "<rec:IndirectEmployee name=\"Bill\" id=\"1\" xmlns:rec=\"http://ws.apache.org/jaxme/test/recursion\">\n" +
	        "  <rec:Manager>\n" +
	        "    <rec:Employees name=\"Bud\" id=\"2\">\n" +
	        "      <rec:Manager>\n" +
	        "        <rec:Employees name=\"Buck\" id=\"3\">\n" +
	        "          <rec:Manager/>\n" +
	        "        </rec:Employees>\n" +
	        "      </rec:Manager>\n" +
	        "    </rec:Employees>\n" +
	        "  </rec:Manager>\n" +
	        "</rec:IndirectEmployee>";

		unmarshalMarshalUnmarshal(IndirectEmployee.class, xml);
	}

    /** Tests direct recursion: The recursing element is an
     * immediate child.
     */
    public void testDirectRecursion() throws Exception {
	    final String xml =
	        "<rec:DirectEmployee name=\"John\" id=\"4\" xmlns:rec=\"http://ws.apache.org/jaxme/test/recursion\">\n" +
	        "  <rec:Employees name=\"Jack\" id=\"5\">\n" +
	        "    <rec:Employees name=\"Jim\" id=\"6\">\n" +
	        "      <rec:Employees name=\"James\" id=\"7\"/>\n" +
	        "    </rec:Employees>\n" +
	        "  </rec:Employees>\n" +
	        "</rec:DirectEmployee>";
		unmarshalMarshalUnmarshal(DirectEmployee.class, xml);
	}

    /** Tests potential recursion: The recursing element may
     * be a direct or an indirect child.
     */
    public void testPotentialRecursion() throws Exception {
	    final String xml1 =
	        "<rec:PotEmployee name=\"John\" id=\"1\" xmlns:rec=\"http://ws.apache.org/jaxme/test/recursion\">\n" +
	        "  <rec:Employees name=\"Francis\" id=\"3\"/>\n" +
	        "</rec:PotEmployee>";
		unmarshalMarshalUnmarshal(PotEmployee.class, xml1);
		final String xml2 =
	        "<rec:PotEmployee name=\"John\" id=\"1\" xmlns:rec=\"http://ws.apache.org/jaxme/test/recursion\">\n" +
	        "  <rec:Manager>\n" +
		    "    <rec:Employees name=\"Francis\" id=\"3\"/>\n" +
		    "  </rec:Manager>\n" +
		    "</rec:PotEmployee>";
		unmarshalMarshalUnmarshal(PotEmployee.class, xml2);
	}

    /** Tests recursion using element references.
     */
    public void testElementRecursion() throws Exception {
    	final String xml =
            "<rec:Attribute name=\"Sam\" id=\"1\" xmlns:rec=\"http://ws.apache.org/jaxme/test/recursion\">\n" +
            "  <rec:AttributeList>\n" +
            "    <rec:Attribute name=\"Sonny\" id=\"2\">\n" +
            "      <rec:AttributeList/>\n" +
            "    </rec:Attribute>\n" +
            "    <rec:Attribute name=\"Severine\" id=\"3\">\n" +
            "      <rec:AttributeList/>\n" +
            "    </rec:Attribute>\n" +
            "  </rec:AttributeList>\n" +
            "</rec:Attribute>";
        unmarshalMarshalUnmarshal(Attribute.class, xml);
    }
}
