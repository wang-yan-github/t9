package t9.mobile.user.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.T9Utility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaPersonLogic {

    public List<Map<String, String>> getPerson(Connection conn, String query,
            boolean flag) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        List ls = new ArrayList<String>();
        try {
            if (flag) { // 根据ID获取
                Map<String, String> map = T9QuickQuery.quickQuery(conn, query);
                list.add(map);
            } else {
                list = T9QuickQuery.quickQueryList(conn, query);
            }

            for (Map<String, String> data : list) {
                String Q_ID = data.get("SEQ_ID");
                String USER_NAME = data.get("USER_NAME");
                String NICK_NAME = data.get("NICK_NAME");
                String BYNAME = data.get("BYNAME");
                String DEPT_ID = data.get("DAPE_ID");
                String SEX = data.get("SEX");
                String BIRTHDAY = data.get("BIRTHDAY");
                String TEL_NO_DEPT = data.get("TEL_NO_DEPT");
                String FAX_NO_DEPT = data.get("FAX_NO_DEPT");
                String ADD_HOME = data.get("ADD_HOME");
                String POST_NO_HOME = data.get("POST_NO_HOME");
                String TEL_NO_HOME = data.get("TEL_NO_HOME");
                String MOBIL_NO = data.get("MOBIL_NO");
                String EMAIL = data.get("EMAIL");
                String OICQ = data.get("OICQ");
                String ICQ = data.get("ICQ");
                String MSN = data.get("MSN");

                String Ssex = "";
                if (Integer.parseInt(SEX) == 0) {
                    Ssex = "男";
                } else if (Integer.parseInt(SEX) == 1) {
                    Ssex = "女";
                }

                String iBirthday = "";
                if (!"".equals(BIRTHDAY) && null != BIRTHDAY) {
                    iBirthday = BIRTHDAY
                            .substring(0, BIRTHDAY.lastIndexOf("."));
                }

                Map map = new HashMap();
                map.put("q_id", Q_ID);
                map.put("user_name", T9Utility.encodeSpecial(T9Utility
                        .null2Empty(USER_NAME)));
                map.put("nick_name", T9Utility.encodeSpecial(T9Utility
                        .null2Empty(NICK_NAME)));
                map.put("byname",
                        T9Utility.encodeSpecial(T9Utility.null2Empty(BYNAME)));
                map.put("dept_id", T9Utility.null2Empty(DEPT_ID));
                map.put("sex", Ssex);
                map.put("brithday", iBirthday);
                map.put("tel_no_dept", T9Utility.null2Empty(TEL_NO_DEPT));
                map.put("fax_no_dept", T9Utility.null2Empty(FAX_NO_DEPT));
                map.put("add_home",
                        T9Utility.encodeSpecial(T9Utility.null2Empty(ADD_HOME)));
                map.put("tel_no_home", T9Utility.encodeSpecial(T9Utility
                        .null2Empty(TEL_NO_HOME)));
                map.put("post_no_home", T9Utility.encodeSpecial(T9Utility
                        .null2Empty(POST_NO_HOME)));
                map.put("mobil_no", MOBIL_NO);
                map.put("email",
                        T9Utility.encodeSpecial(T9Utility.null2Empty(EMAIL)));
                map.put("oicq", T9Utility.null2Empty(OICQ));
                map.put("icq", T9Utility.null2Empty(ICQ));
                map.put("msn", T9Utility.null2Empty(MSN));
                ls.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ls;
    }

}
