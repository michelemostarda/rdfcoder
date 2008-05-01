/**
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

/**
 * Represents a method signature.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public interface TripleIterator {

    /**
     * moves to the next statement, retuns
     * <code>true</code> if the list of statements
     * has not been reahed, <code>false</code> otherwise.
     * @return
     */
    boolean next();

    /**
     * Returns the subject of the current statement.
     * @return
     */
    String getSubject();

    /**
     * Returns the predicate of the current statement.
     * @return
     */

    String getPredicate();

    /**
     * Returns the object of the current statement.
     * @return
     */
    String getObject();

    /**
     * Coses the statement.
     */
    void close();
}
