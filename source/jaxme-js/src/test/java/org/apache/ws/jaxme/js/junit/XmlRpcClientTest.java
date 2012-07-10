package org.apache.ws.jaxme.js.junit;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Vector;

import org.apache.ws.jaxme.js.apps.XmlRpcCaller;
import org.apache.ws.jaxme.js.junit.xmlrpcclient.Dispatcher;

import junit.framework.TestCase;


/** A unit test for the
 * {@link org.apache.ws.jaxme.js.apps.XmlRpcClientGenerator}.
 */
public class XmlRpcClientTest extends TestCase {
	private final XmlRpcCaller caller = new XmlRpcCaller(){
        Dispatcher dispatcher = new Dispatcher();
        public Object xmlRpcCall(String pName, Vector pVector) {
            try {
                return dispatcher.execute(pName, pVector);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        }        
    };
    private final XmlRpcClientTestRemoteClass server = new XmlRpcClientTestRemoteClass();
    private final org.apache.ws.jaxme.js.junit.xmlrpcclient.XmlRpcClientTestRemoteClass client
        = new org.apache.ws.jaxme.js.junit.xmlrpcclient.XmlRpcClientTestRemoteClass(caller);

    /** Creates a new instance with the given name.
	 */
	public XmlRpcClientTest(String pArg0) {
		super(pArg0);
	}

    private void checkSum(int pSum) {
    	assertEquals(pSum, server.getSum());
        assertEquals(pSum, client.getSum());
        String sumAsString = Integer.toString(pSum);
        try {
        	assertEquals(sumAsString, server.getSumAsString());
        } catch (IOException e) {
            // This IOException is never actually thrown.
            // However, the try .. catch clause ensures, that
            // it is present in the signature of server.getSumAsString().
        }
        try {
        	assertEquals(sumAsString, client.getSumAsString());
        } catch (IOException e) {
            // This IOException is never actually thrown.
            // However, the try .. catch clause ensures, that
            // it is present in the signature of client.getSumAsString().
        }
    }

    /** Creates a dispatcher and uses it to run the
     * various methods.
	 */
    public void testXmlRpcClient() {
        checkSum(0);
        server.add(1);
        checkSum(1);
        client.add(1);
        checkSum(2);
        server.add(new int[]{2,3,2});
        checkSum(9);
        client.add(new int[]{0,-1,0,2});
        checkSum(10);
        server.add("4");
        checkSum(14);
        client.add("-8");
        checkSum(6);
    }    
}
