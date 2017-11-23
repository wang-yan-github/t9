package t9.mobile.user.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaDeptLogic {

    public List<Map<String,String>> getDeptTreeJson(int deptId, Connection conn,
            String[] postDeptArray) throws Exception {
        List<Map<String,String>> sb = new ArrayList<Map<String,String>>();
        Set childDeptId = new HashSet();
        for (int i = 0; i < postDeptArray.length; i++) {
            this.getChildDept(conn, Integer.parseInt(postDeptArray[i]),
                    childDeptId);
        }
        this.getDeptTreeByPostDept(deptId, sb, 0, conn, childDeptId);
        if (sb.size() > 0 && sb.get(sb.size() - 1).equals(',') ) {
            sb.remove(sb.size() - 1);
        }
        return sb;
    }

    public Set getChildDept(Connection conn, int postDept, Set childDeptId)
            throws Exception {
        List<Map> list = new ArrayList();
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT="
                + postDept + " order by DEPT_NO, DEPT_NAME asc";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String deptName = rs.getString("DEPT_NAME");
                int seqId = rs.getInt("SEQ_ID");
                Map map = new HashMap();
                map.put("deptName", deptName);
                map.put("seqId", seqId);
                list.add(map);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        childDeptId.add(postDept);
        for (int i = 0; i < list.size(); i++) {
            Map dp = list.get(i);
            int seqId = (Integer) dp.get("seqId");
            childDeptId.add(seqId);
            this.getChildDept(conn, seqId, childDeptId);
        }
        return childDeptId;
    }

    public void getDeptTreeByPostDept(int deptId, List sb, int level,
            Connection conn, Set childDeptId) throws Exception {
        // 首选分级，然后记录级数，是否为最后一个
        List<Map> list = new ArrayList();
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT="
                + deptId + " order by DEPT_NO ASC, DEPT_NAME asc";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String deptName = rs.getString("DEPT_NAME");
                int seqId = rs.getInt("SEQ_ID");
                Map map = new HashMap();
                map.put("deptName", deptName);
                map.put("seqId", seqId);
                list.add(map);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }

        String sql = "";
        for (int i = 0; i < list.size(); i++) {
            Map dp = list.get(i);
            int seqId = (Integer) dp.get("seqId");
            if (childDeptId.contains(seqId)) {
                String deptName = (String) dp.get("deptName");
                
                /*sb.append("{");
                sb.append("\"q_id\":\""
                        + T9Utility.encodeSpecial(String.valueOf(seqId))
                        + "\",");
                sb.append("\"dept_name\":\""
                        + T9Utility.encodeSpecial(deptName) + "\",");
                sb.append("\"dept_parent\":\"" + deptId + "\",");*/

                sql = "SELECT count(*) from `department` where DEPT_PARENT="
                        + seqId;
                int count = T9QuickQuery.getCount(conn, sql);
                /*sb.append("\"subset\":\"" + count + "\",");*/

                sql = "SELECT SEQ_ID , USER_NAME FROM `person` where DEPT_ID='"
                        + seqId + "'";
                List person = T9QuickQuery.quickQueryList(conn, sql);
                /*sb.append("\"person\":" + T9MobileUtility.list2Json(person));
                sb.append("},");*/
                
                Map<String,String> map = new HashMap<String, String>();
                map.put("q_id", T9Utility.encodeSpecial(T9Utility.null2Empty(String.valueOf(seqId))));
                map.put("dept_name", T9Utility.encodeSpecial(T9Utility.null2Empty(deptName)));
                map.put("dept_parent", T9Utility.encodeSpecial(T9Utility.null2Empty(String.valueOf(deptId))));
                map.put("subset", T9Utility.encodeSpecial(T9Utility.null2Empty(String.valueOf(count))));
                map.put("person", T9MobileUtility.list2Json(person));
                sb.add(map);
                
            }
            this.getDeptTreeByPostDept(seqId, sb, level + 1, conn, childDeptId);
        }
    }

}
