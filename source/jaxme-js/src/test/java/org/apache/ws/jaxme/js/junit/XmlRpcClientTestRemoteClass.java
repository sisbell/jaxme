package org.apache.ws.jaxme.js.junit;

import java.io.IOException;
import java.net.MalformedURLException;


/** A test class, does nothing useful.
 */
public class XmlRpcClientTestRemoteClass {
	private static int sum = 0;
	/** Adds <code>pValue</code> to the sum.
	 */
    public void add(int pValue) {
    	sum += pValue;
    }
    /** Adds the given <code>pValues</code> to the sum.
     */
    public void add(int[] pValues) {
    	for (int i = 0;  i < pValues.length;  i++) {
    		add(pValues[i]);
        }
    }

    /** Converts the given string into an int and
     * adds it to the sum.
     */
    public void add(String pValue) {
    	add(Integer.parseInt(pValue));
    }

    /** Returns the sum.
     */
    public int getSum() {
        return sum;
    }

    /** Converts the sum into a string and returns it.
     * @throws IOException Never actually thrown, just to verify
     *   whether exceptions in the signature are handled properly.
     */
    public String getSumAsString() throws IOException, MalformedURLException {
    	return Integer.toString(sum);
    }
}