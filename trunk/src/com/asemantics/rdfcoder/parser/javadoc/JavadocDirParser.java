package com.asemantics.rdfcoder.parser.javadoc;

import com.asemantics.rdfcoder.model.java.JavadocHandler;
import com.asemantics.rdfcoder.parser.CodeParser;
import com.sun.tools.javadoc.Main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of {@link com.asemantics.rdfcoder.parser.CodeParser} for handling javadoc.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 * TODO: Integrate in command line.
 */
public class JavadocDirParser extends CodeParser {

    private JavadocHandler javadocHandler;

    private ByteArrayOutputStream errBuffer    = new ByteArrayOutputStream();
    private ByteArrayOutputStream warnBuffer   = new ByteArrayOutputStream();
    private ByteArrayOutputStream noticeBuffer = new ByteArrayOutputStream();

    public JavadocDirParser(JavadocHandler jh){
        if(jh == null) {
            throw new NullPointerException("javadocHandler cannot be null.");
        }
        javadocHandler = jh;
    }

    public JavadocHandler getJavadocHandler() {
        return javadocHandler;
    }

    public String getErrBuffer() {
        return errBuffer.toString();
    }

    public String getWarnBuffer() {
        return warnBuffer.toString();
    }

    public String getNoticeBuffer() {
        return noticeBuffer.toString();
    }

    public void parseSourceDir(File srcDir) throws JavadocDirParserException {
        if(srcDir == null) {
            throw new NullPointerException("The source dir cannot be null.");
        }
        if( ! srcDir.exists() ) {
            throw new JavadocDirParserException("Cannot find source dir.");
        }
        try {
            File serializationFile = executeDoclet(srcDir);
            JavadocHandlerSerializer handlerSerializer = new JavadocHandlerSerializer();
            handlerSerializer.deserialize(serializationFile, getJavadocHandler());
        } catch (Exception e) {
            throw new JavadocDirParserException("An error occurred during the Javadoc parsing.", e);
        }
    }

    private File executeDoclet(File target) throws JavadocDirParserException {
        File serializationFile;
        try {
            serializationFile = File.createTempFile("rdfcoder", "doclet-serfile");
        } catch (IOException ioe) {
            throw new JavadocDirParserException("Error while creating temporary file.", ioe);
        }

        errBuffer.reset();
        warnBuffer.reset();
        noticeBuffer.reset();

        int exitCode = Main.execute(
                "javadoc-handler-doclet",
                new PrintWriter(errBuffer),
                new PrintWriter(warnBuffer),
                new PrintWriter(noticeBuffer),
                JavadocHandlerDoclet.class.getName(),
                new String[]{
                        JavadocHandlerDoclet.SERIALIZATION_FILE_OPTION,
                        serializationFile.getAbsolutePath(),
                        "-private",
                        "-sourcepath",
                        target.getAbsolutePath(),
                        "-subpackages",
                        getSupPackages(target)
                }
        );


        if(exitCode != 0) {
            throw new JavadocDirParserException(
                    String.format("javadoc ended with unexpected exit code %d", exitCode)
            );
        }
        return serializationFile;
    }

    private String getSupPackages(File srcDir) {
        File[] dirs = srcDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                final File currDir = new File(dir, name);
                return currDir.isDirectory() && ! currDir.isHidden();
            }
        });
        StringBuilder sb = new StringBuilder();
        final int dirsLengthMin2 = dirs.length - 2;
        final String fileSeparator = System.getProperty("file.separator");
        for(int i = 0; i < dirs.length; i++) {
            sb.append(dirs[i].getName());
            if( i < dirsLengthMin2) {
                sb.append(fileSeparator);
            }
        }
        return sb.toString();
    }

}
