package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link com.asemantics.rdfcoder.sourceparse.JavadocEntry}
 * for class Javadoc representation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ClassJavadoc extends JavadocEntry {

    private Identifier identifier;

    private Identifier extendedClass;

    private Identifier[] implementedInterfaces;

    /**
     * Constructor.
     *
     * @param identifier
     * @param identifier
     * @param extendedClass
     * @param implementedInterfaces
     * @param modifiers
     * @param visibility
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public ClassJavadoc(
            Identifier identifier,
            Identifier extendedClass,
            Identifier[] implementedInterfaces,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(sd, ld, attrs, row, col, modifiers, visibility);
        if(identifier == null) {
            throw new NullPointerException();
        }
        if(visibility == null) {
            throw new NullPointerException();
        }
        if(extendedClass == null) {
            throw new NullPointerException();
        }
        if(implementedInterfaces == null) {
            throw new NullPointerException();
        }
        this.identifier = identifier;
        this.extendedClass = extendedClass;
        this.implementedInterfaces = implementedInterfaces;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Identifier getExtendedClass() {
        return extendedClass;
    }

    public Identifier[] getImplementedInterfaces() {
        return implementedInterfaces;
    }
}
