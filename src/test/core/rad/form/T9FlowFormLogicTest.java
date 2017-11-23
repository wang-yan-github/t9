package test.core.rad.form;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import test.core.util.db.TestDbUtil;

public class T9FlowFormLogicTest{

  public void testSelectFlowFormConnectionIntString(){
    fail("Not yet implemented");
  }

  public void testSelectFlowFormConnectionIntStringArray() throws Exception{
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      e.printStackTrace();
    }
    T9FlowFormLogic ffl = new T9FlowFormLogic();
    Map map = ffl.selectFlowForm(dbConn, 53, new String[]{"FORM_NAME","PRINT_MODEL","PRINT_MODEL_SHORT","DEPT_ID"});
  }

  public void testUpdateFlowForm(){
    fail("Not yet implemented");
  }
  public void testGetBySort(){
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      String s = ffl.getIdBySort(dbConn, 70);
      ArrayList<T9FlowFormType> list = (ArrayList<T9FlowFormType>) ffl.getFlowFormType(dbConn, s);
      System.out.println(list);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
