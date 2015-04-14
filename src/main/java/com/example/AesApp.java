package com.example;

import javax.crypto.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


/**
 * Created by coopja on 4/10/15.
 */
public class AesApp {
    public static void main(String[] args) {
        System.out.println(AesApp.class.getName() + ": Start");
        try {
            SecretKey secret = getSecretKey(getKeySize(args));
            byte[] encryptedData = encrypt(secret, "Hello World".getBytes());
            System.out.println("Encrypted Data: " + new String(encryptedData));
            byte[] decryptedData = decrypt(secret, encryptedData);
            System.out.println("Decrypted Data: " + new String(decryptedData));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(AesApp.class.getName() + ": Finish");
        }
    }

    private static int getKeySize(String[] args){
        int keySize = 128;
        for(int i = 0; i < args.length; i++){
            if((args[i]).contains("-b")){
                keySize = Integer.valueOf(args[i+1]);
                break;
            }
        }
        return keySize;
    }

    /**
     * Decrypt a black of data with a given secret key
     *
     * @param secret The key
     * @param encryptedData The byte array to be decrypted
     */
    private static byte[] decrypt(SecretKey secret, byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt a black of data with a given secret key
     *
     * @param secret The key
     * @param message The byte array to be encrypted
     * @return the encrypted array
     */
    private static byte[] encrypt(SecretKey secret, byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a simple key
     *
     * @param keySize The keyspace size
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static SecretKey getSecretKey(int keySize) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(keySize);
        return kgen.generateKey();
    }
}
