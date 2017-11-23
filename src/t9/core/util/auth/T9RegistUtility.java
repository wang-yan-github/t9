package t9.core.util.auth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.catalina.loader.TDHardWareReader;
import org.apache.log4j.Logger;

import t9.core.data.T9AuthKeys;
import t9.core.global.T9Const;
import t9.core.global.T9RegistProps;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.SignProvider;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;

/**
 * 注册相关类
 * 
 * @author yzq
 * 
 */
public class T9RegistUtility {
    /**
     * log
     */
    private static final Logger log = Logger.getLogger("yzq.t9.core.util.auth.T9RegistUtility");

    public static String getMchineCode() throws Exception {
        if (T9SysProps.isWindows()) {
            return TDHardWareReader.getMachineCode(T9SysProps.getRootPath().substring(0, 3));
        } else if (T9SysProps.isLinux()) {
            return T9LinuxUtility.getMachineCode();
        } else {
            throw new Exception("Unsurported OS");
        }
    }

    /**
     * 加载注册文件
     * 
     * @param fileName
     * @return
     */
    public static Map<String, String> loadRegistFromPath(String fileName, String installDisk, String softId,
            String registOrg) {
        Map registMap = new HashMap<String, String>();
        File file = new File(fileName);
        if (!file.exists()) {
            return registMap;
        }
        if (file.isDirectory()) {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {
                String currFileName = fileList[i];
                if (!currFileName.endsWith(".regist") && !currFileName.endsWith(".install")) {
                    continue;
                }
                String filePath = fileName + File.separator + currFileName;
                Map tmpMap = loadRegist(filePath, installDisk, softId, registOrg);
                T9Utility.copyMap(tmpMap, registMap);
            }
        }
        return registMap;
    }

    /**
     * 加载注册文件
     * 
     * @param fileName
     * @return
     */
    public static Map loadRegist(String fileName, String installDisk, String softId, String registOrg) {
        Map registMap = new HashMap();
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            return registMap;
        }
        try {
            String machineCode = T9RegistUtility.getMchineCode();
            // 加载注册属性
            byte[] buf = T9FileUtility.loadFile2Bytes(fileName);
            if (buf == null) {
                return registMap;
            }
            String propStr = T9Authenticator.ciphDecryptBytes(buf);
            T9Utility.str2Map(propStr, registMap);
            // 删除不合法的注册属性
            List<String> keyList = new ArrayList<String>();
            Iterator<String> iKeys = registMap.keySet().iterator();
            while (iKeys.hasNext()) {
                String key = (String) iKeys.next();
                if (key.endsWith(T9AuthKeys.REGIST_PROPKEY_DIGIST_POSTFIX)) {
                    continue;
                }
                String value = (String) registMap.get(key);
                String didgistKey = key + T9AuthKeys.REGIST_PROPKEY_DIGIST_POSTFIX;
                String didgist = (String) registMap.get(didgistKey);
                if (didgist == null) {
                    keyList.add(key);
                    continue;
                }
                try {
                    if (!T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null), value + machineCode
                            + softId + registOrg, didgist)) {
                        keyList.add(key);
                        keyList.add(didgistKey);
                    } else {
                        keyList.add(didgistKey);
                    }
                } catch (Exception ex) {
                    keyList.add(key);
                }
            }
            for (int i = 0; i < keyList.size(); i++) {
                String key = (String) keyList.get(i);
                registMap.remove(key);
            }
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
        }
        return registMap;
    }

    /**
     * 加载注册文件
     * 
     * @param fileName
     * @return
     */
    public static Map loadRegist(String fileName) {
        Map registMap = new HashMap();
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            return registMap;
        }
        try {
            // 加载注册属性
            byte[] buf = T9FileUtility.loadFile2Bytes(fileName);
            if (buf == null) {
                return registMap;
            }
            String propStr = T9Authenticator.ciphDecryptBytes(buf);
            T9Utility.str2Map(propStr, registMap);
            // 删除不合法的注册属性
            List<String> keyList = new ArrayList<String>();
            Iterator<String> iKeys = registMap.keySet().iterator();
            while (iKeys.hasNext()) {
                String key = (String) iKeys.next();
                if (key.endsWith(T9AuthKeys.REGIST_PROPKEY_DIGIST_POSTFIX)) {
                    keyList.add(key);
                }
            }
            for (int i = 0; i < keyList.size(); i++) {
                String key = (String) keyList.get(i);
                registMap.remove(key);
            }
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
        }
        return registMap;
    }

    /**
     * 软件是否已经注册
     * 
     * @return
     */
    public static boolean hasRegisted() {
        return !T9Utility.isNullorEmpty(T9RegistProps.getString(T9AuthKeys.REGIST_ORG + ".t9"));
    }

    /**
     * 软件是否已经过期
     * 
     * @return
     */
    public static boolean isExpired() {
        return remainDays() < 1;
    }

    /**
     * 软件是否已经过期
     * 
     * @return
     */
    public static int remainDays() {
        String installTimeStr = T9SysProps.getString(T9SysPropKeys.INSTALL_TIME);
        String installTimePassStr = T9SysProps.getString(T9SysPropKeys.INSTALL_TIME_PASS);
        if (T9Utility.isNullorEmpty(installTimeStr) || T9Utility.isNullorEmpty(installTimePassStr)) {
            return 0;
        }
        try {
            if (!T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null), installTimeStr,
                    installTimePassStr)) {
                return 0;
            }
            long installTime = Long.parseLong(installTimeStr);
            long currTime = System.currentTimeMillis();
            String evalueDaysStr = T9SysProps.getString("evalueDays");
            String evalueDaysPass = T9SysProps.getString("evalueDaysPass");

            int evalueDate = 30;
            if (!T9Utility.isNullorEmpty(evalueDaysStr)
                    && T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null), evalueDaysStr
                            + T9RegistUtility.getMchineCode(), evalueDaysPass)) {
                evalueDate = Integer.parseInt(evalueDaysStr);
            }

            if (((currTime - installTime) / T9Const.DT_D) > evalueDate) {
                return 0;
            }
            return evalueDate - (int) ((currTime - installTime) / T9Const.DT_D);
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 取得授权用户数
     * 
     * @return
     */
    public static int getUserCnt() {
        int defaultCnt = 30;
        try {
            if (SignProvider.type != null) {
                if (SignProvider.type.equals("oem")) {
                    return Integer.parseInt(SignProvider.userCount);
                }
            }
        } catch (Exception e) {
        }
        return defaultCnt;
        // int maxUserCnt = T9RegistProps.getInt(T9AuthKeys.USER_CNT + ".t9");
        // if (maxUserCnt > 0) {
        // return maxUserCnt;
        // }
        // int defaultCnt = 30;
        // String userCntStr = T9SysProps.getString("maxUserCnt");
        // String userCntPassStr = T9SysProps.getString("maxUserCntPass");
        // if (T9Utility.isNullorEmpty(userCntStr) ||
        // T9Utility.isNullorEmpty(userCntPassStr)) {
        // return defaultCnt;
        // }
        // try {
        // if (!T9Authenticator.isValidRegist(T9AuthKeys.getMD5SaltLength(null),
        // userCntStr
        // + T9RegistUtility.getMchineCode(), userCntPassStr)) {
        // return defaultCnt;
        // }
        // int userCnt = Integer.parseInt(userCntStr);
        // return userCnt;
        // } catch (Exception ex) {
        // return defaultCnt;
        // }
    }
}
