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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Defines the unique context of a resource.
 * Can be used to build a resource identifier.
 *
 * @version $Id$
 * @see IdentifierBuilder
 */
public class Identifier implements Serializable {

    /**
     * The qualifier separator.
     */
    public static final String QUALIFIER_SEPARATOR = ":";

    /**
     * The prefix of the identifier.
     */
    protected String prefix;

    /**
     * The list of ordered fragments composing the identifier.
     */
    protected List<IdentifierFragment> fragments;

    /**
     * Constructor.
     *
     * @param prefix
     * @param fs
     */
    protected Identifier(String prefix, List<IdentifierFragment> fs) {
        this.prefix = prefix;
        fragments   = new ArrayList<IdentifierFragment>(fs);
    }

    /**
     * Constructor.
     *
     * @param prefix
     * @param fs
     */
    protected Identifier(String prefix, IdentifierFragment[] fs) {
        this(prefix, Arrays.asList(fs));
    }

    /**
     * Returns the <code>i-th</code> fragment.
     *
     * @param f
     * @return the identifier fragment.
     */
    public IdentifierFragment getFragment(int f) {
        return fragments.get(f);
    }

    /**
     * @return the identifier prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the identifier as string.
     */
    public String getIdentifier() {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        String currentQualifier = null;
        IdentifierFragment cf;
        final int lastIndex = fragments.size() - 2;
        for(int i = 0; i < fragments.size(); i++) {
            cf = fragments.get(i);
            if( ! cf.getQualifier().equals(currentQualifier) ) {
                currentQualifier = cf.getQualifier();
                sb.append(currentQualifier).append(QUALIFIER_SEPARATOR).append(cf.getFragment());
            } else {
                sb.append(cf.getFragment());
            }
            if(i <= lastIndex) {
                sb.append(JavaCodeHandler.PACKAGE_SEPARATOR); // TODO: generalize this dep.
            }
        }
        return sb.toString();
    }

    /**
     * @return the last fragment of the identifier.
     */
    public Identifier getTail() {
        if(fragments.size() > 0) {
            return new Identifier( prefix, Arrays.asList( fragments.get( fragments.size() - 1) ) );
        }
        return new Identifier( prefix, Collections.<IdentifierFragment>emptyList() );
    }

    /**
     * @return all but the last fragment of the identifier.
     */
    // TODO: HIGH : rename to getParent()
    public Identifier getPreTail() {
        if(fragments.size() > 0) {
            return new Identifier( prefix, fragments.subList(0, fragments.size() - 1) );
        }
        return new Identifier( prefix, Collections.<IdentifierFragment>emptyList() );
    }

    /**
     * @return the first fragment of the identifier.
     */
    public Identifier getHead() {
        return new Identifier( prefix, Arrays.asList( fragments.get(0) ) );
    }

    /**
     * The fragments comprised between <code>from</code> and <code>to</code> indexes.
     *
     * @param from from fragment, inclusive.
     * @param to to fragment <b>exclusive</b>.
     * @return the sub identifier.
     */
    public Identifier getSections(int from, int to) {
        return new Identifier( prefix, fragments.subList(from, to) );
    }

    /**
     * The fragments comprised between <code>0</code> and <code>to</code> indexes.
     *
     * @param to to fragment <b>exclusive</b>.
     * @return the sub identifier.
     */
    public Identifier getSections(int to) {
        return getSections(0, to);
    }

    /**
     * Returns the first fragment matching the given <i>qualifier</i>.
     *
     * @param qualifier the qualifier to be matched.
     * @return the fragment found if any, <code>null</code> otherwise.
     */
    public String getFirstFragmentWithQualifier(String qualifier) {
        for(IdentifierFragment fragment : fragments) {
            if(fragment.getQualifier().equals(qualifier)) {
                return fragment.getFragment();
            }
        }
        return null;
    }

    /**
     * Returns the tail fragment.
     *
     * @return the tail fragment.
     */
    public IdentifierFragment getTailFragment() {
        return fragments.get( fragments.size() - 1 );
    }

    /**
     * Returns the last fragment matching the given <i>qualifier</i>.
     *
     * @param qualifier the qualifier to be matched.
     * @return the fragment found if any, <code>null</code> otherwise.
     */
    public String getLastFragmentWithQualifier(String qualifier) {
        ListIterator<IdentifierFragment> fragmentsIterator = fragments.listIterator( fragments.size() );
        IdentifierFragment fragment;
        while( fragmentsIterator.hasPrevious() ) {
            fragment = fragmentsIterator.previous();
            if(fragment.getQualifier().equals(qualifier)) {
                return fragment.getFragment();
            }
        }
        return null;
    }

    /**
     * @return The qualifier of the last fragment.
     */
    public String getStrongestQualifier() {
        final int fragmentsSize = fragments.size();
        if(fragmentsSize == 0) {
            throw new UnsupportedOperationException("Invalid identifier size to perform this operation.");
        }
        return fragments.get(fragmentsSize - 1).getQualifier();
    }

    /**
     * Creates a copy of the current identifier.
     *
     * @return a new identifier.
     */
    public IdentifierBuilder.IdentifierBuilderInstance copy() {
        return IdentifierBuilder.create(this);
    }

    /**
     * @return the number of fragments defined in this identifier.
     */
    public int size() {
        return fragments.size();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof Identifier) {
            Identifier other = (Identifier) obj;
            return
                    prefix.equals( other.prefix )
                        &&
                    fragments.equals( other.fragments );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return prefix.hashCode() * fragments.hashCode() * 2;
    }

    @Override
    public String toString() {
        return String.format( "%s<%s>", getClass().getSimpleName(), getIdentifier() );
    }
}
