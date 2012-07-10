package org.apache.ws.jaxme.js.pattern;

import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;


/** The <code>Reflector</code> obtains informations on a certain
 * class by converting it into an instance of {@link JavaSource}.
 */
public interface Reflector {
	/** Returns the converted information.
	 */
    public JavaSource getJavaSource(JavaSourceFactory pFactory) throws Exception;
}
