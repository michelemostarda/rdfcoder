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

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * This class contains some general utility methods.
 */
public class CoderUtils {

    /**
     * Defines the char separator in a package path.
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
     * Defines the char sepatator in an archive path.
     */
    public static final String ARCHIVE_PATH_SEPARATOR = "/";

    /**
     * Java source extension.
     */
    public static final String JAVA_SOURCE_FILE_EXT  = ".java";

    /**
     * Java class extension.
     */
    public static final String JAVA_SOURCE_CLASS_EXT = ".class";

    /**
     * Java JAR archive extension.
     */
    public static final String JAVA_JAR_EXT = ".jar";

    /**
     * Filters all java source files inside a dir.
     */
    public static class JavaSourceFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() || file.isHidden() ) { return false; }
            String postfix = JAVA_SOURCE_FILE_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
        }
    }

    /**
     * Filters all java class files inside a dir.
     */
    public static class JavaClassFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() || file.isHidden() ) { return false; }
            String postfix = JAVA_SOURCE_CLASS_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
        }
    }

    /**
     * Filters all jar archives inside a dir.
     */
    public static class JavaJarFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() || file.isHidden() ) { return false; }
            String postfix = JAVA_JAR_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
        }
    }

    /**
     * join method string builder.
     */
    private static StringBuilder join_sb = new StringBuilder();

    public static String join(List list, String separator) {
        join_sb.delete(0, join_sb.length());
        for(int i = 0; i < list.size(); i++) {
            join_sb.append(list.get(i));
            if(i < list.size() -1 ) {
                join_sb.append(separator);
            }
        }
        return join_sb.toString();
    }

    public static String fullyQualifiedObjectToArchivePath(String op) {
        return op.replaceAll("\\" + PACKAGE_SEPARATOR, ARCHIVE_PATH_SEPARATOR) + JAVA_SOURCE_CLASS_EXT;
    }

    public static String fullyQualifiedObjToFilePath(String op) {
        return op.replaceAll(PACKAGE_SEPARATOR, File.pathSeparator);
    }

}
