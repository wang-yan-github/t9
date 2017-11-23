package t9.subsys.oa.vehicle.act;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.subsys.oa.vehicle.data.T9VehicleUsage;
import t9.subsys.oa.vehicle.logic.T9ExportLogic;

public class T9ExportAct {
  /**
   * lz
   * 
   * */
  public String exportXls(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("车辆使用记录.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      //接受参数
      String vuStatus = request.getParameter("vuStatus");
      String vId = request.getParameter("vId");
      String vuDriver = request.getParameter("vuDriver");
      String vuRequestDateMin = request.getParameter("vuRequestDateMin");
      String vuRequestDateMax = request.getParameter("vuRequestDateMax");
      String vuUser = request.getParameter("vuUser");
      String vuDept = request.getParameter("vuDept");
      String vuStartMin = request.getParameter("vuStartMin");
      String vuStartMax = request.getParameter("vuStartMax");
      String vuEndMin = request.getParameter("vuEndMin");
      String vuEndMax = request.getParameter("vuEndMax");
      String vuProposer = request.getParameter("vuProposer");
      String vuReason = request.getParameter("vuReason");
      String vuRemark = request.getParameter("vuRemark");
      String vuOperator = request.getParameter("vuOperator");
      
      T9VehicleUsage usage = new T9VehicleUsage();
      usage.setVuStatus(vuStatus);
      usage.setVId(vId);
      usage.setVuDriver(vuDriver);
      usage.setVuUser(vuUser);
      usage.setVuDept(vuDept);
      usage.setVuProposer(vuProposer);
      usage.setVuReason(vuReason);
      usage.setVuRemark(vuRemark);
      usage.setVuOperator(vuOperator);
      if (!T9Utility.isNullorEmpty(vuRequestDateMin)) {
        usage.setVuRequestDate(T9Utility.parseDate(vuRequestDateMin));
      }
      if (!T9Utility.isNullorEmpty(vuStartMin)) {
        usage.setVuStart(T9Utility.parseDate(vuStartMin));
      }
      if (!T9Utility.isNullorEmpty(vuEndMin)) {
        usage.setVuEnd(T9Utility.parseDate(vuEndMin));
      }
      //返回LIST集合
      List<T9VehicleUsage> usageList = T9ExportLogic.vehicleAll(dbConn, usage,vuRequestDateMax,vuStartMax,vuEndMax);
      ArrayList<T9DbRecord > dbL = T9ExportLogic.getDbRecord(usageList,dbConn);
      T9JExcelUtil.writeExc(ops, dbL);
      //T9CSVUtil.CVSWrite(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
}
