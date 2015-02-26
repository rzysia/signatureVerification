package pl.rzysia.signatureVerification;

import java.awt.FlowLayout;
import java.awt.Point;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
        super("Dzien dobry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 1000);
        setLayout(new FlowLayout());
    }
    
    public void compareFiles() throws IOException, Exception{
        
        originHandler = new ImageHandler("src/resources/kb_1.png");
        currentHandler = new ImageHandler("src/resources/kb_2.png");
//        currentHandler = new ImageHandler("src/resources/siema_obrocone.png");
        
        JLabel originImage = new JLabel(new ImageIcon(originHandler.getOriginImage()));
        add(originImage);
        
        Point cropSize = new Point(200, 100);
        
//        originHandler.repaintOriginToBlackAndWhite();
        JLabel croppedImage = new JLabel(new ImageIcon(originHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
        add(croppedImage);
        
        JLabel ScalledCroppedImage = new JLabel(new ImageIcon(currentHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
        add(ScalledCroppedImage);
        
        int result = originHandler.compare(currentHandler, ImagePixelByPixelComparer.class);
        
        
        
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
