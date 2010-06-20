package com.asemantics.rdfcoder.sourceparse.javadoc;

/**
 * Any error raised by {@link com.asemantics.rdfcoder.sourceparse.javadoc.JavadocHandlerSerializer}.
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
