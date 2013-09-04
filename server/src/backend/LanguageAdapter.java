package backend;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;


public class LanguageAdapter {
    private Map<String,Object> mStored = new HashMap<String,Object>();


    public void clear() { mStored.clear(); }

    public String run(String on, String method, String argsString) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Object instance = null;
        Method[] methods = null;
        Object[] args = new Gson().fromJson(argsString,Object[].class);

        Class<?> cl = findClass(on);
        if (cl != null) {
            log("running method "+method+" on static class "+cl);
            methods = cl.getDeclaredMethods();
        } else if ((instance = mStored.get(on)) != null) {
            log("running method "+method+" on instance "+instance);
            methods = instance.getClass().getDeclaredMethods();
        } else { throw new ClassNotFoundException("Object/class '"+on+"' not found."); }


        for (Method m : methods) {
            Class<?>[] argTypes = m.getParameterTypes();
            if (m.getName().equalsIgnoreCase(method) && argTypes.length == args.length) {
                try {
                    Object ret = m.invoke(instance, adaptArgs(args, argTypes));
                    return (m.getReturnType() == Void.TYPE)? "" : adaptReturn(ret);
                } catch (IllegalArgumentException e) { continue; }
            }
        }
        throw new NoSuchMethodError("Method "+method+" not found. instance="+instance+", class="+cl);
    }

    private Class<?> findClass(String string) {
        try {
            return Class.forName(string);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String adaptReturn(Object res) {
        log("returning "+res + " of "+res.getClass().getPackage());
        if (res != null && res.getClass().toString().contains("java.lang")) {
            return "" + res;
        } else {
            String hashcode = "hash:"+res.hashCode();
            mStored.put(hashcode, res);
            return hashcode;
        }
    }

    private Object[] adaptArgs(Object[] args, Class<?>[] argTypes) {
        for(int i = 0; i < args.length; i++) {
            //Primitives
            if(args[i] instanceof Double) {
                Double temp = (Double) args[i];
                String paramName = argTypes[i].getSimpleName();
                if (paramName.equals("int")) {
                    args[i] = temp.intValue();
                } else if (paramName.equals("long")) {
                    args[i] = temp.longValue();
                } else if (paramName.equals("float")) {
                    args[i] = temp.floatValue();
                }
            } else if (!(args[i] instanceof Boolean)){
                args[i] = argTypes[i].cast(args[i]);
            }
        }
        return args;
    }


    public static void log(String s) {
        System.out.println(s);
    }
}
