package t6.fis.funcs.voucher.act;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t6.fis.funcs.voucher.data.T9VouchDataSource;
import t6.fis.funcs.voucher.data.T9Voucher;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.logic.T9AddressLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;


/**
 * 
 * @author ych
 *
 */
public class T9VoucherAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.voucher.logic.T9VoucherLogic");
  /**
   * 添加凭证
   * <ol>
   * <li>本功能主要插入数据的表分别是VOUCHER 和  VOUCDETL ,两张表的关系是one-to-many 即一个VOUCHER 对应多个    VOUCDETL</li>
   * <li>首先获取表单提交数据,从表单提交的数据创建凭证对象</li>
   * <li>设置凭证对象的当前登录人员所在部门SEQ_ID及该人员SEQ_ID</li>
   * <li> 再插入的时候应注意<b><i>取得凭证编码：</i></b>自动生成凭证编码，取得最大编码 + 1,手工生成凭证编码的时候 应判断是否重复</li>
   * <li><b><I>关于辅助核算的问题：</I>保存到数据库是对应核算编号(具体核算编码可以到SUBJECT表和AACCOUNTITEM) </b></li>
   * <li>把数据先插入主表(VOUCHER)当中</li>
   * <li>借贷方向和其含义</li>
   * <li>做币种的时候，注意汇率的问题，在后台设置汇率是动态的，可以更改的</li>
   * <li>插入附表(VOUCDETL)分录新的的流水号,首先要取得分录数，在逐一进行插入</li>
   * <li>插入保存到数据库之前，要判断摘要、币种、科目是否为空</li>
   * </ol>
   * <b>注意事项</b>
   * <ol>
   * <li>在保存时0=不存在凭证；>0=存在凭证，然后取得最大的凭证分录流水号，进行保存，然后去到最大凭证号的日期</li>
   * <li>摘要：可以手动录入和双击选择</li>
   * <li>凭证号：系统自动取号，可以在“账务参数”中设置凭证取号方式。建议采用正常情况下采用自动取号，出现断码的情况下可调整为手工编号，待断号凭证补齐后，再将参数调整为自动取号。</li>
   * <li>凭证日期：输入当前会计期间内的日期。目前系统可以自动获取</li>
   * <li><b>付单据数：要求附单据数大于0。</B></li>
   * <li>摘要：对分录的简要说明。可以收录录入，也可以双击 选择“账务参数”中“凭证摘要是否携带参数控制前一条分录的摘要是否自动复制到下一条分录。</li>
   * <li>凭证字：是从VOUCWORD中查询出来，记录到voucher表中时候使用是 对应的ID</li>
   * <li>然后返回成功和失败信息</li>
   * </ol>　
   * @author ych
   * @param request voucher对象  
   * @param response 保存成功或者失败的状态及消息
   * @return 
   * @throws Exception 凭证日期所在的会计期间已经关闭或者账务环境还没有创建
   * **/
  public String addVoucher(HttpServletRequest request,
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
   * 修改凭证
   * <ol>
   * <li>首先先判断是否是已审核,已审核不做任何修改，返回提示</li>
   * <li>判断是否有权限，如果没有，返回提示</li>
   * <li>校验凭证的有效性</li>
   * <li>更新红冲流水号</li>
   * <li>其他同上</li>     
   * </ol>
   * @author ych
   * @param request voucher数据对象
   * @param response 成功或失败的信息
   * @return
   * @throws Exception 凭证日期所在的会计期间已经关闭或者账务环境还没有创建
   * **/
  public String  updataVoucher(HttpServletRequest request,
      HttpServletResponse response) throws Exception { 
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *删除凭证
   *<ol>
   *<li>首先先判断是否是已审核,已审核不做任何修改，返回提示</li>
   *<li>判断是否有权限，如果没有，返回提示</li>
   *<li>获取VOUCHERID,然后删除VOUCDETL表中VOUC_SEQ_ID为voucherId的记录，最后删除VOUCHER表中记录</li>
   *</ol>
   * @author ych
   * @param request seqId
   * @param response 成功或者失败的信息
   * @return
   * @throws Exception
   * **/
  public String deleteVoucher(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
   /**
    * 查找凭证
    * <ol>
    * <li>设计一些主要的查询字段，可以进行模糊查询和精确查询</li></ol>
    *<ol>
    *<li>此功能是查询一些制作好的凭证和一些凭证明细，以便于业务的管理。</li>
    *<li>可以根据凭证号进行查询 </li>
    *<li>根据凭证状态进行查询</li>
    *<li>根据凭证摘要进行查询</li>
    *<li>根据凭证来源进行查询</li>
    *<li>根据凭证的日期进行一段时间的内的凭证查询</li>
    *<li>根据科目及借贷方向的金额进行查询</li>
    *<li>也可以进行联合查询找到精确数据</li>
    *</ol>
    * @author ych
    * @param request 查询需要的关键字
    * @param response 列表模式显示
    * @return
    * @throws Exception
    * **/ 
  public String findVoucher(HttpServletRequest request,
       HttpServletResponse response) throws Exception {
     try {
       //TODO
       //
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
     } catch(Exception ex) {
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
       throw ex;
     }
     return "/core/inc/rtjson.jsp";
   }
   /**
    * 凭证的审核
    *<ol>
    *<li>首先要查看一些凭证，然后接受审核</li>
    *<li>判断制单人和审核人是否是同一个人，是，则返回不能审核信息，否，可以进行审核。</li>
    *<li>不签字的凭证任然可以审核。</li>
    *</ol>
    *<ol><li><b>注意：审核之后的凭证是不能修改的，如果出现错误，要按步骤回退</b></li>
    *<li><b><i>根据的会计制度要求，当前用户不能复核自己制单的凭证</i></b></li>
    *<li>批量复核：“逐张复核”在不选中的情况下，输入凭证号范围，系统将批量复核指定号码的凭证。如果输入的凭证号无效，或者其中某些凭证已经复核，该操作不会对此类凭证做任何处理。</li>
    *<li>逐张复核：复核前如果需要核对每张凭证可以采用该方式。</li>
    *</ol> 
    * @author ych
    * @param request acctYear, period, firstVouchNo, lastVouchNo, userName
    * @param response 审核是否成功的信息
    * @return
    * @throws Exception
   * **/
  public String checkVouch(HttpServletRequest request,
       HttpServletResponse response) throws Exception {
     try {
       //TODO
       //
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "审核成功");
     } catch(Exception ex) {
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
       throw ex;
     }
     return "/core/inc/rtjson.jsp";
   }
  /**
   * 退复核
   * <ol>
   * <li>取得acctYear、period、firstVouchNo、lastVouchNo</li>
   * <li>判断是否已经对登记，如果没有，必须先退记账、然后在退复核</li>
   * <li>如果已经退记账，还要没有被退复核的，在查询时候一定要注意</li>
   * <li>执行退复核操作，也就是更新数据库VOUCHER表中的字段</li>
   * </ol>
   * @param request acctYear, period, firstVouchNo, lastVouchNo, userName
   * @param response  是否成功的信息
   * @return
   * @throws Exception
   * **/
  public String rtCheckVouch(HttpServletRequest request,
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
   * 获取初始化数据
   * @author ych
   * @param request id
   * @param response Form
   * @return
   * @throws Exception
  **/
  public String getIntiData(HttpServletRequest request,
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
   * 凭证记账
   * <ol>
   * <li>判断是否有记账的权限，还要判断记账人，和凭证的录入人是否是同一个，如果不是可以记账，否则反之，弹出信息提醒。</li>
   * <li>获取表单参数，获取firstVouchNo和lastVouchNo</li>
   * <li>执行凭证记账的方法，取出凭证的参数。(由于财务的分期是非常严格的，所以一定要有会计期间、会计年度的概念)</li>
   * <li>要查询凭证，即要登帐的凭证，通过取得的凭证参数进行查询，查询结果应该是已经被审核，但还没有被登帐的凭证。</li>
   * <li>根据查询凭证表得到的seqId(应该得到的是seqId的集合)</li>
   * <li>根据得到的seqId,到凭证明细表中进行查询。</li>
   * <li>对各个凭证，逐条取出分录，进行记帐（修改余额表）</li>
   * <li>把取到的数据(会计年度、会计期间、科目编码，币种、辅助核算项目)拼接成一个字符串</li>
   * <li>本币和外币的借贷期间要搞清楚，一般设为四个字段</li>
   * <li>科目余额汇总存储过程操作类型，0-期初余额，1-记帐</li>
   * <li>要写好一个存储方式，进行存储这些数据</li>
   * <li>更新凭证记账人</li>
   * </ol>
   * @author ych
   * @param request 取得表单参数   acctYear, period, firstVouchNo, lastVouchNo, userName
   * @param response  seqId periodId acctYear
   * @return
   * @throws Exception
   * **/
  public String acctVouch(HttpServletRequest request,
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
   * 退记账
   * <ol>
   * <li>判断上是否已经记账，已经记账，则执行下面操作</li>
   * <li>获取表单参数，获取firstVouchNo和lastVouchNo</li>
   * <li>执行凭证记账的方法，取出凭证的参数。(由于财务的分期是非常严格的，所以一定要有会计期间、会计年度的概念)</li>
   * <li>要查询凭证，即要登帐的凭证，通过取得的凭证参数进行查询，查询结果应该是已经被审核，但还没有被登帐的凭证。</li>
   * <li>根据查询凭证表得到的seqId(应该得到的是seqId的集合)</li>
   * <li>根据得到的seqId,到凭证明细表中进行查询。</li>
   * <li>对各个凭证，逐条取出分录，进行记帐（修改余额表）</li>
   * <li>把取到的数据(会计年度、会计期间、科目编码，币种、辅助核算项目)拼接成一个字符串</li>
   * <li>本币和外币的借贷期间要搞清楚，一般设为四个字段</li>
   * <li>科目余额汇总存储过程操作类型，0-期初余额，1-记帐</li>
   * <li>要写好一个存储方式，进行存储这些数据</li>
   * <li>更新凭证记账人</li>
   * </ol>
   * @param request acctYear, period, firstVouchNo, lastVouchNo, userName
   * @param response  seqId periodId acctYear  符合条件的凭证
   * @return
   * @throws Exception**/
  public String rtAcctVouch (HttpServletRequest request,
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
