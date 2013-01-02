package org.geopoke;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;

public class QRGenerator {
    
    public File generateQRCode(Geocache gc) {
        return generateQRCode("http://coord.info/"+gc.getGcNum());
    }

    public File generateQRCode(String text) {
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder encoder = charset.newEncoder();
        byte[] b = null;
        try {
            // Convert a string to UTF-8 bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(text));
            b = bbuf.array();
        } catch (CharacterCodingException e) {
            System.out.println(e.getMessage());
        }
        try {
            String data = new String(b, "UTF-8");
            BitMatrix matrix = null;
            int h = 100;
            int w = 100;
            com.google.zxing.Writer writer = new MultiFormatWriter();
            try {
                HashMap<EncodeHintType, String> hints = new HashMap<>(2);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                matrix = writer.encode(data,
                        com.google.zxing.BarcodeFormat.QR_CODE, w, h, hints);
            } catch (com.google.zxing.WriterException ex) {
                ex.printStackTrace();
                return null;
            }
            try {
                File file = File.createTempFile("qrcode", ".png");
                file.deleteOnExit();
                MatrixToImageWriter.writeToFile(matrix, "PNG", file);
                return file;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}