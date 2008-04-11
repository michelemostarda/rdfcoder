package com.asemantics.model;

/**
 * Represents a class method.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JMethod extends JBase {

    public static boolean exists(QueryModel qm, String pathToMethod) {
        return qm.methodExists(pathToMethod);
    }

    /**
     * Constructor by sections.
     * @param queryModel
     * @param sections
     * @throws CodeModelException
     */
    protected JMethod(QueryModel queryModel, String[] sections)
            throws QueryModelException {
        super(queryModel, sections);
    }

    /**
     * Constructor by path.
     * @param queryModel
     * @param pathToMethod
     * @throws CodeModelException
     */
    protected JMethod(QueryModel queryModel, String pathToMethod)
            throws QueryModelException {
        super(queryModel, pathToMethod);
    }

    /**
     * Returns the signatures of a method.
     * @return
     * @throws CodeModelException
     */
    public JSignature[] getSignatures() throws QueryModelException {
        return getQueryModel().getSignatures(super.getFullName());
    }

    /**
     * Check wether a method exists.
     * @param name
     * @param index
     * @return
     */
    public boolean exists( String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index));
    }

    protected String getHyerarchyElemType() {
        return this.getClass().getSimpleName();
    }

}
