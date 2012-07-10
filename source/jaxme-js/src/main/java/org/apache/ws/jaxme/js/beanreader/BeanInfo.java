package org.apache.ws.jaxme.js.beanreader;

import org.apache.ws.jaxme.js.JavaQName;



/** Data container, which provides the required
 * information on a particular data bean.
 */
public interface BeanInfo {
	/** Returns the generated schemas target namespace.
	 */
	String getTargetNamespace();

	/** Returns the set of bean properties.
	 */
	BeanProperty[] getBeanProperties();

	/** Returns the suggested element name.
	 */
	String getElementName();

	/** Returns the bean class.
	 */
	JavaQName getType();

	/** Returns the bean classes super class.
	 */
	JavaQName getSuperType();
}
