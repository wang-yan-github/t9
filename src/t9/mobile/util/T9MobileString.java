package t9.mobile.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类主要针对 字符串 的辅助类 工具类 
 * 这个类主要是处理字符串 
 * java 字符窜处理类 非常强大 
 * 学习一下 非常有好处
 * 很多的方法很实用
 * 主要是这些方法都很通用 
 * 这些是java基础 是基本功 
 * 简单写了一下注释 
 */
public class T9MobileString {
	

	  /**
	   * 空字符串处理
	   * @param paramString
	   * @return
	   */
	  public static boolean isEmpty(String paramString)
	  {
	    return (paramString == null) || (paramString.trim().length() == 0);
	  }
	  /**
	   * 判断数组 后面加的
	   * @param paramString
	   * @return
	   */
	  public static boolean isEmpty(String[] paramString)
	  {
	    return (paramString == null) || (paramString.toString().trim().length() == 0);
	  }

	  /**
	   * 判断对象引用是否为空
	   * @param paramObject
	   * @return
	   */
	  public static String showObjNull(Object paramObject)
	  {
	    return showObjNull(paramObject, "");
	  }
	  
	  /**
	   * 如果对象为空 引用为空 将后面的字符串返回
	   * 如果不为空 调用对象的toString方法
	   * @param paramObject
	   * @param paramString
	   * @return
	   */
	  public static String showObjNull(Object paramObject, String paramString)
	  {
	    if (paramObject == null)
	      return paramString;
	    return paramObject.toString();
	  }
	  /**
	   * 
	   * 满足条件 互换变量
	   * @param paramObject
	   * @param option
	   * @param paramString
	   * @return
	   */
	  public static String showObjNull(String paramObject,boolean option, String paramString)
	  {
	    if (option)
	      return paramString;
	    return paramObject.toString();
	  }
	  
	  /**
	   * 扩展字符串 
	   * @param paramString
	   * @param paramInt
	   * @param paramChar
	   * @param paramBoolean
	   * @return
	   */
	  public static String expandStr(String paramString, int paramInt, char paramChar, boolean paramBoolean)
	  {
	    int i = paramString.length();
	    if (paramInt <= i) {
	      return paramString;
	    }

	    String str = paramString;
	    for (int j = 0; j < paramInt - i; j++) {
	      str = str + paramChar;
	    }
	    return str;
	  }

	  /**
	   * 判断字符串是否为空 通empty方法 
	   * 但含义不一样
	   * @param paramString
	   * @return
	   */
	  public static String showNull(String paramString)
	  {
	    return showNull(paramString, "");
	  }
	  /**
	   * 重构方法 判空字符串
	   * @param paramString1
	   * @param paramString2
	   * @return
	   */
	  public static String showNull(String paramString1, String paramString2)
	  {
	    return paramString1 == null ? paramString2 : paramString1;
	  }
	  /**
	   * 判空字符串
	   * @param paramString
	   * @return
	   */
	  public static String showEmpty(String paramString)
	  {
	    return showEmpty(paramString, "");
	  }
	  /**
	   * 判空字符串
	   * @param paramString1
	   * @param paramString2
	   * @return
	   */
	  public static String showEmpty(String paramString1, String paramString2)
	  {
	    return isEmpty(paramString1) ? paramString2 : paramString1;
	  }
	  
	  /**
	   * 将字符串 转换成ISO_8859格式的 字符串
	   * @param paramString
	   * @return
	   */
	  public static String toISO_8859(String paramString)
	  {
	    if (paramString == null)
	      return null;
	    try
	    {
	      return new String(paramString.getBytes(), "ISO-8859-1"); } catch (Exception localException) {
	    }
	    return paramString;
	  }

	 /**
	  * 将字符串 转换成 UTF-8格式
	  * @param paramArrayOfByte
	  * @return
	  */
	  public static String getUTF8String(byte[] paramArrayOfByte)
	  {
	    return getUTF8String(paramArrayOfByte, 0, paramArrayOfByte.length);
	  }
	  /**
	   * 重构类
	   * @param paramArrayOfByte
	   * @param paramInt1
	   * @param paramInt2
	   * @return
	   */
	  public static String getUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    int i = 0;
	    int j = paramInt1 + paramInt2;
	    int k = paramInt1;
	    while (k < j) {
	      int m = paramArrayOfByte[(k++)] & 0xFF;
	      switch (m >> 4)
	      {
	      case 0:
	      case 1:
	      case 2:
	      case 3:
	      case 4:
	      case 5:
	      case 6:
	      case 7:
	        i++;
	        break;
	      case 12:
	      case 13:
	        if ((paramArrayOfByte[(k++)] & 0xC0) != 128) {
	          throw new IllegalArgumentException();
	        }
	        i++;
	        break;
	      case 14:
	        if (((paramArrayOfByte[(k++)] & 0xC0) != 128) || ((paramArrayOfByte[(k++)] & 0xC0) != 128)) {
	          throw new IllegalArgumentException();
	        }
	        i++;
	        break;
	      case 8:
	      case 9:
	      case 10:
	      case 11:
	      default:
	        throw new IllegalArgumentException();
	      }
	    }
	    if (k != j) {
	      throw new IllegalArgumentException();
	    }

	    char[] arrayOfChar = new char[i];
	    k = 0;
	    while (paramInt1 < j) {
	      int n = paramArrayOfByte[(paramInt1++)] & 0xFF;
	      switch (n >> 4)
	      {
	      case 0:
	      case 1:
	      case 2:
	      case 3:
	      case 4:
	      case 5:
	      case 6:
	      case 7:
	        arrayOfChar[(k++)] = (char)n;
	        break;
	      case 12:
	      case 13:
	        arrayOfChar[(k++)] = (char)((n & 0x1F) << 6 | paramArrayOfByte[(paramInt1++)] & 0x3F);
	        break;
	      case 14:
	        int i1 = (paramArrayOfByte[(paramInt1++)] & 0x3F) << 6;
	        arrayOfChar[(k++)] = (char)((n & 0xF) << 12 | i1 | paramArrayOfByte[(paramInt1++)] & 0x3F);
	        break;
	      case 8:
	      case 9:
	      case 10:
	      case 11:
	      default:
	        throw new IllegalArgumentException();
	      }
	    }
	    return new String(arrayOfChar, 0, i);
	  }
	  /**
	   * 字节型 转字符串
	   * @param paramArrayOfByte
	   * @return
	   */
	  public static String byteToHexString(byte[] paramArrayOfByte)
	  {
	    return byteToHexString(paramArrayOfByte, ',');
	  }

	  public static String byteToHexString(byte[] paramArrayOfByte, char paramChar)
	  {
	    String str = "";
	    for (int i = 0; i < paramArrayOfByte.length; i++) {
	      if (i > 0) {
	        str = str + paramChar;
	      }
	      str = str + Integer.toHexString(paramArrayOfByte[i]);
	    }
	    return str;
	  }
	  /**
	   * 字节转字符串
	   * @param paramArrayOfByte
	   * @param paramChar
	   * @param paramInt
	   * @return
	   */
	  public static String byteToString(byte[] paramArrayOfByte, char paramChar, int paramInt)
	  {
	    String str = "";
	    for (int i = 0; i < paramArrayOfByte.length; i++) {
	      if (i > 0) {
	        str = str + paramChar;
	      }
	      str = str + Integer.toString(paramArrayOfByte[i], paramInt);
	    }
	    return str;
	  }
	  /**
	   * 过滤字符串 针对sql语句
	   * 主要是将特殊字符 回车换行啥的都踢掉
	   * @param paramString
	   * @return
	   */
	  public static String filterForSQL2(String paramString) {
	    if (isEmpty(paramString))
	      return paramString;
	    return paramString.replaceAll("(?i)([;\n\r])", "");
	  }
	  /**
	   * 过滤字符串 针对xsl
	   * @param paramString
	   * @return
	   */
	  public static String filterForXsltValue(String paramString) {
	    if (paramString == null)
	      return "";
	    paramString = paramString.replaceAll("\\{", "{{");
	    paramString = paramString.replaceAll("\\}", "}}");
	    return paramString;
	  }
	  /**
	   * 正对字符串过滤 针对 xml
	   * @param paramString
	   * @return
	   */
	  public static String filterForXML(String paramString)
	  {
	    if (paramString == null) {
	      return "";
	    }
	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;
	    if (i == 0) {
	      return "";
	    }
	    StringBuffer localStringBuffer = new StringBuffer((int)(i * 1.8D));

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      switch (c) {
	      case '&':
	        localStringBuffer.append("&amp;");
	        break;
	      case '<':
	        localStringBuffer.append("&lt;");
	        break;
	      case '>':
	        localStringBuffer.append("&gt;");
	        break;
	      case '"':
	        localStringBuffer.append("&quot;");
	        break;
	      case '\'':
	        localStringBuffer.append("&apos;");
	        break;
	      default:
	        localStringBuffer.append(c);
	      }
	    }

	    return localStringBuffer.toString();
	  }
	  /**
	   * 字符串 针对 html  过滤
	   * @param paramString
	   * @return
	   */
	  public static String filterForHTMLValue(String paramString)
	  {
	    if (paramString == null) {
	      return "";
	    }

	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;
	    if (i == 0) {
	      return "";
	    }
	    StringBuffer localStringBuffer = new StringBuffer((int)(i * 1.8D));

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      switch (c)
	      {
	      case '&':
	        if (j + 1 < i) {
	          c = arrayOfChar[(j + 1)];
	          if (c == '#')
	            localStringBuffer.append("&");
	          else
	            localStringBuffer.append("&amp;");
	        } else {
	          localStringBuffer.append("&amp;");
	        }break;
	      case '<':
	        localStringBuffer.append("&lt;");
	        break;
	      case '>':
	        localStringBuffer.append("&gt;");
	        break;
	      case '"':
	        localStringBuffer.append("&quot;");
	        break;
	      default:
	        localStringBuffer.append(c);
	      }
	    }

	    return localStringBuffer.toString();
	  }
	  
	  /**
	   * 针对URL 过滤字符串 转意特殊字符
	   * @param paramString
	   * @return
	   */
	  public static String filterForUrl(String paramString)
	  {
	    if (paramString == null) {
	      return "";
	    }
	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;
	    if (i == 0) {
	      return "";
	    }
	    StringBuffer localStringBuffer = new StringBuffer((int)(i * 1.8D));

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      switch (c) {
	      case '%':
	        localStringBuffer.append("%25");
	        break;
	      case '?':
	        localStringBuffer.append("%3F");
	        break;
	      case '#':
	        localStringBuffer.append("%23");
	        break;
	      case '&':
	        localStringBuffer.append("%26");
	        break;
	      case ' ':
	        localStringBuffer.append("%20");
	        break;
	      default:
	        localStringBuffer.append(c);
	      }
	    }

	    return localStringBuffer.toString();
	  }
	  /**
	   * 过滤字符串 针对javascript 脚本中的特殊字符
	   * @param paramString
	   * @return
	   */
	  public static String filterForJs(String paramString)
	  {
	    if (paramString == null) {
	      return "";
	    }
	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;
	    if (i == 0) {
	      return "";
	    }
	    StringBuffer localStringBuffer = new StringBuffer((int)(i * 1.8D));

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      switch (c) {
	      case '"':
	        localStringBuffer.append("\\\"");
	        break;
	      case '\'':
	        localStringBuffer.append("\\'");
	        break;
	      case '\\':
	        localStringBuffer.append("\\\\");
	        break;
	      case '\n':
	        localStringBuffer.append("\\n");
	        break;
	      case '\r':
	        localStringBuffer.append("\\r");
	        break;
	      case '\f':
	        localStringBuffer.append("\\f");
	        break;
	      case '\t':
	        localStringBuffer.append("\\t");
	        break;
	      case '/':
	        localStringBuffer.append("\\/");
	        break;
	      default:
	        localStringBuffer.append(c);
	      }
	    }

	    return localStringBuffer.toString();
	  }
	  /**
	   * 过滤字符串 针对java语言
	   * @param paramString
	   * @return
	   */
	  public static String filterForJava(String paramString)
	  {
	    if (paramString == null) {
	      return "";
	    }
	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;
	    if (i == 0) {
	      return "";
	    }
	    StringBuffer localStringBuffer = new StringBuffer((int)(i * 1.8D));

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      switch (c) {
	      case '"':
	        localStringBuffer.append("\\\"");
	        break;
	      case '\\':
	        localStringBuffer.append("\\\\");
	        break;
	      default:
	        localStringBuffer.append(c);
	      }
	    }
	    return localStringBuffer.toString();
	  }
	  /**
	   * 将数字转换成 字符串
	   * @param paramInt
	   * @return
	   */
	  public static String numberToStr(int paramInt)
	  {
	    return numberToStr(paramInt, 0);
	  }
	  /**
	   * 数字转换成字符串
	   * @param paramInt1
	   * @param paramInt2
	   * @return
	   */
	  public static String numberToStr(int paramInt1, int paramInt2)
	  {
	    return numberToStr(paramInt1, paramInt2, '0');
	  }
	  /**
	   * 数字转字符串
	   * @param paramInt1
	   * @param paramInt2
	   * @param paramChar
	   * @return
	   */
	  public static String numberToStr(int paramInt1, int paramInt2, char paramChar)
	  {
	    String str = String.valueOf(paramInt1);
	    return expandStr(str, paramInt2, paramChar, true);
	  }
	  
	  public static String numberToStr(long paramLong)
	  {
	    return numberToStr(paramLong, 0);
	  }

	  public static String numberToStr(long paramLong, int paramInt)
	  {
	    return numberToStr(paramLong, paramInt, '0');
	  }

	  public static String numberToStr(long paramLong, int paramInt, char paramChar)
	  {
	    String str = String.valueOf(paramLong);
	    return expandStr(str, paramInt, paramChar, true);
	  }
	  
	  public static String circleStr(String paramString)
	  {
	    if (paramString == null) {
	      return null;
	    }
	    String str = "";
	    int i = paramString.length();
	    for (int j = i - 1; j >= 0; j--) {
	      str = str + paramString.charAt(j);
	    }
	    return str;
	  }
	  /**
	   * 判断是否为中尉char
	   * @param paramInt
	   * @return
	   */
	  public static final boolean isChineseChar(int paramInt)
	  {
	    return paramInt > 127;
	  }
	  
	  public static final int getCharViewWidth(int paramInt)
	  {
	    return isChineseChar(paramInt) ? 2 : 1;
	  }

	  public static final int getStringViewWidth(String paramString)
	  {
	    if ((paramString == null) || (paramString.length() == 0)) {
	      return 0;
	    }

	    int i = 0;
	    int j = paramString.length();

	    for (int k = 0; k < j; k++) {
	      i += getCharViewWidth(paramString.charAt(k));
	    }

	    return i;
	  }
	  
	  public static String truncateStr(String paramString, int paramInt)
	  {
	    return truncateStr(paramString, paramInt, "..");
	  }

	  public static String truncateStr(String paramString1, int paramInt, String paramString2)
	  {
	    if (paramString1 == null) {
	      return null;
	    }

	    if (paramString2 == null) {
	      paramString2 = "..";
	    }

	    int i = getStringViewWidth(paramString1);
	    if (i <= paramInt)
	    {
	      return paramString1;
	    }

	    int j = getStringViewWidth(paramString2);
	    if (j >= paramInt)
	    {
	      return paramString1;
	    }

	    int k = paramString1.length();
	    int m = paramInt - j;
	    StringBuffer localStringBuffer = new StringBuffer(paramInt + 2);

	    for (int n = 0; n < k; n++) {
	      char c = paramString1.charAt(n);
	      int i1 = getCharViewWidth(c);
	      if (i1 > m) {
	        localStringBuffer.append(paramString2);
	        break;
	      }
	      localStringBuffer.append(c);
	      m -= i1;
	    }

	    return localStringBuffer.toString();
	  }
	  /**
	   * 过滤字符串 针对 jdom
	   * @param paramString
	   * @return
	   */
	  public static String filterForJDOM(String paramString)
	  {
	    if (paramString == null) {
	      return null;
	    }
	    char[] arrayOfChar = paramString.toCharArray();
	    int i = arrayOfChar.length;

	    StringBuffer localStringBuffer = new StringBuffer(i);

	    for (int j = 0; j < i; j++) {
	      char c = arrayOfChar[j];
	      if (!isValidCharOfXML(c)) {
	        continue;
	      }
	      localStringBuffer.append(c);
	    }
	    return localStringBuffer.toString();
	  }
	  /**
	   * 
	   * @param paramChar
	   * @return
	   */
	  public static boolean isValidCharOfXML(char paramChar)
	  {
	    return (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r') || ((' ' <= paramChar) && (paramChar <= 55295)) || ((57344 <= paramChar) && (paramChar <= 65533)) || ((65536 <= paramChar) && (paramChar <= 1114111));
	  }
	  /**
	   * 获取字符串字节长度
	   * @param paramString
	   * @return
	   */
	  public static int getBytesLength(String paramString)
	  {
	    if (paramString == null) {
	      return 0;
	    }
	    char[] arrayOfChar = paramString.toCharArray();

	    int i = 0;
	    for (int j = 0; j < arrayOfChar.length; j++) {
	      int k = arrayOfChar[j];
	      i += (k <= 127 ? 1 : 2);
	    }
	    return i;
	  }

	 /**
	  * 判断字符串中是否包含中文
	  * @param paramString
	  * @return
	  */
	  public static final boolean isContainChineseChar(String paramString)
	  {
	    if (paramString == null) {
	      return false;
	    }

	    return paramString.getBytes().length != paramString.length();
	  }
	  /**
	   * 
	   * @param paramArrayList
	   * @param paramString
	   * @return
	   */
	  public static String join(ArrayList paramArrayList, String paramString)
	  {
	    if (paramArrayList == null) {
	      return null;
	    }

	    return join(paramArrayList.toArray(), paramString);
	  }

	  public static String join(Object[] paramArrayOfObject, String paramString)
	  {
	    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0) || (paramString == null)) {
	      return null;
	    }
	    if (paramArrayOfObject.length == 1) {
	      return paramArrayOfObject[0].toString();
	    }

	    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfObject[0].toString());
	    for (int i = 1; i < paramArrayOfObject.length; i++) {
	      if (paramArrayOfObject[i] == null) {
	        continue;
	      }
	      localStringBuffer.append(paramString);
	      localStringBuffer.append(paramArrayOfObject[i].toString());
	    }

	    return localStringBuffer.toString();
	  }

	  public static String join(int[] paramArrayOfInt, String paramString) {
	    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
	      return "";
	    }

	    if (paramString == null) {
	      paramString = ",";
	    }
	    StringBuffer localStringBuffer = new StringBuffer();
	    localStringBuffer.append(paramArrayOfInt[0]);
	    for (int i = 1; i < paramArrayOfInt.length; i++) {
	      localStringBuffer.append(paramString).append(paramArrayOfInt[i]);
	    }
	    return localStringBuffer.toString();
	  }

	  public static boolean containsCDATAStr(String paramString) {
	    if (paramString == null) {
	      return false;
	    }
	    return paramString.matches("(?ism).*<!\\[CDATA\\[.*|.*\\]\\]>.*");
	  }


	  public static String transPrettyUrl(String paramString, int paramInt)
	  {
	    return transPrettyUrl(paramString, paramInt, null);
	  }

	  public static String transPrettyUrl(String paramString1, int paramInt, String paramString2)
	  {
	    int i = 0;
	    if ((paramString1 == null) || (paramInt <= 0) || (paramString1.length() <= paramInt) || ((i = paramString1.lastIndexOf('/')) == -1))
	    {
	      return paramString1;
	    }

	    int j = paramString1.lastIndexOf("://") + 3;
	    String str1 = paramString1.substring(0, j);
	    String str2 = paramString1.substring(j, i);
	    if (str2.length() < 3) {
	      return paramString1;
	    }
	    int k = paramInt + str2.length() - paramString1.length();
	    if (k <= 3) {
	      k = 3;
	    }
	    str2 = str2.substring(0, k);
	    str2 = str2 + (paramString2 != null ? paramString2 : "....");

	    String str3 = paramString1.substring(i);
	    return str1 + str2 + str3;
	  }

	  public static String capitalize(String paramString) {
	    if (isEmpty(paramString))
	      return paramString;
	    char c1 = paramString.charAt(0);
	    char c2 = Character.toUpperCase(c1);
	    return c2 + paramString.substring(1);
	  }
	  /**
	   * 格式化时间 形式如 12:50 十二点五十
	   * @param dateTime
	   * @return
	   */
	  public static String formateDateTimeToTime(String dateTime){
		  if(T9MobileString.isEmpty(dateTime)){
			  return "00:00";
		  }
		  if(dateTime.length() < 18){
			  return "00:00";
		  }
		  return dateTime.substring(11, 16);
	  }
	  
	  /**
	   * byte数组转换成 string 应该好使 呵呵  原因就不写了 试试吧
	   * @param target
	   * @return
	   */
	  public static String BytesToStr(byte[] target)
	  {
	   StringBuffer buf = new StringBuffer();
	   for (int i = 0, j = target.length; i < j; i++) {
	    buf.append((char) target[i]);
	   }
	   return buf.toString();
	  }
	  /**
	   * 提供一种 字符串转 数组的方法 只是一种方法 与上面的方法 互相配合使用
	   * @param str
	   * @return
	   */
	  public static byte[] StrToBytes(String str) {
	   byte[] buf = new byte[str.length()];
	   for (int i = 0; i < str.length(); i++) {
	    buf[i] = (byte) str.charAt(i);
	   }
	   return buf;
	  }
}
