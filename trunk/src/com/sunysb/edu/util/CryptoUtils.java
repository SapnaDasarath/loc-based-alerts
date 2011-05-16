package com.sunysb.edu.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

	private static String PUBLIC_KEY_SIGN = "";
	private static String PRIVATE_KEY_SIGN = "";

	private static String PUBLIC_KEY_ENC = "";
	private static String PRIVATE_KEY_ENC = "";

	public CryptoUtils() {
		
	}

	public static String generateSessionKey() {
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			SecretKey key = keyGen.generateKey();
			return byteArrayToHexString(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Generate private public key pair
	public static HashMap<String, String> generateKeyPair() {
		HashMap<String, String> map = new HashMap<String, String>();
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			KeyPair keypair = keyGen.genKeyPair();
			PrivateKey privateKey = keypair.getPrivate();
			byte[] keyPrivate = privateKey.getEncoded();
			String privStr = byteArrayToHexString(keyPrivate);
			
			PublicKey pubkey = keypair.getPublic();
			byte[] keyPublic = pubkey.getEncoded();
			String publicStr = byteArrayToHexString(keyPublic);

			map.put(StringUtil.PRIVATE_KEY, privStr);
			map.put(StringUtil.PUBLIC_KEY, publicStr);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return map;
	}

	// Give its public key
	public static String getPublicKeyInStringForSigning() {
		return PUBLIC_KEY_SIGN;
	}

	public static byte[] getPublicKeyinByteArrayForSigning() {
		return hexStringToByteArray(PUBLIC_KEY_SIGN);
	}

	// Give its public key
	public static String getPublicKeyInStringForEncryption() {
		return PUBLIC_KEY_ENC;
	}

	public static byte[] getPublicKeyinByteArrayForEncryption() {
		return hexStringToByteArray(PUBLIC_KEY_ENC);
	}

	// Given a message sign it using its own private key
	public static String getSignedMessage(String data, final String privatekey) {
		try {
			PrivateKey key = new PrivateKey() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getFormat() {
					return null;
				}

				@Override
				public byte[] getEncoded() {
					return hexStringToByteArray(privatekey);
				}

				@Override
				public String getAlgorithm() {
					return "RSA";
				}
			};

			Signature sig = Signature.getInstance("MD5WithRSA");
			sig.initSign(key);
			byte[] dataBytes = hexStringToByteArray(data);
			sig.update(dataBytes);
			byte[] signatureBytes = sig.sign();
			return byteArrayToHexString(signatureBytes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Given a public key validate a message against the owners public key
	public static boolean validateSignedMessage(String signedMsg, final String publicKey) {
		try {
			PublicKey pubKey = new PublicKey() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getFormat() {
					return null;
				}

				@Override
				public byte[] getEncoded() {
					return hexStringToByteArray(publicKey);
				}

				@Override
				public String getAlgorithm() {
					return "RSA";
				}
			};
			Signature sig = Signature.getInstance("MD5WithRSA");
			sig.initVerify(pubKey);

			byte[] dataBytes = hexStringToByteArray(signedMsg);
			sig.update(dataBytes);
			byte[] signatureBytes = sig.sign();
			return sig.verify(signatureBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Given an encrypted message decrypt it using its own private key
	public static String getEncryptedMessage(String message, final String publicKey) {
		try {
			byte[] input = message.getBytes();
			byte[] cipherText = new byte[input.length];
			byte[] keyBytes = hexStringToByteArray(publicKey);
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);

			return byteArrayToHexString(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Given a message encrypt it using its own private key
	// Given an encrypted message decrypt it using its own private key
	public static String getDecryptedMessage(String message, final String privateKey) {
		try {
			byte[] cipherText = message.getBytes();
			byte[] keyBytes = hexStringToByteArray(privateKey);
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");

			byte[] plainText = new byte[cipherText.length];
			cipher.init(Cipher.DECRYPT_MODE, key);
			int ptLength = cipher.update(cipherText, 0, cipherText.length,
					plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
			return byteArrayToHexString(plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	// http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	public static String byteArrayToHexString(byte[] byteData) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return sb.toString();
	}

	public static void main(String[] args)
	{
		HashMap<String,String> sign = CryptoUtils.generateKeyPair();
		System.out.println("Signature");
		System.out.println("Private= " + sign.get("Private"));
		System.out.println("Public= " + sign.get("Public"));
		
		System.out.println("Enc/dec");
		HashMap<String,String>  encDec = CryptoUtils.generateKeyPair();
		System.out.println("Private= " + encDec.get("Private"));
		System.out.println("Public= " + encDec.get("Public"));
	}
}
