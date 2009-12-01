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

import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.Asset;
import com.asemantics.rdfcoder.model.TripleIterator;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.CodeHandlerImpl;
import com.asemantics.rdfcoder.model.QueryModelException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link JavaQueryModel}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavaQueryModelImpl implements JavaQueryModel {

    private CodeModel codeModel;

    public JavaQueryModelImpl(CodeModel codeModel) {  // Protecting creation.
        this.codeModel = codeModel;
    }

    public Asset getAsset() {
        return JavaCoderFactory.createJAsset(this);
    }

    public String[] getLibraries() {
        List<String> result = new ArrayList<String>();
        TripleIterator ti = codeModel.searchTriples(JavaCodeModel.ASSET, CodeModelBase.CONTAINS_LIBRARY, CodeModel.ALL_MATCH);
        try {
            while(ti.next()) {
                result.add(ti.getObject());
            }
        } finally {
            ti.close();
        }
        return result.toArray( new String[result.size()] );
    }

    public String getLibraryLocation(String library) {
        TripleIterator ti = codeModel.searchTriples(library, JavaCodeModel.LIBRARY_LOCATION, CodeModel.ALL_MATCH);
        try {
            if(ti.next()) {
                return ti.getObject();
            }
        } finally {
            ti.close();
        }
        return null;
    }

    public Date getLibraryDateTime(String library) {
        TripleIterator ti = codeModel.searchTriples(library, JavaCodeModel.LIBRARY_DATETIME, CodeModel.ALL_MATCH);
        try {
            if(ti.next()) {
                return CodeHandlerImpl.parseLibraryDatetime(ti.getObject());
            }
        } finally {
            ti.close();
        }
        return null;
    }

    public boolean packageExists(String pathToPackage) {
        TripleIterator ti = codeModel.searchTriples(
                CodeModelBase.prefixFullyQualifiedName( JavaCodeModel.PACKAGE_PREFIX, pathToPackage),
                CodeModel.SUBCLASSOF,
                JavaCodeModel.JPACKAGE
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean classExists(String pathToClass) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.CLASS_PREFIX, pathToClass),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JCLASS
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean interfaceExists(String pathToInterface) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.INTERFACE_PREFIX, pathToInterface),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JINTERFACE
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean attributeExists(String pathToAttribute) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.ATTRIBUTE_PREFIX, pathToAttribute),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JATTRIBUTE
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean methodExists(String pathToMethod) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.METHOD_PREFIX, pathToMethod),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JMETHOD
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean signatureExists(String pathToSignature) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.SIGNATURE_PREFIX, pathToSignature),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JSIGNATURE
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public boolean enumerationExists(String pathToEnumeration) {
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.ENUMERATION_PREFIX, pathToEnumeration),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JENUMERATION
        );
        try {
            if( ti.next() ) {
                return true;
            }
            return false;
        } finally {
            ti.close();
        }
    }

    public JPackage[] getAllPackages() {
        TripleIterator t1 = codeModel.searchTriples(JavaCodeModel.ALL_MATCH, JavaCodeModel.SUBCLASSOF, JavaCodeModel.JPACKAGE);
        List packages = new ArrayList();
        String subject;
        try {
            while(t1.next()) {
                subject = t1.getSubject();
                try {
                    packages.add( JavaCoderFactory.createJPackage(this, subject) );
                } catch (QueryModelException cme) {
                    System.out.println("subject " + subject);
                    throw new RuntimeException(cme);
                }
            }
        } finally {
            t1.close();
        }
        return (JPackage[]) packages.toArray( new JPackage[packages.size()] );
    }

    public JPackage[] getPackagesInto( String pathToPackage) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.PACKAGE_PREFIX, pathToPackage),
                JavaCodeModel.CONTAINS_PACKAGE,
                JavaCodeModel.ALL_MATCH
        );
        List packages = new ArrayList();
        try {
            while(t1.next()) {
                packages.add( JavaCoderFactory.createJPackage(this, t1.getObject()) );
            } 
        }finally {
            t1.close();
        }
        return (JPackage[]) packages.toArray( new Package[packages.size()] );
    }

    public JPackage getPackage(String pathToPackage) throws QueryModelException {
//        if( ! packageExists(pathToPackage) ) {
//            throw new QueryModelException("Package '" + pathToPackage + "' doesn't exist.");
//        }
        return JavaCoderFactory.createJPackage(this, pathToPackage);
    }

    public JInterface[] getAllInterfaces() {
        TripleIterator t1 = codeModel.searchTriples(JavaCodeModel.ALL_MATCH, JavaCodeModel.SUBCLASSOF, JavaCodeModel.JINTERFACE);
        List classes = new ArrayList();
        try {
            while(t1.next()) {
                try {
                    classes.add( JavaCoderFactory.createJInterface(this, t1.getSubject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return (JInterface[]) classes.toArray( new JInterface[classes.size()] );
    }

    public JInterface getInterface(String pathToInterface) throws QueryModelException {
        return JavaCoderFactory.createJInterface(this, pathToInterface);
    }

    public JClass[] getAllClasses() {
        TripleIterator t1 = codeModel.searchTriples(JavaCodeModel.ALL_MATCH, JavaCodeModel.SUBCLASSOF, JavaCodeModel.JCLASS);
        List classes = new ArrayList();
        try {
            while(t1.next()) {
                try {
                    classes.add( JavaCoderFactory.createJClass(this, t1.getSubject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return (JClass[]) classes.toArray( new JClass[classes.size()] );
    }

    public JClass getClazz(String pathToClass) throws QueryModelException {
        return JavaCoderFactory.createJClass(this, pathToClass);
    }

    public JInterface[] getInterfacesInto(String pathToContainer) throws QueryModelException {
        List interfaces = new ArrayList();
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.PACKAGE_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_INTERFACE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    interfaces.add(JavaCoderFactory.createJInterface(this, t1.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.CLASS_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_INTERFACE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    interfaces.add( JavaCoderFactory.createJInterface( this, t2.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t2.close();
        }
        return (JInterface[]) interfaces.toArray( new JClass[interfaces.size()] );    }

    public JClass[] getClassesInto(String pathToContainer) {
        List classes = new ArrayList();
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.PACKAGE_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_CLASS,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    classes.add(JavaCoderFactory.createJClass(this, t1.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.CLASS_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_CLASS,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    classes.add( JavaCoderFactory.createJClass(this, t2.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t2.close();
        }
        TripleIterator t3 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.INTERFACE_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_CLASS,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t3.next()) {
                try {
                    classes.add( JavaCoderFactory.createJClass(this, t3.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t3.close();
        }
        return (JClass[]) classes.toArray( new JClass[classes.size()] );
    }

    public JAttribute[] getAttributesInto(String pathToContainer) throws QueryModelException {
        List attributes = new ArrayList();
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.CLASS_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_ATTRIBUTE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                attributes.add( JavaCoderFactory.createJAttribute(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.INTERFACE_PREFIX, pathToContainer),
                JavaCodeModel.CONTAINS_ATTRIBUTE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    attributes.add( JavaCoderFactory.createJAttribute(this, t2.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t2.close();
        }
        return (JAttribute[]) attributes.toArray( new JAttribute[attributes.size()] );
    }

    public JAttribute getAttribute(String pathToAttribute) throws QueryModelException {
//        if( ! attributeExists(pathToAttribute) ) {
//            throw new QueryModelException("Attribute ' " + pathToAttribute + "' is not defined.");
//        }
        // Creating the attribute type.
        return JavaCoderFactory.createJAttribute(
                this,
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.ATTRIBUTE_PREFIX, pathToAttribute)
        );
    }

    public JavaCodeModel.JType getAttributeType(String pathToAttribute) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.ATTRIBUTE_PREFIX, pathToAttribute),
                JavaCodeModel.ATTRIBUTE_TYPE, JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                JavaCodeModel.JType result =  JavaCodeModel.rdfTypeToJType(t1.getObject());
                return result;
            }
        } finally {
            t1.close();
        }
        throw new QueryModelException("Cannot find attribute '" + pathToAttribute + "'.");
    }

    public JMethod[] getMethodsInto(String pathToClass) throws QueryModelException {
        List methods = new ArrayList();
        TripleIterator t1 = codeModel.searchTriples(
               JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.CLASS_PREFIX, pathToClass),
               JavaCodeModel.CONTAINS_METHOD,
               JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                methods.add( JavaCoderFactory.createJMethod(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
               JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.INTERFACE_PREFIX, pathToClass),
               JavaCodeModel.CONTAINS_METHOD,
               JavaCodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                methods.add( JavaCoderFactory.createJMethod(this, t2.getObject()) );
            }
        } finally {
            t2.close();
        }
        TripleIterator t3 = codeModel.searchTriples(
               JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.ENUMERATION_PREFIX, pathToClass),
               JavaCodeModel.CONTAINS_METHOD,
               JavaCodeModel.ALL_MATCH
        );
        try {
            while(t3.next()) {
                methods.add( JavaCoderFactory.createJMethod(this, t3.getObject()) );
            }
        } finally {
            t3.close();
        }
        return (JMethod[]) methods.toArray( new JMethod[methods.size()] );
    }

    public JMethod getMethod(String pathToMethod) throws QueryModelException {
        return JavaCoderFactory.createJMethod(this, pathToMethod);
    }

    public JEnumeration[] getEnumerationsInto(String pathToContainer) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
               JavaCodeModel.ALL_MATCH,
               JavaCodeModel.CONTAINS_ENUMERATION,
               JavaCodeModel.ALL_MATCH
        );
        List enumerations = new ArrayList();
        try {
            while(t1.next()) {
                enumerations.add( JavaCoderFactory.createJEnumeration(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        return (JEnumeration[]) enumerations.toArray( new JEnumeration[enumerations.size()] );
    }

    public JEnumeration getEnumeration(String pathToEnumeration) throws QueryModelException {
        return JavaCoderFactory.createJEnumeration(this, pathToEnumeration);
    }

    public String[] getElements(String pathToEnumeration) {
        TripleIterator t1 = codeModel.searchTriples(
               JavaCodeModel.prefixFullyQualifiedName( JavaCodeModel.ENUMERATION_PREFIX, pathToEnumeration),
               JavaCodeModel.CONTAINS_ELEMENT,
               JavaCodeModel.ALL_MATCH
        );
        List enumerations = new ArrayList();
        try {
            while(t1.next()) {
                enumerations.add( t1.getObject() );
            }
        } finally {
            t1.close();
        }
        return (String[]) enumerations.toArray( new String[enumerations.size()] );
    }

    public JSignature[] getSignatures(String pathToMethod) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.METHOD_PREFIX, pathToMethod),
                JavaCodeModel.CONTAINS_SIGNATURE,
                JavaCodeModel.ALL_MATCH
        );
        TripleIterator t2;
        TripleIterator t3;
        List signatures = new ArrayList();
        List parametersIntoSignature = new ArrayList();
        try {
            while(t1.next()) { //Signatures
                t2 = codeModel.searchTriples(t1.getObject(), JavaCodeModel.PARAMETER_TYPE, JavaCodeModel.ALL_MATCH);
                parametersIntoSignature.clear();
                try {
                    while(t2.next()) { //Types.
                        parametersIntoSignature.add(JavaCodeModel.rdfTypeToJType(t2.getSubject()));
                    } 
                } finally {
                    t2.close();
                }
                t3 = codeModel.searchTriples(t1.getObject(), JavaCodeModel.RETURN_TYPE, JavaCodeModel.ALL_MATCH);
                try {
                    if( ! t3.next() ) {
                        throw new QueryModelException("Cannot find the return type for the method '" + pathToMethod + "'.");
                    }
                    signatures.add(
                        JavaCoderFactory.createJSignature(
                                this,
                                t1.getObject(),
                                ( (JavaCodeModel.JType[]) parametersIntoSignature.toArray(new JavaCodeModel.JType[parametersIntoSignature.size()]) ),
                                    JavaCodeModel.rdfTypeToJType( t3.getObject() )
                            )
                    );
                } finally {
                    t3.close();
                }
            }
        } finally {
            t1.close();
        }
        return (JSignature[]) signatures.toArray( new JSignature[signatures.size()] );
    }

    public JavaCodeModel.JType[] getParameters(String pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.SIGNATURE_PREFIX, pathToSignature),
            JavaCodeModel.CONTAINS_PARAMETER,
            JavaCodeModel.ALL_MATCH
        );
        List result;
        try {
            result = new ArrayList();
            while(t1.next()) {
                result.add( JavaCodeModel.rdfTypeToJType( t1.getObject() ) );
            }
        } finally{
            t1.close();
        }
        return (JavaCodeModel.JType[]) result.toArray( new JavaCodeModel.JType[result.size()] );
    }

    public JavaCodeModel.JType getReturnType(String pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.SIGNATURE_PREFIX, pathToSignature),
            JavaCodeModel.RETURN_TYPE,
            JavaCodeModel.ALL_MATCH
        );
        try {
            if( t1.next() ) {
                return JavaCodeModel.rdfTypeToJType( t1.getObject() );
            } else {
                throw new QueryModelException("Cannot find return type.");
            }
        } finally{
            t1.close();
        }
    }

    public JavaCodeModel.JVisibility getVisibility(String pathToEntity) throws QueryModelException {
        String prefix = JavaCodeModel.getPrefixFromRDFType( getRDFType(pathToEntity) );
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(prefix, pathToEntity),
                JavaCodeModel.HAS_VISIBILITY,
                JavaCodeModel.ALL_MATCH
        );
        String result = null;
        try {
            if( t1.next() ) {
                result = t1.getObject();
            }
        } finally {
            t1.close();
        }
        return result == null ? JavaCodeModel.JVisibility.DEFAULT : JavaCodeModel.JVisibility.toJVisibility(result);
    }

    public JavaCodeModel.JModifier[] getModifiers(String pathToEntity) throws QueryModelException {
        String prefix = JavaCodeModel.getPrefixFromRDFType( getRDFType(pathToEntity) );
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.prefixFullyQualifiedName(prefix, pathToEntity),
                JavaCodeModel.HAS_MODIFIERS,
                JavaCodeModel.ALL_MATCH
        );
        String result = null;
        try {
            if( t1.next() ) {
                result = t1.getObject();
            }
        } finally {
            t1.close();
        }
        return result == null ? new JavaCodeModel.JModifier[]{} : JavaCodeModel.JModifier.toModifiers( result );
    }

    public String getRDFType(String pathToEntity) throws QueryModelException {
        String rdfType;

        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.CLASS_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.INTERFACE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.METHOD_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.PACKAGE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.ATTRIBUTE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.CONSTRUCTOR_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( JavaCodeModel.prefixFullyQualifiedName(JavaCodeModel.ENUMERATION_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }

        throw new QueryModelException("Cannot find the full qualification of entity : '" + pathToEntity + "'.");
    }

    public String toString() {
        return JavaQueryModelImpl.class.getName() + "{ " +
                "packages: "   + getAllPackages().length  + ", " +
                "classes: "    + getAllClasses().length   + ", " +
                "interfaces: " + getAllInterfaces().length       +
                "}";
    }

    /**
     * Returns the RDF type of the prefixed entity if exists, <code>null</code>otherwise.
     *
     * @param prefixedPathToEntity the entity to check.
     * @return
     */
    private String checkType(String prefixedPathToEntity) {
        TripleIterator t1 = codeModel.searchTriples(prefixedPathToEntity, JavaCodeModel.SUBCLASSOF, JavaCodeModel.ALL_MATCH);
        try {
            if( t1.next() ) {
                return t1.getObject();
            }
        } finally {
            t1.close();
        }
        return null;
    }

}
