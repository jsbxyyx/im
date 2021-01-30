package io.github.jsbxyyx.pcclient.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;

/**
 * @author
 * @since
 */
public class ImagePanel extends JPanel {

    private JLabel _text;
    private JLabel _image;

    public ImagePanel(String text, byte[] image, MouseAdapter mouseAdapter) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        _text = new JLabel();
        _text.setText(text);

        _image = new JLabel();
        _image.setIcon(new ImageIcon(image));

        add(_text);
        add(_image);

        if (mouseAdapter != null) {
            this.addMouseListener(mouseAdapter);
        }
    }

}
