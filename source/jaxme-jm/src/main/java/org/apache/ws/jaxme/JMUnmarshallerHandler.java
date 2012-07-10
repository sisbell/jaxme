package org.apache.ws.jaxme;

import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.bind.UnmarshallerHandler;

import org.apache.ws.jaxme.util.NamespaceSupport;
import org.xml.sax.Locator;


/** JaxMe's private extension of
 * {@link javax.xml.bind.UnmarshallerHandler}.
 */
public interface JMUnmarshallerHandler extends UnmarshallerHandler {
    /** Returns the {@link JMUnmarshaller}, which created this
     * handler.
     */
    public JMUnmarshaller getJMUnmarshaller();

	/** Sets an observer, which will be notified, when the element has
	 * been parsed.
	 */
	public void setObserver(Observer pObserver);

	/** Returns the observer, which will be notified, when the element has
	 * been parsed.
	 */
	public Observer getObserver();

	/** Returns an instance of {@link NamespaceSupport}.
	 */
	public NamespaceSupport getNamespaceSupport();

	/** Returns an instance of {@link Locator}, if it has
	 * been set, or null.
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public Locator getDocumentLocator();

	/** Returns an instance of {@link DatatypeConverterInterface}.
	 */
	public DatatypeConverterInterface getDatatypeConverter();

	/** Returns the current level of nested elements.
	 */
	public int getLevel();
}
