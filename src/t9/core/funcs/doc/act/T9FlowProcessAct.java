package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowProcess;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowFormLogic;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.logic.T9FlowUserSelectLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.doc.util.sort.T9FlowProcessComparator;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.T9Utility;
public class T9FlowProcessAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.doc.act.T9FlowProcessAct");
  
  public void setFlowProcess(HttpServletRequest request,
      T9DocFlowProcess flowProcess) {
    flowProcess.setFlowSeqId(Integer
        .parseInt(request.getParameter("flowSeqId")));
    flowProcess.setPrcsId(Integer.parseInt(request.getParameter("prcsId")));
    int childFlow = Integer.parseInt(request.getParameter("childFlow"));

    if (childFlow == 1) {
      flowProcess.setChildFlow(Integer.parseInt(request
          .getParameter("childFlowName")));
      flowProcess.setPrcsName(request.getParameter("prcsName"));
      flowProcess.setAllowBack(request.getParameter("copyAttach"));
      String prcsBack = request.getParameter("prcsBack");
      flowProcess.setPrcsTo(prcsBack);

      if (!request.getParameter("prcsBack").equals("")) {
        flowProcess.setAutoUser(request.getParameter("backUserOp"));
        flowProcess.setAutoUserOp(request.getParameter("backUserHo"));
      }
      flowProcess.setRelation(request.getParameter("relation"));
    } else if (childFlow == 0) {
      flowProcess.setPrcsName(request.getParameter("prcsName"));
      flowProcess.setPrcsTo(request.getParameter("prcsTo"));

      // 第二页
      if (request.getParameter("openedAutoSelect").equals("1")) {
        flowProcess.setUserFilter(request.getParameter("userFilter"));
        String autoType = request.getParameter("autoType");
        flowProcess.setAutoType(autoType);
        if (autoType != null) {
          if (autoType.equals("3")) {
            // 设置主办Ho，经办Op
            flowProcess.setAutoUser(request.getParameter("autoUserOp"));
            flowProcess.setAutoUserOp(request.getParameter("autoUserHo"));
          } else if (autoType.equals("2") || autoType.equals("4")
              || autoType.equals("6")) {
            flowProcess.setAutoBaseUser(Integer.parseInt(request
                .getParameter("autoBaseUser")));
          } else if (autoType.equals("7")) {
            flowProcess.setAutoUser(request.getParameter("formListItem"));
          } else if (autoType.equals("8")) {
            flowProcess.setAutoBaseUser(Integer.parseInt(request
                .getParameter("autoPrcsUser")));
          }else if (autoType.equals("20")) {
            flowProcess.setAutoSelectRole(request
                .getParameter("roleListItem"));
          }
        }
      }
      // 第三页
      if (request.getParameter("openedFlowDispatch").equals("1")) {
        flowProcess.setTopDefault(request.getParameter("topDefault"));
        flowProcess.setUserLock(request.getParameter("userLock"));
        String feedBack = request.getParameter("feedBack");
        flowProcess.setFeedback(feedBack);
        if (feedBack != null) {
          if (feedBack.equals("0") || feedBack.equals("2")) {
            flowProcess.setSignlook(request.getParameter("signLook"));
          }
        }
        flowProcess.setTurnPriv(request.getParameter("turnPriv"));
        flowProcess.setAllowBack(request.getParameter("allowBack"));
        flowProcess.setSyncDeal(request.getParameter("syncDeal"));
        flowProcess.setGatherNode(request.getParameter("gatherNode"));
      }

      // 第四页
      if (request.getParameter("openedWarnDispatch").equals("1")) {
        String sRemindOrnot = request.getParameter("remindOrnot");
        flowProcess.setMailTo(request.getParameter("user"));
        int remindFlag = 0;
        if (sRemindOrnot != null && sRemindOrnot.equals("on")) {
          String sSmsRemindNext = request.getParameter("smsRemindNext");
          String sSms2RemindNext = request.getParameter("sms2RemindNext");
          String sWebMailRemindNext = request.getParameter("webMailRemindNext");

          String sSmsRemindStart = request.getParameter("smsRemindStart");
          String sSms2RemindStart = request.getParameter("sms2RemindStart");
          String sWebMailRemindStart = request
              .getParameter("webMailRemindStart");

          String sSmsRemindAll = request.getParameter("smsRemindAll");
          String sSms2RemindAll = request.getParameter("sms2RemindAll");
          String sWebMailRemindAll = request.getParameter("webMailRemindAll");
          remindFlag = T9WorkFlowUtility.getRemindFlag(sSmsRemindNext, sSms2RemindNext, sWebMailRemindNext, sSmsRemindStart, sSms2RemindStart, sWebMailRemindStart, sSmsRemindAll, sSms2RemindAll, sWebMailRemindAll);
        }
        flowProcess.setRemindFlag(remindFlag);
      }

      // 第五页
      if (request.getParameter("openedOtherDispatch").equals("1")) {
        String timeOut = request.getParameter("timeOut");
        flowProcess.setTimeOut(timeOut);
        flowProcess.setTimeOutType(request.getParameter("timeOutTypeDesc"));

        String timeExcept1 = request.getParameter("timeExcept1");
        String timeExcept2 = request.getParameter("timeExcept2");
        String extend = request.getParameter("extend");
        String extend1 = request.getParameter("extend1");
        if (timeExcept1 == null && timeExcept2 == null) {
          flowProcess.setTimeExcept("00");
        }
        if (timeExcept1 == null && timeExcept2 != null) {
          flowProcess.setTimeExcept("01");
        }
        if (timeExcept1 != null && timeExcept2 != null) {
          flowProcess.setTimeExcept("11");
        }
        if (timeExcept1 != null && timeExcept2 == null) {
          flowProcess.setTimeExcept("10");
        }
        String dispAip = request.getParameter("dispAip");
        if (dispAip == null || dispAip.equals("")) {
          flowProcess.setDispAip(0);
        } else {
          flowProcess.setDispAip(Integer.parseInt(dispAip));
        }
        flowProcess.setPlugin(request.getParameter("plugin"));
        flowProcess.setExtend(extend);
        flowProcess.setExtend1(extend1);
      }
    }
  }

  public String doSave(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();

      T9DocFlowProcess flowProcess = new T9DocFlowProcess();

      setFlowProcess(request, flowProcess);
      logic.saveFlowProcess(flowProcess , dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getAddMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String sFlowId = request.getParameter("flowId");
    int flowId = Integer.parseInt(sFlowId);
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      List<Map> typeList = flowTypelogic.getFlowTypeListByType( dbConn , 1);
      int max = 0;
      List<Map> list = logic.getFlowPrcsByFlowId(flowId , dbConn);
      T9UserPrivLogic role = new T9UserPrivLogic();
      T9WorkFlowUtility utility = new T9WorkFlowUtility();
      
      String roles = utility.getRoleJson(dbConn);
      StringBuffer sb = new StringBuffer("{");
      sb.append("prcsList:[");
      for (Map t : list) {
        int prcsId = (Integer)t.get("prcsId");
        int seqId = (Integer)t.get("seqId");
        String prcsName = (String)t.get("prcsName");
        if (prcsId > max) {
          max = prcsId;
        }
        sb.append("{");
        sb.append("prcsId:" + prcsId + ",");
        sb.append("seqId:" + seqId + ",");
        sb.append("prcsName:'" + prcsName + "'");
        sb.append("},");
      }
      sb.append("{prcsId:0,seqId:0,prcsName:'[结束流程]'}");
      String query = "select FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" WHERE SEQ_ID =" + flowId;
      int formId = flowTypelogic.getIntBySeq(query, dbConn) ;
      String  fromItem = ffLogic.getFormJson(dbConn, formId);
      sb.append("],maxPrcsId:" + (max + 1) + ",fromItem:" + fromItem + ",");
      sb.append("flowList:[");
      for(Map ft : typeList){
        sb.append("{");
        sb.append("value:" + ft.get("seqId") + ",");
        sb.append("text:'" + ft.get("flowName") + "'");
        sb.append("},");
      }
      if(typeList.size() > 0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("],disAip:");
      sb.append(logic.getDisAip(flowId, dbConn));
      sb.append(",role:" + roles);
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getEditMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String sFlowId = request.getParameter("flowId");
    int flowId = Integer.parseInt(sFlowId);
    String sSeqId = request.getParameter("seqId");
    int seqId = Integer.parseInt(sSeqId);
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      T9PersonLogic pLogic = new T9PersonLogic();
      T9UserPrivLogic role = new T9UserPrivLogic();
      T9WorkFlowUtility utility = new T9WorkFlowUtility();
      
      String roles = utility.getRoleJson(dbConn);
      
      List<Map> typeList = flowTypelogic.getFlowTypeListByType( dbConn , 1);
      List<Map> list = logic.getFlowPrcsByFlowId(flowId , dbConn);
      T9DocFlowProcess fp = logic.getFlowProcessById(seqId, dbConn);
      
      
      StringBuffer sb = new StringBuffer("{");
      sb.append("prcsList:[");
      for(Map t : list){
        int prcsId = (Integer)t.get("prcsId");
        int seqId2 = (Integer)t.get("seqId");
        String prcsName = (String)t.get("prcsName");
        sb.append("{");
        sb.append("prcsId:" + prcsId+",");
        sb.append("seqId:" + seqId2 + ",");
        sb.append("prcsName:'" + prcsName +"'");
        sb.append("},");
      }
      sb.append("{prcsId:0,seqId:0,prcsName:'[结束流程]'}");
      
      String query = "select FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" WHERE SEQ_ID =" + flowId;
      int formId = flowTypelogic.getIntBySeq(query, dbConn) ;
      String  fromItem = ffLogic.getFormJson(dbConn, formId);
      
      sb.append("],fromItem:" + fromItem + ",");
      sb.append("flowList:[");
      for(Map ft : typeList){
        sb.append("{");
        sb.append("value:" + ft.get("seqId") + ",");
        sb.append("text:'" + ft.get("flowName") + "'");
        sb.append("},");
      }
      if(typeList.size() > 0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("],disAip:");
      sb.append(logic.getDisAip(flowId, dbConn));
      sb.append(",prcsNode:" + fp.toJSON() + ",");
      String backUserIdOp = fp.getAutoUser();
      String backUserHo = fp.getAutoUserOp();
      String backUserHoDesc = pLogic.getNameBySeqIdStr(backUserHo , dbConn);
      String backUserOpDesc = pLogic.getNameBySeqIdStr(backUserIdOp , dbConn);
      if(backUserHoDesc != null 
          && backUserHoDesc.endsWith(",")){
        backUserHoDesc = backUserHoDesc.substring(0, backUserHoDesc.length() -1 );
      }
      if  (!T9Utility.isNullorEmpty(backUserOpDesc) 
          && !backUserOpDesc.endsWith(",")) {
        backUserOpDesc += ",";
      }
      sb.append("backUsers:{backUserHoDesc:'"+ backUserHoDesc  +"',backUserOpDesc:'" + backUserOpDesc + "'},");
      sb.append("autoUsers:{autoUserHoName:'"+ backUserHoDesc  +"',autoUserOpDesc:'" + backUserOpDesc + "'},");
      String mailToDesc = pLogic.getNameBySeqIdStr(fp.getMailTo() , dbConn);;
      sb.append("mailToDesc:'" + mailToDesc + "',");
      String formItem = "";
      if(fp.getChildFlow() != 0){
        String query2 = "select FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" WHERE SEQ_ID =" + fp.getChildFlow();
        int formId2 = flowTypelogic.getIntBySeq(query2, dbConn) ;
        formItem = ffLogic.getTitle(dbConn, formId2);
      }
      sb.append("childItem:'" + formItem + "',");
      sb.append("role:" + roles);
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String doUpdate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    int seqId = Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();

      T9DocFlowProcess fp = logic.getFlowProcessById(seqId , dbConn);
      this.setFlowProcess(request, fp);
      logic.updateFlowProcess(fp , dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getProcessList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    String startColor = "#50A625";
    String childColor = "#70A0DD";
    String processColor = "#EEEEEE";
    String endColor = "#F4A8BD";
    int leftAuto = 20;
    int topAuto = 50;
    try {
      // {prcsId:1,prcsName:'开始',tableId:12,flowType:'start',flowTitle:'开始',fillcolor:'#50A625',leftVml:'21',topVml:'85',prcsTo:'2'}
      // ,{prcsId:2,prcsName:'审批',tableId:13,flowType:'',flowTitle:'审批',fillcolor:'#EEEEEE',leftVml:'246',topVml:'299',prcsTo:'1,3,4'}
      // ,{prcsId:4,prcsName:'审批2',tableId:16,flowType:'child',flowTitle:'审批2',fillcolor:'#70A0DD',leftVml:'344',topVml:'333',prcsTo:'5'}
      // ,{prcsId:5,prcsName:'条件',tableId:17,flowType:'',flowTitle:'条件',fillcolor:'#EEEEEE',leftVml:'344',topVml:'333',prcsTo:'3',condition:'ddddd'}
      // ,{prcsId:3,prcsName:'结束',tableId:14,flowType:'end',flowTitle:'结束',fillcolor:'#F4A8BD',leftVml:'454',topVml:'124',prcsTo:'0'}];
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      List<T9DocFlowProcess> list = logic.getFlowProcessByFlowId(Integer
          .parseInt(flowId) , dbConn);
      Collections.sort(list,new T9FlowProcessComparator());
      T9DocFlowType flowType = flowTypelogic.getFlowTypeById(Integer
          .parseInt(flowId), dbConn);
      boolean isSetPriv = true;
      StringBuffer sb = new StringBuffer("{prcsList:[");
      int min = logic.getMinProcessId(Integer.parseInt(flowId) , dbConn);
      for (int i = 0; i < list.size(); i++) {
        T9DocFlowProcess t = list.get(i);
        int leftVml = leftAuto;
        int topVml = topAuto;
        if (t.getSetTop() != 0) {
          topVml = t.getSetTop();
        }
        if (t.getSetLeft() != 0) {
          leftVml = t.getSetLeft();
        }
        boolean isHave0 = false;
        String prcsTo = t.getPrcsTo();
        if (prcsTo == null || "".equals(prcsTo)) {
          if(i + 1 == list.size()){
            prcsTo = "0";
          }else{
            T9DocFlowProcess t2 = list.get(i + 1);
            prcsTo = t2.getPrcsId() + "";
          }
        }
        String[] aPrcsTo = prcsTo.split(",");
        for (String s : aPrcsTo) {
          if (s.equals("0")) {
            isHave0 = true;
          }
        }
        sb.append("{");
        sb.append("prcsId:" + t.getPrcsId() + ",");
        if(t.getPrcsId() == 1){
          String prcsDept = T9OrgSelectLogic.changeDept(dbConn, t.getPrcsDept());
          String prcsUser = t.getPrcsUser();
          String prcsPriv = t.getPrcsPriv();
          if((prcsDept != null && !"".equals(prcsDept.trim()))
              || (prcsUser != null && !"".equals(prcsUser.trim()))
              || (prcsPriv != null && !"".equals(prcsPriv.trim()))){
            isSetPriv = true;
          }else{
            isSetPriv = false;
          }
        }
        sb.append("prcsName:'" + t.getPrcsName() + "',");
        sb.append("tableId:" + t.getSeqId() + ",");

        if (t.getChildFlow() != 0) {
          sb.append("flowType:'child',");
          sb.append("fillcolor:'" + childColor + "',");
        } else if (1 == t.getPrcsId()) {
          sb.append("flowType:'start',");
          sb.append("fillcolor:'" + startColor + "',");
        } else if (isHave0) {
          sb.append("flowType:'end',");
          sb.append("fillcolor:'" + endColor + "',");
        } else {
          sb.append("flowType:'',");
          sb.append("fillcolor:'" + processColor + "',");
        }

        sb.append("flowTitle:'" + t.getPrcsName() + "',");
        sb.append("leftVml:" + leftVml + ",");
        sb.append("topVml:" + topVml + ",");
        sb.append("prcsTo:'" + prcsTo + "'");
        String prcsIn = t.getPrcsIn();
        String prcsOut = t.getPrcsOut();
        if ((prcsIn != null && !"".equals(prcsIn))
            || (prcsOut != null && !"".equals(prcsOut))) {
          String condition = "";
          if (t.getPrcsIn() != null) {
            String prcsInStr = t.getPrcsIn().replaceAll("'include'", "'包含'");
            prcsInStr = prcsInStr.replaceAll("'exclude'", "'不包含'");
            //prcsInStr = prcsInStr.replace("\n", "");
            condition += "<br>·转入条件列表：" + prcsInStr.trim();
          }
          if (t.getPrcsInSet() != null && !"".equals(t.getPrcsInSet())) {
            String str = t.getPrcsInSet().trim();
            //str = str.replace("\n", "");
            condition += "<br>·转入条件公式：" + str;
          }
          if (t.getPrcsOut() != null) {
            String prcsOutStr = t.getPrcsOut().replaceAll("'include'", "'包含'");
            prcsOutStr = prcsOutStr.replaceAll("'exclude'", "'不包含'");
            //prcsOutStr = prcsOutStr.replace("\n", "");
            condition += "<br>·转出条件列表：" + prcsOutStr.trim();
          }
          if (t.getPrcsOutSet() != null && !"".equals(t.getPrcsOutSet())) {
            String str = t.getPrcsOutSet().trim();
            //str = str.replace("\n", "");
            condition += "<br>·转出条件公式：" + str;
          }
          sb.append(",condition:\"" + condition + "\"");
        }
        sb.append("},");
        if (i % 2 == 0) {
          leftAuto += 180;
          topAuto = 50;
        } else {
          topAuto = 230;
        }
      }

      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("],isSetPriv:"+ isSetPriv +",flowName:'"+flowType.getFlowName()+"'}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getProcessList1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowId");
    String type = request.getParameter("type");
  
    Connection dbConn = null;
    String startColor = "#50A625";
    String childColor = "#70A0DD";
    String processColor = "#EEEEEE";
    String endColor = "#F4A8BD";
    int leftAuto = 20;
    int topAuto = 50;
    try {
      // {prcsId:1,prcsName:'开始',tableId:12,flowType:'start',flowTitle:'开始',fillcolor:'#50A625',leftVml:'21',topVml:'85',prcsTo:'2'}
      // ,{prcsId:2,prcsName:'审批',tableId:13,flowType:'',flowTitle:'审批',fillcolor:'#EEEEEE',leftVml:'246',topVml:'299',prcsTo:'1,3,4'}
      // ,{prcsId:4,prcsName:'审批2',tableId:16,flowType:'child',flowTitle:'审批2',fillcolor:'#70A0DD',leftVml:'344',topVml:'333',prcsTo:'5'}
      // ,{prcsId:5,prcsName:'条件',tableId:17,flowType:'',flowTitle:'条件',fillcolor:'#EEEEEE',leftVml:'344',topVml:'333',prcsTo:'3',condition:'ddddd'}
      // ,{prcsId:3,prcsName:'结束',tableId:14,flowType:'end',flowTitle:'结束',fillcolor:'#F4A8BD',leftVml:'454',topVml:'124',prcsTo:'0'}];
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      List<T9DocFlowProcess> list = logic.getFlowProcessByFlowId(Integer
          .parseInt(flowId) , dbConn);
      Collections.sort(list,new T9FlowProcessComparator());
      T9DocFlowType flowType = flowTypelogic.getFlowTypeById(Integer
          .parseInt(flowId), dbConn);
      boolean isSetPriv = true;
     // StringBuffer sb = new StringBuffer("{prcsList:[");
      
      List<Map> prcsList =  new ArrayList<Map>();
      int min = logic.getMinProcessId(Integer.parseInt(flowId) , dbConn);
      int firstPrcsSeqId = 0;
      for (int i = 0; i < list.size(); i++) {
        T9DocFlowProcess t = list.get(i);
        int leftVml = leftAuto;
        int topVml = topAuto;
        if (t.getSetTop() != 0) {
          topVml = t.getSetTop();
        }
        if (t.getSetLeft() != 0) {
          leftVml = t.getSetLeft();
        }
        boolean isHave0 = false;
        String prcsTo = t.getPrcsTo();
        if (prcsTo == null || "".equals(prcsTo)) {
          if(i + 1 == list.size()){
            prcsTo = "0";
          }else{
            T9DocFlowProcess t2 = list.get(i + 1);
            prcsTo = t2.getPrcsId() + "";
          }
        }
        String[] aPrcsTo = prcsTo.split(",");
        for (String s : aPrcsTo) {
          if (s.equals("0")) {
            isHave0 = true;
          }
        }
        Map map = new HashMap();
        map.put("prcsId", t.getPrcsId());
        if(t.getPrcsId() == 1){
          firstPrcsSeqId =  t.getSeqId();
        }
        if(t.getPrcsId() == 1){
          String prcsDept = T9OrgSelectLogic.changeDept(dbConn, t.getPrcsDept());
          String prcsUser = t.getPrcsUser();
          String prcsPriv = t.getPrcsPriv();
          if((prcsDept != null && !"".equals(prcsDept.trim()))
              || (prcsUser != null && !"".equals(prcsUser.trim()))
              || (prcsPriv != null && !"".equals(prcsPriv.trim()))){
            isSetPriv = true;
          }else{
            isSetPriv = false;
          }
        }
        map.put("prcsName", t.getPrcsName() );
        map.put("tableId", t.getSeqId() );

        if (t.getChildFlow() != 0) {
          map.put("flowType", "child");
          map.put("fillcolor", "childColor");
        } else if (1 == t.getPrcsId()) {
          map.put("flowType", "start");
          map.put("fillcolor", "startColor");
        } else if (isHave0) {
          map.put("flowType", "end");
          map.put("fillcolor", "endColor");
        } else {
          map.put("flowType", "");
          map.put("fillcolor", "processColor");
        }
        map.put("flowTitle",  t.getPrcsName() );
        map.put("leftVml", leftVml);
        map.put("topVml",   topVml);
        map.put("prcsTo", prcsTo);
        String prcsIn = t.getPrcsIn();
        String prcsOut = t.getPrcsOut();
        if ((prcsIn != null && !"".equals(prcsIn))
            || (prcsOut != null && !"".equals(prcsOut))) {
          String condition = "";
          if (t.getPrcsIn() != null) {
            String prcsInStr = t.getPrcsIn().replaceAll("'include'", "'包含'");
            prcsInStr = prcsInStr.replaceAll("'exclude'", "'不包含'");
            //prcsInStr = prcsInStr.replace("\n", "");
            condition += "<br>·转入条件列表：" + prcsInStr.trim();
          }
          if (t.getPrcsInSet() != null && !"".equals(t.getPrcsInSet())) {
            String str = t.getPrcsInSet().trim();
            //str = str.replace("\n", "");
            condition += "<br>·转入条件公式：" + str;
          }
          if (t.getPrcsOut() != null) {
            String prcsOutStr = t.getPrcsOut().replaceAll("'include'", "'包含'");
            prcsOutStr = prcsOutStr.replaceAll("'exclude'", "'不包含'");
            //prcsOutStr = prcsOutStr.replace("\n", "");
            condition += "<br>·转出条件列表：" + prcsOutStr.trim();
          }
          if (t.getPrcsOutSet() != null && !"".equals(t.getPrcsOutSet())) {
            String str = t.getPrcsOutSet().trim();
            //str = str.replace("\n", "");
            condition += "<br>·转出条件公式：" + str;
          }
          map.put("condition", condition);
        }
        if (i % 2 == 0) {
          leftAuto += 180;
          topAuto = 50;
        } else {
          topAuto = 230;
        }
        prcsList.add(map);
      }
      request.setAttribute("prcsList", prcsList);
      request.setAttribute("isSetPriv", isSetPriv);
      request.setAttribute("firstPrcsSeqId", firstPrcsSeqId);
      request.setAttribute("flowName", flowType.getFlowName());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    if (T9Utility.isNullorEmpty(type)) {
      return "/core/funcs/doc/flowdesign/canvas2.jsp?flowId=" + flowId;
      
    }else {
      return "/core/funcs/doc/flowrun/list/viewgraph/index2.jsp?flowId=" + flowId;
    }
  }
  public String getPersonsByDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      List<T9Person> list = logic.getPersonsByDept(Integer.parseInt(deptId) , dbConn);
      StringBuffer sb = new StringBuffer("[");
      for (T9Person p : list) {
        String userId = p.getUserId();
        String userName = p.getUserName();
        sb.append("{");
        sb.append("userId:'" + userId + "',");
        sb.append("userName:'" + userName + "'");
        sb.append("},");
      }
      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getDeptByProc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String procId = request.getParameter("seqId");
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      // T9FlowProcess proc =
      // logic.getFlowProcessById(Integer.parseInt(procId));
      T9DeptLogic deptLogic = new T9DeptLogic();
      List deptList = deptLogic.getDeptList(dbConn);
      StringBuffer sb;
      if (deptId == null || "".equals(deptId)) {
        sb = logic.getDeptJson(deptList, Integer.parseInt(procId) , dbConn);
      } else {
        sb = logic.getDeptJson(deptList, Integer.parseInt(procId), Integer
            .parseInt(deptId), dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getRoles(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic logic = new T9UserPrivLogic();
      // T9FlowProcess proc =
      // logic.getFlowProcessById(Integer.parseInt(procId));
      List<T9UserPriv> list = logic.getRoleList(dbConn);
      StringBuffer sb = new StringBuffer();
      for (T9UserPriv up : list) {
        sb.append(up.getJsonSimple());
      }
      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + sb.toString() + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getUsers(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PersonLogic logic = new T9PersonLogic();
      T9FlowProcessLogic fp = new T9FlowProcessLogic();
      T9DocFlowProcess proc = fp.getFlowProcessById(Integer.parseInt(seqId) , dbConn);

      String ids = proc.getPrcsUser();

      String data = logic.getPersonSimpleJson(ids , dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + data + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getPriv(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9PersonLogic personLogic = new T9PersonLogic();
      T9DeptLogic deptLogic = new T9DeptLogic();
      T9UserPrivLogic privLogic = new T9UserPrivLogic();

      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);

      String deptIds = fp.getPrcsDept();
      
      if (deptIds == null) {
        deptIds = "";
      }
      String deptNames = "";
      if ("0".equals(deptIds)) {
        deptNames = "全体部门";
      } else {
        deptNames = deptLogic.getNameByIdStr(deptIds , dbConn);
      }
      
      String privIds = fp.getPrcsPriv();
      if (privIds == null) {
        privIds = "";
      }
      String userIds = fp.getPrcsUser();
      if (userIds == null) {
        userIds = "";
      }

      
      String privNames = privLogic.getNameByIdStr(privIds , dbConn);
      Map map = personLogic.getMapBySeqIdStr(userIds , dbConn);
      String userNames = (String)map.get("name");
      userIds = (String)map.get("id");
      StringBuffer sb = new StringBuffer("{");
      sb.append("userId:'" + userIds + "',");
      sb.append("deptId:'" + deptIds + "',");
      sb.append("privId:'" + privIds + "',");

      sb.append("userName:'" + userNames + "',");
      sb.append("deptName:'" + deptNames + "',");
      sb.append("privName:'" + privNames + "'");

      sb.append("}");

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String savePriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String procId = request.getParameter("procId");
    String userId = request.getParameter("user");
    if (userId == null) {
      userId = "";
    }
    String deptId = request.getParameter("dept");
    if (deptId == null) {
      deptId = "";
    }
    String privId = request.getParameter("role");
    if (privId == null) {
      privId = "";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(procId) , dbConn);
      fp.setPrcsDept(deptId);
      fp.setPrcsPriv(privId);
      fp.setPrcsUser(userId);
      logic.updateFlowProcess(fp , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getMetadataMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String sFlowId = request.getParameter("flowId");
    int flowId = 0 ;
    if (T9Utility.isInteger(sFlowId)) {
      flowId = Integer.parseInt(sFlowId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int formId = fru.getFormId(dbConn, flowId);
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      String items = fp.getMetadataItem();
      if (items == null) {
        items = "";
      }
      StringBuffer sb = new StringBuffer("{");
      sb.append("procId:'" + fp.getPrcsId() + "',");
      sb.append("items:["+ffLogic.getMetaDataItem(formId, dbConn)+"]");
      sb.append(",selectedItem:'"+ items +"'");
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
    
  }
  public String getFieldMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9DocFlowType ft = flowTypelogic.getFlowTypeById(Integer.parseInt(flowId) , dbConn);
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      String flowDoc = ft.getFlowDoc();
      int formId = ft.getFormSeqId();
      String formItem = ffLogic.getTitle(dbConn, formId);

      if (flowDoc.equals("1")) {
        formItem += ",[B@],[A@],";
      } else {
        formItem += ",[B@],";
      }

      StringBuffer sb = new StringBuffer("{");
      sb.append("procId:'" + fp.getPrcsId() + "',");
      sb.append("items:'" + formItem + "',");
      sb.append("hiddenItem:'"
          + (fp.getHiddenItem() != null ? fp.getHiddenItem() : "") + "',");
      sb.append("prcsItem:'"
          + (fp.getPrcsItem() != null ? fp.getPrcsItem() : "") + "',");
      sb.append("attachPriv:'"
          + (fp.getAttachPriv() != null ? fp.getAttachPriv() : "") + "',");
      sb.append("itemAuto:'"
          + (fp.getPrcsItemAuto() != null ? fp.getPrcsItemAuto() : "") + "'");

      sb.append("}");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getDocMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);

      StringBuffer sb = new StringBuffer("{");
      sb.append("procId:'" + fp.getPrcsId() + "',");
      sb.append("docCreate:'"
          + (fp.getDocCreate() != null ? fp.getDocCreate() : "") + "',");
      sb.append("extend:'"
          + (fp.getExtend() != null ? fp.getExtend() : "") + "',");
      sb.append("extend1:'"
          + (fp.getExtend1() != null ? fp.getExtend1() : "") + "',");
      sb.append("attachPriv:'"
          + (fp.getDocAttachPriv() != null ? fp.getDocAttachPriv() : "") + "',");
      sb.append("docSmsStyle:\""
          + T9Utility.encodeSpecial(T9Utility.null2Empty(fp.getDocSmsStyle())) + "\",");
      sb.append("docSendFlag:\""
          + T9Utility.encodeSpecial(T9Utility.null2Empty(fp.getDocSendFlag())) + "\"");
      sb.append("}");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String setDocItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    String fieldStr = request.getParameter("fieldStr");
    String attachPriv = "";
    String privEdit = request.getParameter("privEdit");
    String privDel = request.getParameter("privDel");
    String privOfficeDown = request.getParameter("privOfficeDown");
    String privOfficePrint = request.getParameter("privOfficePrint");
    String docSmsStyle = T9Utility.null2Empty(request.getParameter("docSmsStyle"));
    String docSendFlag = T9Utility.null2Empty(request.getParameter("DOC_SEND_FLAG"));
    if (privEdit != null) {
      attachPriv += "2,";
    }
    if (privDel != null) {
      attachPriv += "3,";
    }
    if (privOfficeDown != null) {
      attachPriv += "4,";
    }
    if (privOfficePrint != null) {
      attachPriv += "5,";
    }
    int seqId = Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      
      String extend1 = T9Utility.null2Empty(request.getParameter("extend1"));
      String extend = T9Utility.null2Empty(request.getParameter("extend"));
      String docCreate = T9Utility.null2Empty(request.getParameter("DOC_CREATE"));
      T9DocFlowProcess fp = logic.getFlowProcessById(seqId , dbConn);
      fp.setDocAttachPriv(attachPriv);
      fp.setExtend1(extend1);
      fp.setExtend(extend);
      fp.setDocCreate(docCreate);
      fp.setDocSmsStyle(docSmsStyle);
      fp.setDocSendFlag(docSendFlag);
      logic.updateFlowProcess(fp , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String setFormItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    String fieldStr = request.getParameter("fieldStr");
    String type = request.getParameter("type");
    String attachPriv = "";
    if (type.equals("isWrite")) {
      String privNew = request.getParameter("privNew");
      String privEdit = request.getParameter("privEdit");
      String privDel = request.getParameter("privDel");
      String privOfficeDown = request.getParameter("privOfficeDown");
      String privOfficePrint = request.getParameter("privOfficePrint");
      if (privNew != null) {
        attachPriv += "1,";
      }
      if (privEdit != null) {
        attachPriv += "2,";
      }
      if (privDel != null) {
        attachPriv += "3,";
      }
      if (privOfficeDown != null) {
        attachPriv += "4,";
      }
      if (privOfficePrint != null) {
        attachPriv += "5,";
      }
    }
    int seqId = Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();

      T9DocFlowProcess fp = logic.getFlowProcessById(seqId , dbConn);
      if (type.equals("isWrite")) {
        fp.setAttachPriv(attachPriv);
        fp.setPrcsItem(fieldStr);
      } else if (type.equals("auto")) {
        fp.setPrcsItemAuto(fieldStr);
      } else {
        fp.setHiddenItem(fieldStr);
      }
      logic.updateFlowProcess(fp , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getConditionMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9DocFlowType ft = flowTypelogic.getFlowTypeById(Integer.parseInt(flowId), dbConn);
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      String flowDoc = ft.getFlowDoc();
      int formId = ft.getFormSeqId();
      String formItem = ffLogic.getTitle(dbConn, formId);
      formItem += ",[主办人会签意见],[从办人会签意见],[公共附件名称],[公共附件个数],[当前步骤号],[当前流程设计步骤号],[当前主办人姓名],[当前主办人角色号],[当前主办人角色],[当前主办人辅助角色],[当前主办人部门],[当前主办人上级部门]";

      String prcsInDesc = "";
      String prcsOutDesc = "";
      String conditionDesc = fp.getConditionDesc();
      if (conditionDesc != null && !"".equals(conditionDesc)
          && conditionDesc.split("\n").length > 1) {
        prcsInDesc = conditionDesc.split("\n")[0];
        prcsOutDesc = conditionDesc.split("\n")[1];
      }
      String prcsInSet = this.getConditionSet(fp.getPrcsInSet());
      String prcsOutSet = this.getConditionSet(fp.getPrcsOutSet());

      String prcsIn = fp.getPrcsIn();
      String prcsOut = fp.getPrcsOut();

      StringBuffer sb = new StringBuffer("{");
      sb.append("items:'" + formItem + "',");
      sb.append("prcsInSet:" + prcsInSet + ",");
      sb.append("prcsOutSet:" + prcsOutSet + ",");
      sb.append("prcsIn:\"" + (prcsIn = prcsIn != null ? prcsIn : "") + "\",");
      sb.append("prcsOut:\"" + (prcsOut = prcsOut != null ? prcsOut : "")
          + "\",");
      prcsInDesc = prcsInDesc.replaceAll("[\n-\r]", "");
      prcsOutDesc = prcsOutDesc.replaceAll("[\n-\r]", "");
      sb.append("prcsInDesc:'" + prcsInDesc + "',");
      sb.append("prcsOutDesc:'" + prcsOutDesc + "'");
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getAutoFieldMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(Integer.parseInt(seqId) , dbConn);
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9DocFlowType ft = flowTypelogic.getFlowTypeById(Integer.parseInt(flowId) ,dbConn);
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      String flowDoc = ft.getFlowDoc();
      int formId = ft.getFormSeqId();
      List<T9DocFlowFormItem> formItem = ffLogic.formToMap(dbConn, formId);

      String result = "";
      for (T9DocFlowFormItem item : formItem) {
        // 如果是自动赋值        String clazz = item.getClazz();
        if (clazz != null && "AUTO".equals(clazz)) {
          String val = item.getTitle();
          String key = item.getName();
          if ( key != null && key.indexOf("OTHER") != -1) {
            //val = val.split(":")[1];
            continue;
          }
          if (!"".equals(result)) {
            result += ",";
          }
          result += val;
        }
      }
      StringBuffer sb = new StringBuffer("{");
      sb.append("procId:'" + fp.getPrcsId() + "',");
      sb.append("items:'" + result + "',");
      sb.append("itemAuto:'"
          + (fp.getPrcsItemAuto() != null ? fp.getPrcsItemAuto() : "") + "'");

      sb.append("}");
      //System.out.append(sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String updateCondition(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    String prcsInSet = request.getParameter("prcsInSet");
    String prcsOutSet = request.getParameter("prcsOutSet");
    String prcsIn = request.getParameter("prcsIn").replaceAll("\r\n", "");
    String prcsOut = request.getParameter("prcsOut").replaceAll("\r\n", "");

    String prcsInDesc = request.getParameter("prcsInDesc");
    String prcsOutDesc = request.getParameter("prcsOutDesc");
    int seqId = Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      prcsInSet = prcsInSet.replace("]AND", "] AND");
      prcsInSet = prcsInSet.replace("]OR", "] OR");
      prcsInSet = prcsInSet.replace("AND[", "AND [");
      prcsInSet = prcsInSet.replace("OR[", "OR [");

      prcsOutSet = prcsOutSet.replace("]AND", "] AND");
      prcsOutSet = prcsOutSet.replace("]OR", "] OR");
      prcsOutSet = prcsOutSet.replace("AND[", "AND [");
      prcsOutSet = prcsOutSet.replace("OR[", "OR [");

      String conditionDesc = prcsInDesc + "\n" + prcsOutDesc;
      T9DocFlowProcess fp = logic.getFlowProcessById(seqId , dbConn);
      fp.setConditionDesc(conditionDesc);
      fp.setPrcsIn(prcsIn);
      fp.setPrcsOut(prcsOut);
      fp.setPrcsInSet(prcsInSet);
      fp.setPrcsOutSet(prcsOutSet);
      logic.updateFlowProcess(fp , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getConditionSet(String array) {
    String result = "[";
    if (array != null) {
      String[] aPrcsInSet = array.split("\n");
      int count = 0;
      for (String text : aPrcsInSet) {
        result += "'" + text + "',";
        count++;
      }
      if (count > 0) {
        result = result.substring(0, result.length() - 1);
      }
    }
    result += "]";
    return result;
  }
  
//[{userId:'liuhan1',userName:'liuhan1'},{userId:'liuhan',userName:'liuhan2'},{userId:'liuhan3',userName:'liuhan3'},{userId:'liuhan5',userName:'liuhan7'}];
  
  public String getPrivUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sPrcsId = request.getParameter("prcsId");
    String sFlowId = request.getParameter("flowId");
    String sSeqId  = request.getParameter("seqId");
    
    String sDeptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp  = null;
      if(sSeqId == null || "".equals(sSeqId) || "null".equals(sSeqId)){
        int flowId =  Integer.parseInt(sFlowId);
        fp = logic.getFlowProcessById(flowId, sPrcsId , dbConn);
      }else{
        int seqId = Integer.parseInt(sSeqId);
        fp = logic.getFlowProcessById(seqId, dbConn);
      }
      String data = "";
      if (fp != null) {
        String user = fp.getPrcsUser();//人员
        String dept = sDeptId;
        String priv = fp.getPrcsPriv();//角色
        T9FlowUserSelectLogic select = new T9FlowUserSelectLogic();
        
        dept = T9OrgSelectLogic.changeDept(dbConn, fp.getPrcsDept());
        data = select.getPersonInDept(user, dept, priv, dbConn, sDeptId);
      } 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + data + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getFormItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sFlowId = request.getParameter("flowId");
    int flowId =  Integer.parseInt(sFlowId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
      T9DocFlowType ft = flowTypelogic.getFlowTypeById(flowId,dbConn);
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      int formId = ft.getFormSeqId();
      String formItem = ffLogic.getTitle(dbConn, formId);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "'" + formItem + "'");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delProc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    int seqId =  Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      logic.delFlowProcess(seqId , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String cloneProc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    int seqId =  Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9DocFlowProcess fp = logic.getFlowProcessById(seqId , dbConn);
      int flowId = fp.getFlowSeqId();
      int max = logic.getMaxProcessId(flowId , dbConn);
      max++;
      fp.setSeqId(0);
      fp.setPrcsId(max);
      logic.saveFlowProcess(fp , dbConn);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "克隆成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String savePosition(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String strSql = request.getParameter("strSql");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      logic.savePosition(strSql , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String setMatadataItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sSeqId = request.getParameter("seqId");
    String fieldStr = request.getParameter("fieldStr");
    if (fieldStr == null) {
      fieldStr = "";
    }
    int seqId = Integer.parseInt(sSeqId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      logic.setMatadataItem(fieldStr, seqId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
