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


package com.asemantics.rdfcoder;

/**
 * Defines a generic exception raised by {@link com.asemantics.rdfcoder.RDFCoder}.
 *
 * @see com.asemantics.rdfcoder.RDFCoder
 */
public class RDFCoderException extends RuntimeException {

    public RDFCoderException() {
        super();
    }

    public RDFCoderException(String s, Throwable t) {
        super(s, t);
    }

    public RDFCoderException(String message) {
        super(message);
    }

    public RDFCoderException(Throwable cause) {
        super(cause);
    }

}
