package t9.subsys.oa.giftProduct.outstock.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.giftProduct.instock.data.T9GiftInstock;
import t9.subsys.oa.giftProduct.instock.logic.T9GiftInstockLogic;
import t9.subsys.oa.giftProduct.outstock.data.T9GiftOutstock;
import t9.subsys.oa.giftProduct.outstock.logic.T9GiftOutstockLogic;

public class T9GiftOutstockAct {
  /*
   * 新建礼品管理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addGiftOutstock(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Date curDate = new Date();
      String transUser = request.getParameter("user");
      String giftId = request.getParameter("giftId");
      String transQty = request.getParameter("transQty");
      String type = "2";
      if(transUser!=null&&!transUser.equals("")&&giftId!=null){
        T9GiftOutstock giftOutstock = (T9GiftOutstock) T9FOM.build(request.getParameterMap());
        giftOutstock.setTransDate(curDate);
        giftOutstock.setTransUser(transUser);
        //giftOutstock.setGiftId(Integer.parseInt(giftId.split(",")[0]));
        giftOutstock.setOperator(String.valueOf(userId));
        if(!T9Utility.isInteger(transQty)){
          giftOutstock.setTransQty(0);
        }
        int qty = giftOutstock.getTransQty();
        T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
        T9GiftInstock instock = instockLogic.selectGiftInstockById(dbConn, Integer.parseInt(giftId));
        if(instock!=null){
          //查询库存量
          int useTransTotal = instockLogic.selectGiftQty(dbConn, instock.getSeqId());
          int useGiftQty = instock.getGiftQty()-useTransTotal;
          if(qty<= instock.getGiftQty()){
            type = "1";
            T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
            int seqId = giftLogic.addGiftOutstock(dbConn, giftOutstock);
            //可存数量也要减掉领用数量
            instockLogic.updateInstockById(dbConn, giftId, qty);
          }
        }
   
      
      }
      String data = "{type:\""+type+"\"}";
      //String data = "{seqId:\"" + seqId + "\",giftName:\""+ giftOutstock.getGiftName()+"\"}";
    /*    //短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("5");
        sb.setContent("请查看日程安排！内容："+content);
        sb.setRemindUrl("/t9/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
        sb.setToId(String.valueOf(userId));
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);*/
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询今日领用的礼品记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOutstockByToday(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      T9PersonLogic perLogic = new T9PersonLogic();
      List<Map<String ,String>> giftList = new ArrayList<Map<String ,String>>();
      giftList = giftLogic.getGiftOutstockToday(dbConn, dateFormat.format(new Date()),userId);
      String data = getJson(giftList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getJson(List<Map<String,String>> mapList){
    StringBuffer buffer=new StringBuffer("["); 
    for(Map<String, String> equipmentsMap:mapList){ 
    buffer.append("{"); 
    Set<String>keySet=equipmentsMap.keySet(); 
    for(String mapStr:keySet){ 
      //System.out.println(mapStr + ":>>>>>>>>>>>>" + equipmentsMap.get(mapStr)); 
      String name=equipmentsMap.get(mapStr); 
      if(name!=null){
        name =name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
      }
     /* if(mapStr!=null&&mapStr.equals("seqId")){
        
      }*/
      buffer.append( mapStr+":\"" + (name==null? "":name) + "\","); 
    } 
    buffer.deleteCharAt(buffer.length()-1); 
    buffer.append("},"); 
    }
    buffer.deleteCharAt(buffer.length()-1); 
    if (mapList.size()>0) { 
      buffer.append("]"); 
    }else { 
      buffer.append("[]"); 
    }
    String data = buffer.toString();
    //System.out.println(data);
    return data;
  }
  public String getJson(Map<String,String> map){
    StringBuffer buffer=new StringBuffer(""); 
    buffer.append("{"); 
    Set<String>keySet=map.keySet(); 
    for(String mapStr:keySet){ 
      //System.out.println(mapStr + ":>>>>>>>>>>>>" + map.get(mapStr)); 
      String name=map.get(mapStr); 
      if(name!=null){
        name =name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
      }
      buffer.append( mapStr+":\"" + (name==null? "":name) + "\","); 

    }
    buffer.deleteCharAt(buffer.length()-1); 
    buffer.append("}"); 
    String data = buffer.toString();
    //System.out.println(data);
    return data;
  }
  /**
   * 删除outstock
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delOutstockById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if(seqId!=null&&!seqId.equals("")){
        T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
        //得到本次领用的礼品个数-退回的个数
        T9GiftOutstock outstock = giftLogic.selectOutstockById(dbConn,Integer.parseInt(seqId));
        int transQty = outstock.getTransQty();
        int transFlag = outstock.getTransFlag();
        //得到本次真正领用的礼品个数
        int useTran = 0;
        useTran = transQty-transFlag;
        if(useTran>0){
          //给还原 礼品入库
         T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
         instockLogic.updateInstockByIdBack(dbConn, outstock.getGiftId(), useTran);
        }
        giftLogic.delOutstockById(dbConn, Integer.parseInt(seqId));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询outstock 退库
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOutstockByIdBack(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String data = "";
      if(seqId!=null&&!seqId.equals("")){
        T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
        Map<String,String> map = giftLogic.getOutstockByIdBack(dbConn, Integer.parseInt(seqId));
        data = getJson(map);
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询outstock ById编辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOutstockById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String data = "";
      if(seqId!=null&&!seqId.equals("")){
        T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
        T9GiftOutstock outstock = new T9GiftOutstock();
        Map<String ,String> map = giftLogic.selectGiftOutstockById(dbConn, Integer.parseInt(seqId));
        if(outstock!=null){
          data = getJson(map);
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新礼品领用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateGiftstock(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String transFlag = request.getParameter("transFlag");
      String data = "";
      if(seqId!=null&&!seqId.equals("")&&transFlag!=null&&!transFlag.equals("")){
        T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
        //得到领用表
        T9GiftOutstock outstock = giftLogic.selectOutstockById(dbConn,Integer.parseInt(seqId));
        if(outstock!=null){
          //领用表改变
          giftLogic.updateGiftBack(dbConn, Integer.parseInt(seqId), transFlag);
          //改变礼品表
          //给还原 礼品入库
          T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
         int useTran = 0;
         instockLogic.updateInstockByIdBack(dbConn, outstock.getGiftId(),Integer.parseInt(transFlag));
        }
       
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新礼品领用详细信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateGiftstockInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String newTransQty = request.getParameter("transQty");//新的领用数量
      String transUses = request.getParameter("transUses");
      String transMemo = request.getParameter("transMemo");
      String userId = request.getParameter("user");
      String data = "";
      String Type = "1";//1：正常
      String giftQty = "0";
      if(!T9Utility.isNullorEmpty(seqId)){
        T9GiftOutstockLogic  outLogic = new T9GiftOutstockLogic();
        //得到领用表
        T9GiftOutstock outstock = outLogic.selectOutstockById(dbConn,Integer.parseInt(seqId));
        if(outstock!=null&&!T9Utility.isNullorEmpty(newTransQty)){
          int transQty = outstock.getTransQty();//原来的本次领用的数量
          int transFlag = outstock.getTransFlag();//原来的本次领用的退回数量
          int transUes = transQty - transFlag;//原来实际领用量
          if(Integer.parseInt(newTransQty)<transFlag){
            Type = "2";//不能小于退回数量
          }else{ 
            T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
            T9GiftInstock instock = instockLogic.selectGiftInstockById(dbConn,outstock.getGiftId());//得到礼品
            if(instock!=null){
              //查询库存量
              int useTransTotal = instock.getGiftQty();//instockLogic.selectGiftQty(dbConn, instock.getSeqId());
              int newUseTransTotal = useTransTotal + transUes;//得到库存总量
              if(Integer.parseInt(newTransQty)>newUseTransTotal){
                Type = "3";//超出库存
                giftQty = newUseTransTotal + "";
              }else{
                //领用表改变
                 outstock.setTransUser(userId);
                 outstock.setTransQty(Integer.parseInt(newTransQty));
                 outstock.setTransMemo(transMemo);
                 outstock.setTransUses(transUses);
                 outLogic.updateGiftOutstock(dbConn, outstock);
                 //给还原 礼品入库
                 int useTotal = transQty - Integer.parseInt(newTransQty);
                 instockLogic.updateInstockByIdBack(dbConn, outstock.getGiftId(),useTotal);
              }
            }
          
          }    
        } 
      }
      data = "{type:" + Type +  ",giftQty:" + giftQty + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询领用的礼品记录根据用户ID
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  getOutstockByUserId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      //int userId = user.getSeqId();
      String userId = request.getParameter("userId");
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      T9PersonLogic perLogic = new T9PersonLogic();
      List<Map<String ,String>> giftList = new ArrayList<Map<String ,String>>();
      if(userId!=null&&!userId.equals("")){
        giftList = giftLogic.getGiftOutstockByUserId(dbConn,userId);
      }
      String data = getJson(giftList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String queryGiftByTemp(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String giftName = request.getParameter("giftName");
      String giftType = request.getParameter("giftType");
      String giftId = request.getParameter("giftId");
      String fromDate = request.getParameter("fromDate");
      String toDate = request.getParameter("toDate");
      if(giftName==null){
        giftName = "";
      }
      if(giftType==null){
        giftType = "";
      }
      if(giftId==null){
        giftId = "";
      }
      if(fromDate==null){
        fromDate = "";
      } 
      if(toDate==null){
        toDate = "";
      } 
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      String data = giftLogic.toSearchData(dbConn,request.getParameterMap(),giftType,giftId,giftName,fromDate,toDate);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 采购物品报表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectReport(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String giftType = request.getParameter("giftType");
      String giftId = request.getParameter("giftId");
      String fromDate = request.getParameter("fromDate");
      String toDate = request.getParameter("toDate");
      if(giftType==null){
        giftType = "";
      }
      if(giftId==null){
        giftId = "";
      }
      if(fromDate==null){
        fromDate = "";
      } 
      if(toDate==null){
        toDate = "";
      } 
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      List<Map<String,String>> list = giftLogic.selectReport(dbConn, giftType, giftId, fromDate, toDate);
      String data = getJson(list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 物品总表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectProductInfo(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String giftType = request.getParameter("giftType");
      String giftId = request.getParameter("giftId");
      String fromDate = request.getParameter("fromDate");
      String toDate = request.getParameter("toDate");
      if(giftType==null){
        giftType = "";
      }
      if(giftId==null){
        giftId = "";
      }
      if(fromDate==null){
        fromDate = "";
      } 
      if(toDate==null){
        toDate = "";
      } 
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      List<Map<String,String>> list = giftLogic.selectProductInfo(dbConn, giftType, giftId, fromDate, toDate);
      String data = getJson(list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 部门领用汇总
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getGiftOutByDeptId(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String giftType = request.getParameter("giftType");
      String giftId = request.getParameter("giftId");
      String fromDate = request.getParameter("fromDate");
      String toDate = request.getParameter("toDate");
      String deptId = request.getParameter("deptId");
      if(giftType==null){
        giftType = "";
      }
      if(giftId==null){
        giftId = "";
      }
      if(fromDate==null){
        fromDate = "";
      } 
      if(toDate==null){
        toDate = "";
      } 
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9GiftOutstockLogic  giftLogic = new T9GiftOutstockLogic();
      String data  = "";
      if(deptId!=null&&!deptId.equals("")){
        List<Map<String,String>> list = giftLogic.getGiftOutByDeptId(dbConn, giftType, giftId, fromDate, toDate, deptId);
        data = getJson(list);
      }
      if(data.equals("")){
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
