/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.MyImage;
import Model.NVSS;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import java.io.IOException;

/**
 *
 * @author Rachael
 */
public class Controller {

    private NVSS nvss;
    
    /**
     * Constructor of the class.
     * Automatically create a new NVSS class
     * @param n shares' size
     */
    public Controller(int n) {
        this.nvss= new NVSS(n);
    }

    /**
     * Calls NVSS class' feature extraction method
     * @param b size of block pixel, has to be an even number
     * @param pNoise probability of adding a noise
     * @param seed value that is used to generate random sequence number
     */
    public void featureExtraction(int b, double pNoise, int seed) throws IOException {
        nvss.featureExraction(b, pNoise, seed);
    }

    /**
     * Method to appoint the input MyImage as the secret image.
     * Calls NVSS class' secret image mutator
     * @param s Secret image
     */
    public void setSecretImage(MyImage s) {
        nvss.setSecretImage(s);
    }
    
    public MyImage getSecretImage(){
        return nvss.getSecretImage();
    }
    
    public void setShare(MyImage[] shares){
        this.nvss.setSharesUsed(shares);
    }

    /**
     * Method to encrypt and automatically creates QR Code
     * @return true if the QR Code is successfully constructed, false otherwise
     */
    public boolean encrypt() throws IOException, WriterException {
        MyImage temp = nvss.encryptDecrypt(nvss.getSecretImage(), "share.png");
//        temp.printPixelValue();
        nvss.setGeneratedShare(temp);
//        System.out.println("PSNR: "+nvss.calculatePSNR(nvss.getSecretImage(), temp));
        String x= nvss.convertToDecimal();
//        System.out.println(x.length());
        // 7090 because that's the maximum capacity for numerical String
        if(x.length()<7090){
            nvss.generateQRCode(x);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Method to decrypt and automatically creates the MyImage file and saves it on computer
     * @param share the compressed string
     */
    public void decrypt(String share) throws IOException{
        String x=nvss.convertToBinary(share);
        MyImage temp=nvss.buildShare(x);
        MyImage res=nvss.encryptDecrypt(temp, "decrypted.png");
    }
    
    /**
     * Method which reads QR Code from the input path and returns the compressed string
     * @param input QR Code file path
     * @return the compressed string
     */
    public String readQRCode(String input) throws ChecksumException, FormatException, IOException, NotFoundException{
        return this.nvss.readQRCode(input);
    }
}
