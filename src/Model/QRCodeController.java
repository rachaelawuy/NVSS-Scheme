/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 *
 * @author Rachael
 */
public class QRCodeController {

    public QRCodeController() {
    }

    /**
     * Method to produce QR Code from string input
     *
     * @param input string that is to be hidden in QR Code
     */
    public void generateQRCode(String input) throws WriterException, IOException {
        BitMatrix matrix = null;
        int h = 100;
        int w = 100;
        com.google.zxing.Writer writer = new QRCodeWriter();
        try {
            matrix = writer.encode(input,
                    com.google.zxing.BarcodeFormat.QR_CODE, w, h);
        } catch (com.google.zxing.WriterException e) {
            System.out.println(e.getMessage());
        }

        String filePath = "qrcode.png";
//        File file = new File(filePath);
        Path path = Paths.get(filePath);
        try {
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);
            System.out.println("printing to " + path.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//        try {
//            MatrixToImageWriter.writeToFile(matrix, "PNG", file);
//            System.out.println("printing to " + file.getAbsolutePath());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
    }

    /**
     * Method to read QR Code and produce string
     *
     * @param path Path of the QR Code
     * @return String that is in the QR Code
     */
    public String readQRCode(String path) throws ChecksumException, FormatException, IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(path)))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        return qrCodeResult.getText();
    }
}
