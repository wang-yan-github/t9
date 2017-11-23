package t9.subsys.oa.giftProduct.instock.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.codeclass.data.T9CodeClass;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.subsys.oa.giftProduct.instock.data.T9GiftInstock;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9GiftInstockLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public int addGiftInstock(Connection dbConn,T9GiftInstock giftInstock) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, giftInstock);
    return T9CalendarLogic.getMaSeqId(dbConn, "GIFT_INSTOCK");
  }
  public void updateGiftInstock(Connection dbConn,T9GiftInstock giftInstock) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, giftInstock);
  }
  public T9GiftInstock selectGiftInstockById(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9GiftInstock giftInstock =  (T9GiftInstock) orm.loadObjSingle(dbConn, T9GiftInstock.class, seqId);
    return giftInstock;
  }
  public List<T9GiftInstock> selectGiftInstock(Connection dbConn,String[] str ) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9GiftInstock> giftList = new ArrayList<T9GiftInstock>();
    giftList =  orm.loadListSingle(dbConn, T9GiftInstock.class, str);
    return giftList;
  }
  public List<T9GiftInstock> selectGiftInstock(Connection dbConn,String giftType ) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9GiftInstock> giftList = new ArrayList<T9GiftInstock>();
    Statement stmt = null;
    ResultSet rs = null; 
    //sql = "select gi.SEQ_ID, gi.GIFT_NAME,gi.GIFT_QTY,go.use_gift_qty as USE_GIFT_QTY from gift_instock gi left outer join 
    //"(select sum(trans_qty- trans_flag) as use_gift_qty,gift_id as gift_id  FROM gift_outstock   group by gift_id) go on gi.seq_id = go.gift_id
    try {
      String queryStr = "select SEQ_ID, GIFT_NAME,GIFT_QTY from GIFT_INSTOCK"  ;
      if(!giftType.equals("")){
        queryStr = queryStr + " where GIFT_TYPE = '" + giftType + "' order by GIFT_NAME";
      }
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        T9GiftInstock instock = new T9GiftInstock();
        instock.setSeqId(rs.getInt(1));
        instock.setGiftName(rs.getString(2));
        //System.out.println(rs.getString(2));
        instock.setGiftQty(rs.getInt(3));
        giftList.add(instock);
      }
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
    return giftList;
  }
  public int selectGiftQty(Connection dbConn,int  seqId ) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9GiftInstock> giftList = new ArrayList<T9GiftInstock>();
    Statement stmt = null;
    ResultSet rs = null; 
    int giftQty = 0;
    try {
      String queryStr = "select sum(trans_qty- trans_flag) as gift_qty FROM gift_outstock  WHERE gift_id = " + seqId  ;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if(rs.next()){
        giftQty = rs.getInt("gift_qty");
      }
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
    return giftQty;
  }
  public void delGiftInstockById(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9GiftInstock.class, seqId);
  }
  public void delGiftInstock(Connection dbConn,String seqIds) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    if(seqIds.endsWith(",")){
      seqIds = seqIds.substring(0, seqIds.length()-1);
    }
    try {
      String sql = "delete from GIFT_INSTOCK where SEQ_ID in(" + seqIds +")";
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
  }
  public T9CodeClass getCodeClass(Connection dbConn,int seqId) throws Exception {
    T9CodeClass codeClass = null;
    Statement stmt = null;
    ResultSet rs = null; 
    
    try {
      String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS where SEQ_ID= " + seqId;

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
     
      if(rs.next()){
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt(1));
        codeClass.setClassNo(rs.getString(2));
        codeClass.setSortNo(rs.getString(3));
        codeClass.setClassDesc(rs.getString(4));
        codeClass.setClassLevel(rs.getString(5));
      }
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
    return codeClass;
  }
  public T9CodeItem getCodeItemById(Connection dbConn,String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_CODE from CODE_ITEM where SEQ_ID ="+seqId;

    T9CodeItem codeItem = null;
    try{
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        codeItem = new T9CodeItem();
        codeItem.setSeqId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
      } 
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
   
    return   codeItem;
  }

public void updateCodeItemById(Connection dbConn,String seqId,String classCode,String sortNo,String classDesc) throws Exception {
  PreparedStatement pstmt = null;
  //Statement pstmt = null;
  ResultSet rs = null; 
  T9ORM orm = new T9ORM();
/*  classCode = classCode.replace("'", "''");
  sortNo = sortNo.replace("'", "''");
  classDesc = classDesc.replace("'", "''");*/
  //String sql = "update CODE_ITEM set CLASS_CODE='"+classCode+"',SORT_NO='"+sortNo+"',CLASS_DESC='"+classDesc+"' where SEQ_ID =" + seqId;
  String sql = "update CODE_ITEM set CLASS_CODE=?,SORT_NO=?,CLASS_DESC=? where SEQ_ID =?" ;


  T9CodeItem codeItem = null;
  try{
    //pstmt = dbConn.createStatement();
    pstmt = dbConn.prepareStatement(sql);
   // rs = stmt.executeQuery(queryStr);
    pstmt.setString(1, classCode);
    pstmt.setString(2, sortNo);
    pstmt.setString(3, classDesc);
    pstmt.setString(4, seqId);
   pstmt.executeUpdate();
  }catch(Exception ex) {
    throw ex;
  }finally {
    T9DBUtility.close(pstmt, rs, log);
  }
}
  public List<T9CodeItem> getCodeItem(Connection dbConn,String classNo) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    List<T9CodeItem>   codeList = new ArrayList<T9CodeItem>();
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_CODE from CODE_ITEM where CLASS_NO = (select CLASS_NO from CODE_CLASS where CLASS_NO = '"+classNo+"') order by SORT_NO";

    T9CodeItem codeItem = null;
    try{
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        codeItem = new T9CodeItem();
        codeItem.setSeqId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
        codeList.add(codeItem);
      } 
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
   
    return   codeList;
  }
  public boolean checkCodeClass(Connection dbConn,String calssDesc) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    calssDesc = calssDesc.replace("'", "''");
    List<T9CodeItem>   codeList = new ArrayList<T9CodeItem>();
    String queryStr = "select * from CODE_ITEM where CLASS_DESC = '" + calssDesc + "'";

    T9CodeItem codeItem = null;
    try{
      pstmt = dbConn.prepareStatement(queryStr);
      rs = pstmt.executeQuery(queryStr);
      while(rs.next()){
        return false;
      } 
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
   
    return   true;
  }
  public int addCodeItem(Connection dbConn,String classCode,String classNo,String sortNo,String classDesc) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    List<T9CodeItem>   codeList = new ArrayList<T9CodeItem>();
   T9CodeItem codeItem = null;
    try{
      String queryStr = "insert into CODE_ITEM(CLASS_NO, CLASS_CODE, SORT_NO, CLASS_DESC) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(queryStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classCode);
      pstmt.setString(3, sortNo);
      pstmt.setString(4, classDesc);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
    return T9CalendarLogic.getMaSeqId(dbConn, "CODE_ITEM");
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String giftName,String giftDesc,String giftCode,String giftType) throws Exception{
    giftName = T9DBUtility.escapeLike(giftName);
    giftDesc =T9DBUtility.escapeLike(giftDesc);
    giftCode = T9DBUtility.escapeLike(giftCode);
    String sql = "select gpd.SEQ_ID as SEQ_ID, gpd.GIFT_NAME as GIFT_NAME , c.class_desc as GIFT_TYPE, gpd.GIFT_PRICE as GIFT_PRICE,"
            +"gpd.GIFT_SUPPLIER as GIFT_SUPPLIER,gpd.GIFT_QTY as GIFT_QTY, p2.user_name as GIFT_KEEPER, gpd.DEPT_ID as DEPT_ID,gpd.DEPT_ID_DESC as DEPT_ID_DESC,"
            +"gpd.GIFT_CREATOR as GIFT_CREATOR, gpd.CREATE_DATE as CREATE_DATE,gpd.GIFT_MEMO as GIFT_MEMO from(select g.SEQ_ID as SEQ_ID,g.GIFT_NAME as GIFT_NAME ,g.GIFT_TYPE as GIFT_TYPE,g.GIFT_PRICE as GIFT_PRICE,g.GIFT_SUPPLIER as GIFT_SUPPLIER,"
            +"g.GIFT_QTY as GIFT_QTY, g.gift_keeper as GIFT_KEEPER,g.DEPT_ID as DEPT_ID,d.DEPT_NAME as DEPT_ID_DESC,p.USER_NAME as GIFT_CREATOR,g.CREATE_DATE as CREATE_DATE,"
            +"g.GIFT_MEMO as GIFT_MEMO from GIFT_INSTOCK g left outer join  PERSON p  on g.GIFT_CREATOR =p.SEQ_ID left outer join DEPARTMENT d " 
            +"on g.DEPT_ID =d.SEQ_ID  where 1=1";
    if(!giftName.equals("")){
      sql = sql + " and g.GIFT_NAME like '%"+giftName+"%' "+ T9DBUtility.escapeLike();
    }
    if(!giftType.equals("")){
      sql = sql + " and g.GIFT_TYPE = '"+giftType+"'";
    }
    if(!giftDesc.equals("")){
      sql = sql + " and g.GIFT_DESC like '%"+giftDesc+"%' "+ T9DBUtility.escapeLike();
    }
    if(!giftCode.equals("")){
      sql = sql + " and g.GIFT_CODE like '%"+giftCode+"%' "+ T9DBUtility.escapeLike();
    }
    sql = sql + ") gpd  left outer join code_item c  on c.seq_id=gpd.GIFT_TYPE left OUTER join person p2 ON gpd.GIFT_KEEPER=p2.seq_id  order by gpd.GIFT_NAME";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      /*String sql = "select gpd.SEQ_ID as SEQ_ID, gpd.GIFT_NAME as GIFT_NAME , c.class_desc as GIFT_TYPE, gpd.GIFT_PRICE as GIFT_PRICE,"
            +"gpd.GIFT_SUPPLIER as GIFT_SUPPLIER,gpd.GIFT_QTY as GIFT_QTY, p2.user_name as GIFT_KEEPER, gpd.DEPT_ID as DEPT_ID,"
            +"gpd.GIFT_CREATOR as GIFT_CREATOR, gpd.CREATE_DATE as CREATE_DATE,gpd.GIFT_MEMO as GIFT_MEMO from code_item c right outer join (select g.SEQ_ID as SEQ_ID,g.GIFT_NAME as GIFT_NAME ,g.GIFT_TYPE as GIFT_TYPE,g.GIFT_PRICE as GIFT_PRICE,g.GIFT_SUPPLIER as GIFT_SUPPLIER,"
            +"g.GIFT_QTY as GIFT_QTY, g.gift_keeper as GIFT_KEEPER,d.DEPT_NAME as DEPT_ID,p.USER_NAME as GIFT_CREATOR,g.CREATE_DATE as CREATE_DATE,"
            +"g.GIFT_MEMO as GIFT_MEMO from PERSON p right outer join  GIFT_INSTOCK g on g.GIFT_CREATOR =p.SEQ_ID left outer join DEPARTMENT d " 
            +"on g.DEPT_ID =d.SEQ_ID g.DEPT_ID =d.SEQ_ID  where g.GIFT_TYPE = '"+giftType+"' and g.GIFT_NAME like '%"+giftName+"%' and g.GIFT_CODE='"+giftCode+"' and g.gift_desc like '%"+giftDesc+"%'  ) gpd on c.seq_id=gpd.GIFT_TYPE left OUTER join person p2 ON gpd.GIFT_KEEPER=p2.seq_id  order by gpd.GIFT_NAME";
 */     String dateStr = dateFormat.format(new Date());
      //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  public void updateInstockById(Connection dbConn,String seqId,int qty) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    String sql = "update  GIFT_INSTOCK set GIFT_QTY = GIFT_QTY-"+qty+"  where SEQ_ID ="+seqId;
    T9CodeItem codeItem = null;
    try{
      stmt = dbConn.createStatement();
     // rs = stmt.executeQuery(queryStr);
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void updateInstockByIdBack(Connection dbConn,int seqId,int qty) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    String sql = "update  GIFT_INSTOCK set GIFT_QTY = GIFT_QTY+"+qty+"  where SEQ_ID ="+seqId;
    T9CodeItem codeItem = null;
    try{
      stmt = dbConn.createStatement();
     // rs = stmt.executeQuery(queryStr);
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public List<T9GiftInstock> selectGiftInstockByName(Connection dbConn,String giftName ) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9GiftInstock> giftList = new ArrayList<T9GiftInstock>();
    Statement stmt = null;
    ResultSet rs = null; 
    giftName = T9DBUtility.escapeLike(giftName);
    try {
      String queryStr = "select SEQ_ID, GIFT_NAME,GIFT_QTY from GIFT_INSTOCK"  ;
      if(!giftName.equals("")){
        queryStr = queryStr + " where GIFT_NAME like '%" + giftName + "%' " + T9DBUtility.escapeLike() + " order by GIFT_NAME";
      }
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        T9GiftInstock instock = new T9GiftInstock();
        instock.setSeqId(rs.getInt(1));
        instock.setGiftName(rs.getString(2));
        //System.out.println(rs.getString(2));
        instock.setGiftQty(rs.getInt(3));
        giftList.add(instock);
      }
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
    return giftList;
  }
}
