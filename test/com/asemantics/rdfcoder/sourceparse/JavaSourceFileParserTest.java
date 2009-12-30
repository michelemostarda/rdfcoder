/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.java.JavaCodeHandlerDebugImpl;
import net.sourceforge.jrefactory.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for {@link com.asemantics.rdfcoder.sourceparse.JavaSourceFileParser}.
 */
public class JavaSourceFileParserTest {

    private JavaSourceFileParser jsfp;

    public JavaSourceFileParserTest() {}

    @Before
    public void setUp() {
        JavaCodeHandlerDebugImpl chdi = new JavaCodeHandlerDebugImpl();
        ObjectsTable ot           = new ObjectsTable();
        jsfp                      = new JavaSourceFileParser();
        jsfp.initialize(chdi, ot);
    }

    @After
    public void tearDown() {
        jsfp.dispose();
        jsfp = null;
    }

    @Test
    public void testProcessFileOnClass() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("target_test/TestClass.java") );
    }

    @Test
    public void testProcessFileOnInterface() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("target_test/TestInterface.java") );
    }

    @Test
    public void testProcessEnum() throws IOException, ParseException, ParserException {
        jsfp.parse( new File("target_test/TestEnum.java") );
    }

}
