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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.PropertySGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.PropertySGChainImpl;
import org.apache.ws.jaxme.generator.sg.impl.PropertySGImpl;
import org.apache.ws.jaxme.impl.JMSAXElementParser;
import org.apache.ws.jaxme.impl.JMSAXGroupParser;
import org.apache.ws.jaxme.impl.JMUnmarshallerHandlerImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;


/** An instance of <code>GroupHandlerSG</code> is
 * responsible for creating an instance of
 * {@link org.apache.ws.jaxme.impl.JMSAXGroupParser},
 * or {@link org.apache.ws.jaxme.impl.JMSAXElementParser}.<br>
 * The {@link org.apache.ws.jaxme.generator.sg.ComplexContentSG}
 * creates a <code>GroupHandlerSG</code> for any group,
 * contained in the element.
 */
public abstract class GroupHandlerSG extends HandlerSGImpl {
	protected final ComplexContentSG ccSG;
	protected final ParticleSG particle;
	protected final GroupSG group;
	protected final ParticleSG[] particles;
	private final Map groups = new HashMap();
	private JavaField stateField;
	private final boolean isMixed;
	protected final GroupHandlerSG outerHandler;

	/** Creates a new instance, which generates a handler for
	 * the complex type <code>pTypeSG</code> by adding methods
	 * and fields to the Java class <code>pJs</code>.
	 */
	protected GroupHandlerSG(ComplexTypeSG pType, JavaSource pJs) throws SAXException {
		super(pType, pJs);
		outerHandler = this;
		ccSG = pType.getComplexContentSG();
		particle = ccSG.getRootParticle();
		group = particle.getGroupSG();
		particles = group.getParticles();
		isMixed = ccSG.isMixed();
		findGroups(particles);
	}

	/** Creates a new instance, which generates a handler for
	 * the group <code>pGroupSG</code> by adding methods and
	 * fields to the Java class <code>pJs</code>.
	 */
	protected GroupHandlerSG(GroupHandlerSG pOuterHandler, ComplexTypeSG pType,
							 ParticleSG pParticle, JavaSource pJs) throws SAXException {
		super(pType, pJs);
		outerHandler = pOuterHandler;
		if (!pJs.isInnerClass()) {
			throw new IllegalStateException("Expected inner class");
		}
		ccSG = null;
		particle = pParticle;
		group = particle.getGroupSG();
		particles = group.getParticles();
		isMixed = pType.getComplexContentSG().isMixed();
		findGroups(particles);
	}

	protected abstract JavaField newStateField() throws SAXException;

	protected JavaField getStateField() throws SAXException {
		if (stateField == null) {
			stateField = newStateField();
		}
		return stateField;
	}

	protected GroupHandlerSG getGroupHandlerSG(GroupSG pGroup) {
		return (GroupHandlerSG) groups.get(pGroup);
	}

	private GroupHandlerSG newGroupHandlerSG(ParticleSG pParticle, String pName) throws SAXException {
		JavaSource js = getJavaSource();
		JavaInnerClass jic = js.newJavaInnerClass(pName, JavaSource.PUBLIC);
		jic.addExtends(JMSAXGroupParser.class);
		GroupSG group = pParticle.getGroupSG();
		if (group.isSequence()) {
			return new SequenceHandlerSG(outerHandler, ctSG, pParticle, jic);
		} else if (group.isChoice()) {
			return new ChoiceHandlerSG(outerHandler, ctSG, pParticle, jic);
		} else if (group.isAll()) {
			return new AllHandlerSG(outerHandler, ctSG, pParticle, jic);
		} else {
			throw new IllegalStateException("Invalid group type");
		}
	}

	private boolean isValidInnerClassName(JavaSource pSource, String pName) {
		boolean isInnerClass = pSource.isInnerClass();
		final String name;
		if (isInnerClass) {
			JavaInnerClass jic = (JavaInnerClass) pSource;
			if (!isValidInnerClassName(jic.getOuterClass(), pName)) {
				return false;
			}
			name = jic.getQName().getInnerClassName();
		} else {
			name = pSource.getQName().getClassName();
		}
		if (name.equals(pName)) {
			return false;
		}
		if (pSource.getInnerClass(pName) != null) {
			return false;
		}
		return true;
	}

	private GroupHandlerSG newGroupHandlerSG(ParticleSG pParticle) throws SAXException {
		JavaSource js = getJavaSource();
		String name = GroupUtil.getGroupName(pParticle.getGroupSG());
		for (int i = 0;  ;  i++) {
			String n = name;
			if (i > 0) {
				name += i;
			}
			n += "Handler";
			if (isValidInnerClassName(js, n)) {
				GroupHandlerSG result = newGroupHandlerSG(pParticle, n);
				result.newGetHandlerMethod(js);
				return result;
			}
		}
	}

	public JavaMethod newAddAttributeMethod() throws SAXException {
		if (ccSG == null) {
			return null;
		} else {
			return super.newAddAttributeMethod();
		}
	}

	private void findGroups(ParticleSG[] pParticles) throws SAXException {
		for (int i = 0;  i < pParticles.length;  i++) {
			ParticleSG particle = pParticles[i];
			if (particle.isGroup()) {
				GroupSG group = particle.getGroupSG();
				if (!groups.containsKey(group)) {
					GroupHandlerSG handler = newGroupHandlerSG(particle);
					groups.put(group, handler);
				}
			} else if (particle.isElement()) {
				TypeSG tSG = particle.getObjectSG().getTypeSG();
				if (tSG.isComplex()  &&  !tSG.isGlobalClass()) {
					tSG.getComplexTypeSG().getXMLHandler(outerHandler.getJavaSource());
				}
			}
		}
	}

	protected boolean isRequiredParticle(ParticleSG particleSG) throws SAXException {
		if (particleSG.getMinOccurs() == 0) {
			return false;
		} else {
			if (particleSG.isGroup()) {
				GroupSG group = particleSG.getGroupSG();
				ParticleSG[] particles = group.getParticles();
				if (group.isChoice()) {
					for (int i = 0;  i < particles.length;  i++) {
						if (!isRequiredParticle(particles[i])) {
							return false;
						}
					}
					return true;
				} else {
					for (int i = 0;  i < particles.length;  i++) {
						if (isRequiredParticle(particles[i])) {
							return true;
						}
					}
					return false;
				}
			} else {
				return true;
			}
		}
	}

	protected void handleStartOfChildElement(Object pUnmarshallerHandler,
											 JavaMethod pJm, ParticleSG pParticle) {
		ObjectSG oSG = pParticle.getObjectSG();
		TypeSG tSG = oSG.getTypeSG();
		if (tSG.isComplex()) {
			Object[] o, h;
			if (oSG.getClassContext().isGlobal()) {
				JavaQName elementInterfaceClass = pParticle.getObjectSG().getClassContext().getXMLInterfaceName();
				LocalJavaField manager = pJm.newJavaField(JMManager.class);
				manager.addLine("getHandler().getJMUnmarshaller().getJAXBContextImpl().getManagerS(",
						elementInterfaceClass, ".class)");
				o = new Object[]{manager, ".getElementS();"};
				h = new Object[]{manager, ".getHandler();"};
			} else {
				Context context = oSG.getClassContext();
				o = new Object[]{" new ", context.getXMLImplementationName(), "()"};
				h = new Object[]{" new ", context.getXMLHandlerName(), "()"};
			}
			LocalJavaField oField = pJm.newJavaField(Object.class);
			oField.addLine(o);
			LocalJavaField hField = pJm.newJavaField(JMSAXElementParser.class);
			hField.addLine(h);
			XsQName name = oSG.getName();
			pJm.addLine(hField, ".init(", pUnmarshallerHandler, ", ", oField, ", ",
					JavaSource.getQuoted(name.getNamespaceURI()), ", ",
					JavaSource.getQuoted(name.getLocalName()),
			", ", pUnmarshallerHandler, ".getLevel());");
			pJm.addLine(hField, ".setAttributes(", getParamAttrs(), ");");
			pJm.addLine(pUnmarshallerHandler, ".addElementParser(", hField, ");");
		} else {
			pJm.addLine(pUnmarshallerHandler, ".addSimpleAtomicState();");
		}
	}

	protected abstract void acceptParticle(JavaMethod pJm, int pNum) throws SAXException;

	protected void handleStartElementStates(Object pUnmarshallerHandler,
											JavaMethod pJm, int pFrom,
											int pTo) throws SAXException {
		if (pFrom < 0  ||  pFrom >= particles.length  ||
		    pTo < 0  ||  pTo >= particles.length  ||  pTo < pFrom) {
			return;
		}
		for (int i = pFrom;  i <= pTo;  i++) {
			ParticleSG particle = particles[i];
			if (particle.isElement()) {
				ObjectSG oSG = particle.getObjectSG();
				XsQName name = oSG.getName();
				Object uriCondition;
				if ("".equals(name.getNamespaceURI())) {
					uriCondition = new Object[]{"(", getParamNamespaceURI(),
							" == null  ||  ",
							getParamNamespaceURI(),
					".length() == 0)"};
				} else {
					uriCondition = new Object[]{JavaSource.getQuoted(name.getNamespaceURI()),
							".equals(", getParamNamespaceURI(), ")"};
				}
				pJm.addIf(i == pFrom, uriCondition, "  &&  ",
						  JavaSource.getQuoted(name.getLocalName()),
						  ".equals(", getParamLocalName(), ")");
				acceptParticle(pJm, i);
				handleStartOfChildElement(pUnmarshallerHandler, pJm, particle);
				pJm.addLine("return true;");
			} else if (particle.isGroup()) {
				GroupSG gSG = particle.getGroupSG();
				GroupHandlerSG handlerSG = getGroupHandlerSG(gSG);
				pJm.addIf(i == pFrom,
						  pUnmarshallerHandler, ".testGroupParser(new ",
						  handlerSG.getJavaSource().getQName(), "(), ",
						  getParamNamespaceURI(), ", ", getParamLocalName(),
						  ", ", getParamQName(), ", ", getParamAttrs(), ")");
				acceptParticle(pJm, i);
				pJm.addLine("return true;");
			} else if (particle.isWildcard()) {
				throw new IllegalStateException("TODO: Add support for wildcards");
			} else {
				throw new IllegalStateException("Invalid particle type");
			}
		}
		pJm.addEndIf();
	}

	protected abstract int getState(int pParticleNum);
	protected abstract DirectAccessible getEndElementState() throws SAXException;

	public JavaMethod newEndElementMethod() throws SAXException {
		JavaMethod result = super.newEndElementMethod();
		JavaQName elementInterface = ctSG.getClassContext().getXMLInterfaceName();
		LocalJavaField element = result.newJavaField(elementInterface);
		element.addLine("(", elementInterface, ") result");
		result.addSwitch(getEndElementState());
		for (int i = 0;  i < particles.length;  i++) {
			result.addCase(new Integer(getState(i)));
			ParticleSG particle = particles[i];
			handleEndElementState(result, element, particle);
		}
		result.addDefault();
		result.addThrowNew(IllegalStateException.class,
						   JavaSource.getQuoted("Illegal state: "), " + ",
						   getEndElementState());
		result.addEndSwitch();
		return result;
	}

    public JavaMethod newIsFinishedMethod() throws SAXException {
        JavaMethod jm = super.newIsFinishedMethod();
        if (isMixed) {
            jm.addLine("normalize();");
        }
        return jm;
    }

	private void handleEndElementState(JavaMethod pJm, LocalJavaField pElement,
									   ParticleSG pParticle) throws SAXException {
		if (pParticle.isElement()) {
			ObjectSG oSG = pParticle.getObjectSG();
			TypeSG childType = oSG.getTypeSG();
			XsQName name = oSG.getName();
			Object[] uriCondition;
			if ("".equals(name.getNamespaceURI())) {
				uriCondition = new Object[]{
					getParamNamespaceURI(), " == null  ||  ",
					getParamNamespaceURI(), ".length() == 0"
				};
			} else {
				uriCondition = new Object[]{
					JavaSource.getQuoted(name.getNamespaceURI()), ".equals(",
					getParamNamespaceURI(), ")"
				};
			}
			pJm.addIf(uriCondition, "  &&  ", JavaSource.getQuoted(name.getLocalName()),
					".equals(", getParamLocalName(), ")");
			JavaQName type;
			TypedValue v = getParamResult();
			if (childType.isComplex()) {
				type = childType.getComplexTypeSG().getClassContext().getXMLInterfaceName();
				if (isMixed) {
					pJm.addLine(pElement, ".getContent().add(", v, ");");
				} else {
					pParticle.getPropertySG().addValue(pJm, pElement, v, type);
				}
			} else {
				PropertySG pSG;
				DirectAccessible element;
				if (isMixed) {
					LocalJavaField f = pJm.newJavaField(GroupUtil.getContentClass(group, pParticle, ctSG.getClassContext().getXMLInterfaceName()));
					f.addLine("new ", GroupUtil.getContentClass(group, pParticle, ctSG.getClassContext().getXMLImplementationName()), "()");
					PropertySGChain chain = ((PropertySGImpl) pParticle.getPropertySG()).getHeadOfChain();
					PropertySGChain head = new PropertySGChainImpl(chain){
						public String getXMLFieldName(PropertySG pController) throws SAXException {
							return "_value";
						}
						public String getPropertyName(PropertySG pController) throws SAXException {
							return "value";
						}
					};
					pSG = new PropertySGImpl(head);
					element = f;
				} else {
					pSG = pParticle.getPropertySG();
					element = pElement;
				}
				createSimpleTypeConversion(pJm, childType, v,
										   oSG.getName().toString(),
										   pSG, element);
				if (isMixed) {
					pJm.addLine(pElement, ".getContent().add(", element, ");");
				}
			}
			pJm.addLine("return;");
			pJm.addEndIf();
			pJm.addBreak();
		} else if (pParticle.isGroup()) {
			pJm.addThrowNew(IllegalStateException.class,
							JavaSource.getQuoted("This case should be handled by a nested group parser."));
		} else if (pParticle.isWildcard()) {
			throw new IllegalStateException("TODO: Add support for wildcards.");
		} else {
			throw new IllegalStateException("Invalid particle type");
		}
	}

	public JavaMethod newIsAtomicMethod() throws SAXException {
		return null;
	}

	public JavaMethod newIsEmptyMethod() throws SAXException {
		return null;
	}

	protected JavaMethod newIsMixedMethod() throws SAXException {
		if (!isMixed) {
			return null;
		}
		JavaMethod jm = getJavaSource().newJavaMethod("isMixed", boolean.class, JavaSource.PUBLIC);
		jm.addLine("return true;");
		return jm;
	}

	protected JavaMethod newAddTextMethod() throws SAXException {
		if (!isMixed) {
			return null;
		}
        if (ccSG != null) {
            return null;
        }
		JavaMethod jm = getJavaSource().newJavaMethod("addText", void.class, JavaSource.PUBLIC);
		Parameter buffer = jm.addParam(char[].class, "pBuffer");
		Parameter offset = jm.addParam(int.class, "pOffset");
		Parameter length = jm.addParam(int.class, "pLength");
		jm.addLine(outerHandler.getJavaSource().getQName(), ".this.addText(",
		        buffer, ", ", offset, ", ", length, ");");
        return jm;
    }

    private JavaMethod newContentListMethod() {
        if (!isMixed) {
            return null;
        }
        JavaMethod jm = getJavaSource().newJavaMethod("getContentList", List.class, JavaSource.PROTECTED);
        JavaQName elementInterface = ctSG.getClassContext().getXMLInterfaceName();
        jm.addLine("return ((", elementInterface, ") result).getContent();");
        return jm;
	}

	private JavaMethod newGetHandlerMethod(JavaSource pOuter) throws SAXException {
		JavaMethod result = getJavaSource().newJavaMethod("getHandler", JMUnmarshallerHandlerImpl.class, JavaSource.PUBLIC);
		result.addLine("return ", pOuter.getQName(), ".this.getHandler();");
		return result;
	}

	public void generate() throws SAXException {
		super.generate();
		newAddTextMethod();
		newIsMixedMethod();
        newContentListMethod();
		for (Iterator iter = groups.values().iterator();  iter.hasNext();  ) {
			HandlerSG handler = (HandlerSG) iter.next();
			handler.generate();
		}
	}
}