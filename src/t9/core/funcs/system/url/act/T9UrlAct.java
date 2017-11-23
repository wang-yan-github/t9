package t9.core.funcs.system.url.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.logic.T9AddressLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9UrlAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.act.T9SystemAct");
  public String doLoginIn(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      //dologin
      String userName = request.getParameter("userName");
      String pwd = request.getParameter("pwd");
      if(userName == null){
        userName  = "";
      }
      if(pwd == null){
        pwd = "";
      }
      Map query = new  HashMap();
      query.put("user_id", userName);
      
      T9ORM t = new T9ORM();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person person = (T9Person)t.loadObjSingle(dbConn , T9Person.class , query);
      if(person != null && person.getPassword() == null){
        person.setPassword("");
      }
      if(person == null 
          || !pwd.equals(person.getPassword())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "用户名或密码错误!");
      }else{
        request.getSession().setAttribute("LOGIN_USER", person);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }finally{
      if(dbConn != null){
       // dbConn.close();
      }
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doLoginOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      //doLoginOut
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      request.getSession().removeAttribute("LOGIN_USER");
      person = null;
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }finally{
      if(dbConn != null){
       // dbConn.close();
      }
    }
    return "/core/funcs/display/login.jsp";
  }
  public String getMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      //doLoginOut
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      
      Map query = new  HashMap();
      query.put("SEQ_ID", person.getUserPriv());
      
      T9ORM t = new T9ORM();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9UserPriv priv = (T9UserPriv)t.loadObjSingle(dbConn , T9UserPriv.class , query);
      
      String funcIdStr = priv.getFuncIdStr();
      String[] menuIds = funcIdStr.split(",");
      StringBuffer sb = new StringBuffer("[");
      
      int i,j; 
      for(j = 0;j < menuIds.length;j++){ 
        for( i = j + 1 ;i < menuIds.length  ; i++){
          if(!menuIds[i].equals("") && !menuIds[j].equals("")){
            int iMenuId = Integer.parseInt(menuIds[i]);
            int iMenuIdNext = Integer.parseInt(menuIds[j]);
            if (iMenuId < iMenuIdNext){
              String tmp  = menuIds[i]; 
              menuIds[i] = menuIds[j]; 
              menuIds[j] = tmp;
            } 
          }
        } 
      }
      
      

      
      for(String menuId : menuIds){
        if(menuId.length() == 2){
          this.getMenuStringBuffer(dbConn, sb, menuId, menuIds, request.getContextPath());
        }
      }
      if(menuIds.length > 0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA , sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }finally{
      if(dbConn != null){
       // dbConn.close();
      }
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
  var menuData = [{name:'menuName',attachCtrl:true,isHaveChild:true,childMenu:[
         { name:'ddd',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
         , '-'
         , {name:'dd',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
         , {name:'sss',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
         , '-'
         ,{name:'dssss',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
  ]},{name:'dddd',isHaveChild:true,attachCtrl:true,childMenu:[{name:'adad',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
                                            , '-'
                                            , {name:'dada',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
                                            , {name:'dafdas',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
                                            , '-'
                                            , {name:'daf',action:test,icon:'/t9/raw/lh/rightmenu/image/addStep.gif',extData:'d'}
                                            ]}
  ,{name:'dddddd',isHaveChild:false,address:''}];
  */

  public void getMenuStringBuffer(Connection dbConn, StringBuffer sb, String menuId ,String[] menuIds , String contextPath) throws Exception{
    Map query = new  HashMap();
    query.put("MENU_ID", menuId);
    T9ORM t = new T9ORM();
    T9SysMenu menu = (T9SysMenu)t.loadObjSingle(dbConn , T9SysMenu.class , query);
    if(menu != null){
      sb.append("{name:'" + menu.getMenuName() + "',attachCtrl:true,isHaveChild:true,childMenu:[");
      
      String[] funcArray = menuIds;
      boolean isFirst = true;
      //查找二级
      for(int i = 0 ; i < funcArray.length ; i++){
        String funcTmp = funcArray[i];
        boolean isChild = funcTmp.startsWith(menuId);
        int funCount = 0 ;
        
        if(isChild && funcTmp.length() == 4 && !funcTmp.equals(menuId)){
          int count = 0 ;
          //查找三级
          Map funcQuery = new HashMap();
          funCount++;
          if(!isFirst){
            //加分割符
            sb.append(",'-',");
          }
          isFirst = false;
          funcQuery.put("MENU_ID", funcTmp);
          T9SysFunction functionTmp = (T9SysFunction)t.loadObjSingle(dbConn , T9SysFunction.class , funcQuery);
          
          for(int j = 0 ; j < funcArray.length ; j++){
            String funcTmp2 = funcArray[j];
            isChild = funcTmp2.startsWith(funcTmp);
            if(isChild && funcTmp2.length() == 6 && !funcTmp2.equals(funcTmp)){
              funcQuery.put("MENU_ID", funcTmp2);
              T9SysFunction function = (T9SysFunction)t.loadObjSingle(dbConn , T9SysFunction.class , funcQuery);
              String funcAddress = null;
              String imageAddress = null;
              imageAddress = contextPath + "/core/funcs/display/img/org.gif";
              if (function.getFuncCode().startsWith("/")) {
                funcAddress = contextPath + function.getFuncCode();
              }else {
                funcAddress = contextPath + "/core/funcs/" + function.getFuncCode() + "/";
              }
              
              sb.append("{name:'" + function.getFuncName() + "'" +
                  ",action:test" +
                  ",icon:'"+  imageAddress +"'" +
                  ",label:'" + functionTmp.getFuncName() +"',extData:'" + funcAddress +"'},");
              count++;
            }
          }
          if(count == 0){
            String funcAddress = null;
            String imageAddress = null;
            if (functionTmp.getFuncCode().startsWith("/")) {
              funcAddress = contextPath + functionTmp.getFuncCode() ;
              imageAddress = contextPath + "/core/funcs/display/img/org.gif";
            }else {
              funcAddress = contextPath + "/core/funcs/" + functionTmp.getFuncCode() + "/";
              imageAddress = contextPath + "/core/funcs/display/img/" + functionTmp.getFuncCode() + ".gif";
            }
            sb.append("{name:'" + functionTmp.getFuncName() + "'" +
                ",action:test" +
                ",icon:'"+  imageAddress +"'" +
                ",extData:'" + funcAddress +"'},");
            
          }
        }
        if(funCount > 0){
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      sb.append("]},");
    }
  }
  
  /**
   * 添加公共网址
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addPublicUrl(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //int seqId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String urlType = request.getParameter("urlType");
      String urlNo = request.getParameter("urlNo");
      String urlDesc = request.getParameter("urlDesc");
      String url = request.getParameter("url");
      String subType = request.getParameter("subType");
      //System.out.println(urlType+"SSS");
      if(urlType.equals("")){
        subType = "";
      }else {
        subType = "1";
      }
     
      Map m =new HashMap();
      m.put("urlNo", urlNo);
      m.put("urlType", urlType);
      m.put("urlDesc", urlDesc);
      m.put("url", url);
      m.put("subType", subType);
      T9ORM t = new T9ORM();

      t.saveSingle(dbConn, "url", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
