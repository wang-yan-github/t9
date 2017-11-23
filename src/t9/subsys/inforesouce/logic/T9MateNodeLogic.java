package t9.subsys.inforesouce.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.data.T9MateNode;

/**
 * 处理选择的树的选中的节点
 * @author qwx110
 *
 */
public class T9MateNodeLogic{
  
  /**
   * 插入选择树的节点
   * @param dbConn
   * @param node
   * @return
   * @throws Exception
   */
  public int saveAjax(Connection dbConn, T9MateNode node, String nodeType) throws Exception{    
    PreparedStatement ps = null;
    String sql = "insert into MATE_NODE(USER_ID, NODES, TAGNAME, NODE_TYPE) values(?,?,?,?)" ;   
    int k =0;   
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1, node.getUserId());
      ps.setString(2, node.getNodes());
      ps.setString(3, node.getTagName());
      ps.setString(4, nodeType);
      k = ps.executeUpdate();
    } catch (SQLException e){      
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
    return k;
  } 
  
  /**
   * 删除某个用户下的所有选择的节点
   * @param dbConn
   * @param user
   * @return
   * @throws Exception
   */
  public int deleteNode(Connection dbConn, T9Person user)throws Exception{
    PreparedStatement ps = null;
    String sql = "delete from MATE_NODE where USER_ID=" + user.getSeqId() ;   
    int k=0;
    try{
      ps = dbConn.prepareStatement(sql);
      k = ps.executeUpdate();
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
    return k;
  }
  
  /**
   * 查询某个用户下所有的标签名
   * @param dbConn
   * @param user
   * @return
   * @throws Exception
   */
  public List<T9MateNode> allTagName(Connection dbConn, T9Person user, String nodeType)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID, USER_ID, NODES, TAGNAME from MATE_NODE where USER_ID="+user.getSeqId()+" and node_type="+ nodeType +" order by SEQ_ID DESC"; 
    ps = dbConn.prepareStatement(sql);
    rs = ps.executeQuery();
    List<T9MateNode> nodes = new ArrayList<T9MateNode>();
    while(rs.next()){
      T9MateNode node = new T9MateNode();
      node.setSeqId(rs.getInt("SEQ_ID"));
      node.setUserId(rs.getInt("USER_ID"));
      node.setNodes(rs.getString("NODES"));
      node.setTagName(rs.getString("TAGNAME"));
      nodes.add(node);
    }
    return nodes;    
  }
  
  /**
   * 把一个list转化为字符串类型
   * @param dbConn
   * @param user
   * @return
   * @throws Exception
   */
  public String tagName(Connection dbConn, T9Person user, String nodeType)throws Exception{
    return toAString(allTagName(dbConn,user, nodeType ));
  }
  
  /**
   * 删除所选标签
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int deleteTagName(Connection dbConn, int seqId)throws Exception{
    PreparedStatement ps = null;  
    int ok = 0;
    try{
      String sql = "delete from MATE_NODE where seqId=?"; 
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1, seqId);
      ok = ps.executeUpdate();
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }   
    return ok;    
  }
  
  public String toAString(List<T9MateNode> nodes){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if(nodes != null && nodes.size()>0){
      for(int i=0; i<nodes.size(); i++){
        if(i < nodes.size()-1){
          sb.append(nodes.get(i).toString()).append(",");
        }else{
          sb.append(nodes.get(i).toString());
        }
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
