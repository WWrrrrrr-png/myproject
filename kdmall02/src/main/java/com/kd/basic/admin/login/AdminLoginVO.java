package com.kd.basic.admin.login;

import java.sql.Date;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;
@ToString
@Getter 
@Setter
public class AdminLoginVO {
   
	private String ad_userid; 
	private String ad_password;
	private Date LOGIN_DATE;
}
