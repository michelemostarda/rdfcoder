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
import com.asemantics.profile.Profile;
import com.asemantics.repository.Repository;
import com.asemantics.sourceparse.*;
import com.asemantics.storage.JenaCoderFactory;
import com.asemantics.storage.CodeStorage;
import com.asemantics.storage.JenaCodeModel;

import java.io.*;
import java.util.*;


public class JavaProfile implements Profile {

    /**
     * The prefix of the resource name associated to a JRE.
     */
    private static final String JRE_MODEL_PREFIX = "jre_model_";

    /**
     * JRE lib dir.
     */
    private static final String JRE_LIB_DIR = "lib";

    /**
     * JRE extensions dir.
     */
    private static final String JRE_EXT_DIR = "lib" + File.separator + "ext";

    /**
     * JRE security dir.
     */
    private static final String JRE_SEC_DIR = "lib" + File.separator + "security";

    /**
     * Related java ontology.
     */
    private final JavaOntology javaOntology;

    /**
     * Coder model.
     */
    private final Model model;

    /**
     * Coder repository.
     */
    private final Repository repository;

    /**
     * Code storage internal instance.
     */
    private final CodeStorage codeStorage;

    /**
     * Constructor.
     *
     * @param m
     * @param r
     */
    protected JavaProfile( Model m, CodeStorage cs, Repository r ) {
        javaOntology = new JavaOntology();

        model       = m;
        codeStorage = cs;
        repository  = r;
    }

    /**
     * Prints the ontology OWL.
     * 
     * @param ps
     */
    public void printOntologyOWL(PrintStream ps) {
        javaOntology.toOWL(ps);
    }

    /**
     * Returns the name of the JRE resource.
     *
     * @param jreDirName
     * @return
     */
    private String getJREResourceName(String jreDirName) {
        return JRE_MODEL_PREFIX + jreDirName;
    }

    /**
     * Checks that in the current repository a <i>Java Runtime Environment</i>
     * model already exists.
     *
     * @param pathToJRE
     * @return
     */
    public boolean checkJREInit(File pathToJRE) {
        if( pathToJRE == null ) {
            throw new RDFCoderException("pathToJRE cannot be null.");
        }
        if( ! pathToJRE.exists() ) {
            throw new RDFCoderException("specified JRE path: '" + pathToJRE.getAbsolutePath() + "' doesn't exist.");
        }

        String jreResourceName = getJREResourceName( pathToJRE.getName() );
        return repository.containsResource(jreResourceName);
    }

    public JREReport initJRE(File pathToJRE) {
        if( pathToJRE == null ) {
            throw new RDFCoderException("pathToJRE cannot be null.");
        }
        if( ! pathToJRE.exists() ) {
            throw new RDFCoderException("specified JRE path: '" + pathToJRE.getAbsolutePath() + "' doesn't exist.");
        }

        // Prepares jars list.
        File[] libJars = new File( pathToJRE, JRE_LIB_DIR).listFiles( new CoderUtils.JavaJarFilter() );
        File[] libExts = new File( pathToJRE, JRE_EXT_DIR).listFiles( new CoderUtils.JavaJarFilter() );
        File[] libSecs = new File( pathToJRE, JRE_SEC_DIR).listFiles( new CoderUtils.JavaJarFilter() );
        List<File> files = new ArrayList<File>(libJars.length + libExts.length + libSecs.length);
        files.addAll( Arrays.asList(libJars) );
        files.addAll( Arrays.asList(libExts) );
        files.addAll( Arrays.asList(libSecs) );

        // Creates structures.
        JStatistics statistics    = new JStatistics();
        ObjectsTable objectsTable = model.getObjectsTable();
        CodeModelBase cmb         = model.getCodeModelBase();
        CodeHandler codeHandler   = model.getCoderFactory().createHandlerOnModel(cmb);

        CodeStorage cs = model.getCoderFactory().createCodeStorage();
        if( ! cs.supportsFile() ) {
            throw new IllegalStateException();
        }

        // Initializes structures.
        JavaBytecodeJarParser parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        parser.initialize(statCH, objectsTable);
        codeHandler.startParsing( pathToJRE.getName(), pathToJRE.getAbsolutePath() );

        // Does jar parsing.
        for(File f : files) {
            try {
                parser.parseFile(f);
            } catch (IOException ioe) {
                if( RDFCoder.assertions() ) {
                    ioe.printStackTrace();
                } else {
                    System.err.println("ERROR: " + ioe.getMessage());
                }
            }
        }

        // Disposes parsing.
        codeHandler.endParsing();
        objectsTable.clear();
        objectsTable = null;
        parser.dispose();
        parser = null;

        // Saves model in repository.
        try {

            Repository.Resource resource = repository.createResource( getJREResourceName(pathToJRE.getName()), Repository.ResourceType.XML );
            OutputStream os  = resource.getOutputStream();
            codeStorage.saveModel(cmb, os);
            os.close();

        } catch (Exception e) {
            throw new RDFCoderException("Cannot store model in repository.", e);
        }

        // Disposes model.
        cmb.clearAll();
        cmb = null;

        return new JREReport(pathToJRE ,statistics);
    }

    public JStatistics loadSources(String libName, String srcPath) {
        return loadJava(libName, srcPath, new JavaSourceFileParser());
    }

    public JStatistics loadClasses(String libName, String clsPath) {
        return loadJava(libName, clsPath, new JavaBytecodeFileParser());
    }

    public JStatistics loadJar(String libName, String pathToJar) throws IOException {
        JStatistics statistics = new JStatistics();
        CodeHandler codeHandler = model.getCoderFactory().createHandlerOnModel( model.getCodeModelBase() );

        JavaBytecodeJarParser parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        parser.initialize( statCH, model.getObjectsTable() );

        File lib = new File(pathToJar);

        codeHandler.startParsing(libName, lib.getAbsolutePath());
        parser.parseFile( new File(pathToJar) );
        codeHandler.endParsing();

        statistics.reset();
        parser.dispose();
        parser = null;

        return statistics;
    }

    /**
     * Loads Java resources.
     * 
     * @param libName
     * @param path
     * @param fileParser
     * @return
     */
    private JStatistics loadJava(String libName, String path, FileParser fileParser) {
         CodeHandler ch = model.getCoderFactory().createHandlerOnModel( model.getCodeModelBase() );

        DirectoryParser directoryParser = new DirectoryParser( fileParser, new CoderUtils.JavaSourceFilenameFilter() );
        JStatistics statistics = new JStatistics();
        CodeHandler sch = statistics.createStatisticsCodeHandler(ch);

        directoryParser.initialize(sch, model.getObjectsTable() );
        directoryParser.parseDirectory(libName, new File(path) );

        directoryParser.dispose();
        ch = null;

        return statistics;
    }

    private JavaQueryModel jqmInstance;

    public JavaQueryModel getQueryModel() {
        if( jqmInstance == null ) {
            jqmInstance = new JavaQueryModelImpl( model.getCodeModelBase() );
        }
        return jqmInstance;
    }
}
