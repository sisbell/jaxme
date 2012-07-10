package org.apache.ws.jaxme;


public class XMLConstants {
	/** <p>The XML Schema namespace:
     * <code>http://www.w3.org/2001/XMLSchema-instance</code>
     */
    public static final String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema-instance";

    /** <p>The attribute specifying a schema, which may
     * be used to validate the instance, if the schema
     * has a target namespace: <code>schemaLocation</code>.</p>
     * @see #XML_SCHEMA_URI
     * @see #XML_SCHEMA_NO_NS_ATTR
     */
    public static final String XML_SCHEMA_NS_ATTR = "schemaLocation";

    /** <p>The attribute specifying a schema, which may
     * be used to validate the instance, if the schema
     * doesn't have a namespace: <code>noNamespaceSchemaLocation</code>.</p>
     * @see #XML_SCHEMA_URI
     */
    public static final String XML_SCHEMA_NO_NS_ATTR = "noNamespaceSchemaLocation";
}
