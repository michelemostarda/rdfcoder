package org.asemantics;

import junit.framework.TestSuite;
import junit.framework.Test;
import junit.framework.TestResult;
import org.asemantics.model.ModelTest;
import org.asemantics.modelimpl.JenaImplTest;
import org.asemantics.sourceparse.*;

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
