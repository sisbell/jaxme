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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;

import org.apache.ws.jaxme.ValidationEvents;
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
 * which parses an "all" group.
 */
public class AllHandlerSG extends GroupHandlerSG {
	private JavaField childNumField;

	/** Creates a new instance, which generates a handler for
	 * the complex type <code>pTypeSG</code> by adding methods
	 * and fields to the Java class <code>pJs</code>.
	 */
	public AllHandlerSG(ComplexTypeSG pType, JavaSource pJs)
			throws SAXException {
		super(pType, pJs);
	}

	AllHandlerSG(GroupHandlerSG pOuterHandler, ComplexTypeSG pType,
				 ParticleSG pParticle, JavaSource pJs)
			throws SAXException {
		super(pOuterHandler, pType, pParticle, pJs);
	}

	protected int getState(int pParticleNum) { return pParticleNum; }

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

	protected void acceptParticle(JavaMethod pJm, int pNum) throws SAXException {
		pJm.addIf(getStateField(), "[" + pNum, "]");
		ParticleSG particle = particles[pNum];
		if (particle.isElement()) {
			pJm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".WARNING, ",
						JavaSource.getQuoted("The element " + particle.getObjectSG().getName() +
										     " has already been defined."),
					    ", ", ValidationEvents.class, ".EVENT_ALL_GROUP_REUSE, null);");
		} else if (particle.isGroup()) {
			pJm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".WARNING, ",
						JavaSource.getQuoted("The group " + GroupUtil.getGroupName(particle.getGroupSG()) +
											 " has already been defined."),
					    ", ", ValidationEvents.class, ".EVENT_ALL_GROUP_REUSE, null);");
		} else if (particle.isWildcard()) {
			throw new IllegalStateException("TODO: Add support for wildcards.");
		} else {
			throw new IllegalStateException("Invalid particle type");
		}
		pJm.addEndIf();
		pJm.addLine(getChildNumField(), " = " + pNum + ";");
		pJm.addLine(getStateField(), "[" + pNum, "] = true;");
	}

	public JavaMethod newStartElementMethod() throws SAXException {
		JavaMethod result = super.newStartElementMethod();
		LocalJavaField unmarshallerHandler = result.newJavaField(JMUnmarshallerHandlerImpl.class);
		unmarshallerHandler.addLine("getHandler()");
		handleStartElementStates(unmarshallerHandler, result, 0, particles.length-1);
		result.addLine("return false;");
		return result;
	}

	public JavaMethod newIsFinishedMethod() throws SAXException {
		JavaMethod result = super.newIsFinishedMethod();
		List requiredParticles = new ArrayList();
		for (int i = 0;  i < particles.length;  i++) {
			if (isRequiredParticle(particles[i])) {
				if (requiredParticles.size() > 0) {
					requiredParticles.add(" && ");
				}
				requiredParticles.add(new Object[]{getStateField(),
												   "[" + i + "]"});
			}
		}
		if (requiredParticles.size() == 0) {
			result.addLine("return true;");
		} else {
			result.addLine("return ", requiredParticles, ";");
		}
		return result;
	}

	protected JavaField newStateField() throws SAXException {
		JavaField jf = getJavaSource().newJavaField("__state", boolean[].class, JavaSource.PRIVATE);
		jf.addLine("new ", boolean.class, "[" + particles.length + "]");
		JavaComment jc = jf.newComment();
		jc.addLine("This array indicates the state of the group. For any");
		jc.addLine("possible child, the corresponding boolean value is set,");
		jc.addLine("if the child is parsed.");
		jc.addLine("If the same child occurs again, and the childs boolean");
		jc.addLine("value is true, then an exception is thrown.");
		jc.addLine("These are the indices, to which the child elements are");
		jc.addLine("mapped:");
		for (int i = 0;  i < particles.length;  i++) {
			ParticleSG particle = particles[i];
			if (particle.isGroup()) {
				GroupSG group = particle.getGroupSG();
				if (group.isGlobal()) {
					jc.addLine(" " + i + " = The nested group " + group.getName());
				} else {
					jc.addLine(" " + i + " = An anonymous nested group.");
				}
			} else if (particle.isElement()) {
				jc.addLine(" " + i + " = The child element " + particle.getObjectSG().getName());
			} else if (particle.isWildcard()) {
				throw new IllegalStateException("TODO: Add support for wildcards.");
			} else {
				throw new IllegalStateException("Invalid particle type.");
			}
		}
		return jf;
	}
}