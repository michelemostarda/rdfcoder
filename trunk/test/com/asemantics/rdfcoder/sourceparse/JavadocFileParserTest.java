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

import com.asemantics.rdfcoder.model.java.JavadocHandler;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Test unit of <code>JavadocFileParser</code>.
 */
public class JavadocFileParserTest extends TestCase {

    class TestJavadocParserListener implements JavadocHandler {

        private JavadocEntry je;

        private JavadocEntry methodJavadocEntry;

        private JavadocEntry classJavadocEntry;

        JavadocEntry getJavadocEntry() {
            return je;
        }

        public void startParsing(String libraryName, String location) {
            System.out.println("Start parsing of '" + libraryName + "' at location: '" + location + "'");
        }

        public void endParsing() {
            System.out.println("End parsing");
        }

        public void startCompilationUnit(String identifier) {
            System.out.println("start compilation unit: " + identifier);
        }

        public void endCompilationUnit() {
            System.out.println("endCompilationUnit");
        }

        public void parseError(String location, String description) {
            System.err.println("Parse error at location:" + location + ": " + description);
        }

        public void parsedEntry(JavadocEntry entry) {
            je = entry;
        }

        public void classJavadoc(JavadocEntry entry, String pathToClass) {
            classJavadocEntry = entry;
            System.out.println("=======================================");
            System.out.println("classJavadoc: " + pathToClass);
            System.out.println(entry);
            System.out.println("=======================================");
        }

        public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
            methodJavadocEntry = entry;
            System.out.println("=======================================");
            System.out.print("methodJavadoc: " + pathToMethod);
            System.out.print("(");
            for(String s: signature) { System.out.print(s + ", "); }
            System.out.println(")");
            System.out.println( entry );
            System.out.println("=======================================");
        }

    }

    private static final String SHORT_DESCRIPTION = "This is the short description";

    private static final String LONG_DESCRIPTION_1 = "This is the long one";
    private static final String LONG_DESCRIPTION_2 = "bla bla bla bla bla";

    private static final String RETURN_DESCRIPTION = "return description";

    private static final String PATH_TO_SEE        = "#path.to.see";

    private static final String SINCE_VALUE        = "x.y";

    private ByteArrayInputStream byteArrayInputStream;

    public JavadocFileParserTest() {}

    protected void setUp() {
        String input = "/* Neutral comment */\n" +
                       "\n" +
                       "/**\n" +
                       " * " + SHORT_DESCRIPTION + ".\n" +
                       " * " + LONG_DESCRIPTION_1 + "\n" + LONG_DESCRIPTION_2 +
                       " * @param param1 desc value 1\n" +
                       " * @param param2 desc value 2\n" +
                       " * @param param3 desc value 3\n" +
                       " * @param param4 desc value 4\n" +
                       " * @return " + RETURN_DESCRIPTION + "\n" +
                       " * @see "+ PATH_TO_SEE + "\n" +
                       " * @since " + SINCE_VALUE + "\n" +
                       " * @author Name1 Surname1 \n" +
                       " * @author Name2 Surname2 \n" +
                       " */" +
                       " // Inline neutral comment." +
                       " public static final void methodA(O1 o1, O2 o2, O3 o3) {}" +
                       " public static final void methodB(O4 o4, O5 o5) {}";

        byteArrayInputStream = new ByteArrayInputStream( input.getBytes() , 0, input.length());
    }

    protected void tearDown() {
        byteArrayInputStream = null;
    }

    public void testInputStream() throws ParserException {
        // Parses javadoc buffer.
        JavadocFileParser javadocFileParser = new JavadocFileParser();
        TestJavadocParserListener tjpl = new TestJavadocParserListener();
        javadocFileParser.initialize(tjpl, new ObjectsTable() );
        javadocFileParser.parse(byteArrayInputStream, "test_input");
        javadocFileParser.dispose();

        // Checks integrity.
        JavadocEntry entry = tjpl.getJavadocEntry();
        System.out.println("entry: " + entry);

        assertEquals(3, entry.getRow());
        assertEquals(0, entry.getCol());

        assertEquals(SHORT_DESCRIPTION, entry.getShortDescription());
        assertEquals(LONG_DESCRIPTION_1 + " " + LONG_DESCRIPTION_2 , entry.getLongDescription() );

        assertEquals(4, entry.getParameterNames().length);
        List expected = Arrays.asList( new String[] {"param1", "param2", "param3", "param4"} );
        List parameterNames = Arrays.asList( entry.getParameterNames() );
        assertTrue( expected.containsAll(parameterNames) );

        assertEquals(RETURN_DESCRIPTION, entry.getReturnDescription());

        assertEquals(PATH_TO_SEE, entry.getSee()[0]);

        assertEquals(SINCE_VALUE, entry.getSince());

        List authors = Arrays.asList( new String[] {"Name1 Surname1", "Name2 Surname2"} );
        assertTrue( Arrays.asList( entry.getAuthors()).containsAll(authors) );

    }

    public void testInputFile() throws ParserException {
        // Parses file content.
        JavadocFileParser javadocFileParser = new JavadocFileParser();
        TestJavadocParserListener tjpl = new TestJavadocParserListener();
        javadocFileParser.initialize( tjpl, new ObjectsTable() );
        javadocFileParser.parse(new File("target_test/TestJavadoc.java"));
        javadocFileParser.dispose();

        // Checks extracted content.
        JavadocEntry entry = tjpl.getJavadocEntry();
        //System.out.println("entry: " + entry);
        
    }
}
