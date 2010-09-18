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
 * Represents the Javadoc associated to a Java class method.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class MethodJavadoc extends JavadocEntry {

    private JavaCodeModel.JType[] signature;

    private String signatureStr;

    private String[] parameterNames;

    private JavaCodeModel.JType returnType; 

    private JavaCodeModel.ExceptionType[] thrownExceptions;

    /**
     * Constructor.
     *
     * @param pathToMethod
     * @param signature
     * @param signatureStr
     * @param parameterNames
     * @param returnType
     * @param thrownExceptions
     * @param modifiers
     * @param visibility
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public MethodJavadoc(
            Identifier pathToMethod,
            JavaCodeModel.JType[] signature,
            String signatureStr,
            String[] parameterNames,
            JavaCodeModel.JType returnType,
            JavaCodeModel.ExceptionType[] thrownExceptions,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(pathToMethod, sd, ld, attrs, row, col, modifiers, visibility);
        if(pathToMethod == null){
            throw new NullPointerException();
        }
        if(signature == null || signatureStr == null){
            throw new NullPointerException("signature cannot be null.");
        }
        if(parameterNames == null) {
            throw new NullPointerException("parameter names list cannot be null.");
        }
        if(returnType == null) {
            throw new NullPointerException("return type cannot be null.");
        }
        if(thrownExceptions == null) {
            throw new NullPointerException("thrown exceptions list cannot be null.");
        }
        this.signature = signature;
        this.signatureStr = signatureStr;
        this.parameterNames = parameterNames;
        this.returnType = returnType;
        this.thrownExceptions = thrownExceptions;
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

    public JavaCodeModel.ExceptionType[] getThrownExceptions() {
        return thrownExceptions;
    }

    public JavaCodeModel.JType getReturnType() {
        return returnType;
    }
}
