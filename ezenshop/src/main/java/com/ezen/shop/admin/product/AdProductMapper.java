package com.ezen.shop.admin.product;

import java.util.HashMap;
import java.util.List;

import com.ezen.shop.admin.category.CategoryVO;
import com.ezen.shop.common.utils.SearchCriteria;

public interface AdProductMapper {

	// insert,update,delete 용도로 사용하는 메서드는 리턴타입이 void
	// select용도 사용하는 메서드는 리턴타입이 그 결과에 맞는 타입을 사용
	//상품등록작업
	void pro_insert(ProductVO vo);
	
	List<ProductVO> pro_list(SearchCriteria cri);
	
	int getTotalCount(SearchCriteria cri);
	
	void pro_sel_delete_2(int[] pro_num_arr);
	
	void pro_sel_delete_3(HashMap<String, Object> map);
	
	ProductVO pro_edit_form(Integer pro_num);
	
	void pro_edit_ok(ProductVO vo);
	
	void pro_delete(Integer pro_num);
	
	
}
