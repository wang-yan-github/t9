package t9.core.funcs.demo.act;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.sanselan.Sanselan;

import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.avatar.logic.T9AvatarLogic;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.workflow.logic.T9AttachmentLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.shtest.Base64Image;
import t9.subsys.shtest.SealLogic;
import t9.subsys.shtest.StringUtil;
/**
 * 处理个上传图片
 * @author shenhua
 *
 */
public class T9ImageUploadAct {
	 
	  public String setSeal(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    T9FileUploadForm fileForm = new T9FileUploadForm();
		   
		    fileForm.parseUploadRequest(request);
		    T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		    
		    String sealPwd = T9Utility.null2Empty(fileForm.getParameter("SEAL_PWD"));
		    String SealWidth =T9Utility.null2Empty( fileForm.getParameter("SealWidth"));
		    String SealHeight = T9Utility.null2Empty(fileForm.getParameter("SealHeight"));
		    String SEAL_NAME = T9Utility.null2Empty(fileForm.getParameter("SEAL_NAME"));
		    
		    Iterator<String> iKeys = fileForm.iterateFileFields();
		        
		    String sealData = "";
		    if (iKeys.hasNext()) {
		      String fieldName = iKeys.next();
		      String fileName = fileForm.getFileName("SEAL_FILE");
		      if (T9Utility.isNullorEmpty(fileName)) {
		        request.setAttribute("msg", "未添加成功，请选择印章图片");
		        return "/core/funcs/demo/makeseal.jsp";
		      }
		      sealData = GetImageStr(fileForm.getFileItem("SEAL_FILE").getInputStream());
		    }
		    
		   
		    StringBuffer sb = new StringBuffer();
		    
		    sb.append("{\"SealVersion\":").append("\"1.0\",")
		    .append("\"SealPwd\":\"").append(T9PassEncrypt.encryptPass(sealPwd)).append("\",")
		    .append("\"SealWidth\":\"").append(SealWidth).append("\",")
		    .append("\"SealHeight\":\"").append(SealHeight).append("\",")
		    .append("\"CreateTime\":\"").append(T9Utility.getCurDateTimeStr()).append("\",")
		    .append("\"SealData\":\"").append(sealData).append("\"}");
		    
		    BASE64Encoder encoder = new BASE64Encoder();
		    String result = encoder.encode(sb.toString().getBytes());
		       
		    StringBuffer sql = new StringBuffer();
		    sql.append("INSERT INTO MOBILE_SEAL (SEAL_NAME,CREATE_TIME,CREATE_USER,SEAL_DATA,DEVICE_LIST) VALUES(?,?," + person.getSeqId() + ", ? ,'')");
		    Connection dbConn = null;
		    PreparedStatement stm = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      
		      stm = dbConn.prepareStatement(sql.toString());
		      stm.setString(1, SEAL_NAME);
		      stm.setTimestamp(2, new Timestamp(new Date().getTime()));
		      stm.setString(3, result);
		      stm.executeUpdate();
		    } catch(Exception ex) {
		      ex.printStackTrace();
		      request.setAttribute("msg", "添加失败");
		    }finally{
		      T9DBUtility.close(stm, null, null);
		    }
		    request.setAttribute("msg", "添加成功");
		    return "/core/funcs/demo/makeseal.jsp";
		  }
	  
	  public static String GetImageStr(InputStream in ) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	        byte[] data = null;
	        
	        // 读取图片字节数组
	        try {
	            data = new byte[in.available()];
	            in.read(data);
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	     // 对字节数组Base64编码
	        BASE64Encoder encoder = new BASE64Encoder();
	        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	    }
	  public String converts(InputStream is)
	    {
	        StringBuilder sb = new StringBuilder();
	        String readline = "";
	        try
	        {
	            /**
	             * 若乱码，请改为new InputStreamReader(is, "GBK").
	             */
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            while (br.ready())
	            {
	                readline = br.readLine();
	                sb.append(readline);
	            }
	            br.close();
	        } catch (IOException ie)
	        {
	            System.out.println("converts failed.");
	        }
	        String str1 = sb.toString();
	      //  System.out.println("没有加密的图片：："+str1);
	       // BASE64Encoder encoder = new BASE64Encoder();
	     //  System.out.println("加密之后的："+encoder.encode(str1.getBytes())); // 返回Base64编码过的字节数组字符串
	  return sb.toString();
	    }
	public static void main(String[] args) {
		  BASE64Encoder encoder = new BASE64Encoder();
		  System.out.println(encoder.encode("haha".getBytes()));
	}
}
