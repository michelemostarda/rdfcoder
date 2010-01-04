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


package com.asemantics.rdfcoder.model.java;

import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.QueryModelException;

/**
 * Defines the base class for any code entity.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
//TODO: LOW - extract the Base class.
public abstract class JBase {

    /**
     * The observed code org.asemantics.model.
     */
    private JavaQueryModel queryModel;

    /**
     * The parent container.
     */
    protected JBase parent;

    /**
     * The name of the container.
     */
    private Identifier identifier;

    /**
     * Construction by sections.
     * @param qm
     * @param identifier
     */
    protected JBase(JavaQueryModel qm, Identifier identifier) throws QueryModelException {
        if(qm == null) {
            throw new NullPointerException("qm cannot be null");
        }
        if(identifier == null) {
            throw new NullPointerException("identifier cannot be null.");
        }

        queryModel = qm;
        this.identifier = identifier;
        parent = makeHierarchy(qm, identifier);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Returns the associated query model.
     *
     * @return model instance.
     */
    public JavaQueryModel getQueryModel() {
        return queryModel;
    }

    /**
     * Returns the element parent if any.
     *
     * @return the parent, <code>null</code> if none.
     */
    public JContainer getParent() {
        return (JContainer) parent;
    }

    /**
     * Check the existence of an entity.
     *
     * @param identifier
     * @return <code>true</code> if exists.
     */
    protected abstract boolean exists(Identifier identifier);

    /**
     * Return the hierarchy element type.
     * 
     * @return the name of the type.
     */
    protected abstract String getHierarchyElemType();

    /**
     * Validates the sections value.
     * 
     * @param identifier
     */
    protected final void validateAndCheckName(final Identifier identifier, int index)
    throws QueryModelException {
        Identifier sections = identifier.getSections(index);
        if(sections.size() == 0) {
            throw  new IllegalArgumentException("sections length must be > 0");
        }
        if( ! exists(sections) ) {
            throw new QueryModelException("entity '" + sections + "' doesn't exist.");
        }
    }

    /**
     * Concatenate the sections from 0 to index in a string package separated.
     * @param sections
     * @param index
     * @return the concatenated string.
     */
    public static final String concatenate(final String[] sections, final int index) {
        StringBuilder sb = new StringBuilder();
        for(int s = 0; s <= index; s++) {
            sb.append(sections[s]);
            if(s != index) {
                sb.append(JavaCodeHandler.PACKAGE_SEPARATOR);
            }
        }
        return sb.toString();
    }


    /**
     * Splits the container path into sections.
     *
     * @param path
     * @return the split sections.
     */
    public static final String[] splitPath(String path) {
        int psi = path.indexOf(CodeModel.PREFIX_SEPARATOR); // Ignoring prefix identifier if any.
        String subPath = path.substring(psi + 1);
        String[] sectionNames = subPath.split("\\" + JavaCodeHandler.PACKAGE_SEPARATOR);
        return sectionNames;
    }

    /**
     * Creates a hierarchy result on given org.asemantics.model and sections.
     * @param qm
     * @param entity
     * @return  the created hierarchy.
     */
    protected final JBase makeHierarchy(JavaQueryModel qm, Identifier entity)
    throws QueryModelException {
        Identifier parentIdentifier = entity.getPreTail();
        if(parentIdentifier.size() == 0) {
            return null;
        }
        JBase parent = JavaCoderFactory.createBaseOnRDFClass( qm, parentIdentifier);
        //HierarchyResult hr = new HierarchyResult();
        //hr.p = (JContainer) parent;
        return parent;
    }

    public String toString() {
        return identifier.toString();
    }

}
