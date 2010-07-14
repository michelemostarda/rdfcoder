package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;

import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MethodJavadoc extends JavadocEntry {

    private Identifier pathToMethod;

    private String[] signature;

    /**
     * Constructor.
     *
     * @param pathToMethod
     * @param signature
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public MethodJavadoc(
            Identifier pathToMethod,
            String[] signature,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col);
        if(pathToMethod == null){
            throw new NullPointerException();
        }
        if(signature == null){
            throw new NullPointerException();
        }
        this.pathToMethod = pathToMethod;
        this.signature = signature;
    }

    public Identifier getPathToMethod() {
        return pathToMethod;
    }

    public String[] getSignature() {
        return signature;
    }
}
