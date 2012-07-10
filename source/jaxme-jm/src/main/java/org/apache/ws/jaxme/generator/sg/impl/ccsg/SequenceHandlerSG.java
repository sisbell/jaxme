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

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
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
 * which parses a sequence.
 */
public class SequenceHandlerSG extends GroupHandlerSG {
	/** Creates a new instance, which generates a handler for
	 * the complex type <code>pTypeSG</code> by adding methods
	 * and fields to the Java class <code>pJs</code>.
	 */
	public SequenceHandlerSG(ComplexTypeSG pTypeSG, JavaSource pJs)
			throws SAXException {
		super(pTypeSG, pJs);
	}

	SequenceHandlerSG(GroupHandlerSG pOuterHandler, ComplexTypeSG pTypeSG,
					  ParticleSG pParticle, JavaSource pJs)
			throws SAXException {
		super(pOuterHandler, pTypeSG, pParticle, pJs);
	}

	protected DirectAccessible getEndElementState() throws SAXException {
		return getStateField();
	}

	protected int getState(int pNum) {
		return pNum+1;
	}

	private int fromState(int pState) {
		return pState-1;
	}

	protected JavaField newStateField() throws SAXException {
		JavaField jf = getJavaSource().newJavaField("__state", int.class, JavaSource.PRIVATE);
		JavaComment jc = jf.newComment();
		jc.addLine("The current state. The following values are valid states:");
		jc.addLine(" 0 = Before parsing the element");
		for (int i = 0;  i < particles.length;  i++) {
			ParticleSG particle = particles[i];
			if (particle.isGroup()) {
				GroupSG group = particle.getGroupSG();
				if (group.isGlobal()) {
					jc.addLine(" " + getState(i) + " = While parsing the nested group " + group.getName());
				} else {
					jc.addLine(" " + getState(i) + " = While parsing the nested group " + GroupUtil.getGroupName(group));
				}
			} else if (particle.isElement()) {
				jc.addLine(" " + getState(i) + " = While or after parsing the child element " + particle.getObjectSG().getName());
			} else if (particle.isWildcard()) {
				throw new IllegalStateException("TODO: Add support for wildcards.");
			} else {
				throw new IllegalStateException("Invalid particle type.");
			}
		}
		
		return jf;
	}


	protected void acceptParticle(JavaMethod pJm, int pNum) throws SAXException {
		pJm.addLine(getStateField(), " = " + getState(pNum) + ";");
	}

	public JavaMethod newStartElementMethod() throws SAXException {
		JavaMethod result = super.newStartElementMethod();
		LocalJavaField unmarshallerHandler = result.newJavaField(JMUnmarshallerHandlerImpl.class);
		unmarshallerHandler.addLine("getHandler()");
		result.addSwitch(getStateField());
		result.addCase(new Integer(0));
		handleStartElementStates(unmarshallerHandler,
								 result, getFirstValidParticle(0),
								 getLastValidParticle(0));
		result.addBreak();
		for (int i = 0;  i < particles.length;  i++) {
			int state = getState(i);
			result.addCase(new Integer(state));
			handleStartElementStates(unmarshallerHandler,
									 result, getFirstValidParticle(state),
									 getLastValidParticle(state));
			result.addBreak();
		}
		result.addDefault();
		result.addThrowNew(IllegalStateException.class,
						   JavaSource.getQuoted("Invalid state: "),
						   " + ", getStateField());
		result.addEndSwitch();
		result.addLine("return false;");
		return result;
	}

	/** Assuming, we are currently in state <code>pState</code>,
	 * returns the index of the first valid particle. Returns
	 * -1, if there is no valid particle.
	 */
	private int getFirstValidParticle(int pState) {
		if (pState == 0) {
			return 0;
		} else {
			int i = fromState(pState);
			ParticleSG particle = particles[i];
			if (particle.isMultiple()) {
				return i;
			} else if (i+1 < particles.length) {
				return i+1;
			} else {
				return -1;
			}
		}
	}

	/** Assuming, we are currently in state <code>pState</code>,
	 * returns the index of the last valid particle. Returns
	 * -1, if there is no valid particle.
	 */
	private int getLastValidParticle(int pState) throws SAXException {
		int lastParticle;
		if (pState == 0) {
			lastParticle = 0;
		} else {
			lastParticle = fromState(pState)+1;
			if (lastParticle >= particles.length) {
				return fromState(pState);
			}
		}
		while (lastParticle < particles.length-1) {
			ParticleSG particle = particles[lastParticle];
			if (isRequiredParticle(particle)) {
				break;
			} else {
				++lastParticle;
			}
		}
		return lastParticle;
	}

	public JavaMethod newIsFinishedMethod() throws SAXException {
		JavaMethod result = super.newIsFinishedMethod();
		result.addSwitch(getStateField());
		boolean allOptional = true;
		for (int i = particles.length-1;  i >= 0;  i--) {
			ParticleSG particle = particles[i];
			result.addCase(new Integer(getState(i)));
			if (isRequiredParticle(particle)) {
				allOptional = false;
				break;
			}
		}
		if (allOptional) {
			result.addCase(new Integer(0));
		}
		result.addLine("return true;");
		result.addDefault();
		result.addLine("return false;");
		result.addEndSwitch();
		return result;
	}
}