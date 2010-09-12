package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * Represents the Javadoc for a class field.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FieldJavadoc extends JavadocEntry {

    /**
     * The identifier of the field.
     */
    private Identifier pathToField;

    /**
     * The identifier of the field type.
     */
    private JavaCodeModel.JType fieldType;

    /**
     * Constant field value.
     */
    private String fieldValue;

    /**
     * Constructor.
     *
     * @param pathToField
     * @param fieldType
     * @param value
     * @param modifiers
     * @param visibility
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public FieldJavadoc(
            Identifier pathToField,
            JavaCodeModel.JType fieldType,
            String value,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col, modifiers, visibility);
        if(pathToField == null) {
            throw new NullPointerException();
        }
        if(fieldType == null) {
            throw new NullPointerException();
        }
        this.pathToField = pathToField;
        this.fieldType   = fieldType;
        this.fieldValue  = fieldValue;
    }

    public Identifier getPathToField() {
        return pathToField;
    }

    public JavaCodeModel.JType getPathToType() {
        return fieldType;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
