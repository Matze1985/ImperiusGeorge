import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class TestServer extends UiAutomatorTestCase {

    public void testAndroidServer() throws Exception {
        Server server = new Server();
        server.start();

        while(true)
            Thread.sleep(1000);
    }
}
