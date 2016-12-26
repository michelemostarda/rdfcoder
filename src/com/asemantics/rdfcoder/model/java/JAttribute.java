/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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
import com.asemantics.rdfcoder.model.Identifier;

/**
 * Represents a <i>Java</i> attribute.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JAttribute extends JModifiable {

    /**
     * Checks if the path to the attribute exists.
     * @param qm the <code>QueryModel</code>.
     * @param pathToAttribute the path to the attribute.
     * @return true if the path is valid, false otherwise.
     */
    public static final boolean exists(JavaQueryModel qm, final Identifier pathToAttribute) {
        return qm.attributeExists(pathToAttribute);
    }

    /**
     * Constructor.
     * @param qm
     * @param pathToAttribute
     * @throws com.asemantics.rdfcoder.model.CodeModelException
     */
    protected JAttribute(JavaQueryModel qm, Identifier pathToAttribute)
    throws QueryModelException {
        super(qm, pathToAttribute);
    }

    /**
     * Returns the name of the attribute.
     *
     * @return the string representing the name of the attribute.
     */
    public String getName() {
        return getIdentifier().getLastFragmentWithQualifier(JavaCodeModel.ATTRIBUTE_KEY);
    }

    /**
     * Returns the attribute parent class.
     *
     * @return the parent class.
     */
    public JClass getParentClass() {
        return (JClass) getParent();
    }

    /**
     * Returns the hierachy element name.
     *
     * @return the name of the element.
     */
    public String getHierarchyElemType() {
        return this.getClass().getName();
    }

    /**
     * Check wheter the attribute exists.
     *
     * @param identifier
     * @return <code>true</code> if exists.
     */
    public boolean exists(final Identifier identifier) {
        return exists(getQueryModel(), identifier);
    }

}
