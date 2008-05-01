package com.asemantics.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a method signature.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class QueryModelImpl implements QueryModel {

    private CodeModel codeModel;

    protected QueryModelImpl(CodeModel codeModel) {  // Protecting creation.
        this.codeModel = codeModel;
    }

    public JAsset getAsset() {
        return CoderFactory.createJAsset(this);
    }

    public String[] getLibraries() {
        List<String> result = new ArrayList<String>();
        TripleIterator ti = codeModel.searchTriples(CodeModel.JASSET, CodeModel.CONTAINS_LIBRARY, CodeModel.ALL_MATCH);
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
        TripleIterator ti = codeModel.searchTriples(library, CodeModel.LIBRARY_LOCATION, CodeModel.ALL_MATCH);
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
        TripleIterator ti = codeModel.searchTriples(library, CodeModel.LIBRARY_DATETIME, CodeModel.ALL_MATCH);
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
                CodeModel.prefixFullyQualifiedName( CodeModel.PACKAGE_PREFIX, pathToPackage),
                CodeModel.SUBCLASSOF,
                CodeModel.JPACKAGE
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
                CodeModel.prefixFullyQualifiedName( CodeModel.CLASS_PREFIX, pathToClass),
                CodeModel.SUBCLASSOF,
                CodeModel.JCLASS
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
                CodeModel.prefixFullyQualifiedName( CodeModel.INTERFACE_PREFIX, pathToInterface),
                CodeModel.SUBCLASSOF,
                CodeModel.JINTERFACE
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
                CodeModel.prefixFullyQualifiedName( CodeModel.ATTRIBUTE_PREFIX, pathToAttribute),
                CodeModel.SUBCLASSOF,
                CodeModel.JATTRIBUTE
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
                CodeModel.prefixFullyQualifiedName( CodeModel.METHOD_PREFIX, pathToMethod),
                CodeModel.SUBCLASSOF,
                CodeModel.JMETHOD
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
                CodeModel.prefixFullyQualifiedName( CodeModel.SIGNATURE_PREFIX, pathToSignature),
                CodeModel.SUBCLASSOF,
                CodeModel.JSIGNATURE
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
                CodeModel.prefixFullyQualifiedName( CodeModel.ENUMERATION_PREFIX, pathToEnumeration),
                CodeModel.SUBCLASSOF,
                CodeModel.JENUMERATION
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
        TripleIterator t1 = codeModel.searchTriples(CodeModel.ALL_MATCH, CodeModel.SUBCLASSOF, CodeModel.JPACKAGE);
        List packages = new ArrayList();
        try {
            while(t1.next()) {
                try {
                    packages.add( CoderFactory.createJPackage(this, t1.getSubject()) );
                } catch (QueryModelException cme) {
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
                CodeModel.prefixFullyQualifiedName(CodeModel.PACKAGE_PREFIX, pathToPackage),
                CodeModel.CONTAINS_PACKAGE,
                CodeModel.ALL_MATCH
        );
        List packages = new ArrayList();
        try {
            while(t1.next()) {
                packages.add( CoderFactory.createJPackage(this, t1.getObject()) );
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
        return CoderFactory.createJPackage(this, pathToPackage);
    }

    public JInterface[] getAllInterfaces() {
        TripleIterator t1 = codeModel.searchTriples(CodeModel.ALL_MATCH, CodeModel.SUBCLASSOF, CodeModel.JINTERFACE);
        List classes = new ArrayList();
        try {
            while(t1.next()) {
                try {
                    classes.add( CoderFactory.createJInterface(this, t1.getSubject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return (JInterface[]) classes.toArray( new JInterface[classes.size()] );
    }

    public JClass[] getAllClasses() {
        TripleIterator t1 = codeModel.searchTriples(CodeModel.ALL_MATCH, CodeModel.SUBCLASSOF, CodeModel.JCLASS);
        List classes = new ArrayList();
        try {
            while(t1.next()) {
                try {
                    classes.add( CoderFactory.createJClass(this, t1.getSubject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        return (JClass[]) classes.toArray( new JClass[classes.size()] );
    }

    public JInterface[] getInterfacesInto(String pathToContainer) throws QueryModelException {
        List interfaces = new ArrayList();
        TripleIterator t1 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName( CodeModel.PACKAGE_PREFIX, pathToContainer),
                CodeModel.CONTAINS_INTERFACE,
                CodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    interfaces.add(CoderFactory.createJInterface(this, t1.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, pathToContainer),
                CodeModel.CONTAINS_INTERFACE,
                CodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    interfaces.add( CoderFactory.createJInterface( this, t2.getObject()) );
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
                CodeModel.prefixFullyQualifiedName( CodeModel.PACKAGE_PREFIX, pathToContainer),
                CodeModel.CONTAINS_CLASS,
                CodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                try {
                    classes.add(CoderFactory.createJClass(this, t1.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, pathToContainer),
                CodeModel.CONTAINS_CLASS,
                CodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    classes.add( CoderFactory.createJClass(this, t2.getObject()) );
                } catch (QueryModelException qme) {
                    throw new RuntimeException(qme);
                }
            }
        } finally {
            t2.close();
        }
        TripleIterator t3 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, pathToContainer),
                CodeModel.CONTAINS_CLASS,
                CodeModel.ALL_MATCH
        );
        try {
            while(t3.next()) {
                try {
                    classes.add( CoderFactory.createJClass(this, t3.getObject()) );
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
                CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, pathToContainer),
                CodeModel.CONTAINS_ATTRIBUTE,
                CodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                attributes.add( CoderFactory.createJAttribute(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, pathToContainer),
                CodeModel.CONTAINS_ATTRIBUTE,
                CodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                try {
                    attributes.add( CoderFactory.createJAttribute(this, t2.getObject()) );
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
        return CoderFactory.createJAttribute(
                this,
                CodeModel.prefixFullyQualifiedName(CodeModel.ATTRIBUTE_PREFIX, pathToAttribute)
        );
    }

    public CodeModel.JType getAttributeType(String pathToAttribute) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(CodeModel.ATTRIBUTE_PREFIX, pathToAttribute),
                CodeModel.ATTRIBUTE_TYPE, CodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                CodeModel.JType result =  CodeModel.rdfTypeToJType(t1.getObject());
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
               CodeModel.prefixFullyQualifiedName( CodeModel.CLASS_PREFIX, pathToClass),
               CodeModel.CONTAINS_METHOD,
               CodeModel.ALL_MATCH
        );
        try {
            while(t1.next()) {
                methods.add( CoderFactory.createJMethod(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        TripleIterator t2 = codeModel.searchTriples(
               CodeModel.prefixFullyQualifiedName( CodeModel.INTERFACE_PREFIX, pathToClass),
               CodeModel.CONTAINS_METHOD,
               CodeModel.ALL_MATCH
        );
        try {
            while(t2.next()) {
                methods.add( CoderFactory.createJMethod(this, t2.getObject()) );
            }
        } finally {
            t2.close();
        }
        TripleIterator t3 = codeModel.searchTriples(
               CodeModel.prefixFullyQualifiedName( CodeModel.ENUMERATION_PREFIX, pathToClass),
               CodeModel.CONTAINS_METHOD,
               CodeModel.ALL_MATCH
        );
        try {
            while(t3.next()) {
                methods.add( CoderFactory.createJMethod(this, t3.getObject()) );
            }
        } finally {
            t3.close();
        }
        return (JMethod[]) methods.toArray( new JMethod[methods.size()] );
    }

    public JMethod getMethod(String pathToMethod) throws QueryModelException {
        return CoderFactory.createJMethod(this, pathToMethod);
    }

    public JEnumeration[] getEnumerationsInto(String pathToContainer) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
               CodeModel.ALL_MATCH,
               CodeModel.CONTAINS_ENUMERATION,
               CodeModel.ALL_MATCH
        );
        List enumerations = new ArrayList();
        try {
            while(t1.next()) {
                enumerations.add( CoderFactory.createJEnumeration(this, t1.getObject()) );
            }
        } finally {
            t1.close();
        }
        return (JEnumeration[]) enumerations.toArray( new JEnumeration[enumerations.size()] );
    }

    public JEnumeration getEnumeration(String pathToEnumeration) throws QueryModelException {
        return CoderFactory.createJEnumeration(this, pathToEnumeration);
    }

    public String[] getElements(String pathToEnumeration) {
        TripleIterator t1 = codeModel.searchTriples(
               CodeModel.prefixFullyQualifiedName( CodeModel.ENUMERATION_PREFIX, pathToEnumeration),
               CodeModel.CONTAINS_ELEMENT,
               CodeModel.ALL_MATCH
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
                CodeModel.prefixFullyQualifiedName(CodeModel.METHOD_PREFIX, pathToMethod),
                CodeModel.CONTAINS_SIGNATURE,
                CodeModel.ALL_MATCH
        );
        TripleIterator t2;
        TripleIterator t3;
        List signatures = new ArrayList();
        List parametersIntoSignature = new ArrayList();
        try {
            while(t1.next()) { //Signatures
                t2 = codeModel.searchTriples(t1.getObject(), CodeModel.PARAMETER_TYPE, CodeModel.ALL_MATCH);
                parametersIntoSignature.clear();
                try {
                    while(t2.next()) { //Types.
                        parametersIntoSignature.add(CodeModel.rdfTypeToJType(t2.getSubject()));
                    } 
                } finally {
                    t2.close();
                }
                t3 = codeModel.searchTriples(t1.getObject(), CodeModel.RETURN_TYPE, CodeModel.ALL_MATCH);
                try {
                    if( ! t3.next() ) {
                        throw new QueryModelException("Cannot find the return type for the method '" + pathToMethod + "'.");
                    }
                    signatures.add(
                        CoderFactory.createJSignature(
                                this,
                                t1.getObject(),
                                ( (CodeModel.JType[]) parametersIntoSignature.toArray(new CodeModel.JType[parametersIntoSignature.size()]) ),
                                    CodeModel.rdfTypeToJType( t3.getObject() )
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

    public CodeModel.JType[] getParameters(String pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            CodeModel.prefixFullyQualifiedName(CodeModel.SIGNATURE_PREFIX, pathToSignature),
            CodeModel.CONTAINS_PARAMETER,
            CodeModel.ALL_MATCH
        );
        List result;
        try {
            result = new ArrayList();
            while(t1.next()) {
                result.add( CodeModel.rdfTypeToJType( t1.getObject() ) );
            }
        } finally{
            t1.close();
        }
        return (CodeModel.JType[]) result.toArray( new CodeModel.JType[result.size()] );
    }

    public CodeModel.JType getReturnType(String pathToSignature) throws QueryModelException {
        TripleIterator t1 = codeModel.searchTriples(
            CodeModel.prefixFullyQualifiedName(CodeModel.SIGNATURE_PREFIX, pathToSignature),
            CodeModel.RETURN_TYPE,
            CodeModel.ALL_MATCH
        );
        try {
            if( t1.next() ) {
                return CodeModel.rdfTypeToJType( t1.getObject() );
            } else {
                throw new QueryModelException("Cannot find return type.");
            }
        } finally{
            t1.close();
        }
    }

    public CodeModel.JVisibility getVisibility(String pathToEntity) throws QueryModelException {
        String prefix = CodeModel.getPrefixFromRDFType( getRDFType(pathToEntity) );
        TripleIterator t1 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(prefix, pathToEntity),
                CodeModel.HAS_VISIBILITY,
                CodeModel.ALL_MATCH
        );
        String result = null;
        try {
            if( t1.next() ) {
                result = t1.getObject();
            }
        } finally {
            t1.close();
        }
        return result == null ? CodeModel.JVisibility.DEFAULT : CodeModel.JVisibility.toJVisibility(result);
    }

    public CodeModel.JModifier[] getModifiers(String pathToEntity) throws QueryModelException {
        String prefix = CodeModel.getPrefixFromRDFType( getRDFType(pathToEntity) );
        TripleIterator t1 = codeModel.searchTriples(
                CodeModel.prefixFullyQualifiedName(prefix, pathToEntity),
                CodeModel.HAS_MODIFIERS,
                CodeModel.ALL_MATCH
        );
        String result = null;
        try {
            if( t1.next() ) {
                result = t1.getObject();
            }
        } finally {
            t1.close();
        }
        return result == null ? new CodeModel.JModifier[]{} : CodeModel.JModifier.toModifiers( result );
    }

    public String getRDFType(String pathToEntity) throws QueryModelException {
        String rdfType;

        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.METHOD_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.PACKAGE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.ATTRIBUTE_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.CONSTRUCTOR_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }
        rdfType = checkType( CodeModel.prefixFullyQualifiedName(CodeModel.ENUMERATION_PREFIX, pathToEntity) );
        if(rdfType != null) {
            return rdfType;
        }

        throw new QueryModelException("Cannot find the full qualification of entity : '" + pathToEntity + "'.");
    }

    /**
     * Returns the RDF type of the prefixed entity if exists, <code>null</code>otherwise.
     *
     * @param prefixedPathToEntity the entity to check.
     * @return
     */
    private String checkType(String prefixedPathToEntity) {
        TripleIterator t1 = codeModel.searchTriples(prefixedPathToEntity, CodeModel.SUBCLASSOF, CodeModel.ALL_MATCH);
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
