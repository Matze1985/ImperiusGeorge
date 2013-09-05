package imperiusgeorge.backend.tests;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import imperiusgeorge.backend.Server;
import junit.framework.TestCase;

public class ServerShould extends TestCase {

    private Server server;

    protected void setUp() throws Exception {
        super.setUp();
        server = new Server(4242);
    }

    public static class ExceptionThrower {
        public static void throwException() { throw new RuntimeException(); }
    }

    public void testExceptionHandling() throws Exception {
        Map<String,String> headers = new HashMap<String,String>();
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String> files = new HashMap<String,String>();

        params.put("on", "imperiusgeorge.backend.tests.ServerShould$ExceptionThrower");
        params.put("method", "throwException");
        params.put("args", "[]");

        Response res = server.serve("/execute", Method.POST, headers, params, files);

        assertEquals(Status.NOT_ACCEPTABLE,res.getStatus());
        assertEquals("application/json",res.getMimeType());
    }
}