/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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


package com.asemantics.rdfcoder.model;

/**
 * Defines the <i>Code Model</i>, that is a graph representing
 * the <i>Kabbalah</i> model. 
 *
 * @author Michele Mostarda
 */
public interface CodeModel {
    
    /**
     * RDF schema prefix URI.
     */
    String RDF_SCHEMA_URI = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Defines the relation subclass of.
     */
    String TYPE = RDF_SCHEMA_URI + "type";

    /**
     * The separator used for URI prefixes.
     */
    String URI_PREFIX_SEPARATOR = "#";

    /**
     * Coder base prefix URI.
     */
    String CODER_URI_BASE = "http://www.rdfcoder.org/2007/1.0";

    /**
     * Coder prefix URI with prefix separator.
     */
    String CODER_URI = CODER_URI_BASE + URI_PREFIX_SEPARATOR;

    /**
     * Prefix separator definition.
     */
    String PREFIX_SEPARATOR = ":";
    
    /**
     * The temporary Type ID prefix.
     */
    String TEMPORARY_TYPE_ID_PREFIX = "tmpTID_";

    /**
     * No element specified.
     */
    String ALL_MATCH = null;

    /**
     * Allows to perform a triple search on the org.asemantics.model.
     * @param subject
     * @param predicate
     * @param object
     * @return the iterator to access the search result.
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
     * @param subject the triple subject.
     * @param predicate the triple predicate.
     * @param object the triple object.
     * @see #addTripleLiteral(String, String, String)
     */
    void removeTripleLiteral(String subject, String predicate, String object);

    /**
     * Adds a triple which object is a collection.
     *
     * @param subject the triple subject.
     * @param predicate the triple predicate.
     * @param values the triple values list.
     */
    void addTripleCollection(Object subject, String predicate, String[] values);

    /**
     *  Remove the entire content of the model.
     */
    void clearAll();

}
