package test.console;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.management.OperatingSystemMXBean;

import seamoonotp.seamoonapi;
//import sun.management.ManagementFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.autorun.T9FileInfoExtract;
import t9.core.data.T9AuthKeys;
import t9.core.data.T9MapComparator;
import t9.core.data.T9PropField;
import t9.core.funcs.email.logic.T9WebmailLogic;
import t9.core.funcs.news.data.T9News;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.install.T9InstallConfig;
import t9.core.load.T9ConfigLoader;
import t9.core.load.T9DataSourceLoader;
import t9.core.util.T9Guid;
import t9.core.util.T9Out;
import t9.core.util.T9ReflectUtility;
import t9.core.util.T9RegexpUtility;
import t9.core.util.T9TimeCounter;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9Authenticator;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.auth.T9UsbKey;
import t9.core.util.cmd.T9CmdFileUtility;
import t9.core.util.cmd.T9CmdUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.util.file.T9ZipFileUtility;
import t9.core.util.form.T9FOM;
import t9.core.util.mail.T9MailSenderInfo;
import t9.core.util.mail.T9SimpleMailSender;
import t9.subsys.inforesouce.data.T9SignFile;
import t9.subsys.inforesouce.db.T9MetaDbHelper;
import test.core.dto.T9InnerBean1;
import test.core.mail.MailSenderInfo;
import test.core.mail.SimpleMailSender;
import test.core.poi.T9TestWord;
import t9.core.util.auth.T9PassEncrypt;
//import test.core.util.aut.T9GetSysInfo;
import test.core.util.db.TestDbUtil;
public class T9TestMain {
  static {
    String installPath = "D:\\project\\t9";
    //加载数据库配置信息compressJs
    String sysConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\sysconfig.properties";
    T9SysProps.setProps(T9ConfigLoader.loadSysProps(sysConfFile));
    String selfConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\selfconfig.properties";
    T9SysProps.addProps(T9ConfigLoader.loadSysProps(selfConfFile));
    
//    String installPath = "D:\\project\\t9\\webroot\\t9\\";
//    try {
//      T9ConfigLoader.loadInit(installPath);
//    }catch(Exception ex) {
//      ex.printStackTrace();
//    }
  }
  
  public static void main(String[] args) throws Exception {
//    test80(); 
    //System.out.println(System.currentTimeMillis());
//    System.out.println(test81("yzq-85wpjmbt8kk@20091033082858", "TD20X-12345677-7890"));
//    test82();
//    test84();
    test56();
  }
  
  public static void test85() throws Exception {
    long start = 1330225529583l;
    long done = 1330225739122l;
    int rowCnt = 108031;
    System.out.println("time cost>>" + (done - start));
    System.out.println("total bytes>>" + rowCnt * 100 );
    System.out.println("throughoutput / sec>>" + (rowCnt * 100 / ((done - start)/1000)));
  }
  public static void test84() throws Exception {
    try {
      System.out.println("Network   infos: ");
      System.out.println("Operating System:" + System.getProperty("os.name"));
      System.out.println("IP/Localhost: " + InetAddress.getLocalHost().getHostAddress());
      //System.out.println("MAC Address: " + T9GetSysInfo.getMacAddress());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  public static void test83() throws Exception {
    try {
      System.out.println(T9SysProps.isWindows());
    }catch(Exception ex) {
      throw ex;
    }
  }
  public static void test82() throws Exception {
    try {
      String fileName64 = "=?gb2312?B?tPPM/M/uxL9fsfjG99bcsahfo6i12jQ1xtqjqV+jqDIwMTEwMDkwNS0yMDExMDkwOaOpXzIwMTEwOTA5VjEuZG9j?=";
      String fileName = getFromBASE64(fileName64.substring("=?gb2312?b?".length(),fileName64.indexOf("?=")),"gbk");
      System.out.println(fileName);
    }catch(Exception ex) {
      throw ex;
    }
  }
  /**
   * 得到解码后的BASE64所有字符串
   * @param s
   * @param charset
   * @return
   */
  public static String getFromBASE64(String s,String charset) {
    if (s == null)
      return null;
    BASE64Decoder decoder = new BASE64Decoder();
    try {
      s = s.replaceAll(" ", "");//去掉base64编码后的空格字符串

     // System.out.println(s);
      byte[] b = decoder.decodeBuffer(s.trim());
      return new String(b,charset);
    } catch (Exception e) {
      return null;
    }
  }
  
  private static void allockMem() throws Exception {
    int[] bufSize = new int[]{100, 200, 300, 400, 50, 10, 300, 200};
    for (int i = 0; i < 10; i++) {
      Thread.sleep(10 * T9Const.DT_S);
      System.out.println("will alloc " + bufSize[i % 8] + "M memory");
      byte[] buf = new byte[bufSize[i % 8] * T9Const.M];
      buf = null;
    }
  }
  
  public static String test81(String machineCode, String softId) {
    String key = "$1$"
      + softId.substring(0, 3)
      + softId.substring(8, 11)
      + softId.substring(softId.length() - 3, softId.length() - 1)
      + "$";
    String tmpCode = T9PassEncrypt.encrypt(machineCode, key);
    int pos = tmpCode.indexOf("$", 3);
    tmpCode = tmpCode.substring(pos + 1);
    StringBuffer regCode = new StringBuffer();
    int length = tmpCode.length();
    for (int i = 0; i < length; i++) {
      char c = tmpCode.charAt(i);
      regCode.append(Integer.toHexString((int)(c & 0x0F)).toUpperCase());
      regCode.append(Integer.toHexString((int)((c >> 4) & 0x0F)).toUpperCase());
    }
    String rtCode = regCode.substring(0, 4)
      + regCode.substring(length * 2 - 4)
      + regCode.substring(length - 4, length)
      + regCode.substring(length + 1, length + 5);

    return rtCode;
  }
  public static void test80() throws Exception {
    try {
      allockMem();
      //byte[] buf = new byte[200 * T9Const.M];
      Thread.sleep(20 * 60000);
    }catch(Exception ex) {
      throw ex;
    }
  }
  
  public static void test79() throws Exception {
    try {
//      OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//      System.out.println("系统物理内存总计：" + osmb.getTotalPhysicalMemorySize() / 1024/1024 + "MB"); 
//      System.out.println("系统物理可用内存总计：" + osmb.getFreePhysicalMemorySize() / 1024/1024 + "MB");
    }catch(Exception ex) {
      throw ex;
    }
  }
  
  
  
  public static void test78() throws Exception {
    try {
      T9TimeCounter t = new T9TimeCounter();
      T9WebmailLogic wml = new T9WebmailLogic();
      wml.loadWebMail();
      t.logTime("totalTime");
    }catch(Exception ex) {
      throw ex;
    }
  }
  
  public static void test77() throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, "MAIL");
      
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery("select SEQ_ID, SUBJECT from EMAIL_BODY where from_id=-2 order by seq_id");
      List<Integer> seqIdList = new ArrayList<Integer>();
      List<String> subjectList = new ArrayList<String>();
      while (rs.next()) {
        int seqId = rs.getInt(1);
        String subject = rs.getString(2);
        if (!subjectList.contains(subject)) {
          seqIdList.add(seqId);
          subjectList.add(subject);
          System.out.println("seqId:=" + seqId + ">>>" + subject);
        }
      }
      System.out.println("subjectCnt>>" + subjectList.size());
      
//      for (int i = 0; i < seqIdList.size(); i++) {
//        int seqId = seqIdList.get(i);
//        String subject = subjectList.get(i);
//        
//        rs = stmt.executeQuery("select SEQ_ID, WEBMAIL_UID from email_body where from_id=-2 and SEQ_ID>" + subject + " and SUBJECT='" + subject + "' order by seq_id");
//        List<Integer> bodyIdList = new ArrayList<Integer>();
//        List<String> uidList = new ArrayList<String>();
//        while (rs.next()) {
//          bodyIdList.add(rs.getInt(1));
//          uidList.add(rs.getString(2));
//        }
//        for (int j = 0; j < bodyIdList.size(); j++) {
//          int bodyId = bodyIdList.get(j);
//          stmt.executeUpdate("delete from email where BODY_ID=" + bodyId);
//          stmt.executeUpdate("delete from email_body where SEQ_ID=" + bodyId);
//        }
//      }
      for (int i = 0; i < seqIdList.size(); i++) {
        int seqId = seqIdList.get(i);
        String subject = subjectList.get(i);
        
        String sql = "select SEQ_ID, FROM_WEBMAIL_ID from email_body where from_id=-2 and SEQ_ID>" + seqId + " and SUBJECT='" + subject + "' order by seq_id";
        System.out.println("sql>>" + sql);
        rs = stmt.executeQuery(sql);
        List<Integer> bodyIdList = new ArrayList<Integer>();
        List<Integer> fromIdList = new ArrayList<Integer>();
        while (rs.next()) {
          int bodyId = rs.getInt(1);
          int fromId = rs.getInt(2);
          bodyIdList.add(bodyId);
          fromIdList.add(fromId);
//          System.out.println("bodyId:=" + bodyId + ">>>" + uid);
        }
        for (int j = 0; j < bodyIdList.size(); j++) {
          int bodyId = bodyIdList.get(j);
          int fromId = fromIdList.get(j);
          stmt.executeUpdate("delete from email where BODY_ID=" + bodyId);
          stmt.executeUpdate("delete from email_body where SEQ_ID=" + bodyId);
          stmt.executeUpdate("delete from webmail_body where SEQ_ID=" + fromId);
        }
      }
    }catch(Exception ex) {
      dbConn.rollback();
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, null);
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  
  public static void test76() throws Exception {
    String value = "中国兵器工业信息中心";
    String machineCode = "02CA2FE4";
    String softId = "03DD3CF8-F6430001-VE8CXCG1";
    String registOrg = "中国兵器工业信息中心";
    String didgist = "JHjvPWYtkw6jPlk2ngfo6iu+x8BG0TfjmlE=";
    System.out.println(T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null), value + machineCode + softId + registOrg, didgist));
  }
  
  public static void test74() throws Exception {
   // T9RegistTool.genMobileSmsRegCode("1234567890", "55B36C6D-91A90001-8U3EA85S");
  }
  public static void test73() throws Exception {
    String sn = "797042125";
    String snInfo = "SS000060797042125C317EFAD034F178F01853FBD8F5FAC85D2F31FE8E8E2B7CF0FB788825A3B7FACF9E3194C885A72A4534B4FC4F1A2FE5593AF29C1686D5E1E451CA1735FF1E1873FC4742508DD7109DD2D68D24C9D282425C99E25C7986A550E85EAEFBB7B12890E85EAEFBB7B12890E85EAEFBB7B12890E85EAEFBB7B12890E85EAEFBB7B12890E85EAEFBB7B1289==";
    String newSnInfo = null;
    try {
      seamoonapi sc = new seamoonapi();
      newSnInfo = sc.checkpassword(snInfo, "484717");
      if (newSnInfo.length()>3)
      {
          System.out.println("密码验证通过");   
      }
      else if(newSnInfo.equals("-1"))
      {
        System.out.println("SN字符串有错");
      }
      else if(newSnInfo.equals("0"))
      {
        System.out.println("密码错误");
      }  
      else  
      {
        System.out.println("未知错误");
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public static void test72() throws Exception {
    byte[] data = "".getBytes("UTF-8");
    String digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest.toUpperCase());
    digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest);
    data = "354526hbl".getBytes("UTF-8");
    System.out.println("is valid>>" + T9DigestUtility.isMatch(data, digest));
  }
  public static void test71() throws Exception {
    byte[] data = "".getBytes("UTF-8");
    String digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest.toUpperCase());
    digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest);
    data = "354526hbl".getBytes("UTF-8");
    System.out.println("is valid>>" + T9DigestUtility.isMatch(data, digest));
  }
  public static void test70() throws Exception {
    long t1 = System.currentTimeMillis();
    String filePath = "E:\\others\\video\\玩具总动员.nrg";
    String digest = T9DigestUtility.md5File(filePath);
    System.out.println(digest);
    digest = T9DigestUtility.md5File(filePath);
    System.out.println(digest);
    System.out.println("is valid>>" + T9DigestUtility.isFileMatch(filePath, digest));
    long t2 = System.currentTimeMillis();
    System.out.println(t2 - t1);
  }
  public static void test69() throws Exception {
    byte[] data = "中国".getBytes("UTF-8");
    String digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest);
    digest = T9DigestUtility.md5Hex(data);
    System.out.println(digest);
    data = "中国".getBytes("UTF-8");
    System.out.println("is valid>>" + T9DigestUtility.isMatch(data, digest));
  }
  public static void test68() throws Exception {
    String timeStr = "500";
    String pass = T9Authenticator.encryptBase64(T9AuthKeys.getMD5SaltLength(null) - 2, timeStr);
    System.out.println("maxUserCnt=" + timeStr);
    System.out.println("maxUserCntPass=" + pass);
  }
  public static void test67() throws Exception {
    String timeStr = String.valueOf(System.currentTimeMillis());
    String pass = T9Authenticator.encryptBase64(T9AuthKeys.getMD5SaltLength(null) - 2, timeStr);
    System.out.println(T9SysPropKeys.INSTALL_TIME + "=" + timeStr);
    System.out.println(T9SysPropKeys.INSTALL_TIME_PASS + "=" + pass);
  }
  public static void test66() throws Exception {
    T9Out.println(new BASE64Encoder().encode("30".getBytes("UTF-8")) + T9PassEncrypt.encryptPass("30").substring(11, 21));
  }
  public static void test65() throws Exception {
    T9Out.println("T9Out 测试");
  }
  public static void test64() throws Exception {
    Connection dbConn = null;
    PreparedStatement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, "TD_OA");
      
//      stmt = dbConn.prepareStatement("update news set COMPRESS_CONTENT=? where SEQ_ID=1656");
//      stmt.setBytes(1, "中国".getBytes("UTF-8"));
      
//      stmt.executeUpdate();
      
//      stmt2 = dbConn.createStatement();
//      rs = stmt2.executeQuery("select COMPRESS_CONTENT from news where SEQ_ID=39");
//      if (rs.next()) {
//        String content = new String(rs.getBytes(1), "UTF-8");
//        System.out.println("content>>" + content);
//      }
//      System.out.println("done");
      
      T9ORM orm = new T9ORM();
      
      T9News news = (T9News)orm.loadObjSingle(dbConn, T9News.class, 1656);
      String content = new String(news.getCompressContent(), "UTF-8");
      System.out.println("content>>" + content);
      
      news = new T9News();
      news.setSeqId(1656);
      news.setCompressContent("BLob测试".getBytes("UTF-8"));
      orm.updateSingle(dbConn, news);
      dbConn.commit();
      
      news = (T9News)orm.loadObjSingle(dbConn, T9News.class, 1656);
      content = new String(news.getCompressContent(), "UTF-8");
      System.out.println("content>>" + content);
      
    }catch(Exception ex) {
      dbConn.rollback();
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, null);
      T9DBUtility.close(stmt2, rs, null);
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  
  public static void test63() throws Exception {
    T9CmdFileUtility.compactJsInpath("D:\\project\\t9\\webroot\\t9\\core\\js\\src", "D:\\project\\t9\\webroot\\t9\\core\\js");
  }
  public static void test62() throws Exception {
    System.out.println(T9FileUtility.isFileEqual("D:\\project\\t9\\webroot\\setup\\index.jsp",
        "D:\\t9\\webroot\\setup\\index.jsp"));
  }
  public static void test61() throws Exception {
    List msrgList = new ArrayList();
    T9FileUtility.copyDirDiff("D:\\project\\t9\\tomcat\\work",
        "D:\\t9\\tomcat\\work",
        "C:\\Users\\yzq\\Desktop\\codediff\\webroot",
        msrgList,
        true);
    for (int i = 0; i < msrgList.size(); i++) {
      System.out.println(msrgList.get(i));
    }
  }
  public static void test60() throws Exception {
    List msrgList = new ArrayList();
    T9FileUtility.copyNotExists("C:\\Users\\yzq\\Desktop\\codediff\\jspsrc",
        "C:\\Users\\yzq\\Desktop\\codediff\\jspsrc2",
        "C:\\Users\\yzq\\Desktop\\codediff\\add",
        msrgList);
    for (int i = 0; i < msrgList.size(); i++) {
      System.out.println(msrgList.get(i));
    }
    msrgList.clear();
    T9FileUtility.copyDirDiff("C:\\Users\\yzq\\Desktop\\codediff\\jspsrc",
        "C:\\Users\\yzq\\Desktop\\codediff\\jspsrc2",
        "C:\\Users\\yzq\\Desktop\\codediff\\update",
        msrgList,
        true);
    for (int i = 0; i < msrgList.size(); i++) {
      System.out.println(msrgList.get(i));
    }
  }
  public static void test59() throws Exception {
    System.out.println(File.pathSeparator);
    System.out.println(File.pathSeparatorChar);
    System.out.println(File.separator);
    System.out.println(File.separatorChar);
    File file = new File("..\\t9\\core\\js\\jquery\\ux");
    System.out.println(file.getAbsolutePath());
    System.out.println(file.getCanonicalPath());
  }
  public static void test58() throws Exception {
    T9CmdFileUtility.compactJsInpath("D:\\project\\t9\\webroot\\t9\\core\\js\\jquery\\ux", "C:\\Users\\yzq\\Desktop\\ux2");
  }
  public static void test57() throws Exception {
    System.out.println(Thread.currentThread().getContextClassLoader().getClass().getName());
  }
  public static void test56() throws Exception {
    T9CmdFileUtility.compactJsInpath("D:\\project\\t9\\webroot\\t9\\core\\js\\src", "D:\\project\\t9\\webroot\\t9\\core\\js");
  }
  public static void test55() throws Exception {
    T9TimeUtility.getNextRunTime2(3, 0, 3);
  }
  public static void test54() throws Exception {
    Class c = System.class;
    Package p = c.getPackage();
  }
  public static void test53() throws Exception {
    System.out.println(T9FileInfoExtract.truncateHtml("香港地区党务骨干培训班学员到朝阳区高碑店乡参观<DIV>_x000D_<DIV>_x000D_<"));
  }
  public static void test52() throws Exception {
    System.out.println(Pattern.matches("^\\w{32}.+", "44e7c7600ce9c92197537898a4977209_文档 11423.docx"));
  }
  public static void test50() throws Exception {
    System.out.println(">>" + "中国");
  }
  
  public static void test49() throws Exception {
    T9ReflectUtility.exeMethod("test.console.T9TestInvoke", "testPrim", new String[]{"int"}, new Object[]{1});
    T9ReflectUtility.exeMethod("test.console.T9TestInvoke", "testVoidParam", new String[]{}, new Object[]{});
  }
  public static void test48() {
    System.out.println(Short.parseShort("0AFF", 16));
//    Integer.
  }
  
  public static void test46() throws Exception {
    String basePath = "C:\\Users\\yzq\\Desktop\\WordBooks\\";
    String[] fileArray = new File(basePath).list();
    long startTime = System.currentTimeMillis();
    System.out.println("start");
    for (int i = 0; i < 100; i ++) {
      T9TestWord.readWord(basePath + fileArray[i]);
    }
    System.out.println("done, take time >>" + (System.currentTimeMillis() - startTime) + "毫秒");
  }
  
  public static void test43() throws Exception {
    String currTime = String.valueOf(System.currentTimeMillis());
    String timePass = T9Authenticator.encryptBase64(T9AuthKeys.getMD5SaltLength(null) - 2, currTime);
    System.out.println(currTime + ">>" + timePass);
  }
//  public static void test42() throws Exception {
//    List idList = new ArrayList();
//    for (int i = 0; i < 100000; i++) {
//      String id = T9RegistTool.getSoftId("0001");
//      if (idList.contains(id)) {
//        System.out.println("出现了重复");
//        continue;
//      }
//      idList.add(id);
//      if (!T9RegistTool.isValidSoftId(id)) {
//        System.out.println("出现了无效的情况");
//      }
//    }
//  }
  //rx3Y/PLUVYw=
  public static void test41() throws Exception {
    System.out.println(T9Authenticator.ciphDecryptStr("rx3Y/PLUVYw="));
  }
  public static void test40() throws Exception {
    T9InstallConfig config = new T9InstallConfig();
    config.deleteSysConf("D:\\tmp\\t9", "t9", "D:\\tmp\\t9\\update\\props\\1.properties");
  }
  public static void test39() throws Exception {
    T9InstallConfig config = new T9InstallConfig();
    Map paramMap = new HashMap();
    paramMap.put("host", "192.167.0.123");
    paramMap.put("port", "1433");
    paramMap.put("dbms", "sqlserver");
    paramMap.put("user", "yzq");
    paramMap.put("pass", "12345");
    config.updateDbConf("D:\\tmp\\t9", "t9", paramMap);
  }
  public static void test38() throws Exception {
    T9InstallConfig config = new T9InstallConfig();
    config.updateSysConf("D:\\tmp\\t9", "t9", "D:\\tmp\\t9\\update\\props\\1.properties");
  }
  public static void test37() throws Exception {
    T9InstallConfig config = new T9InstallConfig();

    config.exeSqlInPath("D:\\tmp\\t9\\update\\sql\\0803");
  }
  
  public static void test36() throws Exception {
    T9InstallConfig config = new T9InstallConfig();
    config.opeIE("D:\\T9", "t9");
  }
  
  public static void test35() throws Exception {
    String[] args = new String[]{"sql", "-host192.168.0.1", "-port1433", "-pass1234", "-useryzq"};
    Map rtMap = T9CmdUtility.parseOptions(args, new String[]{"host", "port", "user"});
    System.out.println(T9FOM.toJson(rtMap));
  }
  
  public static void test34() throws Exception {
    String confFilePath = "D:\\tmp\\server.text";
    System.out.println(T9InstallConfig.getTomcatPort(confFilePath));
    T9InstallConfig.updateTomcatPort(confFilePath, 999);
  }
  public static void test33() throws Exception {
    String jsonStr = "a:  \"a,中文测试aa\",b:12.2,c: \"cc\",d:\"中文测试\",e :1234,f:2.22,g  :    .98,h:-2.3,i:\"\\\"\\\'\"";
//    jsonStr = jsonStr.trim().replace("\r\n", "").replace("\n", "").replace("\\\"", "{{quote}}").replace("\\\"", "{{squote}}");
    //List wordList = T9RegexpUtility.getMatchedWords(jsonStr, "(?:(?:\"[^\"]+\")|(?:[^\":,]+))\\s*:\\s*(?:(?:-?\\d*\\.?\\d+(?:E\\d+)?)|(?:\"[^\"]*\"))");
    //List wordList = T9RegexpUtility.getMatchedWords(jsonStr, "\"[^\"]+\"");
    List wordList = T9RegexpUtility.splitJson(jsonStr);
    for (int i = 0; i <  wordList.size(); i++) {
      String str = (String) wordList.get(i);
      wordList.set(i, str.replace("{{quote}}", "\\\"").replace("{{squote}}", "\\\'"));
    }
    System.out.println(wordList.size());
    for (int i = 0; i < wordList.size(); i++) {
      System.out.println(wordList.get(i));
    }
  }
  
  public static void test32() throws Exception {
    Connection dbConn = null;
    PreparedStatement stmt = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TD_OA2");
      
      //stmt = dbConn.p
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, null);
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  
  public static void test31() throws Exception {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    String dateStr = "1968-01-01";
    Date date = format.parse(dateStr);
    
    System.out.println("dateStr>>" + dateStr);
    System.out.println("date>>" + format.format(date));
  }
  public static void test30() throws Exception {
    // 这个类主要是设置邮件
    T9MailSenderInfo mailInfo = T9MailSenderInfo.build("mailConfBiligual");
    if (mailInfo == null) {
      System.out.println("请检查邮件配置信息是否正确！");
      return;
    }
    mailInfo.setToAddress("172000390@qq.com");
    mailInfo.setSubject("T9 Mail Test T9 邮件测试 ______New");
    mailInfo.setContent("<h1>这是T9 发送的邮件测试!______New</h1>");
    // 这个类主要来发送邮件
    T9SimpleMailSender sms = new T9SimpleMailSender();
//    sms.sendTextMail(mailInfo)
    ;// 发送文体格式
    String[] attachs = new String[]{
        "C:\\Users\\yzq\\Desktop\\近期工作\\索尼VPL-EX175投影机.png",
        "C:\\Users\\yzq\\Desktop\\近期工作\\总部研发中心投影仪.zip",
        "C:\\Users\\yzq\\Desktop\\近期工作\\t9patch_aa1234_aaaa.properties"
    };
    mailInfo.setAttachFileNames(attachs);
    sms.sendHtmlAndAttachMail(mailInfo);// 发送html格式
    System.out.println("发送邮件成功！");
  }
  
  public static void test29() throws Exception {
    for (int i = 0; i < 300; i++) {
      int t = i / 60;
      System.out.println(t);
    }
    Connection dbConn = null;
//    DatabaseMetaData meta =  dbConn.getMetaData();
//    meta.getTables(arg0, arg1, arg2, arg3)
  }
  
  public static void test28() throws Exception {
    System.out.println(Charset.defaultCharset());
    System.out.println(System.getProperty("file.encoding"));
  }
  
  public static void test27() throws Exception {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TD_OA2");
      Map metaMap = new HashMap();
      metaMap.put("M1", "M1");
      metaMap.put("M2", "M2");
      metaMap.put("MEX101", "MEX101");
      metaMap.put("MEX200", "MEX200");
      
      T9MetaDbHelper update = new T9MetaDbHelper();
      System.out.println("update done.");
      
//      List<T9SignFile> fileList = update.searchFileList(dbConn, "", metaMap, "", "1005_49bf1c1f40219e6921dbce5095309bc2");
//      for (int i = 0; i < fileList.size(); i++) {
//        T9SignFile file = fileList.get(i);
//        System.out.println(file.toJson());
//      }
      T9SignFile file = update.loadFile(dbConn, "1005_49bf1c1f40219e6921dbce5095309bc2", 0);
      System.out.println(file.toJson());
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }

  public static void test26() throws Exception {
    // 这个类主要是设置邮件
    MailSenderInfo mailInfo = new MailSenderInfo();
    mailInfo.setMailServerHost("smtp.sina.com.cn");
    mailInfo.setMailServerPort("25");
    mailInfo.setValidate(true);
    mailInfo.setUserName("yaozhiqiang01@sina.com");
    mailInfo.setPassword("");// 您的邮箱密码
    mailInfo.setFromAddress("yaozhiqiang01@sina.com");
    mailInfo.setToAddress("t9@tongda2000.com");
    mailInfo.setSubject("T9 Mail Test T9 邮件测试");
    mailInfo.setContent("<h1>这是T9 发送的邮件测试!</h1>");
    // 这个类主要来发送邮件
    SimpleMailSender sms = new SimpleMailSender();
    // sms.sendTextMail(mailInfo);// 发送文体格式
    sms.sendHtmlMail(mailInfo);// 发送html格式
    System.out.println("发送邮件成功！");
  }
  
  public static void test25() throws Exception {
    System.out.println(new File("D:\\MYOA\\attach\\email\\1002").getName());
    System.out.println(new File("D:\\MYOA\\attach\\email\\1002\\1793362259.管理日报_党群_0125_0129.doc").getName());
    System.out.println(new File("D:\\MYOA\\attach\\email\\1002\\1793362259.管理日报_党群_0125_0129.doc").getAbsolutePath());
    System.out.println(new File("D:\\MYOA\\attach\\email\\1002\\1793362259.管理日报_党群_0125_0129.doc").getParent());
  }
  
  public static void test24() throws Exception {
    List list = new ArrayList();
    for (int i = 0; i < 100; i++) {
      Map map = new HashMap();
      map.put("num", String.valueOf(i));
      list.add(map);
    }
    T9Utility.sortDesc(list, "num", T9MapComparator.TYPE_STR);
    for (int i = 0; i < 100; i++) {
      Map map = (Map)list.get(i);
      System.out.println(map.get("num"));
    }
  }
  
  public static void test23() throws Exception {
    T9DataSourceLoader.loadDataBases("D:\\project\\t9\\webroot\\t9\\WEB-INF\\config\\dbconfig.properties");
  }
  
  public static void test22() throws Exception {
    List list = null;
    String srcPass = "";
    String cryptPass = T9PassEncrypt.encryptPass(srcPass);
    System.out.println("cryptPass>>" + cryptPass);
    System.out.println("isValid>>" + T9PassEncrypt.isValidPas(srcPass, cryptPass));
    //System.out.println(T9PassEncrypt.isValidPas("123qwe!@#", "$1$tk3.y73.$946qMJjq9ZBVI9v3FONMa0"));
    //System.out.println(T9PassEncrypt.encryptPass("123qwe!@#"));
    //T9PassEncrypt.outputLong(T9PassEncrypt.truncLong(0x0FFFFFFFFFFFFFFFl));
    //System.out.println((T9PassEncrypt.int2Long(0x10FFFFFF)));
    //System.out.println(T9PassEncrypt.byte2Int((byte)0x80));
    //T9PassEncrypt.outputLong(T9PassEncrypt.int2Long(0xBC37190B));
  }
  public static void test21() throws Exception {
    String str = "a\nb";
    System.out.println(str);
    str = str.replace("\n", "");
    System.out.println(str);
  }
  
  
  
  public static void test19() throws Exception {
    T9ZipFileUtility.doZip("E:\\TDDOWNLOAD\\新建文件夹", "C:\\Users\\yzq\\Desktop\\1.zip");
  }
  
  public static void test18() throws Exception {
    System.out.println((int)"A".charAt(0));
  }
  
  public static void test17() throws Exception {
    System.out.println(Pattern.matches("^\\d{4}_[0-9a-z]{32}_.*$", "1004_0c9537f8d8c281511ce0815cf41eb0b4_数据字典.docx"));
  }
  
  public static void test16() throws Exception {
    System.out.println(T9SysProps.getAttachPath());
  }
  
  public static void test15() throws Exception {
    byte[] array = T9UsbKey.str2Bytes("AB");
    for (int i = 0; i < array.length; i++) {
      byte currByte = array[i];
      byte heigh = (byte)((currByte & 0xF0) >> 4);
      byte low = (byte)(currByte & 0x0F);
      System.out.println(heigh + ">>" + low);
    }
  }
  public static void test14() throws Exception {
    //System.out.println(T9UsbKey.md5Hex("tVHbkPWW57Hw.".getBytes()));
    //System.out.println(T9UsbKey.md5Hex("a".getBytes()));
    //boolean isPass = T9UsbKey.digestComp("68E24FAD8D3C3F7D06E3679CDBB4A122", "6934", T9UsbKey.md5Hex("tVHbkPWW57Hw."));
    //boolean isPass = T9UsbKey.digestComp("A230C667460EE8BC9A6491977E8BB70A", "9543", "04e5e2036955ea3aea6dedf92fb23731");
    boolean isPass = T9UsbKey.digestComp("D7AE40DB5ED6CB2512EE15EB760D14ED", "998989", T9UsbKey.md5Hex("tVHbkPWW57Hw."));
    System.out.println(isPass);
  }
  public static void test13() throws Exception {
    System.out.println(T9Utility.isSysDateFormat("2010-10-10 12:20:21"));
    System.out.println(T9Utility.isSysDateFormat("2010-10-10 12:20:21:334"));
    System.out.println(T9Utility.isSysDateFormat("2010-10-10"));
    System.out.println(T9Utility.isSysDateFormat("2010-10-1a"));
  }
  
  
  public static void test12() throws Exception {
//    byte[] array = T9UsbKey.hexstr2array("AB");
//    for (int i = 0; i < array.length; i++) {
//      byte currByte = array[i];
//      byte heigh = (byte)((currByte & 0xF0) >> 4);
//      byte low = (byte)(currByte & 0x0F);
//      System.out.println(heigh + ">>" + low);
//    }
  }
  public static void test11() throws Exception {
    System.out.println(System.currentTimeMillis());
    System.out.println(T9Guid.getRawGuid());    
    System.out.println(System.currentTimeMillis());
    System.out.println("----------------");
    System.out.println(T9Guid.getGuid());
    System.out.println(System.currentTimeMillis());
    System.out.println("----------------");
    System.out.println(T9Guid.getGuid(T9Guid.BEFORE_MD5));
    System.out.println(System.currentTimeMillis());
    System.out.println("----------------");
  }
  public static void test10() throws Exception {
    System.out.println(T9DBUtility.getDayFilter("TIME", T9Utility.parseDate("2010-10-01")));
    System.out.println(T9DBUtility.getWeekFilter("TIME", T9Utility.parseDate("2010-10-01")));
    System.out.println(T9DBUtility.getMonthFilter("TIME", T9Utility.parseDate("2010-3-09")));
    System.out.println(T9DBUtility.getYearFilter("TIME", T9Utility.parseDate("2010-10-21")));
  }
  public static void test9() throws Exception {
    String[] timeArray = T9Utility.getDateLimitStr();
    System.out.println("日" + timeArray[0] + ">>" +timeArray[1]);
    timeArray = T9Utility.getWeekLimitStr();
    System.out.println("周" + timeArray[0] + ">>" +timeArray[1]);
    timeArray = T9Utility.getMonthLimitStr();
    System.out.println("月" + timeArray[0] + ">>" +timeArray[1]);
    timeArray = T9Utility.getYearLimitStr();
    System.out.println("年" + timeArray[0] + ">>" +timeArray[1]);
  }
  public static void test8() throws Exception {
    Calendar cd = Calendar.getInstance();   
    int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天   
    cd.set(Calendar.DAY_OF_YEAR, 1);
    System.out.println(T9Utility.getDateTimeStr(cd.getTime()));
  }
  public static void test7() throws Exception {
    GregorianCalendar   now = new GregorianCalendar(2003, 9, 33);   
    System.out.println(now.get(Calendar.DAY_OF_MONTH));
    System.out.println(now.get(Calendar.DAY_OF_YEAR));
  }
  public static void test6() throws Exception {
    Class cla=Class.forName("t9.core.funcs.person.data.T9Person"); 
    Field[] f=cla.getDeclaredFields(); 
    for (Field field : f) { 
     System.out.println("属性="+field.toString()); 
     System.out.println("数据类型＝"+field.getType()); 
     System.out.println("属性名＝"+field.getName()); 
     int mod=field.getModifiers(); 
     System.out.println("属性修饰符＝"+Modifier.toString(mod)); 
    } 
  }
  public static void test5() throws Exception {
    Pattern pattern = Pattern.compile("([+-*]+)中文");
    String str = "+-*/+-*/".replaceAll("[+]", "\\\\+");
    T9Out.println(str);
    str = str.replaceAll("[-*]", "\\\\*");
    T9Out.println(str);
  }
  
  public static void test4() throws Exception {
    String srcStr = "(名称+密码+出生日期)*(工龄*系数-调整15/调整15调整15)";
    String destStr = T9RegexpUtility.replaceTitle(srcStr, "名称", "get(data_1)");
    
    destStr = T9RegexpUtility.replaceTitle(destStr, "密码", "get(data_2)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "出生日期", "get(data_3)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "工龄", "get(data_4)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "系数", "get(data_5)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "调整15", "get(data_6)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "调整15调整15", "get(data_7)");
    
    T9Out.println(destStr);
    
    srcStr = "(名称+密码+出生日期)*(工龄*系数-1/11)";
    destStr = T9RegexpUtility.replaceTitle(srcStr, "名称", "get(data_1)");
    
    destStr = T9RegexpUtility.replaceTitle(destStr, "密码", "get(data_2)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "出生日期", "get(data_3)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "工龄", "get(data_4)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "系数", "get(data_5)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "1", "get(data_6)");
    destStr = T9RegexpUtility.replaceTitle(destStr, "11", "get(data_7)");
    
    T9Out.println(destStr);
  }
  
  public static void test3() throws Exception {
    String dateStr = "2010-02-24 09:17:01";
    System.out.println(T9Utility.getDateTimeStr(T9Utility.parseDate(dateStr)));
  }
    
  public static void test2() throws Exception {
    Date[] dateArray = T9Utility.getDateLimit(new Date());
    System.out.println(T9Utility.getDateTimeStr(dateArray[0]));
    System.out.println(T9Utility.getDateTimeStr(dateArray[1]));
    System.out.println(T9DBUtility.curDayFilter("oracle", "BIRTHDAY"));
  }
  
  public static void test1() {
    Map<String, String> map = new HashMap<String, String>();
    List<String> list = new ArrayList<String>();
    if (List.class.isAssignableFrom(list.getClass())) {
      T9Out.println(true);
    }else {
      T9Out.println(false);
    }
    Class cls = T9InnerBean1.class;
    Field[] fieldArray = cls.getDeclaredFields();
    for (int i = 0; i < fieldArray.length; i++) {
      Field field = fieldArray[i];
      String fieldName = field.getName();
      if (List.class.isAssignableFrom(field.getType())) {
        T9Out.println(fieldName + ">>List");
      }else if (Map.class.isAssignableFrom(field.getType())) {
        T9Out.println(fieldName + ">>Map");
      }else {
        T9Out.println(fieldName + ">>other");
      }
    }
  }
}
