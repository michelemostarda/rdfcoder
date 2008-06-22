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


package com.asemantics.sourceparse;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

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
        objectsTable.preloadSourceDir(new File("src/"));
    }

     public void testClassPreloading() {
        objectsTable.preloadClassDir(new File("classes"));
    }

    public void testJarPreloading() throws IOException {
        objectsTable.preloadJar(new File("lib/antlr-2.7.5.jar"));
    }

}
