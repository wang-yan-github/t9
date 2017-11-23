package t9.core.esb.server.data;

import t9.core.util.auth.T9DigestUtility;
import t9.core.util.file.T9FileUtility;

public class T9RandomFileWrap {
  private byte[] dataBuf = null;
  private String md5 = "";
  
  public T9RandomFileWrap(String file) {
    try {
       dataBuf = T9FileUtility.loadFile2Bytes(file);
       md5 = T9DigestUtility.md5File(file);
    }catch(Exception ex) {  
      ex.printStackTrace();
    }
  }
  
  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  /**
   * 取得文件长度
   * @return
   */
  public int getFileLength() {
    if (dataBuf == null) {
      return 0;
    }
    return dataBuf.length;
  }
  
  /**
   * 读取
   * @param buf
   * @param offset
   */
  public void read(byte[] buf, int offset, int len) {
    if (dataBuf == null || buf == null || offset >= dataBuf.length) {
      return;
    }
    if (len > buf.length) {
      len = buf.length;
    }
    int end = offset + len;
    if (end > dataBuf.length) {
      end = dataBuf.length;
    }
    for (int i = 0; i < len; i++) {
      buf[i] = dataBuf[offset + i];
    }
  }
  /**
   * 读取
   * @param buf
   * @param offset
   */
  public void readFull(byte[] buf, int offset) {
    if (dataBuf == null || buf == null || offset >= dataBuf.length) {
      return;
    }
    long end = offset + buf.length;
    if (end > dataBuf.length) {
      return;
    }
    for (int i = 0; i < buf.length; i++) {
      buf[i] = dataBuf[offset + i];
    }
  }
}
