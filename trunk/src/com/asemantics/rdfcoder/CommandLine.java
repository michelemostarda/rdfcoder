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

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


/**
 * The Command line <i>RDFCoder</i> utility.
 */
public class CommandLine extends AbstractCommandLine {

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
    throws IOException, IllegalAccessException, InvocationTargetException {
        super(file);
    }

    /**
     * Command to enable / disable the debug flag.
     *
     * @param args
     */
    public void command_debug(String[] args) {
        boolean debug = isDebug();
        if( args.length == 0 ) {
            System.out.println("debug: " + debug);
        } else if(args.length == 1 && "true".equals(args[0]) ) {
            debug = true;
        }  else if(args.length == 1 && "false".equals(args[0]) ) {
            debug = false;
        } else {
            throw new IllegalArgumentException("invalid argument");
        }
        setDebug(debug);
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
        System.out.println( getCurrentDirectory().getAbsolutePath() );
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

    private void lsDir(File target) {
        if( ! target.exists() ) {
            System.err.println( String.format("Cannot find dir '%s'", target.getAbsolutePath()) );
            return;
        }
        if( target.isFile() ) {
            System.out.println( target.getAbsolutePath() );
            System.out.println();
            return;
        }
        File[] content = target.listFiles();
        FilePermission fp;
        System.out.println( target.getAbsolutePath() );
        System.out.println();
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
        System.out.println();
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
//        removeModelHandler(modelName);
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
//        clearModelHandler(modelName);
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
            System.out.println("selected model: " + getSelectedModel());
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
        final String selectedModel = getSelectedModel();
        for(String mhn : getModelHandlerNames()) {
            System.out.println("\t" + mhn + (mhn.equals(selectedModel) ? "[X]" : "") );
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
        return "allows to query the current model";
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

}
