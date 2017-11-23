package t9.mobile.address.act;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.address.logic.T9PdaAddressLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaAddressAct {

    /**
     * 通讯录列表/详情接口
     * 
     * @param request
           .所有系统成员列表（拥有权限看到的用户）
           ATYPE             refreshList
           A                 loadList
           .获取通讯簿下的联系人（个人通讯簿联系人）
           ATYPE             getPsnList
           A                 loadList
           .获取联系人明细
           ATYPE             getDetail
           A                 loadList
           .模糊查询条件
           ATYPE             getPsnList
           DEPT_NAME         单位名称
           .联系人姓名模糊查询条件
           ATYPE             getPsnList
           PSN_NAME          姓名
     * @param response
           .所有系统成员列表（拥有权限看到的用户）
           q_id             唯一标识
           group_name       分组名称
           psn_name         联系人姓名
           sex              联系人性别
           dept_name        单位名称
           .通讯录列表/详情
           q_id             唯一标识
           group_name       分组名称
           psn_name         联系人姓名
           sex              联系人性别
           dept_long_name   单位长名称
           dept_name        单位名称
           ministration     职务
           birthday         生日
           tel_no_dept      工作电话
           fax_no_dept      工作传真
           mobil_no         手机
           nick_name        昵称
           oicq_no          QQ号码
           icq_no           MSN
           email            电子邮件
           mate             配偶
           child            子女
           add_dept         单位地址
           post_no_dept     单位邮编
           add_home         家庭住址
           post_no_home     家庭邮编
           tel_no_home      家庭电话
           notes            备注
     * @return
           Json
     * @throws Exception
     */
    public String getAddressList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();

            String ATYPE = request.getParameter("ATYPE");
            String A = request.getParameter("A");
            String CURRITERMS = request.getParameter("CURRITERMS");
            if (CURRITERMS == null || "".equals(CURRITERMS)) {
                CURRITERMS = "0";
            }
            List<Map<String,String>> data;
            String sql = "";
            /**
             * 先判断 ATYPE 若为refreshList 标示动作 获取数据 然后再根据 A参数 判断 是全部取出 还是取更新的 或者取
             * 指定的记录数
             */
            if ("refreshList".equals(ATYPE)) {
                // 这是第一个接口 refreshList
                if ("loadList".equals(A)) {
                    // 第一种 loadList 全部取出 带分页
                    sql = "SELECT PERSON.SEQ_ID as q_id,PERSON.MOBIL_NO as PHONE,SEX,AUATAR,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME  from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID !=0 and person.USER_PRIV=USER_PRIV.SEQ_ID and person.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME ";
                    T9PdaAddressLogic addLogic = new T9PdaAddressLogic();
                    data = addLogic.getList(dbConn, sql, "");
                } else if ("getNew".equals(A)) { // 取最新的 这里是没有
                    //T9MobileUtility.output(response, "NONEWDATA");
                    T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没有数据", null));
                    return null;
                } else {
                    // 第三种情况 取从传入的条数开始的 置顶条数
                    sql = "SELECT PERSON.SEQ_ID as q_id,SEX,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID !=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME ";
                    T9PdaAddressLogic addLogic = new T9PdaAddressLogic();
                    data = addLogic.getList(dbConn, sql, "");
                }
                //T9MobileUtility.output(response, data);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                return null;
            } else if ("getPsnList".equals(ATYPE)) {
                // 这是 第二个接口 getPsnList
                StringBuffer sb = new StringBuffer();
                StringBuffer sb1 = new StringBuffer();

                String LOGIN_DEPT_ID = String.valueOf(person.getDeptId());
                String LOGIN_USER_PRIV = person.getUserPriv();
                if (LOGIN_DEPT_ID == null || "null".equals(LOGIN_DEPT_ID)) {
                    LOGIN_DEPT_ID = "";
                }
                if (LOGIN_USER_PRIV == null || "null".equals(LOGIN_USER_PRIV)) {
                    LOGIN_USER_PRIV = "";
                }
                String DEPT_NAME = request.getParameter("DEPT_NAME");
                String PSN_NAME = request.getParameter("PSN_NAME");
                String GROUP_ID_STR = "";
                /**
                 * 拼出 获取部门的sql id语句
                 */
                sb.append("select SEQ_ID from ADDRESS_GROUP where USER_ID='");
                sb.append("" + person.getSeqId() + "'");
                sb.append(" or ((USER_ID = '' or USER_ID IS NULL) AND ("
                        + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRIV_USER") + ")");
                sb.append(" or PRIV_DEPT='ALL_DEPT' or ");
                sb.append("" + T9DBUtility.findInSet(LOGIN_DEPT_ID, "PRIV_DEPT"));
                sb.append(" or ");
                sb.append("" + T9DBUtility.findInSet(LOGIN_USER_PRIV, "PRIV_DEPT") + ")");

                T9PdaAddressLogic addLogic = new T9PdaAddressLogic();
                GROUP_ID_STR = addLogic.getDeptIds(dbConn, sb.toString());

                if (!T9Utility.isNullorEmpty(GROUP_ID_STR)) {
                    GROUP_ID_STR += ",";
                }
                GROUP_ID_STR += "0";
                /**
                 * 获取地址表信息 传入之前的 组ids
                 */
                sb1.append("SELECT * from ADDRESS where GROUP_ID in (" + GROUP_ID_STR + ")");
                if (PSN_NAME != null && !"".equals(PSN_NAME)) {
                    sb1.append(" and PSN_NAME like '%" + PSN_NAME + "%' ");
                }
                if (DEPT_NAME != null && !"".equals(DEPT_NAME)) {
                    sb1.append("  and DEPT_NAME like '%" + DEPT_NAME + "%' ");
                }
                data = addLogic.getPsnList(dbConn, sb1.toString());
                //T9MobileUtility.output(response, data);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                return null;
            } else if ("getDetail".equals(ATYPE)) {
                // 这是第三个接口 getDetail
                String Q_ID = request.getParameter("Q_ID");
                if (Q_ID == null || "".equals(Q_ID)) {
                    Q_ID = "0";
                }
                String _sql = "SELECT * from ADDRESS where SEQ_ID = '" + Q_ID + "'";
                T9PdaAddressLogic addLogic = new T9PdaAddressLogic();
                data = addLogic.getDetail(dbConn, _sql);
                //T9MobileUtility.output(response, data);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

}
