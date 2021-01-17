package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.msg.LoginRequestMsg;
import io.github.jsbxyyx.msg.LoginResponseMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.ServiceErrorMsg;
import io.github.jsbxyyx.msg.StatusCode;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @since
 */
public class LoginUI extends JDialog implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUI.class);

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public LoginUI() {
        setTitle("登录");
        setSize(300, 200);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLayout(new FlowLayout());

        add(new JLabel("用户名"));
        tfUsername = new JTextField(20);
        add(tfUsername);

        add(new JLabel("密码"));
        pfPassword = new JPasswordField(20);
        add(pfPassword);

        btnLogin = new JButton("登录");
        btnLogin.addActionListener(this);
        btnLogin.setActionCommand("LOGIN");
        add(btnLogin);

    }

    public void launch() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equalsIgnoreCase("LOGIN")) {

            LoginRequestMsg msg = new LoginRequestMsg();
            msg.setUsername(tfUsername.getText());
            msg.setPassword(new String(pfPassword.getPassword()));
            Msg rsp = ApplicationContext.sendSync(msg);
            String statusCode = rsp.getStatusCode();
            if (!Objects.equals(statusCode, StatusCode.OK.code)) {
                ServiceErrorMsg error = (ServiceErrorMsg) rsp.getBody();
                JOptionPane.showMessageDialog(null, error.getMessage());
                return ;
            }
            LoginResponseMsg body = (LoginResponseMsg) rsp.getBody();
            Global.setUsername(msg.getUsername());
            Global.setToken(body.getToken());
            byte[] aesKey = body.getAesKey();
            Global.putKey(aesKey);
            Global.setGroup(body.getGroup());
            Global.setGroupName(body.getGroupName());

            startHeartbeat();

            this.dispose();

            new MainUI().launch();
        }
    }

    private void startHeartbeat() {
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    HeartbeatMsg msg = new HeartbeatMsg();
                    msg.setPing(true);
                    ApplicationContext.sendAsync(msg);
                } catch (Exception e) {
                    LOGGER.error("heartbeat failed.", e);
                }
            }
        }, 0, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
