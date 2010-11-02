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
 * Represents the Javadoc for a class field.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class FieldJavadoc extends JavadocEntry {

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
     * @param fieldValue
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
            String fieldValue,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String sd, String ld, Map<String, List<String>> attrs, int row, int col
    ) {
        super(pathToField, sd, ld, attrs, row, col, modifiers, visibility);
        if(pathToField == null) {
            throw new NullPointerException();
        }
        if(fieldType == null) {
            throw new NullPointerException();
        }
        this.fieldType   = fieldType;
        this.fieldValue  = fieldValue;
    }

    public JavaCodeModel.JType getPathToType() {
        return fieldType;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
