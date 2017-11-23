package t9.core.funcs.system.act.imp;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.logic.T9SystemLogic;

public interface T9LoginValidator {
  public static Logger log = Logger.getLogger("t9.core.funcs.system.act.imp.T9LoginValidator");
  public static final T9SystemLogic logic = new T9SystemLogic();
  public boolean isValid(HttpServletRequest request, T9Person person, Connection conn) throws Exception;
  public void addSysLog(HttpServletRequest request, T9Person person, Connection conn) throws Exception;
  public String getValidatorType();
  public int getValidatorCode();
  public String getValidatorMsg();
}
