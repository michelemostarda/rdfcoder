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
import com.asemantics.rdfcoder.model.IdentifierReader;

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
     * Map connecting predicate names with properties.
     */
    private final Map<String, List<PropertyObjectBase>> predicateIndex;

    public DefaultOntology() {
        predicateIndex = new HashMap<String, List<PropertyObjectBase>>();
    }

    public void defineRelation(String subjectPrefix, URL predicate, String objectPrefix) throws OntologyException {
        validatePrefix(subjectPrefix);
        validatePrefix(objectPrefix);

        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getProperties(predicateStr);
        final URIObjectProperty property = new URIObjectProperty(subjectPrefix, predicateStr, objectPrefix);
        addPropertyInList(properties, property);
    }

    public void defineRelation(String subjectPrefix, URL predicate, ListBounds listBounds) throws OntologyException {
        validatePrefix(subjectPrefix);

        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getProperties(predicateStr);
        final ListObjectProperty property = new ListObjectProperty(subjectPrefix, predicateStr, listBounds);
        addPropertyInList(properties, property);
    }

    public void defineRelation(String subjectPrefix, URL predicate) throws OntologyException {
        validatePrefix(subjectPrefix);

        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getProperties(predicateStr);
        final LiteralObjectProperty property = new LiteralObjectProperty(subjectPrefix, predicateStr);
        addPropertyInList(properties, property);
    }

    public void defineRelation(URL predicate) throws OntologyException {
        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getProperties(predicateStr);
        final URIObjectProperty property = new URIObjectProperty(null, predicateStr, null);
        addPropertyInList(properties, property);
    }

    public void undefineRelation(String subjectPrefix, URL predicate) throws OntologyException {
        validatePrefix(subjectPrefix);

        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getPropertiesOrException(predicateStr, "Cannot undefine relation");
        for(PropertyObjectBase base : properties) {
            if( ((LiteralObjectProperty) base).subPrefixStr.equals( subjectPrefix ) ) {
                properties.remove(base);
            }
        }
        updateSession();
    }

    public void undefineRelationList(String subjectPrefix, URL predicate) throws OntologyException {
        undefineRelation(subjectPrefix, predicate);
    }

    public void undefineRelation(String subjectPrefix, URL predicate, String objectPrefix) throws OntologyException {
        validatePrefix(subjectPrefix);

        final String predicateStr = predicate.toString();
        final List<PropertyObjectBase> properties = getPropertiesOrException(predicateStr, "Cannot undefine relation");
        for(PropertyObjectBase base : properties) {
            URIObjectProperty property = (URIObjectProperty) base;
            if(
                    property.subPrefixStr.equals( subjectPrefix )
                        &&
                    property.objPrefixStr.equals( objectPrefix  )
            ) {
                properties.remove(property);
            }
        }
        updateSession();
    }

    protected void validatePrefix(String prefix) throws OntologyException {
        if( prefix == null ) {
            throw new OntologyException( String.format("invalid prefix: '%s'", prefix) );
        }
    }

    protected void updateSession() {
        session++;
    }

    protected List<PropertyObjectBase> getProperties(String predicate) {
        List<PropertyObjectBase> properties = predicateIndex.get(predicate);
        if(properties == null) {
            properties = new ArrayList<PropertyObjectBase>();
            predicateIndex.put(predicate, properties);
        }
        return properties;
    }


    protected List<PropertyObjectBase> getPropertiesOrException(String predicate, String msgPrefix)
    throws OntologyException {
        List<PropertyObjectBase> properties = predicateIndex.get(predicate);
        if (properties == null) {
            throw new OntologyException(
                    String.format("%s '%s' because is undefined.", msgPrefix, predicate)
            );
        }
        return properties;
    }

    protected void addPropertyInList( List<PropertyObjectBase> properties, PropertyObjectBase property)
    throws OntologyException {
        if(properties.contains(property)) {
            throw new OntologyException("Property rule redefinition");
        }
        properties.add(property);
        updateSession();
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
    protected PropertyObjectBase validateTerms(String subject, String predicate, Object object, boolean literal)
    throws OntologyException {
        List<PropertyObjectBase> properties = predicateIndex.get(predicate);

        if(properties == null) {
            throw new OntologyException( String.format("predicate '%s' is not defined.", predicate) );
        }

        List<OntologyException> causes = new ArrayList<OntologyException>();
        for(PropertyObjectBase property : properties) {
            try {
                if( literal == property.isLiteral() ) {
                    property.validate(subject, predicate, object);                    
                    return property;
                }
            } catch (OntologyException oe) {
                causes.add(oe);
            }
        }
        throw new OntologyException(
                String.format("No property matches specified terms for predicate: '%s'", predicate)
        );
    }

    public void validateTriple(String subject, String predicate, Object object) throws OntologyException {
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
    private PropertyObjectBase[] properties;

    /**
     * Populates the properties array.
     */
    private void orderRelations() {
        List<PropertyObjectBase> relationsList = new ArrayList<PropertyObjectBase>();
        for(List<PropertyObjectBase> propertyList :  predicateIndex.values() ) {
            relationsList.addAll( propertyList );
        }
        Collections.sort( relationsList );
        properties = relationsList.toArray( new PropertyObjectBase[ relationsList.size() ] );
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
        return ((LiteralObjectProperty) properties[i]).getSubjectPrefix();
    }

    public URL getRelationPredicate(int i) {
        try {
            return new URL( ((LiteralObjectProperty) properties[i]).getPredicate() );
        } catch (MalformedURLException e) {
            throw new IllegalStateException();
        }
    }

    public String getRelationObjectPrefix(int t) {
        return ((URIObjectProperty) properties[t]).getObjectPrefix();
    }

    public void printOntology(PrintStream ps) {
        orderRelations();
        for(PropertyObjectBase rb : properties) {
            rb.print(ps);
        }
    }

    public void toOWL(OutputStream os) {
        //TODO: LOW - implement this.
        throw new UnsupportedOperationException();
    }

    /**
     * Defines a base property.
     */
    abstract class PropertyObjectBase implements Comparable {

        /**
         * Returns the prefix of a term.
         *
         * @param term
         * @return
         * @throws OntologyException
         */
        String getPrefix(String term) throws OntologyException {
            try {
                return IdentifierReader.readIdentifier(term).getStrongestQualifier() + CodeModel.PREFIX_SEPARATOR;
            } catch (Exception e) {
                throw new OntologyException("Invalid term.", e);
            }
        }

        /**
         * Validates a triple over this property.
         *
         * @param subject
         * @param predicate
         * @param object
         * @throws OntologyException
         */
        abstract void validate(String subject, String predicate, Object object) throws OntologyException;

        /**
         * Returns <code>true</code> if this is a literal property, <code>false</code>
         * otherwise.
         *
         * @return
         */
        abstract boolean isLiteral();

        /**
         * Prints the current property as a human readable string on the given print stream.
         *
         * @param ps
         */
        abstract void print(PrintStream ps);

    }

    /**
     * Represents a literal property.
     */
    class LiteralObjectProperty extends PropertyObjectBase {

        /**
         * The subject expected prefix.
         */
        final String subPrefixStr;

        /**
         * predicate string.
         */
        final String predicateStr;

        /**
         * Constructor.
         *
         * @param subjectPrefix
         * @param predicate
         */
        LiteralObjectProperty(String subjectPrefix, String predicate) {
            subPrefixStr = subjectPrefix;
            predicateStr = predicate;
        }

        public String getSubjectPrefix() {
            return subPrefixStr;
        }

        public String getPredicate() {
            return predicateStr;
        }

        public void validate(String subject, String predicate, Object object) throws OntologyException {
            if( subPrefixStr == null ) {
                return;
            }

            String subPrefix = getPrefix(subject);
            if(
                ! subPrefixStr.equals( subPrefix )
            ) {
                throw new OntologyException(
                    String.format("Invalid subject prefix: '%s' for predicate: '%s'", subPrefix, predicateStr)
                );
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
            if(obj instanceof LiteralObjectProperty) {
                LiteralObjectProperty other = (LiteralObjectProperty) obj;
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
            LiteralObjectProperty literalObjectProperty = (LiteralObjectProperty) obj;
            int cmp = predicateStr.compareTo(literalObjectProperty.predicateStr);
            return cmp != 0 ? cmp : subPrefixStr.compareTo(literalObjectProperty.subPrefixStr);
        }
    }

    /**
     * Represents a property.
     */
    class URIObjectProperty extends LiteralObjectProperty {

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
        URIObjectProperty(String subjectPrefix, String predicate, String objectPrefix) {
            super(subjectPrefix, predicate);
            objPrefixStr = objectPrefix;
        }

        public String getObjectPrefix() {
            return objPrefixStr;
        }

        public void validate(String subject, String predicate, Object object) throws OntologyException {
            if( ! (object instanceof String) ) {
                throw new IllegalArgumentException("object must be a string.");
            }

            final String objectStr = (String) object;

            super.validate(subject, predicate, objectStr);

            if(objPrefixStr == null) {
                return;
            }

            String objPrefix = getPrefix(objectStr);
            if(
                ! objPrefix.equals( objPrefixStr )
            ) {
                throw new OntologyException(
                    String.format("Invalid object prefix: '%s' for predicate: '%s'", objPrefixStr, predicate)
                );
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

            if(obj instanceof URIObjectProperty) {
                URIObjectProperty other = (URIObjectProperty) obj;
                return objPrefixStr.equals(other.objPrefixStr);
            }
            return false;
        }

        public int hashCode() {
            return super.hashCode() * objPrefixStr.hashCode();
        }
    }

    class ListObjectProperty extends LiteralObjectProperty {

        private ListBounds listBounds;

        ListObjectProperty(String subjectPrefix, String predicate, ListBounds listBounds) {
            super(subjectPrefix, predicate);
            this.listBounds = listBounds;
        }

        public void validate(String subject, String predicate, Object object) throws OntologyException {
            if( ! (object.getClass().isArray() && object.getClass().getComponentType().equals(String.class)) ) {
                throw new IllegalArgumentException("object must be an String array.");
            }

            super.validate(subject, predicate, object);

            if( ! listBounds.inBounds( (String[]) object )) {
                throw new OntologyException("Invalid object size.");
            }
        }

        @Override
        boolean isLiteral() {
            return false;
        }

        @Override
        void print(PrintStream ps) {
            ps.printf("[%s] --[%s]--> %s", subPrefixStr, predicateStr, listBounds);
            ps.println();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(obj == this) {
                return true;
            }
            if(obj instanceof ListObjectProperty) {
                final ListObjectProperty other = (ListObjectProperty) obj;
                return super.equals(other) && listBounds.equals(other.listBounds);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode() * listBounds.hashCode();
        }
    }

}
