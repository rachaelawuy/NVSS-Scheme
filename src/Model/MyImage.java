/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Rachael
 */
public class MyImage {

    private int[][][] coloredFeatureMatrix; //keeps the value of each pixel in each color component

    /**
     * Constructor of MyImage class
     *
     * @param input BufferedImage that's to be converted to MyImage
     */
    public MyImage(BufferedImage input) {
        coloredFeatureMatrix = new int[input.getHeight()][input.getWidth()][3];
        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
                int rgb = input.getRGB(i, j);
                Color temp = new Color(rgb);
                this.coloredFeatureMatrix[j][i][0] = temp.getRed();
                this.coloredFeatureMatrix[j][i][1] = temp.getGreen();
                this.coloredFeatureMatrix[j][i][2] = temp.getBlue();
            }
        }
    }

    /**
     * Method to extract feature from share
     *
     * @param b size of block pixel, has to be an even number
     * @param pNoise probability of adding a noise
     * @param seed value that is used to generate random sequence number
     */
    public void featureExtraction(int b, double pNoise, int seed) {
        Random random = new Random(seed);
        int[][] featureMatrix = new int[this.coloredFeatureMatrix.length][this.coloredFeatureMatrix[0].length]; //contains binary number
        int[][] imgTemp = new int[this.coloredFeatureMatrix.length][this.coloredFeatureMatrix[0].length]; //contains number that is the sum of each color component
        int[] medianTemp = new int[b * b]; //contains values in a pixel block
        int counter = 0; //counter used to save matrix in the medianTemp array 
        int white = 0, equal = 0;
        //first pair of for is for the matrix row and column iteration
        for (int i = 0; i < imgTemp.length; i++) {
            for (int j = 0; j < imgTemp[0].length; j++) {
                //the next pair of for is to iterate the block
                for (int k = 0; k < b; k++) {
                    for (int l = 0; l < b; l++) {
                        int value = this.coloredFeatureMatrix[i + k][j + l][0] + this.coloredFeatureMatrix[i + k][j + l][1] + this.coloredFeatureMatrix[i + k][j + l][2];
                        imgTemp[i + k][j + l] = value;
                        medianTemp[counter] = imgTemp[i + k][j + l];
//                        pixelValue[i + k][j + l] = value;
                        counter++;
                    }
                }
                counter = 0;
                double medianX = getMedian(medianTemp, b);
                for (int k = 0; k < b; k++) {
                    for (int l = 0; l < b; l++) {
                        if ((double) imgTemp[i + k][j + l] >= medianX) {
                            featureMatrix[i + k][j + l] = 1;
                            white += featureMatrix[i + k][j + l];
                        } else {
                            featureMatrix[i + k][j + l] = 0;
                        }
                        if ((double) imgTemp[i + k][j + l] == medianX) {
                            equal++;
                        }
                    }
                }
                //end of featureExtraction process
                //start of stabilization process
                int counterQS = white - (int) (Math.pow(b, 2) / 2);//number of stabilization process(es) to be done
                if (counterQS > 0 && equal > 0) {
                    int[] x = new int[equal];//to save the x coordinate of number with the same value of median
                    int[] y = new int[equal];//to save the y coordinate of number with the same value of median
                    int indexCounter = 0; //index for array which saves the coordinate of the value that's the same as the median
                    for (int k = 0; k < b; k++) {
                        for (int l = 0; l < b; l++) {
                            if (imgTemp[i + k][j + l] == medianX) {
                                x[indexCounter] = i + k;
                                y[indexCounter] = j + l;
                                indexCounter++;
                            }
                        }
                    }
//                    System.out.println(tempIndex);
                    while (counterQS > 0) {
                        int randomPixel = random.nextInt(x.length); //choosing which pixel whose value will be changed
                        featureMatrix[x[randomPixel]][y[randomPixel]] = 0;
                        counterQS--;
                    }
//                    counter = 0;
                }
                medianTemp = new int[b * b];
                white = 0;
                equal = 0;
                //end of stabilization process
                //start of chaos process
                int qc = (int) (Math.pow(b, 2) / 2 * pNoise);
                int minRow = i, maxRow = i + b; //to hold the max and min coordinate for the block's row
                int minCol = j, maxCol = j + b; //to hold the max and min coordinate for the block's column
                int tempCol = 0, tempRow = 0;
                while (qc > 0) {
                    int randomCol = random.nextInt(maxCol - minCol) + minCol;
                    int randomRow = random.nextInt(maxRow - minRow) + minRow;
                    if (featureMatrix[randomRow][randomCol] == 1) {
                        tempCol = randomCol;
                        tempRow = randomRow;
                        randomCol = random.nextInt(maxCol - minCol) + minCol;
                        randomRow = random.nextInt(maxRow - minRow) + minRow;
                        while (featureMatrix[randomRow][randomCol] != 0) {
                            randomCol = random.nextInt(maxCol - minCol) + minCol;
                            randomRow = random.nextInt(maxRow - minRow) + minRow;
                        }
                        featureMatrix[tempRow][tempCol] = 0;
                        featureMatrix[randomRow][randomCol] = 1;
                    } else {
                        tempCol = randomCol;
                        tempRow = randomRow;
                        randomCol = random.nextInt(maxCol - minCol) + minCol;
                        randomRow = random.nextInt(maxRow - minRow) + minRow;
                        while (featureMatrix[randomRow][randomCol] != 1) {
                            randomCol = random.nextInt(maxCol - minCol) + minCol;
                            randomRow = random.nextInt(maxRow - minRow) + minRow;
                        }
                        featureMatrix[tempRow][tempCol] = 1;
                        featureMatrix[randomRow][randomCol] = 0;
                    }
                    qc--;
                }
                j += (b - 1);
            }
            i += (b - 1);
        }
        convertToRGB(featureMatrix);
    }

    /**
     * Method used to save the actual image into computer
     *
     * @param name of the file
     */
    public void saveImage(String name) throws IOException {
        BufferedImage share = new BufferedImage(this.coloredFeatureMatrix.length, this.coloredFeatureMatrix[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < this.coloredFeatureMatrix.length; i++) {
            for (int j = 0; j < this.coloredFeatureMatrix[0].length; j++) {
                int rgb, red, green, blue;
                red = this.coloredFeatureMatrix[i][j][0];
                green = this.coloredFeatureMatrix[i][j][1];
                blue = this.coloredFeatureMatrix[i][j][2];
                rgb = new Color(red, green, blue).getRGB();
//                System.out.println("red: " + red + ", green: " + green + ", blue: " + blue + ", rgb: " + rgb);
                share.setRGB(j, i, rgb);
            }
        }
        File outputfile = new File(name);
        ImageIO.write(share, "png", outputfile);
    }

    /**
     * Method used to get median in a block pixel
     *
     * @param array of number
     * @param b size
     * @return median of the list of number
     */
    private double getMedian(int[] number, int b) {
        double res = 0.0;
        Arrays.sort(number);
        res = (number[(b * b / 2) - 1] + number[(b * b / 2)]) / (2 * 1.0);
        return res;
    }

    /**
     * Matrix accessor
     *
     * @return feature matrix attribute of this image
     */
    public int[][][] getColoredFeatureMatrix() {
        return coloredFeatureMatrix;
    }

    /**
     * Convert 2-bit to 24-bit Used only to convert the result of feature
     * extraction that is in 2-bit
     *
     * @param featureMatrix that is in 2-bit
     */
    private void convertToRGB(int[][] featureMatrix) {
        coloredFeatureMatrix = new int[featureMatrix.length][featureMatrix[0].length][3];
        //the first iteration is for the row 
        for (int i = 0; i < featureMatrix.length; i++) {
            //the second is for the column
            for (int j = 0; j < featureMatrix[0].length; j++) {
                //iteration for each of the color component
                for (int k = 0; k < 3; k++) {
                    //last iteration for the bit-plane
                    for (int l = 0; l < 8; l++) {
                        coloredFeatureMatrix[i][j][k] += featureMatrix[i][j] * Math.pow(2, l);
                    }
                }
            }
        }
    }

    /**
     * Convert the value matrix to binary String
     *
     * @return array of string that's created
     */
    public String getStringOfPixelValue() {
        String res = "";
        for (int i = 0; i < this.coloredFeatureMatrix.length; i++) {
            String holder = "";
            for (int j = 0; j < this.coloredFeatureMatrix[0].length; j++) {
                for (int k = 0; k < this.coloredFeatureMatrix[0][0].length; k++) {
                    String x = this.coloredFeatureMatrix[i][j][k] + "";
                    holder = Integer.toBinaryString(Integer.parseInt(x));
                    int difference = 8 - holder.length();
                    while (difference > 0) {
                        res = res.concat("0");
                        difference -= 1;
                    }
                    res = res.concat(holder);
                }
            }
        }
        return res;
    }

    /**
     * Matrix mutator
     *
     * @param desiredMatrix value of matrix
     */
    public void setColoredFeatureMatrix(int[][][] desiredMatrix) {
        this.coloredFeatureMatrix = desiredMatrix;
    }

//    public void printPixelValue() {
//        for (int i = 0; i < coloredFeatureMatrix.length; i++) {
//            //the second is for the column
//            for (int j = 0; j < coloredFeatureMatrix[0].length; j++) {
//                //iteration for each of the color component
//                for (int k = 0; k < 3; k++) {
//                    if (k < 3 - 1) {
//                        System.out.print(coloredFeatureMatrix[i][j][k] + ",");
//                    } else {
//                        System.out.print(coloredFeatureMatrix[i][j][k]);
//                    }
//                }
//                if (j < coloredFeatureMatrix[0].length - 1) {
//                    System.out.print("   ");
//                } else {
//                    System.out.print("");
//                }
//            }
//            System.out.println("");
//        }
//        System.out.println("--------------------------");
//    }
}
