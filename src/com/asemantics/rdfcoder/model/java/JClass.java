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
 * Represents a <i>Java</i> class.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JClass extends JObject {

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToClass
     * @throws com.asemantics.rdfcoder.model.QueryModelException
     */
    protected JClass(JavaQueryModel queryModel, Identifier pathToClass)
    throws QueryModelException {
        super(queryModel, pathToClass);
    }

    /**
     * Returns the class name.
     *
     * @return the string representing the name of the class.
     */
    public String getName() {
        return getIdentifier().getLastFragmentWithQualifier(JavaCodeModel.CLASS_KEY);
    }

    public boolean exists(Identifier identifier) {
        return getQueryModel().classExists(identifier);
    }

    /**
     * Returns <code>true</code> if this is an inner class,
     * false otherwise.
     *
     * @return <code>true</code> if inner.
     */
    public boolean isInnerClass() {
        return parent instanceof JClass || parent instanceof JInterface;
    }

    /**
     * Return the parent class if this is an inner class.
     *
     * or <code>null</code> otherwise.
     * @return the class of the parent.
     */
    public JClass getParentClass() {
        if(parent instanceof JClass) {
            return (JClass) parent;
        }
        return null;
    }

    /**
     * Return the parent package if this is firs level class
     * or <code>null</code> otherwise.
     *
     * @return the package of the parent.
     */

    public JPackage getParentPackage() {
        if(parent instanceof JPackage) {
            return (JPackage) parent;
        }
        return null;
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }
    
}
