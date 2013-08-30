import java.util.Map;

import com.android.uiautomator.core.UiDevice;
import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {

    public Server() {
        super(7120);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params,Map<String, String> files) {
        try {
            if (uri.equalsIgnoreCase("/execute")) { return execute(params); }
            else if(method == Method.GET && uri.equalsIgnoreCase("/clean")) { return clean(); }
        } catch(Exception e) {
            System.out.println(exceptionToString(e));
            return new Response("Somebody fucked up :|\n" + exceptionToString(e));
        }
        return new Response("no dice grandma");
    }

    public Response clean() {
        return new Response("Cleaning successful!");
    }

    public Response execute(Map<String,String> params) throws Exception {

        //Get this parameter shit to work
        //Do something about having persistent values
        //Do something about boolean return values
        String clas = "com.android.uiautomator.core." + params.get("class");
        Class<?> c = Class.forName(clas);
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
                Double temp = (Double) args[i];
                String paramName = parameterTypes[i].getSimpleName();
                if(paramName.equals("int")) {
                    args[i] = temp.intValue();
                } else if (paramName.equals("long")) {
                    args[i] = temp.longValue();
                } else if (paramName.equals("float")) {
                    args[i] = temp.floatValue();
                } else {}
            } else {
                args[i] = parameterTypes[i].cast(args[i]);
            }
        }
        Object o = method.invoke(UiDevice.getInstance(),args);

        return new Response("Shits working.");
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
