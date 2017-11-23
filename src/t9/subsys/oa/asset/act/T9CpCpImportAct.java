package t9.subsys.oa.asset.act;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.asset.data.T9CpAssetType;
import t9.subsys.oa.asset.data.T9CpCptlInfo;
import t9.subsys.oa.asset.data.T9CpCptlRecord;
import t9.subsys.oa.asset.data.T9Cpcptl;
import t9.subsys.oa.asset.logic.T9CpAssetTypeLogic;
import t9.subsys.oa.asset.logic.T9CpCptlRecordLogic;
public class T9CpCpImportAct {

  public String importAsset (HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement stmt = null;
    InputStream is = null;
    int num = 0;
    int numOne = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      is = fileForm.getInputStream();
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);
      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
      String newDate = sf.format(new Date());
      numOne = drl.size();
      for (int i = 0; i < drl.size(); i++) {
        T9DbRecord rd = new T9DbRecord();
        rd = drl.get(i);
        String cptlNo = (String)rd.getValueByName("资产编号");
        String cptlName = (String)rd.getValueByName("资产名称");
        String cptlVal = (String)rd.getValueByName("资产值");
        String cptlSpec = (String)rd.getValueByName("资产类型");
        String listDate = (String)rd.getValueByName("单据日期");
        String safekeeping = (String)rd.getValueByName("保管地点");
        String remark = (String)rd.getValueByName("备注");
        if (T9Utility.isNullorEmpty(listDate)) {
          listDate = sd.format(new Date());
        }
        else{
          listDate = listDate.replaceAll("/", "-");
        }
        T9CpAssetType type = T9CpAssetTypeLogic.cptlSpec(dbConn,cptlSpec);
        if (type == null) {
          T9CpAssetType typeName = new T9CpAssetType();
          typeName.setTypeName(cptlSpec);
          typeName.setTypeNo(0);
          T9CpAssetTypeLogic.addType(dbConn,typeName);
          int seqId = T9CpAssetTypeLogic.getMaxSeqId(dbConn);
          cptlSpec = String.valueOf(seqId);
        }else {
          cptlSpec = String.valueOf(type.getSeqId());
        }
        String nameVlue = "'" + cptlNo + "','" + cptlName + "'," + cptlVal + ",'" + cptlSpec + "'";
        String sql = "insert into cp_cptl_info "
          + "(CPTL_NO,CPTL_NAME,CPTL_VAL,CPTL_SPEC,LIST_DATE,CREATE_DATE,safekeeping,remark) values (" + nameVlue + ",?,?,'"+ safekeeping +"','"+ remark +"')";
        stmt = dbConn.prepareStatement(sql);
        stmt.setDate(1,java.sql.Date.valueOf(listDate));
        stmt.setDate(2,java.sql.Date.valueOf(newDate));
        stmt.executeUpdate();//执行SQL
        num ++;
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据成功！");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据失败");
      throw e;
    }
    return "/subsys/oa/asset/manage/mgs.jsp?num=" + num + "&numOne=" + numOne;
  }
  
  /**
   * 下载CSV模板
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String downCSVTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String fileName = URLEncoder.encode("固定资产模板.csv", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      T9DbRecord record = new T9DbRecord();
      record.addField("资产编号", "");
      record.addField("资产名称", "");
      record.addField("资产值", "");
      record.addField("资产类型", "");
      record.addField("单据日期", "");
      record.addField("保管地点", "");
      record.addField("备注", "");
      dbL.add(record);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }


  public String outAsset (HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CpCptlInfo cptlInfo = new T9CpCptlInfo();
      T9CpCptlRecord cptlRecord = new T9CpCptlRecord();
      T9CpCptlRecordLogic recordLogic = new T9CpCptlRecordLogic();

      String cpreFlag = request.getParameter("CPRE_FLAG");
      String cptlName = request.getParameter("CPTL_NAME");
      String cptlSpec = request.getParameter("CPTL_SPEC");
      String deptId2 = request.getParameter("DEPT_ID");
      String typeId = request.getParameter("TYPE_ID");
      String useFor = request.getParameter("USE_FOR"); 
      String useState = request.getParameter("USE_STATE");
      String useUser = request.getParameter("USE_USER");

      int deptId = 0;
      if (!T9Utility.isNullorEmpty(deptId2)) {
        deptId = Integer.parseInt(deptId2);
        cptlRecord.setDeptId(deptId);
      }
      cptlRecord.setCpreFlag(cpreFlag);
      cptlInfo.setCptlName(cptlName);
      cptlInfo.setCptlSpec(cptlSpec);
      cptlInfo.setTypeId(typeId);
      cptlInfo.setUseFor(useFor);
      cptlInfo.setUseState(useState);
      cptlInfo.setUseUser(useUser);

      List<T9Cpcptl> list = recordLogic.importOut(dbConn,cptlInfo,cptlRecord);

      OutputStream ops = null;
      String fileName = URLEncoder.encode("固定资产使用明细表.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9CpCptlRecordLogic expl = new T9CpCptlRecordLogic();
      ArrayList<T9DbRecord > dbL = expl.getDbRecord(list);
      T9JExcelUtil.writeExc(ops, dbL);
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

