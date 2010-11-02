package com.asemantics.rdfcoder.parser.javadoc;

/**
 * Any error raised by {@link com.asemantics.rdfcoder.parser.javadoc.JavadocHandlerSerializer}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocHandlerSerializerException extends Exception {

    public JavadocHandlerSerializerException(String message) {
        super(message);
    }

    public JavadocHandlerSerializerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
