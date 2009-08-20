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

/**
 * Describes an inspection atomic operation.
 */
public abstract class Pattern {

    /**
     * No macth object.
     */
    public static final Object NO_MATCH = new Object();

    /**
     * Returns <code>true</code> if <i>part</i> is applicable to
     * this pattern, <code>false</code> otherwise.
     *
     * @param part
     * @return <code>true</code> if the pattern is applicable on the part.
     */
    abstract boolean isApplicable(String part);

    /**
     * Returns an instance pattern to be used by the parser.
     * @param part
     * @return
     */
    abstract Pattern getInstance(String part);

    /**
     * Implementation of specific accessor.
     *
     * @param in
     * @return result of application.
     */
    abstract protected Object internalApply(Object in) throws Exception;

    /**
     * Applies current inspection step on <i>in</i> object
     * returning a result. Returns NO_MATCH if nothing found.
     *
     * @param in
     * @return result of operation.
     * @throws PatternException if an error occurs.
     */
     public Object applyPattern(Object in) throws PatternException {
        try {
            return internalApply(in);
        } catch (Exception e) {
            throw new PatternException("Error while applying pattern " + getClass().getName() + ".", e);
        }
    }

}
