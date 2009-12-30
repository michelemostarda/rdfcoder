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


package com.asemantics.rdfcoder.model;

import java.util.Stack;

/**
 * Helper class used to build the current parsing context.
 *
 * @see com.asemantics.rdfcoder.model.IdentifierReader
 * @see com.asemantics.rdfcoder.model.Identifier
 * @version $Id$
 */
public class IdentifierBuilder {

    private IdentifierBuilder() {}

    public static IdentifierBuilderInstance create() {
        return new IdentifierBuilderInstance();
    }

    public static IdentifierBuilderInstance create(Identifier identifier) {
        return new IdentifierBuilderInstance(identifier);
    }

    public static class IdentifierBuilderInstance {

        private String prefix;

        private Stack<IdentifierFragment> stack;

        private IdentifierBuilderInstance() {
            prefix = "";
            stack = new Stack<IdentifierFragment>();
        }

        private IdentifierBuilderInstance(Identifier identifier) {
            prefix = identifier.getPrefix();
            stack = new Stack<IdentifierFragment>();
            stack.addAll(identifier.fragments);
        }

        public IdentifierBuilderInstance setPrefix(String prefix) {
            if (prefix == null || prefix.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid prefix content.");
            }
            if( prefix.indexOf(CodeModel.URI_PREFIX_SEPARATOR) != prefix.length() - 1 ) {
                throw new IllegalArgumentException(
                        String.format("Invalid prefix: must end with '%s'", CodeModel.URI_PREFIX_SEPARATOR)
                );
            }
            this.prefix = prefix;
            return this;
        }

        public IdentifierBuilderInstance pushFragment(String fragment, String qualifier) {
            stack.push( new IdentifierFragment(fragment, qualifier) ) ;
            return this;
        }

        public IdentifierBuilderInstance popFragment() {
            stack.pop();
            return this;
        }

        public Identifier build() {
            return new Identifier( prefix, stack.toArray( new IdentifierFragment[stack.size()] ));
        }

    }

}
