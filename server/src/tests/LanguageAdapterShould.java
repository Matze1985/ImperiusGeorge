package tests;
import backend.LanguageAdapter;
import junit.framework.TestCase;


public class LanguageAdapterShould extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static class TestClass {
        public static boolean called = false;
        public static void call() { called = true; }
    }
    
    public void testStaticMethodVoidVoid() throws Exception {
        LanguageAdapter la = new LanguageAdapter();
        la.run("tests.LanguageAdapterShould$TestClass", "call", "[]");

        assertTrue(TestClass.called);
    }

}
