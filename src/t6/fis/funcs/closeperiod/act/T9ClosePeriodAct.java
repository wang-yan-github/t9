package t6.fis.funcs.closeperiod.act;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
/**
 * @author ych
 * 一下每对<li></li>都代表一个操作步骤
 * **/
public class T9ClosePeriodAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.closeperiod.logic.T9ClosePeriodLogic");
  /**
   * 期末结转打开界面
   * <ol>
   * <li>此功能是在执行期末结转的时候，进行加载数据，并且提示信息，以便于使用者注意。</li>
   * <li>获取系统参数，会计年度acctYear会计期间period。</li>
   * <li>根据得到的会计期间和会计年度，没有被关闭并且不存在未登帐凭证则正常跳转，否则进行错误提示。</li>
   * </ol>
   * <ol>
   * <b>注意事项:</b>
   * <li>此功能只是使用期末结转时候的辅助性的功能。</li>
   * <li>在判断会计期间是否已经关闭的时候，如果已经关闭，还要判断此期间是否存在未生成凭证的业务数据。</li>
   * </ol>
   * @author ych
   * @param request 系统参数  ，会计年度acctYear会计期间period
   * @param response 返回成功或者失败的额信息
   * @return
   * @throws Exception 
   * **/
  public String  trnsNextInputOpen(HttpServletRequest request,
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
   * 期末结转执行
   * <ol>
   * <li>本功能是对会计中一期账务进行结转，把当前期账务关闭，结账到下一期。</li>
   * <li>获取系统参数，会计年度acctYear、会计期间period、会计年月acctYM、一级科目长度TopSubLength和用户信息Bean，描述用户的基本信息userInfo。</li>
   * <li>查看当前年度和当前期间是否可以结转。</li>
   * <li>主要要查找出来的数据有凭证序号记账字号、现收字号、银收字号、现付字号、银付字号和转账字号他们总张数，最大号，已经是否存在断码，若存在断码手动调整即可。</li>
   * <li>查找有多少张凭证没有签字、复核和登帐的，存在未登帐的凭证不允许结转。</li>
   * <li>还有关于期末余额这里算出借贷是否平衡，资产余额总计=负债+所有者权益余额总计 这个等式是否成立。</li>
   * <li>以上条件都满足的话，进行结转，在结转的时候主要操作的数据表是ACCTBLNS。</li>
   * <li>然后根据帐套参数的设置当前系统的时间属性,也就是ACCOUNT_SET表中的时间调到下一期的时间。</li>
   * <li>操作完成</li>
   * </ol>
   * <ol>
   * <b>注意事项:</b>
   * <li>在做结转的时候注意，了解一下关于期末和期初的关系，上一期的期末是下一期的期初。</li>
   * <li>换算公式，期末数=年度期初数+年度借累计-年度带累计;本期期末数=本期期初数+本期借-本期贷。</li>
   * <li>在做累计数的时候，如果是新的科目的时候，增加一条新的记录即可，反之叠加到对应项即可。</li>
   * </ol>
   * @author ych
   * @param request 会计年度acctYear、会计期间period、会计年月acctYM、一级科目长度TopSubLength和用户信息Bean，描述用户的基本信息userInfo
   * @param response 显示结转后的结果
   * @return
   * @throws Exception 
   * **/
  public String trnsNextInputSave(HttpServletRequest request,
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
   * 通用转帐页面打开
   * <ol>
   * <li>此功能是在进行正式结转时，提前对一些数据进行判断，提示用户。</li>
   * <li>加载系统参数，会计年度acctYear和会计期间period。</li>
   * <li></li>
   * <li>判断要转账的期间账务是否已经关闭，若没有关闭则继续执行，否则显示提示信息。</li>
   * <li>判断是否有未记账的凭证，若没有则正常继续执行，否侧显示提示信息。</li>
   * <li>判断是否已经存在结转损益凭证，若没有正常继续执行，否则显示提示信息。</li>
   * <li>加载转帐主表定义，也就是操作 转帐功能描述表TRNSVDESC，根据会计年度以及模板编码进行查询，查询到的数据放到转账主表实例化对象中，
   *     在对象放到ArrayList中返回。</li>
   * <li>把得到的数据传给也正式执行界面。</li>
   * <li>跳转到 执行转帐逻辑界面。</li>
   * </ol>
   * <ol>
   * <b>注意事项:</b>
   * <li>在执行主表定义的时候，设置查询条件的时候，只有当模板编号不为空的时候查询条件才是会计年度模板编号，否则就一个会计年度作为查询条件。</li>
   * </ol>
   * @author ych
   * @param request resources acctYear trnsvNo
   * @param response TRNSVDESC(主表中的数据)
   * @return
   * @throws Exception
   * **/
  public String doInputOpen(HttpServletRequest request,
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
   * 执行转帐逻辑
   * <ol>
   * <li>此操作是结转损益类科目，目的是查看公司在一定期间内的损益情况。</li>
   * <li>获取通用转账模板编号trnsvNo、转入科目tnsToSubNo和直接登帐审核checkAcct。</li>
   * <li>获取系统参数sysParams、辅助核算项目类型集合itemTypeSet、科目数据对象subList、会计年度acctYear、会计期间periodId和系统用户信息userInfo。</li>
   * <li>判断转入科目tnsToSubNo是否为空&&是否和损益类传入相同,此条件为ture则执行加载小编码项目；加载小编码项目操作ACPARAM表，通过会计年度以及参数名称，
   *     修改ACPARAM表中参数值字段，使其变成转入科目tnsToSubNo，最后转入科目tnsToSubNo赋值给损益类转入科目。</li>
   * <li>实例化一个转账主表对象trnsAcctMain和ArrayList对象，判断转账模板编号trnsvNo是否为空，若不为空则加载转账主表(传入会计年度acctYear, 
   *     通用转账模板编号trnsvNo参数)返回一个转账主表定义对象，然后加载转账明细表(传入刚刚得到的转账主表的seqId以及凭证摘要VouchSummary)返回一个了List集合，
   *     若为空则要手动给转账主表添加信息，再加载转账明细表，也返回一个list集合</li>
   * <li>得到会话Id ，清空临时表中的数据，主要清空的表有记帐凭证中间表VOUCHER_IMPORT、记帐凭证中间表VOUCDETL_IMPORT和凭证明细中间表 VOUCDETL_TRNSV
   *     遍历加载转账明细表中返回的list，在这个for循环中执行新增该转帐明细对应的分录，包括转出科目对应的分录和转入科目对应的分录，取得在凭证最大日期，然后把临时表中的凭证保存到凭证表中，返回一个boolean的值</li>
   * <li>判断刚刚返回的boolean是否为ture,执行获取最大凭证号的方法(参数时会计年度acctYear,会计期间periodId和凭证状态)，拿到最大凭证号，可以直接复核以及记账，也可以手动进行，
   *     自动复核记账，弹出（转账凭证自动创建成功，同时已经完成复核、记账，凭证号是" + lastVouchNo),反之弹出(转账凭证自动创建成功，凭证号是" + lastVouchNo + "，还没有复核、记账)"
   *     如果刚刚返回的值为false,提示"不存在需要结转的数据"</li>
   * <li>输入保存成功</li>
   * </ol>
   * <ol>
   * <b>注意事项:</b>
   * <li>通用转账用于科目账户的余额的结转，比如结转损益可以通过该功能实现。</li>
   * <li>此番操作是很复杂的，在做此项功能的时候，首先要了解一下常用的术语，以及要对整个功能是做什么有一定的了解</li>
   * <li>注意转入类科目和损益类转入科目的关系</li>
   * </ol>
   * @author ych
   * @param request  trnsvNo  tnsToSubNo checkAcct
   * @param response 提示成功或者失败的信息
   * @return
   * @throws Exception
   * **/
  public String  doTrnsacctExecute(HttpServletRequest request,
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
