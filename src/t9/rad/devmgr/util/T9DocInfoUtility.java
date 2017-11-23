package t9.rad.devmgr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import t9.core.data.T9Props;
import t9.rad.devmgr.global.T9RadDevMgrConst;

public class T9DocInfoUtility {
  /**
   * 
   * @param filePath
   * @return
   * @throws Exception
   */
  public static List<T9Props> loadInfoList(String filePath) throws Exception {
    List<T9Props> rtList = new ArrayList<T9Props>();
    File moduleFile = new File(filePath);
    String[] fileArray = moduleFile.list();
    for (int i = 0; i < fileArray.length; i++) {
      String fileName = fileArray[i];
      String dirPath = filePath + "\\" + fileName;
      File dirFile = new File(dirPath);
      if (!dirFile.isDirectory()) {
        continue;
      }
      String infoPath = dirPath + "\\info.text";
      File infoFile = new File(infoPath);
      if (!infoFile.exists()) {
        continue;
      }
      T9Props infoProps = new T9Props();
      infoProps.loadProps(infoPath);
      infoProps.addProp(T9RadDevMgrConst.ENTRY_DIR, fileName);
      rtList.add(infoProps);
    }
    
    return rtList;
  }
  /**
   * 转换成Select下拉框用数据Json
   * @param infoList
   * @param id
   * @param value
   * @return
   */
  public static String toSelectJson(List<T9Props> infoList, String id, String value) {
    StringBuffer rtBuf = new StringBuffer("{");
    rtBuf.append(id);
    rtBuf.append(":{value:\"");
    rtBuf.append(value);
    rtBuf.append("\",data:[");
    for (int i = 0; i < infoList.size(); i++) {
      T9Props entry = infoList.get(i);
      String subsysDir = entry.get(T9RadDevMgrConst.ENTRY_DIR);
      String subsysDesc = entry.get(T9RadDevMgrConst.ENTRY_DESC);
      if (i > 0) {
        rtBuf.append(",");
      }
      rtBuf.append("{code:\"");
      rtBuf.append(subsysDir);
      rtBuf.append("\", desc:\"");
      rtBuf.append(subsysDesc);
      rtBuf.append("\"}");
    }
    rtBuf.append("]}}");
    return rtBuf.toString();
  }
}
