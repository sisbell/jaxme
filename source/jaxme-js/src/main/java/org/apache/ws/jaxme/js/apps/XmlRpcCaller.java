package org.apache.ws.jaxme.js.apps;

import java.util.Vector;


/** Interface being implemented by the actual XML-RPC caller.
 * The main purpose of delegating this to an interface, is the
 * separation between generated classes and things like
 * authentication, server location, and so on.
 */
public interface XmlRpcCaller {
	/** Call the server, invoking the method named <code>pName</code>,
     * passing the arguments given by <code>pVector</code>.
	 */
    public Object xmlRpcCall(String pName, Vector pVector) throws Exception;
}
