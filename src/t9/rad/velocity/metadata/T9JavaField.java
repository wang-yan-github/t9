package t9.rad.velocity.metadata;

public class T9JavaField {

  private String fieldName;
  private String type;
  private String identifier;//标示符
  private String defaultValue;
  public T9JavaField() {
    // TODO Auto-generated constructor stub
  }
  private T9JavaField(String fieldName, String type, String identifier,
      String defaultValue) {
    this.fieldName = fieldName;
    this.type = type;
    this.identifier = identifier;
    this.defaultValue = defaultValue;
  }
  public String getFieldName() {
    return fieldName;
  }
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getIdentifier() {
    return identifier;
  }
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  public String getDefaultValue() {
    return defaultValue;
  }
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
  public static T9JavaField get(String fieldName, String type, String identifier,
      String defaultValue){
    return new T9JavaField(fieldName, type, identifier, defaultValue);
  }
  @Override
  public String toString() {
    return "T9JavaField [defaultValue=" + defaultValue + ", fieldName="
        + fieldName + ", identifier=" + identifier + ", type=" + type + "]";
  }
}
