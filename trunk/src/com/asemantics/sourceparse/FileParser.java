package com.asemantics.sourceparse;

import java.io.File;
import java.io.IOException;

/**
 * Interface for every file parser.
 */
public abstract class FileParser extends CodeParser {

    /**
     * Parses the given file.
     *
     * @param file
     * @throws ParserException if any compilation error occurs.
     * @throws IOException if any error occurs during file access.
     */
    public abstract void parse(File file) throws ParserException, IOException;


}
