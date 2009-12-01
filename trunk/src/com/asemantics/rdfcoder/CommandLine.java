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
import com.asemantics.rdfcoder.model.CodeHandler;
import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.java.JavaCoderFactory;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.model.QueryResult;
import com.asemantics.rdfcoder.model.SPARQLException;
import com.asemantics.rdfcoder.model.SPARQLQuerableCodeModel;
import com.asemantics.rdfcoder.sourceparse.DirectoryParser;
import com.asemantics.rdfcoder.sourceparse.JStatistics;
import com.asemantics.rdfcoder.sourceparse.JavaBytecodeFileParser;
import com.asemantics.rdfcoder.sourceparse.JavaBytecodeJarParser;
import com.asemantics.rdfcoder.sourceparse.JavaSourceFileParser;
import com.asemantics.rdfcoder.sourceparse.JavadocFileParser;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.storage.CodeStorage;
import com.asemantics.rdfcoder.storage.CodeStorageException;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import jline.ArgumentCompletor;
import jline.CandidateListCompletionHandler;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.History;
import jline.SimpleCompletor;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Command line EDFCoder utility.
 */
public class CommandLine {

    /**
     * Defines a model handler, i.e. a set of related classes
     * used to handle a code model.
     */
    class ModelHandler {

        /**
         * Code model.
         */
        CodeModel codeModel;

        /**
         * Code handler.
         */
        CodeHandler codeHandler;

        /**
         * Query interface.
         */
        JavaQueryModel queryModel;

        ModelHandler(CodeModel cm, CodeHandler ch, JavaQueryModel qm) {
            codeModel   = cm;
            codeHandler = ch;
            queryModel  = qm;
        }
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
     * Defualt jar expected extension. 
     */
    private static final String JAR_EXT = ".jar";

    /* Fields. */

    private JavaCoderFactory coderFactory;

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

    /* Console readed stuff. */

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
     * Constructor.

     * @param file the initial location.
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public CommandLine(File file) throws IOException, IllegalAccessException, InvocationTargetException {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        currentDirectory = file;

        coderFactory = new JenaCoderFactory();
        modelHandlers = new HashMap();
        toBeSaved = new ArrayList();

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
        Command[] commands = getAvailableCommands();
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new SimpleCompletor( getAvailableCommandNames() )
                )
        );
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new FileNameCompletor ()
                )
        );        
    }

    /**
     * Sets a new current location.
     *
     * @param newLocation
     */
    protected void setCurrentDirectory(File newLocation) {
        if(newLocation.exists()) {
            currentDirectory = new File( toAbsolutePath( newLocation.getAbsolutePath() ).getAbsolutePath());
        } else {
            throw new IllegalArgumentException("cannot change directory to unexisting path:'" + newLocation.getAbsolutePath() + "'");
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
     * If <i>in</i>parameter is an absolute file it is returned unchanged,
     * otherwise a concatenation of #currentDirectory and the relative path is
     * returned.
     *  
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
    private ModelHandler createModelHandler(String modelName) {
        if(modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("a model with name " + modelName + " already exists.");   
        }

        CodeModelBase cmb = coderFactory.createCodeModel();
        CodeHandler   ch  = coderFactory.createHandlerOnModel(cmb);
        JavaQueryModel qm = coderFactory.createQueryModel(cmb);
        ModelHandler mh   = new ModelHandler(cmb, ch, qm);
        modelHandlers.put(modelName, mh);
        return mh;
    }

    /**
     * Removes a model handler for the specified model name.
     *
     * @param modelName
     */
    private void removeModelHandler(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist."); 
        }
        if(modelHandlers.size() == 1) {
            throw new IllegalArgumentException("at least a model must be present in the models set");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        mh.codeModel.clearAll();
        modelHandlers.remove(modelName);
    }

    /**
     * Cleans up a model handler.
     *
     * @param modelName
     */
    private void clearModelHandler(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        mh.codeModel.clearAll();
    }

    /**
     * Sets the selected model.
     *
     * @param modelName
     */
    private void setSelectedModel(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        selectedModel = modelName;
    }

    /**
     * Performs a SPARQL query on the specified model.
     * 
     * @param modelName
     * @param qry
     * @param ps
     */
    private void performQueryOnModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        CodeModel cm = mh.codeModel;
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
    private void performQueryOnModel(String qry, PrintStream ps) {
        performQueryOnModel(selectedModel, qry, ps);
    }

    /**
     * Inspect the specified model as a bean.
     *
     * @param modelName
     * @param qry
     * @param ps
     */
    private void inspectModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName) ) {
            throw new IllegalArgumentException("model with name '" + modelName + "' doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        JavaQueryModel qm = mh.queryModel;

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
     * @param qry
     * @param ps
     */
    private void inspectModel(String qry, PrintStream ps) {
        inspectModel(selectedModel, qry, ps);
    }

    /**
     * Describes the specified model.
     * @param modelName
     * @param qry
     * @param ps
     */
    private void describeModel(String modelName, String qry, PrintStream ps) {
        if( ! modelHandlers.containsKey(modelName) ) {
            throw new IllegalArgumentException("model with name '" + modelName + "' doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        JavaQueryModel qm = mh.queryModel;

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
     * @param qry
     * @param ps
     */
    private void describeModel(String qry, PrintStream ps) {
        describeModel(selectedModel, qry, ps);
    }

    /**
     * Returns the code handler associated to the selected model.
     *
     * @return
     */
    private CodeHandler getCodeHandler() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.codeHandler;
    }

    /**
     * Returns the code model associated to the selected model.
     *
     * @return
     */
    private CodeModel getCodeModel() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.codeModel;
    }

    /**
     * Validates a library name.
     *
     * @param libName
     */
    private void validLibraryName(String libName) {
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
    private File validJar(String pathToJar) {
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
    private File validSource(String pathToSource) {
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
    private File validJavadoc(String pathToJavadoc) {
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
    private File validClass(String pathToClass) {
       if(pathToClass.indexOf(RESOURCE_CLASS_PREFIX) != 0) {
             return null;
        }
        File classDir = toAbsolutePath(pathToClass.substring(RESOURCE_CLASS_PREFIX.length()));
        return classDir.exists() && classDir.isDirectory() ? classDir : null;
    }

    /**
     * Enumeration on library types.
     */
    private enum LibraryType {
        JAR_FILE,
        SOURCE_DIR,
        JAVADOC_DIR,
        CLASS_DIR,
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

    /**
     * Loads a list of libraries and reports the result operations on the given PrintStream.
     * 
     * @param args
     * @param ps
     * @throws IOException
     */
    private void loadLibraries(String[] args, PrintStream ps) throws IOException {
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

        // Process arguments.
        ObjectsTable ot = new ObjectsTable();
        JStatistics statistics = new JStatistics();
        CodeHandler statisticsCodeHandler = statistics.createStatisticsCodeHandler( getCodeHandler() );

        JavaBytecodeJarParser javaBytecodeJarParser = null;
        DirectoryParser sourceDirectoryParser       = null;
        DirectoryParser javadocDirectoryParser      = null;
        DirectoryParser classDirectoryParser        = null;
        for(int i = 0; i < libraries.size(); i++) {

            try {
                //statisticsCodeHandler.startParsing(libraries.get(i).name, libraries.get(i).location.getAbsolutePath());

                javaBytecodeJarParser = new JavaBytecodeJarParser();
                javaBytecodeJarParser.initialize( statisticsCodeHandler, ot );

                JavaSourceFileParser javaSourceFileParser = new JavaSourceFileParser();
                sourceDirectoryParser = new DirectoryParser(javaSourceFileParser, new CoderUtils.JavaSourceFilenameFilter() );
                sourceDirectoryParser.initialize( statisticsCodeHandler, ot );

                JavadocFileParser javadocFileParser = new JavadocFileParser();
                javadocDirectoryParser = new DirectoryParser(javadocFileParser, new CoderUtils.JavaSourceFilenameFilter() );
                javadocDirectoryParser.initialize( statisticsCodeHandler, ot );

                JavaBytecodeFileParser javaBytecodeFileParser = new JavaBytecodeFileParser();
                classDirectoryParser = new DirectoryParser(javaBytecodeFileParser, new CoderUtils.JavaClassFilenameFilter() );
                classDirectoryParser.initialize( statisticsCodeHandler, ot );

                if( libraries.get(i).type == LibraryType.JAR_FILE ) {

                    try {
                        System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                        javaBytecodeJarParser.parseFile(libraries.get(i).location);
                        System.out.println(" done");
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error while loading JAR: '" + libraries.get(i).location + "'", e);
                    }

                } else if( libraries.get(i).type == LibraryType.SOURCE_DIR ) {

                    try {
                        System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                        sourceDirectoryParser.parseDirectory( libraries.get(i).name, libraries.get(i).location );
                        System.out.println(" done");
                    } catch(Exception e) {
                        throw new IllegalArgumentException("Error while reading SRC dir: '" + libraries.get(i).location + "'", e);
                    }

                } else if(libraries.get(i).type == LibraryType.JAVADOC_DIR ) {

                    try {
                        System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                        javadocDirectoryParser.parseDirectory( libraries.get(i).name, libraries.get(i).location );
                        System.out.println(" done");
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error while reading JAVADOC dir: '" + libraries.get(i).location + "'", e);
                    }

                } else if(libraries.get(i).type == LibraryType.CLASS_DIR ) {

                    try {
                        System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                        classDirectoryParser.parseDirectory( libraries.get(i).name, libraries.get(i).location );
                        System.out.println(" done");
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error while reading CLASS dir: '" + libraries.get(i).location + "'", e);
                    }

                } else {
                    throw new IllegalStateException("Cannot load library: '" + libraries.get(i).name + "'");
                }

            } finally {
                if(javaBytecodeJarParser  != null) { javaBytecodeJarParser.dispose();  }
                if(sourceDirectoryParser  != null) { sourceDirectoryParser.dispose();  }
                if(javadocDirectoryParser != null) { javadocDirectoryParser.dispose(); }
                if(classDirectoryParser   != null) { classDirectoryParser.dispose();   }
            }
        }

        //statisticsCodeHandler.endParsing();

        ot.clear();

        ps.print(statistics.toStringReport());
        statistics.clean();

        toBeSaved.add(selectedModel);
    }

    /**
     * Saves a model on the basis of the given parameters.
     *
     * @param parameters
     * @throws IOException
     */
    private void saveModel(Map parameters) throws CodeStorageException {
        CodeStorage codeStorage = coderFactory.createCodeStorage();
        codeStorage.saveModel( getCodeModel(), parameters );

        toBeSaved.remove(selectedModel);
    }

    /**
     * Loads a model on the basis of the given parameters.
     *
     * @param parameters
     * @throws IOException
     */
    private void loadModel(Map parameters) throws CodeStorageException {
        CodeStorage codeStorage = coderFactory.createCodeStorage();
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
     * Defines a command with a name and a description.
     */
    private class Command {
        private String name;
        private String description;
    }

    /**
     * Retrieves the short command description of a specified command.
     *
     * @param commandName
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private String getShortCommandDescription(String commandName) throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        for(int i = 0; i < methods.length; i++) {
            if(methods[i].getName().equals(SHORT_COMMAND_DESCRIPTION_PREFIX + commandName)) {
                String description = (String) methods[i].invoke(this);
                return description;
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
    private String getLongCommandDescription(String commandName) throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        for(int i = 0; i < methods.length; i++) {
            if(methods[i].getName().equals(LONG_COMMAND_DESCRIPTION_PREFIX + commandName)) {
                String description = (String) methods[i].invoke(this);
                return description;
            }
        }
        return null;
    }

    /**
     * Returns an array of the available commands declared in this class.
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Command[] getAvailableCommands() throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        List<Command> commands = new ArrayList<Command>();
        for(int i = 0; i < methods.length; i++) {
            Command command;
            if( methods[i].getName().indexOf(COMMAND_PREFIX) == 0) {
                String commandName =  methods[i].getName().substring(COMMAND_PREFIX.length());
                command = new Command();
                command.name = commandName;
                command.description = getShortCommandDescription(commandName);
                commands.add(command);
            }
        }
        return commands.toArray( new Command[commands.size()] );
    }

    /**
     * Returns an array of the available command names.
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private String[] getAvailableCommandNames() throws IllegalAccessException, InvocationTargetException {
        Command[] commands = getAvailableCommands();
        String[] commandNames = new String[commands.length];
        for(int i = 0; i < commands.length; i++) {
            commandNames[i] = commands[i].name;
        }
        return commandNames;
    }

    /**
     * Prints the usage of the CommandLine interface.
     *
     * @param ps
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void printUsage(PrintStream ps) throws IllegalAccessException, InvocationTargetException {
        ps.println("Usage: <command> <parameters>");
        ps.println();
        ps.println("\tavailable commands:");
        Command[] availableCommands = getAvailableCommands();
        for(int i = 0; i < availableCommands.length; i++) {
            ps.println("\t" + availableCommands[i].name + "\t\t\t" + availableCommands[i].description);
        }
        ps.println();
    }

    /* BEGIN: Command line commands. */

    /**
     * Command to enable / disable the debug flag.
     *
     * @param args
     */
    public void command_debug(String[] args) {
        if( args.length == 0 ) {
            System.out.println("debug: " + debug);
        } else if(args.length == 1 && "true".equals(args[0]) ) {
            debug = true;
        }  else if(args.length == 1 && "false".equals(args[0]) ) {
            debug = false;
        } else {
            throw new IllegalArgumentException("invalid argument");
        }
    }

    public String __command_debug() {
        return "shows/sets the debug flag";
    }

    public String ___command_debug() {
        return
                __command_debug()   +
                "\n\tsyntax: debug\n" +
                "\tthe debug flag affects the level of debugging messages printed out during processing";
    }

    /**
     * Prints the current directory.
     *
     * @param args
     */
    public void command_pwd(String[] args) {
        System.out.println(currentDirectory.getAbsolutePath());
    }

    public String __command_pwd() {
        return "prints the current directory";
    }

    public String ___command_pwd() {
         return __command_pwd();
    }

    public void command_cd(String args[]) {
        if(args.length != 1) {
            throw new IllegalArgumentException("A single path must be specified");
        }
        setCurrentDirectory( new File(args[0]) );
    }

    public String __command_cd() {
        return "changes the current directory";
    }

    public String ___command_cd() {
        return
                __command_cd() +
                "\n\tsyntax: cd <path to new location>";
    }

    private String rewriteActions(String in) {
        StringBuilder sb = new StringBuilder();
        if(in.indexOf("read")    != -1) { sb.append("r"); }
        if(in.indexOf("write")   != -1) { sb.append("w"); }
        if(in.indexOf("execute") != -1) { sb.append("e"); }
        if(in.indexOf("delete")  != -1) { sb.append("d"); }
        return sb.toString();
    }

    /**
     * Lists the content of the current directory.
     */
    public void command_ls(String args[]) {
        File[] content = getCurrentDirectory().listFiles();
        FilePermission fp;
        for(File f : content) {
            fp = new FilePermission(f.getAbsolutePath(), "read,write,execute,delete");
            System.out.printf(
                    "%s\t%s\t\t\t%s\t%d\n",
                    ( f.isDirectory() ? "d" : "-"),
                    f.getName(),
                    rewriteActions( fp.getActions() ),
                    f.length()
            );
        }
    }

    public String __command_ls() {
        return "Lists the content of the current directory";
    }

    public String ___command_ls() {
        return
                __command_ls() +
                "\n\tsyntax: ls";
    }

    /**
     * Command to create a new model.
     *
     * @param args
     */
    public void command_newmodel(String[] args) {
        if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String modelName = args[0];
        createModelHandler(modelName);
    }

    public String __command_newmodel() {
       return "creates a new model";
    }
    public String ___command_newmodel() {
        return
                __command_newmodel() +
                "\n\tsyntax: newmodel <model_name>" +
                "\n\tthis command allows to create a new model and add it to the model set";
    }

    /**
     * Command to remove an existing model.
     *
     * @param args
     */
    public void command_removemodel(String[] args) {
        if( args.length != 1 ) {
            throw new IllegalArgumentException("a model name must be specified");
        }
        String modelName = args[0];
        removeModelHandler(modelName);
    }

    public String __command_removemodel() {
       return "removes the current model";
    }

    public String ___command_removemodel() {
        return
                __command_removemodel() +
                "\n\tsyntax: removemodel <model_name>" +
                "\n\tremoves a model from the models set";
    }

    /**
     * Command to clear the content of a model.
     *
     * @param args
     */
    public void command_clearmodel(String[] args) {
        if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String modelName = args[0];
        clearModelHandler(modelName);
    }

     public String __command_clearmodel() {
        return "clears the current model";
    }

    public String ___command_clearmodel() {
        return
                __command_clearmodel() +
                "\nsyntax: clearmodel <model_name>" +
                "\n\tcleans up the content of an existing model";
    }

    /**
     * Command to set the active model.
     *
     * @param args
     */
    public void command_setmodel(String[] args) {
        if(args.length == 0) {
            System.out.println("selected model: " + selectedModel);
        } else if( args.length == 1 ) {
            String modelName = args[0];
            setSelectedModel(modelName);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String __command_setmodel() {
        return "sets a new model";
    }

    public String ___command_setmodel() {
        return
                __command_setmodel() +
                "\nsyntax: setmodel <model_name>" +
                "\n\tsets the model as the default one, osed for every subsequent" +
                "\n\toperation where the model is not specified";
    }

    /**
     * Command to list the available models.
     *
     * @param args
     */
    public void command_list(String[] args) {
        if(args.length > 0) {
            throw new IllegalArgumentException();
        }
        System.out.println("Models:");
        for(String me : modelHandlers.keySet()) {
            System.out.println("\t" + me + (me.equals(selectedModel) ? "[X]" : "") );
        }
        System.out.println();
    }

    public String __command_list() {
        return "lists the loaded models";
    }

    public String ___command_list() {
        return
                __command_list() +
                "\n\tsyntax: list" +
                "\n\tlists all the models in the models set.";
    }

    /**
     * Command to query a model with a SPARQL query.
     *
     * @param args
     */
    public void command_querymodel(String[] args) {
        if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String qry = args[0];
        performQueryOnModel(qry, System.out);
    }

    public String __command_querymodel() {
        return "allows to query yhe current model";
    }

    public String ___command_querymodel() {
        return
                __command_querymodel() +
                "\nsyntax: querymodel <SPARQL_query>" +
                "\n\tperforms the given query on the current query model.";
    }

    public void command_inspect(String[] args) {
       if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String qry = args[0];
        inspectModel(qry, System.out);
    }

    public String __command_inspect() {
        return "allows to inspect the active model";
    }

    public String ___command_inspect() {
        return
                __command_inspect() +
                "\nsyntax: inspect <inspection_query>" +
                "\n\tperforms an inspection on the current query model";        
    }

    public void command_describe(String[] args) {
       if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String qry = args[0];
        describeModel(qry, System.out);
    }

    public String __command_describe() {
        return "describes the object referenced by the given path";
    }

    public String ___command_describe() {
        return
                __command_describe() +
                "\nsyntax: describe <inspection_query>" +
                "\n\tperforms an inspection on the current query model";
    }

    /**
     * Command to load a classpath on the active model.
     *
     * Expected: load <lib_name> file1 [, file2, ...]
     *
     * @param args
     * @throws IOException
     */
    public void command_loadclasspath(String[] args) throws IOException {
        if( args.length == 0 || args.length % 2 != 0 ) {
            throw new IllegalArgumentException("couples <library_name> <library_location> must be specified.");
        }
        loadLibraries(args, System.out);
    }

    public String __command_loadclasspath() {
        return "loads a classpath on the current model";
    }

    public String ___command_loadclasspath() {
        return
                __command_loadclasspath() +
                "\nsyntax: loadclasspath resource1 [resource2 ... ]" +
                "\n\twhere resource can be" +
                "\n\t\ta jar    file:  jar:/path/to/jarfile.jar" +
                "\n\t\ta source  dir:  src:/path/to/src" +
                "\n\t\ta javadoc dir:  javadoc:/path/to/src" +
                "\n\t\ta class   dir:  class:/path/to/class" +
                "\n" +
                "\n\tPerforms a parsing of the given set of resources" +
                "\n\tand loads the generated model in the active model";
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
        Map parameters = new HashMap();
        String server = retrieveParam(CodeStorage.DB_SERVER, args);
        if( server == null ) {
            throw new IllegalArgumentException(CodeStorage.FS_FILENAME + " or " + CodeStorage.DB_SERVER + " must be specified.");
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
                "\n\tloads on the current model the content of the storage with name storagename by using the given parameters" +
                "\n\t possible values for storagename are: " + CodeStorage.STORAGE_FS  + "==filesystem" + CodeStorage.STORAGE_DB + "==database" +
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

    /* END Command line commands. */

    /**
     * Processes a command line input.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected boolean processCommand(String[] args) throws IllegalAccessException, InvocationTargetException {
        if(args.length == 0) {
            return true;
        }

        String command = args[0];
        String[] commandArgs = new String[args.length -1];
        int i;
        for(i = 1; i < args.length; i++) {
            commandArgs[i - 1] = args[i];
        }
        Method[] methods = this.getClass().getMethods();
        for(i = 0; i < methods.length; i++) {
            if( methods[i].getName().equals( COMMAND_PREFIX + command)) {
                try {
                    methods[i].invoke(this, (Object) commandArgs);
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
        }
        throw new IllegalArgumentException("unknown command: " + command);
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
     * Main cycle of the command line console.
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    protected void mainCycle() throws IllegalAccessException, InvocationTargetException, IOException {
        printHello();
        while (true) {
            String[] arguments = extractArgs( readInput( getPrompt() ) );
            if ( isExit(arguments) && confirmExit() ) {
                System.out.println("Bye");
                System.exit(0);
            }
            try {
                processCommand(arguments);
            } catch (IllegalArgumentException iae) {
                handleIllegalArgumentException(iae);
            } catch (Throwable t) {
                handleGenericException(t);
            }
        }
    }

    private static List<String> commands = new ArrayList();

    /**
     * Extracts arguments from a command line input.
     *
     * @param cl
     * @return the list of arguments.
     */
    protected static String[] extractArgs(String cl) {
        commands.clear();
        boolean insideQuotes = false;
        int begin = 0;
        for(int c = 0; c < cl.length(); c++) {
            if( cl.charAt(c) == '"' ) {
                if(insideQuotes) {
                    insideQuotes = false;
                    if(c - begin > 0) {
                        commands.add(cl.substring(begin, c));
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
                    commands.add( cl.substring(begin, c) );
                }
                begin = c + 1;
            } else if(c == cl.length() - 1 && c - begin > 0) {
                commands.add( cl.substring(begin, c + 1) );
            }
        }
        return commands.toArray(new String[commands.size()]);
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
}
