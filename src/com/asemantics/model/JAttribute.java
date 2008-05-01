package com.asemantics.model;

/**
 * Represents a class Attribute.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JAttribute extends JModifiable {

    /**
     * Checks if the path to the attribute exists.
     * @param qm the <code>QueryModel</code>.
     * @param pathToAttribute the path to the attribute.
     * @return true if the path is valid, false otherwise.
     */
    public static final boolean exists(QueryModel qm, final String pathToAttribute) {
        return qm.attributeExists(pathToAttribute);
    }

    /**
     * Constructor.
     * @param qm
     * @param sections
     * @throws CodeModelException
     */
    protected JAttribute(QueryModel qm, String[] sections)
    throws QueryModelException {
        super(qm, sections);
    }

    /**
     * Constructor.
     * @param qm
     * @param pathToAttribute
     * @throws CodeModelException
     */
    protected JAttribute(QueryModel qm, String pathToAttribute)
    throws QueryModelException {
        super(qm, pathToAttribute);
    }

    /**
     * Returns the attribute parent class.
     *
     * @return
     */
    public JClass getParentClass() {
        return (JClass) getParent();
    }

    /**
     * Returns the hierachy element name.
     * @return
     */
    public String getHyerarchyElemType() {
        return this.getClass().getName();
    }

    /**
     * Check wheter the attribute exists.
     * @param name
     * @param index
     * @return
     */
    public boolean exists(final String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index));
    }

}
