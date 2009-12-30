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
 * Represents a <i>Java</i> method.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JMethod extends JModifiable {

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToMethod
     * @throws com.asemantics.rdfcoder.model.CodeModelException
     */
    protected JMethod(JavaQueryModel queryModel, Identifier pathToMethod)
            throws QueryModelException {
        super(queryModel, pathToMethod);
    }

    /**
     * Returns the method name.
     *
     * @return the string identifying the method.
     */
    public String getName() {
        return getIdentifier().getLastFragmentWithQualifier(JavaCodeModel.METHOD_KEY);
    }

    /**
     * Returns the signatures of a method.
     *
     * @return list of signatures.
     * @throws com.asemantics.rdfcoder.model.CodeModelException
     */
    public JSignature[] getSignatures() throws QueryModelException {
        return getQueryModel().getSignatures( super.getIdentifier() );
    }

    /**
     * Check wether a method exists.
     *
     * @param identifier
     * @return <code>true</code> if exists.
     */
    public boolean exists(Identifier identifier) {
        return getQueryModel().methodExists(identifier);
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }

}
