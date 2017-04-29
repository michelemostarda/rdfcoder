/*
 * Copyright 2007-2017 Michele Mostarda ( michele.mostarda@gmail.com ).
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

package com.asemantics.rdfcoder.inspector;

import com.fasterxml.jackson.core.JsonGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes inspection strings. An inspection string is a path
 * defining a resource in an object graph.
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
     * Given an expression string returns the type of the target object.
     * 
     * @param expression
     * @return the detected object type
     * @throws PatternException
     * @throws InspectorParserException
     */
    public BeanAccessor.ObjectType getType(String expression) throws PatternException, InspectorParserException {
        Object target = inspect(expression);
        return BeanAccessor.toObjectType(target);
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
        return BeanAccessor.describeBeanHR(obj);
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

    /**
     * Describes the bean addressed by the given expression on the given bean context in JSON format.
     *
     * @param expression
     * @param generator
     * @return the description.
     */
    public void describeJSON(String expression, Object bean, JsonGenerator generator) throws PatternException, InspectorParserException {
        Object obj = inspect(expression, bean);
        BeanAccessor.describeBeanJSON(obj, generator);
    }

    /**
     * Describes the bean addressed by the given expression on the default context.
     *
     * @param expression
     * @param generator
     * @return the description.
     */
    public void describeJSON(String expression, JsonGenerator generator) throws InspectorParserException, PatternException {
        describeJSON(expression, context, generator);
    }

    /**
     * Lists the available properties for the current model.
     *
     * @return
     * @throws PatternException
     */
    public Map<String, List<String>> listProperties() {
        final Map<String, List<String>> properties = new HashMap<>();
        try {
            for(Map.Entry<String,Object> entry : context.entrySet()) {
                properties.put(entry.getKey(), BeanAccessor.getPropertyNames(entry.getValue()));
            }
            return properties;
        } catch (PatternException pe) {
            throw new RuntimeException("Unexpected error while loading properties.", pe);
        }
    }

    /**
     * Returns the public properties of the object idenfied by expression.
     *
     * @param expression the expression targeting the inspected object
     * @return
     */
    public List<String> getProperties(String expression) throws PatternException, InspectorParserException {
        return BeanAccessor.getPropertyNames(inspect(expression));
    }
}
