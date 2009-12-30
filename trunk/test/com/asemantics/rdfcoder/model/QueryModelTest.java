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

import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.model.java.JavaQueryModelImpl;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.sourceparse.JavaBytecodeJarParser;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.sourceparse.ParserException;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class QueryModelTest {

    private CodeModelBase codeModelBase;

    private JavaQueryModel javaQueryModel;

    /**
     * Creates a query model by parsing target.jar.
     * @throws IOException
     */
    protected static CodeModelBase createQueryModel() throws IOException, ParserException {
        JStatistics statistics = new JStatistics();
        CoderFactory<JavaCodeHandler> coderFactory = new JenaCoderFactory();
        CodeModelBase codeModelBase = coderFactory.createCodeModel();
        JavaCodeHandler javaCodeHandler = coderFactory.createHandlerOnModel(codeModelBase);

        JavaBytecodeJarParser parser = new JavaBytecodeJarParser();
        JavaCodeHandler statCH = statistics.createStatisticsCodeHandler(javaCodeHandler);
        ObjectsTable objectsTable = new ObjectsTable();
        parser.initialize( statCH, objectsTable );

        File lib = new File("target_test/target.jar");

        javaCodeHandler.startParsing("test_model", lib.getAbsolutePath());
        parser.parseFile( lib );
        javaCodeHandler.endParsing();

        statistics.reset();
        parser.dispose();
        parser = null;

        return codeModelBase;
    }

    public QueryModelTest() throws IOException, ParserException {
        codeModelBase = createQueryModel();
    }

    @Before
    public void setUp() throws Exception {
         javaQueryModel = new JavaQueryModelImpl(codeModelBase);
    }

    @After
    public void tearDown() throws Exception {
        javaQueryModel = null;
    }

    @Test
    public void testQueryModel() {
        final String EXPECTED_LIBRARY = "asset:test_model";

        QueryModel queryModel = javaQueryModel;

        Asset asset = queryModel.getAsset();
        Assert.assertNotNull(asset);

        String[] libraries = asset.getLibraries();
        Assert.assertNotNull(libraries);
        Assert.assertTrue( libraries.length == 1);
        Assert.assertEquals("Wrong library name.", libraries[0], EXPECTED_LIBRARY);

        Assert.assertNotNull("Cannot retrieve library date",  queryModel.getLibraryDateTime(EXPECTED_LIBRARY));

        Assert.assertNotNull("Cannot retrieve library location",  queryModel.getLibraryLocation(EXPECTED_LIBRARY));
    }

}
