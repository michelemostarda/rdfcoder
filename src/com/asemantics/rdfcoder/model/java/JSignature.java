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
import com.asemantics.rdfcoder.model.Identifier;

/**
 * Represents a <i>Java</i> method signature.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JSignature extends JBase {

    /**
     * The parameter types of the signature.
     */
    JavaCodeModel.JType[] paramTypes;

    /**
     * The return type of the signature.
     */
    JavaCodeModel.JType returnType;

    protected JSignature(
            JavaQueryModel queryModel,
            Identifier identifier,
            JavaCodeModel.JType[] paramTypes,
            JavaCodeModel.JType returnType
    ) throws QueryModelException {
        super(queryModel, identifier);
        if(paramTypes == null || returnType == null) {
            throw new IllegalArgumentException();
        }

        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    protected JSignature(
        JavaQueryModel queryModel,
        Identifier identifier
    ) throws QueryModelException {
        super(queryModel, identifier);
        paramTypes = queryModel.getParameters(identifier);
        returnType = queryModel.getReturnType(identifier);
    }

    JavaCodeModel.JType[] getParameters() {
        return paramTypes.clone();
    }

    JavaCodeModel.JType getReturnType() {
        return returnType.cloneType();
    }


    public boolean exists(Identifier identifier) {
        return getQueryModel().signatureExists(identifier);
    }

    protected String getHierarchyElemType() {
        return this.getClass().getSimpleName();
    }
}