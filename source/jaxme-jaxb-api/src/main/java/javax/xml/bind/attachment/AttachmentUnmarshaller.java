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
package javax.xml.bind.attachment;

import javax.activation.DataHandler;

/**
 * The attachment unmarshaller allows to read some parts
 * of the generated XML as binary attachments (MTOM
 * or SWA attachments).
 * @since JAXB 2.0
 * @see javax.xml.bind.Unmarshaller#setAttachmentUnmarshaller(AttachmentUnmarshaller)
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
 * @see <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">WS-I Attachments Profile Version 1.0.</a>
 * @see <a href="http://www.w3.org/TR/xml-media-types/">Describing Media Content of Binary Data in XML</a>
 */
public abstract class AttachmentUnmarshaller {
   /**
    * Asks for an attachments data and returns it as a
    * {@link DataHandler}.
    * @param Content ID of the attachment.
    * @return A {@link DataHandler} for reading the attachments data.
    * @throws IllegalArgumentException The content ID is unknown.
    */
   public abstract DataHandler getAttachmentAsDataHandler(String cid);

    /**
    * Asks for an attachments data and returns it as a
    * byte array.
    * @param Content ID of the attachment.
    * @return A byte array with the attachments data.
    * @throws IllegalArgumentException The content ID is unknown.
     */
    public abstract byte[] getAttachmentAsByteArray(String cid);

    /**
     * Returns, whether the unmarshaller ought to support XOP
     * processing.
     */
    public boolean isXOPPackage() { return false; } 
}


