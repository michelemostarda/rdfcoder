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


package com.asemantics.inspector;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link InspectorParser}.
 */
public class InspectorParserImpl implements InspectorParser {

    /**
     * List of applicable patterns for this parser.
     */
    private List<Pattern> applicablePatterns = new ArrayList<Pattern>();

    public Pattern[] parse(final String expression) throws InspectorParserException {
        if(expression == null) {
            throw new InspectorParserException("Invalid expression: null");
        }

        String[] parts = expression.contains(PATH_SEPARATOR) ? expression.split("\\" + PATH_SEPARATOR) : new String[] { expression };
        if(parts.length == 0) {
            throw new InspectorParserException("Invalid expression: '" + expression + "'");
        }

        // Trims and validates all parts.
        int l,location = 0;
        for(int i = 0; i < parts.length; i++) {
            l = parts[i].length();
            parts[i] = parts[i].trim();
            validatePart(parts[i], location);
            location += l;
        }

        Pattern[] patters = new Pattern[ parts.length ];
        Pattern applicablePattern;
        String part;
        for(int i = 0; i < parts.length; i++) {
            part = parts[i];
            applicablePattern = findApplicablePattern(part);
            if( applicablePattern == null ) {
                throw new InspectorParserException("Cannot find an applicable pattern for part '" + part + "'");
            }
            patters[i] = applicablePattern;
        }
        return patters;
    }

    public void registerApplicablePattern(Pattern pattern) {
        applicablePatterns.add(pattern);
    }

    public void deregisterApplicablePattern(Pattern pattern) {
        applicablePatterns.remove(pattern);
    }

    protected Pattern findApplicablePattern(String part) {
        for(Pattern pattern : applicablePatterns) {
            if( pattern.isApplicable(part) ) {
                return pattern.getInstance(part);
            }
        }
        return null;
    }

    private void validatePart(String part, int beginLocation) throws InspectorParserException {
        if(part.length() == 0) {
            throw new InspectorParserException("Invalid part: '" + part + "'", beginLocation);
        }
    }

}
