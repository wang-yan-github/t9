package t9.mobile.attach.logic;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9PdaAttachmentLogic {
	
	  public Map fileUploadLogic(HttpServletRequest request, T9FileUploadForm fileForm,
	      String pathPx, String module) throws Exception {
	    Map result = new HashMap();
	    String filePath = pathPx;
	    try {
	      Calendar cld = Calendar.getInstance();
	      int year = cld.get(Calendar.YEAR) % 100;
	      int month = cld.get(Calendar.MONTH) + 1;
	      String mon = month >= 10 ? month + "" : "0" + month;
	      String hard = year + mon;
	      Iterator<String> iKeys = fileForm.iterateFileFields();
	      while (iKeys.hasNext()) {
	        String fieldName = iKeys.next();
	        String fileName = fileForm.getFileName(fieldName).replaceAll("\\'", "");
	        String fileNameV = fileName;
	        if (T9Utility.isNullorEmpty(fileName)) {
	          continue;
	        }
	        String rand = T9DiaryUtil.getRondom();
	        fileName = rand + "_" + fileName;
	        
	        while (T9DiaryUtil.getExist(filePath + File.separator + hard, fileName)) {
	          rand = T9DiaryUtil.getRondom();
	          fileName = rand + "_" + fileName;
	        }
	        result.put("attachment_id", hard + "_" + rand);
	        result.put("attachment_Name", fileNameV);
	            
            StringBuffer url = request.getRequestURL();
            String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
	        result.put("down_file_url",localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                    + fileNameV+"&attachmentId="
                    + hard + "_" + rand  +"&module="+module);
	        fileForm.saveFile(fieldName, filePath + File.separator + module + File.separator + hard + File.separator + fileName);
	      }
	    } catch (Exception e) {
	      throw e;
	    }
	    return result;
	  }
}
