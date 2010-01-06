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
 * Test case for {@link com.asemantics.rdfcoder.model.IdentifierBuilder}.
 *
 * @version $Id$
 */
public class IdentifierBuilderTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Tests the builder methods.
     */
    @Test
    public void testBuild() {
        final Identifier identifier = IdentifierBuilder
                .create()
                .setPrefix("http://test/prefix#")
                .pushFragment("f1", "q1")
                .pushFragment("f2", "q1")
                .pushFragment("f3", "q2")
                .pushFragment("f4", "q2")
                .pushFragment("f5", "q2")
                .pushFragment("f6", "q3")
                .pushFragment("f7", "q3")
                .pushFragment("f8", "q3")
                .pushFragment("f9", "q3")
                .build();
        Assert.assertEquals(
                "Unexpected context.",
                "http://test/prefix#q1:f1.f2.q2:f3.f4.f5.q3:f6.f7.f8.f9",
                identifier.getIdentifier()
        );

        Identifier copy = identifier.copy().build();
        Assert.assertEquals("Expected to be the same.", identifier, copy);
    }

    /**
     * Tests a building sequence with modification.
     */
    @Test
    public void testBuildWithModification() {
        final Identifier identifier = IdentifierBuilder
                .create()
                .setPrefix("http://test/prefix#")
                .pushFragment("f1", "q1")
                .pushFragment("f2", "q1")
                .pushFragment("f3", "q2")
                .pushFragment("f4", "q2")
                .popFragment()
                .popFragment()
                .build();
        Assert.assertEquals(
                "Unexpected context.",
                "http://test/prefix#q1:f1.f2",
                identifier.getIdentifier()
        );
    }

}
