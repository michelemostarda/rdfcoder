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


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import net.sourceforge.jrefactory.ast.ASTClassDeclaration;
import net.sourceforge.jrefactory.ast.ASTClassOrInterfaceType;
import net.sourceforge.jrefactory.ast.ASTCompilationUnit;
import net.sourceforge.jrefactory.ast.ASTConstructorDeclaration;
import net.sourceforge.jrefactory.ast.ASTEnumDeclaration;
import net.sourceforge.jrefactory.ast.ASTEnumElement;
import net.sourceforge.jrefactory.ast.ASTFieldDeclaration;
import net.sourceforge.jrefactory.ast.ASTFormalParameter;
import net.sourceforge.jrefactory.ast.ASTGenericNameList;
import net.sourceforge.jrefactory.ast.ASTIdentifier;
import net.sourceforge.jrefactory.ast.ASTImportDeclaration;
import net.sourceforge.jrefactory.ast.ASTInterfaceDeclaration;
import net.sourceforge.jrefactory.ast.ASTMethodDeclaration;
import net.sourceforge.jrefactory.ast.ASTMethodDeclarator;
import net.sourceforge.jrefactory.ast.ASTName;
import net.sourceforge.jrefactory.ast.ASTNameList;
import net.sourceforge.jrefactory.ast.ASTPackageDeclaration;
import net.sourceforge.jrefactory.ast.ASTPrimitiveType;
import net.sourceforge.jrefactory.ast.ASTReferenceType;
import net.sourceforge.jrefactory.ast.ASTResultType;
import net.sourceforge.jrefactory.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.jrefactory.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.jrefactory.ast.ASTVariableDeclaratorId;
import net.sourceforge.jrefactory.ast.AccessNode;
import net.sourceforge.jrefactory.ast.NamedNode;
import net.sourceforge.jrefactory.ast.Node;
import net.sourceforge.jrefactory.ast.SimpleNode;
import net.sourceforge.jrefactory.parser.JavaParser;
import net.sourceforge.jrefactory.parser.ParseException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is able to parse a <i>Java</i> souce file and generates
 * the corresponding triples (according to the ontology).
 */
public class JavaSourceFileParser extends FileParser {

    /**
     * Represents a method attributes container.
     */
    private class Method {

        /**
         * List of modifiers.
         */
        JavaCodeModel.JModifier[] modifiers;

        /**
         * Method visibility.
         */
        JavaCodeModel.JVisibility visibility;

        /**
         * Full method path.
         */
        private Identifier methodPath;

        /**
         * Parameter names.
         */
        private String[] parameterNames;

        /**
         * Parameter types.
         */
        private JavaCodeModel.JType[] parameterTypes;

        /**
         * Method return type.
         */
        JavaCodeModel.JType returnType;

        /**
         * Method exceptions.
         */
        JavaCodeModel.ExceptionType[] exceptions;

        /**
         * Constructor.
         *
         * @param m
         * @param v
         * @param mp
         * @param pns
         * @param pts
         * @param rt
         * @param excs
         */
        Method(
                JavaCodeModel.JModifier[] m,
                JavaCodeModel.JVisibility v,
                Identifier mp,
                String[] pns,
                JavaCodeModel.JType[] pts,
                JavaCodeModel.JType rt,
                JavaCodeModel.ExceptionType[] excs
        ) {
            modifiers = m;
            visibility = v;
            methodPath = mp;
            parameterNames = pns;
            parameterTypes = pts;
            returnType = rt;
            exceptions = excs;            
        }
    }

    /**
     * Internal class logger.
     */
    private static final Logger logger = Logger.getLogger(JavaSourceFileParser.class);


    /**
     * The code handler to be used during processing.
     */
    private JavaCodeHandler javaCodeHandler;

    /**
     * Processes a single compilation unit.
     *
     * @param location
     * @param ast
     */
    public void processCompilationUnit(String location, ASTCompilationUnit ast) {

        javaCodeHandler = (JavaCodeHandler) getParseHandler();

        try {
            javaCodeHandler.startCompilationUnit(location);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Retrieve package.
        List packs = ast.findChildrenOfType(ASTPackageDeclaration.class);
        final String packagePathStr;
        final Identifier packagePath;
        if (packs.size() > 0) {
            packagePathStr = ((ASTName) ((ASTPackageDeclaration) packs.get(0)).findChildrenOfType(ASTName.class).get(0)).getName();
        } else {
            packagePathStr = ""; // Default package.
        }
        packagePath = IdentifierReader.readPackage(packagePathStr);

        try {
            javaCodeHandler.startPackage(packagePath);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {

            // Define inports context.
            ImportsContext importsContext = new ImportsContext();
            importsContext.setContextPackage(packagePathStr);

            // Retrieve imports.
            List imports = ast.findChildrenOfType(ASTImportDeclaration.class);
            for (int i = 0; i < imports.size(); i++) {
                ASTImportDeclaration importDeclaration = (ASTImportDeclaration) imports.get(i);
                String importEntry = ((ASTName) importDeclaration.findChildrenOfType(ASTName.class).get(0)).getName();
                if (importDeclaration.isImportOnDemand()) { //Starred package.
                    try {
                        importsContext.addStarredPackage(importEntry);
                    } catch(IllegalArgumentException iae) {
                        javaCodeHandler.parseError( location, iae.getClass().getName()  + "[" + iae.getMessage() + "]" );
                    }
                } else {
                    importsContext.addFullyQualifiedObject(importEntry);
                }
            }

            // Note: enumerations can be present only at first level.
            extractEnumerations(
                    packagePath,
                    javaCodeHandler,
                    (ASTCompilationUnit) ast.findChildrenOfType(ASTCompilationUnit.class).get(0)
            );

            // Process first level entities.
            processLevel(packagePath, importsContext, ast);

        } finally {
            javaCodeHandler.endPackage();
            javaCodeHandler.endCompilationUnit();
        }
    }

    private void processLevel(Identifier packagePath, ImportsContext importsContext, SimpleNode simpleNode) {

        // Find classes.
        List<ASTClassDeclaration> classes = findChildrenAtSameLevel(simpleNode, ASTClassDeclaration.class);

        // Find interfaces.
        List<ASTInterfaceDeclaration> interfaces = findChildrenAtSameLevel(simpleNode, ASTInterfaceDeclaration.class);

        // Process Class.
        String className;
        String superClass = null;
        for (ASTClassDeclaration classDeclaration : classes ) {

            List<ASTUnmodifiedClassDeclaration> unmodifiedClassDeclarations = classDeclaration.findChildrenOfType(ASTUnmodifiedClassDeclaration.class);
            for( NamedNode unmodifiedClassDeclaration : unmodifiedClassDeclarations) {

                // Class name.
                className = unmodifiedClassDeclaration.getName();
                final Identifier clazz = packagePath.copy().pushFragment(className, JavaCodeModel.CLASS_KEY).build();
                getObjectsTable().addObject(clazz);

                // Class visibility.
                JavaCodeModel.JVisibility clsOrIntVisibility = retrieveVisibility(classDeclaration);

                // Find super class.
                List classOrInterface = unmodifiedClassDeclaration.findChildrenOfType(ASTClassOrInterfaceType.class);
                if (classOrInterface.size() > 0) {   // Has super Class.
                    superClass = retrieveObjectName( ((ASTClassOrInterfaceType) classOrInterface.get(0)).findChildrenOfType(ASTIdentifier.class) );
                }

                javaCodeHandler.startClass(
                        //TODO: HIGH - verify the subsequest row.
                        extractModifiers( classDeclaration ),
                        clsOrIntVisibility,
                        packagePath.copy().pushFragment(className, JavaCodeModel.CLASS_KEY).build(),
                        superClass != null ? qualifyType(importsContext, superClass, new JavaCodeModel.ObjectType(null) ): null,
                        extractImplementedInterfaces(importsContext, unmodifiedClassDeclaration)
                );
                try {
                    extractEnumerations(packagePath, javaCodeHandler, unmodifiedClassDeclaration);
                    extractAttributes  (packagePath, javaCodeHandler, importsContext, unmodifiedClassDeclaration);
                    extractContructors (javaCodeHandler, importsContext, unmodifiedClassDeclaration);
                    extractMethods     (packagePath, javaCodeHandler, importsContext, unmodifiedClassDeclaration);

                    // Recursive invocation.
                    processLevel(
                            packagePath.copy().pushFragment(className, JavaCodeModel.CLASS_KEY).build(),
                            importsContext,
                            unmodifiedClassDeclaration
                    );

                } finally {
                    javaCodeHandler.endClass();
                }

            }
        }

        // Process interface.
        String interfaceName = null;
        for(ASTInterfaceDeclaration interfaceDeclaration : interfaces ) {

            List<ASTUnmodifiedInterfaceDeclaration> unmodifiedInterfaceDeclarations =
                    interfaceDeclaration.findChildrenOfType(ASTUnmodifiedInterfaceDeclaration.class);

            for(NamedNode unmodifiedInterfaceDeclaration : unmodifiedInterfaceDeclarations) {

                // Interface name.
                interfaceName = unmodifiedInterfaceDeclaration.getName();
                final Identifier iface = packagePath.copy().pushFragment(interfaceName, JavaCodeModel.INTERFACE_KEY).build();
                getObjectsTable().addObject(iface);

                javaCodeHandler.startInterface(
                        packagePath.copy().pushFragment(interfaceName, JavaCodeModel.INTERFACE_KEY).build(),
                        extractImplementedInterfaces(importsContext, unmodifiedInterfaceDeclaration)
                );
                try {
                    extractEnumerations(packagePath, javaCodeHandler, unmodifiedInterfaceDeclaration);
                    extractAttributes  (packagePath, javaCodeHandler, importsContext, unmodifiedInterfaceDeclaration);
                    extractMethods     (packagePath, javaCodeHandler, importsContext, unmodifiedInterfaceDeclaration);

                    // Recursive invocation.
                    processLevel(
                            packagePath.copy().pushFragment(interfaceName, JavaCodeModel.INTERFACE_KEY).build(),
                            importsContext,
                            unmodifiedInterfaceDeclaration
                    );

                } finally {
                    javaCodeHandler.endInterface();
                }

            }
        }

    }

    /**
     * List of empty modifiers.
     */
    private static final JavaCodeModel.JModifier[] NO_MODIFIERS = new JavaCodeModel.JModifier[0];

    /**
     * Extracts the list of modifiers associated to this access node.
     * @param accessNode
     * @return
     */
    private JavaCodeModel.JModifier[] extractModifiers(AccessNode accessNode) {
        List<JavaCodeModel.JModifier> modifiers
                = new ArrayList<JavaCodeModel.JModifier>(JavaCodeModel.JModifier.values().length);
        if( accessNode.isAbstract() ) {
            modifiers.add(JavaCodeModel.JModifier.ABSTRACT);
        }
        if( accessNode.isFinal() ) {
            modifiers.add(JavaCodeModel.JModifier.FINAL);
        }
        if( accessNode.isStatic() ) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        if( accessNode.isVolatile() ) {
            modifiers.add(JavaCodeModel.JModifier.VOLATILE);
        }
        if( accessNode.isNative() ) {
            modifiers.add(JavaCodeModel.JModifier.NATIVE);
        }
        if( accessNode.isTransient() ) {
            modifiers.add(JavaCodeModel.JModifier.TRANSIENT);
        }
        if( accessNode.isSynchronized() ) {
            modifiers.add(JavaCodeModel.JModifier.SYNCHRONIZED);
        }
        return  modifiers.size() == 0 ? NO_MODIFIERS : modifiers.toArray(new JavaCodeModel.JModifier[modifiers.size()]);
    }


    /**
     * Returns an array containing the fully quelified types of the implemented interfaces
     * of the current simple node.
     *
     * @param importsContext
     * @param simpleNode
     * @return
     */
    private Identifier[] extractImplementedInterfaces(ImportsContext importsContext, SimpleNode simpleNode) {
        Identifier[] interfacesArray = null;
        List genericNameList = simpleNode.findChildrenOfType(ASTGenericNameList.class);
        for(int i = 0; i < genericNameList.size(); i++) {
            List classOrInterfaceTypes = ( (ASTGenericNameList) genericNameList.get(i) ).findChildrenOfType(ASTClassOrInterfaceType.class);
            interfacesArray = new Identifier[classOrInterfaceTypes.size()];
            for(int j = 0; j < classOrInterfaceTypes.size(); j++) {
                List identifierList = ( (ASTClassOrInterfaceType) classOrInterfaceTypes.get(j)).findChildrenOfType(ASTIdentifier.class);
                interfacesArray[j] = qualifyType(importsContext, retrieveObjectName(identifierList), new JavaCodeModel.InterfaceType(null) );
            }
        }
        return interfacesArray;
    }

    private void extractAttributes(
            Identifier packagePath,
            JavaCodeHandler ch,
            ImportsContext importsContext,
            NamedNode namedNode
    ) {
        List attributes = namedNode.findChildrenOfType(ASTFieldDeclaration.class);
        String attributeName;
        JavaCodeModel.JVisibility attributeVisibility;
        JavaCodeModel.JType attributeType;
        for (int f = 0; f < attributes.size(); f++) {
            ASTFieldDeclaration fieldDeclaration = (ASTFieldDeclaration) attributes.get(f);

            // Retrieve name.
            ASTVariableDeclaratorId variableDeclaratorId =
                    (ASTVariableDeclaratorId) fieldDeclaration.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
            attributeName = variableDeclaratorId.getName();

            // Visibility.
            attributeVisibility = retrieveVisibility(fieldDeclaration);
            
            // Retrieve array size.
            int arraySize;
             boolean isArray;
            List referenceTypes = fieldDeclaration.findChildrenOfType(ASTReferenceType.class);
            if( referenceTypes.size() > 0 ) {
                 ASTReferenceType astReferenceType = (ASTReferenceType) referenceTypes.get(0);
                 arraySize = astReferenceType.getArrayCount();
                 isArray = arraySize > 0;
            } else {
                arraySize = 0;
                isArray = false;
            }

            // Retrieve type.
            List primitiveType = fieldDeclaration.findChildrenOfType(ASTPrimitiveType.class);
            if (primitiveType.size() > 0) { // Primitive type declared.
                JavaCodeModel.JType type = JavaCodeModel.javaTypeToJType(((ASTPrimitiveType) primitiveType.get(0)).getName());
                attributeType =
                        isArray
                                ?
                        new JavaCodeModel.ArrayType(type, arraySize)
                                :
                        type;
            } else { // Obj identifier declared.
                String typeName = retrieveObjectName( fieldDeclaration.findChildrenOfType(ASTIdentifier.class) );
                JavaCodeModel.JType type = new JavaCodeModel.ObjectType(
                        qualifyType(
                                importsContext,
                                typeName,
                                isArray
                                        ?
                                        new JavaCodeModel.ArrayType(
                                            new JavaCodeModel.ObjectType(null),
                                            arraySize
                                        )
                                        :
                                        new JavaCodeModel.ObjectType(null)
                        )
                );
                attributeType = isArray ? new JavaCodeModel.ArrayType(type, variableDeclaratorId.getArrayCount()) : type;
            }

            ch.attribute(
                    extractModifiers(fieldDeclaration),
                    attributeVisibility,
                    qualifyAttribute(packagePath, attributeName), attributeType, null);
        }
    }

    private void extractContructors(JavaCodeHandler ch, ImportsContext importsContext, NamedNode namedNode) {
        // Constructors.
        List constructors = namedNode.findChildrenOfType(ASTConstructorDeclaration.class);
        ASTConstructorDeclaration constructorDeclaration;
        for(int c = 0; c < constructors.size(); c++) {

            constructorDeclaration = ((ASTConstructorDeclaration) constructors.get(c));
            List formalParameters = constructorDeclaration.findChildrenOfType(ASTFormalParameter.class);

            // Extract parameters.
            ASTFormalParameter formalParameter;
            List<String> parameterNames = new ArrayList<String>();
            List<JavaCodeModel.JType> parameterTypes = new ArrayList<JavaCodeModel.JType>();
            for( int fp = 0; fp < formalParameters.size(); fp++ ) {
                formalParameter = (ASTFormalParameter) formalParameters.get(fp);
                parameterNames.add(
                        ( (ASTVariableDeclaratorId) formalParameter.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getName()
                );
                parameterTypes.add( extractFormalparameterType(importsContext, formalParameter) );
            }

            // Extract exceptions.
            JavaCodeModel.ExceptionType[] exceptions = extractExceptions(importsContext, constructorDeclaration);

            ch.constructor(
                    extractModifiers(constructorDeclaration),
                    retrieveVisibility(constructorDeclaration),
                    c,
                    parameterNames.toArray( new String[parameterNames.size()] ),
                    parameterTypes.toArray( new JavaCodeModel.JType[parameterTypes.size()] ),
                    exceptions
            );
        }
    }

    private void extractMethods(
            Identifier packagePath,
            JavaCodeHandler ch,
            ImportsContext importsContext,
            NamedNode namedNode
    ) {
        List methods = namedNode.findChildrenOfType(ASTMethodDeclaration.class);
        String methodName;
        JavaCodeModel.JVisibility methodVisibility;
        boolean returnTypeIsArray;
        int returnTypeArraySize;
        JavaCodeModel.JType returnType;
        List<JavaSourceFileParser.Method> methodsBuffer = new ArrayList<JavaSourceFileParser.Method>();
        for (int m = 0; m < methods.size(); m++) {
            ASTMethodDeclaration methodDeclaration = (ASTMethodDeclaration) methods.get(m);

            // Retrieve method name.
            methodName = ((ASTMethodDeclarator) methodDeclaration.findChildrenOfType(ASTMethodDeclarator.class).get(0)).getName();

            // Retrieve method visibility.
            methodVisibility = retrieveVisibility(methodDeclaration);

            // Return type is array.
            List referenceType = methodDeclaration.findChildrenOfType(ASTReferenceType.class);
            if (referenceType.size() > 0) {
                returnTypeArraySize = ((ASTReferenceType) referenceType.get(0)).getArrayCount();
                returnTypeIsArray = returnTypeArraySize > 0;
            } else {
                returnTypeArraySize = 0;                
                returnTypeIsArray = false;
            }


            // Find return type.
            ASTResultType resultType = (ASTResultType) methodDeclaration.findChildrenOfType(ASTResultType.class).get(0);
            List primitiveType = resultType.findChildrenOfType(ASTPrimitiveType.class);
            if (primitiveType.size() > 0) { // Result type is primitive.
                JavaCodeModel.JType type = JavaCodeModel.javaTypeToJType(((ASTPrimitiveType) primitiveType.get(0)).getName());
                if( returnTypeIsArray ) {
                    returnType = new JavaCodeModel.ArrayType(type, returnTypeArraySize);
                } else {
                   returnType = type;
                }
            } else if (resultType.findChildrenOfType(ASTIdentifier.class).size() > 0) { // Result type is Object.
                Identifier qualifiedType = qualifyType(
                        importsContext,
                        retrieveObjectName( resultType.findChildrenOfType(ASTIdentifier.class) ),
                        returnTypeIsArray
                                ?
                                new JavaCodeModel.ArrayType(
                                    new JavaCodeModel.ObjectType(null),
                                    returnTypeArraySize
                                )
                                :
                                new JavaCodeModel.ObjectType(null)
                );
                returnType = (
                        returnTypeIsArray
                                ?
                        new JavaCodeModel.ArrayType( new JavaCodeModel.ObjectType(qualifiedType), returnTypeArraySize)
                                :
                        new JavaCodeModel.ObjectType(qualifiedType)
                );
            } else { // Void type.
                returnType = JavaCodeModel.VOID;
            }

            // Extract formal parameters.
            List formalParameters = methodDeclaration.findChildrenOfType(ASTFormalParameter.class);
            ASTFormalParameter formalParameter;
            List<String> parameterNames = new ArrayList();
            List<JavaCodeModel.JType> parameterTypes = new ArrayList();
            for (int p = 0; p < formalParameters.size(); p++) {
                formalParameter = (ASTFormalParameter) formalParameters.get(p);
                // Parameter identifier.
                parameterNames.add(((ASTVariableDeclaratorId) formalParameter.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getName());
                // Parameter type.
                parameterTypes.add(extractFormalparameterType(importsContext, formalParameter));
            }

            // Extract exceptions.
            JavaCodeModel.ExceptionType[] exceptions = extractExceptions(importsContext, methodDeclaration);

            // Storing method.
            methodsBuffer.add(
                    new JavaSourceFileParser.Method(
                            extractModifiers(methodDeclaration),
                            methodVisibility,
                            qualifyMethod(packagePath, methodName),
                            parameterNames.toArray(new String[parameterNames.size()]),
                            parameterTypes.toArray(new JavaCodeModel.JType[parameterTypes.size()]),
                            returnType,
                            exceptions

                    )
            );
        }

        // Group signatures by name.
        Map<Identifier, List<Method>> methodsMap = new HashMap<Identifier, List<Method>>();
        Iterator<Method> iter = methodsBuffer.iterator();
        JavaSourceFileParser.Method curr;
        while (iter.hasNext()) {
            curr = iter.next();
            List signatures = methodsMap.get(curr.methodPath);
            if (signatures == null) {
                signatures = new ArrayList();
                methodsMap.put(curr.methodPath, signatures);
            }
            signatures.add(curr);
        }

        // Store methods.
        Iterator<Map.Entry<Identifier, List<JavaSourceFileParser.Method>>> entries = methodsMap.entrySet().iterator();
        Map.Entry<Identifier, List<JavaSourceFileParser.Method>> entry;
        while (entries.hasNext()) {
            entry = entries.next();
            for (int i = 0; i < entry.getValue().size(); i++) {
                ch.method(
                        entry.getValue().get(i).modifiers,
                        entry.getValue().get(i).visibility,
                        entry.getKey(),
                        i,
                        entry.getValue().get(i).parameterNames,
                        entry.getValue().get(i).parameterTypes,
                        entry.getValue().get(i).returnType,
                        entry.getValue().get(i).exceptions
                );
            }
        }
    }

    private void extractEnumerations(Identifier packagePath, JavaCodeHandler ch, SimpleNode simpleNode) {
        List enumDeclarations = simpleNode.findChildrenOfType(ASTEnumDeclaration.class);
        ASTEnumDeclaration enumDeclaration;
        for(int ed = 0; ed < enumDeclarations.size(); ed++) {
            enumDeclaration = (ASTEnumDeclaration) enumDeclarations.get(ed);
             JavaCodeModel.JVisibility visibility = retrieveVisibility(enumDeclaration);
            ASTIdentifier indentifier = (ASTIdentifier) enumDeclaration.findChildrenOfType(ASTIdentifier.class).get(0);
            String identifier = indentifier.getName();
            List enumElements = enumDeclaration.findChildrenOfType(ASTEnumElement.class);
            List elements = new ArrayList();
            for(int ee = 0; ee < enumElements.size(); ee++ ) {
                elements.add( ((ASTEnumElement) enumElements.get(ee)).getName() );
            }
            ch.startEnumeration(
                    extractModifiers(enumDeclaration),
                    visibility,
                    qualifyEnumeration(packagePath, identifier),
                    (String[]) elements.toArray(new String[elements.size()])
            );
            //TODO: HIGH - extract fields + methods.
            ch.endEnumeration();
            elements.clear();
        }
    }

    private JavaCodeModel.JType extractFormalparameterType(ImportsContext importsContext, ASTFormalParameter formalParameter) {

        // Retrieve array size.
        int arraySize;
        List referenceTypes = formalParameter.findChildrenOfType(ASTReferenceType.class);
        if( referenceTypes.size() > 0 ) {
            ASTReferenceType referenceType = (ASTReferenceType) referenceTypes.get(0);
            arraySize = referenceType.getArrayCount();
        } else {
            arraySize = 0;
        }

        // Retrieve type.
        List primitiveType = formalParameter.findChildrenOfType(ASTPrimitiveType.class);
        if (primitiveType.size() > 0) { // Result type is Primitive.
            return JavaCodeModel.javaTypeToJType(((ASTPrimitiveType) primitiveType.get(0)).getName());
        } else { // Result type is Object.
            String typeName =  retrieveObjectName( formalParameter.findChildrenOfType(ASTIdentifier.class) );
            if(arraySize > 0) {
                return new JavaCodeModel.ArrayType(
                        new JavaCodeModel.ObjectType(
                            qualifyType(
                                    importsContext,
                                    typeName,
                                    new JavaCodeModel.ArrayType(new JavaCodeModel.ObjectType(null), arraySize)
                            )
                        ),
                         arraySize
                );
            } else {
                return new JavaCodeModel.ObjectType(
                        qualifyType(
                            importsContext,
                            typeName,
                            new JavaCodeModel.ObjectType(null)
                        )
                );
            }
        }
    }

    private JavaCodeModel.ExceptionType[] extractExceptions(ImportsContext importsContext,AccessNode accessNode) {
        List nameLists = accessNode.findChildrenOfType(ASTNameList.class);
        JavaCodeModel.ExceptionType[] exceptions = null;
        if(nameLists.size() > 0) {
            ASTNameList nameList = (ASTNameList) nameLists.get(0);
            List namesList = nameList.findChildrenOfType(ASTName.class);
            String exceptionName;
            exceptions = new JavaCodeModel.ExceptionType[namesList.size()];
            for( int i = 0; i < namesList.size(); i++ ) {
                ASTName name = (ASTName) namesList.get(i);
                exceptionName = retrieveObjectName(name.findChildrenOfType(ASTIdentifier.class));
                exceptions[i] = new JavaCodeModel.ExceptionType(
                    qualifyType(importsContext, exceptionName, new JavaCodeModel.ExceptionType(null))
                );
            }
        }
        return exceptions;
    }

    private static String printArray(final int arrayCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrayCount; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }

    private static String tab(int t) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    public void parse(File file) throws ParserException, IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            throw new ParserException(fnfe, file.getAbsolutePath());
        }
        JavaParser jp = new JavaParser(fis);
        ASTCompilationUnit ast;
        try {
            ast = jp.CompilationUnit();
            processCompilationUnit(file.getAbsolutePath(), ast);
        } catch (ParseException pe) {
            throw new ParserException( pe, file.getAbsolutePath() );
        } finally {
            fis.close();
        }
    }

    /**
       * Returns the relative depth of a descendant in respect with the parent.
       * @param parent
       * @param descendant
       * @return
       */
      private int childRelativeDepth(Node parent, Node descendant) {
          Node childParent = descendant;
          int depth = 0;
          while(childParent != parent) {
              childParent = childParent.jjtGetParent();
              depth++;
              if(childParent == null) {
                  throw new IllegalStateException();
              }
          }
          return depth;
      }

      /**
       * Returns a list of descendant of simpleNode of type maching the given clazz
       * where all results are at the same depth level of the first child found.
       *
       * @param simpleNode
       * @param clazz
       * @return
       */
      private List findChildrenAtSameLevel(SimpleNode simpleNode, Class clazz) {
          List<SimpleNode> children = simpleNode.findChildrenOfType(clazz);
          if(children.size() == 0) {
              return children;
          }
          final int depthLevel = childRelativeDepth(simpleNode, children.get(0));
          List sameDepth = new ArrayList();
          for(SimpleNode child : children) {
              if( childRelativeDepth(simpleNode, child ) == depthLevel ) {
                  sameDepth.add(child);
              }
          }
          return sameDepth;
      }

      /**
       * Returns the comlete name of an object.
       *
       * @param list
       * @return
       */
      private String retrieveObjectName(List list) {
          String type = "";
          for(int i = 0; i < list.size(); i++) {
              type += ( (ASTIdentifier) list.get(i) ).getName() + ( i < list.size() - 1 ? "." : "");
          }
          return type;
      }

      /**
       * Returns the JVisibility of an access node.
       *
       * @param an
       * @return
       */
      private JavaCodeModel.JVisibility retrieveVisibility(AccessNode an) {
          if (an.isPublic()) {
              return JavaCodeModel.JVisibility.PUBLIC;
          } else if (an.isProtected()) {
              return JavaCodeModel.JVisibility.PROTECTED;
          } else if (an.isPackage()) {
              return JavaCodeModel.JVisibility.DEFAULT;
          } else {
              return JavaCodeModel.JVisibility.PRIVATE;
          }
      }

      /**
       * Retruns the qualified typeName for a typeName string.
       *
       * @param typeName
       * @return
       */
      private Identifier qualifyType(ImportsContext importsContext, String typeName, JavaCodeModel.JType type) {
          Identifier qualifiedObject = importsContext.qualifyType(getObjectsTable(), typeName);
          if (qualifiedObject == null) {
              Identifier tempId = javaCodeHandler.generateTempUniqueIdentifier();
              if(type instanceof JavaCodeModel.ObjectType) {
                  ((JavaCodeModel.ObjectType) type).setInternalIdentifier(tempId);
              } else if( type instanceof JavaCodeModel.InterfaceType) {
                  ((JavaCodeModel.InterfaceType) type).setInternalIdentifier(tempId);
              } else if( type instanceof JavaCodeModel.ArrayType) {
                  ((JavaCodeModel.ArrayType) type).setInternalIdentifier(tempId);
              } else {
                  throw new IllegalStateException();
              }
              getObjectsTable().addUnresolvedType(typeName, type, importsContext);
              return tempId;
          }
          return qualifiedObject;
      }

      private Identifier qualifyAttribute(Identifier packagePath, String attributeName) {
          return packagePath.copy().pushFragment(attributeName, JavaCodeModel.ATTRIBUTE_KEY).build();
      }

      private Identifier qualifyMethod(Identifier packagePath, String methodName) {
          return packagePath.copy().pushFragment(methodName, JavaCodeModel.METHOD_KEY).build();
      }

      private Identifier qualifyEnumeration(Identifier packagePath, String enumerationName) {
          return packagePath.copy().pushFragment(enumerationName, JavaCodeModel.ENUMERATION_KEY).build();
      }


}
