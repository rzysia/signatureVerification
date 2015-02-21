/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.interfaces;

import java.awt.image.BufferedImage;

/**
 *
 * @author comarch
 */
public interface SignatureComparer {
    int compare(BufferedImage current);
    boolean isAccessGranted(BufferedImage current);
}
