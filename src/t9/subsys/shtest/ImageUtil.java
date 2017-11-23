package t9.subsys.shtest;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import sun.misc.BASE64Encoder;

public class ImageUtil {

	  public static String GetImageStr(String imgFilePath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	        byte[] data = null;
	        
	        // 读取图片字节数组
	        try {
	            InputStream in = new FileInputStream(imgFilePath);
	            data = new byte[in.available()];
	            in.read(data);
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        String str = new String(data);
	        // 对字节数组Base64编码
	        return str;// 返回Base64编码过的字节数组字符串
	    }
	public static void main(String[] args) {
		String str = ImageUtil.GetImageStr("D:\\1231.jpg");
		// 定义图像buffer
		BufferedImage buffImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		ImageIcon im = null;
		Image image = im.getImage();
		Graphics g1 = image.getGraphics();
		
		
		// 将图像填充为白色
		//g.setColor(Color.WHITE);
		//g.setColor(getRandColor(220, 250)); 
		//g.fillRect(0, 0, nPicWidth, nPicHeight);
		//ImageIO.write(buffImg, "jpeg", sos);
		
		
		 //OutputStream out = new FileOutputStream(imgFilePath);
        // out.write(bytes);
        // out.flush();
       //  out.close();
	}
}
