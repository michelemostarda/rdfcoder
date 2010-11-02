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


package com.asemantics.rdfcoder.parser.bytecode;

import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeHandlerDebugImpl;
import com.asemantics.rdfcoder.parser.ObjectsTable;
import com.asemantics.rdfcoder.parser.ParserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for the {@link com.asemantics.rdfcoder.parser.bytecode.JavaBytecodeFileParser}.
 */
public class JavaBytecodeFileParserTest {

    private ObjectsTable objectsTable;
    private JavaCodeHandler javaCodeHandler;
    private JavaBytecodeFileParser javaBytecodeFileParser;

    @Before
    public void setUp() {
        objectsTable = new ObjectsTable();
        javaCodeHandler = new JavaCodeHandlerDebugImpl();
        javaBytecodeFileParser = new JavaBytecodeFileParser();
        javaBytecodeFileParser.initialize(javaCodeHandler, objectsTable);
    }

    @After
    public void tearDown() {
        javaBytecodeFileParser.dispose();
        javaBytecodeFileParser = null;
        objectsTable = null;
        javaCodeHandler = null;
    }

    @Test
    public void testParse() throws IOException, ParserException {
        javaBytecodeFileParser.parse(
                new File("target_test/classes/p1/p2/Test.class")
        );
    }

}
