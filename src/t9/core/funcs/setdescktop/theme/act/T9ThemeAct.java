package t9.core.funcs.setdescktop.theme.act;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sanselan.Sanselan;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.theme.logic.T9ThemeLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.menu.data.T9SysMenu;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
public class T9ThemeAct {
  public final static String SOUND_PATH = "theme"+ File.separator +"sound" + File.separator ;
  public final static String AVATAR_PATH = "core"+ File.separator +"styles"+ File.separator +"imgs"+ File.separator +"avatar" + File.separator ;
  private T9ThemeLogic logic = new T9ThemeLogic();
  
  /**
   * 初始化表单使用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String initForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
   
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map<String,String> map = logic.getForm(dbConn, user.getSeqId());
      
      String wr[] = logic.getWeatherRss(dbConn);
      boolean show = logic.isAllowTheme(dbConn);
      
      map.putAll(T9FOM.json2Map(user.getParamSet()));
      map.put("RSS", wr[0]);
      map.put("WEATHER", wr[1]);
      map.put("SHOW_THEME", show ? "1" : "0");
      String rtData = this.toJson(map).toString();
      request.setAttribute(T9ActionKeys.RET_DATA, rtData);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
            
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取默认展开菜单的备选项
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      List<T9SysMenu> list = new ArrayList<T9SysMenu>();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      List<String> menuList = T9SystemAct.listUserMenu(dbConn, person);
      
      for (String s : menuList){
        if (s.trim().length() == 2){
          T9SysMenu menu = this.logic.getMenuList(dbConn, s);
          if (menu != null){
            list.add(menu);
          }
        }
      }
      StringBuffer sb = new StringBuffer("[");
      for(T9SysMenu o : list){
        sb.append("{");
        sb.append("\"seqId\":\"");
        sb.append(o.getMenuId());
        sb.append("\",");
        sb.append("\"menuName\":\"");
        sb.append(o.getMenuName());
        sb.append("\"}");
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]"); 
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  public String changeWeatherCity(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    String city = request.getParameter("WEATHER_CITY");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      user.setWeatherCity(city);
      
      T9UserinfoLogic logic = new T9UserinfoLogic();
      logic.updateWeatherCity(dbConn, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 更新修改
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setTheme(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    
    Iterator<String> iKeys = fileForm.iterateFileFields();
    
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
    
    String fileName = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String sp = System.getProperty("file.separator");
      String filePath = request.getSession().getServletContext().getRealPath(sp) + SOUND_PATH;
      fileName = user.getSeqId() + ".swf";
      fileForm.saveFile(fieldName, filePath + fileName);
    }
    
    Map<String,String> map = fileForm.getParamMap();
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      user.setTheme(map.get("THEME"));
      user.setMenuImage(map.get("MENU_IMAGE"));
      user.setMenuType(map.get("MENU_TYPE"));
      user.setMenuExpand(map.get("MENU_EXPAND"));
      user.setPanel(map.get("PANEL"));
      user.setCallSound(map.get("CALL_SOUND"));
      user.setSmsOn(map.get("SMS_ON"));
      user.setShowRss("on".equals(map.get("SHOW_RSS")) ? "1" : "0");
      user.setCallSound(map.get("CALL_SOUND"));
      user.setWeatherCity(map.get("WEATHER_CITY"));
      user.setNevMenuOpen(map.get("NEV_MENU_OPEN"));
      T9UserinfoLogic logic = new T9UserinfoLogic();
      Map param = new HashMap<String, String>();
      param.put("fx", map.get("FX"));
      param.put("SHOW_WEATHER", map.get("SHOW_WEATHER"));
      logic.addUserParam(dbConn, param, user);
      
      request.getSession().setAttribute("STYLE_INDEX", T9SystemAct.getStyleIndex(request));
      
      this.logic.setTheme(dbConn, user);
    } catch(Exception ex) {
      throw ex;
    }
    return "/core/funcs/setdescktop/theme/update.jsp";
  }
  
  /**
   * 设置昵称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setAvatarNickName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9FileUploadForm fileForm = new T9FileUploadForm();
    
    
    fileForm.parseUploadRequest(request);
    
    Iterator<String> iKeys = fileForm.iterateFileFields();
    InputStream is = fileForm.getInputStream((String)fileForm.iterateFileFields().next());
    
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
    
    String fileName = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      if (T9Utility.isNullorEmpty(fieldName)) {
        continue;
      }
      fileName = "upload-" + user.getSeqId();
      String sp = System.getProperty("file.separator");
      String filePath = request.getSession().getServletContext().getRealPath(sp) + AVATAR_PATH;
      File file = new File(filePath + fileName + ".gif");
      if (file.exists()) {
        file.delete();
      }
      fileForm.saveFile(fieldName, filePath + fileName + ".gif");
    }
    
    Map map = fileForm.getParamMap();
    
    //头像是否改变的标志
    String avatarVal = String.valueOf(map.get("avatarVal"));
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      Map<String,String> conf = this.logic.getAvatarConfig(dbConn);
      
      BufferedImage srcImage;
      try {
        srcImage = ImageIO.read(is);
        
      } catch (Exception ex) {
        srcImage = Sanselan.getBufferedImage(is);
      }
      
      if (srcImage != null) {
        int imageWidth = srcImage.getWidth(null);
        int imageHeight = srcImage.getHeight(null);
        if (Integer.parseInt(conf.get("width")) < imageWidth || Integer.parseInt(conf.get("height")) < imageHeight) {
          request.setAttribute("msg", "图片大小超过" + conf.get("width") + "*" + conf.get("height"));
          return "/core/funcs/setdescktop/avatar/update.jsp";
        }
      }
      
      
      if (!T9Utility.isNullorEmpty(avatarVal)) {
        user.setAuatar(avatarVal);
        map.put("AVATAR", avatarVal);
      }
      else {
        map.put("AVATAR", fileName);
        user.setAuatar(fileName);
      }
      
      if (this.logic.hasNickName(dbConn, map, user.getSeqId())){
        request.setAttribute("msg", "昵称已经存在");
        return "/core/inc/rtjson.jsp";
      }
      else{
        this.logic.setAvatarNickName(dbConn, map, user.getSeqId());
      }
      request.setAttribute("msg", "添加成功");
      return "/core/funcs/setdescktop/avatar/update.jsp";
    }catch(Exception ex) {
      request.setAttribute("msg", "添加未成功");
      throw ex;
    }finally{
      
    }
  }
  
  /**
   * 检查昵称是否重复
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkNickName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String nickName = request.getParameter("NICK_NAME");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int amount = this.logic.checkNickName(dbConn, nickName);
      
      request.setAttribute(T9ActionKeys.RET_DATA, amount + "");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功修改昵称和头像");
      
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取昵称,用于表单上的显示
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAvatarNickName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map<String,String> map = logic.getAvatarNickName(dbConn, user.getSeqId());
      
      String rtData = this.toJson(map).toString();
      request.setAttribute(T9ActionKeys.RET_DATA, rtData);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
            
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 获取头像属性(是否允许上传/宽/高)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAvatarConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map<String,String> map = logic.getAvatarConfig(dbConn);
      
      String rtData = this.toJson(map).toString();
      request.setAttribute(T9ActionKeys.RET_DATA, rtData);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取头像属性(是否允许上传/宽/高)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String resetAvatar(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      user.setAuatar("1");
      
      this.logic.resetAvatar(dbConn, user.getSeqId());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  private String toJson(Map<String,String> m) throws Exception {
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