/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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

import com.asemantics.rdfcoder.profile.ProfileException;
import com.asemantics.rdfcoder.storage.CodeStorage;
import com.asemantics.rdfcoder.storage.CodeStorageException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.FileNameCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The Command line <i>RDFCoder</i> utility.
 */
public class CommandLine extends AbstractCommandLine {

    private final ModelNameCompleter modelNameCompleter = new ModelNameCompleter(this);

    /**
     * Constructor.
     *
     * @param file the initial location.
     * @throws java.io.IOException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     *
     */
    public CommandLine(File file)
    throws IOException, IllegalAccessException, InvocationTargetException, ProfileException {
        super(file);
    }

    /**
     * Command to enable / disable the debug flag.
     *
     * @param args
     */
    public void command_debug(String[] args) {
        if( args.length == 0 ) {
            println("debug: " + isDebug());
        } else if(args.length == 1 && "true".equals(args[0]) ) {
            setDebug(true);
        }  else if(args.length == 1 && "false".equals(args[0]) ) {
            setDebug(false);
        } else {
            throw new IllegalArgumentException("invalid argument");
        }
    }

    public String __command_debug() {
        return "Show/set the debug flag";
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
        final String wd = getCurrentDirectory().getAbsolutePath();
        if (getOutputType() == OutputType.JSON) {
            try {
                final JsonGenerator generator = getOutJSONGenerator();
                generator.writeStartObject();
                generator.writeFieldName("operation");
                generator.writeObject("pwd");
                generator.writeFieldName("result");
                generator.writeObject(wd);
                generator.writeEndObject();
                generator.flush();
                println();
            } catch (IOException ioe) {
                throw new RuntimeException("Error while generating JSON output.", ioe);
            }
        } else if (getOutputType() == OutputType.TEXT) {
            println(wd);
        } else {
            throw new IllegalStateException();
        }
    }

    public String __command_pwd() {
        return "Print the current directory";
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
        return "Change the current directory";
    }

    public String ___command_cd() {
        return
                __command_cd() +
                "\n\tsyntax: cd <path to new location>";
    }

    private String rewriteActions(File f) {
        StringBuilder sb = new StringBuilder();
        if(f.canRead()) { sb.append("r"); }
        if(f.canWrite()) { sb.append("w"); }
        if(f.canExecute()) { sb.append("x"); }
        return sb.toString();
    }

    private void lsDir(File target) {
        if( ! target.exists() ) {
            System.err.println( String.format("Cannot find dir '%s'", target.getAbsolutePath()) );
            return;
        }
        if( target.isFile() ) {
            println(target.getAbsolutePath());
            return;
        }
        File[] content = target.listFiles();
        if(getOutputType() == OutputType.JSON) {
            try {
                final JsonGenerator generator = getOutJSONGenerator();
                generator.writeStartObject();
                generator.writeFieldName("operation");
                generator.writeObject("ls");
                generator.writeFieldName("path");
                generator.writeObject(target.getAbsolutePath());
                generator.writeFieldName("result");
                generator.writeStartArray();
                for (int i = 0; i < content.length; i++) {
                    File f = content[i];
                    generator.writeStartObject();
                    generator.writeFieldName("name");
                    generator.writeObject(f.getName());
                    generator.writeFieldName("is_dir");
                    generator.writeObject(f.isDirectory());
                    generator.writeFieldName("actions");
                    generator.writeObject(rewriteActions(f));
                    generator.writeFieldName("size");
                    generator.writeObject(f.length());
                    generator.writeEndObject();
                }
                generator.writeEndArray();
                generator.writeEndObject();
                generator.flush();
                println();
            } catch (IOException ioe) {
                throw new RuntimeException("Error while generating JSON output.", ioe);
            }
        } else {
            println( target.getAbsolutePath() );
            final int lastNLIndex = content.length - 1;
            StringBuilder sb = new StringBuilder();
            int fileNameMaxLen = 0;
            for (int i = 0; i < content.length; i++) {
                File f = content[i];
                int len = f.getName().length();
                if(len > fileNameMaxLen) fileNameMaxLen = len;
            }
            fileNameMaxLen += 4;
            for (int i = 0; i < content.length; i++) {
                File f = content[i];
                sb.append(String.format(
                        "%s\t%-" + fileNameMaxLen + "s%s\t%d",
                        (f.isDirectory() ? "d" : "-"),
                        f.getName(),
                        rewriteActions(f),
                        f.length()
                ));
                if (i < lastNLIndex) sb.append('\n');
            }
            println(sb.toString());
        }
    }

    /**
     * Lists the content of the current directory.
     */
    public void command_ls(String args[]) {
        if(args.length == 0) {
            lsDir( getCurrentDirectory() );
        } else {
            for(String arg : args) {
                lsDir( toAbsolutePath(arg) );
            }
        }
    }

    public String __command_ls() {
        return "List content of current directory";
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
    public void command_newmodel(String[] args) throws ProfileException {
        if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String modelName = args[0];
        createModelHandler(modelName);
        reportBinaryCommand("new_model", true, String.format("Model '%s' created.", modelName));
    }

    public String __command_newmodel() {
       return "Create a new model";
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
        if (args.length != 1) {
            throw new IllegalArgumentException("a model name must be specified");
        }
        String modelName = args[0];
        final String msg;
        final boolean success = removeModelHandler(modelName);
        if(success) {
            msg = String.format("Model '%s' deleted.", modelName);
        } else {
            msg = String.format("Model '%s' cannot be deleted.", modelName);
        }
        reportBinaryCommand("remove_model", success, msg);
    }

    public String __command_removemodel() {
       return "Remove the current model";
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
        reportBinaryCommand("clear_model", true, String.format("Model '%s' cleaned up.", modelName));
    }

     public String __command_clearmodel() {
        return "Cleanup the current model";
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
            println("selected model: " + getSelectedModel());
        } else if( args.length == 1 ) {
            String modelName = args[0];
            setSelectedModel(modelName);
            reportBinaryCommand("set_model", true, String.format("Model set to '%s'", modelName));
        } else {
            throw new IllegalArgumentException("Expected model name");
        }
    }

    public String __command_setmodel() {
        return "Set a new model";
    }

    public String ___command_setmodel() {
        return
                __command_setmodel() +
                "\nsyntax: setmodel <model_name>" +
                "\n\tsets the model as the default one, osed for every subsequent" +
                "\n\toperation where the model is not specified";
    }

    /**
     * Command to save a model on the storage specified with parameters.
     *
     * @param args
     */
    public void command_savemodel(String[] args) throws CodeStorageException {
        if(args.length < 1) {
            throw new IllegalArgumentException("at least storage name must be specified");
        }

        Map<String,String> parameters;
        if(CodeStorage.STORAGE_FS.equals(args[0])) {
            parameters = fileStorageParams(args);
        } else {
            parameters = databaseStorageParams(args);
        }
        saveModel(parameters);
        reportBinaryCommand("save_model", true,
                String.format("Model '%s' cleaned up.", "Model saved with parameters " + parameters.toString())
        );
    }

    public String __command_savemodel() {
        return "Save the current model on file system";
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
        reportBinaryCommand("load_model", true, "Model loaded.");
    }

    public String __command_loadmodel() {
        return "Load a model from file system";
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
            printUsage();
            return;
        }
        if(args.length == 1) {
            String longHelp = getLongCommandDescription(args[0]);
            if(longHelp == null) {
                println("cannot find help for command '" + args[0] + "'");
            } else {
                println(longHelp);
            }
            return;
        }
        println("invalid command");
        println( __command_help() );
    }

    public String __command_help() {
        return "Get help. For info about a command type: help <command>";
    }

    public String ___command_help() {
        return
                "prints the usage help of a specific command." +
                "\n\tsyntax: help <command>";
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
        println("Models:");
        final String selectedModel = getSelectedModel();
        if(getOutputType() == OutputType.TEXT) {
            for (String mhn : getModelHandlerNames()) {
                println("\t" + mhn + (mhn.equals(selectedModel) ? "[X]" : ""));
            }
        } else if(getOutputType() == OutputType.JSON){
            try {
                final JsonGenerator generator = getOutJSONGenerator();
                generator.writeStartObject();
                generator.writeFieldName("operation");
                generator.writeObject("list_models");
                generator.writeFieldName("result");
                generator.writeStartArray();
                for (String mhn : getModelHandlerNames()) {
                    generator.writeStartObject();
                    generator.writeFieldName("model");
                    generator.writeObject(mhn);
                    generator.writeFieldName("active");
                    generator.writeObject(mhn.equals(selectedModel));
                    generator.writeEndObject();
                }
                generator.writeEndArray();
                generator.writeEndObject();
                generator.flush();
                println();
            } catch (IOException ioe) {
                throw new RuntimeException("Error while generating JSON output.", ioe);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public String __command_list() {
        return "List the loaded models";
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
        performQueryOnModel(qry);
    }

    public String __command_querymodel() {
        return "Query the active model";
    }

    public String ___command_querymodel() {
        return
                __command_querymodel() +
                "\nsyntax: querymodel <SPARQL_query>" +
                "\n\tperforms the given query on the current query model.";
    }

    public void command_inspect(String[] args) {
       if( args.length != 1 ) {
            throw new IllegalArgumentException("Missing arguments");
        }
        String qry = args[0];
        inspectModel(qry);
    }

    public String __command_inspect() {
        return "Inspect the active model";
    }

    public String ___command_inspect() {
        return
                __command_inspect() +
                "\nsyntax: inspect <inspection_query>" +
                "\n\tperforms an inspection on the current query model." +
                String.format("\n\tThe model root object name is %s.", INSPECTION_MODEL_NAME) +
                "\n\texample:" +
                String.format("\n\tinspect %s.asset", INSPECTION_MODEL_NAME);        
    }

    public void command_describe(String[] args) {
       if( args.length != 1 ) {
            throw new IllegalArgumentException();
        }
        String qry = args[0];
        describeModel(qry, System.out);
    }

    public String __command_describe() {
        return "Describe the object referenced by the given path";
    }

    public String ___command_describe() {
        return
                __command_describe() +
                "\nsyntax: describe <inspection_query>" +
                "\n\tdescribes the object targeted by the given inspection query." +
                String.format("\n\tThe model root object name is %s.", INSPECTION_MODEL_NAME) +
                "\n\texample:" +
                String.format("\n\tdescribe %s.asset", INSPECTION_MODEL_NAME);          

    }

    /**
     * Command to load a classpath on the active model.
     *
     * Expected: load &lt;lib_name&gt; file1 [, file2, ...]
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
        return "Load a classpath on the active model";
    }

    public String ___command_loadclasspath() {
        return
                __command_loadclasspath() +
                "\nsyntax: loadclasspath [<library_name> <library_location>]+" +
                "\n\twhere <library_location> can be expressed as" +
                "\n\t\ta jar    file:  jar:/path/to/jarfile.jar" +
                "\n\t\ta source  dir:  src:/path/to/src" +
                "\n\t\ta javadoc dir:  javadoc:/path/to/src" +
                "\n\t\ta class   dir:  class:/path/to/class" +
                "\n" +
                "\n\tPerforms a parsing of the given set of resources" +
                "\n\tand loads extracted data within the current model";
    }

    public void command_loadjre(String[] args) throws IOException{
        getJavaProfile().loadJREModel();
    }

    public String __command_loadjre() { return "Load the JRE data into the active model"; }

    public String ___command_loadjre() {
        return
                __command_loadjre() +
                "\nsyntax: loadjre" +
                "\n" +
                "\n\tThis can take minutes to complete.";
    }

    protected Completer configureCommandCompletors() {
        // cd completer.
        final ArgumentCompleter cdCompleter = new ArgumentCompleter(
                new StringsCompleter("cd"),
                new FileNameCompleter(),
                new NullCompleter()
        );

        // debug completer.
        final ArgumentCompleter debugCompleter = new ArgumentCompleter(
                new StringsCompleter("debug"),
                new StringsCompleter("true", "false"),
                new NullCompleter()
        );

        // describe completer.
        final ArgumentCompleter describeCompleter = new ArgumentCompleter(
                new StringsCompleter("describe"),
                new StringsCompleter("model.asset", "model.libraries"),
                new NullCompleter()
        );

        // help completer.
        final ArgumentCompleter helpCompletor = new ArgumentCompleter(
                new StringsCompleter("help"),
                new StringsCompleter(getCommandNames()),
                new NullCompleter()
        );

        // loadclasspath completer.
        final ArgumentCompleter loadClasspathCompleter = new ArgumentCompleter(
                new StringsCompleter("loadclasspath"),
                new StringsCompleter("<modelname>"),
                new FileNameCompleter(),
                new NullCompleter()
        );

        // ls completer.
        final ArgumentCompleter lsCompleter = new ArgumentCompleter(
                new StringsCompleter("ls"),
                new FileNameCompleter(),
                new NullCompleter()
        );

        // newmodel completer.
        final ArgumentCompleter newmodelCompleter = new ArgumentCompleter(
                new StringsCompleter("newmodel"),
                new StringsCompleter("<modelname>"),
                new NullCompleter()
        );

        // querymodel completer.
        final ArgumentCompleter querymodelCompleter = new ArgumentCompleter(
                new StringsCompleter("querymodel"),
                new StringsCompleter("<SPARQL query>", "\"SELECT * WHERE {?s ?p ?o}\""),
                new NullCompleter()
        );

        // removemodel completer.
        final ArgumentCompleter removemodelCompleter = new ArgumentCompleter(
                new StringsCompleter("removemodel"),
                modelNameCompleter,
                new NullCompleter()
        );

        // setmodel completer.
        final ArgumentCompleter setmodelCompleter = new ArgumentCompleter(
                new StringsCompleter("setmodel"),
                modelNameCompleter,
                new NullCompleter()
        );

        // Generic completer.
        final ArgumentCompleter completer = new ArgumentCompleter(
                new StringsCompleter(getCommandNames()),
                new NullCompleter()
        );

        return new AggregateCompleter(
                cdCompleter,
                debugCompleter,
                describeCompleter,
                helpCompletor,
                loadClasspathCompleter,
                lsCompleter,
                newmodelCompleter,
                querymodelCompleter,
                removemodelCompleter,
                setmodelCompleter,
                completer
                );
    }

    private void reportBinaryCommand(String command, boolean success, String msg) {
        if (getOutputType() == OutputType.JSON) {
            try {
                final JsonGenerator generator = getOutJSONGenerator();
                generator.writeStartObject();
                generator.writeFieldName("operation");
                generator.writeObject(command);
                generator.writeFieldName("success");
                generator.writeObject(success);
                generator.writeEndObject();
                generator.flush();
                println();
            } catch (IOException ioe) {
                throw new RuntimeException("Error while generating JSON output.", ioe);
            }
        } else if (getOutputType() == OutputType.TEXT) {
            println(msg);
        } else {
            throw new IllegalStateException();
        }
    }

    class ModelNameCompleter implements Completer {

        private CommandLine cl;

        ModelNameCompleter(CommandLine cl) {
            this.cl = cl;
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            final List<Candidate> modelCandidates = new ArrayList<>();
            for(String modelName : cl.getModelHandlerNames()) {
                modelCandidates.add(new Candidate(modelName));
            }
            candidates.addAll(modelCandidates);
        }
    }

}
