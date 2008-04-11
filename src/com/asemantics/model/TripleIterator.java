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
