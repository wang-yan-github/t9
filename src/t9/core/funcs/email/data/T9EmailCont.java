package t9.core.funcs.email.data;

import t9.core.global.T9SysProps;

public class T9EmailCont{
  private static String upload;
  static {
    upload = T9SysProps.getAttachPath();
  }
  public static final String IMGSRC = "";
  public static final String EMAIL_HOME_ACT = "/t9/t9/core/funcs/email/act/T9InnerEMailAct/";
  public static final String EMAIL_HOME_UTILACT = "/t9/t9/core/funcs/email/act/T9InnerEMailUtilAct/";
  public static final String UPLOAD_HOME = upload;
  public static final String MODULE = "email";
  public static final int webLimit = 10;//M
}
