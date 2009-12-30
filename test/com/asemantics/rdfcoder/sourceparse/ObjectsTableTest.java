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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for {@link com.asemantics.rdfcoder.sourceparse.ObjectsTable} class.
 */
public class ObjectsTableTest {

    private ObjectsTable objectsTable;

    @Before
    public void setUp() {
        objectsTable = new ObjectsTable();
    }

    @After
    public void tearDown() {
        objectsTable = null;
    }

    @Test
    public void testSourcePreloading() {
        objectsTable.preloadSourceDir(new File("src/"));
    }

    @Test
    public void testClassPreloading() {
        objectsTable.preloadClassDir(new File("classes"));
    }

    @Test
    public void testJarPreloading() throws IOException {
        objectsTable.preloadJar(new File("lib/antlr-2.7.5.jar"));
    }

}
