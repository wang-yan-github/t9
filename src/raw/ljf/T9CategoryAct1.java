package raw.ljf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class T9CategoryAct1 {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9CategoryAct1");
  
  public String getContent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    List<T9Category> catList = new ArrayList<T9Category>();
    System.out.print("dd");
    T9Category cat1 = new T9Category();
    cat1.setTitle("a");
    cat1.setContent("aaaaaaaa");
    cat1.setImgUrl("/t9/raw/ljf/imgs/1hrms.gif");
    catList.add(cat1);
    
    T9Category cat2 = new T9Category();
    cat2.setTitle("b");
    cat2.setContent("bbbbbbbbb");
    cat2.setImgUrl("/t9/raw/ljf/imgs/1news.gif");
    catList.add(cat2);
    
    T9Category cat3 = new T9Category();
    cat3.setTitle("c");
    cat3.setContent("cccccccc");
    cat3.setImgUrl("/t9/raw/ljf/imgs/asset.gif");
    catList.add(cat3);
    
    T9Category cat4 = new T9Category();
    cat4.setTitle("v");
    cat4.setContent("vvvvvvvvvv");
    cat4.setImgUrl("/t9/raw/ljf/imgs/asset.gif");
    catList.add(cat4);
    
    String title = request.getParameter("title");
    for(Iterator<T9Category> it = catList.iterator(); it.hasNext();) {
      T9Category cat = it.next();
      String title1 = cat.getTitle();
      if(title1.equals(title)) {
        String content = cat.getContent();
        request.setAttribute("content", content);
      }
    }
  
    return "/raw/ljf/jsp/get1.jsp";
  }
}
