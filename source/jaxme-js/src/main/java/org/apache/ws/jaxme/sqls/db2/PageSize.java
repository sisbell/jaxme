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
package org.apache.ws.jaxme.sqls.db2;

/** <p>A DB2 page size is limited to certain values.</p>
 */
public class PageSize {
   /** The default page size (4096 bytes).
    */
   public static final PageSize PAGESIZE_4096 = new PageSize(4096);
   /** The default page size (8192 bytes).
    */
   public static final PageSize PAGESIZE_8192 = new PageSize(8192);
   /** The default page size (16384 bytes).
    */
   public static final PageSize PAGESIZE_16384 = new PageSize(16384);
   /** The default page size (32768 bytes).
    */
   public static final PageSize PAGESIZE_32768 = new PageSize(32768);

   private static final PageSize[] instances = new PageSize[]{
      PAGESIZE_4096, PAGESIZE_8192, PAGESIZE_16384, PAGESIZE_32768
   };

   private long size;

   private PageSize(long pSize) {
      size = pSize;
   }
 
   /** Returns the size of the pagesize specification.
    */
   public long getSize() { return size; }
   public String toString() { return "PAGESIZE_" + size; }
   public boolean equals(Object pOther) {
      return pOther != null  &&  pOther instanceof PageSize  &&
         size == ((PageSize) pOther).size;
   }

   /** Returns the possible pagesize specifications.
    */
   public static PageSize[] getInstances() { return instances; }

   /** Converts the given string into a pagesize specification.
    */
   public static PageSize valueOf(String pSize) {
      long l;
      try {
         l = Long.parseLong(pSize);
      } catch (Exception e) {
         throw new IllegalArgumentException("PageSize is no long value: " + pSize);
      }
      return valueOf(l);
   }

   /** Converts the given long value into a pagesize specification.
    */
   public static PageSize valueOf(long pSize) {
      for (int i = 0;  i < instances.length;  i++) {
         if (instances[i].size == pSize) {
            return instances[i];
         }
      }
      throw new IllegalArgumentException("Invalid page size: " + pSize);
   }

   public int hashCode() {
      return (int) size / 4096;
   }
}
