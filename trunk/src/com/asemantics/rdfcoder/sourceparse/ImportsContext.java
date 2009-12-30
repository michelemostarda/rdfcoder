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


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the imports of a class
 * and it is able to fully qualify an object.
 */
class ImportsContext {

    /**
     * The current package of the object.
     */
    private Identifier contextPackage;

    /**
     * Contains all objects that are imported with full qualification.
     */
    private List<Identifier> fullyQualifiedObjects;

    /**
     * Contains all packages imported with the star '*' notation.
     */
    private List<Identifier> starredPackages;


    ImportsContext(){}

    public void setContextPackage(String pkg) {
        contextPackage = IdentifierReader.readPackage(pkg);
    }

    public void addFullyQualifiedObject(String pack) {
        if(fullyQualifiedObjects == null) {
            fullyQualifiedObjects = new ArrayList<Identifier>();
        }
        /* Disabled because many classes have repeated packages.
        else if(fullyQualifiedObjects.contains(pack)) {
            throw new IllegalArgumentException("Package '" +  pack + "' is already present in the Context.");
        }
        */
        fullyQualifiedObjects.add(IdentifierReader.readPackage(pack) );
    }

    public void addStarredPackage(String pack) {
        if(starredPackages == null) {
            starredPackages = new ArrayList<Identifier>();
        } else if(starredPackages.contains(pack)) {
            throw new IllegalArgumentException("Starred package '" + pack + "' is already defined.");
        }
        starredPackages.add( IdentifierReader.readIdentifier(pack) );
    }

    public Identifier qualifyType(ObjectsTable objectsTable, String type) {
        if(type == null || type.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        //The object is fully qualified.
        //if( checkFullyQualified(type) ) {
        try {
            return IdentifierReader.readPackage(type);
        } catch (IllegalArgumentException iae) {
            // continue.
        }
        //}

        // The object is not qualified:

        // finding into fully qualified objects among imports.
        if( fullyQualifiedObjects != null ) {
            Identifier fqo;
            for(Iterator<Identifier> fqos = fullyQualifiedObjects.iterator(); fqos.hasNext(); ) {
                fqo = fqos.next();
                if( fqo.getIdentifier().indexOf(type) == fqo.getIdentifier().length() - type.length() ) { // Postfix found.
                    return fqo;
                }
            }
        }

        // finding in the same package.
        if ( objectsTable.checkObject(contextPackage, type) ) {
            return IdentifierReader.readPackage( contextPackage + JavaCodeHandler.PACKAGE_SEPARATOR + type);
        }

        // finding into the Objects table using starred objects.
        if(starredPackages != null) {
            Identifier spk;
            for(Iterator<Identifier> spks = starredPackages.iterator(); spks.hasNext(); ) {
                spk = spks.next();
                if( objectsTable.checkObject(spk, type) ) {
                    return IdentifierReader.readPackage( spk + JavaCodeHandler.PACKAGE_SEPARATOR + type );
                }
            }
        }

        return null;
    }

    /*
    boolean checkFullyQualified(String type) {
        if( type.trim().length() == 0 ) {
            throw new IllegalArgumentException();
        }
        int packSeparator = type.indexOf(JavaCodeHandler.PACKAGE_SEPARATOR);
        if(packSeparator == -1 ) {
            return false;
        }
        if(packSeparator == 0 || packSeparator == type.length() - 1) {
            throw new IllegalArgumentException();
        }
        return true;
    }
    */

}
