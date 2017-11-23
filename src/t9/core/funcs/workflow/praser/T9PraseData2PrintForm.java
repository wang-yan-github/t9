package t9.core.funcs.workflow.praser;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9PraseData2PrintForm {

    public String parsePrintForm(T9Person user, String modelShort, int runId, int flowId,
            List<T9FlowRunData> frdList, List<T9FlowFormItem> itemList, Connection conn, boolean isWord,
            String isAutoPrint) throws Exception {
        if (T9WorkFlowUtility.isSave2DataTable()) {
            T9PraseData2FormUtility util = new T9PraseData2FormUtility();
            frdList = util.tableData2FlowRunData(conn, flowId, runId, itemList);
        }
        // -----------判断字段对于当前用户是否为隐藏----------------
        String hidden = "";
        String query = "select HIDDEN_ITEM from FLOW_PROCESS,FLOW_RUN_PRCS "
                + "where FLOW_PROCESS.FLOW_SEQ_ID=" + flowId + " and FLOW_RUN_PRCS.RUN_ID=" + runId
                + " and FLOW_RUN_PRCS.USER_ID=" + user.getSeqId()
                + " and FLOW_PROCESS.PRCS_ID=FLOW_RUN_PRCS.FLOW_PRCS";
        Statement stm = null;
        ResultSet rs = null;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            while (rs.next()) {
                String tmp = rs.getString("HIDDEN_ITEM");
                if (tmp != null && !"".equals(tmp)) {
                    hidden += tmp;
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm, rs, null);
        }
        if (itemList.size() < 0) {
            String content = T9WorkFlowUtility.Message("表单内容为空", 2);
            return content;
        }

        String signObject = "";
        String signCheckStr = "";
        boolean isHaveSign = false;
        for (T9FlowFormItem item : itemList) {
            String realValue = "";
            int itemId = item.getItemId();
            T9FlowRunData flowRunData = this.getFlowRunData(frdList, itemId);
            if (flowRunData != null && flowRunData.getItemData() != null) {
                realValue = flowRunData.getItemData();
                String type = item.getType();
                if ("hidden".equals(type)) {
                    flowRunData.setItemData("");
                    continue;
                }
                realValue = T9WorkFlowUtility.getOutSpecialChar(realValue);
            }
            String tag = item.getTag();
            String title = item.getTitle();
            String content = item.getContent();
            String clazz = item.getClazz();
            String tag1 = tag.toLowerCase();
            if (content != null)
                content = content.replace("<" + tag1, "<" + tag);
            item.setContent(content);
            // 判断是否是保密字段

            if (T9WorkFlowUtility.findId(hidden, title)) {
                // 替换为空
                realValue = "";
            }

            if ("DATE".equals(clazz) || "USER".equals(clazz)) {
                realValue = "";
                modelShort = modelShort.replaceAll("\\{" + item.getName() + "\\}", realValue);
                continue;
            } else if ("RADIO".equals(clazz) && "IMG".equals(tag)) {
                String radioField = T9Utility.null2Empty(item.getRadioField());
                String radioCheck = T9Utility.null2Empty(item.getRadioCheck());
                String[] radioArray = radioField.split("`");
                String name = item.getName();
                String disabled = "";
                if (!T9Utility.isNullorEmpty(realValue)) {
                    radioCheck = realValue;
                }
                disabled = "disabled";
                realValue = "";
                for (String s : radioArray) {
                    String checked = "";
                    if (s.equals(radioCheck)) {
                        checked = "checked";
                    }
                    realValue += "<input type=\"radio\" name=\"" + name + "\" value=\"" + s + "\" " + checked
                            + " " + disabled + "><label>" + s + "</label>&nbsp;";
                }
            } else if ("SELECT".equals(tag) && !"AUTO".equals(clazz)) {
                String child = item.getChild();
                if (child != null && !"".equals(child)) {
                    for (T9FlowFormItem tmp : itemList) {
                        int itemId2 = tmp.getItemId();
                        String title2 = tmp.getTitle();
                        String clazz2 = tmp.getClazz();
                        String tag2 = tmp.getTag();

                        if ("DATE".equals(clazz2) || "USER".equals(clazz2)) {
                            continue;
                        }
                        if (T9WorkFlowUtility.findId(child, title2) && "SELECT".equals(tag2)) {
                            T9FlowRunData rd = this.getFlowRunData(frdList, itemId2);
                            String childValue = rd.getItemData();
                            if (rd != null && childValue != null && !"".equals(childValue)) {
                                childValue = childValue.replaceAll("\"", "&quot;");
                                childValue = childValue.replaceAll("<", "&lt;");
                                childValue = childValue.replaceAll(">", "&gt;");
                                int index = childValue.indexOf("|");
                                if (index != -1) {
                                    childValue = childValue.substring(0, index);
                                    rd.setItemData(childValue);
                                }
                            }
                            break;
                        }
                    }
                }
            } else if ("AUTO".equals(clazz) && "SELECT".equals(tag) && !"".equals(realValue)) {
            } else if ("LIST_VIEW".equals(clazz)) {
                int sumFlag = 0;
                String lvTitle = item.getLvTitle();
                String lvAlign = item.getLvAlign();
                if (lvAlign == null) {
                    lvAlign = "";
                }
                if (lvTitle == null) {
                    lvTitle = "";
                }
                String lvSize = item.getLvSize();
                if (lvSize == null) {
                    lvSize = "";
                }
                String lvSum = item.getLvSum();
                if (lvSum == null) {
                    lvSum = "";
                }

                String[] lvSumArray = lvSum.split("`");
                if (lvSum.indexOf("1") != -1) {
                    sumFlag = 1;
                }
                String[] myArraySize = lvSize.split("`");
                String lvValue = realValue;
                realValue = "<TABLE class='LIST_VIEW' style='border-collapse:collapse' border=1 cellspacing=0 cellpadding=2><TR style='font-weight:bold;font-size:14px;' class='LIST_VIEW_HEADER'>";
                String[] myArray = lvTitle.split("`");

                int arrayCountTitle = myArray.length;
                String[] alignArray = lvAlign.split("`");
                for (int t = 0; t < arrayCountTitle; t++) {
                    String tmp = myArray[t];
                    String align = "";
                    if (alignArray.length > t) {
                        align = alignArray[t];
                    }
                    if ("".equals(align) || align == null) {
                        align = "left";
                    }
                    int l = 10;
                    if (T9Utility.isInteger(myArraySize[t])) {
                        l = Integer.parseInt(myArraySize[t]);
                    }
                    realValue += "<TD nowrap align='" + align + "' width=" + (l * 9) + ">" + tmp + "</TD>";
                }
                realValue += "</TR>";
                lvValue = lvValue.replace("&#13;", "");
                myArray = lvValue.split("&#10;");
                int arrayCount = myArray.length;
                Float[] sumData = new Float[arrayCountTitle];

                for (String tmp : myArray) {
                    String[] myArray1 = tmp.split("`");

                    if (!"".equals(tmp) && myArray1.length > 0) {
                        realValue += "<tr>";
                        for (int j = 0; j < arrayCountTitle; j++) {
                            if (j < myArray1.length && "1".equals(lvSumArray[j]) && myArray1[j] != null
                                    && T9Utility.isNumber(myArray1[j])) {
                                if (sumData[j] == null) {
                                    sumData[j] = new Float(0);
                                }
                                sumData[j] += Float.parseFloat(myArray1[j]);
                            }
                            String tdData = "";
                            if (j < myArray1.length && !"".equals(myArray1[j])) {
                                tdData = myArray1[j];
                            } else {
                                tdData = "&nbsp;";
                            }
                            String align = "";
                            if (alignArray.length > j) {
                                align = alignArray[j];
                            }
                            if ("".equals(align) || align == null) {
                                align = "left";
                            }
                            tdData = tdData.replace("&lt;br&gt;", "<br>");
                            realValue += "<td  align='" + align + "'>" + tdData + "<br></td>";
                        }
                        realValue += "</tr>";
                    }
                }
                if (sumFlag == 1 && arrayCount > 0) {
                    realValue += "<tr style='font-weight:bold;'>";
                    for (int j = 0; j < arrayCountTitle; j++) {
                        String sumValue = "";
                        if ("".equals(sumData[j]) || sumData[j] == null) {
                            sumValue = "&nbsp;";
                        } else {
                            sumValue = "合计：" + sumData[j];
                        }
                        int l = 10;
                        if (T9Utility.isInteger(myArraySize[j])) {
                            l = Integer.parseInt(myArraySize[j]);
                        }
                        realValue += "<td align=right width=" + (l * 9) + ">" + sumValue + "</td>";
                    }
                    realValue += "<td>";
                }
                realValue += "</TABLE>";
            } else if ("SIGN".equals(clazz)) {
                if (isWord) {
                    realValue = "";
                } else {
                    String signId = "DATA_" + itemId;
                    String itemCheck = "";
                    String signCheck = "";
                    if (item.getDatafld() != null) {
                        signCheck = item.getDatafld();
                    }
                    if (!signCheck.endsWith(",")) {
                        signCheck += ",";
                    }
                    for (T9FlowFormItem item2 : itemList) {
                        String title2 = item2.getTitle();
                        String clazz2 = item2.getClazz();
                        int itemId2 = item2.getItemId();

                        if ("DATE".equals(clazz2) || "USER".equals(clazz2)) {
                            continue;
                        }
                        if (T9WorkFlowUtility.findId(signCheck, title2)) {
                            itemCheck += "DATA_" + itemId2 + ",";
                        }
                    }
                    signCheckStr += "\"" + signId + "\":\"" + itemCheck + "\",";
                    signObject += signId + ",";
                    String tmp = "<div id=SIGN_POS_" + signId + ">&nbsp;</div>";
                    realValue = tmp + "<input type=hidden id=DATA_" + itemId + " name=DATA_" + itemId
                            + " value='" + realValue + "' title='" + title + "'>\n";
                    isHaveSign = true;
                }
            } else if ("MODULE".equals(clazz)) {
                if (!T9WorkFlowUtility.findId(hidden, title)) {
                    String module = item.getValue();
                    String divId = "module-" + module + "-DATA_" + itemId;
                    String moduleSeqId = realValue;
                    realValue = "<div id=\"" + divId + "\">" + content + "</div>";
                    realValue += "<script>";
                    realValue += "printModuleContent(\"" + module + "\" , \"" + divId + "\" ,  \""
                            + moduleSeqId + "\")";
                    realValue += "</script>";
                }
            } else if ("IMGUPLOAD".equals(clazz)) {
                if (!T9WorkFlowUtility.findId(hidden, title)) {
                    realValue = this.getImgUpload(item, "/t9", realValue);
                }
            } else if ("MOBILE_SEAL".equals(clazz)) {
                realValue = T9PraseData2FormUtility.mobileSeal(item, itemList, realValue, flowId, runId);
            } else {
                if ("AUTO".equals(clazz) && "{宏控件}".equals(realValue)) {
                    realValue = "";
                }
                realValue = realValue.replaceAll("<", "&lt;");
                realValue = realValue.replaceAll(">", "&gt;");
                realValue = realValue.replaceAll("  ", "&nbsp;&nbsp;");
                realValue = realValue.replaceAll("\r\n", "<br>");
                realValue = realValue.replaceAll("  ", "&nbsp;&nbsp;");
                if ("INPUT".equals(tag)
                        && (content.indexOf("type=checkbox") == -1
                                || content.indexOf("type=\"checkbox\"") == -1 || content
                                .indexOf("type=\\\"checkbox\\\"") == -1)) {
                    String hidden2 = item.getHidden();
                    if ("1".equals(hidden2)) {
                        realValue = "";
                    }
                }
            }
            if (flowRunData != null) {
                flowRunData.setItemData(realValue);
            }
        }
        for (T9FlowFormItem item : itemList) {
            int itemId = item.getItemId();
            T9FlowRunData flowRunData = this.getFlowRunData(frdList, itemId);
            String name = item.getName();
            String clazz = item.getClazz();
            String title = item.getTitle();
            if ("DATE".equals(clazz) || "USER".equals(clazz)) {
                continue;
            }
            String realValue = "";
            if (flowRunData != null) {
                realValue = flowRunData.getItemData();
            }
            if (!isWord && !"DATE".equals(clazz) && !"USER".equals(clazz) && !"RADIO".equals(clazz)
                    && !"SIGN".equals(clazz) && !"MOBILE_SEAL".equals(clazz) && !"MODULE".equals(clazz)
                    && !"IMGUPLOAD".equals(clazz)) {
                String tag = item.getTag();
                String content = item.getContent();
                if ("INPUT".equals(tag)
                        && (content.indexOf("type=checkbox") != -1
                                || content.indexOf("type=\"checkbox\"") != -1 || content
                                .indexOf("type=\\\"checkbox\\\"") != -1)) {
                    if ("on".equals(realValue)) {
                        realValue = "<input type=checkbox checked onclick='this.checked=1;'>";
                        realValue += "<input type=hidden name=DATA_" + itemId + " id=DATA_" + itemId
                                + " value=\"on\" title=\"" + title + "\">";
                    } else {
                        realValue = "<input type=checkbox onclick='this.checked=0;'>";
                        realValue += "<input type=hidden name=DATA_" + itemId + " id=DATA_" + itemId
                                + " title=\"" + title + "\">";
                    }
                } else {
                    if ("TEXTAREA".equals(tag)) {
                        realValue = T9Utility.null2Empty(realValue);
                        realValue = realValue.replace("&#10;", "<br/>");
                        // 不含有换行
                        if (realValue.indexOf("<br/>") < 0) {
                            realValue = "<table border=0><tr><td style='width:600px;word-wrap:break-word; word-break:break-all;'>"
                                    + realValue + "</td></tr></table>";
                        }

                    }
                    realValue += "<input type=hidden name=DATA_" + itemId + " id=DATA_" + itemId
                            + " value=\"" + realValue + "\" title=\"" + title + "\">";
                }
            }

            modelShort = modelShort.replaceAll("\\{" + name + "\\}", realValue);
        }
        // 处理签章控件
        String sign = "";
        if (isHaveSign) {
            sign += "<script>";
            if (signObject.endsWith(",")) {
                signObject = signObject.substring(0, signObject.length() - 1);
            }
            sign += "sign_str = \"" + signObject + "\";";
            if (signCheckStr.endsWith(",")) {
                signCheckStr = signCheckStr.substring(0, signCheckStr.length() - 1);
            }
            sign += "sign_check = {" + signCheckStr + "};";
            sign += "isHaveSign = true;LoadSignData();";

            if ("1".equals(isAutoPrint)) {
                sign += "setTimeout(\"print2();\" , 2000);";
            }
            sign += "</script>";
        } else {
            sign += "<script>";
            if ("1".equals(isAutoPrint)) {
                sign += "print2();";
            }
            sign += "</script>";
        }
        modelShort = sign + modelShort;
        return modelShort;
    }

    private String getImgUpload(T9FlowFormItem item, String contextPath, String value) {
        // TODO Auto-generated method stub
        value = T9Utility.null2Empty(value);
        String name = "";
        String id = "";
        String path = contextPath + "/core/funcs/workflow/flowform/editor/plugins/NImgupload/pic.png";
        if (!T9Utility.isNullorEmpty(value)) {
            String[] vas = value.split("\\:");
            if (vas.length > 1) {
                name = vas[1];
                id = vas[0];
                if (!T9Utility.isNullorEmpty(name) && !T9Utility.isNullorEmpty(id)) {
                    path = contextPath
                            + "/t9/core/funcs/office/ntko/act/T9NtkoAct/upload.act?attachmentName="
                            + URLEncoder.encode(name) + "&attachmentId=" + id
                            + "&module=workflow&directView=1";
                }
            }
        }
        String str = " <div class=\"imgUpload\">";
        str += "<img  src='" + path + "' style='width:" + item.getImgWidth() + "px;height:"
                + item.getImgHeight() + "px' title='" + item.getTitle() + "'>";
        str += "<input type='hidden' name='DATA_" + item.getItemId() + "' id='DATA_" + item.getItemId()
                + "' value='" + value + "' />";
        str += "</div>";
        return str;
    }

    public T9FlowRunData getFlowRunData(List<T9FlowRunData> frdList, int itemId) {
        T9FlowRunData flowRunData = null;
        for (T9FlowRunData tmp : frdList) {
            if (tmp.getItemId() == itemId) {
                flowRunData = tmp;
            }
        }
        return flowRunData;
    }
}
