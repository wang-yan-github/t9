package t9.rad.velocity.createtable;

import t9.rad.velocity.metadata.T9IDialect;
import t9.rad.velocity.metadata.T9MsSqlDialect;
import t9.rad.velocity.metadata.T9MySqlDialect;
import t9.rad.velocity.metadata.T9OracleDialect;

public class T9DBDialectUtil {
  public static final String ORACLEDIALECT = "Oracle";
  public static final String MYSQLDIALECT = "MySql";
  public static final String MSSQLDIALECT = "MsSql";
  
  public static T9IDialect getDialect(String dialect) {
    T9IDialect dia = null;
    if(ORACLEDIALECT.equals(dialect)){
      dia = new T9OracleDialect();
    }if(MYSQLDIALECT.equals(dialect)){
      dia = new T9MySqlDialect();
    }if(MSSQLDIALECT.equals(dialect)){
      dia = new T9MsSqlDialect();
    }
    return dia;
  }
}
