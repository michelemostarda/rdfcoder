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

import com.asemantics.rdfcoder.model.java.JavaCodeHandler;

import java.io.Serializable;

/**
 * Represents the fragment of an {@link com.asemantics.rdfcoder.model.Identifier}
 * of a resource.
 *
 * @see com.asemantics.rdfcoder.model.Identifier
 * @see com.asemantics.rdfcoder.model.IdentifierBuilder
 * @see com.asemantics.rdfcoder.model.IdentifierReader
 * @version $Id$
 */
//TODO Rename as section so to be armonized with the documentation.
public class IdentifierFragment implements Serializable {

    /**
     * Fragment.
     */
    private String fragment;

    /**
     * Fragment qualifier.
     */
    private String qualifier;

    /**
     * Constructor.
     * 
     * @param f
     * @param q
     */
    protected IdentifierFragment(String f, String q) {
        if (f == null) {
            throw new NullPointerException("Fragment cannot be null");
        }
        //if (f.length() == 0) {
        //    throw new IllegalArgumentException("Invalid 0 length fragment.");
        //}
        if (q == null) {
            throw new NullPointerException("Qualifier cannot be null.");
        }
        if (q.length() == 0) {
            throw new IllegalArgumentException("Invalid 0 length qualifier.");
        }
        if(f.contains(JavaCodeHandler.PACKAGE_SEPARATOR)) {
            throw new IllegalArgumentException("The fragment cannot contain a package separator.");
        }
        if(q.contains(JavaCodeHandler.PACKAGE_SEPARATOR)) {
            throw new IllegalArgumentException("The qualifier cannot contain a package separator.");
        }
        if(f.contains(Identifier.QUALIFIER_SEPARATOR)) {
            throw new IllegalArgumentException("The fragment cannot contain a qualifier separator.");
        }
        if(q.contains(Identifier.QUALIFIER_SEPARATOR)) {
            throw new IllegalArgumentException("The qualifier cannot contain a qualifier separator.");
        }
        fragment  = f;
        qualifier = q;
    }

    public String getFragment() {
        return fragment;
    }

    public String getQualifier() {
        return qualifier;
    }

    @Override
    public int hashCode() {
        return fragment.hashCode() * 2 * qualifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof IdentifierFragment) {
            IdentifierFragment other = (IdentifierFragment) obj;
            return
                    fragment.equals( other.fragment )
                            &&
                    qualifier.equals( other.qualifier);
        }
        return false;
    }
}