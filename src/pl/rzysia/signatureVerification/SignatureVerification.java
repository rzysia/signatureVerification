package pl.rzysia.signatureVerification;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import pl.rzysia.signatureVerification.ImageHandler.ImageGrayScaleComparer;
import pl.rzysia.signatureVerification.ImageHandler.ImageHandler;
import static pl.rzysia.signatureVerification.ImageHandler.ImageHandler.SIGMA;
import pl.rzysia.signatureVerification.ImageHandler.ImagePixelByPixelComparer;
import pl.rzysia.signatureVerification.ImageHandler.ImageProjectionComparer;

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
        setSize(600, 600);
        setLayout(new FlowLayout());
    }

    public void compareFiles() throws IOException, Exception {
        String path = "";
        JFileChooser choice = new JFileChooser("D:/rzysia/studia/fais/biometria/weryfilkacjaPodpisu/signatureVerification/src/resources");
        int option = choice.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            path = choice.getSelectedFile().getPath();

        }

        originHandler = new ImageHandler("src/resources/kb_1.png");
//        currentHandler = new ImageHandler("src/resources/kb_2.png");
//        currentHandler = new ImageHandler("src/resources/kb_3.png");
//        currentHandler = new ImageHandler("src/resources/kb_4.png");
//        currentHandler = new ImageHandler("src/resources/kb_duzelitery.png");    //duze litery
//        currentHandler = new ImageHandler("src/resources/kb_pisane.png");    //podstawówkowe
//        currentHandler = new ImageHandler("src/resources/kb_9.png");    //ok
//        currentHandler = new ImageHandler("src/resources/pm_p.jpg");    //Przemo
        currentHandler = new ImageHandler(path);    //Przemo

        JLabel originImage = new JLabel(new ImageIcon(originHandler.getOriginImage()));
//        add(originImage);

        originImage = new JLabel(new ImageIcon(currentHandler.getOriginImage()));
        add(originImage);

        Point cropSize = new Point(200, 200);

//        JLabel croppedImage = new JLabel(
//                new ImageIcon(originHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
//        
//        add(croppedImage);
//        JLabel scalledCroppedImage = new JLabel(
//                new ImageIcon(currentHandler.getScaledCroppedImage(cropSize.x,cropSize.y)));
//        
//        add(scalledCroppedImage);
        //'lista'
        List<BufferedImage> patterns = new ArrayList<>();
//        patterns.add(originHandler.getScaledCroppedImage(cropSize.x,cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_1.png").getScaledCroppedImage(cropSize.x, cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_2.png").getScaledCroppedImage(cropSize.x, cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_3.png").getScaledCroppedImage(cropSize.x, cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_4.png").getScaledCroppedImage(cropSize.x, cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_5.png").getScaledCroppedImage(cropSize.x, cropSize.y));
        patterns.add(new ImageHandler("src/resources/kb_6.png").getScaledCroppedImage(cropSize.x, cropSize.y));

        int result = originHandler.compare(currentHandler, ImageGrayScaleComparer.class, this, patterns);

        JLabel info = new JLabel();
        String infoText = "<html>Podobieństwo sektorami: " + result + "% <br>";
//        add(info);

        result = originHandler.compare(currentHandler, ImagePixelByPixelComparer.class, this, patterns);

        infoText += "Podobieństwo pikselami (+/- " + SIGMA + "): " + result + "% <br>";
//        add(info);

        result = originHandler.compare(currentHandler, ImageProjectionComparer.class, this, patterns);

        infoText += "Podobieństwo projekcji: " + result + "% </html>";

        info.setText(infoText);
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
