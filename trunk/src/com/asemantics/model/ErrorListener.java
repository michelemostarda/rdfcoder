/**
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
 * This class allows to collect errors and warnings occurred during the parsing process.
 */
public interface ErrorListener {

    /**
     * Notifies if a class decleres a package different from the package it belongs to.
     *
     * @param codeHandler
     * @param processedPackage
     * @param declaredContainerPackage
     */
    public void packageDiscrepancy(CodeHandler codeHandler, String processedPackage, String declaredContainerPackage);

    /**
     * Notifies parse errors during the compilation process.
     *
     * @param codeHandler
     * @param location
     * @param description
     */
    public void parseError(CodeHandler codeHandler, String location, String description);

    /**
     * Notifies the list of the unresolved types.
     *
     * @param codeHandler
     * @param types
     */
    public void unresolvedTypes(CodeHandler codeHandler, String[] types);
}
