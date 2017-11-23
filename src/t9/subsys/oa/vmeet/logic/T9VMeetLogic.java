package t9.subsys.oa.vmeet.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9Sms;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vmeet.data.T9VMeet;

public class T9VMeetLogic {
  
   public void addVMeetInfoLogic(Connection dbConn,T9Person person,String inviteUsers,String content)throws Exception{
     T9ORM orm=new T9ORM();
     
     
     String VMEET=T9DigestUtility.md5Hex((person.getSeqId()+"").getBytes());
     VMEET=VMEET.substring(0, 8);
     SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
     String VT=T9Utility.getCurDateTimeStr(format);
     String VCK=T9DigestUtility.md5Hex(getKeyed_str(VT,"TD_VMEET").getBytes());
     VCK=VCK.substring(0, 4);
     
    String users[]=inviteUsers.split(",");

     T9VMeet meet=new T9VMeet();
     meet.setVmeet(VMEET);
     meet.setVt(VT);
     meet.setVck(VCK);
     meet.setBeginUser(person.getSeqId()+"");
     meet.setInviteUsers(inviteUsers);
     meet.setContent(content);
     String ss=T9Utility.getDateTimeStr(new Date());
     meet.setAddTime(ss);
     orm.saveSingle(dbConn, meet);
     
     String id=this.getMaxSeqId(dbConn);
     for(int i=0;i<users.length;i++){
       String user=users[i];
       if(!"".equals(user)){
           T9SmsBack sb = new T9SmsBack();
           sb.setSmsType("0");
           sb.setContent(content);
           String url="/subsys/oa/vmeet/checkUser.jsp?seqId="+id; 
           sb.setRemindUrl(url);
           sb.setToId(user);
           sb.setFromId(person.getSeqId());
           T9SmsUtil.smsBack(dbConn, sb);
       }
     }
     
   }
   
   
   public void editUsersLogic(Connection dbConn,T9Person person,String inviteUsers,String content,String seqId)throws Exception{
     T9ORM orm=new T9ORM();
     T9VMeet meet=(T9VMeet)orm.loadObjSingle(dbConn, T9VMeet.class, Integer.parseInt(seqId));
     String addUsers=meet.getInviteUsers();
     if(addUsers.endsWith(",")){
       addUsers+=inviteUsers;
     }else{
       addUsers+=","+inviteUsers;
     }
     meet.setInviteUsers(addUsers);
     orm.updateSingle(dbConn, meet);
     String users[]=inviteUsers.split(",");
     String id=seqId;
     for(int i=0;i<users.length;i++){
       String user=users[i];
       if(!"".equals(user)){
           T9SmsBack sb = new T9SmsBack();
           sb.setSmsType("6");
           sb.setContent(content);
           String url="/subsys/oa/vmeet/checkUser.jsp?seqId="+id; 
           sb.setRemindUrl(url);
           sb.setToId(user);
           sb.setFromId(person.getSeqId());
           T9SmsUtil.smsBack(dbConn, sb);
       }
     }
     
   }
   
   public String getMaxSeqId(Connection conn)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
     try{
       String sql=" select max(seq_id) from v_meet ";
       stmt=conn.createStatement();
       rs=stmt.executeQuery(sql);
       if(rs.next()){
         data=rs.getInt(1)+"";
       }
     }catch(Exception ex){
       ex.printStackTrace();
     }finally{
       T9DBUtility.close(stmt, rs, null);
     }
     
     return data;
   }
   
   public String getKeyed_str(String txt ,String encrypt){
     String tmp="";
     encrypt=T9DigestUtility.md5Hex(encrypt.getBytes());
     tmp+=txt+encrypt;
     tmp=T9DigestUtility.md5Hex(tmp.getBytes());
     return tmp;
   }
   
   public String getVMeetPriv(Connection dbConn,T9Person person)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
    try{
      String sql="select * from SYS_PARA where "+T9DBUtility.findInSet("VMEET_NEW_PRIV", "para_name");
     stmt=dbConn.createStatement();
     rs=stmt.executeQuery(sql);
      if(rs.next()){
        data=rs.getString("para_value");
      }
      String hasPriv="0";
      if(!T9Utility.isNullorEmpty(data) && data.indexOf(person.getSeqId()+"")!=-1){
        hasPriv="1";
      }
      data="{id:'"+data+"',name:'"+this.getUserNames(dbConn,data)+"',userPriv:'"+person.getUserPriv()+"',hasPriv:'"+hasPriv+"',curUser:'"+person.getUserName()+"'}";
      
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
     return data;
   }
   
   public void setVMeetPrivLogic(Connection dbConn,String toIds)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     int seqId=0;
    try{
     String sql="select * from SYS_PARA where "+T9DBUtility.findInSet("VMEET_NEW_PRIV", "para_name");
     stmt=dbConn.createStatement();
     rs=stmt.executeQuery(sql);
     T9ORM orm=new T9ORM();
      if(rs.next()){
        seqId=rs.getInt("seq_id");
        T9SysPara sp=(T9SysPara) orm.loadObjComplex(dbConn, T9SysPara.class, seqId);
        sp.setParaValue(toIds);
        orm.updateSingle(dbConn, sp);
      }else{
        T9SysPara sp=new T9SysPara();
        sp.setParaName("VMEET_NEW_PRIV");
        sp.setParaValue(toIds);
        orm.saveSingle(dbConn, sp);
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
   }
   
   
   public String getUserNames(Connection conn,String userIds)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
     if(T9Utility.isNullorEmpty(userIds)){
       return "";
     }
     try{
       String users[]=userIds.split(",");
       for(int i=0;i<users.length;i++){
           String user=users[i];
         if(!T9Utility.isNullorEmpty(user)){
         
           String sql=" select user_name from person where seq_id='"+user+"' ";
           stmt=conn.createStatement();
           rs=stmt.executeQuery(sql);
           if(rs.next()){
             data+=rs.getString("user_name");
             data+=",";
           }
         
         }
       }
     }catch(Exception ex){
       ex.printStackTrace();
     }finally{
       T9DBUtility.close(stmt, rs, null);
     }
     if(data.endsWith(",")){
       data=data.substring(0, data.length()-1);
     }
     return data;
   }
   
   public String getLastBeginMeet(Connection dbConn,T9Person person)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
    try{
     String sql="select * from V_MEET where "+T9DBUtility.findInSet(person.getSeqId()+"", "begin_user") +" order by add_time desc ";
     stmt=dbConn.createStatement();
     rs=stmt.executeQuery(sql);
      while(rs.next()){
        String content=rs.getString("content");
        String add_time=rs.getString("add_time");
        int seqId=rs.getInt("seq_id");
        data+="{";
        data+=" seqId:'"+seqId+"',content:'"+content+"',addTime:'"+add_time+"' ";
        data+="},";
      }
     
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0, data.length()-1);
    }
     return data;
   }
   
   public String getVMeetByIdLogic(Connection dbConn,T9Person person,String seq_id)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
    try{
     String sql="select * from V_MEET where seq_id='"+seq_id+"'";
     stmt=dbConn.createStatement();
     rs=stmt.executeQuery(sql);
      if(rs.next()){
        String vmeet=rs.getString("vmeet");  
        String beginUser=rs.getString("begin_user");
        String role="3";
        if(beginUser.trim().equals(person.getSeqId()+"")){
          role="2";
        }
        data+="{";
        data+=" pass:'1',roomId:'"+vmeet+"',userName:'"+person.getUserName()+"',role:'"+role+"'";
        data+="}";
      }
     
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0, data.length()-1);
    }
    if("".equals(data)){
      data="{pass:'0'}";
    }
     return data;
   }
   
   
   public void deleteVMeet(Connection dbConn,String seqId)throws Exception{
     T9ORM orm=new T9ORM();
     
    try{
     orm.deleteSingle(dbConn, T9VMeet.class, Integer.parseInt(seqId));
     
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
    
    }
   }
  
   
   
   public String getLastInvitedMeet(Connection dbConn,T9Person person)throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
    try{
     String sql="select * from V_MEET where "+T9DBUtility.findInSet(person.getSeqId()+"", "invite_users")+" order by add_time desc ";
     stmt=dbConn.createStatement();
     rs=stmt.executeQuery(sql);
      while(rs.next()){
        String content=rs.getString("content");
        String add_time=rs.getString("add_time");
        int seqId=rs.getInt("seq_id");
        data+="{";
        data+=" seqId:'"+seqId+"',content:'"+content+"',addTime:'"+add_time+"' ";
        data+="},";
      }
     
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0, data.length()-1);
    }
     return data;
   }
   
   
}
