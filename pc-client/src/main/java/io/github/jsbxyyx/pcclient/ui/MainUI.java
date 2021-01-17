package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.DateUtil;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.Global;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * @author
 * @since
 */
public class MainUI extends JFrame implements ActionListener {

    private JTextArea msgArea;
    private JTextField input;
    private JButton send;

    public MainUI() {
        setTitle(Global.getGroupName() + " - " + Global.getUsername());
        setSize(600, 400);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ApplicationContext.shutdown();
            }
        });

        msgArea = new JTextArea(200, 560);
        add(new JScrollPane(msgArea));
        input = new JTextField(500);
        add(input);

        send = new JButton("发送");
        send.setActionCommand("SEND");
        send.addActionListener(this);
        add(send);
    }

    public void launch() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equalsIgnoreCase("SEND")) {
            if (!StringUtil.isBlank(input.getText())) {
                StringBuilder builder = new StringBuilder();
                Date date = new Date();
                builder.append(Global.getUsername())
                        .append("    ")
                        .append(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"))
                        .append("\n")
                        .append(input.getText())
                        .append("\n");

                msgArea.append(builder.toString());
                input.setText("");

                TextMsg msg = new TextMsg();
                msg.setCreateTime(date);
                msg.setFrom(Global.getUsername());
                msg.setTo(Global.getGroup());
                msg.setToType("1");
                msg.setText(input.getText());
                ApplicationContext.sendAsync(msg);
            }
        }
    }
}
