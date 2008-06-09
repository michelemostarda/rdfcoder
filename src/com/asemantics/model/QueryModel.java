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

import java.util.Date;

/**
 * Represents a method signature.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public interface QueryModel {

    /**
     * Returns the Code Model asset.
     *
     * @return
     */
    public JAsset getAsset();

    /**
     * Returns the asset libraries.
     *
     * @return
     */
    public String[] getLibraries();

    /**
     * Returns the library location.
     *
     * @return
     */
    public String getLibraryLocation(String library);

    /**
     * Returns the library datetime.
     *
     * @param library
     * @return
     */
    public Date getLibraryDateTime(String library);

    /**
     * Checks if a package exists in the Code Model.
     *
     * @param pack package path.
     * @return
     */
    public boolean packageExists(String pack);

    /**
     * Checks if a class exixts in Code Model.
     *
     * @param pathToClass fully qualified path of the class.
     * @return
     */
    public boolean classExists(String pathToClass);

    /**
     * Checks if an interface exists in Code Model.
     *
     * @param pathToInterface  fully qualified path of the interface.
     * @return
     */
    public boolean interfaceExists(String pathToInterface);

    /**
     * Checks if an attribute exists in Code Model.
     *
     * @param pathToAttribute fully qualified path of the attribute.
     * @return
     */
    public boolean attributeExists(String pathToAttribute);

    /**
     * Checks if a method exists in Code Model.
     *
     * @param pathToMethod fully qualified path of the method.
     * @return
     */
    public boolean methodExists(String pathToMethod);

    /**
     * Checks if a signature exists in Code Model.
     * @param pathToSignanure
     * @return
     */
    public boolean signatureExists(String pathToSignanure);

    /**
     * Checks if an enumeration exists in Code Model.
     *
     * @param pathToEnumeration fully qualified path of the enumeration.
     * @return
     */
    public boolean enumerationExists(String pathToEnumeration);

    /**
     * Returns all packages.
     * @return
     */
    public JPackage[] getAllPackages();

    /**
     * Retruns the package with a given pack name.
     *
     * @param pack
     * @return
     * @throws QueryModelException
     */
    public JPackage getPackage(String pack) throws QueryModelException;

    /**
     * Returns all packages in the given package expressed as string.
     *
     * @param path
     * @return
     * @throws QueryModelException
     */
    public JPackage[] getPackagesInto(String path) throws QueryModelException;

    /**
     * Returns all interfaces in Code Model.
     *
     * @return
     */
    public JInterface[] getAllInterfaces();

    /**
     * Returns the interface specified by the given path.
     *
     * @param pathToInterface
     * @return
     * @throws QueryModelException
     */
    public JInterface getInterface(String pathToInterface) throws QueryModelException;

    /**
     * Returns all classes in Code Model.
     *
     * @return
     */
    public JClass[] getAllClasses();

    /**
     * Returns the class specified by tge given path.
     *
     * @param pathToClass
     * @return
     * @throws QueryModelException
     */
    public JClass getClazz(String pathToClass) throws QueryModelException;

    /**
     * Returns all interfaces into a container path.
     *
     * @param path
     * @return
     * @throws QueryModelException
     */
    public JInterface[] getInterfacesInto(String path) throws QueryModelException;

    /**
     * Returns all classes into a container path.
     *
     * @param path
     * @return
     * @throws QueryModelException
     */
    public JClass[] getClassesInto(String path) throws QueryModelException;

    /**
     * Returns all the attributes in a class.
     *
     * @param pathToClass the path to the class containing the attributes.
     * @return
     */
    public JAttribute[] getAttributesInto(String pathToClass) throws QueryModelException;

    /**
     * Returns the attribute of a class.
     *
     * @param pathToAttribute the path to the attribute.
     * @return
     * @throws QueryModelException
     */
    public JAttribute getAttribute(String pathToAttribute) throws QueryModelException;

    /**
     * Returns the attribute type of a given attribute.
     *
     * @param pathToAttribute
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JType getAttributeType(String pathToAttribute)  throws QueryModelException;

    /**
     * Returns the methods in the given container.
     *
     * @param pathToContainer
     * @return
     * @throws QueryModelException
     */
    public JMethod[] getMethodsInto(String pathToContainer) throws QueryModelException;

    /**
     * Returns the fully qualified method.
     *
     * @param pathToMethod
     * @return the <code>JMethod</code> object if exists.
     * @throws QueryModelException if the fully qualified method nam doesn't exist.
     */
    public JMethod getMethod(String pathToMethod) throws QueryModelException;

    /**
     * Returns the enumerations in the given container.
     *
     * @param pathToContainer
     * @return
     * @throws QueryModelException
     */
    public JEnumeration[] getEnumerationsInto(String pathToContainer) throws QueryModelException;

    /**
     * Returns the fully qualified enumeration.
     *
     * @param pathToEnumeration
     * @return
     * @throws QueryModelException
     */
    public JEnumeration getEnumeration(String pathToEnumeration) throws QueryModelException;

    /**
     * Returns the elements into an enumeration.
     *
     * @param pathToEnumeration
     * @return
     */
    public String[] getElements(String pathToEnumeration);

    /**
     * Returns the signatures of a method.
     *
     * @param pathToMethod to the method.
     * @return
     * @throws QueryModelException
     */
    public JSignature[] getSignatures(String pathToMethod) throws QueryModelException;

    /**
     * Returns the parameter types of a signature.
     *
     * @param pathToSignature
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JType[] getParameters(String pathToSignature) throws QueryModelException;

    /**
     * Returns the return type of a signature.
     *
     * @param pathToSignature
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JType getReturnType(String pathToSignature) throws QueryModelException;

    /**
     * Returns the visibility associated to the entity.
     * 
     * @param pathtoEntity path to the entity.
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JVisibility getVisibility(String pathtoEntity) throws QueryModelException;

    /**
     * Returns the modifiers associated to the given entity.
     *
     * @param pathToEntity
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JModifier[] getModifiers(String pathToEntity) throws QueryModelException;

    /**
     * Returns the RDF type of a path.
     *
     * @param path the path to the entity to extract the RDF type.
     * @return
     * @throws QueryModelException
     */
    public String getRDFType(String path) throws QueryModelException;
}
