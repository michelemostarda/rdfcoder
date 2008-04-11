package com.asemantics.model;

/**
 * Represents a Java interface.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JInterface extends JObject {

    /**
     * Check if an interface exists.
     * @param qm
     * @param name
     * @param index
     * @return
     */
    public static boolean exists(QueryModel qm, final String name[], int index) {
       return qm.interfaceExists( concatenate(name, index) );
    }

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToInterface
     * @throws QueryModelException
     */
    protected JInterface(QueryModel queryModel, String pathToInterface)
    throws QueryModelException {

        super(queryModel, pathToInterface);
    }

    /**
     * Constructor by sections.
     * @param qm
     * @param sections
     * @throws QueryModelException
     */
    protected JInterface(QueryModel qm, String[] sections) throws QueryModelException {
        super(qm, sections);
    }

    public boolean exists(final String[] name, int index) {
        return exists(getQueryModel(), name, index);
    }

    /**
     * Returns <code>true</code> if this is a inner interface,
     * false otherwise.
     * @return
     */
    public boolean isInnerInterface() {
        return parent instanceof JClass || parent instanceof JInterface;
    }

    /**
     * Return the parent class if this is an inner class
     * or <code>null</code> otherwise.
     * @return
     */
    public JClass getParentClass() {
        if(parent instanceof JClass) {
            return (JClass) parent;
        }
        return null;
    }

    /**
     * Return the parent package if this is first level class
     * or <code>null</code> otherwise.
     * @return
     */

    public JPackage getParentPackage() {
        if(parent instanceof JPackage) {
            return (JPackage) parent;
        }
        return null;
    }

    public String toString() {
        return parent.toString() + CodeHandler.PACKAGE_SEPARATOR + super.toString();
    }

    protected String getHyerarchyElemType() {
        return this.getClass().getSimpleName();
    }
}
