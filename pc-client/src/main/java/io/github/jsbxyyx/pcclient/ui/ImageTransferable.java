package io.github.jsbxyyx.pcclient.ui;

import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author
 * @since
 */
public class ImageTransferable implements Transferable {

    private BufferedImage img;

    public ImageTransferable(byte[] image) {
        try {
            img = ImageIO.read(new ByteArrayInputStream(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor))
            return img;
        throw new UnsupportedFlavorException(flavor);
    }
}
