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


package com.asemantics.rdfcoder.model;

import com.asemantics.rdfcoder.model.java.JAttribute;
import com.asemantics.rdfcoder.model.java.JClass;
import com.asemantics.rdfcoder.model.java.JEnumeration;
import com.asemantics.rdfcoder.model.java.JInterface;
import com.asemantics.rdfcoder.model.java.JMethod;
import com.asemantics.rdfcoder.model.java.JPackage;
import com.asemantics.rdfcoder.model.java.JSignature;
import com.asemantics.rdfcoder.model.java.JavaCodeHandler;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;
import com.asemantics.rdfcoder.model.java.JavaQueryModel;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.java.JavaCodeModel}.
 */
//TODO: HIGH : this test must be decomposed.
public class JavaCodeModelTest {

    private JenaCoderFactory jcmf;

    private CodeModelBase cmb;

    private JavaCodeHandler jch;

    private JavaQueryModel qm;

    public JavaCodeModelTest() {}

    @Before
    public void setUp() {
        jcmf = new JenaCoderFactory();
        cmb  = jcmf.createCodeModel();
        jch  = jcmf.createHandlerOnModel(cmb);
        qm   = jcmf.createQueryModel(cmb);
    }

    @After
    public void tearDown() {
        jcmf = null;
        cmb  = null;
        jch  = null;
        qm   = null;
    }

    @Test
    public void testHandler() throws QueryModelException {

        // Loading model.
        jch.startParsing("test_lib", "test_location");

        jch.startPackage( IdentifierReader.readPackage("p0") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1.p2") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1.p2.p3") );

        jch.startClass(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.STATIC },
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader.readPackage("p0.p1.p2.p3").copy().pushFragment("class1", JavaCodeModel.CLASS_KEY).build(),
                null,
                null
        );

        jch.startEnumeration(new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.FINAL },
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader
                        .readPackage("p0.p1.p2.p3")
                        .copy()
                        .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                        .pushFragment("enum1", JavaCodeModel.ENUMERATION_KEY)
                        .build(),
                new String[] {"ELEM_1", "ELEM_2", "ELEM_3"}
        );

        jch.method(
            new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.NATIVE },
            JavaCodeModel.JVisibility.DEFAULT,
            IdentifierReader
                    .readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                    .pushFragment("enum1", JavaCodeModel.ENUMERATION_KEY)
                    .pushFragment("method1", JavaCodeModel.METHOD_KEY)
                    .build(),
            1,
            new String[]{"param1", "param2"},
            new JavaCodeModel.JType[] {JavaCodeModel.INT, JavaCodeModel.FLOAT},
            JavaCodeModel.VOID,
            null
        );
        jch.endEnumeration();

        jch.attribute(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.TRANSIENT },
                JavaCodeModel.JVisibility.PRIVATE,
                IdentifierReader
                    .readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                    .pushFragment("attribute1", JavaCodeModel.ATTRIBUTE_KEY)
                    .build(),
                JavaCodeModel.INT,
                "0"
        );
        jch.attribute(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.STATIC },
                JavaCodeModel.JVisibility.PROTECTED,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                    .pushFragment("attribute2", JavaCodeModel.ATTRIBUTE_KEY)
                        .build(),
                new JavaCodeModel.ObjectType( IdentifierReader.readFullyQualifiedClass("a.b.Obj1") ),
                "0"
        );

        jch.endClass();

        jch.startClass(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.FINAL },
                JavaCodeModel.JVisibility.PROTECTED,
                IdentifierReader
                    .readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("class2", JavaCodeModel.CLASS_KEY)
                    .build(),
                null,
                null
        );

        jch.method(
            new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.NATIVE },
            JavaCodeModel.JVisibility.DEFAULT,
            IdentifierReader.readPackage("p0.p1.p2.p3")
                .copy()
                .pushFragment("class2", JavaCodeModel.CLASS_KEY)
                .pushFragment("method1", JavaCodeModel.METHOD_KEY)
                .build(),
            1,
            new String[]{"param1", "param2", "param3"},
            new JavaCodeModel.JType[] {JavaCodeModel.INT, JavaCodeModel.FLOAT, JavaCodeModel.LONG},
            JavaCodeModel.VOID,
            null
        );

        jch.startClass(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.STATIC },
                JavaCodeModel.JVisibility.PRIVATE,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("class2", JavaCodeModel.CLASS_KEY)
                    .pushFragment("class3", JavaCodeModel.CLASS_KEY)
                    .build(),
                null,
                null
        );
        jch.endClass();

        jch.startInterface(
                IdentifierReader.readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("I1", JavaCodeModel.INTERFACE_KEY)
                    .build(),
                new Identifier[] {
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E1"),
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E2"),
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E3")
                } 
        );

        jch.method(
                new JavaCodeModel.JModifier[] { JavaCodeModel.JModifier.NATIVE },
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                    .copy()
                    .pushFragment("I1", JavaCodeModel.INTERFACE_KEY)
                    .pushFragment("method1", JavaCodeModel.METHOD_KEY)
                    .build(),
                1,
                new String[] {"pa", "pb"},
                new JavaCodeModel.JType[] {
                        JavaCodeModel.INT,
                        new JavaCodeModel.ObjectType(IdentifierReader.readFullyQualifiedClass("a.b.T") )
                },
                JavaCodeModel.VOID,
                null
        );
        jch.endInterface();

        jch.endClass();

        jch.endPackage();
        jch.endPackage();
        jch.endPackage();
        jch.endPackage();

        jch.endParsing();


        /* End model preparation. *********************************/

        // Querying model.

        // Checking asset.
        System.out.println("Asset >>>");
        Asset asset = qm.getAsset();
        for(String lib :  asset.getLibraries() ) {
            System.out.println(
                    "library: " + lib + " at location: " + asset.getLibraryLocation(lib) + " with date: " + asset.getLibraryDateTime(lib) );
        }
        Assert.assertEquals("Expected one library in asset", 1 ,asset.getLibraries().length);
        System.out.println("Asset <<<");

        // Retrieve packages.
        System.out.println("Packages >>>");
        JPackage[] packs = qm.getAllPackages();
        Assert.assertEquals(4, packs.length);
        for (JPackage pack : packs) {
            System.out.println(pack.toString());
        }
        System.out.println("Packages <<<");

        System.out.println("Classes >>>");

        // Check classes containement.
        JClass[] classes  = qm.getAllClasses();
        Assert.assertEquals(3, classes.length);

        JClass[] classes2 = qm.getClassesInto(IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3") );
        Assert.assertEquals(2, classes2.length);

        JClass[] classes3 = qm.getClassesInto(IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class2") );
        Assert.assertEquals(1, classes3.length);

        // Print out classes.
        for(JClass clazz : classes) {
            System.out.println(clazz);
        }

        // Check visibility and modifiers.
        for (JClass aClass : classes) {
            if ("class1".equals(aClass.getName())) {
                Assert.assertEquals(JavaCodeModel.JVisibility.PUBLIC, aClass.getVisibility());
                Assert.assertEquals(1, aClass.getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.STATIC, aClass.getModifiers()[0]);
            }
            if ("class2".equals(aClass.getName())) {
                Assert.assertEquals(JavaCodeModel.JVisibility.PROTECTED, aClass.getVisibility());
                Assert.assertEquals(1, aClass.getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.FINAL, aClass.getModifiers()[0]);
            }
        }

        System.out.println("Classes <<<");

        // Retrieve methods from class.
        System.out.println("Methods >>>");

        JMethod[] methods = qm.getMethodsInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class2") );
        Assert.assertEquals(1, methods.length);
        for (JMethod method : methods) {
            System.out.println(method);
            JSignature[] signatures = method.getSignatures();
            Assert.assertEquals(signatures.length, 1);
            System.out.println("\tSignatures >>>");
            for (JSignature signature : signatures) {
                System.out.println(signature);
            }
            System.out.println("\tSignatures <<<");
        }

        // Check visibility and modifiers.
        for(int i = 0; i < methods.length; i++) {
            if ( "method1".equals(methods[i].getName()) ) {
                Assert.assertEquals(JavaCodeModel.JVisibility.DEFAULT, methods[i].getVisibility());
                Assert.assertEquals(1, methods[i].getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.NATIVE, methods[i].getModifiers()[0]);
            }
        }


        System.out.println("Methods <<<");

        // Retrieve attributes.
        JAttribute[] attributes = qm.getAttributesInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class1") );
        Assert.assertEquals(attributes.length, 2);
        System.out.println("Attributes >>>");

        // Check visibility and modifiers.
        for(int i = 0; i < attributes.length; i++) {
            if ( "attribute1".equals(attributes[i].getName()) ) {
                Assert.assertEquals(JavaCodeModel.JVisibility.PRIVATE, attributes[i].getVisibility());
                Assert.assertEquals(1, attributes[i].getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.TRANSIENT, attributes[i].getModifiers()[0]);
            }
            if ( "attribute2".equals(attributes[i].getName()) ) {
                Assert.assertEquals(JavaCodeModel.JVisibility.PROTECTED, attributes[i].getVisibility());
                Assert.assertEquals(1, attributes[i].getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.STATIC, attributes[i].getModifiers()[0]);
            }
        }


        for (JAttribute attribute : attributes) {
            System.out.println(attribute);
        }


        System.out.println("Attributes <<<");

        // Retrieve interfaces.
        System.out.println("Interfaces >>>");

        JInterface[] interfaces = qm.getAllInterfaces();
        Assert.assertEquals(1, interfaces.length);
        for(JInterface inter : interfaces) {
            System.out.println(inter);
        }

        System.out.println("Interfaces <<<");

        System.out.println("Enumerations >>>");

        JEnumeration[] enumerations = qm.getEnumerationsInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class1") );
        Assert.assertEquals(1, enumerations.length);
        for(JEnumeration enumeration : enumerations) {
            System.out.println(enumeration);
            JMethod enumerationMethods[] = qm.getMethodsInto( enumeration.getIdentifier() );
            Assert.assertEquals(1, enumerationMethods.length);
            for(JMethod method : enumerationMethods) {
                System.out.println("method: " + method);
            }
        }

        System.out.println("Enumerations <<<");
    }

}
