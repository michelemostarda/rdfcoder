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

import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.CodeHandler;

/**
 * Represents a <i>Java</i> interface.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JInterface extends JObject {

    /**
     * Check if an interface exists.
     *
     * @param qm
     * @param name
     * @param index
     * @return <code>true</code> if exists.
     */
    public static boolean exists(JavaQueryModel qm, final String name[], int index) {
       return qm.interfaceExists( concatenate(name, index) );
    }

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToInterface
     * @throws com.asemantics.rdfcoder.model.QueryModelException
     */
    protected JInterface(JavaQueryModel queryModel, String pathToInterface)
    throws QueryModelException {

        super(queryModel, pathToInterface);
    }

    public JavaCodeModel.JVisibility getVisibility() throws QueryModelException {
        return JavaCodeModel.JVisibility.PUBLIC;
    }

    /**
     * Constructor by sections.
     * @param qm
     * @param sections
     * @throws QueryModelException
     */
    protected JInterface(JavaQueryModel qm, String[] sections) throws QueryModelException {
        super(qm, sections);
    }

    public boolean exists(final String[] name, int index) {
        return exists(getQueryModel(), name, index);
    }

    /**
     * Returns <code>true</code> if this is a inner interface,
     * false otherwise.
     *
     * @return <code>true</code> if inner.
     */
    public boolean isInnerInterface() {
        return parent instanceof JClass || parent instanceof JInterface;
    }

    /**
     * Return the parent class if this is an inner class
     * or <code>null</code> otherwise.
     *
     * @return the parent class.
     */
    public JClass getParentClass() {
        if(parent instanceof JClass) {
            return (JClass) parent;
        }
        return null;
    }

    /**
     * Return the parent package if this is first level class
     * or <code>null</code> otherwise.
     *
     * @return the parent package.
     */

    public JPackage getParentPackage() {
        if(parent instanceof JPackage) {
            return (JPackage) parent;
        }
        return null;
    }

    public String toString() {
        return parent.toString() + CodeHandler.PACKAGE_SEPARATOR + super.toString();
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }
}
