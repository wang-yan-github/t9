package t9.project.comment.logic;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.project.comment.data.T9ProjComment;

public class T9ProjCommentLogic {
  /**
   * 显示翻页
   * 
   * @param con
   * @param paraMap
   * @param request
   * @return
   * @throws Exception
   */
  public static String getPages(Connection con, HttpServletRequest request) throws Exception {
    String sql = "select seq_id,writer,content,write_time from proj_comment";
    try {
      sql += " where proj_id = "+request.getParameter("projId")+" order by write_time desc";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request.getParameterMap());
      T9PageDataList dataList = T9PageLoader.loadPageList(con, queryParam, sql);
      return dataList.toJson();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    }
  }
  /**
   * 添加新批注
   * @param con
   * @param comment
   * @throws Exception
   */
  public void addComment(Connection con,T9ProjComment comment) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.saveSingle(con, comment);
  }
  /**
   * 更新
   * @param con
   * @param comment
   * @throws Exception
   */
  public void updateComment(Connection con,T9ProjComment comment) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, comment);
  }
  /**
   * 删除
   * @param con
   * @param seqId
   * @throws Exception
   */
  public void deleteComment(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.deleteSingle(con, T9ProjComment.class, seqId);
  }
  public T9ProjComment getById(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    return (T9ProjComment) t9orm.loadObjSingle(con, T9ProjComment.class, seqId);
  }
}

