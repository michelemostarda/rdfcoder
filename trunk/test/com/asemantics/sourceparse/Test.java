package com.asemantics.sourceparse;

/**
 * This class is use to test the parser.
 */

enum PreEnum {
    A, B, C, X;
    int f1;
    int f2;
}

interface PreClass {
    void m1();

}

class Exc1 extends Exception {

}

class Exc2 extends Exception {

}

public class Test {

    private static int i;

    private enum EnumA {
        AAA,
        BBB,
        CCC;

        int a;
        int b;

        public void m1() {}
    }

    enum EnumB {
        AAA,
        BBB,
        CCC
    }

    public class Inner {

        public class InnerInner {
            int ii;
        }

        int i;
    }

    public Test() {
        // Constructor.
    }

    public void mExc() throws Exc1, Exc2 {}

    protected int m1(String string1, String string2, int int1, int int2) {
        return 0;
    }

}

interface InternalInterface {

    int v = 0;
}

enum Post {
    A, B, C, D
}

interface PostInterface {
    int m();
}

