package test.cy.vel;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import t9.rad.velocity.T9CodeUtil;
import t9.rad.velocity.T9velocityUtil;
import t9.rad.velocity.metadata.T9Field;
import t9.rad.velocity.metadata.T9GridField;
import test.core.util.db.TestDbUtil;

public class T9velocityUtilTest {

  @Test
  public void testVelocity() {
    Map request = new HashMap();
    request.put("title", "标记");
    request.put("fileName", "index.jsp");
    request.put("url", "/test/rad/client/nevpage");
    String url = "D:\\project\\t9\\webroot\\t9\\test\\rad\\client\\nevpage";
    String templateName = "index.vm";
    T9velocityUtil.velocity(request, url, templateName, "D:\\project\\t9\\templates\\curd");
    
  }
  @Test
  public void testVelocity2() {
    Map request = new HashMap();
    ArrayList< T9GridField> list = new ArrayList<T9GridField>();
    
    T9GridField gf = new T9GridField("标记sort", "flagSort", "FLAG_SORT", "false", null);
    T9GridField gf2 = new T9GridField("flagSortDesc", "flagSortDesc", "FLAG_SORT_DESC", "false", null);
    T9GridField gf3 = new T9GridField("flagCode", "flagCode", "FLAG_CODE", "false", null);
    T9GridField gf4 = new T9GridField("flagDesc", "flagDesc", "FLAG_DESC", "false", null);
    list.add(gf);
    list.add(gf2);
    list.add(gf3);
    list.add(gf4);
    request.put("list", list);
    request.put("tabNo", "11114");
    request.put("fileName", "list.jsp");
    request.put("url", "/test/rad/client/nevpage");
    String url = "D:\\project\\t9\\webroot\\t9\\test\\rad\\client\\nevpage";
    String templateName = "list.vm";
    T9velocityUtil.velocity(request, url, templateName, "D:\\project\\t9\\templates\\curd");
    
  }
  @Test
  public void testVelocity4() {
    Map request = new HashMap();

    ArrayList< T9Field> list = new ArrayList<T9Field>();
    T9Field f = new T9Field("sdf","天啊",true,"input");
    T9Field f2 = new T9Field("ddddddd","懂啊",true,"input");
    T9Field f3 = new T9Field("gggg","嘿嘿",true,"input");
    T9Field f4 = new T9Field("aaaaaa","不黑额",true,"input");
    T9Field f5 = new T9Field("gggggg","切",true,"input");
    T9Field f6 = new T9Field("ssss","不是交付",false,"input");
    list.add(f);
    list.add(f2);
    list.add(f3);
    list.add(f4);
    list.add(f5);
    list.add(f6);
    request.put("fields", list);
    request.put("classPath", "t9.core.funcs.specialflag.data");
    request.put("fileName", "input.jsp");
    request.put("acturl","/test/cy/T9SpecialFlagAct");
    request.put("url", "/test/rad/client/nevpage");//
    String url = "D:\\project\\t9\\webroot\\t9\\test\\rad\\client\\nevpage";//
    String templateName = "input.vm";
    T9velocityUtil.velocity(request, url, templateName, "D:\\project\\t9\\templates\\curd");
    
  }
  @Test
  public void testVelocity3() {
    Map request = new HashMap();
    request.put("fileName", "T9SpecialFlagAct.java");
    request.put("className", "T9SpecialFlag");//
    request.put("tableName", "specialFlag");//
    request.put("classPath", "t9.core.funcs.specialflag.data");//
    request.put("packageName", "test.cy");
    String url = "D:\\project\\t9\\src\\test\\cy";
    String templateName = "curdact.vm";
    T9velocityUtil.velocity(request, url, templateName, "D:\\project\\t9\\templates\\curd");
  }
  @Test
  public void testStr() {
    String className = "T9SpecialFlag";
    System.out.println(className.substring(2, 3).toLowerCase() + className.substring(3));  
  }
  @Test
  public void testCode() throws Exception {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ArrayList< T9GridField> list2 = new ArrayList<T9GridField>();
    T9GridField gf = new T9GridField("标记sort", "flagSort", "FLAG_SORT", "false", null);
    T9GridField gf2 = new T9GridField("flagSortDesc", "flagSortDesc", "FLAG_SORT_DESC", "false", null);
    T9GridField gf3 = new T9GridField("flagCode", "flagCode", "FLAG_CODE", "false", null);
    T9GridField gf4 = new T9GridField("flagDesc", "flagDesc", "FLAG_DESC", "false", null);
    list2.add(gf);
    list2.add(gf2);
    list2.add(gf3);
    list2.add(gf4);
    ArrayList< T9Field> list = new ArrayList<T9Field>();
    T9Field f = new T9Field("flagSort","标记sort",true,"input");
    T9Field f2 = new T9Field("flagSortDesc","flagSortDesc",true,"input");
    T9Field f3 = new T9Field("flagCode","flagCode",true,"input");
    T9Field f4 = new T9Field("flagDesc","flagDesc",true,"input");
    list.add(f);
    list.add(f2);
    list.add(f3);
    list.add(f4);
    Map request = new HashMap();
    request.put("pojoOutPath", "D:\\project\\t9\\src\\test\\cy\\code");
    request.put("pojoPagckageName", "test.cy.code");
    request.put("pojoTemplateName", "db2JavaCode.vm");
    request.put("pojoTemlateUrl", "D:\\project\\t9\\templates\\db");
    
    request.put("actOutPath", "D:\\project\\t9\\src\\test\\cy\\code\\act");
    request.put("actPackageName", "test.cy.code.act");
    request.put("actTemplateName", "curdact.vm");
    request.put("actTemlateUrl", "D:\\project\\t9\\templates\\curd");
    request.put("actFileNamePre", "");
    
    request.put("pageUrl",  "/test/code");
    request.put("pageOutPath", "D:\\project\\t9\\webroot\\t9\\test\\code");
    request.put("indexFileName", "index");
    request.put("indexTitle",  "标记");
    request.put("indexTemplateName",  "index.vm");
    request.put("pageTemlateUrl", "D:\\project\\t9\\templates\\curd");
    request.put("listFields", list2);
    request.put("listFileName", "list");
    request.put("listTemplateName", "list.vm");
    request.put("inputFields", list);
    request.put("inputFileName", "input");
    request.put("inputTemplateName", "input.vm");
    T9CodeUtil.autoCode(dbConn, "11114", request);
  }
}
