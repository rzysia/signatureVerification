/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rzysia.signatureVerification.interfaces;

import pl.rzysia.signatureVerification.ImageHandler.ImageHandler;

/**
 *
 * @author comarch
 */
public interface SignatureComparer {
    int compare(ImageHandler current);
    boolean isAccessGranted(ImageHandler current);
}
