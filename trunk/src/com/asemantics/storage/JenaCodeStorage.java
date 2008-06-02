/**
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


package com.asemantics.storage;

import com.asemantics.RDFCoder;
import com.asemantics.model.CodeModel;
import com.asemantics.storage.CodeStorage;

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
