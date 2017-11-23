package test.core.img;

import java.io.File;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class T9ImgMetaData {
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    extractMetaData();
  }
  
  public static void extractMetaData() throws Exception {
    File jpegFile = new File("C:\\Users\\yzq\\Desktop\\DSC00063.JPG");
    Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
    Iterator directories = metadata.getDirectoryIterator();
    while (directories.hasNext()) {
        Directory directory = (Directory)directories.next();
        // iterate through tags and print to System.out
        Iterator tags = directory.getTagIterator();
        while (tags.hasNext()) {
            Tag tag = (Tag)tags.next();
            // use Tag.toString()
            System.out.println(tag);
        }
    }
  }
}
