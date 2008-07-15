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


package com.asemantics;

import com.asemantics.model.*;
import com.asemantics.model.ontology.ValidatingCodeModel;
import com.asemantics.profile.Profile;
import com.asemantics.repository.Repository;
import com.asemantics.repository.RepositoryException;
import com.asemantics.sourceparse.ObjectsTable;
import com.asemantics.storage.CodeStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


/**
 * Defines an RDF model. 
 */
public class Model {

    /**
     * <i>Check package discrepancy</i> flag defualt value.
     */
    private static boolean DEFAULT_VALIDATING_MODEL = true;

    /**
     * Check package discrepancy flag.
     */
    private boolean validatingModel = DEFAULT_VALIDATING_MODEL;

    /**
     * The validating code model instance.
     */
    private ValidatingCodeModel validatingCodeModel;

    /**
     * The model name.
     */
    private String name;

    /**
     * The root coder object.
     */
    private RDFCoder coder;

    /**
     * The coder factory.
     */
    private CoderFactory coderFactory;

    /**
     * The map of profile instances.
     */
    private final Map<String, Profile> profileInstances;

    /**
     * Internal objects table.
     */
    private final ObjectsTable objectsTable;

    /**
     * The code model base.
     */
    private final CodeModelBase codeModelBase;

    /**
     * The current model.
     */
    private CodeModelBase currentModel;

    /**
     * The code storage instance.
     */
    private final CodeStorage codeStorage;

    protected Model(String n, RDFCoder c, CoderFactory cf) {
        name          = n;
        coder         = c;
        coderFactory  = cf;

        profileInstances = new HashMap<String,Profile>();
        objectsTable     = new ObjectsTable();
        codeModelBase    = cf.createCodeModel();
        currentModel     = codeModelBase;
        codeStorage      = cf.createCodeStorage();
    }

    public boolean isValidating() {
        return validatingModel;
    }
    
    public void setValidating(boolean f) {
        if(f) {
            if( validatingCodeModel == null) {
                validatingCodeModel = new ValidatingCodeModel(codeModelBase, new JavaOntology() ); //TODO HIGH geberalize this (ontology is related to profiles)
            }
            currentModel = validatingCodeModel;
        } else {
            currentModel = codeModelBase;
        }
        validatingModel = f;
    }

    /**
     * Returns the model name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a profile by name.
     *
     * @param name
     * @return
     */
    public Profile getProfile(String name) {
        Profile profile = profileInstances.get(name);
        if(profile != null) {
            return profile;
        }

        Class<Profile> profileClass = coder.getProfileType(name);

        Profile instance;
        try {
            Constructor constructor = profileClass.getDeclaredConstructor(Model.class, CodeStorage.class, Repository.class);
            instance = (Profile) constructor.newInstance(this, codeStorage, coder.getRepository() );
        } catch (Exception e) {
            throw new RDFCoderException("Error while instantiating class.", e);
        }
        profileInstances.put(name, instance);
        return instance;
    }

    /**
     * Returns <code>true</code> if this model supports <i>SPARQL</i> queries,
     * <code>false</code> otherwise.
     * 
     * @return
     */
    public boolean supportsSparqlQuery() {
        return codeModelBase instanceof SPARQLQuerableCodeModel;
    }

    /**
     * Performs a SPARQL query on this model.
     *
     * @param sparql
     * @return
     * @throws ClassCastException if this model doesn't support <i>SPARQL</i> queries.
     * @see #supportsSparqlQuery()
     */
    public QueryResult sparqlQuery(String sparql) {
        try {
            return ( (SPARQLQuerableCodeModel) codeModelBase).performQuery(sparql);
        } catch (SPARQLException sparqle) {
            throw new RDFCoderException("Error while perfoming SPARQL query.", sparqle);
        }
    }

    /**
     * Loads the content of the resource name into the current
     * <i>Model</i>.
     *
     * @param resouceName name of the model containing the resource
     * @return
     */
    public void load(String resouceName) {

        // Retrieve resource.
        Repository.Resource resource = retrieveResource( getModelResourceName(resouceName) );

        // Load model.
        InputStream inputStream = null;
        CodeStorage codeStorage = getCoderFactory().createCodeStorage();
        try {
            inputStream = resource.getInputStream();
            codeStorage.loadModel(codeModelBase, inputStream);
        } catch (Exception e) {
            throw new RDFCoderException("Cannot load model.", e);
        } finally {
            if( inputStream != null ) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Saves the content of the current <i>Model</i>
     * on the underlying <i>repository</i> in a resource with given name.
     *
     * @param name the name of the resource in which to store the model.
     */
    public void save(String name) {

        // Retrieve resource.
        Repository.Resource resource = retrieveResource( getModelResourceName( name ) );

        // Save model.
        OutputStream outputStream = null;
        CodeStorage codeStorage = getCoderFactory().createCodeStorage();
        try {
            outputStream = resource.getOutputStream();
            codeStorage.saveModel(codeModelBase, outputStream);
        } catch (Exception e) {
            throw new RDFCoderException("Cannot save model.", e);
        } finally {
            if( outputStream != null ) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Returns the name of the resource in {@link com.asemantics.repository.Repository}
     * for this model.
     *
     * @return
     */
    public String getModelResourceName() {
       return getModelResourceName(name);
    }

    /**
     * Saves the content of the current model
     * in a resource with the same name of the model.
     */
    public void save() {
        save( getName() );
    }

    /**
     * Clears the content of this model.
     */
    public void clear() {
        codeModelBase.clearAll();
    }

    /**
     * Retrieves the resource with the given name.
     *
     * @param rn
     * @return
     */
    protected Repository.Resource retrieveResource(String rn) {
        Repository repository = coder.getRepository();
        Repository.Resource resource;
        try {
            if( repository.containsResource( rn ) ) {
                resource = repository.getResource( rn );
            } else {
                resource = repository.createResource( rn, Repository.ResourceType.XML );
            }
        } catch (RepositoryException re) {
            throw new RDFCoderException("Cannot access resource '" + rn + "'");
        }
        return resource;
    }

    /**
     * Returns the name of the resource associated to this model.
     *
     * @param modelName
     * @return
     */
    protected String getModelResourceName(String modelName) {
        return  RDFCoder.MODEL_RESOUCE_PREFIX + modelName;
    }

    /**
     * Returns the {@link com.asemantics.model.CoderFactory} insstance for this model.
     *
     * @return
     */
    protected CoderFactory getCoderFactory() {
        return coderFactory;
    }

    /**
     * Returns the objects table associated to this model.
     *
     * @return
     */
    protected ObjectsTable getObjectsTable() {
        return objectsTable;
    }

    /**
     * Returns the {@link com.asemantics.model.CodeModelBase} common underlying instance.
     * @return
     */
    protected CodeModelBase getCodeModelBase() {
        return codeModelBase;
    }

    /**
     * Destroies the model content.
     */
    protected void destroy() {
        // Empty.
    }

}
