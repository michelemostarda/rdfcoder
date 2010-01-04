/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.rdfcoder.model.ontology;

import java.util.Collection;
import java.util.Collections;


/**
 * Defines any exception related to <i>ontology validation</i>.
 */
public class OntologyException extends Exception {

    private Collection<Throwable> causes;

    /**
     * Constructor.
     *
     * @param msg error message.
     * @param cause exception cause.
     */
    public OntologyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructor.
     * 
     * @param msg error message.
     */
    public OntologyException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg error message.
     * @param causes list of causes for the current exception.
     */
    public OntologyException(String msg, Collection<Throwable> causes) {
        super(msg);
        this.causes = Collections.unmodifiableCollection(causes);
    }

    public Collection<Throwable> getCauses() {
        return causes;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + String.format("(%d causes)", causes == null ? 0 : causes.size() );
    }
    
}
