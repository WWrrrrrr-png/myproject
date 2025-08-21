package com.ezen.shop.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ezen.shop.admin.product.ProductVO;
import com.ezen.shop.common.utils.SearchCriteria;

public interface ProductMapper {
	
	List<ProductVO> getProductListBysecondCategory(@Param("cri") SearchCriteria cri, @Param("cate_code") Integer second_cate_code);
	
	int getCountProductListBysecondCategory(@Param("cate_code") Integer second_cate_code);
	
	ProductVO pro_info(Integer pro_num);
	
	void review_count(Integer pro_num);
	
	// 상품후기 작업후 리뷰카운트 조회.
	int review_count_pro_info(Integer pro_num);
}
