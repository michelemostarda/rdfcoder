/**
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


package com.asemantics.model;

import com.asemantics.RDFCoder;
import com.asemantics.sourceparse.JavadocEntry;
import com.asemantics.sourceparse.ObjectsTable;
import com.hp.hpl.jena.vocabulary.RDFS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * The <code>CodeHandler</code> standard implementation.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 *
 * TODO: LOW - define an interface and implementation SourceCodeHandler / SourceCodehandlerImpl able to accept unresolved types.
 */
public class CodeHandlerImpl implements CodeHandler {

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
    private final String DEFAULT_PACKAGE = CodeModel.PACKAGE_PREFIX;

    /**
     * The code org.asemantics.model on which store code events.
     */
    private CodeModel model;

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
    private Stack<String> packagesStack;

    /**
     * The stack of current container, intended as class, interface or enumeration.
     */
    private Stack containersStack;

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
     * Contructor.
     * 
     * @param cm the code org.asemantics.model to use to store handler data.
     */
    protected CodeHandlerImpl(CodeModel cm) {
        if(cm == null) {
            throw new NullPointerException();
        }
        model         = cm;
        packagesStack = new Stack();
        containersStack = new Stack();
    }

    /**
     * Returns the underlying org.asemantics.model.
     * @return
     */
    protected CodeModel getModel() {
        return model;
    }

    protected void checkLibraryName(String name) {
        TripleIterator ti = model.searchTriples(CodeModel.JASSET, CodeModel.CONTAINS_LIBRARY, CodeModel.ALL_MATCH);
        String targetLibraryName = CodeModel.prefixFullyQualifiedName(CodeModel.JASSET_PREFIX, name);
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

        String prefixedlibraryName = CodeModel.prefixFullyQualifiedName(CodeModel.JASSET_PREFIX, libraryName);
        String formattedDate = formatLibraryDatetime( new Date() );        
        model.addTriple(CodeModel.JASSET, CodeModel.CONTAINS_LIBRARY,  prefixedlibraryName);
        model.addTripleLiteral(prefixedlibraryName, CodeModel.LIBRARY_LOCATION, libraryLocation);
        model.addTripleLiteral(prefixedlibraryName, CodeModel.LIBRARY_DATETIME, formattedDate);
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

    public void startPackage(String pathToPackage) {
        if(pathToPackage == null) {
            throw new IllegalArgumentException();
        }
        if( ! containersStack.isEmpty()) { // Inside a class.
            throw new CodeHandlerException("Cannot start a package when inside a class");
        }

        String prefPTP = CodeModel.prefixFullyQualifiedName(CodeModel.PACKAGE_PREFIX, pathToPackage);
        model.addTriple(prefPTP, SUBCLASSOF, CodeModel.JPACKAGE);
        String parent = peekPackage();
        model.addTriple(parent, CodeModel.CONTAINS_PACKAGE, prefPTP);
        pushPackage(prefPTP);
    }

    public void endPackage() {
        if(packagesStack.isEmpty()) {
            throw new CodeHandlerException("No packages to end.");
        }

        popPackage();
    }

    public void startInterface(String pathToInterface, String[] extendedInterfaces) {
        if(pathToInterface == null) {
            throw new IllegalArgumentException("Invalid pathToInterface");
        }

        checkPackageDiscrepancy(pathToInterface);

        String prefPTI = CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, pathToInterface);
        model.addTriple(prefPTI, CodeModel.SUBCLASSOF, CodeModel.JINTERFACE);
        String parentClass = peekContainer();
        model.addTriple(parentClass, CodeModel.CONTAINS_INTERFACE, prefPTI);
        if(extendedInterfaces != null) {
            for(int i = 0; i < extendedInterfaces.length; i++) {
                model.addTriple(
                        prefPTI,
                        CodeModel.EXTENDS_INT,
                        CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, extendedInterfaces[i])
                );
            }
        }
        pushInterfaceOrClass(prefPTI);
    }

    public void endInterface() {
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("No interface to end.");
        }

        popClassOrInterface();
    }

    public void startClass(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToClass,
            String extededClass,                // Can be null.
            String[] implementedInterfaces      // Can be null.
    ) {
        if(modifiers == null || visibility == null || pathToClass == null) {
            throw new IllegalArgumentException();
        }

        checkPackageDiscrepancy(pathToClass);

        String prefPTC = CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, pathToClass);
        model.addTriple(prefPTC, SUBCLASSOF, CodeModel.JCLASS);
        model.addTriple(prefPTC, CodeModel.HAS_MODIFIERS, CodeModel.JModifier.toByte(modifiers).toString() );
        model.addTriple(prefPTC, CodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        if(extededClass != null) {
            model.addTriple(
                    prefPTC,
                    CodeModel.EXTENDS_CLASS,
                    CodeModel.prefixFullyQualifiedName(CodeModel.CLASS_PREFIX, extededClass)
            );
        }
        if(implementedInterfaces != null) {
            for(int i = 0; i < implementedInterfaces.length; i++) {
                model.addTriple(
                        prefPTC,
                        CodeModel.IMPLEMENTS_INT,
                        CodeModel.prefixFullyQualifiedName(CodeModel.INTERFACE_PREFIX, implementedInterfaces[i])
                );
            }
        }
        String parentClass = peekContainer();
        model.addTriple(parentClass, CodeModel.CONTAINS_CLASS, prefPTC);
        pushInterfaceOrClass(prefPTC);
    }

    public void endClass() {
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("No class to end.");
        }

        popClassOrInterface();
    }

    public void startEnumeration(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToEnumeration,
            String[] elements
    ) {
        if(modifiers == null || visibility == null || pathToEnumeration == null || elements == null || elements.length == 0) {
            throw new IllegalArgumentException();
        }

        checkPackageDiscrepancy(pathToEnumeration);

        String prefPTE = CodeModel.prefixFullyQualifiedName(CodeModel.ENUMERATION_PREFIX, pathToEnumeration);
        model.addTriple(prefPTE, SUBCLASSOF, CodeModel.JENUMERATION);
        model.addTriple(prefPTE, CodeModel.HAS_MODIFIERS, CodeModel.JModifier.toByte(modifiers).toString() );
        model.addTriple(prefPTE, CodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        for(int i = 0; i < elements.length; i++) {
            model.addTriple(
                prefPTE,
                CodeModel.CONTAINS_ELEMENT,
                CodeModel.prefixFullyQualifiedName(CodeModel.ELEMENT_PREFIX, elements[i])
            );
        }
        String parentClass = peekContainer();
        model.addTriple(parentClass, CodeModel.CONTAINS_ENUMERATION, prefPTE);
        pushInterfaceOrClass(prefPTE);
    }

    public void endEnumeration() {
        //TODO: check if the peek element is an enumeration.
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("No enumeration to end.");
        }

        popClassOrInterface();
    }

    public void attribute(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToAttribute,
            CodeModel.JType type,
            String value                        // Can be null.
    ) {
        if(modifiers == null || visibility == null || pathToAttribute == null || type == null) {
            throw new IllegalArgumentException();
        }
        if(containersStack.isEmpty()) {
            throw new CodeHandlerException("Must be inside a class to define an attribute.");
        }

        String prefPTA = CodeModel.prefixFullyQualifiedName(CodeModel.ATTRIBUTE_PREFIX, pathToAttribute);
        model.addTriple(prefPTA, SUBCLASSOF, CodeModel.JATTRIBUTE);
        model.addTriple(prefPTA, CodeModel.HAS_MODIFIERS, CodeModel.JModifier.toByte(modifiers).toString() );
        model.addTriple(prefPTA, CodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        model.addTriple(prefPTA, CodeModel.ATTRIBUTE_TYPE,  type.getIdentifier());
        if(value != null) { // Default value defined.
            model.addTriple(prefPTA, CodeModel.ATTRIBUTE_VALUE, value);
        }
        String parentClass = peekContainer();
        model.addTriple(parentClass, CodeModel.CONTAINS_ATTRIBUTE, prefPTA);
    }

    public void constructor(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            int overloadIndex,
            String[] parameterNames,
            CodeModel.JType[] parameterTypes,
            CodeModel.ExceptionType[] exceptions
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
        String pathToClass = peekContainer();
        String prefPTCO =  CodeModel.prefixFullyQualifiedName(CodeModel.CONSTRUCTOR_PREFIX, pathToClass) + "_" + overloadIndex;
        model.addTriple(prefPTCO, SUBCLASSOF, CodeModel.JCONSTRUCTOR);
        model.addTriple(prefPTCO, CodeModel.HAS_MODIFIERS, CodeModel.JModifier.toByte(modifiers).toString() );
        model.addTriple(prefPTCO, CodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        String qualifiedParameter;
        for(int i = 0; i < paramNamesSize; i++) {
            qualifiedParameter = qualifyParameterName( pathToClass, parameterNames[i]);
            model.addTriple( qualifiedParameter, SUBCLASSOF, CodeModel.JPARAMETER);
            model.addTriple( qualifiedParameter, CodeModel.PARAMETER_TYPE, parameterTypes[i].getIdentifier());
            model.addTriple( prefPTCO, CodeModel.CONTAINS_PARAMETER, qualifiedParameter);
        }
        for(int i = 0; i < exceptionsSize; i++) {
            model.addTriple(prefPTCO, CodeModel.THROWS, exceptions[i].getIdentifier());
        }
        model.addTriple(pathToClass, CodeModel.CONTAINS_CONSTRUCTOR, prefPTCO);
    }

    public void method(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToMethod,
            int overloadIndex,
            String[] parameterNames,
            CodeModel.JType[] parameterTypes,
            CodeModel.JType returnType,
            CodeModel.ExceptionType[] exceptions
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

        String prefPTM = CodeModel.prefixFullyQualifiedName(CodeModel.METHOD_PREFIX, pathToMethod);
        // Creating structure.
        model.addTriple(prefPTM, SUBCLASSOF, CodeModel.JMETHOD);
        model.addTriple(prefPTM, CodeModel.HAS_MODIFIERS, CodeModel.JModifier.toByte(modifiers).toString() );
        model.addTriple(prefPTM, CodeModel.HAS_VISIBILITY, visibility.getIdentifier());
        String signature = CodeModel.prefixFullyQualifiedName(CodeModel.SIGNATURE_PREFIX, pathToMethod) + "_" + overloadIndex;
        model.addTriple(signature, SUBCLASSOF, CodeModel.JSIGNATURE);
        String qualifiedParameter; 
        for(int i = 0; i < paramNamesSize; i++) {
            qualifiedParameter = qualifyParameterName( prefPTM, parameterNames[i]);
            model.addTriple( qualifiedParameter, SUBCLASSOF, CodeModel.JPARAMETER);
            model.addTriple( qualifiedParameter, CodeModel.PARAMETER_TYPE, parameterTypes[i].getIdentifier());
            model.addTriple( signature, CodeModel.CONTAINS_PARAMETER, qualifiedParameter);
        }
        model.addTriple(signature, CodeModel.RETURN_TYPE, returnType.getIdentifier());
        model.addTriple(prefPTM, CodeModel.CONTAINS_SIGNATURE, signature);
        for(int i = 0; i < exceptionsSize; i++) {
            model.addTriple(prefPTM, CodeModel.THROWS, exceptions[i].getIdentifier());
        }
        String parentContainer = peekContainer();
        model.addTriple(parentContainer, CodeModel.CONTAINS_METHOD, prefPTM);
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
        TripleIterator t1 = model.searchTriples(CodeModel.ALL_MATCH, CodeModel.SUBCLASSOF, CodeModel.JCLASS);
        try {
        String fullyQualifiedObject;
            while(t1.next()) {
                fullyQualifiedObject = t1.getSubject();
                fullyQualifiedObject = fullyQualifiedObject.substring(CodeModel.CLASS_PREFIX.length());
                System.out.println("Add object: " + fullyQualifiedObject );
                objectsTable.addObject( fullyQualifiedObject );
            }
        } finally {
            t1.close();
        }

        // Preloading interfaces.
        TripleIterator t2 = model.searchTriples(CodeModel.ALL_MATCH, CodeModel.SUBCLASSOF, CodeModel.JINTERFACE);
        try {
        String fullyQualifiedObject;
            while(t2.next()) {
                fullyQualifiedObject = t2.getSubject();
                fullyQualifiedObject = fullyQualifiedObject.substring(CodeModel.INTERFACE_PREFIX.length());
                System.out.println("Add object: " + fullyQualifiedObject );
                objectsTable.addObject( fullyQualifiedObject );
            }
        } finally {
            t2.close();
        }
    }

    public void serializeUnresolvedTypeEntries(ObjectsTable objectTable) {
        for(ObjectsTable.UnresolvedTypeEntry entry : objectTable.getUnresolvedTypeEntries() ) {
            //TODO: LOW - TBI
        }
    }

    public void deserializeUnresolvedTypeEntries(ObjectsTable objectTable) {
        //TODO: LOW - TBI
    }

    public void addErrorListener(ErrorListener errorListener) {
        if(errorListeners == null) {
            errorListeners = new ArrayList();
        }
        errorListeners.add(errorListener);
    }

    public void removeErrorListener(ErrorListener errorListener) {
        if(errorListeners != null && errorListeners.remove(errorListener) && errorListeners.isEmpty()) {
            errorListeners = null;
        }
    }

    public void parsedEntry(JavadocEntry entry) {
        System.out.println("parsedEntry: " + entry);
    }

    public void classJavadoc(JavadocEntry entry, String pathToClass) {
        System.out.println("classJavadoc:" + pathToClass + "{" + entry + "}");
    }

    public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
        System.out.println("methodJavadoc:" + pathToMethod + "{" + entry + "}");
    }

    public String generateTempUniqueIdentifier() {
        return model.generateTempUniqueIdentifier();
    }

    public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType) {
        return model.replaceIdentifierWithQualifiedType(identifier, qualifiedType);
    }

    /**
     * Adds a package on the packages stack.
     * @param p
     */
    protected void pushPackage(String p) {
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
     * @return
     */
    protected String peekPackage() {
        if(packagesStack.isEmpty()) {
            return DEFAULT_PACKAGE;
        }
        return (String) packagesStack.peek();
    }

    /**
     * Pushes a class or an intergace on the stack.
     * @param cls
     */
    protected void pushInterfaceOrClass(String cls) {
        containersStack.push(cls);
    }

    /**
     * Pops a class or an interface.
     */
    protected void popClassOrInterface()  {
        containersStack.pop();
    }

    /**
     * Peeks the on top class, interface or enumeration.
     * @return
     */
    protected String peekContainer() {
        if( containersStack.isEmpty() ) {
            return peekPackage();
        } else {
            return (String) containersStack.peek();
        }
    }

    /**
     * Qualifies a parameter name.
     * @param pathToMethod
     * @param parameterName
     * @return
     */
    private String qualifyParameterName(String pathToMethod, String parameterName) {
        if(parameterName.indexOf(CodeHandler.PACKAGE_SEPARATOR) != -1) {
            throw new IllegalArgumentException();
        }
        String postFix = CodeHandler.PACKAGE_SEPARATOR + parameterName;
        return CodeModel.prefixParameter( CodeModel.PARAMETER_PREFIX, pathToMethod + postFix);
    }

    private String[]     checkPackageDiscrepancy_stackArray;
    private StringBuffer checkPackageDiscrepancy_sb = new StringBuffer();

    private void checkPackageDiscrepancy(String pathToContainer) {
        if( ! RDFCoder.isCHECK_PACKAGE_DISCREPANCY() ) { return; }

        if(checkPackageDiscrepancy_stackArray == null || checkPackageDiscrepancy_stackArray.length != packagesStack.size()) {
            checkPackageDiscrepancy_stackArray = new String[packagesStack.size()];
        }
        String[] packages = packagesStack.toArray(checkPackageDiscrepancy_stackArray);
        checkPackageDiscrepancy_sb.delete(0, checkPackageDiscrepancy_sb.length());
        for(int p = 0; p < packages.length; p++) {
            checkPackageDiscrepancy_sb.append(packages[p]);
            if(p < packages.length - 1) {
                checkPackageDiscrepancy_sb.append(CodeHandler.PACKAGE_SEPARATOR);
            }
        }
        String processedPackage = checkPackageDiscrepancy_sb.toString();
        String containerPackage = pathToContainer.substring(0, pathToContainer.lastIndexOf(CodeHandler.PACKAGE_SEPARATOR) );
        if( ! processedPackage.equals(containerPackage) ) {
            notifyPackageDiscrepancy(processedPackage, containerPackage);
        }
    }

    private void notifyPackageDiscrepancy(String processedPackage, String declaredContainerPackage) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            el.packageDiscrepancy(this, processedPackage, declaredContainerPackage);
        }
    }

    private void notifyParseError(String location, String description) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            el.parseError(this, location, description);
        }
    }

    private void notifyUnresolvedTypes(String[] types) {
        if(errorListeners == null) {
            return;
        }
        for(ErrorListener el : errorListeners) {
            el.unresolvedTypes(this, types);
        }
    }

}
