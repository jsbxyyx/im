package io.github.jsbxyyx.pcclient.ui;

import io.github.jsbxyyx.common.DateUtil;
import io.github.jsbxyyx.common.IoUtil;
import io.github.jsbxyyx.msg.AnyMsg;
import io.github.jsbxyyx.msg.AnyMsgType;
import io.github.jsbxyyx.msg.IdMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.TextMsgToType;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private JButton face;
    private JButton image;
    private JTextField input;
    private JButton send;
    private FaceDialog fd;

    public MainUI() {
        setTitle(Global.getGroupName() + " - " + Global.getUsername());
        setSize(600, 430);
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

        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(580, 35));
        add(panel2);

        face = new JButton("表情");
        face.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fd == null) {
                    fd = new FaceDialog(new ClickCallback() {
                        @Override
                        public void callback(String str) {
                            sendMsg(Base64.getDecoder().decode(str), AnyMsgType.Image);
                        }
                    });
                }
                fd.setVisible(true);
            }
        });
        panel2.add(face);

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
                    sendMsg(bytes, AnyMsgType.Image);
                }
            }
        });
        panel2.add(image);

        JPanel panel3= new JPanel();
        panel3.setPreferredSize(new Dimension(580, 60));
        add(panel3);

        input = new JTextField(40);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getSource() == input) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMsg(input.getText().getBytes(StandardCharsets.UTF_8), AnyMsgType.Text);
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
                                sendMsg(baos.toByteArray(), AnyMsgType.Image);
                            } catch (UnsupportedFlavorException ex) {
                            } catch (IOException ex) {
                            }
                        } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            try {
                                List<File> fileList = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
                                if (fileList != null && !fileList.isEmpty()) {
                                    File file = fileList.get(0);
                                    if (IoUtil.checkImage(file)) {
                                        sendMsg(IoUtil.readFile(file), AnyMsgType.Image);
                                    }
                                }
                            } catch (UnsupportedFlavorException ex) {
                            } catch (IOException ex) {
                            }
                        }
                    }
                }), ctrlV, JComponent.WHEN_FOCUSED);
        panel3.add(input);

        send = new JButton("发送");
        send.setActionCommand("SEND");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg(input.getText().getBytes(StandardCharsets.UTF_8), AnyMsgType.Text);
            }
        });
        panel3.add(send);
    }

    public void launch() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void appendMsg(AnyMsg anyMsg) {
        StringBuilder builder = new StringBuilder();

        Object content = null;
        if (anyMsg.getType() == AnyMsgType.Text) {
            content = anyMsg.getText();
        } else if (anyMsg.getType() == AnyMsgType.Image) {
            content = anyMsg.getImage();
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

        if (anyMsg.getType() == AnyMsgType.Image) {
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
                                byte[] image = am.getContent();
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
                                byte[] image = am.getContent();
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
                            Transferable trans = new StringSelection(am.getText());
                            Toolkit.getDefaultToolkit().getSystemClipboard()
                                    .setContents(trans, null);
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
        sp_pane.getViewport().setViewPosition(new Point(0, Integer.MAX_VALUE));
        sp_pane.updateUI();
    }

    private void sendMsg(byte[] content, int anyMsgType) {
        if (content != null && content.length > 0) {
            AnyMsg anyMsg = new AnyMsg();
            anyMsg.setCreateTime(new Date());
            anyMsg.setFrom(Global.getUsername());
            anyMsg.setTo(Global.getGroup());
            anyMsg.setToType(TextMsgToType.TO_TYPE_GROUP);
            anyMsg.setContent(content);
            anyMsg.setType(anyMsgType);
            Msg resultMsg = ApplicationContext.sendSync(anyMsg);
            IdMsg idMsg = (IdMsg) resultMsg.getBody();
            anyMsg.setId(idMsg.getId());
            ApplicationContext.appendMsg(anyMsg);
            input.setText("");
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
