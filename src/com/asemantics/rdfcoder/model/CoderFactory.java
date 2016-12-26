/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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

import com.asemantics.rdfcoder.model.ontology.Ontology;
import com.asemantics.rdfcoder.storage.CodeStorage;

/**
 * Factory class allowing the creation of main library objects. 
 */
public interface CoderFactory<T extends CodeHandler> {

    /**
     * Creates the <i>Code Model Ontology</i>. 
     *
     * @return the ontology instance.
     */
    Ontology createCodeModelOntology();

    /**
     * Creates a <i>Code Model</i> instance.
     *
     * @return the  code model instance.
     */
    CodeModelBase createCodeModel();

    /**
     * Creates a <i>Code Storage</i> instance.
     *
     * @return the code storage instance.
     */
    CodeStorage createCodeStorage();

    /**
     * Creates a <i>Code Handler</i> instance.
     * 
     * @param model
     * @return the code handler instance.
     */
    T createHandlerOnModel(CodeModelBase model);
}
