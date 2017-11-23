package t6.fis.funcs.acsetparam.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9AcParamAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.acsetparam.logic.T9AcParamLogic");
  /**
   * 账务参数管理
   * <ol>
   * <li>此功能是管理系统用到的一些参数,</li>
   * <li>从前台页面传来value，暂且叫action_Flag</li>
   * <li>if(action_flag == null || action_flag.equalsIgnoreCase(""))</li>
   * <li>如果条件满足的话，给action_flag赋值一个String 类型的  "1"</li>
   * <li>由于账务管理是前台时分三个界面的，每个界面对应不同的数据，刚打开对应value="1"的界面</li>
   * <li>然后编写小编码醒目，也就是查询ACPARAM表，返回一个list回来</li>
   * <li>遍历这个list,把得到的值循环放到 acParam 实体中，然后if(acsetParam.getParaname().substring(1,2).equalsIgnoreCase(action_flag))
   *     acsetParam.getParaname()中的数据类似于这样"P10002" "P20002" "P30002 根据action_flag的值显示对应的信息，</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>数据库中保存的一些参数是没有用的，按上述的方法遍历的时候，可能会把没有用的也遍历出来，新版本可以在
   *     在数据加一个标识的字段即可，0-可用，1-不可用</li>
   * </ol>
   * @author ych
   * @param request action_flag
   * @param response List
   * @return
   * @throws Exception 不清楚
   * **/
  public String AcsetParamOpen(HttpServletRequest request,
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
   * 修改保存
   * 将修改的修改的账务参数保存到数据库中
   * <ol>
   * <li>从前台获取一个参数 action_Flag,参数值可以 String类型的 "save1" "save2" "sava3" 和什么都没有</li>
   * <li>还是要先判断一下if(action_flag == null || action_flag.equalsIgnoreCase(""))</li>
   * <li>如果为ture 则先给附一个初值，如果为false，action_flag = action_flag.substring(4,5)</li>
   * <li>获取系统参数 和会计年度SystemParams sysParams  acctYear</li>
   * <li>从数据库中ACPARAM表查出paraValue字段的所有数据，放ArrayList  paraNameList 里面，然后遍历这个 list，</li>
   * <li>判断if(paraNameList.get(i).toString().substring(1,2).equalsIgnoreCase(action_flag))</li>
   * <li>符合条件的 String paraValue = request.getParameter(paraNameList.get(i).toString());</li>
   * <li>在判断是否为空,然后根据会计年度和参数名字把ACPARAM表的paraValue的值更改</li>
   * <li>然后加载帐套参数，是否关闭期间</li>
   * <li>加载帐套的时候，应注意几个判断，从ACPARAM表中查出paraValue字段的所有数据，根据当前会计年度，根据参数名字进行排序</li>
   * <li>查出来的数据放到，List里面，在加载的，先判断list.size()>40,若为false返回信息， 数据库中没有设置系统参数，为ture 继续加载</li>
   * <li>基础参数设置加载完后，在加载结转损益科目的科目是要注意先判断list.size()>45，为false systemParams.setProfitLoss2Sub((String)paramValueList.get(fieldIndex++)); //结转损益科目 P10026
   * 为ture继续加载 科目相关参数和凭证相关参数</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>上面的list.size()>X是以前在处理版本问题的解决办法，新本版可以不用</li>
   * </ol>
   * @author ych
   * @param request list
   * @param response 成功或者失败的信息
   * @return
   * @throws Exception 不清楚
   * **/
  public String AcsetParamSave (HttpServletRequest request,
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
