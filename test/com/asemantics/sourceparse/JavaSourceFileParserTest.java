package com.asemantics.sourceparse;

import junit.framework.TestCase;
import com.asemantics.model.CodeHandlerDebugImpl;
import net.sourceforge.jrefactory.parser.ParseException;

import java.io.IOException;
import java.io.File;

/**
 * A basic test on JavaSourceFileScanner.
 */
public class JavaSourceFileParserTest extends TestCase {

    JavaSourceFileParser jsfp;

    public JavaSourceFileParserTest() {}

    public void setUp() {
        CodeHandlerDebugImpl chdi = new CodeHandlerDebugImpl();
        ObjectsTable ot           = new ObjectsTable();
        jsfp                      = new JavaSourceFileParser();
        jsfp.initialize(chdi, ot);
    }

    public void tearDown() {
        jsfp.dispose();
        jsfp = null;
    }

    public void testProcessFileOnClass() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/TestClass.java") );
    }

    public void testProcessFileOnInterface() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/TestInterface.java") );
    }

    public void testProcessEnum() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/TestEnum.java") );
    }

}
