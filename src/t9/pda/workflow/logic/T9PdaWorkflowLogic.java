package t9.pda.workflow.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.praser.T9PraseData2FormUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9PdaWorkflowLogic {

    public Map getHandlerMsg(T9Person user, int runId, int prcsId, String flowPrcs, String ip,
            Connection conn, String imgPath, String isWriteLog) throws Exception {
        // TODO Auto-generated method stub
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
        T9FlowRun flowRun = flowRunLogic.getFlowRunByRunId(runId, conn);
        Date date = new Date();
        T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowRun.getFlowId(), conn);
        boolean attachPrivWrite = false;
        boolean filePrivWrite = true;
        T9FlowProcess flowProcess = null;
        String attachPriv = "";
        Map runPrcsQuery = new HashMap();
        runPrcsQuery.put("RUN_ID", runId);
        runPrcsQuery.put("PRCS_ID", prcsId);
        runPrcsQuery.put("USER_ID", user.getSeqId());
        String item = "";
        T9ORM orm = new T9ORM();
        T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class, runPrcsQuery);
        if (runProcess != null && "1".equals(flowType.getFlowType())) {
            T9FlowProcessLogic flowPrcsLogic = new T9FlowProcessLogic();
            // 查出相关步骤
            flowProcess = flowPrcsLogic.getFlowProcessById(flowRun.getFlowId(), flowPrcs, conn);
            item = flowProcess.getPrcsItem();
            // 附件是否可写
            if (runProcess.getOpFlag() != null && "1".equals(runProcess.getOpFlag())) {
                attachPrivWrite = T9WorkFlowUtility.findId(item, "[A@]");
                attachPriv = flowProcess.getAttachPriv();
                if (attachPriv == null) {
                    attachPriv = "";
                }
                // 文号是否可写
                filePrivWrite = T9WorkFlowUtility.findId(item, "[B@]");
            }
        } else {
            attachPriv = "1,2,3,4,5";
            attachPrivWrite = true;
        }
        // 查出第一步骤
        // 查出运行中的步骤

        // 查出表单
        T9FlowFormType fft = (T9FlowFormType) orm.loadObjSingle(conn, T9FlowFormType.class,
                flowType.getFormSeqId());

        // 查询表单字段信息
        Map formItemQuery = new HashMap();
        formItemQuery.put("FORM_ID", flowType.getFormSeqId());
        List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class, formItemQuery);

        Map runDataQuery = new HashMap();
        runDataQuery.put("RUN_ID", runId);
        List<T9FlowRunData> frdList = orm.loadListSingle(conn, T9FlowRunData.class, runDataQuery);

        if (T9WorkFlowUtility.isSave2DataTable()) {
            T9PraseData2FormUtility util = new T9PraseData2FormUtility();
            frdList = util.tableData2FlowRunData(conn, flowRun.getFlowId(), flowRun.getRunId(), list);
        }

        String sealImg = "";
        String itemCheck = "";
        for (T9FlowFormItem it : list) {
            String title = it.getTitle();
            String clazz = it.getClazz();

            if ("MOBILE_SEAL".equals(clazz)) {
                itemCheck = "";
                String datafld = it.getDatafld();
                String ids = "";
                for (T9FlowFormItem item2 : list) {
                    String title2 = item2.getTitle();
                    String clazz2 = item2.getClazz();
                    int itemId2 = item2.getItemId();
                    if ("DATE".equals(clazz2) || "USER".equals(clazz2)) {
                        continue;
                    }
                    if (T9WorkFlowUtility.findId(datafld, title2)) {
                        itemCheck += "DATA_" + itemId2 + ",";
                        ids += itemId2 + ",";
                    }
                }
                String val = "";
                for (T9FlowRunData d : frdList) {
                    if (d.getItemId() == it.getItemId())
                        val = d.getItemData();
                }

                if (!T9Utility.isNullorEmpty(val) && !val.equals(it.getValue())) {
                    String freeItem = runProcess.getFreeItem();
                    boolean isReadOnly = "0".equals(runProcess.getOpFlag())
                            || ("2".equals(flowType.getFlowType()) && freeItem != null
                                    && !"".equals(freeItem) && !T9WorkFlowUtility.findId(freeItem, title))
                            || (("1".equals(flowType.getFlowType()) && !T9WorkFlowUtility.findId(item, title)))
                            && !"DATE".equals(clazz) && !"USER".equals(clazz);
                    String sealDelStr = isReadOnly ? ""
                            : "<span class=\"mobile_seal_span\" style=\"width: 16px;display:inline;\">—</span>";
                    sealImg += "<div class=\"seal_wrap\"><img  data_id=\""
                            + it.getName()
                            + "\" class=\"mobile_seal\" src=\"/t9/t9/mobile/workflow/act/T9SealDataShowAct/data.act?FLOW_ID="
                            + flowType.getSeqId() + "&RUN_ID=" + runId + "&ITEM_ID=" + it.getItemId()
                            + "&CHECK_FIELD=" + ids + "\"/>" + sealDelStr + "</div>";
                }
            }
        }

        Map map = new HashMap();
        map.put("sealImg", sealImg);

        if (runProcess != null && runProcess.getPrcsFlag() != null && runProcess.getPrcsFlag().equals("1")) {
            runProcess.setPrcsFlag("2");
            runProcess.setPrcsTime(date);
            orm.updateSingle(conn, runProcess);
            if ("1".equals(runProcess.getTopFlag()) && "1".equals(runProcess.getOpFlag())) {
                String query = "update FLOW_RUN_PRCS set OP_FLAG=0 WHERE " + " USER_ID<>'" + user.getSeqId()
                        + "'  " + " AND RUN_ID='" + flowRun.getRunId() + "'  " + " AND PRCS_ID='"
                        + runProcess.getPrcsId() + "'  " + " AND FLOW_PRCS='" + runProcess.getFlowPrcs()
                        + "'";
                Statement stm = null;
                try {
                    stm = conn.createStatement();
                    ;
                    stm.executeUpdate(query);
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm, null, null);
                }
            }
            if (runProcess.getPrcsId() == 1) {
                flowRun.setBeginUser(user.getSeqId());
                flowRun.setBeginTime(date);
                orm.updateSingle(conn, flowRun);
                // 父流程先不做
            }
        }
        // 修改上一步骤状态为已经办理完毕
        int oldPrcsId = prcsId - 1;
        String query = "update FLOW_RUN_PRCS set PRCS_FLAG='4' WHERE " + " RUN_ID='" + runId + "'  "
                + " AND PRCS_ID='" + oldPrcsId + "'";
        if (!"0".equals(runProcess.getParent()) && !T9Utility.isNullorEmpty(runProcess.getParent()))
            query += " AND FLOW_PRCS IN (" + runProcess.getParent() + ")";
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ;
            stm.executeUpdate(query);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, null, null);
        }
        String flowTypeStr = flowType.getFlowType();
        boolean hasEnd = false;
        if ("2".equals(flowTypeStr) && "1".equals(runProcess.getOpFlag())) {
            query = "SELECT * from FLOW_RUN_PRCS where RUN_ID=" + runId + " and PRCS_ID>'" + prcsId
                    + "' and PRCS_FLAG='5'";
            Statement stm1 = null;
            ResultSet rs1 = null;
            try {
                stm1 = conn.createStatement();
                rs1 = stm1.executeQuery(query);
                if (!rs1.next()) {
                    hasEnd = true;
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm1, rs1, null);
            }
        } else if ("2".equals(flowTypeStr) && "2".equals(runProcess.getTopFlag())) {
            query = "select 1 FROM FLOW_RUN_PRCS WHERE RUN_ID='" + runId + "' AND PRCS_ID='" + prcsId
                    + "' AND FLOW_PRCS='" + flowPrcs + "' AND USER_ID<>'" + user.getSeqId()
                    + "' AND PRCS_FLAG IN('1','2')";
            Statement stm1 = null;
            ResultSet rs1 = null;
            try {
                stm1 = conn.createStatement();
                rs1 = stm1.executeQuery(query);
                if (!rs1.next()) {
                    hasEnd = true;
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm1, rs1, null);
            }
        }
        // 设置宏标记

        String formMsg = "";
        Map<String, Map<String, String>> formMaps = new LinkedHashMap<String, Map<String, String>>();
        if (list.size() > 0) {
            String modelShort = flowRunLogic.analysisAutoFlag(flowRun, flowType, fft, conn, imgPath);
            T9PraseData2FormPda pdf = new T9PraseData2FormPda();
            formMaps = pdf.parseForm(user, modelShort, flowProcess, runProcess, flowType, frdList, list, ip,
                    conn);
            formMsg = formMsg.replaceAll("\\\\", "");
            formMsg = formMsg.replaceAll("\n", "");
            formMsg = formMsg.replaceAll("\\\n", "");
        }
        String js = (fft == null || fft.getScript() == null) ? "" : fft.getScript();
        String css = (fft == null || fft.getCss() == null) ? "" : fft.getCss();
        js = js.replaceAll("\'", "\\\\'");
        js = js.replaceAll("[\n-\r]", "");
        css = css.replaceAll("\'", "\\\\'");
        css = css.replaceAll("[\n-\r]", "");

        // StringBuffer sb = new StringBuffer();

        String feedback = "0";
        if (flowProcess != null && flowProcess.getFeedback() != null) {
            feedback = flowProcess.getFeedback();
        }
        String allowBack = "0";// 不允许回退
        if (flowProcess != null && flowProcess.getAllowBack() != null) {
            allowBack = flowProcess.getAllowBack();
        }
        String focusUser = flowRun.getFocusUser();
        T9PersonLogic logic = new T9PersonLogic();
        String focusUserName = logic.getNameBySeqIdStr(focusUser, conn);
        map.put("formMaps", formMaps);
        map.put("js", js);
        map.put("css", css);
        // sb.append("{formMsg:'" + formMsg + "'");
        // sb.append(",js:'" + js + "'");
        // sb.append(",css:'" + css + "'");
        String runName = flowRun.getRunName();
        runName = T9WorkFlowUtility.getRunName(runName);

        map.put("runName", runName);
        if ("1".equals(isWriteLog)) {
            T9FlowRunLogLogic log = new T9FlowRunLogLogic();
            int iFlowPrcs = 0;
            if (T9Utility.isInteger(flowPrcs)) {
                iFlowPrcs = Integer.parseInt(flowPrcs);
            }
            log.runLog(runId, prcsId, iFlowPrcs, user.getSeqId(), 8, "访问了工作流：" + runName + "的办理界面！", ip, conn);
        }
        map.put("runId", runId);
        map.put("opFlag", runProcess.getOpFlag());
        map.put("feedbackFlag", feedback);
        map.put("allowBack", allowBack);
        map.put("flowType", flowType.getFlowType());

        return map;
    }
}
