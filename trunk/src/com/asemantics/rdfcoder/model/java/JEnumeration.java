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


package com.asemantics.rdfcoder.model.java;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.QueryModelException;

/**
 * Represents a <i>Java</i> enumeration.
 */
public class JEnumeration extends JObject {

    /**
     * Constructor by path.
     *
     * @param queryModel
     * @param pathToMethod
     * @throws com.asemantics.rdfcoder.model.CodeModelException
     *
     */
    protected JEnumeration(JavaQueryModel queryModel, Identifier pathToMethod)
            throws QueryModelException {
        super(queryModel, pathToMethod);
    }

    /**
     * Defines an empty enumeration.
     */
    private static final JEnumeration[] EMPTY_ENUMERATION = new JEnumeration[0];

    /**
     * Returns the name of the enumentation.
     *
     * @return the string identifying the enumeration.
     */
    public String getName() {
        return getIdentifier().getLastFragmentWithQualifier(JavaCodeModel.ENUMERATION_KEY);
    }

    /**
     * No enumerations can be defined inside an enumeration.
     *
     * @return the list of enumerations.
     * @throws QueryModelException
     */
    public JEnumeration[] getEnumerations() throws QueryModelException {
        return EMPTY_ENUMERATION;
    }

    /**
     * Returns the elements defined into this enumeration.
     *
     * @return list of elements.
     */
    public String[] getElements() {
        return getQueryModel().getElements(getIdentifier());
    }

      /**
     * Checks whether the enumeration exists.
     *
     * @param identifier the element to be checked.
     * @return <code>true</code> if exists, <code>false</code> otherwise.
     */
    public boolean exists(Identifier identifier) {
        return getQueryModel().enumerationExists(identifier);
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }

}
