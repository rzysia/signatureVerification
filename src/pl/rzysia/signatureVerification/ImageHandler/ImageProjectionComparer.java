package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import static pl.rzysia.signatureVerification.ImageHandler.ImageHandler.calculateDistanceToTextColor;
import static pl.rzysia.signatureVerification.ImageHandler.ImageHandler.scaledImageSize;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Krzysztof
 */
public class ImageProjectionComparer implements SignatureComparer {

    private ImageHandler imageHandler;
    private List<BufferedImage> list;

    ImageProjectionComparer(ImageHandler imageHandler) {
        this.imageHandler = imageHandler;
    }

    ImageProjectionComparer(List<BufferedImage> list) {
        this.list = list;
    }

    @Override
    public int compare(ImageHandler current) {
        int[] projOriginX,
                projOriginY;


        /*
         Pomysł - porównywanie po kolei każdej pary iX[i] <> cX[i] (procentowo o ile się różnią) i pote średnia z tych procentów
         - może co z tego będzie?
         */
        int bestMatchPercent = 0;

        for (BufferedImage bi : list) {

            int[][] projections = countProjections(bi);

            projOriginX = projections[0];
            projOriginY = projections[1];

//            int[] projCurrX = scaleProjection(projOriginX, current.projectionX),
//                    projCurrY = scaleProjection(projOriginY, current.projectionY);
            projections = countProjections(current.getScaledCroppedImage(bi.getWidth(), bi.getHeight()));

            int[] projCurrX = projections[0],
                    projCurrY = projections[1];

            double[] percentageDifferencesX = calculatePercentadeDifferences(projOriginX, projCurrX);
            double[] percentageDifferencesY = calculatePercentadeDifferences(projOriginY, projCurrY);

            double diffAverageX = calculateAverage(percentageDifferencesX);
            double diffAverageY = calculateAverage(percentageDifferencesY);

            int currBest = (int) (diffAverageY * 100);
            if (diffAverageX > diffAverageY) {
                currBest = (int) (diffAverageX * 100);
            }

            bestMatchPercent = bestMatchPercent < currBest ? currBest : bestMatchPercent;
        }
        return bestMatchPercent;
    }

    @Override
    public boolean isAccessGranted(ImageHandler image) {
        return compare(image) > image.MIN_PERCENT;
    }

    public int[] scaleProjection(int[] originProj, int[] projToScale) {
        double currBlackPixels = countPixelsFromProjection(originProj);
        double otherBlackPixels = countPixelsFromProjection(projToScale);

        double prop = currBlackPixels / otherBlackPixels;

        return scaleProjection(projToScale, prop);
    }

    private int countPixelsFromProjection(int[] projectionX) {
        int result = 0;
        for (int i : projectionX) {
            result += i;
        }
        return result;
    }

    private int[] scaleProjection(int[] projToScale, double prop) {
        int[] toRet = new int[projToScale.length];
        for (int i = 0; i < projToScale.length; i++) {
            toRet[i] = (int) Math.round(projToScale[i] * prop);
        }
        return toRet;
    }

    private double[] calculatePercentadeDifferences(int[] projOriginX, int[] projCurrX) {
        double[] percentageDifferences = new double[projOriginX.length];
        for (int i = 0; i < projOriginX.length; i++) {
            percentageDifferences[i] = calculatePercentageDifference(projOriginX[i], projCurrX[i]);
//            System.out.println(percentageDifferences[i] + " <- i = " + i + ": " + projOriginX[i] + "," + projCurrX[i]);
        }
        return percentageDifferences;
    }

    private double calculatePercentageDifference(int projOriginX, int projCurrX) {
        double bigger = projOriginX,
                smaller = projCurrX;
        if (projOriginX < projCurrX) {
            bigger = projCurrX;
            smaller = projOriginX;
        }

        if (bigger == 0) {
            return 1;
        }
//        System.out.println(Math.abs(projOriginX - projCurrX));
        return smaller / bigger;
    }

    private double calculateAverage(double[] percentageDifferences) {
        double tempSum = 0;
        for (int i = 0; i < percentageDifferences.length; i++) {
            tempSum += percentageDifferences[i];
        }
        return tempSum / percentageDifferences.length;
    }

    private int[][] countProjections(BufferedImage image) {

        int[][] toRet = new int[2][];

        int[] projectionX = new int[image.getWidth()];
        int[] projectionY = new int[image.getHeight()];

        toRet[0] = projectionX;
        toRet[1] = projectionY;
        int overalBlacPixels = 0;
        //liczmy dla x
        for (int i = 0; i < image.getWidth(); i++) {
            int blackPixelsCount = 0;
            for (int j = 0; j < image.getHeight(); j++) {
//                if (scaledCroppedImage.getRGB(i, j) == Color.BLACK.getRGB()) {
                if (calculateDistanceToTextColor(new Color(image.getRGB(i, j))) < 200) {
                    blackPixelsCount++;
                }
            }
            overalBlacPixels += blackPixelsCount;
            projectionX[i] = blackPixelsCount;
        }
        int XBlackPixels = overalBlacPixels;
        overalBlacPixels = 0;
        //liczmy dla y
        for (int i = 0; i < image.getHeight(); i++) {
            int blackPixelsCount = 0;
            for (int j = 0; j < image.getWidth(); j++) {
                if (calculateDistanceToTextColor(new Color(image.getRGB(j, i))) < 200) {
                    blackPixelsCount++;
                }
            }
            overalBlacPixels += blackPixelsCount;
            projectionY[i] = blackPixelsCount;
        }

        return toRet;
    }
}
