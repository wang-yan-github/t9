package t9.core.esb.common.util;

import java.io.IOException;

import org.apache.http.HttpResponse;

public class T9HttpClientUtil {
  public static void releaseConnection(HttpResponse response) {
    try {
      if (response != null) {
        response.getEntity().getContent().close();
      }
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
