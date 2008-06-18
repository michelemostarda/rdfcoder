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
 * Represents a code package.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JPackage extends JContainer {

    /**
     * Check wether a package exists.
     * @param qm
     * @param pathToPackege
     * @return
     */
    public static boolean exists(JavaQueryModel qm,  String pathToPackege) {
        return qm.packageExists( pathToPackege );
    }

    /**
     * Constructor.
     * @param codeModel
     * @param packageSections
     */
    protected JPackage(JavaQueryModel codeModel, String[] packageSections)
    throws QueryModelException {
        super(codeModel, packageSections);
    }

    /**
     * Constructor.
     * @param codeModel
     * @param name
     */
    protected JPackage(JavaQueryModel codeModel, String name)
    throws QueryModelException {
        super(codeModel, name);
    }

    public boolean exists(String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index));
    }

    protected String getHyerarchyElemType() {
        return this.getClass().getSimpleName();
    }

    public JavaCodeModel.JVisibility getVisibility() throws QueryModelException {
        return JavaCodeModel.JVisibility.PUBLIC;
    }
}
