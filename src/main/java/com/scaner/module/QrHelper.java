/**
 * =================================
 * 版权所属：卡荟（中国）有限责任公司
 * Copyright (c) 2015年 Cardapp(China) Limited Company. All rights reserved.
 *
 * @author Woode Wang
 * E-mail:wangwoode@qq.com
 * tel：18718575523
 * @version 创建时间：2015年4月24日 下午2:38:06
 * ==================================
 */
package com.scaner.module.helper;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author Woode Wang
 * @description <b>【功能】</b><br>
 * <p/>
 * <p/>
 * <b>【使用方法】</b><br>
 * <p/>
 * <p/>
 * <b>【注】</b><br>
 * <p/>
 * <p/>
 * @since 1.0.0
 */
public class QrHelper {
    @SuppressLint("TrulyRandom")
    public static String encryptText(String plainText, String encryptKey) {

        // String to be encoded with Base64

        String encryptedText = null;
        try {
            // DES算法
            byte[] keyBytes = encryptKey.getBytes("UTF8");
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            // ENCODE plainTextPassword String
            // F16C03A7849D102D,2,2014-10-28-14-10-11
            byte[] plainBytes = plainText.getBytes("UTF8");

            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread
            // safe
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypedBytes = cipher.doFinal(plainBytes);
            // ZN0BsY5mlJLhIoUfPugbI9Q8IbjbxNFh33P/hSgHqHop5+7rUNY8PQ==
            // String encryptedPwd = base64encoder.encode(doFinal);
            encryptedText = Base64
                    .encodeToString(encrypedBytes, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return encryptedText;
    }

    public static String decryptText(String encryptedText, String encryptKey) {

        // DECODE encryptedPwd String

        String plainText = null;
        try {
            byte[] encrypedPwdBytes = Base64.decode(encryptedText,
                    Base64.DEFAULT);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            byte[] key = encryptKey.getBytes("UTF8");
            DESKeySpec keySpec = new DESKeySpec(key);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES");// cipher is not thread
            // safe
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] plainBytes = cipher.doFinal(encrypedPwdBytes);
            plainText = new String(plainBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return plainText;
    }

    public static Bitmap createQrBitmap(String qrCode) {
        try {
            return createBarcodeBitmap(qrCode, BarcodeFormat.QR_CODE);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createBarcodeBitmap(String qrCode, BarcodeFormat format)
            throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        // hst配合EncodeHintType的常量名指定MultiFormatWriter编码的附加参数
        Hashtable<EncodeHintType, Object> hst = new Hashtable<>();
        // Specifies what character encoding to use where applicable (type
        // String)指定字符编码使用什么样的适用（ String类型）
        // 指定UTF-8的字符编码方式
        hst.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // Encode a barcode using the default settings.
        // contents - The contents to encode in the barcode
        // format - The barcode format to generate
        // width - The preferred width in pixels 以像素为单位的首选宽度
        // height - The preferred height in pixels
        // hints - Additional parameters to supply to the encoder提示 - 附加参数提供给编码器
        BitMatrix matrix = writer.encode(qrCode, format, 800, 800, hst);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
