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

import com.asemantics.rdfcoder.inspector.Inspector;
import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.model.QueryResult;
import com.asemantics.rdfcoder.model.SPARQLException;
import com.asemantics.rdfcoder.model.SPARQLQuerableCodeModel;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.storage.CodeStorage;
import com.asemantics.rdfcoder.storage.CodeStorageException;
import jline.ArgumentCompletor;
import jline.CandidateListCompletionHandler;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.History;
import jline.SimpleCompletor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The Command line <i>RDFCoder</i> utility.
 */
public abstract class AbstractCommandLine {

    /**
     * Enumeration on library types.
     */
    private enum LibraryType {
        JAR_FILE,
        SOURCE_DIR,
        JAVADOC_DIR,
        CLASS_DIR
    }

    /* Public constants. */

    /**
     * Major version of CommandLine console.
     */
    public static final int VERSION_MAJOR  = 0;

    /**
     * Minor version of CommandLine console.
     */
    public static final int VERSION_MINOR  = 5;

    /**
     * Name of the active model used to perform inspection command.
     */
    public static final String INSPECTION_MODEL_NAME = "model";

    /**
     * The command method expected prefix.
     */
    protected static final String COMMAND_PREFIX = "command_";

    /**
     * Short command description prefix.
     */
    protected static final String SHORT_COMMAND_DESCRIPTION_PREFIX = "__"  + COMMAND_PREFIX;

    /**
     * Extended command description prefix.
     */
    protected static final String LONG_COMMAND_DESCRIPTION_PREFIX  = "___" + COMMAND_PREFIX;

    /**
     * Jar resource prefix.
     */
    protected static final String RESOURCE_JAR_PREFIX    = "jar:";

    /**
     * Source dir resource prefix.
     */
    protected static final String RESOURCE_SOURCE_PREFIX = "src:";

    /**
     * Source dir resource prefix.
     */
    protected static final String RESOURCE_JAVADOC_PREFIX = "javadoc:";

    /**
     * Class dir resource prefix.
     */
    protected static final String RESOURCE_CLASS_PREFIX  = "class:";

    /**
     * Exit command.
     */
    protected static final String EXIT_COMMAND = "exit";

    /**
     * Command shell history file.
     */
    protected static final File HISTORY_FILE = new File( new File(System.getProperty("user.home") ), ".rdfcoder");

    /* Private constants. */

    /**
     * Default code model name.
     */
    protected static final String DEFAULT_CODE_MODEL = "default";

    /**
     * Java profile name.
     */
    private static final String JAVA_PROFILE = "java-profile";

    /**
     * Arguments buffer.
     */
    private static List<String> argsBuffer = new ArrayList<String>();

    /* Fields. */

    /**
     * Loaded model handlers.
     */
    private Map<String,ModelHandler> modelHandlers;

    /**
     * Currently selected model.
     */
    private String selectedModel;

    /**
     * Inspector common instance.
     */
    private Inspector inspector;

    /**
     * Debug mode flag.
     */
    private boolean debug = false;

    /**
     * List of model that has been modified and are waiting to be saved.
     */
    private List<String> toBeSaved;

    /* Console related stuff. */

    /**
     * Command line console reader.
     */
    private ConsoleReader consoleReader;

    /**
     * Console history.
     */
    private History history;

    /**
     * The current console directory.
     */
    private File currentDirectory;

    /**
     * Map of detected commands.
     */
    private Map<String,Command> commands;

    /**
     * Internal library facade.
     */
    private final RDFCoder rdfCoder;

    /**
     * Constructor.
     *
     * @param file the initial location.
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public AbstractCommandLine(File file)
    throws IOException, IllegalAccessException, InvocationTargetException {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        currentDirectory = file;

        modelHandlers = new HashMap<String, ModelHandler>();
        toBeSaved = new ArrayList<String>();

        // Java profile initialization.
        rdfCoder = new RDFCoder("./rdfcoder-repository");
        rdfCoder.setDebug( debug );
        rdfCoder.registerProfile(JAVA_PROFILE, "com.asemantics.rdfcoder.JavaProfile");
        // TODO: invoke initJRE()

        // Model handler initialization.
        createModelHandler(DEFAULT_CODE_MODEL);
        selectedModel = DEFAULT_CODE_MODEL;

        // Init console reader and history.
        consoleReader = new ConsoleReader();
        if(!HISTORY_FILE.exists()) {
            HISTORY_FILE.createNewFile();
        }
        history = new History(HISTORY_FILE);
        consoleReader.setHistory(history);
        consoleReader.setUseHistory(true);
        CandidateListCompletionHandler completionHandler = new CandidateListCompletionHandler();
        consoleReader.setCompletionHandler(completionHandler);
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new SimpleCompletor( getCommandNames() )
                )
        );
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new FileNameCompletor ()
                )
        );
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean f) {
        rdfCoder.setDebug(f);
    }

    public String getSelectedModel() {
        return selectedModel;
    }

    public Set<String> getModelHandlerNames() {
        return modelHandlers.keySet();
    }

    /**
     * Sets a new current location.
     *
     * @param candidateLocation the new candidate location.
     */
    public void setCurrentDirectory(File candidateLocation) {
        File newDirectory;
        try {
            newDirectory = toAbsolutePath( candidateLocation.getPath() ).getCanonicalFile();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("An error occurred while switching to new location.", ioe);
        }
        if( newDirectory.exists() ) {
            currentDirectory = newDirectory;
        } else {
            throw new IllegalArgumentException(
                String.format("Cannot change directory to un-existing path:'%s'", newDirectory.getAbsolutePath() )
            );
        }
    }

    /**
     * Returns the current location.
     *
     * @return file representing the current location.
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Returns an array of the available argsBuffer declared in this class.
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Command[] getCommands() {
        loadCommands();
        Collection<Command> result = commands.values();
        return result.toArray( new Command[result.size()] );
    }

    /**
     * Returns an array of the available command names.
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public String[] getCommandNames() {
        loadCommands();
        Collection<String> result = commands.keySet();
        return result.toArray( new String[result.size()] );
    }

    /**
     * Sets the selected model.
     *
     * @param modelName
     */
    protected void setSelectedModel(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        selectedModel = modelName;
    }

    /**
     * If <i>in</i>parameter is an absolute file it is returned unchanged,
     * otherwise a concatenation of #currentDirectory and the relative path is
     * returned.
     *
     * @param in
     * @return create file.
     */
    protected File toAbsolutePath(String in) {
        File inFile = new File(in);
        if(inFile.isAbsolute()) {
            return inFile;
        }
        return new File( getCurrentDirectory(), in );
    }

    /**
     * Creates a model handler for a model name.
     *
     * @param modelName
     * @return
     */
    protected ModelHandler createModelHandler(String modelName) {
        if(modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("a model with name " + modelName + " already exists.");
        }

        Model model = rdfCoder.createModel(modelName);
        JavaProfile jprofile = (JavaProfile) model.getProfile(JAVA_PROFILE);
        ModelHandler mh = new ModelHandler(model, jprofile);
        modelHandlers.put(modelName, mh);
        return mh;
    }

    /**
     * Performs a SPARQL query on the specified model.
     *
     * @param modelName
     * @param qry
     * @param ps
     */
    protected void performQueryOnModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        CodeModel cm = mh.model.getCodeModelBase();
        if( ! (cm instanceof SPARQLQuerableCodeModel) ) {
            throw new IllegalArgumentException("code model " + modelName + " is not a QuerableCodeModel instance.");
        }
        SPARQLQuerableCodeModel qcm = (SPARQLQuerableCodeModel) cm;
        QueryResult qr = null;
        try {
            qr = qcm.performQuery(qry);
            qr.toTabularView(ps);
        } catch (SPARQLException e) {
            throw new IllegalArgumentException("Cannot perform SPARQL query.");
        } finally {
            if(qr != null) { qr.close(); }
        }
    }

    /**
     * Performs a SPARQL query on the selected model.
     *
     * @param qry
     * @param ps
     */
    protected void performQueryOnModel(String qry, PrintStream ps) {
        performQueryOnModel(selectedModel, qry, ps);
    }

    /**
     * Inspect the specified model as a bean.
     *
     * @param modelName
     * @param qry
     * @param ps
     */
    protected void inspectModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName) ) {
            throw new IllegalArgumentException("model with name '" + modelName + "' doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        JavaQueryModel qm = mh.javaProfile.getQueryModel();

        if( inspector == null) {
            inspector = new Inspector();
            inspector.addToContext(INSPECTION_MODEL_NAME, qm);
        }

        try {
            Object o = inspector.inspect(qry);
            ps.println(o);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot perform inspection query.", e);
        }
    }

    /**
     * Inspects the active code model as a bean.
     *
     * @param qry
     * @param ps
     */
    protected void inspectModel(String qry, PrintStream ps) {
        inspectModel(selectedModel, qry, ps);
    }

    /**
     * Describes the specified model.
     *
     * @param modelName
     * @param qry
     * @param ps
     */
    protected void describeModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName) ) {
            throw new IllegalArgumentException("model with name '" + modelName + "' doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        JavaQueryModel qm = mh.javaProfile.getQueryModel();

        if( inspector == null) {
            inspector = new Inspector();
            inspector.addToContext(INSPECTION_MODEL_NAME, qm);
        }

        try {
            String description = inspector.describe(qry);
            ps.println(description);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot perform inspection query.", e);
        }
    }

    /**
     * Describes the active code model.
     *
     * @param qry
     * @param ps
     */
    protected void describeModel(String qry, PrintStream ps) {
        describeModel(selectedModel, qry, ps);
    }

    /**
     * Returns the code model associated to the selected model.
     *
     * @return
     */
    protected CodeModel getCodeModel() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.model.getCodeModelBase();
    }

    protected JavaProfile getJavaProfile() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.javaProfile;
    }

    protected CoderFactory getCoderFactory() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.model.getCoderFactory();
    }

    /**
     * Validates a library name.
     *
     * @param libName
     */
    protected void validLibraryName(String libName) {
        if(libName == null || libName.trim().length() == 0) {
            throw new IllegalArgumentException("Illegal library name: '" + libName + "'");
        }
    }

    /**
     * Validates a Jar file.
     *
     * @param pathToJar
     * @return
     */
    protected File validJar(String pathToJar) {
        if(pathToJar.indexOf(RESOURCE_JAR_PREFIX) != 0) {
             return null;
        }
        File jarFile = toAbsolutePath(pathToJar.substring(RESOURCE_JAR_PREFIX.length()));
        return jarFile.exists() && jarFile.isFile() ? jarFile : null;
    }

    /**
     * Validates a source dir path.
     *
     * @param pathToSource
     * @return
     */
    protected File validSource(String pathToSource) {
       if(pathToSource.indexOf(RESOURCE_SOURCE_PREFIX) != 0) {
             return null;
        }
        File sourceDir = toAbsolutePath(pathToSource.substring(RESOURCE_SOURCE_PREFIX.length()));
        return sourceDir.exists() && sourceDir.isDirectory() ? sourceDir : null;
    }

    /**
     * Validates a javadoc dir path.
     *
     * @param pathToJavadoc
     * @return
     */
    protected File validJavadoc(String pathToJavadoc) {
       if(pathToJavadoc.indexOf(RESOURCE_JAVADOC_PREFIX) != 0) {
             return null;
        }
        File javadocDir = toAbsolutePath(pathToJavadoc.substring(RESOURCE_JAVADOC_PREFIX.length()));
        return javadocDir.exists() && javadocDir.isDirectory() ? javadocDir : null;
    }

    /**
     * Validates a classes dir path.
     *
     * @param pathToClass
     * @return
     */
    protected File validClass(String pathToClass) {
       if(pathToClass.indexOf(RESOURCE_CLASS_PREFIX) != 0) {
             return null;
        }
        File classDir = toAbsolutePath(pathToClass.substring(RESOURCE_CLASS_PREFIX.length()));
        return classDir.exists() && classDir.isDirectory() ? classDir : null;
    }

    /**
     * Loads a list of libraries and reports the result operations on the given PrintStream.
     *
     * @param args
     * @param ps
     * @throws IOException
     */
    protected void loadLibraries(String[] args, PrintStream ps) throws IOException {
        String libraryName;

        // Validate arguments first.
        List<Library> libraries = new ArrayList<Library>();
        for(int i = 0; i < args.length; i += 2) {
            libraryName = args[i];
            validLibraryName(libraryName);
            String resource = args[i + 1];
            File resourceFile;
            LibraryType type;
            if( (resourceFile = validJar(resource)) != null ) {
                type = LibraryType.JAR_FILE;
            } else if ( (resourceFile = validSource(resource) ) != null ) {
                type = LibraryType.SOURCE_DIR;
            } else if ( (resourceFile = validJavadoc(resource)) != null ) {
                type = LibraryType.JAVADOC_DIR;
            } else if ( (resourceFile = validClass(resource)  ) != null ) {
                type = LibraryType.CLASS_DIR;
            } else {
                throw new IllegalArgumentException("Cannot find resource: '" + args[i + 1] + "'");
            }
            libraries.add(new Library(libraryName, resourceFile, type));
        }

        JavaProfile jprofile = getJavaProfile();
        final List<JStatistics> statistics = new ArrayList<JStatistics>();
        for(int i = 0; i < libraries.size(); i++) {
            final Library currentLibrary = libraries.get(i);
            if( currentLibrary.type == LibraryType.JAR_FILE ) {

                try {
                    System.out.print("loading " + currentLibrary.location.getAbsolutePath() + " ...");
                    statistics.add(
                            jprofile.loadJar( currentLibrary.name, currentLibrary.location.getAbsolutePath() )
                    );
                    System.out.println(" done");
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            String.format("Error while loading JAR: '%s'", currentLibrary.location),
                            e
                    );
                }

            } else if( libraries.get(i).type == LibraryType.SOURCE_DIR ) {

                try {
                    System.out.format( "loading '%s' ...", currentLibrary.location.getAbsolutePath() );
                    statistics.add(
                        jprofile.loadSources( currentLibrary.name, currentLibrary.location.getAbsolutePath() )
                    );
                    System.out.println(" done");
                } catch(Exception e) {
                    throw new IllegalArgumentException(
                            String.format("Error while reading SRC dir: '%s'", libraries.get(i).location),
                            e
                    );
                }

            } else if(libraries.get(i).type == LibraryType.JAVADOC_DIR ) {

                // TODO: implement this.
                throw new UnsupportedOperationException();

//                    try {
//                        System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
//                        javadocDirectoryParser.parseDirectory( libraries.get(i).name, libraries.get(i).location );
//                        System.out.println(" done");
//                    } catch (Exception e) {
//                        throw new IllegalArgumentException(
//                              "Error while reading JAVADOC dir: '" + libraries.get(i).location + "'", e
//                        );
//                    }

            } else if(libraries.get(i).type == LibraryType.CLASS_DIR ) {

                try {
                    System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
//                        classDirectoryParser.parseDirectory( libraries.get(i).name, libraries.get(i).location );
                    statistics.add(
                        jprofile.loadClasses( currentLibrary.name, currentLibrary.location.getAbsolutePath() )
                    );
                    System.out.println(" done");
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            String.format("Error while reading CLASS dir: '%s'", libraries.get(i).location),
                            e
                    );
                }
            } else {
                throw new IllegalStateException(
                        String.format("Cannot load library: '%s'", libraries.get(i).name)
                );
            }
        }

        for(JStatistics stats : statistics) {
            ps.print( stats.toStringReport() );
        }

        toBeSaved.add(selectedModel);
    }

    /**
     * Saves a model on the basis of the given parameters.
     *
     * @param parameters
     * @throws IOException
     * @throws com.asemantics.rdfcoder.storage.CodeStorageException
     */
    protected void saveModel(Map<String,String> parameters) throws CodeStorageException {
        CodeStorage codeStorage = getCoderFactory().createCodeStorage();
        codeStorage.saveModel( getCodeModel(), parameters );

        toBeSaved.remove(selectedModel);
    }

    /**
     * Loads a model on the basis of the given parameters.
     *
     * @param parameters
     * @throws IOException
     * @throws com.asemantics.rdfcoder.storage.CodeStorageException
     */
    protected void loadModel(Map<String,String> parameters) throws CodeStorageException {
        CodeStorage codeStorage = getCoderFactory().createCodeStorage();
        codeStorage.loadModel( getCodeModel(), parameters );
    }

    /**
     * Retrieve a specified param on an array of arguments.
     *
     * @param param
     * @param args
     * @return
     */
    private String retrieveParam(String param, String[] args) {
        String target = param + "=";
        for(String arg : args) {
            if( arg.indexOf(target) == 0 ) {
                return arg.substring(target.length());
            }
        }
        return null;
    }

    /**
     * Prints the usage of the CommandLine interface.
     *
     * @param ps
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void printUsage(PrintStream ps) throws IllegalAccessException, InvocationTargetException {
        ps.println("Usage: <command> <parameters>");
        ps.println();
        ps.println("\tavailable commands:");
        Command[] availableCommands = getCommands();
        for(int i = 0; i < availableCommands.length; i++) {
            ps.println("\t" + availableCommands[i].name + "\t\t\t" + availableCommands[i].description);
        }
        ps.println();
    }

    /**
     * Retrieves the short command description of a specified command.
     *
     * @param commandName
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private String getShortCommandDescription(String commandName)
    throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        for(int i = 0; i < methods.length; i++) {
            if(methods[i].getName().equals(SHORT_COMMAND_DESCRIPTION_PREFIX + commandName)) {
                return (String) methods[i].invoke(this);
            }
        }
        return null;
    }

    /**
     * Retrieves the extended command description of a specified command.
     *
     * @param commandName
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private String getLongCommandDescription(String commandName)
    throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        for(int i = 0; i < methods.length; i++) {
            if(methods[i].getName().equals(LONG_COMMAND_DESCRIPTION_PREFIX + commandName)) {
                return (String) methods[i].invoke(this);
            }
        }
        return null;
    }

    /**
     * Retrieves the file storage parameters.
     *
     * @param args
     * @return
     */
    private Map fileStorageParams(String[] args) {
        Map parameters = new HashMap();
        String filename = retrieveParam(CodeStorage.FS_FILENAME, args);
        parameters.put(CodeStorage.FS_FILENAME, filename);
        return parameters;
    }

    /**
     * Retrieves the database storage parameters.
     *
     * @param args
     * @return
     */
    private Map databaseStorageParams(String[] args) {
        Map<String,String> parameters = new HashMap<String,String>();
        String server = retrieveParam(CodeStorage.DB_SERVER, args);
        if( server == null ) {
            throw new IllegalArgumentException(
                    CodeStorage.FS_FILENAME + " or " + CodeStorage.DB_SERVER + " must be specified."
            );
        }
        String port = retrieveParam(CodeStorage.DB_PORT, args);
        if( port == null ) {
            throw new IllegalArgumentException(CodeStorage.DB_PORT + " must be specified.");
        }
        String name = retrieveParam(CodeStorage.DB_NAME, args);
        if( name == null ) {
            throw new IllegalArgumentException(CodeStorage.DB_NAME + " must be specified.");
        }
        String username = retrieveParam(CodeStorage.DB_USERNAME, args);
        if( username == null ) {
            throw new IllegalArgumentException(CodeStorage.DB_USERNAME + " must be specified.");
        }
        String password = retrieveParam(CodeStorage.DB_PASSWORD, args);
        if( password == null ) {
            throw new IllegalArgumentException(CodeStorage.DB_PASSWORD + " must be specified.");
        }
        parameters.put(CodeStorage.DB_SERVER,   server);
        parameters.put(CodeStorage.DB_PORT,     port);
        parameters.put(CodeStorage.DB_NAME,     name);
        parameters.put(CodeStorage.DB_USERNAME, username);
        parameters.put(CodeStorage.DB_PASSWORD, password);
        return parameters;
    }

    /**
     * Command to save a model on the storage specified with parameters.
     *
     * @param args
     * @throws IOException
     */
    public void command_savemodel(String[] args) throws CodeStorageException {
        if(args.length < 1) {
            throw new IllegalArgumentException("at least storage name must be specified");
        }

        Map parameters;
        if(CodeStorage.STORAGE_FS.equals(args[0])) {
            parameters = fileStorageParams(args);
        } else {
            parameters = databaseStorageParams(args);
        }
        saveModel(parameters);
    }

    public String __command_savemodel() {
        return "Saves the current model";
    }

    public String ___command_savemodel() {
        return
                __command_savemodel() +
                "\nsyntax: savemodel <storagename> [<storage_param1> ...]" +
                "\n\tsaves the current model on the storage with name storagename by using the given parameters" +
                "\n\t possible values for storagename are: " +
                "\n\t\t" + CodeStorage.STORAGE_FS  + "==filesystem" +
                "\n\t\t" + CodeStorage.STORAGE_DB + "==database" +
                "\n\texample:" +
                "\n\tmodel1> savemodel fs filename=/path/to/file.xml";

    }

    /**
     * Command to load a model.
     *
     * @param args
     * @throws IOException
     */
    public void command_loadmodel(String[] args) throws CodeStorageException {
        if(args.length < 1) {
            throw new IllegalArgumentException("at least storage name must be specified");
        }

        Map parameters;
        if(CodeStorage.STORAGE_FS.equals(args[0])) {
            parameters = fileStorageParams(args);
        } else {
            parameters = databaseStorageParams(args);
        }
        loadModel(parameters);
    }

    public String __command_loadmodel() {
        return "Loads a model from a storage";
    }

    public String ___command_loadmodel() {
        return
                __command_loadmodel() +
                "\nsyntax: loadmodel <storagename> [<storage_param1> ...]" +
                "\n\tloads on the current model the content of the storage with name storagename" +
                " by using the given parameters" +
                "\n\t possible values for storagename are: "
                        + CodeStorage.STORAGE_FS  + "==filesystem" + CodeStorage.STORAGE_DB + "==database" +
                "\n\texample:" +
                "\n\tmodel1> loadmodel fs filename=/path/to/file.xml";

    }

    /**
     * Command to obtain help on other command.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void command_help(String[] args) throws IllegalAccessException, InvocationTargetException {
        if(args.length == 0) {
            printUsage(System.out);
            return;
        }
        if(args.length == 1) {
            String longHelp = getLongCommandDescription(args[0]);
            if(longHelp == null) {
                System.out.println("cannot find help for command '" + args[0] + "'");
            } else {
                System.out.println(longHelp);
            }
            return;
        }
        System.out.println("invalid command");
        System.out.println( __command_help() );
    }

    public String __command_help() {
        return "prints this help\n\tto obtain more informations about a specific command type: help <command>";
    }

    public String ___command_help() {
        return
                "prints the usage help of a specific command." +
                "\n\tsyntax: help <command>";
    }

    /* END Command line argsBuffer. */

    /**
     * Loads all the commands defined in the command line.
     */
    private void loadCommands() {
        if(commands != null) {
            return;
        }

        commands = new HashMap<String, Command>();
        Method[] methods = this.getClass().getMethods();
        Command command;
        try {
            for (Method method : methods) {
                final String methodName = method.getName();
                if (methodName.indexOf(COMMAND_PREFIX) == 0) {
                    String commandName = methodName.substring(COMMAND_PREFIX.length());
                    command = new Command(commandName, getShortCommandDescription(commandName), method);
                    commands.put(commandName, command);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while loading commands.", e);
        }
    }

    /**
     * Processes a command line input.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return returns <code>true</code> if the command has been processed, <code>false</code> otherwise.
     *
     */
    protected boolean processCommand(String[] args) throws IllegalAccessException, InvocationTargetException {
        if(args.length == 0) {
            return true;
        }

        loadCommands();

        String command = args[0];
        String[] commandArgs = new String[args.length -1];
        int i;
        for(i = 1; i < args.length; i++) {
            commandArgs[i - 1] = args[i];
        }

        final Method target = commands.get(command).target;
        if(target == null) {
            throw new IllegalArgumentException("unknown command: " + command);
        }

        try {
            target.invoke(this, (Object) commandArgs);
        } catch (InvocationTargetException ite) {
            if (ite.getCause() instanceof IllegalArgumentException) {
                IllegalArgumentException iae = (IllegalArgumentException) ite.getCause();
                handleIllegalArgumentException(iae, command);
                return false;
            } else {
                throw ite;
            }
        }
        return true;
    }

    /**
     * Handles an IllegalArgumentException raised by a command method transforming it
     * in a error message.
     *
     * @param iae
     * @param cmd
     */
    private void handleIllegalArgumentException(IllegalArgumentException iae, String cmd) {
        System.out.println("ERROR: '" + iae.getMessage() + "'");
        if(debug) { iae.printStackTrace(); }
        Throwable cause = iae.getCause();
        int causeLevel = 0;
        while(cause != null) {
            System.out.println("[" + (causeLevel++) + "] with cause: '" + iae.getCause().getMessage() + "'");
            if(debug) { iae.getCause().printStackTrace(); }
            cause = cause.getCause();
        }

        try {
            if(cmd == null) {
                printUsage(System.out);
            } else {
                System.out.println(getLongCommandDescription(cmd));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an IllegalArgumentException.
     *
     * @param iae
     */
    private void handleIllegalArgumentException(IllegalArgumentException iae) {
        handleIllegalArgumentException(iae, null);
    }

    /**
     * Handles a severe exception.
     *
     * @param t
     */
    private void handleGenericException(Throwable t) {
        System.out.println("SEVERE ERROR: an expected exception occurred: '" + t.getMessage() + "'");
        if(debug) {
            t.printStackTrace();
        }
        if(t.getCause() != null) {
            System.out.println("with cause: '" + t.getCause().getMessage() + "'");
            if(debug) {
                t.getCause().printStackTrace();
            }
         }
    }

    /**
     * Prints a <i>Hello</i> message.
     */
    private void printHello() {
        System.out.println("RDFCoder command line console [version " + VERSION_MAJOR + "." + VERSION_MINOR + "]");
        System.out.println();
    }

    private String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        String ret = consoleReader.readLine();
        return ret;
    }

    /**
     * Requires a confirmation exit.
     *
     * @return
     * @throws IOException
     */
    private boolean confirmExit() throws IOException {
       if( ! toBeSaved.isEmpty() ) { // There are models not yes stored.
            System.out.println("Following models has been modified but not stored:");
            for(String m : toBeSaved ) {
                System.out.println("\t" + m);
            }
            while(true) {
                String response = readInput("Are you sure do you want to exit? [Yes/No]");
                if(response.toLowerCase().equals("y") || response.toLowerCase().equals("yes")) {
                    return true;
                } else if(response.toLowerCase().equals("n") || response.toLowerCase().equals("no") ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Generates the prompt string.
     *
     * @return the prompts string.
     */
    protected String getPrompt() {
        return getCurrentDirectory().getName() + "~" + selectedModel + "> ";
    }

    /**
     * Processes a single command line input.
     *
     * @param line the input command line.
     * @return
     * @throws IOException
     */
    protected boolean processLine(String line) throws IOException {
        String[] arguments = extractArgs(line);
        if (isExit(arguments) && confirmExit()) {
            return false;
        }
        try {
            processCommand(arguments);
        } catch (IllegalArgumentException iae) {
            handleIllegalArgumentException(iae);
        } catch (Throwable t) {
            handleGenericException(t);
        }
        return true;
    }

    /**
     * Main cycle of the command line console.
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    protected void mainCycle() throws IllegalAccessException, InvocationTargetException, IOException {
        printHello();
        while ( processLine( readInput( getPrompt() ) )  );
        System.out.println("Bye");
        System.exit(0);
    }

    /**
     * Extracts arguments from a command line input.
     *
     * @param cl
     * @return the list of arguments.
     */
    protected static String[] extractArgs(String cl) {
        argsBuffer.clear();
        cl += " ";
        boolean insideQuotes = false;
        int begin = 0;
        int c;
        for(c = 0; c < cl.length(); c++) {
            if( cl.charAt(c) == '"' ) {
                if(insideQuotes) {
                    insideQuotes = false;
                    if(c - begin > 0) {
                        argsBuffer.add(cl.substring(begin, c));
                    }
                } else {
                    insideQuotes = true;
                }
                begin = c + 1;
            }
            if(insideQuotes) {
                if(c == cl.length() - 1) {
                    throw new IllegalArgumentException("not found quotes closure.");
                }
                continue;
            }
            if( cl.charAt(c) == ' ' ) {
                if(c - begin > 0) {
                    argsBuffer.add( cl.substring(begin, c) );
                }
                begin = c + 1;
            } else if(c == cl.length() - 1 && c - begin > 0) {
                argsBuffer.add( cl.substring(begin, c + 1) );
            }
        }
        return argsBuffer.toArray(new String[argsBuffer.size()]);
    }

    /**
     * Returns <code>true</code> if <i>EXIT_COMMAND</i> has been specified.
     * @param args
     * @return
     */
    private static boolean isExit(String[] args) {
        return args.length > 0 && args[0].equals(EXIT_COMMAND);
    }

    /**
     * Access point.
     *
     * @param args
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void main(String[] args)
    throws IOException, IllegalAccessException, InvocationTargetException {
        CommandLine commandLine = new CommandLine(new File("."));
        commandLine.mainCycle();
    }

    /**
     * Defines a command with a name and a description.
     */
    public class Command {
        protected final String name;
        protected final String description;
        protected final Method target;

        private Command(String description, String name, Method target) {
            this.description = description;
            this.name = name;
            this.target = target;
        }
    }

    /**
     * Defines a model handler, i.e. a set of related classes
     * used to handle a code model.
     */
    class ModelHandler {

        /**
         * Code model.
         */
        final Model model;

        /**
         * Code handler.
         */
        final JavaProfile javaProfile;

        ModelHandler(Model m, JavaProfile jp) {
            model       = m;
            javaProfile = jp;
        }

    }

    /**
     * Defines a library entity when defining a class path.
     */
    private class Library {

        /**
         * Library name.
         */
        String name;

        /**
         * Library location.
         */
        File location;

        /**
         * Library type.
         */
        LibraryType type;

        Library(String n, File f, LibraryType t) {
            name     = n;
            location = f;
            type     = t;
        }
    }

}
