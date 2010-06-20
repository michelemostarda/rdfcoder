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

import com.asemantics.rdfcoder.CoderUtils;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Test case for the {@link com.asemantics.rdfcoder.sourceparse.DirectoryParser}.
 */
public class DirectoryParserTest {

    private static final Logger logger = Logger.getLogger(DirectoryParserTest.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSourceDirParser() {
        JavaSourceFileParser javaSourceFileParser = new JavaSourceFileParser();
        JStatistics statistics = processDir(javaSourceFileParser, new File("./src"));
        logger.info( "Source parsing statistics: " + statistics.toString() );
        Assert.assertTrue( "No classes found.", statistics.getParsedClasses() > 0);
        Assert.assertTrue( "No interfaces found.", statistics.getParsedInterfaces() > 0);
        Assert.assertTrue( "No methods found.", statistics.getParsedMethods() > 0);
        Assert.assertTrue( "No enumerations found.", statistics.getParsedEnumerations() > 0);
        Assert.assertTrue( "No attributes found.", statistics.getParsedAttributes() > 0);
    }

    public static JStatistics processDir(FileParser fileParser, File dir) {
        JStatistics statistics = new JStatistics();
        CoderFactory<JavaCodeHandler> coderFactory = new JenaCoderFactory();
        CodeModelBase codeModel   = coderFactory.createCodeModel();
        JavaCodeHandler javaCodeHandler = coderFactory.createHandlerOnModel(codeModel);
        ObjectsTable objectsTable = new ObjectsTable();
        JavaCodeHandler statisticsJavaCodeHandler = statistics.createStatisticsCodeHandler(javaCodeHandler);

        DirectoryParser directoryParser = new DirectoryParser(fileParser, new CoderUtils.JavaSourceFilenameFilter() );
        directoryParser.initialize(statisticsJavaCodeHandler, objectsTable );
        directoryParser.parseDirectory(dir.getName(), dir );
        return statistics;
    }

}
