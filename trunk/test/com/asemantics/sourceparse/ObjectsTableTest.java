package com.asemantics.sourceparse;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.asemantics.model.CoderFactory;
import com.asemantics.model.CodeModel;
import com.asemantics.model.CodeStorage;
import com.asemantics.model.TripleIterator;
import com.asemantics.modelimpl.JenaCoderFactory;

/**
 * Test unit of <code>ObjectsTable</code>
 * //TODO: LOW - integrate testModelPreloading somewhere.
 */
public class ObjectsTableTest extends TestCase {

    private ObjectsTable objectsTable;

    public void setUp() {
        objectsTable = new ObjectsTable();
    }

    public void tearDown() {
        objectsTable = null;
    }

    public void testSourcePreloading() {
        objectsTable.preloadSourceDir(new File("/Developer/Java/JDK 1.5.0/src/"));
    }

     public void testClassPreloading() {
        objectsTable.preloadClassDir(new File("/Users/michele/IdeaProjects/RDFCoder/classes/production/RDFCoder"));
    }

    public void testJarPreloading() throws IOException {
        objectsTable.preloadJar(new File("/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/lib/jce.jar"));
    }


//    public void testModelPreloading() throws IOException {
//        CoderFactory coderFactory =  new JenaCoderFactory();
//        CodeModel codeModel       = coderFactory.createCodeModel();
//        CodeStorage codeStorage   = coderFactory.createCodeStorage();
//        Map map = new HashMap();
//        map.put(CodeStorage.FS_FILENAME, "/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/test_scan_src_dir.xml");
//        codeStorage.loadModel(codeModel, map);
//        objectsTable.preloadModel(codeModel);
//    }


}
