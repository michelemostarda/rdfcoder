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

package com.asemantics;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.io.*;
import java.util.List;
import java.util.ArrayList;


public class CommandLineTest extends TestCase {

    class PrintStreamWrapper {

        private StringBuilder sb;

        private List<String> lines;

        PrintStreamWrapper() {
            sb    = new StringBuilder();
            lines = new ArrayList<String>();
        }

        PrintStream getPrintStream(OutputStream os) {
            return new PrintStream(os) {

                public void write(byte b[]) throws IOException {
                    super.write(b);
                    sb.append(b);
                }

                public void write(int b) {
                    super.write(b);
                    sb.append(b);
                }

                public void write(byte buf[], int off, int len) {
                    super.write(buf, off, len);
                    String s;
                    try {
                        s = new String(buf, off, len, "ISO-8859-1");
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException(uee);
                    }
                    sb.append(s);
                }

                public void println() {
                    super.println();
                    lines.add( sb.toString() );
                    sb.delete(0, sb.length());
                }
            };
        }

        void dumpLines(PrintStream ps) {
            if( lines.isEmpty() ) {
                ps.println("[buffer] " + sb.toString());
                return;
            }

            int i = 0;
            for(String s : lines) {
                ps.println("[" + i++ + "] " + s);
            }
        }

        void dumpLines() {
            dumpLines(System.err);
        }

    }

    private PrintStreamWrapper printStreamWrapper;

    private CommandLine commandLine;

    public CommandLineTest() {
        printStreamWrapper = new PrintStreamWrapper();
    }

    protected void setUp() throws Exception {
        System.out.println(">" + new File(".").getAbsolutePath());
        commandLine = new CommandLine(new File("."));
        System.setOut( printStreamWrapper.getPrintStream(System.out) );
    }

    protected void tearDown() throws Exception {
        commandLine = null;
    }

    public void testLoadSources() throws IllegalAccessException, InvocationTargetException {
        String[] command = new String[]{"loadclasspath", "sources", "src:src"};
        assertTrue( commandLine.processCommand(command) );
        printStreamWrapper.dumpLines();
    }

   public void testLoadClasses() throws IllegalAccessException, InvocationTargetException {
        String[] command = new String[]{"loadclasspath", "classes", "class:classes"};
        assertTrue( commandLine.processCommand(command) );
    }

    public void testInspectModel() throws IllegalAccessException, InvocationTargetException {
        String[] command = new String[]{"inspect", "model"};
        assertTrue( commandLine.processCommand(command) );
    }
}
