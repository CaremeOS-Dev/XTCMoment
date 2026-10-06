package com.xtc.utils.encode;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/** RSA helpers for the encrypted request headers. */
public class RSAUtil {

    /** Built-in public key shipped with the app. */
    public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfQz49r6aXpY49YPr3a9n1PHUy/bYzufv/Ag3Cdv1GLtugWy3qYBE6uj2CrRuBGgaWlmTtWpw9EmQtnen4xsKjxw8eqqNqud+pAUEG4k2YLEBe79MOuslld6R6vT+a9kvpY60rGO7/Pm+x8fVyCZvIQouzbf+T2b/GDTU0XNJR0wIDAQAB";

    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /** Generates the built-in key pair used by the network layer at startup. */
    public static void initDefaultKeys(String[] ignored) throws Exception {
        encryptWithPrivateKey("heal", "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAOD5Y19YslL7R9Srz33ra/2+RVGinKz6r1bGS8Rcr7a2/zPLvkGpscMdrpFiXFIUXLTnFLPjGNNpvkqfJKKCuHMCAwEAAQ==");
        decryptWithPrivateKey("X521AF2AvjRd4FCBPBKSzF+zWpgknWjKygyQFgHgY2JSpc21rTJh3uc39kV4BXvivc3X6QNGnjXPnIgH1f6epg==", "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAovOBRlkhAd7M3RBJzVlvwvxyxhMjksJmCNwdWGQF4n2RgLEKzc3b9wuOs9Q3BOEFjimDiXnd9N2iP9uYP7a/VQIDAQABAkB64BoMfSs5qNNco2qzkYyIQSsfF9GMWlDsv2bVf188oPU5UQBkDPT239NLe6wiqFCB3X5+jqqxcBlWkVDeOSYlAiEA0+WvYfhXw/FiyCNxxJ6hblhRfQ/CrRKMz89BlC3t+FsCIQDE3eBeL90/OLS8X3qAIA3wChON4VmGzVznRWk0ssY2DwIhAITGxfET1pr3ZLiYTS+xXuJwAQ/mkkw09Xs6GZOqfBVFAiAmhmj25ZT9X0J3LpQRaLRxifdDp5rWd2+7zmiFKIsDXwIhAI+t4kFXZIGr0HE8DmkKjhTmMiVNsPOYswNumeh33XT5");
    }

    /** Encrypts with the private key, Base64 encoded. */
    public static String encryptWithPrivateKey(String text, String privateKeyBase64) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, parsePrivateKey(privateKeyBase64));
            return Base64.encodeToString(cipher.doFinal(text.getBytes(UTF_8)), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Encrypts with the public key, Base64 encoded. */
    public static String encryptWithPublicKey(String text, String publicKeyBase64) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, parsePublicKey(publicKeyBase64));
            return Base64.encodeToString(cipher.doFinal(text.getBytes(UTF_8)), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Decrypts a Base64 payload with the private key. */
    public static String decryptWithPrivateKey(String base64Text, String privateKeyBase64) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, parsePrivateKey(privateKeyBase64));
            return new String(cipher.doFinal(Base64.decode(base64Text, Base64.DEFAULT)), UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Decrypts a Base64 payload with the public key. */
    public static String decryptWithPublicKey(String base64Text, String publicKeyBase64) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, parsePublicKey(publicKeyBase64));
            return new String(cipher.doFinal(Base64.decode(base64Text, Base64.DEFAULT)), UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Verifies a SHA1withRSA signature over {@code text}. */
    public static boolean verifySignature(String text, String signatureBase64, String publicKeyBase64) {
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyBase64, Base64.DEFAULT)));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(text.getBytes());
            return signature.verify(Base64.decode(signatureBase64, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static PublicKey parsePublicKey(String base64) throws Exception {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64Util.decode(base64)));
    }

    private static PrivateKey parsePrivateKey(String base64) throws Exception {
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64Util.decode(base64)));
    }
}