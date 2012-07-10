package org.apache.ws.jaxme.js.apps;

import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;


/** Attempts to find a given instance of
 * {@link org.apache.ws.jaxme.js.JavaSource}.
 * This is used, for example, in the following case:
 * <ul>
 *   <li>A generator would like to know all methods
 *     of a given class.</li>
 *   <li>The class is derived from another class.</li>
 *   <li>The super classes methods should be treated,
 *     as if they were usual methods.</li>
 * </ul>
 */
public interface JavaSourceResolver {
	/** Queries for an instance of {@link JavaSource}
     * with the fully qualified class name <code>pClassName</code>.
	 */
    public JavaSource getJavaSource(JavaQName pQName);
}
