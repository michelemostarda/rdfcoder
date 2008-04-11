package com.asemantics.model;

/**
 * Represents a <code>CodeModel</code> that can
 * be queried by SPARQL queries.
 */
public abstract class SPARQLQuerableCodeModel extends CodeModel {

    public abstract QueryResult performQuery(String sparqlQry);

}
