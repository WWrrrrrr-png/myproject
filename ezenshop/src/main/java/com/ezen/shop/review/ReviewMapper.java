package com.ezen.shop.review;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ezen.shop.common.utils.SearchCriteria;

public interface ReviewMapper {
	// 1)상품후기 테이블 1개로 쿼리를 조회
	List<ReviewVO> rev_list(@Param("pro_num") Integer pro_num, @Param("cri") SearchCriteria cri);
	
	// 2)상품후기테이블, 상품후기답변테이블을 조인하여 조회.
	//List<Map<String, Object>> rev_list(@Param("pro_num") Integer pro_num, @Param("cri") SearchCriteria cri);
	
	// 페이징정보를 구성하기위한 상품후기 개수.
	int getCountReviewByPro_num(Integer pro_num);
	
	void review_save(ReviewVO vo);
	
	ReviewVO review_info(Long rev_code);
	
	void review_modify(ReviewVO vo);
	
	void review_delete(Long rev_code);
	
	void reply_insert(ReviewReply vo);
	
	
	
}
