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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.IdentifierReader}.
 *
 * @version $Id$
 */
public class IdentifierReaderTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void testReadIdentifier() {
        Identifier identifier = IdentifierReader.readIdentifier("http://path/to/prefix#q1:p1.p2.p3.p4");
        Assert.assertTrue("Unexpected prefix.", "http://path/to/prefix#".equals( identifier.getPrefix() ) );
        Identifier newIdentifier = IdentifierReader.readIdentifier( identifier.getIdentifier() );
        Assert.assertEquals("Expected to be equal.", identifier, newIdentifier);
    }

    @Test
    public void testReadSinglePackage() {
        Identifier identifier = IdentifierReader.readPackage("p1");
        Assert.assertTrue("Unexpected size.", identifier.size() == 1);
    }

    @Test
    public void testReadPackage() {
        Identifier identifier = IdentifierReader.readPackage("p1.p2.p3.p4");
        Identifier identifierNew = IdentifierReader.readIdentifier("jpackage:p1.p2.p3.p4");
        Assert.assertEquals("Expected to be equal.", identifier, identifierNew);
    }

    @Test
    public void testFullyQualifiedClass() {
        Identifier identifier = IdentifierReader.readFullyQualifiedClass("p1.p2.p3.p4.Class1");
        Assert.assertEquals("Unexpected identifier.", "jpackage:p1.p2.p3.p4", identifier.getPreTail().getIdentifier() );
        Assert.assertEquals("Unexpected identifier.", "jclass:Class1", identifier.getTail().getIdentifier() );
    }

}
