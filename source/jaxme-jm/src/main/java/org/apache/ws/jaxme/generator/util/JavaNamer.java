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
package org.apache.ws.jaxme.generator.util;

import org.apache.ws.jaxme.generator.sg.SchemaSG;


/**
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class JavaNamer {
  
  /** punctuation characters defined in JAXB spec */
  public static final char[] PUNCTUATION_CHARS = {'-', '.', ':', '_', '\u00B7', '\u0387', '\u06DD', '\u06DE'};

  /**
   * Check whether the given character is a punctuation one or not.
   * 
   * @param c character to check out
   * @param pSchema hint for handling underscore
   * @return true if c belongs to the punctuation characters, otherwise false.
   */
  public static boolean isPunctuationCharacter(char c, SchemaSG pSchema) {
    for (int i = 0; i < PUNCTUATION_CHARS.length; i++) {
      char pc = PUNCTUATION_CHARS[i];
      if (pc == '_') {
        if (!pSchema.isUnderscoreWordSeparator()) {
          continue;
        }
      }
      if (c == pc) {
        return true;
      }
    }
    return false;
  }

  /**
   * Convert a local part name in XML to a class or field name in Java.
   * 
   * @param pLocalName a given local name
   * @param pSchema hint for following Java naming conventions and handling underscore
   * @return the converted name based on the given hints.
   */
  
  public static String convert(String pLocalName, SchemaSG pSchema) {
    if (!pSchema.isJavaNamingConventionsEnabled()) {
      return pLocalName;
    }
    StringBuffer result = new StringBuffer();
    char c = pLocalName.charAt(0);
    if (Character.isJavaIdentifierStart(c)) {
      result.append(Character.toUpperCase(c));
    } else {
      result.append('_');
    }
    for (int i = 1;  i < pLocalName.length();  i++) {
      c = pLocalName.charAt(i);
      if (Character.isJavaIdentifierPart(c) && (c != '_')) {
        result.append(c);
      } else {
        if (isPunctuationCharacter(c, pSchema)) {
          i++;
          c = pLocalName.charAt(i);
          result.append(Character.toUpperCase(c));
        } else {
          result.append('_');
        }
      }
    }
    return result.toString();
  }
}
