package com.asemantics.rdfcoder.parser.javadoc;

/**
 * Describes any error occurring in {@link com.asemantics.rdfcoder.parser.javadoc.JavadocHandlerDoclet}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocHandlerDocletException extends RuntimeException {

    public JavadocHandlerDocletException(String message) {
        super(message);
    }

    public JavadocHandlerDocletException(String s, Exception e) {
        super(s, e);
    }
    
}
