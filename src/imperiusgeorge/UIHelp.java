package imperiusgeorge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.graphics.Rect;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class UIHelp {
    private static ArrayList<String> sLogMessages = new ArrayList<String>();

    /* ----- utility methods ----- */

    public static void log(String s) {
        sLogMessages.add(s);
        System.out.println(s);
    }

    public static String exportLogs() {
        return JSONValue.toJSONString(sLogMessages);
    }

    public static void clearLogs() {
        sLogMessages.clear();
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static String exceptionToString(Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        String ret = e.getClass().getSimpleName() + " :: " + e.getMessage() + "\n";
        for (int i = 0; i < Math.min(6, stack.length); i++) {
            ret += "   at " + stack[i].toString() + "\n";
        }
        if (e.getCause() != null) {
            ret += "Cause:" + exceptionToString(e.getCause());
        }
        return ret;
    }

    public static String getScreenDump() {
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("logs", UIHelp.exportLogs());
        resp.put("screenshot_location", getScreenshot());
        resp.put("activity_name", getActivityName());
        resp.put("pkg", getCurrentTopActivity());
        resp.put("text_views", getAllNamesOfClass("android.widget.TextView"));
        resp.put("buttons", getAllNamesOfClass("android.widget.Button"));
        return JSONObject.toJSONString(resp);
    }

    public static String getScreenshot() {
        try {
            String fname = "/sdcard/" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date()) + ".png";
            UiDevice.getInstance().takeScreenshot(new File(fname));
            return fname;
        } catch (NoSuchMethodError e) {
            log("can't take screenshot");
            return null;
        }
    }

    public static List<String> getAllNamesOfClass(String classname) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            try {
                UiObject obj = new UiObject(new UiSelector().className(classname).instance(i));
                if (!obj.exists()) {
                    break;
                }
                list.add(obj.getText());
            } catch (UiObjectNotFoundException e) {
                break;
            }
        }
        return list;
    }

    /* ----- public DOM interaction ----- */

    /** Click this item. Fails with lots of debug output. */
    public static void click(UiObject item) {
        if (item.exists()) {
            try {
                item.click();
            } catch (Exception e) {
                fail("click failed");
            }
        } else {
            fail("click failed");
        }
    }

    /** Click this item. Fails with lots of debug output. */
    public static boolean clickAndWaitForNewWindow(UiObject item) {
        if (item.exists()) {
            try {
                return item.clickAndWaitForNewWindow();
            } catch (Exception e) {
                fail("click failed");
            }
        } else {
            fail("click failed");
        }
        return false;
    }

    /** Click this item. Fails with lots of debug output. */
    public static boolean clickAndWaitForNewWindow(UiObject item, long timeout) {
        if (item.exists()) {
            try {
                return item.clickAndWaitForNewWindow(timeout);
            } catch (Exception e) {
                fail("click failed");
            }
        } else {
            fail("click failed");
        }
        return false;
    }

    /** Click this item. Fails with lots of debug output. */
    public static void clickAndWaitForNewWindow(String viewText) {
        UiObject item = find(viewText);
        if (!item.exists()) {
            log("waiting for " + viewText + " to exist..");
            item.waitForExists(2000);
        }
        clickAndWaitForNewWindow(item);
    }

    /** Waits for this text to appear in a view. Fails with lots of debug output. */
    public static void waitUntilViewWithExactTextExists(String text, long timeout) {
        boolean exists = new UiObject(new UiSelector().text(text)).waitForExists(timeout);
        if (!exists) {
            fail((timeout / 1000) + "s timeout exceeded waiting for: " + text);
        }
    }

    /** Waits for this description to appear in a view. Fails with lots of debug output. */
    public static void waitUntilViewWithExactDescriptionExists(String description, long timeout) {
        boolean exists = new UiObject(new UiSelector().description(description)).waitForExists(timeout);
        if (!exists) {
            fail((timeout / 1000) + "s timeout exceeded waiting for: " + description);
        }
    }

    public static String waitUntilExactTextExists(String[] texts, long timeout) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeout) {
            for (String s : texts) {
                UiObject obj = find(s);
                if ((obj != null) && obj.exists()) {
                    return s;
                }
            }
        }
        fail((timeout / 1000) + "s timeout exceeded waiting for: " + Arrays.toString(texts));
        return null;
    }

    /** Waits for this text to appear in a view. Fails with lots of debug output. */
    public static void waitUntilViewWithPartialTextExists(String text, long timeout) {
        boolean exists = new UiObject(new UiSelector().textContains(text)).waitForExists(timeout);
        if (!exists) {
            fail((timeout / 1000) + "s timeout exceeded waiting for: " + text);
        }
    }

    public static UiScrollable getScrollView() {
        UiScrollable settingsItem = new UiScrollable(new UiSelector().className("android.widget.ListView"));
        try {
            settingsItem.setMaxSearchSwipes(10);
        } catch (NoSuchMethodError e) {
            log("setMaxSearchSwipes() not supported!!??");
        }
        return settingsItem;
    }

    public static void openNotificationBar() {
        UiDevice.getInstance().openNotification();
    }

    /* ----- other useful DOM interaction ----- */

    public static boolean hasViewWithExactText(String text) {
        return (new UiObject(new UiSelector().text(text)).exists());
    }

    /** Click and wait for new window if this text item exists */
    public static boolean clickIfExactExists(String text) {
        return clickIfExactExists(text, 1000, true);
    }

    public static boolean clickIfExactExists(String text, int timeout, boolean waitForNewWindow) {
        UiObject clickme = find(text);
        clickme.waitForExists(timeout);
        try {
            if (clickme.exists()) {
                if (waitForNewWindow) {
                    clickme.clickAndWaitForNewWindow();
                } else {
                    clickme.click();
                }
                return true;
            }
        } catch (UiObjectNotFoundException e) {
        }
        return false;
    }

    public static UiObject findClass(String type) {
        return new UiObject(new UiSelector().className(type));
    }

    public static UiObject find(String text) {
        return new UiObject(new UiSelector().text(text));
    }

    public static UiObject findViewThatContains(String text) {
        return new UiObject(new UiSelector().textContains(text));
    }

    public static UiObject findViewThatStartsWith(String text) {
        return new UiObject(new UiSelector().textStartsWith(text));
    }

    public static UiObject findViaDescription(String text) {
        return new UiObject(new UiSelector().description(text));
    }

    public static UiObject findViaResourceId(String id) {
        return new UiObject(new UiSelector().resourceId(id));
    }

    public static UiObject findItemInList(String text) throws UiObjectNotFoundException {
        UiScrollable settingsItem = getScrollView();
        return settingsItem.getChildByText(new UiSelector().className("android.widget.LinearLayout"), text);
    }

    public static UiObject getSubItem(UiObject parent, String uiClass) throws UiObjectNotFoundException {
        return parent.getChild(new UiSelector().className(uiClass));
    }

    public static void enterText(int numberInView, String text) throws UiObjectNotFoundException {
        UiObject textObject = new UiObject(new UiSelector().className("android.widget.EditText").instance(numberInView));
        textObject.setText(text);
    }

    public static String clickBiggestButton() {
        UiObject biggest = null;
        int biggestSize = 0;
        for (int i = 0; i < 20; i++) {
            try {
                UiObject obj = new UiObject(new UiSelector().className("android.widget.Button").instance(i));
                if (!obj.exists()) {
                    break;
                }
                Rect bounds = obj.getBounds();
                int size = bounds.width() * bounds.height();
                if (size > biggestSize) {
                    biggestSize = size;
                    biggest = obj;
                }
            } catch (UiObjectNotFoundException e) {
                break;
            }
        }
        if ((biggest != null) && biggest.exists()) {
            try {
                String text = biggest.getText();
                clickAndWaitForNewWindow(biggest);
                return text;
            } catch (UiObjectNotFoundException e) {
            }
        }
        return null;
    }

    public static String clickOne(String[] texts) {
        for (String s : texts) {
            UiObject obj = find(s);
            if (obj.exists()) {
                if (!clickAndWaitForNewWindow(obj)) {
                    throw new IllegalStateException("Button exists but can't click! texts:" + Arrays.toString(texts));
                }
                return s;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static String getActivityName() {
        return UiDevice.getInstance().getCurrentActivityName();
    }

    public static String getDeviceName() throws IOException {
        try {
            return UiDevice.getInstance().getProductName();
        } catch (Error e) {
        }
        return shell("getprop ro.product.name").trim();
    }

    public static String getCurrentTopActivity() {
        try {
            String all = shell("dumpsys window windows");
            String line = matchOnce("mCurrentFocus=Window\\{([^}]+)\\}", all);
            return matchOnce("\\w+(?: u\\d)? ([\\w./]+)", line);
        } catch (IOException e) {
            return "";
        }
    }

    public static String matchOnce(String regex, String text) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return (m.find() ? m.group(1) : "");
    }

    public static String shell(String args) throws IOException {
        Process process = Runtime.getRuntime().exec(args);
        return readFully(process.getInputStream());
    }

    public static String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        inputStream.close();
        return new String(baos.toByteArray());
    }

    public static float map(float value, float fromSource, float toSource, float fromTarget, float toTarget) {
        return (((value - fromSource) / (toSource - fromSource)) * (toTarget - fromTarget)) + fromTarget;
    }

    /** Swipes relative to the device screen size (0,0 top left, 1,1 bottom right) */
    public static boolean swipeRelative(float x, float y, float xend, float yend) {
        UiDevice d = UiDevice.getInstance();
        return d.swipe((int) map(x, 0, 1, 0, d.getDisplayWidth()), (int) map(y, 0, 1, 0, d.getDisplayHeight()),
                        (int) map(xend, 0, 1, 0, d.getDisplayWidth()), (int) map(yend, 0, 1, 0, d.getDisplayHeight()),
                        10);
    }

}
