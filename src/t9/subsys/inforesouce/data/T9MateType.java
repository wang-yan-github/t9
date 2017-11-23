package t9.subsys.inforesouce.data;



import java.io.Serializable;
import java.util.List;

import t9.core.util.T9Utility;
/**
 * 信息管理
 * @author qwx110
 *
 */
public class T9MateType implements Serializable{
  private int seqId;         //主键
  private String numberId;   //编号
  private String cNname;     //中文名
  private String eNname;     //英文名
  private String define;     //描述
  private String aim;        //目的
  private String constraint; //约束
  private String repeat;     //是否重复
  private String element_type;//文件类型 1,文本类型 2，图片类型 3，视频类型
  private String typeId;      //数据类型
  private String codeId;      //------------>无用
  private String rangeId;     //值域
  private String lessValue;   //默认值
  private String elementId;   //元素类型
  private String parentId;    //父类id
  private String note;        //注释
  
  private List<T9MateValue> values; //这个元数据类型的值域
  private List<T9MateType> subs;    //这个元数据类型的子元素
  public List<T9MateType> getSubs() {
    return subs;
  }
  public void setSubs(List<T9MateType> subs) {
    this.subs = subs;
  }
  public List<T9MateValue> getValues(){
    return values;
  }
  public void setValues(List<T9MateValue> values){
    this.values = values;
  }
  public String getNote(){
    return note;
  }
  public void setNote(String note){
    this.note = note;
  }
  public int getSeqId(){
    return seqId;
  }
  public void setSeqId(int seqId){
    this.seqId = seqId;
  }

  public String getNumberId(){
    return numberId;
  }
  public void setNumberId(String numberId){
    this.numberId = numberId;
  }
  public String getcNname(){
    if(T9Utility.isNullorEmpty(cNname) || "null".equalsIgnoreCase(cNname)){
      cNname = "";
    }
    return cNname;
  }
  public void setcNname(String cNname){
    this.cNname = cNname;
  }
  public String geteNname(){
    if(T9Utility.isNullorEmpty(eNname) || "null".equalsIgnoreCase(eNname)){
      eNname = "";
    }
    return eNname;
  }
  public void seteNname(String eNname){
    this.eNname = eNname;
  }
  public String getDefine(){
    if(T9Utility.isNullorEmpty(define) || "null".equalsIgnoreCase(define)){
      define = "";
    }
    return define;
  }
  public void setDefine(String define){
    this.define = define;
  }
  public String getAim(){
    if(T9Utility.isNullorEmpty(aim) || "null".equalsIgnoreCase(aim)){
      aim = "";
    }
    return aim;
  }
  public void setAim(String aim){
    this.aim = aim;
  }
  public String getConstraint(){
    if(T9Utility.isNullorEmpty(constraint) || "null".equalsIgnoreCase(constraint)){
      constraint = "";
    }
    return constraint;
  }
  public void setConstraint(String constraint){
    this.constraint = constraint;
  }
  public String getRepeat(){
    return repeat;
  }
  public void setRepeat(String repeat){
    this.repeat = repeat;
  }
  public String getElement_type(){
    return element_type;
  }
  public void setElement_type(String elementType){
    element_type = elementType;
  }
  public String getTypeId(){
    return typeId;
  }
  public void setTypeId(String typeId){
    this.typeId = typeId;
  }
  public String getCodeId(){
    return codeId;
  }
  public void setCodeId(String codeId){
    this.codeId = codeId;
  }
  public String getRangeId(){
    return rangeId;
  }
  public void setRangeId(String rangeId){
    this.rangeId = rangeId;
  }
  public String getLessValue(){
    return lessValue;
  }
  public void setLessValue(String lessValue){
    this.lessValue = lessValue;
  }
  public String getElementId(){
    return elementId;
  }
  public void setElementId(String elementId){
    this.elementId = elementId;
  }
  public String getParentId(){
    return parentId;
  }
  public void setParentId(String parentId){
    this.parentId = parentId;
  }
  
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append("{");
       sb.append("seqId:").append(seqId).append(",");
       sb.append("numberId:").append("'").append(numberId).append("',");
       sb.append("cNname:").append("'").append(cNname).append("',");
       sb.append("eNname:").append("'").append(eNname).append("',");
       sb.append("define:").append("'").append(define).append("',");
       sb.append("aim:").append("'").append(aim).append("',");
       sb.append("constraint:").append("'").append(constraint).append("',");
       sb.append("repeat:").append("'").append(repeat).append("',");
       sb.append("typeId:").append("'").append(typeId).append("',");
       sb.append("rangeId:").append("'").append(rangeId).append("',");
       sb.append("lessValue:").append("'").append(lessValue).append("',");
       sb.append("elementId:").append("'").append(elementId).append("',");
       sb.append("parentId:").append("'").append(parentId).append("',");
       sb.append("note:").append("'").append(note).append("',");
       if(values!=null && values.size()!=0){         
           sb.append("list:[");
           for(int i=0; i<values.size(); i++){
             if(i < values.size()-1 ){
               sb.append(values.get(i).toString()).append(",");
             }else{
               sb.append(values.get(values.size()-1).toString());
             }
           }
           sb.append("]");
       }else{
         sb.append("list:[]");
       }
    sb.append("}");
    return sb.toString();
  }
  
  public static void main(String[] args){
    T9MateType mt = new T9MateType();
    //System.out.println(mt.toString());
  }
}
