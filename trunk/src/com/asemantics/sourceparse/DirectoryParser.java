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


package com.asemantics.sourceparse;

import com.asemantics.model.CodeHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Scans a given directory.
 */
public class DirectoryParser extends CodeParser {

    /**
     * Directory stack.
     */
    private Stack<File> dirStack;

    /**
     * File parser.
     */
    private FileParser fileParser;

    /**
     * Filename filter.
     */
    private FilenameFilter filenameFilter;

    /**
     * Constructor.
     *
     * @param fs
     */
    public DirectoryParser(FileParser fs, FilenameFilter ff) {
        dirStack = new Stack<File>();
        fileParser = fs;
        filenameFilter = ff;
    }

    /**
     * Parses a directory content.
     *
     * @param d directory in which the library is located.
     * @return the number of unresolved objects.
     */
    public int parseDirectory(String libraryName, File d) {
        dirStack.clear();
        if( d == null || ! d.exists() ||! d.isDirectory() ) {
            throw new IllegalArgumentException();
        }
        dirStack.push(d);

        fileParser.initialize( getParseHandler(), getObjectsTable() );

        // Begin parsing.
        try {
            getParseHandler().startParsing(libraryName, d.getAbsolutePath());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {
            preScan();
            File current;
            while( ! dirStack.isEmpty() ) {
                current = dirStack.pop();
                scanDirectory( current );
            }
        } finally {
            // End parsing.
            try {
                getParseHandler().endParsing();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }


        int unresolved = postScan();
        fileParser.dispose();
        return unresolved;
    }

    /**
     * Low level scan operation.
     * @param dir
     */
    protected void scanDirectory(File dir) {

        File[] content = dir.listFiles();
        for(File f : content) {
            if(f.isDirectory() && ! f.isHidden() ) {
                dirStack.push(f);
            }
        }

        File[] javaFiles = dir.listFiles( filenameFilter );
        for(int f = 0; f < javaFiles.length; f++) {
            try {
                fileParser.parse(javaFiles[f]);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } catch (ParserException pe) {
                pe.printStackTrace();
                getParseHandler().parseError(javaFiles[f].getAbsolutePath(), "[" + pe.getClass().getName() + "]" + pe.getMessage());
            }
        }
    }

    /**
     * Pre scan operation handler.
     */
    public void preScan() {
        // Empty.
    }

    /**
     * Post scan operation handler.
     * 
     * @return number of unresolved objects.
     */
    public int postScan() {
        CodeHandler codeHandler = (CodeHandler) getParseHandler();
        Set<String> unresolved = getObjectsTable().processTemporaryIdentifiers( codeHandler );
        List<String> unresolvedList = new ArrayList(unresolved);
        unresolved.clear();
        Collections.sort(unresolvedList);
        String[] unresolvedTypes = unresolvedList.toArray( new String[unresolvedList.size()] );
        try {
            codeHandler.unresolvedTypes(unresolvedTypes);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return unresolved.size();
    }
}
