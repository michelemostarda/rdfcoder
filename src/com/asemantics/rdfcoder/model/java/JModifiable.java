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
 * Represents any <i>Java</i> entity class with visibility and modifiers.
 */
public abstract class JModifiable extends JBase {

    /**
     * Constructor.
     *
     * @param qm
     * @param identifier
     * @throws com.asemantics.rdfcoder.model.QueryModelException
     */
    protected JModifiable(JavaQueryModel qm, Identifier identifier) throws QueryModelException {
        super(qm, identifier);
    }

    /**
     * Returns the <i>entity</i> visibility.
     *
     * @return visibility.
     * @throws QueryModelException
     */
    public JavaCodeModel.JVisibility getVisibility() throws QueryModelException {
        return getQueryModel().getVisibility( super.getIdentifier() );
    }

    /**
     * Returns the <i>entity</i> modifiers.
     * 
     * @return  the modifiers.
     * @throws QueryModelException
     */
    public JavaCodeModel.JModifier[] getModifiers() throws QueryModelException {
        return getQueryModel().getModifiers( super.getIdentifier() );
    }
}
