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

/**
 * Defines an exception raised by a {@link com.asemantics.rdfcoder.model.CodeModel}
 * implementation whilst a debug control fails.
 */
public class CodeModelDebugException extends RuntimeException {

    public CodeModelDebugException() {
        super();
    }

    public CodeModelDebugException(String message) {
        super(message);
    }

    public CodeModelDebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeModelDebugException(Throwable cause) {
        super(cause);
    }
    
}
