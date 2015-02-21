/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Krzysztof
 */
public class ImageHandler {

    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = Color.BLACK;

    BufferedImage originImage;
    BufferedImage croppedImage;
    BufferedImage scaledCroppedImage;

    public ImageHandler(String imageName) throws IOException {
        originImage = repaintImageToBlackAndWhite(ImageIO.read(new File(imageName)));
    }

    public static BufferedImage repaintImageToBlackAndWhite(BufferedImage image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        BufferedImage copyOfImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(image, 0, 0, null);

        Color c;

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                c = new Color(copyOfImage.getRGB(j, i));
                int difference = calculateDistanceToBackground(c);
                if (difference > 50) {
                    copyOfImage.setRGB(j, i, Color.BLACK.getRGB());
                } else {
                    copyOfImage.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }

        return copyOfImage;
    }
    
    public int compare(ImageHandler imageToCompare, Class method) throws Exception{
        
        if(method == null)
            throw new Exception("Nie ma podanej metody sprawdzania podpisu");
        
        SignatureComparer comparer = null;
        
        if(ImagePixelByPixelComparer.class == method)
            comparer = new ImagePixelByPixelComparer(getScaledCroppedImage());
        else if(ImageGrayScaleComparer.class == method)
            comparer = new ImageGrayScaleComparer(new ArrayList());
        
        return comparer.compare(imageToCompare.getCroppedImage());
    }

    public void repaintOriginToBlackAndWhite() {
        originImage = repaintImageToBlackAndWhite(originImage);
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

    public BufferedImage getScaledCroppedImage() {
        if (scaledCroppedImage == null) {
            scaledCroppedImage = getScaledImage(getCroppedImage(), 200, 100);
            scaledCroppedImage = repaintImageToBlackAndWhite(scaledCroppedImage);
        }
        return scaledCroppedImage;
    }

    private BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        int finalw = w;
        int finalh = h;
        double factor = 1.0d;
        if (src.getWidth() > src.getHeight()) {
            factor = ((double) src.getHeight() / (double) src.getWidth());
            finalh = (int) (finalw * factor);
        } else {
            factor = ((double) src.getWidth() / (double) src.getHeight());
            finalw = (int) (finalh * factor);
        }

        BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, finalw, finalh, null);
        g2.dispose();
        return resizedImg;
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

    private static int calculateDistanceToBackground(Color color) {
        return calculateColorsDistance(color, BACKGROUND_COLOR);
    }

    private static int calculateDistanceToTextColor(Color color) {
        return calculateColorsDistance(color, TEXT_COLOR);
    }

    private static int calculateColorsDistance(Color c1, Color c2) {
        float dx = c1.getBlue() - c2.getBlue();
        float dy = c1.getGreen() - c2.getGreen();
        float dz = c1.getRed() - c2.getRed();

        return (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

}
