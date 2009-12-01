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


package com.asemantics.rdfcoder.model;

/**
 * Exception raised by {@link com.asemantics.rdfcoder.model.SPARQLQuerableCodeModel} if any
 * error occurs during the execution of the <i>SPARQL</i> query.
 */
public class SPARQLException extends Exception {

    public SPARQLException(String msg) {
        super(msg);
    }

    public SPARQLException() {
        super();
    }

    public SPARQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPARQLException(Throwable cause) {
        super(cause);
    }
}
