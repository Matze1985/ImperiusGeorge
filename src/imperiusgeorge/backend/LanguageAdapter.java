package imperiusgeorge.backend;

import imperiusgeorge.UIHelp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LanguageAdapter {
    private Map<String, Object> mStored = new HashMap<String, Object>();
    private List<String> mPackages = new ArrayList<String>();
    JSONParser mParser = new JSONParser();

    public LanguageAdapter() {
        mPackages.add("");
    }

    public void clear() {
        mStored.clear();
        UIHelp.clearLogs();
    }

    public String run(String on, String method, String argsString)
                    throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException, ParseException {
        Object instance = null;
        Method[] methods = null;

        JSONArray args = (JSONArray) mParser.parse(argsString);
        UIHelp.log("parsing args-string: "+argsString + " became: "+args);

        String parsedOn = (String) JSONValue.parse(on);
        if (parsedOn != null)
            on = parsedOn;

        Class<?> cl = findClass(on);
        if (method.equals("new")) {
            UIHelp.log("running constructor on class "+cl);
            return construct(cl,args);
        } else if (cl != null) {
            UIHelp.log("running method '"+method+"' on static class "+cl);
            methods = cl.getDeclaredMethods();
        } else if ((instance = mStored.get(on)) != null) {
            UIHelp.log("running method '"+method+"' on instance "+instance);
            methods = instance.getClass().getDeclaredMethods();
        } else { throw new ClassNotFoundException("Object/class '"+on+"' not found."); }


        for (Method m : methods) {
            Class<?>[] argTypes = m.getParameterTypes();
            if (m.getName().equalsIgnoreCase(method) && argTypes.length == args.size()) {
                try {
                    Object ret = m.invoke(instance, adaptArgs(args, argTypes));
                    return (m.getReturnType() == Void.TYPE)? "" : adaptReturn(ret);
                } catch (IllegalArgumentException e) { UIHelp.log("got ill:"+e); continue; }
            }
        }
        throw new NoSuchMethodError("Method "+method+" not found. instance="+instance+", class="+cl);
    }

    private Class<?> findClass(String string) {
        for(String pack : mPackages) {
            try {
                return Class.forName(pack + string);
            } catch (ClassNotFoundException e) { continue; }
        }
        return null;
    }

    private String adaptReturn(Object res) {
        if (res == null) { return ""; }
        UIHelp.log("returning "+res + " of "+res.getClass().getPackage());
        if (!res.getClass().toString().contains("java.lang")) {
            Object obj = res;
            res = "hash:"+res.hashCode();
            mStored.put((String)res, obj);
        }
        return JSONValue.toJSONString(res);
    }

    private String construct(Class<?> cl, List<?> args) throws InstantiationException, IllegalAccessException, InvocationTargetException{
        UIHelp.log("construct = "+cl);
        Constructor<?>[] constructors = cl.getDeclaredConstructors();

        for (Constructor<?> c : constructors) {
            Class<?>[] argTypes = c.getParameterTypes();
            if (argTypes.length == args.size()) {
                try {
                    return adaptReturn(c.newInstance(adaptArgs(args,argTypes)));
                } catch (IllegalArgumentException e) { UIHelp.log("got ill:"+e); continue; }
            }
        }

        return null;
    }

    private Object[] adaptArgs(List<?> args, Class<?>[] argTypes) {
        Object[] ret = new Object[args.size()];

        for (int i = 0; i < args.size(); i++) {
            // Primitives
            if (args.get(i) instanceof Number) {
                Number num = (Number) args.get(i);
                String paramName = argTypes[i].getSimpleName();
                if      (paramName.equals("int")) { ret[i] = num.intValue(); }
                else if (paramName.equals("long")) { ret[i] = num.longValue(); }
                else if (paramName.equals("float")) { ret[i] = num.floatValue(); }
                else if (paramName.equals("double")) { ret[i] = num.doubleValue(); }
                //if (Math.abs((ret[i] - num) < 0.0001) { throw new ArgumentError(); }
            } else if (args.get(i) instanceof Boolean) { ret[i] = args.get(i); }
            else { ret[i] = argTypes[i].cast(args.get(i)); }
        }
        return ret;
    }

    public boolean clear(String on) {
        return (null != mStored.remove(on));
    }

    public void setPackages(String packages) throws ParseException {
        JSONArray packs = (JSONArray) mParser.parse(packages);
        ArrayList<String> fixedPacks = new ArrayList<String>();
        for(Object pack : packs) {
            String curPack = (String) pack;
            if (curPack.charAt(curPack.length() - 1) != '.')
                curPack += ".";
            fixedPacks.add(curPack);
        }
        mPackages.addAll(fixedPacks);
    }
}
