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


package com.asemantics.rdfcoder.storage;

import com.asemantics.model.QueryResult;
import com.hp.hpl.jena.query.*;

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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( this.getClass().getName() );
        String[] variables;
        variables = getVariables();
        sb.append("{");
        while ( hasNext() ) {
            next();
            for(String v : variables) {
                sb.append( getVariable(v) );
                sb.append("\t");
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
