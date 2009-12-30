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


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.ParseHandler;

/**
 * Base class for any <code>Code Parser</code> implementation. 
 */
public abstract class CodeParser {

    /**
     * Internal parse handler.
     */
    private ParseHandler    parseHandler;

    /**
     * Internal objects table.
     */
    private ObjectsTable    objectsTable;


    protected CodeParser(){}

    
    /**
     * initializes the parser with the given parse handler and objects table.
     *
     * @param ch
     * @param ot
     */
    public void initialize(ParseHandler ch, ObjectsTable ot) {
        if( ch == null ) {
            throw new IllegalArgumentException("parseHandler cannot be null");
        }
        if( ot == null ) {
            throw new IllegalArgumentException("ot cannot be null");
        }
        parseHandler = ch;
        objectsTable = ot;
    }

    /**
     * Disposes the parser session.
     */
    public void dispose() {
        parseHandler = null;
        objectsTable = null;
    }

    protected ParseHandler getParseHandler() {
        return parseHandler;
    }

    protected ObjectsTable getObjectsTable() {
        return objectsTable;
    }
}
