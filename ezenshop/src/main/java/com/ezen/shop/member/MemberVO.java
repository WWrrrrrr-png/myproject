package com.ezen.shop.member;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 클래스명은 테이블명과 상관이 없지만, 필드는 컬럼명과 동일하게 작성한다.
@Getter
@Setter
@ToString
public class MemberVO {

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
