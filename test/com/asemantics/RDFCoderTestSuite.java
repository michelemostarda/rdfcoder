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


package com.asemantics;

import junit.framework.TestSuite;
import junit.framework.Test;

import com.asemantics.model.CodeModelTest;
import com.asemantics.storage.JenaImplTest;
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
        addTest( new CodeModelTest() );
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
