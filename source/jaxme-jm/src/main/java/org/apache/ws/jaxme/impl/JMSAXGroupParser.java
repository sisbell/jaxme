package org.apache.ws.jaxme.impl;

import javax.xml.bind.ValidationEvent;

import org.apache.ws.jaxme.ValidationEvents;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/** The {@link javax.xml.parsers.SAXParser} is controlling
 * an internal stack of {@link JMSAXGroupParser} instances, one
 * for any nested sequence, choice, or all group being parsed.<br>
 * Note, that complex types with complex content are 
 */
public abstract class JMSAXGroupParser {
	protected abstract JMUnmarshallerHandlerImpl getHandler();

    /** Equivalent to
     * {@link org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)}.
     */
    public abstract boolean startElement(String pNamespaceURI, String pLocalName, String pQName,
                                         Attributes pAttrs) throws SAXException;
    /** Roughly equivalent to
     * {@link org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)}.
     * @param pResult The object that has been parsed.
     */
    public abstract void endElement(String pNamespaceURI, String pLocalName,
									String pQName, Object pResult) throws SAXException;

    /** Returns, whether the group contents are valid.
     */
    public abstract boolean isFinished();

    private boolean isEmpty(char[] pChars, int pOffset, int pLen) {
        for (int i = 0;  i < pLen;  i++) {
            if (!Character.isWhitespace(pChars[pOffset+i])) {
                return false;
            }
        }
        return true;
    }

	/** Returns, whether the group supports mixed content.
	 */
	public boolean isMixed() {
		return false;
	}

	/** Used for adding textual context. Valid only, if
	 * {@link #isMixed()} returns true.
	 * @param pChars Character buffer, as specified by
	 * {@link org.xml.sax.ContentHandler#characters(char[], int, int)}.
	 * @param pOffset Offset into buffer, as specified by
	 * {@link org.xml.sax.ContentHandler#characters(char[], int, int)}.
	 * @param pLen Length of relevant buffer part, as specified by
	 * {@link org.xml.sax.ContentHandler#characters(char[], int, int)}.
	 */
	public void addText(char[] pChars, int pOffset, int pLen) throws SAXException {
		if (!isEmpty(pChars, pOffset, pLen)) {
			if (pLen > 100) {
				pLen = 100;  // Keep the message approximately human readable.
			}
			getHandler().validationEvent(ValidationEvent.WARNING, "Unexpected non-whitespace characters: '" +
										 new String(pChars, pOffset, pLen) + "'",
										 ValidationEvents.EVENT_UNEXPECTED_TEXTUAL_CONTENTS,
										 null);
		}
	}
}
