package t9.core.util.auth;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.exps.T9InvalidPasswordException;
import t9.core.global.T9Const;
import t9.core.global.T9MessageKeys;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9SecurityUtility;

public class T9Authenticator {
    /**
     * 执行MD5算法生成密码的指纹
     * 
     * @param saltLength
     * @param passWord
     * @return
     * @throws T9InvalidPasswordException
     */
    public static byte[] encrypt(int saltLength, byte[] passWord) throws T9InvalidPasswordException,
            NoSuchAlgorithmException {
        byte[] rtArray = null;
        byte[] salt = new byte[saltLength];
        byte[] digest = null;

        if (passWord == null) {
            throw new T9InvalidPasswordException(T9MessageKeys.COMMON_ERROR_NULL_PASSWORD);
        }

        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw ex;
        }
        md.update(salt);
        md.update(passWord);
        digest = md.digest();

        rtArray = new byte[salt.length + digest.length];
        System.arraycopy(salt, 0, rtArray, 0, salt.length);
        System.arraycopy(digest, 0, rtArray, salt.length, digest.length);
        return rtArray;
    }

    /**
     * 判断用户密码是否合法
     * 
     * @param saltLength
     * @param inputPass
     * @param savedPass
     * @return
     * @throws T9InvalidPasswordException
     * @throws NoSuchAlgorithmException
     */
    public static boolean isValidPassword(String inputPass, String savedPass)
            throws T9InvalidPasswordException, NoSuchAlgorithmException, IOException {

        return isValidPassword(T9SysProps.getInt(T9SysPropKeys.SALT_LENGTH),
                inputPass.getBytes(T9Const.DEFAULT_CODE), new BASE64Decoder().decodeBuffer(savedPass));
    }

    /**
     * 判断用户密码是否合法
     * 
     * @param saltLength
     * @param inputPass
     * @param savedPass
     * @return
     * @throws T9InvalidPasswordException
     * @throws NoSuchAlgorithmException
     */
    public static boolean isValidPassword(byte[] inputPass, byte[] savedPass)
            throws T9InvalidPasswordException, NoSuchAlgorithmException {

        return isValidPassword(T9SysProps.getInt(T9SysPropKeys.SALT_LENGTH), inputPass, savedPass);
    }

    /**
     * 判断用户密码是否合法
     * 
     * @param saltLength
     * @param inputPass
     * @param savedPass
     * @return
     * @throws T9InvalidPasswordException
     * @throws NoSuchAlgorithmException
     */
    public static boolean isValidPassword(int saltLength, byte[] inputPass, byte[] savedPass)
            throws T9InvalidPasswordException, NoSuchAlgorithmException {

        if (savedPass == null || savedPass.length < saltLength) {
            throw new T9InvalidPasswordException(T9MessageKeys.COMMON_ERROR_SAVED_PASS_INVALID);
        }

        byte[] salt = new byte[saltLength];
        System.arraycopy(savedPass, 0, salt, 0, saltLength);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw ex;
        }
        md.update(salt);
        md.update(inputPass);
        byte[] inputDigest = md.digest();
        byte[] savedPassDigest = new byte[savedPass.length - saltLength];
        System.arraycopy(savedPass, saltLength, savedPassDigest, 0, savedPass.length - saltLength);

        if (Arrays.equals(inputDigest, savedPassDigest)) {
            return true;
        }
        return false;
    }

    /**
     * 生成摘要，然后Base64编码
     * 
     * @param saltLength
     * @param passWord
     * @return
     */
    public static String encryptBase64(int saltLength, byte[] passWord) throws Exception {

        return new BASE64Encoder().encode(encrypt(saltLength, passWord));
    }

    /**
     * 生成摘要，然后Base64编码
     * 
     * @param saltLength
     * @param passWord
     * @return
     */
    public static String encryptBase64(int saltLength, String passWord) throws Exception {

        return new BASE64Encoder().encode(encrypt(saltLength, passWord.getBytes(T9Const.DEFAULT_CODE)));
    }

    /**
     * 判断两密码是否匹配
     * 
     * @param saltLength
     * @param inputPassStr
     * @param savedPassStr
     * @return
     * @throws Exception
     */
    public static boolean isValidPasswordBase64(int saltLength, String inputPassStr, String savedPassStr)
            throws Exception {

        byte[] inputPass = inputPassStr.getBytes(T9Const.DEFAULT_CODE);
        byte[] savedPass = new BASE64Decoder().decodeBuffer(savedPassStr);

        return isValidPassword(saltLength, inputPass, savedPass);
    }

    /**
     * 判断两密码是否匹配
     * 
     * @param saltLength
     * @param inputPassStr
     * @param savedPassStr
     * @return
     * @throws Exception
     */
    public static boolean isValidRegist(int saltLength, String inputPassStr, String savedPassStr)
            throws Exception {

        if (saltLength < 2) {
            saltLength = 10;
        }

        byte[] inputPass = inputPassStr.getBytes(T9Const.DEFAULT_CODE);
        byte[] savedPass = new BASE64Decoder().decodeBuffer(savedPassStr);

        return isValidPassword(saltLength - 2, inputPass, savedPass);
    }

    /**
     * 判断两密码是否匹配
     * 
     * @param inputPassStr
     * @param savedPassStr
     * @return
     * @throws Exception
     */
    public static boolean isValidRegist(String inputPassStr, String savedPassStr) throws Exception {

        return isValidRegist(T9SysProps.getInt(T9SysPropKeys.SALT_LENGTH), inputPassStr, savedPassStr);
    }

    /**
     * 加密字符串，生成密文字节
     * 
     * @param srcStr
     * @return 密文字节
     * @throws Exception
     */
    public static byte[] ciphEncryptBytes(String srcStr) throws Exception {
        Cipher cipher = T9SecurityUtility.getPassWordCipher(Cipher.ENCRYPT_MODE);
        byte[] bufBytes = srcStr.getBytes(T9Const.DEFAULT_CODE);
        return cipher.doFinal(bufBytes);
    }

    /**
     * 解密字节数组
     * 
     * @param bufBytes
     *            密文字节
     * @return 解密的字符串
     * @throws Exception
     */
    public static String ciphDecryptBytes(byte[] bufBytes) throws Exception {
        Cipher cipher = T9SecurityUtility.getPassWordCipher(Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(bufBytes), T9Const.DEFAULT_CODE);
    }

    /**
     * 加密密码，生成密文
     * 
     * @param pass
     * @return
     * @throws Exception
     */
    public static String ciphEncryptStr(String srcPass) throws Exception {
        Cipher cipher = T9SecurityUtility.getPassWordCipher(Cipher.ENCRYPT_MODE);
        byte[] passBytes = srcPass.getBytes(T9Const.DEFAULT_CODE);
        passBytes = cipher.doFinal(passBytes);
        return new BASE64Encoder().encode(passBytes);
    }

    /**
     * 解密密码
     * 
     * @param encryptPass
     * @return
     * @throws Exception
     */
    public static String ciphDecryptStr(String encryptPass) throws Exception {
        Cipher cipher = T9SecurityUtility.getPassWordCipher(Cipher.DECRYPT_MODE);
        byte[] passBytes = new BASE64Decoder().decodeBuffer(encryptPass);
        return new String(cipher.doFinal(passBytes), T9Const.DEFAULT_CODE);
    }

    public static void main(String[] args) {
        try {
            System.out.println(ciphEncryptStr("root"));
            System.out.println(ciphDecryptStr("hZvBRX7B0kQ="));

            System.out.println(ciphEncryptStr("myoa888"));
            System.out.println(ciphDecryptStr("vaRrwyc/Yps="));
            
            System.out.println(ciphEncryptStr("t9password"));
            System.out.println(ciphDecryptStr("EAr16XhsD+fgdyiJhUb0xA=="));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
