package t9.mobile.mobileseal.services;
/**
 * XXTea 有点困难 。。。 之后慢慢写
 * @author shenhua
 *
 */
public class TDDevice {
	/**
	private $conn;
    private $key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
    private $cur_ver = '1.0.120729';
    
    public function __construct() {
        global $connection;
        $this->conn = $connection;
    }
      
    public function insert_device() {
        $ret = array("state"=>0, "msg"=>"","data"=>"");
        if(!dongle_optional("MOBILE_SEAL") || !tdoa_optional("MOBILE_SEAL")) 
        {
            $query = "select count(*) from MOBILE_DEVICE";
            $cursor = exequery($this->conn, $query);
            if($row = mysql_fetch_array($cursor));
                $count = $row[0];
            if($count >= TRIAL_LIMIT)
            {
                $ret["state"] = -3;
        	    $ret["msg"] = "超过试用版最大授权数";
        	    return $ret;
            }              
        }
        if($_POST["authData"] != "")
        {
            $device_str = xxtea_decrypt(base64_decode(urldecode($_POST["authData"])), $this->key);
            $device_array = td_json_decode($device_str);
            file_put_contents("c:/9.txt", $device_str);
            file_put_contents("c:/10.txt", var_export($device_array,true));
            $DEVICE_INFO = "";
            $DEVICE_INFO .= pack("a".MODEL_LEN, $device_array["model"]);
            $DEVICE_INFO .= pack("a".DEVICE_ID_LEN, $device_array["deviceId"]);
            $DEVICE_INFO .= pack("a".PHONE_NUM_LEN, $device_array["phoneNumber"]);
            $DEVICE_INFO .= pack("a".IMSI_LEN, $device_array["imsi"]);
            $DEVICE_INFO .= pack("a".IMEI_LEN, $device_array["imei"]);
                
            $header = "";
            $header .= pack("a".DEVICE_HEADER_IDENTIFIER_LEN, DEVICE_HEADER_IDENTIFIER);
            $header .= pack("a".DEVICE_HEADER_VERSION_LEN, $this->cur_ver);
            $header .= pack("L", strlen($DEVICE_INFO));
            
            $DEVICE_INFO = $header.$DEVICE_INFO;
            
            $md5_check = md5($DEVICE_INFO);
            
            $query = "select 1 FROM MOBILE_DEVICE WHERE MD5_CHECK='$md5_check'";
            $cursor = exequery($this->conn,$query);
            if(mysql_fetch_array($cursor))
            {
        	    $ret["state"] = -1;
        	    $ret["msg"] = "您已提交过申请，请勿重复提交";
        	}
        	else
        	{
        	    $curtime = time();
        	    $query = "insert into MOBILE_DEVICE (UID,SUBMIT_TIME,DEVICE_TYPE,DEVICE_INFO,DEVICE_NAME,MD5_CHECK) VALUES
        	        ('".$_SESSION["LOGIN_UID"]."', '$curtime', 0, '$DEVICE_INFO', '".$device_array["model"]."','$md5_check')";
        	    exequery($this->conn,$query);
        	    $SEQ_ID = mysql_insert_id();
        	    $ret["data"] = array("SEQ_ID"=>$SEQ_ID,"model"=>$device_array["model"],"time"=>date("Y-m-d",time()));
        	}
        	
        }
        else
        {
            $ret["state"] = -2;
        	$ret["msg"] = "提交数据错误！";
        }
        
        return $ret;
    }
    
    
    public function delete_device() 
    {
        
    }
    
    public function get_device_info($authData)
    {
    	$device_str = xxtea_decrypt(base64_decode(urldecode($authData)), $this->key);
        $device_array = td_json_decode($device_str);
        
        $DEVICE_INFO = "";
        $DEVICE_INFO .= pack("a".MODEL_LEN, $device_array["model"]);
        $DEVICE_INFO .= pack("a".DEVICE_ID_LEN, $device_array["deviceId"]);
        $DEVICE_INFO .= pack("a".PHONE_NUM_LEN, $device_array["phoneNumber"]);
        $DEVICE_INFO .= pack("a".IMSI_LEN, $device_array["imsi"]);
        $DEVICE_INFO .= pack("a".IMEI_LEN, $device_array["imei"]);
            
        $header = "";
        $header .= pack("a".DEVICE_HEADER_IDENTIFIER_LEN, DEVICE_HEADER_IDENTIFIER);
        $header .= pack("a".DEVICE_HEADER_VERSION_LEN, $this->cur_ver);
        $header .= pack("L", strlen($DEVICE_INFO));
        
        $DEVICE_INFO = $header.$DEVICE_INFO;
        
        return $DEVICE_INFO;
    }
    
    public function get_info($DEVICE_INFO) {
        global $device_version_array;
        $ret = array();
        $header_head = unpack(DEVICE_HEADER_HEAD, substr($DEVICE_INFO, 0, DEVICE_HEADER_LEN));
	 	if(array_key_exists($header_head["header_version"], $device_version_array))
	 	{
	 		$ret = unpack($device_version_array[$header_head["header_version"]], substr($DEVICE_INFO,DEVICE_HEADER_LEN, $header_head['data_size']));
	 	}
	 	return $ret;
    }
    
    
}

if (!extension_loaded('xxtea')) {
    function long2str($v, $w) {
        $len = count($v);
        $n = ($len - 1) << 2;
        if ($w) {
            $m = $v[$len - 1];
            if (($m < $n - 3) || ($m > $n)) return false;
            $n = $m;
        }
        $s = array();
        for ($i = 0; $i < $len; $i++) {
            $s[$i] = pack("V", $v[$i]);
        }
        if ($w) {
            return substr(join('', $s), 0, $n);
        }
        else {
            return join('', $s);
        }
    }

    function str2long($s, $w) {
        $v = unpack("V*", $s. str_repeat("\0", (4 - strlen($s) % 4) & 3));
        $v = array_values($v);
        if ($w) {
            $v[count($v)] = strlen($s);
        }
        return $v;
    }

    function int32($n) {
        while ($n >= 2147483648) $n -= 4294967296;
        while ($n <= -2147483649) $n += 4294967296;
        return (int)$n;
    }

    function xxtea_encrypt($str, $key) {
        if ($str == "") {
            return "";
        }
        $v = str2long($str, true);
        $k = str2long($key, false);
        if (count($k) < 4) {
            for ($i = count($k); $i < 4; $i++) {
                $k[$i] = 0;
            }
        }
        $n = count($v) - 1;

        $z = $v[$n];
        $y = $v[0];
        $delta = 0x9E3779B9;
        $q = floor(6 + 52 / ($n + 1));
        $sum = 0;
        while (0 < $q--) {
            $sum = int32($sum + $delta);
            $e = $sum >> 2 & 3;
            for ($p = 0; $p < $n; $p++) {
                $y = $v[$p + 1];
                $mx = int32((($z >> 5 & 0x07ffffff) ^ $y << 2) + (($y >> 3 & 0x1fffffff) ^ $z << 4)) ^ int32(($sum ^ $y) + ($k[$p & 3 ^ $e] ^ $z));
                $z = $v[$p] = int32($v[$p] + $mx);
            }
            $y = $v[0];
            $mx = int32((($z >> 5 & 0x07ffffff) ^ $y << 2) + (($y >> 3 & 0x1fffffff) ^ $z << 4)) ^ int32(($sum ^ $y) + ($k[$p & 3 ^ $e] ^ $z));
            $z = $v[$n] = int32($v[$n] + $mx);
        }
        return long2str($v, false);
    }

    function xxtea_decrypt($str, $key) {
        if ($str == "") {
            return "";
        }
        $v = str2long($str, false);
        $k = str2long($key, false);
        if (count($k) < 4) {
            for ($i = count($k); $i < 4; $i++) {
                $k[$i] = 0;
            }
        }
        $n = count($v) - 1;

        $z = $v[$n];
        $y = $v[0];
        $delta = 0x9E3779B9;
        $q = floor(6 + 52 / ($n + 1));
        $sum = int32($q * $delta);
        while ($sum != 0) {
            $e = $sum >> 2 & 3;
            for ($p = $n; $p > 0; $p--) {
                $z = $v[$p - 1];
                $mx = int32((($z >> 5 & 0x07ffffff) ^ $y << 2) + (($y >> 3 & 0x1fffffff) ^ $z << 4)) ^ int32(($sum ^ $y) + ($k[$p & 3 ^ $e] ^ $z));
                $y = $v[$p] = int32($v[$p] - $mx);
            }
            $z = $v[$n];
            $mx = int32((($z >> 5 & 0x07ffffff) ^ $y << 2) + (($y >> 3 & 0x1fffffff) ^ $z << 4)) ^ int32(($sum ^ $y) + ($k[$p & 3 ^ $e] ^ $z));
            $y = $v[0] = int32($v[0] - $mx);
            $sum = int32($sum - $delta);
        }
        return long2str($v, true);
    }
    **/
}
