package t9.core.funcs.system.info.act;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.setdescktop.userinfo.act.T9UserinfoAct;
import t9.core.funcs.system.info.logic.T9InfoLogic;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9RegistProps;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.SignProvider;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9RegistUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9InfoAct {
    public static final String REG_FILE_PATH = "" + File.separator + "WEB-INF" + File.separator + "config"
            + File.separator;

    /**
     * 获取系统信息
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getSystemInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        T9InfoLogic logic = new T9InfoLogic();
        // String postfix = ".t9";
        try {
            // String regUnitName = "";
            String regUserAmount = "";
            // String regImUserAmount = "";
            // String regMachineCode = "";
            String regOrNot = "0";
            // 软件是否已经注册
            String webInfoPath = T9SysProps.getWebInfPath();
            String keyPath = webInfoPath + File.separator + "config";
            if (SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                // String sp = System.getProperty("file.separator");
                // String path =
                // request.getSession().getServletContext().getRealPath(sp) +
                // REG_FILE_PATH
                // + "t9.regist";
                // Map<String, String> regMap =
                // T9RegistUtility.loadRegist(path);
                // regUnitName = SignProvider.useUnit;//
                // regMap.get(T9AuthKeys.REGIST_ORG
                // + postfix);
                // regMachineCode = regMap.get(T9AuthKeys.MACHINE_CODE +
                // postfix);
                // regUserAmount = SignProvider.userCount;//
                // String.valueOf(T9RegistUtility.getUserCnt());
                // regImUserAmount = "30";
            } else {
                regOrNot = "1";
                // regUnitName = T9RegistProps.getString(T9AuthKeys.REGIST_ORG +
                // postfix);
                // regMachineCode =
                // T9RegistProps.getString(T9AuthKeys.MACHINE_CODE + postfix);
                // regUserAmount = T9RegistProps.getString(T9AuthKeys.USER_CNT +
                // postfix);
                // regUserAmount = String.valueOf(T9RegistUtility.getUserCnt());
                // int userCnt = T9RegistProps.getInt("im.userCnt.t9");
                // if (userCnt <= 0) {
                // userCnt = 30;
                // }
                // regImUserAmount = String.valueOf(userCnt);
            }
            // regUnitName = SignProvider.useUnit;// 注册单位
            regUserAmount = SignProvider.userCount;// 注册用户数

            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            Map<String, String> map = new HashMap<String, String>();

            int userAmount = logic.getUserAmount(dbConn);
            int userAmountNotLogin = logic.getUserAmountNotLogin(dbConn);
            // Map<String, String> version = logic.getVersion(dbConn);
            map.put("softName", SignProvider.softName);
            map.put("regOrNot", regOrNot);
            map.put("userAmount", String.valueOf(userAmount));
            map.put("regUserAmount", regUserAmount);
            // map.put("regImUserAmount", regImUserAmount);
            map.put("userAmountNotLogin", String.valueOf(userAmountNotLogin));
            // map.putAll(version);
            map.put("version", SignProvider.softVersion);
            map.put("systemInfo", getSystemInfo());
            map.put("port", String.valueOf(getPort(request)));
            map.put("serverInfo", String.valueOf(getServerInfo(request)));
            map.put("setupPath", String.valueOf(getSetupPath()));
            // map.put("unitName",
            // T9Utility.encodeSpecial(logic.getUnitName(dbConn)));
            map.put("unitName", SignProvider.softUnit);
            map.put("regUnitName", SignProvider.useUnit);
            // map.put("reg" + T9AuthKeys.MACHINE_CODE, regMachineCode);
            // map.put(T9AuthKeys.MACHINE_CODE,
            // T9RegistUtility.getMchineCode());
            // map.put("serialId", T9RegistProps.getString(T9AuthKeys.SERIAL_ID
            // + ".t9"));
            map.put("remainDays", String.valueOf(SignProvider.validDays()));
            String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
            if (dbms.equals(T9Const.DBMS_ORACLE)) {
                map.put("dbms", "Oracle");
            } else if (dbms.equals(T9Const.DBMS_MYSQL)) {
                map.put("dbms", "Mysql");
            } else if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
                map.put("dbms", "SqlServer");
            }

            String installTimeStr = SignProvider.registerDate;// T9SysProps.getString(T9SysPropKeys.INSTALL_TIME);
            // String timeStr = null;
            // if (!T9Utility.isNullorEmpty(installTimeStr)) {
            // timeStr = T9Utility.getDateTimeStr(new
            // Date(Long.parseLong(installTimeStr)));
            // } else {
            // timeStr = "";
            // }
            // if
            // (T9Utility.isNullorEmpty(T9RegistProps.getString(T9AuthKeys.REGIST_ORG
            // + ".t9"))) {
            // int remainDays = T9RegistUtility.remainDays();
            // if (remainDays < 1) {
            // timeStr += "&nbsp;软件已过期！";
            // } else {
            // }
            // }
            map.put("installTime", installTimeStr);
            String data = T9FOM.toJson(map).toString();

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, data);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 处理上传的注册文件
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String reg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String postfix = ".t9";
        Connection dbConn = null;
        T9InfoLogic logic = new T9InfoLogic();

        T9FileUploadForm fileForm = new T9FileUploadForm();
        fileForm.parseUploadRequest(request);

        Iterator<String> iKeys = fileForm.iterateFileFields();

        String path = null;

        while (iKeys.hasNext()) {
            String fieldName = iKeys.next();
            String fileName = fileForm.getFileName(fieldName);
            if (T9Utility.isNullorEmpty(fileName)) {
                continue;
            }
            String sp = System.getProperty("file.separator");
            path = request.getSession().getServletContext().getRealPath(sp) + T9InfoAct.REG_FILE_PATH
                    + fileName;
            fileForm.saveFile(fieldName, path);
        }
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            // Map<String, String> registInfo =
            // T9RegistUtility.loadRegist(path);
            // 软件是否已经注册
            String webInfoPath = T9SysProps.getWebInfPath();
            String keyPath = webInfoPath + File.separator + "config";
            if (!SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                request.setAttribute("rtState", T9Const.RETURN_ERROR);
                request.setAttribute("rtMsg", "加载注册文件失败，请确认注册文件的合法性!");
                return "/core/funcs/system/info/result.jsp";
            }
            // if (registInfo == null || registInfo.size() < 1) {
            //
            // }
            // String regOrg = registInfo.get(T9AuthKeys.REGIST_ORG + postfix);
            // String org = logic.getUnitName(dbConn);
            //
            // T9RegistProps.clear(".t9");
            // if (regOrg == null || !regOrg.equals(org)) {
            // request.setAttribute("rtState", T9Const.RETURN_ERROR);
            // request.setAttribute("rtMsg", "用户单位错误,注册失败!");
            // return "/core/funcs/system/info/result.jsp";
            // }

            // String regMachinCode = registInfo.get(T9AuthKeys.MACHINE_CODE +
            // postfix);
            // String machinCode = T9RegistUtility.getMchineCode();
            //
            // if (regMachinCode == null || !regMachinCode.equals(machinCode)) {
            // request.setAttribute("rtState", T9Const.RETURN_ERROR);
            // request.setAttribute("rtMsg", "机器码错误,注册失败!");
            // return "/core/funcs/system/info/result.jsp";
            // }
            // T9RegistProps.addProps(registInfo);
            // String sn = registInfo.get(T9AuthKeys.SERIAL_ID + postfix);
            // int imUserCnt = Integer.parseInt(registInfo.get("im.userCnt" +
            // postfix));
            // logic.updateSN(dbConn, sn);
            // logic.updateIM(dbConn, imUserCnt);
            request.setAttribute("rtState", T9Const.RETURN_OK);
            request.setAttribute("rtMsg", "注册成功!");
        } catch (Exception ex) {
            request.setAttribute("rtState", T9Const.RETURN_ERROR);
            request.setAttribute("rtMsg", "注册失败");
            throw ex;
        }
        return "/core/funcs/system/info/result.jsp";
    }

    /**
     * 重新载入注册信息
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String reloadRegInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String postfix = ".t9";
        Connection dbConn = null;
        T9InfoLogic logic = new T9InfoLogic();

        try {

            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            List<String> registInfo = T9SystemLogic.loadRegistRequires(dbConn);
            String webInfoPath = T9SysProps.getWebInfPath();
            String path = webInfoPath + "" + File.separator + "config" + File.separator + "regist"
                    + File.separator + "t9.regist";
            String disk = webInfoPath.substring(0, 3);
            String sn = registInfo.get(0);
            String unitName = registInfo.get(1);

            System.out.println("路径:" + webInfoPath + "" + File.separator + "config" + File.separator
                    + "regist" + File.separator + "t9.regist");
            System.out.println("盘符:" + disk);
            System.out.println("SN:" + sn);
            System.out.println("单位名称:" + unitName);

            Map registMap = T9RegistUtility.loadRegist(path, disk, sn, unitName);
            if (registMap != null && registMap.size() > 0) {
                T9RegistProps.clear(".t9");
                T9RegistProps.addProps(registMap);
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            } else {
                T9RegistProps.clear(".t9");
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "请确认注册文件是否正确！");
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "请确认注册文件是否正确！");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 查看注册文件中的信息
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String detailRegInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String webInfoPath = T9SysProps.getWebInfPath();
            Map registMap = T9RegistUtility.loadRegist(webInfoPath + File.separator + "config"
                    + File.separator + "regist" + File.separator + "t9.regist");
            if (registMap != null && registMap.size() > 0) {
                String data = T9UserinfoAct.toJson(registMap);
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_DATA, data);
                request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            } else {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "请确认注册文件是否正确！");
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "请确认注册文件是否正确！");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 延长试用天数，增加试用人数
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String triaReg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            T9FileUploadForm fileForm = new T9FileUploadForm();
            fileForm.parseUploadRequest(request);
            Iterator<String> iKeys = fileForm.iterateFileFields();
            InputStream is = fileForm.getInputStream((String) fileForm.iterateFileFields().next());
            Map map = fileForm.getParamMap();
            String fileName = "";
            while (iKeys.hasNext()) {
                String fieldName = iKeys.next();
                fileName = fileForm.getFileName(fieldName);
                if (T9Utility.isNullorEmpty(fileName)) {
                    continue;
                }
                String sp = System.getProperty("file.separator");
                String filePath = T9SysProps.getWebInfPath() + File.separatorChar + "config"
                        + File.separatorChar;
                fileForm.saveFile(fieldName, filePath + "patchadeval.properties");
            }
            String patchadeval = T9SysProps.getWebInfPath() + File.separator + "config" + File.separator
                    + "patchadeval.properties";
            T9SysProps.addProps(T9ConfigLoader.loadSysProps(patchadeval));
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "增加试用人数延长试用期限成功！");
            request.setAttribute("rtMsg", "增加试用人数延长试用期限成功！");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "请确认授权文件是否正确！");
            request.setAttribute("rtMsg", "请确认授权文件是否正确！");
            throw ex;
        }
        return "/core/funcs/system/info/result.jsp";
    }

    /**
     * 获取安装路径
     * 
     * @return
     */
    private String getSetupPath() {

        return T9Utility.encodeSpecial(T9SysProps.getRootPath());
    }

    private String getSystemInfo() {
        return System.getProperty("os.name") + " " + System.getProperty("os.arch");
    }

    private int getPort(HttpServletRequest request) {
        int port = request.getServerPort();
        return port;
    }

    private String getDBMS() {
        String dbms = T9SysProps.getString("db.jdbc.dbms");
        return dbms;
    }

    private String getServerInfo(HttpServletRequest request) {
        ServletContext application = request.getSession().getServletContext();
        String serverInfo = application.getServerInfo();
        return serverInfo;
    }

}
