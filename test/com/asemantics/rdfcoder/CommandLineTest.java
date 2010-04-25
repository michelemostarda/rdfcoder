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

package com.asemantics.rdfcoder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Test case for {@link com.asemantics.rdfcoder.CommandLine}.
 */
public class CommandLineTest {

    private PrintStreamWrapper printStreamWrapper;

    private CommandLine commandLine;

    public CommandLineTest() {
        printStreamWrapper = new PrintStreamWrapper();
    }

    @Before
    public void setUp() throws Exception {
        System.out.println(">" + new File(".").getAbsolutePath());
        commandLine = new CommandLine(new File("."));
        System.setOut( printStreamWrapper.getPrintStream(System.out) );
    }

    @After
    public void tearDown() throws Exception {
        commandLine = null;
        printStreamWrapper.clear();
    }

    @Test
    public void testRetrieveCommands() {
        Assert.assertEquals("Unexpected number of commands.", 16, commandLine.getCommands().length);
    }

    @Test
    public void testDebugCommand() throws IOException {
        Assert.assertTrue( commandLine.processLine("debug true") );
        printStreamWrapper.clear();
        commandLine.processLine("debug");
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("debug: true");

        Assert.assertTrue( commandLine.processLine("debug false") );
        printStreamWrapper.clear();
        commandLine.processLine("debug");
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("debug: false");
    }

    @Test
    public void testPwdCommand() throws IOException {
        Assert.assertTrue( commandLine.processLine("pwd") );
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("trunk");
    }

    @Test
    public void testCdCommand() throws InvocationTargetException, IllegalAccessException, IOException {
        Assert.assertTrue(commandLine.processLine("cd ."));
        printStreamWrapper.dumpLines();
        Assert.assertEquals( "Unexpected number of lines.", 1, printStreamWrapper.getLines().size() );
    }

    @Test
    public void testLsCommand() throws IOException {
        Assert.assertTrue(commandLine.processLine("ls ."));
        printStreamWrapper.dumpLines();
        Assert.assertTrue( "Unexpected number of lines.", printStreamWrapper.getLines().size() > 5 );
    }

    @Test
    public void testCreateNewModelCommand() throws IOException {
        Assert.assertTrue(commandLine.processLine("newmodel testmodel"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("Model 'testmodel' created.");
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("newmodel testmodel"));
        printStreamWrapper.assertContent("ERROR: 'a model with name testmodel already exists.'");
    }

    @Test
    public void testRemoveModelCommand() throws IOException {
        Assert.assertTrue(commandLine.processLine("removemodel fakemodel"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("ERROR: 'Cannot find model 'fakemodel'");
        Assert.assertTrue(commandLine.processLine("newmodel toBeDeletedModel"));
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("removemodel toBeDeletedModel"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("Model 'toBeDeletedModel' deleted");
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("removemodel default"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("Model 'default' cannot be deleted.");
    }

    @Test
    public void testClearModelCommand() throws IOException {
        Assert.assertTrue(commandLine.processLine("clearmodel fakemodel"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("ERROR: 'Cannot find model 'fakemodel''");
        Assert.assertTrue(commandLine.processLine("newmodel toBeDeletedClean"));
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("clearmodel toBeDeletedClean"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("Model 'toBeDeletedClean' clean");
    }

    @Test
    public void testSetModelCommand() throws IOException {
        Assert.assertTrue(commandLine.processLine("newmodel modelA"));
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("setmodel"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("selected model: default");
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("setmodel modelA"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("Model set to 'modelA'");
        printStreamWrapper.clear();
        Assert.assertTrue(commandLine.processLine("setmodel XXX"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("ERROR: 'model with name XXX doesn't exist.'");
    }

    @Test
    public void testSaveModelCommand() throws IOException {
        final String tmpFile =
                System.getProperty("java.io.tmpdir") + File.pathSeparator + UUID.randomUUID().hashCode();
        final File file = new File(tmpFile);
        Assert.assertFalse(file.exists());
        printStreamWrapper.clear();
        Assert.assertTrue(
                commandLine.processLine( String.format("savemodel fs filename=%s", tmpFile))
        );
        Assert.assertTrue(file.exists());
        printStreamWrapper.assertContent("Model saved.");
    }

    @Test
    public void testLoadModelCommand() throws IOException {
        final String tmpFile =
                System.getProperty("java.io.tmpdir") + File.pathSeparator + UUID.randomUUID().hashCode();
        final File file = new File(tmpFile);
        Assert.assertFalse(file.exists());
        Assert.assertTrue(
                commandLine.processLine( String.format("savemodel fs filename=%s", tmpFile))
        );
        Assert.assertTrue(file.exists());
        printStreamWrapper.clear();
        Assert.assertTrue(
                commandLine.processLine( String.format("loadmodel fs filename=%s", tmpFile))
        );
        printStreamWrapper.assertContent("Model loaded.");
    }

    @Test
    public void testHelpCommand() throws IOException {
        Assert.assertTrue( commandLine.processLine("help") );
        printStreamWrapper.dumpLines();
        Assert.assertTrue("Unexpected number of lines.", printStreamWrapper.getLines().size() >= 16 );

        printStreamWrapper.clear();
        Assert.assertTrue( commandLine.processLine("help help") );
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("prints the usage help of a specific command.");
    }

    @Test
    public void testListCommand() throws IOException {
        Assert.assertTrue( commandLine.processLine("list") );
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("default[X]");
    }

    @Test
    public void testQueryModelCommand() throws IOException {
        Assert.assertTrue( commandLine.processLine("querymodel \"select * where {?s ?p ?o}\"") );
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("| s | p | o |");
    }


    @Test
    public void testInspectModelCommand() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("loadclasspath junit jar:lib/junit-4.4.jar"));
        Assert.assertTrue(commandLine.processLine("inspect model"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent(
                "com.asemantics.rdfcoder.model.java.JavaQueryModelImpl{ packages: 21, classes: 131, interfaces: 23}"
        );
    }

    @Test
    public void testDescribeModelCommand() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("describe model"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("allClasses:[Lcom.asemantics.rdfcoder.model.java.JClass;");
        printStreamWrapper.assertContent("allInterfaces:[Lcom.asemantics.rdfcoder.model.java.JInterface;");
        printStreamWrapper.assertContent("allPackages:[Lcom.asemantics.rdfcoder.model.java.JPackage;");
        printStreamWrapper.assertContent("asset:com.asemantics.rdfcoder.model.Asset");
        printStreamWrapper.assertContent("libraries:[Ljava.lang.String;");
    }

    @Test
    // TODO: add check for 0 errors.
    public void testLoadSources() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("loadclasspath sources src:src"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("parse errors");
        printStreamWrapper.assertContent("unresolved");
    }

    @Test
    public void testLoadClasses() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("loadclasspath classes class:classes"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("parse errors[0]");
        printStreamWrapper.assertContent("unresolved [0]");
    }

    @Test
    public void testLoadJar() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("loadclasspath junit jar:lib/junit-4.4.jar"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent("parse errors[0]");
        printStreamWrapper.assertContent("unresolved [0]");
    }

    /**
     * Provides a wrapper to the print stream.
     */
    class PrintStreamWrapper {

        ByteArrayOutputStreamWrapper baos;

        PrintStream ps;

        private boolean extracted = false;

        private List<String> lines = new ArrayList<String>();

        PrintStreamWrapper() {
        }

        void clear() {
            lines.clear();
            baos.reset();
        }

        List<String> getLines() {
            extractLines();
            return lines;
        }

        PrintStream getPrintStream(final OutputStream os) {
            extracted = false;
            baos = new ByteArrayOutputStreamWrapper(os);
            ps   = new PrintStream(baos);
            return ps;
        }

        void extractLines() {
            if(extracted) {
                return;
            }
            extracted = false;
            ps.flush();
            lines.clear();
            String out = new String( baos.toByteArray() );
            lines.addAll( Arrays.asList( out.split("\n") ) );
        }

        void dumpLines(PrintStream ps) {
            extractLines();
            int i = 0;
            for (String s : lines) {
                ps.println("[" + i++ + "] " + s);
            }
        }

        void dumpLines() {
            dumpLines(System.err);
        }

        void assertContent(String in) {
            if(in == null || in.length() == 0) {
                throw new IllegalArgumentException("Invalid in pattern.");
            }
            extractLines();
            for(String line : lines) {
                if(line.contains(in)) {
                    return;
                }
            }
            Assert.fail( String.format("Cannot find \"%s\" content in output lines.", in) );
        }

        class ByteArrayOutputStreamWrapper extends ByteArrayOutputStream {

            OutputStream os;

            ByteArrayOutputStreamWrapper(OutputStream os) {
                this.os = os;
            }

            @Override
            public void write(int b) {
                super.write(b);
                try {
                    os.write(b);
                } catch (IOException ioe) {
                    throw new RuntimeException();
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                super.write(b, off, len);
                try {
                    os.write(b, off, len);
                } catch (IOException ioe) {
                    throw new RuntimeException();
                }
            }

            @Override
            public void write(byte[] b) throws IOException {
                super.write(b);
                os.write(b);
            }
        }

    }

    
}
