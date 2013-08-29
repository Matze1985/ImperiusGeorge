import fi.iki.elonen.NanoHTTPD;

import java.util.Map;
import com.android.uiautomator.core.UiDevice;

public class Server extends NanoHTTPD {

    public Server() {
        super(7120);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params,Map<String, String> files) {
        if(/*method == Method.POST && */uri.equalsIgnoreCase("/execute")) {
            return execute(params);
        }

        return new Response("no dice grandma");
    }

    public Response execute(Map<String,String> params) {
        try {
            String clas = "com.android.uiautomator.core." + params.get("class");
            String method = params.get("method");
            Class c = Class.forName(clas);
            c.getDeclaredMethod(method).invoke(UiDevice.getInstance());
            return new Response("Shits working.");
        } catch(Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            return new Response("Somebody fucked up.");
        }
    }
}
