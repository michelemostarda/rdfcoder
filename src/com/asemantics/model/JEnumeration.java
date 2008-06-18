/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.model;

/**
 * Represents an enumeration.
 */
public class JEnumeration extends JObject {

    public static boolean exists(JavaQueryModel qm, String pathToEnumeration) {
        return qm.enumerationExists(pathToEnumeration);
    }

    /**
      * Constructor by sections.
      * @param queryModel
      * @param sections
      * @throws CodeModelException
      */
     protected JEnumeration(JavaQueryModel queryModel, String[] sections)
             throws QueryModelException {
         super(queryModel, sections);
     }

     /**
      * Constructor by path.
      * @param queryModel
      * @param pathToMethod
      * @throws CodeModelException
      */
     protected JEnumeration(JavaQueryModel queryModel, String pathToMethod)
             throws QueryModelException {
         super(queryModel, pathToMethod);
     }

     private static final JEnumeration[] EMPTY_ENUMERATION = new JEnumeration[0];

    /**
     *  No enumerations can be defined inside an enumeration.
     * @return
     * @throws QueryModelException
     */
     public JEnumeration[] getEnumerations() throws QueryModelException {
        return EMPTY_ENUMERATION;
     }

    /**
     * Returns the elements defined into this enumeration.
     * @return
     */
     public String[] getElements() {
         return getQueryModel().getElements( getFullName() );   
     }

     public boolean exists( String[] name, int index) {
         return exists(getQueryModel(), concatenate(name, index));
     }

     protected String getHyerarchyElemType() {
         return this.getClass().getSimpleName();
     }

}
