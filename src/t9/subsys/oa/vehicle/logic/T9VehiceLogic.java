package t9.subsys.oa.vehicle.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vehicle.data.T9Vehicle;

public class T9VehiceLogic {
  public void addVehicle(Connection dbConn, T9Vehicle vehicle) throws Exception {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, vehicle);  
  }
  public ArrayList<T9Vehicle> selectVehicle(Connection dbConn, T9Vehicle vehicle, Map map) throws Exception{
    T9ORM orm = new T9ORM();
    ArrayList<T9Vehicle> vehicleList = (ArrayList<T9Vehicle>)orm.loadListSingle(dbConn, T9Vehicle.class, map);
    return vehicleList;
  }
  public ArrayList<Map<String,String>> selectVehicle(Connection dbConn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    ArrayList<Map<String,String>> vehicleList = new ArrayList<Map<String,String>>();
    try{
      String sql = "select v.SEQ_ID,v.V_DATE,v.V_MODEL,v.V_DRIVER,v.V_NUM,v.V_STATUS,v.V_REMARK,v.V_ENGINE_NUM,ci.CLASS_DESC"
                   +" from VEHICLE v left join CODE_ITEM ci on v.V_TYPE = ci.SEQ_ID left join CODE_CLASS cc on "
                   +" ci.CLASS_NO = cc.CLASS_NO where cc.CLASS_NO = 'VEHICLE_TYPE'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        map.put("seqId", rs.getString(1));
        map.put("vDate", rs.getString(2));
        map.put("vModel", rs.getString(3));
        map.put("vDriver", rs.getString(4)); 
        map.put("vNum", rs.getString(5));
        map.put("vStatus", rs.getString(6));
        map.put("vRemark", rs.getString(7));
        map.put("vEngineNum", rs.getString(8));
        map.put("classDesc", rs.getString(9));
        vehicleList.add(map);
      }
      
    }catch(Exception e){
      
    }finally{
      
    }
    return vehicleList;
  }
}
