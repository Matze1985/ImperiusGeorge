package backend;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;


public class LanguageAdapter {
    private Map<String,Object> mStored = new HashMap<String,Object>();


    public void clear() { mStored.clear(); }

    public String run(String on, String method, String args) throws JSONException, IllegalAccessException, InvocationTargetException {
        log("running "+ method + " on "+ on + " with "+ args);
        Class<?> cl = findClass(on);
        if (cl != null) {
            log("static method");
            return runClassMethod(cl, args);
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

    private String runClassMethod(Class<?> cl, String argsString) throws JSONException, IllegalAccessException, InvocationTargetException {
        Method[] methods = cl.getDeclaredMethods();
        //JSONArray argsArray = new JSONArray(argsString);

        for (Method m : methods) {
            Class<?>[] argTypes = m.getParameterTypes();
            if (argTypes.length == 0) { //argsArray.length()) {

                try {
                    return adaptReturns(m.invoke(null, null)); //adaptArgs(argsArray, argTypes)));
                } catch (IllegalArgumentException e) { continue; }
            }
        }
        return null;
    }

    private String runInstanceMethod(Object o, String args) {
        return null;
    }

    private String adaptReturns(Object result) {
        return "" + result;
    }

    private Object adaptArgs(JSONArray argsArray, Class<?>[] argTypes) {
        return argsArray;
    }


    public static void log(String s) {
        System.out.println(s);
    }
}
