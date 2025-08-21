package com.ezen.shop.payment;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentVO {
	
	private Integer payment_id;
	private Integer ord_code;
	private String mbsp_id;
	private String payment_method;
	private int  payment_price;
	private String payment_status;
	private Date payment_date;
}
