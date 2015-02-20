/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Krzysztof
 */
public class ImagePixelByPixelComparer {

    public static int compare(BufferedImage pattern, BufferedImage current) {
        long samePixels = 0, differentPixels = 0, allTextPixelsInPattern = 0, allWhitePixels = 0;
        if (pattern.getWidth() != current.getWidth() || pattern.getHeight() != current.getHeight()) {
            return 0;
        }

        for (int j = 0; j < pattern.getHeight(); j++) {
            for (int i = 0; i < pattern.getWidth(); i++) {
                if (pattern.getRGB(i, j) == Color.BLACK.getRGB()) {
                    allTextPixelsInPattern++;
                    if (pattern.getRGB(i, j) == current.getRGB(i, j)) {
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
}
