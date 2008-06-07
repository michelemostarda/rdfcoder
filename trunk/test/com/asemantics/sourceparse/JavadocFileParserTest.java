/**
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


package com.asemantics.sourceparse;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.asemantics.model.CodeHandler;
import com.asemantics.model.CodeModel;
import com.asemantics.model.ErrorListener;

/**
 * Test unit of <code>JavadocFileParser</code>.
 */
public class JavadocFileParserTest extends TestCase {

    class TestJavadocParserListener implements CodeHandler {

        public void startParsing(String libraryName, String location) {

        }

        public void endParsing() {

        }

        public void startCompilationUnit(String identifier) {
            System.out.println("start compilation unit: " + identifier);
        }

        public void endCompilationUnit() {
            System.out.println("endCompilationUnit");
        }

        public void startPackage(String pathToPackage) {
            throw new UnsupportedOperationException();
        }

        public void endPackage() {
            throw new UnsupportedOperationException();
        }

        public void startInterface(String pathToInterface, String[] extendedInterfaces) {
            throw new UnsupportedOperationException();
        }

        public void endInterface() {
            throw new UnsupportedOperationException();
        }

        public void startClass(CodeModel.JModifier[] modifiers, CodeModel.JVisibility visibility, String pathToClass, String extendedClass, String[] implementedInterfaces) {
            throw new UnsupportedOperationException();
        }

        public void endClass() {
            throw new UnsupportedOperationException();
        }

        public void startEnumeration(CodeModel.JModifier[] modifiers, CodeModel.JVisibility visibility, String pathToEnumeration, String[] elements) {
            throw new UnsupportedOperationException();
        }

        public void endEnumeration() {
            throw new UnsupportedOperationException();
        }

        public void attribute(CodeModel.JModifier[] modifiers, CodeModel.JVisibility visibility, String pathToAttribute, CodeModel.JType type, String value) {
            throw new UnsupportedOperationException();
        }

        public void constructor(CodeModel.JModifier[] modifiers, CodeModel.JVisibility visibility, int overloadIndex, String[] parameterNames, CodeModel.JType[] parameterTypes, CodeModel.ExceptionType[] exceptions) {
            throw new UnsupportedOperationException();
        }

        public void method(CodeModel.JModifier[] modifiers, CodeModel.JVisibility visibility, String pathToMethod, int overloadIndex, String[] parameterNames, CodeModel.JType[] parameterTypes, CodeModel.JType returnType, CodeModel.ExceptionType[] exceptions) {
            throw new UnsupportedOperationException();
        }

        public void parseError(String location, String description) {
            throw new UnsupportedOperationException();
        }

        public void unresolvedTypes(String[] unresolvedTypes) {
            throw new UnsupportedOperationException();
        }

        public void preloadObjectsFromModel(ObjectsTable objectsTable) {
            throw new UnsupportedOperationException();
        }

        public void serializeUnresolvedTypeEntries(ObjectsTable objectTable) {
            throw new UnsupportedOperationException();
        }

        public void deserializeUnresolvedTypeEntries(ObjectsTable objectTable) {
            throw new UnsupportedOperationException();
        }

        public void addErrorListener(ErrorListener errorListener) {
            throw new UnsupportedOperationException();
        }

        public void removeErrorListener(ErrorListener errorListener) {
            throw new UnsupportedOperationException();
        }

        public void parsedEntry(JavadocEntry entry) {
            System.out.println("parsedEntry: " + entry);
        }

        public void classJavadoc(JavadocEntry entry, String pathToClass) {
//            System.out.println("classJavadoc: " + pathToClass);
//            System.out.println(entry);
        }

        public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
//            System.out.print("methodJavadoc: " + pathToMethod);
//            System.out.print("(");
//            for(String s: signature) { System.out.print(s + ", "); }
//            System.out.println(")");
//            System.out.println( entry );
        }

        public String generateTempUniqueIdentifier() {
            throw new UnsupportedOperationException();
        }

        public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType) {
            throw new UnsupportedOperationException();
        }
    }

    private ByteArrayInputStream byteArrayInputStream;

    public JavadocFileParserTest() {}

    protected void setUp() {
        String input = "/* Neutral comment */\n" +
                       "/**\n" +
                       " * This is the short comment.\n" +
                       " * This is the long one\n" +
                       " *\nbla bla bla bla bla\n" +
                       " * @param param1 desc value 1\n" +
                       " * @param param2 desc value 2\n" +
                       " * @param param3 desc value 3\n" +
                       " * @param param4 desc value 4\n" +
                       " * @return \n" +
                       " * @see {aaa bbb ccc} \n" +
                       " */" +
                       " // post ---" +
                       " public static final void methodA(O1 o1, O2 o2, O3 o3) {}" +
                       " public static final void methodB(O4 o4, O5 o5) {}";

        byteArrayInputStream = new ByteArrayInputStream( input.getBytes() , 0, input.length());
    }

    protected void tearDown() {
        byteArrayInputStream = null;
    }

    public void testInputStream() throws ParserException {
        JavadocFileParser javadocFileParser = new JavadocFileParser();
        javadocFileParser.initialize(new TestJavadocParserListener(), new ObjectsTable() );
        javadocFileParser.parse(byteArrayInputStream, "test_input");
        javadocFileParser.dispose();
    }

    public void testInputFile() throws ParserException {
        JavadocFileParser javadocFileParser = new JavadocFileParser();
        javadocFileParser.initialize(new TestJavadocParserListener(), new ObjectsTable() );
        javadocFileParser.parse(new File("target_test/TestJavadoc.java"));
        javadocFileParser.dispose();
    }
}
