package t9.core.dto;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 下拉框数据加载参数集合
 * @author yzq
 *
 */
public class T9CodeLoadParamSet {
  //参数哈希表
  Map<String, T9CodeLoadParam> paramMap = new LinkedHashMap<String, T9CodeLoadParam>();
  /**
   * 新添加参数
   * @param param
   */
  public void addParam(T9CodeLoadParam param) {
    paramMap.put(param.getCntrlId(), param);
  }
  /**
   * 取得迭代器
   * @return
   */
  public Iterator<T9CodeLoadParam> itParams() {
    return paramMap.values().iterator();
  }
  /**
   * 取得参数
   * @param cntrlId
   * @return
   */
  public T9CodeLoadParam getParam(String cntrlId) {
    return paramMap.get(cntrlId);
  }
  
  /**
   * 取得参数
   * @param cntrlId
   * @return
   */
  public T9CodeLoadParam getParam() {
    Iterator<T9CodeLoadParam> iParam = itParams();
    if (iParam.hasNext()) {
      return iParam.next();
    }
    return null;
  }
  
  public String toString() {
    StringBuffer rtBuf = new StringBuffer();
    rtBuf.append("{");
    Iterator<T9CodeLoadParam> itParams = itParams();
    while (itParams.hasNext()) {
      T9CodeLoadParam param = itParams.next();
      rtBuf.append(param.getCntrlId() + ":");
      rtBuf.append(param.toString());
      rtBuf.append(",");
    }
    rtBuf.append("}");
    return rtBuf.toString();
  }
}
