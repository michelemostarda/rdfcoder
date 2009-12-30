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


package com.asemantics.rdfcoder.model.java;

import com.asemantics.rdfcoder.model.Asset;

import java.util.Date;

/**
 * Defines a <i>Java</i> asset.
 */
public class JAsset implements Asset {

    /**
     * Query model associated to the asset.
     */
    private JavaQueryModel javaQueryModel;

    protected JAsset(JavaQueryModel qm) {
        javaQueryModel = qm;
    }

    /**
     * @return the list of libraries constituting this asset.
     */
    public String[] getLibraries() {
        return javaQueryModel.getLibraries();
    }

    /**
     * Returns the location of a specified library.
     *
     * @param library library name.
     * @return library location.
     */
    public String getLibraryLocation(String library) {
        return javaQueryModel.getLibraryLocation(library);
    }

    /**
     * Returns the parsing date of a library.
     *
     * @param library library name.
     * @return last time that <i>library</i> has been parsed.
     */
    public Date getLibraryDateTime(String library) {
        return javaQueryModel.getLibraryDateTime(library);
    }
    
}
