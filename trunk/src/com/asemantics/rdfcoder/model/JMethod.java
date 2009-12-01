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


package com.asemantics.rdfcoder.model;

/**
 * Represents a <i>Java</i> method.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JMethod extends JModifiable {

    public static boolean exists(JavaQueryModel qm, String pathToMethod) {
        return qm.methodExists(pathToMethod);
    }

    /**
     * Constructor by sections.
     * @param queryModel
     * @param sections
     * @throws CodeModelException
     */
    protected JMethod(JavaQueryModel queryModel, String[] sections)
            throws QueryModelException {
        super(queryModel, sections);
    }

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToMethod
     * @throws CodeModelException
     */
    protected JMethod(JavaQueryModel queryModel, String pathToMethod)
            throws QueryModelException {
        super(queryModel, pathToMethod);
    }

    /**
     * Returns the signatures of a method.
     *
     * @return list of signatures.
     * @throws CodeModelException
     */
    public JSignature[] getSignatures() throws QueryModelException {
        return getQueryModel().getSignatures(super.getFullName());
    }

    /**
     * Check wether a method exists.
     *
     * @param name
     * @param index
     * @return <code>true</code> if exists.
     */
    public boolean exists( String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index));
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }

}
