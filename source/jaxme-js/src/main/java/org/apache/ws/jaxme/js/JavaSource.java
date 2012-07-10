/*
 * Copyright 2004  The Apache Software Foundation
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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/** A class representing a Java source file.
 */
public class JavaSource extends IndentationEngineImpl {
	/** Specifies the type of a java class (interface, or class).
	 */
    public static class Type implements Serializable {
		private static final long serialVersionUID = -9130708752353639211L;
		private String name;
    	Type(String pName) {
    		name = pName;
    	}
    	public String toString() { return name; }
    	/** Converts the given string into a java class type.
    	 */
        public static Type valueOf(String pType) {
    		if ("class".equals(pType)) {
    			return CLASS;
    		} else if ("interface".equals(pType)) {
    			return INTERFACE;
    		} else {
    			throw new IllegalArgumentException("Type must be either 'class' or 'interface'.");
    		}
    	}
    }

    /** Specifies a java objects protection (default, public,
     * protected, or private).
     */
    public static class Protection implements Serializable {
		private static final long serialVersionUID = 1184782160268911467L;
		private String name;
    	Protection(String pName) {
    		name = pName;
    	}
    	public String toString() { return name; }
    	/** Converts the given string into a protection type.
    	 */
        public static Protection valueOf(String pProtection) {
    		if ("public".equals(pProtection)) {
    			return PUBLIC;
    		} else if ("protected".equals(pProtection)) {
    			return PROTECTED;
    		} else if ("private".equals(pProtection)) {
    			return PRIVATE;
    		} else if (pProtection == null  ||  "".equals(pProtection)) {
    			return DEFAULT_PROTECTION;
    		} else {
    			throw new IllegalArgumentException("Protection must be either 'public', 'protected', 'private', null or '' (default protection).");
    		}
    	}
    	/** Returns an instance of Protection by using the methods
    	 * {@link Modifier#isPublic(int)}, {@link Modifier#isProtected(int)} and
    	 * {@link Modifier#isPrivate(int)} on the argument <code>pModifiers</code>.
    	 * If neither returns true, assumes {@link JavaSource#DEFAULT_PROTECTION}.
    	 */
    	public static Protection valueOf(int pModifiers) {
    		if (Modifier.isPublic(pModifiers)) {
    			return PUBLIC;
    		} else if (Modifier.isProtected(pModifiers)) {
    			return PROTECTED;
    		} else if (Modifier.isPrivate(pModifiers)) {
    			return PRIVATE;
    		} else {
    			return DEFAULT_PROTECTION;
    		}
    	}
    }

    /** Type of a JavaSource class.
     * @see #INTERFACE
     * @see #getType
     * @see #setType
     */
    public static final Type CLASS = new Type("class");
    
    /** Type of a JavaSource interface.
     * @see #CLASS
     * @see #getType
     * @see #setType
     */
    public static final Type INTERFACE = new Type("interface");
    
    /** Protection of a class, field or method: public
     */
    public static final Protection PUBLIC = new Protection("public");
    
    /** Protection of a class, field or method: protected
     */
    public static final Protection PROTECTED = new Protection("protected");
    
    /** Protection of a class, field or method: private
     */
    public static final Protection PRIVATE = new Protection("private");
    
    /** Default protection of a class, field or method
     */
    public static final Protection DEFAULT_PROTECTION = new Protection("");
    
    /** Creates a new instance of JavaSource with the given protection.
     * @param pFactory The {@link JavaSourceFactory factory} creating this
     *   instance of JavaSource.
     * @param pName The class or interface name
     * @param pProtection null, "public", "protected" or "private"
     */
    JavaSource(JavaSourceFactory pFactory, JavaQName pName, Protection pProtection) {
    	factory = pFactory;
    	setQName(pName);
    	setProtection(pProtection);
    }
    
    List myObjects = new ArrayList();
    
    /** Returns the static class initializers.
     */
    public JavaClassInitializer[] getClassInitializers() {
    	List result = new ArrayList(myObjects);
    	for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
    		ConditionalIndentationJavaSourceObject object =
    			(ConditionalIndentationJavaSourceObject) iter.next();
    		if (object instanceof JavaClassInitializer) {
    			result.add(object);
    		}
    	}
    	return (JavaClassInitializer[]) result.toArray(new JavaClassInitializer[result.size()]);
    }
    
    private JavaSourceFactory factory;

    /** Returns the {@link JavaSourceFactory} that created this instance of
     * JavaSource.
     */
    public JavaSourceFactory getFactory() {
    	return factory;
    }
    
    private JavaQName myQName;
    /** Returns the JavaSource's JavaQName.
     */
    public JavaQName getQName() {
    	return myQName;
    }
    /** Sets the JavaSource's JavaQName.
     */
    public void setQName(JavaQName pQName) {
    	myQName = pQName;
    }
    /** Returns the class or interface name.
     * @see #setQName
     */
    public String getClassName() { return myQName.getClassName(); }
    /** Returns the package name. The empty String represents the
     * root package.
     */
    public String getPackageName() { return myQName.getPackageName(); }
    
    private Protection myProtection;
    /** Returns the protection.
     * @see #setProtection
     */
    public Protection getProtection() { return myProtection; }
    /** Sets the protection; use null for default protection.
     * @see #getProtection
     */
    public void setProtection(Protection protection) {
    	myProtection = (protection == null) ? DEFAULT_PROTECTION : protection;
    }
    
    private Type type = CLASS;
    /** Returns the JavaSource type.
     * @return "class" or "interface"
     * @see #setType
     */
    public Type getType() { return type; }
    /** Sets the JavaSource type.
     * @param pType "class" or "interface"
     * @see #getType
     */
    public void setType(Type pType) {
    	this.type = (pType == null) ? CLASS : pType;
    }
    
    private JavaComment comment;
    /** Returns the comment describing this class or interface.
     * @see #newComment
     */
    public JavaComment getComment() { return comment; }
    /** Creates a new Javadoc comment describing this class or interface.
     * @see #getComment
     */
    public JavaComment newComment() {
    	if (comment == null) {
    		comment = new JavaComment();
    		return comment;
    	} else {
    		throw new IllegalStateException("A Javadoc comment has already been created for this object.");
    	}
    }
    
    private List extendedClasses;
    /** Clears the list of extended classes or interfaces.
     */
    public void clearExtends() {
    	extendedClasses = null;
    }
    /** Returns the class or interface extended by this class or interface.
     * @see #addExtends(JavaQName)
     */
    public JavaQName[] getExtends() {
    	if (extendedClasses == null) {
    		return new JavaQName[0];
    	} else {
    		return (JavaQName[]) extendedClasses.toArray(new JavaQName[extendedClasses.size()]);
    	}
    }
    /** Sets the class or interface extended by this class or interface.
     * Null or the empty string disable the "extends" clause.
     * @see #getExtends
     */
    public void addExtends(JavaQName pExtends) {
    	if (extendedClasses == null) {
    		extendedClasses = new ArrayList();
    	} else if ("class".equals(getType())) {
    		throw new IllegalStateException("Only interfaces may extend multiple classes.");
    	}
    	extendedClasses.add(pExtends);
    }
    /** Sets the class or interface extended by this class or interface.
     * Null or the empty string disable the "extends" clause.
     * @see #getExtends
     */
    public void addExtends(Class pExtends) {
    	addExtends(JavaQNameImpl.getInstance(pExtends));
    }
    
    /** Returns whether the class is extending the given super class or interface.
     */
    public boolean isExtending(JavaQName pClass) {
    	for (Iterator iter = extendedClasses.iterator();  iter.hasNext();  ) {
    		if (iter.next().equals(pClass)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /** Returns whether the class is extending the given super class or interface.
     */
    public boolean isExtending(Class pClass) {
    	return isExtending(JavaQNameImpl.getInstance(pClass));
    }
    
    private ArrayList imports = new ArrayList();
    /** Returns the list of packages and classes being imported.
     * @see #addImport(JavaQName)
     */
    public JavaQName[] getImports() {
    	return (JavaQName[]) imports.toArray(new JavaQName[imports.size()]);
    }
    
    /** Adds a package or class to the list of packages and classes being imported.
     * @see #getImports()
     */
    public void addImport(JavaQName s) {
    	if (s.isArray()) {
    		throw new IllegalArgumentException("Arrays cannot be imported");
    	}
    	imports.add(s);
    }
    
    /** Adds a package or class to the list of packages and classes being imported.
     * @see #addImport(JavaQName)
     */
    public void addImport(Class s) { imports.add(JavaQNameImpl.getInstance(s)); }
    
    /** Clears the list of imports.
     */
    public void clearImports() { imports.clear(); }
    
    private ArrayList myImplements = new ArrayList();
    /** Returns the list of interfaces being implented by this class or
     * interface.
     * @see #addImplements(JavaQName)
     */
    public JavaQName[] getImplements() {
    	return (JavaQName[]) myImplements.toArray(new JavaQName[myImplements.size()]);
    }
    
    /** Adds an interface to the list of interfaces being implemented by
     * this class or interface.
     * @see #getImplements()
     */
    public void addImplements(JavaQName s) { myImplements.add(s); }
    
    /** Adds an interface to the list of interfaces being implemented by
     * this class or interface.
     * @see #getImplements()
     */
    public void addImplements(Class s) { myImplements.add(JavaQNameImpl.getInstance(s)); }
    
    /** Clears the list of implemented interfaces.
     */
    public void clearImplements() { myImplements.clear(); }
    
    /** Returns whether the class is implementing the given interface.
     */
    public boolean isImplementing(JavaQName pClass) {
    	for (Iterator iter = myImplements.iterator();  iter.hasNext();  ) {
    		if (iter.next().equals(pClass)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /** Returns whether the class is implementing the given interface.
     */
    public boolean isImplementing(Class pClass) {
    	return isImplementing(JavaQNameImpl.getInstance(pClass));
    }
    
    private ArrayList myFields = new ArrayList();
    /** Returns the field with the given name or null, if no such field exists.
     */
    public JavaField getField(String pName) {
    	if (pName == null) {
    		throw new NullPointerException("A field name must not be null.");
    	}
    	JavaField[] fields = getFields();
    	for (int i = 0;  i < fields.length;  i++) {
    		if (pName.equals(fields[i].getName())) {
    			return fields[i];
    		}
    	}
    	return null;
    }
    /** Returns the list of fields that this class has.
     * @see #newJavaField(String, JavaQName, Protection)
     */
    public JavaField[] getFields() {
    	return (JavaField[]) myFields.toArray(new JavaField[myFields.size()]);
    }
    /** Adds a field to this classes list of fields.
     * @see #getFields
     */
    void addField(JavaField f) {
    	String s = f.getName();
    	for (int i = 0;  i < myFields.size();  i++) {
    		if (s.equals(((JavaField) myFields.get(i)).getName())) {
    			throw new IllegalStateException("The class " + getQName() + " already has a field " + s + ".");
    		}
    	}
    	myFields.add(f);
    }
    
    /** Returns the list of constructors that this class has.
     * @see #newJavaConstructor(JavaConstructor, boolean)
     */
    public JavaConstructor[] getConstructors() {
    	List result = new ArrayList();
    	for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
    		ConditionalIndentationJavaSourceObject object =
    			(ConditionalIndentationJavaSourceObject) iter.next();
    		if (object instanceof JavaConstructor) {
    			result.add(object);
    		}
    	}
    	return (JavaConstructor[]) result.toArray(new JavaConstructor[result.size()]);
    }
    /** Adds a constructor to this classes list of constructors.
     * @see #getConstructors()
     */
    void addConstructor(JavaConstructor c) { myObjects.add(c); }
    /** Returns an iterator the the classes constructors. This
     * iterator allows to remove constructors.
     */
    public Iterator getConstructorIterator() {
    	return new MyObjectIterator(JavaConstructor.class);
    }
    
    /** Returns the list of methods that this class has.
     * @see #newJavaMethod(String, JavaQName, Protection)
     */
    public JavaMethod[] getMethods() {
    	List result = new ArrayList();
    	for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
    		ConditionalIndentationJavaSourceObject object = (ConditionalIndentationJavaSourceObject) iter.next();
    		if (object instanceof JavaMethod) {
    			result.add(object);
    		}
    	}
    	return (JavaMethod[]) result.toArray(new JavaMethod[result.size()]);
    }

    private boolean isMatching(Parameter[] parameters, JavaQName[] pParams) {
		if (parameters.length == pParams.length) {
			for (int i = 0;  i < parameters.length;  i++) {
				if (!parameters[i].getType().equals(pParams[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
    }

    /** Returns the method with the given signature or null, if there
     * is no such method.
     */
    public JavaMethod getMethod(String pMethodName, JavaQName[] pParams) {
    	for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
    		ConditionalIndentationJavaSourceObject object = (ConditionalIndentationJavaSourceObject) iter.next();
    		if (object instanceof JavaMethod) {
    			JavaMethod jm = (JavaMethod) object;
    			if (jm.getName().equals(pMethodName)) {
    				if (isMatching(jm.getParams(), pParams)) {
    					return jm;
    				}
    			}
    		}
    	}
    	return null;
    }

    /** Returns the constructor with the given signature or null, if there
     * is no such constructor.
     */
    public JavaConstructor getConstructor(JavaQName[] pParams) {
        for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
            ConditionalIndentationJavaSourceObject object = 
            	(ConditionalIndentationJavaSourceObject) iter.next();
            if (object instanceof JavaConstructor) {
                JavaConstructor jc = (JavaConstructor) object;
                if (isMatching(jc.getParams(), pParams)) {
                	return jc;
                }
            }
        }
        return null;
    }

    private class MyObjectIterator implements Iterator {
    	private final Class instanceClass;
    	MyObjectIterator(Class pInstanceClass) {
    		instanceClass = pInstanceClass;
    	}
    	
    	private Object result = null;
    	private boolean mayRemove;
    	private Iterator inner = myObjects.iterator();
    	public void remove() {
    		if (!mayRemove) {
    			throw new IllegalStateException("remove() is only allowed immediately after next()");
    		}
    		inner.remove();
    		mayRemove = false;
    	}
    	public boolean hasNext() {
    		mayRemove = false;
    		if (result != null) {
    			return true;
    		}
    		while (inner.hasNext()) {
    			Object o = inner.next();
    			if (instanceClass.isAssignableFrom(o.getClass())) {
    				result = o;
    				return true;
    			}
    		}
    		result = null;
    		return false;
    	}
    	public Object next() {
    		if (!hasNext()) {
    			throw new NoSuchElementException();
    		}
    		Object myResult = result;
    		result = null;
    		mayRemove = true;
    		return myResult;
    	}
    }
    
    /** Returns an iterator to the classes methods. This iterator
     * allows to remove certain methods.
     */
    public Iterator getMethodIterator() {
    	return new MyObjectIterator(JavaMethod.class);
    }
    
    
    /** Adds a method to this classes list of methods.
     * @see #getMethods()
     */
    void addMethod(JavaMethod m) { myObjects.add(m); }
    
    
    public void write(IndentationTarget pTarget) throws IOException {
    	if (!isInnerClass()) {
    		String packageName = getPackageName();
    		if (packageName.length() > 0) {
    			pTarget.indent(0);
    			pTarget.write("package ");
    			pTarget.write(packageName);
    			pTarget.write(";");
    			pTarget.write();
    			pTarget.indent(0);
    			pTarget.write();
    		}
    	}
    	
    	JavaQName[] myExtendedClasses = getExtends();
    	JavaQName[] implementedInterfaces = getImplements();
    	JavaInnerClass[] myInnerClasses = getInnerClasses();
    	JavaField[] fields = getFields();
    	getConstructors();
    	getMethods();
    	
    	JavaQName[] myImports = getImports();
    	Arrays.sort(myImports);
    	if (myImports.length > 0) {
    		for (int i = 0;  i < myImports.length;  i++) {
    			pTarget.indent(0);
    			pTarget.write("import ");
    			pTarget.write(myImports[i].toString());
    			pTarget.write(";");
    			pTarget.write();
    		}
    		pTarget.indent(0);
    		pTarget.write();
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	if (comment != null) {
    		comment.write(pTarget);
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	
    	pTarget.indent(0);
    	if (myProtection != null  &&  !myProtection.equals(DEFAULT_PROTECTION)) {
    		pTarget.write(myProtection.toString());
    		pTarget.write(" ");
    	}
    	if (isStatic) {
    		pTarget.write("static ");
    	}
    	if (isAbstract()) {
    		pTarget.write("abstract ");
    	}
    	pTarget.write(getType().toString());
    	pTarget.write(" ");
    	String s = getClassName();
    	int offset = s.lastIndexOf('.');
    	if (offset > -1) {
    		s = s.substring(offset+1);
    	}
    	offset = s.lastIndexOf('$');
    	if (offset > -1) {
    		s = s.substring(offset+1);
    	}
    	pTarget.write(s);
    	pTarget.write(" ");
    	for (int i = 0;  i < myExtendedClasses.length;  i++) {
    		if (i > 0) {
    			pTarget.write(", ");
    		} else {
    			pTarget.write("extends ");
    		}
    		pTarget.write(pTarget.asString(myExtendedClasses[i]));
    		pTarget.write(" ");
    	}
    	if (implementedInterfaces.length > 0) {
    		for (int i = 0;  i < implementedInterfaces.length;  i++) {
    			if (i == 0) {
    				pTarget.write("implements ");
    			} else {
    				pTarget.write(", ");
    			}
    			pTarget.write(pTarget.asString(implementedInterfaces[i]));
    			pTarget.write(" ");
    		}
    	}
    	pTarget.write("{");
    	pTarget.write();
    	
    	IncreasingTarget increasingTarget = new IncreasingTarget(pTarget);
    	for (int i = 0;  i < myInnerClasses.length;  i++) {
    		increasingTarget.setInterface(myInnerClasses[i].isInterface() ?
    				Boolean.TRUE : Boolean.FALSE);
    		myInnerClasses[i].write(increasingTarget);
    		increasingTarget.setInterface(null);
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	
    	if (fields != null  &&  fields.length > 0) {
    		for (int i = 0;  i < fields.length;  i++) {
    			fields[i].write(increasingTarget);
    			pTarget.indent(0);
    			pTarget.write();
    		}
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	
    	for (Iterator iter = myObjects.iterator();  iter.hasNext();  ) {
    		ConditionalIndentationJavaSourceObject object =
    			(ConditionalIndentationJavaSourceObject) iter.next();
    		object.write(increasingTarget);
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	
    	String[] myRawJavaSources = getRawJavaSources();
    	
    	for (int i = 0;  i < myRawJavaSources.length;  i++) {
    		for (StringTokenizer st = new StringTokenizer(myRawJavaSources[i], "\r\n");
    		st.hasMoreTokens();  ) {
    			pTarget.indent(0);
    			String tok = st.nextToken();
    			if (tok.length() > 0) {
    				pTarget.write(tok);
    			}
    			pTarget.write();
    		}
    		pTarget.indent(0);
    		pTarget.write();
    	}
    	pTarget.indent(0);
    	pTarget.write("}");
    	pTarget.write();
    }
    
    /** Returns a quoted string constant suitable for embedding
     * into Java source, but without quotes.
     */
    public static String getQuotedNoQuotes(String s) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0;  i < s.length();  i++) {
    		char c = s.charAt(i);
    		if (c == '\n') {
    			sb.append("\\n");
    		} else if (c == '\\') {
    			sb.append("\\\\");
    		} else if (c == '\t') {
    			sb.append("\\t");
    		} else if (c == '\r') {
    			sb.append("\\r");
    		} else if (c == '\f') {
    			sb.append("\\f");
    		} else if (c == '\"') {
    			sb.append("\\\"");
    		} else {
    			sb.append(c);
    		}
    	}
    	return sb.toString();
    }
    
    /** Returns a quoted string constant suitable for embedding
     * into Java source.
     */
    public static String getQuoted(String s) {
    	return "\"" + getQuotedNoQuotes(s) + "\"";
    }
    
    private java.util.List innerClasses;
    /** Adds an inner class.
     */
    public void addInnerClass(JavaInnerClass pClass) {
    	if (innerClasses == null) {
    		innerClasses = new java.util.ArrayList();
    	}
    	innerClasses.add(pClass);
    }
    /** Clears the list of inner classes.
     */
    public void clearInnerClasses() {
    	innerClasses = null;
    }
    /** Returns the array of inner classes.
     */
    public JavaInnerClass[] getInnerClasses() {
    	if (innerClasses == null) {
    		return new JavaInnerClass[0];
    	}
    	return (JavaInnerClass[])
		innerClasses.toArray(new JavaInnerClass[innerClasses.size()]);
    }
	/** Returns the inner class named <code>pName</code>, or
	 * null, if no such class exists.
	 */
	public JavaInnerClass getInnerClass(String pName) {
		if (innerClasses == null) {
			return null;
		}
		for (int i = 0;  i < innerClasses.size();  i++) {
			JavaInnerClass jic = (JavaInnerClass) innerClasses.get(i);
			if (jic.getQName().getInnerClassName().equals(pName)) {
				return jic;
			}
		}
		return null;
	}
    
    private boolean isStatic;
    /** Returns whether this JavaSource is static (for inner classes).
     */
    protected boolean getStatic() {
    	return isStatic;
    }
    
    /** Sets whether this JavaSource is static (for inner classes).
     */
    public void setStatic(boolean pStatic) {
    	isStatic = pStatic;
    }
    
    private java.util.List rawJavaSources;
    /** Adds a piece of raw Java source to the class.
     */
    public void addRawJavaSource(String pSource) {
    	if (pSource != null  &&  pSource.length() > 0) {
    		if (rawJavaSources == null) {
    			rawJavaSources = new java.util.ArrayList();
    		}
    		rawJavaSources.add(pSource);
    	}
    }
    
    /** Clears the list of raw Java sources.
     */
    public void clearRawJavaSources() {
    	rawJavaSources = null;
    }
    
    /** Returns an array with the pieces of raw Java sources.
     */
    public String[] getRawJavaSources() {
    	if (rawJavaSources == null) {
    		return new String[0];
    	}
    	return (String[]) rawJavaSources.toArray(new String[rawJavaSources.size()]);
    }
    
    private boolean bAbstract;
    /** Returns whether class is abstract.
     */
    public boolean isAbstract() {
    	return bAbstract;
    }
    /** Sets whether this class is abstract.
     */
    public void setAbstract(boolean isAbstract) {
    	this.bAbstract = isAbstract;
    }
    
    /** Returns whether this is an interface or not.
     */
    public boolean isInterface() {
    	return INTERFACE.equals(getType());
    }
    
    /** Returns whether the given JavaQName is a local class.
     * In other words, whether the package name can be omitted
     * when referencing the class.
     */
    public String asString(JavaQName pQName, boolean pAddIfPossible) {
    	return _asString(pQName, pAddIfPossible).replace('$', '.');
    }
    
    private String _asString(JavaQName pQName, boolean pAddIfPossible) {
    	if (isForcingFullyQualifiedName()) {
    		return pQName.toString();
    	}
    	if (pQName.isArray()) {
    		return asString(pQName.getInstanceClass(), pAddIfPossible) + "[]";
    	}
    	if (!pQName.isImportable()) {
    		return pQName.toString();
    	}
    	if ("".equals(pQName.getPackageName())) {
    		return pQName.getClassName();
    	}
    	
    	JavaQName outerQName = pQName;
    	int offset = outerQName.getClassName().indexOf('$');
    	if (offset >= 0) {
    		String className = outerQName.getClassName().substring(0, offset);
    		outerQName = JavaQNameImpl.getInstance(outerQName.getPackageName(), className);
    	}
    	
    	offset = outerQName.getClassName().indexOf('.');
    	if (offset >= 0) {
    		String className = pQName.getClassName().substring(0, offset);
    		outerQName = JavaQNameImpl.getInstance(outerQName.getPackageName(), className);
    	}
    	
    	if (getQName().equals(outerQName)) {
    		return pQName.getClassName();
    	} else if (getQName().getClassName().equals(outerQName.getClassName())) {
    		return pQName.toString();
    	}
    	
    	boolean done = false;
    	boolean imported = false;
    	
    	for (Iterator iter = imports.iterator();  !done  &&  iter.hasNext();  ) {
    		JavaQName jqName = (JavaQName) iter.next();
    		if (jqName.equals(outerQName)) {
    			done = true;
    			imported = true;
    		} else if (outerQName.getClassName().equals(jqName.getClassName())) {
    			done = true;
    			imported = false;
    		}
    	}
    	
    	if (!done) {
    		String packageName = pQName.getPackageName();
    		if (packageName.equals(getPackageName())  ||
    				packageName.equals("java.lang")) {
    			imported = true;
    			done = true;
    		}
    		if (!done) {
    			if (pAddIfPossible) {
    				addImport(outerQName);
    				done = true;
    				imported = true;
    			} else {
    				done = true;
    				imported = false;
    			}
    		}
    	}
    	
    	if (imported) {
    		return pQName.getClassName();
    	} else {
    		return pQName.toString();
    	}
    }
    
    private boolean forcingFullyQualifiedName = false;
    /** Returns, whether class references are always using the
     * fully qualified class name.
     */
    public boolean isForcingFullyQualifiedName() {
    	return forcingFullyQualifiedName;
    }
    /** Sets, whether class references are always using the
     * fully qualified class name.
     */
    public void setForcingFullyQualifiedName(boolean pForcingFullyQualifiedName) {
    	forcingFullyQualifiedName = pForcingFullyQualifiedName;
    }

    private boolean hasDynamicImports = true;
    /** Returns, whether the class is automatically adding imports.
     */
    public boolean hasDynamicImports() {
    	return hasDynamicImports;
    }
    /** Sets, whether the class is automatically adding imports.
     */
    public void setDynamicImports(boolean pDynamicImports) {
    	hasDynamicImports = pDynamicImports;
    }
    
    /** Writes the JavaSource contents into the given Writer.
     */
    public void write(Writer pTarget) throws IOException {
    	if (hasDynamicImports()) {
    		IndentationTarget devNullTarget = new IndentationTarget(){
    			public boolean isInterface() { return JavaSource.this.isInterface(); }
    			public String asString(JavaQName pQName) {
    				return JavaSource.this.asString(pQName, true);
    			}
    			public void write(String pValue) {}
    			public void write() {}
    			public void indent(int i) {}
    		};
    		write(devNullTarget);
    	}
    	WriterTarget wt = new WriterTarget(){
    		public boolean isInterface() { return JavaSource.this.isInterface(); }
    		public String asString(JavaQName pQName) {
    			return JavaSource.this.asString(pQName, false);
    		}
    	};
    	wt.setTarget(pTarget);
    	write(wt);
    }
    
    /** Returns a string representation of this JavaSource file.
     */
    public String toString() {
    	StringWriter sw = new StringWriter();
    	try {
    		write(sw);
    		return sw.toString();
    	} catch (IOException e) {
    		throw new IllegalStateException("Unexcpected IOException while writing into a StringWriter: " + e.getMessage());
    	}
    }
    
    /** Creates a new instance of JavaClassInitializer.
     */
    public JavaClassInitializer newJavaClassInitializer() {
    	JavaClassInitializer result = new JavaClassInitializer();
    	result.setJavaSource(this);
    	myObjects.add(result);
    	return result;
    }
    
    /** Creates a new JavaConstructor with default protection.
     */
    public JavaConstructor newJavaConstructor() {
    	return newJavaConstructor(DEFAULT_PROTECTION);
    }
    
    /** Creates a new JavaConstructor with the given protection.
     */
    public JavaConstructor newJavaConstructor(JavaSource.Protection pProtection) {
    	JavaConstructor result = new JavaConstructor(getClassName(), pProtection);
    	result.setJavaSource(this);
    	this.addConstructor(result);
    	return result;
    }
    
    /** Creates a new JavaConstructor with the given protection.
     * Equivalent to <code>newJavaConstructor(Protection.valueOf(pProtection))</code>.
     */
    public JavaConstructor newJavaConstructor(String pProtection) {
    	return newJavaConstructor(Protection.valueOf(pProtection));
    }
    
    /** Creates a new JavaConstructor with the same parameters,
     * protection and exceptions than the given. Equivalent to
     * <code>newJavaConstructor(pConstructor, false)</code>.
     */
    public JavaConstructor newJavaConstructor(JavaConstructor pConstructor) {
    	return newJavaConstructor(pConstructor, false);
    }
    
    /** Creates a new JavaConstructor with the same signature
     * than the given constructors. Equivalent to
     * <code>newJavaConstructor(pConstructor, false)</code>.
     * If the <code>pSuper</code> argument is true, adds an
     * invocation of the super classes constructor with the
     * same arguments.
     */
    public JavaConstructor newJavaConstructor(JavaConstructor pConstructor, boolean pSuper) {
    	JavaConstructor result = newJavaConstructor(pConstructor.getProtection());
    	List superParams = pSuper ? new ArrayList() : null;
    	Parameter[] params = pConstructor.getParams();
    	for (int i = 0;  i < params.length;  i++) {
    		DirectAccessible p = result.addParam(params[i]);
    		if (pSuper) {
    			if (!superParams.isEmpty()) {
    				superParams.add(", ");
    			}
    			superParams.add(p);
    		}
    	}
    	JavaQName[] exceptions = pConstructor.getExceptions();
    	for (int i = 0;  i < exceptions.length;  i++) {
    		result.addThrows(exceptions[i]);
    	}
    	if (pSuper) {
    		result.addLine("super(", superParams, ");");
    	}
    	return result;
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and default protection.
     */
    public JavaMethod newJavaMethod(String pName, String pType) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType), (Protection) null);
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, String pType, Protection pProtection) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType), pProtection);
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, String pType, String pProtection) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType),
    			Protection.valueOf(pProtection));
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and default protection.
     */
    public JavaMethod newJavaMethod(String pName, JavaQName pType) {
    	return newJavaMethod(pName, pType, DEFAULT_PROTECTION);
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, JavaQName pType, Protection pProtection) {
    	JavaMethod result = new JavaMethod(pName, pType, pProtection);
    	result.setJavaSource(this);
    	addMethod(result);
    	return result;
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, JavaQName pType, String pProtection) {
    	JavaMethod result = new JavaMethod(pName, pType, Protection.valueOf(pProtection));
    	result.setJavaSource(this);
    	addMethod(result);
    	return result;
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and default protection.
     */
    public JavaMethod newJavaMethod(String pName, Class pType) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType), DEFAULT_PROTECTION);
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, Class pType, Protection pProtection) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType), pProtection);
    }
    
    /** Creates a new JavaMethod with the given name, return
     * type and protection.
     */
    public JavaMethod newJavaMethod(String pName, Class pType, String pProtection) {
    	return newJavaMethod(pName, JavaQNameImpl.getInstance(pType),
    			Protection.valueOf(pProtection));
    }

    /** Creates a new JavaField with the given fields signature.
     */
    public JavaField newJavaField(Field pField) {
    	final int modifiers = pField.getModifiers();
    	final JavaField result = newJavaField(pField.getName(), pField.getType(), Protection.valueOf(modifiers));
    	result.setFinal(Modifier.isFinal(modifiers));
    	result.setStatic(Modifier.isStatic(modifiers));
    	result.setTransient(Modifier.isTransient(modifiers));
    	return result;
    }

    /** Creates a new JavaConstructor with the signature of the
     * given constructor.
     */
    public JavaConstructor newJavaConstructor(Constructor pConstructor) {
    	final int modifiers = pConstructor.getModifiers();
    	JavaConstructor result = newJavaConstructor(Protection.valueOf(modifiers));
    	addParameters(result, pConstructor.getParameterTypes());
    	addExceptions(result, pConstructor.getExceptionTypes());
    	return result;
    }
    
    /** Creates a new JavaMethod with the signature of the given method
     * <code>pMethod</code>.
     * More precise:
     * <ol>
     *   <li>The name of the created method is <code>pMethod.getName()</code>.</li>
     *   <li>The return type is set to <code>pMethod.getReturnType()</code>.</li>
     *   <li>The protection is set to that of <code>pMethod</code>.
     *   <li>For any class in <code>pMethod.getParameterTypes()</code>, calls
     *     {@link JavaMethod#addParam(Class,String)} with the parameter names "p0", "p1", ...</li>
     *   <li>For any exception in <code>pMethod.getExceptionTypes()</code>, calls
     *     {@link JavaMethod#addThrows(Class)}.</li>
     * </ol>
     */
    public JavaMethod newJavaMethod(Method pMethod) {
    	final int modifiers = pMethod.getModifiers();
    	JavaMethod result = newJavaMethod(pMethod.getName(),
    			JavaQNameImpl.getInstance(pMethod.getReturnType()),
				JavaSource.Protection.valueOf(modifiers));
    	result.setAbstract(Modifier.isAbstract(modifiers));
    	result.setFinal(Modifier.isFinal(modifiers));
    	result.setStatic(Modifier.isStatic(modifiers));
    	result.setSynchronized(Modifier.isSynchronized(modifiers));
    	addParameters(result, pMethod.getParameterTypes());
    	addExceptions(result, pMethod.getExceptionTypes());
    	return result;
    }

    private void addExceptions(AbstractJavaMethod pMethod, Class[] pExceptions) {
		if (pExceptions != null) {
    		for (int i = 0;  i < pExceptions.length;  i++) {
    			pMethod.addThrows(pExceptions[i]);
    		}
    	}
	}

    private void addParameters(AbstractJavaMethod pMethod, Class[] pParameters) {
		if (pParameters != null) {
    		for (int i = 0;  i < pParameters.length;  i++) {
    			String parameterName = "p" + i;
    			pMethod.addParam(pParameters[i], parameterName);
    		}
    	}
	}
    
    /** Creates a new JavaMethod with the given methods name and signature.
     * This is useful, for example, if you have an interface and derive an
     * implementation. The following values are not cloned:
     * {@link JavaMethod#isAbstract()}, {@link JavaMethod#isStatic()},
     * {@link JavaMethod#isFinal()}, and {@link JavaMethod#isSynchronized()}.
     */
    public JavaMethod newJavaMethod(JavaMethod pMethod) {
    	JavaMethod jm = newJavaMethod(pMethod.getName(), pMethod.getType(), pMethod.getProtection());
    	Parameter[] params = pMethod.getParams();
    	for (int i = 0;  i < params.length;  i++) {
    		jm.addParam(params[i].getType(), params[i].getName());
    	}
    	JavaQName[] exceptions = pMethod.getExceptions();
    	for (int i = 0;  i < exceptions.length;  i++) {
    		jm.addThrows(exceptions[i]);
    	}
    	return jm;
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection.
     */
    public JavaField newJavaField(String pName, JavaQName pType, Protection pProtection) {
    	JavaField result = new JavaField(pName, pType, pProtection);
    	result.setJavaSource(this);
    	addField(result);
    	return result;
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection.
     */
    public JavaField newJavaField(String pName, JavaQName pType, String pProtection) {
    	JavaField result = new JavaField(pName, pType, Protection.valueOf(pProtection));
    	result.setJavaSource(this);
    	addField(result);
    	return result;
    }
    
    /** Creates a new JavaField with the given name, type and
     * default protection.
     */
    public JavaField newJavaField(String pName, JavaQName pType) {
    	return newJavaField(pName, pType, DEFAULT_PROTECTION);
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection.
     */
    public JavaField newJavaField(String pName, String pType, Protection pProtection) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType), pProtection);
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection.
     */
    public JavaField newJavaField(String pName, String pType, String pProtection) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType),
    			Protection.valueOf(pProtection));
    }
    
    /** Creates a new JavaField with the given name, type and
     * default protection.
     */
    public JavaField newJavaField(String pName, String pType) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType), (Protection) null);
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection.
     */
    public JavaField newJavaField(String pName, Class pType, JavaSource.Protection pProtection) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType), pProtection);
    }
    
    /** Creates a new JavaField with the given name, type and
     * protection. Equivalent to <code>newJavaField(pName, pType,
     * Protection.valueOf(pProtecttion)</code>.
     */
    public JavaField newJavaField(String pName, Class pType, String pProtection) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType),
    			Protection.valueOf(pProtection));
    }
    
    /** Creates a new JavaField with the given name, type and
     * default protection.
     */
    public JavaField newJavaField(String pName, Class pType) {
    	return newJavaField(pName, JavaQNameImpl.getInstance(pType));
    }
    
    /** Creates a new JavaInnerClass with the given name and
     * default protection.
     */
    public JavaInnerClass newJavaInnerClass(String pName) {
    	return newJavaInnerClass(pName, DEFAULT_PROTECTION);
    }
    
    /** Creates a new JavaInnerClass with the given name and
     * protection.
     */
    public JavaInnerClass newJavaInnerClass(String pName, Protection pProtection) {
    	JavaQName innerClassName = JavaQNameImpl.getInnerInstance(getQName(), pName);
    	JavaInnerClass result = new JavaInnerClass(this, innerClassName, pProtection);
    	addInnerClass(result);
    	return result;
    }
    
    /** Creates a new JavaInnerClass with the given name and
     * protection.
     */
    public JavaInnerClass newJavaInnerClass(String pName, String pProtection) {
    	return newJavaInnerClass(pName, Protection.valueOf(pProtection));
    }
    
    /** Returns, whether this is an inner class.
     */
    public boolean isInnerClass() {
    	return false;
    }
    /** Creates a new Java property with the given type and name.
     * Shortcut for
     * <pre>
     * String upperCaseName = Character.toUpperCase(pName.charAt(0)) +
     *     pName.substring(1);
     * if (JavaQNameImpl.VOID.equals(pType)) {
     *   newBeanProperty(pType, pName, "is" + upperCaseName, "set" + upperCaseName);
     * } else {
     *   newBeanProperty(pType, pName, "get" + upperCaseName, "set" + upperCaseName);
     * }
     * </pre>
     */
    public void newBeanProperty(JavaQName pType, String pName) {
    	String upperCaseName = Character.toUpperCase(pName.charAt(0)) + pName.substring(1);
    	if (JavaQNameImpl.BOOLEAN.equals(pType)) {
    		newBeanProperty(pType, pName, "is" + upperCaseName, "set" + upperCaseName);
    	} else {
    		newBeanProperty(pType, pName, "get" + upperCaseName, "set" + upperCaseName);
    	}
    }
    
    /** Shortcut for <code>newBeanProperty(JavaQNameImpl.getInstance(pClass), pName)</code>.
     * @see #newBeanProperty(JavaQName, String)
     */
    public void newBeanProperty(Class pClass, String pName) {
    	newBeanProperty(JavaQNameImpl.getInstance(pClass), pName);
    }
    
    /** Shortcut for
     * <pre>
     * newJavaField(pFieldName, pType, JavaSource.PRIVATE);
     * JavaMethod getMethod = newJavaMethod(pGetMethodName, pType, JavaSource.PUBLIC);
     * getMethod.addLine("return this.", pFieldName, ";");
     * JavaMethod setMethod = newJavaMethod(pSetMethodName, JavaQNameImpl.VOID, JavaSource.PUBLIC);
     * setMethod.addParam(pType, pFieldName);
     * setMethod.addLine("this.", pFieldName, " = ", pFieldName, ";");
     * </pre>
     * @param pType The property type
     * @param pFieldName The name of the generated field. The generated field has private
     * access.
     * @param pGetMethodName The name of the generated get method or null, if no such
     * method is being created.
     * @param pSetMethodName The name of the generated set method or null, if no such
     * method is being created.
     */
    public void newBeanProperty(JavaQName pType, String pFieldName,
    		String pGetMethodName, String pSetMethodName) {
    	newJavaField(pFieldName, pType, JavaSource.PRIVATE);
    	if (pGetMethodName != null) {
    		JavaMethod getMethod = newJavaMethod(pGetMethodName, pType, JavaSource.PUBLIC);
    		getMethod.addLine("return this.", pFieldName, ";");
    	}
    	if (pSetMethodName != null) {
    		JavaMethod setMethod = newJavaMethod(pSetMethodName, JavaQNameImpl.VOID, JavaSource.PUBLIC);
    		setMethod.addParam(pType, pFieldName);
    		setMethod.addLine("this.", pFieldName, " = ", pFieldName, ";");
    	}
    }
    
    /** Shortcut for <code>newBeanProperty(JavaQNameImpl.getInstance(pClass),
     * pFieldName, pGetMethodName, pSetMethodName)</code>.
     * @see #newBeanProperty(JavaQName, String, String, String)
     */
    public void newBeanProperty(Class pClass, String pFieldName,
    		String pGetMethodName, String pSetMethodName) {
    	newBeanProperty(JavaQNameImpl.getInstance(pClass), pFieldName, pGetMethodName,
    			pSetMethodName);
    }
}
