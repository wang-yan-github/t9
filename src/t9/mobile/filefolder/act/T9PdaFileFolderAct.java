package t9.mobile.filefolder.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.filefolder.logic.T9PdaFileFolderLogic;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaFileFolderAct {

    /**
     * 地址信息类
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getFileFolderList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();
            String ATYPE = request.getParameter("ATYPE");
            String A = request.getParameter("A");
            // String CURRITERMS = request.getParameter("CURRITERMS");
            // /**
            // * 这个先不用了 不知道为什么 oa 的php 中是这样写的 但是 安卓精灵上没有用到该参数
            // */
            // // String FTYPE = request.getParameter("FTYPE");
            // if (CURRITERMS == null || "".equals(CURRITERMS)) {
            // CURRITERMS = "0";
            // }
            // String data = "";
            /**
             * 先判断 ATYPE 若为refreshList 标示动作 获取数据 然后再根据 A参数 判断 是全部取出 还是取更新的 或者取
             * 指定的记录数
             */
            if ("refreshList".equals(ATYPE)) {
                // 这是第一个接口 refreshList
                // ============================ 显示根目录下目录
                // =======================================
                if ("loadList".equals(A)) {
                    // if("personal".equals(FTYPE)){
                    StringBuffer rsData = new StringBuffer("[");
                    /**
                     * 根目录 目录
                     */
                    // and USER_ID='" + person.getSeqId() + "' 
                    String sql1 = "SELECT SORT_NAME,SEQ_ID from file_sort where SORT_TYPE IS NULL AND SORT_PARENT=0 order by SEQ_ID ASC";
                    /**
                     * 目录下的文件
                     */
                    // 
                    String sql2 = "SELECT READERS,SEQ_ID,SUBJECT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME"
                            + " from FILE_CONTENT where SORT_ID=0 order by SEQ_ID desc";

                    T9PdaFileFolderLogic tff = new T9PdaFileFolderLogic();
                    /**
                     * 根目录json 以及 下面的文件 json
                     */
                    List<Map<String,String>> data1 = tff.getFileFolderList(dbConn, sql1);

                    List<Map<String, String>> data2 = tff.getFileList(dbConn, sql2);

                    List<Map<String,String>> data = new ArrayList<Map<String,String>>();
                    data.addAll(data1);
                    data.addAll(data2);
                    /*boolean flag1 = false;
                    if (null != data1 && !"".equals(data1)) {
                        rsData.append(data1);
                        flag1 = true;
                    }
                    if (null != data2 && !"".equals(data2)) {
                        if (flag1) {
                            rsData.append(",");
                        }
                        rsData.append(data2);
                    }
                    rsData.append("]");*/

                    //T9MobileUtility.output(response, rsData.toString());
                    T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                    return null;

                }
            } else if ("getFileContent".equals(ATYPE)) {

                /**
                 * 接收文件内容ID 根据ID获取内容
                 */
                String Q_ID = request.getParameter("Q_ID");
                Q_ID = T9MobileString.showNull(Q_ID, "0");

                T9FileContent fc = null;
                T9PdaFileFolderLogic tfl = new T9PdaFileFolderLogic();
                fc = tfl.getFileContentById(dbConn, Q_ID);

                StringBuffer _data1 = new StringBuffer("[");

                String SUBJECT = fc.getSubject();
                Date SEND_TIME = fc.getSendTime();
                String CONTENT = fc.getContent();
                // int SORT_ID = fc.getSeqId();
                String ATTACHMENT_ID = fc.getAttachmentId();
                String ATTACHMENT_NAME = fc.getAttachmentName();

                String USER_ID = fc.getUserId();
                // (T9Utility.isNumber(USER_ID)){
                // USER_ID = "0";
                // }
                T9FileSort tfs = new T9FileSort();
                if (person.getSeqId() != Integer.parseInt(USER_ID)) {
                    boolean ACCESS_PRIV = false;
                    boolean DOWN_PRIV = false;
                    /**
                     * 这里用的是对象
                     */
                    tfs = tfl.getFileSortById(dbConn, Q_ID);
                    String SHARE_PRIV = tfl.share_priv(Q_ID, person, dbConn);
                    // SHARE_USER=//substr($SHARE_PRIV, 0, strpos($SHARE_PRIV,
                    // "|"));
                    String SHARE_USER = SHARE_PRIV.substring(0, SHARE_PRIV.indexOf("|"));
                    String DOWN_USER = tfs.getDownUser();
                    String MANAGE_USER = tfs.getManageUser();
                    String OWNER = tfs.getOwner();
                    /**
                     * 各种判断权限 。。 暂时用布尔型
                     */
                    Boolean OWNER_PRIV = tfl.check_priv(OWNER, person);

                    ACCESS_PRIV = (person.getSeqId() == Integer.parseInt(USER_ID))
                            || tfl.check_priv(USER_ID, person)
                            || tfl.find_id(SHARE_USER, String.valueOf(person.getSeqId())) || OWNER_PRIV;
                    boolean MANAGE_PRIV = (person.getSeqId() == Integer.parseInt(USER_ID))
                            || tfl.check_priv(MANAGE_USER, person);
                    DOWN_PRIV = MANAGE_PRIV || (person.getSeqId() == Integer.parseInt(USER_ID))
                            || tfl.check_priv(DOWN_USER, person);

                    if (!DOWN_PRIV)
                        return null;
                }// end if
                /**
                 * 判断是否有附件
                 */
                int has_attachment = 0;
                if (T9MobileString.isEmpty(ATTACHMENT_ID) && !T9MobileString.isEmpty(ATTACHMENT_NAME))
                    has_attachment = 1;
                else
                    has_attachment = 0;
                SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                _data1.append("{\"q_id\":\"" + Q_ID + "\"," + "\"subject\":\"" + SUBJECT + "\","
                        + "\"send_time\":\"" + sf.format(SEND_TIME) + "\"," + "\"content\":\"" + CONTENT
                        + "\"," + "\"has_attachment\":\"" + has_attachment + "\"," + "\"attachment_id\":\""
                        + T9Utility.null2Empty(ATTACHMENT_ID) + "\"," + "\"attachment_name\":\""
                        + T9Utility.null2Empty(ATTACHMENT_NAME) + "\"}");

                _data1.append("]");
                //T9MobileUtility.output(response, _data1.toString());
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, _data1.toString()));
                return null;
            } else if ("getListContent".equals(ATYPE)) {
                /**
                 * 这是个人的
                 */
                String SORT_ID = request.getParameter("Q_ID");
                SORT_ID = T9MobileString.showObjNull(SORT_ID, "0");

                /*StringBuffer sortsb = new StringBuffer("[");*/

                T9PdaFileFolderLogic tfl = new T9PdaFileFolderLogic();

                String _sqlCount = "SELECT count(*) FROM FILE_CONTENT WHERE SEQ_ID='" + SORT_ID + "'";
                int TOTAL_ITEMS_PUBLIC = T9MobileUtility.resultCount(dbConn, _sqlCount);// 计算数量
                
                // SORT_TYPE = 4 and USER_ID='"
                // + person.getSeqId() + "' and
                String _countSql2 = "SELECT count(*) from file_sort where SORT_PARENT='" + SORT_ID + "' order by SEQ_ID ASC";
                int TOTAL_ITEMS_PUBLIC_1 = T9MobileUtility.resultCount(dbConn, _countSql2);

                int TOTAL_ITEMS_PUBLIC_2 = TOTAL_ITEMS_PUBLIC_1 + TOTAL_ITEMS_PUBLIC;

                if (TOTAL_ITEMS_PUBLIC_2 < 1) {
                    //T9MobileUtility.output(response, "NOMOREDATA");
                    T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没有数据", null));
                    return null;
                }
                // String temSortParentSql =
                // "SELECT SORT_PARENT from file_sort where SEQ_ID='" + SORT_ID
                // + "' and USER_ID='" + person.getSeqId() + "'";
                String SORT_PARENT = T9MobileUtility.getDateByField(dbConn, "file_sort", "SORT_PARENT",
                        " SEQ_ID='" + SORT_ID + "'"); //+ "' and USER_ID='" + person.getSeqId() + 

                SORT_PARENT = T9MobileString.showObjNull(SORT_PARENT, "");

                boolean temFlag = false;

                //SORT_TYPE = 4 and USER_ID='"
                // + person.getSeqId() + "' and
                String filesortSql = "SELECT SORT_NAME,SEQ_ID from file_sort where SORT_PARENT='" + SORT_ID + "' order by SEQ_ID ASC";

                List <Map<String,String>> sortsb = new ArrayList<Map<String,String>>();
                List<Map<String,String>> temData = tfl.getFileFolderList1(dbConn, filesortSql, SORT_PARENT);
                if (temData != null && temData.size() > 0) {
                    /*sortsb.append(temData);*/
                    temFlag = true;
                    sortsb.addAll(temData);
                }

                String filelistsql = "SELECT READERS,SEQ_ID,SUBJECT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME from FILE_CONTENT where SORT_ID='"
                        + SORT_ID + "' order by SEQ_ID desc";

                if (temFlag) {
                    /*sortsb.append(",");*/
                }
                List<Map<String,String>> f = tfl.getFileContentList1(dbConn, filelistsql, SORT_PARENT);
                if (f != null && f.size() > 0) {
                  sortsb.addAll(f);
                }
                
                /*sortsb.append(tfl.getFileContentList1(dbConn, filelistsql, SORT_PARENT));
                sortsb.append("]");*/

                //T9MobileUtility.output(response, sortsb.toString());
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(sortsb)));
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

}
