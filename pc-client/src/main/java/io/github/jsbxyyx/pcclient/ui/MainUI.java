package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.DateUtil;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.msg.TextMsgToType;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * @author
 * @since
 */
public class MainUI extends JFrame implements ActionListener, KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainUI.class);

    private JTextArea msgArea;
    private JScrollPane pane;
    private JTextField input;
    private JButton send;

    public MainUI() {
        setTitle(Global.getGroupName() + " - " + Global.getUsername());
        setSize(600, 400);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.warn("main ui closed...");
                ApplicationContext.shutdown();
            }
        });

        msgArea = new JTextArea(20, 48);
        msgArea.setEditable(false);
        pane = new JScrollPane(msgArea);
        add(pane);
        input = new JTextField(40);
        input.addKeyListener(this);
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
            sendMsg(input.getText());
        }
    }

    public void appendMsg(TextMsg textMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append(textMsg.getFrom())
                .append("\t")
                .append(DateUtil.format(textMsg.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                .append("\n")
                .append(textMsg.getText())
                .append("\n");

        msgArea.append(builder.toString());
    }

    public int getScrollValue() {
        return pane.getVerticalScrollBar().getMaximum();
    }

    public void scrollBottom(int oldValue) {
        pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMaximum());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getSource() == input) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                sendMsg(input.getText());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void sendMsg(String text) {
        if (!StringUtil.isBlank(text)) {
            TextMsg msg = new TextMsg();
            msg.setCreateTime(new Date());
            msg.setFrom(Global.getUsername());
            msg.setTo(Global.getGroup());
            msg.setToType(TextMsgToType.TO_TYPE_GROUP);
            msg.setText(text);
            ApplicationContext.appendMsg(msg);
            ApplicationContext.sendAsync(msg);
            input.setText("");
        }
    }
}
