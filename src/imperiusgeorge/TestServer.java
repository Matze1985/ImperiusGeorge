package imperiusgeorge;

import imperiusgeorge.backend.Server;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class TestServer extends UiAutomatorTestCase {

    public void testAndroidServer() throws Exception {
        int port = 4242;
        try {
            port = Integer.parseInt((String) getParams().get("port"));
        } catch(NumberFormatException e) {}
        Server server = new Server(port);
        server.start();

        while(true)
            Thread.sleep(1000);
    }
}
