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


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * Represents the Javadoc of to a class constructor.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ConstructorJavadoc extends JavadocEntry {

    private Identifier constructorIdentifier;

    private JavaCodeModel.JType[] signature;

    private String signatureStr;

    private String[] parameterNames;

    private JavaCodeModel.ExceptionType[] exceptions;

    /**
     * Constructor.
     *
     * @param constructorIdentifier
     * @param signature
     * @param signatureStr
     * @param paramNames
     * @param exceptions
     * @param modifiers
     * @param visibility
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public ConstructorJavadoc(
            Identifier constructorIdentifier,
            JavaCodeModel.JType[] signature,
            String signatureStr,
            String[] paramNames,
            JavaCodeModel.ExceptionType[] exceptions,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col, modifiers, visibility);
        if(constructorIdentifier == null) {
            throw new NullPointerException("identifier cannot be null.");
        }
        if(signature == null || signatureStr == null) {
            throw new NullPointerException("signature cannot be null.");
        }
        if(paramNames == null) {
            throw new NullPointerException("parameter names list cannot be null.");
        }
        if(exceptions == null) {
            throw new NullPointerException("exception types cannot be null.");
        }
        if(signature.length != paramNames.length) {
            throw new IllegalArgumentException(
                    "The size of signature must correspond with the size of parameters names."
            );
        }
        this.constructorIdentifier = constructorIdentifier;
        this.signature = signature;
        this.signatureStr = signatureStr;
        this.parameterNames = paramNames;
        this.exceptions = exceptions;
    }

    public Identifier getConstructorIdentifier() {
        return constructorIdentifier;
    }

    public JavaCodeModel.JType[] getSignature() {
        return signature;
    }

    public String getSignatureStr() {
        return signatureStr;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public JavaCodeModel.ExceptionType[] getExceptions() {
        return exceptions;
    }
}
