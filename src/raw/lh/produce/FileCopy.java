package raw.lh.produce;

import java.io.*;
public class FileCopy
{
  FileReader FIS;
  FileWriter FOS;
  public boolean copyFile(String src, String des)
  {
    try
    {
      FIS = new FileReader(src);
      FOS = new FileWriter(des);
      char[] cbuf = new char[1];
      while(FIS.read(cbuf) != -1) {
      	FOS.write(cbuf);
      }
      FIS.close();
      FOS.close();
      return true;
    }
    catch (Exception e)
    {
      try
      {
        FIS.close();
        FOS.close();
      }
      catch (IOException f)
      {
        // TODO
      }
      return false;
    }
    finally
    {
    }
  }
}
