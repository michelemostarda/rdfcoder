package com.asemantics.modelimpl;

import com.asemantics.RDFCoder;
import com.asemantics.model.CodeModel;
import com.asemantics.model.CodeStorage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Represents a <code>CodeStorage</code> for the Jena <code>CodeModel</code>.
 */
public class JenaCodeStorage extends CodeStorage {

    protected JenaCodeStorage() {}  // Protecting instantiation.

    public void saveModel(CodeModel codeModel,  Map<String, String> parameters) throws IOException {
        if( ! ( codeModel instanceof JenaCodeModel ) ) {
            throw new IllegalArgumentException("codeModel must be instaceof JenaCodeModel");
        }
        if(parameters == null) {
            throw new NullPointerException("properties cannot be null");
        }

        JenaCodeModel jenaCodeModel = (JenaCodeModel) codeModel;
        if(RDFCoder.isDEBUG()) {
            JenaCodeModel.checkModel(jenaCodeModel.getJenaModel());
        }
        FileOutputStream fos = openFileOutputStream(parameters);
        jenaCodeModel.getJenaModel().write( fos );
        fos.close();
    }


    public void loadModel(CodeModel codeModel, Map parameters) throws IOException {
        if( ! ( codeModel instanceof JenaCodeModel ) ) {
            throw new IllegalArgumentException("codeModel must be instaceof JenaCodeModel");
        }
        if(parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }

        JenaCodeModel jenaCodeModel = (JenaCodeModel) codeModel;
        FileInputStream fis = openFileInputStream(parameters);
        jenaCodeModel.getJenaModel().read( fis, null );
        fis.close();
    }

    public boolean supportsDatabase() {
        return false;
    }

    public boolean supportsFile() {
        return true;
    }
}
