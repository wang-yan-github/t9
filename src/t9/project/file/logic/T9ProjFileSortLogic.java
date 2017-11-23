package t9.project.file.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.project.file.data.T9ProjFileSort;

public class T9ProjFileSortLogic {
/**
 * 获取单个数据
 * @param con
 * @param seqId
 * @return
 * @throws Exception
 */
  public  T9ProjFileSort getById(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    T9ProjFileSort projFileSort = (T9ProjFileSort) t9orm.loadObjSingle(con, T9ProjFileSort.class, seqId);
    return projFileSort;
  }
  /**
   * 增加数据
   * @param con
   * @param projFileSort
   * @throws Exception
   */
  public  void addFileSort(Connection con,T9ProjFileSort projFileSort) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.saveSingle(con, projFileSort);
  }
  /**
   * 更新数据
   * @param con
   * @param projFileSort
   * @throws Exception
   */
  public  void updateFileSort(Connection con,T9ProjFileSort projFileSort) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, projFileSort);
  }
  /**
   * 删除单个数据
   * @param con
   * @param seqId
   * @throws Exception
   */
  public  void deleteFileSort(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.deleteSingle(con, T9ProjFileSort.class, seqId);
  }
  /**
   * 获取目录列表
   * @param dbConn
   * @param projId
   * @return
   * @throws Exception
   */
  public String getStyleList(Connection dbConn,int projId) throws Exception{
    List<T9ProjFileSort> sortList = null;
    StringBuffer sb = new StringBuffer("[");
    T9ORM orm=new T9ORM();
    String[] filters={" 1=1 and proj_id = '"+ projId+"'"};
    sortList=orm.loadListSingle(dbConn, T9ProjFileSort.class, filters);
    if (sortList != null && sortList.size() > 0) {
         for (T9ProjFileSort sortItem : sortList) {
             sb.append("{");
             sb.append("\"seqId\":"+sortItem.getSeqId()+",");
             sb.append("\"sortName\":\""+sortItem.getSortName()+"\",");
             sb.append("\"sortNo\":\""+sortItem.getSortNo()+"\"},");
         }
        
     }
     if(sb.length()>3){
         sb=sb.deleteCharAt(sb.length()-1);
     }
     sb.append("]");
    return sb.toString();
}
  public String getPriAndUser(Connection con,int seqId,int projId) throws Exception {
    List<T9ProjFileSort> sortList = null;
    StringBuffer sb = new StringBuffer("[");
    sb.append("{userName:\"");
    sb.append(this.getProjUserNameById(con, projId));
    sb.append("\",userId:\"");
    sb.append(this.getProjUserById(con, projId));
    sb.append("\"},");
    T9ORM orm=new T9ORM();
    String[] filters={" 1=1 and seq_id = '"+ seqId+"'"};
    sortList=orm.loadListSingle(con, T9ProjFileSort.class, filters);
    if (sortList != null && sortList.size() > 0) {
      for (T9ProjFileSort sortItem : sortList) {
          sb.append("{");
          sb.append("\"newUser\":\""+sortItem.getNewUser()+"\",");
          sb.append("\"viewUser\":\""+sortItem.getViewUser()+"\",");
          sb.append("\"manageUser\":\""+sortItem.getManageUser()+"\",");
          sb.append("\"modifyUser\":\""+sortItem.getModifyUser()+"\"},");
      }
     
  }
  if(sb.length()>3){
      sb=sb.deleteCharAt(sb.length()-1);
  }
  sb.append("]");
    return sb.toString();
    
  }
  /**
   * 获取用户名
   * @author zq
   * 2013-4-8
   * @param con
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getProjUserNameById(Connection con,int seqId) throws Exception {
    String data="";
    data = this.getProjUserById(con, seqId);
    T9ORM t9orm = new T9ORM();
    String[] userId=data.split(",");
    data="";
    for (int i = 0; i < userId.length; i++) {
    	if("".equals(userId[i])){
    	  continue;
    	}
      T9Person user = (T9Person) t9orm.loadObjSingle(con, T9Person.class, Integer.parseInt(userId[i]));
      data += user.getUserName()+",";
    }
    return data;
  }
  /**
   * 获取用户ids
   * @author zq
   * 2013-4-8
   * @param con
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getProjUserById(Connection con,int seqId) throws Exception {
    String projUser="";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select proj_user from proj_project where seq_id = " + seqId;
    try {
      stmt = con.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        projUser = rs.getString("proj_user");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    String data="";
    if(projUser!=null && !"null".equals(projUser) && !"".equals(projUser)){
      projUser=projUser.replace("|", "");
      String[] users = projUser.split(",");
      String[] user=new String[users.length];
      user[0]=users[0];
      int n=1;
      data=user[0]+",";
      for(int i=1;i<users.length;i++){
        for (int j = 0; j < i; j++) {
           if(users[i].equals(user[j])){
             break;
           }
           if(j==(i-1)){
             user[n]=users[i];
             data+=user[n]+",";
             n++;
           }
        }
      }
      data=data.replace(",,",",");
    }
    
//    System.out.println(data);
    return data;
  }
  /**
   * 获取文件夹权限
   * @author zq
   * 2013-4-3
   * @param con
   * @param seqId
   * @throws Exception 
   */
  public T9ProjFileSort getSortPrivById(Connection con,int seqId) throws Exception{
    T9ORM t9orm = new T9ORM();
    return (T9ProjFileSort) t9orm.loadObjSingle(con, T9ProjFileSort.class, seqId);
    
  }
	/**
	 * 递归获取文件夹名路径
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public void getSortNamePath(Connection dbConn, int seqId, StringBuffer buffer) throws Exception {
		T9ProjFileSort fileSort = getSortNameById(dbConn, seqId);

		int sortParent = 0;
		String sortName = "";
		if (fileSort != null) {

			sortParent = fileSort.getSortParent();
			sortName = fileSort.getSortName();
		}

		// 处理特殊字符
		sortName = sortName.replaceAll("[\\\\/:*?\"<>|]", "");
		sortName = sortName.replaceAll("[\n-\r]", "<br>");
		sortName = sortName.replace("\"", "\\\"");

		buffer.append(sortName + "/,");
		boolean flag = isHaveSortParent(dbConn, sortParent);
		if (flag) {
			getSortNamePath(dbConn, sortParent, buffer);
		}
	}

	/**
	 * 查询文件夹信息

	 * 
	 * 
	 * 
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9ProjFileSort getSortNameById(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9ProjFileSort) orm.loadObjSingle(dbConn, T9ProjFileSort.class, seqId);
	}
	/**
	 * 判断是否还有子级文件夹

	 * 
	 * 
	 * 
	 * 
	 * @param dbConn
	 * @param sortParent
	 * @return
	 * @throws Exception
	 */
	public boolean isHaveSortParent(Connection dbConn, int sortParent) throws Exception {
		Boolean flag = false;
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("SEQ_ID", sortParent);
		List<T9ProjFileSort> list = orm.loadListSingle(dbConn, T9ProjFileSort.class, map);
		if (list.size() > 0) {
			flag = true;
		}
		return flag;
	}

}
