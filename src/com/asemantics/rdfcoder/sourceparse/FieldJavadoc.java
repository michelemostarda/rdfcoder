package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;

import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FieldJavadoc extends JavadocEntry {

    private Identifier pathToField;

    /**
     * Constructor.
     *
     * @param pathToField
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public FieldJavadoc(
            Identifier pathToField,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col);
        if(pathToField == null) {
            throw new NullPointerException();
        }
        this.pathToField = pathToField;
    }

    public Identifier getPathToField() {
        return pathToField;
    }
}
