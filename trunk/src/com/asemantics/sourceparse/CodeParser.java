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
