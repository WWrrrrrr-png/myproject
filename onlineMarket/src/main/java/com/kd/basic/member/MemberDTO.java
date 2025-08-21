package com.kd.basic.member;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {

	private String mbsp_id;
	private String mbsp_name;
	private String mbsp_email;
	private String mbsp_password;
	private String mbsp_zipcode;
	private String mbsp_addr;
	private String mbsp_deaddr;
	private String mbsp_phone;
	private String mbsp_nick;
	private String mbsp_receive;
	private int mbsp_point;
	private Date mbsp_lastlogin;
	private Date mbsp_datesub;
	private Date mbsp_updatedate;
}
