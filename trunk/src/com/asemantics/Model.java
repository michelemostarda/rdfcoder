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
import com.asemantics.model.JavaQueryModel;


/**
 * Defines an RDF model. 
 */
public class Model {

    private RDFCoder coder;

    protected Model(RDFCoder c) {
        coder = c;
    }

    /**
     * Returns a profile by name.
     *
     * @param name
     * @return
     */
    public Profile getProfile(String name) {
        return coder.getProfile(name);
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
     * Destroies the model content.
     */
    protected void destroy() {
        // Empty.
    }
}
