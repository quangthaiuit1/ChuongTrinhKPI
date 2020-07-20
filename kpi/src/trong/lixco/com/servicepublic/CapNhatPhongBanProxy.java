package trong.lixco.com.servicepublic;

public class CapNhatPhongBanProxy implements trong.lixco.com.servicepublic.CapNhatPhongBan {
  private String _endpoint = null;
  private trong.lixco.com.servicepublic.CapNhatPhongBan capNhatPhongBan = null;
  
  public CapNhatPhongBanProxy() {
    _initCapNhatPhongBanProxy();
  }
  
  public CapNhatPhongBanProxy(String endpoint) {
    _endpoint = endpoint;
    _initCapNhatPhongBanProxy();
  }
  
  private void _initCapNhatPhongBanProxy() {
    try {
      capNhatPhongBan = (new trong.lixco.com.servicepublic.CapNhatPhongBanServiceLocator()).getCapNhatPhongBanPort();
      if (capNhatPhongBan != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)capNhatPhongBan)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)capNhatPhongBan)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (capNhatPhongBan != null)
      ((javax.xml.rpc.Stub)capNhatPhongBan)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public trong.lixco.com.servicepublic.CapNhatPhongBan getCapNhatPhongBan() {
    if (capNhatPhongBan == null)
      _initCapNhatPhongBanProxy();
    return capNhatPhongBan;
  }
  
  public java.lang.String updateDepForEmp(java.lang.String arg0, java.lang.String arg1) throws java.rmi.RemoteException{
    if (capNhatPhongBan == null)
      _initCapNhatPhongBanProxy();
    return capNhatPhongBan.updateDepForEmp(arg0, arg1);
  }
  
  
}