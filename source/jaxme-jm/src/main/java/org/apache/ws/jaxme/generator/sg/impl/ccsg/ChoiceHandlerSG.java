/*
 * Copyright 2005  The Apache Software Foundation
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
package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import javax.xml.bind.ValidationEvent;

import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.impl.JMUnmarshallerHandlerImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaComment;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.xml.sax.SAXException;


/** Creates an instance of
 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser},
 * or {@link org.apache.ws.jaxme.impl.JMSAXGroupParser},
 * which parses a choice group.
 */
public class ChoiceHandlerSG extends GroupHandlerSG {
	private JavaField childNumField;

	/** Creates a new instance, which generates a handler for
	 * the complex type <code>pTypeSG</code> by adding methods
	 * and fields to the Java class <code>pJs</code>.
	 */
	public ChoiceHandlerSG(ComplexTypeSG pType, JavaSource pJs)
			throws SAXException {
		super(pType, pJs);
	}

	ChoiceHandlerSG(GroupHandlerSG pOuterHandler, ComplexTypeSG pType,
					ParticleSG pParticle, JavaSource pJs)
			throws SAXException {
		super(pOuterHandler, pType, pParticle, pJs);
	}

	protected int getState(int pParticleNum) { return pParticleNum; }

	protected void acceptParticle(JavaMethod pJm, int pNum) throws SAXException {
		pJm.addIf(getStateField());
		ParticleSG particle = particles[pNum];
		pJm.addIf(getChildNumField(), " != " + pNum);
		pJm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".WARNING, ",
					JavaSource.getQuoted("Multiple different particles present in a choive group."),
					", ", ValidationEvents.class, ".EVENT_CHOICE_GROUP_REUSE, null);");
		if (!particles[pNum].isMultiple()) {
			pJm.addElse();
			if (particle.isElement()) {
				pJm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".WARNING, ",
							JavaSource.getQuoted("The element " + particle.getObjectSG().getName() +
											     " has already been defined."),
							", ", ValidationEvents.class, ".EVENT_CHOICE_GROUP_REUSE, null);");
			} else if (particle.isGroup()) {
				pJm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".WARNING, ",
							JavaSource.getQuoted("The group " + GroupUtil.getGroupName(particle.getGroupSG()) +
												 " has already been defined."),
						    ", ", ValidationEvents.class, ".EVENT_CHOICE_GROUP_REUSE, null);");
			} else if (particle.isWildcard()) {
				throw new IllegalStateException("TODO: Add support for wildcards.");
			} else {
				throw new IllegalStateException("Invalid particle type");
			}
		}
		pJm.addEndIf();
		pJm.addEndIf();
		pJm.addLine(getStateField(), " = true;");
		pJm.addLine(getChildNumField(), " = " + pNum + ";");
	}

	public JavaMethod newStartElementMethod() throws SAXException {
		JavaMethod result = super.newStartElementMethod();
		LocalJavaField unmarshallerHandler = result.newJavaField(JMUnmarshallerHandlerImpl.class);
		unmarshallerHandler.addLine("getHandler()");
		handleStartElementStates(unmarshallerHandler, result, 0, particles.length-1);
		result.addLine("return false;");
		return result;
	}

	private JavaField getChildNumField() {
		if (childNumField == null) {
			childNumField = getJavaSource().newJavaField("__childNum", int.class, JavaSource.PRIVATE);
			JavaComment jc = childNumField.newComment();
			jc.addLine("Index of the particle being currently parsed");
		}
		return childNumField;
	}

	protected DirectAccessible getEndElementState() throws SAXException {
		return getChildNumField();
	}

	public JavaMethod newIsFinishedMethod() throws SAXException {
		JavaMethod result = super.newIsFinishedMethod();
		Object o = isRequiredParticle(particle) ? (Object) getStateField() : "true";
		result.addLine("return ", o, ";");
		return result;
	}

	protected JavaField newStateField() throws SAXException {
		JavaField jf = getJavaSource().newJavaField("__state", boolean.class, JavaSource.PRIVATE);
		JavaComment jc = jf.newComment();
		jc.addLine("Will be set to true, if the first child is parsed.");
		jc.addLine("It is an error, if another child is parsed, and the");
		jc.addLine("fields value is true.");
		return jf;
	}
}
