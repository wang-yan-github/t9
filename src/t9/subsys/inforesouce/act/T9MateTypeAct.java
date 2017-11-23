package t9.subsys.inforesouce.act;


import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.logic.T9MateTypeLogic;


/**
 * 元数据类型 * @author qwx110
 *
 */
public class T9MateTypeAct{
 private T9MateTypeLogic mlogic = new T9MateTypeLogic();
 
 /**
  * 查找所有的数据源
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String findMata(HttpServletRequest request, HttpServletResponse response)throws Exception{
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
   Connection dbConn = null;
   dbConn = requestDbConn.getSysDbConn();
   String defType = request.getParameter("defalutType");
   List<T9MateType> mates = mlogic.findMatas(dbConn,defType);
   request.setAttribute("mates", mates);
   return "/subsys/inforesource/inforindex.jsp";
 }
 /**
  * 查找子元素
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String findSubMata(HttpServletRequest request, HttpServletResponse response)throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
   dbConn = requestDbConn.getSysDbConn();
   String seqId = request.getParameter("seqid");   
   List<T9MateType> mates = mlogic.findSubMatas(dbConn, Integer.parseInt(seqId));
   T9MateType aMate = mlogic.findAMateType(dbConn, Integer.parseInt(seqId));
   request.setAttribute("mates", mates);
   request.setAttribute("parentId", seqId);
   request.setAttribute("ftypes", aMate.getElement_type());
   return "/subsys/inforesource/subinfor.jsp";
 }
 /**
  * 删除主表元素
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String deleteSubMata(HttpServletRequest request, HttpServletResponse response)throws Exception{
  Connection dbConn = null;
  T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  dbConn = requestDbConn.getSysDbConn();
  String seqId  = request.getParameter("seqid");
  int flag = mlogic.deleteSubMata(dbConn,Integer.parseInt(seqId));
  request.setAttribute("flag", flag);
  return "/t9/subsys/inforesouce/act/T9MateTypeAct/findMata.act?defalutType=1";
 }
 
 /**
  * 查找某一个元数据类型
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String editAMeta(HttpServletRequest request, HttpServletResponse response)throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   dbConn = requestDbConn.getSysDbConn();
   String seqId = request.getParameter("seqid");
   String parentId = request.getParameter("pId");
   if(parentId == null){
     parentId = "0";
   }
   try{
     T9MateType type = mlogic.findAMateType(dbConn, Integer.parseInt(seqId)); 
     T9MateType paId = new T9MateType();
    if(parentId != "0"){
      paId = mlogic.findAMateType(dbConn, Integer.parseInt(parentId));       
    }
     request.setAttribute("parentType", paId.getElementId());
     request.setAttribute("type", type);
     request.setAttribute("seqId", seqId);
  } catch (Exception e){  
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
    throw e;
  }
   return "/subsys/inforesource/editMeta.jsp";
 }

 /**
  * 更新某个元数据
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String updateMate(HttpServletRequest request, HttpServletResponse response)throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   dbConn = requestDbConn.getSysDbConn();
   String seqId = request.getParameter("seqId");
   T9MateType type = new T9MateType();   
    String boardNo = request.getParameter("BOARD_NO"); //编号  
    int num = Integer.parseInt(boardNo);
    if(num > 100){
      boardNo = "MEX" + boardNo;
    }else{
      boardNo = "M" + boardNo;
    }
    String cnName = request.getParameter("cn_NAME");
    String enName = request.getParameter("en_NAME");
    String defineText = request.getParameter("define_TEXT");
    String aimText = request.getParameter("aim_TEXT"); 
    String constraint = request.getParameter("constraint");//约束性
    String repeat = request.getParameter("repeat");
    String element_type = request.getParameter("element_type");
    String typeId = request.getParameter("typeId");    
    String[] ftypes = request.getParameterValues("eleType");
    String ftypeIds = "";
    for(int i=0; i<ftypes.length; i++){
      ftypeIds += ftypes[i] +",";
    }
    type.setSeqId(Integer.parseInt(seqId));
    type.setNumberId(boardNo);
    type.setcNname(cnName);
    type.seteNname(enName);
    type.setDefine(defineText);
    type.setAim(aimText);
    type.setConstraint(constraint);
    type.setRepeat(repeat);
    type.setElementId(element_type);
    type.setTypeId(typeId);
    type.setElement_type(ftypeIds);
    try{
      mlogic.updateAmate(dbConn, type);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
     throw e;
    }
    return "/t9/subsys/inforesouce/act/T9MateTypeAct/findMata.act?defalutType=1";
 }
 
}
