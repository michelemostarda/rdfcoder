/**
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


package com.asemantics.modelimpl;

import com.asemantics.model.CoderFactory;
import com.asemantics.model.SPARQLQuerableCodeModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 ** The <code>CoderFactory</code> implementation for the Jena backend.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JenaCoderFactory extends CoderFactory {

    public SPARQLQuerableCodeModel createCodeModel() {
        Model model = ModelFactory.createDefaultModel();
        return new JenaCodeModel(model);
    }

    public JenaCodeStorage createCodeStorage() {
        return new JenaCodeStorage();
    }

}
