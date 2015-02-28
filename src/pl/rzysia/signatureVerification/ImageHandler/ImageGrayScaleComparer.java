/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.ImageHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import pl.rzysia.signatureVerification.interfaces.SignatureComparer;

/**
 *
 * @author Return of the Retard
 */
public class ImageGrayScaleComparer implements SignatureComparer{

    private List<BufferedImage> sources;
    private List<Integer[]> signsAvgs;
    private final int EPSILON;
    private final double MIN_PERCENT;
    private JFrame window;
    
    public ImageGrayScaleComparer(List<BufferedImage> sources) {
        this.sources = sources;
        this.EPSILON = 20;
        this.MIN_PERCENT = 90d;
        
        this.processSignsAverages();
    }
    
    public ImageGrayScaleComparer(List<BufferedImage> sources, JFrame window){
        this(sources);
        this.window = window;
        this.processSignsAverages();
    }
    
    @Override
    public int compare(ImageHandler image) {
        BufferedImage current = image.getScaledCroppedImage(sources.get(0).getWidth(),sources.get(0).getHeight());
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
    public boolean isAccessGranted(ImageHandler image){
        return compare(image) > this.MIN_PERCENT;
    }
    
    private int compareSignsAverages(Integer[] curr, Integer[] org){
        double percent;
        int resultPercent = 0;
        
        for(int i = 0; i < 100; i++){
            resultPercent += compareColors(curr[i], org[i]) ? 1 : 0;
        }
            
        return resultPercent;
    }
    
    private boolean compareColors(int curr, int org){
        Color o = new Color(org);
        Color c = new Color(curr);
        
        return o.getRed() - this.EPSILON < c.getRed() && c.getRed() < o.getRed() + this.EPSILON;
    }
    
    private void processSignsAverages(){    
        List<Integer[]> signsAverages = new LinkedList<>();
        
        for(BufferedImage sign : this.sources){
            signsAverages.add(processSignAverages(sign));
        }
        
        signsAvgs = signsAverages;
    }
    
    private Integer[] processSignAverages(BufferedImage sign){
       Integer[] avgs = new Integer[sign.getWidth()/10 * sign.getHeight()/10];
       int p = 0;
       
       BufferedImage mi = new BufferedImage(sign.getWidth(), sign.getHeight(), BufferedImage.TYPE_INT_RGB);
       
       for(int i = 0; i < mi.getWidth()/10; i++)
           for(int j = 0; j < mi.getWidth()/10; j++){
               int v = this.getImageAverage(sign.getSubimage(10 * i, 10 * j, 10, 10));
               avgs[p++] = v;
               fillSubImage(10 * i, 10 * j, v, mi);
           }
       
       if(this.window != null)
            this.window.add(new JLabel(new ImageIcon(mi)));
       
       return avgs;
    }
    
    private int getImageAverage(BufferedImage subsign){
        int avg = 0,
            red = 0,
            blue = 0,
            green = 0;
        
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++){
                avg += subsign.getRGB(i, j);
            }
        
        avg = avg / 100;
        Color c = new Color(avg);
        int av = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
        return new Color(av, av, av).getRGB();
    }
    
    private void fillSubImage(int x, int y, int avg, BufferedImage im){
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++)
                im.setRGB(x + i, y + j, avg);
    }
}
