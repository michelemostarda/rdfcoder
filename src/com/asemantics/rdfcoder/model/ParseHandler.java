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


package com.asemantics.rdfcoder.model;

/**
 * Defines the interface exposed by a <i>Parse Handler</i>, that
 * is the handler receiving messages from a
 * {@link com.asemantics.rdfcoder.parser.CodeParser}.
 */
public interface ParseHandler {

    /**
     * Notifies the beginning of parsing process.
     *
     * @param libraryName the unique name of the parsed library.
     * @param location the location of the parsed library.
     */
    void startParsing(String libraryName, String location);

    /**
     * Notifies the end of parsing process.
     */
    void endParsing();

    /**
     * Notifies the begin of a compilation unit (A class or interface or enumeration of first level).
     */
    void startCompilationUnit(String identifier);

    /**
     * Notifies the end of a compilation unit.
     */
    void endCompilationUnit();

    /**
     * Notifies an error occurred during parsing.
     *
     * @param location the location of the compilation unit raising the error.
     * @param description the error description.
     */
    public void parseError(String location, String description);
}
