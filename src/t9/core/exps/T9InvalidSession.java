package t9.core.exps;

import javax.servlet.jsp.JspException;

public class T9InvalidSession extends JspException {
  /**
   * 构造方法
   * @param msrg
   */
  public T9InvalidSession(String msrg) {
    super(msrg);
  }
}
