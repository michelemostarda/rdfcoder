package com.asemantics.model;

/**
 * Represents a code package.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JPackage extends JContainer {

    /**
     * Check wether a package exists.
     * @param qm
     * @param pathToPackege
     * @return
     */
    public static boolean exists(QueryModel qm,  String pathToPackege) {
        return qm.packageExists( pathToPackege );
    }

    /**
     * Constructor.
     * @param codeModel
     * @param packageSections
     */
    protected JPackage(QueryModel codeModel, String[] packageSections)
    throws QueryModelException {
        super(codeModel, packageSections);
    }

    /**
     * Constructor.
     * @param codeModel
     * @param name
     */
    protected JPackage(QueryModel codeModel, String name)
    throws QueryModelException {
        super(codeModel, name);
    }

    public boolean exists(String[] name, int index) {
        return exists(getQueryModel(), concatenate(name, index));
    }

    protected String getHyerarchyElemType() {
        return this.getClass().getSimpleName();
    }

    public CodeModel.JVisibility getVisibility() throws QueryModelException {
        return CodeModel.JVisibility.PUBLIC;
    }
}
