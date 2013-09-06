package imperiusgeorge.backend;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD {

    private LanguageAdapter la = new LanguageAdapter();

    public Server(int port) {
        super(port);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        try {
            if (uri.equalsIgnoreCase("/execute")) { return execute(params); }
            else if(method == Method.GET && uri.equalsIgnoreCase("/clean")) { return clean(); }
            else if(method == Method.POST && uri.equalsIgnoreCase("/packages")) { return packages(params); }
        } catch(Exception e) {
            String exceptionString = exceptionToString(e);
            System.out.println(exceptionString);
            String exceptionJSON = exceptionToJSON(exceptionString,la.exportLogs());
            return new Response(Status.NOT_ACCEPTABLE,"application/json",exceptionJSON);
        }
        return new Response("no dice grandma, params are:" + params);
    }

    public Response packages(Map<String, String> params) throws ParseException {
        la.setPackages(params.get("packages"));

        return new Response(Status.OK,"text/plain","Packages set!");
    }

    public Response clean() {
        la.clear();
        return new Response(Status.OK,"text/plain","Cleaning successful!");
    }

    public Response execute(Map<String,String> params) throws Exception {
        String on = params.get("on");
        String method = params.get("method");
        String args = params.get("args");

        String res = la.run(on, method, args);

        return new Response(Status.OK,"text/plain",res);
    }

    public static String exceptionToString(Throwable e) {
        String ret = "";
        StackTraceElement[] stack = e.getStackTrace();
        ret += "Exception: " + e.getMessage() + " at "+ stack[0];
        for (int i=1; i < Math.min(6, stack.length); i++) {
            ret += "   at "+stack[i].toString();
        }
        if (e.getCause() != null) { ret += "Cause:" + exceptionToString(e.getCause()); }
        return ret;
    }

    public String exceptionToJSON(String exceptionString,String logString) {
        Map<String,String> resp = new HashMap<String,String>();
        resp.put("exception", exceptionString);
        resp.put("logs", la.exportLogs());
        return JSONObject.toJSONString(resp);
    }
}
