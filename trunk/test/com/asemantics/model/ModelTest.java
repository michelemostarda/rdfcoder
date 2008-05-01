package com.asemantics.model;

import com.asemantics.modelimpl.JenaCoderFactory;
import junit.framework.TestCase;

/**
 * Basic test.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 *
 */
public class ModelTest extends TestCase {

    private JenaCoderFactory jcmf;

    private CodeModel cm;

    private CodeHandler jch;

    private QueryModel qm;

    public void setUp() {
        jcmf = new JenaCoderFactory();
        cm   = jcmf.createCodeModel();
        jch  = jcmf.createHandlerOnModel(cm);
        qm   = jcmf.createQueryModel(cm);
    }

    public void tearDown() {

    }

    public void testHandler() throws QueryModelException {

        // Loading org.asemantics.model.
        jch.startParsing("test_lib", "test_location");

        jch.startPackage("p0");
        jch.startPackage("p0.p1");
        jch.startPackage("p0.p1.p2");
        jch.startPackage("p0.p1.p2.p3");

        jch.startClass(
                new CodeModel.JModifier[] { CodeModel.JModifier.STATIC },
                CodeModel.JVisibility.PUBLIC,
                "p0.p1.p2.p3.class1",
                null,
                null
        );

        jch.startEnumeration(new CodeModel.JModifier[] { CodeModel.JModifier.FINAL },
                CodeModel.JVisibility.PUBLIC, "p0.p1.p2.p3.class1.enum1",
                new String[] {"ELEM_1", "ELEM_2", "ELEM_3"}
        );

        jch.method(
            new CodeModel.JModifier[] { CodeModel.JModifier.NATIVE },
            CodeModel.JVisibility.DEFAULT,
            "p0.p1.p2.p3.class1.enum1.method1",
            1,
            new String[]{"param1", "param2"},
            new CodeModel.JType[] {CodeModel.INT, CodeModel.FLOAT},
            CodeModel.VOID,
            null
        );
        jch.endEnumeration();

        jch.attribute(
                new CodeModel.JModifier[] { CodeModel.JModifier.TRANSIENT },
                CodeModel.JVisibility.PRIVATE,
                "p0.p1.p2.p3.class1.attribute1",
                CodeModel.INT,
                "0"
        );
        jch.attribute(
                new CodeModel.JModifier[] { CodeModel.JModifier.STATIC },
                CodeModel.JVisibility.PROTECTED,
                "p0.p1.p2.p3.class1.attribute2",
                new CodeModel.ObjectType("a.b.Obj1"),
                "0"
        );

        jch.endClass();

        jch.startClass(
                new CodeModel.JModifier[] { CodeModel.JModifier.FINAL },
                CodeModel.JVisibility.PROTECTED,
                "p0.p1.p2.p3.class2",
                null,
                null
        );

        jch.method(
            new CodeModel.JModifier[] { CodeModel.JModifier.NATIVE },
            CodeModel.JVisibility.DEFAULT,
            "p0.p1.p2.p3.class2.method1",
            1,
            new String[]{"param1", "param2", "param3"},
            new CodeModel.JType[] {CodeModel.INT, CodeModel.FLOAT, CodeModel.LONG},
            CodeModel.VOID,
            null
        );

        jch.startClass(
                new CodeModel.JModifier[] { CodeModel.JModifier.STATIC },
                CodeModel.JVisibility.PRIVATE,
                "p0.p1.p2.p3.class2.class3",
                null,
                null
        );
        jch.endClass();

        jch.startInterface( "p0.p1.p2.p3.I1", new String[] {"E1", "E2", "E3"} );

        jch.method(
                new CodeModel.JModifier[] { CodeModel.JModifier.NATIVE },
                CodeModel.JVisibility.PUBLIC,
                "p0.p1.p2.p3.method1",
                1,
                new String[] {"pa", "pb"},
                new CodeModel.JType[] { CodeModel.INT, new CodeModel.ObjectType("a.b.T") },
                CodeModel.VOID,
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
        JAsset asset = qm.getAsset();
        for(String lib :  asset.getLibraries() ) {
            System.out.println(
                    "library: " + lib + " at location: " + asset.getLibraryLocation(lib) + " with date: " + asset.getLibraryDateTime(lib) );
        }
        assertEquals("Expected one library in asset", 1 ,asset.getLibraries().length);
        System.out.println("Asset <<<");

        // Retrieve packages.
        System.out.println("Packages >>>");
        JPackage[] packs = qm.getAllPackages();
        assertEquals(4, packs.length);
        for (JPackage pack : packs) {
            System.out.println(pack.toString());
        }
        System.out.println("Packages <<<");

        System.out.println("Classes >>>");

        // Check classes containement.
        JClass[] classes  = qm.getAllClasses();
        JClass[] classes2 = qm.getClassesInto("p0.p1.p2.p3");
        JClass[] classes3 = qm.getClassesInto("p0.p1.p2.p3.class2");
        assertEquals(3, classes.length);
        assertEquals(2, classes2.length);
        assertEquals(1, classes3.length);

        // Print out classes.
        for(JClass clazz : classes) {
            System.out.println(clazz);
        }

        // Check visibility and modifiers.
        for(int i = 0; i < classes.length; i++) {
            if ( "class1".equals(classes[i].getName()) ) {
                assertEquals(CodeModel.JVisibility.PUBLIC, classes[i].getVisibility());
                assertEquals(1, classes[i].getModifiers().length);
                assertEquals(CodeModel.JModifier.STATIC, classes[i].getModifiers()[0]);
            }
            if ( "class2".equals(classes[i].getName()) ) {
                assertEquals(CodeModel.JVisibility.PROTECTED, classes[i].getVisibility());
                assertEquals(1, classes[i].getModifiers().length);
                assertEquals(CodeModel.JModifier.FINAL, classes[i].getModifiers()[0]);
            }
        }

        System.out.println("Classes <<<");

        // Retrieve methods from class.
        System.out.println("Methods >>>");

        JMethod[] methods = qm.getMethodsInto("p0.p1.p2.p3.class2");
        assertEquals(1, methods.length);
        for (JMethod method : methods) {
            System.out.println(method);
            JSignature[] signatures = method.getSignatures();
            assertEquals(signatures.length, 1);
            System.out.println("\tSignatures >>>");
            for (JSignature signature : signatures) {
                System.out.println(signature);
            }
            System.out.println("\tSignatures <<<");
        }

        // Check visibility and modifiers.
        for(int i = 0; i < methods.length; i++) {
            if ( "method1".equals(methods[i].getName()) ) {
                assertEquals(CodeModel.JVisibility.DEFAULT, methods[i].getVisibility());
                assertEquals(1, methods[i].getModifiers().length);
                assertEquals(CodeModel.JModifier.NATIVE, methods[i].getModifiers()[0]);
            }
        }


        System.out.println("Methods <<<");

        // Retrieve attributes.
        JAttribute[] attributes = qm.getAttributesInto("p0.p1.p2.p3.class1");
        assertEquals(attributes.length, 2);
        System.out.println("Attributes >>>");

        // Check visibility and modifiers.
        for(int i = 0; i < attributes.length; i++) {
            if ( "attribute1".equals(attributes[i].getName()) ) {
                assertEquals(CodeModel.JVisibility.PRIVATE, attributes[i].getVisibility());
                assertEquals(1, attributes[i].getModifiers().length);
                assertEquals(CodeModel.JModifier.TRANSIENT, attributes[i].getModifiers()[0]);
            }
            if ( "attribute2".equals(attributes[i].getName()) ) {
                assertEquals(CodeModel.JVisibility.PROTECTED, attributes[i].getVisibility());
                assertEquals(1, attributes[i].getModifiers().length);
                assertEquals(CodeModel.JModifier.STATIC, attributes[i].getModifiers()[0]);
            }
        }


        for (JAttribute attribute : attributes) {
            System.out.println(attribute);
        }


        System.out.println("Attributes <<<");

        // Retrieve interfaces.
        System.out.println("Interfaces >>>");

        JInterface[] interfaces = qm.getAllInterfaces();
        assertEquals(1, interfaces.length);
        for(JInterface inter : interfaces) {
            System.out.println(inter);
        }

        System.out.println("Interfaces <<<");

        System.out.println("Enumerations >>>");

        JEnumeration[] enumerations = qm.getEnumerationsInto("p0.p1.p2.p3.class1");
        assertEquals(1, enumerations.length);
        for(JEnumeration enumeration : enumerations) {
            System.out.println(enumeration);
            JMethod enumerationMethods[] = qm.getMethodsInto(enumeration.getFullName());
            assertEquals(1, enumerationMethods.length);
            for(JMethod method : enumerationMethods) {
                System.out.println("method: " + method);
            }
        }

        System.out.println("Enumerations <<<");
    }

}
