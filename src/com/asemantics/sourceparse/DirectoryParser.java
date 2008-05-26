package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
     * Constructor.
     *
     * @param fs
     */
    public DirectoryParser(FileParser fs) {
        dirStack = new Stack<File>();
        fileParser = fs;
    }

    /**
     * Parses a directory content.
     *
     * @param d
     * @return
     */
    public int parseDirectory(String libraryName, File d) {
        dirStack.clear();
        if( d == null || ! d.exists() ||! d.isDirectory() ) {
            throw new IllegalArgumentException();
        }
        dirStack.push(d);

        fileParser.initialize( getCodeHandler(), getObjectsTable() );

        // Begin parsing.
        getCodeHandler().startParsing(libraryName, d.getAbsolutePath());

        try {
            preScan();
            File current;
            while( ! dirStack.isEmpty() ) {
                current = dirStack.pop();
                scanDirectory( current );
            }
        } finally {
            // End parsing.
            getCodeHandler().endParsing();
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

        File[] javaFiles = dir.listFiles( new CoderUtils.JavaSourceFilenameFilter() );
        for(int f = 0; f < javaFiles.length; f++) {
            try {
                fileParser.parse(javaFiles[f]);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } catch (ParserException pe) {
                pe.printStackTrace();
                getCodeHandler().parseError(javaFiles[f].getAbsolutePath(), "[" + pe.getClass().getName() + "]" + pe.getMessage());
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
     * @return
     */
    public int postScan() {
        Set<String> unresolved = getObjectsTable().processTemporaryIdentifiers(getCodeHandler());
        List<String> unresolvedList = new ArrayList(unresolved);
        unresolved.clear();
        Collections.sort(unresolvedList);
        String[] unresolvedTypes = unresolvedList.toArray( new String[unresolvedList.size()] );
        getCodeHandler().unresolvedTypes(unresolvedTypes);
        return unresolved.size();
    }
}
