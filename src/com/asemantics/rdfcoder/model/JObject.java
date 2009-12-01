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

/**
 * The base class for both <code>JInterface</code>
 * and <code>JClass</code>, and defines some methods useful to access
 * classes, interfaces and common entities.
 */
public abstract class JObject extends JContainer {

    protected JObject(JavaQueryModel qm, String[] sections)
    throws QueryModelException {
        super(qm, sections);
    }

    protected JObject(JavaQueryModel qm, String pathToContainer)
    throws QueryModelException {
        super(qm, pathToContainer);
    }

    /**
     * Returns the inner classes inside the object.
     *
     * @return list of inner objects.
     * @throws QueryModelException
     */
    public JClass[] getInnerClasses() throws QueryModelException {
        JavaQueryModel qm = getQueryModel();
        return qm.getClassesInto( getPathAsString() );
    }

    /**
     * Returns the methods inside the object.
     *
     * @return list of methods.
     * @throws QueryModelException
     */
    public JMethod[] getMethods() throws QueryModelException {
        JavaQueryModel qm = getQueryModel();
        return qm.getMethodsInto( getPathAsString() );
    }

    /**
     * Returns the attributes inside the object.
     *
     * @return list of attributes.
     */
    public JAttribute[] getAttributes() throws QueryModelException {
        JavaQueryModel qm = getQueryModel();
        return qm.getAttributesInto( getPathAsString() );
    }

    /**
     * Returns the enumerations inside the object.
     *
     * @return list of enumerations.
     * @throws QueryModelException
     */
    public JEnumeration[] getEnumerations() throws QueryModelException {
        JavaQueryModel qm = getQueryModel();
        return qm.getEnumerationsInto( getPathAsString() );
    }
}
