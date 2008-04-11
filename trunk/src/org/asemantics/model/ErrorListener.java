package com.asemantics.model;

/**
 * This class allows to collect errors and warnings occurred during the parsing process.
 */
public interface ErrorListener {

    /**
     * Notifies if a class decleres a package different from the package it belongs to.
     *
     * @param codeHandler
     * @param processedPackage
     * @param declaredContainerPackage
     */
    public void packageDiscrepancy(CodeHandler codeHandler, String processedPackage, String declaredContainerPackage);

    /**
     * Notifies parse errors during the compilation process.
     *
     * @param codeHandler
     * @param location
     * @param description
     */
    public void parseError(CodeHandler codeHandler, String location, String description);

    /**
     * Notifies the list of the unresolved types.
     *
     * @param codeHandler
     * @param types
     */
    public void unresolvedTypes(CodeHandler codeHandler, String[] types);
}
