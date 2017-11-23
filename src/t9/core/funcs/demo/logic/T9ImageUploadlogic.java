package t9.core.funcs.demo.logic;

import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9ImageUploadlogic {

	/**
	 * 上传图片 
	 * @param fileForm
	 * @param uploadFields
	 * @return
	 * @throws Exception
	 */
	public String uploadImg( T9FileUploadForm fileForm , String uploadFields) throws Exception{
	    String[] uploads = uploadFields.split(",");
	    T9WorkFlowUtility util = new T9WorkFlowUtility();
	    StringBuffer sb = new StringBuffer();
	    sb.append("[");
	    int count = 0 ;
	    for (String img : uploads) {
	      if (!T9Utility.isNullorEmpty(img)) {
	        String fieldName = "_upload_" + img;
	        String fileName = fileForm.getFileName(fieldName);
	        if (T9Utility.isNullorEmpty(fileName)) {
	          continue;
	        }
	        String[] paths = util.getNewAttachPath(fileName, "workflow");
	        String id = paths[0];
	        fileForm.saveFile(fieldName , paths[1]);
	        sb.append("{").append("img:").append(img).append(",id:\"").append(id).append("\"").append(",name:\"").append(T9WorkFlowUtility.encodeSpecial(fileName)).append("\"").append("},");
	        count++;
	      }
	    }
	    if (count > 0) {
	      sb.deleteCharAt(sb.length() -1);
	    }
	    sb.append("]");
	    return sb.toString();
	  }
}
