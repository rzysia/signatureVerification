/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Krzysztof
 */
public class ImagePixelByPixelComparer implements SignatureComparer {

    private final BufferedImage pattern;
    private final int sigma;

    public ImagePixelByPixelComparer(BufferedImage pattern, int sigma) {
        this.pattern = pattern;
        this.sigma = sigma;
    }

    @Override
    public int compare(BufferedImage current) {

        long samePixels = 0, differentPixels = 0, allTextPixelsInPattern = 0, allWhitePixels = 0;

        if (pattern.getWidth() != current.getWidth() || pattern.getHeight() != current.getHeight()) {
            System.out.println("Rozne rozmiary!");
            return 0;
        }

        for (int j = 0; j < pattern.getHeight(); j++) {
            for (int i = 0; i < pattern.getWidth(); i++) {
                if (pattern.getRGB(i, j) == Color.BLACK.getRGB()) {
                    allTextPixelsInPattern++;
//                    if (pattern.getRGB(i, j) == current.getRGB(i, j)) {
                    if (comparePixels(current, i, j, sigma)) {
                        samePixels++;
                    } else {
                        differentPixels++;
                    }
                } else {
                    allWhitePixels++;
                }

            }
        }
        
        
        System.out.println("Takie same piksele: " + samePixels);
        System.out.println("Różne piksele: " + differentPixels);
        System.out.println("Czarne piksele: " + allTextPixelsInPattern);
        System.out.println("Białe piksele: " + allWhitePixels);
        System.out.println("Podobieństwo: " + samePixels * 100 / allTextPixelsInPattern);
        
        return (int) (samePixels * 100 / allTextPixelsInPattern);
    }

    @Override
    public boolean isAccessGranted(BufferedImage current) {
        return this.compare(current) > 90;
    }

    private boolean comparePixels(BufferedImage current, int x, int y, int sigma) {
        int patternRGB = pattern.getRGB(x, y);
//        System.out.println("******************** " + x + ", " + y + " *****************************");
        for (int i = x - sigma; i <= x + sigma; i++) {
            for (int j = y - sigma; j <= y + sigma; j++) {
//                System.out.println("Sprawdzam " + i + ", " + j);
                try{
                if(current.getRGB(i, j) == patternRGB)
                    return true;
                } catch (ArrayIndexOutOfBoundsException e){
//                    System.out.println("out of bounds!");
                }
            }
        }
//        System.out.println("**********************************************************************");
        return false;
    }
}
