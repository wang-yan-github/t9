package t9.mobile.mobileseal.services;
/**
 * 这个类 主要是配合php 的 TDSeal.php来做的
 * 处理方法 插入签章 
 * @author shenhua
 *
 */
import java.sql.Connection;
public class TDSeal {

	    private Connection conn;
	    private String cur_ver = "1.0.120729";
	    private String seal_id;
	    private String seal_data;
	    private String header;
	    private String body;
	    
	    
	    public TDSeal(){
	    	
	    }
	    
	    public void insert_seal(String SEAL_FILE, String SEAL_NAME, String SEAL_PASSWORD, String DEPT_ID) {
	     /**
	    	if($SEAL_FILE){
	            $SEAL_IMG_DATA = file_get_contents($SEAL_FILE);
	            $SEAM_IMG_INFO = getimagesize($SEAL_FILE);
	        }
	        if(strlen($SEAL_IMG_DATA) > 1024*50)
	        {
	            return _("图片大小超过50k");
	        }
	        if($SEAM_IMG_INFO[2]!=2 &&$SEAM_IMG_INFO[2]!=3 && $SEAM_IMG_INFO[2]!=6)
	        {
	            return _("不支持的图片格式（bmp/jpg/png）");
	        }
	        $SEAL_PASSWORD = crypt($SEAL_PASSWORD);

	        //签章标识
	        $SEAL_DATA .= pack("a".SEAL_HEADER_IDENTIFIER_LEN, SEAL_HEADER_IDENTIFIER);
	        //签章版本
	        $SEAL_DATA .= pack("a".SEAL_HEADER_VERSION_LEN, $this->cur_ver);
	        //签章密钥长度
	        $SEAL_DATA .= pack("L", strlen($SEAL_PASSWORD));
	        //图片格式
	        $SEAL_DATA .= pack("L", $SEAM_IMG_INFO[2]);
	        //印章图片长度
	        $SEAL_DATA .= pack("L", strlen($SEAL_IMG_DATA));
	        //签章密钥数据段
	        $SEAL_DATA .= pack("a".strlen($SEAL_PASSWORD), $SEAL_PASSWORD);
	        //印章图片数据段
	        $SEAL_DATA .= pack("a".strlen($SEAL_IMG_DATA), $SEAL_IMG_DATA);
	        //------------------- 保存 -----------------------
	        $CREATE_TIME=time();
	        
	        $SEAL_DATA_HEX = bin2hex($SEAL_DATA);
	        
	        $query="insert into MOBILE_SEAL(DEPT_ID,SEAL_NAME,SEAL_DATA,CREATE_TIME,CREATE_USER) values ('$DEPT_ID','$SEAL_NAME',0x$SEAL_DATA_HEX,'$CREATE_TIME','".$_SESSION["LOGIN_UID"]."')";
	        exequery($this->conn,$query);
	        
	        return true;
	      **/
	    }
	    public void get_pic()
	    {
		   // return array($this->header["imgType"], $this->body["pic"]);
	    }
	    
	    private void  get_header()
	    {
	    /**
	        if($this->seal_data != "")
	        {
	            $header = unpack(SEAL_HEADER_HEAD, substr($this->seal_data, 0, SEAL_HEADER_LEN));
	            if(is_array($header))
	            {
	                $this->header = $header;
	            }
	        }
	    **/
	    }
	    private boolean get_seal(String id)
	    {
	        int iid = Integer.parseInt(id);
	        if(iid == 0) {
	            return false;
	        }
	        
	        seal_id = id;
	        String sql = "select SEAL_DATA FROM MOBILE_SEAL WHERE ID='"+seal_id+"'";
	       // $cursor= exequery($this->conn,$query);
	        //if($ROW=mysql_fetch_array($cursor))
	        //{
	         //   $this->seal_data = $ROW["SEAL_DATA"];
	        //    return true;
	       // }
	       // else
	         //   return false;
	        return false;
	    }
	    
	    private String get_body(){
	    	return "";
	    }
	    
	    public String check_password(String password)
	    {
	        //return crypt($password, $this->body["password"]) == $this->body["password"] ? true : false;
	    	return "";
	    }
	    
	    
	    /**
	     * 
	    private function get_body()
	    {
	        global $seal_version_array, $header;
	        $header = $this->header;
	        if(array_key_exists($this->header["header_version"], $seal_version_array))
		 	{
		 	    $pack_format = preg_replace_callback('/{(.*?)}/i',
		 	        create_function('$matches','return $GLOBALS["header"][$matches[1]];'),
		 	        $seal_version_array[$this->header["header_version"]]);
		        $body = unpack($pack_format, substr($this->seal_data, SEAL_HEADER_LEN));
		        if(is_array($body))
		        {
		            $this->body = $body;
		        }
		    }        
	    }
	     * @return
	     */
	    
	    public void change_password(String password)
	    {
	    	String SEAL_PASSWORD = "";//crypt($password);
	    	String SEAL_IMG_TYPE = "";//$this->header["imgType"];
	    	String SEAL_IMG_DATA = "";//$this->body["pic"];
	    	/**
	    	//签章标识
	        $SEAL_DATA .= pack("a".SEAL_HEADER_IDENTIFIER_LEN, SEAL_HEADER_IDENTIFIER);
	        //签章版本
	        $SEAL_DATA .= pack("a".SEAL_HEADER_VERSION_LEN, $this->cur_ver);
	        //签章密钥长度
	        $SEAL_DATA .= pack("L", strlen($SEAL_PASSWORD));
	        //图片格式
	        $SEAL_DATA .= pack("L", $SEAL_IMG_TYPE);
	        //印章图片长度
	        $SEAL_DATA .= pack("L", strlen($SEAL_IMG_DATA));
	        //签章密钥数据段
	        $SEAL_DATA .= pack("a".strlen($SEAL_PASSWORD), $SEAL_PASSWORD);
	        //印章图片数据段
	        $SEAL_DATA .= pack("a".strlen($SEAL_IMG_DATA), $SEAL_IMG_DATA);
	        //------------------- 保存 -----------------------
	        $SEAL_DATA_HEX = bin2hex($SEAL_DATA);
	        $query = "update MOBILE_SEAL set SEAL_DATA=0x$SEAL_DATA_HEX where ID='".$this->seal_id."'";
	        exequery($this->conn,$query);
	        **/
	    }
}
