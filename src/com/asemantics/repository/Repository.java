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

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a file repository where put code storages.
 */
public class Repository {

    /**
     * Kinds of resources.
     */
    public enum ResourceType {
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
    public class Resource {

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
         * Number of readers of this resource.
         */
        private short readersCount = 0;

        /**
         * Number of writers of this resource.
         */
        private int writersCount = 0;



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
        public InputStream getInputStream() throws FileNotFoundException, RepositoryException {
            if(readersCount > 0) {
                throw new RepositoryException("Cannot open resource in write mode: there is at least one reader on it");
            }
            if(writersCount > 0) {
                throw new RepositoryException("Cannot open resource in write mode: there is aready a writer on it");
            }

            // Check there is a single writer.
            if(writersCount > 0) {
                throw new RepositoryException("Resource '" + name + "' already accessed in write mode.");
            }
            // Creates a lock file for write mode.
            writersCount++;
            try {
                createLockFile(name);
            } catch (IOException e) {
                throw new RepositoryException(e);
            }

            return new FileInputStream( getLocation() ) {

                /**
                 * Releases the lock file.
                 *
                 * @throws IOException
                 */
                public void close() throws IOException {
                    super.close();
                    writersCount--;
                    removeLockFile(name);
                }

            };
        }

        /**
         * Opens an output stream on the resource.
         *
         * @return
         * @throws FileNotFoundException
         */
        public OutputStream getOutputStream() throws FileNotFoundException, RepositoryException {
            if(writersCount > 0) {
                throw new RepositoryException("Cannot open resource in read mode: there is a writer on it");
            }

            readersCount++;
            return new FileOutputStream( getLocation() ) {

                public void close() throws IOException {
                    super.close();
                    readersCount--;
                }
                
            };
        }
    }

    /**
     * Filters all lock files inside a dir.
     */
    public static class LockFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() ) { return false; }
            String postfix = LOCK_FILE_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
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
     * Extension of lock files.
     */
    private static final String LOCK_FILE_EXT = ".lck";

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
    protected Map<String,Resource> loadResources(File target) {
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
            // Performs a cleanup of repository.
            removeLockedResources();
            // Loads existing resources.
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
        try {
            removeLockFile(resourceName);
        } catch(Exception e) {
            e.printStackTrace();
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

        File file    = new File(repositoryLocation, type.getFileName(resourceName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RepositoryException("Cannot create file: '" + file.getAbsolutePath() + "'");
        }
        try {
            createLockFile(resourceName);
        } catch (IOException e) {
            try {
                file.delete();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            throw new RepositoryException("Cannot create lock file for resource: '" + resourceName + "'");
        }

        Resource resource = new Resource(repositoryLocation, resourceName, type);
        resources.put(resourceName, resource);

        return resource;
    }

    /**
     * Returns <code>true</code> if a resource is locked in writing mode,
     * <code>false</code> otherwise.
     *
     * @param resourceName
     * @return
     * @throws RepositoryException is resourceName doesn't exist.
     */
    public boolean isLocked(String resourceName) throws RepositoryException {
        if( resources != null && ! containsResource(resourceName) ) {
            throw new RepositoryException("Repository doesn't contain resource name '" + resourceName + "'");
        }

        File resourceFileLock = new File(repositoryLocation, getFileLockName(resourceName));
        return resourceFileLock.exists();
    }

    /**
     * Returns the name of the open file lock of a resource.
     *
     * @param resourceName
     * @return
     */
    private static String getFileLockName(String resourceName) {
        return "." + resourceName + LOCK_FILE_EXT;
    }

    /**
     * Returns the resource name associated to a given lock file name.
     *
     * @param lockFileName
     * @return
     */
    private static String getResourceNameFromLockFile(String lockFileName) {
        return lockFileName
                .substring(1) // "."
                .substring(0, lockFileName.length() - 1 - LOCK_FILE_EXT.length() );
    }

    /**
     * Creates a lock file for a resource name.
     *
     * @param resourceName
     */
    private void createLockFile(String resourceName) throws IOException {
        File lockFile = new File(repositoryLocation, getFileLockName(resourceName));
        lockFile.createNewFile();
    }

    /**
     * Removes a lock file.
     * 
     * @param resourceName
     */
    private void removeLockFile(String resourceName) {
        File lockFile = new File(repositoryLocation, getFileLockName(resourceName) );
        lockFile.delete();
    }

    /**
     * Removes all files that are locked and then inconsistent.
     */
    private void removeLockedResources() throws RepositoryException {
        Map<String,Resource> resources = loadResources(repositoryLocation);
        for(Resource r : resources.values()) {
            if( isLocked( r.getName() ) ) {
                try {
                    removeLockFile( r.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File resourceLocation = r.getLocation();
                try {
                    resourceLocation.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        File[] lockFiles = repositoryLocation.listFiles( new LockFileFilter() );
//        File resourceFile;
//        for( File lf : lockFiles ) {            ;
//            try {
//                resourceFile.delete();
//            }catch(Throwable t) {
//                t.printStackTrace();
//            }try {
//                lf.delete();
//            }catch(Throwable t) {
//                t.printStackTrace();
//            }
//        }

    }

}
