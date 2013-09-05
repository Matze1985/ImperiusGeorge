package imperiusgeorge.backend.tests;
import imperiusgeorge.backend.LanguageAdapter;
import junit.framework.TestCase;


public class LanguageAdapterShould extends TestCase {

    private LanguageAdapter la;

    protected void setUp() throws Exception {
        super.setUp();
        la = new LanguageAdapter();
    }

    public static class TestClass {
        public static boolean called = false;
        public static void call() { called = true; }
        public static void call(boolean bool) { called = bool; }
        public static boolean callAndResponse(boolean bool) { called = bool; return called; }
        public static String callReturnString() { return "hello world"; }
        public static int callReturnInt() { return 42; }
        public static double callReturnDouble() { return 3.14; }
        public static long callReturnLong() { return 10000L; }
        public static TestClass callReturnObj() { return new TestClass(); }
        public int instanceMethod() { return 45; }
    }

    public static class OtherTestClass {
    	public boolean constructed = false;
    	public boolean called  = false;
    	public OtherTestClass() { constructed = true; }
    	public boolean isConstructed() { return constructed; }
    	public void setCalled(boolean called) { this.called = called; }
    	public String getCalled(String arbitraryParam) { return arbitraryParam + called; }
    }

    public void testStaticMethodVoidVoid() throws Exception {
        TestClass.called = false;
        la.run("tests.LanguageAdapterShould$TestClass", "call", "[]");

        assertTrue(TestClass.called);
    }

    public void testStaticMethodVoidParam() throws Exception {
        TestClass.called = false;
        la.run("tests.LanguageAdapterShould$TestClass","call","[true]");
        assertTrue(TestClass.called);
        la.run("tests.LanguageAdapterShould$TestClass","call","[false]");
        assertFalse(TestClass.called);
    }

    public void testStaticMethodReturnParam() throws Exception {
        TestClass.called = false;
        String res = la.run("tests.LanguageAdapterShould$TestClass","callAndResponse","[true]");

        assertTrue(TestClass.called);
        assertEquals("true",res);
    }

    public void testMethodReturns() throws Exception {
        assertEquals("", la.run("tests.LanguageAdapterShould$TestClass", "call", "[]"));
        assertEquals("hello world", la.run("tests.LanguageAdapterShould$TestClass", "callReturnString", "[]"));
        assertEquals("42", la.run("tests.LanguageAdapterShould$TestClass", "callReturnInt", "[]"));
        assertEquals("3.14", la.run("tests.LanguageAdapterShould$TestClass", "callReturnDouble", "[]"));
        assertEquals("10000", la.run("tests.LanguageAdapterShould$TestClass", "callReturnLong", "[]"));
    }


    public void testInstanceReturn() throws Exception {
        String ret = la.run("tests.LanguageAdapterShould$TestClass", "callReturnObj", "[]");
        String ret2 = la.run(ret, "instanceMethod", "[]");
        assertEquals("45", ret2);
    }

    public void testConstructInstance() throws Exception {
    	String hash = la.run("tests.LanguageAdapterShould$OtherTestClass", "new","[]");
    	String res = la.run(hash,"isConstructed","[]");

    	assertEquals("true",res);

    }

    public void testInstanceReturnParameter() throws Exception {
        String tag = "RESULT OF CALLED: ";

        String hash = la.run("tests.LanguageAdapterShould$OtherTestClass", "new", "[]");
        la.run(hash,"setCalled","[true]");
        String res = la.run(hash,"getCalled","[\"" + tag + "\"]");

        assertEquals(tag + "true",res);
    }
}
