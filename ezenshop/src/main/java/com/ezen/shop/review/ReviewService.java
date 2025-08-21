package com.ezen.shop.review;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.product.ProductMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {

	private final ReviewMapper reviewMapper;
	private final ProductMapper productMapper;
	
	public List<ReviewVO> rev_list(Integer pro_num, SearchCriteria cri) {
		return reviewMapper.rev_list(pro_num, cri);
	}
	
	public int getCountReviewByPro_num(Integer pro_num) {
		return reviewMapper.getCountReviewByPro_num(pro_num);
	}
	
	@Transactional
	public void review_save(ReviewVO vo) {
		// 1)상품후기등록
		reviewMapper.review_save(vo);
		// 2)상품테이블 후기카운트 작업
		productMapper.review_count(vo.getPro_num());
	}
	
	public ReviewVO review_info(Long rev_code) {
		return reviewMapper.review_info(rev_code);
	}
	
	public void review_modify(ReviewVO vo) {
		reviewMapper.review_modify(vo);
	}
	
	public void review_delete(Long rev_code) {
		reviewMapper.review_delete(rev_code);
	}
	
	public void reply_insert(ReviewReply vo) {
		reviewMapper.reply_insert(vo);
	}
}
