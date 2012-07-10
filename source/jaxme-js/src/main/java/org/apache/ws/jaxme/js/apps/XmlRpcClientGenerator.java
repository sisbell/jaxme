package org.apache.ws.jaxme.js.apps;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaComment;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.pattern.MethodKey;


/** This class generates clients for Apache XML-RPC.
 * The basic idea goes as follows:
 * <ol>
 *   <li>Provide a class implementing the interface {@link XmlRpcCaller}.</li>
 *   <li>Provide a server side class being called via XML-RPC. The class
 *     must have a public default constructor, should be stateless, and
 *     all callable methods must be public instance methods.</li>
 *   <li>Run the generator, specifying a target package.</li>
 * </ol>
 * On the client, use the generated class, as if it were the server
 * side class.
 */
public class XmlRpcClientGenerator {
    private final JavaSourceFactory factory;
    private final String targetPackage;
    private final Map methods = new HashMap();
    private boolean dispatcherImplementsXmlRpcHandler = true;

    /** Returns whether the generated dispatcher implements
     * XmlRpcHandler. The default value is true.
     */
	public boolean isDispatcherImplementsXmlRpcHandler() {
		return dispatcherImplementsXmlRpcHandler;
	}

    /** Sets whether the generated dispatcher implements
     * XmlRpcHandler. The default value is true.
     */
    public void setDispatcherImplementsXmlRpcHandler(
			boolean pDispatcherImplementsXmlRpcHandler) {
		dispatcherImplementsXmlRpcHandler = pDispatcherImplementsXmlRpcHandler;
	}

    /** Creates a new instance with the given factory and target package.
     */
    public XmlRpcClientGenerator(JavaSourceFactory pFactory, String pTargetPackage) {
    	factory = pFactory;
        targetPackage = pTargetPackage;
    }

    /** Returns the factory, that was submitted to the constructor.
     */
    public JavaSourceFactory getFactory() { return factory; }

    /** Returns the target package, that was submitted to the constructor.
     */
    public String getTargetPackage() { return targetPackage; }

    /** Generates a name for the method <code>pMethod</code> and
     * adds it to the method map, the name being the key.
     * @return The generated name.
     */
    protected String addMethod(JavaMethod pMethod) {
    	String className = pMethod.getJavaSource().getQName().toString();
        String methodName = pMethod.getName();
        for (int i = 0;  ;  i++) {
        	String name = className + "-" + methodName;
            if (i > 0) {
            	name += i;
            }
            if (!methods.containsKey(name)) {
            	methods.put(name, pMethod);
                return name;
            }
        }
    }

    /** Converts the result value <code>pValue</code> into the
     * requested type <code>pType</code>.
     */
    protected Object getResultValue(JavaMethod pMethod, JavaQName pType, Object pValue) {
        if (JavaQNameImpl.BOOLEAN.equals(pType)) {
            return new Object[]{"((", Boolean.class, ") ", pValue, ").booleanValue()"};
        } else if (JavaQNameImpl.BYTE.equals(pType)) {
    		return new Object[]{"((", Byte.class, ") ", pValue, ").byteValue()"};
        } else if (JavaQNameImpl.SHORT.equals(pType)) {
            return new Object[]{"((", Short.class, ") ", pValue, ").shortValue()"};
        } else if (JavaQNameImpl.INT.equals(pType)) {
            return new Object[]{"((", Integer.class, ") ", pValue, ").intValue()"};
        } else if (JavaQNameImpl.LONG.equals(pType)) {
            return new Object[]{"((", Long.class, ") ", pValue, ").longValue()"};
        } else if (JavaQNameImpl.FLOAT.equals(pType)) {
            return new Object[]{"((", Float.class, ") ", pValue, ").floatValue()"};
        } else if (JavaQNameImpl.DOUBLE.equals(pType)) {
            return new Object[]{"((", Double.class, ") ", pValue, ").doubleValue()"};
        } else if (pType.isArray()) {
            LocalJavaField resultV = pMethod.newJavaField(Vector.class);
            resultV.addLine("(", Vector.class, ") ", pValue, ";");
            LocalJavaField resultA = pMethod.newJavaField(pType);
            pMethod.addIf(resultV , " == null");
            pMethod.addLine(resultA, " = null;");
            pMethod.addElse();
            pMethod.addLine(resultA, " = new ", pType.getInstanceClass(), "[", resultV, ".size()];");
            DirectAccessible i = pMethod.addForArray(resultA);
            Object element = new Object[]{resultV, ".elementAt(", i, ")"};
            pMethod.addLine(resultA, "[", i, "] = ", getResultValue(pMethod, pType.getInstanceClass(), element), ";");
            pMethod.addEndFor();
            pMethod.addEndIf();
            return resultA;
        } else if (JavaQNameImpl.getInstance(Object.class).equals(pType)) {
            return pValue;
        } else {
        	return new Object[]{"(", pType, ") ", pValue};
        }
    }

    /** Converts the given input <code>pValue</code> with type
     * <code>pType</code> into a valid XML-RPC type.
     */
    protected Object getInputValue(JavaMethod pMethod, JavaQName pType, Object pValue) {
        if (pType.equals(JavaQNameImpl.BOOLEAN)) {
        	return new Object[]{pValue, " ? ", Boolean.class, ".TRUE : ", Boolean.class, ".FALSE"};
        } else if (pType.equals(JavaQNameImpl.BYTE)) {
            return new Object[]{"new ", Byte.class, "(", pValue, ")"};
        } else if (pType.equals(JavaQNameImpl.SHORT)) {
            return new Object[]{"new ", Short.class, "(", pValue, ")"};
        } else if (pType.equals(JavaQNameImpl.INT)) {
            return new Object[]{"new ", Integer.class, "(", pValue, ")"};
        } else if (pType.equals(JavaQNameImpl.LONG)) {
            return new Object[]{"new ", Long.class, "(", pValue, ")"};
        } else if (pType.equals(JavaQNameImpl.FLOAT)) {
            return new Object[]{"new ", Float.class, "(", pValue, ")"};
        } else if (pType.equals(JavaQNameImpl.DOUBLE)) {
            return new Object[]{"new ", Double.class, "(", pValue, ")"};
        } else if (pType.isArray()) {
            if (!(pValue instanceof DirectAccessible)) {
            	LocalJavaField val = pMethod.newJavaField(pType);
                val.addLine(pValue);
                pValue = val;
            }
            LocalJavaField v = pMethod.newJavaField(Vector.class);
            pMethod.addIf(pValue, " == null");
            pMethod.addLine(v, " = null;");
            pMethod.addElse();
            pMethod.addLine(v, " = new ", Vector.class, "();");
            DirectAccessible i = pMethod.addForArray(pValue);
            Object element = new Object[]{pValue, "[", i, "]"};
            pMethod.addLine(v, ".add(", getInputValue(pMethod, pType.getInstanceClass(), element), ");");
            pMethod.addEndFor();
            pMethod.addEndIf();
            return v;
        } else {
            return pValue;
        }
    }

    /** Generates a method, invoking method <code>pMethod</code> using
     * the name <code>pName</code>.
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    protected JavaMethod getMethod(JavaSource pJs, JavaField pCaller,
                                   String pName, JavaMethod pMethod)
            throws SecurityException, NoSuchMethodException {
    	Method m = XmlRpcCaller.class.getMethod("xmlRpcCall", new Class[]{String.class, Vector.class});
        Class[] exceptions = m.getExceptionTypes();
        List exceptionList = new ArrayList();
        if (exceptions != null) {
        	for (int i = 0;  i < exceptions.length;  i++) {
        		JavaQName qName = JavaQNameImpl.getInstance(exceptions[i]);
                if (!pMethod.isThrowing(qName)) {
                	exceptionList.add(qName);
                }
        	}
        }
        
        JavaMethod jm = pJs.newJavaMethod(pMethod);
        LocalJavaField v = jm.newJavaField(Vector.class);
        v.addLine("new ", Vector.class, "()");
        Parameter[] params = jm.getParams();
        for (int i = 0;  i < params.length;  i++) {
        	Parameter p = params[i];
            jm.addLine(v, ".add(", getInputValue(jm, p.getType(), p), ");");
        }
        if (!exceptionList.isEmpty()) {
        	jm.addTry();
        }
        Object result = new Object[]{pCaller, ".xmlRpcCall(",
        		                     JavaSource.getQuoted(pName), ", ", v, ")"};
        if (JavaQNameImpl.VOID.equals(jm.getType())) {
            jm.addLine(result, ";");
        } else {
        	jm.addLine("return ", getResultValue(jm, jm.getType(), result), ";");
        }
        if (!exceptionList.isEmpty()) {
            for (int i = 0;  i < exceptionList.size();  i++) {
            	JavaQName exClass = (JavaQName) exceptionList.get(i);
                DirectAccessible e = jm.addCatch(exClass);
                jm.addThrowNew(UndeclaredThrowableException.class, e);
            }
            jm.addEndTry();
        }
        return jm;
    }

    protected JavaField getXmlRpcCaller(JavaSource pJs) {
    	JavaField jf = pJs.newJavaField("caller", XmlRpcCaller.class, JavaSource.PRIVATE);
        jf.setFinal(true);
        return jf;
    }

    protected JavaConstructor getConstructor(JavaSource pJs, JavaField jf) {
        JavaConstructor jcon = pJs.newJavaConstructor(JavaSource.PUBLIC);
        Parameter param = jcon.addParam(XmlRpcCaller.class, "pCaller");
        jcon.addLine(jf, " = ", param, ";");
        return jcon;
    }

    /** Returns, whether a remote method call is generated for method
     * <code>pMethod</code>. The default implementation returns true,
     * if the method is public and not static.
     */
    protected boolean isMethodGenerated(JavaMethod pMethod) {
    	return JavaSource.PUBLIC.equals(pMethod.getProtection())
          &&  !pMethod.isStatic();
    }

    /** Creates a new client class, which is invoking the given
     * server side class <code>pSource</code>.
     */
    public JavaSource addClass(JavaSource pSource, JavaSourceResolver pResolver)
            throws SecurityException, NoSuchMethodException {
        JavaSource js = getFactory().newJavaSource(JavaQNameImpl.getInstance(getTargetPackage(), pSource.getQName().getClassName()), JavaSource.PUBLIC);
        JavaField jf = getXmlRpcCaller(js);
        getConstructor(js, jf);
        Map keys = new HashMap();
        addMethods(js, pSource, keys, jf, pResolver);
        return js;
    }

    protected void addMethods(JavaSource pResult, JavaSource pSource, Map pKeys,
                              JavaField pField, JavaSourceResolver pResolver)
            throws SecurityException, NoSuchMethodException {
        JavaMethod[] methods = pSource.getMethods();
        for (int i = 0;  i < methods.length;  i++) {
            JavaMethod m = methods[i];
            if (isMethodGenerated(m)) {
                MethodKey key = new MethodKey(m);
                if (pKeys.containsKey(key)) {
                	continue;
                }
            	String name = addMethod(m);
            	pKeys.put(key, getMethod(pResult, pField, name, m));
            }
        }
        if (pResolver != null) {
            JavaQName[] qNames = pSource.getExtends();
            for (int i = 0;  i < qNames.length;  i++) {
            	JavaSource js = pResolver.getJavaSource(qNames[i]);
                if (js != null) {
                	addMethods(pResult, js, pKeys, pField, pResolver);
                }
            }
        }
    }

    /** Generates the abstract invoker class.
     */
    public JavaSource getInvokerClass(JavaSource pSource) {
        JavaInnerClass invoker = pSource.newJavaInnerClass("Invoker", JavaSource.PUBLIC);
        JavaComment comment = invoker.newComment();
        comment.addLine("The dispatcher is implemented with a {@link java.util.Map}.");
        comment.addLine("The map keys are the method names, the values");
        comment.addLine("are instances of <code>Invoker</code>.");
        invoker.setType(JavaSource.INTERFACE);
        JavaMethod jm = invoker.newJavaMethod("invoke", Object.class, JavaSource.PUBLIC);
        comment = jm.newComment();
        comment.addLine("This method creates a new instance of the class being");
        comment.addLine("called, converts the parameter objects (if required)");
        comment.addLine("and invokes the requested method. If required, the");
        comment.addLine("result is converted also and returned.");
        jm.addParam(Vector.class, "pParams");
        jm.addThrows(Throwable.class);
        return invoker;
    }

    /** Creates the field with the {@link Map} of invokers.
     */
    protected JavaField getInvokerMap(JavaSource pSource) {
    	JavaField result = pSource.newJavaField("map", Map.class, JavaSource.PRIVATE);
    	result.addLine("new ", HashMap.class, "()");
        return result;
    }

    /** Creates a new invoker class for the given method.
     */
    protected JavaSource getInvoker(JavaSource pSource, JavaMethod pMethod, JavaQName pInvoker, int pNum) {
        Parameter[] params = pMethod.getParams();

        JavaInnerClass js = pSource.newJavaInnerClass("Invoker" + pNum, JavaSource.PUBLIC);
        StringBuffer sb = new StringBuffer();
        for (int i = 0;  i < params.length;  i++) {
        	if (i > 0) {
        		sb.append(", ");
            }
            sb.append(params[i].getType());
        }
        JavaComment comment = js.newComment();
        comment.addLine("Invoker for method " + pMethod.getName() + "(" + sb + ")");
        comment.addLine("in class " + pMethod.getJavaSource().getQName() + ".");
        js.setStatic(true);
        js.addImplements(pInvoker);
        JavaMethod jm = js.newJavaMethod("invoke", Object.class, JavaSource.PUBLIC);
        Parameter param = jm.addParam(Vector.class, "params");
        JavaQName[] classes = pMethod.getExceptions();
        for (int i = 0;  i < classes.length;  i++) {
        	jm.addThrows(classes[i]);
        }
        List args = new ArrayList();
        for (int i = 0;  i < params.length;  i++) {
        	if (i > 0) {
        		args.add(", ");
            }
            Parameter p = params[i];
            args.add(getResultValue(jm, p.getType(), new Object[]{param, ".elementAt(" + i + ")"}));
        }
        Object o = new Object[]{"new ", pMethod.getJavaSource().getQName(), "().",
        		                pMethod.getName(), "(", args, ")"};
        if (JavaQNameImpl.VOID.equals(pMethod.getType())) {
        	jm.addLine(o, ";");
            jm.addLine("return null;");
        } else {
            jm.addLine("return ", getInputValue(jm, pMethod.getType(), o), ";");
        }
        return js;
    }

    /** Creates the dispatchers constructor.
     */
    public JavaConstructor getDispatcherConstructor(JavaSource pSource,
                                                    JavaField pMap,
                                                    JavaQName pInvoker) {
        JavaConstructor con = pSource.newJavaConstructor(JavaSource.PUBLIC);
        JavaComment comment = con.newComment();
        comment.addLine("Creates a new dispatcher.");
        int num = 0;
        for (Iterator iter = methods.entrySet().iterator();  iter.hasNext();  ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            JavaMethod method = (JavaMethod) entry.getValue();
            JavaSource innerClass = getInvoker(pSource, method, pInvoker, num++);
            con.addLine(pMap, ".put(", JavaSource.getQuoted(name), ", new ", innerClass.getQName(), "());");
        }
        return con;
    }

    /** Creates the dispatchers <code>getInvoker</code> method.
     */
    protected JavaMethod getGetInvokerMethod(JavaSource pSource, JavaQName pInvoker, JavaField pMap) {
    	JavaMethod jm = pSource.newJavaMethod("getInvoker", pInvoker, JavaSource.PROTECTED);
        Parameter param = jm.addParam(String.class, "pName");
        jm.addLine("return (", pInvoker, ") ", pMap, ".get(", param, ");");
        return jm;
    }

    /** Creates the dispatchers <code>invoke</code> method.
     */
    protected JavaMethod getDispatcherInvokeMethod(JavaSource pSource, JavaQName pInvoker) {
        JavaMethod jm = pSource.newJavaMethod("execute", Object.class, JavaSource.PUBLIC);
        JavaComment comment = jm.newComment();
        comment.addLine("Called for invocation of method <code>pName</code> with");
        comment.addLine("the parameters given by <code>pParams</code>.");
        Parameter name = jm.addParam(String.class, "pName");
        Parameter args = jm.addParam(Vector.class, "pParams");
        jm.addThrows(Exception.class);
        LocalJavaField invoker = jm.newJavaField(pInvoker);
        invoker.addLine("getInvoker(", name, ")");
        jm.addIf(invoker, " == null");
        jm.addThrowNew(IllegalStateException.class,
                       JavaSource.getQuoted("Unknown method name: "),
                       " + ", name);
        jm.addEndIf();
        jm.addTry();
        jm.addLine("return ", invoker, ".invoke(", args, ");");
        DirectAccessible e = jm.addCatch(Exception.class);
        jm.addLine("throw ", e, ";");
        DirectAccessible t = jm.addCatch(Throwable.class);
        jm.addThrowNew(UndeclaredThrowableException.class, t);
        jm.addEndTry();
        return jm;
    }

    /** Creates the dispatcher class. Make sure, that this method
     * is invoked <em>after</em> {@link #addClass(JavaSource, JavaSourceResolver)}!
     * @param pQName Fully qualified class name of the dispatcher class.
     */
    public JavaSource getDispatcher(JavaQName pQName) {
    	JavaSource js = getFactory().newJavaSource(pQName, JavaSource.PUBLIC);
        if (isDispatcherImplementsXmlRpcHandler()) {
        	js.addImport(JavaQNameImpl.getInstance("org.apache.xmlrpc.XmlRpcHandler"));
        }
    	JavaComment comment = js.newComment();
        comment.addLine("The dispatcher is being used by the XmlRpcServer.");
        comment.addLine("It delegates incoming XML-RPC calls to the classes");
        comment.addLine("and methods, for which client classes have been");
        comment.addLine("created by the " +
                        XmlRpcClientGenerator.class.getName() + ".");
        JavaSource invoker = getInvokerClass(js);
        JavaField map = getInvokerMap(js);
        getDispatcherConstructor(js, map, invoker.getQName());
        getGetInvokerMethod(js, invoker.getQName(), map);
        getDispatcherInvokeMethod(js, invoker.getQName());
        return js;
    }
}
