package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.DateUtil;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.msg.TextMsgToType;
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

        msgArea = new JTextArea(20, 48);
        msgArea.setEditable(false);
        add(new JScrollPane(msgArea));
        input = new JTextField(40);
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

                TextMsg msg = new TextMsg();
                msg.setCreateTime(new Date());
                msg.setFrom(Global.getUsername());
                msg.setTo(Global.getGroup());
                msg.setToType(TextMsgToType.TO_TYPE_GROUP);
                msg.setText(input.getText());

                appendMsg(msg);

                ApplicationContext.sendAsync(msg);

                input.setText("");
            }
        }
    }

    public void appendMsg(TextMsg textMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append(textMsg.getFrom())
                .append("    ")
                .append(DateUtil.format(textMsg.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                .append("\n")
                .append(textMsg.getText())
                .append("\n");

        msgArea.append(builder.toString());
    }
}
