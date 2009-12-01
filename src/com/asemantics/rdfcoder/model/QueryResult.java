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

import java.io.PrintStream;

/**
 * Defines the result type of a <code>QuerableCodeModel</code>
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
    public boolean hasNext();

    /**
     * Moves to the next entry.
     */
    public void next();

    /**
     * Returns the value associated to the variable v.
     *
     * @param v
     * @return the next variable.
     */
    public String getVariable(String v);

    /**
     * Closes the result set.
     */
    public void close();

    /**
     * Prints a tabular view of the result set on the given out stream.
     * @param ps
     */
    public void toTabularView(PrintStream ps);
    
}
