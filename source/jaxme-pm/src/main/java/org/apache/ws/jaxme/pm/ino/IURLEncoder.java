package org.apache.ws.jaxme.pm.ino;

import java.io.UnsupportedEncodingException;


/** This interface allows to choose between JDK 1.3
 * and JDK 1.4 dependent classes.
 */
public interface IURLEncoder {
    /** Encodes the string <code>pValue</code>.
     */
    public String encode(String pValue) throws UnsupportedEncodingException;
}
