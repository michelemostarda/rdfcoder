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


package com.asemantics.model;

import com.asemantics.rdfcoder.RDFCoder;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains base methods common to every <i>Code Model</i>.
 */
public abstract class CodeModelBase implements CodeModel, BackTrackingSupport {


    /* BEGIN: Asset classes and properties. */

    /**
     * The asset term.
     */
    public static final String ASSET_KEY        = "asset";

    /**
     * Asset subject.
     */
    public static final String ASSET            = toPrefix(ASSET_KEY);

    /**
     * Asset prefix.
     */
    public static final String ASSET_PREFIX     = toPrefix(ASSET_KEY);

    /**
     * The model asset contains library.
     */
    public static final String CONTAINS_LIBRARY = toURI("contains_library");

    /**
     * Location of a library.
     */
    public static final String LIBRARY_LOCATION = toURI("library_location");

    /**
     * Date of parsing of the library.
     */
    public static final String LIBRARY_DATETIME = toURI("library_date");

    /* END: Asset classes and properties. */


    /**
     * Marks unqualified types.
     */
    public static final String UNQUALIFIED_PREFIX = toPrefix("unqualified");

    /**
     * URI-fier method.
     *
     * @param prop
     * @return the URI form as string.
     */
    protected static String toURI(String prop) {
        return CODER_URI + prop;
    }

    /**
     * Prefix-fier method.
     * @param pref
     * @return the prefix.
     */
    protected static final String toPrefix(String pref) {
        return pref + PREFIX_SEPARATOR;
    }

    /**
     * Unique identifier discriminator counter.
     */
    private long _counter = 0;

    /**
     * Generates a temporary unique identifier.
     */
    public String generateTempUniqueIdentifier() {
        return TEMPORARY_TYPE_ID_PREFIX + System.currentTimeMillis() + "_" + _counter++;
    }

    /**
     * Returns <code>true</code> if the path is already prefixed, <code>false</code>
     * otherwise.
     *
     * @param path the path to check.
     * @return <code>true</code> if the path is prefixed.
     */
    protected static boolean isPrefixed(String path) {
         return path.indexOf(CodeModel.PREFIX_SEPARATOR) != -1;
    }

    /**
     * Prefixes a parameter with the given prefix validating both first.
     * 
     * @param prefix
     * @param parameter
     * @return the prefixed string.
     */
    protected static String prefixParameter(String prefix, String parameter) {
        if(prefix == null || prefix.trim().length() == 0 || parameter == null || parameter.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        int prefLocation = parameter.indexOf(prefix);
        if(prefLocation == -1) { // Not pefixed.
            return prefix + parameter;
        } else if(prefLocation == 0) { // Already prefixed.
            return parameter;
        } else {
            throw new IllegalArgumentException(
                "Something wrong in parameter '" + parameter + "' in applying prefix '" + prefix + "'"
            );
        }
    }

    /**
     * Returns the prefix associated to a <i>RDF</i> type.
     *
     * @param rdfType
     * @return the prefix string.
     */
    protected static String getPrefixFromRDFType(String rdfType) {
        if(rdfType.indexOf(CODER_URI) < 0) {
            throw new IllegalArgumentException("Invalid rdfType: '" + rdfType + "'");
        }
        return rdfType.substring(CODER_URI.length()) + CodeModel.PREFIX_SEPARATOR;
    }

    /**
     * Prefixes a full qualifier with the given prefix validating both first.
     *
     * @param prefix
     * @param path
     * @return the prefixed string.
     */
    protected static String prefixFullyQualifiedName(String prefix, String path) {
        if(prefix == null || prefix.trim().length() == 0 || path == null) {
            throw new IllegalArgumentException();
        }

        int prefixSeparatorIndex = path.indexOf(PREFIX_SEPARATOR);
        if(prefixSeparatorIndex == 0) { // Invalid path: prefix is ""
            throw new IllegalArgumentException("Invalid path: '" + path + "'");
        }
        if(prefixSeparatorIndex > 0) { // Removes last prefix.
            path = path.substring(prefixSeparatorIndex + 1);
        }

        return prefix + path;
    }

    /**
     * Replaces temporary identifiers with final types.
     *
     * @param identifier
     * @param qualifiedType
     * @return the number of replaced identifiers.
     * @see #generateTempUniqueIdentifier()
     */
    public int replaceIdentifierWithQualifiedType(final String identifier, final String qualifiedType) {
        int effectedTriples;

//        System.out.println("indentifier: " + identifier);
//        System.out.println("qualified type: " + qualifiedType);

        if( RDFCoder.assertions() && identifier.indexOf(CodeModel.PREFIX_SEPARATOR) == -1) {
            throw new IllegalArgumentException("identifier: " + identifier + " qualified type: " + qualifiedType);
        }

        List newTriples = new ArrayList();
        String[] nextTriple;

        // Replacing all subjects.

        // Creates new triples image.
        TripleIterator ti = searchTriples(identifier, CodeModel.ALL_MATCH, JavaCodeModel.ALL_MATCH);
        while(ti.next()) {
            newTriples.add( new String[] { ti.getPredicate(), ti.getObject() } );
        }
        effectedTriples = newTriples.size();

        // Delete old triples.
        Iterator<String[]> newTriplesIter = newTriples.iterator();
        while(newTriplesIter.hasNext()) {
            nextTriple = newTriplesIter.next();
            removeTriple( identifier, nextTriple[0], nextTriple[1] );
        }
        // Add new ones.
        newTriplesIter = newTriples.iterator();
        while(newTriplesIter.hasNext()) {
            nextTriple = newTriplesIter.next();
            removeTriple( identifier, nextTriple[0], nextTriple[1] );
        }

        // Replacing all objects.

        newTriples.clear();

        // Creates new triples image.
        ti = searchTriples(CodeModel.ALL_MATCH, CodeModel.ALL_MATCH, identifier);
        while(ti.next()) {
            newTriples.add( new String[] {ti.getSubject(), ti.getPredicate() } );
        }
        effectedTriples += newTriples.size();

        // Delete old triples.
        newTriplesIter = newTriples.iterator();
        while(newTriplesIter.hasNext()) {
            nextTriple = newTriplesIter.next();
            removeTriple( nextTriple[0], nextTriple[1], identifier );
        }
        // Add new ones.
        newTriplesIter = newTriples.iterator();
        while(newTriplesIter.hasNext()) {
            nextTriple = newTriplesIter.next();
            removeTriple( nextTriple[0], nextTriple[1], qualifiedType );
        }

        return effectedTriples;
    }
}
