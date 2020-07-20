/**
 * CapNhatPhongBanServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package trong.lixco.com.servicepublic;

import trong.lixco.com.util.StaticPath;

public class CapNhatPhongBanServiceLocator extends org.apache.axis.client.Service implements trong.lixco.com.servicepublic.CapNhatPhongBanService {

    public CapNhatPhongBanServiceLocator() {
    }


    public CapNhatPhongBanServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CapNhatPhongBanServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CapNhatPhongBanPort
    private java.lang.String CapNhatPhongBanPort_address = StaticPath.getPathCenter()+"/dulieutrungtam_ejb/CapNhatPhongBan";

    public java.lang.String getCapNhatPhongBanPortAddress() {
        return CapNhatPhongBanPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CapNhatPhongBanPortWSDDServiceName = "CapNhatPhongBanPort";

    public java.lang.String getCapNhatPhongBanPortWSDDServiceName() {
        return CapNhatPhongBanPortWSDDServiceName;
    }

    public void setCapNhatPhongBanPortWSDDServiceName(java.lang.String name) {
        CapNhatPhongBanPortWSDDServiceName = name;
    }

    public trong.lixco.com.servicepublic.CapNhatPhongBan getCapNhatPhongBanPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CapNhatPhongBanPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCapNhatPhongBanPort(endpoint);
    }

    public trong.lixco.com.servicepublic.CapNhatPhongBan getCapNhatPhongBanPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            trong.lixco.com.servicepublic.CapNhatPhongBanServiceSoapBindingStub _stub = new trong.lixco.com.servicepublic.CapNhatPhongBanServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCapNhatPhongBanPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCapNhatPhongBanPortEndpointAddress(java.lang.String address) {
        CapNhatPhongBanPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (trong.lixco.com.servicepublic.CapNhatPhongBan.class.isAssignableFrom(serviceEndpointInterface)) {
                trong.lixco.com.servicepublic.CapNhatPhongBanServiceSoapBindingStub _stub = new trong.lixco.com.servicepublic.CapNhatPhongBanServiceSoapBindingStub(new java.net.URL(CapNhatPhongBanPort_address), this);
                _stub.setPortName(getCapNhatPhongBanPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CapNhatPhongBanPort".equals(inputPortName)) {
            return getCapNhatPhongBanPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://servicepublic.com.lixco.trong/", "CapNhatPhongBanService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://servicepublic.com.lixco.trong/", "CapNhatPhongBanPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CapNhatPhongBanPort".equals(portName)) {
            setCapNhatPhongBanPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
