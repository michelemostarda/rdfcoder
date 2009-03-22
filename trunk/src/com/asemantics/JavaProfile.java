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
import com.asemantics.repository.RepositoryException;
import com.asemantics.sourceparse.*;
import com.asemantics.storage.CodeStorage;

import java.io.*;
import java.util.*;


public class JavaProfile implements Profile {

    /**
     * The prefix of the resource name associated to the JRE CodeModel.
     */
    private static final String JRE_MODEL_PREFIX = "jre_model_";

    /**
     * The prefix of the resource name associated to the JRE Object Table.
     */
    private static final String JRE_OBJECT_TABLE_PREFIX = "jre_ot_";

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
     * Returns the name of the JRE Model resource.
     *
     * @param jreDirName
     * @return
     */
    private String getJREModelResourceName(String jreDirName) {
        return JRE_MODEL_PREFIX + jreDirName;
    }

    /**
     * Returns the name of the JRE Object Table resource.
     *
     * @param jreDirName
     * @return
     */
    private String getJREObjectTableResourceName(String jreDirName) {
        return JRE_OBJECT_TABLE_PREFIX + jreDirName;
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

        String jreResourceName = getJREModelResourceName( pathToJRE.getName() );
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
        final String jreName = pathToJRE.getName();
        JavaBytecodeJarParser parser = new JavaBytecodeJarParser();
        CodeHandler statCH = statistics.createStatisticsCodeHandler(codeHandler);
        parser.initialize(statCH, objectsTable);
        codeHandler.startParsing( jreName, pathToJRE.getAbsolutePath() );

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

        // Stores the objects table content.
        try {
            serializeJREObjectsTable(jreName, objectsTable);
        } catch (Exception e) {
            throw new RDFCoderException("Cannot serialize Object Table in repository.", e);   
        }

        objectsTable = null;
        parser.dispose();
        parser = null;

        // Saves model in repository.
        OutputStream os = null;
        Repository.Resource resource = null;
        try {
            resource = repository.createResource( getJREModelResourceName(pathToJRE.getName()), Repository.ResourceType.XML );
            os  = resource.getOutputStream();
            codeStorage.saveModel(cmb, os);
            os.close();
        } catch (Exception e) {
            resource.delete();
            throw new RDFCoderException("Cannot store model in repository.", e);
        }

        // Disposes model.
        cmb.clearAll();
        cmb = null;

        return new JREReport(pathToJRE ,statistics);
    }

    /**
     * Loads an existing JRE data.
     *
     * @param pathToJRE
     */
    public void loadJRE(File pathToJRE) {
        try {
            model.getObjectsTable().load( deserializeJREObjectsTable( pathToJRE.getName() ) );
        } catch (Exception e) {
            throw new RDFCoderException("Cannot deserialize Object Table from repository.", e);
        }
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

    /**
     * Serializes the content of the JRE {@link com.asemantics.sourceparse.ObjectsTable}
     * into the repository.
     *
     * @param objectsTable
     */
    private void serializeJREObjectsTable(String jreName, ObjectsTable objectsTable) throws RepositoryException, IOException {
        Repository.Resource resource = repository.createResource( getJREObjectTableResourceName( jreName ), Repository.ResourceType.BINARY );
        OutputStream os = resource.getOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject( objectsTable );
        } finally {
            if(oos != null) { oos.close(); }
        }
    }

    /**
     * Deserializes the content of the JRE {@link com.asemantics.sourceparse.ObjectsTable}
     * from the repository.
     * 
     * @param jreName
     * @return
     * @throws RepositoryException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private ObjectsTable deserializeJREObjectsTable(String jreName) throws RepositoryException, IOException, ClassNotFoundException {
        Repository.Resource resource = repository.getResource( getJREObjectTableResourceName( jreName ) );
        InputStream is = resource.getInputStream();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            ObjectsTable ot = (ObjectsTable) ois.readObject();
            return ot;
        } finally {
            if(ois != null) { ois.close(); }
        }
    }

    private JavaQueryModel jqmInstance;

    public JavaQueryModel getQueryModel() {
        if( jqmInstance == null ) {
            jqmInstance = new JavaQueryModelImpl( model.getCodeModelBase() );
        }
        return jqmInstance;
    }
}