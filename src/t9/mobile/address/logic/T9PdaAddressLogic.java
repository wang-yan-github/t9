package t9.mobile.address.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9PdaAddressLogic {

    public List<Map<String,String>> getList(Connection dbConn, String sql, String cURRITERMS) throws Exception {
        try {
            //StringBuffer data = new StringBuffer("[");
        	List<Map<String,String>> resultlist = new ArrayList<Map<String,String>>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean flag = false;
            int c = T9MobileUtility.getCURRITERMS(cURRITERMS);
            int j = 0;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {

                    if (j < c) {
                        j++;
                        continue;
                    }
                    if (j >= T9MobileConfig.PAGE_SIZE + c)
                        break;

                    /*data.append("{\"q_id\":" + rs.getInt("q_id") + "," + "\"user_name\":\""
                            + rs.getString("USER_NAME") + "\"," + "\"priv_name\":\""
                            + rs.getString("priv_name") + "\"," + "vdept_long_name\":\""
                            + rs.getString("DEPT_ID") + "\"," + "\"sex\":\"" + sSex + "\"},");*/
                    
                    Map resultMap = new HashedMap();
                    resultMap.put("q_id", rs.getObject("Q_ID")); // 唯一标识
                    resultMap.put("user_name", rs.getObject("USER_NAME")); // 用户名
                    resultMap.put("priv_name", rs.getObject("priv_name")); // 角色
                    resultMap.put("dept_id", rs.getObject("DEPT_ID")); // 部门唯一标识
                    resultMap.put("sex", changeSexUtil(rs.getInt("sex"))); // 性别
                    resultMap.put("phone", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("phone")))); // 手机号
                    resultlist.add(resultMap);
                    flag = true;
                    j++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            /*if (flag) {
                resultlist = resultlist.deleteCharAt(resultlist.size() - 1);
            }
            data.append("]");
            return data.toString();*/
            return resultlist;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取psn列表
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getPsnList(Connection dbConn, String sql) throws Exception {
        try {
            //StringBuffer data = new StringBuffer("[");
        	List<Map<String,String>> resultlist = new ArrayList<Map<String,String>>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean flag = false;
            // System.out.println(sql);
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String GROUP_NAME = null;
                    GROUP_NAME = "";
                    GROUP_NAME = getDateByField(dbConn, "ADDRESS_GROUP", "GROUP_NAME",
                            "SEQ_ID = '" + rs.getInt("GROUP_ID") + "'");
                    // if(rs.getInt("GROUP_ID") == 0 || rs.getInt("GROUP_ID") ==
                    // 1){
                    if (rs.getInt("GROUP_ID") == 0) { // zrh 20170222修改
                        GROUP_NAME = "默认";
                    }
                    
                    Map resultMap = new HashedMap();
                    resultMap.put("q_id", rs.getObject("SEQ_ID")); // 唯一标识
                    resultMap.put("group_name", T9Utility.encodeSpecial(T9Utility.null2Empty(GROUP_NAME))); // 分组名称
                    resultMap.put("psn_name", T9Utility.null2Empty(rs.getString("PSN_NAME"))); // 联系人姓名
                    resultMap.put("sex", changeSexUtil(rs.getInt("sex"))); // 联系人性别
                    resultMap.put("dept_long_name", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("DEPT_NAME")))); // 单位长名称
                    resultMap.put("dept_name", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("DEPT_NAME")))); // 单位名称
                    resultMap.put("ministration", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("MINISTRATION")))); // 职务
                    resultMap.put("birthday", changeBirthdayiUtil(rs.getTimestamp("BIRTHDAY"))); // 生日
                    resultMap.put("tel_no_dept", T9Utility.null2Empty(rs.getString("TEL_NO_DEPT"))); // 工作电话
                    resultMap.put("fax_no_dept", T9Utility.null2Empty(rs.getString("FAX_NO_DEPT"))); // 工作传真
                    resultMap.put("tel_no_home", T9Utility.null2Empty(rs.getString("TEL_NO_HOME"))); // 家庭电话
                    resultMap.put("mobil_no", T9Utility.null2Empty(rs.getString("MOBIL_NO"))); // 手机
                    resultMap.put("nick_name", T9Utility.null2Empty(rs.getString("NICK_NAME"))); // 昵称
                    resultMap.put("oicq_no", T9Utility.null2Empty(rs.getString("OICQ_NO"))); // QQ号码
                    resultMap.put("icq_no", T9Utility.null2Empty(rs.getString("ICQ_NO"))); // MSN
                    resultMap.put("email", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("EMAIL")))); // 电子邮件
                    resultMap.put("mate", T9Utility.null2Empty(rs.getString("MATE"))); // 配偶
                    resultMap.put("child", T9Utility.null2Empty(rs.getString("CHILD"))); // 子女
                    resultMap.put("add_dept", T9Utility.null2Empty(rs.getString("ADD_DEPT"))); // 单位地址
                    resultMap.put("post_no_dept", T9Utility.null2Empty(rs.getString("POST_NO_DEPT"))); // 单位邮编
                    resultMap.put("add_home", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ADD_HOME")))); // 家庭住址
                    resultMap.put("post_no_home", T9Utility.null2Empty(rs.getString("POST_NO_HOME"))); // 家庭邮编
                    resultMap.put("notes", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("NOTES")))); // 备注
                    resultlist.add(resultMap);
                    flag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            /*if (flag) {
                data = data.deleteCharAt(data.length() - 1);
            }
            data.append("]");
            return data.toString();*/
            return resultlist;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取详细接口 获取具体的地址信息
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getDetail(Connection dbConn, String sql) throws Exception {
        //StringBuffer data = new StringBuffer("");
    	List<Map<String,String>> resultlist = new ArrayList<Map<String,String>>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean flag = false;
        try {
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String GROUP_NAME = "";
                if (rs.getInt("GROUP_ID") == 0) {
                    GROUP_NAME = "默认";
                } else {
                    GROUP_NAME = getDateByField(dbConn, "ADDRESS_GROUP", "GROUP_NAME",
                            "SEQ_ID = '" + rs.getInt("GROUP_ID") + "'");
                }
                
                Map resultMap = new HashedMap();
                resultMap.put("q_id", rs.getObject("SEQ_ID")); // 唯一标识
                resultMap.put("group_name", T9Utility.encodeSpecial(T9Utility.null2Empty(GROUP_NAME))); // 分组名称
                resultMap.put("psn_name", T9Utility.null2Empty(rs.getString("PSN_NAME"))); // 联系人姓名
                resultMap.put("sex", changeSexUtil(rs.getInt("sex"))); // 联系人性别
                resultMap.put("dept_long_name", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("DEPT_NAME")))); // 单位长名称
                resultMap.put("dept_name", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("DEPT_NAME")))); // 单位名称
                resultMap.put("ministration", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("MINISTRATION")))); // 职务
                resultMap.put("birthday", changeBirthdayiUtil(rs.getTimestamp("BIRTHDAY"))); // 生日
                resultMap.put("tel_no_dept", T9Utility.null2Empty(rs.getString("TEL_NO_DEPT"))); // 工作电话
                resultMap.put("fax_no_dept", T9Utility.null2Empty(rs.getString("FAX_NO_DEPT"))); // 工作传真
                resultMap.put("tel_no_home", T9Utility.null2Empty(rs.getString("TEL_NO_HOME"))); // 家庭电话
                resultMap.put("mobil_no", T9Utility.null2Empty(rs.getString("MOBIL_NO"))); // 手机
                resultMap.put("nick_name", T9Utility.null2Empty(rs.getString("NICK_NAME"))); // 昵称
                resultMap.put("oicq_no", T9Utility.null2Empty(rs.getString("OICQ_NO"))); // QQ号码
                resultMap.put("icq_no", T9Utility.null2Empty(rs.getString("ICQ_NO"))); // MSN
                resultMap.put("email", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("EMAIL")))); // 电子邮件
                resultMap.put("mate", T9Utility.null2Empty(rs.getString("MATE"))); // 配偶
                resultMap.put("child", T9Utility.null2Empty(rs.getString("CHILD"))); // 子女
                resultMap.put("add_dept", T9Utility.null2Empty(rs.getString("ADD_DEPT"))); // 单位地址
                resultMap.put("post_no_dept", T9Utility.null2Empty(rs.getString("POST_NO_DEPT"))); // 单位邮编
                resultMap.put("add_home", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ADD_HOME")))); // 家庭住址
                resultMap.put("post_no_home", T9Utility.null2Empty(rs.getString("POST_NO_HOME"))); // 家庭邮编
                resultMap.put("notes", T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("NOTES")))); // 备注
                resultlist.add(resultMap);
                flag = true;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(ps, rs, null);
        }
        /*
        if (flag) {
            data = data.deleteCharAt(data.length() - 1);
        }
        return data.toString();*/
        return resultlist;
    }

    /**
     * 获取部门ids
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public String getDeptIds(Connection dbConn, String sql) throws Exception {
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String deps = "";
            boolean flag = false;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    deps = deps + "," + rs.getInt("SEQ_ID");
                    flag = true;
                }
                if (flag) {
                    deps = deps.substring(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            return deps;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 取某一表中的某一字段值
     * 
     * @param dbConn
     * @param tableName
     * @param field
     * @return
     * @throws Exception
     */
    public String getDateByField(Connection dbConn, String tableName, String field, String sWhere)
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
     * 0男 1女
     * 
     * @param iSex
     * @return
     */
    private String changeSexUtil(int iSex) {
        if (iSex == 0) {
            return "男";
        } else {
            return "女";
        }
    }
    
    /**
     * 生日日期格式
     * @param iBirthday
     * @return
     */
    private String changeBirthdayiUtil(Timestamp iBirthday) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = "";
        if(!"".equals(iBirthday) && null != iBirthday){
            return sdf.format(iBirthday);
        }else{
        	return birthday;
        }
    }
    
}
