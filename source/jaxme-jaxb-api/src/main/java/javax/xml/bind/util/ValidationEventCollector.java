/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.util;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;


/** <p>Simple implementation of a {@link javax.xml.bind.ValidationEventHandler},
 * which simply collects all the events, regardless whether they
 * are warnings, errors, or fatal errors. You may retrieve these events
 * at a later time using {@link #getEvents()}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class ValidationEventCollector implements ValidationEventHandler {
  private List events = new ArrayList();

  /** <p>Creates a new instance of <code>ValidationEventCollector</code>.</p>
   */
  public ValidationEventCollector() {
  }

  /** <p>Returns the events collected so far. Empty array, if no
   * events have been found.</p>
   */
  public ValidationEvent[] getEvents() {
    return (ValidationEvent[]) events.toArray(new ValidationEvent[events.size()]);
  }

  /** <p>Clears the list of collected warnings, errors, and fatal errors.</p>
   */
  public void reset() {
    events.clear();
  }

  /** <p>Returns whether any event has been collected.</p>
   */
  public boolean hasEvents() {
    return !events.isEmpty();
  }

  /** <p>Will always return true.</p>
   */
  public boolean handleEvent(ValidationEvent pEvent) {
    events.add(pEvent);
    return true;
  }
}
