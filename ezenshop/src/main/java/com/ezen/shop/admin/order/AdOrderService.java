package com.ezen.shop.admin.order;

import java.time.Period;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.order.OrderVO;
import com.ezen.shop.payment.PaymentVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdOrderService {

	private final AdOrderMapper adOrderMapper;
	
	public List<Map<String, Object>> order_list(SearchCriteria cri, String period, String start_date, String end_date, String payment_method, String ord_status) {
		return adOrderMapper.order_list(cri, period, start_date, end_date, payment_method, ord_status);
	}
	
	public int getTotalCount (SearchCriteria cri, String period, String start_date, String end_date, String payment_method, String ord_status) {
		return adOrderMapper.getTotalCount(cri, period, start_date, end_date, payment_method, ord_status);
	}
	
	public List<Map<String, Object>> orderdetail_info(Integer ord_code) {
		return adOrderMapper.orderdetail_info(ord_code);
	}
	
	public PaymentVO payment_info(Integer ord_code) {
		return adOrderMapper.payment_info(ord_code);
	}
	
	public OrderVO order_info(Integer ord_code) {
		return adOrderMapper.order_info(ord_code);
	}
	
	public void admin_ord_message(Integer ord_code, String ord_message) {
		adOrderMapper.admin_ord_message(ord_code, ord_message);
	}
	
	public void order_info_edit(OrderVO vo) {
		adOrderMapper.order_info_edit(vo);
	}
	
	@Transactional
	public void order_product_del(Integer ord_code, Integer pro_num) {
		
		// 주문상세테이블에서 상품삭제
		adOrderMapper.order_product_del(ord_code, pro_num);
		
		// 주문번호를 사용하여, 주문상세테이블의 주문금액
		int ord_total_price = adOrderMapper.order_total_price(ord_code);
		
		// 주문테이블 총금액 변경(update)
		adOrderMapper.order_info_change_price(ord_code, ord_total_price);
		
		// 결제테이블 결제금액 변경(update)
		adOrderMapper.payment_change_price(ord_code, ord_total_price);
	}
	
	public void order_status(Integer ord_code, String ord_status) {
		adOrderMapper.order_status(ord_code, ord_status);
	}
}
