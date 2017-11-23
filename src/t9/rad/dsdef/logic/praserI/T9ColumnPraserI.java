package t9.rad.dsdef.logic.praserI;

import java.sql.Connection;
import java.util.ArrayList;

import t9.core.data.T9DsField;

public interface T9ColumnPraserI {
  public void execPhyicsSql(Connection conn,String tableName,ArrayList<T9DsField> dsFields) throws Exception;
}
