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

import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.QueryModelTest;
import com.asemantics.rdfcoder.parser.ParserException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test case for the {@link com.asemantics.rdfcoder.model.java.JavaQueryModelImpl} class.
 */
public class JavaQueryModelImplTest {

    private JavaQueryModel javaQueryModel;

    public JavaQueryModelImplTest() {}

    @Before
    public void setUp() throws ParserException, IOException {
        javaQueryModel = new JavaQueryModelImpl( QueryModelTest.createQueryModel() );
    }

    @After
    public void tearDown() {
        javaQueryModel = null;
    }

    @Test
    public void testPackageExists() {
        Assert.assertTrue(
                javaQueryModel.packageExists( IdentifierReader.readPackage("org.jivesoftware.smack") )
        );
    }

    @Test
    public void testClassExists() {
        Assert.assertTrue(
                javaQueryModel.classExists(
                        IdentifierReader.readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                )
        );
    }

    @Test
    public void testInterfaceExists() {
        Assert.assertTrue(
                javaQueryModel.interfaceExists(
                        IdentifierReader.readFullyQualifiedInterface("org.jivesoftware.smack.PacketListener")
                )
        );
    }

    @Test
    public void testAttributeExists() {
        Assert.assertTrue(
                javaQueryModel.attributeExists(
                        IdentifierReader
                                .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                                .copy()
                                .pushFragment("chatManagerListeners", JavaCodeModel.ATTRIBUTE_KEY)
                                .build()
                )
        );
    }

    @Test
    public void testMethodExists() {
        Assert.assertTrue(
                javaQueryModel.methodExists(
                        IdentifierReader
                                .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                                .copy()
                                .pushFragment("sendMessage", JavaCodeModel.METHOD_KEY)
                                .build()
                )
        );
    }

    @Test
    public void testSignatureExists() {
        Assert.assertTrue(
                javaQueryModel.signatureExists(
                        IdentifierReader
                                .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                                .copy()
                                .pushFragment("sendMessage", JavaCodeModel.METHOD_KEY)
                                .pushFragment("_285434957", JavaCodeModel.SIGNATURE_KEY)
                                .build()
                )
        );
    }

    @Test
    public void testEnumerationExists() {
        Assert.assertTrue(
                // TODO: HIGH - an inner enumeration should be contained inside a class parent.
                //Identifier<jpackage:org.jivesoftware.smack.packet.jenumeration:RosterPacket$ItemType>
                javaQueryModel.enumerationExists(
                        IdentifierReader
                                .readPackage("org.jivesoftware.smack.packet")
                                .copy()
                                .pushFragment("RosterPacket$ItemType", JavaCodeModel.ENUMERATION_KEY)
                                .build()
                )
        );
    }

    @Test
    public void testGetAllPackages() throws IOException, ParserException {
        JPackage[] packages = javaQueryModel.getAllPackages();
        Assert.assertTrue("Unexpected number of packages.", packages.length > 0 );
    }

    @Test
    public void testGetPackage() throws IOException, ParserException, QueryModelException {
        JPackage pack = javaQueryModel.getPackage(
                IdentifierReader.readPackage("org.jivesoftware.smack.packet")
        );
        Assert.assertNotNull("Unexpected number of packages.", pack );
    }

    @Test
    public void testGetPackagesInto() throws IOException, ParserException, QueryModelException {
        JPackage[] packages = javaQueryModel.getPackagesInto(
                IdentifierReader.readPackage("org.jivesoftware.smack")
        );
        Assert.assertEquals("Unexpected number of packages.", 6, packages.length );
    }

    @Test
    public void testGetAllInterfaces() throws IOException, ParserException {
        JInterface[] interfaces = javaQueryModel.getAllInterfaces();
        Assert.assertTrue("Unexpected number of interfaces.", interfaces.length > 0 );
    }

    @Test
    public void testGetInterface() throws IOException, ParserException, QueryModelException {
        JInterface iface = javaQueryModel.getInterface(
                IdentifierReader.readFullyQualifiedInterface("org.jivesoftware.smack.PacketListener")
        );
        Assert.assertNotNull("Expected valid interface.", iface); // TODO: HIGH - there is no control on identifier existence in query model: add it.
    }

    @Test
    public void testGetAllClasses() throws IOException, ParserException {
        JClass[] classes = javaQueryModel.getAllClasses();
        Assert.assertTrue("Unexpected number of classes.", classes.length > 0 );
    }

    @Test
    public void testGetClazz() throws IOException, ParserException, QueryModelException {
        JClass clazz = javaQueryModel.getClazz(
                IdentifierReader.readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
        );
        Assert.assertNotNull("Expected valid class.", clazz );
    }

    @Test
    public void testGetInterfacesInto() throws QueryModelException {
        JInterface[] interfaces = javaQueryModel.getInterfacesInto(
                IdentifierReader.readPackage("org.jivesoftware.smack")
        );
        Assert.assertTrue( "Expected interfaces.", interfaces.length > 0 );
    }

    @Test
    public void testGetClassesInto() throws QueryModelException {
        JClass[] classes = javaQueryModel.getClassesInto(
                IdentifierReader.readPackage("org.jivesoftware.smack")
        );
        Assert.assertTrue("Expected classes.", classes.length > 0);
    }

    @Test
    public void testGetAttributesInto() throws QueryModelException {
        JAttribute[] attributes = javaQueryModel.getAttributesInto(
                IdentifierReader.readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
        );
        Assert.assertTrue("Expected attributes.", attributes.length > 0);
    }

    @Test
    public void testGetAttribute() throws QueryModelException {
        JAttribute attribute = javaQueryModel.getAttribute(
                IdentifierReader
                        .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                        .copy()
                        .pushFragment("chatManagerListeners", JavaCodeModel.ATTRIBUTE_KEY)
                        .build()
        );
        Assert.assertNotNull("Expected valid attribute.", attribute);
    }

    @Test
    public void testGetAttributeType() throws QueryModelException {
         JavaCodeModel.JType type = javaQueryModel.getAttributeType(
                IdentifierReader
                        .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                        .copy()
                        .pushFragment("chatManagerListeners", JavaCodeModel.ATTRIBUTE_KEY)
                        .build()
        );
        Assert.assertNotNull("Expected valid type type.", type);
        Assert.assertEquals(
                "Unexpected type.",
                "http://www.rdfcoder.org/2007/1.0#jpackage:java.util.jclass:Set", 
                type.getIdentifier().getIdentifier()
        );
    }

    @Test
    public void testGetMethodsInto() throws QueryModelException {
        JMethod[] methods = javaQueryModel.getMethodsInto(
            IdentifierReader.readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
        );
        Assert.assertTrue(methods.length > 0);
    }

    @Test
    public void testGetMethod() throws QueryModelException {
        JMethod method = javaQueryModel.getMethod(
            IdentifierReader
                .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                .copy()
                .pushFragment("sendMessage", JavaCodeModel.METHOD_KEY)
                .build()
        );
        Assert.assertNotNull("Expected valid method.", method);
    }

    @Test
    public void testGetEnumerationsInto() throws QueryModelException {
        JEnumeration[] enumerations = javaQueryModel.getEnumerationsInto(
            IdentifierReader
                    .readPackage("org.jivesoftware.smack")
        );
        Assert.assertTrue(enumerations.length > 0);
    }

    @Test
    public void testGetEnumeration() throws QueryModelException {
        JEnumeration enumeration = javaQueryModel.getEnumeration(
                IdentifierReader
                        .readPackage("org.jivesoftware.smack.packet")
                        .copy()
                        .pushFragment("RosterPacket$ItemType", JavaCodeModel.ENUMERATION_KEY)
                        .build()
        );
        Assert.assertNotNull("Expected valid enumeration.", enumeration);
    }

    @Test
    public void testGetElements() {
        String[] elements = javaQueryModel.getElements(
                IdentifierReader
                        .readPackage("org.jivesoftware.smack.packet")
                        .copy()
                        .pushFragment("RosterPacket$ItemType", JavaCodeModel.ENUMERATION_KEY)
                        .build()
        );
        Assert.assertEquals("Invalid size.", 5, elements.length);
        Set<String> expected = new HashSet<String>(
                Arrays.asList(
                        "jelement:from",
                        "jelement:none",
                        "jelement:both",
                        "jelement:to",
                        "jelement:remove"
                )
        );
        for(String element : elements) {
            Assert.assertTrue("Expected element.", expected.contains(element) );
        }
    }

    @Test
    public void testGetSignatures() throws QueryModelException {
        JSignature[] signatures = javaQueryModel.getSignatures(
            IdentifierReader
                    .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                    .copy()
                    .pushFragment("sendMessage", JavaCodeModel.METHOD_KEY)
                    .build()
        );
        Assert.assertTrue(signatures.length > 0);
    }

    @Test
    public void testGetParameters() throws QueryModelException {
        JavaCodeModel.JType[] types = javaQueryModel.getParameters(
                IdentifierReader
                        .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                        .copy()
                        .pushFragment("sendMessage", JavaCodeModel.METHOD_KEY)
                        .pushFragment("_285434957", JavaCodeModel.SIGNATURE_KEY)
                        .build()
        );
        // TODO: HIGH - the parameter order in a signature is not respected: fix it.
        Assert.assertEquals("Unexpected number of types.", 2, types.length);
        Assert.assertEquals(
                "http://www.rdfcoder.org/2007/1.0#jpackage:org.jivesoftware.smack.jclass:Chat",
                types[1].getIdentifier().getIdentifier()
        );
        Assert.assertEquals(
                "http://www.rdfcoder.org/2007/1.0#jpackage:org.jivesoftware.smack.packet.jclass:Message",
                types[0].getIdentifier().getIdentifier()
        );
    }

    @Test
    public void testGetReturnType() throws QueryModelException {
        JavaCodeModel.JType type = javaQueryModel.getReturnType(
                IdentifierReader
                        .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                        .copy()
                        .pushFragment("createPacketCollector", JavaCodeModel.METHOD_KEY)
                        .pushFragment("_1382257162", JavaCodeModel.SIGNATURE_KEY)
                        .build()
        );
        Assert.assertEquals(
                "Unexpected return type.",
                "http://www.rdfcoder.org/2007/1.0#jpackage:org.jivesoftware.smack.jclass:PacketCollector",
                type.getIdentifier().getIdentifier()
        );
    }

    @Test
    public void testGetVisibility() throws QueryModelException {
        JavaCodeModel.JVisibility visibility = javaQueryModel.getVisibility(
                IdentifierReader
                        .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                        .copy()
                        .pushFragment("createPacketCollector", JavaCodeModel.METHOD_KEY)
                        .pushFragment("_13", JavaCodeModel.SIGNATURE_KEY)
                        .build()
        );
        Assert.assertEquals("Unexpected visiblity.", JavaCodeModel.JVisibility.DEFAULT, visibility);
    }

    @Test
    public void testGetModifiers() throws QueryModelException {
        JavaCodeModel.JModifier[] modifier = javaQueryModel.getModifiers(
            IdentifierReader
                    .readFullyQualifiedClass("org.jivesoftware.smack.ChatManager")
                    .copy()
                    .pushFragment("prefix", JavaCodeModel.ATTRIBUTE_KEY)
                    .build()
        );
        Assert.assertEquals("Unexpected size.", 1, modifier.length);
        Assert.assertEquals(
                "Unexpected modifier.",
                JavaCodeModel.JModifier.STATIC,
                modifier[0]
        );
    }

}
