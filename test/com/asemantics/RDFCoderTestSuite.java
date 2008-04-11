package com.asemantics;

import junit.framework.TestSuite;
import junit.framework.Test;

import com.asemantics.model.ModelTest;
import com.asemantics.modelimpl.JenaImplTest;
import com.asemantics.sourceparse.*;
import com.asemantics.sourceparse.JavaSourceFileParserTest;

/**
 * This class calls all the unit tests defined into the project.
 */
public class RDFCoderTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new RDFCoderTestSuite();
        return suite;
    }

    public RDFCoderTestSuite() {
        loadModelTests();
        loadModelImplTests();
        loadSourceParseTests();
    }

    private void loadModelTests() {
        addTest( new ModelTest() );
    }

    private void loadModelImplTests() {
        addTest( new JenaImplTest() );
    }

    private void loadSourceParseTests() {
        addTest( new JavaSourceFileParserTest()   );
        addTest( new JavaSourceDirParserTest()    );
        addTest( new JavaBytecodeFileParserTest() );
        addTest( new JavaBytecodeJarParserTest()  );
        addTest( new JavadocFileParserTest()      );
        addTest( new JavadocDirParserTest()       );
    }

}
