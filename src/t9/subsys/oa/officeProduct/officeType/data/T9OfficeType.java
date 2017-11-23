package t9.subsys.oa.officeProduct.officeType.data;

import java.util.List;

import t9.subsys.oa.officeProduct.person.data.T9OfficeProducts;

public class T9OfficeType {
 private int seqId; //自增长                                                                                                                                                           
 private String typeName;	// 办公用品类型名称                                                                                                                                                                          
 private String typeOrder;	//办公用品类型排序号                                                                                                                                                                     
 private int	typeParentId; //暂没用                                                                                                                                                                           
 private int	typeDepository; //和办公用品库中的seq_id字段对应
 private List<T9OfficeProducts> products;
 
 public List<T9OfficeProducts> getProducts() {
	return products;
}
public void setProducts(List<T9OfficeProducts> products) {
	this.products = products;
}
public int getSeqId() {
	return seqId;
}
public void setSeqId(int seqId) {
	this.seqId = seqId;
}
public String getTypeName() {
	return typeName;
}
public void setTypeName(String typeName) {
	this.typeName = typeName;
}
public String getTypeOrder() {
	return typeOrder;
}
public void setTypeOrder(String typeOrder) {
	this.typeOrder = typeOrder;
}

public int getTypeParentId() {
	return typeParentId;
}
public void setTypeParentId(int typeParentId) {
	this.typeParentId = typeParentId;
}
public int getTypeDepository() {
	return typeDepository;
}
public void setTypeDepository(int typeDepository) {
	this.typeDepository = typeDepository;
}

}
