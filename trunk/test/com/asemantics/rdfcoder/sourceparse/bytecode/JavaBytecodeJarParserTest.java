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


package com.asemantics.rdfcoder.sourceparse.bytecode;

import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.sourceparse.ParserException;
import com.asemantics.rdfcoder.storage.JenaCodeModel;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for the {@link com.asemantics.rdfcoder.sourceparse.bytecode.JavaBytecodeJarParser}.
 */
public class JavaBytecodeJarParserTest {

    private static final Logger logger = Logger.getLogger(JavaBytecodeJarParserTest.class);

    private ObjectsTable objectsTable;
    private JenaCoderFactory jcf;
    private JenaCodeModel jcm;
    private JavaCodeHandler javaCodeHandler;
    private JavaBytecodeJarParser parser;
    private JStatistics statistics;

    public JavaBytecodeJarParserTest() {
        statistics = new JStatistics();
        objectsTable = new ObjectsTable();
        jcf = new JenaCoderFactory();
        jcm = (JenaCodeModel) jcf.createCodeModel();
        javaCodeHandler = jcf.createHandlerOnModel(jcm);
    }

    @Before
    public void setUp() {
        parser = new JavaBytecodeJarParser();
        JavaCodeHandler statCH = statistics.createStatisticsCodeHandler(javaCodeHandler);
        parser.initialize(statCH, objectsTable);
        javaCodeHandler.startParsing("smack_3.0.4", "lib_location");
    }

    @After
    public void tearDown() {
        javaCodeHandler.endParsing();
        logger.info("Bytecode parser statistics: " + statistics.toString() );
        statistics.reset();
        objectsTable.clear();
        parser.dispose();
        parser = null;
    }

    @Test
    public void testParse() throws IOException, ParserException {
        parser.parseFile(new File("target_test/target.jar") );
    }

}
