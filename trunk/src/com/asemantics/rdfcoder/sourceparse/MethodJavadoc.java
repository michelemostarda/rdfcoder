package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MethodJavadoc extends JavadocEntry {

    private Identifier pathToMethod;

    private JavaCodeModel.JType[] signature;

    private String[] parameterNames;

    private JavaCodeModel.JType returnType; 

    private JavaCodeModel.ExceptionType[] thrownExceptions;

    /**
     * Constructor.
     *
     * @param pathToMethod
     * @param signature
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
            String[] parameterNames,
            JavaCodeModel.JType returnType,
            JavaCodeModel.ExceptionType[] thrownExceptions,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col, modifiers, visibility);
        if(pathToMethod == null){
            throw new NullPointerException();
        }
        if(signature == null){
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
        this.pathToMethod = pathToMethod;
        this.signature = signature;
        this.parameterNames = parameterNames;
        this.returnType = returnType;
        this.thrownExceptions = thrownExceptions;
    }

    public Identifier getPathToMethod() {
        return pathToMethod;
    }

    public JavaCodeModel.JType[] getSignature() {
        return signature;
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
