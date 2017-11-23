package t9.core.funcs.portal.act;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.logic.T9FileContentLogic;
import t9.core.funcs.netdisk.logic.T9NetDiskLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.picture.logic.T9PictureLogic;
import t9.core.funcs.portal.util.T9PortalProducer;
import t9.core.funcs.portal.util.rules.T9ImgRule;
import t9.core.funcs.portal.util.rules.T9ModulesRule;
import t9.core.funcs.portal.util.rules.T9TextRule;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9PublicPortAct {
  private String sp = System.getProperty("file.separator");
  private String webPath = "core" + sp 
    + "funcs" + sp 
    + "portal" + sp 
    + "modules" + sp;
  /**type 1 网络硬盘， 2 文件柜，3 图片浏览， 4 图片新闻 ,5 数据库
   * @param args
   */
  public static final String ICON_FOLDER = "/" 
    + T9SysProps.getString(T9SysPropKeys.JSP_ROOT_DIR) 
    + "/core/styles/style1/img/folder.png";
  T9FileContentLogic fcl = new T9FileContentLogic();
  T9NetDiskLogic disk = new T9NetDiskLogic();
  T9PictureLogic lc = new T9PictureLogic();
  public String setPublicPort(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String type = request.getParameter("type");//区分各个模块名称（或类型）      String publicPath = request.getParameter("publicPath");//接收文件名称路径
      String picName = request.getParameter("picName");
      
      T9PortalProducer producer = null;
      if("null".equalsIgnoreCase(picName)&&"".equalsIgnoreCase(picName)){
        picName = "";
      }
      T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = 0;
      int startPage = 1;
      int endPage = 6;
      if(type.equalsIgnoreCase("1")){
                           //调用网络硬盘模块 Loginc
        producer = disk.getNetdiskInfoToDeskTop(dbConn,loginUser,publicPath,startPage,endPage);
//        System.out.println("网络硬盘:::::"+publicPath+"===="+type);
      } else if(type.equalsIgnoreCase("2")){
                        //调用文件柜模块Logic
        producer = fcl.getFileFolderInfoToDeskTop(dbConn,Integer.parseInt(publicPath),1,6);
//       System.out.println("文件柜:::"+publicPath+"==="+type);
      } else if(type.equals("3")){
                        //调用图片浏览 ACT
       producer = lc.getNetdiskInfoToDeskTop(dbConn,publicPath,0,5);
//       System.out.println("图片浏览::::"+picName+"==="+ publicPath+"===="+type);
      }else if(type.equals("4")){
                        //调用图片新闻模块
      } else if ("5".equals(type)) {
        //根据配置的数据库文件取出列表
        String dataPath = request.getSession().getServletContext().getRealPath(sp) 
          + webPath
          + "data" + sp 
          + "data.properties"; 
        producer = new T9PortalProducer();
        String sqlkey = request.getParameter("sqlKey");
        String sql = request.getParameter("sql");
        String ruleList = request.getParameter("ruleList");
        if (!T9Utility.isNullorEmpty(sqlkey)) {
          Map map = getDefSql(dataPath , sqlkey);
          sql = (String)map.get("sql");
          ruleList = (String)map.get("ruleList");
        } 
        ruleList = ruleList.substring(1, ruleList.length() - 1);
        String[] rules =  ruleList.split("\\},\\{");
        for (String r : rules) {
          if (!r.startsWith("{")) {
            r = "{" + r;
          }
          if (!r.endsWith("}")) {
            r = r + "}";
          }
          Map map = T9FOM.json2Map(r);
          String typeStr = (String)map.get("type");
          String showText = (String)map.get("showText");
          String[] showTexts = T9PortalProducer.convert2Array(showText);
          T9ModulesRule rule = null;
          if ("img".equals(typeStr)) {
            String imageAddress = (String)map.get("imageAddress");
            String linkAddress = (String)map.get("linkAddress");
            String[] las = T9PortalProducer.convert2Array(linkAddress);
            String[] ias = T9PortalProducer.convert2Array(imageAddress);
            rule = new T9ImgRule(showTexts, ias , las);
          } else {
            rule = new T9TextRule(showTexts);
          }
          producer.addRule(rule);
        }
        producer.setData(dbConn, sql);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA, producer.toJson());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public static Map getDefSql(String path , String key) throws Exception {
    Map map = new HashMap();
    try {
      File dataFile = new File(path);
      if (!dataFile.exists()) {
        dataFile.createNewFile();
      }
      Map<String , String> dataMap = new HashMap();
      T9FileUtility.load2Map(path, dataMap);
      String value = (String)dataMap.get(key).trim();
      int index = value.indexOf(":");
      int end = value.indexOf("\"ruleList\":[");
      String sqlStr = value.substring(index + 2 , end).trim();
      if (sqlStr.endsWith(",")) {
        sqlStr = sqlStr.substring(0 , sqlStr.length() - 1).trim();
      }
      if (sqlStr.endsWith("\"")) {
        sqlStr = sqlStr.substring(0 , sqlStr.length() - 1).trim();
      }
      map.put("sql", sqlStr);
      String ruleList = value.substring(end + "\"ruleList\":[".length(), value.length() - 2);
      map.put("ruleList", ruleList);
    } catch (Exception ex) {
      throw ex;
    }
    
    return map;
  }
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    String path = "D:\\project\\t9\\webroot\\t9\\core\\funcs\\portal\\modules\\data\\data.properties";
    try {
      Map map = getDefSql(path , "ddd");
      String sql = (String)map.get("sql");
      String ruleList = (String)map.get("ruleList");
      System.out.println(sql);
      System.out.print(ruleList);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

}
