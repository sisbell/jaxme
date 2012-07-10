package org.apache.ws.jaxme.xs.jaxb;


/** Interface of an element providing a <code>jaxb:javaType</code>
 * customization.
 */
public interface JAXBJavaTypeOwner {
	/** Returns the elements <code>jaxb:javaType</code> customization,
     * if any, or null.
	 */
    public JAXBJavaType getJAXBJavaType();
}
