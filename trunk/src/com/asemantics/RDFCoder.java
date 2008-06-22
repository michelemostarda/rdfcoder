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

import com.asemantics.profile.Profile;
import com.asemantics.repository.Repository;
import com.asemantics.repository.RepositoryException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines an high - level usage class to work with
 * <i>RDFCoder</i>.
 */
public class RDFCoder {

    /**
     * Debug flag default value.
     */
    private static boolean DEFAULT_DEBUG = true;

    /**
     * <i>Check package discrepancy</i> flag defualt value.
     */
    private static boolean DEFAULT_VALIDATING_MODEL = true;

    /**
     * Debug flag.
     */
    private boolean debug = DEFAULT_DEBUG;

    /**
     * Check package discrepancy flag.
     */
    private boolean validatingModel = DEFAULT_VALIDATING_MODEL;

    /**
     * Returns the assertions level.
     * 
     * @return
     * //TODO: replace with isDebug()
     */
    public static boolean assertions() {
        return true;
    }

    /**
     * Returns the package discrepancy check flag.
     *
     * @return
     */
    public static boolean checkPackageDiscrepancy() {
        return true;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean f) {
        debug = f;
    }

    public boolean isValidatingModel() {
        return validatingModel;
    }

    public void setValidatingModel(boolean f) {
        validatingModel = f;
    }

    /**
     * The working repository.
     */
    private Repository repository;

    /**
     * Coder profiles.
     */
    private Map<String, Profile> profiles;

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

        // Loading class.
        Class profileClass;
        Object instance;
        Profile profile;
        try {
             profileClass = this.getClass().getClassLoader().loadClass(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new RDFCoderException("Cannot load profile class.", cnfe);
        }
        try {
            instance = profileClass.newInstance();
        } catch (Exception e) {
            throw new RDFCoderException("Error while instantiating class.", e);
        }
        try {
            profile = (Profile) instance;
        } catch (ClassCastException cce) {
            throw new RDFCoderException("The specified class: '" + clazz + "' is not a " + Profile.class.getName() + " type", cce );
        }

        // Adds profile to map.
        if(profiles == null) {
            profiles = new HashMap<String,Profile>();
        }
        profiles.put(name, profile);
    }

    /**
     * Returns the profile names.
     *
     * @return
     */
    public String[] getProfileNames() {
        return profiles.keySet().toArray( new String[profiles.keySet().size()] );
    }

    /**
     * Returns a profile by name.
     *
     * @param name
     * @return
     */
    protected Profile getProfile(String name) {
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
        if(profiles != null) {
            profiles.clear();
        }
    }

    /**
     * Creates a model with the specified name.
     *
     * @param name
     * @return
     */
    public Model createModel(String name) {
        if(name == null || name.trim().length() == 0) {
            throw new RDFCoderException("Invalid model name: '" + name + "'");
        }

        if( models.containsKey(name) ) {
            throw new RDFCoderException("Model name '" + name + "' already exists.");
        }

        Model model = new Model( this );
        models.put(name, model);

        return model;
    }

    /**
     * Deletes an existing model.
     *
     * @param name
     */
    public void deleteModel(String name) {
        Model target = models.get(name);
        if( target == null ) {
            throw new RDFCoderException("Cannot delete model '" + name + "'");
        }

        target.destroy();
    }

    /**
     * Returns <code>true<code> if model <i>name</i> exists, false otherwise.
     * 
     * @param name
     * @return
     */
    public boolean modelExists(String name) {
        return models.containsKey(name);
    }

    /**
     * Returns the numer of aailable models.
     *
     * @return
     */
    public int modelsCount() {
        return models.size();
    }

    /**
     * Returns the list of model names.
     *
     * @return
     */
    public String[] getModelNames() {
        return models.keySet().toArray( new String[models.keySet().size()] );
    }


    
}
