package t9.mobile.util;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;

public class T9MobileUtility {
    public static String getResultJson(Integer flag, String msg, String data) {
        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("rtState", String.valueOf(flag));
        if (msg == null) {
            resMap.put("rtMsrg", "");
        } else {
            resMap.put("rtMsrg", msg);
        }
        if (data == null) {
            resMap.put("rtData", "");
        } else {
            resMap.put("rtData", data);
        }
        return T9MobileUtility.obj2Json(resMap);
    }

    public static Map getHeadMap(String c1, String e1, String t1, String t2, String c3, String e3, String t3) {
        Map m6l = new HashMap();
        m6l.put("class", c1);
        m6l.put("event", e1);// 导航栏左上角按钮事件
        m6l.put("title", t1);// 导航栏左上角按钮显示名称
        Map m6c = new HashMap();
        m6c.put("title", t2);// 导航栏中间栏标题
        Map m6r = new HashMap();
        m6r.put("class", c3);
        m6r.put("event", e3);// 导航栏右上角按钮触发事件
        m6r.put("title", t3);// 导航栏右上角按钮显示名称
        Map l6 = new HashMap();
        l6.put("l", m6l);
        l6.put("c", m6c);
        l6.put("r", m6r);

        return l6;
    }

    public static Map getHeadMap(String c1, String e1, String t1, String t2) {
        Map m6l = new HashMap();
        m6l.put("class", c1);
        m6l.put("event", e1);
        m6l.put("title", t1);
        Map m6c = new HashMap();
        m6c.put("title", t2);
        Map l6 = new HashMap();
        l6.put("l", m6l);
        l6.put("c", m6c);

        return l6;
    }

    public static String buildHead(Map map) {
        StringBuffer sb = new StringBuffer("<div id=\"header\">");
        Set<String> sets = map.keySet();
        for (String ss : sets) {
            String style = "";
            Map m = (Map) map.get(ss);
            String style2 = T9Utility.null2Empty((String) m.get("style"));
            if (m.containsKey("display")) {
                String display = (String) m.get("display");
                if ("none".equals(display)) {
                    style = " style='" + style2 + "display:none;'";
                } else {
                    style = " style='" + style2 + "'";
                }
            } else {
                if (!"1".equals(ss)) {
                    style = " style='" + style2 + "display:none;'";
                }
            }
            sb.append("<div id=\"header_").append(ss).append("\"").append(style).append(">");

            Map ll = (Map) m.get("l");
            if (ll != null) {
                String cl = (String) ll.get("class");
                String el = (String) ll.get("event");
                String title = (String) ll.get("title");

                String lclass = T9Utility.isNullorEmpty(cl) ? "" : " " + cl;
                String levent = T9Utility.isNullorEmpty(el) ? "" : " onclick='" + el + "'";

                sb.append("<span class=\"lcbtn").append(lclass).append("\"").append(levent).append(">");

                sb.append("<span>").append(title).append("</span></span >");
            }
            Map c = (Map) m.get("c");
            if (c != null) {
                String title = (String) c.get("title");
                sb.append("<span class=\"t\">").append(title).append("</span>");
            }
            Map r = (Map) m.get("r");

            if (r != null) {
                style2 = (String) r.get("style");
                if (r.containsKey("display")) {
                    String display = (String) r.get("display");
                    if ("none".equals(display)) {
                        style = " style='" + style2 + "display:none;'";
                    } else {
                        style = " style='" + style2 + "'";
                    }
                } else {
                    style = "";
                }

                String cl = (String) r.get("class");
                String el = (String) r.get("event");
                String title = (String) r.get("title");

                String lclass = T9Utility.isNullorEmpty(cl) ? "" : " " + cl;
                String levent = T9Utility.isNullorEmpty(el) ? "" : " onclick='" + el + "'";

                sb.append("<span class=\"combtn rbtn").append(lclass).append("\"").append(levent)
                        .append(style).append(">");
                sb.append("<span>").append(title).append("</span>").append("</span>");
            }
            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    public static String buildMessage() {
        String ss = "<div id=\"message\"><div id=\"blank\" class=\"transparent_class\"></div><div id=\"text\"></div></div>";
        return ss;
    }

    public static String buildProLoading() {
        String ss = "<div id=\"overlay\"></div><div class=\"ui-loader loading\"><span class=\"ui-icon ui-icon-loading\"></span><h1>加载中...</h1> </div>";
        return ss;
    }

    public static String buildPullUp() {
        String ss = "<div class=\"pullUp\"><span class=\"pullWrapper\"><span class=\"pullUpIcon\"></span><span class=\"pullUpLabel\">上拉加载更多...</span></span></div>";
        return ss;
    }

    public static String buildPullDown() {
        String ss = "<div class=\"pullDown\"><span class=\"pullWrapper\"><span class=\"pullDownIcon\"></span><span class=\"pullDownLabel\">下拉刷新...</span></span></div>";
        return ss;
    }

    public static void output(HttpServletResponse response, String sb) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        try {
            PrintWriter out = response.getWriter();
            out.print(sb);
            out.flush();
            out.close();
        } catch (Exception ex2) {
            throw ex2;
        }
    }

    public static String mapToJson(Map<String, String> map) {
        StringBuffer sb = new StringBuffer("{");
        Set<String> keys = map.keySet();
        int count = 0;
        for (String key : keys) {
            if (!T9Utility.isNullorEmpty(key)) {
                sb.append("\"").append(key).append("\"").append(":").append("\"")
                        .append(String.valueOf(map.get(key))).append("\",");
                count++;
            }
        }
        if (count > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * list转json
     * 
     * @param map
     * @return
     */
    public static String list2Json(Object obj) {
        if (obj != null) {
            try {
                JSONArray jsonObject = JSONArray.fromObject(obj);
                String res = jsonObject.toString();
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * obj转json
     * 
     * @param map
     * @return
     */
    public static String obj2Json(Object obj) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(obj);
            String res = jsonObject.toString();
            return "[" + res + "]";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String showAvatar(String avatar, String sex) {
        if (T9Utility.isNullorEmpty(avatar) || "default.gif".equals(avatar) || "1".equals(avatar)
                || "0".equals(avatar)) {
            return sex;
        }

        return "/t9/attachment/avatar/" + avatar;
    }

    /**
     * 判段id是不是在str里面
     * 
     * @param str
     * @param id
     * @return
     */
    public static boolean find_id(String str, String id) {
        if (str == null || id == null || "".equals(str) || "".equals(id)) {
            return false;
        }
        String[] aStr = str.split(",");
        for (String tmp : aStr) {
            if (tmp.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static String avatarPath(String avatar, String sex, String size) {
        // String rootPath = T9SysProps.getRootPath();

        /*
         * if($SIZE == "big"){ $AVATAR =
         * substr($AVATAR,0,strrpos($AVATAR,"."))."_big"
         * .substr($AVATAR,strrpos($AVATAR,".")); }
         * 
         * $AVATAR = trim($AVATAR); $URL_ARRAY = attach_url_old("avatar",
         * $AVATAR); if(strpos($AVATAR, ".")) { $AVATAR_PATH =
         * $URL_ARRAY['view']; $AVATAR_FILE = attach_real_path("avatar",
         * $AVATAR); } else if($AVATAR != "") { $AVATAR_PATH =
         * "/images/avatar/$AVATAR.gif"; $AVATAR_FILE = $ROOT_PATH.$AVATAR_PATH;
         * } else { $AVATAR_PATH = "/images/avatar/avatar_".$SEX.".jpg";
         * $AVATAR_FILE = $ROOT_PATH.$AVATAR_PATH; }
         * 
         * if(file_exists($AVATAR_FILE)) return $AVATAR_PATH; else
         */
        if (T9Utility.isNullorEmpty(avatar)) {
            return "/t9/attachment/avatar/default.gif";
        }
        return "/t9/attachment/avatar/" + avatar;
    }

    /**
     * 获取SYS_CODE 名称方法
     * 
     * @param dbConn
     * @param CODE_NO
     * @param PARENT_NO
     * @return
     * @throws Exception
     */
    public static String get_code_name(Connection dbConn, String CODE_NO, String PARENT_NO) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (!T9MobileString.isEmpty(CODE_NO) || !T9MobileString.isEmpty(PARENT_NO)) {
            sb.append("SELECT CODE_NAME from SYS_CODE where PARENT_NO='" + PARENT_NO + "' and ");
            sb.append(T9DBUtility.findInSet(CODE_NO, "CODE_NO"));
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        String code = "";
        boolean flag = false;
        try {
            ps = dbConn.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                code = code + "," + rs.getString("CODE_NAME");
                flag = true;
            }
            if (flag) {
                code = code.substring(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        return code;
    }

    public static String getLongDept(Connection dbConn, int deptId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int deptID2 = 0;
        String deptName = "";
        try {
            ps = dbConn.prepareStatement("select DEPT_NAME, DEPT_PARENT FROM DEPARTMENT WHERE SEQ_ID = "
                    + deptId);
            rs = ps.executeQuery();
            if (rs.next()) {
                deptID2 = rs.getInt("DEPT_PARENT");
                deptName = rs.getString("DEPT_NAME");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        if (deptID2 == 0) {
            return deptName;
        } else {
            String parentName = getLongDept(dbConn, deptID2);
            return parentName + "/" + deptName;
        }
    }

    public static String getAttachLinkPda(String aId, String aName, String sessionId, String module,
            boolean isShowSize, boolean downPriv, String contextPath) throws Exception {
        if (T9Utility.isNullorEmpty(aId)) {
            return "无";
        }
        if (T9Utility.isNullorEmpty(module)) {
            return "无";
        }
        String[] ids = aId.split(",");
        String[] names = aName.split("\\*");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            String name = names[i];
            if (T9Utility.isNullorEmpty(id) || T9Utility.isNullorEmpty(name))
                continue;
            String sizeStr = "";
            if (isShowSize) {
                long size = T9WorkFlowUtility.getAttachSize(name, id, module);
                if (Math.floor(size / 1024 / 1024) > 0) {
                    sizeStr = Math.round(size / 1024 / 1024) + "MB";
                } else if (Math.floor(size / 1024) > 0) {
                    sizeStr = Math.round(size / 1024) + "KB";
                } else {
                    sizeStr = size + "字节";
                }
            }
            if (downPriv) {
                String imgType = imageMimetype(name);
                String isImage = isImage(name) ? "1" : "0";
                String urlArray = "message:" + isImage
                        + ":/t9/core/funcs/office/ntko/act/T9NtkoAct/downFile.act?module=" + module
                        + "&attachmentId=" + id + "&sessionid=" + sessionId + "&attachmentName="
                        + URLEncoder.encode(name);
                sb.append("<a href=\"" + urlArray + "\" is_image = \"" + isImage
                        + "\" class=\"pda_attach\" target=\"_blank\" style=\"background-image:url('"
                        + contextPath + "/mobile/images/" + imgType + "')\"><span>" + name + "<span><em>"
                        + sizeStr + "</em><div class=\"ui-icon-rarrow\"></div></a>\n");
            } else {
                sb.append(name);
            }
        }
        return sb.toString();
    }

    public static boolean isImage(String fileName) {
        String imgs = "gif,jpg,jpeg,png,bmp,iff,jp2,jpx,jb2,jpc,xbm,wbmp,";
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return T9WorkFlowUtility.findId(imgs, ext);
    }

    public static String imageMimetype(String fileName) {
        Map<String, String> m = new HashMap();
        m.put("7z", "7z.gif");
        m.put("aac", "avi.gif");
        m.put("ace", "zip.gif");
        m.put("ai", "ai.gif");
        m.put("ain", "ain.gif");
        m.put("amr", "mov.gif");
        m.put("arj", "zip.gif");
        m.put("asf", "avi.gif");
        m.put("asp", "asp.gif");
        m.put("aspx", "asp.gif");
        m.put("av", "avi.gif");
        m.put("avi", "avi.gif");
        m.put("bat", "com.gif");
        m.put("bin", "bin.gif");
        m.put("bmp", "bmp.gif");
        m.put("cab", "cab.gif");
        m.put("cad", "cad.gif");
        m.put("cat", "cat.gif");
        m.put("chm", "chm.gif");
        m.put("com", "com.gif");
        m.put("css", "css.gif");
        m.put("csv", "csv.gif");
        m.put("cur", "cdr.gif");
        m.put("dat", "dat.gif");
        m.put("db", "db.gif");
        m.put("dll", "dll.gif");
        m.put("dmv", "avi.gif");
        m.put("doc", "doc.gif");
        m.put("docx", "docx.gif");
        m.put("dot", "dot.gif");
        m.put("dpt", "dpt.gif");
        m.put("dps", "dps.gif");
        m.put("dwg", "dwg.gif");
        m.put("dxf", "dxf.gif");
        m.put("emf", "emf.gif");
        m.put("eml", "eml.gif");
        m.put("eps", "eps.gif");
        m.put("esp", "esp.gif");
        m.put("et", "et.gif");
        m.put("ett", "ett.gif");
        m.put("exe", "exe.gif");
        m.put("fla", "fla.gif");
        m.put("gif", "gif.gif");
        m.put("gz", "zip.gif");
        m.put("hlp", "help.gif");
        m.put("html", "html.gif");
        m.put("htm", "html.gif");
        m.put("icl", "icl.gif");
        m.put("ico", "ico.gif");
        m.put("img", "iso.gif");
        m.put("inf", "inf.gif");
        m.put("ini", "ini.gif");
        m.put("iso", "iso.gif");
        m.put("jpg", "jpg.gif");
        m.put("jpeg", "jpg.gif");
        m.put("js", "js.gif");
        m.put("key", "reg.gif");
        m.put("m3u", "m3u.gif");
        m.put("max", "max.gif");
        m.put("mdb", "mdb.gif");
        m.put("mde", "mde.gif");
        m.put("mht", "mht.gif");
        m.put("mid", "mid.gif");
        m.put("mov", "mov.gif");
        m.put("mp3", "mp3.gif");
        m.put("mp4", "avi.gif");
        m.put("mpg", "avi.gif");
        m.put("mpeg", "avi.gif");
        m.put("msi", "msi.gif");
        m.put("nrg", "iso.gif");
        m.put("ocx", "dll.gif");
        m.put("ogg", "avi.gif");
        m.put("ogm", "avi.gif");
        m.put("pdf", "pdf.gif");
        m.put("php", "php.gif");
        m.put("phtml", "php.gif");
        m.put("pl", "pl.gif");
        m.put("png", "png.gif");
        m.put("pot", "pot.gif");
        m.put("ppt", "ppt.gif");
        m.put("pptx", "pptx.gif");
        m.put("psd", "psd.gif");
        m.put("pub", "pub.gif");
        m.put("qt", "mov.gif");
        m.put("rar", "rar.gif");
        m.put("ra", "ram.gif");
        m.put("ram", "ram.gif");
        m.put("reg", "reg.gif");
        m.put("rm", "ram.gif");
        m.put("rmvb", "ram.gif");
        m.put("rtf", "rtf.gif");
        m.put("sel", "esp.gif");
        m.put("sql", "txt.gif");
        m.put("flv", "flash.gif");
        m.put("fla", "flash.gif");
        m.put("swf", "flash.gif");
        m.put("tar", "zip.gif");
        m.put("tgz", "zip.gif");
        m.put("tif", "tif.gif");
        m.put("tiff", "tif.gif");
        m.put("torrent", "torrent.gif");
        m.put("txt", "txt.gif");
        m.put("url", "html.gif");
        m.put("vbs", "vbs.gif");
        m.put("vsd", "vsd.gif");
        m.put("vss", "vss.gif");
        m.put("vst", "vst.gif");
        m.put("wav", "wav.gif");
        m.put("wm", "avi.gif");
        m.put("wma", "avi.gif");
        m.put("wmd", "avi.gif");
        m.put("wmf", "wmf.gif");
        m.put("wmv", "avi.gif");
        m.put("wps", "wps.gif");
        m.put("wpt", "wpt.gif");
        m.put("xls", "xls.gif");
        m.put("xlsx", "xlsx.gif");
        m.put("xlt", "xlt.gif");
        m.put("xml", "xml.gif");
        m.put("z", "zip.gif");
        m.put("zip", "zip.gif");
        m.put("aip", "aip.gif");
        m.put("tdjm", "tdjm.gif");

        String extName = fileName.substring(fileName.indexOf(".") + 1);
        if (!T9Utility.isNullorEmpty(extName) && m.containsKey(extName)) {
            return (String) m.get(extName);
        } else {
            return "defaut.gif";
        }
    }

    /**
     * 获取记录条数 主要目的 是为了 和php 中的那个 resultCount 相同 如果没有查到 返回 0
     * 
     * @param dbConn
     * @param Sql
     * @return
     * @throws Exception
     */
    public static int resultCount(Connection dbConn, String Sql) throws Exception {
        /**
         * 返回的记录数 默认为 0
         */
        int count = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbConn.prepareStatement(Sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        return count;
    }

    /**
     * 返回 结果集的记录数量
     * 
     * @param dbConn
     * @param Sql
     * @return
     * @throws Exception
     */
    public static int resultSetCount(Connection dbConn, String Sql) throws Exception {
        /**
         * 返回的记录数 默认为 0
         */
        int count = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbConn.prepareStatement(Sql);
            rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                i++;
            }
            count = i;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        return count;
    }

    /**
     * 获取最大的ID
     * 
     * @param dbConn
     * @param Sql
     * @return
     * @throws Exception
     */
    public static int getMaxSeqIdCount(Connection dbConn, String Sql) throws Exception {
        /**
         * 返回的记录数 默认为 0
         */
        int count = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbConn.prepareStatement(Sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        return count;
    }

    /**
     * 主要目的 执行sql 的update语句 方便
     * 
     * @param dbConn
     * @param Sql
     * @return
     * @throws Exception
     */
    public static int updateSql(Connection dbConn, String Sql) throws Exception {
        int result = 0;
        PreparedStatement ps = null;
        try {
            ps = dbConn.prepareStatement(Sql);
            result = ps.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(ps, null, null);
        }
        return result;
    }

    /**
     * 根据seqid串返回一个名字串
     * 
     * @param ids
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String getNameBySeqIdStr(String names, Connection conn) throws Exception, Exception {
        if (T9Utility.isNullorEmpty(names))
            return "";
        String ids = "";
        names = T9WorkFlowUtility.getInStr(names);
        String query = "select SEQ_ID from PERSON where USER_ID in (" + names + ")";
        Statement stm = null;
        ResultSet rs = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            while (rs.next()) {
                ids += rs.getString("SEQ_ID") + ",";
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, rs, null);
        }
        if (ids.endsWith(",")) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    /**
     * 直接获取 结果集 这样方便操作一些
     * 
     * @param dbConn
     * @param Sql
     * @return
     * @throws Exception
     */
    public static ResultSet getSqlResultSet(Connection dbConn, String Sql) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbConn.prepareStatement(Sql);
            rs = ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static Map uploadAttachment(T9FileUploadForm fileForm, String module, String idOld, String nameOld)
            throws Exception {
        String filePath = T9SysProps.getAttachPath() + File.separator + module;
        File f1 = new File(filePath);
        if (!f1.exists()) {
            f1.mkdir();
        }
        Calendar cld = Calendar.getInstance();
        int year = cld.get(Calendar.YEAR) % 100;
        int month = cld.get(Calendar.MONTH) + 1;
        String mon = month >= 10 ? month + "" : "0" + month;
        String hard = year + mon;
        Iterator<String> iKeys = fileForm.iterateFileFields();
        if (!T9Utility.isNullorEmpty(nameOld)) {
            nameOld = nameOld.replace(",", "*");
        } else {
            nameOld = "";
        }
        if (T9Utility.isNullorEmpty(idOld)) {
            idOld = "";
        }
        if (!T9Utility.isNullorEmpty(nameOld) && !nameOld.endsWith("*")) {
            nameOld += "*";
        }
        if (!T9Utility.isNullorEmpty(idOld) && !idOld.endsWith(",")) {
            idOld += ",";
        }

        String attachmentNameStr = "";
        String attachmentIdStr = "";
        while (iKeys.hasNext()) {
            String fieldName = iKeys.next();
            if (fieldName.startsWith("ATTACHMENT_")) {
                continue;
            }
            String fileName = URLDecoder.decode(fileForm.getFileName(fieldName), "UTF-8");
            if (T9Utility.isNullorEmpty(fileName)) {
                continue;
            }
            String attachmentId = T9Guid.getRawGuid();
            String fileName2 = attachmentId + "_" + fileName;
            File f2 = new File(filePath + File.separator + hard);
            if (!f2.exists()) {
                f2.mkdir();
            }
            String tmp = filePath + File.separator + hard + File.separator + fileName2;
            fileForm.saveFile(fieldName, tmp);

            attachmentNameStr += fileName + "*";
            attachmentIdStr += hard + "_" + attachmentId + ",";
        }
        attachmentNameStr = nameOld + attachmentNameStr;
        attachmentIdStr = idOld + attachmentIdStr;

        Map map = new HashMap();
        map.put("id", attachmentIdStr);
        map.put("name", attachmentNameStr);
        return map;
    }

    /**
     * 获取某张表中某一字段的数值 需要传表明 字段名 和条件
     * 
     * @param dbConn
     * @param tableName
     * @param field
     * @param sWhere
     * @return
     * @throws Exception
     */
    public static String getDateByField(Connection dbConn, String tableName, String field, String sWhere)
            throws Exception {
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String value = "";
            if (sWhere == null || "".equals(sWhere)) {
                sWhere = " 1=1";
            }
            try {
                ps = dbConn.prepareStatement("select * from " + tableName + " where " + sWhere);
                rs = ps.executeQuery();
                if (rs.next()) {
                    value = rs.getString(field);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            return value;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 封装了一下 不过只能取 rs.getString 类型的
     * 
     * @param dbConn
     * @param tableName
     * @param field
     * @param sWhere
     * @return
     * @throws Exception
     */
    public static Map getDateByField(Connection dbConn, String tableName, String[] field, String sWhere)
            throws Exception {
        Map map = new HashMap<String, String>();
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (sWhere == null || "".equals(sWhere)) {
                sWhere = " 1=1";
            }
            try {
                ps = dbConn.prepareStatement("select * from " + tableName + " where " + sWhere);
                rs = ps.executeQuery();
                if (rs.next()) {

                    for (int i = 0; i < field.length; i++) {
                        map.put(field[i], rs.getString(field[i]));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            return map;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 查询数据库 返回 1,2,3,形式的字符串
     * 
     * @param dbConn
     * @param tableName
     * @param field
     * @param sWhere
     * @return
     * @throws Exception
     */
    public static String getFieldvalueMerge(Connection dbConn, String tableName, String field, String sWhere)
            throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;
        String value = "";
        if (sWhere == null || "".equals(sWhere)) {
            sWhere = " 1=1";
        }
        try {
            ps = dbConn.prepareStatement("select * from " + tableName + " where " + sWhere);
            rs = ps.executeQuery();
            while (rs.next()) {
                value = rs.getString(field) + ",";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        return value;
    }

    public static String privOtherSql(String field, String privOther) throws SQLException {
        if (T9Utility.isNullorEmpty(privOther)) {
            return "";
        }
        if (privOther.endsWith(",")) {
            privOther = privOther.substring(0, privOther.length() - 1);
        }
        String[] privOthers = privOther.split(",");
        String str = "";
        for (String ss : privOthers) {
            if (!T9Utility.isNullorEmpty(ss)) {
                str += " or " + T9DBUtility.findInSet(ss, field);
            }
        }
        return str;
    }

    public static boolean isVoiceMsg(String content) {
        if (T9Utility.isNullorEmpty(content)) {
            return false;
        }
        if (content.startsWith("[vm]") && content.endsWith("[/vm]")) {
            return true;
        } else {
            return false;
        }
    }

    public static String[] getVoiceMsgAttach(String content) {
        if (T9Utility.isNullorEmpty(content) || !isVoiceMsg(content))
            return null;

        content = content.replace("[vm]", "");
        content = content.replace("[/vm]", "");

        String[] result = content.split("\\|");
        return result;
    }

    public static String getVoiceMsgOutputForMobile(String content, String vOICE_PF) {
        // TODO Auto-generated method stub
        if (T9Utility.isNullorEmpty(content))
            return "";
        return "";
    }

    public static int getCURRITERMS(String c) {
        return (!T9Utility.isNullorEmpty(c) && T9Utility.isInteger(c)) ? Integer.parseInt(c) : 0;
    }
}
