package t9.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;

import t9.core.data.T9AuthKeys;
import t9.core.util.auth.T9Authenticator;
import t9.core.util.cmd.T9CmdFileUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.file.T9NameFileFilter;
import t9.core.util.file.T9ZipFileTool;

public class T9Tools {
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      String cmd = args[0];
      if (cmd.equals("1")) {        
      }else if (args[0].equals("8")) {
        String deployDir = null;
        if (args.length > 4) {
          deployDir = args[4];
        }
        extractUpdateFile(args[1], args[2], args[3], deployDir);
        //带加密的拷贝目录
      }else if (args[0].equals("encryptFiles")) {
        doEncryptCopyDir(args[1], args[2], args[3], T9AuthKeys.getPassword(null));
      }else if (args[0].equals("9")) {
        doZip(args[1], args[2]);
      //加密口令
      }else if (args[0].equals("13")) {
        String passStr = T9Authenticator.ciphEncryptStr(args[1]);
        System.out.println(passStr);
      //解密口令
      }else if (args[0].equals("14")) {
        String passStr = T9Authenticator.ciphDecryptStr(args[1]);
        if (passStr.length() < 1) {
          System.out.println("emptyPass");
        }else {
          System.out.println(passStr);
        }
      }else if (args[0].equals("18")) {
        T9TimeCounter tc = new T9TimeCounter();
        String newDir = args[1];
        String oldDir = args[2];
        String outDir = args[3];
        String copyEmpyDir = args[4];
        T9FileUtility.copyDirDiff(newDir, oldDir, outDir, null, copyEmpyDir.equalsIgnoreCase("true"));
        tc.logTime("copyTime");
      }else if (cmd.equals("listWritable")) {
        String outFile = args[1];
        List<String> fileList = new ArrayList<String>();
        String filterStr = args[3];
        String[] filters = null;
        if (filterStr != null && filterStr.length() > 0) {
          filters = filterStr.split(",");
        }
        String[] fileArray = args[2].split(",");
        for (String filePath : fileArray) {
          listWritable(filePath, fileList, filters);
        }
        T9FileUtility.storeArray2Line(outFile, fileList);
      }else if (cmd.equals("checkSemicolon")) {
        T9CmdFileUtility.checkSemicolon(args[1], args[2], args[3]);
      }else if (cmd.equals("compatSrc")) {
        T9CmdFileUtility.compactSrc(args[1], args[2]);
      }else if (cmd.equals("setFileTime")) {
        T9FileUtility.setLastModified(args[1], new Date().getTime() + 10000);
      }else if (cmd.equals("clearFile")) {
        T9CmdFileUtility.clearFile(args[1]);
      }else if (cmd.equals("transCode")) {
        T9CmdFileUtility.transCode(args[1], args[2], args[3], args[4]);
      }else if (cmd.equals("compactJs")) {
        T9CmdFileUtility.compactJsInpath(args[1], args[2]);
      }else {
        System.out.println("不支持的命令>>" + args[0]);
      }
      System.out.println("done.");
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Copy目录，并加密文件
   * @param srcDirName
   * @param destDirName
   * @param filter
   * @param passWord
   * @throws Exception
   */
  private static void doEncryptCopyDir(
      String srcDirName, String destDirName,
      String filter, char[] passWord) throws Exception {
    doCihpCopyDir(srcDirName, destDirName, filter, 
        passWord, Cipher.ENCRYPT_MODE);
  }
  
  /**
   * Copy目录，并解密文件
   * @param srcDirName
   * @param destDirName
   * @param filter
   * @param passWord
   * @throws Exception
   */
  private static void doDecryptCopyDir(
      String srcDirName, String destDirName,
      String filter, char[] passWord) throws Exception {
    
    doCihpCopyDir(srcDirName, destDirName, filter, 
        passWord, Cipher.DECRYPT_MODE);
  }
  
  private static void doCihpCopyDir(
      String srcDirName, String destDirName,
      String filter, char[] passWord, int mode) throws Exception {
    
    T9NameFileFilter fileFilter = null;
    if (filter != null && filter.length() > 0) {
      String[] filterArray = filter.split(",");
      List filterList = new ArrayList();
      for (int i = 0; i < filterArray.length; i++) {
        filterList.add(filterArray[i].trim());
      }
      fileFilter = new T9NameFileFilter(filterList, false);
    }
    long t1 = System.currentTimeMillis();
    Cipher cipher = T9SecurityUtility.getPassWordCipher(passWord, 
        T9AuthKeys.getSalt(null),
        T9AuthKeys.getItCnt(null),
        mode);
    T9FileUtility.setExt("cipher", cipher);
    T9FileUtility.copyDirCiph(srcDirName, destDirName, null, 
        fileFilter, false, true,
        passWord, 
        T9AuthKeys.getSalt(null),
        T9AuthKeys.getItCnt(null),
        mode);
    T9FileUtility.removeExt("cipher");
    long t2 = System.currentTimeMillis();
    System.out.println("totalTime>>" + (t2 - t1));
  }
  
  /**
   * 列表可写文件
   * @param path
   */
  private static void listWritable(String path,  List<String> fileList,
      String[] filters) {
    if (filters != null && filters.length > 0) {
      for (String filter : filters) {
        if (path.indexOf(filter) >= 0) {
          return;
        }
      }
    }
    File file = new File(path);
    if (!file.exists()) {
      return;
    }
    if (file.isFile() && file.canWrite()) {
      fileList.add(path);
      return;
    }else if (file.isDirectory()) {
      String[] fileArray = file.list();
      for (int i = 0; i < fileArray.length; i++) {
        listWritable(path + "\\" + fileArray[i], fileList, filters);
      }
    }
  }
  
  /**
   * 提取更新的文件
   * @param srcDirName            源目录
   * @param destDirName           输出目录
   * @param cpDateStr             比较时间串，yyyy-MM-dd HH:mm:ss/yyyy-MM-dd/yyyyMMdd
   * @throws Exception
   */
  private static void extractUpdateFile(
      String srcDirName,
      String destDirName,
      String cpDateStr,
      String deployDir) throws Exception  {
    
    String currDateTimeStr = deployDir;
    if (currDateTimeStr == null) {
      currDateTimeStr = T9Utility.getCurDateTimeStr("yyyyMMddHHmmssSSS");
    }
    ArrayList msrgList = new ArrayList();
    msrgList.add("compare dateTime>>" + cpDateStr);
    msrgList.add("extract start dateTime>>" + currDateTimeStr);
    destDirName += "\\" + currDateTimeStr;
    
    T9FileUtility.extractUpdateFile(
        srcDirName, 
        destDirName,
        msrgList,
        cpDateStr);
    
    String msrgFile = T9FileUtility.appendFileName(destDirName,
        T9Utility.getCurDateTimeStr("yyyyMMddHHmmssSSS") + ".log");
    T9FileUtility.storeArray2Line(msrgFile, msrgList);
  }
  
  private static void doZip(String srcDir, String destFile) throws Exception {
    T9ZipFileTool.doZip(srcDir, destFile);
  }
}
