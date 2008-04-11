package com.asemantics;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Utility class.
 */
public class CoderUtils {

    public static final String PACKAGE_SEPARATOR = ".";

    public static final String ARCHIVE_PATH_SEPARATOR = "/";

    public static final String JAVA_SOURCE_FILE_EXT  = ".java";

    public static final String JAVA_SOURCE_CLASS_EXT = ".class";

    public static class JavaSourceFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() || file.isHidden() ) { return false; }
            String postfix = JAVA_SOURCE_FILE_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
        }
    }

    public static class JavaClassFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if( file.isDirectory() || file.isHidden() ) { return false; }
            String postfix = JAVA_SOURCE_CLASS_EXT;
            int lastIndexOf = name.lastIndexOf(postfix);
            return lastIndexOf > 0 && lastIndexOf == name.length() - postfix.length();
        }
    }

    private static StringBuffer join_sb = new StringBuffer();

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
