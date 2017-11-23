package t9.core.funcs.doc.receive.data;
import t9.core.util.T9Utility;

public class T9DocNext{
  private String runId;
  private String flowId;
  private String flowPrcs;
  private String prcsName;
  public String getRunId(){
    if(runId == null || runId=="" || runId =="null"){
      return "";
    }
    return runId;
  }
  public void setRunId(String runId){
    this.runId = runId;
  }
  public String getFlowId(){
    if(flowId == null || flowId=="" || flowId =="null"){
      return "";
    }
    return flowId;
  }
  public void setFlowId(String flowId){
    this.flowId = flowId;
  }
  public String getFlowPrcs(){
    if(flowPrcs == null || flowPrcs=="" || flowPrcs =="null"){
      return "";
    }
    return flowPrcs;
  }
  public void setFlowPrcs(String flowPrcs){
    this.flowPrcs = flowPrcs;
  }
  public String getPrcsName(){
    if(prcsName == null || prcsName=="" || prcsName =="null"){
      return "";
    }
    return prcsName;
  }
  public void setPrcsName(String prcsName){
    this.prcsName = prcsName;
  }
 
  public String toJson(){
    StringBuffer sb = new StringBuffer();
    sb.append("{");
      sb.append("runId:").append("'").append(T9Utility.encodeSpecial(this.getRunId())).append("',");
      sb.append("flowId:").append("'").append(T9Utility.encodeSpecial(this.getFlowId())).append("',");
      sb.append("flowPrcs:").append("'").append(T9Utility.encodeSpecial(this.getFlowPrcs())).append("',");
      sb.append("prcsName:").append("'").append(T9Utility.encodeSpecial(this.getPrcsName())).append("'");
    sb.append("}");
    return sb.toString();
  }
}
