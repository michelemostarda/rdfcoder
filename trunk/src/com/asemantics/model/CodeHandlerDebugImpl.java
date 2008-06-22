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

import com.asemantics.sourceparse.JavadocEntry;
import com.asemantics.sourceparse.ObjectsTable;

/**
 * A <code>CodeHandler</code> implementation
 * printing on System.out all the received events.
 */
public class CodeHandlerDebugImpl implements CodeHandler {

    public void startParsing(String libraryName, String location) {
        System.out.println("Start parsing process of library '" + libraryName + "' at location '" + location + "'");
    }

    public void endParsing() {
        System.out.println("End parsing process");
    }

    public void startCompilationUnit(String identifier) {
        System.out.println("Start compilation unit: " + identifier);
    }

    public void endCompilationUnit() {
        System.out.println("End compilation unit");
    }

    public void startPackage(String pathToPackage) {
        System.out.println("Start package: " + pathToPackage);
    }

    public void endPackage() {
        System.out.println("End package");
    }

    public void startInterface(String pathToInterface, String[] extendedInterfaces) {
        System.out.print("Start interface " + pathToInterface + " extending < ");
        for(int i = 0; i < extendedInterfaces.length; i++) {
            System.out.print(" " +  extendedInterfaces[i] + " ");
        }
        System.out.println(">");
    }

    public void endInterface() {
        System.out.println("End interface");
    }

    public void startClass(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String pathToClass,
            String extendedClass,
            String[] implementedInterfaces
    ) {
        System.out.print(
                "Start class " + pathToClass +
                        " visibility: " + visibility +
                        " modifiers: " + JavaCodeModel.JModifier.toByte(modifiers)
        );
        System.out.print(" extending class " + extendedClass);
        System.out.print(" implemented interfaces < ");
        for(int i = 0; i < implementedInterfaces.length; i++) {
            System.out.print(" " + implementedInterfaces[i] + " ");
        }
        System.out.print(">");
    }

    public void endClass() {
        System.out.println("end class");
    }

    public void startEnumeration(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String pathToEnumeration,
            String[] elements
    ) {
        System.out.print(
                "start enumeration " + pathToEnumeration +
                        " visibility " + visibility  +
                        " modifiers "  + JavaCodeModel.JModifier.toByte(modifiers)
        );
        System.out.println("{");
        for(int i = 0; i < elements.length; i++) {
            System.out.println(elements[i]);
        }
        System.out.println("}");
    }

    public void endEnumeration() {
        System.out.println("end enumeration");
    }

    public void attribute(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String pathToAttribute,
            JavaCodeModel.JType type,
            String value) {
        System.out.print(
                "attribute " + pathToAttribute +
                        " visibility " + visibility +
                        " modifiers " + JavaCodeModel.JModifier.toByte(modifiers)
        );
        System.out.print(" of type " + type + " ");
        System.out.println(" with value " + value);
    }

    public void constructor(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            int overloadIndex,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.ExceptionType[] exceptions
    ) {
        System.out.print(
                "constructor " + overloadIndex +
                        " visibility " + visibility +
                        " modifiers "  + JavaCodeModel.JModifier.toByte(modifiers)
        );
        System.out.println("{");
        printParameters(parameterNames, parameterTypes);
        System.out.println("}");
        printExceptions(exceptions);
    }

    public void method(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            String pathToMethod,
            int overloadIndex,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.JType returnType,
            JavaCodeModel.ExceptionType[] exceptions
    ) {
        System.out.print(
                "method " + pathToMethod + " index " + overloadIndex +
                        " visibility " + visibility +
                        " modifiers " + JavaCodeModel.JModifier.toByte(modifiers)
        );
        System.out.println(" return type " + returnType);
        System.out.println("{");
        printParameters(parameterNames, parameterTypes);
        System.out.println("}");
        printExceptions(exceptions);
    }

    public void parseError(String location, String description) {
        System.out.println("parseError: location: " + location + " description: " + description);
    }

    public void unresolvedTypes(String[] types) {
        System.out.println("unresolvedTypes {");
        for(int i = 0; i < types.length; i++) {
            System.out.println(types[i]);
        }
        System.out.println("}");
    }

    public void preloadObjectsFromModel(ObjectsTable objectsTable) {
        throw new UnsupportedOperationException();
    }

    public void serializeUnresolvedTypeEntries(ObjectsTable objectTable) {
        throw new UnsupportedOperationException();
    }

    public void deserializeUnresolvedTypeEntries(ObjectsTable objectTable) {
        throw new UnsupportedOperationException();
    }

    public void addErrorListener(ErrorListener errorListener) {
        throw new UnsupportedOperationException();
    }

    public void removeErrorListener(ErrorListener errorListener) {
        throw new UnsupportedOperationException();
    }

    public void parsedEntry(JavadocEntry entry) {
        System.out.println("parsedEntry: " + entry );
    }

    public void classJavadoc(JavadocEntry entry, String pathToClass) {
        System.out.println("classJavadoc:" + entry + " at location: " + pathToClass );
    }

    public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
        System.out.println("methodJavadoc: " + entry + " at location: " + pathToMethod);
        System.out.print("(");
        for(String s: signature) { System.out.print(s + ", "); }
        System.out.println(")");
    }

    public String generateTempUniqueIdentifier() {
        return "debug_identifier_" + System.currentTimeMillis();
    }

    public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType) {
        System.out.println("replaceIdentifierWithQualifiedType " + identifier + " => " + qualifiedType);
        return 0;
    }

    private void printParameters(String[] names, JavaCodeModel.JType[] types) {
        for(int i = 0; i < names.length; i++) {
            System.out.println(names[i] + ": " + types[i]);
        }
    }

    private void printExceptions(JavaCodeModel.ExceptionType[] exceptions) {
        if(exceptions == null || exceptions.length == 0) {
            return;
        }
        System.out.print(" throws ");
        System.out.print("<");
        for(int i = 0; i < exceptions.length; i++) {
            System.out.print(" " + exceptions[i] + " ");
        }
        System.out.println(">");
    }
}
