package com.ezen.shop.order; // 사용자 주문기능목적

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class OrderVO {

	// 소문자 CTRL + SHIFT + Y
	private Integer ord_code; // auto_increment
	private String mbsp_id;
	private String ord_name;
	private String ord_addr_zipcode;
	private String ord_addr_basic;
	private String ord_addr_detail;
	private String ord_tel;
	private String ord_mail;
	private int ord_price;
	private String ord_status;
	private Date ord_regdate;
	private String ord_message;  // 관리자 메모용도
}
