package t9.subsys.inforesouce.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.data.T9MateShow;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.data.T9MateValue;
import t9.subsys.inforesouce.util.T9MateUtil;
import t9.subsys.inforesouce.util.T9StringUtil;

public class T9MataTreeLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.logic.T9WorkLogLogic");
  /**
   * 查询父节点
   */
  public List<T9MateType> findParent(Connection conn,T9Person person,String typemenu)
  throws Exception{

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      String sql ="select seq_id, chname, parent_id, value_range, elem_id,number_id from mate_type where parent_id=0 and "+T9DBUtility.findInSet(typemenu, "ELEMENT_TYP");  
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      List<T9MateType> va = new ArrayList<T9MateType>();
      while(rs.next()){
        T9MateType mv = new T9MateType();
        mv.setSeqId(rs.getInt("seq_id"));
        List<T9MateType> mate2 =  findSon(conn,person,mv.getSeqId(),typemenu);
        mv.setcNname(rs.getString("chname"));
        mv.setParentId(rs.getString("parent_id"));
        mv.setRangeId(rs.getString("value_range"));
        mv.setElementId(rs.getString("elem_id"));
        mv.setNumberId(rs.getString("number_id"));
        va.add(mv);
      }
     return va; 
     // rs = ps.executeQuery();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 查找子元素
   */
  public List<T9MateType> findSon(Connection conn,T9Person person,int sonid,String typemenu)
  throws Exception{

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      //如果传过来的sonid 与mate_typ表中的parent_id字段相等说明是子元素， parent_id为0表示是父元素       
      String sql ="select seq_id, chname, parent_id, value_range, number_id  from mate_type where parent_id="+sonid+" and "+T9DBUtility.findInSet(typemenu, "ELEMENT_TYP");  
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      List<T9MateType> son = new ArrayList<T9MateType>();
      while(rs.next()){
        T9MateType mv = new T9MateType();
        mv.setSeqId(rs.getInt("seq_id")); //子元素的seq_id       
        mv.setcNname(rs.getString("chname"));
        mv.setParentId(rs.getString("parent_id"));
        mv.setRangeId(rs.getString("value_range"));
        mv.setNumberId(rs.getString("number_id"));
        //System.out.println(mv.getRangeId()+":::::");
       // mv.setParentId(rs.getString("value_range"));
      //  if(mv.getParentId()!=null&& !mv.getParentId().equals("null")){
        //  T9MateType mate1 =  valuerang(conn,person,mv.getSeqId(), mv.getParentId());//通过子元素对应的值域
      //    T9Out.println("mate1:::"+mate1);
      //  }
        
       son.add(mv);
      }
     return son; 
     // rs = ps.executeQuery();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  /**
   *  查询值域
   */
  public T9MateType valuerang(Connection conn,T9Person person,int sonid,String parentId)
  throws Exception{

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
       
      String sql ="select seq_id,chname,parent_id,value_range from mate_type where parent_id="+parentId;  
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
     // T9MateType son = new T9MateType();
      T9MateType mv = new T9MateType();
      while(rs.next()){
        mv.setSeqId(rs.getInt("seq_id")); //子元素的seq_id       
        mv.setcNname(rs.getString("chname"));
        mv.setParentId(rs.getString("parent_id"));
        mv.setRangeId(rs.getString("value_range"));
        T9MateValue mate =  sonDate(conn,person,Integer.parseInt(mv.getRangeId()));
        }
     return mv; 
     // rs = ps.executeQuery();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 查询值域 相对应的 父元素下的子元素ID  
   * @param seqId
   * @param conn
   * @param person
   * @return
   * @throws Exception
   */
  public T9MateValue sonDate( Connection conn,T9Person person,int parentId)
  throws Exception{

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
       
      String sql = "select seq_id, value_name from mate_value where seq_id ="+parentId;  
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
     // T9MateType son = new T9MateType();
      T9MateValue mv = new T9MateValue();
      while(rs.next()){
        mv.setSeqId(rs.getInt("seq_id")); //子元素的seq_id       
        mv.setValueName(rs.getString("value_name"));
        }
     return mv; 
     // rs = ps.executeQuery();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 查询 设置权限表 mate_show的父节点
   */
  public List<T9MateShow> findMateShow(Connection conn,T9Person person)
  throws Exception{
    
    Statement stmt = null;
    ResultSet rs = null;
   
    T9MateShow sh = new T9MateShow();
    //List<Map> list = new ArrayList();
    try{
      stmt = conn.createStatement();
      String sql = "select user_id, pr_id, idstr from mate_show where user_id="+person+"";
     rs =  stmt.executeQuery(sql);
     List<T9MateShow> all = new ArrayList<T9MateShow>();
     while(rs.next()){
       sh.setUSER_ID((rs.getInt("user_id")));
       sh.setPR_ID(rs.getString("pr_id"));
       sh.setIDSTR(rs.getString("idstr"));
       all.add(sh);
     }
   //把父元素Id形式（M1-128_,M21-139_,）过滤成数字类型如:128,139 
    String parentId = T9MateUtil.findParents(sh.getPR_ID());
  //把前缀M1-128_ 过滤掉如（M1-128_V103,M1-128_V104,M21-139_143）剩下 V103, V104,143
   String prentStr = sh.getPR_ID();
   String [] prentNode =  prentStr.split(",");
   for(int i=0 ; i<prentNode.length; i++){
     T9MateUtil.self(sh.getIDSTR(), prentNode[i],String.valueOf(sh.getUSER_ID()));
   }   
    
  //  String findsubs = util.findSub(sh.getIDSTR(),sh.getPR_ID());
  // 此方法判断 是否为值域，还是子元素  。 如果有v的是值域，不带v的是子元素
  //  String subs = util.filterSub(String.valueOf(sh.getUSER_ID()),findsubs, sh.getPR_ID());
      return all;
    }
    catch(Exception e){
    throw e;
    }finally{
    T9DBUtility.close(stmt, null, log);     
    }
  //  return list;
  }
  

  
  
  /**
   * 返回用户选择的树
   * 判断用户设置树，如果没有，直接查询所有； 如果设置了，从mate_show里查询设置条件
   * @param conn
   * @param person
   * @return
   * @throws Exception 
   */
  public List<T9MateType> findSelMenu(String typemenu,Connection conn,T9Person person) throws Exception{
      return findMySelMenu(typemenu,conn, person);
  }
  
    
  
  
  /**
   * 查询我选择的元数据及值域
   * @param conn
   * @param person
   * @return
   * @throws Exception 
   */
  public List<T9MateType> findMySelMenu(String typemenu,Connection conn,T9Person person) throws Exception{
    T9MateShow show = findMyMateShow(conn, person,typemenu);
    String pNode = show.getPR_ID();//我选择的父节点  形式如：M1-128_,M2-129_,M8-135_
    if(pNode!=null && pNode!=""){
    String allNode = show.getIDSTR();//我选择的所有的节点   形式如：M1-128_,M1-128_V103,M1-128_V104,|M2-129_,M2-129_130,
    String[] parsePNode = pNode.split(",");// 开始解析我选择的串
    for(int i=0; i<parsePNode.length; i++){
      T9MateUtil.self(allNode, parsePNode[i], String.valueOf(show.getUSER_ID()));//组装数据
    }
    String pIdNodes = T9MateUtil.findParents(pNode);//把父节点解析为父id串
    List<T9MateType> types = findParentType(typemenu,conn, person, pIdNodes);
    return types;
    } else {
      return null;
    }
    
  }
  
  /**
   * 查找所有的父元素,以及这个父元素下的值域和子元素
   * @param conn
   * @param person
   * @return
   * @throws Exception 
   */
  public List<T9MateType> findParentType(String typemenu,Connection conn,T9Person person,String pIdNode) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    List<T9MateType> pIdlist = new ArrayList<T9MateType>();
    try{
      conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      String [] pId = pIdNode.split(",");
     // T9Out.println(pId);
      for(int i=0; i<pId.length; i++){      
      String sql = "select SEQ_ID,CHNAME,NUMBER_ID,PARENT_ID,VALUE_RANGE from MATE_TYPE where SEQ_ID ="+pId[i]+" and "+T9DBUtility.findInSet(typemenu, "ELEMENT_TYP");
      //T9Out.println(sql);
      stmt = conn.createStatement();
      rs =  stmt.executeQuery(sql);    
      while(rs.next()){
        T9MateType mate = new T9MateType();
        mate.setSeqId(rs.getInt("SEQ_ID"));
        mate.setcNname(rs.getString("CHNAME"));
        mate.setNumberId(rs.getString("NUMBER_ID"));
        mate.setParentId(rs.getString("PARENT_ID"));
        mate.setRangeId(rs.getString("VALUE_RANGE"));
        String key = person.getSeqId()+"_"+mate.getNumberId()+"-"+mate.getSeqId()+"_";//123_M23-139_
        String keyValue = key+"rage";                                                //取值域的key 123_M23-139_rage
        String keySub = key + "sub";                                                 //取子元素           123_M23-139_sub
        //取父 元素下的值域
         if(T9StringUtil.isNotEmpty(mate.getRangeId())){
          String valueRageIds = T9MateUtil.getMateMap().get(keyValue);//通过self方法已经把值域串拼好了
          if(T9StringUtil.isNotEmpty(valueRageIds)){           
            List<T9MateValue> values = findValueName(conn,valueRageIds);
            mate.setValues(values);
          }          
         }          
        //取父元素下的子元素        String subIds = T9MateUtil.getMateMap().get(keySub);           //这个父类下要显示的子元素id串        if(T9StringUtil.isNotEmpty(subIds)){
          List<T9MateType> subTypes = findMySubs(conn, subIds, key);
          mate.setSubs(subTypes);
        }        
        pIdlist.add(mate); 
      }
     }
      return pIdlist;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
 } 
  
  
  /**
   * 从mate_show中查询出我选择的元数据
   * @param conn
   * @param person
   * @return
   * @throws Exception
   */
  public T9MateShow findMyMateShow(Connection conn,T9Person person,String typemenu) throws Exception{ 
    Statement stmt = null;
    ResultSet rs = null;    
    T9MateShow sh = null;   
    try{
      conn.createStatement();
      stmt = conn.createStatement();
      String sql = "select user_id, pr_id, idstr from mate_show where user_id="+person.getSeqId()+" and typeId="+typemenu.trim();
      rs =  stmt.executeQuery(sql);    
     if(rs.next()){
       sh = new T9MateShow();
       sh.setUSER_ID((rs.getInt("user_id")));
       sh.setPR_ID(rs.getString("pr_id"));
       sh.setIDSTR(rs.getString("idstr"));      
     }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    return sh;
  }
  /**
   * 判断用户是否设置过。

   * @param user 当前用户
   * @return <code>true</code> or <code>false</code>
  * @throws Exception 
   */
  public boolean iHaveSave(Connection dbConn, T9Person user, String typemenu) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;   
    String sql = "select user_id from mate_show where user_id =" + user.getSeqId()+" and TYPEID="+typemenu.trim();   
    try{
     ps = dbConn.prepareStatement(sql);
     rs = ps.executeQuery();
      if(rs.next()){
        return true;
      }
   } catch (SQLException e){
    throw e;
   }finally{
     T9DBUtility.close(ps, rs, null);
   }
    return false;
  }
  
  /**
   * 从mate_value表中查找对应valueids的值域名称
   * @param dbConn
   * @param ids
   * @return
   * @throws Exception 
   */
  public List<T9MateValue> findValueName(Connection dbConn, String ids) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String sql = "select SEQ_ID, VALUE_NAME from MATE_VALUE where SEQ_ID in (" + ids +")";//值域串
    //T9Out.println(sql);
    List<T9MateValue> vals = new ArrayList<T9MateValue>();
    try{
     ps = dbConn.prepareStatement(sql);
     rs = ps.executeQuery();     
      while(rs.next()){
        T9MateValue mv = new T9MateValue();
        mv.setSeqId(rs.getInt("SEQ_ID"));
        mv.setValueName(rs.getString("VALUE_NAME"));
        vals.add(mv);
      }
   } catch (SQLException e){
     throw e;
   }finally{
     T9DBUtility.close(ps, rs, null);
   }
    return vals;
  }
  
  /**
   * 查询子元素
   * @param dbConn
   * @param subIds
   * @param pkey 父串
   * @return
   * @throws Exception 
   */
  public List<T9MateType> findMySubs(Connection dbConn, String subIds, String pkey) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String sql = "select SEQ_ID,CHNAME,NUMBER_ID,PARENT_ID,VALUE_RANGE from MATE_TYPE where SEQ_ID in ("+ subIds +")";
    //T9Out.println(sql);
    List<T9MateType> types = new ArrayList<T9MateType>();
    try{
     ps = dbConn.prepareStatement(sql);
     rs = ps.executeQuery();     
      while(rs.next()){
        T9MateType mate = new T9MateType();
        mate.setSeqId(rs.getInt("SEQ_ID"));
        mate.setcNname(rs.getString("CHNAME"));
        mate.setNumberId(rs.getString("NUMBER_ID"));
        mate.setParentId(rs.getString("PARENT_ID"));
        mate.setRangeId((rs.getString("VALUE_RANGE")));
        
        String keyValue = pkey+mate.getSeqId()+"_rage";      //取子元素下的值域key        
        if(T9StringUtil.isNotEmpty(mate.getRangeId())){
          String valueRageIds = T9MateUtil.getMateMap().get(keyValue);
            if(T9StringUtil.isNotEmpty(valueRageIds)){
          List<T9MateValue> values = findValueName(dbConn,valueRageIds);
            
          mate.setValues(values);
         }
        }
        types.add(mate);
      }
   } catch (SQLException e){
     throw e;
   }finally{
     T9DBUtility.close(ps, rs, null);
   }
    return types;
  }
  public static String findParents(String parent){
    if(T9StringUtil.isNotEmpty(parent)){  
      return parent.replaceAll("_", "").replaceAll("[M][0-9]+[-]","");//replaceAll("[M][0-9]+[-]", "");
     }
    return null;
  }
   public static void main(String[] args){
    String test= "M232222-12_,M204-14_,M250-16_";
    test = findParents(test);
    //System.out.println(test);
   /* String subClass="";
    String value = "";
    String test= "M2-12_,M2-12_V23,M2-12_V12,M2-12_34,M2-12_,";
    String findsubs = T9MateUtil.findSub(test, "M2-12_");    
  
     String[] tt = findsubs.split(",");
    for(int i = 0; i<tt.length; i++){
   // System.out.println(tt[i]+":::::::");
    if(tt[i].indexOf("_")==-1){
      if(tt[i].indexOf("V")== -1){   //"如果包含V字，说明是值域"
      //System.out.println("1111");
        subClass += tt[i] +",";
       }else{
         value += tt[i].replace("V", "") +",";
         //System.out.println(value);
       }
     }
          
   }*/
 }
}
