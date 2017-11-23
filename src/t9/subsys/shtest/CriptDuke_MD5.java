package t9.subsys.shtest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * ==========================================================
 * Copyright@1996-2009 Abacus International Pte. Ltd.
 * ==========================================================
 */

/**
 * @Class name:	CriptDuke_MD5.java
 *
 * Short description on the purpose of the program.
 *
 * @author:		shujun
 * @modified:	Mar 26, 2012
 *
 */

public class CriptDuke_MD5 {

	/**
	 * 进行MD5加密
	 * 
	 * @param info
	 *            要加密的信息
	 * @return String 加密后的字符串
	 */
	public String encryptToMD5(String info) {
		byte[] digesta = null;
		try {
			// 得到一个md5的消息摘要
			MessageDigest alga = MessageDigest.getInstance("MD5");
			// 添加要进行计算摘要的信息
			alga.update(info.getBytes());
			// 得到该摘要
			digesta = alga.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 将摘要转为字符串
		String rs = byte2hex(digesta);
		return rs;
	}
	
	public String encryptToMD52(String info) {
		byte[] digesta = null;
		try {
			MessageDigest alga = MessageDigest.getInstance("MD5");
			alga.update(info.getBytes());
			digesta = alga.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String rs = digesta.toString();
		return rs;
	}
	/**
	 * 将二进制转化为16进制字符串
	 * 
	 * @param b
	 *            二进制字节数组
	 * @return String
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	
	public static void main(String[] args) {
		CriptDuke_MD5 jiami = new CriptDuke_MD5();
		// 执行MD5加密"Hello world!"
		System.out.println("Hello经过MD5:" + jiami.encryptToMD5("12341234"));
		System.out.println("Hello经过MD51:" + jiami.encryptToMD52("12341234"));
	}
}