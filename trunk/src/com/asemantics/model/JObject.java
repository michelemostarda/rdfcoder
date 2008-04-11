package com.asemantics.model;

/**
 * This class represents the base class for both <code>JInterface</code>
 * and <code>JClass</code>. inside it there are some  methods to access
 * classes and interfaces common entities.
 */
public abstract class JObject extends JContainer {

    protected JObject(QueryModel qm, String[] sections)
    throws QueryModelException {
        super(qm, sections);
    }

    protected JObject(QueryModel qm, String pathToContainer)
    throws QueryModelException {
        super(qm, pathToContainer);
    }

    /**
     * Returns the inner classes inside the object.
     * @return
     * @throws QueryModelException
     */
    public JClass[] getInnerClasses() throws QueryModelException {
        QueryModel qm = getQueryModel();
        return qm.getClassesInto( getPathAsString() );
    }

    /**
     * Returns the methods inside the object.
     * @return
     * @throws QueryModelException
     */
    public JMethod[] getMethods() throws QueryModelException {
        QueryModel qm = getQueryModel();
        return qm.getMethodsInto( getPathAsString() );
    }

    /**
     * Returns the attributes inside the object.
     * @return
     */
    public JAttribute[] getAttributes() throws QueryModelException {
        QueryModel qm = getQueryModel();
        return qm.getAttributesInto( getPathAsString() );
    }

    /**
     * Returns the enumerations inside the object.
     * @return
     * @throws QueryModelException
     */
    public JEnumeration[] getEnumerations() throws QueryModelException {
        QueryModel qm = getQueryModel();
        return qm.getEnumerationsInto( getPathAsString() );
    }
}
