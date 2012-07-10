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
package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.xs.XSAtomicType;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class AtomicTypeSGImpl implements AtomicTypeSG {
	private final String maxExclusive, maxInclusive;
	private final String minExclusive, minInclusive;
	private final Long fractionDigits, totalDigits;
	private final Long length, maxLength, minLength;
	private final String[] patterns;

	public AtomicTypeSGImpl(XSAtomicType xsAtomicType) {
		maxExclusive = xsAtomicType.getMaxExclusive();
		maxInclusive = xsAtomicType.getMaxInclusive();
		minExclusive = xsAtomicType.getMinExclusive();
		minInclusive = xsAtomicType.getMinInclusive();
		fractionDigits = xsAtomicType.getFractionDigits();
		totalDigits = xsAtomicType.getTotalDigits();
		length = xsAtomicType.getLength();
		maxLength = xsAtomicType.getMaxLength();
		minLength = xsAtomicType.getMinLength();
		String[] myPatterns = xsAtomicType.getPatterns();
		patterns = (myPatterns == null  ||  myPatterns.length == 0) ? null : myPatterns;
	}

	public String getMaxExclusive() { return maxExclusive; }
	public String getMaxInclusive() { return maxInclusive; }
	public String getMinExclusive() { return minExclusive; }
	public String getMinInclusive() { return minInclusive; }
	public Long getFractionDigits() { return fractionDigits; }
	public Long getTotalDigits() { return totalDigits; }
	public Long getLength() { return length; }
	public Long getMaxLength() { return maxLength; }
	public Long getMinLength() { return minLength; }
	public String[] getPatterns() { return patterns; }
}