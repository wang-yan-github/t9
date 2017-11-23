package t9.subsys.oa.vote.act;

import java.sql.Connection;
import java.util.List;

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
import t9.subsys.oa.vote.data.T9VoteItem;
import t9.subsys.oa.vote.data.T9VoteTitle;
import t9.subsys.oa.vote.logic.T9VoteItemLogic;
import t9.subsys.oa.vote.logic.T9VoteTitleLogic;

public class T9VoteItemAct {
  /**
   * 新建
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String itemName = request.getParameter("itemName");
      String voteId = request.getParameter("voteId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      if(T9Utility.isInteger(voteId)){
        T9VoteItem item = new T9VoteItem();
        item.setItemName(itemName);
        item.setVoteId(Integer.parseInt(voteId));
        itemLogic.addItem(dbConn, item);
      }
     // request.setAttribute(T9ActionKeys.RET_DATA,data);
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
   * 更新
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      
      String itemName = request.getParameter("itemName");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      if(T9Utility.isInteger(seqId)){
        itemLogic.updateItem(dbConn, seqId, itemName);
      }
     // request.setAttribute(T9ActionKeys.RET_DATA,data);
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
   * 删除itemById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteItemById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      if(T9Utility.isInteger(seqId)){
        itemLogic.delItemById(dbConn, seqId);
      }
     // request.setAttribute(T9ActionKeys.RET_DATA,data);
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
   * 根据vote_id查询所有投票项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String selectItemByVoteId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER); 
      String voteId = request.getParameter("voteId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      String data = "[";
      long totalCount  = 0;
      long maxCount = 0;
      String count = "0,0";
      if(T9Utility.isInteger(voteId)){
        String[] str = {"vote_id = " + voteId + " order by SEQ_ID "};
        List<T9VoteItem> itemList = itemLogic.selectItem(dbConn, str);
        for (int i = 0; i < itemList.size(); i++) {
          String voteUserName = "";
          T9PersonLogic personLogic = new T9PersonLogic();
          if(!T9Utility.isNullorEmpty(itemList.get(i).getVoteUser())){
            voteUserName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(itemList.get(i).getVoteUser(), dbConn));
          }
          data = data + T9FOM.toJson(itemList.get(i)).toString().substring(0,T9FOM.toJson(itemList.get(i)).toString().length()-1 ) +",voteUserName:\"" +voteUserName + "\"},";
         // totalCount = totalCount + itemList.get(i).getVoteCount();
        }
        if(itemList.size()>0){
          data = data.substring(0,data.length()-1);
        }
        
        count = itemLogic.getCount(dbConn, voteId);
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, count);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据seq_Id
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getItemBySeqId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      String data = "";
      if(T9Utility.isInteger(seqId)){
       T9VoteItem item = itemLogic.selectItemById(dbConn, seqId);
       if(item != null){
         data = data + T9FOM.toJson(item).toString();
       }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA,data);
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
   * 根据parentId查询所有 子投票
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getChiidVoteByParent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String parentId = request.getParameter("parentId");
      T9VoteTitleLogic titleLogic = new T9VoteTitleLogic();
      String data = "[";
      if(T9Utility.isInteger(parentId)){
        String[] childStr = {"PARENT_ID = " + parentId };
        List<T9VoteTitle> childTitleList = titleLogic.selectTitle(dbConn, childStr);  
        for (int i = 0; i < childTitleList.size(); i++) {
          data = data + T9FOM.toJson(childTitleList.get(i)) + ",";
        }
        if(childTitleList.size()>0){
          data = data.substring(0,data.length()-1);
        }
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
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
   * 投票项目更新数据
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateUserId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqIds = request.getParameter("seqIds");
      String anonymity = request.getParameter("anonymity");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      if(!T9Utility.isNullorEmpty(seqIds)){
        if(seqIds.endsWith(",")){
          seqIds = seqIds.substring(0, seqIds.length()-1);
        }
        String[] str = {"SEQ_ID in(" + seqIds + ")" };
        List<T9VoteItem> itemList = itemLogic.selectItem(dbConn, str);  
        for (int i = 0; i < itemList.size(); i++) {
          T9VoteItem item = itemList.get(i);
          String voteUser = "";
          if(!T9Utility.isNullorEmpty(item.getVoteUser())){
            if(item.getVoteUser().endsWith(",")){
              voteUser = item.getVoteUser() + user.getSeqId();
            }else{
              voteUser = item.getVoteUser() + "," + user.getSeqId();
            }
          }else{
            voteUser = user.getSeqId() + "";
          }
          itemLogic.updateItemUserId(dbConn, item.getSeqId(), anonymity, voteUser);
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
}
