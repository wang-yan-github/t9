package raw.lh.formdesign;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.funcs.wizardtool.logic.T9HtmlParser;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.file.T9FileUtility;

public class T9FormDesignAct {
    private static Logger log = Logger.getLogger("lh.raw.lh.formdesign.T9FormDesignAct");

    public String saveForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String path = request.getParameter("path");
            String formHtml = request.getParameter("content");

            String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());

            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = sf.format(date);

            String dataPath = contextRealPath + path + "/" + fileName + ".html";
            T9FileUtility.storeString2File(dataPath, formHtml);

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        } finally {

        }
        return "/core/inc/rtjson.jsp";
    }

    public String previewForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String htmlContent = new String(request.getParameter("htmlContent").getBytes("ISO-8859-1"),
                    "UTF-8");
            if (htmlContent == null || "".equals(htmlContent)) {
                htmlContent = "";
            }
            htmlContent = htmlContent.replaceAll("\"", "'");

            Map<String, String> dataMap = new HashMap();
            Map<String, String[]> map = request.getParameterMap();
            StringBuffer sb = new StringBuffer("{htmlContent:\"" + htmlContent + "\",data:{");
            for (String key : map.keySet()) {
                if (key.equals("htmlContent")) {

                } else {
                    String str = new String(request.getParameter(key).getBytes("ISO-8859-1"), "UTF-8");
                    dataMap.put(key, str);
                    str = str.replaceAll("\"", "\\\\\"");

                    sb.append("\"" + key + "\":\"" + str + "\",");
                }
            }
            if (dataMap.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("}}");
            T9FileUtility.storeString2File("E:\\workspace\\lh\\e.html", sb.toString());
            T9HtmlParser tp = new T9HtmlParser();
            htmlContent = htmlContent.replaceAll("&", "&amp;");
            htmlContent = tp.parseToHtml(htmlContent, request.getContextPath(), dataMap);
            request.setAttribute("htmlContent", htmlContent);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        } finally {

        }
        return "/raw/lh/previewpage.jsp";
    }
}
