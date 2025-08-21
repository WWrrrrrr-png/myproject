package com.ezen.shop.order;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class OrderDetailVO {

	private Integer ord_code;
	private Integer pro_num;
	private int dt_amount;
	private int dt_price;
}
