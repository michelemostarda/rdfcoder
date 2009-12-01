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

package com.asemantics.rdfcoder.inspector;

/**
 * Exception that can occur while inspecting a bean graph.
 */
public class InspectorParserException extends Exception {

    public static final int NO_LOCATION = -1;

    /**
     * Error location index.
     */
    private int errorLocation = NO_LOCATION;

    /**
     * Constructor.
     */
    public InspectorParserException() {
        super();
    }

    /**
     * Constructor.
     */
    public InspectorParserException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message
     * @param el
     */
    public InspectorParserException(String message, int el) {
        super(message);
        errorLocation = el;
    }

    /**
     * Constructor.
     *
     * @param message
     * @param cause
     */
    public InspectorParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause
     */
    public InspectorParserException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns <code>true</code> if error location is well known.
     *
     * @return <code>true</code> if error location is supported.
     */
    public boolean hasErrorLocation() {
        return errorLocation != NO_LOCATION;
    }

    /**
     * Returns the error location.
     * 
     * @return line number of the error.
     */
    public int getErrorLocation() {
        return errorLocation;
    }

    public String toString() {
        return super.toString() + ( hasErrorLocation() ? " location: " + errorLocation : "" ); 
    }

}
