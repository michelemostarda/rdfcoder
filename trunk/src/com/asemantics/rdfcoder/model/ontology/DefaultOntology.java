/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.rdfcoder.model.ontology;

import com.asemantics.rdfcoder.model.CodeModel;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link com.asemantics.rdfcoder.model.ontology.Ontology} interface.
 */
public class DefaultOntology implements Ontology {

    /**
     * Defines a base property.
     */
    abstract class PropertyBase implements Comparable {

        /**
         * Returns the prefix of a term.
         *
         * @param term
         * @return
         * @throws OntologyException
         */
        String getPrefix(String term) throws OntologyException {
            int index = term.indexOf(CodeModel.PREFIX_SEPARATOR);
            if(index == -1) {
                throw new OntologyException("Invalid term: '" + term + "'");
            }
            return term.substring(0, index + 1);
        }

        /**
         * Validates a triple over this property.
         *
         * @param subject
         * @param predicate
         * @param object
         * @throws OntologyException
         */
        abstract void validate(String subject, String predicate, String object) throws OntologyException;

        /**
         * Returns <code>true</code> if this is a literal property, <code>false</code>
         * otherwise.
         * 
         * @return
         */
        abstract boolean isLiteral();

        /**
         * Prints the current property in a umar readable manner on the given print stream.
         * @param ps
         */
        abstract void print(PrintStream ps);

    }

    /**
     * Represents a literal property.
     */
    class LiteralProperty extends PropertyBase {

        /**
         * The subject expected prefix.
         */
        String subPrefixStr;

        /**
         * predicate string.
         */
        String predicateStr;

        /**
         * Constructor.
         *
         * @param subjectPrefix
         * @param predicate
         */
        LiteralProperty(String subjectPrefix, String predicate) {
            subPrefixStr = subjectPrefix;
            predicateStr = predicate;
        }

        public String getSubjectPrefix() {
            return subPrefixStr;
        }

        public String getPredicate() {
            return predicateStr;
        }

        public void validate(String subject, String predicate, String object) throws OntologyException {
            if( subPrefixStr == null ) {
                return;
            }
            
            String subPrefix = getPrefix(subject);
            if(
                ! subPrefixStr.equals( subPrefix )
            ) {
                throw new OntologyException("Invalid subject prefix: '" + subPrefix + "' for predicate: " + predicateStr + "'");
            }
        }

        boolean isLiteral() {
            return true;
        }

        void print(PrintStream ps) {
            ps.printf("[%s] --[%s]--> 'LITERAL'", subPrefixStr, predicateStr);
            ps.println();
        }

        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(obj == this) {
                return true;
            }
            if(obj instanceof LiteralProperty) {
                LiteralProperty other = (LiteralProperty) obj;
                return
                        subPrefixStr.equals(other.subPrefixStr)
                                &&
                        predicateStr.equals(other.predicateStr);
            }
            return false;
        }

        public int hashCode() {
            return subPrefixStr.hashCode() * predicateStr.hashCode();
        }

        /**
         * Ordered over predicate strings.
         * 
         * @param obj
         * @return
         */
        public int compareTo(Object obj) {
            LiteralProperty literalProperty = (LiteralProperty) obj;
            int cmp = predicateStr.compareTo(literalProperty.predicateStr);
            return cmp != 0 ? cmp : subPrefixStr.compareTo(literalProperty.subPrefixStr);
        }
    }

    /**
     * Represents a property.
     */
    class Property extends LiteralProperty {

        /**
         * Object prefix string.
         */
        String objPrefixStr;

        /**
         * Constructor.
         *
         * @param subjectPrefix
         * @param predicate
         * @param objectPrefix
         */
        Property(String subjectPrefix, String predicate, String objectPrefix) {
            super(subjectPrefix, predicate);
            objPrefixStr = objectPrefix;
        }

        public String getObjectPrefix() {
            return objPrefixStr;
        }

        public void validate(String subject, String predicate, String object) throws OntologyException {
            super.validate(subject, predicate, object);

            if(objPrefixStr == null) {
                return;
            }

            String objPrefix = getPrefix(object); 
            if(
                ! objPrefix.equals( objPrefixStr )
            ) {
                throw new OntologyException("Invalid object prefix: '" + objPrefixStr + "' for predicate: " + predicate + "'");
            }
        }

        boolean isLiteral() {
            return false;
        }

        void print(PrintStream ps) {
            ps.printf("[%s] --[%s]--> [%s]", subPrefixStr, predicateStr, objPrefixStr);
            ps.println();
        }

        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(obj == this) {
                return true;
            }

            if( ! super.equals(obj)) {
                return false;
            }

            if(obj instanceof Property) {
                Property other = (Property) obj;
                return objPrefixStr.equals(other.objPrefixStr);
            }
            return false;
        }

        public int hashCode() {
            return super.hashCode() * objPrefixStr.hashCode();
        }
    }

    /**
     * Map connecting predicate names with properties.
     */
    private final Map<String, List<PropertyBase>> predicateIndex;

    public DefaultOntology() {
        predicateIndex = new HashMap<String, List<PropertyBase>>();
    }

    protected void validatePrefix(String prefix) throws OntologyException {
        if( prefix == null ) {
            throw new OntologyException("invalid prefix: '" + prefix + "'");
        }
    }

    public void defineRelation(String subjectPrefix, URL predicate, String objectPrefix) throws OntologyException {
        validatePrefix(subjectPrefix);
        validatePrefix(objectPrefix);

        String predicateStr = predicate.toString();

        List<PropertyBase> properties = predicateIndex.get(predicateStr);
        if(properties == null) {
            properties = new ArrayList<PropertyBase>();
        }

        Property property = new Property(subjectPrefix, predicateStr, objectPrefix);

        if(properties.contains(property)) {
            throw new OntologyException("Property redefinition");
        }

        properties.add(property);
        predicateIndex.put(predicateStr, properties);

        session++;
    }

    public void defineRelation(String subjectPrefix, URL predicate) throws OntologyException {
        validatePrefix(subjectPrefix);

        String predicateStr = predicate.toString();

        List<PropertyBase> properties = predicateIndex.get(predicateStr);
        if(properties == null) {
            properties = new ArrayList<PropertyBase>();
        }

        LiteralProperty literalProperty = new LiteralProperty(subjectPrefix, predicateStr);

        if(properties.contains(literalProperty)) {
            throw new OntologyException("Property redefinition");
        }

        properties.add(literalProperty);
        predicateIndex.put(predicateStr, properties);

        session++;
    }

    public void defineRelation(URL predicate) throws OntologyException {
        String predicateStr = predicate.toString();

        List<PropertyBase> properties = predicateIndex.get(predicateStr);
        if(properties == null) {
            properties = new ArrayList<PropertyBase>();
        }

        Property property = new Property(null, predicateStr, null);

        if(properties.contains(property)) {
            throw new OntologyException("Property redefinition");
        }

        properties.add(property);
        predicateIndex.put(predicateStr, properties);

        session++;
    }

    public void undefineRelation(String subjectPrefix, URL predicate) throws OntologyException {
        validatePrefix(subjectPrefix);

        String predicateStr = predicate.toString();

        List<PropertyBase> properties = predicateIndex.get(predicateStr);

        if(properties == null) {
           throw new OntologyException("Cannot undefine relation '" + predicateStr + "' bacause is not defined.");
        }

        for(PropertyBase base : properties) {
            if( ((LiteralProperty) base).subPrefixStr.equals( subjectPrefix ) ) {
                properties.remove(base);
            }
        }

        session++;
    }

    public void undefineRelation(String subjectPrefix, URL predicate, String objectPrefix) throws OntologyException {
        validatePrefix(subjectPrefix);

        String predicateStr = predicate.toString();

        List<PropertyBase> properties = predicateIndex.get(predicateStr);

        if(properties == null) {
           throw new OntologyException("Cannot undefine relation '" + predicateStr + "' bacause is not defined.");
        }

        for(PropertyBase base : properties) {
            Property property = (Property) base;
            if(
                    property.subPrefixStr.equals( subjectPrefix )
                        &&
                    property.objPrefixStr.equals( objectPrefix  )
            ) {
                properties.remove(property);
            }
        }

        session++;
    }

    /**
     * Validates a triple over the predicates list.
     *
     * @param subject
     * @param predicate
     * @param object
     * @param literal
     * @return the property base applicable on validation.
     * @throws OntologyException
     */
    protected PropertyBase validateTerms(String subject, String predicate, String object, boolean literal) throws OntologyException {
        List<PropertyBase> properties = predicateIndex.get(predicate);

        if(properties == null) {
            throw new OntologyException("predicate '" + predicate + "' is not defined.");
        }

        for(PropertyBase property : properties) {
            try {
                if( literal == property.isLiteral() ) {
                    property.validate(subject, predicate, object);                    
                    return property;
                }
            } catch (OntologyException oe) {}
        }
        throw new OntologyException("No property matches spceified terms for predicate: '" + predicate + "'");
    }

    public void validateTriple(String subject, String predicate, String object) throws OntologyException {
        validateTerms(subject, predicate,  object, false);
    }

    public void validateTripleLiteral(String subject, String predicate) throws OntologyException {
        validateTerms(subject, predicate,  null, true);
    }

    /**
     * Current session.
     */
    private long session = 0L;

    /**
     * Session in which properties has been defined last time.
     */
    private long propertiesSession;

    /**
     * List of ordered properties.
     */
    private PropertyBase[] properties;

    /**
     * Populates the properties array.
     */
    private void orderRelations() {
        List<PropertyBase> relationsList = new ArrayList<PropertyBase>();
        for(List<PropertyBase> propertyList :  predicateIndex.values() ) {
            relationsList.addAll( propertyList );
        }
        Collections.sort( relationsList );
        properties = relationsList.toArray( new PropertyBase[ relationsList.size() ] );
        propertiesSession = session;
    }

    public int getRelationsCount() {
        if(properties == null || propertiesSession != session) {
            orderRelations();
        }
        return properties.length;
    }

    public boolean isLiteralRelation(int i) {
        return properties[i].isLiteral();
    }

    public String getRelationSubjectPrefix(int i) {
        return ((LiteralProperty) properties[i]).getSubjectPrefix();
    }

    public URL getRelationPredicate(int i) {
        try {
            return new URL( ((LiteralProperty) properties[i]).getPredicate() );
        } catch (MalformedURLException e) {
            throw new IllegalStateException();
        }
    }

    public String getRelationObjectPrefix(int t) {
        return ((Property) properties[t]).getObjectPrefix();
    }

    public void printOntology(PrintStream ps) {
        orderRelations();
        for(PropertyBase rb : properties) {
            rb.print(ps);
        }
    }

    public void toOWL(OutputStream os) {
        //TODO: LOW - implement this.
        throw new UnsupportedOperationException();
    }
}
