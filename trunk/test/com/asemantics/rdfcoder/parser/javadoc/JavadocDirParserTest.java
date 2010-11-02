package com.asemantics.rdfcoder.parser.javadoc;

import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavadocHandler;
import com.asemantics.rdfcoder.parser.JStatistics;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;

/**
 * Test case for {@link com.asemantics.rdfcoder.parser.javadoc.JavadocDirParser}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocDirParserTest {

    private static final Logger logger = Logger.getLogger(JavadocDirParserTest.class);

    private JavadocDirParser javadocDirParser;

    private JStatistics statistics;

    private JavadocHandler javadocHandler;

    @Before
    public void setUp() {
        JavaCodeHandler mockCodeHandler = mock(JavaCodeHandler.class);
        statistics = new JStatistics();
        javadocHandler = statistics.createStatisticsCodeHandler(mockCodeHandler);   
        javadocDirParser = new JavadocDirParser(javadocHandler);
    }

    @After
    public void tearDown() {
        javadocDirParser = null;
        javadocHandler   = null;
        statistics       = null;
    }

    @Test
    public void testParseSourceDir() throws JavadocDirParserException {
        javadocDirParser.parseSourceDir( new File("./src") );
        logger.info("Statistics: " + statistics.toStringReport() );

        Assert.assertTrue("Unexpected number of classes."     , statistics.getClassesJavadoc()      > 130);
        Assert.assertTrue("Unexpected number of fields."      , statistics.getFieldsJavadoc()       > 340);
        Assert.assertTrue("Unexpected number of constructors.", statistics.getConstructorsJavadoc() > 150);
        Assert.assertTrue("Unexpected number of methods."     , statistics.getMethodsJavadoc()      > 850);
    }

}
