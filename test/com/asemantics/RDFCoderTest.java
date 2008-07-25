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

import com.asemantics.model.*;
import com.asemantics.sourceparse.JStatistics;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;


public class
        RDFCoderTest extends TestCase {

    private static final String JAVA_PROFILE = "java";

    private static final String TEST_MODEL_NAME= "test_model_name";

    public void testHighLevelAPI() throws QueryModelException, IOException {

        // Creates an RDFCoder instance on a repository.
        RDFCoder coder = new RDFCoder("target_test/hla_repo");

        // Enables debug controls.
        coder.setDebug(true);

        assertTrue("Cannot set debug flag", coder.isDebug() );

        // Registers the Java profile.
        coder.registerProfile(JAVA_PROFILE, "com.asemantics.JavaProfile"); // Loaded by default.

        String[] profileNames = coder.getProfileNames();
        assertEquals("Invalid number of profiles.", 1, profileNames.length );
        assertEquals("Invalid profile name", JAVA_PROFILE, profileNames[0]);

        // Creates a model, i.e. a set of libraries.
        Model model = coder.createModel(TEST_MODEL_NAME);
        assertNotNull("Cannot create model" + TEST_MODEL_NAME, model);
        assertEquals("Invalid model name", TEST_MODEL_NAME, model.getName() );

        // Enables model validation over profile ontologies.
        model.setValidating(true);

        assertTrue("Cannot set validating model flag", model.isValidating() );

        // Retrieves a Java profile model.
        JavaProfile jprofile = (JavaProfile) model.getProfile(JAVA_PROFILE);
        assertNotNull("Cannot instantiate "+  JAVA_PROFILE, jprofile);

        // Initializes the JRE model if not yet done.
        final File JRE = new File( "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" );
        if ( ! jprofile.checkJREInit(JRE)) {
            try {
                JREReport jreReport = jprofile.initJRE(JRE);
                System.out.println(jreReport);
            } catch (Throwable t) {
                t.printStackTrace();
                fail("Cannot intitialise JRE");
            }
        } else {
            try {
            jprofile.loadJRE(JRE);
            } catch (Throwable t) {
                t.printStackTrace();
                fail("Cannot load JRE");
            }
        }
        

        // Retrieves the jprofile ontology.
        try {
            //TODO: activate this
            // jprofile.printOntologyOWL(System.out);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Cannot print Ontology OWL");
        }

        // Processes Java libraries.
        try {
            JStatistics s1 = jprofile.loadSources("src_lib"  , "src");
            System.out.println(s1);

            JStatistics s2 = jprofile.loadClasses("class_lib", "classes");
            System.out.println(s2);

            JStatistics s3 = jprofile.loadJar    ("jar_lib"  , "target_test/target.jar");
            System.out.println(s3);

        } catch (Throwable t) {
            t.printStackTrace();
            fail("Cannot process Java libraries");
        }

        // Querying java model.
        JavaQueryModel jquery = jprofile.getQueryModel();
        assertNotNull("Cannot create " + JavaQueryModel.class, jquery);

        // Retries the attributes of java.lang.String
        JAttribute[] attributes = jquery.getAttributesInto("java.lang.String");
        assertNotNull(attributes);

        // Low level cross querying.
        if( model.supportsSparqlQuery() ) {
            QueryResult result = model.sparqlQuery("select ?a ?b ?c where{?a ?b ?c}");
            System.out.println(result);
        }

        // Saves model data.
        try {
            model.save();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Cannot save model");
        }
        assertTrue("Cannot save model", coder.getRepository().containsResource( model.getModelResourceName() ) );

        // Resets the model content.
        model.clear();

        // Loads existing model data into the current model.
        try {
            model.load( TEST_MODEL_NAME );
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Cannot load model");
        }

    }

}
