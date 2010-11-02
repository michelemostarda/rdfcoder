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

import com.asemantics.rdfcoder.model.CodeHandler;
import com.asemantics.rdfcoder.model.ErrorListener;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.parser.ObjectsTable;

/**
 * This interface defines the <i> Java Code Handler</i>.
 *
 * A handler is responsible of receiving code analysis
 * events and transform it in a usable format.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public interface JavaCodeHandler extends CodeHandler, JavadocHandler {

    /**
     * Expected format of the library date-time.
     */
    public static final String LIBRARY_DATETIME_FORMAT = "yyyy_MM_dd__HH_mm_ss";

    /**
     * The package separator character.
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
     * Notifies the begin of a package parsing.
     * The package is expected to be FULLY QUALIFIED.
     *
     * @param pathToPackage the path expressed as list of fragments to the package.
     */
    public void startPackage(Identifier pathToPackage);

    /**
     * Notifies the end of a package parsing.
     */
    public void endPackage();

    /**
     * Notifies the begin of an interface definition.
     *
     * @param pathToInterface the path expressed as list of fragments to the interface.
     * @param extendedInterfaces the list of extended interfaces.
     */
    public void startInterface(Identifier pathToInterface, Identifier[] extendedInterfaces);

    /**
     * Notifies the end of an interface.
     */
    public void endInterface();

    /**
     * Notifies the begin of a class parsing.
     * The class is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the class modifiers.
     * @param visibility the class visibility level.
     * @param pathToClass the path to the class.
     * @param extendedClass the fully qualified pathToClass of the class extended by the starting class.
     * @param implementedInterfaces the fully qualified names for the interfaces implemented by the starting class.
     */
    public void startClass(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToClass,
            Identifier extendedClass,
            Identifier[] implementedInterfaces
    );

    /**
     * Notifies the end of a class parsing.
     */
    public void endClass();

    /**
     * Notifies the begin of an enumeration class parsing.
     *
     * The enumeration is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the enumeration modifiers.
     * @param visibility the visibility level of the enumeration.
     * @param pathToEnumeration the fully qualified path to the enumeration.
     * @param elements the list of the elements of the enumeration.
     */
    public void startEnumeration(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToEnumeration,
            String[] elements
    );

    /**
     * Notifies the end of the enumeration class.
     */
    public void endEnumeration();

    /**
     * Notifies the parsing of an attribute.
     *
     * The attribute is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the attribute modifiers.
     * @param visibility the attribute visibility level.
     * @param pathToAttribute the absolute path to the attribute.
     * @param type the type of the attribute.
     * @param value the init value for the attribute. 
     */
    public void attribute(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToAttribute,
            JavaCodeModel.JType type,
            String value
    );

    /**
     * Notifies the parsing of a constructor.
     *
     * The length of paramterSize and parameterNames is the same,
     * the association between these elements is done positionally.
     *
     * @param modifiers the constructor modifiers.
     * @param visibility the visibility level for the constructor.
     * @param signatureHashCode the signature hash code. 
     * @param parameterNames the array of parameter names.
     * @param parameterTypes the array of parameter types.
     * @param exceptions exceptions raised by the constructor.
     *
     */
    public void constructor(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            int signatureHashCode,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.ExceptionType[] exceptions
    );

    /**
     * Notifies the parsing of a method.
     *
     * The method is expected to be FULLY QUALIFIED.
     *
     * The length of parameterSize and parameterNames is the same,
     * the association between these elements is done positionally.
     *
     * @param modifiers the method modifiers.
     * @param visibility the method visibility level.
     * @param pathToMethod the fully qualified path to the method.
     * @param signatureHashCode the signature hash code.
     * @param parameterNames the array of parameter names.
     * @param parameterTypes the array of parameter types.
     * @param returnType the return type of the method.
     * @param exceptions exceptions raised by the method.
     *
     */
    public void method(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToMethod,
            int signatureHashCode,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.JType returnType,
            JavaCodeModel.ExceptionType[] exceptions
    );

    /**
     * Collects the unresolved types at the end of the parsing.
     *
     * @param unresolvedTypes
     */
    public void unresolvedTypes(String[] unresolvedTypes);

    /**
     * Preloads the given Objects Table with the objects present in the underlying model.
     *
     * @param objectsTable
     */
    //TODO: LOW - Integrate preloading.
    public void preloadObjectsFromModel(ObjectsTable objectsTable);

    /**
     * Adds an error listener to the code handler.
     * @param errorListener
     */
    public void addErrorListener(ErrorListener errorListener);

    /**
     * Removes an error listener from the code handler.
     * @param errorListener
     */
    public void removeErrorListener(ErrorListener errorListener);

}
