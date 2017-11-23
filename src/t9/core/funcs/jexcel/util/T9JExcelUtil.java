package t9.core.funcs.jexcel.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


import t9.core.data.T9DbRecord;


public class T9JExcelUtil {
  /**
   * 写excel
   * @param ops
   * @param dataArray
   * @return
   * @throws Exception
   */
  public static OutputStream writeExc(OutputStream ops,ArrayList<T9DbRecord> dataArray ) throws Exception {
    HSSFWorkbook wwb = null;
    HSSFSheet ws = null;
    ArrayList<Integer> counSize = new ArrayList<Integer>();
    try {
      wwb = new HSSFWorkbook();
    // 创建Excel工作表

      //表头
      ws = wwb.createSheet();
     // wwb.setSheetName(0,"T9导出",HSSFWorkbook.ENCODING_UTF_16); 
      HSSFRow titlerow = ws.createRow(0);
      for (int i = 0; i < dataArray.size() ; i++) {
        T9DbRecord dbRec = dataArray.get(i);
        HSSFRow row = ws.createRow(i+1);
        for (int j = 0; j < dbRec.getFieldCnt(); j++) {
          //System.out.println(dbRec.getNameByIndex(j));
          
          if(i == 0){
            HSSFCell titleCell = titlerow.createCell(j);
            titleCell.setCellValue(dbRec.getNameByIndex(j));
            titleCell.setCellStyle(getTiltleStyle(wwb));
            counSize.add(dbRec.getNameByIndex(j).length()+20);
          }
          String value = "";
          if(dbRec.getValueByIndex(j) != null){
            value = dbRec.getValueByIndex(j).toString();
          }
          HSSFCell dataCell = row.createCell(j);
          dataCell.setCellValue(value);
        }
      }
      for (int i = 0; i < counSize.size(); i++) {
        ws.setColumnWidth(i, (counSize.get(i) + 10)*150);
      }
      wwb.write(ops);
      ops.flush();
    } catch (Exception e1) {
      throw e1;
    } finally{
     
    }
    return ops;
  }
  /**
   * 读execl文件
   * @param ins
   * @param hasTitle
   * @return
   * @throws WriteException
   * @throws IOException
   * @throws BiffException
   */
  public static ArrayList<T9DbRecord> readExc(InputStream ins,boolean hasTitle) throws Exception {
    ArrayList<T9DbRecord> dataArray = new ArrayList<T9DbRecord>();
    HSSFWorkbook wb = new HSSFWorkbook(ins);
    HSSFSheet s = wb.getSheetAt(0);//第1个sheet
    HSSFCell c = null;
    HSSFRow titleRow = s.getRow(0);
    int rowTotle = s.getLastRowNum();//总行数    int col = s.getRow(0).getLastCellNum();//总列数    int i = 0;
    if(hasTitle){
      i = 1;
    }
    for(;i < rowTotle;i++){
      T9DbRecord dbr = new T9DbRecord();
      HSSFRow row = s.getRow(i);
      for(int j = 0;j < col;j++){
        String title = "";
        if(hasTitle){
          HSSFCell titleC = titleRow.getCell(j);
          title = titleC.getStringCellValue();
        }else{
          title = "cell_" + j;
        }
        c = row.getCell(j);
        if (c == null) {
          continue;
        }
        Object value = null;
        if(c.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
          value = c.getNumericCellValue();
        }else if( c.getCellType() == HSSFCell.CELL_TYPE_STRING){
          value = c.getStringCellValue();
        }else if( c.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN){
          value = c.getBooleanCellValue();
        }
        dbr.addField(title, value);
       }
      dataArray.add(dbr);
    }   
    return dataArray;
  }
  public static HSSFFont getTitleFont(HSSFWorkbook hssfwb){
    HSSFFont fontStyle = hssfwb.createFont();
    fontStyle.setFontName("宋体");
    fontStyle.setFontHeightInPoints((short)20);
    fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    return fontStyle;
  }
  
  public static HSSFCellStyle getTiltleStyle(HSSFWorkbook hssfwb){
    HSSFCellStyle cellStyle = hssfwb.createCellStyle();
    HSSFFont font = getTitleFont(hssfwb);
    cellStyle.setFont(font);
    return cellStyle;
  }
  
  /**
   * 写excel
   * @param ops
   * @param dataArray
   * @return
   * @throws Exception
   */
  public static OutputStream writeWord(OutputStream ops,byte[] bytes ) throws Exception {
    try{
      POIFSFileSystem fs = new POIFSFileSystem();
      ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
     // DirectoryEntry directory = fs.getRoot();
     // DocumentEntry de = directory.createDocument("WordDocument", bs);
      fs.writeFilesystem(ops);
      bs.close();
      ops.flush();
      ops.close();
    } catch (Exception e1) {
      throw e1;
    } finally{
     
    }
    return ops;
  }
}
