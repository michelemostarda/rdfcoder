package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ConstructorJavadoc extends JavadocEntry {

    private Identifier constructorIdentifier;

    private JavaCodeModel.JType[] signature;

    private String[] parameterNames;

    private JavaCodeModel.ExceptionType[] exceptions;

    /**
     * Constructor.
     *
     * @param constructorIdentifier
     * @param signature
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
        if(signature == null) {
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
        this.parameterNames = paramNames;
        this.exceptions = exceptions;
    }

    public Identifier getConstructorIdentifier() {
        return constructorIdentifier;
    }

    public JavaCodeModel.JType[] getSignature() {
        return signature;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public JavaCodeModel.ExceptionType[] getExceptions() {
        return exceptions;
    }
}
