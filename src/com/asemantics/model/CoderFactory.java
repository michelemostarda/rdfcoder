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

import com.asemantics.storage.CodeStorage;

/**
 * A <code>CoderFactory</code> allows to create a code org.asemantics.model and every
 * related object.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class CoderFactory {

    /**
     * Creates a code model instance.
     * @return
     */
    public abstract CodeModel createCodeModel();

    /**
     * Creates a code storage instance.
     * @return
     */
    public abstract CodeStorage createCodeStorage();

    /**
     * Creates a handler on a given model.
     * @param model
     * @return
     */
    public CodeHandler createHandlerOnModel(CodeModel model) {
        return new CodeHandlerImpl(model);
    }

    /**
     * Creates a query org.asemantics.model on a given model.
     * @param model
     * @return
     */
    public static QueryModel createQueryModel(CodeModel model) {
        return new QueryModelImpl(model);        
    }

    public static final JAsset createJAsset(QueryModel qm) {
        return new JAsset(qm);
    }

    /**
     * Creates a new JPackage object.
     * @param sections
     * @return
     */
    public static final JPackage createJPackage(QueryModel qm, String[] sections) throws QueryModelException {
        return new JPackage(qm, sections);
    }

    /**
     * Creates a new JPackage object.
     * @param pathToPackage
     * @return
     */
    public static final JPackage createJPackage(QueryModel qm, String pathToPackage) throws QueryModelException {
        return new JPackage(qm, pathToPackage);
    }

   /**
     * Creates a new JInterface object.
     * @param pathToInterface
     * @return
     */
    public static JInterface createJInterface(QueryModelImpl qm, String pathToInterface) throws QueryModelException {
        return new JInterface(qm, pathToInterface);
    }

    /**
     * Creates a new JClass object.
     * @param sections
     * @return
     */
    public static final JClass createJClass(QueryModel qm, String[] sections) throws QueryModelException {
        return new JClass(qm, sections);
    }

    /**
     * Creates a new JClass object.
     * @param pathToClass
     * @return
     */
    public static final JClass createJClass(QueryModel qm, String pathToClass) throws QueryModelException {
        return new JClass(qm, pathToClass);
    }

    /**
     * Creates a new JAttribute object.
     * @param qm
     * @return
     */
    public static final JAttribute createJAttribute(QueryModel qm, String[] sections) throws QueryModelException {
        return new JAttribute(qm, sections);
    }

    /**
     * Creates a new JAttribute object.
     * @param qm
     * @return
     */
    public static final JAttribute createJAttribute(QueryModel qm, String pathToAttribute) throws QueryModelException {
        return new JAttribute(qm, pathToAttribute);
    }

    /**
     * Creates a new JMethod object.
     * @param sections the path to the method.
     * @return
     */
    public static final JMethod createJMethod(QueryModel qm, String[] sections) throws QueryModelException {
        return new JMethod(qm, sections);
    }

    /**
     * Creates a new JMethod object.
     * @param pathToMethod the path to the method.
     * @return
     */
    public static final JMethod createJMethod(QueryModel qm, String pathToMethod) throws QueryModelException {
        return new JMethod(qm, pathToMethod);
    }

        /**
     * Creates a new JEnumeration object.
     * @param sections
     * @return
     */
    public static final JEnumeration createJEnumeration(QueryModel qm, String[] sections) throws QueryModelException {
        return new JEnumeration(qm, sections);
    }

    /**
     * Creates a new JEnumeration object.
     * @param pathToEnumeration the path to the enumeration.
     * @return
     */
    public static final JEnumeration createJEnumeration(QueryModel qm, String pathToEnumeration) throws QueryModelException {
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
             QueryModel qm,
             String[] sections,
             CodeModel.JType[] parameterTypes,
             CodeModel.JType returnType
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
             QueryModel qm,
             String pathToSignature,
             CodeModel.JType[] parameterTypes,
             CodeModel.JType returnType
    ) throws QueryModelException {
        return new JSignature(qm, pathToSignature, parameterTypes, returnType);
    }

    protected static final JBase createBaseOnRDFClass(QueryModel qm, String[] sections, String rdfClass)
    throws QueryModelException {
        if(CodeModel.JPACKAGE.equals(rdfClass)) {
            return createJPackage(qm, sections);
        } else if( CodeModel.JCLASS.equals(rdfClass) ) {
            return createJClass(qm, sections);
        } else if( CodeModel.JATTRIBUTE.equals(rdfClass)) {
            return createJAttribute(qm, sections);
        } else if( CodeModel.JMETHOD.equals(rdfClass) ) {
            return createJMethod(qm, sections);
        } else if( CodeModel.JENUMERATION.equals(rdfClass) ) {
            return createJEnumeration(qm, sections);
        } else {
            throw new IllegalArgumentException("Unknown rdfClass: " + rdfClass);
        }
    }

}
