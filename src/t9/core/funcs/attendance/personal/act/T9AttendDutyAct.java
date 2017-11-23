package t9.core.funcs.attendance.personal.act;

import java.net.InetAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.data.T9AttendDuty;
import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.funcs.attendance.personal.logic.T9AttendDutyLogic;
import t9.core.funcs.attendance.personal.logic.T9AttendLeaveLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.accesscontrol.data.T9AccessControl;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.accesscontrol.logic.T9AccesscontrolLogic;
import t9.core.funcs.system.accesscontrol.logic.T9IpRuleLogic;
import t9.core.funcs.system.act.common.T9ValidatorHelper;
import t9.core.funcs.system.attendance.data.T9AttendConfig;
import t9.core.funcs.system.attendance.data.T9AttendHoliday;
import t9.core.funcs.system.attendance.logic.T9AttendConfigLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.act.T9AttendScoreAct;

public class T9AttendDutyAct {
  /**
   * 
   * 添加登记记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addDuty(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String registerType = request.getParameter("registerType");
      String configId = request.getParameter("configId");
      String dutyType = request.getParameter("dutyType");
/*      
       * 判断是否超过登记时间段

       
      //得到自己对应的排班类型

      int configId = user.getDutyType();
      //根据排班类型Id得到排版类型的属性

      T9AttendConfigLogic configLogic = new T9AttendConfigLogic();
      T9AttendConfig config = configLogic.selectConfigById(dbConn, String.valueOf(configId));
      String dutyName = config.getDutyName();
      String dutyTime1 = config.getDutyTime1();
      String dutyTime2 = config.getDutyTime2();
      String dutyTime3 = config.getDutyTime3();
      String dutyTime4 = config.getDutyTime4();
      String dutyTime5 = config.getDutyTime5();
      String dutyTime6 = config.getDutyTime6();
      String dutyType1 = config.getDutyType1();
      String dutyType2 = config.getDutyType2();
      String dutyType3 = config.getDutyType3();
      String dutyType4 = config.getDutyType4();
      String dutyType5 = config.getDutyType5();
      String dutyType6 = config.getDutyType6();
      String dutyTime = "";
      String dutyType = "";
      if(registerType.equals("1")){
        dutyTime = dutyTime1;
        dutyType = dutyType2;
      }
      //得到上下班四个时间段的间隔

      String dutyIntervalBefore1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE1");
      String dutyIntervalAfter1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER1");
      String dutyIntervalBefore2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE2");
      String dutyIntervalAfter2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER2");
      long before1 = 0;
      long after1 = 0;
      long before2 = 0;
      long after2 = 0;
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
      String dateStr = dateFormat1.format(new Date());
      long dateLong = Long.parseLong(dateStr.substring(0, 2))*3600 + Long.parseLong(dateStr.substring(3, 5))*60 + Long.parseLong(dateStr.substring(6, 8));
      //System.out.println(dateLong);
      if(!dutyIntervalBefore1.equals("")){
        //System.out.println(dutyIntervalBefore1.length());
        before1 = Long.parseLong(dutyIntervalBefore1)*60;
      }
      if(!dutyIntervalAfter1.equals("")){
        after1 = Long.parseLong(dutyIntervalAfter1)*60;
      }
      if(!dutyIntervalBefore2.equals("")){
        before2 = Long.parseLong(dutyIntervalBefore2)*60;
      }
      if(!dutyIntervalAfter2.equals("")){
        after2 = Long.parseLong(dutyIntervalAfter2)*60;
      } */
      
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        //registerIp = localIp;
      }
      //System.out.println(registerIp+registerType);
      duty.setRegisterTime(dateFormat.parse(dateFormat.format(new Date())));
      duty.setRegisterIp(registerIp);
      duty.setUserId(String.valueOf(userId));
      duty.setRegisterType(registerType);
      t9adl.addDuty(dbConn, duty);
      T9AttendScoreAct scoreAct = new T9AttendScoreAct();
      scoreAct.addRegister(request, response, configId, dutyType, registerType);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/registerduty.jsp");
    return "";
  }
  /**
   * 得到上下班登记的四个时间段

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDutyInterval(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      String dutyIntervalBefore1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE1");
      String dutyIntervalAfter1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER1");
      String dutyIntervalBefore2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE2");
      String dutyIntervalAfter2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER2");
      long before1 = 0;
      long after1 = 0;
      long before2 = 0;
      long after2 = 0;
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
      String dateStr = dateFormat1.format(new Date());
      long dateLong = Long.parseLong(dateStr.substring(0, 2))*3600 + Long.parseLong(dateStr.substring(3, 5))*60 + Long.parseLong(dateStr.substring(6, 8));
      //System.out.println(dateLong);
      if(dutyIntervalBefore1!=null&&!dutyIntervalBefore1.trim().equals("")){
        //System.out.println(dutyIntervalBefore1.length());
        before1 = Long.parseLong(dutyIntervalBefore1)*60;
      }
      if(dutyIntervalAfter1!=null&&!dutyIntervalAfter1.trim().equals("")){
        after1 = Long.parseLong(dutyIntervalAfter1)*60;
      }
      if(dutyIntervalBefore2!=null&&!dutyIntervalBefore2.trim().equals("")){
        before2 = Long.parseLong(dutyIntervalBefore2)*60;
      }
      if(dutyIntervalAfter2!=null&&!dutyIntervalAfter2.trim().equals("")){
        after2 = Long.parseLong(dutyIntervalAfter2)*60;
      } 
      String data = "{curDate:"+dateLong+",before1:"+before1+",after1:"+after1+",before2:"+before2+",after2:"+after2
        +",dutyIntervalBefore1:\""+dutyIntervalBefore1+"\",dutyIntervalAfter1:\""+dutyIntervalAfter1+"\",dutyIntervalBefore2:\""
        +dutyIntervalBefore2+"\",dutyIntervalAfter2:\""+dutyIntervalAfter2+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, dutyIntervalBefore1+","+dutyIntervalAfter1+","+dutyIntervalBefore2+","+dutyIntervalAfter2);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询排班类型 根据用户的id
   * 为刚加载的时候得到排班的Name
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDutyByUserIdName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data  = "";
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      //根据userId得到DUTY_TYPE对应的是attend_config表中的SEQ_ID;
      T9PersonLogic personLogic  = new T9PersonLogic();
      T9Person person = personLogic.getPerson(dbConn, String.valueOf(userId));
      int configSeqId = person.getDutyType();//5
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      T9AttendConfig config = t9adl.selectConfigById(dbConn, String.valueOf(configSeqId));
      if(config!=null){
        data  = data + T9FOM.toJson(config) ;
      }else{
        data = "{}";
      }
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
   * 查询排班类型 根据用户的id
   * 并且判断上下班的时间根据设定好的时间段是否可以登记。如果dutyStatus=0是 可以登记的 如果 是 1不能登记
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDutyByUserId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data  = "{";
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      //根据userId得到DUTY_TYPE对应的是attend_config表中的SEQ_ID;
      T9PersonLogic personLogic  = new T9PersonLogic();
      T9Person person = personLogic.getPerson(dbConn, String.valueOf(userId));
      int configSeqId = person.getDutyType();//5
      T9DBUtility t9dbu = new T9DBUtility();
      String DBStr =  t9dbu.curDayFilter("REGISTER_TIME");
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      T9AttendConfig config = t9adl.selectConfigById(dbConn, String.valueOf(configSeqId));
      if(config!=null){
        String dutyName = config.getDutyName();
        String dutyTime1 = config.getDutyTime1();
        String dutyTime2 = config.getDutyTime2();
        String dutyTime3 = config.getDutyTime3();
        String dutyTime4 = config.getDutyTime4();
        String dutyTime5 = config.getDutyTime5();
        String dutyTime6 = config.getDutyTime6();
        String dutyType1 = config.getDutyType1();
        String dutyType2 = config.getDutyType2();
        String dutyType3 = config.getDutyType3();
        String dutyType4 = config.getDutyType4();
        String dutyType5 = config.getDutyType5();
        String dutyType6 = config.getDutyType6();
        String dutyIntervalBefore1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE1");
        String dutyIntervalAfter1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER1");
        String dutyIntervalBefore2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE2");
        String dutyIntervalAfter2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER2");
        if(dutyIntervalBefore1==null){
          dutyIntervalBefore1 = "";
        }
        if(dutyIntervalAfter1==null){
          dutyIntervalAfter1 = "";
        }
        if(dutyIntervalBefore2==null){
          dutyIntervalBefore2 = "";
        }
        if(dutyIntervalAfter2==null){
          dutyIntervalAfter2 = "";
        }
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        long before1 = 0;
        long after1 = 0;
        long before2 = 0;
        long after2 = 0;
        long temp = 0;
        int c_zStatus1 =0;//判断迟到早退0为正常1为迟到,2为早退
        int c_zStatus2 =0;
        int c_zStatus3 =0;
        int c_zStatus4 =0;
        int c_zStatus5 =0;
        int c_zStatus6 =0;
        String registerType = "0";
        long dateLong = Long.parseLong(dateStr.substring(0, 2))*3600 + Long.parseLong(dateStr.substring(3, 5))*60 + Long.parseLong(dateStr.substring(6, 8));
        //System.out.println(dateLong);
        if(dutyIntervalBefore1!=null&&!dutyIntervalBefore1.trim().equals("")){
          //System.out.println(dutyIntervalBefore1.length());
          before1 = Long.parseLong(dutyIntervalBefore1)*60;
        }
        if(dutyIntervalAfter1!=null&&!dutyIntervalAfter1.trim().equals("")){
          after1 = Long.parseLong(dutyIntervalAfter1)*60;
        }
        if(dutyIntervalBefore2!=null&&!dutyIntervalBefore2.trim().equals("")){
          before2 = Long.parseLong(dutyIntervalBefore2)*60;
        }
        if(dutyIntervalAfter2!=null&&!dutyIntervalAfter2.trim().equals("")){
          after2 = Long.parseLong(dutyIntervalAfter2)*60;
        } 
        //System.out.println(DBStr);
        List<T9AttendDuty> dutyList = t9adl.selectDuty(dbConn, String.valueOf(userId), DBStr,"","");
     /*   for (int i = 0; i < dutyList.size(); i++) {
          duty = dutyList.get(i);
          temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
          registerType = duty.getRegisterType();
        }*/
        if(dutyTime1 !=null&& !dutyTime1.trim().equals("")){
          long dutyTimeInt1 = getLongByDutyTime(dutyTime1);
           if(!dutyIntervalBefore1.equals("")){
            if(dutyType1.equals("1")){
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("1")){break;}
              }
              if(registerType.equals("1")){
                if(temp-dutyTimeInt1>0){
                  c_zStatus1 = 1;
                }   
              }
              //System.out.println(dutyTimeInt1 +":" + dateLong+":" +before1 +":"+after1 );
              if(((dutyTimeInt1-dateLong) <= before1 && (dutyTimeInt1-dateLong)>=0) ||(dateLong  - dutyTimeInt1)>=0 && (dateLong  - dutyTimeInt1) <= after1 ){
                
                data = data + "dutyTime1:\"" + dutyTime1 + "\",dutyType1:" + dutyType1 + ",dutyStatus1:0,";
              }else{
                data = data + "dutyTime1:\"" + dutyTime1 + "\",dutyType1:" + dutyType1 + ",dutyStatus1:1,";
              }
            }else{
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("1")){break;}
              }
              if(registerType.equals("1")){
                if(dutyTimeInt1 - temp>0){
                  c_zStatus1 = 2;
                }   
              }
              if(((dutyTimeInt1-dateLong) <= before2 && (dutyTimeInt1-dateLong)>=0) ||(dateLong  - dutyTimeInt1)>=0 && (dateLong  - dutyTimeInt1) <= after2 ){
                data = data + "dutyTime1:\"" + dutyTime1 + "\",dutyType1:" + dutyType1 + ",dutyStatus1:0,";
              }else{
                data = data + "dutyTime1:\"" + dutyTime1 + "\",dutyType1:" + dutyType1 + ",dutyStatus1:1,";
              }
            }
          }else{
            data = data + "dutyTime1:\"" + dutyTime1 + "\",dutyType1:" + dutyType1 + ",dutyStatus1:1,";
          }
        }else{
          data = data + "dutyTime1:\"\",dutyType1:\"\",dutyStatus1:1,";
        }
        data = data + "c_zStatus1:" + c_zStatus1 + ",";
        //第二次登记 
        if(dutyTime2 != null&& !dutyTime2.trim().equals("")){
          long dutyTimeInt2 = getLongByDutyTime(dutyTime2);
          if(!dutyIntervalBefore1.equals("")){
            if(dutyType2.equals("1")){
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("2")){break;}
              }
              if(registerType.equals("2")){
                if(temp-dutyTimeInt2>0){
                  c_zStatus2 = 1;
                }   
              }
              if(((dutyTimeInt2-dateLong) <= before1 && (dutyTimeInt2-dateLong)>=0) ||(dateLong  - dutyTimeInt2)>=0 && (dateLong  - dutyTimeInt2) <= after1 ){
                data = data + "dutyTime2:\"" + dutyTime2 + "\",dutyType2:" + dutyType2 + ",dutyStatus2:0,";
              }else{
                data = data + "dutyTime2:\"" + dutyTime2 + "\",dutyType2:" + dutyType2 + ",dutyStatus2:1,";
              }
            }else{
              //System.out.println(dutyTimeInt2 +":"+temp);
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("2")){break;}
              }
              if(registerType.equals("2")){
                if(dutyTimeInt2 - temp>0){
                  c_zStatus2 = 2;
                }   
              }
              //System.out.println(dutyTimeInt2 +":" + dateLong+":" +before1 +":"+after1 );
              if(((dutyTimeInt2-dateLong) <= before2 && (dutyTimeInt2-dateLong)>=0) ||(dateLong  - dutyTimeInt2)>=0 && (dateLong  - dutyTimeInt2) <= after2 ){
                data = data + "dutyTime2:\"" + dutyTime2+ "\",dutyType2:" + dutyType2 + ",dutyStatus2:0,";
              }else{
                data = data + "dutyTime2:\"" + dutyTime2 + "\",dutType2:" + dutyType2 + ",dutyStatus2:1,";
              }
            }
          }else{
            data = data + "dutyTime2:\"" + dutyTime2 + "\",dutyType2:" + dutyType2 + ",dutyStatus2:1,";
          }
        }else{
          data = data + "dutyTime2:\"\",dutyType2:\"\",dutyStatus2:1,";
        }
        data = data + "c_zStatus2:" + c_zStatus2 + ",";
        //第三次登记


        if(dutyTime3  != null&& !dutyTime3.trim().equals("")){
          long dutyTimeInt3 = getLongByDutyTime(dutyTime3);
          if(!dutyIntervalBefore1.equals("")){
            if(dutyType3.equals("1")){ 
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("3")){break;}
              }
              if(registerType.equals("3")){
                if(temp-dutyTimeInt3>0){
                  c_zStatus3 = 1;
                }   
              }
              if(((dutyTimeInt3-dateLong) <= before1 && (dutyTimeInt3-dateLong)>=0) ||(dateLong  - dutyTimeInt3)>=0 && (dateLong  - dutyTimeInt3) <= after1 ){
                data = data + "dutyTime3:\"" + dutyTime3 + "\",dutyType3:" + dutyType3 + ",dutyStatus3:0,";
              }else{
                data = data + "dutyTime3:\"" + dutyTime3 + "\",dutyType3:" + dutyType3 + ",dutyStatus3:1,";
              }
            }else{
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("3")){break;}
              }
              if(registerType.equals("3")){
                if(dutyTimeInt3 - temp>0){
                  c_zStatus3 = 2;
                }   
              }
              if(((dutyTimeInt3-dateLong) <= before2 && (dutyTimeInt3-dateLong)>=0) ||(dateLong  - dutyTimeInt3)>=0 && (dateLong  - dutyTimeInt3) <= after2 ){
                data = data + "dutyTime3:\"" + dutyTime3+ "\",dutyType3:" + dutyType3 + ",dutyStatus3:0,";
              }else{
                data = data + "dutyTime3:\"" + dutyTime3 + "\",dutType3:" + dutyType3 + ",dutyStatus3:1,";
              }
            }
          }else{
            data = data + "dutyTime3:\"" + dutyTime3 + "\",dutyType3:" + dutyType3 + ",dutyStatus3:1,";
          }
        }else{
          data = data + "dutyTime3:\"\",dutyType3:\"\",dutyStatus3:1,";
        }
        data = data + "c_zStatus3:" + c_zStatus3 + ",";
        //第三次登记


        if(dutyTime4 != null&& !dutyTime4.trim().equals("")){
          long dutyTimeInt4 = getLongByDutyTime(dutyTime4);
          if(!dutyIntervalBefore1.equals("")){
            if(dutyType4.equals("1")){
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("4")){break;}
              }
              if(registerType.equals("4")){
                if(temp-dutyTimeInt4>0){
                  c_zStatus4 = 1;
                }   
              }
              if(((dutyTimeInt4-dateLong) <= before1 && (dutyTimeInt4-dateLong)>=0) ||(dateLong  - dutyTimeInt4)>=0 && (dateLong  - dutyTimeInt4) <= after1 ){
                data = data + "dutyTime4:\"" + dutyTime4+ "\",dutyType4:" + dutyType4 + ",dutyStatus4:0,";
              }else{
                data = data + "dutyTime4:\"" + dutyTime4 + "\",dutyType4:" + dutyType4 + ",dutyStatus4:1,";
              }
            }else{
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("4")){break;}
              }
              if(registerType.equals("4")){
                if(dutyTimeInt4 - temp>0){
                  c_zStatus4 = 2;
                }   
              }
              if(((dutyTimeInt4-dateLong) <= before2 && (dutyTimeInt4-dateLong)>=0) ||(dateLong  - dutyTimeInt4)>=0 && (dateLong  - dutyTimeInt4) <= after2 ){
                data = data + "dutyTime4:\"" + dutyTime4+ "\",dutyType4:" + dutyType4 + ",dutyStatus4:0,";
              }else{
                data = data + "dutyTime4:\"" + dutyTime4 + "\",dutType4:" + dutyType4 + ",dutyStatus4:1,";
              }
            }
          }else{
            data = data + "dutyTime4:\"" + dutyTime4 + "\",dutyType4:" + dutyType4 + ",dutyStatus4:1,";
          }
        }else{
          data = data + "dutyTime4:\"\",dutyType4:\"\",dutyStatus4:1,";
        }
        data = data + "c_zStatus4:" + c_zStatus4 + ",";
        //第五次登记


        if(dutyTime5 != null && !dutyTime5.trim().equals("")){
          //System.out.println(dutyTime5);
          long dutyTimeInt5 = getLongByDutyTime(dutyTime5);
          if(!dutyIntervalBefore1.equals("")){
            if(dutyType5.equals("1")){
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("5")){break;}
              }
              if(registerType.equals("5")){
                if(temp-dutyTimeInt5>0){
                  c_zStatus5 = 1;
                }   
              }
              if(((dutyTimeInt5-dateLong) <= before1 && (dutyTimeInt5-dateLong)>=0) ||(dateLong  - dutyTimeInt5)>=0 && (dateLong  - dutyTimeInt5) <= after1 ){
                data = data + "dutyTime5:\"" + dutyTime5+ "\",dutyType5:" + dutyType5 + ",dutyStatus5:0,";
              }else{
                data = data + "dutyTime5:\"" + dutyTime5 + "\",dutyType5:" + dutyType5 + ",dutyStatus5:1,";
              }
            }else{
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("5")){break;}
              }
              if(registerType.equals("5")){
                if(dutyTimeInt5 - temp>0){
                  c_zStatus5 = 2;
                }   
              }
              if(((dutyTimeInt5-dateLong) <= before2 && (dutyTimeInt5-dateLong)>=0) ||(dateLong  - dutyTimeInt5)>=0 && (dateLong  - dutyTimeInt5) <= after2 ){
                data = data + "dutyTime5:\"" + dutyTime5+ "\",dutyType5:" + dutyType5 + ",dutyStatus5:0,";
              }else{
                data = data + "dutyTime5:\"" + dutyTime5 + "\",dutType5:" + dutyType5 + ",dutyStatus5:1,";
              }
            }
          }else{
            data = data + "dutyTime5:\"" + dutyTime5 + "\",dutyType5:" + dutyType5 + ",dutyStatus5:1,";
          }
        }else{
          data = data + "dutyTime5:\"\",dutyType5:\"\",dutyStatus5:1,";
        }
        data = data + "c_zStatus5:" + c_zStatus5 + ",";
        //第六次登记


        if(dutyTime6 != null && !dutyTime6.trim().equals("")){
          long dutyTimeInt6 = getLongByDutyTime(dutyTime6);
          if(!dutyIntervalBefore1.equals("")){
            if(dutyType6.equals("1")){
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("6")){break;}
              }
              if(registerType.equals("6")){
                if(temp-dutyTimeInt6>0){
                  c_zStatus6 = 1;
                }   
              }
              if(((dutyTimeInt6-dateLong) <= before1 && (dutyTimeInt6-dateLong)>=0) ||(dateLong  - dutyTimeInt6)>=0 && (dateLong  - dutyTimeInt6) <= after1 ){
                data = data + "dutyTime6:\"" + dutyTime6 + "\",dutyType6:" + dutyType6 + ",dutyStatus6:0,";
              }else{
                data = data + "dutyTime6:\"" + dutyTime6 + "\",dutyType6:" + dutyType6 + ",dutyStatus6:1,";
              }
            }else{
              for (int i = 0; i < dutyList.size(); i++) {
                duty = dutyList.get(i);
                temp = getLongByDutyTime(dateFormat.format(duty.getRegisterTime()));
                registerType = duty.getRegisterType();
                if(registerType.equals("6")){break;}
              }
              if(registerType.equals("6")){
                if(dutyTimeInt6 - temp>0){
                  c_zStatus6 = 2;
                }   
              }
              if(((dutyTimeInt6-dateLong) <= before2 && (dutyTimeInt6-dateLong)>=0) ||(dateLong  - dutyTimeInt6)>=0 && (dateLong  - dutyTimeInt6) <= after2 ){
                data = data + "dutyTime6:\"" + dutyTime6 + "\",dutyType6:" + dutyType6 + ",dutyStatus6:0,";
              }else{
                data = data + "dutyTime6:\"" + dutyTime6 + "\",dutType6:" + dutyType6 + ",dutyStatus6:1,";
              }
            }
          }else{
            data = data + "dutyTime6:\"" + dutyTime6 + "\",dutyType6:" + dutyType6 + ",dutyStatus6:1,";
          }
        }else{
          data = data + "dutyTime6:\"\",dutyType6:\"\",dutyStatus6:1,";
        }
        data = data + "c_zStatus6:" + c_zStatus6 + ",";
        data = data + "seqId:" + configSeqId + ",dutyName:\"" + dutyName +"\",general:\""+config.getGeneral()+"\"}";
      
      }else{
        data = data + "}";
      }
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
   * 检查是否是公假日

   * status =0显示登记TABLE 1 为 不显示

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkHoliday(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
      Map map = new HashMap();
      String data = "{status:1}";
      List<T9AttendHoliday> holidayList = t9adl.selectHoliday(dbConn, map);
      for (int i = 0; i < holidayList.size(); i++) {
        T9AttendHoliday holiday = holidayList.get(i);
        Date beginDate = holiday.getBeginDate();
        Date endDate = holiday.getEndDate();
        Date date = new Date();
        String beginDateStr = dateFormat.format(beginDate);
        String endDateStr = dateFormat.format(endDate);
        String dateStr = dateFormat.format(date);
          if(beginDateStr.compareTo(dateStr) <= 0 && endDateStr.compareTo(dateStr) >= 0){
            data = "{status:0}";
            break ;
          }
        }
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
   * 判断今天是否是请假时间段。是否可以上下班登记
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkIsLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String curDateStr = T9Utility.getCurDateTimeStr();
      String[] str = {"USER_ID = '" + userId + "'",T9DBUtility.getDateFilter("LEAVE_DATE1", curDateStr, "<="),T9DBUtility.getDateFilter("LEAVE_DATE2", curDateStr, ">="),"ALLOW=1"};
      T9AttendLeaveLogic leaveLogic = new T9AttendLeaveLogic();
      List<T9AttendLeave> leaveList = leaveLogic.selectLeave(dbConn, str);
      String data = "{isLeave:0}";
      if(leaveList.size()>0){
        data = "{isLeave:1}";
      }
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
   * 
   * 添加查询所有登记记录

   * 并判断当天的登记 记录是否以登记（第几次登记 有关做判断）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDuty(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      T9DBUtility t9dbu = new T9DBUtility();
      T9Utility t9u = new T9Utility();
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9FOM fom = new T9FOM();
      String data = "[";
      String dateStr =  t9dbu.curDayFilter("REGISTER_TIME");
      //System.out.println(dateStr);
      //System.out.println(dateStr);
      List<T9AttendDuty> dutyList = t9adl.selectDuty(dbConn, String.valueOf(userId), dateStr,"","");
      for (int i = 0; i < dutyList.size(); i++) {
        data = data + T9FOM.toJson(dutyList.get(i)).toString() + ",";
      }
      if(dutyList.size()>0){
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
   * 
   * 添加当天一条登记记录ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDutyById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      String seqId = request.getParameter("seqId");
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      duty = t9adl.selectDutyById(dbConn, seqId);
      T9FOM fom = new T9FOM();
      data = data + fom.toJson(duty);
      if(data.equals("")){
        data = "{}";
      }
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
   * 
   * 更新当天一条登记记录ById(remark)
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateDutyById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String remark = request.getParameter("remark");
      remark = remark.replaceAll("\\\n", "");
      remark = remark.replaceAll("\\\r", "");
      T9AttendDuty duty = new T9AttendDuty();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      t9adl.updateRemarkById(dbConn, seqId, remark);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
     return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 更新当天一条登记记录ById(registerTime)
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateRegisterTimeById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendDuty duty = new T9AttendDuty();
      T9DBUtility t9dbu = new T9DBUtility();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = new Date();
      String dateStr = format.format(date);
      //System.out.println(dateStr);
      T9Utility t9u = new T9Utility();
      String seqId = request.getParameter("seqId");
      String registerType = request.getParameter("registerType");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
       // registerIp = localIp;
      }
      //System.out.println(seqId);
      duty.setSeqId(Integer.parseInt(seqId));
      duty.setRegisterType(registerType);
      duty.setUserId(String.valueOf(userId));
      duty.setRegisterIp(registerIp);
      duty.setRegisterTime( T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",dateStr));
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      t9adl.updateDutyById(dbConn,duty);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/attendance/personal/registerduty.jsp";
  }
  public long getLongByDutyTime(String dutyTime){
    long time = 0;
    String times[] = dutyTime.split(":");
    int length = times.length;
    for (int i = 0; i < times.length; i++) {
      time = time + Long.parseLong(times[i])* (long)(Math.pow(60, length-1-i)) ;
    }
    return time;
  }
  /**
   * 根据一段日期得到所有的上下班记录s
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectdutyByDate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendDuty duty = new T9AttendDuty();  
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String dutyDate1 = request.getParameter("dutyDate1");
      String dutyDate2 = request.getParameter("dutyDate2");
      dutyDate2 = dutyDate2 + " 23:59:59";
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9PersonLogic personLogic  = new T9PersonLogic();
      T9Person person = personLogic.getPerson(dbConn, String.valueOf(userId));
      //根据用户得到排版类型
      int configSeqId = person.getDutyType();
      T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
      //得到所有在一段时间内的上下班登记
      List<Map<String,String>> dutyList = t9adl.selectDutyByDate(dbConn, String.valueOf(userId), T9DBUtility.getDateFilter("REGISTER_TIME", dutyDate1, ">="), T9DBUtility.getDateFilter("REGISTER_TIME", dutyDate2, "<="),String.valueOf(configSeqId));
      //转化为每一天的
      List<Map<String,String>> dayDutyList = t9adl.selectDutyByDate(dutyList);
      //System.out.println(dayDutyList.size());
      StringBuffer buffer=new StringBuffer("["); 
      for(Map<String, String> equipmentsMap:dayDutyList){ 
      buffer.append("{"); 
      Set<String>keySet=equipmentsMap.keySet(); 
      for(String mapStr:keySet){ 
        //System.out.println(mapStr + ":>>>>>>>>>>>>" + equipmentsMap.get(mapStr)); 
        String name=equipmentsMap.get(mapStr); 
        if(name!=null){
          name =name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        buffer.append( mapStr+":\"" + (name==null? "":name) + "\","); 
      } 
      buffer.deleteCharAt(buffer.length()-1); 
      buffer.append("},"); 
      }
      SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日");
      SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
      long daySpace = T9Utility.getDaySpan(format2.parse(dutyDate1), format2.parse(dutyDate2))+1;
      dutyDate1 = format1.format(format2.parse(dutyDate1));
      dutyDate2 = format1.format(format2.parse(dutyDate2));
      buffer.deleteCharAt(buffer.length()-1); 
      if (dayDutyList.size()>0) { 
        buffer.append("]"); 
      }else { 
        buffer.append("[]"); 
      }
      String data = buffer.toString();
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, dutyDate1 + "," +dutyDate2 + "," + daySpace);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 
   * 查询configById
   */
  public String selectConfigById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9PersonLogic personLogic  = new T9PersonLogic();
      T9Person person = personLogic.getPerson(dbConn, String.valueOf(userId));
      //根据用户得到排版类型
      int configSeqId = person.getDutyType();
      String seqId = String.valueOf(configSeqId);//根据用户得到config.SeqId;
      String data = "";
      //System.out.println(seqId+"-------------->");
      T9AttendConfig config = new T9AttendConfig();
      T9AttendConfigLogic t9acl = new T9AttendConfigLogic();
      if(!seqId.equals("")){
        config = t9acl.selectConfigById(dbConn, seqId);
        data = data+(T9FOM.toJson(config)).toString() + "";
      }else{
        data = "{}";
      } 
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " 查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 判断时候为管理员身份

   */
  public static boolean IsAdmin(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    boolean IsManage= false;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(userPriv.equals("1")){
        IsManage = true;
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return IsManage;
  }
  /**
   * 查询访问权限IP 根据TYPE（0为登录1为考勤）

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getIpRuleByType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9IpRuleLogic ruleLogic = new T9IpRuleLogic();
      String ip =  getIpAddr(request) ;
      ArrayList<T9IpRule> ruleList = new ArrayList<T9IpRule>();
      ruleList = ruleLogic.getIpRule(dbConn,"1");
     // ip = request.getRemoteAddr();
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      String isIp = "2";//1为可以考勤，2为不能考勤
      if(ip!=null&&ip.equals("127.0.0.1")){
        ip = localIp;
        isIp = "1";
      }else{
     
     /*   String[] ipStrArray = ip.split("\\.");
        String ipStr = "0";
        if(ipStrArray.length>0){
          ipStr = ipStrArray[ipStrArray.length-1];
        }
*/
        for (int i = 0; i < ruleList.size(); i++) {
          T9IpRule ipRule = ruleList.get(i);
          String beginIp = ipRule.getBeginIp();
          String endIp = ipRule.getEndIp();
          if(T9ValidatorHelper.betweenIP(ip, beginIp, endIp)){
            isIp = "1";
            break;
          }
         /* String[] beginIpArray = beginIp.split("\\.");
          String[] endIpArray = endIp.split("\\.");
          if(Integer.parseInt(ipStr)>=Integer.parseInt(beginIpArray[beginIpArray.length-1])&&Integer.parseInt(ipStr)<=Integer.parseInt(endIpArray[endIpArray.length-1])){
            isIp = "1";
            break;
          }*/
        }
        if(ruleList.size()==0){
          isIp = "1";
        }
      }
      
      String data = "{isIp:" + isIp + ",ip:\""+ip+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  //得到客户端的IP
  public String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    ip = request.getHeader("Proxy-Client-IP");
    }
    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    ip = request.getRemoteAddr();
    }
    return ip;
    }
  /**
   * 查询不受IP限制的人员

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getNoIpUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AccesscontrolLogic orgLogic = new T9AccesscontrolLogic();
      T9AccessControl org = null;
      org = orgLogic.getAccessControl(dbConn);
      //System.out.println(org.getParaName()+":"+org.getParaValue());
      String isNoIp = "2";//2为不是不受IP权限访问的人员1为是
      if(org.getParaValue()!=null){
        String[] orgNo = org.getParaValue().split(",");
        for (int i = 0; i < orgNo.length; i++) {
          if(orgNo[i].equals(String.valueOf(userId))){
            isNoIp = "1";
            break;
          }
        }
      }

      String data = "{isNoIp:" + isNoIp + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取值班次数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendDutyCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = userIdStr;
      }
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      T9AttendDutyLogic adl = new T9AttendDutyLogic();
      int data = adl.getAttendDutyCountLogic(dbConn, year, month, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 值班工资数

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendDutyMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = userIdStr;
      }
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      T9AttendDutyLogic adl = new T9AttendDutyLogic();
      double data = adl.getAttendDutyMoneyLogic(dbConn, year, month, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public static void main(String[] args) {
    /*SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    String dateStr = dateFormat.format(date);
    //System.out.println(dateStr);
    long l = Long.parseLong("4")*25;
    //System.out.println(l);*/
    String s = "aadd";
    //System.out.println(s.contains("aD"));
  }
}
