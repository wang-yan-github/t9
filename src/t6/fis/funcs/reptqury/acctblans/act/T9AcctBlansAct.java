package t6.fis.funcs.reptqury.acctblans.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
/**
 * 账务查询
 * @author ych
 * **/

public class T9AcctBlansAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.reptqury.acctblans.logic.T9AcctBlansLogic");
  /**
   * 明细分类帐查询条件输入页面打开活动
   * <ol>
   * <li>本功能是为了在加载已经记账的期间。</li>
   * <li>从ACCTBLNS科目余额表中，查出最大会计年度和最大年度之内的最大会计期间以及最小会计年度和最小年度之内的最小会计期间</li>
   * <li>然后在查ACCOUNTPERIOD帐务核算会计期表，查询条件是在刚刚查到的最大值与最小值之间的数据。</li>
   * <li>放在accountPeriod对象里，由于数据是多个，在把accountPeriod对象放在List里整体返回</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   *<li>在查询的时候，要先查出最大、最小会计年度，在根据他们查出最大、最小会计期间。</li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 装有accountPeriod对象的List
   * @return
   * @throws Exception 不清楚
   * **/
  public String acctBlnsIndexOpen(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  /**
   * 科目余额查询条件输入页面打开 
   * <ol>
   * <li>本功能是为了在加载已经记账的期间。</li>
   * <li>从ACCTBLNS科目余额表中，查出最大会计年度和最大年度之内的最大会计期间以及最小会计年度和最小年度之内的最小会计期间</li>
   * <li>然后在查ACCOUNTPERIOD帐务核算会计期表，查询条件是在刚刚查到的最大值与最小值之间的数据。</li>
   * <li>放在accountPeriod对象里，由于数据是多个，在把accountPeriod对象放在List里整体返回</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   *<li>在查询的时候，要先查出最大、最小会计年度，再根据他们查出最大、最小会计期间。</li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 装有accountPeriod对象的List
   * @return
   * @throws Exception 不清楚
   * **/
  
  public String acctBlnsSelectOpen(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  /**
   * 科目余额查询执行
   * <ol>
   * <li></li>
   * <li>获取系统参数，创建一个session ，获取辅助核算项目类型集合</li>
   * <li>定义一些参数，参数主要包括帐务年度、帐务月份、报表编码、科目编码列表、截至科目编码列表、选择所有科目标记、起始查询显示级次、截至查询显示级次、包含未登帐凭证和币种流水号
   *     参数值是前台传过来的</li>
   * <li>创建一个String类型的数组，然后根据得到的账务年月，把帐务年月转化为会计年度和会计期间</li>
   * <li>以上的的全部数据放入hashTable里面</li>
   * <li>根据得到的报表编码和会计年度查到对应得模板，并且要得到模板的路径</li>
   * <li>然后把模板模板的路径和hashtable当做参数传入搜索引擎中</li>
   * <li>最后得到相应的数据</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>搜索引擎是重点，再了解时应多学习</li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 
   * @return
   * @throws Exception 不清楚
   * **/
  public  String acctBlnsSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  /**
   * 科目明细帐查询执行
   * <ol>
   * <li></li>
   * <li>获取系统参数，创建一个session ，获取辅助核算项目类型集合</li>
   * <li>定义一些参数，参数主要包括帐务年度、帐务月份、报表编码、科目编码列表、截至科目编码列表、选择所有科目标记、起始查询显示级次、截至查询显示级次、包含未登帐凭证和币种流水号
   *     参数值是前台传过来的</li>
   * <li>创建一个String类型的数组，然后根据得到的账务年月，把帐务年月转化为会计年度和会计期间</li>
   * <li>以上的的全部数据放入hashTable里面</li>
   * <li>根据得到的报表编码和会计年度查到对应得模板，并且要得到模板的路径</li>
   * <li>然后把模板模板的路径和hashtable当做参数传入入搜索引擎中</li>
   * <li>最后得到相应的数据</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>搜索引擎是重点，再了解时应多学习</li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 
   * @return
   * @throws Exception 不清楚
   * **/
  public  String acctDetlSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  /**
   * 日记账
   * <ol>
   * <li>日记账就是一些科目精确到天的资金流动情况，这里的日记帐包括现金日记账、银行日记账和不同日记账等各种日记账。</li>
   * <li>把账务年月转化为会计年度和会计日期</li>
   * <li>加载会计期间，在根据对应得模板和模板解析程序进行数据加载</li>
   * <li>在进行日记账查询的时候，要ACCTBLNS和VOUCDETl表中的数据</li>
   * <li>在进行日记账的时候，查询出来的数据，会有期初余额，这个期初的余额是要用本期初余额减去和期初差的天数所用的费用</li>
   * <li>要查出的数据 包括 ，数据的日期、对应的科目编号、摘要、期初数、借方发生数、贷方发生数和期末数。</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>在加载数据的时候，期初数要通过计算得到，而不是通过查询出来的</li>
   * </ol>
   * @author ych
   * @param request  年度  期间
   * @param response 数据列表
   * @return
   * @throws Exception  不清楚
   * **/
public String dayAccount(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    try {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
	    } catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }  
/**
 * 总分类账
 * <ol>
 * <li>总帐是一级科目在一段时间内，资金的流动情况</li>
 * <li>把账务年月转化为会计年度和会计日期</li>
 * <li>加载会计期间，在根据得到模板和模板解析程序进行数据加载</li>
 * <li>查询出来的数据有一级科目的编码、科目名称、会计期间、摘要（本期期初余额、期间合计、年度累计）、借方 、贷方、 方向（方向包括，借 、贷和平）、 余额 </li>
 * <li在查询的时候可以按照科目的几次进行查询，类似于明细帐></li>
 * </ol>
 * <b>注意事项：</b>
 * <ol>
 * <li>在进行多级科目查询的时候，有个先后顺序。</li>
 * </ol>
 * @author ych
 * @param request  年度  期间
 * @param response 数据列表
 * @return
 * @throws Exception  不清楚
 * **/
public String topSubSelect(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    try {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
	    } catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }  

}
