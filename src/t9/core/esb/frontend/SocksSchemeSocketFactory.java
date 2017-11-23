package t9.core.esb.frontend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpParams;

public class SocksSchemeSocketFactory implements SchemeSocketFactory {

  @Override
  public Socket connectSocket(Socket arg0, InetSocketAddress arg1,
      InetSocketAddress arg2, HttpParams arg3) throws IOException,
      UnknownHostException, ConnectTimeoutException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Socket createSocket(HttpParams arg0) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSecure(Socket arg0) throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return false;
  }

}
