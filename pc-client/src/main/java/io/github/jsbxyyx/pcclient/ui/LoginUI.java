package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.ErrorMsg;
import io.github.jsbxyyx.msg.LoginRequestMsg;
import io.github.jsbxyyx.msg.LoginResponseMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.StatusCode;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.Global;
import io.github.jsbxyyx.pcclient.service.HeartbeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * @author
 * @since
 */
public class LoginUI extends JFrame implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUI.class);

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public LoginUI() {
        setTitle("登录");
        setSize(300, 120);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.warn("login ui closed...");
                ApplicationContext.shutdown();
            }
        });

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

            String username = tfUsername.getText();
            String password = new String(pfPassword.getPassword());
            if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
                JOptionPane.showMessageDialog(null, "用户名密码不能为空");
                return ;
            }

            LoginRequestMsg msg = new LoginRequestMsg();
            msg.setUsername(username);
            msg.setPassword(password);
            Msg rsp = ApplicationContext.sendSync(msg);
            String statusCode = rsp.getStatusCode();
            if (!Objects.equals(statusCode, StatusCode.OK.code)) {
                ErrorMsg error = (ErrorMsg) rsp.getBody();
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

            this.dispose();

            HeartbeatService.startHeartbeat();

            MainUI mainUI = new MainUI();
            ApplicationContext.setMainUI(mainUI);
            mainUI.launch();
        }
    }
}
