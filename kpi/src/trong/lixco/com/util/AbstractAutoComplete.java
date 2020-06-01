/**
 * This class is made by Lam Quan Vu.
 * @Copyright 2013 by Lam Quan Vu. Email : LamQuanVu@gmail.com
 */
package trong.lixco.com.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;

@ManagedBean
public class AbstractAutoComplete {

	MemberServicePublic memberServicePublic;
	DepartmentServicePublic departmentService;

	@PostConstruct
	public void init() {
		memberServicePublic = new MemberServicePublicProxy();
		departmentService = new DepartmentServicePublicProxy();
	}

	public static String converViToEn(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		String result = pattern.matcher(temp).replaceAll("");
		return pattern.matcher(result).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
	}
}
