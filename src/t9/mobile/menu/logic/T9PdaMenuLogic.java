package t9.mobile.menu.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaMenuLogic {

    public List<Map<String, String>> getMenu(Connection conn, String query,
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
                String Q_ID = data.get("MENU_ID");
                String NAME = data.get("MENU_NAME");
                String LOCCATION = data.get("MENU_LOCATION");
                String IMAGE = data.get("IMAGE");

                Map map = new HashMap();
                map.put("q_id", T9Utility.null2Empty(Q_ID));
                map.put("name",
                        T9Utility.encodeSpecial(T9Utility.null2Empty(NAME)));
                map.put("location", T9Utility.encodeSpecial(T9Utility
                        .null2Empty(LOCCATION)));
                map.put("image",
                        T9Utility.encodeSpecial(T9Utility.null2Empty(IMAGE)));
                ls.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ls;
    }

    public void addMenu(HttpServletRequest request,
            HttpServletResponse response, Connection conn, String name,
            String location, String image) throws SQLException {
        String sql = "insert into mobile_menu (MENU_NAME,MENU_LOCATION,IMAGE) value(?,?,?)";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, image);
            boolean flag = ps.execute();
            conn.commit();
            if (flag) {
                T9MobileUtility.output(response,
                        T9MobileUtility.getResultJson(1, "添加失败", null));
            } else {
                T9MobileUtility.output(response,
                        T9MobileUtility.getResultJson(1, "添加成功", null));
            }
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            T9DBUtility.close(ps, null, null);
        }
    }

    public void deleteMenu(HttpServletResponse response, Connection conn,
            String q_id) throws SQLException {
        PreparedStatement ps = null;
        String sql = "delete from mobile_menu where MENU_ID='" + q_id + "'";

        try {
            ps = conn.prepareStatement(sql);
            int i = ps.executeUpdate();
            if (i > 0) {
                T9MobileUtility.output(response,
                        T9MobileUtility.getResultJson(1, "删除成功", null));
            } else {
                T9MobileUtility.output(response,
                        T9MobileUtility.getResultJson(1, "删除失败", null));
            }
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
}
