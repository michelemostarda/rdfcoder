/**
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.repository;

import com.asemantics.repository.RepositoryException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * This class represents a file repository where put code storages.
 */
public class Repository {

    /**
     * Kinds of resources.
     */
    protected enum ResourceType {
        BINARY {

            String getExtension() {
                return "bin";
            }

        },

        XML {

            String getExtension() {
                return "xml";
            }
        };

        /**
         * Returns the type of the file name on the basis of the extension,
         * <code>null</code> if no extension matches.
         *
         * @param fileName
         * @return
         */
        static ResourceType getType(String fileName) {
            for(ResourceType rt : ResourceType.values()) {
                if(fileName.indexOf(rt.getExtension()) != -1 ) {
                    return rt;
                }
            }
            return null;
        }

        /**
         * Returns the name of the resource removing the extension.
         *
         * @param file
         * @return
         */
        static String getResourceName(String file) {
            int separator = file.indexOf(".");
            return file.substring(0, separator);
        }

        /**
         * Returns the extension associated to the type.
         * @return
         */
        abstract String getExtension();

        /**
         * Returns the file name from the resource name.
         *
         * @param resourceName
         * @return
         */
        String getFileName( String resourceName ) {
            return resourceName + "."  + getExtension();
        }

    }

    /**
     * Resource element.
     */
    protected static class Resource {

        /**
         * parent directory containing this resource.
         */
        private File root;

        /**
         * The resource name.
         */
        private String name;

        /**
         * The resource type.
         */
        private ResourceType type;

        /**
         * Constructor.
         *
         * @param n
         * @param rt
         */
        Resource(File r, String n, ResourceType rt) {
            root = r;
            name = n;
            type = rt;
        }

        /**
         * Returns the resource name.
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the resource type.
         * 
         * @return
         */
        public ResourceType getType() {
            return type;
        }

        /**
         * Returns the resource location.
         *
         * @return
         */
        public File getLocation() {
            return new File(root, name + "." + type.getExtension());
        }

        /**
         * Opens an input stream on the resource.
         *
         * @return
         * @throws FileNotFoundException
         */
        public InputStream getInputStream() throws FileNotFoundException {
            return new FileInputStream( getLocation() );
        }

        /**
         * Opens an output stream on the resource.
         *
         * @return
         * @throws FileNotFoundException
         */
        public OutputStream getOutputStream() throws FileNotFoundException {
            return new FileOutputStream( getLocation() );
        }
    }

    /**
     * Default repository name.
     */
    public static final String DEFAULT_REPOSITORY_NAME = "rdfcoder";

    /**
     * Default repository location.
     */
    public static final String DEFAULT_REPOSITORY_LOCATION = "~";

    /**
     * Defualt repository instance.
     */
    private static Repository _defaultRepositoryInstance;

    /**
     * Repository root;
     */
    private final File repositoryLocation;

    /**
     * Map of resources inside the current repository.
     */
    private Map<String,Resource> resources;

    /**
     * Returns a repository at a given location.
     *
     * @param location
     * @return
     */
    public static final Repository getRepository(File location) throws RepositoryException {
        return new Repository(location);
    }

    /**
     * Returns the repository unique instance.
     *
     * @return
     */
    public static final Repository getDefaultRepository() throws RepositoryException {
        if(_defaultRepositoryInstance == null) {
            File location = new File(
                    DEFAULT_REPOSITORY_LOCATION +
                    File.pathSeparator +
                    "." + DEFAULT_REPOSITORY_NAME
            );
            _defaultRepositoryInstance = new Repository(location);
        }
        return _defaultRepositoryInstance;
    }

    /**
     * Loads the resources defined in the <i>target</i>
     * dir.
     *
     * @return
     */
    protected static Map<String,Resource> loadResources(File target) {
        if( ! target.exists() ) {
            throw new IllegalArgumentException("target dir doesn't exist: '" + target.getAbsolutePath() + "'");
        }
        if( ! target.isDirectory() ) {
            throw new IllegalArgumentException("target dir must be a directory");
        }

        File[] files = target.listFiles();
        Map<String,Resource> result = new HashMap<String,Resource>(files.length);
        ResourceType rt;
        String fileName;
        String resourceName;
        for(File file : files) {
            if( file.isFile() ) {
                fileName = file.getName();
                rt = ResourceType.getType(fileName);
                if( rt != null ) {
                    resourceName = ResourceType.getResourceName(fileName);
                    result.put( resourceName, new Resource(target ,resourceName , rt) );
                }
            }
        }
        return result;
    }

    /**
     * Checks the repository folder.
     *
     * @param location
     * @return <code>true</code> if the repo folder has been created,
     * <code>false</code> otherwise.
     */
    protected static boolean checkRepository(File location) throws RepositoryException {
        if( ! location.exists() ) {
            if( ! location.mkdir() ) {
                throw new RepositoryException(
                        "Error while creating repository dir at location '" + location.getAbsolutePath() + "'."
                );
            }
            return true;
        } else if( ! location.isDirectory() ) {
            throw new RepositoryException(
                    "A file with the same name of the repository already exists at location '"+ location.getAbsolutePath() + "'."
            );
        }
        return false;
    }

    /**
     * Singleton restriction constructor.
     */
    private Repository(File rl) throws RepositoryException {
        repositoryLocation = rl;
        if( ! checkRepository(rl) ) {
            resources = loadResources(repositoryLocation);
        } else {
            resources = new HashMap<String,Resource>();
        }
    }

    /**
     * Returns the repository root.
     * 
     * @return
     */
    public File getRepositoryLocation() {
        return repositoryLocation;
    }

    /**
     * Returns the list of available resources.
     *
     * @return
     */
    public Resource[] getResources() {
        Collection<Resource> resourceValues = resources.values();
        return resourceValues.toArray(new Resource[resourceValues.size()]);
    }

    /**
     * Returns <code>true</code> if the repository contains the given resource name,
     * <code>false</code> otherwise.
     *
     * @param resourceName
     * @return
     */
    public boolean containsResource(String resourceName) {
        return resources.containsKey(resourceName);
    }

    /**
     * Returns an existing resource.
     *
     * @param resourceName
     * @return
     */
    public Resource getResource(String resourceName) throws RepositoryException {
        Resource resource = resources.get(resourceName);
        if(resource == null) {
            throw new RepositoryException("Required resource '" + resourceName + "' doesn't exist.");
        }
        return resource;
    }

    /**
     * Removes the specified resource.
     *
     * @param resourceName
     * @throws RepositoryException
     */
    public void removeResource(String resourceName) throws RepositoryException {
        Resource resource = getResource(resourceName);
        if( ! resource.getLocation().delete() ) {
            throw new RepositoryException("Cannot delete resource: '" + resourceName + "'");
        }
        resources.remove(resourceName);
    }

    /**
     * Check if the resource name is valid.
     *
     * @param rn
     */
    static void checkResourceName(String rn) throws RepositoryException {
        if(rn == null || rn.trim().length() == 0) {
            throw new RepositoryException("Invalid resource name: '"+ rn + "'");
        }
    }

    /**
     * Creates a new resource into the repository.
     *
     * @param resourceName
     * @param type
     * @return
     * @throws RepositoryException
     */
    public Resource createResource(String resourceName, ResourceType type) throws RepositoryException {
        checkResourceName(resourceName);

        if( containsResource(resourceName) ) {
            throw new RepositoryException(
                    "Cannot create resource '" + resourceName + "', a resouce with the same name already exists."
            );
        }

        File file = new File(repositoryLocation, type.getFileName(resourceName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RepositoryException("Cannot create file: '" + file.getAbsolutePath() + "'");
        }

        Resource resource = new Resource(repositoryLocation, resourceName, type);
        resources.put(resourceName, resource);

        return resource;
    }

}
