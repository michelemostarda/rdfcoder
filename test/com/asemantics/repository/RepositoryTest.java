/*
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

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import static com.asemantics.repository.Repository.ResourceType.BINARY;
import static com.asemantics.repository.Repository.ResourceType.XML;

public class RepositoryTest extends TestCase {

    private static final String REPOSITORY_TEST_LOCATION = "./target_test/repository";

    private static final File location = new File(REPOSITORY_TEST_LOCATION);

    private static final String RESOURCE_NAME_PREFIX = "resource_name";

    private static final int RESOURCES_COUNT = 1000;



    private Repository repository;

    protected void setUp() throws Exception {
        System.out.println("Removing location...");
        File[] content = location.listFiles();
        if(content == null) {
            return;
        }
        for(File f : content) {
            f.delete();
        }
        location.delete();
    }

    protected void tearDown() throws Exception {}

    public void testCreate() throws RepositoryException {
        repository = Repository.getRepository( new File(REPOSITORY_TEST_LOCATION) );
    }

    public void testAddResource() throws RepositoryException {
        testCreate();
        for(int i = 0; i < RESOURCES_COUNT; i++ ) {
            repository.createResource(RESOURCE_NAME_PREFIX + i, i % 2 == 0 ? BINARY : XML);
        }
    }

    public void testCheckResource() throws RepositoryException, IOException {
        testAddResource();
        String resourceName;
        Repository.Resource resource;
        for(int i = 0; i < RESOURCES_COUNT; i++) {
            resourceName = RESOURCE_NAME_PREFIX + i;
            assertTrue( repository.containsResource(resourceName) );
            resource = repository.getResource(resourceName);
            assertEquals( resource.getName(), resourceName);
            assertEquals( resource.getType(), i % 2 == 0 ? BINARY : XML);
            assertEquals(
                    resource.getLocation().getCanonicalPath(),
                    new File(location, resource.getType().getFileName(resourceName)).getCanonicalPath()
            );
        }
    }

    public void testRemoveResource() throws RepositoryException {
        testAddResource();
        String resourceName;
        for(int i = 0; i < RESOURCES_COUNT; i++) {
           resourceName = RESOURCE_NAME_PREFIX + i;
           repository.removeResource(resourceName);
        }
    }
}
