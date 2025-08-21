package com.ezen.shop.payment;

import org.apache.ibatis.annotations.Param;

//@Mapper
public interface PaymentMapper {

	
	void payment_insert(PaymentVO vo);
	
	void payment_status(@Param("payment_id") Integer payment_id, @Param("payment_status") String payment_status);
}
