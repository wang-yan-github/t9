package t9.subsys.oa.book.act;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.news.logic.T9NewsManageLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogSearchLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.inforesouce.act.T9OutURLAct;
import t9.subsys.inforesouce.util.T9OutURLUtil;
import t9.subsys.oa.book.data.T9BookInfo;
import t9.subsys.oa.book.logic.T9BookTypeEnterLogic;

public class T9BookTypeEnterAct { 
  private static Logger log = Logger.getLogger(" t9.core.funcs.system.syslog.act.T9SysLogAct");
  public String addBookTypeEnter(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map<String, String> attr = null;
    String attrId = "";
    String attrName = "";
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> paramMap = fileForm.getParamMap();
        String deptId = paramMap.get("deptId");
        String bookName =  paramMap.get("bookName");
        String bookNo =  paramMap.get("bookNo");
        String typeId =  paramMap.get("typeId");
        int booktypeId = Integer.valueOf(typeId);
        String author =  paramMap.get("author");
        String isbn =  paramMap.get("isbn");
        String pubHouse =  paramMap.get("pubHouse");
        String statrTime = paramMap.get("statrTime");
        String area = paramMap.get("area");
        String amt = paramMap.get("amt");
        String price = paramMap.get("price");
        String brief = paramMap.get("brief");
        String deptDesc = paramMap.get("deptDesc");
        String dept = paramMap.get("dept");
        String lend = paramMap.get("lend");
        String borrPerson = paramMap.get("borrPerson");
        String memo = paramMap.get("memo");
  
        String attachmentName = paramMap.get("attachment");
        String seqId = paramMap.get("seqId");

        fileForm.getParamMap().put("fileName", attachmentName);
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9BookTypeEnterLogic newsManageLogic = new T9BookTypeEnterLogic();
        if(fileForm!=null){
            attr = newsManageLogic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
            Set<String> keys = attr.keySet();
            for (String key : keys){
              String value = attr.get(key);
              
              attrId += key ;
           
              attrName += value ;
           }
        }
        T9BookInfo bi = new T9BookInfo();
        bi.setDept(Integer.parseInt(deptId));
        bi.setBookName(bookName);
        bi.setBookNo(bookNo);
        bi.setTypeId(booktypeId);
        bi.setAuthor(author);
        bi.setIsbn(isbn);
        bi.setPubHouse(pubHouse);
        bi.setPubDate(statrTime);
        bi.setArea(area);
        bi.setAmt(Integer.parseInt(amt.trim()));
        if(T9Utility.isNullorEmpty(price))
          price = "0.0";
        bi.setPrice(Double.parseDouble(price));
        bi.setBrief(brief);
        bi.setOpen(dept);
        bi.setLend(lend);
        bi.setBorrPerson(borrPerson);
        bi.setMemo(memo);
        bi.setAttachmentId(attrId);
        bi.setAttachmentName(attrName);
        
        List<T9BookInfo> bookInfo = T9BookTypeEnterLogic.isBookNameRepeat(dbConn, person,bookName);
        String bookNameIsRepeat="";
        for(int i=0; i<bookInfo.size(); i++){
          bookNameIsRepeat = String.valueOf(bookInfo.get(i));
        }
        if(!T9Utility.isNullorEmpty(bookNameIsRepeat)){
          return "/subsys/oa/book/manage/bookNameIsRepeat.jsp?bookName="+bookName;
        }
        List<T9BookInfo> bookNoInfo = T9BookTypeEnterLogic.isBookNoRepeat(dbConn, person,bookNo);
        String bookNoIsRepeat="";
        for(int i=0; i<bookNoInfo.size(); i++){
          bookNoIsRepeat = String.valueOf(bookNoInfo.get(i));
        }
        if(!T9Utility.isNullorEmpty(bookNoIsRepeat)){
          return "/subsys/oa/book/manage/bookNoIsRepeat.jsp?bookNo="+bookNo;
        }
        
        int ok = T9BookTypeEnterLogic.addBookTypeEnter(dbConn, person,bi);
        //String url = "typeId="+typeId+"&lend="+lend+"&bookName="+bookName+"&bookNo="+bookNo+"&author="+author+"&isbn="+isbn+"&pubHouse="+pubHouse+"&area="+area;
        if(ok!=0){
          return "/t9/subsys/oa/book/act/T9BookTypeAct/selectBookType.act";
        }
        /*if(ok!=0){
         // return "/t9/subsys/oa/book/act/T9BookTypeAct/selectBookType.act?url"+url;
          return "/t9/subsys/oa/book/act/T9BookTypeAct/findBooks.act?"+url;
        }*/
       return "";
  }
 /**
  * 编辑图书基本信息
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String editBookTypeInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      Map<String, String> attr = null;
      String attrId = "";
      String attrName = "";
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
       
      Map<String, String> paramMap = fileForm.getParamMap();
      
      String deptId = paramMap.get("deptId");
      String bookName =  paramMap.get("bookName");
      String bookNo =  paramMap.get("bookNo");
      String typeId =  paramMap.get("typeId");
      int booktypeId =0; //没有图书类型默认为0
      if(!T9Utility.isNullorEmpty(typeId)){
          booktypeId = Integer.valueOf(typeId);
      }
      String author =  paramMap.get("author");
      String isbn =  paramMap.get("isbn");
      String pubHouse =  paramMap.get("pubHouse");
      String statrTime = paramMap.get("statrTime");
      String area = paramMap.get("area");
      String amt = paramMap.get("amt");
      String price = paramMap.get("price");
      String brief = paramMap.get("brief");
      String deptDesc = paramMap.get("deptDesc");
      String dept = paramMap.get("dept");
      String lend = paramMap.get("lend");
      String borrPerson = paramMap.get("borrPerson");
      String memo = paramMap.get("memo");
      String attachmentName = paramMap.get("attachment");
      //String seqId = paramMap.get("seqId");
      String seqId = request.getParameter("seqId");
          //String attachmentName = request.getParameter("attachment");
         // String seqIds = request.getParameter("seqId");
          //int seqId = Integer.parseInt(seqIds);
          fileForm.getParamMap().put("fileName", attachmentName);
          //T9Out.println(fileForm.getParamMap().get("fileName"));
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          //T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
          T9BookTypeEnterLogic newsManageLogic = new T9BookTypeEnterLogic();
          if(fileForm!=null){
              attr = newsManageLogic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
              Set<String> keys = attr.keySet();
              for (String key : keys){
                String value = attr.get(key);
                
                attrId += key ;
             
                attrName += value ;
             }
          }
          T9BookInfo bi = new T9BookInfo();
            
          bi.setDept(Integer.parseInt(deptId));
          bi.setBookName(tsziFu(bookName));
          bi.setBookNo(tsziFu(bookNo));
          bi.setTypeId(booktypeId);
          bi.setAuthor(tsziFu(author));
          bi.setIsbn(tsziFu(isbn));
          bi.setPubHouse((pubHouse));
          bi.setPubDate(tsziFu(statrTime));
          bi.setArea(tsziFu(area));
          bi.setAmt(Integer.parseInt(amt));
          if(T9Utility.isNullorEmpty(price))
            price = "0.0";
          bi.setPrice(Double.parseDouble(price));
          bi.setBrief(tsziFu(brief));
          bi.setOpen(tsziFu(dept));
          bi.setLend(tsziFu(lend));
          bi.setBorrPerson(tsziFu(borrPerson));
          bi.setMemo(tsziFu(memo));
          bi.setAttachmentId(attrId);
          bi.setAttachmentName(attrName);
          bi.setSeqId(Integer.parseInt(seqId));
          int ok = T9BookTypeEnterLogic.editBookTypeInfo(dbConn, person,bi);
          String url = "typeId="+"0"+"&lend="+""+"&bookName="+""+"&bookNo="+""+"&author="+""+"&isbn="+""+"&pub_house="+""+"&area="+"";
          if(ok!=0){
           // return "/t9/subsys/oa/book/act/T9BookTypeAct/selectBookType.act?url"+url;
            return "/t9/subsys/oa/book/act/T9BookTypeAct/findBooks.act?"+url;
          }
         return "/t9/subsys/oa/book/act/T9BookTypeAct/findBooks.act?"+url;
    }
  
  public String tsziFu(String zf) throws Exception {
    String newStr = zf.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

    return newStr;
  }
  public String findBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        String str = "";
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9BookTypeEnterLogic bte = new T9BookTypeEnterLogic();
        str = bte.findBookType(dbConn, person);
        //System.out.println(str);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"图书查询成功");
        request.setAttribute(T9ActionKeys.RET_DATA, str);
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String blurFindBookType(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
      Connection dbConn = null;
      String bookNo = request.getParameter("bookNo");
      try{
        String str = "";
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
         T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
         T9BookTypeEnterLogic tte = new T9BookTypeEnterLogic();
         str = tte.blurFindBookType(dbConn, person,bookNo);
         
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);//RET_STATE返回状态  RETURN_OK正确返回
         request.setAttribute(T9ActionKeys.RET_MSRG, "图书模糊查询成功");//RET_MSRG 返回消息
         request.setAttribute(T9ActionKeys.RET_DATA, str);//RET_DATA 返回数据
         request.setAttribute("data", str);
      }catch(Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
    return "/subsys/oa/book/borrow_manage/borrow/bookno_select/bookno_info.jsp";
  } 
}
