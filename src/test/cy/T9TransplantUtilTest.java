package test.cy;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hsmf.MAPIMessage;
import org.junit.Test;

import t9.rad.dbexputil.transplant.logic.core.cfg.T9TableMappingCfg;
import t9.rad.dbexputil.transplant.logic.core.data.T9SpecialHandFun;
import t9.rad.dbexputil.transplant.logic.core.glob.T9DBCont;
import t9.rad.dbexputil.transplant.logic.core.praser.T9DBExcute;
import t9.rad.dbexputil.transplant.logic.core.praser.T9DataPraser;
import t9.rad.dbexputil.transplant.logic.core.util.db.T9TransplantUtil;
import t9.rad.dbexputil.transplant.logic.core.util.raw.T9OaTabMapping;

public class T9TransplantUtilTest {

  @Test
  public void testGetDBConn() {
    try {
      Connection oldDb = T9TransplantUtil.getDBConn(false, T9DBCont.oldDb);
      Connection newDb = T9TransplantUtil.getDBConn(false, T9DBCont.newDb);
      
      Map<String, String> colunmns = null;
      Map<String, String> tabNames = T9OaTabMapping.getDiffTableNameMap();
      Set<String> oldTables = tabNames.keySet();
      String path = T9DBCont.path + "TD_OA2\\" ;
      for (String oldTab : oldTables) {
        String newTab = tabNames.get(oldTab);
        colunmns = (Map<String, String>) T9OaTabMapping.getColumns(oldDb,  "TD_OA", oldTab, newDb, "TD_OA2", newTab);
        T9TableMappingCfg.createMappingCfg(oldTab, "TD_OA", 1, newTab, "TD_OA2", 2, colunmns, null, null ,path);
      }
      System.out.println(oldDb);
      System.out.println(newDb);
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  @Test
  public void toTableConfig() {
    try {
      Connection oldDb = T9TransplantUtil.getDBConn(false, T9DBCont.oldDb);
      Connection newDb = T9TransplantUtil.getDBConn(false, T9DBCont.newDb);
      String path = T9DBCont.path + "TD_OA4\\";
      String tabMapCfgxmlpath = T9DBCont.path + "TD_OA4\\cfg\\table\\" ;
      String tabCfgxmlpath = T9DBCont.path + "TD_OA4\\cfg\\tableMapping.xls" ;
      String colMapCfgxmlpath = T9DBCont.path + "TD_OA4\\cfg\\column\\" ;
      String spCfgxmlpath = T9DBCont.path + "TD_OA4\\cfg\\sphandler\\" ;

      Map<String, String> tabNames = T9OaTabMapping.getTableNames(tabCfgxmlpath);
      Map<String, String> colunmns = null;
      Set<String> oldTables = tabNames.keySet();

      for (String oldTab : oldTables) {
        ArrayList<T9SpecialHandFun> refers = T9OaTabMapping.getSpecialMappingForOa(oldTab, spCfgxmlpath);
        System.out.println("成功取得" + oldTab + "表的特殊字段处理函数.");
        String newTab = tabNames.get(oldTab);
        colunmns = (Map<String, String>) T9OaTabMapping.getColumns(colMapCfgxmlpath, oldTab);
        T9TableMappingCfg.createMappingCfg(oldTab, "TD_OA", 1, newTab, "TD_OA4", 2, colunmns, null,refers ,tabMapCfgxmlpath);
      }
      System.out.println("表结构XML文件生成成功!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Test
  public void clearTable() {
    try {
      Connection newDb = T9TransplantUtil.getDBConn(true, T9DBCont.newDb);
      String tabCfgxmlpath = T9DBCont.path + "TD_OA4\\cfg\\tableMapping.xls" ;

      Map<String, String> tabNames = T9OaTabMapping.getTableNames(tabCfgxmlpath);
      Set<String> oldTables = tabNames.keySet();
      for (String oldTab : oldTables) {
        System.out.println("正在清空" + oldTab + "表的数据...");
        String newTab = tabNames.get(oldTab);
        String sql = "delete from " + newTab;
        PreparedStatement ps = null;
        ps = newDb.prepareStatement(sql);
        int count = ps.executeUpdate();
        System.out.println("清空" + oldTab + "表                                                             " + count + " 条数据.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Test
  public void toDataXml(){
    T9DataPraser dp = new T9DataPraser();
    String path = T9DBCont.path + "TD_OA5\\";
    String tabMapCfgxmlpath = path + "cfg\\table\\" ;
    String dataxmlpath = path + "data\\" ;
    File inputXmls = new File(tabMapCfgxmlpath);
    File[] files = inputXmls.listFiles();
    for (File inputXml : files) {
      try {
        dp.toDataXml(inputXml, dataxmlpath);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  @Test
  public void praserData(){
    T9DataPraser dp = new T9DataPraser();
    String path = T9DBCont.path + "TD_OA4\\";
    String dataxmlpath = path + "data\\" ;
    File inputXmls = new File(dataxmlpath);
    File[] files = inputXmls.listFiles();
    for (File inputXml : files) {
      try {
        dp.praserDataXml(inputXml);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
  }
  @Test
  public void SPtable(){
    String[] params = {"diary,COMPRESS_CONTENT,content","Email_body,COMPRESS_CONTENT,content"};
    try {
      T9OaTabMapping.spHtml(params, 2);
      System.out.println("特殊表处理成功!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void test5(){
    try {
      String xlsPath = "D:\\transplant\\TD_OA4\\cfg\\";
      String oldSch = "TD_OA";
      String newSch = "TD_OA4";
      File f = new File(xlsPath);
      if(!f.exists()){
        f.mkdirs();
      }
      Connection oldDb = T9TransplantUtil.getDBConn(false, T9DBCont.oldDb);
      Connection newDb = T9TransplantUtil.getDBConn(false, T9DBCont.newDb);
      T9OaTabMapping.getTableNames(oldDb, oldSch, newDb, newSch, xlsPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  @Test
  public void test6(){
    try {
      String xlsPath = "D:\\transplant\\TD_OA4\\cfg\\tableMapping.xls";
      String createPath = "D:\\transplant\\TD_OA1\\cfg\\column\\";
      String oldSch = "TD_OA";
      String newSch = "TD_OA4";
      File f = new File(xlsPath);
      if(!f.exists()){
        f.mkdirs();
      }
      Connection oldDb = T9TransplantUtil.getDBConn(false, T9DBCont.oldDb);
      Connection newDb = T9TransplantUtil.getDBConn(false, T9DBCont.newDb);
      T9OaTabMapping.getColumns(oldDb, oldSch, newDb, newSch, xlsPath, createPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  @Test
  public void test7(){
    try {
      String xlsPath = "E:\\资料\\数据移植\\需要转换字段\\new\\";
      String createPath = "D:\\transplant\\TD_OA4\\cfg\\sphandler\\";
      File file = new File(xlsPath);
      File[] files = file.listFiles();
      for (File file2 : files) {
        T9OaTabMapping.createFile(file2, createPath);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  @Test
  public void test8(){
    try {
      String createPath = "D:\\transplant\\TD_OA4\\cfg\\sphandler\\";
      File file = new File(createPath);
      File[] files = file.listFiles();
      for (File file2 : files) {
        String tableName = file2.getName().substring(0,file2.getName().lastIndexOf("."));
        T9OaTabMapping.getSpecialMappingForOa(tableName, file2.getPath());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  @Test
  public void test9(){
    try {
      String createPath = "D:\\transplant\\TD_OA4\\cfg\\tableMapping.xls";
   //   File file = new File(createPath);
      Map<String, String> map = T9OaTabMapping.getTableNames(createPath);
      System.out.println(map);
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  @Test
  public void test10(){
    try {
      String createPath = "D:\\transplant\\TD_OA4\\cfg\\column\\";
   //   File file = new File(createPath);
      File file = new File(createPath);
      File[] files = file.listFiles();
      for (File file2 : files) {
        String tableName = file2.getName().substring(0,file2.getName().lastIndexOf("."));
        System.out.println(T9OaTabMapping.getColumns(createPath, tableName));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
   
  }
  
  public static void main(String[] args) {
    T9TransplantUtilTest tsut = new T9TransplantUtilTest();
    tsut.toDataXml();
    tsut.praserData();
    tsut.SPtable();
  }
}
