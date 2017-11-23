package t9.subsys.oa.vehicle.act;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.vehicle.data.T9VehicleMaintenance;
import t9.subsys.oa.vehicle.logic.T9VehicleMaintenanceLogic;

public class T9VehicleMaintenanceAct {
  /**
   * 
   * 添加车辆维护
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addVehicleMainten(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      if(T9Utility.isInteger(request.getParameter("vId"))){
        vcMaintenance.setVId(Integer.parseInt(request.getParameter("vId")));
        if(T9Utility.isNumber(request.getParameter("vmFee"))){
          vcMaintenance.setVmFee(Double.parseDouble(request.getParameter("vmFee")));
        }
  
        vcMaintenance.setVmPerson(request.getParameter("vmPerson"));
        vcMaintenance.setVmReason(request.getParameter("vmReason"));
        vcMaintenance.setVmRemark(request.getParameter("vmRemark"));
        vcMaintenance.setVmType(request.getParameter("vmType"));
        if(!T9Utility.isNullorEmpty(request.getParameter("vmRequestDate"))){
          vcMaintenance.setVmRequestDate(T9Utility.parseDate("yyyy-MM-dd",request.getParameter("vmRequestDate")));
        }
        T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
        tvml.addMaintenance(dbConn, vcMaintenance);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询车辆维护情况
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVehicleMaintenance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      T9ORM orm = new T9ORM();
      String vId = request.getParameter("vId");
      String vmBeginDate = request.getParameter("vmBeginDate");
      String vmEndDate = request.getParameter("vmEndDate");
      String vmReason = request.getParameter("vmReason");
      String vmType = request.getParameter("vmType");
      String vmPerson = request.getParameter("vmPerson");
      String vmFeeMin = request.getParameter("vmFeeMin");
      String vmFeeMax = request.getParameter("vmFeeMax");
      String vmRemark = request.getParameter("vmRemark");
      String orderType = request.getParameter("orderType");
      if(T9Utility.isNullorEmpty(orderType)){
        orderType = "";
      }
      T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
      ArrayList<Map<String,String>>  vehicleMaintenList = (ArrayList)tvml.selectMaintenance(dbConn,vId,vmBeginDate,vmEndDate,vmReason,vmType,vmPerson,vmFeeMin,vmFeeMax,vmRemark,orderType );
      String feeTotals = "";
      Map<String,String> totalMap = vehicleMaintenList.get(vehicleMaintenList.size()-1);
      feeTotals = totalMap.get("total");
      
      T9VehicleAct vehicleAct = new T9VehicleAct();
      vehicleMaintenList.remove(vehicleMaintenList.size()-1);
      String data = vehicleAct.getJson(vehicleMaintenList);
      
      //request.setAttribute("feeTotal", feeTotals);
     // request.setAttribute("vehicleMaintenList", vehicleMaintenList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, feeTotals);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
    //return "/subsys/oa/vehicle/maintenance/selectvehiclemaintenance.jsp";
  }
  /**
   * 查询车辆维护情况BY VEHICLE(ID)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVehicleMaintenanceByVId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      T9ORM orm = new T9ORM();
      String vId = request.getParameter("vId");
      String orderType = request.getParameter("orderType");
      if(T9Utility.isNullorEmpty(orderType)){
        orderType = "";
      }
      String feeTotals = "";
      String data = "";
      T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
      if(!T9Utility.isNullorEmpty(vId)){
        ArrayList<Map<String,String>>  vehicleMaintenList = (ArrayList)tvml.selectMaintenanceByVId(dbConn,vId,orderType );
        Map<String,String> totalMap = vehicleMaintenList.get(vehicleMaintenList.size()-1);
        feeTotals = totalMap.get("total");
        T9VehicleAct vehicleAct = new T9VehicleAct();
        vehicleMaintenList.remove(vehicleMaintenList.size()-1);
        data = vehicleAct.getJson(vehicleMaintenList);
      }else{
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, feeTotals);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 
   * 删除车辆维护  根据SEQ_ID
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteVehicleMiantenById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      String seqId = request.getParameter("seqId");
      if(!T9Utility.isNullorEmpty(seqId)){
        T9ORM orm = new T9ORM();
        orm.deleteSingle(dbConn, T9VehicleMaintenance.class, Integer.parseInt(seqId));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "");
      }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询车辆维护情况ById
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectVehicleMaintenanceById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      T9ORM orm = new T9ORM();
      String seqId = request.getParameter("seqId");
      String data = "";
      if(T9Utility.isInteger(seqId)){
        T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
        T9VehicleMaintenance mainten = tvml.selectMaintenanceById(dbConn, Integer.parseInt(seqId));
        data = T9FOM.toJson(mainten).toString();
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 更新车辆维护 根据SEQ_ID
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateVehicleMaimtenById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      if(T9Utility.isInteger(request.getParameter("seqId"))){
        vcMaintenance.setSeqId(Integer.parseInt(request.getParameter("seqId")));
        vcMaintenance.setVId(Integer.parseInt(request.getParameter("vId")));
        if(T9Utility.isNumber(request.getParameter("vmFee"))){
          vcMaintenance.setVmFee(Double.parseDouble(request.getParameter("vmFee")));
        }
        vcMaintenance.setVmPerson(request.getParameter("vmPerson"));
        vcMaintenance.setVmReason(request.getParameter("vmReason"));
        vcMaintenance.setVmRemark(request.getParameter("vmRemark"));
        vcMaintenance.setVmType(request.getParameter("vmType"));
        if(!T9Utility.isNullorEmpty(request.getParameter("vmRequestDate"))){
          vcMaintenance.setVmRequestDate(T9Utility.parseDate("yyyy-MM-dd",request.getParameter("vmRequestDate")));
        }
        T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
        tvml.updateMaintenance(dbConn, vcMaintenance);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * csv导出
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportCSV(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    InputStream is = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);  
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      T9ORM orm = new T9ORM();
      String vId = request.getParameter("vId");
      String vmBeginDate = request.getParameter("vmBeginDate");
      String vmEndDate = request.getParameter("vmEndDate");
      String vmReason = request.getParameter("vmReason");
      String vmType = request.getParameter("vmType");
      String vmPerson = request.getParameter("vmPerson");
      String vmFeeMin = request.getParameter("vmFeeMin");
      String vmFeeMax = request.getParameter("vmFeeMax");
      String vmRemark = request.getParameter("vmRemark");
      String orderType = request.getParameter("orderType");
      if(T9Utility.isNullorEmpty(orderType)){
        orderType = "";
      }
      String fileName  = URLEncoder.encode("车辆维护记录.csv","utf-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
      ArrayList<T9DbRecord> dbL = tvml.selectMaintenanceCvs(dbConn, vId, vmBeginDate, vmEndDate, vmReason, vmType, vmPerson, vmFeeMin, vmFeeMax, vmRemark, "");

      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  /**
   * csv导出
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportCSVByVId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    InputStream is = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);  
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9VehicleMaintenance vcMaintenance = new T9VehicleMaintenance();
      T9ORM orm = new T9ORM();
      String vId = request.getParameter("vId");
      String fileName  = URLEncoder.encode("车辆维护记录.csv","utf-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      if(T9Utility.isInteger(vId)){
        T9VehicleMaintenanceLogic tvml = new T9VehicleMaintenanceLogic();
        dbL = tvml.selectMaintenanceCvsByVId(dbConn, vId);
      }
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
}
