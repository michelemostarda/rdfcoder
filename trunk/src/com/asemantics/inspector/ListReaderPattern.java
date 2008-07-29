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


package com.asemantics.inspector;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Pattern to read an enumerable (ordered) type:
 * <ul>
 *      <li>Array</li>
 *      <li>Collection</li>
 *      <li>List</li>
 * </ul>
 *
 * <br/>
 *
 * Pattern format:
 * <i> &lt;attribute_name&gt;[&lt;index&gt;] </i>
 *
 */
public class ListReaderPattern extends Pattern {

    /**
     * Internal pattern recognizer.
     */
    private static final java.util.regex.Pattern pattern;

    /**
     * Associated {@link com.asemantics.inspector.PropertyReaderPattern} to identify correct attribute
     * names.
     */
    private PropertyReaderPattern propertyReaderPattern;

    /**
     * List index.
     */
    private int index;

    static {
        pattern = java.util.regex.Pattern.compile("(" + PropertyReaderPattern.PATTERN_STR + ")\\[([0-9]+)\\]");
    }

    /**
     * Constructor.
     */
    ListReaderPattern() {}

    /**
     * Constructor.
     *
     * @param prp
     * @param i
     */
    private ListReaderPattern(PropertyReaderPattern prp, int i) {
        propertyReaderPattern = prp;
        index = i;
    }

    boolean isApplicable(String part) {
        Matcher matcher = pattern.matcher(part);
        if( matcher.matches() ) {
            PropertyReaderPattern prp = new PropertyReaderPattern();
            return prp.isApplicable( matcher.group(1) );
        }
        return false;
    }

    Pattern getInstance(String part) {
        Matcher matcher = pattern.matcher(part);
        if( ! matcher.matches() ) {
            throw new IllegalStateException();
        }
        PropertyReaderPattern prp = new PropertyReaderPattern(matcher.group(1));
        int i = Integer.parseInt( matcher.group(2) );
        return new ListReaderPattern( prp, i );
    }

    protected Object internalApply(Object in) throws Exception {
        Object result = propertyReaderPattern.internalApply(in);

        if( result.getClass().isArray() ) {
            return ( (Object[]) result)[index];
        }

        if( result instanceof List ) {
            return ( (List) result).get(index);
        }

        if( result instanceof Collection ) {
            Iterator iter = ( (Collection) result).iterator();
            int i = 0;
            Object target = null;
            while( i++ < index && iter.hasNext()) {
                target = iter.next();
            }
            return target;
        }

        return null;
    }
}
