package raw.lh.assistinput;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;

public class T9AssistInputAct {
	private static Logger log = Logger.getLogger("lh.raw.lh.assistinput.T9AssistInputAct");
	  public String assistInput(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    String str = request.getParameter("str");
	    Connection dbConn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    response.setContentType("text/xml");    
	    response.setHeader("Cache-Control", "no-cache");   
	    PrintWriter out = response.getWriter();
	    out.print("<?xml version=\'1.0\' encoding=\'utf-8'?>");
	    out.print("<lis>");
	    try {
	      Class.forName("com.mysql.jdbc.Driver");
	      String url = "jdbc:mysql://localhost:3336/tree?user=root&password=myoa888&useUnicode=true&amp;characterEncoding=utf8";
        dbConn = DriverManager.getConnection(url);
        stmt = dbConn.createStatement();
	      String queryStr = "select * from assistInput where NAME like '"+str+"%' limit 10";
	      rs = stmt.executeQuery(queryStr);
	      int i = 0;
	      while (rs.next()) {
	        out.print("<li>");
	        out.print("<id>"+rs.getInt("id")+"</id>");
          out.print("<name>"+rs.getString("name")+"</name>");
          out.print("</li>");
        }
	      String queryCount = "select count(*)  from assistInput where NAME like '"+str+"%'";
	      rs = stmt.executeQuery(queryCount);
	      if(rs.next()){
	        i = rs.getInt(1);
	      }
	      out.print("<url>"+str+"</url>");
	      out.print("<count>"+i+"</count>");
	      out.print("</lis>");
	      //System.out.print("dd");
	    }catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }finally {
		      T9DBUtility.close(stmt, rs, log);
		  }
	    return null;
	  }
	  
}
