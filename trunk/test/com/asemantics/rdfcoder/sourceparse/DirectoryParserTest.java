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

import com.asemantics.rdfcoder.model.CodeHandler;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import com.asemantics.rdfcoder.CoderUtils;
import junit.framework.TestCase;

import java.io.File;


public class DirectoryParserTest extends TestCase {


    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testSourceDirParser() {
        JavaSourceFileParser javaSourceFileParser = new JavaSourceFileParser();
        JStatistics statistics = processDir(javaSourceFileParser, new File("./src"));
        System.out.println( statistics.toString() );
        assertTrue( "No classes found.", statistics.getParsedClasses() > 0);
        assertTrue( "No interfaces found.", statistics.getParsedInterfaces() > 0);
        assertTrue( "No methods found.", statistics.getParsedMethods() > 0);
        assertTrue( "No enumerations found.", statistics.getParsedEnumerations() > 0);
        assertTrue( "No attributes found.", statistics.getParsedAttributes() > 0);
    }

    public void testJavadocDirParser() {
        JavadocFileParser javadocFileParser = new JavadocFileParser();
        JStatistics statistics = processDir(javadocFileParser, new File("./src"));
        System.out.println( statistics.toString() );
        assertTrue( "No classes found.", statistics.getJavadocEntries() > 0);
        assertTrue( "No methods found.", statistics.getMethodsJavadoc() > 0);
    }

    public static JStatistics processDir(FileParser fileParser, File dir) {
        JStatistics statistics = new JStatistics();
        CoderFactory coderFactory = new JenaCoderFactory();
        CodeModelBase codeModel   = coderFactory.createCodeModel();
        CodeHandler codeHandler   = coderFactory.createHandlerOnModel(codeModel);
        ObjectsTable objectsTable = new ObjectsTable();
        CodeHandler statisticsCodeHandler = statistics.createStatisticsCodeHandler( codeHandler );

        DirectoryParser directoryParser = new DirectoryParser(fileParser, new CoderUtils.JavaSourceFilenameFilter() );
        directoryParser.initialize( statisticsCodeHandler, objectsTable );
        directoryParser.parseDirectory(dir.getName(), dir );
        return statistics;
    }

}
