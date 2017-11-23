package raw.lh.produce;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main2 {
  public static int count = 0;
  public static String[] notDoMethod_Thread = {"setName","isInterrupted","yield","suspend","stop","sleep","setUncaughtExceptionHandler","setDefaultUncaughtExceptionHandler","setContextClassLoader","resume","join","isDaemon","isAlive","interrupt","interrupted","enumerate","getPriority","getState","holdsLock","getUncaughtExceptionHandler","getId","getDefaultUncaughtExceptionHandler","getContextClassLoader","getAllStackTraces","countStackFrames","destroy","setPriority","activeCount","checkAccess","currentThread","getName","getThreadGroup","getStackTrace","dumpStack","setDaemon"};
  public static String[] notDoMethod = {"wait","getClass","notify","notifyAll","hashCode"};
  public static String[] notHasHandler = {"\\t9\\core\\global"};
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    String filePath = "E:\\Test\\src";
    String srcPath = "D:\\project\\t9\\src";
    File file1 = new File(filePath);
    deleteFile(file1 , 0);
    DirectoryCopy cp = new DirectoryCopy();
    cp.copyDirectory(srcPath, filePath);
    
    long time1 = System.currentTimeMillis();
    System.out.println("复制完毕");
    generalSrc(file1 , filePath);
    long time2 = System.currentTimeMillis();
    long time = time2 - time1;
    System.out.println("处理完毕,共处理" + count + "个文件,耗时：" + time  + "ms");
  }
  public static void deleteFile(File file , int i) {
    File[] list = file.listFiles();
    for (File fe : list) {
      if (fe.isDirectory()) {
        deleteFile(fe , 1);
      } else {
        if (fe.exists())
          fe.delete();
      }
    }
    if (i != 0) {
      if (file.exists())
      file.delete();
    }
  }
  public static void generalSrc(File file , String srcPath) {
    File[] list = file.listFiles();
    for (File fe : list) {
      if (fe.isFile()) {
        String feName = fe.getName();
        if (feName.endsWith(".java")) {
          doClear(fe , srcPath);
          count++;
        }
      } else  {
        generalSrc(fe , srcPath);
      }
    }
  }
  public static boolean isHandler(String path) {
    for (String not : notHasHandler) {
      if (path.contains(not)) {
        return false;
      }
    }
    return true;
  }
  public static void doClear(File file , String srcPath) {
    String path = file.getAbsolutePath();
    if (!isHandler(path)) {
      return ;
    }
    System.out.println("正在处理" + path + ".....");
    String str = readFile(file);
    str = str.trim();
    int index = str.indexOf("{");
     str = str.substring(0, index + 1) + "\n";
    path = path.replace(srcPath, "");
    path = path.replace("\\", ".").replace(".java", "");
    String className = path;
    if (className.startsWith(".") ) {
      className = className.substring(1);
    }
    int index2 = className.lastIndexOf(".");
    String simpleClassName = className.substring(index2 + 1);
    System.out.print(simpleClassName);
    String methodStr = "";
    String conStr = "";
    boolean isReWrite;
    try{
      Class clazz = Class.forName(className);
      
      if (clazz.isInterface()) {
        System.out.println("处理完毕" + file.getAbsolutePath() + ".....");
        return ;
      }
      Class clazz2 = clazz.getSuperclass();
      Constructor[] constructors = clazz.getConstructors();
      for (Constructor con : constructors) {
        conStr += getCon(con , simpleClassName);
      }
      Method[] method = clazz.getMethods();
      boolean flag = clazz2 == Thread.class;
      for (Method m : method) {
        if (isDoMethod(m.getName() , flag)) {
          methodStr += getMethod(m);
        }
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
    str += conStr + methodStr + "}";
    writeFile(file , str);
    System.out.println("处理完毕" + file.getAbsolutePath() + ".....");
  }
  
  public static boolean isDoMethod(String name ,boolean flag) {
    boolean result = true;
    if (flag) {
      for (String na : notDoMethod_Thread) {
        if (name.equals(na)) {
          return false;
        }
      }
    }
    for (String na : notDoMethod) {
      if (name.equals(na)) {
        return false;
      }
    }
    return result ;
  }
  public static String getMethod(Method m) {
    String mStr = "";
    String name = m.getName();
    int i = m.getModifiers();
    if (Modifier.isInterface(i)) {
      
    }
    if (Modifier.isPublic(i)) {
      mStr += " public ";
    }
    if (Modifier.isFinal(i)) {
      mStr += " final ";
    }
    if (Modifier.isStatic(i)) {
      mStr += " static ";
    }
    if (Modifier.isAbstract(i)) {
      mStr += " abstract ";
    }
    Class returnType = m.getReturnType();
    String returnTypeName = returnType.getName();
    if (returnType.isArray()) {
      returnTypeName = getArrayClassName(returnTypeName);
    }
    String returnValue = getReturnValue(returnTypeName);
    mStr += returnTypeName + " " + name + "(";
    Class[] types = m.getParameterTypes();
    String par = "";
    int j = 0;
    for (Class clazz : types) {
      String className = clazz.getName();
      j++;
      if (clazz.isArray()) {
        className = getArrayClassName(className);
      } 
      
      par += className + " a" + j + " ,"; 
    }
     
    if (par.endsWith(",")) {
      par = par.substring(0, par.length() - 1);
    }
    mStr += par + ")";
    if (!Modifier.isAbstract(i)) {
      mStr +=  "{" + returnValue ;
      mStr += "}\n";
    } else {
      mStr += ";";
    }
    return mStr;
  }
  public static String getCon(Constructor c , String className1) {
    String cStr = "";
    int i = c.getModifiers();
    if (Modifier.isPublic(i)) {
      cStr += " public ";
    }
    if (Modifier.isAbstract(i)) {
      cStr += " abstract ";
    }
    cStr += className1 + "(";
    Class[] types = c.getParameterTypes();
    String par = "";
    int j = 0;
    for (Class clazz : types) {
      String className = clazz.getName();
      j++;
      if (clazz.isArray()) {
        className = getArrayClassName(className);
      } 
      par += className + " a" + j + " ,"; 
    }
     
    if (par.endsWith(",")) {
      par = par.substring(0, par.length() - 1);
    }
    cStr += par + ")";
    if (!Modifier.isAbstract(i)) {
      cStr +=  "{}\n";
    } else {
      cStr +=  ";\n";
    }
    return cStr;
  }
  public static String getArrayStr(int length) {
    String result = "";
    for (int i = 0 ;i < length ;i++) {
      result +="[]";
    }
    return result;
  }
  public static String getArrayClassName(String className) {
    int length = className.lastIndexOf("[") + 1;
    char type = className.charAt(length);
    if (className.endsWith(";")) {
      className  = className.substring(length + 1, className.length() - 1);
    }
    className = getType(type , className);
    className += getArrayStr(length);
    return className;
  }
  public static String getType(char type , String className) {
    if (type == 'Z') {
      return "boolean";
    } else if (type == 'B'){
      return "byte";
    } else if (type == 'C') {
      return "char";
    } else if (type == 'D') {
      return "double";
    } else if (type == 'F') {
      return "float";
    } else if (type == 'I') {
      return "int";
    } else if (type == 'J') {
      return "long";
    } else if (type == 'S') {
      return "short";
    } else {
      return className;  
    }
  }
  public static String getReturnValue(String verPro) {
    String result = "return ";
    if ("int".equals(verPro)
         || "long".equals(verPro)
         || "float".equals(verPro)
         || "short".equals(verPro)
         || "char".equals(verPro)
         || "double".equals(verPro)
         || "byte".equals(verPro)) {
      result += " 0;";
    } else if ("boolean".equals(verPro)) {
      result +=  " false;";
    } else if ("void".equals(verPro)) {
      result += " ;";
    } else {
      result += " null;";
    }
    return result;
  }
	public static String readFile(File file) {
		StringBuffer sb = new StringBuffer();
		FileReader FIS;
		try {
	    FIS = new FileReader(file);
	    char[] cbuf = new char[1];
      while(FIS.read(cbuf) != -1) {
      	sb.append(cbuf);
      }
      FIS.close();
    } catch (Exception e)
    {
    }
		return sb.toString();
	}
	public static String writeFile(File file , String str) {
    StringBuffer sb = new StringBuffer();
    FileWriter FIS;
    try {
      FIS = new FileWriter(file);
      FIS.write(str);
      FIS.close();
    } catch (Exception e)
    {
    }
    return sb.toString();
  }
}
