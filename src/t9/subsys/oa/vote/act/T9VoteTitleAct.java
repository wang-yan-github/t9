package t9.subsys.oa.vote.act;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.vote.data.T9VoteItem;
import t9.subsys.oa.vote.data.T9VoteTitle;
import t9.subsys.oa.vote.logic.T9VoteDataLogic;
import t9.subsys.oa.vote.logic.T9VoteItemLogic;
import t9.subsys.oa.vote.logic.T9VoteTitleLogic;

public class T9VoteTitleAct {
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectTitle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = T9VoteTitleLogic.selectTitle(dbConn, request.getParameterMap());
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
   * 删除根据选中的seqId字符串--SYL
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String deleteVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      //处理seqIds
      String seqIds = request.getParameter("seqIds");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      if(!T9Utility.isNullorEmpty(seqIds)){
        seqIds = seqIds.substring(0,seqIds.length() - 1);
        String queryStr = "SEQ_ID in(" + seqIds + ")";
        if(!userPriv.equals("1")){
          queryStr =  "SEQ_ID in(" + seqIds + ") and FROM_ID='" + user.getUserId() + "'";
        }
        String[] str = {queryStr};
        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn, str);//根据选中的seq_id字符串得到投票记录
        //得到新seqId字符串
        String newSeqId = "";
        for (int i = 0; i <titleList.size(); i++) {
          newSeqId = newSeqId + titleList.get(i).getSeqId()+ ",";
        }
        if (titleList.size() > 0){
          newSeqId = newSeqId.substring(0, newSeqId.length()-1);
        }
        //根据ID字符串得到他们的所有子投票
        String[] childStr = {"PARENT_ID in (" + newSeqId + ")"};
        List<T9VoteTitle> childTitleList = titleLogic.selectTitle(dbConn, childStr);  
        //得到子投票的seq_id
        String childSeqId = "";
        for (int i = 0; i <childTitleList.size(); i++) {
          childSeqId = childSeqId + childTitleList.get(i).getSeqId()+ ",";
        }
        if(childTitleList.size()>0){
          childSeqId = childSeqId.substring(0, childSeqId.length()-1);
        }
        //得到所有的投票和子投票seq_id字符串
        String newSeqIds = newSeqId; 
        if(!T9Utility.isNullorEmpty(childSeqId)){
          newSeqIds = newSeqIds + "," + childSeqId;
        }
        //得到所有的投票项目
        String[] itemStr = {"vote_id in(" + newSeqIds + ")"};
        List<T9VoteItem> itemList = itemLogic.selectItem(dbConn, itemStr);
        //得到投票项目的seq_id
        String itemSeqId = "";
        for (int i = 0; i <itemList.size(); i++) {
          itemSeqId = itemSeqId + itemList.get(i).getSeqId()+ ",";
        }
        if(itemList.size()>0){
          itemSeqId = itemSeqId.substring(0, itemSeqId.length()-1);
        }
        //删除所有vote_data表中item_id等于
        T9VoteDataLogic dataLogic = new T9VoteDataLogic();
        if (!T9Utility.isNullorEmpty(itemSeqId)) {
          dataLogic.delDataByItemIds(dbConn, itemSeqId,"1");//field不等于0
        }
        //删除所有type是文本输入的vote_data表
        dataLogic.delDataByItemIds(dbConn, newSeqIds, "0");//field等于0
        //删除所有的投票项目
        itemLogic.delItemByVoteIds(dbConn, newSeqIds);
        //删除所有的投票以及子投票
        titleLogic.delTitleBySeqIds(dbConn, newSeqIds);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除全部--SYL
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String deleteAllVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      //String seqIds = request.getParameter("seqIds");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      T9VoteDataLogic dataLogic = new  T9VoteDataLogic(); 
      if(userPriv.equals("1")){
        dataLogic.delAllData(dbConn);
        itemLogic.delAllItem(dbConn);
        titleLogic.delAllTitle(dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 清空--SYL
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String clearVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqIds = request.getParameter("seqIds");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      if(!T9Utility.isNullorEmpty(seqIds)){
        seqIds = seqIds.substring(0,seqIds.length() - 1);
        String queryStr = "SEQ_ID in(" + seqIds + ") and PARENT_ID=0";
        if(!userPriv.equals("1")){
          queryStr =  "SEQ_ID in(" + seqIds + ")  and FROM_ID='" + user.getUserId() + "'";
        }
        String[] str = {queryStr};
        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn, str);//根据选中的seq_id字符串得到投票记录
        //得到新seqId字符串
        String newSeqId = "";
        for (int i = 0; i <titleList.size(); i++) {
          newSeqId = newSeqId + titleList.get(i).getSeqId()+ ",";
        }
        if(titleList.size()>0){
          newSeqId = newSeqId.substring(0, newSeqId.length()-1);
        }
        //根据ID字符串得到他们的所有子投票
        String[] childStr = {"PARENT_ID in (" + newSeqId + ")"};
        List<T9VoteTitle> childTitleList = titleLogic.selectTitle(dbConn, childStr);  
        //得到子投票的seq_id
        String childSeqId = "";
        for (int i = 0; i <childTitleList.size(); i++) {
          childSeqId = childSeqId + childTitleList.get(i).getSeqId()+ ",";
        }
        if(childTitleList.size() > 0){
          childSeqId = childSeqId.substring(0, childSeqId.length()-1);
        }
        //得到所有的投票和子投票seq_id字符串
        String newSeqIds = newSeqId; 
        if(!T9Utility.isNullorEmpty(childSeqId)){
          newSeqIds = newSeqIds + "," + childSeqId;
        }
        //得到所有的投票项目
        String[] itemStr = {"vote_id in(" + newSeqIds + ")"};
        List<T9VoteItem> itemList = itemLogic.selectItem(dbConn, itemStr);
        //得到投票项目的seq_id
        String itemSeqId = "";
        for (int i = 0; i <itemList.size(); i++) {
          itemSeqId = itemSeqId + itemList.get(i).getSeqId()+ ",";
        }
        if(itemList.size()>0){
          itemSeqId = itemSeqId.substring(0, itemSeqId.length()-1);
        }
        //删除所有vote_data表中item_id等于
        T9VoteDataLogic dataLogic = new T9VoteDataLogic();
        if (!T9Utility.isNullorEmpty(itemSeqId)) {
          dataLogic.delDataByItemIds(dbConn, itemSeqId,"1");//field不等于0
        }
        //删除所有type是文本输入的vote_data表        dataLogic.delDataByItemIds(dbConn, newSeqIds, "0");//field等于0
        //更新所有的投票项目
        itemLogic.updateItemByVoteIds(dbConn, newSeqIds);
        //更新的投票以及子投票
        titleLogic.updateTitleBySeqIds(dbConn, newSeqIds);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 克隆--SYL
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String clonVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqIds = request.getParameter("seqIds");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      if(!T9Utility.isNullorEmpty(seqIds)){
        seqIds = seqIds.substring(0,seqIds.length() - 1);
        //克隆父投票

        String queryStr = "SEQ_ID in(" + seqIds + ") and PARENT_ID=0";
        if(!userPriv.equals("1")){
          queryStr =  "SEQ_ID in(" + seqIds + ")  and FROM_ID='" + user.getUserId() + "'";
        }
        String[] str = {queryStr};
        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn, str);//根据选中的seq_id字符串得到投票记录

        /*        //得到新seqId字符串

        String newSeqId = "";
        for (int i = 0; i <titleList.size(); i++) {
          newSeqId = newSeqId + titleList.get(i).getSeqId()+ ",";
        }
        if(titleList.size()>0){
          newSeqId = newSeqId.substring(0, newSeqId.length()-1);
        }*/
        for (int i = 0; i < titleList.size(); i++) {
          T9VoteTitle title = titleList.get(i);
          title.setReaders("");
          String newId = titleLogic.addVote(dbConn, title);//新建并返回SEQ_ID
          //得到所有的投票项目
          String[] itemStr = {"vote_id in(" + title.getSeqId() + ")"};
          List<T9VoteItem> itemList = itemLogic.selectItem(dbConn, itemStr);
          for (int j = 0; j < itemList.size(); j++) {
            T9VoteItem item = itemList.get(j);
            T9VoteItem newItem = new  T9VoteItem();
            newItem.setVoteId(Integer.parseInt(newId));
            newItem.setItemName(item.getItemName());
            itemLogic.addItem(dbConn, newItem);//新建投票项目

          }

          //克隆子投票

          //根据ID字符串得到他们的所有子投票
          String[] childStr = {"PARENT_ID in (" + title.getSeqId() + ")"};
          List<T9VoteTitle> childTitleList = titleLogic.selectTitle(dbConn, childStr);  
          for (int j = 0; j < childTitleList.size(); j++) {
            T9VoteTitle Childtitle = childTitleList.get(j);
            T9VoteTitle newTitle = new T9VoteTitle();
            newTitle.setFromId(Childtitle.getFromId());
            newTitle.setParentId(Integer.parseInt(newId));
            newTitle.setSubject(Childtitle.getSubject());
            newTitle.setContent(Childtitle.getContent());
            newTitle.setSendTime(Childtitle.getSendTime());
            newTitle.setEndDate(Childtitle.getEndDate());
            newTitle.setType(Childtitle.getType());
            newTitle.setMaxNum(Childtitle.getMaxNum());
            newTitle.setMinNum(Childtitle.getMinNum());
            newTitle.setTop(Childtitle.getTop());
            String childNewId = titleLogic.addVote(dbConn, newTitle);//新建并返回SEQ_ID
            //得到所有的投票项目
            String[] ChildItemStr = {"vote_id in(" + title.getSeqId() + ")"};
            List<T9VoteItem> ChildItemList = itemLogic.selectItem(dbConn, ChildItemStr);
            for (int k = 0; k < ChildItemList.size(); k++) {
              T9VoteItem item = ChildItemList.get(k);
              T9VoteItem newItem = new  T9VoteItem();
              newItem.setVoteId(Integer.parseInt(childNewId));
              newItem.setItemName(item.getItemName());
              itemLogic.addItem(dbConn, newItem);//新建投票项目
            }
          }
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }


  /**
   * 更新取消置顶--SYL
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String updateNoTopVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqIds = request.getParameter("seqIds");
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      if(!T9Utility.isNullorEmpty(seqIds)){
        seqIds = seqIds.substring(0,seqIds.length() - 1);
        String queryStr = "SEQ_ID in(" + seqIds + ") and PARENT_ID=0";
        if(!userPriv.equals("1")){
          queryStr =  "SEQ_ID in(" + seqIds + ")  and FROM_ID='" + user.getUserId() + "'";
        }
        String[] str = {queryStr};
        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn, str);//根据选中的seq_id字符串得到投票记录
        //得到新seqId字符串
        String newSeqId = "";
        for (int i = 0; i <titleList.size(); i++) {
          newSeqId = newSeqId + titleList.get(i).getSeqId()+ ",";
        }
        if(titleList.size()>0){
          newSeqId = newSeqId.substring(0, newSeqId.length()-1);
        }
        titleLogic.updateNoTopBySeqIds(dbConn, seqIds);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询vote_title byId---syl
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String selectVoteById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqId = request.getParameter("seqId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      T9VoteDataLogic dataLogic = new  T9VoteDataLogic(); 
      String data = "";
      if(T9Utility.isInteger(seqId)){
        T9PersonLogic personLogic = new T9PersonLogic();
        String[] str = {"SEQ_ID = " + seqId ,"PUBLISH='1'" ,"("
            + T9DBUtility.findInSet("ALL_DEPT","TO_ID")
            + " or " + T9DBUtility.findInSet("0","TO_ID")
            + " or " + T9DBUtility.findInSet(user.getDeptId()+"","TO_ID")
            + " or " + T9DBUtility.findInSet(user.getSeqId()+"", "USER_ID")
            + " or " + T9DBUtility.findInSet(user.getUserPriv(), "PRIV_ID") + ")"
            ,T9DBUtility.getDateFilter("BEGIN_DATE", T9Utility.getCurDateTimeStr(), "<=")
            ,"(" + T9DBUtility.getDateFilter("END_DATE", T9Utility.getCurDateTimeStr(), ">=")
            +" or END_DATE is null)"};

        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn, str);//根据选中的seq_id字符串得到投票记录

        if(titleList.size()>0){
          String fromName = "";
          String deptName = "";

          if(!T9Utility.isNullorEmpty(titleList.get(0).getFromId())){
            fromName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(titleList.get(0).getFromId(), dbConn));
            T9Person person = personLogic.getPerson(dbConn, titleList.get(0).getFromId());
            deptName = T9Utility.encodeSpecial(personLogic.getDeptName(dbConn, person.getDeptId()));
          }
          data = T9FOM.toJson(titleList.get(0)).toString().substring(0, T9FOM.toJson(titleList.get(0)).toString().length()-1) + ",fromName:\"" + fromName +"\",deptName:\""+ deptName + "\"}";
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, user.getSeqId()+"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询vote_title byId---syl
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String selectVoteById2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqId = request.getParameter("seqId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      T9VoteDataLogic dataLogic = new  T9VoteDataLogic(); 
      String data = "";
      if(T9Utility.isInteger(seqId)){
        T9VoteTitle title = titleLogic.selectVoteById(dbConn, Integer.parseInt(seqId));
        if(title !=null){
          data = T9FOM.toJson(title).toString();
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, user.getSeqId()+"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *查询所有(分页)通用列表显示数据--syl  个人事务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVoteToCurrent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = T9VoteTitleLogic.selectVoteToCurrent(dbConn, request.getParameterMap(),person.getSeqId(),person.getDeptId(),person.getUserPriv());
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
   *查询所有(分页)通用列表显示数据--syl  个人事务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVoteToHistory(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = T9VoteTitleLogic.selectVoteToHistory(dbConn, request.getParameterMap(),person.getSeqId(),person.getDeptId(),person.getUserPriv());
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
   * 新建项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VoteTitle title = (T9VoteTitle)T9FOM.build(request.getParameterMap());
      //文件柜中上传附件
      String attachmentName = request.getParameter("attachmentName");
      String attachmentId = request.getParameter("attachmentId");
      T9SelAttachUtil sel = new T9SelAttachUtil(request,"profsys");
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !"".equals(attachmentId) &&  !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !"".equals(attachmentName)  && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;

      title.setAttachmentId(attachmentId);
      title.setAttachmentName(attachmentName);
      if (title.getBeginDate() == null) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        title.setBeginDate(T9Utility.parseSqlDate(sf.format(new Date())));
      }
      String date = T9VoteTitleLogic.addVote(dbConn,title);
      date = "{seqId:" + date + "}";
      request.setAttribute(T9ActionKeys.RET_DATA,date);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 单文件附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    try{
      T9VoteTitleLogic titleLogic = new T9VoteTitleLogic();
      attr = titleLogic.fileUploadLogic(fileForm);
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }


  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = T9VoteTitleLogic.selectVote(dbConn, request.getParameterMap(),person.getSeqId(),person.getUserPriv());
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
   *seqId串转换成privName,userName,deptName串
   */
  public String strString(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String tableName = request.getParameter("tableName");
      String tdName = request.getParameter("tdName");
      String data = T9VoteTitleLogic.strString(dbConn,seqId,tableName,tdName);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VoteTitle title = (T9VoteTitle)T9FOM.build(request.getParameterMap());

      //文件柜中上传附件
      String attachmentName = request.getParameter("attachmentName");
      String attachmentId = request.getParameter("attachmentId");
      T9SelAttachUtil sel = new T9SelAttachUtil(request,"profsys");
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !"".equals(attachmentId) &&  !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !"".equals(attachmentName)  && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;
      title.setAttachmentId(attachmentId);
      title.setAttachmentName(attachmentName);
      if (title.getBeginDate() == null) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        title.setBeginDate(T9Utility.parseSqlDate(sf.format(new Date())));
      }
      T9VoteTitleLogic.updateVote(dbConn, title);

      String strSeqId = null;
      String smsSJ = request.getParameter("smsSJ");//手机短信
      String smsflag = request.getParameter("smsflag");//内部短信
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      if (title.getPublish().equals("1") && title.getTop().equals("1")) {
        if (title.getToId().equals("0") || title.getToId().equals("ALL_DEPT")) {
          strSeqId = T9VoteTitleLogic.strSeqId(dbConn,"",title.getToId(),"");//部门为ALL_DEPT，0
        }
        if (!title.getToId().equals("0") && !title.getToId().equals("ALL_DEPT")) {
          strSeqId = T9VoteTitleLogic.strSeqId(dbConn,title.getUserId(),title.getToId(),title.getPrivId());
        }
        if (smsflag.equals("1")) {
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("11");
          sb.setContent("请查看投票！\n 标题：" + title.getSubject());
          sb.setSendDate(new java.util.Date());
          sb.setFromId(person.getSeqId());
          sb.setToId(strSeqId);
          sb.setRemindUrl("/subsys/oa/vote/show/readVote.jsp?seqId=" + title.getSeqId() + "&openFlag=1&openWidth=780&openHeight=500");
          T9SmsUtil.smsBack(dbConn,sb);
        }
        //手机消息提醒
        if (smsSJ.equals("1")) {
          T9MobileSms2Logic sb2 = new T9MobileSms2Logic();
          sb2.remindByMobileSms(dbConn,strSeqId,person.getSeqId(),"请查看投票！\n 标题：" + title.getSubject(),new java.util.Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 详细信息
   * */
  public String showDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9VoteTitle title = null;
      if (!T9Utility.isNullorEmpty(seqId)) {
        title = T9VoteTitleLogic.showDetail(dbConn, Integer.parseInt(seqId));
      }
      //定义数组将数据保存到Json中
      String data = "";
      if(title != null) {
        data = data + T9FOM.toJson(title);
        data = data.replaceAll("\\n", "");
        data = data.replaceAll("\\r", "");
      }
      data = data + "";
      if(data.equals("")){
        data = "{}";
      }
      //保存查询数据是否成功，保存date
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 查询投票项seqId
   * */
  public String selectId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        String[] str = {"VOTE_ID='" + seqId + "'"};
        //定义数组将数据保存到Json中
        List<T9VoteItem> list = T9VoteItemLogic.selectItem(dbConn,str);
        //遍历返回的list，将数据保存到Json中
        String data = "[";
        T9VoteItem item = new T9VoteItem();; 
        for (int i = 0; i < list.size(); i++) {
          item = list.get(i);
          data = data + T9FOM.toJson(item).toString()+",";
        }
        if(list.size()>0){
          data = data.substring(0, data.length()-1);
        }
        data = data.replaceAll("\\n", "");
        data = data.replaceAll("\\r", "");
        data = data + "]";
        //保存查询数据是否成功，保存date
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 查询子投票项seqId
   * */
  public String selectId2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        String[] str = {"PARENT_ID='" + seqId + "'"};
        //定义数组将数据保存到Json中
        List<T9VoteTitle> list = T9VoteTitleLogic.selectTitle(dbConn, str);
        //遍历返回的list，将数据保存到Json中
        String data = "[";
        T9VoteTitle title = new T9VoteTitle();; 
        for (int i = 0; i < list.size(); i++) {
          title = list.get(i);
          data = data + T9FOM.toJson(title).toString()+",";
        }
        if(list.size()>0){
          data = data.substring(0, data.length()-1);
        }
        data = data.replaceAll("\\n", "");
        data = data.replaceAll("\\r", "");
        data = data + "]";
        //保存查询数据是否成功，保存date
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showVote(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String parentId = request.getParameter("seqId");
      String data = T9VoteTitleLogic.showVote(dbConn,request.getParameterMap(),parentId);
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
   *立即生效,立即终止,恢复终止
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateBeginDate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String tdName = request.getParameter("tdName");
      String dayTime = request.getParameter("dayTime");
      if (!T9Utility.isNullorEmpty(seqId)) {
        if (!T9Utility.isNullorEmpty(dayTime)) {
          T9VoteTitleLogic.updateBeginDate(dbConn,Integer.parseInt(seqId),tdName,T9Utility.parseSqlDate(dayTime));
        }
        if (T9Utility.isNullorEmpty(dayTime)) {
          T9VoteTitleLogic.updateBeginDate(dbConn,Integer.parseInt(seqId),tdName,null);
        }
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

  /**
   *立即发布
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePublish(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String publish = request.getParameter("publish");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9VoteTitleLogic.updatePublish(dbConn,Integer.parseInt(seqId),publish ,loginUser.getSeqId() );
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   *更新投票人

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateReaders(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      String data = "";
      String isReader = "0";
      if (T9Utility.isInteger(seqId)) {
        T9VoteTitle title = T9VoteTitleLogic.showDetail(dbConn, Integer.parseInt(seqId));
        if (!T9Utility.isNullorEmpty(title.getReaders())) {
          String[] readersArray = title.getReaders().split(",");
          for (int i = 0; i < readersArray.length; i++) {
            if (readersArray[i].equals(person.getSeqId())) {
              isReader = "1";
              break;
            }
          }
        }
        if (isReader.equals("0")) {
          String readers = "";
          if(!T9Utility.isNullorEmpty(title.getReaders())){
            if(title.getReaders().endsWith(",")){
              readers = title.getReaders() + person.getSeqId();
            }else{
              readers = title.getReaders() + "," + person.getSeqId();
            }
          }else{
            readers = person.getSeqId() + "";
          }
          T9VoteTitleLogic.updateReaders(dbConn, Integer.parseInt(seqId), readers);
        }
      }
      data = "{isReader:" + isReader + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *导出投票数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String outVote (HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      OutputStream ops = null;
      String fileName = URLEncoder.encode("投票信息.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();

      T9VoteTitleLogic titleLogic = new T9VoteTitleLogic();
      String seqId = request.getParameter("seqId");
      T9VoteTitle voteItem = null;
      List<T9VoteTitle> listItem = new ArrayList<T9VoteTitle>();
      if(!T9Utility.isNullorEmpty(seqId)){
        //父级投票项的投票项---- 一级标题只有一个
        T9VoteTitle title = T9VoteTitleLogic.showDetail(dbConn,Integer.parseInt(seqId));//根据选中的seq_id字符串得到投票记录
        String str[] = {"VOTE_ID='" + seqId +"'"};
        List<T9VoteItem> item = T9VoteItemLogic.selectItem(dbConn,str);
        T9VoteItem itemGet = new T9VoteItem();     
        if (item.size() > 0 && title != null) {
          for (int i = 0; i < item.size(); i ++) {
            itemGet = item.get(i);
            //循环添加数据
            voteItem = new T9VoteTitle();
            if (i > 0) {
              voteItem.setSubject("");
            } else {
              voteItem.setSubject(title.getSubject());
            }
            voteItem.setFromId(itemGet.getVoteUser());
            voteItem.setParentId(itemGet.getVoteCount());
            voteItem.setContent((i + 1) + "、" + itemGet.getItemName());
            listItem.add(voteItem);
          }
        }
        if (item.size() <= 0 && title != null) {
          //循环添加数据
          voteItem = new T9VoteTitle();
          voteItem.setSubject(title.getSubject());
          voteItem.setFromId("");
          voteItem.setParentId(0);
          voteItem.setContent("");
          listItem.add(voteItem);
        }
        //子投票项的投票项----二级标题可有多个
        String str2[] = {"PARENT_ID='" + seqId +"'"};
        List<T9VoteTitle> titleList = titleLogic.selectTitle(dbConn,str2);//根据选中的seq_id字符串得到投票记录
        T9VoteTitle voteTile = new T9VoteTitle();
        T9VoteItem itemGet2 = new T9VoteItem();
        if (titleList.size() > 0) {
          for (int i = 0; i < titleList.size(); i ++) {
            voteTile = titleList.get(i);
            String strItem[] = {"VOTE_ID='" + voteTile.getSeqId() +"'"};
            List<T9VoteItem> itemList = T9VoteItemLogic.selectItem(dbConn,strItem);
            if (itemList.size() > 0) {
              for (int j = 0; j < itemList.size(); j ++) {
                itemGet2 = itemList.get(j);
                //循环添加数据
                voteItem = new T9VoteTitle();
                if (j > 0) {
                  voteItem.setSubject("");
                } else {
                  voteItem.setSubject(voteTile.getSubject());
                }
                voteItem.setFromId(itemGet2.getVoteUser());
                voteItem.setParentId(itemGet2.getVoteCount());
                voteItem.setContent((j + 1) + "、"+ itemGet2.getItemName());
                listItem.add(voteItem);
              }
            } else {
              //循环添加数据
              voteItem = new T9VoteTitle();
              voteItem.setSubject(voteTile.getSubject());
              voteItem.setFromId("");
              voteItem.setParentId(0);
              voteItem.setContent("");
              listItem.add(voteItem);
            }
          }
        }
        ArrayList<T9DbRecord> dbL = T9VoteTitleLogic.getDbRecord(dbConn,listItem);
        T9JExcelUtil.writeExc(ops, dbL);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导出数据成功！");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导出数据失败");
      throw e;
    }
    return null;
  }
}
