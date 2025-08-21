package com.ezen.shop.cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartService {

	private final CartMapper cartMapper;
	
	public void cart_add(CartVO vo) {
		cartMapper.cart_add(vo);
	}
	
	public List<Map<String, Object>> getCartDetailsByUserId(String mbspId) {
		return cartMapper.getCartDetailsByUserId(mbspId);
	}
	
	public Integer getCartTotalPriceByUserId(String mbspId) {
		return cartMapper.getCartTotalPriceByUserId(mbspId);
	}
	
	public List<Map<String, Object>> cart_list(String mbspId) {
		return cartMapper.cart_list(mbspId);
	}
	
	public void cart_empty(String mbspId) {
		cartMapper.cart_empty(mbspId);
	}
	
	public void cart_change(CartVO vo) {
		cartMapper.cart_change(vo);
	}
	
	public void cart_sel_delete(int[] pro_num, String mbsp_id) {
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("pro_num_arr", pro_num); // 선택된 상품코드
		map.put("mbsp_id", mbsp_id); // 사용자아이디
		
		cartMapper.cart_sel_delete(map);
	}
	
}
