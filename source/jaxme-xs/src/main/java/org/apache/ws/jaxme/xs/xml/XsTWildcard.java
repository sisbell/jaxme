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
package org.apache.ws.jaxme.xs.xml;


/** <p>Interface of <code>xs:wildcard</code>, with the
 * following specification:
 * <pre>
 *  &lt;xs:complexType name="wildcard"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:attribute name="namespace" type="xs:namespaceList" use="optional" default="##any"/&gt;
 *        &lt;xs:attribute name="processContents" use="optional" default="strict"
 *          &lt;xs:simpleType&gt;
 *            &lt;xs:restriction base="xs:NMTOKEN"&gt;
 *              &lt;xs:enumeration value="skip"/&gt;
 *              &lt;xs:enumeration value="lax"/&gt;
 *              &lt;xs:enumeration value="strict"/&gt;
 *            &lt;/xs:restriction&gt;
 *          &lt;/xs:simpleType&gt;
 *        &lt;/xs:attribute&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTWildcard extends XsTAnnotated {
  public static class ProcessContents {
    final private String value;
    ProcessContents(String pValue) {
      value = pValue;
    }
    public String toString() { return value; }
    public String getValue() { return value; }
    public static ProcessContents valueOf(String pValue) {
      if ("skip".equals(pValue)) {
        return SKIP;
      } else if ("lax".equals(pValue)) {
        return LAX;
      } else if ("strict".equals(pValue)) {
        return STRICT;
      } else {
        throw new IllegalArgumentException("Invalid value for ProcessContents: " + pValue + "; expected either of 'skip', 'lax', or 'strict'");
      }
    }
  }

  public static final ProcessContents SKIP = new ProcessContents("skip");
  public static final ProcessContents LAX = new ProcessContents("lax");
  public static final ProcessContents STRICT = new ProcessContents("strict");

  public void setNamespace(String pList);
  public XsNamespaceList getNamespace();

  public void setProcessContents(ProcessContents pProcessContents);
  public ProcessContents getProcessContents();
}
