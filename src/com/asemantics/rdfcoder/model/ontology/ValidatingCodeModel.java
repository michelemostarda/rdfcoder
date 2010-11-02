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


package com.asemantics.rdfcoder.model.ontology;

import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.TripleIterator;

/**
 * This decorator validates addition of model triples over a given
 * ontology.
 *
 * @see com.asemantics.rdfcoder.model.CodeModel
 * @see com.asemantics.rdfcoder.model.ontology.Ontology
 */
public class ValidatingCodeModel extends CodeModelBase {

    /**
     * Decorated code model.
     */
    private CodeModel decorated;

    /**
     * Applied ontology.
     */
    private Ontology ontology;

    public ValidatingCodeModel(CodeModel codeModel, Ontology otlg) {
        if(codeModel == null || otlg == null) {
            throw new IllegalArgumentException();
        }

        decorated = codeModel;
        ontology  = otlg;
    }

    public TripleIterator searchTriples(String subject, String predicate, String object) {
        return decorated.searchTriples(subject, predicate, object);
    }

    public void addTriple(String subject, String predicate, String object) {
        try {
            ontology.validateTriple(subject, predicate, object);
        } catch (OntologyException oe) {
            throw createException(oe, subject, predicate, object);
        }
        decorated.addTriple(subject, predicate, object);
    }

    public void removeTriple(String subject, String predicate, String object) {
        decorated.removeTriple(subject, predicate, object);
    }

    public void addTripleLiteral(String subject, String predicate, String literal) {
        try {
            ontology.validateTripleLiteral(subject, predicate);
        } catch (OntologyException oe) {
            throw createException(oe, subject, predicate, literal);
        }
        decorated.addTripleLiteral(subject, predicate, literal);
    }

    public void removeTripleLiteral(String subject, String predicate, String object) {
        decorated.removeTripleLiteral(subject, predicate, object);
    }

    public void addTripleCollection(Object subject, String predicate, String[] list) {
        decorated.addTripleCollection(subject, predicate, list);
    }

    public void clearAll() {
        decorated.clearAll();
    }

    private RuntimeException createException(OntologyException cause, String s, String p, String o) {
        return new RuntimeException(
                String.format("An error occurred while validating triple { %s %s %s }", s, p, o),
                cause
        );
    }
    
}
