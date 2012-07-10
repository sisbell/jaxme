/*
 * Copyright 2003,2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package org.apache.ws.jaxme;

/** <p>List of error codes, being used in validation events.</p>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: ValidationEvents.java 232552 2005-08-14 00:34:05Z jochen $
 */
public interface ValidationEvents {
	/** More than one alternative in a choice group have
	 * been used.
	 */
	public static final String EVENT_CHOICE_GROUP_REUSE = "JM_EVENT_CHOICE_GROUP_REUSE";
	
	/** <p>A processing instruction was found. JaxMe doesn't know
	 * how to handle processing instruction, thus they are treated
	 * as a validation event, possibly throwing an exception.</p>
	 * <p>This behaviour is questionable. It may very well be changed
	 * at a later time, if another way of handling processing
	 * instructions is defined. However, if we throw an event now,
	 * the possible change is upwards compatible.</p>
	 */
	public static final String EVENT_PROCESSING_INSTRUCTION = "JM_EVENT_PROCESSING_INSTRUCTION";
	
	/** <p>A skipped entity was found. JaxMe doesn't know
	 * how to handle skipped entities, thus they are treated
	 * as a validation event, possibly throwing an exception.</p>
	 * <p>This behaviour is questionable. It may very well be changed
	 * at a later time, if another way of handling skipped
	 * entities is defined. However, if we throw an event now,
	 * the possible change is upwards compatible.</p>
	 */
	public static final String EVENT_SKIPPED_ENTITY = "JM_EVENT_SKIPPED_ENTITY";
	
	/** <p>Unexpected textual contents have been found. For example,
	 * a sequence must not have embedded text, because it would have
	 * mixed content otherwise. Textual content is ignored, if it
	 * consists of whitespace characters only.</p>
	 */
	public static final String EVENT_UNEXPECTED_TEXTUAL_CONTENTS = "JM_EVENT_UNEXPECTED_TEXTUAL_CONTENTS";
	
	/** <p>An unexpected child was found in an atomic element or in a
	 * complex element with simple content.</p>
	 */
	public static final String EVENT_UNEXPECTED_CHILD_ELEMENT = "JM_EVENT_UNEXPECTED_CHILD_ELEMENT";
	
	/**A childs of an all group was used more than once.
	 */
	public static final String EVENT_ALL_GROUP_REUSE = "JM_EVENT_ALL_GROUP_REUSE";
	
	/** <p>A child element was not expected at this place. For example,
	 * in the case of a choice, this may indicate that more than one
	 * of the possible elements have been found. In the case of a
	 * sequence, this can indicate a mismatch in the order of the
	 * child elements.</p>
	 */
	public static final String EVENT_UNEXPECTED_CHILD_STATE = "JM_EVENT_UNEXPECTED_CHILD_STATE";
	
	/** <p>A complex elements child is unknown. A possible reason
	 * is an error in the child elements name.</p>
	 */
	public static final String EVENT_ADDITIONAL_CHILD_ELEMENT = "JM_EVENT_ADDITIONAL_CHILD_ELEMENT";
	
	/** <p>An element occurred more than expected. In other words,
	 * the elements <code>maxOccurs</code> facet was violated.</p>
	 */
	public static final String EVENT_MULTIPLE_OCCURRENCIES = "JM_EVENT_MULTIPLE_OCCURRENCIES";
	
	/** <p>The root elements type was wrong.</p>
	 */
	public static final String EVENT_WRONG_ROOT_ELEMENT = "JM_EVENT_WRONG_ROOT_ELEMENT";
	
	/** <p>An element or attribute value was invalid. For example, in the
	 * case of a dateTime instance, this may indicate an unparseable date.</p>
	 */
	public static final String EVENT_ILLEGAL_VALUE = "JM_EVENT_ILLEGAL_VALUE";
	
	/** <p>An attribute was unknown.</p>
	 */
	public static final String EVENT_UNKNOWN_ATTRIBUTE = "JM_EVENT_UNKNOWN_ATTRIBUTE";
	
	/** <p>An attribute with an invalid namespace was detected in an
	 * element with an "anyAttribute" declaration.</p>
	 */
	public static final String EVENT_UNKNOWN_ANY_ATTRIBUTE = "JM_EVENT_UNKNOWN_ANY_ATTRIBUTE";
	
	/** Some of an elements child are missing.
	 */
	public static final String EVENT_PREMATURE_END_ELEMENT = "JM_EVENT_PREMATURE_END_ELEMENT";

	/** Ad ID attribute was declared twice.
	 */
	public static final String EVENT_DUPLICATE_ID = "JM_EVENT_DUPLICATE_ID";

    /** An IDREF attributes value was never declared as ID.
     */
    public static final String EVENT_IDREF_UNDECLARED = "JM_EVENT_IDREF_UNDECLARED";
}
