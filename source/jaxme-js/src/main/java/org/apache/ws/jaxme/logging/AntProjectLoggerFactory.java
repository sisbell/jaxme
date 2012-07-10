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
package org.apache.ws.jaxme.logging;

import org.apache.tools.ant.Task;


/** <p>A LoggerFactory logging via an Ant project.</p>
 */
public class AntProjectLoggerFactory extends LoggerFactoryImpl {
    final Task task;

    /** Creates a new logger factory for the given task.
     */
    public AntProjectLoggerFactory(Task pTask) {
        task = pTask;
    }

    public Logger newLogger(String pName) {
        return new AntProjectLogger(pName, task);
    }
}
