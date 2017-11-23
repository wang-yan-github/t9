package test.core.poi;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.file.T9ZipFileUtility;

public class T9TestWord {
  public static void readWord(String filePath) throws Exception {
    //得到.docx文件提取器
    org.apache.poi.xwpf.extractor.XWPFWordExtractor docx = new XWPFWordExtractor(POIXMLDocument.openPackage(filePath)); 
    //提取.docx正文文本
    String text = docx.getText();
    System.out.println("text>>" + text);
    //提取.docx批注
//    org.apache.poi.xwpf.usermodel.XWPFComment[] comments = ((XWPFDocument)docx.getDocument()).getComments();
//    for(XWPFComment comment:comments){
//      System.out.println("Id>>" + comment.getId());//提取批注Id
//      System.out.println("Author>>" + comment.getAuthor());//提取批注修改人
//      System.out.println("Text>>" + comment.getText());//提取批注内容 
//    }
//    XWPFDocument d;
//    d.
  }
  
  /**
   * 
   * @param excelPath
   * @param wordPath
   * @throws Exception
   */
  public static void splitExcel(String workPath) throws Exception {
    try {
      String excelPath = workPath + "\\src.xlsx";
      String wordPath = workPath + "\\output";
      POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(excelPath));
      HSSFWorkbook wb = new HSSFWorkbook(fs);
      HSSFSheet sheet = wb.getSheetAt(0);   
      HSSFRow row = null;
      HSSFCell cell1 = null;
      HSSFCell cell2 = null;
      for (int i = 1; i < sheet.getLastRowNum(); i++) {
        if (i > 3) {
          break;
        }
        row = sheet.getRow(0);
        cell1 = row.getCell(0);
        cell2 = row.getCell(1);
        String cell1Text = cell1.getStringCellValue();
        String cell2Text = cell2.getStringCellValue();
        if (T9Utility.isNullorEmpty(cell1Text) || T9Utility.isNullorEmpty(cell2Text)) {
          continue;
        }
        String wordFile = wordPath + "\\" + T9Guid.getRawGuid() + "_文档" + i + ".docx";
        genWordFile(workPath, wordFile, cell1Text, cell2Text);
      } 
    }catch (IOException e) { 
      e.printStackTrace(); 
    }finally {
    }
  }
  
  /**
   * 生成Word文件
   * @param workPath           工作目录
   * @param title      
   * @param body
   */
  private static void genWordFile(String workPath, String wordFile, String title, String body) throws Exception {
    String txtFile = workPath + "\\temp\\word\\document.xml";
    String tmpFile = workPath + "\\temp\\word\\document.src";
    
    T9FileUtility.copyFile(tmpFile, txtFile);
    List rules = new ArrayList();
    rules.add(new String[]{"$1", title});
    rules.add(new String[]{"$2", body});
    T9FileUtility.replaceInFile(txtFile, rules);
    
    T9ZipFileUtility.doZip(workPath + "\\temp", wordFile);
  }
  
  public static boolean writeDoc(String path, String content) { 
     boolean w = false; 
     try {
       // byte b[] = content.getBytes("ISO-8859-1"); 
       byte b[] = content.getBytes();
       ByteArrayInputStream bais = new ByteArrayInputStream(b);
    
       POIFSFileSystem fs = new POIFSFileSystem(); 
       DirectoryEntry directory = fs.getRoot();
    
       DocumentEntry de = directory.createDocument("WordDocument", bais);
    
       FileOutputStream ostream = new FileOutputStream(path);
    
       fs.writeFilesystem(ostream);
    
       bais.close(); 
       ostream.close();
     } catch (IOException e) { 
       e.printStackTrace(); 
     } 
     return w; 
   } 
}
