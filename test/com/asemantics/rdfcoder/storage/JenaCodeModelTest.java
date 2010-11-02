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


package com.asemantics.rdfcoder.storage;

import com.asemantics.rdfcoder.model.CodeModelException;
import com.asemantics.rdfcoder.model.QueryResult;
import com.asemantics.rdfcoder.model.SPARQLException;
import com.asemantics.rdfcoder.model.TripleIterator;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.java.JavaCodeModel} class.
 */
public class JenaCodeModelTest {

    private static final String TEST_FILE  = "target_test/jena_impl_test.xml";

    /**
     * Resource object triples.
     */
    private static final int RES_OBJ_TRIPLES = 9000;

    /**
     * Literal object triples.
     */
    private static final int LIT_OBJ_TRIPLES = 1000;

    /**
     * Total triples.
     */
    private static final int TEST_SIZE       = RES_OBJ_TRIPLES + LIT_OBJ_TRIPLES;

    private JenaCoderFactory jcmf;

    private JenaCodeModel jenaCodeModel;

    private CodeStorage cs;

    Map<String,String> params;

    public JenaCodeModelTest() {
        params = new HashMap<String,String>();
        params.put(CodeStorage.FS_FILENAME, TEST_FILE);
    }

    @Before
    public void setUp() {
        jcmf = new JenaCoderFactory();
        jenaCodeModel = jcmf.createCodeModel();
        cs   = jcmf.createCodeStorage();
    }

    @After
    public void tearDown() {
        jcmf = null;
        jenaCodeModel = null;
    }

    @Test
    public void testWrite() throws CodeModelException, CodeStorageException {
        // Writes triples with resource objects.
        for(int i = 0; i < RES_OBJ_TRIPLES; i++) {
            jenaCodeModel.addTriple("proto:subject_" + i, "proto:predicate_" + i, "proto:object_" + i);
        }
        // Writes triples with literal objects.
        for(int i = 0; i < LIT_OBJ_TRIPLES; i++) {
            jenaCodeModel.addTripleLiteral("proto:subject_" + i, "proto:predicate_" + i, "literal_object_" + i);
        }
        cs.saveModel(jenaCodeModel, params);
    }

    @Test
    public void testRead() throws IOException, CodeModelException, CodeStorageException {
        testWrite();

        FileInputStream fis = new FileInputStream(TEST_FILE);
        cs.loadModel(jenaCodeModel, params);
        fis.close();
        TripleIterator ti = jenaCodeModel.searchTriples(
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.ALL_MATCH
        );
        int counter = 0;
        while( ti.next() ) {
            counter++;
        }
        Assert.assertEquals(TEST_SIZE, counter);
    }

    @Test
    public void testQuery() throws IOException, CodeModelException, SPARQLException {
        // Loading data into model.
        for(int i = 0; i < 300; i++) {
            jenaCodeModel.addTriple("proto:subject_" + i, "proto:predicate_" + i, "proto:object_" + i);
        }

        QueryResult queryResult = jenaCodeModel.performQuery("select ?s ?p ?o where {?s ?p ?o}");
        try {
            queryResult.toTabularView(System.out);
        } finally {
            queryResult.close();
        }
    }

    @Test
    public void testAddTripleCollection() {
        jenaCodeModel.addTripleCollection("test-sub", "test-pre", new String[] {"val1", "val2", "val3"});
    }

}
