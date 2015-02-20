/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
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

    public static final Color backgroundColor = Color.WHITE;
    public static final Color textColor = Color.BLACK;

    BufferedImage originImage;
    BufferedImage croppedImage;

    public ImageHandler(String imageName) throws IOException {
        originImage = ImageIO.read(new File(imageName));
        croppedImage = null;
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
                if (difference < 30) {                    
                    if(mostLeft > j){
                        mostLeft = j;
                    }
                    if(mostRight < j){
                        mostRight = j;
                    }
                    if(mostTop < 0){
                        mostTop = i;
                    }
                    if(mostBottom < i){
                        mostBottom = i;
                    }
//                    System.out.print("1");
                } else {
//                    System.out.print("0");
                }
            }
//            System.out.println("");
        }
        
        cropStart.setLocation(mostLeft, mostTop);
        cropEnd.setLocation(mostRight, mostBottom);

        System.out.println(cropStart);
        System.out.println(cropEnd);

        return originImage.getSubimage(mostLeft, mostTop, mostRight - mostLeft, mostBottom - mostTop);
    }

    private int calculateDistanceToBackground(Color color) {
        return calculateColorsDistance(color, backgroundColor);
    }

    private int calculateDistanceToTextColor(Color color) {
        return calculateColorsDistance(color, textColor);
    }

    private int calculateColorsDistance(Color c1, Color c2) {
        float dx = c1.getBlue() - c2.getBlue();
        float dy = c1.getGreen() - c2.getGreen();
        float dz = c1.getRed() - c2.getRed();

        return (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

}
