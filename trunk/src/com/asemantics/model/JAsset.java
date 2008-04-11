package com.asemantics.model;

import java.util.Date;

/**
 * Represents the Asset of the parsed libraries.
 */
public class JAsset {

    private QueryModel queryModel;

    protected JAsset(QueryModel qm) {
        queryModel = qm;   
    }

    public String[] getLibraries() {
        return queryModel.getLibraries();
    }

    public String getLibraryLocation(String library) {
        return queryModel.getLibraryLocation(library);
    }

    public Date getLibraryDateTime(String library) {
        return queryModel.getLibraryDateTime(library);
    }

}
