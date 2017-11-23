package t9.core.esb.server.demo.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.service.T9WSCaller;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.frontend.T9EsbFrontend;
import t9.core.esb.frontend.services.T9EsbService;
import t9.core.esb.frontend.services.T9EsbServiceLocal;
import t9.core.esb.server.act.T9RangeUploadAct;
import t9.core.esb.server.user.data.TdUser;
import t9.core.funcs.doc.logic.T9ConfigLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class ESBDemoAct {
  public T9WSCaller caller = new T9WSCaller();
  String path = T9RangeUploadAct.UPLOAD_PATH + File.separator + "ESB-CACHE";
  String recePath = "d:\\ESB-CACHE";
  
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      
      String toId = fileForm.getParameter("toId");
      if(false == T9EsbFrontend.isOnline()){
        if(T9EsbFrontend.login() != 0){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "连接esb服务器失败！");
        }
      }
      Iterator<String> iKeys = fileForm.iterateFileFields();
      String attachmentNameStr = "";
      String attachmentIdStr = "";
      
      String savePath = "";
      if (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        savePath = path + File.separator + fileName;
        File parentFile = new File(path);
        if (!parentFile.exists()) {
          parentFile.mkdir();
        }
        fileForm.saveFile(savePath);
        T9EsbClientConfig config = T9EsbClientConfig.builder(request.getRealPath("/") + T9EsbConst.CONFIG_PATH) ;
        T9EsbService service = new T9EsbService();
        service.send(savePath, toId, config.getToken(), "test", "测试哦dfa\" 颉发达地 \" 颉 ");
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加入任务队列成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/esb/server/demo/success.jsp";
  }
  
  public String receiveFilePage(HttpServletRequest request, HttpServletResponse response) throws Exception { 
    int count = 0;
    StringBuffer pageData = new StringBuffer();
    File cache = new File(recePath);
    if(cache.isDirectory()){
      File[] dir = cache.listFiles();
      for (int i = 0; i < dir.length; i++) {
        if(dir[i].isDirectory()){
          File[] files = dir[i].listFiles();
          for (int j = 0; j < files.length; j++) {
            File file = files[j];
            count++;
            
            //文件大小
            String module = "B";
            long size = file.length();
            if(file.length() >= 1024){
              module = "KB";
              size = file.length()/1024;
              if(size >= 1024){
                module = "MB";
                size = size/1024;
              }
            }
            module = size + module;
            
            //文件创建时间
            Calendar cd = Calendar.getInstance();
            cd.setTimeInMillis(file.lastModified());
            String month = calculate(cd.get(Calendar.MONTH)+1);
            String day = calculate(cd.get(Calendar.DAY_OF_MONTH));
            String hour = calculate(cd.get(Calendar.HOUR_OF_DAY));
            String minute = calculate(cd.get(Calendar.MINUTE));
            String second = calculate(cd.get(Calendar.SECOND));
            String time = cd.get(Calendar.YEAR) +"-"+ month +"-"+ day +" "+ hour +":"+ minute +":"+ second;
            pageData.append("{fileName:\""+file.getName()+"\",fileLength:\""+module+"\",fileTime:\""+time+"\"},");
          }
        }
      }
    }
    
    StringBuffer sb = new StringBuffer();
    if(count == 0){
      sb.append("{totalRecord:"+count+",pageData:[]}");
    }
    else{
      sb.append("{totalRecord:"+count+",pageData:[");
      sb.append(pageData.substring(0, pageData.length()-1)+"]}");
    }
    
    
//    Connection dbConn = null; 
//    try { 
//      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
//      dbConn = requestDbConn.getSysDbConn(); 
//      TdUser tdUser = (TdUser)request.getSession().getAttribute("ESB_LOGIN_USER");
//      if(tdUser == null){
//        T9EsbService esbService = new T9EsbService();
//        esbService.login();
//        tdUser = (TdUser)request.getAttribute("ESB_LOGIN_USER");
//      }
//      String sql = "select" +
//          " t.FROM_ID," +
//          " t.FILE_PATH," +
//          " s.TO_ID," +
//          " s.STATUS" +
//          " from ESB_TRANSFER_STATUS s" +
//          ", ESB_TRANSFER t" +
//          " where TYPE = '0'" +
//          " and s.to_id="+ClientPropertiesUtil.getProp("usercode")+
//          " order by s.SEQ_ID desc";
//      
//      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
//      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      
      PrintWriter pw = response.getWriter(); 
      pw.println(sb.toString()); 
      pw.flush(); 
  
      return null; 
//    }catch (Exception e) {
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
//      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
//      throw e; 
//    } 
  }
  public String getState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try{
      String guid = request.getParameter("guid");
      long date1  = System.currentTimeMillis();
      String str = T9EsbServiceLocal.getDownloadScale(guid);
      long date2  = System.currentTimeMillis();
      System.out.println(date2 - date1);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, str);
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  private String calculate(int temp){
    return temp > 9 ? String.valueOf(temp) : "0"+temp;
  }
}
