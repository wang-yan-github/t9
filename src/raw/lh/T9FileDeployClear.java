package raw.lh;

import java.io.File;
import java.io.IOException;

import t9.core.util.T9Out;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class T9FileDeployClear {
  public static void main(String[] args) throws Exception {
    File file = new File("H:/文档/修改记录/T9修改记录表_刘涵_20110418.xlsx");
    String str = readExcel(file);
    T9Out.print(str);
  }
  /**读取Excel文件的内容   
   * @param file  待读取的文件   
   * @return   
   */   
  public static String readExcel(File file){    
     StringBuffer sb = new StringBuffer();    
         
      Workbook wb = null;    
      try {    
          //构造Workbook（工作薄）对象    
          wb=Workbook.getWorkbook(file);    
      } catch (BiffException e) {    
          e.printStackTrace();    
      } catch (IOException e) {    
          e.printStackTrace();    
      }    
          
      if(wb==null)    
          return null;    
          
      //获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了    
      Sheet[] sheet = wb.getSheets();    
          
      if(sheet!=null&&sheet.length>0){    
          //对每个工作表进行循环    
          for(int i=0;i < sheet.length ;i++) {
              //得到当前工作表的行数    
              int rowNum = sheet[i].getRows();    
              for(int j=0;j < rowNum ;j++) {
                  //得到当前行的所有单元格    
                  Cell[] cells = sheet[i].getRow(j);    
                  if(cells!=null&&cells.length>0){    
                      //对每个单元格进行循环    
                      for(int k=0;k < cells.length ; k++ ) {
                          //读取当前单元格的值    
                          String cellValue = cells[k].getContents();    
                          sb.append(cellValue+"\t");    
                      }    
                  }    
                  sb.append("\r\n");    
              }    
              sb.append("\r\n");    
          }    
      }    
      //最后关闭资源，释放内存    
      wb.close(); 
      return sb.toString();    
  }  
}
