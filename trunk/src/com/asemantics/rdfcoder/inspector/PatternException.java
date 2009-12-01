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
 * Describes an exception raised while parsing a pattern expression.
 */
public class PatternException extends Exception {

    /**
     * Constructor.
     */
    public PatternException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message
     */
    public PatternException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message
     * @param cause
     */
    public PatternException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public PatternException(Throwable cause) {
        super(cause);
    }
}
