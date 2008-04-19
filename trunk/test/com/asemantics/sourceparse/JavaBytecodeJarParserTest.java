package com.asemantics.sourceparse;

import junit.framework.TestCase;
import com.asemantics.model.CodeHandler;
import com.asemantics.modelimpl.JenaCodeModel;
import com.asemantics.modelimpl.JenaCoderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Unit test of <code>JavaBytecodeJarParser</code> class.
 */
public class JavaBytecodeJarParserTest extends TestCase {

    private ObjectsTable objectsTable;
    private JenaCoderFactory jcf;
    private JenaCodeModel jcm;
    CodeHandler codeHandler;
    private JavaBytecodeJarParser parser;
    Statistics statistics;

    public JavaBytecodeJarParserTest() {
        statistics = new Statistics();
        objectsTable = new ObjectsTable();
        jcf = new JenaCoderFactory();
        jcm = (JenaCodeModel) jcf.createCodeModel();
        codeHandler = jcf.createHandlerOnModel(jcm);
    }

    public void setUp() {
        parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        parser.initialize(statCH, objectsTable);
        codeHandler.startParsing("smack_3.0.4", "lib_location");
    }

    public void tearDown() {
        codeHandler.endParsing();
        System.out.println(statistics.toString());
        statistics.reset();
        objectsTable.clear();
        parser.dispose();
        parser = null;
    }

    public void testParse() throws IOException {
        parser.parseFile(new File("/Developer/Java/smack_src_3_0_4/smack.jar") );
    }

}
