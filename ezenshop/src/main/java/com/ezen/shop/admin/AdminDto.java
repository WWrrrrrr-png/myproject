package com.ezen.shop.admin;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminDto {

	private String ad_userid;
	private String ad_passwd;
	private Date login_date;
}
