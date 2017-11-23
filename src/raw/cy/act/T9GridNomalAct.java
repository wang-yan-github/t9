package raw.cy.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DsTable;
import t9.core.util.db.T9DTJ;
import t9.core.util.db.T9ORM;
import test.core.util.db.TestDbUtil;
public class T9GridNomalAct {
  /**
   * log                                               
   */
  private static Logger log = Logger.getLogger("cy.raw.cy.act.T9GridNomalAct");
  
  public String jsonTest(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    //1.得到tabName,pageNum,pageRows
   response.setCharacterEncoding("UTF-8");
    //2.通过tabName,pageNum,pageRows得到json数据
    String tabNo = request.getParameter("tabNo");
    String pageNumStr = request.getParameter("pageNum");
    String pageRowsStr = request.getParameter("pageRows");
    int pageNum = Integer.parseInt(pageNumStr);
    int pageRows = Integer.parseInt(pageRowsStr);
    System.out.println(pageNum);
    T9DTJ dtj = new T9DTJ();
    try {
      T9ORM t = new T9ORM();
      System.out.println("ddddd");
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      System.out.println("dbConn===========================================:"+dbConn);
      System.out.println(dbConn);
      Map m = new HashMap();
      m.put("tableNo", tabNo);
      String d  = dtj.toJson(dbConn, tabNo, pageNum,pageRows,null);
      //T9DsTable dsTable = (T9DsTable) t.loadObjComplex(dbConn, T9DsTable.class,m );
      System.out.println(d);
      dbConn.close();
      PrintWriter pw = response.getWriter();
      pw.println(d.toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //3.将json数据输出到前端
    return null;
  }

}
