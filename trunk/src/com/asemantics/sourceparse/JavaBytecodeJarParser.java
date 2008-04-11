package com.asemantics.sourceparse;

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
    public void parseFile(File f) throws IOException {
        if( f == null) {
            throw new NullPointerException("f cannot be null");
        }
        if(! f.exists() ) {
            throw new IllegalArgumentException("Cannot find file: " + f.getAbsolutePath());
        }

        fileParser.initialize( getCodeHandler(), getObjectsTable() );

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
                getCodeHandler().parseError(
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
