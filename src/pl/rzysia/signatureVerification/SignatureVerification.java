package pl.rzysia.signatureVerification;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import pl.rzysia.signatureVerification.ImageHandler.ImageHandler;

/**
 *
 * @author Krzysztof
 */
public class SignatureVerification extends JFrame {
    
    ImageHandler imageHandler;
    

    public SignatureVerification() throws IOException {
        super("Dzien dobry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new FlowLayout());
        
        imageHandler = new ImageHandler("src/resources/siema.png");
        JLabel picLabel = new JLabel(new ImageIcon(imageHandler.getOriginImage()));
        add(picLabel);
        
        imageHandler.cropImage();
        
        JLabel croppedImage = new JLabel(new ImageIcon(imageHandler.getCroppedImage()));
        add(croppedImage);
        
        JLabel ScalledCroppedImage = new JLabel(new ImageIcon(imageHandler.getCroppedImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        add(ScalledCroppedImage);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        new SignatureVerification().setVisible(true);

    }

}
