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
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.CoderFactory;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.ontology.Ontology;


/**
 * The <i>Java language</i> Coder Factory.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class JavaCoderFactory implements CoderFactory<JavaCodeHandler> {

    protected JavaCoderFactory() {}

    public Ontology createCodeModelOntology() {
        return new JavaOntology();
    }

    public JavaCodeHandler createHandlerOnModel(CodeModelBase model) {
        return new JavaCodeHandlerImpl(model);
    }

    /**
     * Creates a query org.asemantics.model on a given model.
     *
     * @param model
     * @return the created object.
     */
    public static JavaQueryModel createQueryModel(CodeModel model) {
        return new JavaQueryModelImpl(model);
    }

    public static final JAsset createJAsset(JavaQueryModel qm) {
        return new JAsset(qm);
    }

    /**
     * Creates a new JPackage object.
     *
     * @param identifier
     * @return the created object.
     */
    public static final JPackage createJPackage(JavaQueryModel qm, Identifier identifier)
    throws QueryModelException {
        return new JPackage(qm, identifier);
    }

   /**
     * Creates a new JInterface object.
     * 
     * @param pathToInterface
     * @return the created object.
     */
    public static JInterface createJInterface(JavaQueryModelImpl qm, Identifier pathToInterface)
   throws QueryModelException {
        return new JInterface(qm, pathToInterface);
    }

    /**
     * Creates a new JClass object.
     *
     * @param identifier
     * @return the created object.
     */
    public static final JClass createJClass(JavaQueryModel qm, Identifier identifier)
    throws QueryModelException {
        return new JClass(qm, identifier);
    }

    /**
     * Creates a new JAttribute object.
     *
     * @param qm
     * @return the created object.
     */
    public static final JAttribute createJAttribute(JavaQueryModel qm, Identifier pathToAttribute)
    throws QueryModelException {
        return new JAttribute(qm, pathToAttribute);
    }

    /**
     * Creates a new JMethod object.
     *
     * @param pathToMethod the path to the method.
     * @return the created object.
     */
    public static final JMethod createJMethod(JavaQueryModel qm, Identifier pathToMethod)
    throws QueryModelException {
        return new JMethod(qm, pathToMethod);
    }

    /**
     * Creates a new JEnumeration object.
     * @param pathToEnumeration the path to the enumeration.
     * @return the created object.
     */
    public static final JEnumeration createJEnumeration(JavaQueryModel qm, Identifier pathToEnumeration)
    throws QueryModelException {
        return new JEnumeration(qm, pathToEnumeration);
    }

    /**
     * Creates a new JSignature.
     *
     * @param qm
     * @param pathToSignature
     * @param parameterTypes
     * @param returnType
     * @return the created object.
     * @throws com.asemantics.rdfcoder.model.CodeModelException
     */
    public static final JSignature createJSignature(
             JavaQueryModel qm,
             Identifier pathToSignature,
             JavaCodeModel.JType[] parameterTypes,
             JavaCodeModel.JType returnType
    ) throws QueryModelException {
        return new JSignature(qm, pathToSignature, parameterTypes, returnType);
    }

    /**
     * Creates a base object over an RDF class.
     *
     * @param qm the query manager used to perform the processing.
     * @param entity the entity that must be created.
     * @return the created object.
     * @throws QueryModelException if an error occurs during creation.
     */
    protected static JBase createBaseOnRDFClass(JavaQueryModel qm, Identifier entity)
    throws QueryModelException {
        final String rdfClass = entity.getStrongestQualifier();
        if(JavaCodeModel.PACKAGE_KEY.equals(rdfClass)) {
            return createJPackage(qm, entity);
        } else if( JavaCodeModel.CLASS_KEY.equals(rdfClass) ) {
            return createJClass(qm, entity);
        } else if( JavaCodeModel.ATTRIBUTE_KEY.equals(rdfClass)) {
            return createJAttribute(qm, entity);
        } else if( JavaCodeModel.METHOD_KEY.equals(rdfClass) ) {
            return createJMethod(qm, entity);
        } else if( JavaCodeModel.ENUMERATION_KEY.equals(rdfClass) ) {
            return createJEnumeration(qm, entity);
        } else {
            throw new IllegalArgumentException( String.format("Unknown rdfClass: '%s'", rdfClass) );
        }
    }

}
