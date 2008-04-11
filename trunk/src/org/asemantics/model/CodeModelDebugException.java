package com.asemantics.model;

/**
 * Represents any exception related to the <code>CodeModel</code> and raised by a debug
 * control.
 */
public class CodeModelDebugException extends RuntimeException {


    public CodeModelDebugException() {
        super();
    }

    public CodeModelDebugException(String message) {
        super(message);
    }

    public CodeModelDebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeModelDebugException(Throwable cause) {
        super(cause);
    }
}
