package t9.pda.mobilseal.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import t9.core.util.db.T9DBUtility;

public class SealUtil {

	
	public boolean isExit(List list,String DEVICE_LIST){
		boolean isexit = false;
		//System.out.println("daxiao"+list.size());
		
		String sss[] = DEVICE_LIST.split(",");
		//System.out.println("length"+sss.length);
		if(sss.length == 0){
			return false;
		}
		for(int i=0;i<list.size();i++){
			
			String s = (String)list.get(i);
			for(int j=0;j<sss.length;j++){
				//System.out.println("结果s:"+s);
				//System.out.println("结果sss:"+sss[j]);
				if(s.equals(sss[j])){
				
					return true;
				}
			}
		
		}
		return isexit;
	}
	public List getDevList(Connection conn , int uid) throws Exception{
		List list = new ArrayList();
	    String result = "";
	    String sql = " select seq_id from mobile_device where uid = '" + uid+"'" ;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      while(rs.next()){
	        String id = rs.getString("seq_id");
	        list.add(id);
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, null);
	    }
	    //System.out.println(list.size());
	    return list;
	  }
	
}
