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

import com.asemantics.rdfcoder.model.Asset;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.storage.JenaCoderFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.java.JavaCodeModel}.
 */
public class JavaCodeHandlerTest {

    private static Logger logger = Logger.getLogger(JavaCodeHandlerTest.class);

    private JenaCoderFactory jcmf;

    private JavaCodeHandler jch;

    private CodeModelBase cmb;

    private JavaQueryModel qm;

    /**
     * Simulates a test sequence called <i>Class1</i>.
     *
     * @param jch
     */
    public static void simulateClass1Sequence(JavaCodeHandler jch) {
        jch.startClass(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.STATIC},
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader.readPackage("p0.p1.p2.p3").copy().pushFragment("class1", JavaCodeModel.CLASS_KEY).build(),
                null,
                null
        );

        jch.startEnumeration(new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.FINAL},
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader
                        .readPackage("p0.p1.p2.p3")
                        .copy()
                        .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                        .pushFragment("enum1", JavaCodeModel.ENUMERATION_KEY)
                        .build(),
                new String[]{"ELEM_1", "ELEM_2", "ELEM_3"}
        );

        jch.method(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.NATIVE},
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
                new JavaCodeModel.JType[]{JavaCodeModel.INT, JavaCodeModel.FLOAT},
                JavaCodeModel.VOID,
                null
        );

        jch.endEnumeration();

        jch.attribute(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.TRANSIENT},
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
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.STATIC},
                JavaCodeModel.JVisibility.PROTECTED,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                        .copy()
                        .pushFragment("class1", JavaCodeModel.CLASS_KEY)
                        .pushFragment("attribute2", JavaCodeModel.ATTRIBUTE_KEY)
                        .build(),
                new JavaCodeModel.ObjectType(IdentifierReader.readFullyQualifiedClass("a.b.Obj1")),
                "0"
        );

        jch.endClass();
    }

    /**
     * Simulates a test sequence called <i>Class2</i>.
     *
     * @param jch
     */
    public static void simulateClass2Sequence(JavaCodeHandler jch) {
        jch.startClass(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.FINAL},
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
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.NATIVE},
                JavaCodeModel.JVisibility.DEFAULT,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                        .copy()
                        .pushFragment("class2", JavaCodeModel.CLASS_KEY)
                        .pushFragment("method1", JavaCodeModel.METHOD_KEY)
                        .build(),
                1,
                new String[]{"param1", "param2", "param3"},
                new JavaCodeModel.JType[]{JavaCodeModel.INT, JavaCodeModel.FLOAT, JavaCodeModel.LONG},
                JavaCodeModel.VOID,
                null
        );

        jch.startClass(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.STATIC},
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
                new Identifier[]{
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E1"),
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E2"),
                        IdentifierReader.readFullyQualifiedClass("p0.p1.p2.p3.E3")
                }
        );

        jch.method(
                new JavaCodeModel.JModifier[]{JavaCodeModel.JModifier.NATIVE},
                JavaCodeModel.JVisibility.PUBLIC,
                IdentifierReader.readPackage("p0.p1.p2.p3")
                        .copy()
                        .pushFragment("I1", JavaCodeModel.INTERFACE_KEY)
                        .pushFragment("method1", JavaCodeModel.METHOD_KEY)
                        .build(),
                1,
                new String[]{"pa", "pb"},
                new JavaCodeModel.JType[]{
                        JavaCodeModel.INT,
                        new JavaCodeModel.ObjectType(IdentifierReader.readFullyQualifiedClass("a.b.T"))
                },
                JavaCodeModel.VOID,
                null
        );
        jch.endInterface();

        jch.endClass();
    }

    /**
     * Provides to the given handler a sequence defining a test class. 
     *
     * @param jch
     * @throws com.asemantics.rdfcoder.model.QueryModelException
     */
    public static void simulateHandlerSequence(JavaCodeHandler jch) throws QueryModelException {
        // Loading model.
        jch.startParsing("test_lib", "test_location");

        jch.startPackage( IdentifierReader.readPackage("p0") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1.p2") );
        jch.startPackage( IdentifierReader.readPackage("p0.p1.p2.p3") );

        simulateClass1Sequence(jch);
        simulateClass2Sequence(jch);

        jch.endPackage();
        jch.endPackage();
        jch.endPackage();
        jch.endPackage();

        jch.endParsing();
    }

    /**
     * Checks the content of the model through the given {@link com.asemantics.rdfcoder.model.java.JavaQueryModel}
     * verifying the existence of a specific structure.
     *
     * @param qm
     * @throws QueryModelException
     * @see #simulateHandlerSequence(com.asemantics.rdfcoder.model.java.JavaCodeHandler)
     */
    public static void checkQueryModelContent(JavaQueryModel qm, Logger logger) throws QueryModelException {
        // Checking asset.
        logger.info("Asset >>>");
        Asset asset = qm.getAsset();
        for(String lib :  asset.getLibraries() ) {
            logger.info(String.format(
                    "library %s at location %s with date %s ",
                    lib,
                    asset.getLibraryLocation(lib),
                    asset.getLibraryDateTime(lib)
            ));
        }
        Assert.assertEquals("Expected one library in asset", 1 ,asset.getLibraries().length);
        logger.info("Asset <<<");

        // Retrieve packages.
        logger.info("Packages >>>");
        JPackage[] packs = qm.getAllPackages();
        Assert.assertEquals(4, packs.length);
        for (JPackage pack : packs) {
            logger.info( pack.toString() );
        }
        logger.info("Packages <<<");

        // Retrieve classes.
        logger.info("Classes >>>");
        // Check classes containment.
        JClass[] classes  = qm.getAllClasses();
        Assert.assertEquals(3, classes.length);
        // Print out classes.
        for(JClass clazz : classes) {
            logger.info( String.format("class %s", clazz) );
        }

        JClass[] classes2 = qm.getClassesInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3") );
        Assert.assertEquals(2, classes2.length);

        JClass[] classes3 = qm.getClassesInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class2") );
        Assert.assertEquals(1, classes3.length);

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
        logger.info("Classes <<<");

        // Retrieve methods from class.
        logger.info("Methods >>>");

        JMethod[] methods = qm.getMethodsInto( IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class2") );
        Assert.assertEquals(1, methods.length);
        for (JMethod method : methods) {
            logger.info( String.format("method: %s", method) );
            JSignature[] signatures = method.getSignatures();
            Assert.assertEquals(signatures.length, 1);
            logger.info("Signatures >>>");
            for (JSignature signature : signatures) {
                logger.info( String.format("signature: %s", signature) );
            }
            logger.info("Signatures <<<");
        }

        // Check visibility and modifiers.
        for(int i = 0; i < methods.length; i++) {
            if ( "method1".equals(methods[i].getName()) ) {
                Assert.assertEquals(JavaCodeModel.JVisibility.DEFAULT, methods[i].getVisibility());
                Assert.assertEquals(1, methods[i].getModifiers().length);
                Assert.assertEquals(JavaCodeModel.JModifier.NATIVE, methods[i].getModifiers()[0]);
            }
        }

        logger.info("Methods <<<");

        // Retrieve attributes.
        JAttribute[] attributes = qm.getAttributesInto(
                IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class1")
        );
        Assert.assertEquals(attributes.length, 2);
        logger.info("Attributes >>>");

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
            logger.info( String.format("attribute: %s", attribute) );
        }

        logger.info("Attributes <<<");

        // Retrieve interfaces.
        logger.info("Interfaces >>>");

        JInterface[] interfaces = qm.getAllInterfaces();
        Assert.assertEquals(1, interfaces.length);
        for(JInterface inter : interfaces) {
            logger.info(inter);
        }

        logger.info("Interfaces <<<");

        logger.info("Enumerations >>>");

        JEnumeration[] enumerations = qm.getEnumerationsInto(
                IdentifierReader.readIdentifier("jpackage:p0.p1.p2.p3.jclass:class1")
        );
        Assert.assertEquals(1, enumerations.length);
        for(JEnumeration enumeration : enumerations) {
            logger.info(enumeration);
            JMethod enumerationMethods[] = qm.getMethodsInto( enumeration.getIdentifier() );
            Assert.assertEquals(1, enumerationMethods.length);
            for(JMethod method : enumerationMethods) {
                logger.info("method: " + method);
            }
        }

        logger.info("Enumerations <<<");
    }

    /**
     * Constructor.
     */
    public JavaCodeHandlerTest() {}

    @Before
    public void setUp() {
        jcmf = new JenaCoderFactory();
        cmb  = jcmf.createCodeModel();
        jch  = jcmf.createHandlerOnModel(cmb);
        qm   = JavaCoderFactory.createQueryModel(cmb);
    }

    @After
    public void tearDown() {
        jcmf = null;
        cmb  = null;
        jch  = null;
        qm   = null;
    }

    /**
     * Tests a handler event sequence and verifies the corresponding data.
     * 
     * @throws QueryModelException
     */
    @Test
    public void testHandler() throws QueryModelException {
        simulateHandlerSequence(jch);
        checkQueryModelContent(qm, logger);
    }

}
