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


package com.asemantics.rdfcoder.model;

import junit.framework.TestCase;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.sourceparse.JavaBytecodeJarParser;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import com.asemantics.model.CodeModelBase;
import com.asemantics.model.JavaQueryModel;
import com.asemantics.model.CoderFactory;
import com.asemantics.model.CodeHandler;
import com.asemantics.model.QueryModel;
import com.asemantics.model.Asset;
import com.asemantics.model.JavaQueryModelImpl;

import java.io.File;
import java.io.IOException;

public class QueryModelTest extends TestCase {

    private CodeModelBase codeModelBase;

    private JavaQueryModel javaQueryModel;

    /**
     * Creates a query model by parsing target.jar.
     * @throws IOException
     */
    private CodeModelBase createQueryModel() throws IOException {
        JStatistics statistics = new JStatistics();
        CoderFactory coderFactory = new JenaCoderFactory();
        CodeModelBase codeModelBase = coderFactory.createCodeModel();
        CodeHandler codeHandler = coderFactory.createHandlerOnModel( codeModelBase);

        JavaBytecodeJarParser parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        ObjectsTable objectsTable = new ObjectsTable();
        parser.initialize( statCH, objectsTable );

        File lib = new File("target_test/target.jar");

        codeHandler.startParsing("test_model", lib.getAbsolutePath());
        parser.parseFile( lib );
        codeHandler.endParsing();

        statistics.reset();
        parser.dispose();
        parser = null;

        return codeModelBase;
    }

    public QueryModelTest() throws IOException {
        codeModelBase = createQueryModel();
    }

    public void testQueryModel() {
        final String EXPECTED_LIBRARY = "asset:test_model";

        QueryModel queryModel = javaQueryModel;

        Asset asset = queryModel.getAsset();
        assertNotNull(asset);

        String[] libraries = asset.getLibraries();
        assertNotNull(libraries);
        assertTrue( libraries.length == 1);
        assertEquals("Wrong library name.", libraries[0], EXPECTED_LIBRARY);

        assertNotNull("Cannot retrieve library date",  queryModel.getLibraryDateTime(EXPECTED_LIBRARY));

        assertNotNull("Cannot retrieve library location",  queryModel.getLibraryLocation(EXPECTED_LIBRARY));
    }

    protected void setUp() throws Exception {
         javaQueryModel = new JavaQueryModelImpl(codeModelBase);

    }

    protected void tearDown() throws Exception {
        javaQueryModel = null;
    }

    protected JavaQueryModel getJavaQueryModel() {
        return javaQueryModel;
    }

}
