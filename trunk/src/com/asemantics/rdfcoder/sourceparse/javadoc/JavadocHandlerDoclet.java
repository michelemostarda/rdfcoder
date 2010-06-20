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

import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.java.JavadocHandler;
import com.asemantics.rdfcoder.sourceparse.JavadocEntry;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

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

    private void handleClass(ClassDoc classDoc) {
        final JavadocEntry je = docToEntry(classDoc);
        javadocHandler.classJavadoc(
                je,
                IdentifierReader.readFullyQualifiedClass(classDoc.qualifiedName())
        );

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
        final JavadocEntry je = docToEntry(fieldDoc);
        javadocHandler.fieldJavadoc(
                je,
                IdentifierReader.readFullyQualifiedAttribute( fieldDoc.qualifiedName() )
        );
    }

    private void handleConstructor(ConstructorDoc constructorDoc) {
        final JavadocEntry je = docToEntry(constructorDoc);
        javadocHandler.constructorJavadoc(
                je,
                IdentifierReader.readFullyQualifiedConstructor( constructorDoc.qualifiedName() ),
                new String[]{ constructorDoc.signature() }
        );
    }

    private void handleMethod(MethodDoc methodDoc) {
        final JavadocEntry je = docToEntry(methodDoc);
        javadocHandler.methodJavadoc(
                je,
                IdentifierReader.readFullyQualifiedMethod( methodDoc.qualifiedName() ),
                new String[]{ methodDoc.signature() }
        );
    }

    private JavadocEntry docToEntry(Doc doc) {
        return new JavadocEntry(
            doc.getRawCommentText(),
            doc.commentText(),
            getTags(doc),
            doc.position().line(),
            doc.position().column()
        );
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
