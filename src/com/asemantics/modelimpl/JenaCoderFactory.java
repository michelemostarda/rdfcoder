package com.asemantics.modelimpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.asemantics.model.CoderFactory;
import com.asemantics.model.SPARQLQuerableCodeModel;

/**
 ** The <code>CoderFactory</code> implementation for the Jena backend.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JenaCoderFactory extends CoderFactory {

    public SPARQLQuerableCodeModel createCodeModel() {
        Model model = ModelFactory.createDefaultModel();
        return new JenaCodeModel(model);
    }

    public JenaCodeStorage createCodeStorage() {
        return new JenaCodeStorage();
    }

}
