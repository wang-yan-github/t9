package t9.subsys.oa.book.logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.book.data.T9BookInfo;
import t9.subsys.oa.book.data.T9BookManage;
import t9.subsys.oa.book.data.T9BookType;
import t9.subsys.oa.book.data.T9Page;
/**
 * 图书查询
 * @author qwx110
 *
 */
public class T9BookQueryLogic{
  
  
  /**
   * 图书查询
   * @return
   * @throws Exception 
   */
  public List<T9BookInfo> findBooks(Connection dbConn, T9BookInfo book, String orderflag, T9Person user, T9Page page) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    String sql = null;
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select distinct info.SEQ_ID, info.ISBN, info.TYPE_ID, info.DEPT, info.BOOK_NAME, info.AUTHOR, info.PUB_DATE,info.PUB_HOUSE,info.LEND,info.BOOK_NO,info.AREA,info.[OPEN] from book_info info where 1=1";
    }else {
      sql = "select distinct info.SEQ_ID, info.ISBN, info.TYPE_ID, info.DEPT, info.BOOK_NAME, info.AUTHOR, info.PUB_DATE,info.PUB_HOUSE,info.LEND,info.BOOK_NO,info.AREA,info.OPEN from book_info info where 1=1";
    }
    if(book.getTypeId() != 0){
      sql += " and info.TYPE_ID=" + book.getTypeId();
    }
    if(!T9Utility.isNullorEmpty(book.getLend())){
      sql += " and info.LEND =" + book.getLend();
    }
    if(!T9Utility.isNullorEmpty(book.getBookName().trim())){
      sql += " and info.BOOK_NAME like '%" + T9DBUtility.escapeLike(book.getBookName().trim()) +"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getBookNo().trim())){
      sql += " and info.BOOK_NO='"+book.getBookNo()+"'";
    }
    if(!T9Utility.isNullorEmpty(book.getAuthor().trim())){
      sql += " and info.AUTHOR like '%" + T9DBUtility.escapeLike(book.getAuthor().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getIsbn().trim())){
      sql += " and info.ISBN like '%" + T9DBUtility.escapeLike(book.getIsbn().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getPubHouse().trim())){
      sql += " and info.PUB_HOUSE like '%" + T9DBUtility.escapeLike(book.getPubHouse().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getArea().trim())){
      sql += " and info.AREA like '%" + T9DBUtility.escapeLike(book.getArea().trim())+"%'" + T9DBUtility.escapeLike();
    }    
    if(!user.isAdmin()){
      if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
        sql += " and ("+ T9DBUtility.findInSet(user.getDeptId()+"", "info.[OPEN]") + " or info.[OPEN] = '0')";
      }else {
        sql += " and ("+ T9DBUtility.findInSet(user.getDeptId()+"", "info.OPEN") + " or info.OPEN = '0')";
      }
    }
    sql += " order by " + orderflag +" desc";
    // System.out.println(sql);
    
    List<T9BookInfo> books = new ArrayList<T9BookInfo>();
    try{
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); 
      ps.setMaxRows(page.getCurrentPageIndex() * page.getPageSize());//游标用法，预显示最大行数
      rs = ps.executeQuery();
      rs.first();//如指针用法，指向最上面位置
      rs.relative((page.getCurrentPageIndex()-1) * page.getPageSize() -1);//相对于求出每页显示的长度       
      while(rs.next()){
        T9BookInfo bookInfor = new T9BookInfo();
        bookInfor.setSeqId(rs.getInt("SEQ_ID"));
        bookInfor.setIsbn(rs.getString("ISBN"));
        bookInfor.setTypeId(rs.getInt("TYPE_ID"));
        bookInfor.setDept(rs.getInt("DEPT"));
        bookInfor.setBookName(rs.getString("BOOK_NAME"));
        bookInfor.setAuthor(rs.getString("AUTHOR"));
        bookInfor.setPubHouse(rs.getString("PUB_HOUSE"));
        bookInfor.setLend(rs.getString("LEND"));
        bookInfor.setBookNo(rs.getString("BOOK_NO"));
        bookInfor.setArea(rs.getString("AREA"));
        bookInfor.setOpen(rs.getString("OPEN"));
        bookInfor.setDeptName(findADeptName(dbConn, rs.getInt("DEPT")));//所在部门名
        bookInfor.setOpenNames(findDeptNames(dbConn,rs.getString("OPEN")));
        bookInfor.setTypeName(findTypeName(dbConn, rs.getInt("TYPE_ID")));
        bookInfor.setCanDel(isCanDelBook(dbConn,rs.getString("BOOK_NO")));//判断此书是否借出
        books.add(bookInfor);
      }
    } catch (SQLException e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return books;
  }
  public boolean isCanDelBook(Connection dbConn,String bookNo) throws Exception{
	  PreparedStatement ps = null;
	  ResultSet rs = null;
	  List list = new ArrayList();
	  String sql = "select * from book_manage manage where ((manage.book_status=1 and manage.status=1) or (manage.book_status=0 and manage.status=2)) and manage.BOOK_NO ='"+ bookNo + "'";
	 //System.out.println(sql);
	  try{
		ps = dbConn.prepareStatement(sql);
		rs = ps.executeQuery();
		while(rs.next()){
			list.add(rs.getInt(1));
		}
		if(list.size()>0){
			return true;
		}
	  }catch(Exception e){
		  throw e;
	  }finally{
	      T9DBUtility.close(ps, rs, null);
	  }   
	  return false;
  }
  /**
   * 查找做的个数
   * @param dbConn
   * @param book
   * @param orderflag
   * @param user
   * @return
   * @throws Exception
   */
  public int count(Connection dbConn, T9BookInfo book, String orderflag, T9Person user)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select COUNT(*) as count from book_info info where 1=1";
    if(book.getTypeId() != 0){
      sql += " and info.TYPE_ID=" + book.getTypeId();
    }
    if(!T9Utility.isNullorEmpty(book.getLend())){
      sql += " and info.LEND =" + book.getLend();
    }
    if(!T9Utility.isNullorEmpty(book.getBookName().trim())){
      sql += " and info.BOOK_NAME like '%" + T9DBUtility.escapeLike(book.getBookName().trim()) +"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getBookNo().trim())){
      sql += " and info.BOOK_NO='"+book.getBookNo().trim()+"'";
    }
    if(!T9Utility.isNullorEmpty(book.getAuthor().trim())){
      sql += " and info.AUTHOR like '%" + T9DBUtility.escapeLike(book.getAuthor().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getIsbn().trim())){
      sql += " and info.ISBN like '%" + T9DBUtility.escapeLike(book.getIsbn().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getPubHouse().trim())){
      sql += " and info.PUB_HOUSE like '%" + T9DBUtility.escapeLike(book.getPubHouse().trim())+"%'" + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(book.getArea().trim())){
      sql += " and info.AREA like '%" + T9DBUtility.escapeLike(book.getArea().trim())+"%'" + T9DBUtility.escapeLike();
    }    
    /* 此处不需要 以下转化，以上是根据查询条件先查询总数，
     * String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql += " and ('" + user.getDeptId() +"' in ( info.[OPEN] ) or info.[OPEN] = '0')";
    }else {
      sql += " and ('" + user.getDeptId() +"' in ( info.OPEN ) or info.OPEN = '0')";
    }
     
    sql += " order by " + orderflag +" desc"; 
    */
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();      
      if(rs.next()){
        int r=rs.getInt("count");
        return r;
      }
    } catch (SQLException e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return 0;
  }
  
  /**
   * 查找一个图书的详情
   * @param dbConn
   * @param bookId
   * @return
   * @throws Exception
   */
  public T9BookInfo findABook(Connection dbConn, int bookId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select info.SEQ_ID, info.ISBN, info.TYPE_ID, info.DEPT, info.BOOK_NAME, info.AUTHOR, " +
        " info.PUB_DATE,info.PUB_HOUSE,info.LEND,info.BOOK_NO,info.AREA,info.[OPEN] ,info.ATTACHMENT_ID,info.ATTACHMENT_NAME," +
        " info.MEMO, info.BORR_PERSON, info.BRIEF, info.PRICE,info.AMT FROM book_info info " +
        " where info.SEQ_ID =" + bookId;
    }else {
      sql = "select info.SEQ_ID, info.ISBN, info.TYPE_ID, info.DEPT, info.BOOK_NAME, info.AUTHOR, " +
        " info.PUB_DATE,info.PUB_HOUSE,info.LEND,info.BOOK_NO,info.AREA,info.OPEN ,info.ATTACHMENT_ID,info.ATTACHMENT_NAME," +
        " info.MEMO, info.BORR_PERSON, info.BRIEF, info.PRICE,info.AMT FROM book_info info " +
        " where info.SEQ_ID =" + bookId;
    }
    //T9Out.println(sql);
    T9BookInfo book = new T9BookInfo();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();      
      while(rs.next()){
        book.setSeqId(rs.getInt("SEQ_ID"));
        book.setIsbn(rs.getString("ISBN"));
        book.setTypeId(rs.getInt("TYPE_ID"));
        book.setBookName(rs.getString("BOOK_NAME"));
        book.setAuthor(rs.getString("AUTHOR"));
        book.setPubDate(rs.getString("PUB_DATE"));
        book.setPubHouse(rs.getString("PUB_HOUSE"));
        book.setLend(rs.getString("LEND"));
        book.setBookNo(rs.getString("BOOK_NO"));
        book.setOpen(rs.getString("OPEN"));
        book.setDept(rs.getInt("DEPT"));
        book.setMemo(rs.getString("MEMO"));
        book.setBrief(rs.getString("BRIEF"));
        book.setBorrPerson(rs.getString("BORR_PERSON"));
        book.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        book.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        book.setPrice(rs.getDouble("PRICE"));
        book.setAmt(rs.getInt("AMT"));
        book.setDeptName(findADeptName(dbConn, rs.getInt("DEPT")));
        book.setOpenNames(findDeptNames(dbConn,rs.getString("OPEN")));
        book.setTypeName(findTypeName(dbConn, rs.getInt("TYPE_ID")));
      }
    } catch (SQLException e){
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }   
    return book;
  }
  
  /**
   * 返回部门名字串
   * @param dbConn
   * @param deptIds
   * @return
   * @throws Exception
   */
  public String findDeptNames(Connection dbConn, String  deptIds) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select DEPT_NAME from department where SEQ_ID in (" + deptIds +")";
    if(T9Utility.isNullorEmpty(deptIds)){
      deptIds = "-1";
    }
    String dept = "";
    if("0".equalsIgnoreCase(deptIds) || "ALL_DEPT".equalsIgnoreCase(deptIds)){
      dept = "全体部门";
      return dept;
    }
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();  
      while (rs.next()){
        dept += rs.getString("DEPT_NAME") +",";
      }
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    if (dept != null && dept.lastIndexOf(",") > 0) {
     dept = dept.substring(0, dept.lastIndexOf(","));
    }
   return dept;
  }
  
  /**
   * 查找一个部门名
   * @param dbConn
   * @param deptId
   * @return
   * @throws Exception
   */
  public static String findADeptName(Connection dbConn, int  deptId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select DEPT_NAME from department where SEQ_ID ="+ deptId;
    
    String dept = "";
    if(0 == deptId){
      dept = "全体部门";
      return dept;
    }
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();  
      if(rs.next()){
        dept = rs.getString("DEPT_NAME");
      }
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
   return dept;   
  }
  
  /**
   * 待批书籍，已批书籍，未准书籍
   * @return
   * @throws Exception 
   */
  public List<T9BookManage> findBooksNoAllow(Connection dbConn, T9Person user, String status) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select SEQ_ID, BOOK_NO, BORROW_DATE, RETURN_DATE,BUSER_ID, STATUS, BOOK_STATUS,RUSER_ID from book_manage where STATUS = '"+ status +"' and  (DELETE_FLAG!=1 or DELETE_FLAG is null) and BUSER_ID="
               + user.getSeqId() +" order by BORROW_DATE desc"; 
    //T9Out.println(sql);
    List<T9BookManage> managers = new ArrayList<T9BookManage>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        T9BookManage aManage = new T9BookManage();
        aManage.setSeqId(rs.getInt("SEQ_ID"));
        aManage.setBookNo(rs.getString("BOOK_NO"));
        aManage.setBorrowDate(rs.getDate("BORROW_DATE"));
        aManage.setReturnDate(rs.getDate("RETURN_DATE"));
        aManage.setBuserId(rs.getString("BUSER_ID"));
        aManage.setBookStatus(rs.getString("BOOK_STATUS"));
        aManage.setStatus(rs.getString("STATUS"));
        aManage.setBorPersonName(user.getUserName());
        aManage.setBookName(findBookName(dbConn, rs.getString("BOOK_NO")));
        String regName = rs.getString("RUSER_ID");
        if(T9Utility.isNullorEmpty(regName)){
          regName = "-100";
        }
        aManage.setRegUserName(findUserNameBySeqId(dbConn,Integer.parseInt(regName)));
        if(!T9Utility.isNullorEmpty(aManage.getBookName())){
          managers.add(aManage);
        }
       
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return managers;
  }
  
  /**
   * 查找这个书的待批，未还记录
   * @param dbConn
   * @param status
   * @return
   * @throws Exception
   */
  public List<T9BookManage> findBookConditionByBookId(Connection dbConn, String bookNo, int flag) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = " select SEQ_ID, BOOK_NO, BORROW_DATE, RETURN_DATE,BUSER_ID, STATUS, BOOK_STATUS from book_manage where BOOK_NO =?";
    if(flag == 0){
      sql += " and BOOK_STATUS='0' and  STATUS='0'";//待批u
    }else{
      sql += " and ((BOOK_STATUS='0' and STATUS='1') or ( BOOK_STATUS='1' and (STATUS='0' or STATUS='2')))";//未还
    }
    List<T9BookManage> managers = new ArrayList<T9BookManage>();
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, bookNo);
      rs = ps.executeQuery();
      while(rs.next()){
        T9BookManage aManage = new T9BookManage();
        aManage.setSeqId(rs.getInt("SEQ_ID"));
        aManage.setBookNo(rs.getString("BOOK_NO"));
        aManage.setBorrowDate(rs.getDate("BORROW_DATE"));
        aManage.setReturnDate(rs.getDate("RETURN_DATE"));
        aManage.setBuserId(rs.getString("BUSER_ID"));
        aManage.setBookStatus(rs.getString("BOOK_STATUS"));
        aManage.setStatus(rs.getString("STATUS"));   
        aManage.setBorPersonName(findUserNameBySeqId(dbConn, rs.getInt("BUSER_ID")));   
        aManage.setBookName(findBookName(dbConn, rs.getString("BOOK_NO")));
        managers.add(aManage);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return managers;
  }
  
  /**
   * 查找用户名
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String findUserNameBySeqId(Connection dbConn, int  seqId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select USER_NAME from person where SEQ_ID =" + seqId;
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString("USER_NAME");
      }
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return null;
  }
  
  
  /**
   * 查找图书的类别
   * @return
   * @throws SQLException 
   */
  public List<T9BookType> findBookTypes(Connection dbConn) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID, TYPE_NAME from BOOK_TYPE";
    List<T9BookType> types = new ArrayList<T9BookType>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();       
      while(rs.next()){
        T9BookType type = new T9BookType();
        type.setSeqId(rs.getInt("SEQ_ID"));
        type.setTypeName(rs.getString("TYPE_NAME"));
        types.add(type);
      }
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }   
    return types;
  }
  
  
  /**
   * 图书编号--模糊查找（与权限有关）最多显示50条
   * 1.查找用户的部门id
   * 2.查找图书的借阅范围包含部门id
   * @param dbConn
   * @param condition 查询条件
   * @param user 用户
   * @return
   * @throws Exception 
   * @throws SQLException 
   */
  public List<T9BookInfo> findBookNos(Connection dbConn, T9Person user, String condition) throws SQLException, Exception{     

    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select SEQ_ID,BOOK_NO,DEPT,[OPEN],BOOK_NAME from BOOK_INFO   where (" +
      T9DBUtility.findInSet(findDeptId(dbConn, user), "[OPEN]")+
      " or [OPEN] = 'ALL_DEPT' or [OPEN] = '0') and LEND = '0'" ;
    }else {
      sql = "select SEQ_ID,BOOK_NO,DEPT,OPEN,BOOK_NAME from BOOK_INFO   where (" +
      T9DBUtility.findInSet(findDeptId(dbConn, user), "OPEN")+
      " or OPEN = 'ALL_DEPT' or OPEN = '0') and LEND = '0'" ;
    }
    if(!T9Utility.isNullorEmpty(condition)){
       sql += " and ( BOOK_NO like '%" + T9DBUtility.escapeLike(condition) +"%'"+ T9DBUtility.escapeLike();
       sql += " or BOOK_NAME like '%"+ T9DBUtility.escapeLike(condition) +"%'"+ T9DBUtility.escapeLike() +")";
    }
    //T9Out.println(sql);
    List<T9BookInfo>books = new ArrayList<T9BookInfo>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();  
      int cont = 0;
      while(rs.next() && ++cont <50){
        T9BookInfo book = new T9BookInfo();
        book.setSeqId(rs.getInt("SEQ_ID"));
        book.setBookNo(rs.getString("BOOK_NO"));
        book.setDept(rs.getInt("DEPT"));
        book.setOpen(rs.getString("OPEN"));
        book.setBookName(rs.getString("BOOK_NAME"));
        books.add(book);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return books;
  }
  
  /**
   * 查找用户所在部门号
   * @param dbConn
   * @param user
   * @return
   * @throws Exception
   */
  public String findDeptId(Connection dbConn, T9Person user) throws Exception{   
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select DEPT_ID  from person where SEQ_ID = "+ user.getSeqId(); 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString("DEPT_ID");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return "";
  }
  
  /**
   * 查找类型名称
   * @param dbConn
   * @param typeId
   * @return
   * @throws Exception
   */
  public String findTypeName(Connection dbConn, int typeId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select TYPE_NAME from BOOK_TYPE where SEQ_ID= "+ typeId; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString("TYPE_NAME");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return null;
  }
  
  /**
   * 借阅
   * @param dbConn
   * @param typeId
   * @throws Exception
   */
  public int toRead(Connection dbConn, T9BookManage manage, T9Person user)throws Exception{
    PreparedStatement ps = null;   
    String sql = "insert into book_manage(BUSER_ID, BOOK_NO, BORROW_DATE, BORROW_REMARK,RETURN_DATE,BOOK_STATUS,STATUS,REG_FLAG, RUSER_ID ) values(?,?,?,?,?,'0','0',?,?)"; 
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, manage.getBuserId());
      ps.setString(2, manage.getBookNo());
      ps.setDate(3, new Date(manage.getBorrowDate().getTime()));
      ps.setString(4, manage.getBorrowRemark());
      ps.setDate(5, new Date (manage.getReturnDate().getTime()));
      ps.setString(6, manage.getRegFlag());
      ps.setString(7, String.valueOf(user.getSeqId()));
      int k = ps.executeUpdate(); 
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }    
  }
  
  /**
   * 借阅
   * 1.查看这本书借阅的数量，与库中数量对比
   * 2.如果 borrowCount + 1 = amt,则 update lend=1 否则只借阅
   * @param dbConn
   * @param manage
   * @throws Exception
   */
  public int toReadStatus(Connection dbConn, T9BookManage manage, T9Person user)throws Exception{
    int bCont = borrowCount(dbConn, manage.getBookNo());
    int amt = findBookCount(dbConn, manage.getBookNo());
    if(bCont +1 == amt){
      int k = updateLend(dbConn, manage.getBookNo(), 1);
      if(k!=0){
        return toRead(dbConn, manage, user);
      }      
    }else if(bCont +1 < amt){
      return toRead(dbConn, manage, user);
    }else{
      return -10;
    }
    return 0;
  }
  
  /**
   * 查找这本书被借了多少本
   * @param dbConn
   * @param bookNo
   * @return
   * @throws Exception
   */
  public int borrowCount(Connection dbConn, String bookNo)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select count(*) from BOOK_Manage where BOOK_NO = '"+ bookNo +"' and status = 1 and REAL_RETURN_TIME is null"; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt(1);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 查找这本书的数量
   * @param dbConn
   * @param bookNo
   * @return
   * @throws Exception
   */
  public int findBookCount(Connection dbConn, String bookNo)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;    
    String sql = "select  AMT from book_info where BOOK_NO = '"+ bookNo +"'"; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt(1);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 更新图说的借阅状态
   * @param dbConn
   * @param bookNo
   * @param flag 0或1
   * @return
   * @throws Exception
   */
  public int updateLend(Connection dbConn, String bookNo, int flag)throws Exception{
    PreparedStatement ps = null;    
    String sql = "UPDATE book_info set LEND='"+ flag +"'  where BOOK_NO = '"+ bookNo+"'"; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }   
  }
  
  /**
   * 更新图说的借阅状态
   * @param dbConn
   * @param bookNo
   * @param flag 0或1
   * @return
   * @throws Exception
   */
  public int updateLendById(Connection dbConn, int bookId, int flag)throws Exception{
    PreparedStatement ps = null;    
    String sql = "UPDATE book_info set LEND='"+ flag +"'  where SEQ_ID = "+ bookId; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }   
  }
  
  /**
   * 查找书的名字
   * @param dbConn
   * @param bookNo
   * @return
   * @throws Exception
   */
  public String findBookName(Connection dbConn, String bookNo)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String name = "";
    String sql = "select BOOK_NAME from book_info where BOOK_NAME is not null and BOOK_NO = '"+ bookNo +"'"; 
   // T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        name = rs.getString("BOOK_NAME");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }
  
  /**
   * 查找书的部门
   * @param dbConn
   * @param bookNo
   * @return
   * @throws Exception
   */
  public String findBookDept(Connection dbConn, String bookNo)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String dept = "";
    String sql = "select DEPT from book_info where BOOK_NO = '"+ bookNo +"'"; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        dept = rs.getString("DEPT");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return dept;
  }
  
  /**
   * 查找管理员的id串
   * @param dbConn
   * @param bookNo
   * @return
   * @throws Exception
   */
  public String findManagerIds(Connection dbConn, String bookNo) throws Exception{
    String deptId = findBookDept(dbConn,  bookNo);
    String ids = findManagerId(dbConn, deptId);
    return ids;
  }
  
  /**
   * 查找管理员的id串
   * @param dbConn
   * @param deptId
   * @return
   * @throws Exception
   */
  public String findManagerId(Connection dbConn, String deptId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String manaId = "";
    String sql = "select MANAGER_ID  from book_manager where " + T9DBUtility.findInSet(deptId, "MANAGE_DEPT_ID"); 
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        manaId = rs.getString("MANAGER_ID") +",";
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return quQong(manaId);
  }
  /**
   * 借阅待批，借阅已准 删除
   * @param dbConn
   * @param seqId
   * @return
   */
  public int deleteManage(Connection dbConn, int  seqId)throws Exception{
    PreparedStatement ps = null;    
    String sql = "delete from BOOK_MANAGE where seq_id=" + seqId; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 还书待批
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int returnBook(Connection dbConn, int  seqId) throws Exception{    
    return updateManage(dbConn, seqId, 1, 0);
  }
  
  /**
   * 还书待批（book_status=1, status=0）
   * 还书已准（book_status=1, status=1）
   * 还书未准（book_status=1, status=2）
   * 借书待批（book_status=0, status=0）
   * 借书已准（book_status=0, status=1）
   * 借书未准（book_status=0, status=2）
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int updateManage(Connection dbConn, int  seqId, int bookStatus, int status)throws Exception{
    PreparedStatement ps = null;    
    String sql = "update BOOK_MANAGE set book_status='"+ bookStatus +"', status='"+ status +"',REG_FLAG = '0' where SEQ_ID=" + seqId; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;      
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  } 
 
  
  /**
   * 还书已准删除
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int deleteFlag(Connection dbConn, int  seqId)throws Exception{
    PreparedStatement ps = null;    
    String sql = "update BOOK_MANAGE set DELETE_FLAG='1' where SEQ_ID=" + seqId; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 还书已准删除flag!=1直接删除，flag = 1 set DELETE_FLAG=1
   * @param dbConn
   * @param seqId
   * @param flag
   * @return
   * @throws Exception 
   */
  public int deleteFlagByFlag(Connection dbConn, int  seqId, String flag) throws Exception{
    if(!"1".equalsIgnoreCase(flag)){
      return realDelete(dbConn, seqId);
    }else{
      return deleteFlag(dbConn, seqId);
    }   
  }
  
  public int realDelete(Connection dbConn, int  seqId)throws Exception{
    PreparedStatement ps = null;    
    String sql = "delete from BOOK_MANAGE where SEQ_ID=" + seqId; 
    try{
      ps = dbConn.prepareStatement(sql);
      int k = ps.executeUpdate();
      return k;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 查找图书的借阅状态lend
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String findLendById(Connection dbConn, int  seqId)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String lend = "";
    String sql = "select LEND from book_info where seq_id = "+ seqId; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        lend = rs.getString("LEND");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return lend;
  }
  
  /**
   * 返回审批人的id
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9BookManage findRUserIds(Connection dbConn, int  seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String sql = "select BOOK_NO,RUSER_ID from book_manage where SEQ_ID =" + seqId;
    T9BookManage aManage = new T9BookManage();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){       
        aManage.setRuserId(rs.getString("RUSER_ID"));
        aManage.setBookNo(rs.getString("BOOK_NO"));
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }   
    return aManage;
  }
  
  
  /**
   * 去重 
   * @param ids 有逗号分割的id串
   * @return
   */
  @SuppressWarnings("unchecked")
  public static String quQong(String ids){
    if(T9Utility.isNullorEmpty(ids)){
      return "-1";
    }
    String newIds = "";
    String[] idSplit = ids.split(",");
    Set<String> set = new HashSet<String>();
    for(int i=0; i<idSplit.length; i++){
      if(!T9Utility.isNullorEmpty(idSplit[i])){
        set.add(idSplit[i]);
      }
    }
    for(Iterator it = set.iterator(); it.hasNext();){
      newIds += it.next()+",";
    }
    return newIds.substring(0, newIds.lastIndexOf(","));
  }
  
  public static void main(String[] args){
    String ids = "'1','2','1'";
    String id = quQong(ids);
    System.out.println(id);
  }
}
