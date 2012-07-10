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
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: IndentationEngineImpl.java 232093 2005-04-21 11:25:23Z mac $
 */
public abstract class IndentationEngineImpl implements IndentationEngine {
  private int level = 0;
  final private List lines = new ArrayList();
  private int cursor = -1;

  private class IndentedLine {
    int indLevel;
    Object[] tokens;
  }

  private class PlaceHolderImpl implements PlaceHolder {
    private final boolean autoRemovable;
    private final String name;
    private final int plLevel;
    private Map properties;
    public PlaceHolderImpl(String pName, boolean pAutoRemovable) {
      name = pName;
      autoRemovable = pAutoRemovable;
      plLevel = IndentationEngineImpl.this.getLevel();
    }
    public String getName() { return name; }
    public boolean isAutoRemovable() { return autoRemovable; }
    public void remove() { removePlaceHolder(this); }
    public void setProperty(String pKey, Object pValue) {
      if (properties == null) {
        properties = new HashMap();
      }
      properties.put(pKey, pValue);
    }
    public Object getProperty(String pKey) { return properties == null ? null : properties.get(pKey); }
    public int getLevel() { return plLevel; }
  }

  protected IndentationEngineImpl() {}

  public boolean isEmpty() { return lines.size() == 0; }
  public void clear() { lines.clear(); }
  public void indent() { ++level; }
  public void unindent() { --level; }
  public void setLevel(int pLevel) { level = pLevel; }
  public int getLevel() { return level; }

  public void addLine(int pLevel, Object[] pTokens) {
    IndentedLine l = new IndentedLine();
    l.indLevel = pLevel;
    l.tokens = pTokens;
    if (cursor == -1) {
       lines.add(l);
    } else {
       lines.add(cursor++, l);
    }
  }

  public void moveToTop() {
    cursor = 0;
  }

  public void moveToBottom() {
    cursor = -1;
  }

  public PlaceHolder newPlaceHolder(String pName, boolean pAutoRemovable) {
    PlaceHolder result = new PlaceHolderImpl(pName, pAutoRemovable);
    addLine(result);
    return result;
  }

  public PlaceHolder getPlaceHolder(String pName) {
    for (int i = 0;  i < lines.size();  i++) {
      IndentedLine l = (IndentedLine) lines.get(i);
      Object [] tokens = l.tokens;
      if (tokens.length > 0  &&  tokens[0] instanceof PlaceHolder) {
        PlaceHolder placeHolder = (PlaceHolder) tokens[0];
        if (pName.equals(placeHolder.getName())) {
          cursor = i+1;
          level = placeHolder.getLevel();
          return placeHolder;
        }
      }
    }
    return null;
  }

  protected void removePlaceHolder(PlaceHolder pPlaceHolder) {
    String name = pPlaceHolder.getName();
    for (int i = 0;  i < lines.size();  i++) {
      IndentedLine l = (IndentedLine) lines.get(i);
      Object [] tokens = l.tokens;
      if (tokens.length > 0  &&  tokens[0] instanceof PlaceHolder) {
        PlaceHolder placeHolder = (PlaceHolder) tokens[0];
        if (name.equals(placeHolder.getName())) {
          lines.remove(i);
          if (cursor > i) { --cursor; }
          return;
        }
      }
    }
    throw new IllegalStateException("The placeholder " + pPlaceHolder.getName() +
                                     " was not found and cannot be removed.");
  }

  protected void checkNulls(Object pTokens) {
    if (pTokens == null) {
      throw new NullPointerException("Null token detected.");
    }
    if (pTokens.getClass().isArray()) {
		if (!pTokens.getClass().getComponentType().isPrimitive()) {
			Object[] tokens = (Object[]) pTokens;
			for (int i = 0;  i < tokens.length;  i++) {
				checkNulls(tokens[i]);
			}
		}
    } else if (pTokens instanceof Collection) {
      for (Iterator iter = ((Collection) pTokens).iterator();  iter.hasNext();  ) {
        checkNulls(iter.next());
      }
    } else if (pTokens instanceof JavaSource) {
      if (!(this instanceof JavaSource)) {
        throw new IllegalStateException("An instance of JavaSource cannot be added to an instance of " +
                                        getClass().getName());
      }
      if (!(pTokens instanceof JavaInnerClass)) {
        throw new IllegalStateException("Only inner classes can be added, outer classes are standalone objects.");
      }
      JavaInnerClass jic = (JavaInnerClass) pTokens;
      JavaSource js = (JavaSource) this;
      if (!jic.getOuterClass().equals(js)) {
         throw new IllegalStateException("The inner class " + jic.getQName() +
                                         " cannot be added to a different outer class " +
                                         js.getQName());
      }
    }
  }

  public void addLine(Object[] pTokens) {
    checkNulls(pTokens);
    addLine(getLevel(), pTokens);
  }

  public void addLine() {
    addLine(new Object[0]);
  }
  public void addLine(Object pLine) {
    addLine(new Object[]{pLine});
  }
  public void addLine(Object pToken1, Object pToken2) {
    addLine(new Object[]{pToken1, pToken2});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3) {
    addLine(new Object[]{pToken1, pToken2, pToken3});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10, Object pToken11) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10,
                         pToken11});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10, Object pToken11,
                       Object pToken12) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10,
                         pToken11, pToken12});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10, Object pToken11,
                       Object pToken12, Object pToken13) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10,
                         pToken11, pToken12, pToken13});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10, Object pToken11,
                       Object pToken12, Object pToken13, Object pToken14) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10,
                         pToken11, pToken12, pToken13, pToken14});
  }
  public void addLine(Object pToken1, Object pToken2,
                       Object pToken3, Object pToken4, Object pToken5,
                       Object pToken6, Object pToken7, Object pToken8,
                       Object pToken9, Object pToken10, Object pToken11,
                       Object pToken12, Object pToken13, Object pToken14,
                       Object pToken15) {
    addLine(new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
                         pToken6, pToken7, pToken8, pToken9, pToken10,
                         pToken11, pToken12, pToken13, pToken14, pToken15});
  }

  public String[] getLines(int pLevel) {
    String[] result = new String[lines.size()];
    return result;
  }

  protected Iterator getLines() {
    return lines.iterator();
  }

  public void write(IndentationTarget pTarget, Object pObject)
      throws IOException {
    if (pObject.getClass().isArray()) {
      // We are *not* casting to Object[], because the array
	  // might be primitive.
      int arrayLength = Array.getLength(pObject);
	  for (int i = 0;  i < arrayLength;  i++) {
		write(pTarget, Array.get(pObject, i));
	  }
	} else if (pObject instanceof JavaSourceObject) {
		  pTarget.write(((JavaSourceObject) pObject).getName());
    } else if (pObject instanceof List) {
    	for (Iterator iter = ((List) pObject).iterator();  iter.hasNext();  ) {
    		write(pTarget, iter.next());
    	}
    } else if (pObject instanceof Class) {
      JavaQName name = JavaQNameImpl.getInstance((Class) pObject); 
      write(pTarget, pTarget.asString(name));
    } else if (pObject instanceof JavaQName) {
      write(pTarget, pTarget.asString((JavaQName) pObject));
    } else if (pObject instanceof IndentedObject) {
      ((IndentedObject) pObject).write(this, pTarget);
    } else {
      pTarget.write(pObject.toString());
    }
  }

  public void write(IndentationTarget pTarget) throws IOException {
    for (Iterator iter = getLines();  iter.hasNext();  ) {
      IndentedLine l = (IndentedLine) iter.next();
      Object[] tokens = l.tokens;
      if (tokens.length == 1  &&  tokens[0] instanceof PlaceHolder) {
        PlaceHolder placeHolder = (PlaceHolder) tokens[0];
        if (!placeHolder.isAutoRemovable()) {
          throw new IllegalStateException("The PlaceHolder " + placeHolder.getName() + " has not been removed.");
        }
      } else {
        pTarget.indent(l.indLevel);
        for (int i = 0;  i < tokens.length;  i++) {
          write(pTarget, tokens[i]);
        }
        pTarget.write();
      }
    }
  }

  public String asString() {
  	 StringWriter sw = new StringWriter();
  	 IndentationTarget target = new WriterTarget(sw);
  	 try {
		write(target);
  	 } catch (IOException e) {
  	 	throw new IllegalStateException(e.getMessage());
  	 }
  	 return sw.toString();
  }
}
