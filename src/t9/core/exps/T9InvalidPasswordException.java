package t9.core.exps;

/**
 * 非法的用户密码异常
 * @author YZQ
 * @version 1.0
 * @date 2006-7-31
 */
public class T9InvalidPasswordException extends Exception {
  /**
   * 构造方法
   * @param msrg
   */
  public T9InvalidPasswordException(String msrg) {
    super(msrg);
  }
}
