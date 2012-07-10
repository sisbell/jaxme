package org.apache.ws.jaxme.js.pattern;

import java.lang.reflect.Method;

import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;


/** Reflector for gathering information on a compiled class.
 */
public class CompiledClassReflector implements Reflector {
	private final Class compiledClass;

	/** Creates a new instance of <code>CompiledClassReflector</code>,
	 * reading information from the given class.
	 * @param pClass
	 */
	public CompiledClassReflector(Class pClass) {
		compiledClass = pClass;
	}
	
	/** Creates a new instance of <code>CompiledClassReflector</code>,
	 * which loads the class named <code>pName</code> through
	 * {@link ClassLoader pClassLoader}.
	 */
	public CompiledClassReflector(String pName, ClassLoader pClassLoader)
	        throws ClassNotFoundException {
		this(pClassLoader.loadClass(pName));
	}
	
	/** <p>Converts the given {@link Method} into an instance of
	 * {@link JavaSource}.</p>
	 */
	protected JavaMethod getMethod(JavaSource pSource, Method pMethod) {
		JavaMethod method = pSource.newJavaMethod(pMethod.getName(),
				                                  JavaQNameImpl.getInstance(pMethod.getReturnType()),
												  JavaSource.PUBLIC);
		Class[] classes = pMethod.getParameterTypes();
		for (int i = 0;  i < classes.length;  i++) {
			method.addParam(classes[i], "arg" + i);
		}
		Class[] exceptions = pMethod.getExceptionTypes();
		for (int i = 0;  i < exceptions.length;  i++) {
			method.addThrows(exceptions[i]);
		}
		return method;
	}
	
	/** Returns the compiled class being used to gather information.
	 */
	public Class getCompiledClass() {
		return compiledClass;
	}
	
	/** Reads the interface methods and converts them
	 * into an instance of {@link JavaSource}.
	 */
	public JavaSource getJavaSource(JavaSourceFactory pFactory) {
        Class c = getCompiledClass();
		JavaSource js = new JavaSourceFactory().newJavaSource(JavaQNameImpl.getInstance(c.getName()));
		Method[] methods = c.getMethods();
		for (int i = 0;  i < methods.length;  i++) {
			Method m = methods[i];
			getMethod(js, m);
		}
		return js;
	}
}
