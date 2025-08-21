package com.ezen.shop.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentService {

	private final PaymentMapper paymentMapper;
	
	public void payment_status(Integer payment_id, String payment_status) {
		paymentMapper.payment_status(payment_id, payment_status);
	}
}
