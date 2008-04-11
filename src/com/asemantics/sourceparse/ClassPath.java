package com.asemantics.sourceparse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ClassPath</code> class represents the class path of a parse Asset (a group of related libraries)
 * to be parsed in block.
 */
public class ClassPath {

    /**
     * List of source directories (.java)
     */
    protected List<File> sourceDirs;

    /**
     * List of compiled archives (.jar)
     */
    protected List<File> jarFiles;

    /**
     * List of class directories (.class)
     */
    protected List<File> classDirs;

    public ClassPath() {
        sourceDirs = new ArrayList<File>();
        jarFiles = new ArrayList<File>();
        classDirs = new ArrayList<File>();
    }

    public ClassPath addSourceDir(String sourceDir) {
        File f = new File(sourceDir);
        if( ! f.exists() || !f.isDirectory() ) {
            throw new IllegalArgumentException("invalid sourceDir: " + sourceDir);
        }
        sourceDirs.add(f);
        return this;
    }

     public ClassPath addJar(String jar) {
        File f = new File(jar);
        if( ! f.exists() || !f.isFile() ) {
            throw new IllegalArgumentException("invalid jar: " + jar);
        }
        jarFiles.add(f);
        return this;
    }

    public ClassPath addClassDir(String classDir) {
        File f = new File(classDir);
        if( ! f.exists() || !f.isDirectory() ) {
            throw new IllegalArgumentException("invalid classDir: " + classDir);
        }
        classDirs.add(f);
        return this;
    }

    public File[] getSourceDirs() {
        return sourceDirs.toArray( new File[sourceDirs.size()] );
    }

    public File[] getClassDirs() {
        return classDirs.toArray( new File[classDirs.size()] );
    }

    public File[] getJarFiles() {
        return jarFiles.toArray( new File[jarFiles.size()] );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Source dirs {\n");
        for(File source : sourceDirs) {
            sb.append(source.getAbsolutePath()).append("\n");
        }
        sb.append("}\n");

        sb.append("Jars {\n");
        for(File jar : jarFiles) {
            sb.append(jar.getAbsolutePath()).append("\n");
        }
        sb.append("}\n");

        sb.append("Class dirs {\n");
        for(File classDir : classDirs) {
            sb.append(classDir.getAbsolutePath()).append("\n");
        }
        sb.append("}\n");

        return sb.toString();
    }

}
