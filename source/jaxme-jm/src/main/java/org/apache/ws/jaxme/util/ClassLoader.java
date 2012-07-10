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
package org.apache.ws.jaxme.util;


/** <p>Helper class for working with class loaders.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: ClassLoader.java 231893 2004-07-27 11:19:25Z jochen $
 */
public class ClassLoader {
  /** <p>Loads a class with the given name. First attempts to use
   * the context class loader, then its own class loader.</p>
   *
   * @param pName The fully qualified name of the class being loaded.
   * @throws ClassNotFoundException Loading the class failed.
   * @return The class with the name <code>pName</code>.
   */
  public static Class getClass(String pName) throws ClassNotFoundException {
    // First try: The default ClassLoader
    try {
      return Class.forName(pName);
    } catch (ClassNotFoundException e) {
      // Second try: The threads context ClassLoader
      try {
        java.lang.ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
          throw new ClassNotFoundException(pName);   
        }
        return cl.loadClass(pName);
      } catch (ClassNotFoundException f) {
        throw e;
      }
    }
  }

  /** <p>Loads a class with the given name using <code>getClass(String)</code>.
   * If an instance of the returned class cannot be assigned to the
   * class or interface <code>pAssignableTo</code>, throws an
   * IllegalArgumentException.</p>
   *
   * @throws ClassNotFoundException The class with the name <code>pName</code>
   *   could not be loaded.
   * @throws IllegalArgumentException Instances of the class with the name
   *   <code>pName</code> are not assignable to the interface or class
   *   <code>pAssignableTo</code>.
   */
  public static Class getClass(String pName, Class pAssignableTo)
      throws ClassNotFoundException {
    Class result = getClass(pName);
    if (pAssignableTo != null  &&  !pAssignableTo.isAssignableFrom(result)) {
      throw new IllegalArgumentException("The class " + result.getName() +
                                          " is not implementing or extending " +
                                          pAssignableTo.getName());
    }
    return result;
  }
}
