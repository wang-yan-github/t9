package raw.ljf;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.file.T9FileUtility;

public class T9CodeTrnsAct {
	
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9CodeTrnsAct");
	  
  public String trnsCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
	
	response.setCharacterEncoding("UTF-8");
	String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
	String page = request.getParameter("page");
	String filePath = ctxPath + page.replaceAll("/", "\\");
	List<String> contentList = new ArrayList<String>();
	T9FileUtility.loadLine2Array(filePath, contentList);
	PrintWriter writer = response.getWriter();
	List rtList = new ArrayList();
	for (String lineStr : contentList) {
		//lineStr = lineStr.trim();
		if (lineStr.length() < 1) {
			continue;
		}
		lineStr = lineStr.replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\"", "&quot").replaceAll(" ", "&nbsp;&nbsp;");
		writer.write(lineStr);
		writer.write("<br>\r\n");
	}
	writer.flush();
	writer.close();
	return null;
  }
}
