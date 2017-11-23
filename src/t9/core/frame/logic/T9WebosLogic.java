package t9.core.frame.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.interfaces.logic.T9InterFacesLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9WebosLogic {
  private static Logger log = Logger.getLogger("t9.core.frame.act");
  
  /**
   * 获取title
   * @return
   * @throws Exception
   */
  public Map<String, String> getBannerInfo(Connection dbConn) throws Exception {
    T9InterFacesLogic logic = new T9InterFacesLogic();
    String logo = logic.queryWebOSLOGO(dbConn);
    String hideLogo = "1";
    if (!T9Utility.isNullorEmpty(logo)) {
      hideLogo = "0";
    }
    Map<String, String> map = getBannerText(dbConn);
    if (map != null) {
      map.put("hideLogo", hideLogo);
    }
    return map;
  }
  
  private  Map<String, String> getBannerText(Connection dbConn) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      String sql = "select " +
          "BANNER_TEXT" +
          ",BANNER_FONT" +
          " from INTERFACE";
      
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      
      if (rs.next()) {
        Map<String, String> map = new HashMap<String, String>();
        String bannerText = T9Utility.encodeSpecial(rs.getString("BANNER_TEXT"));
        String bannerFont = T9Utility.encodeSpecial(rs.getString("BANNER_FONT"));
        if (T9Utility.isNullorEmpty(bannerText)) {
          return null;
        }
        map.put("bannerText", bannerText);
        map.put("bannerFont", bannerFont);
        return map;
      }
      return null;
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
}
