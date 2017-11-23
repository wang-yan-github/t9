package t9.rad.velocity.metadata;

public class T9JavaParam {

  private String paramType;
  private String paramName;
  public T9JavaParam() {
    // TODO Auto-generated constructor stub
  }
  private T9JavaParam(String paramType,String paramName) {
    this.paramName = paramName;
    this.paramType = paramType;
  }
  public String getParamType() {
    return paramType;
  }
  public void setParamType(String paramType) {
    this.paramType = paramType;
  }
  public String getParamName() {
    return paramName;
  }
  public void setParamName(String paramName) {
    this.paramName = paramName;
  }
  public static T9JavaParam addParam(String paramType,String paramName) {
    return new T9JavaParam(paramType, paramName);
  }
  @Override
  public String toString() {
    return "T9JavaParam [paramName=" + paramName + ", paramType=" + paramType
        + "]";
  }
  
}
