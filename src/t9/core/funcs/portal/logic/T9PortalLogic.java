package t9.core.funcs.portal.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.portal.act.T9PortalAct;
import t9.core.funcs.portal.data.T9Port;
import t9.core.funcs.portal.data.T9PortStyle;
import t9.core.funcs.portal.data.T9Portal;
import t9.core.funcs.portal.data.T9PortalPort;
import t9.core.funcs.setdescktop.setports.logic.T9DesktopDefineLogic;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.mobile.util.T9QuickQuery;

public class T9PortalLogic {
    private String        sp  = System.getProperty("file.separator");
    private static Logger log = Logger.getLogger("t9.core.funcs.portal.act");

    public void savePorta(Connection dbConn, String seqId, String subject,
            String toId, String userId, String privId, String nickname, T9Person person) {
        T9Port port = new T9Port();
        port.setFileName(subject + ".js");
        if (!"".equals(toId) && toId != null) {
            port.setDeptId(toId);
        } else {
            port.setDeptId("0");
        }
        port.setNickname(T9Utility.null2Empty(nickname));
        port.setDeptId(T9Utility.null2Empty(toId));
        port.setUserId(T9Utility.null2Empty(userId));
        port.setPrivId(T9Utility.null2Empty(privId));
        port.setViewType("1");
        port.setModuleLines(5);
        port.setModuleScroll("0");
        port.setType(1); // 手动添加
        port.setRemark("部门公告.js");

        if (seqId != null && !"".equals(seqId)) { // 修改
            try {
                String sql = "select SEQ_ID,DEPT_ID,MYTABLE_LEFT,MYTABLE_RIGHT from person";
                Statement stmt = null;
                ResultSet rs = null;
                stmt = dbConn.createStatement();
                rs = stmt.executeQuery(sql);
                List<Map<String, String>> list = new ArrayList<Map<String,String>>();
                while(rs.next()){
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("SEQ_ID", rs.getString("SEQ_ID"));
                    map.put("DEPT_ID", rs.getString("DEPT_ID"));
                    map.put("MYTABLE_LEFT", rs.getString("MYTABLE_LEFT"));
                    map.put("MYTABLE_RIGHT", rs.getString("MYTABLE_RIGHT"));
                    list.add(map);
                }
                T9DesktopDefineLogic ddl = new T9DesktopDefineLogic();
                for (int i = 0; i < list.size(); i++) {
                    String personId = list.get(i).get("SEQ_ID");
                    String left = list.get(i).get("MYTABLE_LEFT");
                    String right = list.get(i).get("MYTABLE_RIGHT");
                    sql = "Update person Set MYTABLE_LEFT = replace('" + left + "','"+ seqId+"," + "','') where DEPT_ID NOT IN(" + toId + ") and (MYTABLE_LEFT != 'default') and (SEQ_ID = '" + personId + "')";
                    T9QuickQuery.update(dbConn, sql);
                    sql = "Update person Set MYTABLE_RIGHT = replace('" + right + "','"+ seqId+"," + "','') where DEPT_ID NOT IN(" + toId + ") and (MYTABLE_RIGHT != 'default') and (SEQ_ID = '" + personId + "')";
                    T9QuickQuery.update(dbConn, sql);
                    ddl.setDesktopConfigToUser(dbConn,person);
                    /*sql = "Update person Set MYTABLE_RIGHT = '"+ right + seqId + "," +"' where DEPT_ID IN(" + toId + ") and (MYTABLE_RIGHT != 'default') and (SEQ_ID = '" + personId + "')";
                    T9QuickQuery.update(dbConn, sql);*/
                }
                
                /*
                String updateLeft = "";
                String updateRight = "";
                boolean flag = false;
                for (int i = 0; i < list.size(); i++) {
                    String personId = list.get(i).get("SEQ_ID");
                    String deptId = list.get(i).get("DEPT_ID"); // 用户部门
                    String[] deptIds = port.getDeptId().split(","); // 桌面 部门
                    for (int j = 0; j < deptIds.length; j++) {
                        
                        if (!deptIds[j].equals(deptId)) { // 桌面模块部门权限不等于用户部门
                            String left = list.get(i).get("MYTABLE_LEFT");
                            String[] lefts = left.split(",");
                            for (int k = 0; k < lefts.length; k++) {
                                if (seqId.equals(lefts[k])) {
                                    lefts[k+1] += seqId;
                                    flag = true;
                                }else{
                                    lefts[k] = "";
                                }
                            }
                            if(flag){
                                sql = "UPDATE person SET MYTABLE_LEFT='" + updateLeft + "' WHERE SEQ_ID='" + personId + "'";
                                T9QuickQuery.update(dbConn, sql);
                            }
                            flag = false;
                            String right = list.get(i).get("MYTABLE_RIGHT");
                            String[] rights = right.split(",");
                            for (int k = 0; k < rights.length; k++) {
                                if (seqId.equals(rights[k])) {
                                    rights[k+1] = seqId;
                                    flag = true;
                                }else{
                                    rights[k] = "";
                                }
                            }
                            if(flag){
                                sql = "UPDATE person SET MYTABLE_RIGHT='" + updateRight + "' WHERE SEQ_ID='" + personId + "'";
                                T9QuickQuery.update(dbConn, sql);
                            }
                            flag = false;
                        }
                    }
                }*/

                port.setSeqId(Integer.valueOf(seqId));
                this.updatePort(dbConn, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.newPort(dbConn, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getModData(Connection conn, int seqId, String wPath)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        Statement stm = null;
        ResultSet rs = null;
        String fileName = "";
        String deptId = "";
        String privId = "";
        String userId = "";
        try {
            String query = "select FILE_NAME , DEPT_ID , PRIV_ID , USER_ID FROM PORT WHERE SEQ_ID="
                    + seqId;
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (rs.next()) {
                fileName = rs.getString("FILE_NAME");
                deptId = rs.getString("DEPT_ID");
                privId = rs.getString("PRIV_ID");
                userId = rs.getString("USER_ID");
                deptId = (deptId == null) ? "" : deptId;
                userId = (userId == null) ? "" : userId;
                privId = (privId == null) ? "" : privId;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, rs, log);
        }
        T9DeptLogic deptLogic = new T9DeptLogic();
        String deptDesc = deptLogic.getNameByIdStr(deptId, conn);
        T9UserPrivLogic privLogic = new T9UserPrivLogic();
        String roleDesc = "";
        if (!T9Utility.isNullorEmpty(privId)) {
            roleDesc = privLogic.getNameByIdStr(privId, conn);
        }
        T9PersonLogic personLogic = new T9PersonLogic();
        String userDesc = personLogic.getNameBySeqIdStr(userId, conn);
        String priv = "{user:\"" + userId + "\",userDesc:\"" + userDesc
                + "\",dept:\"" + deptId + "\",deptDesc:\"" + deptDesc
                + "\",role:\"" + privId + "\",roleDesc:\"" + roleDesc + "\"}";
        String path = wPath + fileName;
        File file = new File(path);
        String script = "{}";
        if (file.exists()) {
            script = new String(T9FileUtility.loadFile2Bytes(path), "UTF-8");
        }
        String dataPath = wPath + "data" + sp + "data.properties";
        String defData = "";
        try {
            File dataFile = new File(dataPath);
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            fileName = fileName.replace(".js", "");
            Map<String, String> dataMap = new HashMap();
            T9FileUtility.load2Map(dataPath, dataMap);
            defData = dataMap.get(fileName);
            if (T9Utility.isNullorEmpty(defData)) {
                defData = "{}";
            }
        } catch (Exception ex) {
            throw ex;
        }
        sb.append("{").append("script:").append(script).append(",defData:")
                .append(defData).append(",priv:").append(priv).append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getModData(Connection conn, int seqId)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Statement stm = null;
        ResultSet rs = null;
        String fileName = "";
        String nickname = "";
        String deptId = "";
        String privId = "";
        String userId = "";
        try {
            String query = "select FILE_NAME , DEPT_ID , PRIV_ID , USER_ID , NICKNAME FROM PORT WHERE SEQ_ID="
                    + seqId;
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (rs.next()) {
                fileName = rs.getString("FILE_NAME");
                nickname = rs.getString("NICKNAME");
                deptId = rs.getString("DEPT_ID");
                privId = rs.getString("PRIV_ID");
                userId = rs.getString("USER_ID");
                deptId = (deptId == null) ? "" : deptId;
                userId = (userId == null) ? "" : userId;
                privId = (privId == null) ? "" : privId;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, rs, log);
        }
        T9DeptLogic deptLogic = new T9DeptLogic();
        String deptDesc = deptLogic.getNameByIdStr(deptId, conn);
        T9UserPrivLogic privLogic = new T9UserPrivLogic();
        String roleDesc = "";
        if (!T9Utility.isNullorEmpty(privId)) {
            roleDesc = privLogic.getNameByIdStr(privId, conn);
        }
        T9PersonLogic personLogic = new T9PersonLogic();
        String userDesc = personLogic.getNameBySeqIdStr(userId, conn);
        /*
         * String priv = "{user:\"" + userId + "\",userDesc:\"" + userDesc +
         * "\",dept:\"" + deptId + "\",deptDesc:\"" + deptDesc + "\",role:\"" +
         * privId + "\",roleDesc:\"" + roleDesc + "\"}";
         */
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        map.put("user", userId);
        map.put("userDesc", userDesc);
        map.put("dept", deptId);
        map.put("deptDesc", deptDesc);
        map.put("role", privId);
        map.put("roleDesc", roleDesc);
        fileName = fileName.replace(".js", "");
        map.put("fileName", fileName);
        map.put("nickname", nickname);
        list.add(map);
        /*
         * String defData = ""; try { Map<String, String> dataMap = new
         * HashMap(); defData = dataMap.get(fileName); if
         * (T9Utility.isNullorEmpty(defData)) { defData = "{}"; } } catch
         * (Exception ex) { throw ex; }
         */
        /*
         * sb.append("{").append(",defData:").append(defData).append(",priv:")
         * .append(priv).append("}"); return sb.toString();
         */
        return list;
    }

    public void newPort(Connection conn, T9Port port) throws Exception {
        if (port.getModuleLines() == 0) {
            port.setModuleLines(5);
        }

        if (T9Utility.isNullorEmpty(port.getModulePos())) {
            Random r = new Random();
            String pos = r.nextBoolean() ? "l" : "r";
            port.setModulePos(pos);
        }

        try {
            T9ORM orm = new T9ORM();
            orm.saveSingle(conn, port);
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    /**
     * 新建门户
     * 
     * @param conn
     * @param port
     * @throws Exception
     */
    public int newPortal(Connection conn, T9Portal portal) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            T9ORM orm = new T9ORM();
            orm.saveSingle(conn, portal);

            String sql = "select SEQ_ID" + " from PORTAL"
                    + " where FILE_NAME = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, portal.getFileName());
            rs = ps.executeQuery();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt("SEQ_ID");
            }
            return id;
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
    }

    public void deletePort(Connection conn, int seqId) throws Exception {
        String sp = System.getProperty("file.separator");
        String wPath = T9SysProps.getWebPath() + sp + "core" + sp + "funcs"
                + sp + "portal" + sp + "modules" + sp;
        this.deletePort(conn, seqId, wPath);
    }

    public void deletePort(Connection conn, int seqId, String wPath)
            throws Exception {
        Statement stm = null;
        ResultSet rs = null;
        try {
            T9ORM orm = new T9ORM();
            String query = "select FILE_NAME FROM PORT WHERE SEQ_ID=" + seqId;
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            String fileName = "";
            if (rs.next()) {
                fileName = rs.getString("FILE_NAME");
            }
            String path = wPath + fileName;
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            String dataPath = wPath + "data" + sp + "data.properties";
            try {
                File dataFile = new File(dataPath);
                if (!dataFile.exists()) {
                    dataFile.createNewFile();
                }
                fileName = fileName.replace(".js", "");
                Map<String, String> dataMap = new HashMap();
                T9FileUtility.load2Map(dataPath, dataMap);
                dataMap.remove(fileName);
                Set<String> set = dataMap.keySet();
                String str = "";
                for (String key : set) {
                    String value = dataMap.get(key);
                    str += key + " = " + value + "\r\n";
                }
                T9FileUtility.storeString2File(dataPath, str);
            } catch (Exception ex) {
                throw ex;
            }
            orm.deleteSingle(conn, T9Port.class, seqId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, rs, log);
        }
    }

    public List<T9Port> listPort(Connection dbConn, int userId,
            Collection<String> depts, Collection<String> privs)
            throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String deptStr = findInSet4Collection(depts, "DEPT_ID");
            deptStr = T9Utility.isNullorEmpty(deptStr) ? "" : " or " + deptStr;

            String privStr = findInSet4Collection(privs, "PRIV_ID");
            privStr = T9Utility.isNullorEmpty(privStr) ? "" : " or " + privStr;

            String sql = "select SEQ_ID" + ",FILE_NAME" + ",REMARK"
                    + " from PORT" + " where "
                    + T9DBUtility.findInSet(String.valueOf(userId), "USER_ID")
                    + " or ltrim(rtrim(DEPT_ID)) = '0'"
                    + " or ltrim(rtrim(DEPT_ID)) = 'ALL_DEPT'" + deptStr
                    + privStr;
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            List<T9Port> list = new ArrayList<T9Port>();
            while (rs.next()) {
                T9Port port = new T9Port();
                port.setSeqId(rs.getInt("SEQ_ID"));
                port.setFileName(T9SystemLogic.parseString(rs
                        .getString("FILE_NAME")));
                list.add(port);
            }
            return list;
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
    }

    /**
     * 列出所有有权限的门户
     * 
     * @param dbConn
     * @param userId
     * @param depts
     * @param privs
     * @return
     * @throws Exception
     */
    public List<T9Portal> listPortal(Connection dbConn, int userId,
            Collection<String> depts, Collection<String> privs)
            throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String deptStr = findInSet4Collection(depts, "DEPT_ID");
            deptStr = T9Utility.isNullorEmpty(deptStr) ? "" : " or " + deptStr;

            String privStr = findInSet4Collection(privs, "PRIV_ID");
            privStr = T9Utility.isNullorEmpty(privStr) ? "" : " or " + privStr;

            String sql = "select SEQ_ID" + ",FILE_NAME" + ",STATUS"
                    + ",PORTAL_NAME" + ",REMARK" + " from PORTAL" + " where "
                    + T9DBUtility.findInSet(String.valueOf(userId), "USER_ID")
                    + " or ltrim(rtrim(DEPT_ID)) = '0'"
                    + " or ltrim(rtrim(DEPT_ID)) = 'ALL_DEPT'" + deptStr
                    + privStr;
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            List<T9Portal> list = new ArrayList<T9Portal>();
            while (rs.next()) {
                T9Portal portal = new T9Portal();
                portal.setSeqId(rs.getInt("SEQ_ID"));
                portal.setPortalName(T9SystemLogic.parseString(rs
                        .getString("PORTAL_NAME")));
                portal.setRemark(T9SystemLogic.parseString(rs
                        .getString("REMARK")));
                portal.setStatus(rs.getInt("STATUS"));
                portal.setFileName(T9SystemLogic.parseString(rs
                        .getString("FILE_NAME")));
                list.add(portal);
            }
            return list;
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
    }

    /**
     * 通过Id查询样式
     * 
     * @param dbConn
     * @param styleId
     * @return
     * @throws Exception
     */
    public T9PortStyle queryStyle(Connection dbConn, String styleId)
            throws Exception {
        try {
            T9ORM orm = new T9ORM();
            Map<String, String> filters = new HashMap<String, String>();
            filters.put("GUID", styleId);
            T9PortStyle style = (T9PortStyle) orm.loadObjSingle(dbConn,
                    T9PortStyle.class, filters);
            return style;
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    /**
     * 通过ID查询port信息
     * 
     * @param dbConn
     * @param portId
     * @return
     * @throws Exception
     */
    public T9Port queryPort(Connection dbConn, int portId) throws Exception {
        try {
            T9ORM orm = new T9ORM();
            T9Port port = (T9Port) orm.loadObjSingle(dbConn, T9Port.class,
                    portId);
            return port;
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    /**
     * 通过ID查询port信息,带权限判断
     * 
     * @param dbConn
     * @param portId
     * @return
     * @throws Exception
     */
    public T9Port queryPort(Connection dbConn, int portId, int portalId,
            Collection<String> depts, Collection<String> privs)
            throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String deptStr = findInSet4Collection(depts, "DEPT_ID");
            deptStr = T9Utility.isNullorEmpty(deptStr) ? "" : " or " + deptStr;

            String privStr = findInSet4Collection(privs, "PRIV_ID");
            privStr = T9Utility.isNullorEmpty(privStr) ? "" : " or " + privStr;

            String sql = "select SEQ_ID"
                    + ",FILE_NAME"
                    + ",REMARK"
                    + " from PORT"
                    + " where SEQ_ID = ?"
                    + " and ("
                    + T9DBUtility
                            .findInSet(String.valueOf(portalId), "USER_ID")
                    + " or ltrim(rtrim(DEPT_ID)) = '0'"
                    + " or ltrim(rtrim(DEPT_ID)) = 'ALL_DEPT'" + deptStr
                    + privStr + ")";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, portId);
            rs = ps.executeQuery();

            T9Port port = null;
            if (rs.next()) {
                port = new T9Port();
                port.setSeqId(rs.getInt("SEQ_ID"));
                port.setFileName(T9SystemLogic.parseString(rs
                        .getString("FILE_NAME")));
            }
            return port;
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
    }

    /**
     * 扩展findInSet的参数成集合
     * 
     * @param c
     * @param dbFeildName
     * @return
     * @throws SQLException
     */
    private String findInSet4Collection(Collection<String> c, String dbFeildName)
            throws SQLException {
        String str = "";
        for (String s : c) {
            str += " or " + T9DBUtility.findInSet(s, dbFeildName);
        }
        return str.startsWith(" or") ? str.replaceFirst(" or", "") : str;
    }

    public List<T9PortalPort> listPortalPort(Connection dbConn, int portalId)
            throws Exception {
        try {
            T9ORM orm = new T9ORM();
            String[] filters = new String[] { "PORTAL_ID = " + portalId
                    + " order by SORT_NO" };
            List<T9PortalPort> list = orm.loadListSingle(dbConn,
                    T9PortalPort.class, filters);
            return list;
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    private List<T9Portal> getPortalByFilters(Connection dbConn,
            String[] filters) throws Exception {
        // 没有传递条件的时候返回空结果
        if (filters == null || filters.length == 0) {
            return null;
        }

        try {
            T9ORM orm = new T9ORM();
            List<T9Portal> list = (List<T9Portal>) orm.loadListSingle(dbConn,
                    T9Portal.class, filters);

            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取用户默认门户 优先级为1:用户设置的默认门户; 2:用户自定义的门户; 3:用户部门的门户; 4:用户角色的门户;
     * 5:用户辅助部门的门户(考虑了多个辅助部门的问题); 6:用户辅助角色的门户(考虑了多个辅助角色的问题)
     * 
     * @param dbConn
     * @param person
     * @return
     * @throws Exception
     */
    public T9Portal getDisplayPortal(Connection dbConn, T9Person person)
            throws Exception {

        String[] filters = new String[] { "SEQ_ID = "
                + person.getDefaultPortal() };
        List<T9Portal> list = this.getPortalByFilters(dbConn, filters);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        filters = new String[] { T9DBUtility.findInSet(
                String.valueOf(person.getSeqId()), "USER_ID") };
        list = this.getPortalByFilters(dbConn, filters);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        filters = new String[] { T9DBUtility.findInSet(
                String.valueOf(person.getDeptId()), "DEPT_ID") };
        list = this.getPortalByFilters(dbConn, filters);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        filters = new String[] { T9DBUtility.findInSet(
                String.valueOf(person.getUserPriv()), "PRIV_ID") };
        list = this.getPortalByFilters(dbConn, filters);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        String deptOther = person.getDeptIdOther();
        Set<String> deptSet = T9PortalAct.string2Set(deptOther);
        String deptStr = findInSet4Collection(deptSet, "DEPT_ID");
        if (!T9Utility.isNullorEmpty(deptStr)) {
            String filter = findInSet4Collection(deptSet, "DEPT_ID");

            if (!T9Utility.isNullorEmpty(filter)) {
                filters = new String[] { filter };
                list = this.getPortalByFilters(dbConn, filters);

                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            }
        }

        String privOther = person.getUserPrivOther();
        Set<String> privSet = T9PortalAct.string2Set(privOther);

        String privStr = findInSet4Collection(privSet, "PRIV_ID");
        if (!T9Utility.isNullorEmpty(privStr)) {
            String filter = findInSet4Collection(privSet, "PRIV_ID");
            if (!T9Utility.isNullorEmpty(filter)) {
                filters = new String[] { filter };
                list = this.getPortalByFilters(dbConn, filters);

                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            }
        }

        return null;
    }

    public T9Portal queryPortal(Connection dbConn, int seqId) throws Exception {
        try {
            T9ORM orm = new T9ORM();
            T9Portal up = (T9Portal) orm.loadObjSingle(dbConn, T9Portal.class,
                    seqId);
            return up;
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    public T9Portal queryPersonalPortal(Connection dbConn, int userId)
            throws Exception {
        try {
            String[] filters = new String[] { T9DBUtility.findInSet(
                    String.valueOf(userId), "USER_ID") };
            List<T9Portal> list = this.getPortalByFilters(dbConn, filters);
            if (list.size() > 0) {
                return list.get(0);
            }

            return null;
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    public void deletePortalPort(Connection dbConn, int id) throws Exception {
        try {
            T9ORM orm = new T9ORM();
            orm.deleteSingle(dbConn, T9PortalPort.class, id);
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

    /**
     * 添加用户的模块(用户显示在桌面上的模块) 暂时没考虑权限的验证
     * 
     * @param dbConn
     * @param up
     * @throws Exception
     */
    public void addPortalPort(Connection dbConn, T9PortalPort up)
            throws Exception {
        try {
            T9ORM orm = new T9ORM();
            orm.saveSingle(dbConn, up);
        } catch (Exception ex) {
            throw ex;
        } finally {

        }
    }

    /**
     * 增加模块的样式(对应用户或者部门角色的某一个模块的具体样式)
     * 
     * @param dbConn
     * @param ps
     * @throws Exception
     */
    public void addPortStyle(Connection dbConn, T9PortStyle ps)
            throws Exception {
        try {
            T9ORM orm = new T9ORM();
            orm.saveSingle(dbConn, ps);
        } catch (Exception ex) {
            throw ex;
        } finally {

        }
    }

    /**
     * 更新模块的样式(对应用户或者部门角色的某一个模块的具体样式)
     * 
     * @param dbConn
     * @param ps
     * @throws Exception
     */
    public void updatePortStyle(Connection dbConn, T9PortStyle portStyle)
            throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "update PORT_STYLE" + " set WIDTH = ?" + ",HEIGHT = ?"
                    + ",POS_X = ?" + ",POS_Y = ?" + " where GUID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, portStyle.getWidth());
            ps.setString(2, portStyle.getHeight());
            ps.setInt(3, portStyle.getPosX());
            ps.setInt(4, portStyle.getPosY());
            ps.setString(5, portStyle.getGuid());
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 更新模块的样式(对应用户或者部门角色的某一个模块的具体样式)
     * 
     * @param dbConn
     * @param ps
     * @throws Exception
     */
    public void updatePortalPort(Connection dbConn, String container,
            int seqId, int sortNo) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "update PORTAL_PORT" + " set CONTAINER = ?"
                    + ",SORT_NO = ?" + " where SEQ_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, container);
            ps.setInt(2, sortNo);
            ps.setInt(3, seqId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 更新模块的样式(对应用户或者部门角色的某一个模块的具体样式)
     * 
     * @param dbConn
     * @param ps
     * @throws Exception
     */
    public void deletePortStyle(Connection dbConn, String guid)
            throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "delete from PORT_STYLE" + " where GUID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, guid);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 更新模块的样式(对应用户或者部门角色的某一个模块的具体样式)
     * 
     * @param dbConn
     * @param ps
     * @throws Exception
     */
    public void updatePortPriv(Connection dbConn, T9Port port) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "update PORT" + " set USER_ID = ?" + ",DEPT_ID = ?"
                    + ",PRIV_ID = ?" + " where SEQ_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, port.getUserId());
            ps.setString(2, port.getDeptId());
            ps.setString(3, port.getPrivId());
            ps.setInt(4, port.getSeqId());
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    public void updatePort(Connection dbConn, T9Port port) throws Exception {
        Statement ps = null;
        try {
            String userId = port.getUserId();
            if (userId == null) {
                userId = "";
            }
            String deptId = port.getDeptId();
            if (deptId == null) {
                deptId = "";
            }

            String privId = port.getPrivId();
            if (privId == null) {
                privId = "";
            }
            String sql = "update PORT" + " set FILE_NAME = '"
                    + port.getFileName() + "'" + ",USER_ID = '" + userId + "'"
                    + ",DEPT_ID = '" + deptId + "'" + ",PRIV_ID = '" + privId
                    + "'" + " where SEQ_ID = " + port.getSeqId();
            ps = dbConn.createStatement();
            ps.executeUpdate(sql);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 删除用户门户
     * 
     * @param dbConn
     * @param fileName
     * @param userId
     * @throws Exception
     */
    public void delUserPortal(Connection dbConn, int userId) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "delete from USER_PORTAL" + " where USER_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 删除用户门户
     * 
     * @param dbConn
     * @param fileName
     * @param userId
     * @throws Exception
     */
    public void addUserPortal(Connection dbConn, String fileName, int userId)
            throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "insert into USER_PORTAL" + "(PORTAL_ID"
                    + ", USER_ID)"
                    + " select SEQ_ID, ? from PORTAL where FILE_NAME = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, fileName);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    /**
     * 列出所有门户
     * 
     * @param dbConn
     * @param fileName
     * @param userId
     * @throws Exception
     */
    public String listPortal(Connection dbConn) throws Exception {
        Statement ps = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        try {
            String sql = "select * from PORTAL";
            ps = dbConn.createStatement();
            rs = ps.executeQuery(sql);
            sb.append("[");
            int count = 0;
            while (rs.next()) {
                sb.append("{");
                sb.append("id:'" + rs.getString("SEQ_ID") + "'");
                sb.append(",name:'" + rs.getString("PORTAL_NAME") + "'");
                sb.append("},");
                count++;
            }
            if (count > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
        return sb.toString();
    }

    public void deletePortal(Connection dbConn, int id) throws Exception {
        // TODO Auto-generated method stub
        Statement ps = null;
        try {
            String sql = "delete  from PORTAL where SEQ_ID=" + id;
            ps = dbConn.createStatement();
            ps.executeUpdate(sql);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    public void setPortalPriv(Connection dbConn, int id, String role,
            String user, String dept) throws Exception {
        // TODO Auto-generated method stub
        Statement ps = null;
        try {
            String sql = "update  PORTAL set DEPT_ID = '" + dept
                    + "' , PRIV_ID = '" + role + "' ,USER_ID = '" + user
                    + "'  where SEQ_ID=" + id;
            ps = dbConn.createStatement();
            ps.executeUpdate(sql);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    public boolean checkPortalName(Connection dbConn, String name)
            throws Exception {

        if (T9Utility.isNullorEmpty(name)) {
            return false;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select SEQ_ID" + " from PORTAL"
                    + " where PORTAL_NAME = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, name.trim());
            rs = ps.executeQuery();
            return !rs.next();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
    }

    public void setDefaultPortal(Connection dbConn, T9Person person,
            int portalId) throws Exception {
        PreparedStatement ps = null;
        person.setDefaultPortal(portalId);
        try {
            String sql = "update PERSON" + " set DEFAULT_PORTAL = ?"
                    + " where SEQ_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, portalId);
            ps.setInt(2, person.getSeqId());
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    public boolean existPort(Connection dbConn, String fileName)
            throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select count(1) as AMOUNT" + " from PORT"
                    + " where FILE_NAME = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, fileName);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("AMOUNT") > 0;
            }
            return false;
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, null, log);
        }
    }

    public String getPortalPriv(Connection dbConn, int id) throws Exception {
        // TODO Auto-generated method stub
        Statement ps = null;
        ResultSet rs = null;
        String priv = "{}";
        try {
            String sql = "select * from PORTAL where SEQ_ID=" + id;
            ps = dbConn.createStatement();
            rs = ps.executeQuery(sql);
            String deptId = "";
            String userId = "";
            String privId = "";
            if (rs.next()) {
                deptId = rs.getString("DEPT_ID");
                privId = rs.getString("PRIV_ID");
                userId = rs.getString("USER_ID");
                deptId = (deptId == null) ? "" : deptId;
                userId = (userId == null) ? "" : userId;
                privId = (privId == null) ? "" : privId;
            }
            T9DeptLogic deptLogic = new T9DeptLogic();
            String deptDesc = deptLogic.getNameByIdStr(deptId, dbConn);
            T9UserPrivLogic privLogic = new T9UserPrivLogic();
            String roleDesc = "";
            if (!T9Utility.isNullorEmpty(privId)) {
                roleDesc = privLogic.getNameByIdStr(privId, dbConn);
            }
            T9PersonLogic personLogic = new T9PersonLogic();
            String userDesc = personLogic.getNameBySeqIdStr(userId, dbConn);
            priv = "{user:\"" + userId + "\",userDesc:\"" + userDesc
                    + "\",dept:\"" + deptId + "\",deptDesc:\"" + deptDesc
                    + "\",role:\"" + privId + "\",roleDesc:\"" + roleDesc
                    + "\"}";
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, log);
        }
        return priv;
    }

    public String listPorts(Connection dbConn, T9Person user, int id)
            throws Exception {
        String priv = user.getUserPriv();
        String privOther = user.getUserPrivOther();
        int dept = user.getDeptId();
        String deptOther = user.getDeptIdOther();
        T9Portal portal = null;

        if (id >= 0) {
            portal = queryPortal(dbConn, id);
        } else if (id == -2) {
            portal = queryPersonalPortal(dbConn, user.getSeqId());
        } else {
            portal = getDisplayPortal(dbConn, user);
        }

        if (portal == null) {
            return null;
        }
        String path = "/" + T9SysProps.getString(T9SysPropKeys.JSP_ROOT_DIR)
                + "/core/funcs/portal/modules/portals/" + portal.getFileName();

        Set<String> deptSet = T9PortalAct.string2Set(deptOther,
                String.valueOf(dept));
        Set<String> privSet = T9PortalAct.string2Set(privOther,
                String.valueOf(priv));

        List<T9PortalPort> portList = listPortalPort(dbConn, portal.getSeqId());

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (T9PortalPort up : portList) {
            T9Port p = queryPort(dbConn, up.getPortId(), portal.getSeqId(),
                    deptSet, privSet);
            if (p == null) {
                continue;
            }

            T9PortStyle ps = queryStyle(dbConn, up.getStyleId());

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(p.getSeqId()));
            map.put("file", p.getFileName());
            map.put("parentCmp", up.getContainer());
            map.put("sortNo", String.valueOf(up.getSortNo()));
            if (ps != null) {
                map.put("width", ps.getWidth());
                map.put("height", ps.getHeight());
                map.put("posX", ps.getPosX() + "px");
                map.put("posY", ps.getPosY() + "px");
            }
            list.add(map);
        }

        return "{layoutPath:\"" + path + "\",records:" + this.toJson(list)
                + "}";
    }

    public StringBuffer toJson(List<Map<String, String>> list) throws Exception {
        StringBuffer sb = new StringBuffer("[");
        for (Map<String, String> map : list) {
            sb.append(T9FOM.toJson(map));
            sb.append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb;
    }

    public String getPort(Connection dbConn, String seqId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select SEQ_ID, FILE_NAME, VIEW_TYPE, MODULE_POS,TYPE, USER_ID, DEPT_ID, PRIV_ID from PORT order by SEQ_ID where SEQ_ID ="
                + seqId;
        String data = "";
        try {
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            Map<String, String> map = new HashMap<String, String>();
            while (rs.next()) {
                map.put("FILE_NAME",
                        T9Utility.null2Empty(rs.getString("FILE_NAME")));
                map.put("USER_ID",
                        T9Utility.null2Empty(rs.getString("USER_ID")));
                map.put("DEPT_ID",
                        T9Utility.null2Empty(rs.getString("DEPT_ID")));
                map.put("PRIV_ID",
                        T9Utility.null2Empty(rs.getString("PRIV_ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
