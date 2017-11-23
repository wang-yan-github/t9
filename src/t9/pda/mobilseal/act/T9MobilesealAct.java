package t9.pda.mobilseal.act;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.global.T9BeanKeys;
import t9.core.util.db.T9ORM;
import t9.pda.login.logic.T9PdaSystemLoginLogic;
import t9.pda.mobilseal.logic.T9MobilesealLogic;
import t9.pda.mobilseal.util.Base64;
import t9.pda.mobilseal.util.Base64Util;
import t9.pda.mobilseal.util.XXTEA;
import t9.subsys.shtest.CriptDuke_MD5;
import t9.subsys.shtest.StringUtil;

public class T9MobilesealAct {

	/**
	 * 将手机的 数据获取到 后插入数据库 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void addDevice(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      
	      String deviceinfo = request.getParameter("deviceinfo");
	      
		     
	      /**
	       * 登陆用户信息
	       */
	      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	      int UID = person.getSeqId();
	      /**
	       * 时间
	       */
	      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	      Date date = new Date();
	      String SUBMIT_TIME = null;
	      SUBMIT_TIME = sf.format(date);
	      CriptDuke_MD5 cmd5 = new CriptDuke_MD5();
	      String MD5_CHECK = "";
	      MD5_CHECK = cmd5.encryptToMD5(deviceinfo);
	      
	      String DEVICE_NAME = "";
	      DEVICE_NAME = parseJson(deviceinfo,"'imsi'");
	      if(checkIsExit(dbConn,DEVICE_NAME)){
	    	  request.getRequestDispatcher("/pda/mobileseal/index.jsp?msg=该设备已经申请过，请勿重复申请！").forward(request, response);
	    	 return;
	      }
	      /**
	       * sql 语句 插入到 mobile_device表中
	       */
	      StringBuffer sb = new StringBuffer();
	      sb.append("INSERT INTO mobile_device(UID,SUBMIT_TIME,DEVICE_TYPE,DEVICE_INFO,MD5_CHECK,DEVICE_NAME)")
	      .append("VALUES(\""+UID)
	      .append("\",\"")
	      .append(SUBMIT_TIME)
	      .append("\",\"")
	      .append("0")
	      .append("\",\"")
	      .append(deviceinfo)
	      .append("\",\"")
	      .append(MD5_CHECK)
	      .append("\",\"")
	      .append(DEVICE_NAME)
	      .append("\")");
	      /**
	       * 执行插入语句
	       */
	    boolean rssult = T9MobilesealLogic.updateSql(dbConn, sb.toString());
	    } catch (Exception ex) {
	      request.setAttribute("errorMsg", "操作失败");
	      throw ex;
	    }
	    request.getRequestDispatcher("/pda/mobileseal/index.jsp").forward(request, response);
	    return;
	  }
	
	public void addcontent(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String sid = request.getParameter("sid");
	    String content = request.getParameter("content");
	    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	      int UID = person.getSeqId();
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      
	      
	      
	      CriptDuke_MD5 cmd5 = new CriptDuke_MD5();
	    String vmd5  = cmd5.encryptToMD5(content);
	      
		     
	      /**
	       * 登陆用户信息
	       */
	    String sql = "";
	    if(T9MobilesealLogic.checkUserisExit(dbConn, UID)){
	    	sql = "update sealdata  set content ='"+content+"',md5 = '"+vmd5+"',sealid = '"+sid+"' where uid = '"+UID+"'";
	    }else{
	    	 sql = "insert into sealdata(content,md5,sealid,uid)values('"+content+"','"+vmd5+"','"+sid+"','"+UID+"')";
	    }
	     
	    boolean rssult = T9MobilesealLogic.updateSql(dbConn, sql);
	      
	    } catch (Exception ex) {
	      request.setAttribute("errorMsg", "操作失败");
	      throw ex;
	    }
	    request.getRequestDispatcher("/pda/mobileseal/shouSeal.jsp?id="+sid+"&content="+content).forward(request, response);
	    return;
	  }
	public void updatecontent(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String sid = request.getParameter("sid");
	    String content = request.getParameter("content");
	    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	      int UID = person.getSeqId();
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      
	      
	    
	      /**
	       * 登陆用户信息
	       */
	     String sql = "update sealdata set content = '"+content+"' where  uid = '"+UID+"'";
	    boolean rssult = T9MobilesealLogic.updateSql(dbConn, sql);
	      
	    } catch (Exception ex) {
	      request.setAttribute("errorMsg", "操作失败");
	      throw ex;
	    }
	    request.getRequestDispatcher("/pda/mobileseal/shouSeal.jsp?id="+sid+"&content="+content).forward(request, response);
	    return;
	  }
	public void addDeviceFromJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX"; 
	    //  byte[] ret = XXTEA.encrypt(sb.toString().getBytes(), key.getBytes()); 
	      String deviceJson = request.getParameter("djson");
	      byte[] bytes = Base64Util.decode(deviceJson);
	      String result = new String(bytes);
	      
	      byte[] ret = XXTEA.decrypt(bytes, key.getBytes());
	      String str1 = new String(ret);
	      
	     // BASE64Decoder decoder = new BASE64Decoder();
	     // byte[] bytes = decoder.decodeBuffer(deviceJson);
	     
	     // StringBuffer sb2 = new StringBuffer();
	    //  String result = new String(bytes);
	    //  result = result.replaceAll("\n", "");
	    //  result = result.replaceAll("\r", "");
	    //  System.out.println("jsom2:"+result);
	   //  byte[] ret = XXTEA.decrypt(result.getBytes(), key.getBytes());
	   //  System.out.println("length:"+ret);
	    // String str1 = new String(ret);
	   //  System.out.println("jsom3:"+str1);
	    } catch (Exception ex) {
		      request.setAttribute("errorMsg", "操作失败");
		      throw ex;
		    }
		    request.getRequestDispatcher("/pda/mobilseal/index.jsp").forward(request, response);
		    return;
		  }
	public void getSealByUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	      int UID = person.getSeqId();
	      T9MobilesealLogic logic = new T9MobilesealLogic();
	      
	      String id = request.getParameter("id");
	      String content = request.getParameter("content");
	      CriptDuke_MD5 cmd5 = new CriptDuke_MD5();
		    String vmd5  = cmd5.encryptToMD5(content);
	      String sealdata = logic.getSealByUid(dbConn, id);
	     
	      /**
	       * 处理data 解析出 图片base64
	       * 转换成 流
	       */
	      /**
	       * 取出 图片 64位
	       * 
	       */
	      BASE64Decoder decoder = new BASE64Decoder();
	      byte[] bytes = decoder.decodeBuffer(sealdata);
	     
	      StringBuffer sb2 = new StringBuffer();
	      sealdata = new String(bytes);
	     // System.out.println(sealdata);
	      sealdata = sealdata.replaceAll("\n","");
	      sealdata = sealdata.replaceAll("\r","");
	     // System.out.println("json；"+sealdata);
	      sealdata = parseSealJson(sealdata);
	      
	      byte[] bytes1 = decoder.decodeBuffer(sealdata);
	      for (int i = 0; i < bytes1.length; ++i) {
              if (bytes1[i] < 0) {// 调整异常数据
                  bytes1[i] += 256;
              }
          }
          // 生成png图片
          InputStream is =   new ByteArrayInputStream(bytes1);
	      BufferedImage bi = ImageIO.read(is);
	      
	      
	      if(!logic.checkSign(dbConn, UID, vmd5)){
	    	  //测试代码开始
		      
		      Graphics2D g = bi.createGraphics();
		      g.setStroke(new BasicStroke(8.0f));
		      g.setColor(Color.RED); 
		      g.drawString("签章信息被更改!", (bi.getWidth()/2) -(bi.getWidth()/4), (bi.getHeight()/2)+15);
		  	  g.drawLine(0, 0, bi.getWidth(), bi.getHeight());
		  	  
		  	
		    g.drawLine(0, 0, bi.getWidth(), bi.getHeight()); 
		  	g.drawLine( bi.getWidth(),0, 0,bi.getHeight());
		      //测试代码结束
		     }
	     
	      //BufferedImage bi = ImageIO.read(new File("d:\\1.png"));
	      ServletOutputStream sos = response.getOutputStream();
	      ImageIO.write(bi, "png", sos);
	  	  sos.close();
	   
	    } catch (Exception ex) {
		      request.setAttribute("errorMsg", "操作失败");
		      throw ex;
		    }
		    //request.getRequestDispatcher("/pda/main.jsp").forward(request, response);
	    
		  }
	public void getSealBySid(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	      int UID = person.getSeqId();
	      T9MobilesealLogic logic = new T9MobilesealLogic();
	      
	      String id = request.getParameter("id");
	      String sealdata = logic.getSealByUid(dbConn, id);
	     
	      /**
	       * 处理data 解析出 图片base64
	       * 转换成 流
	       */
	      /**
	       * 取出 图片 64位
	       * 
	       */
	      BASE64Decoder decoder = new BASE64Decoder();
	      byte[] bytes = decoder.decodeBuffer(sealdata);
	     
	      StringBuffer sb2 = new StringBuffer();
	      sealdata = new String(bytes);
	      //System.out.println(sealdata);
	      sealdata = sealdata.replaceAll("\n","");
	      sealdata = sealdata.replaceAll("\r","");
	      //System.out.println("json；"+sealdata);
	      sealdata = parseSealJson(sealdata);
	      
	      byte[] bytes1 = decoder.decodeBuffer(sealdata);
	      for (int i = 0; i < bytes1.length; ++i) {
              if (bytes1[i] < 0) {// 调整异常数据
                  bytes1[i] += 256;
              }
          }
          // 生成png图片
          InputStream is =   new ByteArrayInputStream(bytes1);
	      BufferedImage bi = ImageIO.read(is);
	      
	      
	     
	      //BufferedImage bi = ImageIO.read(new File("d:\\1.png"));
	      ServletOutputStream sos = response.getOutputStream();
	      ImageIO.write(bi, "png", sos);
	  	  sos.close();
	   
	    } catch (Exception ex) {
		      request.setAttribute("errorMsg", "操作失败");
		      throw ex;
		    }
		    //request.getRequestDispatcher("/pda/main.jsp").forward(request, response);
		  }
	/**
	 * 解析 手机客户端 传来的Json数据 解析出 各个字段的数值
	 *
	 * @param str  这是经过安卓的BASE64 编码过的 用java的base64不能解析
	 * @param str2 如 'model'
	 * @return
	 */
	public String parseJson(String str,String str2){
		 String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
	      byte[] bytes = Base64Util.decode(str);
	      String result = new String(bytes);
	      
	      byte[] ret = XXTEA.decrypt(bytes, key.getBytes());
	      String result1 = new String(ret);
	      int pindex = result1.indexOf(str2);
	      result1 = result1.substring(pindex);
	      result1 = result1.replaceAll(str2+":'", "");
	    
	      int endindex = result1.indexOf("'");
	      result1 = result1.substring(0, endindex);
	      
	     // System.out.println("result1"+result1);
		return result1;
	}
	/**
	 * 解析签章 从数据库中读取出来的 数据 
	 * 把签章的数据 解析出来 从Json中
	 * @param sealdata
	 * @return
	 */
	public String parseSealJson(String sealdata){
		if(sealdata == null){
			return "";
		}
		int startindex = sealdata.indexOf("'SealData'");
		sealdata = sealdata.substring(startindex);
		//System.out.println("这是整整的 张啊1："+sealdata);
		sealdata = sealdata.substring("'SealData':'".length());
		//System.out.println("这是整整的 张啊2："+sealdata);
		sealdata = sealdata.substring(0, sealdata.length() - 2);
		//System.out.println("这是整整的 张啊3："+sealdata);
		return sealdata;
	}
	/**
	 * 检查是否有盖章的权限
	 * @return
	 */
	public boolean checkPriv (){
		
		return true;
	}
	/**
	 * 检查签章 是否有效 MD5 验证
	 * 譬如 绑定内容被篡改等
	 * @return
	 */
	public boolean checkSignIsAvilid(){
		
		return true;
	}
	public boolean checkIsExit(Connection conn , String dName){
		boolean isExit = false;
		try {
			isExit =	T9MobilesealLogic.devIsExit(conn,dName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isExit;
	}
	public static void main(String[] args) {
		
		 String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
		 //byte[] ret = XXTEA.encrypt("yjkeYiq6wGlq//8Au7WcjERZw0Iefmvn1kqJBms3IJiedTlUhVxNphBMdmtqFt0VnUGV37jy3xvd gylLya3BB/NTK6W2dFTyJWfgIx4XWt3CzPUPe8/M8/bFN+p8e9k4fgl9rszY2EfZunBAOkYeCcH5 25BUUN+9Qk4SoQ==".getBytes(), key.getBytes());
		 String ss1 = "yjkeYiq6wGlq//8Au7WcjERZw0Iefmvn1kqJBms3IJiedTlUhVxNphBMdmtqFt0VnUGV37jy3xvd gylLya3BB/NTK6W2dFTyJWfgIx4XWt3CzPUPe8/M8/bFN+p8e9k4fgl9rszY2EfZunBAOkYeCcH5 25BUUN+9Qk4SoQ==";
		 byte[] bytes = Base64Util.decode(ss1);
		 byte[] aaa  = XXTEA.decrypt(bytes, key.getBytes());
		 String ss = new String(aaa);
		 System.out.println(ss);
	}
	
}
