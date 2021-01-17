package io.github.jsbxyyx.pcclient.ui;

import org.junit.Test;

/**
 * @author
 * @since
 */
public class UITest {


    @Test
    public void test_main_ui() throws Exception {
        new MainUI().launch();
        Thread.sleep(60000);
    }

    @Test
    public void test_login_ui() throws Exception {
        new LoginUI().launch();
        Thread.sleep(60000);
    }



}
