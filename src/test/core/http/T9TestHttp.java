package test.core.http;


public class T9TestHttp {
//  public static void main(String[] args) throws Exception {
//    HttpClient httpclient = new DefaultHttpClient();
//
//    HttpPost httppost = new HttpPost("http://localhost" +
//            "/t9/test/core/act/T9TestAttaMeun/uploadFile2.act");
//
//    FileBody bin = new FileBody(new File("E:\\tmp\\新建文档.docx"));
//    StringBody comment = new StringBody("A binary file of some kind");
//
//    MultipartEntity reqEntity = new MultipartEntity();
//    reqEntity.addPart("bin", bin);
//    reqEntity.addPart("comment", comment);
//    reqEntity.addPart("attachmentId", comment);
//    reqEntity.addPart("attachmentName", comment);
//    reqEntity.addPart("moduel", comment);
//    
//    httppost.setEntity(reqEntity);
//    
//    System.out.println("executing request " + httppost.getRequestLine());
//    HttpResponse response = httpclient.execute(httppost);
//    HttpEntity resEntity = response.getEntity();
//
//    System.out.println("----------------------------------------");
//    System.out.println(response.getStatusLine());
//    if (resEntity != null) {
//        System.out.println("Response content length: " + resEntity.getContentLength());
//        System.out.println("Chunked?: " + resEntity.isChunked());
//        System.out.println("Chunked?: " + stream2Str(resEntity.getContent(), "UTF-8"));
//    }
//    if (resEntity != null) {
//      resEntity.consumeContent();
//    }
//  }
//  
//  private static String stream2Str(InputStream in, String charSet) throws Exception {
//    StringBuffer rtBuf = new StringBuffer();
//    LineNumberReader reader = null;
//    try {
//      reader = new LineNumberReader(new InputStreamReader(in, charSet));
//      String str = null;
//      for (int i = 0; (str = reader.readLine()) != null; i++) {
//        rtBuf.append(str);
//        rtBuf.append("\r\n");
//      }
//    }catch(Exception ex) {
//      throw ex;
//    }finally {
//      try {
//        if (reader != null) {
//          reader.close();
//        }
//      }catch(Exception ex) {        
//      }
//    }
//    return rtBuf.toString();
//  }
}
