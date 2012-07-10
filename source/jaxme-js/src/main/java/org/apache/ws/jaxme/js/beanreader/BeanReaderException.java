package org.apache.ws.jaxme.js.beanreader;


/** An exception, which is being thrown, if an error
 * was encountered while running the BeanReader.
 */
public class BeanReaderException extends Exception {
	private static final long serialVersionUID = 5230551895989267844L;

	/** Creates a new instance with the given message
	 * and cause.
	 */
	public BeanReaderException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	/** Creates a new instance with the given message
	 * and no cause.
	 */
	public BeanReaderException(String pMessage) {
		super(pMessage);
	}

	/** Creates a new instance with the given cause.
	 */
	public BeanReaderException(Throwable pCause) {
		super(pCause);
	}
}
