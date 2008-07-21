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


package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;
import com.asemantics.model.CodeHandler;
import com.asemantics.model.JavaCodeModel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class defines a table to resolve objects.
 *
 * //TODO: LOW - implement serialization / deserialization of UnresolvedFileEntry(es). 
 */
public class ObjectsTable {

    private final int BLOCK_SIZE = 100;

    /**
     * Contains in a memory optimized way the
     * list of type entries inside a package.
     */
    protected class PackageEntry {

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
    public class UnresolvedTypeEntry {

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
    private Map<String, PackageEntry> packagesToContents;

    /**
     * This map contains as keys the unresolved type names
     * and as values unresolved type entries.
     */
    private List<UnresolvedTypeEntry> unresolvedTypeEntries;

    public ObjectsTable() {
        packagesToContents = new HashMap<String, PackageEntry>();
        unresolvedTypeEntries = new ArrayList<UnresolvedTypeEntry>();
    }

    /**
     * Adds an object to the objects table.
     * @param objectPackage the package containing the object.
     * @param objectName the object name without qualification.
     */
    public void addObject(String objectPackage, String objectName) {
        if(
            objectPackage == null
                ||
            objectName    == null || objectName.trim().length()    == 0
        ) {
            throw new IllegalArgumentException();
        }
        if( objectName.indexOf(CodeHandler.PACKAGE_SEPARATOR) != -1 ) {
            throw new IllegalArgumentException("The object name must be NOT qualified.");
        }

        //System.out.println("objectPackage: " + objectPackage);
        PackageEntry pe = packagesToContents.get(objectPackage);
        if(pe == null) {
            pe = new PackageEntry();
            packagesToContents.put(objectPackage, pe);
        }
        pe.add(objectName);
    }

    /**
     * Adds a fully qualified object to the objects table.
     * @param fullyQualifiedName the fully qualified name.
     */
    public void addObject(String fullyQualifiedName) {
        if(fullyQualifiedName == null) {
            throw new IllegalArgumentException("fullyQualifidName cannot be null.");
        }

        int i = fullyQualifiedName.lastIndexOf(CodeHandler.PACKAGE_SEPARATOR);
        if(i == -1) {
            addObject("", fullyQualifiedName); // Default package.
        } else {
            addObject( fullyQualifiedName.substring(0, i), fullyQualifiedName.substring(i + 1 ) );
        }
    }

    /**
     * Checks if an object name is defined inside a package.
     * @param objectPackage
     * @param type
     * @return
     */
    public boolean checkObject(String objectPackage, String type) {
        PackageEntry pe = packagesToContents.get(objectPackage);
        if(pe != null) {
            return pe.contains(type);
        }
        return false;
    }

    /**
     * Registes a type as unresolved.
     * @param type
     * @param ic
     */
    public void addUnresolvedType(String tn, JavaCodeModel.JType type, ImportsContext ic) {
        if(tn == null || tn.trim().length() == 0 || type == null || ic == null) {
            throw new IllegalArgumentException();
        }

        unresolvedTypeEntries.add( new UnresolvedTypeEntry(tn, type, ic) );
    }

    public UnresolvedTypeEntry[] getUnresolvedTypeEntries() {
        return unresolvedTypeEntries.toArray( new UnresolvedTypeEntry[unresolvedTypeEntries.size()] );
    }

    public int unresolvedCount() {
        return unresolvedTypeEntries.size();
    }

    /**
     * Processes all the unresolved identifiers.
     * @param ch
     */
    public Set<String> processTemporaryIdentifiers(CodeHandler ch) {
        Iterator<UnresolvedTypeEntry> uei = unresolvedTypeEntries.iterator();
        UnresolvedTypeEntry ue;
        String qualifiedType;
        Set<String> definitivelyUnresolved = new HashSet();
        while(uei.hasNext()) {
            ue = uei.next();
            qualifiedType = ue.importsContext.qualifyType(this, ue.typeName);
            String prefixedIdentifier = ue.type.getIdentifier();
            if(qualifiedType != null) {
                ch.replaceIdentifierWithQualifiedType(prefixedIdentifier, qualifiedType);
                uei.remove();
            } else {
                ch.replaceIdentifierWithQualifiedType(
                        prefixedIdentifier,
                        JavaCodeModel.UNQUALIFIED_PREFIX + ue.typeName
                );
                definitivelyUnresolved.add( ue.typeName );
            }
        }
        return definitivelyUnresolved;
    }

    public void clear() {
        packagesToContents.clear();
        unresolvedTypeEntries.clear();
    }

    /**
     * Preloads the Objects table with the object symbols found into the sourceDir directory.
     *
     * @param sourceDir
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
                fullyQualifiedObject = fullyQualifiedObject.replaceAll("/", CodeHandler.PACKAGE_SEPARATOR);
                System.out.println("Add object: " + fullyQualifiedObject);
                addObject(fullyQualifiedObject);
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
                                        .replaceAll("/"   , CodeHandler.PACKAGE_SEPARATOR)
                                        .replaceAll("\\\\", CodeHandler.PACKAGE_SEPARATOR)
                                        .replaceAll("\\$" , CodeHandler.PACKAGE_SEPARATOR); // Inner classes replacing.
            System.out.println("Add object: " + fullyQualifiedObject);
            addObject(fullyQualifiedObject);
        }

        recursivePreload(ff, rootLength, extSize, stack);
    }
}
