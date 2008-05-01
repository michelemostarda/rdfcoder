package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class helps the <code>JavaBytecodeFileParser</code>
 * to load inner classes and parse it.
 *
 * @see JavaBytecodeFileParser
 */
public interface JavaBytecodeClassLoader {

    class BaseClassLoader {
        
    }

    public static class JarClassLoader implements JavaBytecodeClassLoader {

        private File file;

        JarFile jarFile;

        public JarClassLoader(File f) {
            if(f == null) {
                throw new NullPointerException("invalid jarFile: cannot be null");
            }
            if(! f.isFile()) {
                throw new NullPointerException("invalid jarFile: expected dir");
            }

            file = f;
        }

        public JavaClass loadClass(String pathToClass) throws IOException {
            if(jarFile == null) {
                jarFile = new JarFile(file);
            }
            JarEntry jarEntry = jarFile.getJarEntry( CoderUtils.fullyQualifiedObjectToArchivePath(pathToClass) );
            if(jarEntry == null) {
                return null;
            }
            InputStream is = jarFile.getInputStream(jarEntry);
            try {
                ClassParser classParser = new ClassParser(is, pathToClass);
                return classParser.parse();
            } finally {
                is.close();
            }
        }

        @Override
        public void finalize() throws IOException {
            if(jarFile != null) {
                jarFile.close();
            }
        }
        
    }

    public static class DirClassLoader implements JavaBytecodeClassLoader {

        private File dir;

        public DirClassLoader(File d) {
            if(d == null) {
                throw new NullPointerException("invalid classes d: cannot be null");
            }
            if(! d.isDirectory()) {
                throw new NullPointerException("invalid d: expected directory");
            }

            dir = d;
        }

        public JavaClass loadClass(String pathToClass) throws IOException {
            File classFile = new File(dir, CoderUtils.fullyQualifiedObjToFilePath(pathToClass) );
            InputStream is = new FileInputStream(classFile);
            try {
                ClassParser classParser = new ClassParser(is, pathToClass);
                return classParser.parse();
            } finally {
                is.close();
            }
        }

    }

    JavaClass loadClass(String pathToClass) throws IOException;

}
