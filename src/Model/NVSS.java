/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.google.zxing.WriterException;

/**
 *
 * @author Rachael
 */
public class NVSS {

    private MyImage secretImage; //secret image
    private MyImage generatedShare;
    private MyImage[] sharesUsed; //the images used as shares
    private QRCodeController qrControl;
    private Tools tool;

    /**
     * Constructor of this class
     *
     * @param n number of shares that is used
     */
    public NVSS(int n) {
        this.sharesUsed = new MyImage[n - 1];
        this.qrControl = new QRCodeController();
        this.tool = new Tools();
    }

    /**
     *
     * @param b size of block pixel, has to be an even number
     * @param pNoise probability of adding a noise
     * @param seed value that is used to generate random sequence number
     */
    public void featureExraction(int b, double pNoise, int seed) throws IOException {
        for (int i = 0; i < this.sharesUsed.length; i++) {
            this.sharesUsed[i].featureExtraction(b, pNoise, seed);
//            this.sharesUsed[i].saveImage("share" + i + ".png");
        }
    }

//    /**
//     * Method to perform XOR operation between two MyImages.
//     * XOR is done for each pixel in each color component
//     * @param a the first MyPicture
//     * @param b the second MyPicture
//     * @return the resulting MyPicture
//     */
//    private MyImage XOR(MyImage a, MyImage b) {
//        return this.tool.xorOperation(a, b);
//    }
    /**
     * Generated share attribute's accessor
     *
     * @return the generated share
     */
    public MyImage getGeneratedShare() throws IOException {
        return this.generatedShare;
    }

    /**
     * Generated share attribute's mutator
     *
     * @param generatedShare share to be assigned
     */
    public void setGeneratedShare(MyImage generatedShare) {
        this.generatedShare = generatedShare;
    }

    /**
     * Method to produce QR Code from string data
     *
     * @param data string that is to be hidden in QR Code
     */
    public void generateQRCode(String data) throws WriterException, IOException {
        this.qrControl.generateQRCode(data);
    }

    /**
     * Method to read QR Code and produce string
     *
     * @param filePath Path of the QR Code
     * @return String that is in the QR Code
     */
    public String readQRCode(String filePath) throws ChecksumException, FormatException, IOException, NotFoundException {
        return this.qrControl.readQRCode(filePath);
    }

    /**
     * Method used to encrypt or decrypt, depending on the input.
     *
     * @param used The first image to be used. In encryption, the input should
     * be the secret image. In decryption, the input should be the QR Code.
     * @param fileName the resulting MyImage's file name which is used when
     * saving the file in computer
     * @return the resulting MyImage
     * @throws IOException
     */
    public MyImage encryptDecrypt(MyImage used, String fileName) throws IOException {
        MyImage temp = used;
        for (int i = 0; i < this.sharesUsed.length; i++) {
            temp = this.tool.xorOperation(temp, this.sharesUsed[i]);
        }
        temp.saveImage(fileName);
        return temp;
    }

    /**
     * Secret Image accessor
     *
     * @return secret secret image
     */
    public MyImage getSecretImage() {
        return secretImage;
    }

    /**
     * Secret image mutator
     *
     * @param secret secret image
     */
    public void setSecretImage(MyImage secret) {
        secretImage = secret;
    }

    /**
     * Method to convert the generated share's pixel value, which is in binary,
     * to decimal. This method convert each 16-digit binary to 5-digit decimal
     *
     * @return String of decimal
     */
    public String convertToDecimal() {
        return tool.convertToDecimal(this.generatedShare.getStringOfPixelValue());
    }

    /**
     * Method to convert the input string, which is in decimal, to string of
     * binary
     *
     * @param input String of decimal to be converted to binary
     * @return String of binary
     */
    public String convertToBinary(String input) {
        return this.tool.convertToBinary(input);
    }

    /**
     * Method to produce MyImage from the binaryString string. Used in
     * decryption process to create share from the extracted String
     *
     * @param binaryString String of binary to be converted back to MyImage
     * @return the resulting MyImage
     */
    public MyImage buildShare(String binaryString) throws IOException {
        String holder = binaryString;
        int size = (int) Math.sqrt(holder.length() / 24);
        int[][][] coloredFeatureMatrix = new int[size][size][3];
        for (int i = 0; i < coloredFeatureMatrix.length; i++) {
            for (int j = 0; j < coloredFeatureMatrix[0].length; j++) {
                for (int k = 0; k < coloredFeatureMatrix[0][0].length; k++) {
                    String temp = holder.substring(0, 8);
                    holder = holder.substring(8);
                    int num = Integer.parseInt(temp, 2);
                    coloredFeatureMatrix[i][j][k] = num;
                }
            }
        }
        BufferedImage newImage = new BufferedImage(coloredFeatureMatrix.length, coloredFeatureMatrix[0].length, BufferedImage.TYPE_INT_RGB);
        MyImage constructedImage = new MyImage(newImage);
        constructedImage.setColoredFeatureMatrix(coloredFeatureMatrix);
        return constructedImage;
    }

    /**
     * Shares' accessor
     *
     * @return the shares
     */
    public MyImage[] getSharesUsed() {
        return sharesUsed;
    }

    /**
     * Share's mutator
     *
     * @param sharesUsed the shares that are used
     */
    public void setSharesUsed(MyImage[] sharesUsed) {
        this.sharesUsed = sharesUsed;
    }

    public double calculatePSNR(MyImage source, MyImage manipulated) {
        double res = 0.0, mseRed = 0.0, mseGreen = 0.0, mseBlue = 0.0;
        int tempRed = 0, tempGreen = 0, tempBlue = 0;
        for (int i = 0; i < source.getColoredFeatureMatrix().length; i++) {
            for (int j = 0; j < source.getColoredFeatureMatrix()[0].length; j++) {
                tempRed += Math.pow((source.getColoredFeatureMatrix()[i][j][0] - manipulated.getColoredFeatureMatrix()[i][j][0]), 2);
                tempGreen += Math.pow((source.getColoredFeatureMatrix()[i][j][1] - manipulated.getColoredFeatureMatrix()[i][j][1]), 2);
                tempBlue += Math.pow((source.getColoredFeatureMatrix()[i][j][2] - manipulated.getColoredFeatureMatrix()[i][j][2]), 2);
            }
        }
        double size = source.getColoredFeatureMatrix().length * source.getColoredFeatureMatrix()[0].length * 1.0;
        mseRed = tempRed / size;
        mseGreen = tempGreen / size;
        mseBlue = tempBlue / size;
        double mse = (mseRed + mseGreen + mseGreen) / 3;
        System.out.println("SME Red: " + mseRed + ", SME Green: " + mseGreen + ", SME Blue: " + mseBlue + ", mse: " + mse);
        res = (20 * Math.log10(255)) - (10 * Math.log10(mse));
        return res;
    }
}
