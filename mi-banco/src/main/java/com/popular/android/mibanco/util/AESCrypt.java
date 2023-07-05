package com.popular.android.mibanco.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {

    private final Cipher cipher;
    private final SecretKeySpec sks;
    private AlgorithmParameterSpec spec;


    public AESCrypt(String password) throws Exception
    {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] pwd = password.getBytes("utf-8");
        messageDigest.update(pwd, 0, pwd.length);

        byte[] keyBytes = new byte[32];
        System.arraycopy(messageDigest.digest(), 0, keyBytes, 0, keyBytes.length);

        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        sks = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    public AlgorithmParameterSpec getIV()
    {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception
    {
        cipher.init(Cipher.ENCRYPT_MODE, sks, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return (new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8"));
    }

    public String decrypt(String cryptedText) throws Exception
    {
        cipher.init(Cipher.DECRYPT_MODE, sks, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        return (new String(decrypted, "UTF-8"));
    }
}
