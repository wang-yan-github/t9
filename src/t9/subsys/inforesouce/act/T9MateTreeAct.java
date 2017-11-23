package t9.subsys.inforesouce.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9GlSyslogLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.data.T9MateValue;
import t9.subsys.inforesouce.data.T9SignFile;
import t9.subsys.inforesouce.db.T9MetaDbHelper;
import t9.subsys.inforesouce.logic.T9MataTreeLogic;
import t9.subsys.inforesouce.logic.T9MateElementLogic;
import t9.subsys.inforesouce.util.T9AjaxUtil;
import t9.subsys.inforesouce.util.T9StringUtil;


public class T9MateTreeAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.syslog.act.T9SysLogAct");
  private   T9MataTreeLogic tree= new T9MataTreeLogic();
  /**
   * 查询父元素，子元素 以及值域
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findParent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {   
      Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        String typemenu = request.getParameter("typemenu");
        System.out.println(typemenu == null);
        if(typemenu == null){
          typemenu = "1";
        }
        T9MateElementLogic element= new T9MateElementLogic();
        StringBuffer sb = new StringBuffer("[");
        List<T9MateType> parent = tree.findParent(dbConn, person,typemenu);
        for (T9MateType mt : parent){         
          sb.append("{\"name\":\"");
          sb.append(mt.getcNname());
          sb.append("\",\"id\":\"");
          sb.append(mt.getNumberId());
          sb.append("\",\"items\":[");
          String valueRange = mt.getRangeId();
          if(valueRange!=null){
            for (String s : (valueRange.split(","))){
           // String s  = valueRange.split(",")[0]; //判断 父元素下 直接由值域
            T9MateValue mv = tree.sonDate(dbConn, person, Integer.parseInt(s));
            sb.append("{\"attr\":{");
            sb.append("\"id\":\"");
            sb.append(mt.getNumberId()+"-"+mv.getSeqId());
            sb.append("\"},");
            sb.append("\"data\":{");
            sb.append("\"title\":\"");
            String name = mv.getValueName();
            sb.append(name);
            sb.append("\"}");
            sb.append("}");
            sb.append(",");
            }
        } // 判断父元素下有子元素
          List<T9MateType> children = tree.findSon(dbConn, person, mt.getSeqId(),typemenu);
          for (T9MateType ch : children){
            sb.append("{\"attr\":{");
            sb.append("\"id\":\"");
            sb.append(ch.getNumberId());
            sb.append("\",");
            sb.append("\"rel\":\"nodeLv1\"},");
            
            sb.append("\"data\":{");
            sb.append("\"title\":\"");
            sb.append(ch.getcNname());
            sb.append("\"");
            
            String range = ch.getRangeId();
           // System.out.println(range);
            if (range == null || "".equals(range.trim())){
              //System.out.println(range+"jjjjjj");
              sb.append(",\"attr\":{\"class\":\"input\"}}}");
            }
            else{
              sb.append("},");
              sb.append("\"children\":["); 
              for (String s : range.split(",")){//把子元素下的值域进行分割,分割后的数字对应值域表中（mate_value）的seq_id
             // {
               // String s  = range.split(",")[0];
                T9MateValue mv = tree.sonDate(dbConn, person, Integer.parseInt(s));
               // "children" : [   "items": [
                sb.append("{\"attr\":{");
                sb.append("\"id\":\"");
                sb.append(ch.getNumberId()+"-"+mv.getSeqId());
                sb.append("\"},");
                sb.append("\"data\":{");
                sb.append("\"title\":\"");
                String name = mv.getValueName();
                sb.append(name);
                sb.append("\"}");
                sb.append("}");
                sb.append(",");
              }
              if (sb.charAt(sb.length() - 1) == ','){
                sb.deleteCharAt(sb.length() - 1);
              }
              sb.append("]}");
            }
            sb.append(",");
          }
           if (sb.charAt(sb.length() - 1) == ','){
            sb.deleteCharAt(sb.length() - 1);
          }
           sb.append("]}");
          sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        System.out.println(sb.toString());
        PrintWriter pw = response.getWriter();    
        String rtData = sb.toString();
        pw.println(rtData);    
        pw.flush();
        request.setAttribute("va", parent);
        request.setAttribute("treeData", sb.toString());
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     }catch(Exception ex) {
       String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
   //  D:\project\t9\webroot\t9\subsys\inforesource\tree\tree.jsp  "/subsys/inforesource/tree/tree.jsp"
     return null;
  }
  /**点击定义后，
   * 当你选中父元素，子元素或值域后拼好串返回到页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findMymoth(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  
    long start = System.currentTimeMillis();
    try{
    Connection dbConn = null;
    StringBuffer sb = new StringBuffer("[");
    dbConn = requestDbConn.getSysDbConn();
    String typemenu = request.getParameter("typemenu");
    System.out.println(typemenu == null);
    if(typemenu == null){
      typemenu = "1";
    }
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    boolean isHave = tree.iHaveSave(dbConn, person,typemenu);
    if(isHave){ 
      List<T9MateType> types = tree.findSelMenu(typemenu,dbConn, person); //查找父元素的seq_id 和 name
     if(types!=null && types.size()>0){
      for(int i=0; i<types.size()&&types!=null; i++){
       // System.out.println("父元素："+types.get(i).getcNname()+"\n");
       // System.out.println("他的子元素：");
        sb.append("{\"name\":\"");
        sb.append(types.get(i).getcNname());
        sb.append("\",\"id\":\"");
        sb.append(types.get(i).getNumberId());
        sb.append("\",\"items\":[");
        List<T9MateValue> list  = types.get(i).getValues();//???
        if(list != null){    //值域元素不为空 判断 父元素下的值域  
        for(int j=0; j<list.size(); j++){
          sb.append("{\"attr\":{");
          sb.append("\"id\":\"");
          sb.append(types.get(i).getNumberId()+"-"+list.get(j).getSeqId());
          sb.append("\"},");
          sb.append("\"data\":{");
          sb.append("\"title\":\"");
          String name = list.get(j).getValueName();
         // System.out.println(name);
          sb.append(name);
          sb.append("\"}");
          sb.append("}");
          sb.append(",");
        }
       }
        List<T9MateType> subs = types.get(i).getSubs();       
        if(subs != null){
          for(int k=0; k<subs.size(); k++){
        //判断父元素下的子元素 "子元素："+subs.get(k).toString();
            sb.append("{\"attr\":{");
            sb.append("\"id\":\"");
            sb.append(subs.get(k).getNumberId()+"-");
            sb.append("\",");
            sb.append("\"rel\":\"nodeLv1\"},");
            sb.append("\"data\":{");
            sb.append("\"title\":\"");
            sb.append(subs.get(k).getcNname());
            //sb.append("\"}}");
         // sb.append(",");
            sb.append("\"");
            List<T9MateValue> li = subs.get(k).getValues();
           /* if(li==null || "".equals(li) || li.size()<1){
           if(subs.get(k).getValues()==null && "".equals(subs.get(k).getValues())){
             sb.append(",\"attr\":{\"class\":\"input\"}}}");
           }
            }*/
            //T9Out.println(li+"KKKKKKKKKKK");
            if(li==null || "".equals(li) || li.size()<1){
              sb.append(",\"attr\":{\"class\":\"input\"}}}");
           }else{
             sb.append("},");
             sb.append("\"children\":["); 
             for(int m = 0; m<li.size(); m++){//判断子元素下的值域
               sb.append("{\"attr\":{");
               sb.append("\"id\":\"");
               sb.append(subs.get(k).getNumberId()+"-"+li.get(m).getSeqId());
               sb.append("\"},");
               sb.append("\"data\":{");
               sb.append("\"title\":\"");
               String name = li.get(m).getValueName();
               //System.out.println(name);
               sb.append(name);
               sb.append("\"}");
               sb.append("}");
               sb.append(",");
             }
             if (sb.charAt(sb.length() - 1) == ','){
               sb.deleteCharAt(sb.length() - 1);
             }
             sb.append("]}");
           }
            sb.append(",");
         }
        }else{
          
        }
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]}");
        sb.append(",");
      }
    }
      if (sb.charAt(sb.length() - 1) == ','){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      //System.out.println("jjj"+sb.toString());
        
    }else{
     return findParent(request, response);
    }
    long end = System.currentTimeMillis();
    //System.out.println("用时："+ (end-start)+"ms");
    PrintWriter pw = response.getWriter();    
    String rtData = sb.toString();
    pw.println(rtData);    
    pw.flush();
    request.setAttribute("treeData", sb.toString());
    }catch(Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return null;
  }
  public String findMyFile(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person loginUser = null;
    String paras = request.getParameter("par");
    //T9Out.println(paras);
    
    try{ 
      String str="";         
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person personLogin = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9GlSyslogLogic syslog =new T9GlSyslogLogic();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出日志");
      request.setAttribute("data", str); //到search.jsp 页面 去 接收data 对象
      
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    }catch(Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/funcs/rtjson.jsp";
  }
  /**
   * 点击节点传递参数
   */
  public static String passParameter(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String vsplit="";
    String para = request.getParameter("para");
    Connection dbConn = null;
    T9Person loginUser = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    try{
      dbConn = requestDbConn.getSysDbConn();
    StringBuffer sb = new StringBuffer("[");
    String psplit[]= para.split(",");
    for(int i=0; i<psplit.length; i++){
    vsplit  += psplit[i]+",";
    }
   Map<String,String> map = T9StringUtil.toMap(vsplit);
   //T9Out.println(map);
   T9MetaDbHelper helper = new T9MetaDbHelper();
   List <T9SignFile>files=null;
    files = helper.searchFileList(dbConn, null, map);
    //System.out.println("files:::"+files.size());
    //T9FileMetaSaveAct act = new T9FileMetaSaveAct();
   // String jsons =act.toJson(files); 
   if(files.size()>0){
    //System.out.println(map+"999-----"+((T9SignFile)files.get(0)).toJson());
    }
  if(files.size()>0){
    for(int j = 0; j<files.size(); j++){
      sb.append("{");
      sb.append("name:\""+((T9SignFile)files.get(j)).getFileName()+"\"");
      sb.append(",size:\""+((T9SignFile)files.get(j)).getFileSize()+"\"");
      sb.append(",type:\""+((T9SignFile)files.get(j)).getSignType()+"\"");
      sb.append(",path:\""+((T9SignFile)files.get(j)).getFilePath()+"\"");
      sb.append("},");
    }
    if(files.size()>0){
      sb.deleteCharAt(sb.length()-1);
    }
   } 
   sb.append("]");
     T9AjaxUtil.ajax(sb.toString(), response);
 //  request.setAttribute(T9ActionKeys.RET_DATA, jsons); 
   request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

  public static void main(String[] agrs){
  Map<Integer,String> map = new HashMap();
  Map<Integer,String> map1 = new HashMap();
  Map<Integer,String> map2 = new HashMap();
  Map<Integer,String> map3 = new HashMap();
  map.put(1, "a");
  map.put(2, "b");
  map.put(3, "c");
  map.put(4, "d");
  map.put(5, "r");
   map1.put(1, "a");
  map1.put(2, "b");
  map1.put(3, "c");
  map1.put(4, "d");
  map1.put(5, "f");
 for(Iterator <Entry<Integer,String>> it = map.entrySet().iterator(); it.hasNext();){
   Entry<Integer,String> e = it.next();
   for(Iterator <Entry<Integer,String>> its = map1.entrySet().iterator(); its.hasNext();){
     Entry<Integer,String> es = its.next();
     if(e.getKey()==es.getKey()){
      String value = e.getValue()+","+es.getValue();
      map2.put(e.getKey(), value);
     }else{
       map3.put(e.getKey(), e.getValue());//key是不重复的 所以取得值没有重复
     }
   }
 }
 for(int i = 1; i<=map2.size(); i++){
   //System.out.println(i+"--"+ map2.get(i));
 }
 for(int j = 1; j<=map3.size(); j++){ 
   //System.out.println(j+"--"+ map3.get(j));
 }
 /* 第一种获取 如果键相等的话 获取键的不同值
 Set<Integer> keys = map2.keySet();
 for(Integer key: keys){
   //System.out.println("key:"+key+"--value:"+map2.get(key));
 }*/
  
  Set<Integer> keys1 = map.keySet(); //map的keySet()方法只返回一个set实例
  for(Integer key: keys1){
    //System.out.println("key:"+key+"--value:"+map.get(key));
  }
 // System.out.println(keys1+"::");
  }
}
