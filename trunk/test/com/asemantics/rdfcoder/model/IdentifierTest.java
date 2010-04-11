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

import java.util.Arrays;

/**
 * Test case for {@link com.asemantics.rdfcoder.model.Identifier} class.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class IdentifierTest {

    private static final String TEST_PREFIX = "http://test/prefix#";

    private static final IdentifierFragment[] TEST_FRAGMENTS = new IdentifierFragment[]{
        new IdentifierFragment("fragment1", "qualifier1"),
        new IdentifierFragment("fragment2", "qualifier2"),
        new IdentifierFragment("fragment3", "qualifier3"),
    };

    private Identifier identifier;

    @Before
    public void setUp() {
        identifier = new Identifier(TEST_PREFIX, TEST_FRAGMENTS);
    }

    @After
    public void tearDown() {
        identifier = null;
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getPrefix()} method.
     */
    @Test
    public void testGetPrefix() {
        Assert.assertEquals( "Unespected prefix.", TEST_PREFIX, identifier.getPrefix() );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getIdentifier()} method.
     */
    @Test
    public void testGetIdentifier() {
        String identifierStr = identifier.getIdentifier();
        Assert.assertEquals(
                "Unespected identifier.",
                "http://test/prefix#qualifier1:fragment1.qualifier2:fragment2.qualifier3:fragment3",
                identifierStr
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getTail()} method.
     */
    @Test
    public void testGetTail() {
        Identifier tail = identifier.getTail();
        Assert.assertEquals(
                "Unexpected tail.",
                new Identifier(
                        TEST_PREFIX,
                        Arrays.asList(TEST_FRAGMENTS[TEST_FRAGMENTS.length - 1])
                ),
                tail
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getParent()} method.
     */
    @Test
    public void testGetParent() {
        Identifier pretail = identifier.getParent();
        Assert.assertEquals(
                "Unexpected parent.",
                new Identifier(TEST_PREFIX, new IdentifierFragment[]{ TEST_FRAGMENTS[0], TEST_FRAGMENTS[1] } ),
                pretail
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getHead()} method.
     */
    @Test
    public void testGetHead() {
        Identifier head = identifier.getHead();
        Assert.assertEquals(
                "Unexpected head.",
                new Identifier(TEST_PREFIX, new IdentifierFragment[]{TEST_FRAGMENTS[0]}),
                head
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getSections(int, int)} method.
     */
    @Test
    public void testGetSections() {
        Identifier empty = identifier.getSections(0, 0);
        Assert.assertTrue("Expected to be empty.", empty.size() == 0);

        Identifier all = identifier.getSections(0, identifier.size());
        Assert.assertEquals("Expected to be the same.", identifier, all);
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getFirstFragmentWithQualifier(String)} method.
     */
    @Test
    public void testGetFirstFragmentWhithQualifier() {
        Assert.assertEquals(
                "Unexpected fragment.",
                "fragment3",
                identifier.getFirstFragmentWithQualifier("qualifier3")
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getLastFragmentWithQualifier(String)} method.
     */
    @Test
    public void testGetLastFragmentWithQualifier() {
        Assert.assertEquals(
                "Unexpected fragment.",
                "fragment3",
                identifier.getLastFragmentWithQualifier("qualifier3")
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#getStrongestQualifier()} method.
     */
    @Test
    public void testGetStrongestQualifier() {
        Assert.assertEquals(
                "Unexpected value for strongest qualifier.",
                "qualifier3",
                identifier.getStrongestQualifier()
        );
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#copy()} method.
     */
    @Test
    public void testCopy() {
        Identifier copy = identifier.copy().build();
        Assert.assertEquals("Invalid copy.", identifier, copy);
    }

    /**
     * Test the {@link com.asemantics.rdfcoder.model.Identifier#size()} method.
     */
    @Test
    public void testSize() {
        Assert.assertEquals("Unexpected size.", TEST_FRAGMENTS.length, identifier.size() );
    }

}
