package com.asemantics;

import com.asemantics.model.*;
import com.asemantics.modelimpl.JenaCoderFactory;
import com.asemantics.sourceparse.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import jline.*;


/**
 * The Command line EDFCoder utility.
 */
public class CommandLine {

    class ModelHandler {

        CodeModel   codeModel;

        CodeHandler codeHandler;

        ModelHandler(CodeModel cm, CodeHandler ch) {
            codeModel   = cm;
            codeHandler = ch;
        }
    }

    /* Public constants. */

    public static final int VERSION_MAJOR  = 0;

    public static final int VERSION_MINOR  = 6; 

    public static final String COMMAND_PREFIX = "command_";

    public static final String SHORT_COMMAND_DESCRIPTION_PREFIX = "__"  + COMMAND_PREFIX;

    public static final String LONG_COMMAND_DESCRIPTION_PREFIX  = "___" + COMMAND_PREFIX;

    public static final String EXIT_COMMAND = "exit";

    public static final File HISTORY_FILE = new File("/Users/michele/.rdfcoder");

    /* Private constants. */

    private static final String DEFAULT_CODE_MODEL = "default";

    private static final String JAR_EXT = ".jar";

    /* Fields. */

    private CoderFactory coderFactory;

    private Map<String,ModelHandler> modelHandlers;

    private String selectedModel;

    private boolean debug = false;

    private List<String> toBeSaved;

    /* Console readed stuff. */

    private ConsoleReader consoleReader;

    private History history;


    public CommandLine() throws IOException, IllegalAccessException, InvocationTargetException {
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

    private ModelHandler createModelHandler(String modelName) {
        if(modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("a model with name " + modelName + " already exists.");   
        }

        CodeModel cm   = coderFactory.createCodeModel();
        CodeHandler ch = coderFactory.createHandlerOnModel(cm);
        ModelHandler mh = new ModelHandler(cm, ch);
        modelHandlers.put(modelName, mh);
        return mh;
    }

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

    private void clearModelHandler(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        ModelHandler mh = modelHandlers.get(modelName);
        mh.codeModel.clearAll();
    }

    private void setSelectedModel(String modelName) {
        if( ! modelHandlers.containsKey(modelName)) {
            throw new IllegalArgumentException("model with name " + modelName + " doesn't exist.");
        }

        selectedModel = modelName;
    }

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
        QueryResult qr = qcm.performQuery(qry);
        qr.toTabularView(ps);
        qr.close();
    }

    private void performQueryOnModel(String qry, PrintStream ps) {
        performQueryOnModel(selectedModel, qry, ps);
    }

    private CodeHandler getCodeHandler() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.codeHandler;
    }

    private CodeModel getCodeModel() {
        ModelHandler mh = modelHandlers.get( selectedModel );
        return mh.codeModel;
    }

    private void validLibraryName(String libName) {
        // Empty.
    }

    private boolean validJar(File pathToJar) {
        return pathToJar.exists() && pathToJar.isFile() && pathToJar.getName().lastIndexOf(JAR_EXT) == ( pathToJar.length() - JAR_EXT.length() );
    }

    private boolean validSource(File pathToSource) {
        return pathToSource.exists() && pathToSource.isDirectory();
    }

    private enum LibraryType {
        SOURCE_DIR,
        CLASS_DIR,
        JAR_FILE
    }

    private class Library {
        File location;
        LibraryType type;

        Library(File f, LibraryType t) {
            location = f;
            type     = t;
        }
    }

    private void loadLibraries(String[] args, PrintStream ps) throws IOException {
        String libraryName;

        // Validate arguments first.
        List<Library> libraries = new ArrayList<Library>();
        File file;
        for(int i = 0; i < args.length; i+=2) {
            libraryName = args[i];
            validLibraryName(libraryName);
            file = new File(args[i+i]);
            if( validJar(file) ) {
                libraries.add(new Library(file, LibraryType.JAR_FILE));
            } else if ( validSource(file) ) {
                libraries.add(new Library(file, LibraryType.SOURCE_DIR));
            } else {
                throw new IllegalArgumentException("Invalid argument " + args[i + 1]);
            }
        }

        // Process arguments.
        ObjectsTable ot = new ObjectsTable();
        Statistics statistics = new Statistics();
        CodeHandler statisticsCodeHandler = statistics.createStatisticsCodeHandler( getCodeHandler() );

        for(int i = 0; i < args.length; i+=2) {

            libraryName = args[i];
            statisticsCodeHandler.startParsing(libraryName, "location");

            JavaBytecodeJarParser javaBytecodeJarParser = new JavaBytecodeJarParser();
            javaBytecodeJarParser.initialize( statisticsCodeHandler, ot );
            JavaSourceFileParser javaSourceFileParser = new JavaSourceFileParser();
            DirectoryParser directoryParser = new DirectoryParser(javaSourceFileParser);
            directoryParser.initialize( statisticsCodeHandler, ot );

            if( libraries.get(i).type == LibraryType.JAR_FILE ) {
                System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                javaBytecodeJarParser.parseFile(libraries.get(i).location);
                System.out.println(" done");
            } else if( libraries.get(i).type == LibraryType.SOURCE_DIR ) {
                System.out.print("loading " + libraries.get(i).location.getAbsolutePath() + " ...");
                directoryParser.parseDirectory( libraries.get(i).location );
                System.out.println(" done");
            } else {

                throw new IllegalStateException("Invalid argument " + args[i + 1]);
            }

            javaBytecodeJarParser.dispose();
            directoryParser.dispose();
        }

        statisticsCodeHandler.endParsing();

        ot.clear();

        ps.print(statistics.toStringReport());
        statistics.clean();

        toBeSaved.add(selectedModel);
    }

    private void saveModel(Map parameters) throws IOException {
        CodeStorage codeStorage = coderFactory.createCodeStorage();
        codeStorage.saveModel( getCodeModel(), parameters );

        toBeSaved.remove(selectedModel);
    }

    private void loadModel(Map parameters) throws IOException {
        CodeStorage codeStorage = coderFactory.createCodeStorage();
        codeStorage.loadModel( getCodeModel(), parameters );
    }

    private String retrieveParam(String param, String[] args) {
        String target = param + "=";
        for(String arg : args) {
            if( arg.indexOf(target) == 0 ) {
                return arg.substring(target.length());
            }
        }
        return null;
    }

    private class Command {
        private String name;
        private String description;
    }

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

    private Command[] getAvailableCommands() throws IllegalAccessException, InvocationTargetException {
        Method[] methods = this.getClass().getMethods();
        List<Command> commands = new ArrayList();
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

    private String[] getAvailableCommandNames() throws IllegalAccessException, InvocationTargetException {
        Command[] commands = getAvailableCommands();
        String[] commandNames = new String[commands.length];
        for(int i = 0; i < commands.length; i++) {
            commandNames[i] = commands[i].name;
        }
        return commandNames;
    }

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

    /* BEGIN Command line commands. */

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

    /**
     * Expected: load <lib_name> file1 [, file2, ...]
     *
     * @param args
     * @throws IOException
     */
    public void command_loadclasspath(String[] args) throws IOException {
        if( args.length % 2 != 0 ) {
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
                "\n\twhere resource can be a source dir, a classes dir or a jar file." +
                "\n\tPerforms a parsing of the given set of resources" +
                "\n\tand loads them on the curretly selected model";
    }

    private Map fileStorageParams(String[] args) {
        Map parameters = new HashMap();
        String filename = retrieveParam(CodeStorage.FS_FILENAME, args);
        parameters.put(CodeStorage.FS_FILENAME, filename);
        return parameters;
    }

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

    public void command_savemodel(String[] args) throws IOException {
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
                "\n\t possible values for storagename are: " + CodeStorage.STORAGE_FS  + "==filesystem" + CodeStorage.STORAGE_DB + "==database" +       
                "\n\texample:" +
                "\n\tmodel1> savemodel fs filename=/path/to/file.xml";

    }

    public void command_loadmodel(String[] args) throws IOException {
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

    private void processCommand(String[] args) throws IllegalAccessException, InvocationTargetException {
        if(args.length == 0) {
            return;
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
                    } else {
                        throw ite;
                    }
            }
                return;
            }
        }
        throw new IllegalArgumentException("unknown command: " + command);
    }

    private void handleIllegalArgumentException(IllegalArgumentException iae, String cmd) {
        System.out.println("error: " + iae.toString());
        if(debug) { iae.printStackTrace(); }
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

    private void handleIllegalArgumentException(IllegalArgumentException iae) {
        handleIllegalArgumentException(iae, null);
    }

    private void printHello() {
        System.out.println("RDFCoder command line console [version " + VERSION_MAJOR + "." + VERSION_MINOR + "]");
        System.out.println();
    }

//    private static StringBuffer inputBuffer = new StringBuffer();

    private String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        String ret = consoleReader.readLine();
        return ret;

//        inputBuffer.delete(0, inputBuffer.length());
//        System.out.print(prompt);
//        int b;
//        while ((b = System.in.read()) != '\n') {
//            System.out.println("CHAR:" + b);
//            inputBuffer.append((char) b);
//        }
//        return inputBuffer.toString();

    }

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

    private void mainCycle() throws IllegalAccessException, InvocationTargetException, IOException {
        printHello();
        while (true) {
            String[] arguments = extractArgs( readInput(selectedModel + "> ") );
            if ( isExit(arguments) && confirmExit() ) {
                System.out.println("Bye");
                System.exit(0);
            }
            try {
                processCommand(arguments);
            } catch (IllegalArgumentException iae) {
                handleIllegalArgumentException(iae);
            }
        }
    }

    private static List<String> commands = new ArrayList();

    private static String[] extractArgs(String cl) {
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

    private static boolean isExit(String[] args) {
        return args.length > 0 && args[0].equals(EXIT_COMMAND);
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException {
        CommandLine commandLine = new CommandLine();
        commandLine.mainCycle();
    }
}
