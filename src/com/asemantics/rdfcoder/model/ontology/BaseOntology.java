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


package com.asemantics.rdfcoder.model.ontology;

import static com.asemantics.rdfcoder.model.CodeModelBase.*;

import java.net.URL;


/**
 * Defines ontology predicates common to every <i>Ontology</i>.
 */
public abstract class BaseOntology extends DefaultOntology {

    protected BaseOntology() {
        try {

            defineRelation(ASSET, new URL(CONTAINS_LIBRARY), ASSET_PREFIX);
            defineRelation(ASSET, new URL(LIBRARY_LOCATION));
            defineRelation(ASSET, new URL(LIBRARY_DATETIME));


        } catch (Exception e) {
             throw new RuntimeException("Error while defining Base Ontology: " + BaseOntology.class);
        }
    }

}
