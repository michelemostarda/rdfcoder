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

import com.asemantics.rdfcoder.RDFCoder;
import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CodeModelDebugException;
import com.asemantics.rdfcoder.model.QueryResult;
import com.asemantics.rdfcoder.model.SPARQLException;
import com.asemantics.rdfcoder.model.SPARQLQuerableCodeModel;
import com.asemantics.rdfcoder.model.TripleIterator;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@link com.asemantics.rdfcoder.model.CodeModel}
 * implementation for the <i>Jena</i> backend.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JenaCodeModel extends SPARQLQuerableCodeModel {

    public static String getURI() {
        return CODER_URI;
    }

    private static Model m = ModelFactory.createDefaultModel();

    /**
     * Internal Jena org.asemantics.model.
     */
    private Model jenaModel;

    protected JenaCodeModel(Model model) {
        if(model == null) {
            throw new NullPointerException();
        }
        this.jenaModel = model;
    }

    protected Model getJenaModel() {
        return jenaModel;
    }

    class InternalTripleIterator implements TripleIterator {

        private StmtIterator iter;

        private Statement statement;

        InternalTripleIterator(StmtIterator iter) {
            this.iter = iter;
        }

        public boolean next() {
            if(iter.hasNext()) {
                statement = iter.nextStatement();
                return true;
            }
            return false;
        }

        public String getSubject() {
            return statement.getSubject().toString();
        }

        public String getPredicate() {
            return statement.getPredicate().toString();
        }

        public String getObject() {
            return statement.getObject().toString();
        }

        public void close() {
            iter.close();
        }
    }

    public TripleIterator searchTriples(final String subject, final String predicate, final String object) {
        StmtIterator iter = jenaModel.listStatements(new SimpleSelector(
                subject   != null ? jenaModel.createResource(subject)   : null,
                predicate != null ? jenaModel.createProperty(predicate) : null,
                object    != null ? jenaModel.createResource(object)    : null
        ));
        return new InternalTripleIterator(iter);
    }

    public void addTriple(String subject, String predicate, String object) {
        checkTriple(subject, predicate, object);
        
        Resource s = jenaModel.createResource(subject);
        Property p = jenaModel.createProperty(predicate);
        Resource o = jenaModel.createResource(object);
        s.addProperty(p, o);
    }

    public void removeTriple(String subject, String predicate, String object) {
        Resource s = jenaModel.createResource(subject);
        Property p = jenaModel.createProperty(predicate);
        Resource o = jenaModel.createResource(object);
        jenaModel.remove(s, p, o);
    }

    public void addTripleLiteral(String subject, String predicate, String literal) {
        checkTriple(subject, predicate, literal);
        Resource s = jenaModel.createResource(subject);
        Property p = jenaModel.createProperty(predicate);
        Literal  l = jenaModel.createLiteral(literal);
        s.addProperty(p, l);
    }

    public void removeTripleLiteral(String subject, String predicate, String object) {
        Resource s = jenaModel.createResource(subject);
        Property p = jenaModel.createProperty(predicate);
        Literal  l = jenaModel.createLiteral(object);
        jenaModel.remove(s, p, l);
    }

    public void addTripleCollection(Object subject, String predicate, String[] object) {
        final Resource s;
        if(subject instanceof String) {
            s = jenaModel.createResource((String) subject);
        } else if(subject instanceof Resource) {
            s = (Resource) subject;
        } else {
            throw new IllegalArgumentException("Invalid subject.");
        }
        Property p = jenaModel.createProperty(predicate);
        Bag      b = jenaModel.createBag();
        for(String bagElem : object) {
            b.add(bagElem);
        }
        jenaModel.add(s, p, b);
    }

    public void clearAll() {
        jenaModel.removeAll();
    }

    /**
     * Loads the model from a file.
     *
     * @param cm
     * @param modelId
     * @param persistenceFile
     * @throws IOException
     */
    public void load(CodeModel cm, String modelId, File persistenceFile) throws IOException {
        checkParameters( cm, modelId, persistenceFile);

        Model jenaModel = ( (JenaCodeModel) cm).getJenaModel();
        FileReader fr = new FileReader( persistenceFile );
        jenaModel.read(fr, "");
        fr.close();
    }

    /**
     * Saves the model into a file.
     *
     * @param cm
     * @param modelId
     * @param persistenceFile
     * @throws IOException
     */
    public void save(CodeModel cm, String modelId, File persistenceFile) throws IOException {
        checkParameters( cm, modelId, persistenceFile);

        Model jenaModel = ( (JenaCodeModel) cm).getJenaModel();
        FileWriter fw = new FileWriter( persistenceFile );
        jenaModel.write(fw);
        fw.flush();
        fw.close();
    }

    private void checkTriple(String subject, String predicate, String object) {
         if( RDFCoder.assertions() ) {
            if(subject.length() == 0 ) {
                throw new CodeModelDebugException("invalid 0 length subject.");
            }
            if( predicate.length() == 0) {
                throw new CodeModelDebugException("invalid 0 length predicate.");
            }
            if( object.length() == 0) {
                throw new CodeModelDebugException("invalid 0 length object.");
            }
        }
    }

    /**
     * Checks the storage parameters.
     * 
     * @param cm
     * @param modelId
     * @param persistenceFile
     */
    private void checkParameters(CodeModel cm, String modelId, File persistenceFile) {
        if(cm == null) {
            throw new NullPointerException("cm cannot be null");
        }
        if(modelId == null) {
            throw new NullPointerException("modelId cannot be null");
        }
        if(persistenceFile == null) {
            throw new NullPointerException("persistenceFile cannot be null");
        }
        if( ! persistenceFile.exists() ) {
            throw new IllegalArgumentException("persitenceFile: '" + persistenceFile.getAbsoluteFile() + "' cannot be found.");
        }
        if(! (cm instanceof JenaCodeModel) ) {
            throw new IllegalArgumentException("cm is not a JenaCodeHandler instance but: " + cm.getClass().getName());
        }
    }

    public String toString() {
        return this.getClass().getName() + "{" + jenaModel.toString() + "}";
    }

    /**
     * Check the content of the code model.
     * @param model
     */
    public static void checkModel(Model model) throws CodeModelDebugException {
        StmtIterator iterator = model.listStatements();
        Statement statement;
        while( iterator.hasNext() ) {
            statement =iterator.nextStatement();
            if(
                    statement.getSubject().toString().length()   == 0
                    ||
                    statement.getPredicate().toString().length() == 0
                    ||
                    statement.getObject().toString().length()    == 0
                    ||
                    statement.getSubject().toString().indexOf(":") == -1
                    ||
                    statement.getPredicate().toString().indexOf(":") == -1
            ) {
                throw new CodeModelDebugException("Invalid model: found error in statement: " + statement);
            }
        }
        iterator.close();
    }

    public QueryResult performQuery(String sparqlQry) throws SPARQLException {
        Query query = QueryFactory.create(sparqlQry);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, jenaModel);
          try {
            ResultSet results = queryExecution.execSelect() ;
            QueryResult queryResult = new JenaQueryResult(queryExecution, query, results);
            return queryResult;
          } catch(Throwable t) {
              queryExecution.close(); // Query execution is closed only if an error occurs during query excution.
              throw new SPARQLException("Error during execution of SPARQL query: '" + sparqlQry + "'", t);
          }
    }
    
}
