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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test case for {@link com.asemantics.rdfcoder.inspector.Inspector} class.
 */
public class InspectorTest {

    private Inspector inspector;

    @Before
    public void setUp() {
        inspector = new Inspector();
    }

    @After
    public void tearDown() throws Exception {
        inspector = null;
    }

    /**
     * Tests the ability of reading a sinple property.
     *
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testReadSimpleProperty() throws PatternException, InspectorParserException {
        Target target = new Target();

        Object result1 = inspector.inspect("str", target);
        Assert.assertNotNull("Result not found.", result1);
        Assert.assertTrue("Invalid result type.", result1 instanceof String);
        Assert.assertEquals( "Invalid result value", "str", result1);

        Object result2 = inspector.inspect("condition", target);
        Assert.assertNotNull("Result not found.", result2);
        Assert.assertTrue("Invalid result type.", result2 instanceof Boolean);
        Assert.assertEquals( "Invalid result value", true, result2);

        Object result3 = inspector.inspect("value", target);
        Assert.assertNotNull("Result not found.", result3);
        Assert.assertTrue("Invalid result type.", result3 instanceof Integer);
        Assert.assertEquals( "Invalid result value", 100, result3);

        Object result4 = inspector.inspect("targetIn", target);
        Assert.assertNotNull("Result not found.", result4);
        Assert.assertTrue("Invalid result type.", result4 instanceof TargetIn );
    }

    /**
     * Tests the ability of reading a map property.
     *
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testReadMapProperty() throws PatternException, InspectorParserException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("prop1", "val1");
        map.put("prop2", "val2");
        map.put("prop3", "val3");

        Object result1 = inspector.inspect("prop1", map);
        Assert.assertNotNull("Result not found.", result1);
        Assert.assertEquals("val1", result1);

        Object result2 = inspector.inspect("prop2", map);
        Assert.assertNotNull("Result not found.", result2);
        Assert.assertEquals("val2", result2);

        Object result3 = inspector.inspect("prop3", map);
        Assert.assertNotNull("Result not found.", result3);
        Assert.assertEquals("val3", result3);
    }

    /**
     * Tests the ability of reading a sequence of accessors. 
     *
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testReadSequence() throws PatternException, InspectorParserException {
        Target target = new Target();

        Map<String,Object> mapLevel2 = new HashMap<String,Object>();
        mapLevel2.put("prop11", "value11");
        mapLevel2.put("prop12", "value12");
        mapLevel2.put("prop13", target);

        Map<String,Object> mapLevel1 = new HashMap<String,Object>();
        mapLevel1.put("prop1", "value1");
        mapLevel1.put("prop2", mapLevel2);
        mapLevel1.put("prop3", "value3");

        Object result = inspector.inspect("prop2.prop13.targetIn", mapLevel1);
        Assert.assertNotNull("Result not found.", result);
        Assert.assertTrue( result instanceof TargetIn);
    }

    /**
     * Tests the ability of reading an array.
     *
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testInspectArray() throws PatternException, InspectorParserException {
        final int SIZE = 100;

        String[] array = new String[SIZE];
        for(int i = 0; i < array.length; i++) {
            array[i] = "a" + i;
        }

        Map<String,String[]> map = new HashMap<String,String[]>();
        map.put("array", array);

        for( int i = 0; i < array.length; i++) {
            Object result = inspector.inspect("array[" + i +"]", map);
            Assert.assertNotNull("Result not found.", result);
            Assert.assertEquals("Wrong result.", "a" + i, result);
        }

        try {
            inspector.inspect("array[" + SIZE + "]", map);
            Assert.fail("Unespected element.");
        } catch (PatternException pe) {}
    }

    /**
     * Tests the ability of reading a list.
     * 
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testInspectList() throws PatternException, InspectorParserException {
        final int SIZE = 100;

        List<String> list = new ArrayList<String>();
        for(int i = 0; i < SIZE; i++) {
            list.add("element_" + i);
        }

        Map<String,List<String>> map = new HashMap<String,List<String>>();
        map.put("list", list);

        for( int i = 0; i < SIZE; i++) {
            Object result = inspector.inspect("list[" + i +"]", map);
            Assert.assertNotNull("Result not found.", result);
            Assert.assertEquals("Wrong result.", "element_" + i, result);
        }

        try {
            inspector.inspect("list[" + SIZE + "]", map);
            Assert.fail("Unespected element.");
        } catch (PatternException pe) {}
    }

    /**
     * Tests the describe {@link com.asemantics.rdfcoder.inspector.Inspector#describe(String)}  method.
     * 
     * @throws PatternException
     * @throws InspectorParserException
     */
    @Test
    public void testDescribe() throws PatternException, InspectorParserException {
        inspector.addToContext( "target", new Target() );
        String description = inspector.describe("target");
        Assert.assertNotNull("Expected a valid description.", description);
        String[] expected = new String[]{"str", "condition", "value", "targetIn"};
        for(String e : expected) {
            Assert.assertTrue("Cannot find an expected part of description.", description.contains(e) );
        }
    }

    /**
     * Test target referenced in {@link com.asemantics.rdfcoder.inspector.InspectorTest.Target}.
     */
    class TargetIn {}

    /**
     * Test target.
     */
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

}
