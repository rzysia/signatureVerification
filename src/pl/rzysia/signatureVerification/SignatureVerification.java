package pl.rzysia.signatureVerification;

import java.awt.FlowLayout;
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
    
    public void compareFiles() throws IOException{
        
        originHandler = new ImageHandler("src/resources/siema.png");
        currentHandler = new ImageHandler("src/resources/siema_ruszone.png");
        currentHandler = new ImageHandler("src/resources/siema_obrocone.png");
        
//        JLabel originImage = new JLabel(new ImageIcon(originHandler.getOriginImage()));
//        add(originImage);
        
//        originHandler.repaintOriginToBlackAndWhite();
        JLabel croppedImage = new JLabel(new ImageIcon(originHandler.getScaledCroppedImage()));
        add(croppedImage);
        
        JLabel ScalledCroppedImage = new JLabel(new ImageIcon(currentHandler.getScaledCroppedImage()));
        add(ScalledCroppedImage);
        
        //int result = ImagePixelByPixelComparer.compare(originHandler.getScaledCroppedImage(),currentHandler.getScaledCroppedImage());
        
        //JLabel info = new JLabel("Podobieństwo: " + result + "%");
        //add(info);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        SignatureVerification sv = new SignatureVerification();
        sv.compareFiles();
        sv.setVisible(true);

    }

}
