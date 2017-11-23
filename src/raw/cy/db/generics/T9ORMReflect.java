package raw.cy.db.generics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raw.cy.db.T9StringFormat;

public class T9ORMReflect {

  /**
   * 2.0版 得到数据库对应字段和值的映射，值符合数据的要求
   * 
   * @param obj
   *          数据库表所对应的Java对象
   * @param conn
   *          数据库连接
   * @param fkTableNO
   *          外键关联表编码（如果为从表则加入，为主表则为空null）
   * @return map 数据库对应字段和值的映射
   */
  public Map<String, Object> getFieldInfo(Object obj) {
    Map<String, Object> fieldInfo = new HashMap<String, Object>();
    Class cls = obj.getClass();
    Field[] fields = null;
    fields = cls.getDeclaredFields();
    String key = null;
    Object value = null;
    String tableName = T9StringFormat.format(cls.getSimpleName(), "T9");// 得到数据库的表明
    fieldInfo.put("tableName", tableName); // 添加数据表的名称
    // 添加外键关联字段

    for (Field field : fields) {

      value = getFieldValue(obj, field);
      if (value == null) {
        // System.out.println("fieldName >>>>>> when value = null >>> "+field.getName());
        continue;
      } else {
        // System.out.println("fieldName >>>>>> when value ="+value
        // +" >>> "+field.getName());
      }
      Type t = field.getGenericType();
      key = T9StringFormat.format(field.getName());
      if (t instanceof ParameterizedType) {
        Class type = field.getType();
        if (List.class.isAssignableFrom(type)) {// 子接口的问题解决了
          // list类型
          List l = (List) getFieldValue(obj, field);
          List subset = new ArrayList();
          for (Object object : l) {
            // System.out.println(" fkTableNo > > "+fkTableNo);
            object = getFieldInfo(object);// 迭代字表中的信息
            subset.add(object);
          }
          value = subset;
        }
        /**
         * 此处处理MAP类型 未作处理
         */
        if (Map.class.isAssignableFrom(type)) {
          // map类型
        }
        fieldInfo.put(key, value);
        continue;
      }

      fieldInfo.put(key, value);
      // System.out.println(fieldInfo);
    }

    return fieldInfo;
  }

  /**
   * 根据类信息得到 field的信息
   * 
   * @param cls
   *          需要处理的类信息
   * @param conn
   *          数据库连接
   * @param fkTableNo
   *          外键关联
   * @return
   */
  public Map<String, Object> getFieldInfo(Class cls) {
    Map<String, Object> tablePro = null;
    List<String> clsInfo = new ArrayList<String>();

    Field[] fields = null;
    fields = cls.getDeclaredFields();
    tablePro = new HashMap<String, Object>();

    String tableName = T9StringFormat.format(cls.getSimpleName(), "T9");

    tablePro.put("tableName", tableName);
    tablePro.put("Class", cls);
    for (Field field : fields) {
      Object value = null;
      // 判断是否集合类型
      String key = field.getName();
      key = T9StringFormat.format(key);
      Type t = field.getGenericType();
      key = T9StringFormat.format(field.getName());
      if (t instanceof ParameterizedType) {
        ParameterizedType p = (ParameterizedType) t;
        Class type = field.getType();
        if (List.class.isAssignableFrom(type)) {// 子接口的问题解决了
          Class subClass = (Class) p.getActualTypeArguments()[0];
          Map<String, Object> subset = getFieldInfo(subClass);
          value = subset;
        }
        tablePro.put(key, value);
        continue;
      }
      clsInfo.add(key);
    }
    tablePro.put("clsInfo", clsInfo);
    return tablePro;
  }

  /**
   * 2.0版
   * 
   * @param obj
   *          传入的对象
   * @param field
   *          字段属性
   * @return 字段属性的值
   */
  private Object getFieldValue(Object obj, Field field) {
    Object value = null;
    String methodName = getMethod2GetName(field);
    try {
      Method m = obj.getClass().getDeclaredMethod(methodName);
      value = m.invoke(obj);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return value;
  }

  /**
   * 2.0版 得到get方法的方法名
   * 
   * @param f
   *          reflect的Field字段
   * @return get方法的名字
   */
  private String getMethod2GetName(Field f) {
    String methodName = null;
    String str = f.getName();
    str = str.substring(0, 1).toUpperCase() + str.substring(1);
    if (f.getType().isInstance(new Boolean(true))
        || f.getType() == boolean.class) {
      methodName = "is" + str;
    } else {
      methodName = "get" + str;
    }
    return methodName;
  }

  /**
   * 2.0版 得到set方法的方法名
   * 
   * @param fieldName
   *          Java对象的属性名
   * @return set方法的方法名
   */
  private String getMethod2SetName(String fieldName) {
    fieldName = fieldName.substring(0, 1).toUpperCase()
        + fieldName.substring(1);
    return "set" + fieldName;
  }

  /*
   * / 通过cls得到数据库字段名
   * 
   * @param cls Class对象
   * 
   * @return list
   */
  /*
   * public List<String> getTableProl(Class cls){
   * 
   * List<String> tablePro = null; Field[] fields = null;
   * 
   * fields = cls.getDeclaredFields(); tablePro = new ArrayList<String>();
   * 
   * tableName = T9StringFormat.format(cls.getSimpleName(), "T9"); // String
   * querySql = "select FK_TABLE_NO from DS_TABLE where TABLE_NAME = " +
   * tableName;
   * 
   * for (Field field : fields) { .//判断是否集合类型 String key = field.getName(); key
   * = T9StringFormat.format(key); if(!"FIELD_LIST".equals(key))
   * tablePro.add(key); } return tablePro; }
   *//**
   * 通过cls得到数据库字段名 通过键值对保存 格式为：java属性名==>数据库表字段名
   * 
   * @param cls
   *          Class对象
   * @return Map<String,String>
   */
  /*
   * public Map<String,String> getTableProm(Class cls){
   * 
   * Map<String,String> tablePro = null; Field[] fields = null;
   * 
   * fields = cls.getDeclaredFields(); tablePro = new HashMap<String, String>();
   * tableName = T9StringFormat.format(cls.getSimpleName(), "T9"); for (Field
   * field : fields) { .//判断是否集合类型 String key = field.getName(); String value =
   * T9StringFormat.format(key); tablePro.put(key,value); } return tablePro; }
   */

  /*
   * //** 得到数据库的表名
   * 
   * @return
   *//*
      * public String getTableName(){ return this.tableName; }
      *//**
   * 得到对象的set方法
   * 
   * @param str
   *          属性名
   * @return set方法名
   */
  /*
   * private String getSetName(String str){ str =
   * str.substring(0,1).toUpperCase()+str.substring(1); return "set"+str; }
   *//**
   * 将数据库中的表封装成一个java对象
   * 
   * @param cls
   *          Class对象
   * @param rs
   *          ResultSet对象
   * @return
   */
  /*
   * public Object getObject(Class cls,ResultSet rs){ Object o = null; Field[]
   * fields = null;
   * 
   * try { o=cls.newInstance(); } catch (InstantiationException e1) {
   * e1.printStackTrace(); } catch (IllegalAccessException e1) {
   * e1.printStackTrace(); }
   * 
   * fields = cls.getDeclaredFields();
   * 
   * try { while(rs.next()){ o = toObject(rs, fields, o); } } catch
   * (SQLException e) { e.printStackTrace(); } return o; }
   *//**
   * 将数据库中的表封装成一组java对象
   * 
   * @param cls
   *          class对象
   * @param rs
   *          ResultSet对象
   * @return
   */
  /*
   * public List getObjectList(Class cls,ResultSet rs){ List list = null; Object
   * o = null; Field[] fields = null; // System.out.println(rs); fields =
   * cls.getDeclaredFields(); list = new ArrayList(); try {
   * while(rs!=null&&rs.next()){ try { o=cls.newInstance(); } catch
   * (InstantiationException e1) { e1.printStackTrace(); } catch
   * (IllegalAccessException e1) { e1.printStackTrace(); } o = toObject(rs,
   * fields, o); list.add(o); } } catch (SQLException e) { e.printStackTrace();
   * } return list; }
   *//**
   * 将数据库数据封装成一个java对象
   * 
   * @param rs
   * @param fields
   * @param o
   * @return
   */
  /*
   * private Object toObject(ResultSet rs ,Field[] fields,Object o){ Object[]
   * args = null; Method method = null ; String methodName = null; String
   * dbField = null; Class typeClass = null;
   * 
   * for (Field field : fields) { Type t = field.getGenericType(); if(t
   * instanceof ParameterizedType){ ParameterizedType p = (ParameterizedType) t;
   * boolean bool = (field.getType()).isInterface(); Type[] types =
   * p.getActualTypeArguments(); if(!bool){ Object obj =
   * field.getType().newInstance(); if(List.class.isInstance(obj)){ //list类型
   * List l = (List) obj; //得到泛型的信息 for (Type type : types) { Class subset =
   * (Class) type;
   * 
   * } } if(Map.class.isInstance(obj)){ //map类型 Map m = (Map) obj; } } .//处理子表
   * 使用泛型 //1.判断是否集合类型 //2.得到集合类型的泛型性息 //3.通过反省信息组装'子类'对象 //4、将子类存入集合中 continue;
   * }
   * 
   * methodName = getSetName(field.getName()); dbField =
   * T9StringFormat.format(field.getName()); try { try{
   * typeClass=TranToType.getType(field).getReturnedClass(); method =
   * o.getClass().getDeclaredMethod(methodName,typeClass); }catch(Exception e){
   * // System.out.println(field.getName());
   * typeClass=((PrimitiveType)TranToType.getType(field)).getPrimitiveClass();
   * method = o.getClass().getDeclaredMethod(methodName,typeClass); } args =
   * TranToType.dBforObject(rs, field, dbField); method.invoke(o, args); } catch
   * (SecurityException e) { e.printStackTrace(); } catch (NoSuchMethodException
   * e) { e.printStackTrace(); } catch (IllegalArgumentException e) {
   * e.printStackTrace(); } catch (IllegalAccessException e) {
   * e.printStackTrace(); } catch (InvocationTargetException e) {
   * e.printStackTrace(); } } return o; }
   *//**
   * 设定PreparedStatement的值
   * 
   * @param pstmt
   * @param o
   * @param fieldName
   * @param index
   * @return
   */
  /*
   * public PreparedStatement setParam(PreparedStatement pstmt,Object o,String
   * fieldName,int index){ Class cls = o.getClass(); Field field = null; Object
   * value = null ; try { field = cls.getDeclaredField(fieldName); value =
   * getFieldValue(o, fieldName); // System.out.println("pstmt:"+pstmt);
   * TranToType.setPstmtParam(pstmt, field, index, value); } catch
   * (SecurityException e) { e.printStackTrace(); } catch (NoSuchFieldException
   * e) { e.printStackTrace(); } catch (IllegalArgumentException e) {
   * e.printStackTrace(); } return pstmt; }
   *//**
   * 设定PreparedStatement的值
   * 
   * @param pstmt
   * @param o
   * @param fieldName
   * @param index
   * @return
   */
  /*
   * public PreparedStatement setParam(PreparedStatement pstmt,Object o,String
   * fieldName,int index,Object value){ Class cls = o.getClass(); Field field =
   * null; try { field = cls.getDeclaredField(fieldName);
   * //System.out.println("pstmt:"+pstmt); TranToType.setPstmtParam(pstmt,
   * field, index, value); } catch (SecurityException e) { e.printStackTrace();
   * } catch (NoSuchFieldException e) { e.printStackTrace(); } catch
   * (IllegalArgumentException e) { e.printStackTrace(); } return pstmt; }
   *//**
   * 得到一个obj属性段的值
   * 
   * @param obj
   *          Object
   * @param fieldName
   *          filedName
   * @return
   */
  /*
   * public Object getFieldValue(Object obj,String fieldName){ Object value =
   * null; Field field = null; Method method = null ; String methodName = null;
   * Class cls = obj.getClass(); try { field = cls.getDeclaredField(fieldName);
   * methodName = getMethodName(field); method =
   * cls.getDeclaredMethod(methodName); value=method.invoke(obj); } catch
   * (SecurityException e) { e.printStackTrace(); } catch (NoSuchMethodException
   * e) { e.printStackTrace(); } catch (IllegalArgumentException e) {
   * e.printStackTrace(); } catch (IllegalAccessException e) {
   * e.printStackTrace(); } catch (InvocationTargetException e) {
   * e.printStackTrace(); } catch (NoSuchFieldException e) {
   * e.printStackTrace(); } return value; }
   */
}
