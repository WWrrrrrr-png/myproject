package com.ezen.shop.order;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezen.shop.cart.CartMapper;
import com.ezen.shop.common.utils.Criteria;
import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.delivery.DeliveryMapper;
import com.ezen.shop.delivery.DeliveryVO;
import com.ezen.shop.payment.PaymentMapper;
import com.ezen.shop.payment.PaymentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderMapper orderMapper;
	private final PaymentMapper paymentMapper;
	private final CartMapper cartMapper;
	private final DeliveryMapper deliveryMapper;
	
	// 주문하기.(주문테이블, 주문상세테이블, 결제테이블, 장바구니테이블)
	@Transactional
	public void order_process(OrderVO vo, String mbsp_id, String p_method) {
		
		log.info("주문번호: " + vo.getOrd_code()); // null 출력
		
		// 1)주문테이블
		orderMapper.order_insert(vo); 
		
		log.info("주문번호: " + vo.getOrd_code()); // 주문번호 출력
		
		
		// 2)주문상세테이블(장바구니테이블의 정보를 이용하여 작업)
		orderMapper.order_detail_insert(vo.getOrd_code(), mbsp_id);
		
		// 3)결제테이블
		PaymentVO p_vo = new PaymentVO();
		p_vo.setOrd_code(vo.getOrd_code());
		p_vo.setMbsp_id(mbsp_id);
		
		p_vo.setPayment_method(p_method); // "카카오페이"
		p_vo.setPayment_price(vo.getOrd_price()); // 총구매금액
		
		if(p_method.equals("카카오페이")) {
			p_vo.setPayment_status("입금완료");
		}else if(p_method.contains("계좌이체")) {
			p_vo.setPayment_status("입금미납");
		}
		
		paymentMapper.payment_insert(p_vo);
		
		// 4)장바구니테이블
		cartMapper.cart_empty(mbsp_id);
		
		// 5)배송테이블
		DeliveryVO deliveryVO = new DeliveryVO();
		deliveryVO.setOrd_code(vo.getOrd_code());
		deliveryVO.setShipping_addr(vo.getOrd_addr_basic());
		deliveryVO.setShipping_deaddr(vo.getOrd_addr_detail());
		deliveryVO.setShipping_zipcode(vo.getOrd_addr_zipcode());
		
		deliveryMapper.delivery_insert(deliveryVO);
		
	}
	
	// 실시간 결제에 따른 주문내역
	public List<Map<String, Object>> getOrderInfoByOrd_code(Integer ord_code) {
		return orderMapper.getOrderInfoByOrd_code(ord_code);
	}
	
	public List<Map<String, Object>> getOrderInfoByUser_id(String mbsp_id, SearchCriteria cri) {
		return orderMapper.getOrderInfoByUser_id(mbsp_id, cri);
	}
	
	public int getOrderCountByUser_id(String mbsp_id) {
		return orderMapper.getOrderCountByUser_id(mbsp_id);
	}
	
	public String getCategoryNameByPro_num(Integer pro_num) {
		return orderMapper.getCategoryNameByPro_num(pro_num);
	}
	
	public List<Map<String, Object>> review_manage(String mbsp_id, SearchCriteria cri) {
		return orderMapper.review_manage(mbsp_id, cri);
	}
	
	public int getReviewCountByUser_id(String mbsp_id) {
		return orderMapper.getReviewCountByUser_id(mbsp_id);
	}
	
}
