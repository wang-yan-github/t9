package t6.fis.funcs.reptqury.acctrept.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9AcctReptAct {
	private static Logger log = Logger.getLogger("t6.fis.funcs.reptqury.acctrept.logic.T9AcctReptLogic");
	/**
   * 财务报表查询索引页面打开
   * <ol>
   * <li>此功能就是打开查询的索引界面，加载一些数据</li>
   * <li>实例化一个ArrayList reptList,获取报表编码</li>
   * <li>判断报表编码是否为空，这时候传过来的报表模板编码应该是空的，但是为了程序的严谨性，此时要判断他是否为空</li>
   * <li>若为空，加载报表模板，报表模板返回的是一个集合，所以放在reptList</li>
   * <li>正确返回</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>暂且不明</li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 
   * @return
   * @throws Exception 
   * **/
	public String acctReptIndex(HttpServletRequest request,
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
   * 财务报表查询条件输入页面打开
   * 财务报表查询索引页面打开
   * <ol>
   * <li>此功能就是打开查询的索引界面，加载一些数据</li>
   * <li>实例化一个ArrayList reptList,获取报表编码</li>
   * <li>判断报表编码是否为空，这时候传过来的报表模板编码应该是空的，但是为了程序的严谨性，此时要判断他是否为空</li>
   * <li>若为空，加载报表模板，报表模板返回的是一个集合，所以放在reptList</li>
   * <li>返回到输入界面</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li></li>
   * </ol>
   * @author ych
   * @param request  
   * @param response 
   * @return
   * @throws Exception 
   * **/
public String acctReptSelectOpen(HttpServletRequest request,
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
   * 财务报表查询
   * <ol>
   * <li>此功能是帮助企业出有些常用的报表，如损益表、资产负债表、现金流量表等一些报表</li>
   * <li>从前台获取一个String类型的数据actionFlag（名称可以随便定义），然后加入一个条件语句，确定是数据不能为空并且actionFlag.equals("check")</li>
   * <li>条件成立执行doCheck()方法，获取模板编号，判断是否为损益表和资产负债表的编号，如果都不是，弹出信息"该表不涉及科目，不需要检查！"，
   * 然后获取系统参数、科目数据对象、编码分隔符和会计年度，按版本号获取版本信息，根据到模板得到模板的绝对路径，然后根据报表搜索引擎进行查询，
   * 接着还要判断是如果是损益表，取得损益类科目的编码的第一位profitLossStartNo，如果不是profitLossStartNo==null,取得资产负债表没有设置进去的科目列表，返回一个List,
   * 遍历这个List。遍历的时候从最后往前比遍历，去除二三级科目，最后判断是否所有损益类科目已经包含在模板当中。</li>
   * <li>条件不成立执行doSelect，这里面的doSelect是默认查询方法。即前台没有数据传过来的执行的方法</li>
   * </ol>
   * <ol>
   * <b>注意事项：</b>
   * <li>在取出科目编码的时候，要注意去重的问题</li>
   * </ol>
   * @author ych
   * @param request  模板编码
   * @param response 数据列表
   * @return
   * @throws Exception 不清楚
   * **/
	public String acctReptSelect(HttpServletRequest request,
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
