import fi.iki.elonen.NanoHTTPD;

import com.google.gson.Gson;
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
        if(method == Method.GET && uri.equalsIgnoreCase("/clean")) {
            return clean();
        }

        return new Response("no dice grandma");
    }

    public Response clean() {
        return new Response("Cleaning successful!");
    }

    public Response execute(Map<String,String> params) {
        //Get this parameter shit to work
        //Do something about having persistent values
        //Do something about boolean return values
        try {
            String clas = "com.android.uiautomator.core." + params.get("class");
            Class c = Class.forName(clas);
            String meth = params.get("method");
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            java.lang.reflect.Method method = null;
            Class<?>[] parameterTypes = null;
            String argumentJson = params.get("arguments");
            Object[] args = new Gson().fromJson(argumentJson,Object[].class);
            for(java.lang.reflect.Method m : methods) {
                Class<?>[] pt = m.getParameterTypes();
                if(m.getName().equalsIgnoreCase(meth) && pt.length == args.length) {
                    method = m;
                    parameterTypes = pt;
                    break;
                }
            }

            for(int i = 0; i < args.length; i++) {
                //Dealing with primitives
                if(args[i] instanceof Double) {

                } else {
                    args[i] = parameterTypes[i].cast(args[i]);
                }
            }
            Object o = method.invoke(UiDevice.getInstance(),args);

            return new Response("Shits working.");
        } catch(Exception e) {
            System.out.println(e);
            return new Response("Somebody fucked up.");
        }
    }
}
