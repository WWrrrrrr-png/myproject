package com.ezen.shop.cart;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CartVO {

	// private Integer cart_code;
	private Integer pro_num;
	private String mbsp_id;
	private int cart_amount;
	private Date cart_date;
	
}
