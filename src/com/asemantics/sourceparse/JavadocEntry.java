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


package com.asemantics.sourceparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavadocEntry {

    static final String PARAMETER_IDENTIFIER = "@param";

    /**
     * The short desciption of the comment.
     */
    private String shortDescription;

    /**
     * The long decription of the content.
     */
    private String longDescription;

    /**
     * List of parameter names.
     */
    private String[] parameterNames;

    /**
     * The atreibutes of the entry.
     */
    private Map<String,List<String>> attributes;

    /**
     * The location row of the Javadoc entry.
     */
    private int row;

    /**
     * The location col of the Javadoc entry.
     */
    private int col;

    /**
     * Constructor.
     *
     * @param sd
     * @param ld
     * @param attrs
     * @param row
     * @param col
     */
    public JavadocEntry(String sd, String ld, Map<String, List<String>> attrs, int row, int col) {
        shortDescription = sd;
        longDescription = ld;
        attributes = attrs;
        this.row = row;
        this.col = col;
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
            parametersMap = new HashMap();
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

    public String[] getParameterNames() {
        if (parameterNames == null) {
            parameterNames = getParametersMap().keySet().toArray(new String[getParametersMap().keySet().size()]);
        }
        return parameterNames;
    }

    public String getParameterShortDescription(String paramName) {
        String desc = getParametersMap().get(paramName);
        return desc != null ? desc.substring(0, desc.indexOf(".")) : "";
    }

    public String getParameterLongDescription(String paramName) {
        String desc = getParametersMap().get(paramName);
        return desc != null ? desc.substring(desc.indexOf(".")) : "";
    }

    public String getReturnDescription() {
        List<String> ret = attributes.get("@return");
        return ret.get(0);
    }

    public String[] getAuthors() {
        List<String> authors = attributes.get("@author");
        return authors.toArray(new String[authors.size()]);
    }

    public String[] getSee() {
        List<String> sees = attributes.get("@see");
        return sees.toArray(new String[sees.size()]);
    }

    public String getSince() {
        List<String> since = attributes.get("@since");
        return since.get(0);
    }

    public String getVersion() {
        List<String> version = attributes.get("@version");
        return version.get(0);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" at location r=").append(row).append(",c=").append(col).append(" {\n");
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