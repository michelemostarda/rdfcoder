package com.asemantics.sourceparse;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception encapsulates every exception raised
 * by a parser.
 */
public class ParserException extends Exception {

    private String effectedFile;

    public ParserException(Throwable t, String file) {
        super(t);
        effectedFile = file;
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
