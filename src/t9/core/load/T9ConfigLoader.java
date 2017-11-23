package t9.core.load;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRunThread;
import t9.core.data.T9AuthKeys;
import t9.core.data.T9DataSources;
import t9.core.data.T9SessionPool;
import t9.core.funcs.system.ispirit.n12.org.act.T9IsPiritOrgAct;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9Authenticator;
import t9.core.util.db.T9DBUtility;

public class T9ConfigLoader {
    private static Logger log = Logger.getLogger("yzq.t9.core.load.T9ConfigLoader");

    /**
     * 从系统配置文件中加载系统配置
     * 
     * @param sysPropsFile
     *            系统配置文件
     * @return
     */
    public static Properties loadSysProps(String sysPropsFile) {
        return loadSysProps(new File(sysPropsFile));
    }

    /**
     * 从系统配置文件中加载系统配置
     * 
     * @param sysPropsFile
     *            系统配置文件
     * @return
     */
    public static Properties loadSysProps(File sysPropsFile) {
        Properties props = new Properties();

        if (!sysPropsFile.exists()) {
            return props;
        }
        InputStream inProps = null;
        try {
            inProps = new BufferedInputStream(new FileInputStream(sysPropsFile));
            props.load(inProps);
        } catch (IOException ex) {
        } finally {
            try {
                if (inProps != null) {
                    inProps.close();
                }
            } catch (IOException ex) {
            }
        }

        return props;
    }

    /**
     * 系统初始化
     * 
     * @param installPath
     *            系统的安装路径
     * 
     * @return
     */
    public static void loadInit(String rootPath) throws Exception {
        T9SessionPool.stopReleaseThread();
        T9DataSources.closeConnPool();
        T9AutoRunThread.stopRun();
        System.out.println("System init start...");
        if (log.isDebugEnabled()) {
            log.debug("System init start...");
        }
        File rootPathFile = new File(rootPath);
        String installPath = null;
        String webRoot = null;
        String ctx = null;
        try {
            String realRootPath = rootPathFile.getCanonicalPath();
            int p1 = realRootPath.lastIndexOf(File.separator);
            int p2 = realRootPath.substring(0, p1).lastIndexOf(File.separator);
            installPath = realRootPath.substring(0, p2);
            webRoot = realRootPath.substring(p2 + 1, p1);
            ctx = realRootPath.substring(p1 + 1);
            System.out.println("安装路径：" + installPath);
        } catch (Exception ex) {
        }

        // 从配置文件中加载系统配置
        String sysConfFile = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "sysconfig.properties";
        T9SysProps.setProps(T9ConfigLoader.loadSysProps(sysConfFile));
        String selfConfFile = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "selfconfig.properties";
        T9SysProps.addProps(T9ConfigLoader.loadSysProps(selfConfFile));
        String patchadeval = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "patchadeval.properties";
        T9SysProps.addProps(T9ConfigLoader.loadSysProps(patchadeval));
        String patchDays = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "patchdays.properties";
        T9SysProps.addProps(T9ConfigLoader.loadSysProps(patchDays));
        String patchUserCnt = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "patchuser.properties";
        T9SysProps.addProps(T9ConfigLoader.loadSysProps(patchUserCnt));
        // 设置安装路径、Webroot、contextPath
        Map pathMap = new HashMap<String, String>();
        pathMap.put(T9SysPropKeys.ROOT_DIR, installPath);
        pathMap.put(T9SysPropKeys.WEB_ROOT_DIR, webRoot);
        pathMap.put(T9SysPropKeys.JSP_ROOT_DIR, ctx);
        T9SysProps.addProps(pathMap);
        processSysInfo();
        // 构建数据源

        String dbConfPath = rootPath + "WEB-INF" + File.separator + "config" + File.separator
                + "dbconfig.properties";
        T9DataSources.buildDataSourceMap(dbConfPath);

        System.out.println("System init done.");
        if (log.isDebugEnabled()) {
            log.debug("System init done.");
        }

        // 清空在线人员表

        Connection dbConn = null;
        Statement stmt = null;
        try {
            T9DBUtility dbUtil = new T9DBUtility();
            dbConn = dbUtil.getConnection(false, T9SysProps.getSysDbName());

            String sql = "delete from USER_ONLINE";
            synchronized (T9SystemService.onlineSync) {
                stmt = dbConn.createStatement();
                stmt.executeUpdate(sql);
                dbConn.commit();
            }
            // 精灵生成org.xml
            try {
                T9IsPiritOrgAct.getOrgDataStream(dbConn);
            } catch (Exception ex) {
                log.debug(ex.getMessage(), ex);
            }

        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            try {
                if (dbConn != null) {
                    dbConn.rollback();
                }
            } catch (Exception ex2) {
            }
        } finally {
            T9DBUtility.close(stmt, null, log);
            T9DBUtility.closeDbConn(dbConn, log);
        }
        // 启动后台线程
        try {
            int sleepTime = T9SysProps.getInt(T9SysPropKeys.BACK_THREAD_SLEEP_TIME);
            if (sleepTime < 1) {
                sleepTime = 100;
            }
            T9AutoRunThread.startAutoRun(sleepTime);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
        }
    }

    /**
     * 处理系统信息
     */
    private static void processSysInfo() throws Exception {
        String[] keyArray = new String[] { "shortOrgName", "orgName", "productName", "fullOrgName",
                "t9SysInfo", "shortProductName", "orgFirstSite", "orgSecondSite", "workflowZipDown" };
        String[] defaultValue = new String[] { "慧客", "南京慧客", "123", "南京慧客", "智能OA系统信息", "智能OA", "", "", "" };
        Map<String, String> sysInfoMap = new HashMap<String, String>();
        for (int i = 0; i < keyArray.length; i++) {
            String key = keyArray[i];
            String value = T9SysProps.getString(key);
            String pass = T9SysProps.getString(key + "Pass");

            if (!T9Utility.isNullorEmpty(value)) {
                if (T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null), value, pass)) {
                    sysInfoMap.put(key, value);
                } else {
                    sysInfoMap.put(key, defaultValue[i]);
                }
            } else {
                sysInfoMap.put(key, defaultValue[i]);
            }
        }
        T9SysProps.addProps(sysInfoMap);
    }
}
