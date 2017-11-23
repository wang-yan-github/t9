package t9.project.file.logic;

import java.sql.Connection;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.project.file.data.T9ProjFileLog;

public class T9ProjFileLogLogic {

  public void addLog(Connection con,T9ProjFileLog fileLog) throws Exception {
//    System.out.println("run addLog");
    T9ORM t9orm = new T9ORM();
    t9orm.saveSingle(con, fileLog);
  }
  public String getTree(Connection con,String fileId) throws Exception {
    T9ORM t9orm = new T9ORM();
    String data="[";
    String[] filters = {" 1=1 and file_id="+fileId+" order by action_time"};
    List<T9ProjFileLog> list = t9orm.loadListSingle(con, T9ProjFileLog.class, filters);
    for (T9ProjFileLog projFileLog : list) {
      T9Person person = (T9Person) t9orm.loadObjSingle(con, T9Person.class, projFileLog.getUserId());
      String name=person.getUserName();
      String date=T9Utility.getDateTimeStr(projFileLog.getActionTime()).substring(0,19);
      int action=projFileLog.getAction();
      if(action==0){
        data+="{log:\"" 
          +name
          +" "
          +date
          +" 添加文件"
          +"\"},";
      }else{
        data+="{log:\"" 
          +name
          +" "
          +date
          +" 修改文件"
          +"\"},";
      }
    }
    if(data.length()>1){
    	data=data.substring(0,data.length()-1);
    }
    data+="]";
    return data;
    
  }
}
