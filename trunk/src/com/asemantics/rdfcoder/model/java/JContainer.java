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

import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.CodeHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents every <i>Java</i> entity able to contain other entities.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class JContainer extends JModifiable {

    protected JContainer(JavaQueryModel qm, String[] sections)
    throws QueryModelException {
        super(qm, sections);
    }

    protected JContainer(JavaQueryModel qm, String pathToContainer)
    throws QueryModelException {
        super(qm, pathToContainer);
    }

    /**
     * Returns the parent hierarchy.
     *
     * @return the list of containers.
     */
    public JContainer[] getPath() {
        List list = new ArrayList();
        list.add(0, this);
        JContainer current = parent;
        while(current != null) {
            list.add(0, current);
            current = current.parent;
        }
        return (JContainer[]) list.toArray( new JContainer[list.size()] );
    }

    /**
     * Generates the debug path of a container path.
     * @param path
     *
     * @return the absolute path.
     */
    public static String getDebugPath(JContainer[] path) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < path.length; i++) {
            sb.append(path[i].getName()).append(":").append(path[i].getHierarchyElemType());
            if(i < path.length -1) {
                sb.append(CodeHandler.PACKAGE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     * Generates the debug path of this container.
     *
     * @return the path as absolute path.
     */
    public String getDebugPath() {
        return getDebugPath(getPath());
    }

    /**
     * Joins the container hierarchy to a path string.
     *
     * @return string of paths.
     */
    public static String getPathAsString(JContainer[] path) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < path.length; i++) {
            sb.append( path[i].getName() );
            if(i < path.length -1) {
                sb.append(CodeHandler.PACKAGE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public String getPathAsString() {
        return getPathAsString(getPath());
    }

    public String toString() {
        return getDebugPath();
    }

}
