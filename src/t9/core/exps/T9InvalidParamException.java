package t9.core.exps;

/**
 * 无效的参数异常
 * @author YZQ
 * @version 1.0
 * @date 2006-8-14
 */
public class T9InvalidParamException extends Exception {
  
  /**
   * 构造方法
   * @param msrg
   */
  public T9InvalidParamException(String msrg) {
    super(msrg);
  }
}
