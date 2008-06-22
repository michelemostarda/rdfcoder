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

import com.asemantics.model.JavaOntology;
import com.asemantics.profile.Profile;
import com.asemantics.sourceparse.JStatistics;

import java.io.PrintStream;


public class JavaProfile implements Profile {

    private Model model;

    protected JavaProfile( Model m ) {
        model = m;
    }

    /**
     * Related java ontology.
     */
    private JavaOntology javaOntology;

    protected JavaProfile() {
        javaOntology = new JavaOntology();
    }

    /**
     * Prints the ontology OWL.
     * 
     * @param ps
     */
    public void printOntologyOWL(PrintStream ps) {
        javaOntology.toOWL(ps);
    }

    public JStatistics loadSources(String libName, String srcPath) {
        //TODO: TBI
        return null;
    }

    public JStatistics loadClasses(String libName, String clsPath) {
        //TODO: TBI
        return null;
    }

    public JStatistics loadJar(String libName, String pathToJar) {
        //TODO: TBI
        return null;
    }



}
