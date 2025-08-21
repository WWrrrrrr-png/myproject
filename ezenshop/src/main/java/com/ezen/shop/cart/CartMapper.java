package com.ezen.shop.cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CartMapper {
	
	void cart_add(CartVO vo);
	
	List<Map<String, Object>> getCartDetailsByUserId(String mbspId);
	
	Integer getCartTotalPriceByUserId(String mbspId);
	
	void cart_empty(String mbspId);
	
	List<Map<String, Object>> cart_list(String mbspId);
	
	void cart_change(CartVO vo);
	
	void cart_sel_delete(HashMap<String, Object> map);

	
}
