/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import static pl.rzysia.signatureVerification.ImageHandler.ImageHandler.scaledImageSize;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Krzysztof
 */
public class ImageProjectionComparer implements SignatureComparer{

    private final ImageHandler imageHandler;
    
    ImageProjectionComparer(ImageHandler imageHandler) {
        this.imageHandler = imageHandler;
    }

    @Override
    public int compare(ImageHandler current) {
        
    }

    @Override
    public boolean isAccessGranted(ImageHandler current) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void compareProjection(ImageHandler other) {
        double currBlackPixels = countPixelsFromProjection(imageHandler.projectionX);
        double otherBlackPixels = countPixelsFromProjection(other.projectionX);

        double prop = currBlackPixels / otherBlackPixels;

        scaleProjections(other, prop);
    }

    private int countPixelsFromProjection(int[] projectionX) {
        int result = 0;
        for (int i : projectionX) {
            result += i;
        }
        return result;
    }

    private void scaleProjections(ImageHandler other, double prop) {
        for (int i = 0; i < other.projectionX.length; i++) {
            other.projectionX[i] = (int) Math.round(other.projectionX[i] * prop);
        }
        for (int i = 0; i < other.projectionY.length; i++) {
            other.projectionY[i] = (int) Math.round(other.projectionY[i] * prop);
        }
    }
}
