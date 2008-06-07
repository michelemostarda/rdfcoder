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

package com.asemantics;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.io.File;


public class CommandLineTest extends TestCase {

    private CommandLine commandLine;

    protected void setUp() throws Exception {
        System.out.println(">" + new File(".").getAbsolutePath());
        commandLine = new CommandLine(new File("."));
    }

    protected void tearDown() throws Exception {
        commandLine = null;
    }

    public void testLoadSources() throws IllegalAccessException, InvocationTargetException {
        String[] command = new String[]{"loadclasspath", "sources", "src:src"};
        assertTrue( commandLine.processCommand(command) );
    }

   public void testLoadClasses() throws IllegalAccessException, InvocationTargetException {
        String[] command = new String[]{"loadclasspath", "classes", "class:classes"};
        assertTrue( commandLine.processCommand(command) );
    }
}
