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
package org.apache.ws.jaxme.js.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import junit.framework.TestCase;


/** <p>This example demonstrates implementation of multiple
 * inheritance with the ProxyGenerator. The class MyObservableList,
 * an extension of {@link ObservableList}, is a subclass of
 * {@link Observable}, but can also be viewed as a subclass
 * of {@link ArrayList} (or whatever implementation of
 * {@link List} you choose in the constructor. The
 * {@link Observer Observers} are notified whenever an object
 * is added to the list.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: MultipleInheritanceTest.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class MultipleInheritanceTest extends TestCase implements Observer {
  public class MyObservableList extends ObservableList {
    MyObservableList(List pList) {
      super(pList);
    }
    public boolean add(Object o) {
      boolean result = super.add(o);
      setChanged();
      notifyObservers();
      clearChanged();
      return result;
    }
  }

  private int size;
  public void update(Observable o, Object arg) {
    size = ((List) o).size();
  }

  public MultipleInheritanceTest(String arg0) {
    super(arg0);
  }

  public void testObserver() {
    size = 0;
    MyObservableList mol = new MyObservableList(new ArrayList());
    mol.add("s");
    assertEquals(0, size);
    mol.addObserver(this);
    mol.add("t");
    assertEquals(2, size);
    mol.add(new Integer(4));
    assertEquals(3, size);
  }
}
