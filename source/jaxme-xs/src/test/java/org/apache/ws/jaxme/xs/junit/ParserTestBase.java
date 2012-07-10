/*
 * Copyright 2003, 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.xs.junit;

import org.apache.ws.jaxme.xs.XSAtomicType;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSModelGroup;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.apache.ws.jaxme.xs.xml.XsComplexContentType;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class ParserTestBase extends XSTestCase {
	protected XSComplexType assertComplexType(XSType pType) throws SAXException {
		assertTrue(!pType.isSimple());
		XSComplexType result = pType.getComplexType();
		assertNotNull(result);
		boolean haveException = false;
		try {
			pType.getSimpleType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		return result;
	}

	protected XSSimpleType assertSimpleType(XSType pType) throws SAXException {
		assertTrue(pType.isSimple());
		XSSimpleType result = pType.getSimpleType();
		assertNotNull(result);
		boolean haveException = false;
		try {
			pType.getComplexType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		return result;
	}

	protected XSAtomicType assertAtomicType(XSSimpleType pType) throws SAXException {
		assertTrue(pType.isAtomic());
		assertTrue(!pType.isList());
		assertTrue(!pType.isUnion());
		XSAtomicType result = pType.getAtomicType();
		assertNotNull(result);
		boolean haveException = false;
		try {
			pType.getListType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		haveException = false;
		try {
			pType.getUnionType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		return result;
	}

	protected XSListType assertListType(XSSimpleType pType) throws SAXException {
		assertTrue(!pType.isAtomic());
		assertTrue(pType.isList());
		assertTrue(!pType.isUnion());
		XSListType result = pType.getListType();
		assertNotNull(result);
		boolean haveException = false;
		try {
			pType.getAtomicType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		haveException = false;
		try {
			pType.getUnionType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		return result;
	}

	protected XSUnionType assertUnionType(XSSimpleType pType) throws SAXException {
		assertTrue(!pType.isAtomic());
		assertTrue(!pType.isList());
		assertTrue(pType.isUnion());
		XSUnionType result = pType.getUnionType();
		assertNotNull(result);
		boolean haveException = false;
		try {
			pType.getListType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		haveException = false;
		try {
			pType.getAtomicType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		return result;
	}

	protected XSType assertRestriction(XSSimpleType pType) throws SAXException {
		assertTrue(pType.isRestriction());
		XSType result = pType.getRestrictedType();
		assertNotNull(result);
		assertSimpleType(result);
		return result;
	}

	protected XSParticle assertComplexContent(XSComplexType pType) {
		assertTrue(!pType.hasSimpleContent());
		XSParticle result = pType.getParticle();
		assertNotNull(result);
		XsComplexContentType ccType = pType.getComplexContentType();
		assertNotNull(pType.getComplexContentType());
		int num = 0;
		if (pType.isElementOnly()) {
			++num;
			assertEquals(XsComplexContentType.ELEMENT_ONLY, ccType);
		} 
		if (pType.isEmpty()) {
			++num;
			assertEquals(XsComplexContentType.EMPTY, ccType);
		} 
		if (pType.isMixed()) {
			++num;
			assertEquals(XsComplexContentType.MIXED, ccType);
		} 
		assertEquals(1, num);

		num = 0;
		if (result.isElement()) {
			++num;
			assertNotNull(result.getElement());
			assertEquals(XSParticle.ELEMENT, result.getType());
		}
		if (result.isGroup()) {
			++num;
			assertNotNull(result.getGroup());
			assertEquals(XSParticle.GROUP, result.getType());
		}
		if (result.isWildcard()) {
			++num;
			assertNotNull(result.getWildcard());
			assertEquals(XSParticle.WILDCARD, result.getType());
		}
		assertEquals(1, num);

		return result;
	}

	protected XSGroup assertGroup(XSParticle pParticle) {
		assertTrue(pParticle.isGroup());
		assertEquals(XSParticle.GROUP, pParticle.getType());
		XSGroup result = pParticle.getGroup();
		assertNotNull(result);
		return result;
	}

	protected void assertSequence(XSGroup pGroup) {
		assertTrue(pGroup.isSequence());
		assertFalse(pGroup.isChoice());
		assertFalse(pGroup.isAll());
		assertEquals(XSModelGroup.SEQUENCE, pGroup.getCompositor());
	}

	protected void assertChoice(XSGroup pGroup) {
		assertFalse(pGroup.isSequence());
		assertTrue(pGroup.isChoice());
		assertFalse(pGroup.isAll());
		assertEquals(XSModelGroup.CHOICE, pGroup.getCompositor());
	}

	protected XSElement assertElement(XSParticle pParticle) {
		assertTrue(pParticle.isElement());
		assertEquals(XSParticle.ELEMENT, pParticle.getType());
		XSElement result = pParticle.getElement();
		assertNotNull(result);
		return result;
	}

	protected XSSimpleContentType assertSimpleContent(XSComplexType pType) {
		assertTrue(pType.hasSimpleContent());
		XSSimpleContentType result = pType.getSimpleContent();
		assertNotNull(result);
		XSType resultType = result.getType();
		assertNotNull(resultType);
		assertTrue(resultType.isSimple());
		boolean haveException = false;
		try {
			pType.getComplexContentType();
		} catch (IllegalStateException e) {
			haveException = true;
		}
		assertTrue(haveException);
		assertNotNull(result);
		return result;
	}
}
