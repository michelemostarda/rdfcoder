package com.asemantics.model;

/**
 * Defines any exception raised by the <code>QueryModel</code>
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class QueryModelException extends Exception {

    public QueryModelException(String msg) {
        super(msg);
    }

    public QueryModelException(String msg, CodeModelException cme) {
        super(msg, cme);
    }

    public QueryModelException(CodeModelException cme) {
        super(cme);
    }
    
}
