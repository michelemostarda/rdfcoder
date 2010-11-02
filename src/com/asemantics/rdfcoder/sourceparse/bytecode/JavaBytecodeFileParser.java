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


package com.asemantics.rdfcoder.sourceparse.bytecode;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import com.asemantics.rdfcoder.sourceparse.FileParser;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.asemantics.rdfcoder.sourceparse.ParserException;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A parser for compiled <i>Java</i> classes.
 */
public class JavaBytecodeFileParser extends FileParser {

    /**
     * Name that <i>BCEL</i> uses to describe constructor methods.
     */
    protected static final String INIT_METHOD = "<init>";

    /**
     * Internal logger.
     */
    private static final Logger logger = Logger.getLogger(JavaBytecodeFileParser.class);

    /**
     * Represents the empty inner class set.
     */
    private static final InnerClass[] INNER_CLASS_EMPTY = new InnerClass[0];

    /**
     * Inner classes stack.
     */
    private Stack<JavaClass> containersStack;

    /**
     * Constructor.
     */
    public JavaBytecodeFileParser() {
        containersStack = new Stack<JavaClass>();
    }

    /**
     * Parses a <i>.class</i> file.
     * 
     * @param classloader
     * @param inputStream
     * @param fileName
     * @throws IOException
     */
    public void parse(JavaBytecodeClassLoader classloader, InputStream inputStream, String fileName)
    throws IOException, ParserException {
        containersStack.clear();

        // Parse bytecode.
        ClassParser classParser = new ClassParser( inputStream, fileName );
        JavaClass javaClass = classParser.parse();

        final JavaCodeHandler javaCodeHandler = (JavaCodeHandler) getParseHandler();
        ObjectsTable objectsTable = getObjectsTable();

        // Package.
        javaCodeHandler.startPackage(IdentifierReader.readPackage( javaClass.getPackageName() ) );
        try {

            // Objects table update.
            objectsTable.addObject( IdentifierReader.readFullyQualifiedClass(javaClass.getClassName()) );

            // Recursive processing.
            Set processed = new HashSet();
            processClass(javaCodeHandler, classloader, processed, javaClass);

        } catch(Exception e) {
            logger.error("An error occurred while parsing bytecode file.", e);
            throw new ParserException("An error occurred while parsing bytecode file.", e);
        } finally {
            // Close package.
            javaCodeHandler.endPackage();
        }
    }

    public void parse(JavaBytecodeClassLoader classloader, File file) throws IOException, ParserException {
        if(file == null) {
            throw new NullPointerException();
        }
        if( ! file.exists() ) {
            throw new IllegalArgumentException("cannot find file: " + file.getAbsolutePath());
        }

        InputStream inputStream = new FileInputStream(file);
        parse(classloader, inputStream, file.getAbsolutePath());
        inputStream.close();
    }

    public void parse(File file) throws IOException, ParserException {
        //TODO: HIGH - check this method: JavaBytecodeClassLoader must not be null.
        parse(null, file);
    }

    protected void processClass(
            JavaCodeHandler javaCodeHandler,
            JavaBytecodeClassLoader classloader,
            Set<String> processed,
            JavaClass javaClass
    ) throws ParserException {

        if(logger.isDebugEnabled()) {
            logger.debug("Processing class " + javaClass);
        }

        containersStack.push( javaClass );

        javaCodeHandler.startCompilationUnit(javaClass.toString());

        // The stack is currectly handled despite errors.
        try {

            Field[] fields = javaClass.getFields();
            List<String> enumElements = new ArrayList<String>();

            // Extracts class modifiers.
            JavaCodeModel.JModifier[] modifiers = extractModifiers(javaClass);

            if( javaClass.isEnum() ) { // Enumeration.
                //TODO: MED - Check this solution.
                for(int i = 0; i < fields.length - 1; i++) {   // Skip last element containing $VALUE
                    if( fields[i].getType().toString().equals( javaClass.getClassName() ) ) { // Excluding real enumElements.
                        enumElements.add( fields[i].getName() );
                    }
                }
                javaCodeHandler.startEnumeration(
                        modifiers,
                        toVisibility( javaClass ),
                        toQualifiedEnumName( javaClass ),
                        enumElements.toArray( new String[ enumElements.size() ] )
                );
            } else if( javaClass.isClass() ) { // Class.
                javaCodeHandler.startClass(
                   modifiers,
                   toVisibility(javaClass),
                   toQualifiedClassName(javaClass),
                   toQualifiedSuperClassName(javaClass),
                   toQualifiedInterfaces(javaClass)
                );
            } else if( javaClass.isInterface() ) { // Interface.
                javaCodeHandler.startInterface(
                   toQualifiedInterfaceName(javaClass),
                   toQualifiedInterfaces(javaClass)
                );
            } else {
                throw new IllegalStateException();
            }

            // Fields.
            for (Field field : fields) {
                if (enumElements.contains(field.getName())) {
                    continue;
                }
                javaCodeHandler.attribute(
                        extractModifiers(field),
                        toVisibility(field),
                        toQualifiedAttribute(field),
                        bcelTypeToJType(field.getType()),
                        toConstantValue(field.getConstantValue())
                );
            }
            enumElements.clear();


            // Methods.
            Method[] methods = javaClass.getMethods();
            for(int i = 0; i < methods.length; i++) {
                if( INIT_METHOD.equals(methods[i].getName()) ) { // Constructors.
                    javaCodeHandler.constructor(
                        extractModifiers(methods[i]),
                        toVisibility( methods[i] ),
                        methods[i].getSignature().hashCode(),
                        toParameterNames( methods[i].getArgumentTypes()  ),
                        toParameterTypes( methods[i].getArgumentTypes()  ),
                        toExceptionTypes( methods[i].getExceptionTable() )
                    );
                } else {
                    javaCodeHandler.method(
                            extractModifiers(methods[i]),
                            toVisibility(methods[i]),
                            toQualifiedMethod(methods[i]),
                            methods[i].getSignature().hashCode(),
                            toParameterNames(methods[i].getArgumentTypes()),
                            toParameterTypes(methods[i].getArgumentTypes()),
                            bcelTypeToJType(methods[i].getReturnType()),
                            toExceptionTypes(methods[i].getExceptionTable())
                    );
                }
            }

            // Inner classes. If class loader is not defined the inner classes are ignored.
            if(classloader != null) {
                String innerClassName;
                InnerClass[] innerClasses = innerClasses(javaClass);
                ConstantPool constantPool = javaClass.getConstantPool();
                for (InnerClass innerClass1 : innerClasses) {
                    innerClassName = constantPool.getConstantString(
                            innerClass1.getInnerClassIndex(),
                            Constants.CONSTANT_Class
                    );
                    if (processed.contains(innerClassName)) {
                        continue;
                    } else {
                        processed.add(innerClassName);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Processing inner class: " + innerClassName);
                    }
                    innerClassName = Utility.compactClassName(innerClassName);
                    JavaClass innerClass;
                    try {
                        innerClass = classloader.loadClass(innerClassName);
                        if (innerClass == null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Cannot load class: " + innerClassName);
                            }
                        }
                    } catch (IOException ioe) {
                        javaCodeHandler.parseError(javaClass.getClassName(), "[" + ioe.getClass().getName() + "]: " + ioe.getMessage());
                        ioe.printStackTrace();
                    }
                }
            }

        } catch(Exception e) {
            e.printStackTrace(); // TODO: HIGH - Remove me.
            throw new ParserException( String.format("Error while parsing class %s", javaClass.getClassName() ), e);   
        } finally {

            // End container.
            if( javaClass.isClass() ) {
                javaCodeHandler.endClass();
            } else if( javaClass.isInterface() ) {
                javaCodeHandler.endInterface();
            } else if(javaClass.isEnum()) {
                javaCodeHandler.endEnumeration();
            } else {
                throw new IllegalStateException();
            }

            containersStack.pop();
            javaCodeHandler.endCompilationUnit();
        }
    }

    private InnerClass[] innerClasses(JavaClass javaClass) {
        Attribute[] attributes = javaClass.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute instanceof InnerClasses) {
                InnerClasses innerClasses = (InnerClasses) attribute;
                return innerClasses.getInnerClasses();
            }
        }
        return INNER_CLASS_EMPTY;
    }

    private JavaCodeModel.JVisibility toVisibility(AccessFlags af) {
        if( af.isPublic() ) {
            return JavaCodeModel.JVisibility.PUBLIC;
        } else if( af.isProtected() ) {
            return JavaCodeModel.JVisibility.PROTECTED;
        } else if( af.isPrivate() ) {
            return JavaCodeModel.JVisibility.PRIVATE;
        } else {
            return JavaCodeModel.JVisibility.DEFAULT;
        }
    }

    private Identifier getContainerPath() {
        if( containersStack.size() == 0 ) {
            throw new IllegalStateException();
        }
        return IdentifierReader.readFullyQualifiedClass( containersStack.peek().getClassName() );
    }

    private Identifier toQualifiedClassName(JavaClass javaClass) {
        return IdentifierReader.readFullyQualifiedClass( javaClass.getClassName() );
    }

    private Identifier toQualifiedSuperClassName(JavaClass javaClass) {
        return IdentifierReader.readFullyQualifiedClass( javaClass.getClassName() );
    }

    private Identifier toQualifiedInterfaceName(JavaClass javaClass) {
        return IdentifierReader.readFullyQualifiedInterface( javaClass.getClassName() );
    }

    private Identifier toQualifiedEnumName(JavaClass javaClass) {
        return IdentifierReader.readFullyQualifiedEnumeration( javaClass.getClassName() );
    }

    private Identifier[] toQualifiedInterfaces(JavaClass javaClass) {
        JavaClass[] interfaces;
        try {
            interfaces = javaClass.getAllInterfaces();
        } catch (ClassNotFoundException e) {
            return null;
        }
        Identifier[] result = new Identifier[interfaces.length];
        for(int i = 0; i < interfaces.length; i++) {
            result[i] = IdentifierReader.readFullyQualifiedInterface( interfaces[i].getClassName() );
        }
        return result;
    }

    private Identifier toQualifiedAttribute(Field field) {
        return getContainerPath().copy().pushFragment(field.getName(), JavaCodeModel.ATTRIBUTE_KEY).build();
    }

    private Identifier toQualifiedMethod(Method method) {
        return getContainerPath().copy().pushFragment(method.getName(), JavaCodeModel.METHOD_KEY).build();
    }

    private String toConstantValue(ConstantValue cv) {
        if(cv == null) {
            return null;
        }
        ConstantPool cp =  cv.getConstantPool();
        Constant c = cp.getConstant( cv.getConstantValueIndex() );
        return c.toString();
    }

    private JavaCodeModel.JType bcelTypeToJType(org.apache.bcel.generic.Type type) {

        // Convert basic types.
        if(type == org.apache.bcel.generic.Type.VOID ) {
             return JavaCodeModel.VOID;
        } else if( type == org.apache.bcel.generic.Type.BOOLEAN ) {
            return JavaCodeModel.BOOL;
        } else if( type == org.apache.bcel.generic.Type.INT ) {
            return JavaCodeModel.INT;
        } else if( type == org.apache.bcel.generic.Type.SHORT ) {
            return JavaCodeModel.SHORT;
        } else if( type == org.apache.bcel.generic.Type.BYTE ) {
            return JavaCodeModel.BYTE;
        } else if( type == org.apache.bcel.generic.Type.LONG ) {
            return JavaCodeModel.LONG;
        } else if( type == org.apache.bcel.generic.Type.DOUBLE) {
            return JavaCodeModel.DOUBLE;
        } else if( type == org.apache.bcel.generic.Type.FLOAT ) {
            return JavaCodeModel.FLOAT;
        } else if( type == org.apache.bcel.generic.Type.CHAR ) {
            return JavaCodeModel.CHAR;
        }

        // Convert object types.
        if( type instanceof org.apache.bcel.generic.ObjectType) {
            org.apache.bcel.generic.ObjectType bcelOT = (org.apache.bcel.generic.ObjectType) type;
            return new JavaCodeModel.ObjectType( IdentifierReader.readFullyQualifiedClass(bcelOT.getClassName() ) );
        }

        // Array types.
        if ( type instanceof org.apache.bcel.generic.ArrayType ) {
            org.apache.bcel.generic.ArrayType arrayType = (org.apache.bcel.generic.ArrayType) type;
            return new JavaCodeModel.ArrayType(
                    bcelTypeToJType(arrayType.getBasicType() ),
                    arrayType.getDimensions()
            );
        }

        throw new IllegalArgumentException("cannot convert " + type);

    }

    private String toParameterName(org.apache.bcel.generic.Type type ) {
        // Generate name for basic type.
        if( type == org.apache.bcel.generic.Type.BOOLEAN ) {
            return "b";
        } else if( type == org.apache.bcel.generic.Type.INT ) {
            return "i";
        } else if( type == org.apache.bcel.generic.Type.SHORT ) {
            return "s";
        } else if( type == org.apache.bcel.generic.Type.BYTE ) {
            return "e";
        } else if( type == org.apache.bcel.generic.Type.LONG ) {
            return "l";
        } else if( type == org.apache.bcel.generic.Type.DOUBLE) {
            return "d";
        } else if( type == org.apache.bcel.generic.Type.FLOAT ) {
            return "f";
        } else if( type == org.apache.bcel.generic.Type.CHAR ) {
            return "c";
        }

        // Generate name for object type.
        if( type instanceof org.apache.bcel.generic.ObjectType) {
            org.apache.bcel.generic.ObjectType bcelOT = (org.apache.bcel.generic.ObjectType) type;
            String cn = bcelOT.getClassName();
            int i = cn.lastIndexOf(".");
            String result = cn.substring(i + 1);
            if("String".equals(result)) {
                return "s";
            } else if("Object".equals(result)) {
                return "o";
            } else {
                return result;
            }
        }

        // Generate name for array type.
        if ( type instanceof org.apache.bcel.generic.ArrayType ) {
            org.apache.bcel.generic.ArrayType arrayType = (org.apache.bcel.generic.ArrayType) type;
            String typeName = toParameterName( arrayType.getBasicType() );
            return typeName + "s";
        }

        throw new IllegalArgumentException("cannot generate name for " + type);
    }


    private String[] toParameterNames( org.apache.bcel.generic.Type[] type ) {
        HashMap<String,Integer> namesMap = new HashMap<String,Integer>();
        String[] names = new String[type.length];
        String designedName;
        for(int i = 0; i < type.length; i++) {
            designedName = toParameterName( type[i] ).toLowerCase();
            Integer occurrences = namesMap.get(designedName);
            if(occurrences == null) { occurrences = 0; }
            occurrences++;
            namesMap.put(designedName, occurrences);
            names[i] = designedName + ( occurrences == 1 ? "" : occurrences );
        }
        namesMap.clear();
        return names;
    }

    private JavaCodeModel.JType[] toParameterTypes( org.apache.bcel.generic.Type[] types ) {
        JavaCodeModel.JType[] result = new JavaCodeModel.JType[types.length];
        for(int i = 0; i < types.length; i++) {
            result[i] = bcelTypeToJType(types[i]);
        }
        return result;
    }

    private JavaCodeModel.ExceptionType[] toExceptionTypes(ExceptionTable exceptionTable) {
        if(exceptionTable == null) {
            return null;
        }
        String[] exceptionNamesStr = exceptionTable.getExceptionNames();
        Identifier[] exceptionNames = new Identifier[exceptionNamesStr.length];
        int i = 0;
        for( String exceptionNameStr : exceptionNamesStr ) {
            exceptionNames[i++] = IdentifierReader.readFullyQualifiedClass(exceptionNameStr);
        }
        JavaCodeModel.ExceptionType[] exceptions = new JavaCodeModel.ExceptionType[exceptionNames.length];
        for(i = 0; i < exceptionNames.length; i++) {
            exceptions[i] = new JavaCodeModel.ExceptionType(exceptionNames[i]);
        }
        return exceptions;
    }

    /**
     * Extracts the list of modifiers associated to the given entity.
     * @param accessFlags
     * @return
     */
    private JavaCodeModel.JModifier[] extractModifiers(AccessFlags accessFlags) {
        List<JavaCodeModel.JModifier> modifiers =
                new ArrayList<JavaCodeModel.JModifier>(JavaCodeModel.JModifier.values().length);
        
        if( accessFlags.isAbstract() ) {
            modifiers.add(JavaCodeModel.JModifier.ABSTRACT);
        }
        if( accessFlags.isFinal() ) {
            modifiers.add(JavaCodeModel.JModifier.FINAL);
        }
        if( accessFlags.isStatic() ) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        if( accessFlags.isVolatile() ) {
            modifiers.add(JavaCodeModel.JModifier.VOLATILE);
        }
        if( accessFlags.isNative() ) {
            modifiers.add(JavaCodeModel.JModifier.NATIVE);
        }
        if( accessFlags.isTransient() ) {
            modifiers.add(JavaCodeModel.JModifier.TRANSIENT);
        }
        if( accessFlags.isSynchronized() ) {
            modifiers.add(JavaCodeModel.JModifier.SYNCHRONIZED);
        }
        return modifiers.toArray(new JavaCodeModel.JModifier[modifiers.size()]);
    }

}
