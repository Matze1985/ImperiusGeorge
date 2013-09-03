package tests;
import backend.LanguageAdapter;
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
    }
   
    public class OtherTestClass {
    	public boolean constructed = false;
    	OtherTestClass() { constructed = true; }
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
    }
    
    public void testStaticMethodReturnParam() throws Exception {
    	TestClass.called = false;
    	String res = la.run("tests.LanguageAdapterShould$TestClass","callAndResponse","[true]");

    	assertTrue(TestClass.called);
    	assertEquals("true",res);
    }

}
