package com.example.opensourceproject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private static char randomChar (Random r, String cs, boolean uppercase) {
        char c = cs.charAt(r.nextInt(cs.length()));
        return uppercase ? Character.toUpperCase(c) : c;
    }

    public static String mask(String str, int seed) {

        final String cons = "bcdfghjklmnpqrstvwxz";
        final String vowel = "aeiouy";
        final String digit = "0123456789";

        Random r = new Random(seed);
        char data[] = str.toCharArray();

        for(int n=0;n<data.length;++n) {
            char ln = Character.toLowerCase(data[n]);
            if(cons.indexOf(ln) >= 0) {
                data[n] = randomChar(r, cons, ln != data[n]);
            } else if(vowel.indexOf(ln) >= 0) {
                data[n] = randomChar(r, vowel, ln != data[n]);
            } else if(digit.indexOf(ln) >= 0) {
                data[n] = randomChar(r, digit, ln != data[n]);
            }
        }
        return new String(data);
    }

    public static String masking(String pre_string) {
        String result = "";
        char[] charList = pre_string.toCharArray();
        for(int i=0;i<pre_string.length()-2;i++) {
            charList[i] = '*';
        }
        result = new String(charList);
        return result;
    }

    public static byte[] createAESKey(String andAttribute) throws UnsupportedEncodingException {
        String[] temp = andAttribute.split(",");
        StringBuilder keyBuilder = new StringBuilder();
        for(int i=0;i<temp.length;i++) {
            keyBuilder.append(temp[i].substring(0,1));
        }
        Integer gap = 16 - keyBuilder.length();
        if(gap !=0) {
            for(int j=0;j<gap;j++) {
                keyBuilder.append("t");
            }
        }
        byte[] aesKey = keyBuilder.toString().getBytes("UTF-8");

        return aesKey;
    }

    public static String encrypt(String plainText, String attribute) throws Exception {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        byte[] saltBytes = bytes;
        PBEKeySpec spec;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        if(attribute.equals("")) {
            spec = new PBEKeySpec("1".toCharArray(), saltBytes, 1000, 256);
        } else {
            spec = new PBEKeySpec(attribute.toCharArray(), saltBytes, 1000, 256);
        }

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        AlgorithmParameters parameters = cipher.getParameters();
        byte[] ivBytes = parameters.getParameterSpec(IvParameterSpec.class).getIV();

        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);

        return Base64.getEncoder().encodeToString(buffer);
    }

    public static String decrypt(String encryptText, String attribute) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encryptText));
        PBEKeySpec spec;

        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes, 0, ivBytes.length);
        byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
        buffer.get(encryoptedTextBytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        if(attribute.equals("")) {
            spec = new PBEKeySpec("1".toCharArray(), saltBytes, 1000, 256);
        } else {
            spec = new PBEKeySpec(attribute.toCharArray(), saltBytes, 1000, 256);
        }

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        byte[] decryptedTextBytes = cipher.doFinal(encryoptedTextBytes);

        return new String(decryptedTextBytes);
    }
}
