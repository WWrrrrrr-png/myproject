package com.ezen.shop.admin.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.order.OrderVO;
import com.ezen.shop.payment.PaymentVO;

public interface AdOrderMapper {

	List<Map<String, Object>> order_list(@Param("cri") SearchCriteria cri, @Param("period") String period, 
					@Param("start_date") String start_date, @Param("end_date") String end_date, @Param("payment_method") String payment_method,
					@Param("ord_status") String ord_status);
	
	int getTotalCount (@Param("cri") SearchCriteria cri, @Param("period") String period, @Param("start_date") String start_date, @Param("end_date") String end_date, 
			@Param("payment_method") String payment_method, @Param("ord_status") String ord_status);
	
	
	List<Map<String, Object>> orderdetail_info(Integer ord_code);
	
	PaymentVO payment_info(Integer ord_code);
	
	OrderVO order_info(Integer ord_code);
	
	void admin_ord_message(@Param("ord_code") Integer ord_code, @Param("ord_message") String ord_message);
	
	void order_info_edit(OrderVO vo);
	
	void order_product_del(@Param("ord_code") Integer ord_code, @Param("pro_num") Integer pro_num);
	
	int order_total_price(Integer ord_code);
	
	void order_info_change_price(Integer ord_code, int ord_total_price);
	
	void payment_change_price(@Param("ord_code") Integer ord_code, @Param("ord_total_price") int ord_total_price);
	
	void order_status(@Param("ord_code") Integer ord_code, @Param("ord_status") String ord_status);
}
