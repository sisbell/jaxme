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
package org.apache.ws.jaxme.js;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class ConditionalIndentationJavaSourceObject extends JavaSourceObject
    implements ConditionalIndentationEngine {
  private class LocalJavaFieldDeclaration {
    LJFImpl field;
    private LocalJavaFieldDeclaration(LJFImpl pField) {
      field = pField;
    }
  }

  public class LoopVariable implements DirectAccessible, IndentedObject {
     private Object value;
	 private JavaQName type;
     public LoopVariable(Object pValue, JavaQName pType) {
        value = pValue;
		type = pType;
     }
	 public JavaQName getType() { return type; }
     public boolean isNullable() { return false; }
     public void setNullable(boolean pNullable) {
       if (pNullable) {
         throw new IllegalStateException("Loop variables cannot be nullable.");
       }
     }
     public Object getValue() {
        return value;
     }
     public void write(IndentationEngine pEngine, IndentationTarget pTarget)
         throws IOException {
       pEngine.write(pTarget, getValue());
     }
  }

  public class LJFImpl extends JavaField implements LocalJavaField {
    LJFImpl(JavaQName pType, String pName) {
       super(pName, pType, null);
    }
    public void write(IndentationEngine pEngine, IndentationTarget pTarget)
        throws IOException {
      pEngine.write(pTarget, getName());
    }
  }

  private int localVariableCounter = 0;

  protected ConditionalIndentationJavaSourceObject
      (String pName, JavaQName pType, JavaSource.Protection pProtection) {
    super(pName, pType, pProtection);
  }

  public void addIf(Object[] pTokens) {
  	 addLine("if (", pTokens, ") {");
  	 indent();
  }
  public void addIf(Object pToken1) {
	 addIf(new Object[]{pToken1});
  }
  public void addIf(Object pToken1, Object pToken2) {
	 addIf(new Object[]{pToken1, pToken2});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3) {
	 addIf(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                     Object pToken5) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10, Object pToken11) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10, Object pToken11,
							Object pToken12) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							  pToken13});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13, Object pToken14) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							  pToken13, pToken14});
  }
  public void addIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13, Object pToken14,
							Object pToken15) {
	 addIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							  pToken13, pToken14, pToken15});
  }

  public void addElseIf(Object[] pTokens) {
  	 unindent();
  	 addLine("} else if (", pTokens, ") {");
  	 indent();
  }
  public void addElseIf(Object pToken1) {
	 addElseIf(new Object[]{pToken1});
  }
  public void addElseIf(Object pToken1, Object pToken2) {
	 addElseIf(new Object[]{pToken1, pToken2});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10, Object pToken11) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10, Object pToken11,
							    Object pToken12) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10, Object pToken11,
							    Object pToken12, Object pToken13) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							      pToken13});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10, Object pToken11,
							    Object pToken12, Object pToken13, Object pToken14) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							      pToken13, pToken14});
  }
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
							    Object pToken5, Object pToken6, Object pToken7, Object pToken8,
							    Object pToken9, Object pToken10, Object pToken11,
							    Object pToken12, Object pToken13, Object pToken14,
							    Object pToken15) {
	 addElseIf(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							      pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							      pToken13, pToken14, pToken15});
  }

  public void addIf(boolean pFirst, Object[] pTokens) {
  	 if (pFirst) {
  	 	addIf(pTokens);
  	 } else {
  	 	addElseIf(pTokens);
  	 }
  }
  public void addIf(boolean pFirst, Object pToken1) {
	 addIf(pFirst, new Object[]{pToken1});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2) {
	 addIf(pFirst, new Object[]{pToken1, pToken2});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							          pToken7});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							          pToken7, pToken8});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
	 	                         pToken7, pToken8, pToken9});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
	 	                         pToken7, pToken8, pToken9, pToken10});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10, Object pToken11) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							          pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10, Object pToken11,
							Object pToken12) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
	 	                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							          pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							          pToken13});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13, Object pToken14) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
	 	                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
	 	                         pToken13, pToken14});
  }
  public void addIf(boolean pFirst, Object pToken1, Object pToken2, Object pToken3,
                     Object pToken4, Object pToken5, Object pToken6, Object pToken7,
                     Object pToken8, Object pToken9, Object pToken10, Object pToken11,
							Object pToken12, Object pToken13, Object pToken14,	Object pToken15) {
	 addIf(pFirst, new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                               pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							          pToken13, pToken14, pToken15});
  }

  public void addElse() {
	 unindent();
	 addLine("} else {");
	 indent();
  }

  public void addEndIf() {
	 unindent();
	 addLine("}");
  }

  public void addThrowNew(JavaQName pExceptionClass, Object[] pArgs) {
    addLine("throw new ", pExceptionClass, "(", pArgs, ");");
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2, pToken3});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8,
							Object pToken9) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10,
                           Object pToken11) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10,
                           Object pToken11, Object pToken12) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10,
                           Object pToken11, Object pToken12, Object pToken13) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							        pToken13});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10,
                           Object pToken11, Object pToken12, Object pToken13,
                           Object pToken14) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							        pToken13, pToken14});
  }
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1, Object pToken2,
                           Object pToken3, Object pToken4, Object pToken5, Object pToken6,
                           Object pToken7, Object pToken8, Object pToken9, Object pToken10,
                           Object pToken11, Object pToken12, Object pToken13,
                           Object pToken14, Object pToken15) {
	 addThrowNew(pExceptionClass,
	             new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
							        pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
							        pToken13, pToken14, pToken15});
  }

  public void addThrowNew(Class pExceptionClass, Object[] pArgs) {
  	 addThrowNew(JavaQNameImpl.getInstance(pExceptionClass), pArgs);
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2, pToken3});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4) {
	 addThrowNew(pExceptionClass, new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8,
							Object pToken9) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10,
									Object pToken11) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10,
									Object pToken11, Object pToken12) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10,
									Object pToken11, Object pToken12, Object pToken13) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
									  pToken13});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10,
									Object pToken11, Object pToken12, Object pToken13,
									Object pToken14) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
									  pToken13, pToken14});
  }
  public void addThrowNew(Class pExceptionClass, Object pToken1, Object pToken2,
									Object pToken3, Object pToken4, Object pToken5, Object pToken6,
									Object pToken7, Object pToken8, Object pToken9, Object pToken10,
									Object pToken11, Object pToken12, Object pToken13,
									Object pToken14, Object pToken15) {
	 addThrowNew(pExceptionClass,
					 new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
									  pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
									  pToken13, pToken14, pToken15});
  }

  public void addTry() {
    addLine("try {");
    indent();
  }
  public DirectAccessible addCatch(JavaQName pExceptionClass, Object pVar) {
    unindent();
    addLine("} catch (", pExceptionClass, " ", pVar, ") {");
    indent();
    return new LoopVariable(pVar, pExceptionClass);
  }
  public DirectAccessible addCatch(Class pExceptionClass, Object pVar) {
    return addCatch(JavaQNameImpl.getInstance(pExceptionClass), pVar); 
  }
  public DirectAccessible addCatch(JavaQName pExceptionClass) {
    return addCatch(pExceptionClass, getLocalVariableName());
  }
  public DirectAccessible addCatch(Class pExceptionClass) {
    return addCatch(pExceptionClass, getLocalVariableName());
  }
  public void addFinally() {
    unindent();
    addLine("} finally {");
    indent();
  }
  public void addEndTry() {
    unindent();
    addLine("}");
  }


  public void addFor(Object[] pArgs) {
    addLine("for (", pArgs, ") {");
    indent();
  }


  public DirectAccessible addForArray(JavaQName pVarClass, Object pVar, Object pArray) {
    addFor(new Object[]{pVarClass, " ", pVar, " = 0;  ",
                        pVar, " < (", pArray, ").length;  ",
                        pVar, "++"});
    return new LoopVariable(pVar, pVarClass);
  }
  public DirectAccessible addForArray(Class pVarClass, Object pVar, Object pArray) {
    return addForArray(JavaQNameImpl.getInstance(pVarClass), pVar, pArray);
  }
  public DirectAccessible addForArray(Object pVar, Object pArray) {
    return addForArray(int.class, pVar, pArray);
  }
  public DirectAccessible addForArray(JavaQName pVarClass, Object pArray) {
    return addForArray(pVarClass, getLocalVariableName(), pArray);
  }
  public DirectAccessible addForArray(Class pVarClass, Object pArray) {
    return addForArray(pVarClass, getLocalVariableName(), pArray);
  }
  public DirectAccessible addForArray(Object pArray) {
    return addForArray(int.class, pArray);
  }

  public DirectAccessible addForList(JavaQName pVarClass, Object pVar, Object pList) {
    addFor(new Object[]{pVarClass, " ", pVar, " = 0;  ", pVar, " < (", pList,
                         ").size();  ", pVar, "++"});
    return new LoopVariable(pVar, pVarClass);
  }
  public DirectAccessible addForList(Class pVarClass, Object pVar, Object pList) {
    return addForList(JavaQNameImpl.getInstance(pVarClass), pVar, pList);
  }
  public DirectAccessible addForList(Object pVar, Object pList) {
    return addForList(int.class, pVar, pList);
  }
  public DirectAccessible addForList(JavaQName pVarClass, Object pList) {
    return addForList(pVarClass, getLocalVariableName(), pList);
  }
  public DirectAccessible addForList(Class pVarClass, Object pList) {
     return addForList(pVarClass, getLocalVariableName(), pList);
  }
  public DirectAccessible addForList(Object pList) {
    return addForList(int.class, pList);
  }


  public DirectAccessible addForIterator(JavaQName pVarClass, Object pVar, Object pIterator) {
    addFor(new Object[]{pVarClass, " ", pVar, " = ", pIterator, ";  ", pVar,
                        ".hasNext();  "});
    return new LoopVariable(pVar, pVarClass);
  }
  public DirectAccessible addForIterator(Class pVarClass, Object pVar, Object pIterator) {
    return addForIterator(JavaQNameImpl.getInstance(pVarClass), pVar, pIterator);
  }
  public DirectAccessible addForIterator(Object pVar, Object pIterator) {
    return addForIterator(Iterator.class, pVar, pIterator);
  }
  public DirectAccessible addForIterator(JavaQName pVarClass, Object pIterator) {
    return addForIterator(pVarClass, getLocalVariableName(), pIterator);
  }
  public DirectAccessible addForIterator(Class pVarClass, Object pIterator) {
    return addForIterator(pVarClass, getLocalVariableName(), pIterator);
  }
  public DirectAccessible addForIterator(Object pIterator) {
    return addForIterator(Iterator.class, pIterator);
  }

  public DirectAccessible addForEnumeration(JavaQName pVarClass, Object pVar, Object pEnumeration) {
    addFor(new Object[]{pVarClass, " ", pVar, " = ", pEnumeration, ";  ", pVar,
                        ".hasMoreElements();  "});
    return new LoopVariable(pVar, pVarClass);
  }
  public DirectAccessible addForEnumeration(Class pVarClass, Object pVar, Object pEnumeration) {
    return addForEnumeration(JavaQNameImpl.getInstance(pVarClass), pVar, pEnumeration);
  }
  public DirectAccessible addForEnumeration(Object pVar, Object pEnumeration) {
    return addForEnumeration(Enumeration.class, pVar, pEnumeration);
  }
  public DirectAccessible addForEnumeration(JavaQName pVarClass, Object pEnumeration) {
    return addForEnumeration(pVarClass, getLocalVariableName(), pEnumeration);
  }
  public DirectAccessible addForEnumeration(Class pVarClass, Object pEnumeration) {
    return addForEnumeration(pVarClass, getLocalVariableName(), pEnumeration);
  }
  public DirectAccessible addForEnumeration(Object pEnumeration) {
    return addForEnumeration(Enumeration.class, pEnumeration);
  }

  public DirectAccessible addForCollection(JavaQName pVarClass, Object pVar, Object pCollection) {
    return addForIterator(pVarClass, pVar, new Object[]{"(", pCollection, ").iterator()"});
  }
  public DirectAccessible addForCollection(Class pVarClass, Object pVar, Object pCollection) {
    return addForCollection(JavaQNameImpl.getInstance(pVarClass), pVar, pCollection);
  }
  public DirectAccessible addForCollection(Object pVar, Object pCollection) {
    return addForCollection(Iterator.class, pVar, pCollection);
  }
  public DirectAccessible addForCollection(JavaQName pVarClass, Object pCollection) {
    return addForCollection(pVarClass, getLocalVariableName(), pCollection);
  }
  public DirectAccessible addForCollection(Class pVarClass, Object pCollection) {
    return addForCollection(pVarClass, getLocalVariableName(), pCollection);
  }
  public DirectAccessible addForCollection(Object pCollection) {
    return addForCollection(Iterator.class, pCollection);
  }

  public void addEndFor() {
    unindent();
    addLine("}");
  }
  public void addFor(Object pToken1) {
    addFor(new Object[]{pToken1});
  }
  public void addFor(Object pToken1, Object pToken2) {
    addFor(new Object[]{pToken1, pToken2});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3) {
    addFor(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                         pToken13});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                         pToken13, pToken14});
  }
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14, Object pToken15) {
    addFor(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                         pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                         pToken13, pToken14, pToken15});
  }

  /** <p>Shortcut for <code>addLine("while (", pExpr, ") {"); indent();</code>.</p>
   */
  public void addWhile(Object[] pExpr) {
     addLine("while (", pExpr, ") {");
     indent();
  }
  /** <p>Shortcut for <code>addWhile(new Object[]{"(", pIterator, ").hasNext()"});</code>
   */
  public void addWhileIterator(Object pIterator) {
     addWhile(new Object[]{"(", pIterator, ").hasNext()"});
  }
  /** <p>Shortcut for <code>unindent(); addLine("}");</code>.</p>
   */
  public void addEndWhile() {
     unindent();
     addLine("}");
  }
  public void addWhile(Object pToken1) {
    addWhile(new Object[]{pToken1});
  }
  public void addWhile(Object pToken1, Object pToken2) {
    addWhile(new Object[]{pToken1, pToken2});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3) {
    addWhile(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10, pToken11, pToken12});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                         pToken13});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                          pToken13, pToken14});
  }
  public void addWhile(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14, Object pToken15) {
    addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
                          pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
                         pToken13, pToken14, pToken15});
  }

  public void addSwitch(Object[] pTokens) {
    addLine("switch (", pTokens, ") {");
    indent();
    indent();
  }
  public void addSwitch(Object pToken1) {
    addSwitch(new Object[]{pToken1});
  }
  public void addSwitch(Object pToken1, Object pToken2) {
    addSwitch(new Object[]{pToken1, pToken2});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10, Object pToken11) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10,
			   pToken11});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10, Object pToken11, Object pToken12) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10,
			   pToken11, pToken12});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10, Object pToken11, Object pToken12,
			Object pToken13) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10,
			   pToken11, pToken12, pToken13});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10, Object pToken11, Object pToken12,
			Object pToken13, Object pToken14) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10,
			   pToken11, pToken12, pToken13, pToken14});
  }
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
			Object pToken4, Object pToken5, Object pToken6,
			Object pToken7, Object pToken8, Object pToken9,
			Object pToken10, Object pToken11, Object pToken12,
			Object pToken13, Object pToken14, Object pToken15) {
    addSwitch(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
			   pToken6, pToken7, pToken8, pToken9, pToken10,
			   pToken11, pToken12, pToken13, pToken14, pToken15});
  }

  public void addCase(Object[] pTokens) {
    unindent();
    addLine("case ", pTokens, ":");
    indent();
  }
  public void addCase(Object pToken1) {
    addCase(new Object[]{pToken1});
  }
  public void addCase(Object pToken1, Object pToken2) {
    addCase(new Object[]{pToken1, pToken2});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3) {
    addCase(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10, Object pToken11) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10, pToken11});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10, Object pToken11, Object pToken12) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10, pToken11,
			 pToken12});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10, Object pToken11, Object pToken12,
		      Object pToken13) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10, pToken11,
			 pToken12, pToken13});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10, Object pToken11, Object pToken12,
		      Object pToken13, Object pToken14) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10, pToken11,
			 pToken12, pToken13, pToken14});
  }
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
		      Object pToken4, Object pToken5, Object pToken6,
		      Object pToken7, Object pToken8, Object pToken9,
		      Object pToken10, Object pToken11, Object pToken12,
		      Object pToken13, Object pToken14, Object pToken15) {
    addCase(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6,
			 pToken7, pToken8, pToken9, pToken10, pToken11,
			 pToken12, pToken13, pToken14, pToken15});
  }

  public void addDefault() {
    unindent();
    addLine("default:");
    indent();
  }

  public void addBreak() {
    addLine("break;");
  }

  public void addEndSwitch() {
    unindent();
    unindent();
    addLine("}");
  }
  public String getLocalVariableName() {
    return "_" + ++localVariableCounter;
  }

  protected abstract void writeHeader(IndentationTarget pTarget)
    throws IOException;

  public void write(IndentationTarget pTarget) throws IOException {
    JavaComment jc = getComment();
    if (jc != null) {
      jc.write(pTarget);
      pTarget.indent(0);
    }
    writeHeader(pTarget);
    if (!pTarget.isInterface()  &&  !isAbstract()) {
      super.write(new IncreasingTarget(pTarget));
      pTarget.indent(0);
      pTarget.write("}");
      pTarget.write();
    }
  }

  public void write(IndentationTarget pTarget, Object pObject)
        throws IOException {
    if (pObject instanceof LocalJavaFieldDeclaration) {
      LJFImpl f = ((LocalJavaFieldDeclaration) pObject).field;
      f.writeNoEol(pTarget);
    } else {
      super.write(pTarget, pObject);
    }
  }

  /** <p>Creates a new {@link LocalJavaField} with the given class and name,
   * which is not final.</p>
   */
  public LocalJavaField newJavaField(Class pType, String pName) {
     return newJavaField(JavaQNameImpl.getInstance(pType), pName);
  }

  /** <p>Creates a new {@link LocalJavaField} with the given class and
   * name.</p>
   */
  public LocalJavaField newJavaField(JavaQName pType, String pName) {
    LJFImpl result = new LJFImpl(pType, pName);
    addLine(new LocalJavaFieldDeclaration(result));
    return result;
  }

  /** <p>Creates a new {@link LocalJavaField} with the given class, which is
   * not final. The field name will be generated using
   * {@link #getLocalVariableName()}.</p>
   */
  public LocalJavaField newJavaField(JavaQName pType) {
    return newJavaField(pType, getLocalVariableName());
  }

  /** <p>Creates a new {@link LocalJavaField} with the given class, which is
   * not final. The field name will be generated using
   * {@link #getLocalVariableName()}.</p>
   */
  public LocalJavaField newJavaField(Class pType) {
    return newJavaField(pType, getLocalVariableName());
  }
}
