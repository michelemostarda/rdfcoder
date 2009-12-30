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

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.QueryModelException;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents every <i>Java</i> entity able to contain other entities.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class JContainer extends JModifiable {

    /**
     * Constructor.
     *
     * @param qm context query model.
     * @param identifier container identifier.
     * @throws QueryModelException
     */
    protected JContainer(JavaQueryModel qm, Identifier identifier)
    throws QueryModelException {
        super(qm, identifier);
    }

    /**
     * Returns the parent hierarchy.
     *
     * @return the list of containers.
     */
    public JContainer[] getPath() {
        List<JContainer> list = new ArrayList<JContainer>();
        list.add(0, this);
        JContainer current = getParent();
        while(current != null) {
            list.add(0, current);
            current = current.getParent();
        }
        return list.toArray( new JContainer[list.size()] );
    }

}
