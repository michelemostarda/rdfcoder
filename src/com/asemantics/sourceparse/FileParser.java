/**
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
