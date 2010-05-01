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
import com.asemantics.rdfcoder.model.QueryModel;
import com.asemantics.rdfcoder.model.QueryModelException;

/**
 * Defines the query model for the <i>Java</i> language.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 * @see com.asemantics.rdfcoder.model.QueryModel
 * @see JavaCodeModel
 */
public interface JavaQueryModel extends QueryModel {

    /**
     * Checks if a package exists in the Code Model.
     *
     * @param pack package path.
     * @return <code>true</code> if exists.
     */
    public boolean packageExists(Identifier pack);

    /**
     * Checks if a class exixts in Code Model.
     *
     * @param pathToClass fully qualified path of the class.
     * @return <code>true</code> if exists.
     */
    public boolean classExists(Identifier pathToClass);

    /**
     * Checks if an interface exists in Code Model.
     *
     * @param pathToInterface  fully qualified path of the interface.
     * @return <code>true</code> if exists.
     */
    public boolean interfaceExists(Identifier pathToInterface);

    /**
     * Checks if an enumeration exists in Code Model.
     *
     * @param pathToEnumeration fully qualified path of the enumeration.
     * @return <code>true</code> if exists.
     */
    public boolean enumerationExists(Identifier pathToEnumeration);

    /**
     * Checks if an attribute exists in Code Model.
     *
     * @param pathToAttribute fully qualified path of the attribute.
     * @return <code>true</code> if exists.
     */
    public boolean attributeExists(Identifier pathToAttribute);

    /**
     * Checks if a method exists in Code Model.
     *
     * @param pathToMethod fully qualified path of the method.
     * @return <code>true</code> if exists.
     */
    public boolean methodExists(Identifier pathToMethod);

    /**
     * Checks if a signature exists in Code Model.
     *
     * @param pathToSignanure
     * @return <code>true</code> if exists.
     */
    public boolean signatureExists(Identifier pathToSignanure);

    /**
     * Returns all packages defined within this models.
     *
     * @return list of packages in model.
     */
    public JPackage[] getAllPackages();

    /**
     * Retruns the package with a given pack name.
     *
     * @param pack
     * @return the package.
     * @throws com.asemantics.rdfcoder.model.QueryModelException
     */
    public JPackage getPackage(Identifier pack) throws QueryModelException;

    /**
     * Returns all packages in the given package expressed as string.
     *
     * @param path
     * @return the packages found.
     * @throws QueryModelException
     */
    public JPackage[] getPackagesInto(Identifier path) throws QueryModelException;

    /**
     * Returns all the interfaces defined within this model.
     *
     * @return all the interfaces in the model.
     */
    public JInterface[] getAllInterfaces();

    /**
     * Returns the interface specified by the given path.
     *
     * @param pathToInterface
     * @return the interface.
     * @throws QueryModelException
     */
    public JInterface getInterface(Identifier pathToInterface) throws QueryModelException;

    /**
     * Returns all interfaces into a container path.
     *
     * @param path
     * @return the interfaces found.
     * @throws QueryModelException
     */
    public JInterface[] getInterfacesInto(Identifier path) throws QueryModelException;

    /**
     * Returns all classes defined within this model.
     *
     * @return all the classes.
     */
    public JClass[] getAllClasses();

    /**
     * Returns the class specified by tge given path.
     *
     * @param pathToClass
     * @return the specific class.
     * @throws QueryModelException
     */
    public JClass getClazz(Identifier pathToClass) throws QueryModelException;

    /**
     * Returns all classes into a container path.
     *
     * @param path
     * @return the classes found.
     * @throws QueryModelException
     */
    public JClass[] getClassesInto(Identifier path) throws QueryModelException;

    /**
     * Returns all the attributes in a class.
     *
     * @param pathToClass the path to the class containing the attributes.
     * @return the attributes found.
     */
    public JAttribute[] getAttributesInto(Identifier pathToClass) throws QueryModelException;

    /**
     * Returns the attribute of a class.
     *
     * @param pathToAttribute the path to the attribute.
     * @return the attribute found.
     * @throws QueryModelException
     */
    public JAttribute getAttribute(Identifier pathToAttribute) throws QueryModelException;

    /**
     * Returns the attribute type of a given attribute.
     *
     * @param pathToAttribute
     * @return the type of the attribute.
     * @throws QueryModelException
     */
    public JavaCodeModel.JType getAttributeType(Identifier pathToAttribute)  throws QueryModelException;

    /**
     * Returns the methods in the given container.
     *
     * @param pathToContainer
     * @return the methods found.
     * @throws QueryModelException
     */
    public JMethod[] getMethodsInto(Identifier pathToContainer) throws QueryModelException;

    /**
     * Returns the fully qualified method.
     *
     * @param pathToMethod
     * @return the <code>JMethod</code> object if exists.
     * @throws QueryModelException if the fully qualified method nam doesn't exist.
     */
    public JMethod getMethod(Identifier pathToMethod) throws QueryModelException;

    /**
     * Returns all the enumerations defined within the model.
     *
     * @return list of available enumerations, an empty list if no enumerations are found. 
     */
    public JEnumeration[] getAllEnumerations();

    /**
     * Returns the enumerations in the given container.
     *
     * @param pathToContainer
     * @return the enumerations found.
     * @throws QueryModelException
     */
    public JEnumeration[] getEnumerationsInto(Identifier pathToContainer) throws QueryModelException;

    /**
     * Returns the fully qualified enumeration.
     *
     * @param pathToEnumeration
     * @return the enumeration found.
     * @throws QueryModelException
     */
    public JEnumeration getEnumeration(Identifier pathToEnumeration) throws QueryModelException;

    /**
     * Returns the elements into an enumeration.
     *
     * @param pathToEnumeration
     * @return  the elements found.
     */
    public String[] getElements(Identifier pathToEnumeration);

    /**
     * Returns the signatures of a method.
     *
     * @param pathToMethod to the method.
     * @return  the signatures found.
     * @throws QueryModelException
     */
    public JSignature[] getSignatures(Identifier pathToMethod) throws QueryModelException;

    /**
     * Returns the parameter types of a signature.
     *
     * @param pathToSignature
     * @return the parameters found.
     * @throws QueryModelException
     */
    public JavaCodeModel.JType[] getParameters(Identifier pathToSignature) throws QueryModelException;

    /**
     * Returns the return type of a signature.
     *
     * @param pathToSignature
     * @return the return type.
     * @throws QueryModelException
     */
    public JavaCodeModel.JType getReturnType(Identifier pathToSignature) throws QueryModelException;

    /**
     * Returns the visibility associated to the entity.
     * 
     * @param pathToEntity path to the entity.
     * @return the visibility.
     * @throws QueryModelException
     */
    public JavaCodeModel.JVisibility getVisibility(Identifier pathToEntity) throws QueryModelException;

    /**
     * Returns the modifiers associated to the given entity.
     *
     * @param pathToEntity
     * @return the modifiers.
     * @throws QueryModelException
     */
    public JavaCodeModel.JModifier[] getModifiers(Identifier pathToEntity) throws QueryModelException;

}
