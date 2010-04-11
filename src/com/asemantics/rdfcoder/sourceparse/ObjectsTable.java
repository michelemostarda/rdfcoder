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

import com.asemantics.rdfcoder.CoderUtils;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierBuilder;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class defines a table to resolve objects.
 *
 * //TODO: LOW - implement serialization / deserialization of UnresolvedFileEntry(es). 
 */
public class ObjectsTable implements Serializable {

    private static final Logger logger = Logger.getLogger(ObjectsTable.class);

    private final int BLOCK_SIZE = 100;

    /**
     * Contains in a memory optimized way the
     * list of type entries inside a package.
     */
    protected class PackageEntry implements Serializable {

        String[] objects = new String[BLOCK_SIZE];

        int index = 0;

        void add(String objectName) {
            if( contains(objectName) ) {
                return;
            }

            if( objects.length == index ) {
                String[] newObjects = new String[objects.length + BLOCK_SIZE];
                System.arraycopy(objects, 0, newObjects, 0, objects.length);
                objects = newObjects;
            }
            objects[index++] = objectName;
        }

        /**
         * Returns <code>true</code> if the package contains the object,
         * <code>false</code> otherwise.
         * @param objectName
         * @return
         */
        boolean contains(String objectName) {
            for(int c = 0; c < index; c++) {
                if(objects[c].equals(objectName)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Contains the name of the unresolved type
     * and the relative import context.
     */
    protected class UnresolvedTypeEntry implements Serializable {

        /**
         * The name of the unresolved type.
         */
        String typeName;

        /**
         * The usage done for the unresolved entry.
         */
        JavaCodeModel.JType type;

        /**
         * The context of the imports in which the
         * type is defined.
         */
        ImportsContext importsContext;

        /**
         * The temporary identifier currently representing the type.
         */
        UnresolvedTypeEntry(String tn, JavaCodeModel.JType t, ImportsContext ic) {
            typeName            = tn;
            type                = t;
            importsContext      = ic;
        }
    }

    /**
     * This map contains as keys the package prefixes and as
     * values package entries.
     */
    private Map<Identifier, PackageEntry> packagesToContents;

    /**
     * This map contains as keys the unresolved type names
     * and as values unresolved type entries.
     */
    private List<UnresolvedTypeEntry> unresolvedTypeEntries;

    public ObjectsTable() {
        packagesToContents = new HashMap<Identifier, PackageEntry>();
        unresolvedTypeEntries = new ArrayList<UnresolvedTypeEntry>();
    }

    /**
     * Adds an object to the objects table.
     * 
     * @param object the object to be added.
     */
    public void addObject(Identifier object) {
        if(object == null) {
            throw new IllegalArgumentException();
        }

        Identifier pack = object.getParent(); // TODO: extract package.
        PackageEntry pe = packagesToContents.get(pack);
        if(pe == null) {
            pe = new PackageEntry();
            packagesToContents.put(pack, pe);
        }
        pe.add( object.getTailFragment().getFragment() ); // TODO: extract object name.
    }

    /**
     * Checks if an object name is defined inside a package.
     *
     * @param objectPackage the package containing the object.
     * @param type the object type.
     * @return <code>true</code> if found.
     */
    public boolean checkObject(Identifier objectPackage, String type) {
        PackageEntry pe = packagesToContents.get(objectPackage);
        if(pe != null) {
            return pe.contains(type);
        }
        return false;
    }

    /**
     * Registes a type as unresolved.
     *
     * @param type the unresolved type name.
     * @param ic context of imports.
     */
    public void addUnresolvedType(String tn, JavaCodeModel.JType type, ImportsContext ic) {
        if(tn == null || tn.trim().length() == 0 || type == null || ic == null) {
            throw new IllegalArgumentException();
        }

        unresolvedTypeEntries.add( new UnresolvedTypeEntry(tn, type, ic) );
    }

    /**
     * @return the list of unresolved types.
     */
    public UnresolvedTypeEntry[] getUnresolvedTypeEntries() {
        return unresolvedTypeEntries.toArray( new UnresolvedTypeEntry[unresolvedTypeEntries.size()] );
    }

    /**
     * @return the number of unresolved types.
     */
    public int unresolvedCount() {
        return unresolvedTypeEntries.size();
    }

    /**
     * Processes all the unresolved identifiers.
     *
     * @param ch the code handler user to process the identifiers.
     */
    public Set<String> processTemporaryIdentifiers(JavaCodeHandler ch) {
        Iterator<UnresolvedTypeEntry> uei = unresolvedTypeEntries.iterator();
        UnresolvedTypeEntry ue;
        Identifier qualifiedType;
        Set<String> definitivelyUnresolved = new HashSet<String>();
        while(uei.hasNext()) {
            ue = uei.next();
            qualifiedType = ue.importsContext.qualifyType(this, ue.typeName);
            Identifier prefixedIdentifier = ue.type.getIdentifier();
            if(qualifiedType != null) {
                ch.replaceIdentifierWithQualifiedType(prefixedIdentifier, qualifiedType);
                uei.remove();
            } else {
                ch.replaceIdentifierWithQualifiedType(
                        prefixedIdentifier,
                        IdentifierBuilder.create().pushFragment(ue.typeName, JavaCodeModel.UNQUALIFIED_PREFIX).build()
                );
                definitivelyUnresolved.add( ue.typeName );
            }
        }
        return definitivelyUnresolved;
    }

    /**
     * Clears the content of the object table.
     */
    public void clear() {
        packagesToContents.clear();
        unresolvedTypeEntries.clear();
    }

    /**
     * Preloads the Objects table with the object symbols found into the sourceDir directory.
     *
     * @param sourceDir source directory.
     */
    public void preloadSourceDir(File sourceDir) {
        if(sourceDir == null || ! sourceDir.exists() ) {
            throw new IllegalArgumentException();
        }

        Stack<File> stack = new Stack<File>();
        stack.push(sourceDir);
        recursivePreload(
                new CoderUtils.JavaSourceFilenameFilter(),
                sourceDir.getAbsolutePath().length() + 1,
                CoderUtils.JAVA_SOURCE_FILE_EXT.length(),
                stack
        );
    }

    /**
     * Preloads the Objects Table with the object symbols found into the class directory.
     *
     * @param classDir
     */
    public void preloadClassDir(File classDir) {
        if(classDir == null || ! classDir.exists() ) {
            throw new IllegalArgumentException();
        }

        Stack<File> stack = new Stack<File>();
        stack.push(classDir);
        recursivePreload(
                new CoderUtils.JavaClassFilenameFilter(),
                classDir.getAbsolutePath().length() + 1,
                CoderUtils.JAVA_SOURCE_CLASS_EXT.length(),
                stack
        );
    }

    /**
     * Preloads he Object Table with the objects defined in the given jar.
     *
     * @param jar
     * @throws IOException
     */
    public void preloadJar(File jar) throws IOException {
        if(jar == null || ! jar.exists()) {
            throw new IllegalArgumentException();
        }

        JarFile jarFile = new JarFile(jar);
        try {
            Enumeration<JarEntry> entries = jarFile.entries();
            JarEntry entry;
            while(entries.hasMoreElements()) {
                entry = entries.nextElement();
                if(entry.isDirectory()) { continue; }  // is not a file.
                String fullyQualifiedObject = entry.getName();
                if( fullyQualifiedObject.indexOf(CoderUtils.JAVA_SOURCE_CLASS_EXT) == -1 ) { continue; } // is not a .class
                fullyQualifiedObject = fullyQualifiedObject.substring(0, fullyQualifiedObject.length() - CoderUtils.JAVA_SOURCE_CLASS_EXT.length());
                fullyQualifiedObject = fullyQualifiedObject.replaceAll("/", JavaCodeHandler.PACKAGE_SEPARATOR);
                if(logger.isDebugEnabled()) {
                    logger.debug("Preloading object: " + fullyQualifiedObject);
                }
                addObject(IdentifierReader.readPackage(fullyQualifiedObject) );
            }
        } finally {
            jarFile.close();
        }
    }

    public void preloadClasspath(ClassPath classPath) throws IOException {
        if(classPath == null) {
            throw new IllegalArgumentException();
        }

        for(File sourceDir : classPath.sourceDirs) {
            preloadSourceDir(sourceDir);
        }
        for(File classDir : classPath.classDirs) {
            preloadClassDir(classDir);
        }
        for(File jarFile : classPath.jarFiles) {
            preloadJar(jarFile);
        }
    }

    /**
     * Loads the content of an object table into another.
     *
     * @param other
     */
    public void load(ObjectsTable other) {
        if( other == null ) {
            throw new NullPointerException();
        }

        packagesToContents.putAll   ( other.packagesToContents    );
        unresolvedTypeEntries.addAll( other.unresolvedTypeEntries );
    }

    private void recursivePreload(final FilenameFilter ff, final int rootLength, final int extSize, Stack<File> stack) {
        if(stack.isEmpty()) { return; }

        File current = stack.pop();

        File[] all = current.listFiles();
        for(File f : all) {
            if( f.isDirectory() &&  ! f.isHidden() ) {
                stack.push(f);
            }
        }

        File[] list = current.listFiles(ff);
        for(File f : list) {
            String fullyQualifiedObject = f.getAbsolutePath();
            fullyQualifiedObject = fullyQualifiedObject.substring(rootLength, fullyQualifiedObject.length() - extSize);
            fullyQualifiedObject = fullyQualifiedObject
                                        .replaceAll("/"   , JavaCodeHandler.PACKAGE_SEPARATOR)
                                        .replaceAll("\\\\", JavaCodeHandler.PACKAGE_SEPARATOR)
                                        .replaceAll("\\$" , JavaCodeHandler.PACKAGE_SEPARATOR); // Inner classes replacing.
            if(logger.isDebugEnabled()) {
                logger.debug("Preloading object: " + fullyQualifiedObject);
            }
            addObject( IdentifierReader.readPackage(fullyQualifiedObject) );
        }

        recursivePreload(ff, rootLength, extSize, stack);
    }
}
