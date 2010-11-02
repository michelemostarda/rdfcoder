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


package com.asemantics.rdfcoder.sourceparse.bytecode;

import com.asemantics.rdfcoder.sourceparse.CodeParser;
import com.asemantics.rdfcoder.sourceparse.ParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class provides methods to process the content of a Jar file.
 */
public class JavaBytecodeJarParser extends CodeParser {

    /**
     * The internal file parser.
     */
    JavaBytecodeFileParser fileParser;

    /**
     * Constructor.
     * @param jbcfp the file parser to use to parse every single class.
     */
    public JavaBytecodeJarParser(JavaBytecodeFileParser jbcfp) {
        if(jbcfp == null) {
            throw new NullPointerException();
        }
        fileParser = jbcfp;
    }

    /**
     * Constructor.
     */
    public JavaBytecodeJarParser() {
        this( new JavaBytecodeFileParser() );
    }

    /**
     * Parses the entire content of a JAR file.
     * @param f
     * @throws IOException
     */
    public void parseFile(File f) throws IOException, ParserException {
        if( f == null) {
            throw new NullPointerException("f cannot be null");
        }
        if(! f.exists() ) {
            throw new IllegalArgumentException("Cannot find file: " + f.getAbsolutePath());
        }

        try {
            fileParser.initialize( getParseHandler(), getObjectsTable() );
        } catch (Throwable t) {
            t.printStackTrace();
        }

        JarFile jarFile = new JarFile(f);
        Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry entry = null;
        InputStream inputStream = null;
        JavaBytecodeClassLoader.JarClassLoader jarClassLoader = new JavaBytecodeClassLoader.JarClassLoader(f);
        while(entries.hasMoreElements()) {
            try {
                entry = entries.nextElement();
                if( ! isEntryClass(entry) ) { continue; }
                inputStream = jarFile.getInputStream(entry);
                fileParser.parse(jarClassLoader, inputStream, entry.getName());
            } catch(IOException ioe) {
                getParseHandler().parseError(
                        entry.getName(), "[" + entry.getClass().getName() + "]: " + ioe.getMessage()
                );
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                }
            }
        }
        jarFile.close();

        fileParser.dispose();
    }

    private boolean isEntryClass(JarEntry entry) {
        String classExt = ".class";
        String name = entry.getName();
        return name.indexOf(classExt) ==  name.length() - classExt.length();
    }

}
