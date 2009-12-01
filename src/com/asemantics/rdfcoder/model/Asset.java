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


package com.asemantics.rdfcoder.model;

import java.util.Date;

/**
 * An asset represents a logic set of related libraries.
 */
public interface Asset {

    /**
     * Retuns the list of libraries within the asset.
     *
     * @return the list of library names constituting the asset.
     */
    public String[] getLibraries();

    /**
     * Returns the location path of a library.
     *
     * @param library library name.
     * @return the location of a library.
     */
    public String getLibraryLocation(String library);

    /**
     * Returns the date of parsing of a library.
     *
     * @param library library name.
     * @return date of parsing of the library.
     */
    public Date getLibraryDateTime(String library);

}
