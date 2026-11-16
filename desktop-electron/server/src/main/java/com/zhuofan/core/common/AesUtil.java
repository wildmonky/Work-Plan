package com.zhuofan.core.common;


import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/**
 * AES的加密和解密
 * @author libo
 */
public class AesUtil {


    public static String encrypt(String key,String initVector,String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //解密
    public static String decrypt(String key,String initVector,String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //主方法
    public static void main(String[] args) {

        String originalString = "password";
        System.out.println("Original String to encrypt - " + originalString);
        String encryptedString = encrypt("aesEncryptionKey","encryptionIntVec",originalString);
        System.out.println("Encrypted String - " + encryptedString);
        String decryptedString = decrypt("aesEncryptionKey","encryptionIntVec",encryptedString);
        System.out.println("After decryption - " + decryptedString);
    }
}