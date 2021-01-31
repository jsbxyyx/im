package io.github.jsbxyyx.pcclient.ui;

import javax.swing.*;

/**
 * @author
 * @since
 */
public class ImageView extends JDialog {

    private JLabel image;

    public ImageView(byte[] imageBytes) {
        setTitle("查看图片");
        image = new JLabel(new ImageIcon(imageBytes));
        add(image);
        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
