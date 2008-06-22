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


package com.asemantics.model;

import com.asemantics.model.ontology.Ontology;


/**
 * A <code>CoderFactory</code> allows to create a code model and every
 * related object.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class JavaCoderFactory implements CoderFactory {

    public Ontology createCodeModelOntology() {
        return new JavaOntology();
    }

    public CodeHandler createHandlerOnModel(CodeModelBase model) {
        return new CodeHandlerImpl(model);
    }

    /**
     * Creates a query org.asemantics.model on a given model.
     * @param model
     * @return
     */
    public static JavaQueryModel createQueryModel(CodeModel model) {
        return new JavaQueryModelImpl(model);
    }

    public static final Asset createJAsset(JavaQueryModel qm) {
        return new Asset(qm);
    }

    /**
     * Creates a new JPackage object.
     * @param sections
     * @return
     */
    public static final JPackage createJPackage(JavaQueryModel qm, String[] sections) throws QueryModelException {
        return new JPackage(qm, sections);
    }

    /**
     * Creates a new JPackage object.
     * @param pathToPackage
     * @return
     */
    public static final JPackage createJPackage(JavaQueryModel qm, String pathToPackage) throws QueryModelException {
        return new JPackage(qm, pathToPackage);
    }

   /**
     * Creates a new JInterface object.
     * @param pathToInterface
     * @return
     */
    public static JInterface createJInterface(JavaQueryModelImpl qm, String pathToInterface) throws QueryModelException {
        return new JInterface(qm, pathToInterface);
    }

    /**
     * Creates a new JClass object.
     * @param sections
     * @return
     */
    public static final JClass createJClass(JavaQueryModel qm, String[] sections) throws QueryModelException {
        return new JClass(qm, sections);
    }

    /**
     * Creates a new JClass object.
     * @param pathToClass
     * @return
     */
    public static final JClass createJClass(JavaQueryModel qm, String pathToClass) throws QueryModelException {
        return new JClass(qm, pathToClass);
    }

    /**
     * Creates a new JAttribute object.
     * @param qm
     * @return
     */
    public static final JAttribute createJAttribute(JavaQueryModel qm, String[] sections) throws QueryModelException {
        return new JAttribute(qm, sections);
    }

    /**
     * Creates a new JAttribute object.
     * @param qm
     * @return
     */
    public static final JAttribute createJAttribute(JavaQueryModel qm, String pathToAttribute) throws QueryModelException {
        return new JAttribute(qm, pathToAttribute);
    }

    /**
     * Creates a new JMethod object.
     * @param sections the path to the method.
     * @return
     */
    public static final JMethod createJMethod(JavaQueryModel qm, String[] sections) throws QueryModelException {
        return new JMethod(qm, sections);
    }

    /**
     * Creates a new JMethod object.
     * @param pathToMethod the path to the method.
     * @return
     */
    public static final JMethod createJMethod(JavaQueryModel qm, String pathToMethod) throws QueryModelException {
        return new JMethod(qm, pathToMethod);
    }

        /**
     * Creates a new JEnumeration object.
     * @param sections
     * @return
     */
    public static final JEnumeration createJEnumeration(JavaQueryModel qm, String[] sections) throws QueryModelException {
        return new JEnumeration(qm, sections);
    }

    /**
     * Creates a new JEnumeration object.
     * @param pathToEnumeration the path to the enumeration.
     * @return
     */
    public static final JEnumeration createJEnumeration(JavaQueryModel qm, String pathToEnumeration) throws QueryModelException {
        return new JEnumeration(qm, pathToEnumeration);
    }

    /**
     * Creates a new  JSignature object.
     * @param qm
     * @param sections
     * @param parameterTypes
     * @param returnType
     * @return
     * @throws CodeModelException
     */
    public static final JSignature createJSignature(
             JavaQueryModel qm,
             String[] sections,
             JavaCodeModel.JType[] parameterTypes,
             JavaCodeModel.JType returnType
    ) throws QueryModelException {
        return new JSignature(qm, sections, parameterTypes, returnType);
    }

    /**
     * Creates a new JSignature.
     * @param qm
     * @param pathToSignature
     * @param parameterTypes
     * @param returnType
     * @return
     * @throws CodeModelException
     */
    public static final JSignature createJSignature(
             JavaQueryModel qm,
             String pathToSignature,
             JavaCodeModel.JType[] parameterTypes,
             JavaCodeModel.JType returnType
    ) throws QueryModelException {
        return new JSignature(qm, pathToSignature, parameterTypes, returnType);
    }

    /**
     * Creates a base object over an RDF class.
     * @param qm
     * @param sections
     * @param rdfClass
     * @return
     * @throws QueryModelException
     */
    protected static final JBase createBaseOnRDFClass(JavaQueryModel qm, String[] sections, String rdfClass)
    throws QueryModelException {
        if(JavaCodeModel.JPACKAGE.equals(rdfClass)) {
            return createJPackage(qm, sections);
        } else if( JavaCodeModel.JCLASS.equals(rdfClass) ) {
            return createJClass(qm, sections);
        } else if( JavaCodeModel.JATTRIBUTE.equals(rdfClass)) {
            return createJAttribute(qm, sections);
        } else if( JavaCodeModel.JMETHOD.equals(rdfClass) ) {
            return createJMethod(qm, sections);
        } else if( JavaCodeModel.JENUMERATION.equals(rdfClass) ) {
            return createJEnumeration(qm, sections);
        } else {
            throw new IllegalArgumentException("Unknown rdfClass: " + rdfClass);
        }
    }

}
