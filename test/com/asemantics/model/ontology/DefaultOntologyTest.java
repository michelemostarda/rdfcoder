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


package com.asemantics.model.ontology;

import junit.framework.TestCase;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.PrintStream;

import com.asemantics.model.CodeModel;

/**
 * JUnit test of class {@link com.asemantics.model.ontology.DefaultOntology}
 */
public class DefaultOntologyTest extends TestCase {

    private Ontology ontology;

    private final static String SUB_PREFIX = "sub_prefix_";
    private final static String OBJ_PREFIX = "obj_prefix_";
    private final static String PREDICATE  = "http://path.to.url/test_";

    private final static int SIZE = 100;

    protected void setUp() throws Exception {
        ontology = new DefaultOntology();
    }

    protected void tearDown() throws Exception {
    }

    public void testPopulate() throws OntologyException, MalformedURLException {
       for(int i = 0; i < SIZE; i++) {
           ontology.defineRelation(SUB_PREFIX + i, new URL(PREDICATE + (i % 2) ), OBJ_PREFIX + i);
       }
       assertEquals(ontology.getRelationsCount(), SIZE);
    }

    public void testNoRedefinition() throws MalformedURLException, OntologyException {
        final String sub = SUB_PREFIX;
        final URL   pred = new URL(PREDICATE);
        final String obj = OBJ_PREFIX;
        ontology.defineRelation(sub, pred, obj);
        try {
            ontology.defineRelation(sub, pred, obj);
            fail();
        } catch (OntologyException oe) {}
    }

    class Counter {
        int count = 0;

        void increment() {
            count++;
        }
    }

    public void testPrint() throws MalformedURLException, OntologyException {
        testPopulate();

        final Counter counter = new Counter();
        ontology.printOntology(new PrintStream(System.out) {

            public void println() {
                super.println();
                counter.increment();
            }
        });

        assertEquals(SIZE, counter.count);
    }

    public void testPositiveValidation() throws MalformedURLException, OntologyException {
        testPopulate();
        for(int i = 0; i < SIZE; i++) {
            ontology.validateTriple(
                    SUB_PREFIX + i + CodeModel.PREFIX_SEPARATOR + "postfix",
                    PREDICATE  + (i % 2),
                    OBJ_PREFIX + i + CodeModel.PREFIX_SEPARATOR + "postfix2"
             );
        }
    }

     public void testNegativeValidation() throws MalformedURLException, OntologyException {
        testPopulate();
        try {
            ontology.validateTriple(
                    SUB_PREFIX + 0 + CodeModel.PREFIX_SEPARATOR + "postfix",
                    PREDICATE  + 1,
                    OBJ_PREFIX + 0 + CodeModel.PREFIX_SEPARATOR + "postfix2"
            );
            fail();
        } catch (OntologyException oe) {
            // Ok.
        }
    }

    public void testAccess() throws MalformedURLException, OntologyException {
        testPopulate();

        for( int i = 0; i < ontology.getRelationsCount(); i++) {
            assertEquals(SUB_PREFIX + i      , ontology.getRelationSubjectPrefix(i) );
            assertEquals(OBJ_PREFIX + i      , ontology.getRelationObjectPrefix(i) );
            assertEquals(PREDICATE  + (i % 2), ontology.getRelationPredicate(i) );
        }
    }
}
