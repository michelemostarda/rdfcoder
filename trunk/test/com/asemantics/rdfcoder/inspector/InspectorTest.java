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

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Test case for {@link com.asemantics.rdfcoder.inspector.Inspector} class.
 */
public class InspectorTest extends TestCase {

    class TargetIn {

    }

    class Target {

        // String type.
        private String str = "str";

        // Boolean type.
        private boolean condition = true;

        // Primitive type.
        private int value = 100;

        // Complex type.
        private TargetIn targetIn = new TargetIn();

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public TargetIn getTargetIn() {
            return targetIn;
        }

        public void setTargetIn(TargetIn targetIn) {
            this.targetIn = targetIn;
        }

        public boolean isCondition() {
            return condition;
        }

        public void setCondition(boolean condition) {
            this.condition = condition;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private Inspector inspector;

    public void setUp() {
        inspector = new Inspector();
    }

    protected void tearDown() throws Exception {
        inspector = null;
    }

    public void testReadSimpleProperty() throws PatternException, InspectorParserException {
        Target target = new Target();

        Object result1 = inspector.inspect("str", target);
        assertNotNull("Result not found.", result1);
        assertTrue("Invalid result type.", result1 instanceof String);
        assertEquals( "Invalid result value", "str", (String) result1);

        Object result2 = inspector.inspect("condition", target);
        assertNotNull("Result not found.", result2);
        assertTrue("Invalid result type.", result2 instanceof Boolean);
        assertEquals( "Invalid result value", true, result2);

        Object result3 = inspector.inspect("value", target);
        assertNotNull("Result not found.", result3);
        assertTrue("Invalid result type.", result3 instanceof Integer);
        assertEquals( "Invalid result value", 100, result3);

        Object result4 = inspector.inspect("targetIn", target);
        assertNotNull("Result not found.", result4);
        assertTrue("Invalid result type.", result4 instanceof TargetIn );
    }

    public void testReadMapProperty() throws PatternException, InspectorParserException {
        Map map = new HashMap();
        map.put("prop1", "val1");
        map.put("prop2", "val2");
        map.put("prop3", "val3");

        Object result1 = inspector.inspect("prop1", map);
        assertNotNull("Result not found.", result1);
        assertEquals("val1", result1);

        Object result2 = inspector.inspect("prop2", map);
        assertNotNull("Result not found.", result2);
        assertEquals("val2", result2);

        Object result3 = inspector.inspect("prop3", map);
        assertNotNull("Result not found.", result3);
        assertEquals("val3", result3);
    }

    public void testReadComplexSequence() throws PatternException, InspectorParserException {
        Target target = new Target();

        Map mapLevel2 = new HashMap();
        mapLevel2.put("prop11", "value11");
        mapLevel2.put("prop12", "value12");
        mapLevel2.put("prop13", target);

        Map mapLevel1 = new HashMap();
        mapLevel1.put("prop1", "value1");
        mapLevel1.put("prop2", mapLevel2);
        mapLevel1.put("prop3", "value3");

        Object result = inspector.inspect("prop2.prop13.targetIn", mapLevel1);
        assertNotNull("Result not found.", result);
        assertTrue( result instanceof TargetIn);
    }

    public void testInspectArray() throws PatternException, InspectorParserException {
        final int SIZE = 100;

        String[] array = new String[SIZE];
        for(int i = 0; i < array.length; i++) {
            array[i] = "a" + i;
        }

        Map map = new HashMap();
        map.put("array", array);

        for( int i = 0; i < array.length; i++) {
            Object result = inspector.inspect("array[" + i +"]", map);
            assertNotNull("Result not found.", result);
            assertEquals("Wrong result.", "a" + i, result);
        }

        try {
            inspector.inspect("array[" + SIZE + "]", map);
            fail("Unespected element.");
        } catch (PatternException pe) {}
    }

    public void testInspectList() throws PatternException, InspectorParserException {
        final int SIZE = 100;

        List list = new ArrayList();
        for(int i = 0; i < SIZE; i++) {
            list.add("element_" + i);
        }

        Map map = new HashMap();
        map.put("list", list);

        for( int i = 0; i < SIZE; i++) {
            Object result = inspector.inspect("list[" + i +"]", map);
            assertNotNull("Result not found.", result);
            assertEquals("Wrong result.", "element_" + i, result);
        }

        try {
            inspector.inspect("list[" + SIZE + "]", map);
            fail("Unespected element.");
        } catch (PatternException pe) {};
    }

    public void testDescribe() throws PatternException, InspectorParserException {
        inspector.addToContext( "target", new Target() );
        System.out.println( inspector.describe("target") );
    }

}
