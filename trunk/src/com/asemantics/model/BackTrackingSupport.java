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


 package com.asemantics.model;

/**
 * This class extends <code>CodeHandler</code>
 * to provide a back tracing mechaninm able to
 * accept unresolved types and fix it in a second time.
 */
public interface BackTrackingSupport {

    /**
     * Generates a temporary unique identifier to substitute an unknown type.
     *
     * @return a string representing the identifier.
     */
    public String generateTempUniqueIdentifier();

    /**
     * Replaces the remporary identifier with the qualified type.
     * @param identifier the identifier to replace.
     * @param qualifiedType the qualified type.
     * @return number of effected triples.
     */
    public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType);

}