package org.apache.ws.jaxme.js.beanreader;

import javax.xml.namespace.QName;

import org.apache.ws.jaxme.js.JavaQName;


/** An instance of {@link BeanProperty} is being mapped
 * to an attribute, or element, in the schema.
 */
public interface BeanProperty {
	/** Returns the properties Java type.
	 */
	JavaQName getType();
	/** Returns the properties XS data type.
	 */
	QName getXsType();
	/** Returns the property name.
	 */
	String getName();
}
