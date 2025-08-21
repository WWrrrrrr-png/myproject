package com.ezen.shop.kakaopay;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezen.shop.member.MemberVO;
import com.ezen.shop.order.OrderService;
import com.ezen.shop.order.OrderVO;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/kakao/*")
@RequiredArgsConstructor
@Controller
public class KakaopayController {

	private final KakaopayService kakaopayService;
	private final OrderService orderService;
	
	private OrderVO order_info;
	private String mbsp_id;
	private int order_total_price;
	
	@PostMapping("/kakaoPay")
	public ResponseEntity<ReadyResponse> kakaoPay(OrderVO vo, String item_name, int quantity, HttpSession session) {
		
		mbsp_id = ((MemberVO) session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		log.info("주문정보: " + vo);
		
		this.order_info = vo;
		this.order_total_price = vo.getOrd_price();
		
//		log.info("카카오페이 정보: " + kakaopayService.getKakaoPayProperties().toString());
		
		ResponseEntity<ReadyResponse> entity = null;
		
		// 가맹정 주문번호.
		// 주문테이블의 주문번호를 사용하려했으나, 결제시 이슈가 없는 관계로
		// 낭중 가맹점 관리에서 구분해야 하므로, 회원아이디및주문날자 정보 조합사용.
		String partner_order_id = "ezenshop[" + mbsp_id + "] - " + new Date().toString();
		
		ReadyResponse readyResponse = kakaopayService.ready(partner_order_id, mbsp_id, item_name , quantity, order_total_price, 0);
		
		log.info("결제준비요청 응답결과" + readyResponse.toString());
		
		entity = new ResponseEntity<ReadyResponse>(readyResponse, HttpStatus.OK);
		
		return entity; // jquery 결제하기 이벤트로 제어가 넘어간다.
	}
	
	// (카카오페이 API서버가 호출함)
	// 아래주소를 카카오페이 개발자 애플리케이션 플래폼에 설정.
	// 결제준비요청이 성공되면, QR코드 페이지에서 스캔작업 진행 후 아래주소로 pg_token 값이 전달되고 호출된다. 
	@GetMapping("/approval")
	public String approval(String pg_token, RedirectAttributes rttr) {
		
		log.info("pg_token: " + pg_token);
		// 결제승인요청
		String response = kakaopayService.approve(pg_token);
		
		// 결제승인요청의 성공 응답파라미터로 aid를 확인
		if(response.contains("aid")) {
			//OrderService 파일에서 주문관련작업
			orderService.order_process(this.order_info, mbsp_id, "카카오페이");
		}
		
		rttr.addAttribute("ord_code", order_info.getOrd_code());
		
		// /order/order_result?ord_code=주문번호
		return "redirect:/order/order_result";
	}
	
	// 결제가 취소
	@GetMapping("/cancel")
	public String cancel() {
		
		return "order/order_cancel";
	}
	
	// 결제가 실패
	@GetMapping("/fail")
	public String fail() {
		
		return "order/order_fail";
	}
}
