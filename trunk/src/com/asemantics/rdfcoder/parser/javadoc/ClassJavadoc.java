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


package com.asemantics.rdfcoder.parser.javadoc;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link JavadocEntry}
 * for class Javadoc representation.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ClassJavadoc extends JavadocEntry {

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
        super(identifier, sd, ld, attrs, row, col, modifiers, visibility);
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
        this.extendedClass = extendedClass;
        this.implementedInterfaces = implementedInterfaces;
    }

    public Identifier getExtendedClass() {
        return extendedClass;
    }

    public Identifier[] getImplementedInterfaces() {
        return implementedInterfaces;
    }
}
