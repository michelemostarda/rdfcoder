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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a bean inspector able to process inspection strings.
 */
public class Inspector {

    /**
     * Internal inspector parser.
     */
    private InspectorParser inspectorParser;

    private Map<String,Object> context;

    public Inspector() {
        inspectorParser = new InspectorParserImpl();
        context = new HashMap<String,Object>();

        // Registering patterns.
        inspectorParser.registerApplicablePattern( new PropertyReaderPattern() );
        inspectorParser.registerApplicablePattern( new ListReaderPattern()     );
    }

    /**
     * Inspects an object with the given expression.
     *
     * @param expression
     * @param bean
     * @return result of inspection.
     * @throws PatternException
     * @throws InspectorParserException
     */
    public Object inspect(String expression, Object bean) throws InspectorParserException, PatternException {
        Pattern[] patterns = inspectorParser.parse(expression);
        Object current = bean;
        for(Pattern pattern: patterns) {
            current = pattern.applyPattern(current);
        }
        return current;
    }

    /**
     * Inspects the internal context.
     *
     * @param expression
     * @return result of inspection.
     * @throws PatternException
     * @throws InspectorParserException
     */
    public Object inspect(String expression) throws PatternException, InspectorParserException {
        return inspect( expression, context );
    }

    /**
     * Adds a bean with a specific name to the default context.
     *
     * @param name
     * @param bean
     * @return previous bean associated to <i>name</i> if any,
     * <code>null</code> otherwise.
     */
    public Object addToContext(String name, Object bean) {
        return context.put(name, bean);
    }

    /**
     * Removes a bean from the default context.
     *
     * @param name
     * @return previous bean associated to <i>name</i> if any,
     * <code>null</code> otherwise.
     */
    public Object removeFromContext(String name) {
        return context.remove(name);
    }

    /**
     * Describes the bean addressed by the given expression on the given bean context.
     *
     * @param expression
     * @return the description.
     */
    public String describe(String expression, Object bean) throws PatternException, InspectorParserException {
        Object obj = inspect(expression, bean);
        return BeanAccessor.describeBean(obj);
    }

    /**
     * Describes the bean addressed by the given expression on the default context.
     *
     * @param expression
     * @return the description.
     */
    public String describe(String expression) throws InspectorParserException, PatternException {
        return describe(expression, context);
    }
}
