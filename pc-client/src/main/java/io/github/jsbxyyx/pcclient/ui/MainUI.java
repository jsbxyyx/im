package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.DateUtil;
import io.github.jsbxyyx.common.IoUtil;
import io.github.jsbxyyx.common.StringUtil;
import io.github.jsbxyyx.msg.*;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @since
 */
public class MainUI extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainUI.class);

    private JPanel msgArea;
    private JScrollPane sp_pane;
    private JButton image;
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

        msgArea = new JPanel();
        msgArea.setLayout(new BoxLayout(msgArea, BoxLayout.Y_AXIS));
        sp_pane = new JScrollPane();
        sp_pane.setPreferredSize(new Dimension(580, 300));
        sp_pane.setViewportView(msgArea);
        getContentPane().add(sp_pane);

        image = new JButton("图片");
        image.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new FileNameExtensionFilter("图片", "jpg", "jpeg", "png", "gif"));
                int returnVal = fc.showOpenDialog(msgArea);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (!IoUtil.checkImage(file)) {
                        JOptionPane.showMessageDialog(null, "请选择图片");
                        return;
                    }
                    byte[] bytes = IoUtil.readFile(file);
                    String image = Base64.getEncoder().encodeToString(bytes);
                    sendImageMsg(image);
                }
            }
        });
        add(image);

        input = new JTextField(40);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getSource() == input) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendTextMsg(input.getText());
                    }
                }
            }
        });
        KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
        input.registerKeyboardAction(new CombinedAction(input.getActionForKeyStroke(ctrlV),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                if (contents == null) {
                    return;
                }
                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        input.setText((String) contents.getTransferData(DataFlavor.stringFlavor));
                    } catch (UnsupportedFlavorException ex) {
                    } catch (IOException ex) {
                    }
                } else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    try {
                        Image image = (Image) contents.getTransferData(DataFlavor.imageFlavor);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
                        ImageIO.write((RenderedImage) image, "png", baos);
                        sendImageMsg(Base64.getEncoder().encodeToString(baos.toByteArray()));
                    } catch (UnsupportedFlavorException ex) {
                    } catch (IOException ex) {
                    }
                } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> fileList = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
                        if (fileList != null && !fileList.isEmpty()) {
                            File file = fileList.get(0);
                            if (IoUtil.checkImage(file)) {
                                sendImageMsg(Base64.getEncoder().encodeToString(IoUtil.readFile(file)));
                            }
                        }
                    } catch (UnsupportedFlavorException ex) {
                    } catch (IOException ex) {
                    }
                }
            }
        }), ctrlV, JComponent.WHEN_FOCUSED);
        add(input);

        send = new JButton("发送");
        send.setActionCommand("SEND");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendTextMsg(input.getText());
            }
        });
        add(send);
    }

    public void launch() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void appendMsg(AnyMsg anyMsg) {
        StringBuilder builder = new StringBuilder();

        Object content = null;
        if (anyMsg instanceof TextMsg) {
            content = ((TextMsg) anyMsg).getText();
        } else if (anyMsg instanceof ImageMsg) {
            content = Base64.getDecoder().decode(((ImageMsg) anyMsg).getImage());
        } else {
            content = "不支持的消息类型";
        }

        builder.append("<html>")
                .append("<p>")
                .append(anyMsg.getFrom())
                .append("&nbsp;&nbsp;&nbsp;&nbsp;")
                .append(DateUtil.format(anyMsg.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                .append("</p>")
                .append("<p>")
                .append(content instanceof String ? content : "")
                .append("</p>")
                .append("</html>");

        if (anyMsg instanceof ImageMsg) {
            ImagePanel imagePanel = new ImagePanel(builder.toString(), (byte[]) content, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                    if (me.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu pm = new JPopupMenu();
                        JMenuItem view, copy, del;
                        view = new JMenuItem("查看大图");
                        view.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ImagePanel a = (ImagePanel) me.getSource();
                                String id = a.getName();
                                AnyMsg am = ApplicationContext.getAnyMsgById(id);
                                byte[] image = Base64.getDecoder().decode(((ImageMsg) am).getImage());
                                new ImageView(image);
                                pm.setVisible(false);
                            }
                        });
                        pm.add(view);
                        copy = new JMenuItem("复制内容");
                        copy.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ImagePanel a = (ImagePanel) me.getSource();
                                String id = a.getName();
                                AnyMsg am = ApplicationContext.getAnyMsgById(id);
                                byte[] image = Base64.getDecoder().decode(((ImageMsg) am).getImage());
                                Toolkit.getDefaultToolkit().getSystemClipboard()
                                        .setContents(new ImageTransferable(image), null);
                                pm.setVisible(false);
                            }
                        });
                        pm.add(copy);
                        del = new JMenuItem("删除");
                        del.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JComponent comp = (JComponent) me.getSource();
                                ApplicationContext.removeMsg(comp);
                            }
                        });
                        pm.add(del);
                        pm.show((Component) me.getSource(), me.getX(), me.getY());
                    }
                }
            });
            imagePanel.setName(anyMsg.getId());
            msgArea.add(imagePanel);
            return;
        }

        JLabel label = new JLabel();
        label.setName(anyMsg.getId());
        label.setText(builder.toString());
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu pm = new JPopupMenu();
                    JMenuItem copy, del;
                    copy = new JMenuItem("复制内容");
                    copy.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JLabel a = (JLabel) me.getSource();
                            String id = a.getName();
                            AnyMsg am = ApplicationContext.getAnyMsgById(id);
                            Transferable trans = new StringSelection(((TextMsg) am).getText());
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
                            pm.setVisible(false);
                        }
                    });
                    pm.add(copy);
                    del = new JMenuItem("删除");
                    del.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JComponent comp = (JComponent) me.getSource();
                            ApplicationContext.removeMsg(comp);
                        }
                    });
                    pm.add(del);
                    pm.show((Component) me.getSource(), me.getX(), me.getY());
                }
            }
        });
        msgArea.add(label);
    }

    public int getScrollValue() {
        return sp_pane.getVerticalScrollBar().getMaximum();
    }

    public void scrollBottom(int oldValue) {
        int maxHeight = sp_pane.getVerticalScrollBar().getMaximum();
//        System.out.println(String.format("oldValue : %s, newvalue : %s, result : %s",
//                oldValue, maxHeight, maxHeight - oldValue));
        sp_pane.getViewport().setViewPosition(new Point(0, Integer.MAX_VALUE));
        sp_pane.updateUI();
    }

    private void sendTextMsg(String text) {
        if (!StringUtil.isBlank(text)) {
            TextMsg textMsg = new TextMsg();
            textMsg.setCreateTime(new Date());
            textMsg.setFrom(Global.getUsername());
            textMsg.setTo(Global.getGroup());
            textMsg.setToType(TextMsgToType.TO_TYPE_GROUP);
            textMsg.setText(text);
            Msg resultMsg = ApplicationContext.sendSync(textMsg);
            IdMsg idMsg = (IdMsg) resultMsg.getBody();
            textMsg.setId(idMsg.getId());
            ApplicationContext.appendMsg(textMsg);
            input.setText("");
        }
    }

    private void sendImageMsg(String image) {
        if (!StringUtil.isBlank(image)) {
            ImageMsg imageMsg = new ImageMsg();
            imageMsg.setCreateTime(new Date());
            imageMsg.setFrom(Global.getUsername());
            imageMsg.setTo(Global.getGroup());
            imageMsg.setToType(TextMsgToType.TO_TYPE_GROUP);
            imageMsg.setImage(image);
            Msg resultMsg = ApplicationContext.sendSync(imageMsg);
            IdMsg idMsg = (IdMsg) resultMsg.getBody();
            imageMsg.setId(idMsg.getId());
            ApplicationContext.appendMsg(imageMsg);
        }
    }

    public void removeMsg(String id) {
        Component[] components = msgArea.getComponents();
        for (Component comp : components) {
            if (Objects.equals(comp.getName(), id)) {
                msgArea.remove(comp);
                msgArea.updateUI();
                break;
            }
        }
    }

    public void removeMsg(JComponent comp) {
        msgArea.remove(comp);
        msgArea.updateUI();
    }
}
