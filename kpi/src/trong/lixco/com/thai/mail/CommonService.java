package trong.lixco.com.thai.mail;

import java.rmi.RemoteException;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;

public class CommonService {
	private static DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
	public static Department[] findAll() throws RemoteException {
		return DEPARTMENT_SERVICE_PUBLIC.findAll();
	}
}
