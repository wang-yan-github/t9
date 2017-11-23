package t9.core.funcs.sms.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.sms.data.T9Sms;
import t9.core.funcs.sms.data.T9SmsBody;
import t9.core.util.db.T9ORM;

/**
 * 
 * @author cy 20100209
 *
 */
public class T9Sms2Logic{

  /**
   * 取得通信的历史记录
   * @param conn 数据库连接
   * @param from 发件人Id
   * @param to 收件人Id
   * @param 
   * @return
   */
  public StringBuffer getSmsHistory(Connection conn,int from,int to){
    StringBuffer sb = null;
    try{
      
    } catch (Exception e){
      // TODO: handle exception
    } finally{
      
    }
    return sb ;
  }
  /**
   * 得到所有已发送短信
   * @param conn
   * @param userId
   * @return
   */
  public List<T9Sms> listSendSms(Connection conn , int userId){
    ArrayList<T9Sms> result = null;
    String[] filters = null;
    T9ORM orm = new T9ORM();
    try{
      filters = new String[]{};
      
    } catch (Exception e){
      // TODO: handle exception
    } finally{
      
    }
    return result;
  }
  /**
  * 得到所有已接收送短信
  * @param conn
  * @param userId
  * @return
   * @throws Exception 
  */
 public List<T9Sms> listAcceSms(Connection conn , int userId) throws Exception{
   ArrayList<T9Sms> result = null;
   ArrayList<T9SmsBody> smsBodys = null;
   String[] filters = null;
   ArrayList<T9Sms> tem = null;
   T9ORM orm = new T9ORM();
   try{
     filters = new String[]{" FROM_ID = " + userId};
     smsBodys = (ArrayList<T9SmsBody>) orm.loadListSingle(conn, T9SmsBody.class, filters);
     for (T9SmsBody smsBody : smsBodys){
       tem = (ArrayList<T9Sms>) orm.loadListSingle(conn, T9Sms.class, new String[]{" BODY_SEQ_ID = " + smsBody.getSeqId()});
       if(tem == null){
         continue;
       }
       for (T9Sms t9Sms : tem){
         t9Sms.addSmsBodyList(smsBody);
       }
       result.addAll(tem);
    }
   } catch (Exception e){
     throw e;
   }
   return result;
 }
}
