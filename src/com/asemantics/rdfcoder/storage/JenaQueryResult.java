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


package com.asemantics.rdfcoder.storage;

import com.asemantics.rdfcoder.model.QueryResult;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.core.ResultBinding;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * The Jena implementation of the
 * {@link com.asemantics.rdfcoder.model.QueryResult}.
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
         ResultSetFormatter.out(ps, resultSet, query);
    }

    @Override
    public void toJSONView(PrintStream ps) {
        JsonFactory factory = new JsonFactory();
        String[] vars = getVariables();
        try {
            JsonGenerator generator = factory.createGenerator(new BufferedOutputStream(ps));
            generator.writeStartObject();
            generator.writeFieldName("success");
            generator.writeObject(true);
            generator.writeFieldName("query");
            generator.writeObject(query.toString());
            generator.writeFieldName("bindings");
            generator.writeStartArray();
            for(String var : vars) generator.writeObject(var);
            generator.writeEndArray();
            generator.writeFieldName("data");
            generator.writeStartArray();
            while(resultSet.hasNext()) {
                ResultBinding rb = (ResultBinding) resultSet.nextSolution();
                generator.writeStartArray();
                for(String var : vars) {
                    generator.writeObject(rb.get(var).toString());
                }
                generator.writeEndArray();
            }
            generator.writeEndArray();
            generator.writeEndObject();
            generator.flush();
        } catch (IOException ioe) {
            throw new RuntimeException("Error while instantiating a generator.", ioe);
        }
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
