package t9.cms.setting.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.cms.setting.data.T9SysPara;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.cms.setting.logic.T9JhSysParaLogic;


public class T9JhSysParaAct {

  /**
   * 添加-修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAndUpdate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysPara hall = new T9SysPara() ;
      String paraName = request.getParameter("paraName");
      String paraValue = request.getParameter("paraValue");
      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, paraName);//

      if (sysPara == null) {
        hall.setParaName(paraName);
        hall.setParaValue(paraValue);
        T9JhSysParaLogic.addHall(dbConn, hall);
      } else {
        sysPara.setParaName(paraName);
        sysPara.setParaValue(paraValue);
        T9JhSysParaLogic.updateHall(dbConn, sysPara);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 添加-修改,团组号设置
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String NOaddAndUpdate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysPara hall = (T9SysPara) T9FOM.build(request.getParameterMap());
      String paraName = request.getParameter("paraName");
      T9SysPara sysHall = T9JhSysParaLogic.hallObj(dbConn, paraName);
      if (sysHall == null) {
        T9JhSysParaLogic.addHall(dbConn, hall);
      } else {
        T9JhSysParaLogic.updateHall(dbConn, hall);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectObj(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paraName = request.getParameter("paraName");
      T9SysPara sysHall = null;
      String data = "";
      if (!T9Utility.isNullorEmpty(paraName)) {
        sysHall = T9JhSysParaLogic.hallObj(dbConn, paraName);
        // 定义数组将数据保存到Json中
        if (sysHall != null) {
          data = data + T9FOM.toJson(sysHall);
        }
      }
      if (data.equals("")) {
        data = "{seqId:0,paraName:\"" + paraName + "\",paraValue:\"0\"}";
      }
      // 保存查询数据是否成功，保存date
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }
  /**
   * 添加-修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAndUpdateAttach(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysPara hall = new T9SysPara() ;
      String imageFileSize=request.getParameter("IMAGE_FILE_SIZE");
      String audioFileSize=request.getParameter("AUDIO_FILE_SIZE");
      String videoFileSize=request.getParameter("VIDEO_FILE_SIZE");
      String otherFileSize=request.getParameter("OTHER_FILE_SIZE");
      String paraName = request.getParameter("paraName"); 
      String paraValue = request.getParameter("paraValue");
      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, paraName);
      T9SysPara sysPara1 = T9JhSysParaLogic.hallObj(dbConn, "IMAGE_FILE_SIZE");
      T9SysPara sysPara2 = T9JhSysParaLogic.hallObj(dbConn, "AUDIO_FILE_SIZE");
      T9SysPara sysPara3 = T9JhSysParaLogic.hallObj(dbConn, "VIDEO_FILE_SIZE");
      T9SysPara sysPara4 = T9JhSysParaLogic.hallObj(dbConn, "OTHER_FILE_SIZE");
      if (sysPara == null) {
        hall.setParaName(paraName);
        hall.setParaValue(paraValue);
        T9JhSysParaLogic.addHall(dbConn, hall);
      } else {
        sysPara.setParaName(paraName);
        sysPara.setParaValue(paraValue);
        T9JhSysParaLogic.updateHall(dbConn, sysPara);
      }
      if (sysPara1 == null) {
          hall.setParaName("IMAGE_FILE_SIZE");
          hall.setParaValue(imageFileSize);
          T9JhSysParaLogic.addHall(dbConn, hall);
        } else {
        	sysPara1.setParaName("IMAGE_FILE_SIZE");
        	sysPara1.setParaValue(imageFileSize);
          T9JhSysParaLogic.updateHall(dbConn, sysPara1);
        }
      if (sysPara2 == null) {
          hall.setParaName("AUDIO_FILE_SIZE");
          hall.setParaValue(audioFileSize);
          T9JhSysParaLogic.addHall(dbConn, hall);
        } else {
        	sysPara2.setParaName("AUDIO_FILE_SIZE");
           sysPara2.setParaValue(audioFileSize);
           T9JhSysParaLogic.updateHall(dbConn, sysPara2);
        }
      if (sysPara3 == null) {
          hall.setParaName("VIDEO_FILE_SIZE");
          hall.setParaValue(videoFileSize);
          T9JhSysParaLogic.addHall(dbConn, hall);
        } else {
        	sysPara3.setParaName("VIDEO_FILE_SIZE");
        	sysPara3.setParaValue(videoFileSize);
           T9JhSysParaLogic.updateHall(dbConn, sysPara3);
        }
      if (sysPara4 == null) {
          hall.setParaName("OTHER_FILE_SIZE");
          hall.setParaValue(otherFileSize);
          T9JhSysParaLogic.addHall(dbConn, hall);
        } else {
        	sysPara4.setParaName("OTHER_FILE_SIZE");
        	sysPara4.setParaValue(otherFileSize);
          T9JhSysParaLogic.updateHall(dbConn, sysPara4);
        }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

}
