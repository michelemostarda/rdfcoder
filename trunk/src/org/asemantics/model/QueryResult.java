package com.asemantics.model;

import java.io.PrintStream;

/**
 * Defines the result type of a <code>QuerableCodeModel</code>
 */
public interface QueryResult {

    /**
     * Returns all the variables involved in the query.
     * @return
     */
    String[] getVariables();

    /**
     * Returns the original query.
     * @return
     */
    String getQuery();

    /**
     * True if the result set contains other entries.
     * @return
     */
    public boolean hasNext();

    /**
     * Moves to the next entry.
     */
    public void next();

    /**
     * Returns the value associated to the variable v.
     * @param v
     * @return
     */
    public String getVariable(String v);

    /**
     * Closes the result set.
     */
    public void close();

    /**
     * Prints a tabular view of the result set on the given out stream.
     * @param ps
     */
    public void toTabularView(PrintStream ps);
    
}
