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


package com.asemantics.rdfcoder.parser;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception encapsulates every error raised
 * by a parser.
 */
public class ParserException extends Exception {

    private String effectedFile;

    public ParserException(Throwable t, String file) {
        super(t);
        effectedFile = file;
    }

    public ParserException(String msg, Throwable t) {
        super(msg, t);
    }

    public ParserException(String msg) {
        super(msg);
    }

    public void printStackTrace() {
        super.printStackTrace();
    }

    public void printStackTrace(PrintStream ps) {
        ps.println(ParserException.class.getSimpleName() + ": " + effectedFile);
        super.printStackTrace(ps);
    }

    public void printStackTrace(PrintWriter pw) {
        pw.println(ParserException.class.getSimpleName() + ": " + effectedFile);
        super.printStackTrace(pw);
    }

    public String getEffectedFile() {
        return effectedFile;
    }
}
