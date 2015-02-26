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
    public static final int SIGMA = 0;    
    final double MIN_PERCENT = 90;
    public static Point scaledImageSize = new Point(200, 200);

    private static int getColorInGrayScale(Color c) {
        int r = c.getRed(),
                g = c.getGreen(),
                b = c.getBlue();
        int average = (r + b + g) / 3;
        return new Color(average, average, average).getRGB();
    }

    private BufferedImage originImage;
    private BufferedImage croppedImage;
    private BufferedImage scaledCroppedImage;

    int[] projectionX;
    int[] projectionY;
    int[][] sectors;

    public ImageHandler(String imageName) throws IOException {
        originImage = repaintImageToBlackAndWhite(ImageIO.read(new File(imageName)));
        croppedImage = cropImage();
        scaledCroppedImage = getScaledCroppedImage();

        countProjections();
    }

    public static void setScaledImageSize(Point point) {
        scaledImageSize = point;
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
                    copyOfImage.setRGB(j, i, getColorInGrayScale(c));
                } else {
                    copyOfImage.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
        }

        return copyOfImage;
    }

    public int compare(ImageHandler imageToCompare, Class method) throws Exception {

        if (method == null) {
            throw new Exception("Nie ma podanej metody sprawdzania podpisu");
        }

        SignatureComparer comparer = null;

        Point cropSize;// = getCropSize(imageToCompare);
        cropSize = new Point(200, 200);

        if (ImagePixelByPixelComparer.class == method) {
            comparer = new ImagePixelByPixelComparer(getScaledCroppedImage(cropSize.x, cropSize.y), SIGMA);
        } else if (ImageGrayScaleComparer.class == method) {
            comparer = new ImageGrayScaleComparer(new ArrayList());
        } else if (ImageProjectionComparer.class == method) {
            comparer = new ImageProjectionComparer(this);
        }

        return comparer.compare(imageToCompare);
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

    public final BufferedImage getScaledCroppedImage() {
        return getScaledCroppedImage(scaledImageSize.x, scaledImageSize.y);
    }

    public BufferedImage getScaledCroppedImage(int x, int y) {
        if (scaledCroppedImage == null || scaledCroppedImage.getWidth() != x || scaledCroppedImage.getHeight() != y) {
            scaledCroppedImage = getScaledImage(getCroppedImage(), x, y);
            scaledCroppedImage = repaintImageToBlackAndWhite(scaledCroppedImage);
        }
        return scaledCroppedImage;
    }

    private BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        int finalw = w;
        int finalh = h;
//        double factor = 1.0d;
//        if (src.getWidth() > src.getHeight()) {
//            factor = ((double) src.getHeight() / (double) src.getWidth());
//            finalh = (int) (finalw * factor);
//        } else {
//            factor = ((double) src.getWidth() / (double) src.getHeight());
//            finalw = (int) (finalh * factor);
//        }

        BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, finalw, finalh, null);
        g2.dispose();
        return resizedImg;
    }

    private final BufferedImage cropImage() {
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
                    originImage.setRGB(j, i, c.getRGB());
                }
            }
        }

        cropStart.setLocation(mostLeft, mostTop);
        cropEnd.setLocation(mostRight, mostBottom);

        return originImage.getSubimage(mostLeft, mostTop, mostRight - mostLeft, mostBottom - mostTop);
    }

    private static int calculateDistanceToBackground(Color color) {
        return calculateColorsDistance(color, BACKGROUND_COLOR);
    }

    private static int calculateDistanceToTextColor(Color color) {
        return calculateColorsDistance(color, TEXT_COLOR);
    }

    static int calculateColorsDistance(Color c1, Color c2) {
        float dx = c1.getBlue() - c2.getBlue();
        float dy = c1.getGreen() - c2.getGreen();
        float dz = c1.getRed() - c2.getRed();

        return (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

    private Point getCropSize(ImageHandler imageToCompare) {
        int x = getCroppedImage().getWidth(),
                y = getCroppedImage().getHeight();

        BufferedImage bi = imageToCompare.getCroppedImage();
        x = x > bi.getWidth() ? bi.getWidth() : x;
        y = y > bi.getHeight() ? bi.getHeight() : y;

        return new Point(x, y);
    }

    private void countProjections() {

        projectionX = new int[scaledImageSize.x];
        projectionY = new int[scaledImageSize.y];
        int overalBlacPixels = 0;
        //liczmy dla x
        for (int i = 0; i < scaledImageSize.x; i++) {
            int blackPixelsCount = 0;
            for (int j = 0; j < scaledImageSize.y; j++) {
                if (scaledCroppedImage.getRGB(i, j) == Color.BLACK.getRGB()) {
                    blackPixelsCount++;
                }
            }
            overalBlacPixels += blackPixelsCount;
            projectionX[i] = blackPixelsCount;
        }
        int XBlackPixels = overalBlacPixels;
        overalBlacPixels = 0;
        //liczmy dla y
        for (int i = 0; i < scaledImageSize.y; i++) {
            int blackPixelsCount = 0;
            for (int j = 0; j < scaledImageSize.x; j++) {
                if (scaledCroppedImage.getRGB(j, i) == Color.BLACK.getRGB()) {
                    blackPixelsCount++;
                }
            }
            overalBlacPixels += blackPixelsCount;
            projectionY[i] = blackPixelsCount;
        }
    }

}
