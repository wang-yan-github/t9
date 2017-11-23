package raw.lh.assistinput;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9AssistAct {
	private static Logger log = Logger.getLogger("lh.raw.lh.assistinput.T9AssistInputAct");
	public String assistInput(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    String str = request.getParameter("str");
	    if(str == null||"".equals(str)){
	      str = "";
	    }
	    String sLength = request.getParameter("length");
	    int length = 10;
	    if(sLength != null && !sLength.equals("")){
	      length = Integer.parseInt(sLength);
	    }
	    try {
	      String[] s = {"dddd","ddaaadd","ddd","ddddd","ddccaaaaa","ddaadaaa","dddaaaaaa","dddaaaaaa","dddaaaaaa","dddaaaaaa","dddaaaaaa","dddaaaaaa","dddaaaaaa"};
        List<String> list = new ArrayList();
	      StringBuffer sb = new StringBuffer("{lis:[");
        int count = 0 ;
        for(int i = 0 ; i < s.length ;i++){
        String string = s[i];
          if(string.startsWith(str)){
            list.add(string);
          }
        }
        for(int i = 0 ;i < list.size() ;i++){
          if(i < length){
            sb.append("{");
            sb.append("id:\"" + i + "\"");
            sb.append(",string:\"" + list.get(i) + "\"");
            sb.append("},");
          }
        }
   
       
        if(list.size() > 0){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("],count:"+ list.size() +"}");
	        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	        request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
	        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
	    }catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      ex.printStackTrace();
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
  
}
