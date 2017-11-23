package t9.cms.setting.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.cms.setting.data.T9SysPara;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.user.api.core.db.T9DbconnWrap;
public class T9JhSysParaLogic {

  private static Logger log = Logger.getLogger("t9.cms.setting.logic.T9JhSysParaLogic");
  /**
   *  添加 JH
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static void addHall(Connection dbConn,T9SysPara hall) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, hall);
  }
  
  /**
   *  修改
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static void updateHall(Connection dbConn,T9SysPara hall) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn,hall);
  }
  
  /**
   *  查询
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static T9SysPara hallObj(Connection dbConn,String paraName)throws Exception {
    String sql = "select SEQ_ID,PARA_NAME,PARA_VALUE from sys_para where para_name=?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    T9SysPara hall = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,paraName);
      rs = ps.executeQuery();
      if (rs.next()) {
        hall = new T9SysPara();
        hall.setParaName(rs.getString("PARA_NAME"));
        hall.setSeqId(rs.getInt("SEQ_ID"));
        hall.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return hall;
  }
  public String getAttachCtrl() throws Exception{
      T9DbconnWrap dbUtil = new T9DbconnWrap();
      Connection dbConn = dbUtil.getSysDbConn();
       String isCtrlAttachCms="";
       try{
    	   T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "IS_CTRLATTACH_CMS");
    	   isCtrlAttachCms=sysPara.getParaValue();
       }catch(Exception ex){
    	   ex.printStackTrace();
       }
       return isCtrlAttachCms;
  }
  
  public double[] getSizeCtrl() throws Exception{
      T9DbconnWrap dbUtil = new T9DbconnWrap();
      Connection dbConn = dbUtil.getSysDbConn();
      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "IS_CTRLATTACH_CMS");
      double[] maxSize=new double[4];
      try{
      if(sysPara.getParaValue().equals("1")){
    	  T9SysPara sysPara1 = T9JhSysParaLogic.hallObj(dbConn, "IMAGE_FILE_SIZE");
    	  T9SysPara sysPara2 = T9JhSysParaLogic.hallObj(dbConn, "AUDIO_FILE_SIZE");
    	  T9SysPara sysPara3 = T9JhSysParaLogic.hallObj(dbConn, "VIDEO_FILE_SIZE");
    	  T9SysPara sysPara4 = T9JhSysParaLogic.hallObj(dbConn, "OTHER_FILE_SIZE");
    	  double imageSize=0;
    	  double audioSize=0;
    	  double videoSize=0;
    	  double  otherSize=0;
    	  
    	  if(T9Utility.isNullorEmpty(sysPara1.getParaValue())){
    		  imageSize=0;
    	  }else{
    	  imageSize=Double.parseDouble(sysPara1.getParaValue());
    	  }
    	  if(T9Utility.isNullorEmpty(sysPara2.getParaValue())){
    		  audioSize=0;
    	  }else{
    	  audioSize=Double.parseDouble(sysPara2.getParaValue());
    	  }
    	  if(T9Utility.isNullorEmpty(sysPara3.getParaValue())){
    		  videoSize=0;
    	  }else{
    	  videoSize=Double.parseDouble(sysPara3.getParaValue());
    	  }
    	  if(T9Utility.isNullorEmpty(sysPara4.getParaValue())){
    		  otherSize=0;
    	  }else{
    	  otherSize=Double.parseDouble(sysPara4.getParaValue());
    	  }
    	  maxSize[0]=imageSize;
    	  maxSize[1]=audioSize;
    	  maxSize[2]=videoSize;
    	  maxSize[3]=otherSize;
      }
      }catch(Exception ex){
    	  ex.printStackTrace();
      }
      return maxSize;
  }
}
