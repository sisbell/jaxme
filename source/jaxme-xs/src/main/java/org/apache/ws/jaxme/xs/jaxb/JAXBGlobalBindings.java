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
package org.apache.ws.jaxme.xs.jaxb;

import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>This interface implements the JAXB global bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBGlobalBindings.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface JAXBGlobalBindings {
  /** <p>Enumeration class holding possible values for {@link JAXBGlobalBindings#getUnderscoreBinding()}.</p>
   */
  public class UnderscoreBinding {
    private final String name;
    private UnderscoreBinding(String pName) {
      name = pName;
    }
    public String toString() { return name; }
    public String getName() { return name; }
    public static final UnderscoreBinding AS_WORD_SEPARATOR = new UnderscoreBinding("asWordSeparator");
    public static final UnderscoreBinding AS_CHAR_IN_WORD = new UnderscoreBinding("asCharInWord");
    public static UnderscoreBinding valueOf(String pName) {
      if (AS_WORD_SEPARATOR.name.equals(pName)) {
        return AS_WORD_SEPARATOR;
      } else if (AS_CHAR_IN_WORD.name.equals(pName)) {
        return AS_CHAR_IN_WORD;
      } else {
        throw new IllegalArgumentException("Invalid value for underscoreBinding: " + pName +
                                            ", expected either of asWordSeparator|asCharInWord");
      }
    }
  }

  /** <p>Returns the collection type; either of null ("indexed",
   * default) or an implementation of <code>java.util.List</code>.</p>
   */
  public String getCollectionType();

  /** <p>Returns whether fixed attributes are implemented as a
   * constant property. Defaults to false.</p>
   */
  public boolean isFixedAttributeAsConstantProperty();

  /** <p>Returns whether a <code>isSet()</code> method is being
   * generated. Defaults to false.</p>
   */
  public boolean isGenerateIsSetMethod();

  /** <p>Returns whether FailFastCheck is enabled. Defaults to
   * false.</p>
   */
  public boolean isEnableFailFastCheck();

  /** <p>Returns the <code>choiceContentProperty</code> value. Defaults
   * to false. This value is ignored, if <code>bindingStyle</code> is
   * defined as <code>elementBinding</code>. In this case, setting
   * <code>choiceContentProperty</code> is an error. Defaults to false.</p>
   */
  public boolean isChoiceContentProperty();

  /** <p>Returns the binding of underscores. Defaults to "asWordSeparator"
   * (false). The value true indicates "asCharInWord".
   */
  public UnderscoreBinding getUnderscoreBinding();

  /** <p>Returns whether Java naming conventions are enabled. Defaults to
   * true.</p>
   */
  public boolean isEnableJavaNamingConventions();

  /** <p>Returns a list of QNames, which are being implemented as type
   * safe enumerations, if the <code>xs:enumeration</code> facet is
   * used. Defaults to <code>xs:NCName</code> (single element list).</p>
   */
  public XsQName[] getTypesafeEnumBase();

  /** <p>Returns whether the typesafeEnumMemberName generates an
   * error (false, default) or a name.</p>
   */
  public boolean isTypesafeEnumMemberName();

  /** <p>Returns whether the <code>elementBinding</code> style is
   * being used (true, default) or not.</p>
   */
  public boolean isBindingStyleModelGroup();

  /** <p>Returns the list of <code>javaType</code> declarations.</p>
   */
  public JAXBJavaType.JAXBGlobalJavaType[] getJavaType();
}
