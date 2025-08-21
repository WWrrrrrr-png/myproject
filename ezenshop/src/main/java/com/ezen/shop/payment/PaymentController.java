package com.ezen.shop.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.shop.kakaopay.ReadyResponse;
import com.ezen.shop.order.OrderVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class PaymentController {

	private final PaymentService paymentService;
	
	
}
