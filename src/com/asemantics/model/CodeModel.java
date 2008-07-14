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


package com.asemantics.model;

import java.io.OutputStream;

/**
 * Defines the operations exposed by a generic Code Model.
 *
 * @author Michele Mostarda
 */
public interface CodeModel {
    
    /**
     * RDF schema prefix URI.
     */
    static final String RDF_SCHEMA_URI = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Defines the relation subclass of.
     */
    static final String SUBCLASSOF = RDF_SCHEMA_URI + "subClassOf";

    /**
     * RDF type prefix URI.
     */
    static final String CODER_URI = "http://www.rdfcoder.org/2007/1.0#";

    /**
     * Prefix separator definition.
     */
    static final String PREFIX_SEPARATOR = ":";
    
    /**
     * The temporary Type ID prefix.
     */
    public static final String TEMPORARY_TYPE_ID_PREFIX = "tmpTID_";

    /**
     * No elemente specified.
     */
    public static final String ALL_MATCH = null;

    /**
     * Allows to perform a triple search on the org.asemantics.model.
     * @param subject
     * @param predicate
     * @param object
     * @return
     */
    TripleIterator searchTriples(String subject, String predicate, String object);

    /**
     * Allows to add a triple into the model.
     * NOTE: this method accepts only resource objects.
     *
     * @param subject a subject resource
     * @param predicate a predicate
     * @param object a resource object
     */
    void addTriple(String subject, String predicate, String object);

    /**
     * Allows to remove a triple from the model.
     * NOTE: this method accepts only resource objects.
     *
     * @param subject
     * @param predicate
     * @param object
     * @see #addTriple(String, String, String)
     */
    void removeTriple(String subject, String predicate, String object);

    /**
     * Allows to add a triple into the model.
     * NOTE: this method accepts only literal object.
     *
     * @param subject a subject resource
     * @param predicate a predicate
     * @param literal a literal object
     */
    void addTripleLiteral(String subject, String predicate, String literal);

    /**
     * Allows to remove a triple from the model.
     * NOTE: this method accepts only literal object.
     *
     * @param subject
     * @param predicate
     * @param object
     * @see #addTripleLiteral(String, String, String)
     */
    void removeTripleLiteral(String subject, String predicate, String object);

    /**
     *  Remove the entire content of the model.
     */
    void clearAll();

}
