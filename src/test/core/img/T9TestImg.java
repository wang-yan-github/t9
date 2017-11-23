package test.core.img;

import   com.sun.image.codec.jpeg.JPEGCodec;   
import   com.sun.image.codec.jpeg.JPEGImageEncoder;   
import   java.awt.Graphics;   
import   java.awt.Image;   
import java.awt.Toolkit;
import   java.awt.image.BufferedImage;   
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import   java.io.*;   
import   javax.imageio.ImageIO;

import org.apache.sanselan.Sanselan;

public class T9TestImg {

  public static void imageSizer2() {
    Image sourceImage = Toolkit.getDefaultToolkit().getImage("D:\\project\\t9\\webroot\\t9\\core\\styles\\style1\\img\\bg2.jpg");   

    Image croppedImage; 
    ImageFilter cropFilter; 
    cropFilter = new CropImageFilter(25,30,75,75);//四个参数分别为图像起点坐标和宽高，即CropImageFilter(int x,int y,int width,int height)，详细情况请参考API 
    croppedImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(sourceImage.getSource(),cropFilter));
  }
  public static void imageSizer() {
    //File file = new File("D:\\project\\t9\\webroot\\t9\\core\\styles\\style1\\img\\bg2.jpg");
    File file = new File("D:\\MYOA\\webroot\\theme\\1\\product.png");
    int wideth = 0;
    int height = 0;
    String fileName = file.getName();
    try {
      BufferedImage bufferedimage = null;
      try {
        bufferedimage = ImageIO.read(file);
      }catch(Exception ex) {
        bufferedimage = Sanselan.getBufferedImage(file);
      }
      //BufferedImage bufferedimage = Sanselan.getBufferedImage(file);
      wideth = bufferedimage.getWidth(null);
      height = bufferedimage.getHeight(null);
      BufferedImage bufferedimage1 = new BufferedImage(64, 64, 1);
      bufferedimage1.getGraphics().drawImage(bufferedimage, 0, 0, 64, 64, null);
      FileOutputStream fileoutputstream = new FileOutputStream("D:\\tmp\\ar.png");
      JPEGImageEncoder jpegimageencoder = JPEGCodec
          .createJPEGEncoder(fileoutputstream);
      jpegimageencoder.encode(bufferedimage1);
      System.out.print(wideth + ":" + height);
      fileoutputstream.close();
    } catch (Exception exception) {
      System.out.println(exception);
      exception.printStackTrace();
    }
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    imageSizer();
  }

}
