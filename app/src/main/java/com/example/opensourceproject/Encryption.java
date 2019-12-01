package com.example.opensourceproject;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    private static String masking(String pre_string) {
        String result = "";
        char[] charList = pre_string.toCharArray();
        for(int i=0;i<pre_string.length()-2;i++) {
            charList[i] = '*';
        }
        result = new String(charList);
        return result;
    }

//    public static byte[] encrypt(byte[] data) {
//        byte[] enc = new byte[data.length];
//        for(int i=0;i<data.length;i++) {
//            enc[i] = (byte) ((i % 2 == 0) ? data[i] + 1 : data[i] - 1);
//
//        }
//        return enc;
//    }
//
//    public static byte[] decrypt(byte[] data) {
//        byte[] dec = new byte[data.length];
//        for(int i=0;i<data.length;i++) {
//            dec[i] = (byte) ((i % 2 == 0) ? data[i] - 1 : data[i] + 1);
//        }
//        return dec;
//    }

    public static byte[] createAESKey(String andAttribute) {
        String[] temp = andAttribute.split(",");
        StringBuilder keyBuilder = new StringBuilder();
        for(int i=0;i<temp.length;i++) {
            keyBuilder.append(temp[i].substring(0,1));
        }
        byte[] aesKey = keyBuilder.toString().getBytes();

        return aesKey;
    }

    public static String encrypt(String plainText, String attribute) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key secureKey = new SecretKeySpec(createAESKey(attribute), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        byte[] encryptedText = cipher.doFinal(plainText.getBytes());
        String encryptResult = new String(encryptedText);

        return encryptResult;
    }

    public static String decrypt(String encryptedText, String attribute) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key secureKey = new SecretKeySpec(createAESKey(attribute), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        byte[] decryptedText = cipher.doFinal(encryptedText.getBytes());
        String decryptResult = new String(decryptedText);

        return decryptResult;
    }
}
