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


package com.asemantics.rdfcoder.model.java;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.ParseHandler;
import com.asemantics.rdfcoder.sourceparse.JavadocEntry;

/**
 * Defines a consumer for <i>Javadoc</i> parsing events.
 */
public interface JavadocHandler extends ParseHandler {

    /**
     * Raised when the parsed entry refers to a class.
     *
     * @param entry
     * @param pathToClass
     */
    void classJavadoc(JavadocEntry entry, Identifier pathToClass);

    /**
     * Raised when the parsed entry refers to a class.
     *
     * @param entry
     * @param pathToField
     */
    void fieldJavadoc(JavadocEntry entry, Identifier pathToField);

    /**
     * Raised when the parsed entry refers to a method.
     *
     * @param entry
     * @param pathToConstructor
     * @param signature
     */
    void constructorJavadoc(JavadocEntry entry, Identifier pathToConstructor, String[] signature);    

    /**
     * Raised when the parsed entry refers to a method.
     *
     * @param entry
     * @param pathToMethod
     * @param signature
     */
    void methodJavadoc(JavadocEntry entry, Identifier pathToMethod, String[] signature);
}
