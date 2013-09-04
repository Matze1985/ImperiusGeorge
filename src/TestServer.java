import backend.Server;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class TestServer extends UiAutomatorTestCase {

    public void testAndroidServer() throws Exception {
        int port = getParams().getInt("port", 7120);
        Server server = new Server(port);
        server.start();

        while(true)
            Thread.sleep(1000);
    }
}
