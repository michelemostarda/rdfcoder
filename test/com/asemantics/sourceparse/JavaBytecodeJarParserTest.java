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


package com.asemantics.sourceparse;

import junit.framework.TestCase;
import com.asemantics.model.CodeHandler;
import com.asemantics.storage.JenaCodeModel;
import com.asemantics.storage.JenaCoderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Unit test of <code>JavaBytecodeJarParser</code> class.
 */
public class JavaBytecodeJarParserTest extends TestCase {

    private ObjectsTable objectsTable;
    private JenaCoderFactory jcf;
    private JenaCodeModel jcm;
    CodeHandler codeHandler;
    private JavaBytecodeJarParser parser;
    JStatistics statistics;

    public JavaBytecodeJarParserTest() {
        statistics = new JStatistics();
        objectsTable = new ObjectsTable();
        jcf = new JenaCoderFactory();
        jcm = (JenaCodeModel) jcf.createCodeModel();
        codeHandler = jcf.createHandlerOnModel(jcm);
    }

    public void setUp() {
        parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        parser.initialize(statCH, objectsTable);
        codeHandler.startParsing("smack_3.0.4", "lib_location");
    }

    public void tearDown() {
        codeHandler.endParsing();
        System.out.println(statistics.toString());
        statistics.reset();
        objectsTable.clear();
        parser.dispose();
        parser = null;
    }

    public void testParse() throws IOException {
        parser.parseFile(new File("target_test/target.jar") );
    }

}
