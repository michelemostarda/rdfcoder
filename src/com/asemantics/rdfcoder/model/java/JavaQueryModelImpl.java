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
import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.QueryModelException;
import com.asemantics.rdfcoder.model.TripleIterator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link JavaQueryModel}.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavaQueryModelImpl implements JavaQueryModel {

    private static final JavaCodeModel.JModifier[] EMPTY_MODIFIERS_LIST = new JavaCodeModel.JModifier[0];

    private CodeModel codeModel;

    public JavaQueryModelImpl(CodeModel codeModel) {  // Protecting creation.
        this.codeModel = codeModel;
    }

    public Asset getAsset() {
        return JavaCoderFactory.createJAsset(this);
    }

    public String[] getLibraries() {
        List<String> result = new ArrayList<String>();
        TripleIterator ti = codeModel.searchTriples(
                JavaCodeModel.ASSET,
                CodeModelBase.CONTAINS_LIBRARY,
                CodeModel.ALL_MATCH

        );
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
                return JavaCodeHandlerImpl.parseLibraryDatetime(ti.getObject());
            }
        } finally {
            ti.close();
        }
        return null;
    }

    public boolean packageExists(Identifier pathToPackage) {
        TripleIterator ti = codeModel.searchTriples(
                pathToPackage.getIdentifier(),
                CodeModel.SUBCLASSOF,
                JavaCodeModel.JPACKAGE
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean classExists(Identifier pathToClass) {
        TripleIterator ti = codeModel.searchTriples(
                pathToClass.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JCLASS
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean interfaceExists(Identifier pathToInterface) {
        TripleIterator ti = codeModel.searchTriples(
                pathToInterface.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JINTERFACE
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean attributeExists(Identifier pathToAttribute) {
        TripleIterator ti = codeModel.searchTriples(
                pathToAttribute.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JATTRIBUTE
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean methodExists(Identifier pathToMethod) {
        TripleIterator ti = codeModel.searchTriples(
                pathToMethod.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JMETHOD
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean signatureExists(Identifier pathToSignature) {
        TripleIterator ti = codeModel.searchTriples(
                pathToSignature.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JSIGNATURE
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public boolean enumerationExists(Identifier pathToEnumeration) {
        TripleIterator ti = codeModel.searchTriples(
                pathToEnumeration.getIdentifier(),
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JENUMERATION
        );
        try {
            return ti.next();
        } finally {
            ti.close();
        }
    }

    public JPackage[] getAllPackages() {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JPACKAGE
        );
        List<JPackage> packages = new ArrayList<JPackage>();
        String subject;
        try {
            while(t1.next()) {
                subject = t1.getSubject();
                try {
                    packages.add( JavaCoderFactory.createJPackage(this, IdentifierReader.readIdentifier( subject) ));
                } catch (QueryModelException cme) {
                    throw new RuntimeException("Error while retrieving packages.", cme);
                }
            }
        } finally {
            t1.close();
        }
        return packages.toArray( new JPackage[packages.size()] );
    }

    public JPackage[] getPackagesInto(Identifier pathToPackage) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                pathToPackage.getIdentifier(),
                JavaCodeModel.CONTAINS_PACKAGE,
                JavaCodeModel.ALL_MATCH
        );
        List<JPackage> packages = new ArrayList<JPackage>();
        try {
            while(t1.next()) {
                packages.add( JavaCoderFactory.createJPackage(this, IdentifierReader.readIdentifier( t1.getObject()) ));
            } 
        }finally {
            t1.close();
        }
        return packages.toArray( new JPackage[ packages.size() ] );
    }

    public JPackage getPackage(Identifier pathToPackage) throws QueryModelException {
        return JavaCoderFactory.createJPackage(this, pathToPackage);
    }

    public JInterface[] getAllInterfaces() {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JINTERFACE
        );
        List<JInterface> interfaces = new ArrayList<JInterface>();
        try {
            while(t1.next()) {
                try {
                    interfaces.add( JavaCoderFactory.createJInterface(
                            this,
                            IdentifierReader.readIdentifier( t1.getSubject() ))
                    );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return interfaces.toArray( new JInterface[interfaces.size()] );
    }

    public JInterface getInterface(Identifier pathToInterface) throws QueryModelException {
        return JavaCoderFactory.createJInterface(this, pathToInterface);
    }

    public JClass[] getAllClasses() {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JCLASS
        );
        List<JClass> classes = new ArrayList<JClass>();
        try {
            while(t1.next()) {
                try {
                    classes.add( JavaCoderFactory.createJClass(this, IdentifierReader.readIdentifier( t1.getSubject() )));
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return classes.toArray( new JClass[classes.size()] );
    }

    public JClass getClazz(Identifier pathToClass) throws QueryModelException {
        return JavaCoderFactory.createJClass(this, pathToClass);
    }

    public JInterface[] getInterfacesInto(Identifier pathToContainer) throws QueryModelException {
        List<JInterface> interfaces = new ArrayList<JInterface>();
        TripleIterator t1 = codeModel.searchTriples(
                pathToContainer.getIdentifier(),
                JavaCodeModel.CONTAINS_INTERFACE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    interfaces.add(
                            JavaCoderFactory.createJInterface(this, IdentifierReader.readIdentifier( t1.getObject() ))
                    );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return interfaces.toArray( new JInterface[interfaces.size()] );
    }

    public JClass[] getClassesInto(Identifier pathToContainer) {
        List<JClass> classes = new ArrayList<JClass>();
        TripleIterator t1 = codeModel.searchTriples(
                pathToContainer.getIdentifier(),
                JavaCodeModel.CONTAINS_CLASS,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    classes.add(JavaCoderFactory.createJClass(this, IdentifierReader.readIdentifier( t1.getObject() )));
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return classes.toArray( new JClass[classes.size()] );
    }

    public JAttribute[] getAttributesInto(Identifier pathToContainer) throws QueryModelException {
        List<JAttribute> attributes = new ArrayList<JAttribute>();
        TripleIterator t1 = codeModel.searchTriples(
                pathToContainer.getIdentifier(),
                JavaCodeModel.CONTAINS_ATTRIBUTE,
                JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                attributes.add( JavaCoderFactory.createJAttribute(this, IdentifierReader.readIdentifier( t1.getObject()) ));
            }
        } finally {
            t1.close();
        }
        return attributes.toArray( new JAttribute[attributes.size()] );
    }

    public JAttribute getAttribute(Identifier pathToAttribute) throws QueryModelException {
        // Creating the attribute type.
        return JavaCoderFactory.createJAttribute(
                this,
                pathToAttribute
        );
    }

    public JavaCodeModel.JType getAttributeType(Identifier pathToAttribute) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                pathToAttribute.getIdentifier(),
                JavaCodeModel.ATTRIBUTE_TYPE, JavaCodeModel.ALL_MATCH
        );
        try {
            if(t1.next()) {
                return JavaCodeModel.rdfTypeToJType(t1.getObject());
            }
        } finally {
            t1.close();
        }
        throw new QueryModelException("Cannot find attribute '" + pathToAttribute + "'.");
    }

    public JMethod[] getMethodsInto(Identifier pathToClass) throws QueryModelException {
        List<JMethod> methods = new ArrayList<JMethod>();
        TripleIterator t1 = codeModel.searchTriples(
               pathToClass.getIdentifier(),
               JavaCodeModel.CONTAINS_METHOD,
               JavaCodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                methods.add( JavaCoderFactory.createJMethod(this, IdentifierReader.readIdentifier(t1.getObject()) ));
            }
        } finally {
            t1.close();
        }
        return methods.toArray( new JMethod[methods.size()] );
    }

    public JMethod getMethod(Identifier pathToMethod) throws QueryModelException {
        return JavaCoderFactory.createJMethod(this, pathToMethod);
    }

    public JEnumeration[] getAllEnumerations() {
        TripleIterator t1 = codeModel.searchTriples(
                JavaCodeModel.ALL_MATCH,
                JavaCodeModel.SUBCLASSOF,
                JavaCodeModel.JENUMERATION
        );
        List<JEnumeration> packages = new ArrayList<JEnumeration>();
        String subject;
        try {
            while(t1.next()) {
                subject = t1.getSubject();
                try {
                    packages.add(
                            JavaCoderFactory.createJEnumeration(this, IdentifierReader.readIdentifier( subject) )
                    );
                } catch (QueryModelException cme) {
                    throw new RuntimeException("Error while retrieving enumerations.", cme);
                }
            }
        } finally {
            t1.close();
        }
        return packages.toArray( new JEnumeration[packages.size()] );
    }

    public JEnumeration[] getEnumerationsInto(Identifier pathToContainer) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
               JavaCodeModel.ALL_MATCH,
               JavaCodeModel.CONTAINS_ENUMERATION,
               JavaCodeModel.ALL_MATCH
        );
        List<JEnumeration> enumerations = new ArrayList<JEnumeration>();
        try {
            while(t1.next()) {
                enumerations.add(
                        JavaCoderFactory.createJEnumeration(this, IdentifierReader.readIdentifier(t1.getObject() ))
                );
            }
        } finally {
            t1.close();
        }
        return enumerations.toArray( new JEnumeration[enumerations.size()] );
    }

    public JEnumeration getEnumeration(Identifier pathToEnumeration) throws QueryModelException {
        return JavaCoderFactory.createJEnumeration(this, pathToEnumeration);
    }

    public String[] getElements(Identifier pathToEnumeration) {
        TripleIterator t1 = codeModel.searchTriples(
               pathToEnumeration.getIdentifier(),
               JavaCodeModel.CONTAINS_ELEMENT,
               JavaCodeModel.ALL_MATCH
        );
        List<String> enumerations = new ArrayList<String>();
        try {
            while(t1.next()) {
                enumerations.add( t1.getObject() );
            }
        } finally {
            t1.close();
        }
        return enumerations.toArray( new String[enumerations.size()] );
    }

    public JSignature[] getSignatures(Identifier pathToMethod) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                pathToMethod.getIdentifier(),
                JavaCodeModel.CONTAINS_SIGNATURE,
                JavaCodeModel.ALL_MATCH
        );
        TripleIterator t2;
        TripleIterator t3;
        List<JSignature> signatures = new ArrayList<JSignature>();
        List<JavaCodeModel.JType> parametersIntoSignature = new ArrayList<JavaCodeModel.JType>();
        try {
            while(t1.next()) { //Signatures
                t2 = codeModel.searchTriples(t1.getObject(), JavaCodeModel.PARAMETER_TYPE, JavaCodeModel.ALL_MATCH);
                parametersIntoSignature.clear();
                try {
                    while(t2.next()) { //Types.
                        parametersIntoSignature.add( JavaCodeModel.rdfTypeToJType(t2.getSubject()) );
                    } 
                } finally {
                    t2.close();
                }
                t3 = codeModel.searchTriples(t1.getObject(), JavaCodeModel.RETURN_TYPE, JavaCodeModel.ALL_MATCH);
                try {
                    if( ! t3.next() ) {
                        throw new QueryModelException(
                                "Cannot find the return type for the method '" + pathToMethod + "'."
                        );
                    }
                    signatures.add(
                        JavaCoderFactory.createJSignature(
                                this,
                                IdentifierReader.readIdentifier(t1.getObject()),
                                ( parametersIntoSignature.toArray(new JavaCodeModel.JType[parametersIntoSignature.size()]) ),
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
        return signatures.toArray( new JSignature[signatures.size()] );
    }

    public JavaCodeModel.JType[] getParameters(Identifier pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            pathToSignature.getIdentifier(),
            JavaCodeModel.CONTAINS_PARAMETER,
            JavaCodeModel.ALL_MATCH
        );

        // Retrieve parameter names.
        List<String> parameterNames = new ArrayList<String>();
        try {
            while(t1.next()) {
                parameterNames.add(t1.getObject());
            }
        } finally{
            t1.close();
        }

        // Retrieve parameter types.
        List<JavaCodeModel.JType> result = new ArrayList<JavaCodeModel.JType>();
        for (String parameterName : parameterNames) {
            TripleIterator t2 = codeModel.searchTriples(
                    parameterName,
                    JavaCodeModel.PARAMETER_TYPE,
                    JavaCodeModel.ALL_MATCH
            );
            try {
                t2.next(); // Expected to be found.
                result.add(JavaCodeModel.rdfTypeToJType( t2.getObject()) );
            } finally {
                t2.close();
            }
        }

        return result.toArray( new JavaCodeModel.JType[result.size()] );
    }

    public JavaCodeModel.JType getReturnType(Identifier pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            pathToSignature.getIdentifier(),
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

    public JavaCodeModel.JVisibility getVisibility(Identifier pathToEntity) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                pathToEntity.getIdentifier(),
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

    public JavaCodeModel.JModifier[] getModifiers(Identifier pathToEntity) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                pathToEntity.getIdentifier(),
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
        return result == null ? EMPTY_MODIFIERS_LIST : JavaCodeModel.JModifier.toModifiers( result );
    }

    public String toString() {
        return String.format(
                "%s{packages: %s, classes: %s, interfaces: %s, enumerations: %s}",
                this.getClass().getName(),
                getAllPackages().length,
                getAllClasses().length,
                getAllInterfaces().length,
                getAllEnumerations().length
        );
    }

}
