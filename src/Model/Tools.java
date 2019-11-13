/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.image.BufferedImage;

/**
 *
 * @author Rachael
 */
public class Tools {

    public Tools() {
    }

    public MyImage xorOperation(MyImage first, MyImage second) {
        BufferedImage newImage = new BufferedImage(first.getColoredFeatureMatrix().length, first.getColoredFeatureMatrix()[0].length, BufferedImage.TYPE_INT_RGB);
        MyImage result = new MyImage(newImage);
        int h = first.getColoredFeatureMatrix().length, w = first.getColoredFeatureMatrix()[0].length, e = first.getColoredFeatureMatrix()[0][0].length;
        int[][][] temp = new int[h][w][e];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                for (int k = 0; k < e; k++) {
                    temp[i][j][k] = first.getColoredFeatureMatrix()[i][j][k] ^ second.getColoredFeatureMatrix()[i][j][k];
                }
            }
        }
        result.setColoredFeatureMatrix(temp);
        return result;
    }

    public String convertToDecimal(String binaryString) {
        String fqr = binaryString, qr = "";
        while (!fqr.isEmpty()) {
            String temp = fqr.substring(0, 16);
            fqr = fqr.substring(16);
            int num = 0;
            for (int i = 0; i < 16; i++) {
                int x = (temp.charAt(i) - '0'); //substract by char 0, because the temp is in ascii
                num += x * Math.pow(2, i);
            }
            String addition = "";
            int length = String.valueOf(num).length();
            while (length < 5) {
                addition = addition.concat("0");
                length += 1;
            }
            qr = qr.concat(addition.concat(num + ""));
        }
        return qr;
    }
    
    public String convertToBinary(String decimalString) {
        String res = "", temp = decimalString;
        while (!temp.isEmpty()) {
            String holder = temp.substring(0, 5);
            temp = temp.substring(5);
            String num = Integer.toBinaryString(Integer.parseInt(holder));
            int difference = 16 - num.length();
            StringBuilder sb = new StringBuilder(num);
            sb.reverse();
            while (difference > 0) {
                sb.append("0");
                difference -= 1;
            }
            res = res.concat(sb.toString());
        }
        return res;
    }
}
