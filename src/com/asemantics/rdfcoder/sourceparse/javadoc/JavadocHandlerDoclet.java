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

package com.asemantics.rdfcoder.sourceparse.javadoc;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import com.asemantics.rdfcoder.model.java.JavadocHandler;
import com.asemantics.rdfcoder.sourceparse.ClassJavadoc;
import com.asemantics.rdfcoder.sourceparse.ConstructorJavadoc;
import com.asemantics.rdfcoder.sourceparse.FieldJavadoc;
import com.asemantics.rdfcoder.sourceparse.MethodJavadoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a <i>doclet</i> able to produce
 * {@link com.asemantics.rdfcoder.model.java.JavadocHandler} messages.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocHandlerDoclet {

    public static final Identifier OBJECT_SUPERCLASS = IdentifierReader.readFullyQualifiedClass("java.lang.Object");

    public static final String SERIALIZATION_FILE_OPTION = "-serializationFile";

    private JavadocHandlerSerializer javadocHandlerSerializer;

    private JavadocHandler javadocHandler;

    public static boolean start(RootDoc root) {
        final JavadocHandlerDoclet doclet = new JavadocHandlerDoclet();
        doclet.handleRoot(root);
        return true;
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return true;
    }

    public static int optionLength(String option) {
        if(option.equals(SERIALIZATION_FILE_OPTION)) {
	        return 2;
        }
        return 0;
    }

    public JavadocHandlerDoclet() {
        javadocHandlerSerializer = new JavadocHandlerSerializer();
        javadocHandler           = javadocHandlerSerializer.getHandler();
    }

    private void handleRoot(RootDoc rootDoc) {
        final File serializationFile = getSerializationFile( rootDoc.options() );

        javadocHandler.startCompilationUnit(rootDoc.name());
        final String position = rootDoc.position() == null ? "<unknown>" : rootDoc.position().toString();
        javadocHandler.startParsing("javadoc-lib", position);
        try {
            ClassDoc[] classes = rootDoc.classes();
            for (ClassDoc clazz : classes) {
                handleClass(clazz);
            }
        } finally {
            javadocHandler.endParsing();
            javadocHandler.endCompilationUnit();
            try {
                javadocHandlerSerializer.serialize(serializationFile);
            } catch (JavadocHandlerSerializerException jhse) {
                throw new JavadocHandlerDocletException("An error occurred while serializing Javadoc.", jhse);
            }
        }
    }

    private JavaCodeModel.JVisibility getVisibility(ProgramElementDoc doc) {
        if(doc.isPublic()) {
            return JavaCodeModel.JVisibility.PUBLIC;
        }
        if(doc.isProtected()) {
            return JavaCodeModel.JVisibility.PROTECTED;
        }
        if(doc.isPrivate()) {
            return JavaCodeModel.JVisibility.PRIVATE;
        }
        return JavaCodeModel.JVisibility.DEFAULT;
    }

    private JavaCodeModel.JModifier[] getModifiers(MethodDoc doc){
        final List<JavaCodeModel.JModifier> modifiers = new ArrayList<JavaCodeModel.JModifier>();
        if(doc.isAbstract()) {
            modifiers.add(JavaCodeModel.JModifier.ABSTRACT);
        }
        if(doc.isFinal()) {
            modifiers.add(JavaCodeModel.JModifier.FINAL);
        }
        if(doc.isNative()) {
            modifiers.add(JavaCodeModel.JModifier.NATIVE);
        }
        if(doc.isStatic()) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        if(doc.isSynchronized()) {
            modifiers.add(JavaCodeModel.JModifier.SYNCHRONIZED);
        }
        return modifiers.toArray( new JavaCodeModel.JModifier[modifiers.size()]);
    }

    private JavaCodeModel.JModifier[] getModifiers(ConstructorDoc doc){
        final List<JavaCodeModel.JModifier> modifiers = new ArrayList<JavaCodeModel.JModifier>();
        if(doc.isFinal()) {
            modifiers.add(JavaCodeModel.JModifier.FINAL);
        }
        if(doc.isNative()) {
            modifiers.add(JavaCodeModel.JModifier.NATIVE);
        }
        if(doc.isStatic()) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        if(doc.isSynchronized()) {
            modifiers.add(JavaCodeModel.JModifier.SYNCHRONIZED);
        }
        return modifiers.toArray( new JavaCodeModel.JModifier[modifiers.size()]);
    }

    private JavaCodeModel.JModifier[] getModifiers(ClassDoc doc){
        final List<JavaCodeModel.JModifier> modifiers = new ArrayList<JavaCodeModel.JModifier>();
        if(doc.isAbstract()) {
            modifiers.add(JavaCodeModel.JModifier.ABSTRACT);
        }
        if(doc.isFinal()) {
            modifiers.add(JavaCodeModel.JModifier.FINAL);
        }
        if(doc.isStatic()) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        return modifiers.toArray( new JavaCodeModel.JModifier[modifiers.size()]);
    }

    private JavaCodeModel.JModifier[] getModifiers(FieldDoc doc){
        final List<JavaCodeModel.JModifier> modifiers = new ArrayList<JavaCodeModel.JModifier>();
        if(doc.isStatic()) {
            modifiers.add(JavaCodeModel.JModifier.STATIC);
        }
        if(doc.isTransient()) {
            modifiers.add(JavaCodeModel.JModifier.TRANSIENT);
        }
        if(doc.isVolatile()) {
            modifiers.add(JavaCodeModel.JModifier.VOLATILE);
        }
        return modifiers.toArray( new JavaCodeModel.JModifier[modifiers.size()]);
    }

    private Identifier[] getInterfaces(ClassDoc classDoc) {
        ClassDoc[] ifaces = classDoc.interfaces();
        Identifier[] result = new Identifier[ifaces.length];
        for(int i =0; i < ifaces.length; i++) {
            result[i] = IdentifierReader.readFullyQualifiedInterface(ifaces[i].qualifiedName());
        }
        return result;
    }


    // TODO: handle all Types (interfaces, objects, exceptions...).
    private JavaCodeModel.JType typeToJType(Type t) {
        if (t.isPrimitive()) {
            return JavaCodeModel.javaTypeToJType(t.qualifiedTypeName());
        } else {
            return new JavaCodeModel.ObjectType(
                    IdentifierReader.readFullyQualifiedClass(t.qualifiedTypeName())
            );
        }
    }

    private JavaCodeModel.JType[] getSignature(Parameter[] parameters) {
        final JavaCodeModel.JType[] signature = new JavaCodeModel.JType[parameters.length];
        for(int i = 0; i < parameters.length; i++) {
            signature[i] = typeToJType(parameters[i].type());
        }
        return signature;
    }

    private String[] getParameterNames(Parameter[] parameters) {
        final String[] parameterNames = new String[parameters.length];
        for(int i = 0; i < parameters.length; i++) {
            parameterNames[i] = parameters[i].name();
        }
        return parameterNames;
    }

    private JavaCodeModel.ExceptionType[] getExceptions(ClassDoc[] thrownExceptions) {
        final JavaCodeModel.ExceptionType[] exceptionTypes = new JavaCodeModel.ExceptionType[thrownExceptions.length];
        for(int i = 0; i < thrownExceptions.length; i++) {
            exceptionTypes[i] = new JavaCodeModel.ExceptionType(
                    IdentifierReader.readFullyQualifiedClass( thrownExceptions[i].qualifiedName() )
            );
        }
        return exceptionTypes;
    }

    private Identifier getSuperclass(ClassDoc classDoc) {
        ClassDoc superClass = classDoc.superclass();
        if(superClass == null) {
            return OBJECT_SUPERCLASS;
        }
        return IdentifierReader.readFullyQualifiedClass(classDoc.superclass().qualifiedName());
    }

    private void handleClass(ClassDoc classDoc) {
        final ClassJavadoc je = new ClassJavadoc(
            IdentifierReader.readFullyQualifiedClass(classDoc.qualifiedName()),
            getSuperclass(classDoc),
            getInterfaces(classDoc),
            getModifiers(classDoc),
            getVisibility(classDoc),
            classDoc.getRawCommentText(),
            classDoc.commentText(),
            getTags(classDoc),
            classDoc.position().line(),
            classDoc.position().column()
        );
        javadocHandler.classJavadoc(je);

        for(FieldDoc fieldDoc : classDoc.fields()) {
            handleField(fieldDoc);
        }
        for (ConstructorDoc constructorDoc : classDoc.constructors()) {
          handleConstructor(constructorDoc);
        }
        for (MethodDoc methodDoc : classDoc.methods()) {
            handleMethod(methodDoc);
        }
    }

    private void handleField(FieldDoc fieldDoc) {
        final FieldJavadoc je = new FieldJavadoc(
                IdentifierReader.readFullyQualifiedAttribute(fieldDoc.qualifiedName()),
                typeToJType(fieldDoc.type()),
                fieldDoc.constantValue() != null ? fieldDoc.constantValue().toString() : null,
                getModifiers(fieldDoc),
                getVisibility(fieldDoc),
                fieldDoc.getRawCommentText(),
                fieldDoc.commentText(),
                getTags(fieldDoc),
                fieldDoc.position().line(),
                fieldDoc.position().column()
        );
        javadocHandler.fieldJavadoc(je);
    }

    private void handleConstructor(ConstructorDoc constructorDoc) {
        constructorDoc.signature();
        final ConstructorJavadoc je = new ConstructorJavadoc(
                IdentifierReader.readFullyQualifiedConstructor(constructorDoc.qualifiedName()),
                getSignature( constructorDoc.parameters() ),
                constructorDoc.signature(),
                getParameterNames( constructorDoc.parameters() ),
                getExceptions( constructorDoc.thrownExceptions() ),
                getModifiers(constructorDoc),
                getVisibility(constructorDoc),
                constructorDoc.getRawCommentText(),
                constructorDoc.commentText(),
                getTags(constructorDoc),
                constructorDoc.position().line(),
                constructorDoc.position().column()
        );
        javadocHandler.constructorJavadoc(je);
    }

    private void handleMethod(MethodDoc methodDoc) {
        final MethodJavadoc je = new MethodJavadoc(
                IdentifierReader.readFullyQualifiedMethod(methodDoc.qualifiedName()),
                getSignature( methodDoc.parameters() ),
                methodDoc.signature(),
                getParameterNames( methodDoc.parameters() ),
                typeToJType(methodDoc.returnType()),
                getExceptions( methodDoc.thrownExceptions() ),
                getModifiers(methodDoc),
                getVisibility(methodDoc),
                methodDoc.getRawCommentText(),
                methodDoc.commentText(),
                getTags(methodDoc),
                methodDoc.position().line(),
                methodDoc.position().column()
        );
        javadocHandler.methodJavadoc(je);
    }

    private File getSerializationFile(String[][] options) {
        for(int i = 0; i < options.length; i++) {
            if( SERIALIZATION_FILE_OPTION.equals(options[i][0]) ) {
                if(options[i].length != 2) {
                    throw new JavadocHandlerDocletException(
                            String.format("Expected just one option value for %s", SERIALIZATION_FILE_OPTION)
                    );
                }
                return new File(options[i][1]);
            }
        }
        throw new JavadocHandlerDocletException( String.format("Cannot find option %s", SERIALIZATION_FILE_OPTION) );
    }

    private Map<String, List<String>> getTags(Doc doc) {
        Map<String,List<String>> result = new HashMap<String, List<String>>();
        for( Tag tag : doc.tags() ) {
            List<String> values = result.get( tag.name() );
            if(values == null) {
                values = new ArrayList<String>();
            }
            values.add(tag.text());
        }
        return result;
    }

}
