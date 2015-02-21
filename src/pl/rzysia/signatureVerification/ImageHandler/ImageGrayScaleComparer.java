/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Return of the Retard
 */
public class ImageGrayScaleComparer implements SignatureComparer{

    private List<BufferedImage> sources;
    private List<Integer[]> signsAvgs;
    private final double MIN_PERCENT_SUBIMAGE;
    private final double MIN_PERCENT;

    public ImageGrayScaleComparer(List<BufferedImage> sources) {
        this.sources = sources;
        this.MIN_PERCENT_SUBIMAGE = 90.;
        this.MIN_PERCENT = 90.;
        
        this.processSignsAverages();
    }    
    
    @Override
    public int compare(BufferedImage current) {
        int max = 0;
        int value;
        Integer[] currAvgs = this.processSignAverages(current);
        
        for(Integer[] signAvgs : signsAvgs){
            value = this.compareSignsAverages(currAvgs, signAvgs);
            if(max < value)
                max = value;
        }
        
        return max;
    }
    
    @Override
    public boolean isAccessGranted(BufferedImage current){
        return compare(current) > this.MIN_PERCENT;
    }
    
    private int compareSignsAverages(Integer[] curr, Integer[] org){
        double percent;
        int resultPercent = 0;
        
        for(int i = 0; i < 100; i++){
            percent = curr[i] > org[i] ? org[i] / curr[i] : curr[i] / org[i];
            resultPercent += percent >= MIN_PERCENT_SUBIMAGE ? 1 : 0;
        }
            
        return resultPercent;
    }
    
    private List<Integer[]> processSignsAverages(){    
        List<Integer[]> signsAverages = new LinkedList<>();
        
        for(BufferedImage sign : this.sources){
            signsAverages.add(processSignAverages(sign));
        }
        
        return signsAverages;
    }
    
    private Integer[] processSignAverages(BufferedImage sign){
       Integer[] avgs = new Integer[100];
       int p = 0;
       
       for(int i = 0; i < 10; i++)
           for(int j = 0; j < 10; j++)
               avgs[p++] = this.getImageAverage(sign.getSubimage(10 * i, 10 * j, 10, 10));
       
       return avgs;
    }
    
    private int getImageAverage(BufferedImage subsign){
        int avg = 0;
        
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++)
                avg += subsign.getRGB(i, j);
        
        avg = avg / 100; 
        return avg;
    }
}
