package t9.subsys.inforesouce.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.inforesouce.data.T9Node;

public class T9TouchGraphAct {
  private Map<String , T9Node> map= new HashMap();
  private Map<String , String[]> map2 = new HashMap();
  public String getArray(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String ids = request.getParameter("id");
    if (T9Utility.isNullorEmpty(ids)) {
      ids = "28";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.setMap();
      StringBuffer sb = new StringBuffer("{centerNode:");
      String[] ss = ids.split(",");
      T9Node cNode = null;
      if (ss.length > 1) {
        String nodeName = "";
        for (String s : ss) {
          if (!"".equals(s)) {
            T9Node node = (T9Node)map.get(s);
            if (node != null) {
              String tmp = node.getNodeName();
              if (!this.findId(nodeName, tmp))
                nodeName += tmp + ",";
            }
          }
        }
        cNode = new T9Node(ids,true,nodeName,120,"","");
      } else {
        cNode = map.get(ss[0]);
      }
      String str = cNode.toJson();
      sb.append(str);
      sb.append(",nodes:[");
      Set<String> set = map.keySet();
      for (String tmp : set) {
        if (!this.findId(ids, tmp)) {
          T9Node node = (T9Node)map.get(tmp);
          sb.append(node.toJson() + ",");
        }
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append("]}");
      String data = sb.toString();
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 判段id是不是在str里面
   * @param str
   * @param id
   * @return
   */
  public  boolean findId(String str, String id) {
    if(str == null || id == null || "".equals(str) || "".equals(id)){
      return false;
    }
    String[] aStr = str.split(",");
    for(String tmp : aStr){
      if(tmp.equals(id)){
        return true;
      }
    }
    return false;
  }
  public String getSubject(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String ids = request.getParameter("id");
    String subject = request.getParameter("subject");
    if (T9Utility.isNullorEmpty(ids)) {
      ids = "28";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.setMap();
      this.setAllSubject();
      StringBuffer sb = new StringBuffer("{centerNode:");
      
      T9Node cNode = null;
      String[] ss = ids.split(",");
      if (ss.length > 1) {
        String nodeName = "";
        for (String s : ss) {
          if (!"".equals(s)) {
            T9Node node = (T9Node)map.get(s);
            if (node != null) {
              String tmp = node.getNodeName();
              if (!this.findId(nodeName, tmp))
                nodeName += tmp + ",";
            }
          }
        }
        cNode = new T9Node(ids,true,nodeName,120,"","");
      } else {
        cNode = map.get(ss[0]);
      }
      String str = cNode.toJson();
      sb.append(str);
      String[] ssb = map2.get(subject);
      sb.append(",nodes:[");
      for (String tmp : ssb) {
        sb.append(toJson(tmp) + ",");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append("]}");
      String data = sb.toString();
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String toJson(String value) {
    StringBuffer sb = new StringBuffer();
    sb.append("{id:'" + value + "'");
    sb.append(",isRect:false" );
    sb.append(",nodeName:'" + value + "'");
    sb.append(",len:300");
    sb.append(",title:'" + value + "'");
    sb.append(",relation:'" + value + "'");
    sb.append("}");
    return sb.toString();
  }
  public void setAllSubject() {
    String[] ss1 = {"命令","决定","公告"
        ,"通告","通知" ,"通报" ,"议案","报告" ,"请示" };
    map2.put("文种", ss1);
    String[] ss2 = {"草拟","审核","签发","会签","复核" ,"缮印","用印","登记" ,"分发" ,"签收","拟办","批办"};
    map2.put("业务行为", ss2);
    String[] ss3 = {"通知" ,"通报" ,"议案","报告" ,"请示","批复" ,"意见" ,"函"};
    map2.put("姓名", ss3);
    String[] ss4 = {"会议纪要" ,"指示" ,"决议" ,"公报","条例","规定" };
    map2.put("地名", ss4);
    String[] ss5 = {"命令","决定","公告"
        ,"通告","通知" ,"通报"};
    map2.put("组织机构", ss5);
  }
  public void setMap() {
    T9Node node1 = new T9Node("7",true,"外事改革新突破.doc",120,"","");
    map.put("7", node1);
    T9Node node2 = new T9Node("3",false,"包机",290,"","");
    map.put("3", node2);
    T9Node node3 = new T9Node("4",false,"出访",370,"","");
    map.put("4", node3);
    T9Node node4 = new T9Node("5",false,"出国",200,"","");
    map.put("5", node4);
    T9Node node5 = new T9Node("10",false,"考察",400,"","");
    map.put("10", node5);
    T9Node node6 = new T9Node("11",false,"人员",220,"","");
    map.put("11", node6);
    T9Node node27 = new T9Node("27",true,"外交部领导外事工作.doc",350,"","");
    map.put("27", node27);
    T9Node node28 = new T9Node("28",false,"团组",350,"","");
    map.put("28", node28);
    T9Node node29 = new T9Node("29",false,"关系",400,"","");
    map.put("29", node29);
    T9Node node39 = new T9Node("39",false,"招待会",380,"","");
    map.put("39", node39);
    T9Node node40 = new T9Node("40",false,"祝酒词",280,"","");
    map.put("40", node40);
    T9Node node9 = new T9Node("2",true,"我市外事活动新动向.doc",330,"","");
    map.put("2", node9);
    T9Node node30 = new T9Node("30",false,"交流",330,"","");
    map.put("30", node30);
    T9Node node31 = new T9Node("31",false,"友协",320,"","");
    map.put("31", node31);
    T9Node node33 = new T9Node("33",false,"宴会",330,"","");
    map.put("33", node33);
    T9Node node32 = new T9Node("32",true,"北京对外交流活动.doc",300,"","");
    map.put("32", node32);
    T9Node node36 = new T9Node("36",false,"邀请",440,"","");
    map.put("36", node36);
    T9Node node44 = new T9Node("44",true,"拟派出国人员情况表.doc",230,"","");
    map.put("44", node44);
//    T9Node node45 = new T9Node("45",true,"涉外礼仪培训教材.doc",200,"","");
//    map.put("45", node45);
  }
}
