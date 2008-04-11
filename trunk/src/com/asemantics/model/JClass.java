package com.asemantics.model;

/**
 * Represents an Java class.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JClass extends JObject {

    /**
     * Check if a class exists.
     * @param qm
     * @param pathToClass
     * @return
     */
    public static boolean exists(QueryModel qm, final String pathToClass) {
       return qm.classExists(pathToClass);
    }

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToClass
     * @throws QueryModelException
     */
    protected JClass(QueryModel queryModel, String pathToClass)
    throws QueryModelException {

        super(queryModel, pathToClass);
    }

    /**
     * Constructor by sections.
     * @param qm
     * @param sections
     * @throws QueryModelException
     */
    protected JClass(QueryModel qm, String[] sections) throws QueryModelException {
        super(qm, sections);
    }

    public boolean exists(final String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index) );
    }

    /**
     * Returns <code>true</code> if this is an inner class,
     * false otherwise.
     * @return
     */
    public boolean isInnerClass() {
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
     * Return the parent package if this is firs level class
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
