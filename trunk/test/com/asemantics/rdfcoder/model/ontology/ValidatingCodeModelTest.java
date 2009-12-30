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


package com.asemantics.rdfcoder.model.ontology;

import com.asemantics.rdfcoder.CoderUtils;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import com.asemantics.rdfcoder.sourceparse.DirectoryParser;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.sourceparse.JavaBytecodeFileParser;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.ontology.ValidatingCodeModel} class.
 * Tests the <i> Java Ontology</i> validation over a real class path.
 */
public class ValidatingCodeModelTest {

    private CoderFactory<JavaCodeHandler> coderFactory;
    private ValidatingCodeModel vcm;

    @Before
    public void setUp() throws Exception {
        coderFactory = new JenaCoderFactory();
        CodeModelBase codeModel = coderFactory.createCodeModel();
        vcm = new ValidatingCodeModel( codeModel, coderFactory.createCodeModelOntology() );
    }

    @After
    public void tearDown() throws Exception {
        coderFactory = null;
        vcm = null;
    }

    @Test
    public void testApplyOntology() {
        final File dir = new File("./classes");
        if( ! dir.exists() ) {
            throw new RuntimeException(
                String.format("It is expected to find the classes dir at location %s", dir.getAbsolutePath())
            );
        }

        JavaCodeHandler javaCodeHandler = coderFactory.createHandlerOnModel(vcm);
        JStatistics statistics = new JStatistics();
        JavaCodeHandler statisticsJavaCodeHandler = statistics.createStatisticsCodeHandler(javaCodeHandler);

        DirectoryParser directoryParser = new DirectoryParser(
                new JavaBytecodeFileParser(),
                new CoderUtils.JavaClassFilenameFilter()
        );

        ObjectsTable objectsTable = new ObjectsTable();
        directoryParser.initialize(statisticsJavaCodeHandler, objectsTable );
        directoryParser.parseDirectory(dir.getName(), dir );

        Assert.assertTrue("Cannot find classes", statistics.getParsedClasses() > 0);

        System.out.println(statistics);
    }

    @Test
    public void testAddmodifier() {
          vcm.addTripleLiteral(
                  "jpackage:p1.p2.p3:jclassC1",
                  JavaCodeModel.HAS_MODIFIERS,
                  JavaCodeModel.JModifier.toByte(
                          new JavaCodeModel.JModifier[] {JavaCodeModel.JModifier.STATIC}
                  ).toString());
    }
}
