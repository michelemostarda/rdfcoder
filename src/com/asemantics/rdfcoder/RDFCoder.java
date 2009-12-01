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


package com.asemantics.rdfcoder;

import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.profile.Profile;
import com.asemantics.rdfcoder.repository.Repository;
import com.asemantics.rdfcoder.repository.RepositoryException;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines an high - level usage class to work with
 * <i>RDFCoder</i>.
 */
public class RDFCoder {

    /* BEGIN: repository prefixes. */

    protected static final String MODEL_RESOUCE_PREFIX = "model_";

    /* END:   repository prefixes. */

    /**
     * Debug flag default value.
     */
    private static boolean DEFAULT_DEBUG = true;

    /**
     * Debug flag.
     */
    private boolean debug = DEFAULT_DEBUG;

    /**
     * Returns the assertions level.
     * 
     * @return <code>true</code> if assertions are enabled.
     */
    public static boolean assertions() {
        return true;
    }

    /**
     * Returns the package discrepancy check flag.
     *
     * @return <code>true</code> if package check is enabled.
     */
    public static boolean checkPackageDiscrepancy() {
        return true;
    }

    /**
     * The coder factory instance.
     */
    private final CoderFactory coderFactory;

    /**
     * The working repository.
     */
    private Repository repository;

    /**
     * Coder profiles.
     */
    private Map<String, Class<Profile>> profiles;

    /**
     * Models map.
     */
    private Map<String,Model> models;

    /**
     * Creates a new RDFCoder instance on the speficied repository.
     * 
     * @param repositoryPath
     */
    public RDFCoder(String repositoryPath) {
        File repositoryFile = new File(repositoryPath);
        try {
            repository = Repository.getRepository( repositoryFile );
        } catch (RepositoryException re) {
            throw new RDFCoderException("Invalid repository path.", re);
        }

        // Factory creation.
        coderFactory = new JenaCoderFactory();
        profiles     = new HashMap<String,Class<Profile>>();
        models       = new HashMap<String,Model>  ();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean f) {
        debug = f;
    }

    /**
     * Registers a profile into <i>Coder</i>.
     *
     * @param name
     * @param clazz
     */
    public void registerProfile(String name, String clazz) {
        if(name == null || name.trim() == null) {
            throw new RDFCoderException("Invalid profile name");
        }

        // Loads class.
        Class<Profile> profileClass;
        try {
             profileClass = (Class<Profile>) this.getClass().getClassLoader().loadClass(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new RDFCoderException("Cannot load profile class.", cnfe);
        } catch (ClassCastException cce) {
            throw new RDFCoderException("Specified class: '" + clazz + "' is not subclass of " + Profile.class);
        }

        // Adds profile to map.
        profiles.put(name, profileClass);
    }

    /**
     * Returns the profile names.
     *
     * @return list of profile names.
     */
    public String[] getProfileNames() {
        return profiles.keySet().toArray( new String[profiles.keySet().size()] );
    }

    /**
     * Returns a profile type by name.
     *
     * @param name
     * @return class of profile.
     */
    protected Class<Profile> getProfileType(String name) {
        return profiles.get(name);
    }

    /**
     * Deregistes a profile.
     *
     * @param name
     */
    public void deregisterProfile(String name) {
        profiles.remove(name);
    }

    /**
     * Deregisters all defined profiles.
     */
    public void deregisterProfiles() {
        profiles.clear();
    }

    /**
     * Creates a model with the specified name.
     *
     * @param name
     * @return the created model.
     */
    public Model createModel(String name) {
        if(name == null || name.trim().length() == 0) {
            throw new RDFCoderException("Invalid model name: '" + name + "'");
        }

        if( models.containsKey(name) ) {
            throw new RDFCoderException("Model name '" + name + "' already exists.");
        }

        Model model = new Model( name, this , coderFactory );
        models.put(name, model);

        return model;
    }

    /**
     * Deletes an existing model.
     *
     * @param name
     */
    public void deleteModel(String name) {
        Model target = models.remove(name);
        if( target == null ) {
            throw new RDFCoderException("Cannot delete model '" + name + "'");
        }

        target.destroy();
    }

    /**
     * Returns <code>true<code> if model <i>name</i> exists, false otherwise.
     * 
     * @param name
     * @return <code>true</code> exists.
     */
    public boolean modelExists(String name) {
        return models.containsKey(name);
    }

    /**
     * Returns the numer of aailable models.
     *
     * @return number of models.
     */
    public int modelsCount() {
        return models.size();
    }

    /**
     * Returns the list of model names.
     *
     * @return list of model names.
     */
    public String[] getModelNames() {
        return models.keySet().toArray( new String[models.keySet().size()] );
    }

    /**
     * Returns the repository object.
     * 
     * @return repository instance.
     */
    protected Repository getRepository() {
        return repository;
    }
}
