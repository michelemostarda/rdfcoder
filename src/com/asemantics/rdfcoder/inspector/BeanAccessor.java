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

package com.asemantics.rdfcoder.inspector;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Utility class to handle bean properties.
 */
public class BeanAccessor {

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
            Method method = null;
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
     * Describes a given <i>bean</i>.
     * 
     * @param bean
     * @return the bean description of the bean.
     * @throws PatternException
     */
    public static String describeBean(Object bean) throws PatternException {
        if( bean == null ) {
            throw new NullPointerException("Invalid null bean");
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo( bean.getClass() );
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                final String propertyName = propertyDescriptor.getName();
                if("class".equals(propertyName)) {
                    continue;
                }
                sb.append(propertyName)
                        .append(":")
                .append(
                        getHumanDescription(
                                propertyDescriptor.getPropertyType(),
                                propertyDescriptor.getReadMethod().invoke(bean)
                        )
                )
                .append("\n");
            }
            sb.append("}\n");
            return sb.toString();
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
