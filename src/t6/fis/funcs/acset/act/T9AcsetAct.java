package t6.fis.funcs.acset.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9AcsetAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.acset.logic.T9AcsetLogic");
  /**
   * 帐套主页面打开
   * <ol>
   * <li>此功能浏览以前帐套</li>
   * <li>执行sql查询，查ACCOUNT_SET表，的到的数据放放到acset对象中去，然后在把acset放入List中返回</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * </ol>
   * @author ych
   * @param request  
   * @param response list集合
   * @return
   * @throws Exception 不清楚
   * **/

  public String acsetIndex(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 创建帐套
   * <ol>
   * <li>此功能是创建新的帐套,使用者可以根据需要创建多个帐套，方便记账。</li>
   * <li>获取数据库连接。</li>
   * <li>获取当前帐套个数，获取最大可创建帐套个数，判断是否达到最大个数，如果已达到提示信息，未达到，继续创建。</li>
   * <li>从提交表单中创建账套用参数对象，具体内容，定义帐套新名称、会计期间规则、当前的时间、会计年度和开始时间等参数，
   *     由前台穿过来的数据对应赋值，if(会计期间规则.equals(自然年月)||会计期间规则不为空)开始时间为获取的年度的一月份开始，否则可以手动调试月份，
   *     获取当前创建帐套的人部门ID，外币核算，在获取汇率长度、汇率精度、金额长度、金额精度，如果没有填写则取默认值，否则等于填写值，获取本位币，及其本位币标志
   *     如果没有书写，默认值为人民币，获取首期会计期间，创建AcsetBuildParam对象，把帐套新名称、会计期间规则、当前的时间、会计年度、开始时间、
   *     汇率长度、汇率精度、金额长度、金额精度和首期会计期间放入里面，返回AcsetBuildParam对象</li>
   * <li>然后开始创建系统数据库(传入的参数：数据连接,系统参数，acsetBuildParam对象)，逐个acsetBuildParam对象里面的数据，
   *     判断部门ID是否为空，为空赋值为"0",从 TDSYS 的 ACCOUNT_SET里面找到当前的最大的库名字 TD**，然后增加1，取得下一个帐套的编码建立新库
   *     再从file里面读取各个sql语句。包括 建立表，建立存储过程，建立函数,</li>
   * <li>创建成功。</li>
   * 
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>创建账套用参数，在获取时候，注意判断上是否为空，还有再也没有输入值得情况下，使用默认值</li>
   * </ol>
   * @author ych
   * @param request  AcsetBuildParam对象
   * @param response 成功信息或其他弹出信息
   * @return
   * @throws Exception 创建数据库需要一定时间 一旦中途停止会引发异常
   * **/
  public String doCreateDatabase(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 克隆帐套
   * <ol>
   * <li>顾名思义，就是复制原有的帐套</li>
   * <li>获取当前帐套个数，获取最大可创建帐套个数，判断是否达到最大个数，如果已达到提示信息，未达到，继续创建</li>
   * <li>获取要复制的帐套编码</li>
   * <li>从提交表单中创建账套用参数对象，具体内容，定义帐套新名称、会计期间规则、当前的时间、会计年度和开始时间等参数，
   *     由前台穿过来的数据对应赋值，if(会计期间规则.equals(自然年月)||会计期间规则不为空)开始时间为获取的年度的一月份开始，否则可以手动调试月份，
   *     获取当前创建帐套的人部门ID，外币核算，在获取汇率长度、汇率精度、金额长度、金额精度，如果没有填写则取默认值，否则等于填写值，获取本位币，及其本位币标志
   *     如果没有书写，默认值为人民币，获取首期会计期间，创建AcsetBuildParam对象，把帐套新名称、会计期间规则、当前的时间、会计年度、开始时间、
   *     和首期会计期间放入里面，返回AcsetBuildParam对象</li>
   * <li>开始克隆（参数：帐套编码，AcsetBuildParam对象，）</li>
   * <li>//首先从 TDSYS 的 ACCOUNT_SET里面找到当前的最大的库名字 TD**，然后增加1，建立新库，获取下一个帐套编码</li>
   * <li>再从file里面读取各个sql语句。包括 建立表，建立存储过程，建立函数</li>
   * <li>清除数据</li>
   * <li>克隆成功</li>
   * </ol>
   * <ol>
   * <b>注意事项：由于是克隆帐套，有些参数可以直接用以前的，例如汇率长度、汇率精度、金额长度、金额精度</b>
   * </ol>
   * @author ych
   * @param request  帐套编码，AcsetBuildParam对象
   * @param response 成功信息或其他弹出信息
   * @return
   * @throws Exception 
   * **/
  public String cloneAcset(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 帐套删除
   * <ol>
   * <li>由于是一些帐套不适用于当前情况而需要被删除</li>
   * <li>获取要删除帐套的编码，然后根据帐套编码取得帐套数据库编码。</li>
   * <li>根据得到的帐套编码删除ACCOUNT_SET表中与其相关的所有数据。</li>
   * <li>再根据得到的帐套数据库编码删除在数据库中删除对应的库。</li>
   * <li>然后根据帐套编码删除User_ACSET用户帐套权限表对应的数据</li>
   * <li>获取数据库连接，根据帐套编码取得帐套数据库名称</li>
   * <li>根据帐套编码关闭帐套关联的数据库以及删除帐套定义，再根据帐套数据库名称强制删除数据库，解决在数据库连接没有清除时删除失败的问题</li>
   * <li>关闭数据库资源</li>
   * <li>返回信息，帐套删除成功。</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>强制删除数据库的时候，一定确保完全删除</li>
   * </ol>
   * @author ych
   * @param request  帐套编码
   * @param response  返回成功信息
   * @return
   * @throws Exception 目前还不清楚
   * **/
  public String acsetDelete(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
