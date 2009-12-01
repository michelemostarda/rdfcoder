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


import static com.asemantics.rdfcoder.model.java.JavaCodeModel.*;
import com.asemantics.rdfcoder.model.ontology.BaseOntology;

import java.net.URL;


/**
 * Defines the <i>Java</i> ontology.
 */
public class JavaOntology extends BaseOntology {

    /**
     * Package protected class.
     */
    public JavaOntology() {
        super();
        initJavaOntology();
    }

    private void initJavaOntology() {

        try {

            // SUBCLASS OF
            defineRelation( new URL(SUBCLASSOF) );

            // CONTAINS_PACKAGE
            defineRelation(PACKAGE_PREFIX, new URL(CONTAINS_PACKAGE), PACKAGE_PREFIX);
            defineRelation(PACKAGE_PREFIX, new URL(CONTAINS_PACKAGE), CLASS_PREFIX  );

            // CONTAINS_INTERFACE
            defineRelation(PACKAGE_PREFIX,   new URL(CONTAINS_INTERFACE), INTERFACE_PREFIX);
            defineRelation(INTERFACE_PREFIX, new URL(CONTAINS_INTERFACE), INTERFACE_PREFIX);
            defineRelation(CLASS_PREFIX,     new URL(CONTAINS_INTERFACE), INTERFACE_PREFIX);

            // CONTAINS_CLASS
            defineRelation(PACKAGE_PREFIX,   new URL(CONTAINS_CLASS), CLASS_PREFIX);
            defineRelation(CLASS_PREFIX,     new URL(CONTAINS_CLASS), CLASS_PREFIX);
            defineRelation(INTERFACE_PREFIX, new URL(CONTAINS_CLASS), CLASS_PREFIX);

            // CONTAINS_ATTRIBUTE
            defineRelation(INTERFACE_PREFIX,   new URL(CONTAINS_ATTRIBUTE), ATTRIBUTE_PREFIX);
            defineRelation(CLASS_PREFIX,       new URL(CONTAINS_ATTRIBUTE), ATTRIBUTE_PREFIX);
            defineRelation(ENUMERATION_PREFIX, new URL(CONTAINS_ATTRIBUTE), ATTRIBUTE_PREFIX);

            // CONTAINS_CONSTRUCTOR
            defineRelation(CLASS_PREFIX,       new URL(CONTAINS_CONSTRUCTOR), CONSTRUCTOR_PREFIX);
            defineRelation(ENUMERATION_PREFIX, new URL(CONTAINS_CONSTRUCTOR), CONSTRUCTOR_PREFIX);

            // ATTRIBUTE_TYPE
            defineRelation(ATTRIBUTE_PREFIX, new URL(ATTRIBUTE_TYPE));

            // ATTRIBUTE_VALUE
            defineRelation(ATTRIBUTE_PREFIX, new URL(ATTRIBUTE_VALUE));

            // CONTAINS_METHOD
            defineRelation(CLASS_PREFIX,       new URL(CONTAINS_METHOD), METHOD_PREFIX);
            defineRelation(INTERFACE_PREFIX,   new URL(CONTAINS_METHOD), METHOD_PREFIX);
            defineRelation(ENUMERATION_PREFIX, new URL(CONTAINS_METHOD), METHOD_PREFIX);

            // CONTAINS_ENUMERATION
            defineRelation(PACKAGE_PREFIX,   new URL(CONTAINS_ENUMERATION), ENUMERATION_PREFIX);
            defineRelation(CLASS_PREFIX,     new URL(CONTAINS_ENUMERATION), ENUMERATION_PREFIX);
            defineRelation(INTERFACE_PREFIX, new URL(CONTAINS_ENUMERATION), ENUMERATION_PREFIX);

            // CONTAINS_ELEMENT
            defineRelation(ENUMERATION_PREFIX, new URL(CONTAINS_ELEMENT), ELEMENT_PREFIX);

            // CONTAINS_SIGNATURE
            defineRelation(CONSTRUCTOR_PREFIX, new URL(CONTAINS_SIGNATURE), SIGNATURE_PREFIX);
            defineRelation(METHOD_PREFIX,      new URL(CONTAINS_SIGNATURE), SIGNATURE_PREFIX);

            // CONTAINS_PARAMETER
            defineRelation(CONSTRUCTOR_PREFIX, new URL(CONTAINS_PARAMETER), PARAMETER_PREFIX);
            defineRelation(SIGNATURE_PREFIX,   new URL(CONTAINS_PARAMETER), PARAMETER_PREFIX);

            // PARAMETER_TYPE
            defineRelation(PARAMETER_PREFIX, new URL(PARAMETER_TYPE));

            // RETURN_TYPE
            defineRelation(SIGNATURE_PREFIX, new URL(RETURN_TYPE));

            // EXTENDS_CLASS
            defineRelation(CLASS_PREFIX, new URL(EXTENDS_CLASS), CLASS_PREFIX);

            // IMPLEMENTS_INT
            defineRelation(CLASS_PREFIX, new URL(IMPLEMENTS_INT), INTERFACE_PREFIX);

            // EXTENDS_INT
            defineRelation(INTERFACE_PREFIX, new URL(EXTENDS_INT), INTERFACE_PREFIX);

            // THROWS
            defineRelation(SIGNATURE_PREFIX, new URL(THROWS), CLASS_PREFIX);

            // HAS_VISIBILITY
            defineRelation(CLASS_PREFIX,       new URL(HAS_VISIBILITY));
            defineRelation(ENUMERATION_PREFIX, new URL(HAS_VISIBILITY));
            defineRelation(CONSTRUCTOR_PREFIX, new URL(HAS_VISIBILITY));
            defineRelation(METHOD_PREFIX,      new URL(HAS_VISIBILITY));
            defineRelation(ATTRIBUTE_PREFIX,   new URL(HAS_VISIBILITY));

            // HAS_MODIFIERS
            defineRelation(CLASS_PREFIX,       new URL(HAS_MODIFIERS));
            defineRelation(ENUMERATION_PREFIX, new URL(HAS_MODIFIERS));
            defineRelation(CONSTRUCTOR_PREFIX, new URL(HAS_MODIFIERS));
            defineRelation(METHOD_PREFIX,      new URL(HAS_MODIFIERS));
            defineRelation(ATTRIBUTE_PREFIX,   new URL(HAS_MODIFIERS));

            // THROWS
            defineRelation(CONSTRUCTOR_PREFIX, new URL(THROWS), CLASS_PREFIX);
            defineRelation(METHOD_PREFIX,      new URL(THROWS), CLASS_PREFIX);

        } catch (Exception e) {
            throw new RuntimeException("Error while defining Java Ontology " + JavaOntology.class, e);
        }

    }

}
