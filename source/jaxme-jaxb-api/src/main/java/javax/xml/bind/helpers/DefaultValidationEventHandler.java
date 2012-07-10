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
package javax.xml.bind.helpers;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/** <p>Default implementation of a {@link javax.xml.bind.ValidationEventHandler}.
 * Causes the validation to fail as soon as the first error or
 * fatal error is encountered.</p>
 * <p>This instance of {@link javax.xml.bind.ValidationEventHandler} is
 * suitable for use of the unmarshallers or validators default event handler.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class DefaultValidationEventHandler implements ValidationEventHandler {
  static final DefaultValidationEventHandler theInstance =
    new DefaultValidationEventHandler();

  /** <p>Creates a new instance of <code>DefaultValidationEventHandler</code>.</p>
   */
  public DefaultValidationEventHandler() {
  }

  public boolean handleEvent(ValidationEvent event) {
    if (event.getSeverity() == ValidationEvent.WARNING) {
      return true;
    } else {
      return false;
    }
  }
}
