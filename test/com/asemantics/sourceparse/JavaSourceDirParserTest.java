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

import com.asemantics.model.CodeHandler;
import com.asemantics.model.CodeModelException;
import com.asemantics.storage.CodeStorage;
import com.asemantics.storage.JenaCodeModel;
import com.asemantics.storage.JenaCodeStorage;
import com.asemantics.storage.JenaCoderFactory;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test of JavadocSourceDirParser.
 */
public class JavaSourceDirParserTest extends TestCase {

    ObjectsTable ot;
    JenaCoderFactory jcf;
    JenaCodeModel jcm;
    JenaCodeStorage jcs;
    CodeHandler ch;

    public void setUp() {
        ot   = new ObjectsTable();
        jcf = new JenaCoderFactory();
        jcm  = (JenaCodeModel) jcf.createCodeModel();
        jcs  = jcf.createCodeStorage();
        ch   = jcf.createHandlerOnModel(jcm);
    }

     public void tearDown() {
        ot.clear();
        ot   = null;
        jcf = null;
        jcm  = null;
        ch   = null;
    }

    public void testParse() throws IOException, CodeModelException {
        DirectoryParser jsdp = new DirectoryParser( new JavaSourceFileParser() );
        Statistics statistics = new Statistics();
        CodeHandler sch = statistics.createStatisticsCodeHandler(ch);
        jsdp.initialize(sch, ot);
        try {
            jsdp.parseDirectory("src", new File("src") );
            jsdp.dispose();
            Map<String,String> params = new HashMap();
            params.put(CodeStorage.FS_FILENAME, "target_test/out/test_scan_src_dir.xml");
            jcs.saveModel(jcm, params);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            System.out.println(statistics);
        }
    }

}
