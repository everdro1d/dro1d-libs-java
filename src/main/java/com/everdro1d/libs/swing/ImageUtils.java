// dro1dDev - created: 2025-05-10

package com.everdro1d.libs.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.InputStream;

public class ImageUtils {

    // Private constructor to prevent instantiation.
    private ImageUtils() {
        throw new UnsupportedOperationException("ImageUtils class cannot be instantiated");
    }

    /**
     * Set the icon of the frame.
     * @param frame the frame to set the icon of
     * @param internalPath path to the image file within resources
     * @param clazz the class to trace from
     */
    public static void setFrameIcon(JFrame frame, String internalPath, Class<?> clazz) {
        Icon icon = getApplicationIcon(internalPath, clazz);
        if (icon != null) {
            frame.setIconImage(((ImageIcon) icon).getImage());
        }
    }

    /**
     * Retrieve an image as an Icon from the application's resources.
     * @param internalPath path to the image file within resources
     * @param clazz the class to trace from
     * @return the Icon
     */
    public static Icon getApplicationIcon(String internalPath, Class<?> clazz) {
        Icon icon = null;
        try (InputStream iconStream = clazz.getClassLoader().getResourceAsStream(internalPath)) {
            if (iconStream != null) {
                icon = new ImageIcon(ImageIO.read(iconStream));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (icon == null) {
            System.err.println("[ERROR] Could not find icon file at: " + internalPath);
        }
        return icon;
    }

    /**
     * Changes the color of a given icon to the specified color.
     *
     * <p>This method creates a new image from the provided icon and applies a color filter
     * to replace the RGB values of non-transparent pixels with the specified color while
     * preserving the alpha (transparency) values.</p>
     *
     * @param icon the icon to change the color of
     * @param color the new color to apply to the icon
     * @return a new {@link ImageIcon} with the applied color, or {@code null} if an error occurs
     */
    public static ImageIcon changeIconColor(Icon icon, Color color) {
        // Create a new image that can be edited
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = bufferedImage.createGraphics();
        // Draw the icon on the new image
        icon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        // Create a filter that changes the color of the image
        ImageFilter filter = new RGBImageFilter() {
            public int filterRGB(int x, int y, int rgb) {
                // If the pixel is transparent, preserve its original RGB values
                if ((rgb & 0xFF000000) == 0) {
                    return rgb;
                }
                // If the pixel is not transparent, replace its RGB values with the desired color
                else {
                    // Preserve the alpha value
                    int alpha = rgb & 0xFF000000;
                    // Replace the RGB values with the desired color
                    return alpha | (color.getRGB() & 0x00FFFFFF);
                }
            }
        };

        // Apply the filter
        ImageProducer producer = new FilteredImageSource(bufferedImage.getSource(), filter);
        Image newImage = Toolkit.getDefaultToolkit().createImage(producer);

        return new ImageIcon(newImage);
    }

    /**
     * Darkens the given icon by reducing the brightness of its colors.
     *
     * <p>This method creates a new image from the provided icon and applies a darker color
     * transformation to each pixel while preserving the alpha (transparency) values.</p>
     *
     * @param icon the icon to darken
     * @return a new {@link ImageIcon} with the darkened colors, or {@code null} if an error occurs
     */
    public static ImageIcon darkenIcon(Icon icon) {
        try {
            BufferedImage originalImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = originalImage.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();
            BufferedImage darkenedImage = new BufferedImage(
                    originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dDark = darkenedImage.createGraphics();

            for (int x = 0; x < originalImage.getWidth(); x++) {
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    Color originalColor = new Color(originalImage.getRGB(x, y), true);
                    Color darkerColor = originalColor.darker();
                    g2dDark.setColor(darkerColor);
                    g2dDark.fillRect(x, y, 1, 1);
                }
            }

            g2dDark.dispose();

            return new ImageIcon(darkenedImage);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
