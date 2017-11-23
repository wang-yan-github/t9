package t9.core.funcs.seclog.logic;

import java.sql.Connection;
import java.util.Date;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.seclog.data.T9Seclog;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SecLogUtil {
  
  public static void log(Connection conn,T9Person person,String clientIp,String opType,Object opObject,String opResult,String opDesc)throws Exception{
    try{
      T9ORM orm  = new T9ORM();
      T9Seclog log = new T9Seclog();
      log.setUserSeqId(person.getSeqId()+"");
      log.setUserName(person.getUserName());
      log.setOpType(T9Utility.null2Empty(opType));
      log.setOpDesc(T9Utility.null2Empty(opDesc));
      log.setOpResult(opResult);
      log.setClientIp(clientIp);
      log.setOpObject(T9Utility.null2Empty(opObject.toString()));
      log.setOpTime(new Date());
    orm.saveSingle(conn, log);
    }catch(Exception e){
      e.printStackTrace();
    }
    
  }
}
