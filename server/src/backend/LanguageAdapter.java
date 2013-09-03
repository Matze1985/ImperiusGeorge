package backend;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;


public class LanguageAdapter {
    private Map<String,Object> mStored = new HashMap<String,Object>();


    public void clear() { mStored.clear(); }

    public String run(String on, String method, String args) throws JSONException, IllegalAccessException, InvocationTargetException {
        log("running "+ method + " on "+ on + " with "+ args);
        Class<?> cl = findClass(on);
        if (cl != null) {
            log("static method");
            return runClassMethod(cl, method, args);
        }

        return null;
    }

    private Class<?> findClass(String string) {
        try {
            return Class.forName(string);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String runClassMethod(Class<?> cl, String method, String argsString) throws JSONException, IllegalAccessException, InvocationTargetException {
        Method[] methods = cl.getDeclaredMethods();
        Object[] args = new Gson().fromJson(argsString,Object[].class);

        for (Method m : methods) {
            Class<?>[] argTypes = m.getParameterTypes();
            if (m.getName().equalsIgnoreCase(method) && argTypes.length == args.length) {
                try {
                    return adaptReturn(m.invoke(null, adaptArgs(args, argTypes)));
                } catch (IllegalArgumentException e) { continue; }
            }
        }
        return null;
    }

    private String runInstanceMethod(Object o, String args) {
        return null;
    }

    private String adaptReturn(Object result) {
        return "" + result;
    }

    private Object[] adaptArgs(Object[] args, Class<?>[] argTypes) {
    	for(int i = 0; i < args.length; i++) {
    		//Primitives
    		if(args[i] instanceof Double) {
    			Double temp = (Double) args[i];
    			String paramName = argTypes[i].getSimpleName();
    			if(paramName.equals("int")) {
    				args[i] = temp.intValue();
    			} else if(paramName.equals("long")) {
    				args[i] = temp.longValue();
    			} else if(paramName.equals("float")) {
    				args[i] = temp.floatValue();
    			}
    		} else if(!(args[i] instanceof Boolean)){
    			args[i] = argTypes[i].cast(args[i]);
    		}
    	}
        return args;
    }


    public static void log(String s) {
        System.out.println(s);
    }
}
