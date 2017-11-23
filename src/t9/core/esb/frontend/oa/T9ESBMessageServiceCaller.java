package t9.core.esb.frontend.oa;

import java.rmi.RemoteException;

import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;

import t9.core.esb.client.service.OAWebservice;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;

public class T9ESBMessageServiceCaller {
	private static Logger log = Logger.getLogger("esb.t9.core.esb.frontend.oa.T9ESBMessageServiceCaller");
  public static String isLocal =  ClientPropertiesUtil.getProp("isLocal");
	
	public String recvMessage(String filePath, String guid, String fromId, String optGuid ,  String  message) throws Exception {
    if ("1".equals(isLocal)) {
      OAWebservice oa = new OAWebservice();
      return oa.recvMessage(filePath, guid, fromId , optGuid , message);
    } else {
  	  try {
  			String serviceUrl = ClientPropertiesUtil.getWebServiceUri().replace("?WSDL", "");
  			Service service = new Service(); 
  			Call call = (Call) service.createCall(); 
  			call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
  			call.setOperationName("recvMessage"); 
  			call.addParameter("filePath", XMLType.XSD_STRING, ParameterMode.IN); 
  			call.addParameter("guid", XMLType.XSD_STRING, ParameterMode.IN);
  			call.addParameter("fromId", XMLType.XSD_STRING, ParameterMode.IN);
  			call.addParameter("optGuid", XMLType.XSD_STRING, ParameterMode.IN);
  			call.addParameter("message", XMLType.XSD_STRING, ParameterMode.IN);
        
  			call.setReturnType(XMLType.XSD_STRING); 
  			
  			String ret = (String) call.invoke(new Object[] {filePath, guid, fromId ,optGuid , message});
//  			T9EsbUtil.println(ret);
  			return ret;
  		} catch (Exception e) {
  			//e.printStackTrace();
  			log.error("recvMessage - 调用web服务异常,异常信息:" + e.getMessage());
  			return "web服务异常:" + e.getMessage();
  		}
    }
	}
  public String doMessage(String fromId , String message) {
    if ("1".equals(isLocal)) {
      OAWebservice oa = new OAWebservice();
      return oa.doMessage(fromId ,  message);
    } else {
      try {
        String serviceUrl = ClientPropertiesUtil.getWebServiceUri().replace("?WSDL", "");
        Service service = new Service(); 
        Call call = (Call) service.createCall(); 
        call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
        call.setOperationName("recvMessage"); 
        call.addParameter("filePath", XMLType.XSD_STRING, ParameterMode.IN); 
        call.addParameter("guid", XMLType.XSD_STRING, ParameterMode.IN);
        call.addParameter("fromId", XMLType.XSD_STRING, ParameterMode.IN);
        call.addParameter("optGuid", XMLType.XSD_STRING, ParameterMode.IN);
        call.addParameter("message", XMLType.XSD_STRING, ParameterMode.IN);
        
        call.setReturnType(XMLType.XSD_STRING); 
        
        String ret = (String) call.invoke(new Object[] { fromId  , message});
//        T9EsbUtil.println(ret);
        return ret;
      } catch (Exception e) {
        //e.printStackTrace();
        log.error("recvMessage - 调用web服务异常,异常信息:" + e.getMessage());
        return "web服务异常:" + e.getMessage();
      }
    }
  }
  /**
   * 
   * @param guid
   * @param state 1 -上传成功 -2 文件发送失败! 2 对方文件下载成功
   * @param to
   * @return
   * @throws RemoteException
   */
	public String updateState(String guid, int state, String to) throws RemoteException {
	  if ("1".equals(isLocal)) {
	    OAWebservice oa = new OAWebservice();
	    return oa.updateState(guid, state, to);
	  } else {
	    try {
	      String serviceUrl = ClientPropertiesUtil.getWebServiceUri().replace("?WSDL", "");
	      Service service = new Service(); 
	      Call call = (Call) service.createCall(); 
	      call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
	      call.setOperationName("updateState"); 
	      call.addParameter("guid", XMLType.XSD_STRING, ParameterMode.IN); 
	      call.addParameter("state", XMLType.XSD_INT, ParameterMode.IN);
	      call.addParameter("to", XMLType.XSD_STRING, ParameterMode.IN);
	      call.setReturnType(XMLType.XSD_STRING); 
	      String ret = (String) call.invoke(new Object[] {guid, state, to});
//	      T9EsbUtil.println(ret);
	      return ret;
	    } catch (Exception e) {
	      //e.printStackTrace();
	      log.error("updateState - 调用web服务异常,异常信息:" + e.getMessage());
	      return "web服务异常:" + e.getMessage();
	    }
	  }
  }
}
