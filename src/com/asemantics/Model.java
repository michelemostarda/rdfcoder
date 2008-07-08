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

import com.asemantics.repository.Repository;
import com.asemantics.profile.Profile;
import com.asemantics.model.JavaQueryModel;
import com.asemantics.model.CodeModelBase;
import com.asemantics.model.CoderFactory;
import com.asemantics.model.CodeModel;
import com.asemantics.sourceparse.ObjectsTable;

import java.lang.reflect.Constructor;


/**
 * Defines an RDF model. 
 */
public class Model {

    /**
     * The root coder object.
     */
    private RDFCoder coder;

    /**
     * The coder factory.
     */
    private CoderFactory coderFactory;

    /**
     * Internal objects table.
     */
    private ObjectsTable objectsTable;

    /**
     * Internal code model.
     */
    private CodeModelBase codeModelBase;

    protected Model(RDFCoder c, CoderFactory cf) {
        coder         = c;
        coderFactory  = cf;

        objectsTable  = new ObjectsTable();
        codeModelBase = cf.createCodeModel(); 
    }

    /**
     * Returns a profile by name.
     *
     * @param name
     * @return
     */
    public Profile getProfile(String name) {
        Class<Profile> profileClass = coder.getProfileType(name);

        Object instance;
        try {
            Constructor constructor = profileClass.getDeclaredConstructor(Model.class, Repository.class);
            instance = constructor.newInstance(this, coder.getRepository() );
        } catch (Exception e) {
            throw new RDFCoderException("Error while instantiating class.", e);
        }
        return (Profile) instance;
    }

    /**
     * Returns the query model fot the specified
     * <i>profileName</i>.
     *
     * @param profileName
     * @return
     */
    public JavaQueryModel getQueryModel(String profileName) {
        //TODO: TBI
        return null;
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
