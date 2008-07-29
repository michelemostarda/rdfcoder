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

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Pattern to read a bean attrivute:
 * <ul>
 *      <li>property</li>
 *      <li>method</li>
 * </ul>
 *
 * <br/>
 *
 * Pattern format:
 * <i> &lt;attribute_name&gt; </i>
 */
public class PropertyReaderPattern extends Pattern {

    /**
     * Pattern regex identifier.
     */
    protected static final String PATTERN_STR = "[a-z][a-zA-Z_0-9]*";

    private static final java.util.regex.Pattern pattern;

    static {
        pattern = java.util.regex.Pattern.compile(PATTERN_STR);
    }

    /**
     * Pattern property name.
     */
    private String propertyName;

    /**
     * Constructor.
     */
    PropertyReaderPattern() {}

    /**
     * Constructor.
     * 
     * @param pn
     */
    PropertyReaderPattern(String pn) {
        propertyName = pn;
    }

    public boolean isApplicable(String part) {
        Matcher matcher = pattern.matcher(part);
        return matcher.matches();
    }

    Pattern getInstance(String part) {
        return new PropertyReaderPattern(part); 
    }

    protected Object internalApply(Object in) throws Exception {
        if( in instanceof Map) {
            return ((Map) in).get(propertyName);
        }
        
        if( in instanceof Set) {
            return ((Set) in).contains(in) ? in : null;
        }

        return BeanAccessor.getProperty(in, propertyName);
    }

}
