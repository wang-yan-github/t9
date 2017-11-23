package t9.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页数据存储
 * @author yzq
 *
 */
public class T9PageDataList {
  //全部记录条数
  private int totalRecord = 0;
  //数据记录列表
  private List<T9DbRecord> recordList = new ArrayList<T9DbRecord>();
  
  /**
   * 转换成Json字符串
   * @return
   */
  public String toJson() {
    StringBuffer rtBuf = new StringBuffer("{totalRecord:");
    rtBuf.append(totalRecord);
    
    rtBuf.append(",pageData:[");
    int recordCnt = this.getRecordCnt();
    for (int i = 0; i < recordCnt; i++) {
      T9DbRecord record = this.getRecord(i);
      rtBuf.append(record.toJson());
      if (i < recordCnt - 1) {
        rtBuf.append(",");
      }
    }
    rtBuf.append("]");
    rtBuf.append("}");
    return rtBuf.toString();
  }
  /**
   * 取得记录的条数
   * @return
   */
  public int getRecordCnt() {
    return this.recordList.size();
  }
  /**
   * 取得记录
   * @param index
   * @return
   */
  public T9DbRecord getRecord(int index) {
    return this.recordList.get(index);
  }
  /**
   * 添加记录
   * @param record
   */
  public void addRecord(T9DbRecord record) {
    this.recordList.add(record);
  }
  
  public int getTotalRecord() {
    return totalRecord;
  }
  public void setTotalRecord(int totalRecord) {
    this.totalRecord = totalRecord;
  }
}
