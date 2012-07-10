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

/** <p>The Logger interface describes an object which is able to log
 * a message.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Logger {
   /** <p>Logs the message that a method is entered.</p>
    */
   public void entering(String pMethodName, Object[] pDetails);

   /** <p>Logs the message that a method is entered.</p>
    */
   public void entering(String pMethodName);

   /** <p>Logs the message that a method is entered.</p>
    */
   public void entering(String pMethodName, Object pDetails1);

   /** <p>Logs the message that a method is exiting.</p>
    */
   public void exiting(String pMethodName, Object[] pDetails);

   /** <p>Logs the message that a method is exiting.</p>
    */
   public void exiting(String pMethodName);

   /** <p>Logs the message that a method is exiting.</p>
    */
   public void exiting(String pMethodName, Object pDetails1);

   /** <p>Logs the given Throwable.</p>
    */
   public void throwing(String pMethodName, Throwable pThrowable);

   /** <p>Returns whether finest logging is enabled.</p>
    */
   public boolean isFinestEnabled();

   /** <p>Logs the given message with finest level.</p>
    */
   public void finest(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Logs the given message with finest level.</p>
    */
   public void finest(String pMethodName, String pMsg);

   /** <p>Logs the given message with finest level.</p>
    */
   public void finest(String pMethodName, String pMsg, Object pDetails);

   /** <p>Logs the given message with finer level.</p>
    */
   public void finer(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Returns whether fine logging is enabled.</p>
    */
   public boolean isFinerEnabled();

   /** <p>Logs the given message with finer level.</p>
    */
   public void finer(String pMethodName, String pMsg);

   /** <p>Logs the given message with finer level.</p>
    */
   public void finer(String pMethodName, String pMsg, Object pDetails);

   /** <p>Returns whether fine logging is enabled.</p>
    */
   public boolean isFineEnabled();

   /** <p>Logs the given message with fine level.</p>
    */
   public void fine(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Logs the given message with fine level.</p>
    */
   public void fine(String pMethodName, String pMsg);

   /** <p>Logs the given message with fine level.</p>
    */
   public void fine(String pMethodName, String pMsg, Object pDetails);

   /** <p>Returns whether info logging is enabled.</p>
    */
   public boolean isInfoEnabled();

   /** <p>Logs the given message with info level.</p>
    */
   public void info(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Logs the given message with info level.</p>
    */
   public void info(String pMethodName, String pMsg);

   /** <p>Logs the given message with info level.</p>
    */
   public void info(String pMethodName, String pMsg, Object pDetails);

   /** <p>Returns whether warning logging is enabled.</p>
    */
   public boolean isWarnEnabled();

   /** <p>Logs the given message with warning level.</p>
    */
   public void warn(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Logs the given message with warning level.</p>
    */
   public void warn(String pMethodName, String pMsg);

   /** <p>Logs the given message with warning level.</p>
    */
   public void warn(String pMethodName, String pMsg, Object pDetails);

   /** <p>Returns whether error logging is enabled.</p>
    */
   public boolean isErrorEnabled();

   /** <p>Logs the given message with error level.</p>
    */
   public void error(String pMethodName, String pMsg, Object[] pDetails);

   /** <p>Logs the given message with error level.</p>
    */
   public void error(String pMethodName, String pMsg);

   /** <p>Logs the given message with error level.</p>
    */
   public void error(String pMethodName, String pMsg, Object pDetails);
}
