package test.core.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class T9InnerBean1 {
  String name = null;  
  String id = null;
  private List<T9InnerBean11> bean11List = new ArrayList<T9InnerBean11>();
  private List<T9InnerBean12> bean12List = new ArrayList<T9InnerBean12>();
  
  public void addBean12(T9InnerBean12 bean) {
    bean12List.add(bean);
  }
  public Iterator<T9InnerBean12> itBean12() {
    return bean12List.iterator();
  }
  
  public void addBean11(T9InnerBean11 bean) {
    bean11List.add(bean);
  }
  public Iterator<T9InnerBean11> itBean11() {
    return bean11List.iterator();
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
}
