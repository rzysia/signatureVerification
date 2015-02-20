/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Krzysztof
 */
public class ImageHandler {

    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = Color.BLACK;

    BufferedImage originImage;
    BufferedImage croppedImage;
    Image scaledCroppedImage;

    public ImageHandler(String imageName) throws IOException {
        originImage = ImageIO.read(new File(imageName));
        repaintOriginToBlackAndWhite();
    }

    public BufferedImage getOriginImage() {
        return originImage;
    }

    public BufferedImage getCroppedImage() {
        if (croppedImage == null) {
            croppedImage = cropImage();
        }

        return croppedImage;
    }

    public Image getScaledCroppedImage() {
        if (scaledCroppedImage == null) {
            scaledCroppedImage =  getCroppedImage().getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING);
        }
        return scaledCroppedImage;
    }

    public BufferedImage cropImage() {
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        Color c;
        Point cropStart = new Point(),
                cropEnd = new Point();

        int mostLeft = imageWidth, mostRight = -1, mostTop = -1, mostBottom = -1;

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                c = new Color(originImage.getRGB(j, i));
                int difference = calculateDistanceToTextColor(c);
                if (difference < 200) {
                    if (mostLeft > j) {
                        mostLeft = j;
                    }
                    if (mostRight < j) {
                        mostRight = j;
                    }
                    if (mostTop < 0) {
                        mostTop = i;
                    }
                    if (mostBottom < i) {
                        mostBottom = i;
                    }
                    originImage.setRGB(j, i, Color.BLACK.getRGB());
                }
            }
        }

        cropStart.setLocation(mostLeft, mostTop);
        cropEnd.setLocation(mostRight, mostBottom);

        System.out.println(cropStart);
        System.out.println(cropEnd);

        return originImage.getSubimage(mostLeft, mostTop, mostRight - mostLeft, mostBottom - mostTop);
    }

    public void repaintOriginToBlackAndWhite() {
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        Color c;

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                c = new Color(originImage.getRGB(j, i));
                int difference = calculateDistanceToBackground(c);
                if (difference > 50) {
                    originImage.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    originImage.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }
    }

    private int calculateDistanceToBackground(Color color) {
        return calculateColorsDistance(color, BACKGROUND_COLOR);
    }

    private int calculateDistanceToTextColor(Color color) {
        return calculateColorsDistance(color, TEXT_COLOR);
    }

    private int calculateColorsDistance(Color c1, Color c2) {
        float dx = c1.getBlue() - c2.getBlue();
        float dy = c1.getGreen() - c2.getGreen();
        float dz = c1.getRed() - c2.getRed();

        return (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

}
