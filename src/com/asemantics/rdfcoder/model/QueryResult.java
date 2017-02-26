/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Defines the result object of a
 * {@link com.asemantics.rdfcoder.model.SPARQLQuerableCodeModel}.
 */
public interface QueryResult {

    /**
     * Returns all the variables involved in the query.
     *
     * @return list of variables.
     */
    String[] getVariables();

    /**
     * Returns the original query.
     *
     * @return the query string.
     */
    String getQuery();

    /**
     * True if the result set contains other entries.
     *
     * @return <code>true</code> if has a next element.
     */
    boolean hasNext();

    /**
     * Moves to the next entry.
     */
    void next();

    /**
     * Returns the value associated to the variable v.
     *
     * @param v
     * @return the next variable.
     */
    String getVariable(String v);

    /**
     * Closes the result set.
     */
    void close();

    /**
     * Prints a tabular view of the result set on the given out stream.
     * @param ps
     */
    void toTabularView(PrintStream ps);

    /**
     * Prints a JSON view of the result set on the given out stream.
     * @param generator used to compose valid JSON data output.
     */
    void toJSONView(JsonGenerator generator) throws IOException;
    
}
