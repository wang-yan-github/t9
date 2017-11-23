package t9.rad.velocity.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class T9JavaMethod {

  private String methodName;
  private String returnType;
  private List<T9JavaParam> args = new ArrayList<T9JavaParam>();
  private T9JavaMethodBody methodBody;
  
  public T9JavaMethod() {
    // TODO Auto-generated constructor stub
  }
  private T9JavaMethod(String methodName, String returnType) {
    super();
    this.methodName = methodName;
    this.returnType = returnType;
  }
  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  public String getReturnType() {
    return returnType;
  }
  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }
  
  public List<T9JavaParam> getArgs() {
    return args;
  }
  public void setArgs(List<T9JavaParam> args) {
    this.args = args;
  }
  public T9JavaMethod addArgs(T9JavaParam arg) {
    this.args.add(arg);
    return this;
  }
  
  public static T9JavaMethod get(String methodName, String returnType) {
    return new T9JavaMethod(methodName, returnType);
  }
  
  public T9JavaMethodBody getMethodBody() {
    return methodBody;
  }
  
  public T9JavaMethod setMethodBody(T9JavaMethodBody methodBody) {
    this.methodBody = methodBody;
    return this;
  }
  @Override
  public String toString() {
    return "T9JavaMethod [args=" + args + ", methodBody=" + methodBody
        + ", methodName=" + methodName + ", returnType=" + returnType + "]";
  }
  
}
