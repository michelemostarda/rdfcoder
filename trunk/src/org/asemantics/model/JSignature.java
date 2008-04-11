package com.asemantics.model;

/**
 * Represents a method signature.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JSignature extends JBase {

    /**
     * The parameter types of the signature.
     */
    CodeModel.JType[] paramTypes;

    /**
     * The return type of the signature.
     */
    CodeModel.JType returnType;

    /**
     * Check wether a signaure exists.
     * @param qm
     * @param pathToSignature
     * @return
     */
    public static boolean exists( QueryModel qm, String pathToSignature) {
        return qm.signatureExists(pathToSignature);
    }

    protected JSignature(
            QueryModel queryModel,
            String[] sections,
            CodeModel.JType[] paramTypes,
            CodeModel.JType returnType
    ) throws QueryModelException {
        super(queryModel, sections);
        if(paramTypes == null || returnType == null) {
            throw new IllegalArgumentException();
        }

        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    protected JSignature(
            QueryModel queryModel,
            String pathToSignature,
            CodeModel.JType[] paramTypes,
            CodeModel.JType returnType
    ) throws QueryModelException {
        super(queryModel, pathToSignature);
        if(paramTypes == null || returnType == null) {
            throw new IllegalArgumentException();
        }

        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    protected JSignature(
        QueryModel queryModel,
        String[] sections
    ) throws QueryModelException {
        super(queryModel, sections);
        String pathToSignature = concatenate(sections, sections.length -1);
        paramTypes = queryModel.getParameters(pathToSignature);
        returnType = queryModel.getReturnType(pathToSignature);
    }

    protected JSignature(
        QueryModel queryModel,
        String pathToSignature
    ) throws QueryModelException {
        super(queryModel, pathToSignature);
        paramTypes = queryModel.getParameters(pathToSignature);
        returnType = queryModel.getReturnType(pathToSignature);
    }

    CodeModel.JType[] getParameters() {
        return paramTypes.clone();
    }

    CodeModel.JType getReturnType() {
        return returnType.cloneType();
    }


    public boolean exists(final String[] name, int index) {
        return exists( getQueryModel(), concatenate(name, index) );
    }

    protected String getHyerarchyElemType() {
        return this.getClass().getSimpleName();
    }
}