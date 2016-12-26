/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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

import com.asemantics.rdfcoder.CoderUtils;
import com.asemantics.rdfcoder.model.CodeModelException;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.parser.DirectoryParser;
import com.asemantics.rdfcoder.parser.JStatistics;
import com.asemantics.rdfcoder.parser.ObjectsTable;
import com.asemantics.rdfcoder.storage.CodeStorage;
import com.asemantics.rdfcoder.storage.JenaCodeModel;
import com.asemantics.rdfcoder.storage.JenaCodeStorage;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test of JavadocSourceDirParser.
 */
public class JavaBytecodeDirParserTest {

    private static final Logger logger = Logger.getLogger(JavaBytecodeDirParserTest.class);

    private ObjectsTable ot;
    private JenaCoderFactory jcf;
    private JenaCodeModel jcm;
    private JenaCodeStorage jcs;
    private JavaCodeHandler ch;

    @Before
    public void setUp() {
        ot   = new ObjectsTable();
        jcf = new JenaCoderFactory();
        jcm  = (JenaCodeModel) jcf.createCodeModel();
        jcs  = jcf.createCodeStorage();
        ch   = jcf.createHandlerOnModel(jcm);
    }

    @After
    public void tearDown() {
        ot.clear();
        ot = null;
        jcf = null;
        jcm = null;
        ch = null;
    }

    @Test
    public void testParse() throws IOException, CodeModelException {
        DirectoryParser jsdp = new DirectoryParser(
                new JavaBytecodeFileParser(),
                new CoderUtils.JavaClassFilenameFilter()
        );
        JStatistics statistics = new JStatistics();
        JavaCodeHandler sch = statistics.createStatisticsCodeHandler(ch);
        jsdp.initialize(sch, ot);
        try {
            jsdp.parseDirectory("classes", new File("classes") );
            jsdp.dispose();
            Map<String,String> params = new HashMap<String,String>();
            params.put(CodeStorage.FS_FILENAME, "target_test/out/test_scan_classes_dir.xml");
            jcs.saveModel(jcm, params);
        } catch (Exception e) {
            logger.error("An error occurred while parsing bytecode dir.", e);
            Assert.fail();
        } finally {
            logger.info("Parses statistics: " + statistics);
        }
    }

}