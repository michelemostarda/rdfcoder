package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Scans a given directory.
 */
public class DirectoryParser extends CodeParser {

    private Stack<File> dirStack;

    private FileParser fileParser;

    public DirectoryParser(FileParser fs) {
        dirStack = new Stack<File>();
        fileParser = fs;
    }

    public int parseDirectory(File d) {
        dirStack.clear();
        if( d == null || ! d.isDirectory() ) {
            throw new IllegalArgumentException();
        }
        dirStack.push(d);

        fileParser.initialize( getCodeHandler(), getObjectsTable() );

        preScan();

        File current;
        while( ! dirStack.isEmpty() ) {
            current = dirStack.pop();
            scanDirectory( current );
        }

        int unresolved = postScan();
        fileParser.dispose();
        return unresolved;
    }

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

    public void preScan() {
        // Empty.
    }

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
