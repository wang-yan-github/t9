package t9.core.frame.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.frame.logic.T9WebosLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.portal.logic.T9PortalLogic;
import t9.core.funcs.setdescktop.syspara.logic.T9SysparaLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.interfaces.data.T9InterFaceCont;
import t9.core.funcs.system.interfaces.logic.T9InterFacesLogic;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.SignProvider;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9RegistUtility;
import t9.core.util.form.T9FOM;

public class T9WebosAct {
    private static Logger log = Logger.getLogger("t9.core.frame.act.T9WebosAct");

    public String queryInitInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
            // 门户的id
            String idStr = request.getParameter("id");
            int id = -1;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {

            }

            if ("personal".equals(idStr)) {
                id = -2;
            }

            T9SysparaLogic sysParaLogic = new T9SysparaLogic();
            T9UserinfoLogic userInfoLogic = new T9UserinfoLogic();
            T9SystemLogic systemLogic = new T9SystemLogic();
            T9PortalLogic portalLogic = new T9PortalLogic();
            T9WebosLogic webosLogic = new T9WebosLogic();

            String portal = portalLogic.listPorts(dbConn, user, id);
            String title = systemLogic.getIETitle(dbConn);
            if (T9Utility.isNullorEmpty(title)) {
                title = T9SysProps.getString("productName");
            }
            Map<String, String> map = userInfoLogic.queryInfo(dbConn, user);
            Map<String, String> otherPara = getOtherPara(request);
            Map<String, String> smsPara = getSmsPara();

            trimStringMap(map);
            trimStringMap(otherPara);
            trimStringMap(smsPara);

            int count = sysParaLogic.queryUserCount(dbConn);
            String onlineRefStr = T9SysProps.getString("$ONLINE_REF_SEC");
            if (onlineRefStr == null || "".equals(onlineRefStr.trim())) {
                onlineRefStr = "3600";
            }

            String funcId = sysParaLogic.queryFuncId(dbConn, "控制面板");
            otherPara.put("controlId", funcId);
            StringBuffer sb = new StringBuffer("{");
            sb.append("\"userInfo\":");
            sb.append(T9FOM.toJson(map).toString());
            sb.append(",\"background\":\"");
            sb.append(map.get("desktopBg"));
            sb.append("\",\"portal\":");
            sb.append(portal);
            sb.append(",\"browserTitle\":\"");
            sb.append(T9Utility.encodeSpecial(title));
            sb.append("\",\"onlineAmount\": {\"amount\":");
            sb.append(count);
            sb.append(",\"onlineRefStr\":");
            sb.append(onlineRefStr);
            sb.append("},\"smsPara\":");
            sb.append(T9FOM.toJson(smsPara));
            sb.append(",\"otherPara\":");
            sb.append(T9FOM.toJson(otherPara));
            sb.append(",\"bannerInfo\":");
            sb.append(T9FOM.toJson(webosLogic.getBannerInfo(dbConn)));
            sb.append(",\"logoutMsg\":");
            String logoutMsg = sysParaLogic.queryLogoutText(dbConn);
            sb.append("\"" + T9Utility.encodeSpecial(logoutMsg) + "\"");
            sb.append(",\"regist\":");
            sb.append(T9FOM.toJson(registInfo(request)));
            sb.append("}");

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public Map<String, String> getSmsPara() {
        Map<String, String> map = new HashMap<String, String>();
        String smsRef = T9SysProps.getString("$SMS_REF_SEC");
        if (smsRef == null || "".equals(smsRef.trim())) {
            smsRef = "30";
        }
        String smsCallCount = T9SysProps.getString("$SMS_REF_MAX");
        if (smsCallCount == null || "".equals(smsCallCount.trim())) {
            smsCallCount = "3";
        }
        String smsInterval = T9SysProps.getString("$SMS_CALLSOUND_INTERVAL");
        if (smsInterval == null || "".equals(smsInterval.trim())) {
            smsInterval = "3";
        }
        map.put("smsRef", smsRef);
        map.put("smsCallCount", smsCallCount);
        map.put("smsInterval", smsInterval);
        return map;
    }

    public Map<String, String> getOtherPara(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        String sessionToken = (String) request.getSession().getAttribute("sessionToken");
        String statusRefStr = T9SysProps.getString("$STATUS_REF_SEC");
        if (statusRefStr == null || "".equals(statusRefStr.trim())) {
            statusRefStr = "3600";
        }
        T9SysparaLogic logic = new T9SysparaLogic();
        int remainDays = T9RegistUtility.remainDays();
        map.put("sesstionToken", sessionToken);
        map.put("statusRefStr", statusRefStr);
        map.put("remainDays", String.valueOf(remainDays));
        return map;
    }

    private Map<String, String> registInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        String hasReg = "1";
        try {
            // 软件是否已经注册
            String webInfoPath = T9SysProps.getWebInfPath();
            String keyPath = webInfoPath + File.separator + "config";
            if (SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                map.put("versionType", SignProvider.type);
                long remainDays = SignProvider.validDays();
                map.put("remainDays", String.valueOf(remainDays));
                if (!SignProvider.validDomain(request)) {
                    // 域名非法
                    hasReg = "0";
                    map.put("msg", "域名非法");
                }
                // 已经注册（在判断有效期）
                // 先验证版本（试用版30天，收费版）
                if (SignProvider.type != null) {
                    if (SignProvider.type.equals("free")) {
                        hasReg = "0";
                        map.put("msg", "免费版");
                    } else if (SignProvider.type.equals("oem")) {
                        if (!SignProvider.checkExpireValidDate()) {
                            // 已经过期
                            hasReg = "0";
                            map.put("msg", "已经过期");
                        }
                    } else if (SignProvider.type.equals("trail")) {
                        // 验证是否免费到期
                        if (!SignProvider.checkTrailVersion()) {
                            // 试用到期
                            hasReg = "0";
                            map.put("msg", "已经过期");
                        }
                    } else {
                        // 序列号错误
                        hasReg = "0";
                        map.put("msg", "序列号错误，请联系厂家！");
                    }
                } else {
                    // 序列号错误
                    hasReg = "0";
                    map.put("msg", "序列号错误，请联系厂家！");
                }
            } else {
                // 序列号非法
                hasReg = "0";
                map.put("msg", "序列号错误，请联系厂家！");
            }
        } catch (Exception e) {
            hasReg = "0";
            map.put("msg", "序列号错误，请联系厂家！");
            // request.setAttribute(T9ActionKeys.RET_MSRG, "系统没有注册，请联系厂家！");
        }

        // if (!T9RegistUtility.hasRegisted()) {
        // hasReg = "0";
        // int remainDays = T9RegistUtility.remainDays();
        // map.put("remainDays", String.valueOf(remainDays));
        // try {
        // map.put("machineCode", T9RegistUtility.getMchineCode());
        // } catch (Exception e) {
        // log.debug("获取机器码异常：" + e.getMessage());
        // }
        // }
        map.put("hasRegisted", hasReg);
        return map;
    }

    private void trimStringMap(Map<String, String> map) {
        for (String s : map.keySet()) {
            String value = map.get(s);
            if (value != null) {
                map.put(s, map.get(s).trim());
            }
        }
    }

    public String queryHeaderImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String defaultPath = "/core/frame/webos/styles/style1/images/logo.png";
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9InterFacesLogic logic = new T9InterFacesLogic();
            String guid = logic.queryWebOSLOGO(dbConn);

            File dir = new File(T9InterFaceCont.ATTA_PATH + File.separator + T9InterFaceCont.MODULE
                    + File.separator + guid);
            File[] files = dir.listFiles();
            if (files == null || files.length <= 0) {
                return defaultPath;
            }

            File file = files[0];
            if (!file.exists()) {
                return defaultPath;
            }

            FileInputStream fis = new FileInputStream(file);
            response.setContentType("image/");
            OutputStream out = response.getOutputStream();

            byte[] b = new byte[1024];
            int i = 0;

            while ((i = fis.read(b)) > 0) {
                out.write(b, 0, i);
            }

            out.flush();

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
        } catch (FileNotFoundException ex) {
            return defaultPath;
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        // return "/core/inc/rtjson.jsp";
        return "";
    }

    public String listWallpappers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String s = File.separator;
            String path = request.getSession().getServletContext().getRealPath(s) + "core" + s + "frame" + s
                    + "webos" + s + "styles" + s + "wallpapers";

            StringBuffer sb = new StringBuffer("[");

            File dir = new File(path);
            File[] files = dir.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".jpg") || f.getName().endsWith(".JPG")
                            || f.getName().endsWith(".png") || f.getName().endsWith(".PNG")
                            || f.getName().endsWith(".jpeg") || f.getName().endsWith(".JPEG")
                            || f.getName().endsWith(".gif") || f.getName().endsWith(".GIF")) {
                        sb.append("\"");
                        sb.append(f.getName());
                        sb.append("\",");
                    }
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }

            sb.append("]");

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }
}