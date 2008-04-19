package com.asemantics.sourceparse;

import junit.framework.TestCase;
import com.asemantics.model.CodeHandler;
import com.asemantics.model.CodeHandlerDebugImpl;

import java.io.File;
import java.io.IOException;

/**
 * A basic uint test of <code>JavaBytecodeFileParser</code>
 */
public class JavaBytecodeFileParserTest extends TestCase {

    private ObjectsTable objectsTable;
    private CodeHandler codeHandler;
    private JavaBytecodeFileParser javaBytecodeFileParser;


    public void setUp() {
        objectsTable = new ObjectsTable();
        codeHandler = new CodeHandlerDebugImpl();
        javaBytecodeFileParser = new JavaBytecodeFileParser();
        javaBytecodeFileParser.initialize(codeHandler, objectsTable);
    }

    public void tearDown() {
        javaBytecodeFileParser.dispose();
        javaBytecodeFileParser = null;
        objectsTable = null;
        codeHandler = null;
    }

    public void testMain() throws IOException {
        javaBytecodeFileParser.parse(
                new File("/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/trunk/target_test/Test.class")
        );
    }

}
