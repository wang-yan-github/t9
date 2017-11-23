package t9.core.funcs.jexcel.logic;

import java.util.ArrayList;

import t9.core.data.T9DbRecord;

public class T9ExportLogic {
  
  public ArrayList<T9DbRecord> getDbRecord(){
    ArrayList<T9DbRecord > dbL = new ArrayList<T9DbRecord>();
    for (int i = 0; i < 10; i++) {
      T9DbRecord dbrec = new T9DbRecord();
      dbrec.addField("好", "1" + i);
      dbrec.addField("好1", "1");
      dbrec.addField("好2", "1");
      dbrec.addField("好43", "1");
      dbrec.addField("好4", "1");
      dbrec.addField("好5", "1");
      dbrec.addField("好6", "1");
      dbrec.addField("好7", "1");
      dbL.add(dbrec);
    }
    return dbL;
  }
}
