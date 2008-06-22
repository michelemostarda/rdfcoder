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

import com.asemantics.model.JAttribute;
import com.asemantics.model.JavaQueryModel;
import com.asemantics.model.QueryModelException;
import com.asemantics.sourceparse.JStatistics;
import junit.framework.TestCase;


public class RDFCoderTest extends TestCase {

    public void testHighLevelAPI() throws QueryModelException {

        // Creates an RDFCoder instance on a repository.
        RDFCoder coder = new RDFCoder("target_test/hla_repo");

        // Validates generated models over owning ontologies.
        coder.setValidatingModel(true);

        // Enables debug controls.
        coder.setDebug(true);

        // Register coder profiles.
        coder.registerProfile("java", "com.asemantics.JavaProfile"); // Loaded by default.

        // Creates an model, i.e. a set of libraries.
        Model model = coder.createModel("test_model_name");

        // Retrieves a Java profile model.
        JavaProfile jprofile = (JavaProfile) model.getProfile("java");

        // Initializes the JRE model if not yet done.
        //TODO: implement this section.
        /*
        final String JRE = "/path/to/jre_x.y.z";
        if ( ! jprofile.checkJREinit(JRE)) {
            JREReport jreReport = jprofile.initJRE(JRE);
        }
        */

        // Retrieves the jprofile ontology.
        jprofile.printOntologyOWL(System.out);

        // Processes java libraries.
        JStatistics s1 = jprofile.loadSources("src_lib"  , "src");
        JStatistics s2 = jprofile.loadClasses("class_lib", "classes");
        JStatistics s3 = jprofile.loadJar    ("jar_lib"  , "target.jar");

        // Querying java model.
        JavaQueryModel jquery = model.getQueryModel("java");
        JAttribute[] attributes = jquery.getAttributesInto("pathToClass");

    }

}
