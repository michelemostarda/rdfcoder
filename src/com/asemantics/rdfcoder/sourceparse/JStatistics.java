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


package com.asemantics.rdfcoder.sourceparse;

import com.asemantics.rdfcoder.model.CodeHandler;
import com.asemantics.rdfcoder.model.ErrorListener;
import com.asemantics.rdfcoder.model.JavaCodeModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JStatistics {

    /* Statistic fields. */

    private int parseErrors;
    private int parsedFiles;
    private int parsedClasses;
    private int parsedInterfaces;
    private int parsedAttributes;
    private int parsedConstructors;
    private int parsedMethods;
    private int parsedEnumarations;
    private int generatedTempIds;
    private int replacedEntries;
    private int javadocEntries;
    private int classesJavadoc;
    private int methodsJavadoc;
    private String[] unresolved;

    private StringBuilder errorMessages;

    private double startParsingTime;
    private double endParsingTime;

    /**
     * List of handlers collecting data for this statistics.
     */
    private List<StatisticsCodeHandler> statisticsCodeHandlers;

    public JStatistics() {
        errorMessages = new StringBuilder();
        statisticsCodeHandlers = new ArrayList();
    }

    protected JStatistics(int e, int pf, int pc, int pi, int pa, int pco, int pm, int pe, int ui, int re, int je, int cj, int mj, String[] u) {
        this();
        
        parseErrors = e;
        parsedFiles = pf;
        parsedClasses = pc;
        parsedInterfaces = pi;
        parsedAttributes = pa;
        parsedConstructors = pco;
        parsedMethods = pm;
        parsedEnumarations = pe;
        javadocEntries = je;
        classesJavadoc = cj;
        methodsJavadoc = mj;
        generatedTempIds = ui;
        replacedEntries = re;
        unresolved = u;
    }

    public int getParseErrors() {
        return parseErrors;
    }

    public int parsedFiles() {
        return parsedFiles;
    }

    public int getParsedClasses() {
        return parsedClasses;
    }

    public int getParsedInterfaces() {
        return parsedInterfaces;
    }

    public int getParsedAttributes() {
        return parsedAttributes;
    }

    public int getParsedMethods() {
        return parsedMethods;
    }

    public int getParsedEnumerations() {
        return parsedEnumarations;
    }

    public int getJavadocEntries() {
        return javadocEntries;
    }

    public int getClassesJavadoc() {
        return classesJavadoc;
    }

    public int getMethodsJavadoc() {
        return methodsJavadoc;
    }

    public int getGeneratedTempIds() {
        return generatedTempIds;
    }

    public int getReplacedEntries() {
        return replacedEntries;
    }

    public String[] getUnresolved() {
        return unresolved;
    }

    protected void incrementParseErrors() {
        parseErrors++;
    }

    protected void incrementParsedFiles() {
        parsedFiles++;
    }

    protected void incrementParsedClasses() {
        parsedClasses++;
    }

    protected void imcrementParsedInterfaces() {
        parsedInterfaces++;
    }

    protected void incrementParsedAttributes() {
        parsedAttributes++;
    }

    protected void incrementParsedConstructors() {
        parsedConstructors++;
    }

    protected void imcrementParsedMethods() {
        parsedMethods++;
    }

    protected void incrementParsedEnumarations() {
        parsedEnumarations++;
    }

    public int incrementJavadocEntries() {
       return javadocEntries++;
    }

    public int incrementClassesJavadoc() {
        return classesJavadoc++;
    }

    public int incrementMethodsJavadoc() {
        return methodsJavadoc++;
    }

    protected void incrementGeneratedTempIds() {
        generatedTempIds++;
    }

    protected void incrementReplacedEntries() {
        replacedEntries++;
    }

    public void setUnresolved(String[] unresolved) {
        this.unresolved = unresolved;
    }

    public CodeHandler createStatisticsCodeHandler(CodeHandler ch) {
        StatisticsCodeHandler sch = new StatisticsCodeHandler(ch);
        statisticsCodeHandlers.add(sch);
        return sch;
    }

    public void reset() {
        Iterator<StatisticsCodeHandler> handlers = statisticsCodeHandlers.iterator();
        while(handlers.hasNext()) {
            handlers.next().dispose();
            handlers.remove();
        }

        parseErrors = 0;
        parsedFiles = 0;
        parsedClasses = 0;
        parsedInterfaces = 0;
        parsedAttributes = 0;
        parsedConstructors = 0;
        parsedMethods = 0;
        parsedEnumarations = 0;
        generatedTempIds = 0;
        replacedEntries = 0;
        unresolved = null;

        errorMessages.delete(0, errorMessages.length());
    }

    public void clean() {
        reset();
    }

    public String toStringReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("parsing time (secs):").append( (endParsingTime - startParsingTime) / 1000 ).append("\n"); 
        sb.append("parsed files: ").append(parsedFiles).append("\n");
        sb.append("parsed classes: ").append(parsedClasses).append("\n");
        sb.append("parsed interfaces: ").append(parsedInterfaces).append("\n");
        sb.append("parsed attributes: ").append(parsedAttributes).append("\n");
        sb.append("parsed constructors: ").append(parsedConstructors).append("\n");
        sb.append("parsed methods: ").append(parsedMethods).append("\n");
        sb.append("parsed enumerations: ").append(parsedEnumarations).append("\n");
        sb.append("javadoc entries: ").append(javadocEntries).append("\n");
        sb.append("classes javadoc: ").append(classesJavadoc).append("\n");
        sb.append("methods javadoc: ").append(methodsJavadoc).append("\n");
        sb.append("generated temporary indentifiers: ").append(generatedTempIds).append("\n");
        sb.append("replaced entries: ").append(replacedEntries).append("\n");

        sb.append("unresolved [").append( unresolved != null ? unresolved.length : 0).append("] {\n");
        if( unresolved != null ) {
            for( String u : unresolved) {
                sb.append(u).append("\n");
            }
        }
        sb.append("}\n");


        sb.append("parse errors[").append(parseErrors).append("] {\n");
        sb.append( errorMessages );
        sb.append("}\n");

        return sb.toString();
    }

    public String toString() {
        return toStringReport();
    }

    protected class StatisticsCodeHandler implements CodeHandler {

        private CodeHandler wrapped;

        StatisticsCodeHandler(CodeHandler ch) {
            this.wrapped = ch;
        }

        void dispose() {
            wrapped = null;
        }

        public void startParsing(String libraryName, String location) {
            if(wrapped == null) { throw new IllegalStateException(); }
            startParsingTime = System.currentTimeMillis();
            wrapped.startParsing(libraryName, location);
        }

        public void endParsing() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endParsing();
            endParsingTime = System.currentTimeMillis();
        }

        public void startCompilationUnit(String identifier) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.startCompilationUnit(identifier);
            parsedFiles++;
        }

        public void endCompilationUnit() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endCompilationUnit();
        }

        public void startPackage(String pathToPackage) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.startPackage(pathToPackage);
        }

        public void endPackage() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endPackage();
        }

        public void startInterface(String pathToInterface, String[] extendedInterfaces) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.startInterface(pathToInterface, extendedInterfaces);
        }

        public void endInterface() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endInterface();
            parsedInterfaces++;
        }

        public void startClass(JavaCodeModel.JModifier[] modifiers, JavaCodeModel.JVisibility visibility, String pathToClass, String extendedClass, String[] implementedInterfaces) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.startClass(modifiers, visibility, pathToClass, extendedClass, implementedInterfaces);
        }

        public void endClass() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endClass();
            parsedClasses++;
        }

        public void startEnumeration(JavaCodeModel.JModifier[] modifiers, JavaCodeModel.JVisibility visibility, String pathToEnumeration, String[] elements) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.startEnumeration(modifiers, visibility, pathToEnumeration, elements);
            parsedEnumarations++;
        }

        public void endEnumeration() {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.endEnumeration();
        }

        public void attribute(JavaCodeModel.JModifier[] modifiers, JavaCodeModel.JVisibility visibility, String pathToAttribute, JavaCodeModel.JType type, String value) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.attribute(modifiers, visibility, pathToAttribute, type, value);
            parsedAttributes++;
        }

        public void constructor(JavaCodeModel.JModifier[] modifiers, JavaCodeModel.JVisibility visibility, int overloadIndex, String[] parameterNames, JavaCodeModel.JType[] parameterTypes, JavaCodeModel.ExceptionType[] exceptions) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.constructor(modifiers, visibility, overloadIndex, parameterNames, parameterTypes, exceptions);
            parsedConstructors++;
        }

        public void method(JavaCodeModel.JModifier[] modifiers, JavaCodeModel.JVisibility visibility, String pathToMethod, int overloadIndex, String[] parameterNames, JavaCodeModel.JType[] parameterTypes, JavaCodeModel.JType returnType, JavaCodeModel.ExceptionType[] exceptions) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.method(modifiers, visibility, pathToMethod, overloadIndex, parameterNames, parameterTypes, returnType, exceptions);
            parsedMethods++;
        }

        public void parseError(String location, String description) {
            errorMessages.append("location='" + location + "' description='" + description + "'\n" );
            parseErrors++;
        }

        public void unresolvedTypes(String[] types) {
            if(wrapped == null) { throw new IllegalStateException(); }
            unresolved = types;
            wrapped.unresolvedTypes(types);

        }

        public void preloadObjectsFromModel(ObjectsTable objectsTable) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.preloadObjectsFromModel(objectsTable);
        }

        public void addErrorListener(ErrorListener errorListener) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.addErrorListener(errorListener);
        }

        public void removeErrorListener(ErrorListener errorListener) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.removeErrorListener(errorListener);
        }

        public void parsedEntry(JavadocEntry entry) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.parsedEntry(entry);
            javadocEntries++;
        }

        public void classJavadoc(JavadocEntry entry, String pathToClass) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.classJavadoc(entry, pathToClass);
            classesJavadoc++;
        }

        public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature) {
            if(wrapped == null) { throw new IllegalStateException(); }
            wrapped.methodJavadoc(entry, pathToMethod, signature);
            methodsJavadoc++;
        }

        public String generateTempUniqueIdentifier() {
            if(wrapped == null) { throw new IllegalStateException(); }
            generatedTempIds++;
            return wrapped.generateTempUniqueIdentifier();
        }

        public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType) {
            if(wrapped == null) { throw new IllegalStateException(); }
            int replaced = wrapped.replaceIdentifierWithQualifiedType(identifier, qualifiedType);
            replacedEntries += replaced;
            return replaced;
        }
    }
}