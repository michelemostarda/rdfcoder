package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;

import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ConstructorJavadoc extends JavadocEntry {

    private Identifier constructorIdentifier;

    private String[] signature;

    /**
     * Constructor.
     *
     * @param constructorIdentifier
     * @param signature
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public ConstructorJavadoc(
            Identifier constructorIdentifier,
            String[] signature,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col);
        if(constructorIdentifier == null) {
            throw new NullPointerException();
        }
        if(signature == null) {
            throw new NullPointerException();
        }
        this.constructorIdentifier = constructorIdentifier;
        this.signature = signature;
    }

    public Identifier getConstructorIdentifier() {
        return constructorIdentifier;
    }

    public String[] getSignature() {
        return signature;
    }
}
