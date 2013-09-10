package imperiusgeorge.backend;
import imperiusgeorge.UIHelp;

import java.util.Map;

import junit.framework.AssertionFailedError;

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
            else if (method == Method.GET && uri.equalsIgnoreCase("/gc")) { return gc(params); }
            else if (method == Method.GET && uri.equalsIgnoreCase("/dump")) { return screenDump(); }
            else if (method == Method.GET && uri.equalsIgnoreCase("/terminate")) { return terminate(); }
            else if (method == Method.GET && uri.equalsIgnoreCase("/version")) { return version(); }
        } catch (Throwable e) {
            if (e.getCause() instanceof AssertionFailedError) { e = e.getCause(); }
            UIHelp.log("Reporting exception: " + UIHelp.exceptionToString(e));
            e.printStackTrace();
            return new Response(Status.NOT_ACCEPTABLE,"text/plain", UIHelp.exceptionToString(e));
        }
        return new Response("no dice grandma, params are:" + params);
    }

    private Response version() {
        return new Response(Status.OK,"text/plain","1.0.0");
    }

    private Response terminate() {
        this.stop();
        return new Response(Status.OK,"text/plain", "Terminating succcessful!");
    }

    public Response packages(Map<String, String> params) throws ParseException {
        la.setPackages(params.get("packages"));

        return new Response(Status.OK,"text/plain","Packages set!");
    }

    public Response clean() {
        la.clear();
        return new Response(Status.OK,"text/plain","Cleaning successful!");
    }

    public Response screenDump() {
        UIHelp.log("Creating dump of views");
        return new Response(Status.OK,"application/json", UIHelp.getScreenDump());
    }

    public Response execute(Map<String,String> params) throws Exception {
        String on = params.get("on");
        String method = params.get("method");
        String args = params.get("args");

        String res = la.run(on, method, args);

        return new Response(Status.OK,"text/plain",res);
    }

    public Response gc(Map<String,String> params) throws Exception {
        String on = params.get("on");
        String res = la.clear(on)? "sucess" : "no found!";
        return new Response(Status.OK,"text/plain",res);
    }
}
