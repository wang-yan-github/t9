package t9.cms.script.core.CMSException;
/**
 * 自己封装的CMS异常类 
 * 方便以后处理异常信息
 * 可以继续扩展
 * @author shenhua
 *
 */
public class CException extends Exception
{

 /**
  * 定义异常各属性
  */
 private static final long serialVersionUID = 1L;
 private String sMessage;
 private int nErrorNo  ;
 private String sInnerMessage;
 private String sInnerExceptionToString;
 private String sFrom;

 public CException()
 {
 }

 public CException(Exception e)
 {
     sMessage = e.getMessage();
     sFrom = e.getLocalizedMessage();
     sInnerMessage = e.getMessage();
     sInnerExceptionToString = e.toString();
 }

 public CException(Exception e, String sFrom)
 {
     sMessage = e.getMessage();
     this.sFrom = sFrom;
     sInnerMessage = e.getMessage();
     sInnerExceptionToString = e.toString();
 }

 public CException(String sMessage, String sFrom)
 {
     this.sMessage = sMessage;
     this.sFrom = sFrom;
 }

 public CException(String sMessage, String sFrom, Exception e)
 {
     this.sMessage = sMessage;
     this.sFrom = sFrom;
     sInnerMessage = e.getMessage();
     sInnerExceptionToString = e.toString();
 }

 public CException(String sMessage, String sFrom, int nErrorNo)
 {
     this.sMessage = sMessage;
     this.sFrom = sFrom;
     this.setnErrorNo(nErrorNo);
 }

 public CException(String sMessage, String sFrom, Exception e, int nErrorNo)
 {
     this.sMessage = sMessage;
     this.sFrom = sFrom;
     sInnerMessage = e.getMessage();
     sInnerExceptionToString = e.toString();
     this.setnErrorNo(nErrorNo);
 }

 public String getInnerExceptionToString()
 {
     return sInnerExceptionToString;
 }

 public String getInnerMessage()
 {
     return sInnerMessage;
 }

 public String getMessage()
 {
     return sMessage;
 }

 public String getFrom()
 {
     return sFrom;
 }

	public void setnErrorNo(int nErrorNo)
	{
		this.nErrorNo = nErrorNo;
	}

	public int getnErrorNo()
	{
		return nErrorNo;
	}
}
