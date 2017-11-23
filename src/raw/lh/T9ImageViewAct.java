package raw.lh;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.news.data.T9ImgNews;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.util.T9AjaxUtil;

public class T9ImageViewAct {
  public String test(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String ss = request.getParameter("ss");
    return null;
  }
  private static Logger log = Logger.getLogger("lh.raw.lh.T9ImageUploadAct");
  public String getNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String newsId = request.getParameter("newsId");
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9ImgNews news = this.getImageNews(newsId, dbConn);
      String attachmentId = news.getAttachmentId();
      String attachmentName= news.getAttachmentName();
      String[] attrName = null;
      String[] attrId = null;
      StringBuffer sb =  new StringBuffer();
      sb.append("{\"images\":[");
      int count = 0 ;
      if(!T9Utility.isNullorEmpty(attachmentName) && !T9Utility.isNullorEmpty(attachmentId)){
        attrName = attachmentName.split("[*]");
        attrId = attachmentId.split(",");
        for(int i=0; i<attrId.length; i++){
          if(!T9Utility.isNullorEmpty(attrName[i]) && !T9Utility.isNullorEmpty(attrId[i])){
            String bigPath = news.getPicPath(attrName[i], attrId[i]);
            String url = news.getSmallPicPath(attrName[i], attrId[i]);
            File smallImageFile = new File(url);
            if (!smallImageFile.exists()) {
              saveImageAsJpg(bigPath,url, 100,60);
            }
            sb.append("{\"path\":\"" + T9Utility.encodeURL(bigPath) + "\"");
            sb.append(",\"id\":\"" + attrId[i] + "\"");
            sb.append(",\"smallPath\":\"" + T9Utility.encodeURL(url)+ "\"},");
            count++;
          }
        }      
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      sb.append(",\"subject\":\"" + news.getSubject()+ "\"");
      sb.append(",\"content\":\"" + news.getContent()+ "\"");
      Timestamp newsTime = news.getNewsTime();
      SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
      String time = sd.format(newsTime);
      sb.append(",\"time\":\""+ time +"\"");
      sb.append("}");
      T9AjaxUtil.ajax(sb.toString(), response);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
    // targetW，targetH分别表示目标长和宽
    int type = source.getType();
    BufferedImage target = null;
    double sx = (double) targetW / source.getWidth();
    double sy = (double) targetH / source.getHeight();
    //这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
    //则将下面的if else语句注释即可
    if(sx>sy)
    {
        sx = sy;
        targetW = (int)(sx * source.getWidth());
    }else{
        sy = sx;
        targetH = (int)(sy * source.getHeight());
    }
    if (type == BufferedImage.TYPE_CUSTOM) { //handmade
        ColorModel cm = source.getColorModel();
        WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
        boolean alphaPremultiplied = cm.isAlphaPremultiplied();
        target = new BufferedImage(cm, raster, alphaPremultiplied, null);
    } else
        target = new BufferedImage(targetW, targetH, type);
        Graphics2D g = target.createGraphics();
        //smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

      public static void saveImageAsJpg (String fromFileStr,String saveToFileStr,int width,int hight)
      throws Exception {
      BufferedImage srcImage;
      // String ex = fromFileStr.substring(fromFileStr.indexOf("."),fromFileStr.length());
      String imgType = "JPEG";
      if (fromFileStr.toLowerCase().endsWith(".png")) {
          imgType = "PNG";
      }
      // System.out.println(ex);
      File saveFile=new File(saveToFileStr);
      File fromFile=new File(fromFileStr);
      srcImage = ImageIO.read(fromFile);
      if(width > 0 || hight > 0)
      {
          srcImage = resize(srcImage, width, hight);
      }
      ImageIO.write(srcImage, imgType, saveFile);
  
  }
      public static void main (String argv[]) {
        try{
        //参数1(from),参数2(to),参数3(宽),参数4(高)
            saveImageAsJpg("D:\\project\\t9\\attach\\workflow\\0917\\415707.jpg",
                    "D:\\project\\t9\\attach\\workflow\\0917\\smallImage\\415707.jpg",
                    100,60);
        } catch(Exception e){
            e.printStackTrace();
        }
      }
  public T9ImgNews getImageNews(String id  , Connection conn ) throws Exception {
    T9ImgNews news =new T9ImgNews();
    String query = "select seq_id , subject , content , news_time , attachment_id , attachment_name from news where seq_id=" + id;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        int seqId = rs.getInt("seq_id");
        String subject = rs.getString("subject");
        Clob content = rs.getClob("content");
        Timestamp newsTime = rs.getTimestamp("news_time");
        String attachmentId = rs.getString("attachment_id");
        String attachmentName = rs.getString("attachment_name");
        news.setNewsTime(newsTime);
        news.setContent(T9WorkFlowUtility.clob2String(content));
        news.setSeqId(seqId);
        news.setSubject(subject);
        news.setAttachmentId(attachmentId);
        news.setAttachmentName(attachmentName);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return news;
  }

}
