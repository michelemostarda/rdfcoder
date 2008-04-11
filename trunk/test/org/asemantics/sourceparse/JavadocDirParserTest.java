package org.asemantics.sourceparse;

import org.asemantics.sourceparse.ObjectsTable;
import org.asemantics.sourceparse.DirectoryParser;
import org.asemantics.modelimpl.JenaCoderFactory;
import org.asemantics.modelimpl.JenaCodeModel;
import org.asemantics.modelimpl.JenaCodeStorage;
import org.asemantics.model.CodeHandler;
import org.asemantics.model.CodeModelException;
import org.asemantics.model.CodeStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit test of JavadocSourceDirParser.
 */
public class JavadocDirParserTest extends TestCase {

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
        ch.startParsing("jdk_1.5.0", "java_home");
    }

     public void tearDown() {
        ch.endParsing();
        ot.clear();
        ot   = null;
        jcf = null;
        jcm  = null;
        ch   = null;
    }

    public void testParse() throws IOException, CodeModelException {
        DirectoryParser jsdp = new DirectoryParser( new JavadocFileParser() );
        Statistics statistics = new Statistics();
        CodeHandler sch = statistics.createStatisticsCodeHandler(ch);
        jsdp.initialize(sch, ot);
        jsdp.parseDirectory(new File("/Developer/Java/JDK 1.5.0/src/") );
        jsdp.dispose();
        Map<String,String> params = new HashMap();
        params.put(CodeStorage.FS_FILENAME, "/Users/michele/repository/RDFCoder/2007-10-31-RDFCoder/target_test/test_scan_src_javadoc_dir.xml");
        jcs.saveModel(jcm, params);
        System.out.println(statistics);
    }

}
