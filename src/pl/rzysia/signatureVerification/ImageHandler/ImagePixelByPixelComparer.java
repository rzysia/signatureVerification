package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Krzysztof
 */
public class ImagePixelByPixelComparer implements SignatureComparer {

    private BufferedImage source;
    List<BufferedImage> list;
    private final int sigma;

    public ImagePixelByPixelComparer(BufferedImage pattern, int sigma) {
        this.source = pattern;
        this.sigma = sigma;
    }

    ImagePixelByPixelComparer(List<BufferedImage> list, int sigma) {
        this.list = list;
        this.sigma = sigma;
    }

    @Override
    public int compare(ImageHandler image) {
        BufferedImage current = image.getScaledCroppedImage(list.get(0).getWidth(), list.get(0).getHeight());

        long samePixels = 0, differentPixels = 0, allTextPixelsInPattern = 0, allWhitePixels = 0;
        int bestMatchPercent = 0;

        for (BufferedImage pattern : list) {
            samePixels = 0; 
            differentPixels = 0; 
            allTextPixelsInPattern = 0;
            allWhitePixels = 0;

            if (pattern.getWidth() != current.getWidth() || pattern.getHeight() != current.getHeight()) {
                System.out.println("Rozne rozmiary!");
                return 0;
            }

            for (int j = 0; j < pattern.getHeight(); j++) {
                for (int i = 0; i < pattern.getWidth(); i++) {
//                if (pattern.getRGB(i, j) == Color.BLACK.getRGB()) {
                    if (ImageHandler.calculateColorsDistance(new Color(pattern.getRGB(i, j)), Color.WHITE) > 50) {
                        allTextPixelsInPattern++;
//                    if (pattern.getRGB(i, j) == current.getRGB(i, j)) {
                        if (comparePixels(pattern, current, i, j, sigma)) {
                            samePixels++;
                        } else {
                            differentPixels++;
                        }
                    } else {
                        allWhitePixels++;
                    }
                }
            }

//        System.out.println("Takie same piksele: " + samePixels);
//        System.out.println("Różne piksele: " + differentPixels);
//        System.out.println("Czarne piksele: " + allTextPixelsInPattern);
//        System.out.println("Białe piksele: " + allWhitePixels);
//        System.out.println("Podobieństwo: " + samePixels * 100 / allTextPixelsInPattern);
            int currentMatchPercent = (int) (samePixels * 100 / allTextPixelsInPattern);
            if (bestMatchPercent < currentMatchPercent) {
                bestMatchPercent = currentMatchPercent;
            }
        }

        return bestMatchPercent;
    }

    @Override
    public boolean isAccessGranted(ImageHandler current) {
        return this.compare(current) > 90;
    }

    private boolean comparePixels(BufferedImage source, BufferedImage current, int x, int y, int sigma) {
        int patternRGB = source.getRGB(x, y);
        for (int i = x - sigma; i <= x + sigma; i++) {
            for (int j = y - sigma; j <= y + sigma; j++) {
                try {
                    if (areBothTextPixels(current.getRGB(i, j), patternRGB)) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
        return false;
    }

    private boolean areBothTextPixels(int rgb, int patternRGB) {
        return ImageHandler.calculateColorsDistance(new Color(rgb), new Color(patternRGB)) < 100;
    }
}
