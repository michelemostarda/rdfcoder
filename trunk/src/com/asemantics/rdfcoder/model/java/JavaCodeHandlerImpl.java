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

import com.asemantics.rdfcoder.RDFCoder;
import com.asemantics.rdfcoder.model.CodeHandlerException;
import com.asemantics.rdfcoder.model.CodeModel;
import com.asemantics.rdfcoder.model.CodeModelBase;
import com.asemantics.rdfcoder.model.ErrorListener;
import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.IdentifierBuilder;
import com.asemantics.rdfcoder.model.IdentifierReader;
import com.asemantics.rdfcoder.model.TripleIterator;
import com.asemantics.rdfcoder.sourceparse.JavadocEntry;
import com.asemantics.rdfcoder.sourceparse.ObjectsTable;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * The {@link JavaCodeHandler}
 * default implementation.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavaCodeHandlerImpl implements JavaCodeHandler {

    private static final Logger logger = Logger.getLogger(JavaCodeHandlerImpl.class);

    /**
     * Formats the library date time.
     */
    private static final SimpleDateFormat libraryDatetimeFormatter = new SimpleDateFormat(LIBRARY_DATETIME_FORMAT);

    public static final String formatLibraryDatetime(Date date) {
        return libraryDatetimeFormatter.format(date);
    }

    public static final Date parseLibraryDatetime(String datetime) {
        try {
            return libraryDatetimeFormatter.parse(datetime);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * The default package.
     */
    private final Identifier DEFAULT_PACKAGE = IdentifierReader.readPackage("");

    /**
     * The code model on which store code events.
     */
    private CodeModelBase model;

    /**
     * The library name.
     */
    private String libraryName;

    /**
     * String the location of the library. 
     */
    private String libraryLocation;

    /**
     * The stack of current packages.
     */
    private Stack<Identifier> packagesStack;

    /**
     * The stack of current container, intended as class, interface or enumeration.
     */
    private Stack<Identifier> containersStack;

    /**
     * Expresses the RDFS subclass relationship.
     */
    private static final String SUBCLASSOF = RDFS.subClassOf.getURI();

    /**
     * <code>true</code> if start process is parsing is started.
     */
    private boolean parsingStarted = false;

    /**
     * <code>true</code> if inside a compilation unit.
     */
    private boolean compilationUnitStarted = false;

    /**
     * The list of error listeners.
     */
    private List<ErrorListener> errorListeners;


    /**
     * Constructor.
     * 
     * @param cmb the code model to use to store handler data.
     */
    public JavaCodeHandlerImpl(CodeModelBase cmb) {
        if(cmb == null) {
            throw new NullPointerException();
        }
        model           = cmb;
        packagesStack   = new Stack<Identifier>();
        containersStack = new Stack<Identifier>();
    }

    /**
     * @return the underlying model.
     */
    protected CodeModel getModel() {
        return model;
    }

    protected void checkLibraryName(String name) {
        TripleIterator ti = model.searchTriples(JavaCodeModel.ASSET, JavaCodeModel.CONTAINS_LIBRARY, CodeModel.ALL_MATCH);
        final String targetLibraryName = CodeModelBase.prefixFullyQualifiedName(JavaCodeModel.ASSET_PREFIX, name);
        try {
            while(ti.next()) {
                if( targetLibraryName.equals(ti.getObject()) ) {
                    throw new CodeHandlerException("a library with name '" + name + "' already exists in the model asset.");
                }
            }
        } finally {
            ti.close();
        }
    }

    public void startParsing(String libraryName, String libraryLocation) {
        if(libraryName == null || libraryName.trim().length() == 0) {
            throw new CodeHandlerException("invalid library name: '" + libraryName + "'");
        }
        if(libraryLocation == null || libraryLocation.trim().length() == 0) {
            throw new CodeHandlerException("invalid library location: '" + libraryLocation + "'");
        }

        checkLibraryName(libraryName);

        this.libraryName     = libraryName;
        this.libraryLocation = libraryLocation;

        if(parsingStarted) {
            throw new CodeHandlerException("Parse process already started.");
        } else {
            parsingStarted = true;
        }

        packagesStack.clear();
        containersStack.clear();
    }

    public void endParsing() {
        if( !parsingStarted) {
            throw new CodeHandlerException("Parse process not yet started.");
        }
        if(! containersStack.isEmpty() ) {
            throw new CodeHandlerException("There are some classes opened but not closed: " + containersStack.peek());
        }
        if(! packagesStack.isEmpty() ) {
            throw new CodeHandlerException("There are some pakeges opened but not closed.");
        }
        parsingStarted = false;

        String prefixedlibraryName = CodeModelBase.prefixFullyQualifiedName(JavaCodeModel.ASSET_PREFIX, libraryName);
        String formattedDate = formatLibraryDatetime( new Date() );        
        model.addTriple(JavaCodeModel.ASSET, JavaCodeModel.CONTAINS_LIBRARY,  prefixedlibraryName);
        model.addTripleLiteral(prefixedlibraryName, CodeModelBase.LIBRARY_LOCATION, libraryLocation);
        model.addTripleLiteral(prefixedlibraryName, CodeModelBase.LIBRARY_DATETIME, formattedDate);
    }

    public void startCompilationUnit(String identifier) {
        if( !parsingStarted) {
              throw new CodeHandlerException("Started compilation unit without start parsing first.");
        }
        compilationUnitStarted = true;
    }

    public void endCompilationUnit() {
        if(! compilationUnitStarted) {
             throw new CodeHandlerException("Ended compilation unit without start it first.");
        }
        compilationUnitStarted = false;
    }

    public void startPackage(Identifier pathToPackage) {
        if(pathToPackage == null) {
            throw new IllegalArgumentException();
        }
        if( ! containersStack.isEmpty()) { // Inside a class.
            throw new CodeHandlerException("Cannot start a package when inside a class");
        }

        final String packageStr = pathToPackage.getIdentifier();
        model.addTriple(packageStr, SUBCLASSOF, JavaCodeModel.JPACKAGE);
        Identifier parent = peekPackage();
        model.addTriple(parent.getIdentifier(), JavaCodeModel.CONTAINS_PACKAGE, packageStr);
        pushPackage(pathToPackage);
    }

    public void endPackage() {
        if(packagesStack.isEmpty()) {
            throw new CodeHandlerException("No packages to end.");
        }

        popPackage();
    }

    public void startInterface(Identifier pathToInterface, Identifier[] extendedInterfaces) {
        if(pathToInterface == null) {
            throw new IllegalArgumentException("Invalid pathToInterface");
        }

        final String pathToInterfaceIdentifier = pathToInterface.getIdentifier();

        checkPackageDiscrepancy(pathToInterface);

        model.addTriple(pathToInterfaceIdentifier, CodeModel.SUBCLASSOF, JavaCodeModel.JINTERFACE);
        Identifier parentClass = peekContainer();
        model.addTriple(parentClass.getIdentifier(), JavaCodeModel.CONTAINS_INTERFACE, pathToInterfaceIdentifier);
        if(extendedInterfaces != null) {
            for(int i = 0; i < extendedInterfaces.length; i++) {
                model.addTriple(
                        pathToInterfaceIdentifier,
                        JavaCodeModel.EXTENDS_INT,
                        extendedInterfaces[i].getIdentifier()
                );
            }
        }
        pushInterfaceOrClass(pathToInterface);
    }

    public void endInterface() {
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("No interface to end.");
        }

        popClassOrInterface();
    }

    public void startClass(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToClass,
            Identifier extededClass,           // Can be null.
            Identifier[] implementedInterfaces // Can be null.
    ) {
        if(modifiers == null || visibility == null || pathToClass == null) {
            throw new IllegalArgumentException();
        }

        final String pathToClassIdentifier = pathToClass.getIdentifier();

        checkPackageDiscrepancy(pathToClass);

        model.addTriple(pathToClassIdentifier, CodeModel.SUBCLASSOF, JavaCodeModel.JCLASS);
        model.addTripleLiteral(pathToClassIdentifier, JavaCodeModel.HAS_MODIFIERS, JavaCodeModel.JModifier.toByte(modifiers).toString() );
        model.addTripleLiteral(pathToClassIdentifier, JavaCodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        if(extededClass != null) {
            model.addTriple(
                    pathToClassIdentifier,
                    JavaCodeModel.EXTENDS_CLASS,
                    extededClass.getIdentifier()
            );
        }
        if(implementedInterfaces != null) {
            for(int i = 0; i < implementedInterfaces.length; i++) {
                model.addTriple(
                        pathToClassIdentifier,
                        JavaCodeModel.IMPLEMENTS_INT,
                        implementedInterfaces[i].getIdentifier()
                );
            }
        }
        Identifier parentClass = peekContainer();
        model.addTriple(parentClass.getIdentifier(), JavaCodeModel.CONTAINS_CLASS, pathToClassIdentifier);
        pushInterfaceOrClass(pathToClass);
    }

    public void endClass() {
        if( containersStack.isEmpty() ) {
            throw new CodeHandlerException("No class to end.");
        } else {
            popClassOrInterface();
        }
    }

    public void startEnumeration(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToEnumeration,
            String[] elements
    ) {
        if(modifiers == null || visibility == null || pathToEnumeration == null || elements == null) {
            throw new IllegalArgumentException();
        }

        final String pathToEnumerationIdentifier = pathToEnumeration.getIdentifier();

        checkPackageDiscrepancy(pathToEnumeration);

        model.addTriple(pathToEnumerationIdentifier, CodeModel.SUBCLASSOF, JavaCodeModel.JENUMERATION);
        model.addTripleLiteral(pathToEnumerationIdentifier, JavaCodeModel.HAS_MODIFIERS, JavaCodeModel.JModifier.toByte(modifiers).toString() );
        model.addTripleLiteral(pathToEnumerationIdentifier, JavaCodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        for(int i = 0; i < elements.length; i++) {
            model.addTriple(
                pathToEnumerationIdentifier,
                JavaCodeModel.CONTAINS_ELEMENT,
                IdentifierBuilder.create().pushFragment( elements[i], JavaCodeModel.ELEMENT_KEY).build().getIdentifier()
            );
        }
        Identifier parentClass = peekContainer();
        model.addTriple(parentClass.getIdentifier(), JavaCodeModel.CONTAINS_ENUMERATION, pathToEnumerationIdentifier);
        pushInterfaceOrClass(pathToEnumeration);
    }

    public void endEnumeration() {
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("No enumeration to end.");
        }

        popClassOrInterface();
    }

    public void attribute(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToAttribute,
            JavaCodeModel.JType type,
            String value                        // Can be null.
    ) {
        if(modifiers == null || visibility == null || pathToAttribute == null || type == null) {
            throw new IllegalArgumentException();
        }
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("Must be inside a class to define an attribute.");
        }

        final String identifier = pathToAttribute.getIdentifier();
        model.addTriple(identifier, SUBCLASSOF, JavaCodeModel.JATTRIBUTE);
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_MODIFIERS, JavaCodeModel.JModifier.toByte(modifiers).toString() );
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        //TODO: MED - don't use triple literal but define a validation logic for a generic type.
        model.addTripleLiteral(identifier, JavaCodeModel.ATTRIBUTE_TYPE,  type.getIdentifier().getIdentifier());
        if(value != null) { // Default value defined.
            model.addTripleLiteral(identifier, JavaCodeModel.ATTRIBUTE_VALUE, value);
        }
        Identifier parentClass = peekContainer();
        model.addTriple(parentClass.getIdentifier(), JavaCodeModel.CONTAINS_ATTRIBUTE, identifier);
    }

    public void constructor(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            int overloadIndex,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.ExceptionType[] exceptions
    ) {
       if(modifiers == null || visibility == null || overloadIndex < 0) {
            throw new IllegalArgumentException();
        }
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("Must be inside a class to define a prefPTCO.");
        }
        // Validating arguments.
        int paramNamesSize = parameterNames == null ? 0 : parameterNames.length;
        int paramTypesSize = parameterTypes == null ? 0 : parameterTypes.length;
        if(paramNamesSize != paramTypesSize) {
            throw new IllegalArgumentException(
                "The number of parameter names differs from the number of parameters types: "
                + paramNamesSize + " <> " + paramTypesSize
            );
        }
        int exceptionsSize = exceptions == null ? 0 : exceptions.length;

        // Creating structure.
        Identifier pathToClass = peekContainer();
        String classIdentifier = pathToClass.getIdentifier();
        String identifier = IdentifierBuilder.create(pathToClass).pushFragment( "_" + overloadIndex, JavaCodeModel.CONSTRUCTOR_KEY).build().getIdentifier();
        model.addTriple(identifier, CodeModel.SUBCLASSOF, JavaCodeModel.JCONSTRUCTOR);
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_MODIFIERS, JavaCodeModel.JModifier.toByte(modifiers).toString() );
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        String qualifiedParameter;
        for(int i = 0; i < paramNamesSize; i++) {
            qualifiedParameter = qualifyParameterName( pathToClass, parameterNames[i]);
            model.addTriple( qualifiedParameter, CodeModel.SUBCLASSOF, JavaCodeModel.JPARAMETER);
            // TODO - MED : improve this.
            model.addTripleLiteral( qualifiedParameter, JavaCodeModel.PARAMETER_TYPE, parameterTypes[i].getIdentifier().getIdentifier());
            model.addTriple( identifier, JavaCodeModel.CONTAINS_PARAMETER, qualifiedParameter);
        }
        for(int i = 0; i < exceptionsSize; i++) {
            model.addTriple(identifier, JavaCodeModel.THROWS, exceptions[i].getIdentifier().getIdentifier());
        }
        model.addTriple(classIdentifier, JavaCodeModel.CONTAINS_CONSTRUCTOR, identifier);
    }

    public void method(
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility,
            Identifier pathToMethod,
            int overloadIndex,
            String[] parameterNames,
            JavaCodeModel.JType[] parameterTypes,
            JavaCodeModel.JType returnType,
            JavaCodeModel.ExceptionType[] exceptions
    ) {
        if(modifiers == null || visibility == null || pathToMethod == null || overloadIndex < 0) {
            throw new IllegalArgumentException();
        }
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("Must be inside a class to define a method.");
        }
        // Validating arguments.
        int paramNamesSize = parameterNames == null ? 0 : parameterNames.length;
        int paramTypesSize = parameterTypes == null ? 0 : parameterTypes.length;
        if(paramNamesSize != paramTypesSize) {
            throw new IllegalArgumentException(
                "The number of parameter names differs from the number of parameters types: "
                + paramNamesSize + " <> " + paramTypesSize
            );
        }
        int exceptionsSize = exceptions == null ? 0 : exceptions.length;

        final String identifier = pathToMethod.getIdentifier();

        // Creating structure.
        model.addTriple(identifier, SUBCLASSOF, JavaCodeModel.JMETHOD);
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_MODIFIERS, JavaCodeModel.JModifier.toByte(modifiers).toString() );
        model.addTripleLiteral(identifier, JavaCodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        String signature = IdentifierBuilder.create(pathToMethod).pushFragment( "_" + overloadIndex, JavaCodeModel.SIGNATURE_KEY).build().getIdentifier();
        model.addTriple(signature, SUBCLASSOF, JavaCodeModel.JSIGNATURE);
        String qualifiedParameter; 
        for(int i = 0; i < paramNamesSize; i++) {
            qualifiedParameter = qualifyParameterName(pathToMethod, parameterNames[i]);
            model.addTriple( qualifiedParameter, SUBCLASSOF, JavaCodeModel.JPARAMETER);
            model.addTripleLiteral( qualifiedParameter, JavaCodeModel.PARAMETER_TYPE, parameterTypes[i].getIdentifier().getIdentifier() );
            model.addTriple( signature, JavaCodeModel.CONTAINS_PARAMETER, qualifiedParameter);
        }
        model.addTripleLiteral(signature, JavaCodeModel.RETURN_TYPE, returnType.getIdentifier().getIdentifier());
        model.addTriple(identifier, JavaCodeModel.CONTAINS_SIGNATURE, signature);
        for(int i = 0; i < exceptionsSize; i++) {
            model.addTriple(identifier, JavaCodeModel.THROWS, exceptions[i].getIdentifier().getIdentifier());
        }
        Identifier parentContainer = peekContainer();
        model.addTriple(parentContainer.getIdentifier(), JavaCodeModel.CONTAINS_METHOD, identifier);
    }

    public void parseError(String location, String description) {
        notifyParseError(location, description);
    }

    public void unresolvedTypes(String[] types) {
        notifyUnresolvedTypes(types);
    }

    public void preloadObjectsFromModel(ObjectsTable objectsTable) {
        if(objectsTable == null) {
            throw new CodeHandlerException("objectsTable cannot be null.");
        }

        // Preloading classes.
        TripleIterator t1 = model.searchTriples(JavaCodeModel.ALL_MATCH, JavaCodeModel.SUBCLASSOF, JavaCodeModel.JCLASS);
        try {
        Identifier fullyQualifiedObject;
            while(t1.next()) {
                fullyQualifiedObject = IdentifierReader.readIdentifier( t1.getSubject() );
                objectsTable.addObject( fullyQualifiedObject );
                if(logger.isDebugEnabled()) {
                    logger.debug("Added object: " + fullyQualifiedObject );
                }
            }
        } finally {
            t1.close();
        }

        // Preloading interfaces.
        TripleIterator t2 = model.searchTriples(JavaCodeModel.ALL_MATCH, JavaCodeModel.SUBCLASSOF, JavaCodeModel.JINTERFACE);
        try {
        Identifier fullyQualifiedObject;
            while(t2.next()) {
                fullyQualifiedObject = IdentifierReader.readIdentifier( t2.getSubject() );
                objectsTable.addObject( fullyQualifiedObject );
                if(logger.isDebugEnabled()) {
                    logger.debug("Added object: " + fullyQualifiedObject );
                }
            }
        } finally {
            t2.close();
        }
    }

    public void addErrorListener(ErrorListener errorListener) {
        if(errorListeners == null) {
            errorListeners = new ArrayList<ErrorListener>();
        }
        errorListeners.add(errorListener);
    }

    public void removeErrorListener(ErrorListener errorListener) {
        if(errorListeners != null && errorListeners.remove(errorListener) && errorListeners.isEmpty()) {
            errorListeners = null;
        }
    }

    public void parsedEntry(JavadocEntry entry) {
        if(logger.isDebugEnabled()) {
            logger.debug("parsedEntry: " + entry);
        }
    }

    public void classJavadoc(JavadocEntry entry, String pathToClass) {
        if(logger.isDebugEnabled()) {
            logger.debug("classJavadoc:" + pathToClass + "{" + entry + "}");
        }
    }

    public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
        if(logger.isDebugEnabled()) {
            logger.debug("methodJavadoc:" + pathToMethod + "{" + entry + "}");
        }
    }

    public Identifier generateTempUniqueIdentifier() {
        return model.generateTempUniqueIdentifier();
    }

    public int replaceIdentifierWithQualifiedType(Identifier identifier, Identifier qualifiedType) {
        return model.replaceIdentifierWithQualifiedType(identifier, qualifiedType);
    }

    /**
     * Adds a package on the packages stack.
     * @param p
     */
    protected void pushPackage(Identifier p) {
        packagesStack.push(p);
    }

    /**
     * Removes last package from the packges stack.
     */
    protected void popPackage() {
        packagesStack.pop();
    }

    /**
     * Retrieves last package without pop it.
     *
     * @return the peek package string.
     */
    protected Identifier peekPackage() {
        if(packagesStack.isEmpty()) {
            return DEFAULT_PACKAGE;
        }
        return packagesStack.peek();
    }

    /**
     * Pushes a class or an intergace on the stack.
     * @param clazz
     */
    protected void pushInterfaceOrClass(Identifier clazz) {
        if(logger.isDebugEnabled()) {
            logger.debug("PushClassOrInterface " + clazz.getIdentifier());
        }
        containersStack.push(clazz);
    }

    /**
     * Pops a class or an interface.
     */
    protected void popClassOrInterface()  {
        if(logger.isDebugEnabled()) {
            logger.debug("PopClassOrInterface");
        }
        containersStack.pop();
    }

    /**
     * Peeks the on top class, interface or enumeration.
     *
     * @return the peek type. 
     */
    protected Identifier peekContainer() {
        if( containersStack.isEmpty() ) {
            return peekPackage();
        } else {
            return containersStack.peek();
        }
    }

    /**
     * Qualifies a parameter name.
     *
     * @param pathToMethod
     * @param parameterName
     * @return the qualified parameter name.
     * @throws IllegalArgumentException
     */
    private String qualifyParameterName(Identifier pathToMethod, String parameterName) {
        return pathToMethod
                    .copy()
                    .pushFragment(parameterName, JavaCodeModel.PARAMETER_KEY)
                    .build()
                    .getIdentifier();
    }

    private void checkPackageDiscrepancy(Identifier pathToElement) {
        if( ! RDFCoder.checkPackageDiscrepancy() ) { return; }

        Identifier elementContainer = pathToElement.getPreTail();
        Identifier currentContainer = packagesStack.peek();

        if( ! currentContainer.equals(elementContainer) ) {
             notifyPackageDiscrepancy(elementContainer.getIdentifier(), currentContainer.getIdentifier());
        }
    }

    private void notifyPackageDiscrepancy(String processedPackage, String declaredContainerPackage) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            try {
                el.packageDiscrepancy(this, processedPackage, declaredContainerPackage);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void notifyParseError(String location, String description) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            try {
                el.parseError(this, location, description);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void notifyUnresolvedTypes(String[] types) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            try {
                el.unresolvedTypes(this, types);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
