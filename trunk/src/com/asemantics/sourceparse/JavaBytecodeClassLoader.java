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

    /**
     * Loads a <i>JavaClass</i> object.
     *
     * @param pathToClass
     * @return
     * @throws IOException
     */
     JavaClass loadClass(String pathToClass) throws IOException;

    /**
     * Allows to load a Jar class file.
     */
    public static class JarClassLoader implements JavaBytecodeClassLoader {

        /**
         * Represents the JAR file. 
         */
        private File file;

        /**
         * Represents the jarfile object.
         */
        JarFile jarFile;

        /**
         * Constructor.
         *
         * @param f  path to the JAR file to load.
         */
        public JarClassLoader(File f) {
            if(f == null) {
                throw new NullPointerException("invalid jarFile: cannot be null");
            }
            if(! f.isFile()) {
                throw new NullPointerException("invalid jarFile: expected dir");
            }

            file = f;
        }

        /**
         * Loads a Java class representation on the given path to class.
         *
         * @param pathToClass
         * @return
         * @throws IOException
         */
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

    /**
     * Directory class loader.
     */
    public static class DirClassLoader implements JavaBytecodeClassLoader {

        /**
         * The directory to load.
         */
        private File dir;

        /**
         * Constructor.
         *
         * @param d
         */
        public DirClassLoader(File d) {
            if(d == null) {
                throw new NullPointerException("invalid classes d: cannot be null");
            }
            if(! d.isDirectory()) {
                throw new NullPointerException("invalid d: expected directory");
            }

            dir = d;
        }

        /**
         * Loads a class file into a directory.
         * 
         * @param pathToClass
         * @return
         * @throws IOException
         */
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



}
