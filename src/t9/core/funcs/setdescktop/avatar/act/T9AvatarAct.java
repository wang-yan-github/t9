package t9.core.funcs.setdescktop.avatar.act;

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
import t9.core.funcs.setdescktop.avatar.logic.T9AvatarLogic;
import t9.core.funcs.setdescktop.theme.logic.T9ThemeLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9AvatarAct {
  public final static String SOUND_PATH = "theme"+File.separator+"sound"+File.separator+"";
  public final static String AVATAR_PATH = "attachment"+File.separator+"avatar"+File.separator+"";
  public final static String PHOTO_PATH = "attachment"+File.separator+"photo"+File.separator+"";
  private T9AvatarLogic logic = new T9AvatarLogic();
  
  
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
    
    Map map = fileForm.getParamMap();
    
    //头像是否改变的标志

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
      
      T9AvatarLogic logic = new T9AvatarLogic();
      
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String sp = System.getProperty("file.separator");
        String filePath = "";
        
        if (T9Utility.isNullorEmpty(fieldName)) {
          continue;
        }
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName) || !fileName.contains(".")) {
          continue;
        }
        
        String[] names = fileName.split("\\.");
        
        fileName = user.getSeqId() + "." + names[names.length - 1];
        if ("avatar".equals(fieldName)) {
          filePath = request.getSession().getServletContext().getRealPath(sp) + AVATAR_PATH;
          logic.setAvatar(dbConn, user.getSeqId(), fileName);
          T9MsgPusher.pushAvatar(String.valueOf(user.getSeqId()), fileName);
          T9MsgPusher.updateOrg(dbConn);
        }
        else {
          filePath = request.getSession().getServletContext().getRealPath(sp) + PHOTO_PATH;
          logic.setPhoto(dbConn, user.getSeqId(), fileName);
        }
        
        File file = new File(filePath + fileName);
        if (file.exists()) {
          file.delete();
        }
        fileForm.saveFile(fieldName, filePath + fileName);
      }
      
      if (this.logic.hasNickName(dbConn, map, user.getSeqId())){
        request.setAttribute("msg", "昵称已经存在");
        return "/core/funcs/setdescktop/avatar/update.jsp";
      }
      else{
        this.logic.setAvatarNickName(dbConn, map, user.getSeqId());
      }
      request.setAttribute("msg", "修改成功");
      return "/core/funcs/setdescktop/avatar/update.jsp";
    } catch(Exception ex) {
      request.setAttribute("msg", "修改未成功");
    }finally{
      
    }
    return "/core/funcs/setdescktop/avatar/update.jsp";
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
   * 删除头像
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
      
      user.setAuatar("");
      
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
  
  /**
   * 删除照片
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String resetPhoto(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      user.setPhoto("");
      
      this.logic.resetPhoto(dbConn, user.getSeqId());
      
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