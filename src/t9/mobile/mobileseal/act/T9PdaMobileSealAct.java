package t9.mobile.mobileseal.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.demo.logic.T9SealLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.mobile.mobileseal.data.T9MobileDevice;
import t9.mobile.mobileseal.logic.T9PdaMobileSealLogic;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.XXTEA;
/**
 * 该类主要对盈动印章 进行操作
 * @author shenhua
 *
 */
public class T9PdaMobileSealAct {
  public String resetPass(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String sealId = request.getParameter("ID");
      String sealPassword = T9Utility.null2Empty(request.getParameter("SEAL_PASS"));
      
      String data = "";
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement("select SEAL_DATA FROM MOBILE_SEAL WHERE SEQ_ID = " + sealId);
        rs = ps.executeQuery();
        while(rs.next()){
          data = rs.getString(1);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      BASE64Decoder decoder = new BASE64Decoder();
      String result = new String(decoder.decodeBuffer(data));
        
      Map m = T9FOM.json2Map(result);
      String sealPwd =(String) m.get("SealPwd");
      boolean checkpass = T9PassEncrypt.isValidPas(sealPassword, sealPwd);
      if (!checkpass) {
        T9MobileUtility.output(response, "印章密码错误！请重试");
        return null;
      }
      String resealPassword = T9Utility.null2Empty(request.getParameter("SEAL_NEW_PASS"));
      m.put("SealPwd", T9PassEncrypt.encryptPass(resealPassword));
      String r = T9FOM.toJson(m).toString();
      
      BASE64Encoder encoder = new BASE64Encoder();
      result = encoder.encode(r.getBytes());
      
      try {
        ps = conn.prepareStatement("update MOBILE_SEAL set SEAL_DATA = ?  WHERE SEQ_ID = " + sealId);
        ps.setString(1, result);
        ps.executeUpdate();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
    }  catch (Exception ex) {
       throw ex;
    }
    return "+OK";
  }
	public String submit(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	    	//System.out.println("shenqing................");
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	     Map data = new HashMap<String,String>();
	     
	     /**
	      * 判断是否插超过试用版最大授权数
	      */
	     String authData = request.getParameter("authData");
	     
	     URLDecoder decoder = new URLDecoder();
	     BASE64Decoder base64Decoder = new BASE64Decoder();
	     
	     String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
	     if(!T9MobileString.isEmpty(authData)){
//	    	 authData = decoder.decode(authData, "UTF-8");
//	    	 System.out.println(authData);
//	    	byte[] res = XXTEA.decrypt(base64Decoder.decodeBuffer(authData),key.getBytes());
//	    	/**
//	    	 * 自己写的方法不坑爹哦 呵呵
//	    	 */
//	    	String datas = T9MobileString.BytesToStr(res);//解密后的json 
	    	
	    	 
	    	 
	       //  byte[] bytes = Base64Util.decode(authData);
		     // String result = new String(bytes);
		      
		     // byte[] ret = XXTEA.decrypt(bytes, key.getBytes());
		      //String datas = new String(ret);
	   	// System.out.println("datas:"+datas);
	    	/**
	    	 * 将json 解析成 map
	    	 */
	    	Map infoMap  =  T9FOM.json2Map(authData);
	    	/**
	    	 * 解析 model 信息
	    	 */
	    	String model = (String)infoMap.get("model");
	    	
	    	 //System.out.println("model................:"+model);
	    	 
	    	String md5_check = "";
	    	md5_check = T9DigestUtility.md5Hex(authData.getBytes());
	    	
	    	int rsCount = T9MobileUtility.resultSetCount(dbConn, "select 1 FROM MOBILE_DEVICE WHERE MD5_CHECK='"+md5_check+"'");
	    	if(rsCount > 0 ){
	    		data.put("msg", "您已提交过申请，请勿重复提交");
	    		data.put("state", "-3");
	    		
	    		T9MobileUtility.output(response, T9MobileUtility.mapToJson(data));
	   	     return null;
	    	}
    	    
    	    T9PdaMobileSealLogic sl = new T9PdaMobileSealLogic();
    	    /**
    	     * 组装设备对象
    	     */
    	    T9MobileDevice md = new T9MobileDevice();
    	    md.setDeviceName(model);
    	    md.setDeviceInfo(authData);
    	    md.setDeviceType(0);
    	    md.setMd5Check(md5_check);
    	    /**
    	     * 插入设备信息
    	     */
    	    sl.insertDevice(dbConn, md, person.getSeqId());
    	    int maxId = T9MobileUtility.getMaxSeqIdCount(dbConn, "select max(seq_id) from MOBILE_DEVICE");
    	    
    	    data.put("SEQ_ID", String.valueOf(maxId));
    	    data.put("model", model);
    	    data.put("time", T9Utility.getCurDateTimeStr());
    	    data.put("state", "0");
    	    
	     }else{
	    	 //如果接受参数为空 
	    	   data.put("msg", "提交数据错误！");
	    	    
	     }
	    String  resultStr =  T9MobileUtility.mapToJson(data);
	     T9MobileUtility.output(response, resultStr);
	     return null;
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	    return null;
	}
	
	public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	     Map data = new HashMap<String,String>();
	     
	      T9PdaMobileSealLogic tsl = new T9PdaMobileSealLogic();
	      StringBuffer sb  = new StringBuffer();
	      List dList = new ArrayList();
	      List sList = new ArrayList();
	      sb.append("SELECT * from mobile_device where UID='"+person.getSeqId()+"' order by SUBMIT_TIME desc");
	      /**
	       * 获取印章 名称 和印章Data
	       */
	      dList = tsl.getDeviceListBySql(dbConn, sb.toString());
	      sList = tsl.getSealListByDevList(dbConn, dList);
	     // System.out.println("dList"+dList.size());
	    //  System.out.println("sList"+sList.size());
	      
	      data.put("dList",dList);
	      data.put("sList",sList);
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取移动印章数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    String sessionid = request.getSession().getId();
	    return "/mobile/mobileseal/index.jsp?sessionid=" +sessionid ;
	}
	
	public static List getMobileSealByAuthData(Connection dbConn , String authData , int personId ) throws Exception {
	     URLDecoder decoder = new URLDecoder();
	     BASE64Decoder base64Decoder = new BASE64Decoder();
	     
	     String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
	     if(!T9MobileString.isEmpty(authData)){
	    	 
	    	// byte[] bytes = Base64Util.decode(authData);
		      //String result = new String(bytes);
		      //System.out.println(result);
	      
		      byte[] ret =  XXTEA.encrypt(authData.getBytes(), key.getBytes());
		      /**
		       * 这里的datas 就是 DEVICE_INFO 的数值了  ！！！
		       */
		    //  String datas = new String(ret);
		     // System.out.println(datas);
	    	String md5_check = "";
	    	md5_check = T9DigestUtility.md5Hex(authData.getBytes());
	    	//System.out.println(md5_check);
	    	int rsCount = T9MobileUtility.resultSetCount(dbConn, "select 1 FROM MOBILE_DEVICE WHERE MD5_CHECK='"+md5_check+"'");
	    	
	    	if(rsCount == 1 ){
	    		/**
	    		 * 这里说明已经找到 且只有一条数据 说明只有一个 设备 这就对了
	    		 */
	    		String devId = T9MobileUtility.getDateByField(dbConn, "MOBILE_DEVICE", "SEQ_ID", "MD5_CHECK='"+md5_check+"'");
	    		if(!T9MobileString.isEmpty(devId)){
	    			//String[] queryData = {"SEQ_ID","SEAL_NAME"};
	    			//Map rsMap  = T9MobileUtility.getDateByField(dbConn, "MOBILE_SEAL", queryData,"");
	    			
	    			T9PdaMobileSealLogic msl = new T9PdaMobileSealLogic();
	    			StringBuffer sb = new StringBuffer();
	    			sb.append("select * from MOBILE_SEAL where ");
	    			sb.append(T9DBUtility.findInSet(devId, "DEVICE_LIST"));
	    			return msl.getSealListBySql(dbConn, sb.toString());
	    		}
	    	}
	     }
	   return new ArrayList();
	}
	/**edit_seal
	 * 通过ID获取 印章
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSealById(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	     String seqId = request.getParameter("ID");
	     seqId = T9MobileString.showObjNull(seqId, "0");
	     Map data = null;
	     
	      T9PdaMobileSealLogic tsl = new T9PdaMobileSealLogic();
	      StringBuffer sb  = new StringBuffer();
	      sb.append("  SELECT SEAL_NAME,SEAL_DATA from mobile_seal where SEQ_ID='");
	      sb.append(seqId);
	      sb.append("'");
	     // System.out.println("测试sql"+sb.toString());
	      /**
	       * 获取印章 名称 和印章Data
	       */
	      data = tsl.getSealById(dbConn, sb.toString());
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取移动印章数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    String sessionid = request.getSession().getId();
	    return "/mobile/mobileseal/edit_seal.jsp?sessionid=" + sessionid;
	}
	
	/**
	 * 获取设备 信息 通过seqId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getDeviceById(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	     String seqId = request.getParameter("SEQ_ID");
	     seqId = T9MobileString.showObjNull(seqId, "0");
	     Map data = null;
	     
	      T9PdaMobileSealLogic tsl = new T9PdaMobileSealLogic();
	      StringBuffer sb  = new StringBuffer();
	      sb.append("SELECT * from mobile_device where SEQ_ID='");
	      sb.append(seqId);
	      sb.append("'");
	      /**
	       * 获取印章 名称 和印章Data
	       */
	      data = tsl.getDeviceById(dbConn, sb.toString());
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取移动印章数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    String sessionid = request.getSession().getId();
	    return "/mobile/mobileseal/get_device.jsp?sessionid=" + sessionid;
	}
	
	/**
	 * 用于 get_seal.jsp页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSealDeviceById(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	     String seqId = request.getParameter("ID");
	     seqId = T9MobileString.showObjNull(seqId, "0");
	     Map data = null;
	     
	      T9PdaMobileSealLogic tsl = new T9PdaMobileSealLogic();
	      StringBuffer sb  = new StringBuffer();
	      sb.append("SELECT * from mobile_device where SEQ_ID='");
	      sb.append(seqId);
	      sb.append("'");
	      String SEAL_NAME = T9MobileUtility.getDateByField(dbConn, "mobile_seal", "SEAL_NAME", "");
	      
	      String DEVICE_LIST = T9MobileUtility.getDateByField(dbConn, "mobile_seal", "DEVICE_LIST", "");
        if(DEVICE_LIST.lastIndexOf(",") > -1){
          DEVICE_LIST = DEVICE_LIST.substring(0, DEVICE_LIST.length()-1);
        }
        String DEVICE_NAME_STR = "";
        
        if(!T9MobileString.isEmpty(DEVICE_LIST))
      {
        String sql = "SELECT DEVICE_NAME from mobile_device where SEQ_ID in ("+DEVICE_LIST+")";
        DEVICE_NAME_STR = T9MobileUtility.getFieldvalueMerge(dbConn, "mobile_device", "DEVICE_NAME", " SEQ_ID in ("+DEVICE_LIST+")");
      }
	      data = new HashMap<String, String>();
	      data.put("seqId", seqId);
	      data.put("SEAL_NAME", SEAL_NAME);
	      data.put("DEVICE_NAME_STR", DEVICE_NAME_STR);
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取移动印章数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      ex.printStackTrace();
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    String sessionid = request.getSession().getId();
	    return "/mobile/mobileseal/get_seal.jsp?sessionid=" + sessionid;
	}
	
	public static void main(String[] args) {
//	    String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
//	    String data = "{'shenhua':'12312'}";
//	  byte[]  b1 =   XXTEA.encrypt(data.toString().getBytes(), key.getBytes());
//	  
//	  System.out.println(b1);
//	  
//	  byte[]  b2 =   XXTEA.decrypt(b1, key.getBytes());
//	  
//	  String s1 = new String(b2);
//	  System.out.println(T9PdaMobileSealAct.BytesToStr(b2));
	    	 
	    	 
	}
}
