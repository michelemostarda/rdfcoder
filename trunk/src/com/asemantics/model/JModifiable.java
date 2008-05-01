package com.asemantics.model;

/**
 * Represents any entity class with visibility and modifiers.
 */
public abstract class JModifiable extends JBase {

    /**
     * Constructor.
     *
     * @param qm
     * @param sections
     * @throws QueryModelException
     */
    protected JModifiable(QueryModel qm, String[] sections) throws QueryModelException {
        super(qm, sections);
    }

    /**
     * Constructor.
     *
     * @param qm
     * @param path
     * @throws QueryModelException
     */
    public JModifiable(QueryModel qm, String path) throws QueryModelException {
        super(qm, path);
    }

    /**
     * Returns the <i>entity</i> visibility.
     *
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JVisibility getVisibility() throws QueryModelException {
        return getQueryModel().getVisibility(super.getFullName());
    }

    /**
     * Returns the <i>entity</i> modifiers.
     * @return
     * @throws QueryModelException
     */
    public CodeModel.JModifier[] getModifiers() throws QueryModelException {
        return getQueryModel().getModifiers(super.getFullName());
    }
}
