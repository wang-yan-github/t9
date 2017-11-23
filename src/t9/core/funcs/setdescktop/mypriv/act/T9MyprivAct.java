package t9.core.funcs.setdescktop.mypriv.act;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.mypriv.logic.T9MyprivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.menu.data.T9SysMenu;

public class T9MyprivAct {
  private T9MyprivLogic logic = new T9MyprivLogic();
  
  /**
   * 设置别名
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setByName(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String byName = request.getParameter("BYNAME");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      if(user.getUserId().equals(byName)){
        request.setAttribute("message","用户名和别名不能相同");
        return "/core/funcs/setdescktop/mypriv/update.jsp";
      }
      
      if(this.logic.checkByName(dbConn, byName) > 0){
        request.setAttribute("message","用户名或别名 " + byName + " 已存在");
        return "/core/funcs/setdescktop/mypriv/update.jsp";
      }
      
      this.logic.setByName(dbConn, byName, user.getSeqId());
      
      request.setAttribute("message","已保存修改");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/funcs/setdescktop/mypriv/update.jsp";
  }
  
  /**
   * 获取页面信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map<String,String> map = this.logic.getPriv(dbConn, user.getSeqId());
      
      if("0".equals(map.get("POST_PRIV"))){
        map.put("POST_PRIV", "本部门");
      }
      else if("1".equals(map.get("POST_PRIV"))){
        map.put("POST_PRIV", "全体");
      }
      else if("2".equals(map.get("POST_PRIV"))){
        map.put("POST_PRIV", "指定部门");
        
        //当用户管理范围为指定部门时,列出用户的管理范围部门名称
        if(map.get("POST_DEPT") != null && !"".equals(map.get("POST_DEPT"))){
          String[] postDept = map.get("POST_DEPT").trim().split(",");
          
          String postDeptName = "";
          for (String s : postDept){
            if (s != null && !"".equals(s.trim())){
              postDeptName += this.logic.getPostDept(dbConn, Integer.parseInt(s)) + ',';
            }
          }
          
          if (postDeptName.endsWith(",")){
            map.put("POST_PRIV", "指定部门:" + postDeptName.substring(0, postDeptName.length() - 1));
          }
          else{
            map.put("POST_PRIV", "指定部门:" + postDeptName);
          }
        }
      }
      
      if("".equals(map.get("EMAIL_CAPACITY")) || map.get("EMAIL_CAPACITY") == null){
        map.put("EMAIL_CAPACITY", "不限制");
      }
      else{
        String e =  map.get("EMAIL_CAPACITY");
        if (T9Utility.isInteger(e)) {
          e = Integer.parseInt(e) + "";
        }
        map.put("EMAIL_CAPACITY",e + "MB");
      }
      
      if("".equals(map.get("FOLDER_CAPACITY"))|| map.get("FOLDER_CAPACITY") == null){
        map.put("FOLDER_CAPACITY", "不限制");
      }
      else{
        String e =  map.get("FOLDER_CAPACITY");
        if (T9Utility.isInteger(e)) {
          e = Integer.parseInt(e) + "";
        }
        map.put("FOLDER_CAPACITY", e + "MB");
      }
      if(map.get("USER_PRIV_OTHER") != null && !"".equals(map.get("USER_PRIV_OTHER"))){
        String[] userPrivOther = map.get("USER_PRIV_OTHER").trim().split(",");
        
        String privOtherName = "";
        for (String s : userPrivOther){
          if (s != null && !"".equals(s.trim())){
            privOtherName += this.logic.getUserPrivOther(dbConn, Integer.parseInt(s)) + ",";;
          }
        }
        
        if (privOtherName.endsWith(",")){
          map.put("USER_PRIV_OTHER", privOtherName.substring(0, privOtherName.length() - 1));
        }
        else{
          map.put("USER_PRIV_OTHER", privOtherName);
        }
      }
      
      //把map转化成json串
      String rtData = this.toJson(map);
      request.setAttribute(T9ActionKeys.RET_DATA, rtData);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  private String toJson(Map<String,String> m) throws Exception{
    StringBuffer sb = new StringBuffer("{");
    for (Iterator<Entry<String,String>> it = m.entrySet().iterator(); it.hasNext();){
      Entry<String,String> e = it.next();
      sb.append(e.getKey());
      sb.append(":\"");
      sb.append(e.getValue());
      sb.append("\",");
    }
    if (sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("}");
    return sb.toString();
  }
}