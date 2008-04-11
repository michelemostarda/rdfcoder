package com.asemantics.modelimpl;

import com.hp.hpl.jena.query.*;
import com.asemantics.model.QueryResult;

import java.io.PrintStream;
import java.util.List;

/**
 * The Jena implementation of <code>QueryResult</code>.
 */
public class JenaQueryResult implements QueryResult {

    QueryExecution queryExecution;

    private Query query;

    private ResultSet resultSet;

    private String[] resultVars;

    private QuerySolution querySolution;

    private boolean closed = false;

    protected JenaQueryResult(QueryExecution qe, Query qry, ResultSet rs) {
        queryExecution = qe;
        query = qry;
        resultSet = rs;

        List rvl = rs.getResultVars();
        resultVars = (String[]) rvl.toArray( new String[rvl.size()] );
    }

    public String[] getVariables() {
        return resultVars;
    }

    public String getQuery() {
        return query.serialize();
    }

    public boolean hasNext() {
        if(closed) {
            throw new IllegalStateException();
        }

        return resultSet.hasNext();
    }

    public void next() {
        if(closed) {
            throw new IllegalStateException();
        }
        
        querySolution = resultSet.nextSolution();
        if(querySolution == null) {
            throw new IllegalStateException();
        }
    }

    public String getVariable(String v) {
        return querySolution.get(v).toString();
    }

    public void close() {
        if(closed) { return; }

        querySolution = null;
        resultSet     = null;
        query         = null;
        queryExecution.close();
        queryExecution = null;
        closed = true;
    }

    public void toTabularView(PrintStream ps) {
         ResultSetFormatter.out(ps, resultSet, query) ;
    }

    public void finalize() {
        close();
    }
}
