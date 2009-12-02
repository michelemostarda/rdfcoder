/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.rdfcoder.model;

import junit.framework.TestCase;

import java.io.IOException;

import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.model.java.JavaQueryModelImpl;
import com.asemantics.rdfcoder.model.java.JPackage;

public class JavaQueryModelTest extends TestCase {

    public JavaQueryModelTest() throws IOException {
        super();
    }

    public void testJavaQueryModel() throws IOException {
        JavaQueryModel javaQueryModel = new JavaQueryModelImpl( QueryModelTest.createQueryModel() );

        JPackage[] packages = javaQueryModel.getAllPackages();
        assertTrue("Unespected number of packages.", packages.length > 0 );
    }

}