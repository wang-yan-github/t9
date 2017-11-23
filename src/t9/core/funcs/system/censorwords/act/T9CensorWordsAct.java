package t9.core.funcs.system.censorwords.act;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.censorwords.data.T9CensorWords;
import t9.core.funcs.system.censorwords.logic.T9CensorWordsLogic;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.system.extuser.logic.T9ExtUserLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9CensorWordsAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.extuser.T9ExtUserAct");
  public String addSingleWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      response.setContentType("text/html;charset=UTF-8");
      request.setCharacterEncoding("UTF-8");
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String find = request.getParameter("find"); 
      String replacement = request.getParameter("replacement");
      if(replacement.equals("") || replacement.equals("null")){
        replacement = "**";
      }
      Map m =new HashMap();
      m.put("userId", seqId);
      m.put("find", find);
      m.put("replacement", replacement);
      T9ORM t = new T9ORM();
      T9CensorWordsLogic cwLogic = new T9CensorWordsLogic();
      if(cwLogic.existsCensorWords(dbConn, find)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "错误  词语"+find+"已存在，请重新填写！");
        return "/core/inc/rtjson.jsp";
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      t.saveSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSingleWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
    
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("find", find);
      m.put("replacement", replacement);
      orgLogic.updateSingleWords(dbConn, find, replacement);
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateMore1Words(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic cwLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("find", find);
      m.put("replacement", replacement);
      m.put("userId", userId);
      if(cwLogic.existsCensorWords(dbConn, find)){
        cwLogic.updateSingleWords(dbConn, find, replacement);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        //request.setAttribute(T9ActionKeys.RET_MSRG, "错误  词语"+find+"以存在，请重新填写！");
        //return "/core/inc/rtjson.jsp";
      }else{
        t.saveSingle(dbConn, "censorWords", m);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateMore99Words(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
     
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic cwLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
      List<Map> list = new ArrayList();
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("find", find);
      m.put("replacement", replacement);
      
      String censorVal = request.getParameter("censorVal");
      //System.out.println(censorVal+"XXXXXXXXXXXXXXXXXXXXx");
      //String censorSum[] = censorVal.split(",");
     // String findStr = "";
     // for(int x = 0; x < censorSum.length; x++){
      //  findStr = censorSum[x];
     // }
      if(cwLogic.existsCensorWords(dbConn, find)){
        //request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        //return "/core/funcs/system/censorwords/new/import.jsp?find="+find+"&replacement="+replacement;
        //System.out.println(find+"PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
        
        T9ORM orm = new T9ORM();
        HashMap map = null;
        StringBuffer sb = new StringBuffer("[");
        String[] filters = new String[]{"FIND="+find};
        List funcList = new ArrayList();
        funcList.add("censorWords");
        map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
        list.addAll((List<Map>) map.get("CENSOR_WORDS"));
        if(list.size() > 1){
          for(Map ms : list){
            sb.append("{");
            sb.append("find:\"" + ms.get("find") + "\"");
            sb.append(",replacement:\"" + ms.get("replacement") + "\"");
            sb.append("},");
          }
          sb.deleteCharAt(sb.length() - 1); 
        }else{
          for(Map ms : list){
            sb.append("{");
            sb.append("find:\"" + ms.get("find") + "\"");
            sb.append(",replacement:\"" + ms.get("replacement") + "\"");
            sb.append("}");
          }
        }    
        sb.append("]");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        //return "/core/inc/rtjson.jsp";
      }else{
        t.saveSingle(dbConn, "censorWords", m);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/system/censorwords/new/import.jsp";
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateMore0Words(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic cwLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
      List<Map> list = new ArrayList();
      String findStrs = "";
      String replacementStrs = "";
      String sumStr = "";
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      StringBuffer sb = new StringBuffer("[");
      String censorVal = request.getParameter("find");
      //System.out.println(censorVal+"XXXXXXXXXXXXXXXXXXXXx");
      T9ORM orm = new T9ORM();
      HashMap map = null;
      String censorSum[] = censorVal.split(",");
      String findStr = "";
      String finds = "";
      int okCount = 0;
      int errCount = 0;
      String replacements = "";
      for(int x = 0; x < censorSum.length; x++){
        findStr = censorSum[x];
        if(censorSum[x].indexOf("=")!=-1){
          finds = censorSum[x].substring(0,censorSum[x].indexOf("="));
          replacements = censorSum[x].substring(censorSum[x].indexOf("=")+1, censorSum[x].length());
        }else{
          finds = findStr;
          replacements = "";
        }
        if(cwLogic.existsCensorWords(dbConn, finds)){
          errCount++;
          findStrs += finds + ",";
          replacementStrs += replacements + ",";
          if(replacements!=""){
            sumStr += finds+"="+replacements+",";
          }else{
            sumStr += finds;
          }
          continue;
        }else{
          okCount++;
          m.put("find", finds);
          m.put("replacement", replacements);
          m.put("userId", userId);
          t.saveSingle(dbConn, "censorWords", m);
        }
      }
      //System.out.println(sumStr+"YYYYYYYYYYYYY");
      String str[] = sumStr.split(",");
      String reStr = "";
      for(int i = 0; i < str.length; i++){
        reStr = str[i];
        sb.append("{");
        sb.append("find:\"" + reStr + "\"");
        sb.append(",errCount:\"" + errCount + "\"");
        sb.append(",okCount:\"" + okCount + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/system/censorwords/new/import.jsp";
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateMore2Words(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
    
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("find", find);
      m.put("userId", userId);
      m.put("replacement", replacement);
      //orgLogic.deleteAllWords(dbConn);
      t.saveSingle(dbConn, "censorWords", m);
      //orgLogic.updateSingleWords(dbConn, find, replacement);
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteMore2Words(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      orgLogic.deleteAllWords(dbConn);
      //orgLogic.updateSingleWords(dbConn, find, replacement);
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"词语批量添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getCensorWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      T9CensorWordsLogic extLogic = new T9CensorWordsLogic();
      ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
      wordList = extLogic.getCensorWords(dbConn);
      request.setAttribute("wordList", wordList);
      request.setAttribute("userName", userName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/censorwords/manage/index.jsp";
  }
  
  public String getCensorWordsSearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String find = request.getParameter("find");
      if(!T9Utility.isNullorEmpty(find)){
        find = T9DBUtility.escapeLike(find);
      }
      String replacement = request.getParameter("replacement");
      if(!T9Utility.isNullorEmpty(replacement)){
        replacement = T9DBUtility.escapeLike(replacement);
      }
      
      userName = person.getUserName();
      T9CensorWordsLogic extLogic = new T9CensorWordsLogic();
      ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
      wordList = extLogic.getCensorWordsSearch(dbConn, seqId, find, replacement);
      request.setAttribute("wordList", wordList);
      request.setAttribute("userName", userName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/censorwords/query/search.jsp";
  }
  
  public String getCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ExtUserLogic extLogic = new T9ExtUserLogic();
      long sum = 0;
      sum = extLogic.existsCount(dbConn, 0);
      StringBuffer sb = new StringBuffer("[");
      sb.append("{");
      sb.append("sum:\"" + sum + "\"");
      sb.append("}");
      sb.append("]");
      request.setAttribute("extListSum", sum);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
    //return "/core/funcs/system/extuser/manage.jsp";
  }
  
  public String getCensorWordsId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("censorWords");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("CENSOR_WORDS");
      for(Map m : list) {
        String find = (String) m.get("find");
        if(!T9Utility.isNullorEmpty(find)){
          find = find.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String replacement = (String) m.get("replacement");
        if(!T9Utility.isNullorEmpty(replacement)){
          replacement = replacement.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",userId:\"" + m.get("userId") + "\"");
        sb.append(",find:\"" + find + "\"");
        sb.append(",replacement:\"" + replacement + "\"");
        sb.append("}");
      }       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateCensorWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
    
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("userId", userId);
      m.put("find", find);
      m.put("replacement", replacement);
      t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteCensorWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("idStrs");
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      //t.deleteSingle(dbConn, "extUser", m);
      T9CensorWordsLogic wordsLogic = new T9CensorWordsLogic();
      wordsLogic.deleteAll(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSearchWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int c = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
      
      T9CensorWordsLogic extLogic = new T9CensorWordsLogic();
      ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
      wordList = extLogic.getCensorWords(dbConn);
      int a = wordList.size();
      T9CensorWordsLogic wordsLogic = new T9CensorWordsLogic();
      wordsLogic.deleteSearch(dbConn, userId, find, replacement);
      
      T9CensorWordsLogic extLogicEnd = new T9CensorWordsLogic();
      ArrayList<T9CensorWords> wordListEnd = new ArrayList<T9CensorWords>();
      wordListEnd = extLogicEnd.getCensorWords(dbConn);
      int b = wordListEnd.size();
      c = a - b;
      //request.setAttribute("wordList", wordList);
      //request.setAttribute("wordListEnd", wordListEnd);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/censorwords/query/deletequery.jsp?c="+c;
  }
  
  public String deleteAllCensorWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic wordsLogic = new T9CensorWordsLogic();
      if(userId == 196){//管理员seqId
        wordsLogic.deleteAllWords(dbConn);
      }else{
        wordsLogic.deleteAllFast(dbConn, userId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取用户名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String data = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userId");
      int userId = Integer.parseInt(userIdStr);
      T9PersonLogic dl = new T9PersonLogic();
      data = dl.getUserNameLogic(dbConn, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String exportToTxt(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    PrintWriter pw = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
      Calendar cal = Calendar.getInstance();        
      java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");        
      String logTime = sdf.format(cal.getTime()); 
      
      String fileName = URLEncoder.encode("词语过滤"+logTime+".txt","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      pw = response.getWriter();
      String txtStr = "";
      T9CensorWordsLogic cwl = new T9CensorWordsLogic();
      ArrayList<T9CensorWords> list = cwl.getCensorWordsTxtList(conn, userId, find, replacement);
      if(list.size() == 0){
        txtStr = "";
      }else{
        for(int i = 0; i < list.size(); i++){
          String findStr = list.get(i).getFind();
          String replaceStr = list.get(i).getReplacement();
          txtStr += findStr + "=" + replaceStr + "\r\n";
        }
      }
      pw.write(txtStr);
      pw.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      pw.close();
    }
    return null;
  }
  
}
