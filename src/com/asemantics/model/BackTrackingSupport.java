
package com.asemantics.model;

/**
 * This class extends <code>CodeHandler</code>
 * to provide a back tracing mechaninm able to
 * accept unresolved types and fix it in a second time.
 */
public interface BackTrackingSupport {

    /**
     * Generates a temporary unique identifier to substitute an unknown type.
     * @return
     */
    public String generateTempUniqueIdentifier();

    /**
     * Replaces the remporary identifier with the qualified type.
     * @param identifier the identifier to replace.
     * @param qualifiedType the qualified type.
     * @return number of effected triples.
     */
    public int replaceIdentifierWithQualifiedType(String identifier, String qualifiedType);

}