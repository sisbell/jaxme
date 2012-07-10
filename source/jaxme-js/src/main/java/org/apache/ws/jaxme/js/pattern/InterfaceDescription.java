package org.apache.ws.jaxme.js.pattern;

import java.net.URL;

import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;


/** The <code>InterfaceDescription</code> is used by the
 * {@link ProxyGenerator} as information storage about
 * the interfaces being implemented.<br>
 * The main purporse of an intermediate class is to
 * encapsulate the way, how information about these
 * classes is gathered:
 * <ol>
 *   <li>If the interface being implemented is a compiled
 *     class, then Java reflection is used.</li>
 *   <li>Otherwise, if the interface being implemented is
 *     present as a Java source file, then the
 *     {@link org.apache.ws.jaxme.js.util.JavaParser}
 *     is used.</li>
 * </ol
 */
public class InterfaceDescription {
	private final ClassLoader classLoader;
	private boolean isMandatory = true;
	private String interfaceName;
	private String type;
	private JavaSource javaSource;

	/** Creates a new instance, which uses the given class loader.
	 */
	public InterfaceDescription(ClassLoader pClassLoader) {
		classLoader = pClassLoader;
	}

	/** Sets the name of the interface being implemented.
	 */
	public void setInterface(String pName) {
		interfaceName = pName;
	}
      
	/** Returns the name of the interface being implemented.
	 */
	public String getInterface() {
		return interfaceName;
	}
      
	/** Sets, how to gather information about the interface.
	 * Supported values are "Reflection" (Java reflection),
	 * or "Source" ({@link org.apache.ws.jaxme.js.util.JavaParser}).
	 * The default is null, in which case "Reflection" and "Source"
	 * are tried, in that order.
	 */
	public void setType(String pType) {
		if (pType == null
				||  "Reflection".equalsIgnoreCase(pType)
				||  "Source".equalsIgnoreCase(pType)) {
			type = pType;
		} else {
			throw new IllegalArgumentException("Invalid type: " + pType +
			", expected 'Reflection', 'Source', or null.");
		}
	}
      
	/** Returns, how to gather information about the interface.
	 * Supported values are "Reflection" (Java reflection),
	 * or "Source" ({@link org.apache.ws.jaxme.js.util.JavaParser}).
	 * The default is null, in which case "Reflection" and "Source"
	 * are tried, in that order.
	 */
	public String getType() {
		return type;
	}
	
	/** Sets whether this interface is mandatory. By default interfaces
	 * are mandatory and the backing objects must implement this interface.
	 * If an interface isn't mandatory, then a Proxy instance can be created
	 * even for objects which don't implement the interface. However, in that
	 * case it may happen that a ClassCastException is thrown while invoking
	 * a method declared by the interface.
	 */
	public void setMandatory(boolean pMandatory) { isMandatory = pMandatory; }
	
	/** Returns whether this interface is mandatory. By default interfaces
	 * are mandatory and the backing objects must implement this interface.
	 * If an interface isn't mandatory, then a Proxy instance can be created
	 * even for objects which don't implement the interface. However, in that
	 * case it may happen that a ClassCastException is thrown while invoking
	 * a method declared by the interface.
	 */
	public boolean isMandatory() { return isMandatory; }
	
	/** Returns an instance of {@link JavaSource}, matching
	 * the interface {@link #getInterface()}.
	 */
	public JavaSource getJavaSource() throws Exception {
		if (javaSource == null) {
			javaSource = initJavaSource();
		}
		return javaSource;
	}
	
	/** Initializes the object, after all parameters are set.
	 */
	private JavaSource initJavaSource() throws Exception {
		Exception ex = null;
		String mode = getType();
		if (mode == null  ||  "Reflection".equals(mode)) {
			try {
				Class c = classLoader.loadClass(getInterface());
				if (c != null) {
					return new CompiledClassReflector(c).getJavaSource(new JavaSourceFactory());
				}
			} catch (Exception e) {
				if (ex == null) {
					ex = e;
				}
			}
		}
		if (mode == null  ||  "Source".equals(mode)) {
			URL url = classLoader.getResource(getInterface().replace('.', '/') + ".java");
			if (url != null) {
				SourceReflector reflector = new SourceReflector(url);
				return reflector.getJavaSource(new JavaSourceFactory());
			}
		}
		if (ex == null) {
			throw new IllegalStateException("Failed to locate Java class "
					+ getInterface());
		} else {
			throw ex;
		}
	}
}