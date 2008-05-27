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


package com.asemantics.sourceparse;

import com.asemantics.model.CodeHandler;

/**
 * Base class for any <code>CodeParser</code> implementation.
 * TODO: LOW - introduce ClassParser, JavadocParser as extensions of CodeParser,
 *       ClassParserListener, JavadocParserListener, define CodeHandler as implemntation of both these listeners. 
 */
public abstract class CodeParser {

    private CodeHandler     codeHandler;
    private ObjectsTable    objectsTable;

    protected CodeParser() {

    }

    /**
     * initializes the parser with the given codeHandler and objects table.
     *
     * @param ch
     * @param ot
     */
    public void initialize(CodeHandler ch, ObjectsTable ot) {
        if( ch == null ) {
            throw new IllegalArgumentException("codeHandler cannot be null");
        }
        if( ot == null ) {
            throw new IllegalArgumentException("ot cannot be null");
        }
        codeHandler  = ch;
        objectsTable = ot;
    }

    /**
     * Disposes the parser session.
     */
    public void dispose() {
        codeHandler  = null;
        objectsTable = null;
    }

    protected CodeHandler getCodeHandler() {
        return codeHandler;
    }

    protected ObjectsTable getObjectsTable() {
        return objectsTable;
    }
}
