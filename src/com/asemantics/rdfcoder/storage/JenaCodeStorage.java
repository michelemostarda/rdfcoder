/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.rdfcoder.storage;

import com.asemantics.rdfcoder.RDFCoder;
import com.asemantics.model.CodeModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFReader;

import java.io.*;
import java.util.Map;

/**
 * Represents a <code>CodeStorage</code> for the Jena <code>CodeModel</code>.
 */
public class JenaCodeStorage extends CodeStorage {

    /**
     * Constructor.
     */
    protected JenaCodeStorage() {}  // Protecting instantiation.

    public void loadModel(CodeModel codeModel, InputStream inputStream) throws CodeStorageException {
        Model jenaModel;
        try {
            jenaModel = ((JenaCodeModel) codeModel).getJenaModel();
        } catch (ClassCastException cce) {
            throw new JenaCodeStorageException("Expected " + JenaCodeModel.class + " here", cce);
        }
        try {
            RDFReader reader = jenaModel.getReader();
            reader.read(jenaModel, inputStream, null);
        } catch (Exception e) {
            throw new JenaCodeStorageException("Error while reading model.", e);
        }
    }

    public void saveModel(CodeModel codeModel, OutputStream outputStream) throws CodeStorageException {
        Model jenaModel;
        try {
            jenaModel = ((JenaCodeModel) codeModel).getJenaModel();
        } catch (ClassCastException cce) {
            throw new JenaCodeStorageException("Expected " + JenaCodeModel.class + " here", cce);
        }
        try {
            RDFWriter writer = jenaModel.getWriter();
            writer.write(jenaModel, outputStream, null);
        } catch (Exception e) {
            throw new JenaCodeStorageException("Error while writing model.", e);
        }
    }

    public void saveModel(CodeModel codeModel,  Map<String, String> parameters) {
        if( ! ( codeModel instanceof JenaCodeModel ) ) {
            throw new IllegalArgumentException("codeModel must be instaceof JenaCodeModel");
        }
        if(parameters == null) {
            throw new NullPointerException("properties cannot be null");
        }

        JenaCodeModel jenaCodeModel = (JenaCodeModel) codeModel;
        if( RDFCoder.assertions() ) {
            JenaCodeModel.checkModel(jenaCodeModel.getJenaModel());
        }
        FileOutputStream fos = openFileOutputStream(parameters);
        try {
        jenaCodeModel.getJenaModel().write( fos );
        } finally {
            try {
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    public void loadModel(CodeModel codeModel, Map parameters) {
        if( ! ( codeModel instanceof JenaCodeModel ) ) {
            throw new IllegalArgumentException("codeModel must be instaceof JenaCodeModel");
        }
        if(parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }

        JenaCodeModel jenaCodeModel = (JenaCodeModel) codeModel;
        FileInputStream fis = openFileInputStream(parameters);
        try {
            jenaCodeModel.getJenaModel().read( fis, null );
        } finally {
            try {
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public boolean supportsDatabase() {
        return false;
    }

    public boolean supportsFile() {
        return true;
    }
}
