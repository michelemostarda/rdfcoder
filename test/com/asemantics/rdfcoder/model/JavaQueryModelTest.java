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

import com.asemantics.rdfcoder.model.java.JPackage;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.model.java.JavaQueryModelImpl;
import com.asemantics.rdfcoder.sourceparse.ParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.QueryModel} class.
 */
public class JavaQueryModelTest {

    public JavaQueryModelTest() throws IOException {
        super();
    }

    @Test
    public void testJavaQueryModel() throws IOException, ParserException {
        JavaQueryModel javaQueryModel = new JavaQueryModelImpl( QueryModelTest.createQueryModel() );

        JPackage[] packages = javaQueryModel.getAllPackages();
        Assert.assertTrue("Unexpected number of packages.", packages.length > 0 );
    }

}