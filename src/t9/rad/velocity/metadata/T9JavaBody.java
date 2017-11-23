package t9.rad.velocity.metadata;

import java.util.ArrayList;
import java.util.List;

public class T9JavaBody {

  private String  className;
  private List<T9JavaField>  fields  = new ArrayList<T9JavaField>();
  private List<T9JavaMethod> methods = new ArrayList<T9JavaMethod>();

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<T9JavaField> getFields() {
    return fields;
  }

  public void setFields(List<T9JavaField> fields) {
    this.fields = fields;
  }

  public T9JavaBody addFields(T9JavaField field) {
    this.fields.add(field);
    return this;
  }

  public List<T9JavaMethod> getMethods() {
    return methods;
  }

  public void setMethods(List<T9JavaMethod> methods) {
    this.methods = methods;
  }

  public T9JavaBody addMethods(T9JavaMethod methods) {
    this.methods.add(methods);
    return this;
  }

  @Override
  public String toString() {
    return "T9JavaBody [className=" + className + ", fields=" + fields
        + ", methods=" + methods + "]";
  }

}
