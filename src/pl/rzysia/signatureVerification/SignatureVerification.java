package pl.rzysia.signatureVerification;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import pl.rzysia.signatureVerification.ImageHandler.ImageGrayScaleComparer;
import pl.rzysia.signatureVerification.ImageHandler.ImageHandler;
import pl.rzysia.signatureVerification.ImageHandler.ImagePixelByPixelComparer;

/**
 *
 * @author Krzysztof
 */
public class SignatureVerification extends JFrame {
    
    ImageHandler originHandler;
    ImageHandler currentHandler;
    

    public SignatureVerification() throws IOException {
        super("VVeryfikacja podpisu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(new FlowLayout());
    }
    
    public void compareFiles() throws IOException, Exception{
        
        originHandler = new ImageHandler("src/resources/kb_1.png");
//        currentHandler = new ImageHandler("src/resources/kb_2.png");
//        currentHandler = new ImageHandler("src/resources/kb_3.png");
        currentHandler = new ImageHandler("src/resources/kb_4.png");
//        currentHandler = new ImageHandler("src/resources/kb_5.png");
//        currentHandler = new ImageHandler("src/resources/kb_6.png");
//        currentHandler = new ImageHandler("src/resources/kb_7.png");
        
        JLabel originImage = new JLabel(new ImageIcon(originHandler.getOriginImage()));
        add(originImage);
        
        Point cropSize = new Point(100, 100);
        
//        originHandler.repaintOriginToBlackAndWhite();
        JLabel croppedImage = new JLabel(
                new ImageIcon(originHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
        
        add(croppedImage);
        
        JLabel scalledCroppedImage = new JLabel(
                new ImageIcon(currentHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
        
        add(scalledCroppedImage);
        
        //'lista'
        List<BufferedImage> lib = new ArrayList<>();
        lib.add(originHandler.getScaledCroppedImage(cropSize.x,cropSize.y));
        
        int result = originHandler.compare(currentHandler, ImageGrayScaleComparer.class, this, lib);
        
        JLabel info = new JLabel("Podobieństwo: " + result + "%");
        add(info);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        SignatureVerification sv = new SignatureVerification();
        sv.compareFiles();
        sv.setVisible(true);
    }
}
