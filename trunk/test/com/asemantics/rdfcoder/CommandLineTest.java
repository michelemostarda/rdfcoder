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
    }

    @Test
    public void testCdCommand() throws InvocationTargetException, IllegalAccessException, IOException {
        Assert.assertTrue(commandLine.processLine("cd ."));
        printStreamWrapper.dumpLines();
        Assert.assertEquals( "Unexpected number of lines.", 1, printStreamWrapper.getLines().size() );
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

    @Test
    public void testInspectModel() throws IllegalAccessException, InvocationTargetException, IOException {
        Assert.assertTrue(commandLine.processLine("loadclasspath junit jar:lib/junit-4.4.jar"));
        Assert.assertTrue(commandLine.processLine("inspect model"));
        printStreamWrapper.dumpLines();
        printStreamWrapper.assertContent(
                "com.asemantics.rdfcoder.model.java.JavaQueryModelImpl{ packages: 21, classes: 131, interfaces: 23}"
        );
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

        public List<String> getLines() {
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
            extractLines();
            for(String line : lines) {
                if(line.contains(in)) {
                    return;
                }
            }
            Assert.fail( String.format("Cannot find %s in output.", in) );
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
