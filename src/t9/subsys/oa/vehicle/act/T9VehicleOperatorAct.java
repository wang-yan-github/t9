package t9.subsys.oa.vehicle.act;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vehicle.data.T9VehicleOperator;
import t9.subsys.oa.vehicle.logic.T9VehicleOperatorLogic;

public class T9VehicleOperatorAct {
  /**
   * 添加或更新调度人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateOperator(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleOperator vcOperator = new T9VehicleOperator();
      T9VehicleOperatorLogic tvo = new T9VehicleOperatorLogic();
      String operatorName = request.getParameter("operatorName");
      String operatorId = request.getParameter("operatorId");
      String seqId = request.getParameter("seqId");
      T9ORM orm = new T9ORM();
      //查询调度人员
      Map map = null;
      ArrayList<T9VehicleOperator> operatorList =  tvo.selectOperator(dbConn, map);
      vcOperator.setOperatorId(operatorId);
      vcOperator.setOperatorName(operatorName);
      if(operatorList.size()>0){//更新
         tvo.updateOperator(dbConn, operatorId,operatorName);
      }else{//新建
        tvo.addOperator(dbConn, vcOperator);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询调度人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectOperator(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String data =  "{";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9VehicleOperator vcOperator = new T9VehicleOperator();
      T9VehicleOperatorLogic tvo = new T9VehicleOperatorLogic();
      Map map = null;
      ArrayList<T9VehicleOperator> operatorList =  tvo.selectOperator(dbConn, map);
      T9PersonLogic personLogic = new T9PersonLogic();
      for (int i = 0; i < operatorList.size(); i++) {
        T9VehicleOperator operator = operatorList.get(0);
        String operatorName = personLogic.getNameBySeqIdStr(operator.getOperatorId(), dbConn);
        data = data + "operatorId:\"" + operator.getOperatorId() + "\",operatorName:\"" + T9Utility.encodeSpecial(operatorName) + "\"";
      }
      data = data + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出主菜单和子菜单项的数据");
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
   * 查询调度 人员 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectManagerPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9VehicleOperatorLogic logic = new T9VehicleOperatorLogic();
      Map map = null;
      List<T9Person> personList = new ArrayList<T9Person>();
      personList = logic.getPersonByIds(dbConn, map);
      String data = "[";
      for (int i = 0; i < personList.size(); i++) {
        T9Person person = personList.get(i);
        data = data + "{seqId:" + person.getSeqId() + ",userName:\"" + person.getUserName() + "\"},";
      }
      if(personList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
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
   * 在线调度人员 -lz
   * 
   * */
  public String selectPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = T9VehicleOperatorLogic.selectPerson(dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
