package raw.cy.act;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;

public class T9GridAct {
  /**
   * log                                               
   */
  private static Logger log = Logger.getLogger("cy.raw.cy.act.T9GridAct");
  
  
  public String jsonTest(HttpServletRequest request,
      HttpServletResponse response) throws Exception {//--
    String pa = request.getParameter("pageNum").trim();
    String json = "";
    System.out.println("sssssssssssssss");
    if(pa.equals("0")){
      System.out.println("pa = 0");
      json = "{\"total\":19,\"records\":[{\"id\":1,\"name\":\"lh\",\"age\":23},{\"id\":2,\"name\":\"cy\",\"age\":23},{\"id\":3,\"name\":\"cc\",\"age\":23},{\"id\":4,\"name\":\"cc\",\"age\":23}]}";
    }
    if(pa.equals("1")){
      System.out.println("pa = 1");
      json = "{\"total\":19,\"records\":[{\"id\":5,\"name\":\"lh\",\"age\":23},{\"id\":6,\"name\":\"cy\",\"age\":23},{\"id\":7,\"name\":\"cc\",\"age\":23},{\"id\":8,\"name\":\"cc\",\"age\":23}]}";
    }
    if(pa.equals("2")){
      System.out.println("pa = 2");
      json = "{\"total\":19,\"records\":[{\"id\":9,\"name\":\"lh\",\"age\":23},{\"id\":10,\"name\":\"cy\",\"age\":23},{\"id\":11,\"name\":\"cc\",\"age\":23},{\"id\":12,\"name\":\"cc\",\"age\":23}]}";
    }
    if(pa.equals("3")){
      System.out.println("pa = 3");
      json = "{\"total\":19,\"records\":[{\"id\":13,\"name\":\"lh\",\"age\":23},{\"id\":14,\"name\":\"cy\",\"age\":23},{\"id\":15,\"name\":\"cc\",\"age\":23},{\"id\":16,\"name\":\"cc\",\"age\":23}]}";
    }
    if(pa.equals("4")){
      System.out.println("pa = 4");
      json = "{\"total\":19,\"records\":[{\"id\":17,\"name\":\"lh\",\"age\":23},{\"id\":18,\"name\":\"cy\",\"age\":23},{\"id\":19,\"name\":\"cc\",\"age\":23}]}";

    }
    PrintWriter pw = response.getWriter();
    System.out.println("ssssssssssssss");
    pw.println(json.trim());
    pw.flush();
    return null;
  }
}
