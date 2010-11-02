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


package com.asemantics.rdfcoder.sourceparse.javadoc;

import com.asemantics.rdfcoder.model.Identifier;
import com.asemantics.rdfcoder.model.java.JavaCodeModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a generic <i>Javadoc</i> entry.
 */
public abstract class JavadocEntry implements Serializable {

    static final String PARAMETER_IDENTIFIER = "@param";

    /**
     * The identifier addressing the entity associated to this javadoc.
     */
    private Identifier pathToEntity;

    /**
     * The short description of the comment.
     */
    private final String shortDescription;

    /**
     * The long description of the content.
     */
    private final String longDescription;

    /**
     * The attributes of the entry.
     */
    private final Map<String,List<String>> attributes;

    /**
     * Modifiers associated to this entry.
     */
    private final JavaCodeModel.JModifier[] modifiers;

    /**
     * The visibility of the entry target.
     */
    private final JavaCodeModel.JVisibility visibility;

    /**
     * The location row of the Javadoc entry.
     */
    private final int row;

    /**
     * The location col of the Javadoc entry.
     */
    private final int col;

    /**
     * List of declared attribute names.
     */
    private String[] attributeNames;

    /**
     * List of parameter names.
     */
    private String[] parameterNames;

    /**
     * Constructor.
     *
     * @param pathToEntity
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     * @param modifiers
     * @param visibility
     */
    public JavadocEntry(
            Identifier pathToEntity,
            String sd,
            String ld,
            Map<String, List<String>> attrs,
            int row, int col,
            JavaCodeModel.JModifier[] modifiers,
            JavaCodeModel.JVisibility visibility
    ) {
        if(pathToEntity == null) {
            throw new NullPointerException("path to entity identifier cannot be null.");
        }
        this.pathToEntity = pathToEntity;
        shortDescription = sd;
        longDescription = ld;
        attributes = attrs;
        this.row = row;
        this.col = col;
        this.modifiers = modifiers;
        this.visibility = visibility;
    }

    public Identifier getIdentifier() {
        return pathToEntity;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    private Map<String, String> parametersMap;

    private Map<String, String> getParametersMap() {
        if (parametersMap == null) {
            parametersMap = new HashMap<String,String>();
            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                if ( PARAMETER_IDENTIFIER.equals( entry.getKey() ) ) {
                    for(String listItem: entry.getValue()) {
                        int separator = listItem.indexOf(" ");
                        if(separator != -1) {
                            parametersMap.put(listItem.substring(0, separator), listItem.substring(separator));
                        } else {
                            parametersMap.put(listItem, null);
                        }
                    }
                }
            }
        }
        return parametersMap;
    }

    public String[] getAttributeNames() {
        if(attributeNames == null) {
            attributeNames = attributes.keySet().toArray( new String[ attributes.keySet().size() ]); 
        }
        return attributeNames;
    }

    public String[] getAttributeValues(String attrName) {
        List<String> values = attributes.get(attrName);
        return values.toArray( new String[values.size()] ); 
    }

    public String[] getParameterNames() {
        if (parameterNames == null) {
            parameterNames = getParametersMap().keySet().toArray(new String[getParametersMap().keySet().size()]);
        }
        return parameterNames;
    }

    public String getParameterDescription(String paramName) {
        return getParametersMap().get(paramName);
    }

    public String getParameterShortDescription(String paramName) {
        final String desc = getParameterDescription(paramName);
        return desc != null ? desc.substring(0, desc.indexOf(".")) : "";
    }

    public String getParameterLongDescription(String paramName) {
        final String desc = getParameterDescription(paramName);
        return desc != null ? desc.substring(desc.indexOf(".")) : "";
    }

    public String getReturnDescription() {
        final List<String> ret = attributes.get("@return");
        return ret.get(0);
    }

    public String[] getAuthors() {
        final List<String> authors = attributes.get("@author");
        return authors.toArray(new String[authors.size()]);
    }

    public String[] getSee() {
        final List<String> sees = attributes.get("@see");
        return sees.toArray(new String[sees.size()]);
    }

    public String getSince() {
        final List<String> since = attributes.get("@since");
        return since.get(0);
    }

    public String getVersion() {
        final List<String> version = attributes.get("@version");
        return version.get(0);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public JavaCodeModel.JModifier[] getModifiers() {
        return modifiers;
    }

    public JavaCodeModel.JVisibility getVisibility() {
        return visibility;
    }

    @Override
    public int hashCode() {
        return
                shortDescription.hashCode() *
                longDescription.hashCode()  * 2 *
                parameterNames.hashCode()   * 3 *
                attributes.hashCode()       * 5 *
                row * 7 *
                col * 11;        
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof JavadocEntry) {
            JavadocEntry other = (JavadocEntry) obj ;
            return
                    shortDescription.equals( other.shortDescription )
                        &&
                    longDescription.equals(other.longDescription)
                        &&
                    Arrays.equals(parameterNames, other.parameterNames)
                        &&
                    attributes.equals(other.attributes)
                        &&
                    row == other.row
                        &&
                    col == other.col;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" at location r=").append(row).append(",c=")
                .append(col).append(" {\n");
        sb.append("\tshort description: ").append(shortDescription).append("\n");
        sb.append("\tlong  description: ").append(longDescription).append("\n");
        sb.append("parameter names: ").append( printArray(getParameterNames()) ).append("\n");
        sb.append("\tparams {\n");
        for (Map.Entry e : attributes.entrySet()) {
            sb.append("\t\t'").append(e.getKey()).append("'='").append(e.getValue()).append("'\n");
        }
        sb.append("\t}\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String printArray(Object[] a) {
        StringBuilder sb = new StringBuilder();
        for(Object o : a) {
            sb.append(o);
            sb.append(" ");
        }
        return sb.toString();
    }
}