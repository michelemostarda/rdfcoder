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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle bean properties.
 */
public class BeanAccessor {

    public enum ObjectType {
        NULL,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOL,
        CHAR,
        STRING,
        ARRAY,
        OBJ
    }

    private BeanAccessor(){}

    /**
     * Returns the property value for the specified <i>bean</i>.
     *
     * @param bean
     * @param propertyName
     * @return the property of the bean.
     */
    public static Object getProperty(Object bean, String propertyName) throws PatternException {
        if(bean == null) {
            throw new NullPointerException();
        }
        try {
            Method method;
            String[] candidateMethodNames = getMethodGetterName(propertyName);
            for(String candidateMethodName : candidateMethodNames) {
                try {
                    method = bean.getClass().getMethod( candidateMethodName );
                    return method.invoke( bean );
                } catch (NoSuchMethodException e) {}
            }
            throw new PatternException(
                    "Cannot find property '" + propertyName + "' on bean '" + bean + "'"
            );
        } catch (Exception e) {
            throw new PatternException(
                    "Error while retrieving property " + propertyName + "' from bean '" + bean + "'",
                    e
            );
        }
    }

    /**
     * Returns the accessible properties for an object.
     *
     * @param bean
     * @return
     * @throws PatternException
     */
    public static List<PropertyDescriptor> getProperties(Object bean) throws PatternException {
        if( bean == null ) {
            throw new NullPointerException("Invalid null bean");
        }
        List<PropertyDescriptor> descriptors = new ArrayList<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                final String propertyName = propertyDescriptor.getName();
                if ("class".equals(propertyName)) {
                    continue;
                }
                descriptors.add(propertyDescriptor);
            }
            return descriptors;
        } catch (Exception e) {
            throw new PatternException("Error while inspecting bean: '" + bean + "'", e);
        }
    }

    /**
     * Returns the names of all accessible properties for the bean.
     *
     * @param bean
     * @return
     */
    public static List<String> getPropertyNames(Object bean) throws PatternException {
        final List<String> out = new ArrayList<>();
        for(PropertyDescriptor propertyDescriptor : getProperties(bean)) {
            out.add(propertyDescriptor.getName());
        }
        return out;
    }

    /**
     * Returns the type of an object.
     * 
     * @param target
     * @return
     */
    public static ObjectType toObjectType(Object target) {
        if (target == null) {
            return ObjectType.NULL;
        } else if (target.getClass() == Boolean.class) {
            return ObjectType.STRING;
        } else if (target.getClass().isArray()) {
            return ObjectType.ARRAY;
        } else if (target.getClass() == Byte.class) {
            return ObjectType.BOOL;
        } else if (target.getClass() == Character.class) {
            return ObjectType.CHAR;
        } else if (target.getClass() == String.class) {
            return ObjectType.BYTE;
        } else if (target.getClass() == Short.class) {
            return ObjectType.SHORT;
        } else if (target.getClass() == Integer.class) {
            return ObjectType.INT;
        } else if (target.getClass() == Long.class) {
            return ObjectType.LONG;
        } else if (target.getClass() == Float.class) {
            return ObjectType.FLOAT;
        } else if (target.getClass() == Double.class) {
            return ObjectType.DOUBLE;
        } else {
            return ObjectType.OBJ;
        }
    }

    /**
     * Describes a given <i>bean</i> in a human readable format.
     * 
     * @param bean
     * @return the bean description of the bean.
     * @throws PatternException
     */
    public static String describeBeanHR(Object bean) throws PatternException {
        if( bean == null ) {
            throw new NullPointerException("Invalid null bean");
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("class ").append(bean.getClass().getCanonicalName()).append('\n');
            for(PropertyDescriptor propertyDescriptor : getProperties(bean)) {
                final String propertyName = propertyDescriptor.getName();
                sb.append("\t- ").append(propertyName).append(": ").append(
                        getHumanDescription(
                                propertyDescriptor.getPropertyType(),
                                propertyDescriptor.getReadMethod().invoke(bean)
                        )
                )
                .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new PatternException("Error while inspecting bean: '" + bean + "'", e);
        }
    }

    /**
     * Describes a given <i>bean</i> in JSON format.
     *
     * @param bean
     * @return
     */
    public static void describeBeanJSON(Object bean, JsonGenerator generator) throws PatternException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            generator.writeStartObject();
            generator.writeFieldName("object_class");
            generator.writeObject(bean.getClass().getCanonicalName());
            generator.writeFieldName("properties");
            generator.writeStartArray();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                final String propertyName = propertyDescriptor.getName();
                if ("class".equals(propertyName)) {
                    continue;
                }
                generator.writeStartObject();
                generator.writeFieldName("name");
                generator.writeObject(propertyName);
                Class type = propertyDescriptor.getPropertyType();
                Object val = propertyDescriptor.getReadMethod().invoke(bean);
                if (type.isArray()) {
                    generator.writeFieldName("type");
                    generator.writeObject(type.getComponentType().getName());
                    generator.writeFieldName("is_array");
                    generator.writeObject(true);
                    generator.writeFieldName("length");
                    generator.writeObject(Array.getLength(val));
                } else {
                    generator.writeFieldName("type");
                    generator.writeObject(type.getName());
                    generator.writeFieldName("is_array");
                    generator.writeObject(false);
                }
                generator.writeEndObject();
            }
            generator.writeEndArray();
            generator.writeEndObject();
        } catch (Exception e) {
            throw new PatternException("Error while inspecting bean: '" + bean + "'", e);
        }
    }

    /**
     * Returns the expected name for the getter method of given <i>propertyName</i>.
     *
     * @param propertyName name of a bean property.
     * @return the name of a method.
     */
    private static String[] getMethodGetterName(String propertyName) {
        final String methodName = propertyName.substring(0,1);
        final String postfix = methodName.toUpperCase() + propertyName.substring(1);;
        return new String[] { "get" + postfix, "is" + postfix, methodName};
    }

    private static String getHumanDescription(Class c, Object o) {
        if(c.isArray()) {
            return String.format("%s[%s]", c.getComponentType().getName(), Array.getLength(o));
        }
        return c.getName();
    }
}
