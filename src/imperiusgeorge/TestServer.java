package imperiusgeorge;

import imperiusgeorge.backend.Server;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class TestServer extends UiAutomatorTestCase {

    public void testAndroidServer() throws Exception {
        int port = Integer.parseInt(getParams().getString("port", "4242"));
        UIHelp.log("Starting server on port "+port);
        Server server = new Server(port);
        server.start();

        while(true)
            Thread.sleep(1000);
    }
}
