package com.asemantics.rdfcoder.parser.javadoc;

/**
 * Defines any error raised by {@link com.asemantics.rdfcoder.parser.javadoc.JavadocDirParser}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocDirParserException extends Exception {

    public JavadocDirParserException(String message) {
        super(message);
    }

    public JavadocDirParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
