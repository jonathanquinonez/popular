package com.popular.android.mibanco.util;

import android.util.Log;

import org.bouncycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private String algorithm = "DESede";
    private Cipher cipher = null;
    private Base64 encoder = new Base64();
    private Base64 decoder = new Base64();
    private static final String CIPHER_INSTANCE_ENCRYPTION = "AES/CBC/PKCS5Padding";

    public CryptoUtils() {
        try {
            this.cipher = Cipher.getInstance(this.algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Log.e("CryptoUtils", ex.toString());
        }

    }

    public String generateKey() {
        try {
            Key key = KeyGenerator.getInstance(this.algorithm).generateKey();
            byte[] keyBytes = key.getEncoded();
            byte[] base64 = this.encoder.encode(keyBytes);
            String encoderString = new String(base64, "UTF-8");
            return encoderString;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Log.e("CryptoUtils", ex.toString());
            return "";
        }
    }

    public String encrypt(String input, String strKey) {
        try {
            byte[] keyBytes = this.decoder.decode(strKey.getBytes());
            byte[] inputBytes = input.getBytes();
            SecretKey key = new SecretKeySpec(keyBytes, this.algorithm);
            this.cipher.init(1, key);
            byte[] encrypted = this.cipher.doFinal(inputBytes);
            byte[] base64 = this.encoder.encode(encrypted);
            String encoderString = new String(base64, "UTF-8");
            return encoderString;
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException ex) {
            Log.e("CryptoUtils", ex.toString());
            return "";
        }
    }

    public String decrypt(String encryption, String strKey) {
        try {
            byte[] keyBytes = this.decoder.decode(strKey.getBytes());
            byte[] encryptionBytes = this.decoder.decode(encryption.getBytes());
            SecretKey key = new SecretKeySpec(keyBytes, this.algorithm);
            this.cipher.init(2, key);
            byte[] recoveredBytes = this.cipher.doFinal(encryptionBytes);
            String recovered = new String(recoveredBytes);
            return recovered;
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException ex) {
            Log.e("CryptoUtils", ex.toString());
            return "";
        }
    }

    public String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_ENCRYPTION);
            cipher.init(2, skeySpec, iv);
            Base64 ed = new Base64();
            byte[] original = cipher.doFinal(ed.decode(encrypted.getBytes()));
            return new String(original);
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException
                | UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException ex) {
            Log.e("CryptoUtils", ex.toString());
            return "";
        }
    }

}
