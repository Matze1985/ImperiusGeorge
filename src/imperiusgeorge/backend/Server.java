package imperiusgeorge.backend;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

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
        } catch(Exception e) {
            System.out.println(exceptionToString(e));
            return new Response("Somebody fucked up :|\n" + exceptionToString(e));
        }
        return new Response("no dice grandma, params are:" + params);
    }

    public Response clean() {
        la.clear();
        return new Response("Cleaning successful!");
    }

    public Response execute(Map<String,String> params) throws Exception {

        String on = params.get("on");
        String method = params.get("method");
        String args = params.get("args");

        String res = la.run(on, method, args);

        return new Response(res);
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

}
