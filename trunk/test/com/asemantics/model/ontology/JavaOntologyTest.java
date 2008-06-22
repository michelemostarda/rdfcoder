package com.asemantics.model.ontology;

import com.asemantics.model.CodeHandler;
import com.asemantics.model.CodeModelBase;
import com.asemantics.model.CoderFactory;
import com.asemantics.sourceparse.DirectoryParser;
import com.asemantics.sourceparse.JavaBytecodeFileParser;
import com.asemantics.sourceparse.ObjectsTable;
import com.asemantics.sourceparse.JStatistics;
import com.asemantics.storage.JenaCoderFactory;
import junit.framework.TestCase;

import java.io.File;/*
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

public class JavaOntologyTest extends TestCase {


    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testCreate() {
        final File dir = new File("./src");
        JStatistics statistics = new JStatistics();
        CoderFactory coderFactory = new JenaCoderFactory();
        CodeModelBase codeModel   = coderFactory.createCodeModel();
        ValidatingCodeModel vcm   = new ValidatingCodeModel( codeModel, coderFactory.createCodeModelOntology() );
        CodeHandler codeHandler   = coderFactory.createHandlerOnModel(vcm);
        ObjectsTable objectsTable = new ObjectsTable();
        CodeHandler statisticsCodeHandler = statistics.createStatisticsCodeHandler( codeHandler );

        DirectoryParser directoryParser = new DirectoryParser(new JavaBytecodeFileParser());
        directoryParser.initialize( statisticsCodeHandler, objectsTable );
        directoryParser.parseDirectory(dir.getName(), dir );
    }
}
