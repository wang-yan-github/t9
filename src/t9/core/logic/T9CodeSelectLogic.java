package t9.core.logic;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import t9.core.dto.T9CodeLoadParam;
import t9.core.dto.T9CodeLoadParamSet;
import t9.core.load.T9CodeLoader;
import t9.core.util.T9Utility;

public class T9CodeSelectLogic {
  /**
   * 加载选择框数据
   * @param dbConn
   * @param paramSet
   * @return
   * @throws Exception
   */
  public String loadSelectData(Connection dbConn,
      T9CodeLoadParamSet paramSet) throws Exception {
    
    Map<String, List> dataMap = loadSelectDataMap(dbConn, paramSet);
    //返回Json字符串
    StringBuffer rtBuf = new StringBuffer("{");
    Iterator<T9CodeLoadParam> iParam = paramSet.itParams();
    int iCnt = 0;
    while (iParam.hasNext()) {
      T9CodeLoadParam param = iParam.next();
      String cntrlId = param.getCntrlId();
      List<String[]> dataList = dataMap.get(cntrlId);
      int codeCnt = dataList.size();
      if (iCnt > 0) {
        rtBuf.append(",");
      }
      rtBuf.append(cntrlId);
      rtBuf.append(":");
      rtBuf.append("{value:");
      rtBuf.append("\"");
      rtBuf.append(T9Utility.null2Empty(param.getValue()));
      rtBuf.append("\",data:[");
      String isMustFill = param.getIsMustFill();
      boolean insertEmpty = false;
      if (!T9Utility.isNullorEmpty(isMustFill)
           && isMustFill.equals("0")) {
        rtBuf.append("{code:\"\",desc:\"\"}");
        insertEmpty = true;
      }
      for (int i = 0; i < codeCnt; i++) {
        String[] codeRecord = dataList.get(i);
        if (insertEmpty || i > 0) {
          rtBuf.append(",");
        }
        rtBuf.append("{code:\"");
        rtBuf.append(T9Utility.encodeSpecial(codeRecord[0]));
        rtBuf.append("\",desc:\"");
        rtBuf.append(T9Utility.encodeSpecial(codeRecord[1]));
        rtBuf.append("\"}");
      }
      rtBuf.append("]}");
      iCnt++;
    }
    rtBuf.append("}");
    return rtBuf.toString();
  }
  /**
   * 加载选择框数据
   * @param dbConn
   * @param paramSet
   * @return
   * @throws Exception
   */
  public Map loadSelectDataMap(Connection dbConn,
      T9CodeLoadParamSet paramSet) throws Exception {
    Map<String, List> dataMap = new LinkedHashMap<String, List>();
    //首先处理不联动重新加载的数据
    Iterator<T9CodeLoadParam> iParams = paramSet.itParams();
    while (iParams.hasNext()) {
      T9CodeLoadParam param = iParams.next();
      if (!T9Utility.isNullorEmpty(param.getReloadBy())) {
        continue;
      }
      List<String[]> dataList = T9CodeLoader.loadData(dbConn, param);
      dataMap.put(param.getCntrlId(), dataList);
      if (T9Utility.isNullorEmpty(param.getValue())) {
        if (dataList.size() > 0) {
          param.setValue(dataList.get(0)[0]);
        }
      }
    }
    //然后处理联动重新加载的数据
    iParams = paramSet.itParams();
    while (iParams.hasNext()) {
      T9CodeLoadParam param = iParams.next();
      if (T9Utility.isNullorEmpty(param.getReloadBy())) {
        continue;
      }
      String reloadBy = param.getReloadBy();
      T9CodeLoadParam reloadParam = paramSet.getParam(reloadBy);
      if (reloadParam != null) {
        param.setFilterValue(reloadParam.getValue());
      }      
      List<String[]> dataList = T9CodeLoader.loadData(dbConn, param);
      dataMap.put(param.getCntrlId(), dataList);
      if (T9Utility.isNullorEmpty(param.getValue())) {
        if (dataList.size() > 0) {
          param.setValue(dataList.get(0)[0]);
        }
      }
    }
    return dataMap;
  }
}
