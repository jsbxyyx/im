package io.github.jsbxyyx.pcclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Base64;
import java.util.List;

/**
 * @author
 * @since
 */
public class FaceDialog extends JDialog {

    private JScrollPane sp;
    private JPanel table;
    private ClickCallback clickCallback;

    public FaceDialog(ClickCallback clickCallback) {
        FaceDialog that = this;
        this.clickCallback = clickCallback;
        setTitle("表情");
        setSize(450, 450);
        setResizable(false);
        List<String> faces = Face.getFaces2();
        table = new JPanel();
        int cols = 6;
        int rows = faces.size() % cols == 0 ? faces.size() / cols : faces.size() / cols + 1;
        table.setLayout(new GridLayout(rows, cols));

        for (String face : faces) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(Base64.getDecoder().decode(face)));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                        that.clickCallback.callback(face);
                        that.dispose();
                    }
                }
            });
            table.add(label);
        }
        sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(380, 380));
        sp.setViewportView(table);
        add(sp);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

}
