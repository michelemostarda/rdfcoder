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


package com.asemantics.rdfcoder;

import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.QueryResult;
import com.asemantics.rdfcoder.model.java.JAttribute;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.profile.ProfileException;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test case for the {@link com.asemantics.rdfcoder.RDFCoder} class.
 */
public class RDFCoderTest {

    private static final Logger logger = Logger.getLogger(RDFCoderTest.class);

    private static final String JAVA_PROFILE = "java";

    private static final String TEST_MODEL_NAME= "test_model_name";

    @Test
    public void testFacadeAPI() throws QueryModelException, IOException, ProfileException {

        // Creates an RDFCoder instance on a repository.
        RDFCoder coder = new RDFCoder("target_test/hla_repo");

        // Enables debug controls.
        coder.setDebug(true);

        Assert.assertTrue("Cannot set debug flag", coder.isDebug() );

        // Registers the Java profile.
        coder.registerProfile(JAVA_PROFILE, "com.asemantics.rdfcoder.JavaProfile"); // Loaded by default.

        String[] profileNames = coder.getProfileNames();
        Assert.assertEquals("Invalid number of profiles.", 1, profileNames.length );
        Assert.assertEquals("Invalid profile name", JAVA_PROFILE, profileNames[0]);

        // Creates a model, i.e. a set of libraries.
        Model model = coder.createModel(TEST_MODEL_NAME);
        Assert.assertNotNull("Cannot create model" + TEST_MODEL_NAME, model);
        Assert.assertEquals("Invalid model name", TEST_MODEL_NAME, model.getName() );

        // Enables model validation over profile ontologies.
        model.setValidating(true);

        Assert.assertTrue("Cannot set validating model flag", model.isValidating() );

        // Retrieves the Java profile model.
        JavaProfile jprofile = (JavaProfile) model.getProfile(JAVA_PROFILE);
        Assert.assertNotNull("Cannot instantiate "+  JAVA_PROFILE, jprofile);

         // Prints the JProfile ontology.
        try {
            // TODO: to be reactivated.  jprofile.printOntologyOWL(System.out);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Cannot print Java Ontology");
        }

        // Initializes the JRE model if not yet done.
        JREReport report = jprofile.initOrLoadJRE();
        if(report != null) {
            logger.info("JRE analysis report: " + report);
        } else {
            logger.info("Init skipped");
        }
        
        // Processes the RDFCoder src and classes as test.
        try {
            final int RC_MIN_EXPECTED_CLASSES      = 100;
            final int RC_MIN_EXPECTED_INTERFACES   = 15;
            final int RC_MIN_EXPECTED_ENUMERATIONS = 5;
            JStatistics s1 = jprofile.loadSources("src_lib"  , "src");
            logger.info("src_lib statistics " + s1);
            Assert.assertTrue("Unexpected number of files.", s1.getParsedFiles() > 90);
            Assert.assertTrue(
                    "Unexpected number of classes."     , s1.getParsedClasses()      > RC_MIN_EXPECTED_CLASSES
            );
            Assert.assertTrue(
                    "Unexpected number of interfaces."  , s1.getParsedInterfaces()   > RC_MIN_EXPECTED_INTERFACES
            );
            Assert.assertTrue(
                    "Unexpected number of enumerations.", s1.getParsedEnumerations() > RC_MIN_EXPECTED_ENUMERATIONS
            );

            JStatistics s2 = jprofile.loadClasses("class_lib", "classes");
            logger.info("class_lib statistics " + s2);
            Assert.assertTrue("Unexpected number of files.", s2.getParsedFiles() > 180);
            Assert.assertTrue(
                    "Unexpected number of classes."     , s2.getParsedClasses()      > RC_MIN_EXPECTED_CLASSES
            );
            Assert.assertTrue(
                    "Unexpected number of interfaces."  , s2.getParsedInterfaces()   > RC_MIN_EXPECTED_INTERFACES
            );
            Assert.assertTrue(
                    "Unexpected number of enumerations.", s2.getParsedEnumerations() > RC_MIN_EXPECTED_ENUMERATIONS
            );

            JStatistics s3 = jprofile.loadJar("jar_lib"  , "target_test/target.jar");
            logger.info("jar_lib statistics " + s3);
            Assert.assertTrue("Unexpected number of files."     , s3.getParsedFiles()      > 170);
            Assert.assertTrue("Unexpected number of classes."   , s3.getParsedFiles()      > 150);
            Assert.assertTrue("Unexpected number of interfaces.", s3.getParsedInterfaces() > 20 );

        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Cannot process Java libraries");
        }

        // Querying java model.
        JavaQueryModel jquery = jprofile.getQueryModel();
        Assert.assertNotNull("Cannot create " + JavaQueryModel.class, jquery);

        // Retries the attributes of java.lang.String
        JAttribute[] attributes = jquery.getAttributesInto( IdentifierReader.readFullyQualifiedClass("java.lang.String") );
        Assert.assertNotNull(attributes);

        // Low level cross querying.
        if( model.supportsSparqlQuery() ) {
            try {
                final String query = "select ?a ?b ?c where{?a ?b ?c} limit 100";
                QueryResult result = model.sparqlQuery(query);
                int resultSize = 0;
                while (result.hasNext()) {
                    result.next();
                    resultSize++;
                }
                Assert.assertEquals( "Unexpected result size.", 100, resultSize );
                logger.info( String.format("Result of query [%s]:\n%s", query, result) );
            } catch (Exception e) {
                Assert.fail("Cannot perform SPARQL query.");
            }
        }

        // Saves model data.
        try {
            model.save();
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Cannot save model");
        }
        Assert.assertTrue("Cannot save model", coder.getRepository().containsResource( model.getModelResourceName() ) );

        // Resets the model content.
        model.clear();

        // Loads existing model data into the current model.
        try {
            model.load( TEST_MODEL_NAME );
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Cannot load model");
        }

    }

}
