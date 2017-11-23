package t9.subsys.inforesouce.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.data.T9Kengine;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.util.T9FileMateConstUtil;

/**
 * 元数据类型
 * @author qwx110
 *
 */
public class T9MateTypeLogic{
/**
 * 查找所有的父类元数据
 * dbConn
 * @return
 */
  public List<T9MateType> findMatas(Connection dbConn,String defType) throws Exception{    
    PreparedStatement ps = null;
    ResultSet rs = null;     
    String sql = "select SEQ_ID, " +
                    		"NUMBER_ID,  " +
                    		"CHNAME, " +
                    		"ENNAME, " +
                    		"DEFINEE, " +
                    		"AIM, " +
                    		"CONSTRAINTT, " +
                    		"REPEATE, " +
                    		"ELEMENT_TYP, " +
                    		"TYPE_ID, " +
                    		"CODE_ID, " +
                    		"VALUE_RANGE, " +
                    		"DEF_VALUE, " +
                    		"ELEM_ID, " +
                    		"PARENT_ID, " +
    		"NOTE from MATE_TYPE where (PARENT_ID =0 or PARENT_ID is null) and "+T9DBUtility.findInSet(defType, "ELEMENT_TYP")+"  order by NUMBER_ID asc";
  //  T9Out.println(sql);
    List<T9MateType> mates = new ArrayList<T9MateType>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();      
      while(rs.next()){
        T9MateType amate = new T9MateType();
        amate.setSeqId(rs.getInt(1));
        amate.setNumberId(rs.getString(2));
        amate.setcNname(rs.getString(3));
        amate.seteNname(rs.getString(4));
        amate.setDefine(rs.getString(5));
        amate.setAim(rs.getString(6));
        amate.setConstraint(rs.getString(7));
        amate.setRepeat(rs.getString(8));
        amate.setElement_type(rs.getString(9));
        amate.setTypeId(rs.getString(10));
        amate.setCodeId(rs.getString(11));
        amate.setRangeId(rs.getString(12));
        amate.setLessValue(rs.getString(13));
        amate.setElementId(rs.getString(14));
        amate.setParentId(rs.getString(15));
        amate.setNote(rs.getString(16));
        mates.add(amate);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return mates;
  }
  
  /**
   *查找seqId下的所有的子元素
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public List<T9MateType> findSubMatas(Connection dbConn, int seqId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;     
    String sql = "select SEQ_ID, " +
                        "NUMBER_ID,  " +
                        "CHNAME, " +
                        "ENNAME, " +
                        "DEFINEE, " +
                        "AIM, " +
                        "CONSTRAINTT, " +
                        "REPEATE, " +
                        "ELEMENT_TYP, " +
                        "TYPE_ID, " +
                        "CODE_ID, " +
                        "VALUE_RANGE, " +
                        "DEF_VALUE, " +
                        "ELEM_ID, " +
                        "PARENT_ID, " +
        "NOTE from MATE_TYPE where PARENT_ID =" + seqId +" order by NUMBER_ID asc";
    //T9Out.println(sql);
    List<T9MateType> mates = new ArrayList<T9MateType>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();      
      while(rs.next()){
        T9MateType amate = new T9MateType();
        amate.setSeqId(rs.getInt(1));
        amate.setNumberId(rs.getString(2));
        amate.setcNname(rs.getString(3));
        amate.seteNname(rs.getString(4));
        amate.setDefine(rs.getString(5));
        amate.setAim(rs.getString(6));
        amate.setConstraint(rs.getString(7));
        amate.setRepeat(rs.getString(8));
        amate.setElement_type(rs.getString(9));
        amate.setTypeId(rs.getString(10));
        amate.setCodeId(rs.getString(11));
        amate.setRangeId(rs.getString(12));
        amate.setLessValue(rs.getString(13));
        amate.setElementId(rs.getString(14));
        amate.setParentId(rs.getString(15));
        amate.setNote(rs.getString(16));
        mates.add(amate);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return mates;
  }
  
  /**
   * 查找某一个元数据
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9MateType findAMateType(Connection dbConn, int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select SEQ_ID, " +
                        "NUMBER_ID,  " +
                        "CHNAME, " +
                        "ENNAME, " +
                        "DEFINEE, " +
                        "AIM, " +
                        "CONSTRAINTT, " +
                        "REPEATE, " +
                        "ELEMENT_TYP, " +
                        "TYPE_ID, " +
                        "CODE_ID, " +
                        "VALUE_RANGE, " +
                        "DEF_VALUE, " +
                        "ELEM_ID, " +
                        "PARENT_ID, " +
                    "NOTE from MATE_TYPE where SEQ_ID=" + seqId;
          //T9Out.println(sql);
          T9MateType amate = new T9MateType();
          try{
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();      
            if(rs.next()){             
              amate.setSeqId(rs.getInt(1));
              amate.setNumberId(rs.getString(2));
              amate.setcNname(rs.getString(3));
              amate.seteNname(rs.getString(4));
              amate.setDefine(rs.getString(5));
              amate.setAim(rs.getString(6));
              amate.setConstraint(rs.getString(7));
              amate.setRepeat(rs.getString(8));
              amate.setElement_type(rs.getString(9));
              amate.setTypeId(rs.getString(10));
              amate.setCodeId(rs.getString(11));
              amate.setRangeId(rs.getString(12));
              amate.setLessValue(rs.getString(13));
              amate.setElementId(rs.getString(14));
              amate.setParentId(rs.getString(15));
              amate.setNote(rs.getString(16));             
            }
          }catch(Exception e){
            throw e;
          }finally{
            T9DBUtility.close(ps, rs, null);
          }
    return amate;
  }
  
  public void updateAmate(Connection dbConn, T9MateType mate) throws Exception{
    PreparedStatement ps = null;
    String sql = "update MATE_TYPE " +    		               
                        " set NUMBER_ID=?,  " +
                        "CHNAME=?, " +
                        "ENNAME=?, " +
                        "DEFINEE=?, " +
                        "AIM=?, " +
                        "CONSTRAINTT=?, " +
                        "REPEATE=?, " +
                        "TYPE_ID=?, " +
                        "VALUE_RANGE=?, " +
                        "DEF_VALUE=?, " +
                        "ELEM_ID=?, " +
                        "NOTE=?, " +
                        "ELEMENT_TYP=? " +
                    "where SEQ_ID=" + mate.getSeqId();
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, mate.getNumberId());
      ps.setString(2, mate.getcNname());
      ps.setString(3, mate.geteNname());
      ps.setString(4, mate.getDefine());
      ps.setString(5, mate.getAim());
      ps.setString(6, mate.getConstraint());
      ps.setString(7, mate.getRepeat());
      ps.setString(8, mate.getTypeId());
      ps.setString(9, mate.getRangeId());
      ps.setString(10, mate.getLessValue());
      ps.setString(11, mate.getElementId());
      ps.setString(12, mate.getNote());
      ps.setString(13, mate.getElement_type());
      int ok = ps.executeUpdate();
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  /**
   *在删除之前 先查询表中的rang_value值
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String SelectSubMata(Connection dbConn, int seqId ) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
   // int flag =0;
    String flag ="";
    
    String sql = "select value_range from mate_type where seq_id="+seqId;
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
      flag =  rs.getString(1);
      }
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return flag;
  }
  
  
  /**
   *删除主表seqId下的元素
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int deleteSubMata(Connection dbConn, int seqId ) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    int flag =0;   
    String sql="";
    try{//只要父类下有子类或值域，或父类下直接由值域，只要和父类id有关的，都从最小范围开始删除
      String flgs = SelectSubMata(dbConn,seqId);//父类的值域     
        String childs = findSubMateType(dbConn,seqId); //子类的id串
        if(!T9Utility.isNullorEmpty(childs)){//如果有子类，删除子类和值域
           String[] cIds = childs.split(",");
           for(int i=0; i<cIds.length; i++){
             deleteSubMateValue(dbConn, Integer.parseInt(cIds[i]));//b把子类的所有的值域删除
           }
          
           deleteSubMateType(dbConn, childs);             //删除子元素
        }        
        deleteSonMata(dbConn,seqId,flgs);                 //删除值域
      
        sql = "delete from mate_type where seq_id="+seqId;  
      ps = dbConn.prepareStatement(sql);
      flag = ps.executeUpdate();
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return flag;
  }
  /**
   *删除关联表(附表)下的元素
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int deleteSonMata(Connection dbConn, int seqId ,String flgs) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    int flag =0;
    String sql="";
     sql = "delete from mate_value where seq_id in ("+flgs+")";    
    try{
      ps = dbConn.prepareStatement(sql);
      flag = ps.executeUpdate();
      
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return flag;
  } 
  
 /**
  * 删除子类 
  * @param dbConn
  * @param seqIds 子类的id串
  * @return
  * @throws Exception
  */
  public int deleteSubMateType(Connection dbConn, String seqIds)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    int flag =0;
    String sql = "delete from mate_type where seq_id in ("+seqIds+")";
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      flag = ps.executeUpdate();
      
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return flag;
  }
  
  /**
   * 查找父类的所有的子类的id串   * @param dbConn
   * @param pid
   * @return
   * @throws Exception
   */
  public String findSubMateType(Connection dbConn, int pid)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    //String sql = "select seq_id from (select seq_id from mate_type connect by prior seq_id = parent_id start with seq_id=" + pid +") where seq_id !="+pid;
    String sql = "select seq_id from mate_type where parent_id =" + pid;
    //T9Out.println(sql);
    String ids = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        ids += rs.getString(1) +",";
      }
      
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return ids.substring(0, ids.lastIndexOf(",")==-1?0:ids.lastIndexOf(","));
  }
  
  public void deleteSubMateValue(Connection dbConn, int seqId) throws Exception{
    String rangIds = SelectSubMata(dbConn, seqId);   //查找出值域  
    if(rangIds!=null && rangIds!=""&& !rangIds.equals("null")){
    deleteSonMata(dbConn, seqId, rangIds);           //从值域表中删除对应rangIds的记录
  }
  }
  
  /**
   * 查找name的编码
   * @param dbConn
   * @param name
   * @return
   * @throws Exception
   */
  public String findNumberId(Connection dbConn, String name)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;     
    String sql = "select NUMBER_ID from mate_type where CHNAME =?" ;   
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, name.trim());
      rs = ps.executeQuery();
     if(rs.next()){
       return rs.getString("NUMBER_ID");
     }
      
    }catch (Exception e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
   return null;
  }
  
  /**
   * 提取文件的摘要
   * @param dbConn
   * @param name 摘要
   * @param fileId 文件的文件id，不是seq_id
   * @return
   * @throws SQLException
   */
  public int saveAbstract(Connection dbConn, String content, String fileId) throws SQLException{
    PreparedStatement ps = null;
    String sql = "update sign_files set ABSTRACT = ? where FILE_ID = ?";  
    int ok = 0;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, content);
      ps.setString(2, fileId);
      ok = ps.executeUpdate();      
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
    return ok;
 }
  
  /**
   * 取摘要
   * @param dbConn
   * @param content
   * @param fileId
   * @return
   * @throws SQLException
   */
  public String findAbstract(Connection dbConn, String fileId) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select ABSTRACT from sign_files  where FILE_ID = ?";      
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, fileId);
      rs = ps.executeQuery();
      if(rs.next()){
        String zhaiYao = rs.getString("ABSTRACT");
        return T9Utility.encodeSpecial(zhaiYao);
      }        
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return null;
 }  
  /**1111
   * 知识搜索引擎 查找文件id 相对应的seq_id
   * @param dbConn 
   * @param attachmentId
   * @return  1111
   * @throws SQLException
   */
  public int findKengine(Connection dbConn, String attachmentId)throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int seqId = 0;
    int fileId = 0;
    String findSql = "select seq_id, file_id from sign_files where file_id = ?";
    try{
     ps = dbConn.prepareStatement(findSql);
     ps.setString(1, attachmentId); 
     rs = ps.executeQuery();
     if(rs.next()){
        seqId = rs.getInt("seq_id");
     }
    // fileId =  findFileAttrs(dbConn , seqId);
    }catch(SQLException ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return seqId;
  }
  public int findFileAttrs(Connection dbConn, int seqId)throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int seqIds =0;
    String findSql = "select seq_id, file_seq_id from file_attrs01 where file_seq_id =?";
    try{
      ps = dbConn.prepareStatement(findSql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      if(rs.next()){
         seqIds = rs.getInt("seq_id");
      }
      
    }catch(SQLException ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return seqIds;
  }
  public String findName(Connection dbConn, String name)throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int seqIds =0;
    String numberId = null;
    String chName = null;
    String findPersonSql = "select seq_id, number_id, chname from mate_type where chname ='"+ name +"'";
     try{
      ps = dbConn.prepareStatement(findPersonSql);
      rs = ps.executeQuery();
      if(rs.next()){
         seqIds = rs.getInt("seq_id");
         numberId = rs.getString("number_id");
         chName = rs.getString("chname");
      }
      
    }catch(SQLException ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return numberId;
  }
  /**
   * 把相关的人名,地名，组织机构存放到集合中。
   * @param seqId
   * @param userName
   * @param areaName
   * @param org
   * @param subJect
   * @param keyWord
   * @param dbConn
   * @return
   * @throws Exception
   */
  public T9Kengine findString(int seqId,String userName,String areaName,String org,String subJect,String keyWord, Connection dbConn)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int seqIds =0;
    String numberId = null;
    String chName = null;
    List list = new ArrayList();
    T9Kengine ki = new T9Kengine();
    try{
         // 查询出所有的人名，地名..... 组织好串 返回到页面 ok
      if(! T9Utility.isNullorEmpty(userName)){
        String nameString = SeqIdAndString(seqId,userName,dbConn);
        ki.setUserName(nameString);
      }if(! T9Utility.isNullorEmpty(areaName)){
        String areaString = SeqIdAndString(seqId,areaName,dbConn);
        ki.setAreaName(areaString);
      }if(! T9Utility.isNullorEmpty(org)){
        String orgString = SeqIdAndString(seqId,org,dbConn);
        ki.setOrgName(orgString);
      }if(! T9Utility.isNullorEmpty(subJect)){
        String subjectString = SeqIdAndString(seqId,subJect,dbConn);
        ki.setSubJect(subjectString);
      }if(! T9Utility.isNullorEmpty(keyWord)){
        String keyWordString = SeqIdAndString(seqId,keyWord,dbConn);
        ki.setKeyWord(keyWordString);
      }
       
    }catch(SQLException ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return ki;
  }
 /**
  * 查询相关的人名，地名，组织机构名...
  * @param seqId
  * @param fileString
  * @param dbConn
  * @return
  * @throws Exception
  */
 public String SeqIdAndString(int seqId, String fileString,Connection dbConn) throws Exception{
   PreparedStatement ps = null;
   ResultSet rs = null;
   String attrString = null;
   String findSql = null;
   try{
     int number = T9FileMateConstUtil.checkString(fileString);
     String nums = null;
     if(number>100){ // 从file_attrs02 表查询 
       nums = "" + number;
       findSql = "select seq_id, attr_"+number+" from file_attrs02 where file_seq_id ="+seqId;
     }else {//  从file_attrs01 表查询 
       if(number<10){
         nums = "00"+number;
         findSql = "select seq_id, attr_"+nums+" from file_attrs01 where file_seq_id ="+seqId;
       } else{
         nums = "0"+number;
         findSql = "select seq_id, attr_"+nums+" from file_attrs01 where file_seq_id ="+seqId;
       }
     }
     ps = dbConn.prepareStatement(findSql);
     rs = ps.executeQuery();
     while(rs.next()){
       int seqIds = rs.getInt("seq_id");
       attrString = rs.getString("attr_"+nums+"");
      // Map mapTmp = new HashMap();
       //mapTmp.put("seqIds", seqIds);
      // mapTmp.put("attrStr", attrStr);
      // mapTmp.put(seqIds, attrStr);
       //list.add(mapTmp);
     }
   }catch(Exception ex){
     throw ex;
   }finally{
     T9DBUtility.close(ps, rs, null);
   }
    
    return attrString;
  }
   /**
    * 查询摘要内容
    * @param fileId
    * @param dbConn
    * @return
    * @throws Exception
    */
  public String findzhaiYao(String fileId,Connection dbConn)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int seqId = 0;
    String abstarct = null;
    String findSql ="select SEQ_ID,ABSTRACT from sign_files where FILE_ID = '"+fileId +"'";
    try{
     ps = dbConn.prepareStatement(findSql);
     rs = ps.executeQuery();
     if(rs.next()){
       seqId = rs.getInt("SEQ_ID");
       abstarct = rs.getString("ABSTRACT");
     }
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    if(abstarct == null){
      abstarct = "";
    }
    return T9Utility.encodeSpecial(abstarct);
  }
 
}
