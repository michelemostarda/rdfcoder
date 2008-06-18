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


package com.asemantics.model;

/**
 * Represents any entity class with visibility and modifiers.
 */
public abstract class JModifiable extends JBase {

    /**
     * Constructor.
     *
     * @param qm
     * @param sections
     * @throws QueryModelException
     */
    protected JModifiable(JavaQueryModel qm, String[] sections) throws QueryModelException {
        super(qm, sections);
    }

    /**
     * Constructor.
     *
     * @param qm
     * @param path
     * @throws QueryModelException
     */
    public JModifiable(JavaQueryModel qm, String path) throws QueryModelException {
        super(qm, path);
    }

    /**
     * Returns the <i>entity</i> visibility.
     *
     * @return
     * @throws QueryModelException
     */
    public JavaCodeModel.JVisibility getVisibility() throws QueryModelException {
        return getQueryModel().getVisibility(super.getFullName());
    }

    /**
     * Returns the <i>entity</i> modifiers.
     * @return
     * @throws QueryModelException
     */
    public JavaCodeModel.JModifier[] getModifiers() throws QueryModelException {
        return getQueryModel().getModifiers(super.getFullName());
    }
}
