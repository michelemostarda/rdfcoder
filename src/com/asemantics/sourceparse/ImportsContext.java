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


package com.asemantics.sourceparse;

import com.asemantics.model.CodeHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the context of the imports of the class
 * and it is able to ully qualify an object.
 */
class ImportsContext {

    /**
     * The current package of the object.
     */
    private String contextPackage;

    /**
     * Contains all objects that are imported with full qualification.
     */
    private List<String> fullyQualifiedObjects;

    /**
     * Contains all pakages imported with the star '*' notation.
     */
    private List<String> starredPackages;

    ImportsContext() { }

    public void setContextPackage(String pkg) {
        if(pkg == null) {
            throw new IllegalArgumentException();
        }
        contextPackage = pkg;
    }

    public void addFullyQualifiedObject(String pack) {
        if(fullyQualifiedObjects == null) {
            fullyQualifiedObjects = new ArrayList();
        }
        /* Disabled because many classes have repeated packages.
        else if(fullyQualifiedObjects.contains(pack)) {
            throw new IllegalArgumentException("Package '" +  pack + "' is already present in the Context.");
        }
        */
        fullyQualifiedObjects.add(pack);
    }

    public void addStarredPackage(String pack) {
        if(starredPackages == null) {
            starredPackages = new ArrayList();
        } else if(starredPackages.contains(pack)) {
            throw new IllegalArgumentException("Starred package '" + pack + "' is already defined.");
        }
        starredPackages.add(pack);
    }

    public String qualifyType(ObjectsTable objectsTable, String type) {
        if(type == null || type.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        //The object is fully qualified.
        if( checkFullyQualified(type) ) {
            return type;
        }

        // The object is not qualified:

        // finding into fully qualified objects among imports.
        if( fullyQualifiedObjects != null ) {
            String fqo;
            for(Iterator<String> fqos = fullyQualifiedObjects.iterator(); fqos.hasNext(); ) {
                fqo = fqos.next();
                if( fqo.indexOf(type) == fqo.length() - type.length() ) { // Postfix found.
                    return fqo;
                }
            }
        }

        // finding in the same package.
        if ( objectsTable.checkObject(contextPackage, type) ) {
            return contextPackage + CodeHandler.PACKAGE_SEPARATOR + type;
        }

        // finding into the Objects table using starred objects.
        if(starredPackages != null) {
            String spk;
            for(Iterator<String> spks = starredPackages.iterator(); spks.hasNext(); ) {
                spk = spks.next();
                if( objectsTable.checkObject(spk, type) ) {
                    return spk + CodeHandler.PACKAGE_SEPARATOR + type;
                }
            }
        }

        return null;
    }

    boolean checkFullyQualified(String type) {
        if( type.trim().length() == 0 ) {
            throw new IllegalArgumentException();
        }
        int packSeparator = type.indexOf(CodeHandler.PACKAGE_SEPARATOR);
        if(packSeparator == -1 ) {
            return false;
        }
        if(packSeparator == 0 || packSeparator == type.length() - 1) {
            throw new IllegalArgumentException();
        }
        return true;
    }

}
