package t9.core.funcs.jexcel.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import t9.core.data.T9DbRecord;
import t9.core.global.T9Const;

public class T9CSVUtil {
  public static void CVSWrite (PrintWriter printWriter ,ArrayList<T9DbRecord> dataArray ) throws Exception{
    T9CVSWriter csvw = new T9CVSWriter(printWriter);
    csvw.writeAll(dataArray);
    csvw.flush();
    csvw.close();
  }
  
  public static ArrayList<T9DbRecord> CVSReader(InputStream ips) throws Exception {
    return CVSReader(ips, T9Const.CSV_FILE_CODE);
  }
  public static ArrayList<T9DbRecord> CVSReader(InputStream ips, String charset) throws Exception {
    T9CSVReader csvReader = null;
    String[] header = null;
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    try {
      csvReader = new T9CSVReader(new InputStreamReader(ips, charset), ',');// importFile为要导入的文本格式逗号分隔的csv文件，提供getXX/setXX方法
      if (csvReader != null) {
        header = csvReader.readNext();
        String[] csvRow = null;// row

        while ((csvRow = csvReader.readNext()) != null) {
          T9DbRecord dbRecord = new T9DbRecord();
          for (int i = 0; i < csvRow.length; i++) {
            String temp = csvRow[i];
            if ("".equals(temp)) {
              temp = null;
            }
            if (header.length > i) {
              dbRecord.addField(header[i], temp);
            }
          }
          result.add(dbRecord);
        }
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
}
