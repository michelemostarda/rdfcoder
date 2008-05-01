/**
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


package com.asemantics.modelimpl;

import com.asemantics.RDFCoder;
import com.asemantics.model.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The <code>CodeModel</code> implementation for the Jena backend.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JenaCodeModel extends SPARQLQuerableCodeModel {

    public static String getURI() {
        return URI;
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

//    int tripleTTT = 0;
    public void addTriple(String subject, String predicate, String object) {
        if(RDFCoder.isDEBUG()) {
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
                System.err.println("Found error at statement: " + statement);
                throw new CodeModelDebugException("Invalid model");
            }
        }
        iterator.close();
    }

    public QueryResult performQuery(String sparqlQry) {
        Query query = QueryFactory.create(sparqlQry);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, jenaModel);
          try {
            ResultSet results = queryExecution.execSelect() ;
            QueryResult queryResult = new JenaQueryResult(queryExecution, query, results);
            return queryResult;
          } catch(Throwable t) {
              queryExecution.close();
              t.printStackTrace();
              throw new RuntimeException(t);
          }
    }
    
}
