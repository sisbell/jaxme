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
package javax.xml.bind;


/**
 * This is a runtime exception, which is used to represent a failure
 * in a JAXB operation. Unlike {@link JAXBException}, it is derived
 * from the {@link RuntimeException}. In other words, it is no
 * checked exception.
 */
@SuppressWarnings("serial") // The RI doesn't specify a serialversionuid.
public class DataBindingException extends RuntimeException {
    /**
     * Creates a new instance with the given detail message
     * and cause.
     * @param message The exceptions detail message.
     * @param cause The exceptions cause.
     */
    public DataBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the given cause.
     * @param cause The exceptions cause.
     */
    public DataBindingException(Throwable cause) {
        super(cause);
    }
}
