package com.tobyschwartz;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Encrypt {

    private static SecretKeySpec generateStrongPasswordSpec(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, 192); //recommended key length
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(hash, "AES");
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private static Cipher createCipher(int operation, SecretKeySpec secretKeySpec, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        if (operation == Cipher.ENCRYPT_MODE) {
            cipher.init(operation, secretKeySpec, iv);
        } else if (operation == Cipher.DECRYPT_MODE) {
            cipher.init(operation, secretKeySpec, iv);
        } else {
            throw new IllegalArgumentException(String.format("Illegal argument %s provided as an operation!", operation));
        }
        return cipher;
    }

    public static void encryptFile(char[] password, File inputFile, File outputFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        long start = System.currentTimeMillis();
        System.out.println("Starting encryption process...");

        IvParameterSpec iv = new IvParameterSpec(getSalt());

        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, generateStrongPasswordSpec(password, iv.getIV()), iv);

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[8192];
        int bytesRead;

        outputStream.write(iv.getIV());

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
        long end = System.currentTimeMillis();
        System.out.printf("Encryption finished! Process took %s seconds!%n", (end - start) / 1000);
    }

    public static void decryptFile(char[] password, File inputFile, File outputFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        long start = System.currentTimeMillis();
        System.out.println("Starting decryption process...");

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[8192];
        int bytesRead;

        // Read the salt from the input file
        byte[] salt = new byte[16];
        bytesRead = inputStream.read(salt);
        if (bytesRead != salt.length) {
            throw new IOException("Invalid salt size");
        }

        IvParameterSpec iv = new IvParameterSpec(salt);
        Cipher cipher = createCipher(Cipher.DECRYPT_MODE, generateStrongPasswordSpec(password, salt), iv);

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }

        inputStream.close();
        outputStream.close();

        long end = System.currentTimeMillis();
        System.out.printf("Decryption finished! Process took %s seconds!%n", (end - start) / 1000);
    }
}
