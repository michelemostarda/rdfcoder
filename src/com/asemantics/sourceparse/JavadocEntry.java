package com.asemantics.sourceparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavadocEntry {

    /**
     * The short desciption of the comment.
     */
    private String shortDescription;

    /**
     * The long decription of the content.
     */
    private String longDescription;

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
                if (entry.getKey().equals("parameter")) {
                    for(String listItem: entry.getValue()) {
                        int separator = listItem.indexOf(" ");
                        parametersMap.put(listItem.substring(0, separator), listItem.substring(separator));
                    }
                }
            }
        }
        return parametersMap;
    }

    String[] parameterNames;

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

    public String[] getAuthor() {
        List<String> authors = attributes.get("author");
        return authors.toArray(new String[authors.size()]);
    }

    public String[] getSee() {
        List<String> sees = attributes.get("see");
        return sees.toArray(new String[sees.size()]);
    }

    public String getSince() {
        List<String> since = attributes.get("see");
        return since.get(0);
    }

    public String getVersion() {
        List<String> version = attributes.get("version");
        return version.get(0);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName()).append(" at location r=").append(row).append(",c=").append(col).append(" {\n");
        sb.append("\tshort description: ").append(shortDescription).append("\n");
        sb.append("\tlong  description: ").append(longDescription).append("\n");
        sb.append("\tparams {\n");
        for (Map.Entry e : attributes.entrySet()) {
            sb.append("\t\t'").append(e.getKey()).append("'='").append(e.getValue()).append("'\n");
        }
        sb.append("\t}\n");
        sb.append("}\n");
        return sb.toString();
    }
}