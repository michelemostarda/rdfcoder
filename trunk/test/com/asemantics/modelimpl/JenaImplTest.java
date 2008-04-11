package com.asemantics.modelimpl;

import junit.framework.TestCase;
import com.asemantics.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>JenaCodeModel</code>
 */
public class JenaImplTest extends TestCase {

    private static final String TEST_FILE  = "/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/test.xml";

    private static final int TEST_SIZE     = 10000;

    private static final String TEST_MODEL = "test_model";

    private JenaCoderFactory jcmf;

    private SPARQLQuerableCodeModel cm;

    private CodeStorage cs;

    Map<String,String> params;

    public JenaImplTest() {
        params = new HashMap();
        params.put(CodeStorage.FS_FILENAME, TEST_FILE);
    }

    public void setUp() {
        jcmf = new JenaCoderFactory();
        cm   = jcmf.createCodeModel();
        cs   = jcmf.createCodeStorage();
    }

    public void tearDown() {
        jcmf = null;
        cm   = null;
    }

    public void testWrite() throws IOException, CodeModelException {
        for(int i = 0; i < 10000; i++) {
            cm.addTriple("proto:subject_" + i, "proto:predicate_" + i, "proto:object_" + i);
        }
        cs.saveModel(cm, params);
    }

    public void testRead() throws IOException, CodeModelException {
        testWrite();

        FileInputStream fis = new FileInputStream(TEST_FILE);
        cs.loadModel(cm, params);
        fis.close();
        TripleIterator ti = cm.searchTriples(CodeModel.ALL_MATCH, CodeModel.ALL_MATCH, CodeModel.ALL_MATCH);
        int counter = 0;
        while( ti.next() ) {
            counter++;
        }
        assertEquals(TEST_SIZE, counter);
    }

    public void testQuery() throws IOException, CodeModelException {
        // Loading data into model.
        for(int i = 0; i < 1000; i++) {
            cm.addTriple("proto:subject_" + i, "proto:predicate_" + i, "proto:object_" + i);
        }

        QueryResult queryResult = cm.performQuery("select ?s ?p ?o where {?s ?p ?o}");
        queryResult.toTabularView(System.out);
    }

}
